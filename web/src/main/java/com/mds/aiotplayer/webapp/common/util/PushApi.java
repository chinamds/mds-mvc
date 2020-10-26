/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.util;

import java.util.List;
import java.util.Map;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-7-17 下午2:21
 * <p>Version: 1.0
 */
public interface PushApi {

    /**
     * 推送未读消息
     * @param userId
     */
    public void pushUnreadMessage(final Long userId, Long unreadMessageCount);

    /**
     * 推送未读通知
     * @param userId
     */
    public void pushNewNotification(final Long userId, List<Map<String, Object>> notifiations);

    /**
     * 离线用户，即清空用户的DefferedResult 这样就是新用户，可以即时得到通知
     *
     * 比如刷新主页时，需要offline
     *
     * @param userId
     */
    void offline(Long userId);
}
