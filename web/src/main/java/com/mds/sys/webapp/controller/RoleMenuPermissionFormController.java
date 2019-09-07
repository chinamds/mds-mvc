package com.mds.sys.webapp.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.sys.service.MenuFunctionPermissionManager;
import com.mds.sys.service.RoleManager;
import com.mds.sys.util.UserUtils;
import com.mds.util.HelperFunctions;
import com.mds.sys.model.Role;
import com.mds.sys.model.RoleType;
import com.mds.common.webapp.controller.BaseFormController;

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
import java.util.Locale;

@Controller
@RequestMapping("/sys/roleMenuPermissionform*")
public class RoleMenuPermissionFormController extends BaseFormController {
    private RoleManager roleManager = null;
    private MenuFunctionPermissionManager menuFunctionPermissionManager = null;

    @Autowired
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }
    
    @Autowired
    public void setMenuFunctionPermissionManager(MenuFunctionPermissionManager menuFunctionPermissionManager) {
        this.menuFunctionPermissionManager = menuFunctionPermissionManager;
    }

    public RoleMenuPermissionFormController() {
        setCancelView("redirect:roles");
        setSuccessView("redirect:roles");
    }
    
    @RequestMapping(method = RequestMethod.GET)
    protected Model menuPermissions(HttpServletRequest request)
    throws Exception {   	
		Model model = new ExtendedModelMap();
    	Role role = new Role();
        String id = request.getParameter("id");
        if (!StringUtils.isBlank(id)) {
            role = roleManager.get(new Long(id));
        }
        model.addAttribute("role", role);

        return model;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Role role, Long[] menuPermissions, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(role, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/roleMenuPermissionform";
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
        	if (menuPermissions != null && menuPermissions.length > 0)
        		role.setMenuFunctionPermissions(menuFunctionPermissionManager.find(menuPermissions));
        	role.setCurrentUser(UserUtils.getLoginName());
        	
            roleManager.save(role);
            HelperFunctions.purgeCache();
            String key = (isNew) ? "role.added" : "role.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:roleMenuPermissionform?id=" + role.getId();
            }
        }

        return success;
    }
}
