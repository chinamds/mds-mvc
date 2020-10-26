/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.ps.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.ps.service.PlayerTunerManager;
import com.mds.aiotplayer.ps.model.PlayerTuner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/playerTuners*")
public class PlayerTunerController {
    private PlayerTunerManager playerTunerManager;

    @Autowired
    public void setPlayerTunerManager(PlayerTunerManager playerTunerManager) {
        this.playerTunerManager = playerTunerManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(playerTunerManager.search(query, PlayerTuner.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(playerTunerManager.getAll());
        }
        return model;
    }
}
