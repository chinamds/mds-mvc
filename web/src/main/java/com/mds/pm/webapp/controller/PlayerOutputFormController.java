package com.mds.pm.webapp.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.pm.service.PlayerManager;
import com.mds.pm.service.PlayerOutputManager;
import com.mds.sys.util.UserUtils;
import com.mds.common.webapp.controller.BaseFormController;
import com.mds.pm.model.PlayerOutput;

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
@RequestMapping("/pm/playerOutputform*")
public class PlayerOutputFormController extends BaseFormController {
    private PlayerOutputManager playerOutputManager = null;
    private PlayerManager playerManager = null;

    @Autowired
    public void setPlayerOutputManager(PlayerOutputManager playerOutputManager) {
        this.playerOutputManager = playerOutputManager;
    }
    
    @Autowired
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public PlayerOutputFormController() {
        setCancelView("redirect:playerOutputs");
        setSuccessView("redirect:playerOutputs");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected PlayerOutput showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return playerOutputManager.get(new Long(id));
        }

        return new PlayerOutput();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(PlayerOutput playerOutput, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(playerOutput, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "playerOutputform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (playerOutput.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            playerOutputManager.remove(playerOutput.getId());
            saveMessage(request, getText("playerOutput.deleted", locale));
        } else {
        	if (playerOutput.getPlayer() == null){
        		if (!StringUtils.isBlank(request.getParameter("playerId"))) {
        			playerOutput.setPlayer(playerManager.get(new Long(request.getParameter("playerId"))));
        		}
        	}
        	playerOutput.fillLog(UserUtils.getLoginName(), isNew);
            playerOutputManager.save(playerOutput);
            String key = (isNew) ? "playerOutput.added" : "playerOutput.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:playerOutputform?id=" + playerOutput.getId();
            }
        }

        return success;
    }
}
