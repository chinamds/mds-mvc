package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.service.RoleManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sys/roleAccessOrganizationform*")
public class RoleAccessOrganizationFormController extends BaseFormController {
    private RoleManager roleManager = null;
    private OrganizationManager organizationManager;

    @Autowired
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }
    
    @Autowired
    public void setOrganizationManager(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    public RoleAccessOrganizationFormController() {
        setCancelView("redirect:roles");
        setSuccessView("redirect:roles");
    }
        
    @RequestMapping(method = RequestMethod.GET)
    protected Model accessPermissions(HttpServletRequest request)
    throws Exception {
		Model model = new ExtendedModelMap();

    	Role role = new Role();
        String id = request.getParameter("id");
        if (!StringUtils.isBlank(id)) {
            role = roleManager.get(new Long(id));
        }
        model.addAttribute("role", role);
        //model.addAttribute("organizations", UserUtils.getOrganizationList());

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Role role, Long[] organizationIds, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(role, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/roleAccessOrganizationform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (role.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            roleManager.remove(role.getId());
            saveMessage(request, getText("role.deleted", locale));
        } else {
        	role = roleManager.get(role.getId());
        	if (role.getOrganization() != null && role.getOrganization().getId() != null)
        		role.setOrganization(organizationManager.get(role.getOrganization().getId()));
        	/*if (role.getOrganizations() != null && !role.getOrganizations().isEmpty()) {
        		//List<Organization> organizations = Lists.newArrayList();
        		List<String> organizationCodes = role.getOrganizations().stream().map(Organization::getCode).collect(Collectors.toList());
        		Searchable searchable = Searchable.newSearchable();
				searchable.addSearchFilter("code", SearchOperator.in, organizationCodes);
				role.setOrganizations(organizationManager.findAll(searchable));
        	}*/
        	if (organizationIds != null && organizationIds.length > 0)
        		role.setOrganizations(organizationManager.find(organizationIds));
        	role.setCurrentUser(UserUtils.getLoginName());
        		
            roleManager.save(role);
            HelperFunctions.purgeCache();
            String key = (isNew) ? "role.added" : "role.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:roleAccessOrganizationform?id=" + role.getId();
            }
        }

        return success;
    }
}
