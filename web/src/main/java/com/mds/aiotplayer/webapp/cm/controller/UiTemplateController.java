/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.cm.service.UiTemplateManager;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.webapp.common.controller.AbstractBaseController;

@Controller
@RequestMapping("/cm/uiTemplates*")
public class UiTemplateController extends AbstractBaseController<UiTemplate, Long> {
    private UiTemplateManager uiTemplateManager;

    @Autowired
    public void setUiTemplateManager(UiTemplateManager uiTemplateManager) {
        this.uiTemplateManager = uiTemplateManager;
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(uiTemplateManager.search(query, UiTemplate.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(uiTemplateManager.getAll());
        }
        
        return model;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public Model main() {
        return new ExtendedModelMap();
    }
}
