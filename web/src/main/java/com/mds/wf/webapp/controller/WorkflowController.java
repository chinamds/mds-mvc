package com.mds.wf.webapp.controller;

import com.mds.wf.service.WorkflowManager;
import com.mds.common.exception.SearchException;
import com.mds.common.webapp.controller.AbstractBaseController;
import com.mds.wf.model.Workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wf/workflows*")
public class WorkflowController extends AbstractBaseController<Workflow, Long> {
    private WorkflowManager workflowManager;

    @Autowired
    public void setWorkflowManager(WorkflowManager workflowManager) {
        this.workflowManager = workflowManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        /*try {
            model.addAttribute(workflowManager.search(query, Workflow.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(workflowManager.getAll());
        }*/
        return model;
    }
    
}
