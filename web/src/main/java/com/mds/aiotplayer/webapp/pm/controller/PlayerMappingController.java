package com.mds.aiotplayer.webapp.pm.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.pm.service.PlayerMappingManager;
import com.mds.aiotplayer.pm.model.PlayerMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pm/playerMappings*")
public class PlayerMappingController {
    private PlayerMappingManager playerMappingManager;

    @Autowired
    public void setPlayerMappingManager(PlayerMappingManager playerMappingManager) {
        this.playerMappingManager = playerMappingManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(playerMappingManager.search(query, PlayerMapping.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(playerMappingManager.getAll());
        }
        return model;
    }
}
