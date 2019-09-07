package com.mds.sys.webapp.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.common.exception.SearchException;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.sys.service.MyCalendarManager;
import com.mds.sys.service.UserManager;
import com.mds.sys.util.UserUtils;
import com.mds.util.DateUtils;
import com.mds.i18n.util.I18nUtils;
import com.mds.util.StringUtils;
import com.mds.sys.webapp.bind.annotation.CurrentUser;
import com.mds.common.web.controller.BaseController;
import com.mds.sys.model.MyCalendar;
import com.mds.sys.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sys/myCalendars*")
public class MyCalendarController extends BaseController<MyCalendar, Long>{
    private static final long oneDayMillis = 24L * 60 * 60 * 1000;
    private static final String dataFormat = "yyyy-MM-dd HH:mm:ss";
    
    private MyCalendarManager myCalendarManager;
    private UserManager userManager = null;

    @Autowired
    public void setMyCalendarManager(MyCalendarManager myCalendarManager) {
        this.myCalendarManager = myCalendarManager;
    }
    
    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(myCalendarManager.search(query, MyCalendar.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(myCalendarManager.getAll());
        }
        return model;
    }
    
    @RequestMapping()
    public String list() {
        return viewName("list");
    }


    @RequestMapping("/load")
    @ResponseBody
    public Collection<Map> ajaxLoad(Searchable searchable) {
        searchable.addSearchFilter("user.id", SearchOperator.eq, UserUtils.getUserId());
        List<MyCalendar> calendarList = myCalendarManager.findAll(searchable);

        return Lists.<MyCalendar, Map>transform(calendarList, new Function<MyCalendar, Map>() {
            @Override
            public Map apply(MyCalendar c) {
                Map<String, Object> m = Maps.newHashMap();

                Date startDate = new Date();
                Date endDate = DateUtils.addDays(startDate, 1);
                boolean allDays = c.getStartTime() == null && c.getEndTime() == null;
                if (c.getStartDate() != null){
	                startDate = new Date(c.getStartDate().getTime());
	                if (c.getDuration() != null)
	                	endDate = DateUtils.addDays(startDate, c.getDuration() - 1);
	                if(!allDays) {
	                    startDate.setHours(c.getStartTime().getHours());
	                    startDate.setMinutes(c.getStartTime().getMinutes());
	                    startDate.setSeconds(c.getStartTime().getSeconds());
	                    endDate.setHours(c.getEndTime().getHours());
	                    endDate.setMinutes(c.getEndTime().getMinutes());
	                    endDate.setSeconds(c.getEndTime().getSeconds());
	                }
                }

                m.put("id", c.getId());
                m.put("start", DateFormatUtils.format(startDate, "yyyy-MM-dd HH:mm:ss"));
                m.put("end", DateFormatUtils.format(endDate, "yyyy-MM-dd HH:mm:ss"));
                m.put("allDay", allDays);
                m.put("title", c.getTitle());
                m.put("details", c.getDetails());
                if(StringUtils.isNotEmpty(c.getBackgroundColor())) {
                    m.put("backgroundColor", c.getBackgroundColor());
                    m.put("borderColor", c.getBackgroundColor());
                }
                if(StringUtils.isNotEmpty(c.getTextColor())) {
                    m.put("textColor", c.getTextColor());
                }
                return m;
            }
        });
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> newCalendar(@ModelAttribute("calendar") MyCalendar calendar, final HttpServletRequest request) {
    	Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
    	User loginUser = userManager.get(UserUtils.getUserId());
        calendar.setUser(loginUser);
        calendar.setCurrentUser(loginUser.getUsername());
        myCalendarManager.save(calendar);
        
        resultMap.put("message", I18nUtils.getString("myCalendar.added", request.getLocale()));
        resultMap.put("status", 200);
        return resultMap;
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> moveCalendar(
            @RequestParam("id") Long id,
            @RequestParam(value = "start", required = false) @DateTimeFormat(pattern = dataFormat) Date start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(pattern = dataFormat) Date end, final HttpServletRequest request
    ) {
    	Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
    	
    	MyCalendar calendar = myCalendarManager.get(id);
        if(end == null) {
            end = start;
        }

        calendar.setStartDate(start);
        calendar.setDuration((int)Math.ceil(1.0 * (end.getTime() - start.getTime()) / oneDayMillis));
        if(DateUtils.isSameDay(start, end)) {
            calendar.setDuration(1);
        }
        if(!"00:00:00".equals(DateFormatUtils.format(start, "HH:mm:ss"))) {
            calendar.setStartTime(start);
        }
        if(!"00:00:00".equals(DateFormatUtils.format(end, "HH:mm:ss"))) {
            calendar.setEndTime(end);
        }
        myCalendarManager.copyAndRemove(calendar);

        resultMap.put("message", I18nUtils.getString("myCalendar.added", request.getLocale()));
        resultMap.put("status", 200);
        return resultMap;
    }


    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> deleteCalendar(@RequestParam("id") Long id, final HttpServletRequest request) {
        myCalendarManager.remove(id);
        
        Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
        resultMap.put("message", I18nUtils.getString("myCalendar.deleted", request.getLocale()));
        resultMap.put("status", 200);
        
        return resultMap;
    }
    
    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String viewCalendar(@PathVariable("id") Long id, Model model) {
        model.addAttribute("calendar", myCalendarManager.get(id));
        return viewName("view");
    }


    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String showNewForm(
            @RequestParam(value = "start", required = false) @DateTimeFormat(pattern = dataFormat) Date start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(pattern = dataFormat) Date end,
            Model model) {

        setColorList(model);

        MyCalendar calendar = new MyCalendar();
        calendar.setDuration(1);
        if(start != null) {
            calendar.setStartDate(start);
            calendar.setDuration((int)Math.ceil(1.0 * (end.getTime() - start.getTime()) / oneDayMillis));
            if(DateUtils.isSameDay(start, end)) {
                calendar.setDuration(1);
            }
            if(!"00:00:00".equals(DateFormatUtils.format(start, "HH:mm:ss"))) {
                calendar.setStartTime(start);
            }
            if(!"00:00:00".equals(DateFormatUtils.format(end, "HH:mm:ss"))) {
                calendar.setEndTime(end);
            }

        }
        model.addAttribute("myCalendar", calendar);
        return viewName("newForm");
    }
    
    private void setColorList(Model model) {
        List<String> backgroundColorList = Lists.newArrayList();
        backgroundColorList.add("#3a87ad");
        backgroundColorList.add("#0d7813");
        backgroundColorList.add("#f2a640");
        backgroundColorList.add("#b373b3");
        backgroundColorList.add("#f2a640");
        backgroundColorList.add("#668cb3");
        backgroundColorList.add("#28754e");
        backgroundColorList.add("#8c66d9");

        model.addAttribute("backgroundColorList", backgroundColorList);
    }
}
