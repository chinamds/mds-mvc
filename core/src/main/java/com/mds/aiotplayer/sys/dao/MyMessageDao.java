package com.mds.aiotplayer.sys.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mds.aiotplayer.common.dao.GenericDao;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.MyMessage;

/**
 * An interface that provides a data management interface to the MyMessage table.
 */
public interface MyMessageDao extends GenericDao<MyMessage, Long> {
	int changeMessageFolder(Long senderId, MessageFolder oldFolder, MessageFolder newFolder, Date changeDate);

    int changeMessageFolder(ArrayList<MessageFolder> states, MessageFolder newFolder, Date changeDate, Date expireDate);
    
    int clearDeletedMessage(MessageFolder deletedFolder);
    List<MyMessage> getReplyMessages(Long replyMessageId);
    
    /**
     * Saves a MyMessage's information.
     * @param myMessage the object to be saved
     * @return the persisted MyMessage object
     */
    MyMessage saveMyMessage(MyMessage myMessage);
    
    /**
     * Mark user's messages as read
     *
     * @param userId user Id
     * @param ids message Ids
     */
    void markRead(Long userId, List<Long> ids);
    
    /**
     * Mark user's message as read
     *
     * @param userId user Id
     * @param messageId message Id
     */
    void markRead(Long userId, Long messageId);
}