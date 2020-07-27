package com.mds.aiotplayer.webapp.pl.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.pl.service.LayoutMstManager;
import com.mds.aiotplayer.pl.model.LayoutMst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pl/layoutMsts*")
public class LayoutMstController {
    private LayoutMstManager layoutMstManager;

    @Autowired
    public void setLayoutMstManager(LayoutMstManager layoutMstManager) {
        this.layoutMstManager = layoutMstManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(layoutMstManager.search(query, LayoutMst.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(layoutMstManager.getAll());
        }
        return model;
    }
}
