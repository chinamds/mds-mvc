package com.mds.sys.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.sys.service.TaskDefinitionManager;
import com.mds.sys.model.TaskDefinition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/taskDefinitions*")
public class TaskDefinitionController {
    private TaskDefinitionManager taskDefinitionManager;

    @Autowired
    public void setTaskDefinitionManager(TaskDefinitionManager taskDefinitionManager) {
        this.taskDefinitionManager = taskDefinitionManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(taskDefinitionManager.search(query, TaskDefinition.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(taskDefinitionManager.getAll());
        }
        return model;
    }
}
