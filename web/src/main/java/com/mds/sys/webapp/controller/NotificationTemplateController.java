package com.mds.sys.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.sys.service.NotificationTemplateManager;
import com.mds.sys.model.NotificationTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/notificationTemplates*")
public class NotificationTemplateController {
    private NotificationTemplateManager notificationTemplateManager;

    @Autowired
    public void setNotificationTemplateManager(NotificationTemplateManager notificationTemplateManager) {
        this.notificationTemplateManager = notificationTemplateManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(notificationTemplateManager.search(query, NotificationTemplate.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(notificationTemplateManager.getAll());
        }
        return model;
    }
}
