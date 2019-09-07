/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.webapp.controller;

import com.google.common.collect.Maps;
import com.mds.common.webapp.util.NotificationApi;
import com.mds.common.webapp.util.PushService;
import com.mds.common.webapp.util.MyMessageApi;
import com.mds.sys.model.MessageFolder;
import com.mds.sys.model.User;
import com.mds.sys.service.MyMessageManager;
import com.mds.sys.util.UserUtils;
import com.mds.sys.webapp.bind.annotation.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 1、实时推送用户：消息和通知
 * <p>User: Zhang Kaitao
 * <p>Date: 13-7-16 下午2:08
 * <p>Version: 1.0
 */
@Controller
public class PushController {

    @Autowired
    private MyMessageManager myMessageManager;

    @Autowired
    private NotificationApi notificationApi;

    @Autowired
    private PushService pushService;

    /**
     * 获取页面的提示信息
     * @return
     */
    @RequestMapping(value = "/sys/polling")
    @ResponseBody
    public Object polling(HttpServletResponse resp) {
        resp.setHeader("Connection", "Keep-Alive");
        resp.addHeader("Cache-Control", "private");
        resp.addHeader("Pragma", "no-cache");

        Long userId = UserUtils.getUserId();
        if(userId == Long.MIN_VALUE) {
            return null;
        }
        //If first login user, return immediately
        if(!pushService.isOnline(userId)) {
            Long unreadMessageCount = myMessageManager.countUnread(userId, MessageFolder.inbox);
            List<Map<String, Object>> notifications = notificationApi.topFiveNotification(userId);

            Map<String, Object> data = Maps.newHashMap();
            data.put("unreadMessageCount", unreadMessageCount);
            data.put("notifications", notifications);
            pushService.online(userId);
            
            return data;
        } else {
            //long-polling
            return pushService.newDeferredResult(userId);
        }
    }
}
