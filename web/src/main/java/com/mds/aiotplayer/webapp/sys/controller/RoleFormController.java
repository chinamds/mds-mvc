package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.service.RoleManager;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.sys.exception.RoleExistsException;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.i18n.util.I18nUtils;

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
@RequestMapping("/sys/roleform*")
public class RoleFormController extends BaseFormController {
    private RoleManager roleManager = null;
    
    private GalleryManager galleryManager;
    
    @Autowired
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }
    
    @Autowired
    public void setGalleryManager(GalleryManager galleryManager) {
        this.galleryManager = galleryManager;
    }
    
    private OrganizationManager organizationManager;

    @Autowired
    public void setOrganizationManager(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    public RoleFormController() {
        setCancelView("redirect:roles");
        setSuccessView("redirect:roles");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
    	model.addAttribute("roleTypes", getRoleType(request));

    	Role role = new Role();
        String id = request.getParameter("id");
        if (!StringUtils.isBlank(id)) {
            role = roleManager.get(new Long(id));
        }else {
        	role.setOrganization(organizationManager.get(UserUtils.getUserOrganizationId()));
        }
        if (role.getOrganization() == null)
        	role.setOrganization(new Organization(1L));
        
        model.addAttribute("role", role);

        return model;
    }
    
    private List<Map<String, Object>> getRoleType(HttpServletRequest request) {
    	UserAccount user = UserUtils.getUser();
    	List<Map<String, Object>> roleTypes = Lists.newArrayList();
    	if (user.isSystem() || user.isRoleType(RoleType.ad)) {
    		for(RoleType roleType : RoleType.values()) {
    			Map<String, Object> map = Maps.newHashMap();
    			map.put("roleType", roleType);
    			map.put("info", I18nUtils.getRoleType(roleType, request));
    			roleTypes.add(map);
    		}
    		return roleTypes;
    	} else if (user.isRoleType(RoleType.oa)) {
    		for(RoleType roleType : RoleType.values()) {
    			if (roleType==RoleType.sa || roleType==RoleType.ad || roleType==RoleType.ur || roleType==RoleType.gt)
    				continue;
    			
    			Map<String, Object> map = Maps.newHashMap();
    			map.put("roleType", roleType);
    			map.put("info", I18nUtils.getRoleType(roleType, request));
    			roleTypes.add(map);
    		}
    	} else if (user.isRoleType(RoleType.ga)) {
    		for(RoleType roleType : RoleType.values()) {
    			if (roleType==RoleType.ga || roleType==RoleType.gu || roleType==RoleType.gg){   			
	    			Map<String, Object> map = Maps.newHashMap();
	    			map.put("roleType", roleType);
	    			map.put("info", I18nUtils.getRoleType(roleType, request));
	    			roleTypes.add(map);
    			}
    		}
    	}
    	
    	return roleTypes;
    }
    
    @RequestMapping(value = "/menuPermissions", method = RequestMethod.GET)
    protected String menuPermissions(HttpServletRequest request, Model model)
    throws Exception {   	
    	Role role = new Role();
        String id = request.getParameter("id");
        if (!StringUtils.isBlank(id)) {
            role = roleManager.get(new Long(id));
        }
        model.addAttribute("role", role);

        return "sys/roleMenuPermissionform";
    }
    
    @RequestMapping(value = "/accessPermissions", method = RequestMethod.GET)
    protected String accessPermissions(HttpServletRequest request, Model model)
    throws Exception {   	
    	Role role = new Role();
        String id = request.getParameter("id");
        if (!StringUtils.isBlank(id)) {
            role = roleManager.get(new Long(id));
        }
        model.addAttribute("role", role);
        model.addAttribute("organizations", UserUtils.getOrganizationList());

        return "sys/roleAccessPermissionform";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Role role, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        long oid = StringUtils.toLong(request.getParameter("organizationId"));
        if (validator != null) { // validator is null during testing
            validator.validate(role, errors);

            if (!UserUtils.hasRoleType(RoleType.sa) && !UserUtils.hasRoleType(RoleType.ad) && (oid == 0 || oid == Long.MIN_VALUE) ) {
                errors.rejectValue("organization", "errors.required", new Object[] { getText("role.organization", request.getLocale()) },
                        "Organization is a required field.");
            }
            
            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/roleform";
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
        	if (oid != 0 && oid != Long.MIN_VALUE) {
				role.setOrganization(organizationManager.get(oid));
			}else {
				if (UserUtils.hasRoleType(RoleType.sa) || UserUtils.hasRoleType(RoleType.ad)) {
					role.setOrganization(organizationManager.get(Organization.getRootId()));
				}
			}
        	if (!isNew) {
        		Role exists = roleManager.get(role.getId());
        		role.getOrganizations().addAll(exists.getOrganizations());
        		if (!RoleType.getRoleTypes("g").contains(role.getType())) {
        			role.getGalleries().addAll(exists.getGalleries());
        		}
        		role.getMenuFunctionPermissions().addAll(exists.getMenuFunctionPermissions());
        		role.getAlbums().addAll(exists.getAlbums());
        		roleManager.clear();
        	}
        	if (RoleType.getRoleTypes("g").contains(role.getType())) {
        		final String[] roleGalleries = request.getParameterValues("roleGalleries");
        		if (roleGalleries != null) {
        			role.getGalleries().clear();
                    for (final String galleryId : roleGalleries) {
                    	role.getGalleries().add(galleryManager.get(new Long(galleryId)));
                    }
                }
        	}
        	
        	role.setCurrentUser(UserUtils.getLoginName());
        	try {
        		roleManager.saveRole(role);
        		HelperFunctions.purgeCache();
        	}catch(final RoleExistsException e) {
        		errors.rejectValue("name", "role.existing.error",
                        new Object[] { role.getName() }, "role name existing");
        		roleManager.clear();
        		if (isNew) {
        			role.setId(null);
        		}

                return "sys/roleform";
        	}
        	
            //roleManager.save(role);
            String key = (isNew) ? "role.added" : "role.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:roleform?id=" + role.getId();
            }
        }

        return success;
    }
}
