/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.pl.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.pl.service.ZoneManager;
import com.mds.aiotplayer.pl.model.Zone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pl/zones*")
public class ZoneController {
    private ZoneManager zoneManager;

    @Autowired
    public void setZoneManager(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(zoneManager.search(query, Zone.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(zoneManager.getAll());
        }
        return model;
    }
}
