package com.mds.sys.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.sys.service.MyMessageContentManager;
import com.mds.sys.model.MyMessageContent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/myMessageContents*")
public class MyMessageContentController {
    private MyMessageContentManager myMessageContentManager;

    @Autowired
    public void setMyMessageContentManager(MyMessageContentManager myMessageContentManager) {
        this.myMessageContentManager = myMessageContentManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(myMessageContentManager.search(query, MyMessageContent.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(myMessageContentManager.getAll());
        }
        return model;
    }
}
