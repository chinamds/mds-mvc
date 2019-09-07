package com.mds.sys.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.sys.service.ModuleManager;
import com.mds.sys.model.Module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/sys/modules*")
public class ModuleController {
    private ModuleManager moduleManager;

    @Autowired
    public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(moduleManager.search(query, Module.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(moduleManager.getAll());
        }
        return model;
    }
}
