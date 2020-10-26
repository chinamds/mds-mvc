/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.ps.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.ps.service.PlayerTunerManager;
import com.mds.aiotplayer.ps.service.ChannelManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.pm.service.PlayerManager;
import com.mds.aiotplayer.ps.model.PlayerTuner;
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
@RequestMapping("/playerTunerform*")
public class PlayerTunerFormController extends BaseFormController {
    private PlayerTunerManager playerTunerManager = null;
    private ChannelManager channelManager = null;
    private PlayerManager playerManager = null;

    @Autowired
    public void setPlayerTunerManager(PlayerTunerManager playerTunerManager) {
        this.playerTunerManager = playerTunerManager;
    }
    
    @Autowired
    public void setChannelManager(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }
    
    @Autowired
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public PlayerTunerFormController() {
        setCancelView("redirect:playerTuners");
        setSuccessView("redirect:playerTuners");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected PlayerTuner showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return playerTunerManager.get(new Long(id));
        }

        return new PlayerTuner();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(PlayerTuner playerTuner, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(playerTuner, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "playerTunerform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (playerTuner.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            playerTunerManager.remove(playerTuner.getId());
            saveMessage(request, getText("playerTuner.deleted", locale));
        } else {
        	if (playerTuner.getChannel() == null){
        		if (!StringUtils.isBlank(request.getParameter("channelId"))) {
        			playerTuner.setChannel(channelManager.get(new Long(request.getParameter("channelId"))));
        		}
        	}
        	if (playerTuner.getPlayer() == null){
        		if (!StringUtils.isBlank(request.getParameter("playerId"))) {
        			playerTuner.setPlayer(playerManager.get(new Long(request.getParameter("playerId"))));
        		}
        	}
        	playerTuner.fillLog(UserUtils.getLoginName(), isNew);
            playerTunerManager.save(playerTuner);
            String key = (isNew) ? "playerTuner.added" : "playerTuner.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:playerTunerform?id=" + playerTuner.getId();
            }
        }

        return success;
    }
}
