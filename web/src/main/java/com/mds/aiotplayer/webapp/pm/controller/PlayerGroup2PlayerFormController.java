package com.mds.aiotplayer.webapp.pm.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.pm.service.PlayerGroupManager;
import com.mds.aiotplayer.pm.service.PlayerManager;
import com.mds.aiotplayer.pm.service.PlayerGroup2PlayerManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.pm.model.PlayerGroup2Player;
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
@RequestMapping("/pm/playerGroup2Playerform*")
public class PlayerGroup2PlayerFormController extends BaseFormController {
    private PlayerGroup2PlayerManager playerGroup2PlayerManager = null;
    private PlayerManager playerManager = null;
    private PlayerGroupManager playerGroupManager = null;

    @Autowired
    public void setPlayerGroup2PlayerManager(PlayerGroup2PlayerManager playerGroup2PlayerManager) {
        this.playerGroup2PlayerManager = playerGroup2PlayerManager;
    }
    
    @Autowired
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }
    
    @Autowired
    public void setPlayerGroupManager(PlayerGroupManager playerGroupManager) {
        this.playerGroupManager = playerGroupManager;
    }

    public PlayerGroup2PlayerFormController() {
        setCancelView("redirect:playerGroup2Players");
        setSuccessView("redirect:playerGroup2Players");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected PlayerGroup2Player showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return playerGroup2PlayerManager.get(new Long(id));
        }

        return new PlayerGroup2Player();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(PlayerGroup2Player playerGroup2Player, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(playerGroup2Player, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "playerGroup2Playerform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (playerGroup2Player.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            playerGroup2PlayerManager.remove(playerGroup2Player.getId());
            saveMessage(request, getText("playerGroup2Player.deleted", locale));
        } else {
        	if (playerGroup2Player.getPlayer() == null){
	    		if (!StringUtils.isBlank(request.getParameter("playerId"))) {
	    			playerGroup2Player.setPlayer(playerManager.get(new Long(request.getParameter("playerId"))));
	    		}
	    	}
        	if (playerGroup2Player.getPlayerGroup() == null){
        		if (!StringUtils.isBlank(request.getParameter("playerGroupId"))) {
        			playerGroup2Player.setPlayerGroup(playerGroupManager.get(new Long(request.getParameter("playerGroupId"))));
        		}
        	}
        	playerGroup2Player.fillLog(UserUtils.getLoginName(), isNew);
            playerGroup2PlayerManager.save(playerGroup2Player);
            String key = (isNew) ? "playerGroup2Player.added" : "playerGroup2Player.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:playerGroup2Playerform?id=" + playerGroup2Player.getId();
            }
        }

        return success;
    }
}
