package com.mds.cm.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.cm.service.ContentTemplateManager;
import com.mds.cm.model.ContentTemplate;

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
@RequestMapping("/cm/contenttemplates*")
public class ContentTemplateController extends AbstractBaseController<ContentTemplate, Long> {
    private ContentTemplateManager contentTemplateManager;

    @Autowired
    public void setContentTemplateManager(ContentTemplateManager contentTemplateManager) {
        this.contentTemplateManager = contentTemplateManager;
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(contentTemplateManager.search(query, ContentTemplate.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(contentTemplateManager.getAll());
        }
        return model;
    }
    
    @RequestMapping(value = {"", "main"}, method = RequestMethod.GET)
    public String main() {
        return viewName("main");
    }
}
