package com.mds.pm.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.pm.service.PlayerGroupManager;
import com.mds.pm.model.PlayerGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pm/playerGroups*")
public class PlayerGroupController {
    private PlayerGroupManager playerGroupManager;

    @Autowired
    public void setPlayerGroupManager(PlayerGroupManager playerGroupManager) {
        this.playerGroupManager = playerGroupManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(playerGroupManager.search(query, PlayerGroup.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(playerGroupManager.getAll());
        }
        return model;
    }
}
