/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.google.common.collect.Maps;
import com.mds.aiotplayer.webapp.common.util.NotificationApi;
import com.mds.aiotplayer.webapp.common.util.PushService;
import com.mds.aiotplayer.webapp.common.util.MyMessageApi;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.MyMessageManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.webapp.sys.bind.annotation.CurrentUser;
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
