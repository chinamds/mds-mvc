package com.mds.pm.webapp.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.pm.service.PlayerManager;
import com.mds.pm.model.Player;
import com.mds.common.webapp.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/pm/playerform*")
public class PlayerFormController extends BaseFormController {
    private PlayerManager playerManager = null;

    @Autowired
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public PlayerFormController() {
        setCancelView("redirect:players");
        setSuccessView("redirect:players");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Player showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return playerManager.get(new Long(id));
        }

        return new Player();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Player player, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(player, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "playerform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (player.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            playerManager.remove(player.getId());
            saveMessage(request, getText("player.deleted", locale));
        } else {
            playerManager.save(player);
            String key = (isNew) ? "player.added" : "player.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:playerform?id=" + player.getId();
            }
        }

        return success;
    }
}
