/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.pm.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.pm.service.PlayerGroup2PlayerManager;
import com.mds.aiotplayer.pm.model.PlayerGroup2Player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pm/playerGroup2Players*")
public class PlayerGroup2PlayerController {
    private PlayerGroup2PlayerManager playerGroup2PlayerManager;

    @Autowired
    public void setPlayerGroup2PlayerManager(PlayerGroup2PlayerManager playerGroup2PlayerManager) {
        this.playerGroup2PlayerManager = playerGroup2PlayerManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(playerGroup2PlayerManager.search(query, PlayerGroup2Player.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(playerGroup2PlayerManager.getAll());
        }
        return model;
    }
}
