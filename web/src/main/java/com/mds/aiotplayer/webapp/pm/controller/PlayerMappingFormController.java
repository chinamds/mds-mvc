package com.mds.aiotplayer.webapp.pm.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.pm.service.PlayerGroupManager;
import com.mds.aiotplayer.pm.service.PlayerManager;
import com.mds.aiotplayer.pm.service.PlayerMappingManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.pm.model.PlayerMapping;
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
@RequestMapping("/pm/playerMappingform*")
public class PlayerMappingFormController extends BaseFormController {
    private PlayerMappingManager playerMappingManager = null;
    private PlayerManager playerManager = null;
    private PlayerGroupManager playerGroupManager = null;

    @Autowired
    public void setPlayerMappingManager(PlayerMappingManager playerMappingManager) {
        this.playerMappingManager = playerMappingManager;
    }
    
    @Autowired
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }
    
    @Autowired
    public void setPlayerGroupManager(PlayerGroupManager playerGroupManager) {
        this.playerGroupManager = playerGroupManager;
    }

    public PlayerMappingFormController() {
        setCancelView("redirect:playerMappings");
        setSuccessView("redirect:playerMappings");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected PlayerMapping showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return playerMappingManager.get(new Long(id));
        }

        return new PlayerMapping();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(PlayerMapping playerMapping, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(playerMapping, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "playerMappingform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (playerMapping.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            playerMappingManager.remove(playerMapping.getId());
            saveMessage(request, getText("playerMapping.deleted", locale));
        } else {
        	if (playerMapping.getPlayer() == null){
	    		if (!StringUtils.isBlank(request.getParameter("playerId"))) {
	    			playerMapping.setPlayer(playerManager.get(new Long(request.getParameter("playerId"))));
	    		}
	    	}
        	if (playerMapping.getPlayerGroup() == null){
        		if (!StringUtils.isBlank(request.getParameter("playerGroupId"))) {
        			playerMapping.setPlayerGroup(playerGroupManager.get(new Long(request.getParameter("playerGroupId"))));
        		}
        	}
        	playerMapping.fillLog(UserUtils.getLoginName(), isNew);
            playerMappingManager.save(playerMapping);
            String key = (isNew) ? "playerMapping.added" : "playerMapping.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:playerMappingform?id=" + playerMapping.getId();
            }
        }

        return success;
    }
}
