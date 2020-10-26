/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.i18n.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.i18n.service.CultureManager;
import com.mds.aiotplayer.i18n.model.Culture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/i18n/cultures*")
public class CultureController {
    private CultureManager cultureManager;

    @Autowired
    public void setCultureManager(CultureManager cultureManager) {
        this.cultureManager = cultureManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(cultureManager.search(query, Culture.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(cultureManager.getAll());
        }
        return model;
    }
}
