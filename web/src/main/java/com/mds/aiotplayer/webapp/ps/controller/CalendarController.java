package com.mds.aiotplayer.webapp.ps.controller;

import com.mds.aiotplayer.ps.service.CalendarManager;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.ps.model.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ps/calendars*")
public class CalendarController {
    private CalendarManager calendarManager;

    @Autowired
    public void setCalendarManager(CalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(calendarManager.search(query, Calendar.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(calendarManager.getAll());
        }
        return model;
    }
}
