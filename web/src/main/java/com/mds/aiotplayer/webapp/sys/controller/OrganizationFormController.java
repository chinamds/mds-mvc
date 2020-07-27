package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.sys.service.AreaManager;
import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.hrm.model.Staff;
import com.mds.aiotplayer.hrm.service.StaffManager;
import com.mds.aiotplayer.i18n.service.CultureManager;
import com.mds.aiotplayer.sys.exception.OrganizationExistsException;
import com.mds.aiotplayer.sys.model.Area;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/sys/organizationform*")
public class OrganizationFormController extends BaseFormController {
    private OrganizationManager organizationManager = null;
    private StaffManager staffManager = null;
    private AreaManager areaManager = null;
    private CultureManager cultureManager = null;

    @Autowired
    public void setOrganizationManager(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }
    
    @Autowired
    public void setStaffManager(StaffManager staffManager) {
        this.staffManager = staffManager;
    }
    
    @Autowired
    public void setAreaManager(AreaManager areaManager) {
        this.areaManager = areaManager;
    }
    
    @Autowired
    public void setCultureManager(CultureManager cultureManager) {
        this.cultureManager = cultureManager;
    }

    public OrganizationFormController() {
        setCancelView("redirect:organizations");
        setSuccessView("redirect:organizations");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Organization showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return organizationManager.get(new Long(id));
        }
        
        Organization organization = new Organization();
        String parentId = request.getParameter("parentId");
        if (!StringUtils.isBlank(parentId)) {
        	organization.setParent(organizationManager.get(new Long(parentId)));
        } else {
	        Staff staff = UserUtils.getStaffId() == Long.MIN_VALUE ? null : staffManager.get(UserUtils.getStaffId());
	        if (staff != null){
	        	organization.setParent(staff.getOrganization());
	        }
	        
	        if (staff != null && staff.getOrganization() != null) {
	        	organization.setArea(staff.getOrganization().getArea());
	        }
			organization.setPreferredlanguage(cultureManager.findOne(null));
        }
        
        return organization;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Organization organization, BindingResult errors, @RequestParam(value="logofile", required = false) byte[] logofile
    		, @RequestParam(value="removelogo", required = false) boolean removelogo, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(organization, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/organizationform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (organization.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            organizationManager.remove(organization.getId());
            saveMessage(request, getText("organization.deleted", locale));
        } else {
        	if (organization.getParent() == null || organization.getParent().getId() == null) {
        		organization.setParent(organizationManager.get(Organization.getRootId()));
        	}
        	
        	//organization.getOrganizationLogos().clear();
        	if (!removelogo) {
    	        if (logofile != null && logofile.length > 0) {
    	        	organization.addLogo(logofile);
    	        }else if(!isNew){
    	        	organization.addLogo(organizationManager.get(organization.getId()).getLogo());
    	        	organizationManager.clear();
    	        }
            }
        	
        	if (organization.getArea() == null || organization.getArea().getId() == null){
        		if (!StringUtils.isBlank(request.getParameter("areaId"))) {
                	organization.setArea(areaManager.get(new Long(request.getParameter("areaId"))));
        		}
        	}else {
        		//Searchable searchable = Searchable.newSearchable();
        		//searchable.addSearchFilter("code", SearchOperator.eq, organization.getArea().getCode());	
	        	//organization.setArea(areaManager.findAll(searchable).get(0));;
        		organization.setArea(areaManager.get(organization.getArea().getId()));
        	}
        	
        	//organization.setCurrentUser(request.getRemoteUser());
        	if (organization.getPreferredlanguage() != null && !StringUtils.isBlank(organization.getPreferredlanguage().getCultureCode())) {
        		Searchable searchable = Searchable.newSearchable();
        		searchable.addSearchFilter("cultureCode", SearchOperator.eq, organization.getPreferredlanguage().getCultureCode());	
	        	organization.setPreferredlanguage(cultureManager.findAll(searchable).get(0));
        	}else {
        		if (!StringUtils.isBlank(request.getParameter("cultureId"))) {
                	organization.setPreferredlanguage(cultureManager.get(new Long(request.getParameter("cultureId"))));
        		}
        	}
        	organization.setCurrentUser(UserUtils.getLoginName());
            try {
            	organizationManager.saveOrganization(organization);
        	}catch(final OrganizationExistsException e) {
        		errors.rejectValue("code", "organization.existing.error",
                        new Object[] { organization.getCode() }, "organization code existing");
        		organizationManager.clear();
        		if (isNew) {
        			organization.setId(null);
        		}

                return "sys/organizationform";
        	}
            
            String key = (isNew) ? "organization.added" : "organization.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:organizationform?id=" + organization.getId();
            }
        }

        return success;
    }
}
