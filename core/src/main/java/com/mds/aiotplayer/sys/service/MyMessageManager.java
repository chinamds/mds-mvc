/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.MyMessage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//@WebService
public interface MyMessageManager extends GenericManager<MyMessage, Long> {
	/**
     * Saves a myMessage's information
     *
     * @param myMessage the myMessage's information
     * @return updated myMessage
     * @throws MyMessageExistsException thrown when myMessage already exists
     */
    MyMessage saveMyMessage(MyMessage myMessage) throws RecordExistsException;
    
	Response removeMyMessage(String myMessageIds);
	
	/**
     * get message for the user specify by foler
     *
     * @param userId
     * @param state
     * @param pageable
     * @return
     */
    Page<MyMessage> findUserMyMessage(Long userId, String state, String searchTerm, Pageable pageable);
    
	/**
     * 改变发件人 消息的原状态为目标状态
     *
     * @param senderId
     * @param oldFolder
     * @param newFolder
     */
    Integer changeMessageFolder(Long senderId, MessageFolder oldFolder, MessageFolder newFolder);

    /**
     * 物理删除那些已删除的（即收件人和发件人 同时都删除了的）
     *
     * @param deletedFolder
     */
    Integer clearDeletedMessage(MessageFolder deletedFolder);

    /**
     * Change message folder
     *
     * @param oldFolders
     * @param newFolder
     * @param expireDays 当前时间-过期天数 时间之前的消息将改变状态
     */
    Integer changeMessageFolder(ArrayList<MessageFolder> oldFolders, MessageFolder newFolder, int expireDays);
    
    /**
     * find all ancestors and descendants for message
     *
     * @param message
     * @return
     */
    List<MyMessage> findAncestorsAndDescendants(MyMessage message);
    
    /**
     * Send Message
     *
     * @param message
     * @throws SQLException 
     */
    void send(MyMessage message) throws SQLException;
    
    /**
     * Mark message as read
     *
     * @param message
     */
    void markRead(MyMessage message);

    /**
     * mark as replied
     *
     * @param message
     */
    void markReplied(MyMessage message);


    /**
     * Mark user's messages as read
     *
     * @param userId user Id
     * @param ids message Ids
     */
    void markRead(Long userId, Long[] ids);
    
    /**
     * Mark user's message as read
     *
     * @param userId user Id
     * @param messageId message Id
     */
    void markRead(Long userId, Long messageId);
    
    /**
     * move user's message to deleted folder
     *
     * @param userId
     * @param messageId
     * @return
     */
    void recycle(Long userId, Long messageId);

    /**
     * move user's messages to deleted folder
     *
     * @param userId
     * @param messageIds
     * @return
     */
    void recycle(Long userId, Long[] messageIds);

    /**
     * move user's message to deleted folder
     *
     * @param userId
     * @param messageId
     * @return
     */
    void archive(Long userId, Long messageId);

    /**
     * move user's messages to archive folder
     *
     * @param userId
     * @param messageIds
     * @return
     */
    void archive(Long userId, Long[] messageIds);

    /**
     * delete user's message from deleted folder 
     *
     * @param userId
     * @param messageId
     */
    void delete(Long userId, Long messageId);

    /**
     * delete user's messages from deleted folder
     *
     * @param userId
     * @param messageIds
     */
    void delete(Long userId, Long[] messageIds);

    /**
     * Empty the specified folder for user
     *
     * @param userId
     * @param state
     */
    void clearFolder(Long userId, MessageFolder state);

    /**
     * Empty the draft folder for user
     *
     * @param userId
     */
    void clearDraft(Long userId);

    /**
     * Empty the inbox folder for user
     *
     * @param userId
     */
    void clearInbox(Long userId);

    /**
     * Empty the outbox folder for user
     *
     * @param userId
     */
    void clearOutbox(Long userId);

    /**
     * Empty the archive folder for user
     *
     * @param userId
     */
    void clearArchive(Long userId);

    /**
     * Empty the junk folder for user
     *
     * @param userId
     */
    void clearJunk(Long userId);

    /**
     * Total unread Inbox messages
     *
     * @param userId
     */
    Long countUnread(Long userId, MessageFolder messageFolder);
    
    /**
     * Mark user's messages as read
     *
     * @param userId user Id
     * @param ids message Ids
     */
    Response markRead(String userId, String ids);
        
    /**
     * move user's messages to deleted folder
     *
     * @param userId
     * @param messageIds
     * @return
     */
    Response recycle(String userId, String ids);

    /**
     * move user's messages to archive folder
     *
     * @param userId
     * @param messageIds
     * @return
     */
    Response archive(String userId, String ids);

    /**
     * delete user's messages from deleted folder
     *
     * @param userId
     * @param messageIds
     */
    Response delete(String userId, String ids);

    /**
     * Empty the specified folder for user
     *
     * @param userId
     * @param state
     */
    Response clearFolder(String userId, String messageFolder);

    /**
     * Empty the draft folder for user
     *
     * @param userId
     */
    Response clearDraft(String userId);

    /**
     * Empty the inbox folder for user
     *
     * @param userId
     */
    Response clearInbox(String userId);

    /**
     * Empty the outbox folder for user
     *
     * @param userId
     */
    Response clearOutbox(String userId);

    /**
     * Empty the archive folder for user
     *
     * @param userId
     */
    Response clearArchive(String userId);

    /**
     * Empty the junk folder for user
     *
     * @param userId
     */
    Response clearJunk(String userId);

    /**
     * Total unread Inbox messages
     *
     * @param userId
     */
    Long countUnread(String userId, String messageFolder);
}