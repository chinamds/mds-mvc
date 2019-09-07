package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.MessageFolder;
import com.mds.sys.model.MyMessageRecipient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.jws.WebService;

import org.apache.commons.lang3.ArrayUtils;

@WebService
public interface MyMessageRecipientManager extends GenericManager<MyMessageRecipient, Long> {
	/**
     * 改变收件人人 消息的原状态为目标状态
     *
     * @param receiverId
     * @param oldFolder
     * @param newFolder
     */
    Integer changeRecipientFolder(Long receiverId, MessageFolder oldFolder, MessageFolder newFolder);
    
    /**
     * 更改状态
     *
     * @param oldFolders
     * @param newFolder
     * @param expireDays 当前时间-过期天数 时间之前的消息将改变状态
     */
    Integer changeRecipientFolder(ArrayList<MessageFolder> oldFolders, MessageFolder newFolder, int expireDays);
	
	/**
     * count user inbox unread messages
     *
     * @param userId
     * @return
     */
    Long countUnread(Long userId) ;


    /**
     * mask messages read
     *
     * @param userId
     * @param ids
     * @return
     */
    void markRead(final Long userId, final Long[] ids);
    
    /**
     * mask message read
     *
     * @param userId
     * @param ids
     * @return
     */
    void markRead(final Long userId, final Long messageId);
}