package com.mds.wf.webapp.controller;

import com.mds.wf.service.ActivityManager;
import com.mds.common.exception.SearchException;
import com.mds.common.webapp.controller.AbstractBaseController;
import com.mds.wf.model.Activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wf/activities*")
public class ActivityController extends AbstractBaseController<Activity, Long> {
    private ActivityManager activityManager;

    @Autowired
    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        /*try {
            model.addAttribute(activityManager.search(query, Activity.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(activityManager.getAll());
        }*/
        return model;
    }
}
