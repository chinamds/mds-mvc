package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.MenuFunctionManager;
import com.mds.aiotplayer.sys.service.PermissionManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.sys.exception.MenuFunctionExistsException;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;
import com.mds.aiotplayer.sys.model.MenuTarget;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.UserAction;
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
@RequestMapping("/sys/menuFunctionform*")
public class MenuFunctionFormController extends BaseFormController {
    private MenuFunctionManager menuFunctionManager = null;
    private PermissionManager permissionManager;

    @Autowired
    public void setMenuFunctionManager(MenuFunctionManager menuFunctionManager) {
        this.menuFunctionManager = menuFunctionManager;
    }
    
    @Autowired
    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public MenuFunctionFormController() {
        setCancelView("redirect:menuFunctions");
        setSuccessView("redirect:menuFunctions");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
    	model.addAttribute("menuTargets", MenuTarget.values());
    	/*model.addAttribute("userActions", UserAction.values());*/
    	
    	MenuFunction menu = new MenuFunction();
        String id = request.getParameter("id");
        if (!StringUtils.isBlank(id)) {
        	menu = menuFunctionManager.get(new Long(id));
        }else {        
	        String parentId = request.getParameter("parentId");
	        if (!StringUtils.isBlank(parentId)) {
	        	menu.setParent(menuFunctionManager.get(new Long(parentId)));
	        }
        }
        model.addAttribute("menuFunction", menu);
        model.addAttribute("resourceIds", getResourceId(request));

        return model;
    }
    
    private List<Map<String, Object>> getResourceId(HttpServletRequest request) {
    	List<ResourceId> rIds = ResourceId.getResourceIds();
    	List<Map<String, Object>> resourceIds = Lists.newArrayList();
		for(ResourceId resourceId : rIds) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("resourceId", resourceId);
			map.put("info", I18nUtils.getResourceId(resourceId, request));
			resourceIds.add(map);
		}
   	
    	return resourceIds;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(MenuFunction menuFunction, Long[] menuPermissions, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response, Model model)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        model.addAttribute("menuTargets", MenuTarget.values());
        if (validator != null) { // validator is null during testing
            validator.validate(menuFunction, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/menuFunctionform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (menuFunction.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            menuFunctionManager.remove(menuFunction.getId());
            saveMessage(request, getText("menuFunction.deleted", locale));
        } else {
        	if (menuFunction.getParent() == null || menuFunction.getParent().getId() == null) {
        		menuFunction.setParent(menuFunctionManager.get(1L));
        	}
        	if (menuPermissions != null) {
        		List<MenuFunctionPermission> menuFunctionPermissions = Lists.newArrayList();
        		for(Long permission : menuPermissions) {
        			menuFunctionPermissions.add(new MenuFunctionPermission(menuFunction, permissionManager.get(permission)) );
        		}
        		menuFunction.setMenuFunctionPermissions(menuFunctionPermissions);
        	}
        	menuFunction.setCurrentUser(UserUtils.getLoginName());
        	try {
        		menuFunctionManager.saveMenuFunction(menuFunction);
        		HelperFunctions.purgeCache();
        	}catch(final MenuFunctionExistsException e) {
        		errors.rejectValue("code", "menuFunction.existing.error",
                        new Object[] { menuFunction.getCode() }, "Menu or function existing");
        		menuFunctionManager.clear();
        		if (isNew) {
        			menuFunction.setId(null);
        		}

                return "sys/menuFunctionform";
        	}
            String key = (isNew) ? "menuFunction.added" : "menuFunction.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:menuFunctionform?id=" + menuFunction.getId();
            }
        }

        return success;
    }
}
