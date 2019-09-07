package com.mds.sys.webapp.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.sys.service.PermissionManager;
import com.mds.sys.util.UserUtils;
import com.mds.sys.model.Permission;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.webapp.controller.BaseFormController;
import com.mds.core.UserAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/sys/permissionform*")
public class PermissionFormController extends BaseFormController {
    private PermissionManager permissionManager = null;

    @Autowired
    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public PermissionFormController() {
        setCancelView("redirect:permissions");
        setSuccessView("redirect:permissions");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
    	model.addAttribute("userActions", UserAction.values());
    	
        String id = request.getParameter("id");

        Permission permission = new Permission();
        if (!StringUtils.isBlank(id)) {
        	permission = permissionManager.get(new Long(id));
        }
        model.addAttribute("permission", permission);

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Permission permission, @RequestParam(value="selectActions", required = false) UserAction[] selectActions, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response, Model model)
    throws Exception {
    	model.addAttribute("userActions", UserAction.values());
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        //permission.setPermission(null);
        if (selectActions != null) {
	        permission.setPermission(0L);
	        for(UserAction userAction : selectActions) {
	        	permission.setPermission(permission.getPermission() | userAction.getValue());
	        }
        }

        if (validator != null) { // validator is null during testing
            validator.validate(permission, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/permissionform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (permission.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            permissionManager.remove(permission.getId());
            saveMessage(request, getText("permission.deleted", locale));
        } else {
        	permission.setCurrentUser(UserUtils.getLoginName());
        	try {
        		permissionManager.savePermission(permission);
        	}catch(final RecordExistsException e) {
        		errors.rejectValue("name", "permission.existing.error",
                        new Object[] { permission.getName() }, "permission name existing");
        		permissionManager.clear();
        		if (isNew) {
        			permission.setId(null);
        		}

                return "sys/permissionform";
        	}
        	
            //permissionManager.save(permission);
            String key = (isNew) ? "permission.added" : "permission.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:permissionform?id=" + permission.getId();
            }
        }

        return success;
    }
}
