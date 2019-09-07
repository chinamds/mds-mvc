package com.mds.wf.webapp.controller;

import com.mds.wf.service.OrganizationWorkflowTypeManager;
import com.mds.common.exception.SearchException;
import com.mds.common.webapp.controller.AbstractBaseController;
import com.mds.wf.model.OrganizationWorkflowType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wf/organizationWorkflowTypes*")
public class OrganizationWorkflowTypeController extends AbstractBaseController<OrganizationWorkflowType, Long> {
    private OrganizationWorkflowTypeManager organizationWorkflowTypeManager;

    @Autowired
    public void setOrganizationWorkflowTypeManager(OrganizationWorkflowTypeManager organizationWorkflowTypeManager) {
        this.organizationWorkflowTypeManager = organizationWorkflowTypeManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        /*try {
            model.addAttribute(organizationWorkflowTypeManager.search(query, OrganizationWorkflowType.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(organizationWorkflowTypeManager.getAll());
        }*/
        return model;
    }
}
