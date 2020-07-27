/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.webapp.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.mapper.JsonMapper;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.MyCalendarManager;
import com.mds.aiotplayer.sys.service.MyMessageManager;
import com.mds.aiotplayer.sys.service.MyMessageRecipientManager;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.webapp.sys.bind.annotation.CurrentUser;

import eu.bitwalker.useragentutils.UserAgent;

import com.mds.aiotplayer.webapp.common.util.PushApi;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>User: John Lee
 * <p>Date: 06/08/2017
 * <p>Version: 1.0
 */
@Controller
public class HomeController {
	protected final transient Logger log = LoggerFactory.getLogger(getClass());
	
	private MyMessageManager myMessageManager;
    private PushApi pushApi;
    private MyCalendarManager calendarManager;
    
    @Autowired
    public void setMyMessageManager(MyMessageManager myMessageManager) {
        this.myMessageManager = myMessageManager;
    }
	
	@Autowired
	public void setPushApi(PushApi pushApi) {
        this.pushApi = pushApi;
    }
    

    @Autowired
    public void setMyCalendarManager(MyCalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }
    
	
	@RequestMapping(value = "/{home:home;?.*}")
    public String home(Model model, final HttpServletRequest request) throws InvalidMDSRoleException {
		//List list = UserUtils.getMenuFunctionList(true);
		/*List topmenulist = Lists.newArrayList();
		for (int i=0; i < list.size(); i++)    
	   {
		   Menu menu=(Menu) list.get(i);
		   if (menu.getParent() == null)
		   {
			   topmenulist.add(menu);
		   }
	   }*/
		//model.addAttribute("menus", list);
		model.addAttribute("userMenuRepository", UserUtils.createMenuRepository(UserUtils.getUser(), request));
				
		//model.addAttribute("i18n", JsonMapper.toJsonString(I18nUtils.getStrings(null, request.getLocale())));
		//log.debug("home 'home' method...");
		if (UserUtils.isMobileDevice(request)){
			Long messageUnreadCount = myMessageManager.countUnread(UserUtils.getUserId(), MessageFolder.inbox);
	        model.addAttribute("messageUnreadCount", messageUnreadCount);

	        //The last 3 days of the calendar
	        model.addAttribute("calendarCount", calendarManager.countRecentlyCalendar(UserUtils.getUserId(), 2)); 
		}
		
		pushApi.offline(UserUtils.getUserId());

        //return UserUtils.isMobileDevice(request) ? "common/welcome" : "home";
		return "home";
    }
	
    @RequestMapping(value = "/welcome")
    public String welcome(Model model) {
		//User user = UserUtils.getUser();
        //unread message
        Long messageUnreadCount = myMessageManager.countUnread(UserUtils.getUserId(), MessageFolder.inbox);
        model.addAttribute("messageUnreadCount", messageUnreadCount);

        //The last 3 days of the calendar
        model.addAttribute("calendarCount", calendarManager.countRecentlyCalendar(UserUtils.getUserId(), 2)); 

        return "common/welcome";
    }
}
