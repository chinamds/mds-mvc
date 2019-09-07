package com.mds.sys.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mds.common.dao.GenericDao;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.model.Parameter;
import com.mds.sys.model.MessageFolder;
import com.mds.sys.model.MyMessageRecipient;

/**
 * An interface that provides a data management interface to the MyMessageRecipient table.
 */
public interface MyMessageRecipientDao extends GenericDao<MyMessageRecipient, Long> {
	int changeRecipientFolder(Long recipientId, MessageFolder oldFolder, MessageFolder newFolder, Date changeDate);
	
    int changeRecipientFolder(ArrayList<MessageFolder> oldFolders, MessageFolder newFolder, Date changeDate, Date expireDate);
	
    Long countUnread(Long userId, MessageFolder recipientFolder);
    
    void markRead(Long userId, List<Long> ids);
    void markRead(Long userId, Long messageId);

	/**
     * Saves a myMessageRecipient's information.
     * @param myMessageRecipient the object to be saved
     * @return the persisted MyMessageRecipient object
     */
    MyMessageRecipient saveMyMessageRecipient(MyMessageRecipient myMessageRecipient);
}