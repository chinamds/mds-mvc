package com.mds.sys.webapp.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.sys.service.MenuFunctionManager;
import com.mds.sys.service.MenuFunctionPermissionManager;
import com.mds.sys.service.PermissionManager;
import com.mds.sys.util.UserUtils;
import com.mds.sys.model.MenuFunctionPermission;
import com.mds.common.webapp.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/sys/menuFunctionPermissionform*")
public class MenuFunctionPermissionFormController extends BaseFormController {
    private MenuFunctionPermissionManager menuFunctionPermissionManager = null;
    private MenuFunctionManager menuFunctionManager = null;
    private PermissionManager permissionManager = null;

    @Autowired
    public void setMenuFunctionPermissionManager(MenuFunctionPermissionManager menuFunctionPermissionManager) {
        this.menuFunctionPermissionManager = menuFunctionPermissionManager;
    }
    
    @Autowired
    public void setMenuFunctionManager(MenuFunctionManager menuFunctionManager) {
        this.menuFunctionManager = menuFunctionManager;
    }
    
    @Autowired
    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }


    public MenuFunctionPermissionFormController() {
        setCancelView("redirect:menuFunctionPermissions");
        setSuccessView("redirect:menuFunctionPermissions");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected MenuFunctionPermission showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return menuFunctionPermissionManager.get(new Long(id));
        }

        return new MenuFunctionPermission();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(MenuFunctionPermission menuFunctionPermission, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(menuFunctionPermission, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "menuFunctionPermissionform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (menuFunctionPermission.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            menuFunctionPermissionManager.remove(menuFunctionPermission.getId());
            saveMessage(request, getText("menuFunctionPermission.deleted", locale));
        } else {
        	if (menuFunctionPermission.getMenuFunction() == null){
        		if (!StringUtils.isBlank(request.getParameter("menuFunctionId"))) {
        			menuFunctionPermission.setMenuFunction(menuFunctionManager.get(new Long(request.getParameter("menuFunctionId"))));
        		}
        	}
        	if (menuFunctionPermission.getPermission() == null){
        		if (!StringUtils.isBlank(request.getParameter("permissionId"))) {
        			menuFunctionPermission.setPermission(permissionManager.get(new Long(request.getParameter("permissionId"))));
        		}
        	}
        	menuFunctionPermission.fillLog(UserUtils.getLoginName(), isNew);
            menuFunctionPermissionManager.save(menuFunctionPermission);
            String key = (isNew) ? "menuFunctionPermission.added" : "menuFunctionPermission.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:menuFunctionPermissionform?id=" + menuFunctionPermission.getId();
            }
        }

        return success;
    }
}
