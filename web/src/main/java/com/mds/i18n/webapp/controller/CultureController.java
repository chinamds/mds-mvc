package com.mds.i18n.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.i18n.service.CultureManager;
import com.mds.i18n.model.Culture;

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
