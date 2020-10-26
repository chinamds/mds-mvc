/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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
import com.mds.aiotplayer.cm.service.GalleryManager;
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
@RequestMapping("/sys/roleAccessGalleryform*")
public class RoleAccessGalleryFormController extends BaseFormController {
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

    public RoleAccessGalleryFormController() {
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
        model.addAttribute("galleries", galleryManager.findGalleries(role.getOrganization().getId()));

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Role role, Long[] galleryIds, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(role, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/roleAccessGalleryform";
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
        	if (galleryIds == null || galleryIds.length == 0) {
        		galleryIds = role.getGalleries().stream().map(r->r.getId()).toArray(Long[]::new);
        	}
        	
        	role = roleManager.get(role.getId());
        	if (galleryIds != null && galleryIds.length > 0)
        		role.setGalleries(galleryManager.find(galleryIds));
        	role.setCurrentUser(UserUtils.getLoginName());
        		
            roleManager.save(role);
            HelperFunctions.purgeCache();
            String key = (isNew) ? "role.added" : "role.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:roleAccessGalleryform?id=" + role.getId();
            }
        }

        return success;
    }
}
