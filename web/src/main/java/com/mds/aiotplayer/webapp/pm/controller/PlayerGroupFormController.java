package com.mds.aiotplayer.webapp.pm.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.pm.service.PlayerGroupManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.pm.model.PlayerGroup;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

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
@RequestMapping("/pm/playerGroupform*")
public class PlayerGroupFormController extends BaseFormController {
    private PlayerGroupManager playerGroupManager = null;

    @Autowired
    public void setPlayerGroupManager(PlayerGroupManager playerGroupManager) {
        this.playerGroupManager = playerGroupManager;
    }

    public PlayerGroupFormController() {
        setCancelView("redirect:playerGroups");
        setSuccessView("redirect:playerGroups");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected PlayerGroup showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return playerGroupManager.get(new Long(id));
        }
        
        PlayerGroup playerGroup = new PlayerGroup();
        if (!StringUtils.isBlank(request.getParameter("parentId"))) {
			playerGroup.setParent(playerGroupManager.get(new Long(request.getParameter("parentId"))));
		}

        return playerGroup;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(PlayerGroup playerGroup, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(playerGroup, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "playerGroupform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (playerGroup.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            playerGroupManager.remove(playerGroup.getId());
            saveMessage(request, getText("playerGroup.deleted", locale));
        } else {
        	/*if (playerGroup.getParent() == null){
        		if (!StringUtils.isBlank(request.getParameter("parentId"))) {
        			playerGroup.setParent(playerGroupManager.get(new Long(request.getParameter("parentId"))));
        		}
        	}*/
        	playerGroup.fillLog(UserUtils.getLoginName(), isNew);
            playerGroupManager.save(playerGroup);
            String key = (isNew) ? "playerGroup.added" : "playerGroup.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:playerGroupform?id=" + playerGroup.getId();
            }
        }

        return success;
    }
}
