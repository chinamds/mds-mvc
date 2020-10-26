/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.utils.PrettyTimeUtils;
import com.mds.aiotplayer.sys.model.Notification;
import com.mds.aiotplayer.sys.model.NotificationTemplate;
import com.mds.aiotplayer.sys.exception.TemplateNotFoundException;
import com.mds.aiotplayer.sys.service.NotificationManager;
import com.mds.aiotplayer.sys.service.NotificationTemplateManager;
import com.mds.aiotplayer.sys.service.UserManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-7-8 下午5:25
 * <p>Version: 1.0
 */
@Service
public class NotificationApiImpl implements NotificationApi {

    @Autowired
    private NotificationTemplateManager notificationTemplateManager;

    @Autowired
    private NotificationManager notificationManager;
    
    @Autowired
    private UserManager userManager;

    @Autowired
    private PushApi pushApi;

    /**
     * 异步发送
     * @param userId 接收人用户编号
     * @param templateName 模板名称
     * @param context 模板需要的数据
     */
    @Async
    @Override
    public void notify(final Long userId, final String templateName, final Map<String, Object> context) {
        NotificationTemplate template = notificationTemplateManager.findByName(templateName);

        if(template == null) {
            throw new TemplateNotFoundException(templateName);
        }

        Notification data = new Notification();

        data.setUser(userManager.get(userId));
        data.setSource(template.getSource());
        data.setDate(new Date());

        String content = template.getTemplate();
        String title = template.getTitle();
        if(context != null) {
            for(String key : context.keySet()) {
                //TODO 如果量大可能有性能问题 需要调优
                title = title.replace("{" + key + "}", String.valueOf(context.get(key)));
                content = content.replace("{" + key + "}", String.valueOf(context.get(key)));
            }
        }

        data.setTitle(title);
        data.setContent(content);

        notificationManager.save(data);
        pushApi.pushNewNotification(userId, topFiveNotification(userId));
    }

    @Override
    public List<Map<String, Object>> topFiveNotification(final Long userId) {

        List<Map<String, Object>> dataList = Lists.newArrayList();

        Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter("user.id", SearchOperator.eq, userId);
//        searchable.addSearchFilter("read", SearchOperator.eq, Boolean.FALSE);
        searchable.addSort(Sort.Direction.DESC, "id");
        searchable.setPage(0, 5);

        Page<Notification> page = notificationManager.findPaging(searchable);

        for(Notification data : page.getContent()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", data.getId());
            map.put("title", data.getTitle());
            map.put("content", data.getContent());
            map.put("read", data.getRead());
            map.put("date", PrettyTimeUtils.prettyTime(data.getDate()));
            dataList.add(map);
        }

        return dataList;
    }
}
