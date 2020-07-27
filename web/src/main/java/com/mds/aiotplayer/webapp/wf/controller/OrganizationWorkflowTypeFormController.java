package com.mds.aiotplayer.webapp.wf.controller;

import com.mds.aiotplayer.wf.service.OrganizationWorkflowTypeManager;
import com.mds.aiotplayer.wf.model.OrganizationWorkflowType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.LongCollection;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.WorkflowType;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/wf/organizationWorkflowTypeform*")
public class OrganizationWorkflowTypeFormController extends BaseFormController {
    private OrganizationWorkflowTypeManager organizationWorkflowTypeManager = null;

    @Autowired
    public void setOrganizationWorkflowTypeManager(OrganizationWorkflowTypeManager organizationWorkflowTypeManager) {
        this.organizationWorkflowTypeManager = organizationWorkflowTypeManager;
    }
    
    private OrganizationManager organizationManager;

    @Autowired
    public void setOrganizationManager(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    public OrganizationWorkflowTypeFormController() {
        setCancelView("redirect:organizationWorkflowTypes");
        setSuccessView("redirect:organizationWorkflowTypes");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
        String id = request.getParameter("id");

        OrganizationWorkflowType organizationWorkflowType  = new OrganizationWorkflowType();
        if (!StringUtils.isBlank(id)) {
        	organizationWorkflowType = organizationWorkflowTypeManager.get(new Long(id));
        }else {
        	organizationWorkflowType.setOrganization(organizationManager.get(UserUtils.getUserOrganizationId()));
        }
        
        model.addAttribute("workflowTypes", getWorkflowType(request));
        model.addAttribute("organizationWorkflowType", organizationWorkflowType);

        return model;
    }
    
    private List<Map<String, Object>> getWorkflowType(HttpServletRequest request) {
    	List<Map<String, Object>> workflowTypes = Lists.newArrayList();
		for(WorkflowType workflowType : WorkflowType.values()) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("workflowType", workflowType);
			map.put("info", I18nUtils.getWorkflowType(workflowType, request));
			workflowTypes.add(map);
		}
   	
    	return workflowTypes;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(OrganizationWorkflowType organizationWorkflowType, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        organizationWorkflowType.setCurrentUser(UserUtils.getLoginName());
        long oid = StringUtils.toLong(request.getParameter("organizationId"));
        if (validator != null) { // validator is null during testing
            validator.validate(organizationWorkflowType, errors);
            
            if (!UserUtils.hasRoleType(RoleType.sa) && !UserUtils.hasRoleType(RoleType.ad) && (oid == Long.MIN_VALUE || oid == 0) ) {
                errors.rejectValue("organization", "errors.required", new Object[] { getText("organizationWorkflowType.organization", request.getLocale()) },
                        "Organization is a required field.");
            }

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "wf/organizationWorkflowTypeform";
            }
        }
        
        log.debug("entering 'onSubmit' method...");

        boolean isNew = (organizationWorkflowType.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            organizationWorkflowTypeManager.remove(organizationWorkflowType.getId());
            saveMessage(request, getText("organizationWorkflowType.deleted", locale));
        } else {
        	if (oid != Long.MIN_VALUE && oid != 0) {
        		organizationWorkflowType.setOrganization(organizationManager.get(oid));
			}else {
				if (UserUtils.hasRoleType(RoleType.sa) || UserUtils.hasRoleType(RoleType.ad)) {
					organizationWorkflowType.setOrganization(organizationManager.get(Organization.getRootId()));
				}
			}
        	
        	try {
        		organizationWorkflowTypeManager.saveOrganizationWorkflowType(organizationWorkflowType);
	        } catch (final RecordExistsException e) {
	        	organizationWorkflowTypeManager.clear();
	        	if (isNew) {
	        		organizationWorkflowType.setId(null);
	        	}
	        	
	        	errors.rejectValue("workflowType", "organizationWorkflowType.existing.error",
                        new Object[] { organizationWorkflowType.getWorkflowType().toString() }, "organization's WorkflowType existing");
	        	
	        	return "wf/organizationWorkflowTypeform";
	        }
            
            String key = (isNew) ? "organizationWorkflowType.added" : "organizationWorkflowType.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:organizationWorkflowTypeform?id=" + organizationWorkflowType.getId();
            }
        }

        return success;
    }
}
