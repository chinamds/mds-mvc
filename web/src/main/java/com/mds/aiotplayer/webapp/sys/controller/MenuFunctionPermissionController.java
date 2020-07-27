package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.sys.service.MenuFunctionManager;
import com.mds.aiotplayer.sys.service.MenuFunctionPermissionManager;
import com.mds.aiotplayer.sys.service.PermissionManager;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/sys/menuFunctionPermissions*")
public class MenuFunctionPermissionController {
    private MenuFunctionPermissionManager menuFunctionPermissionManager;
    private MenuFunctionManager menuFunctionManager;
    private PermissionManager permissionManager;

    @Autowired
    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Autowired
    public void setMenuFunctionManager(MenuFunctionManager menuFunctionManager) {
        this.menuFunctionManager = menuFunctionManager;
    }

    @Autowired
    public void setMenuFunctionPermissionManager(MenuFunctionPermissionManager menuFunctionPermissionManager) {
        this.menuFunctionPermissionManager = menuFunctionPermissionManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(menuFunctionPermissionManager.search(query, MenuFunctionPermission.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(menuFunctionPermissionManager.getAll());
        }
        return model;
    }
}
