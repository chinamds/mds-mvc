/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.common.webapp.util;

import com.mds.sys.model.MyMessage;
import com.mds.sys.model.MessageFolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-22 下午2:52
 * <p>Version: 1.0
 */
public interface MyMessageApi {
    public static final String REPLY_PREFIX = "Reply：";
    public static final String FOWRARD_PREFIX = "Forward：";
    public static final String FOWRARD_TEMPLATE = "<br/><br/>-----------Forward Message------------<br/>From:%s<br/>To：%s<br/>Subject：%s<br/><br/>%s";

    /**
     * 得到用户 指定状态的消息
     *
     * @param userId
     * @param state
     * @param pageable
     * @return
     */
    Page<MyMessage> findUserMyMessage(Long userId, MessageFolder state, Pageable pageable);

    /**
     * 查询消息的祖先 和 后代
     *
     * @param message
     * @return
     */
    List<MyMessage> findAncestorsAndDescendants(MyMessage message);

    /**
     * 保存草稿
     *
     * @param message
     */
    void saveDraft(MyMessage message);

    /**
     * 发送消息
     *
     * @param message
     */
    void send(MyMessage message);

    /**
     * 发送系统消息给多个人
     *
     * @param recipientIds
     * @param message
     */
    void sendSystemMessage(Long[] recipientIds, MyMessage message);

    /**
     * 发送系统消息给所有人
     *
     * @param message
     */
    void sendSystemMessageToAllUser(MyMessage message);
    
    /**
     * push Unread Message to all recipient
     *
     * @param message
     */
    void pushUnreadMessage(MyMessage message);

    /**
     * 将消息移动到垃圾箱
     *
     * @param userId
     * @param messageId
     * @return
     */
    void recycle(Long userId, Long messageId);

    /**
     * 批量将消息移动到垃圾箱
     *
     * @param userId
     * @param messageIds
     * @return
     */
    void recycle(Long userId, Long[] messageIds);

    /**
     * 将消息保存到收藏箱
     *
     * @param userId
     * @param messageId
     * @return
     */
    void store(Long userId, Long messageId);

    /**
     * 批量将消息保存到收藏箱
     *
     * @param userId
     * @param messageIds
     * @return
     */
    void store(Long userId, Long[] messageIds);

    /**
     * 从垃圾箱删除消息
     *
     * @param userId
     * @param messageId
     */
    void delete(Long userId, Long messageId);

    /**
     * 从垃圾箱删除消息
     *
     * @param userId
     * @param messageIds
     */
    void delete(Long userId, Long[] messageIds);

    /**
     * 清空指定状态的消息
     *
     * @param userId
     * @param state
     */
    void clearBox(Long userId, MessageFolder state);

    /**
     * 清空草稿箱
     *
     * @param userId
     */
    void clearDraftBox(Long userId);

    /**
     * 清空收件箱
     *
     * @param userId
     */
    void clearInBox(Long userId);

    /**
     * 清空收件箱
     *
     * @param userId
     */
    void clearOutBox(Long userId);

    /**
     * 清空收藏箱
     *
     * @param userId
     */
    void clearStoreBox(Long userId);

    /**
     * 清空垃圾箱
     *
     * @param userId
     */
    void clearTrashBox(Long userId);

    /**
     * 未读收件箱消息总数
     *
     * @param userId
     */
    Long countUnread(Long userId);


    /**
     * 标识为已读
     *
     * @param message
     */
    void markRead(MyMessage message);

    /**
     * 标识为已回复
     *
     * @param message
     */
    void markReplied(MyMessage message);


    void markRead(Long userId, Long[] ids);
    
    void markRead(Long userId, Long messageId);
}
