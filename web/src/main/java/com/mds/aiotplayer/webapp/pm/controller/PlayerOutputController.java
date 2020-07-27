package com.mds.aiotplayer.webapp.pm.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.pm.service.PlayerOutputManager;
import com.mds.aiotplayer.pm.model.PlayerOutput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pm/playerOutputs*")
public class PlayerOutputController {
    private PlayerOutputManager playerOutputManager;

    @Autowired
    public void setPlayerOutputManager(PlayerOutputManager playerOutputManager) {
        this.playerOutputManager = playerOutputManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(playerOutputManager.search(query, PlayerOutput.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(playerOutputManager.getAll());
        }
        return model;
    }
}
