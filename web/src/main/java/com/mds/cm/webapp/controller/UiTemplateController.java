package com.mds.cm.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.cm.service.UiTemplateManager;
import com.mds.cm.model.UiTemplate;

import com.mds.common.webapp.controller.AbstractBaseController;
import com.mds.common.webapp.controller.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cm/uitemplates*")
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
    
    @RequestMapping(value = {"", "main"}, method = RequestMethod.GET)
    public String main() {
        return viewName("main");
    }
}
