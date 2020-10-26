/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.pm.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.pm.service.PlayerGroupManager;
import com.mds.aiotplayer.pm.model.PlayerGroup;

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
