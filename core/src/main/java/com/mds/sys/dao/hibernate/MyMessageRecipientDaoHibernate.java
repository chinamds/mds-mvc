package com.mds.sys.dao.hibernate;

import com.mds.sys.model.MessageFolder;
import com.mds.sys.model.MyMessageRecipient;
import com.mds.sys.dao.MyMessageRecipientDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.model.Parameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("myMessageRecipientDao")
public class MyMessageRecipientDaoHibernate extends GenericDaoHibernate<MyMessageRecipient, Long> implements MyMessageRecipientDao {

    public MyMessageRecipientDaoHibernate() {
        super(MyMessageRecipient.class);
    }
    
    public int changeRecipientFolder(Long recipientId, MessageFolder oldFolder, MessageFolder newFolder, Date changeDate){
    	return update("update MyMessageRecipient set messageFolder=:p1, recievedTime=:p2  where (user.id=:p3 and messageFolder=:p4)"
    			, new Parameter(newFolder, changeDate, recipientId, oldFolder));
    }
    
    public int changeRecipientFolder(ArrayList<MessageFolder> oldFolders, MessageFolder newFolder, Date changeDate, Date expireDate){
    	return update("update MyMessageRecipient set messageFolder=:p1, recievedTime=:p2  where (recievedTime<:p3 and messageFolder in (:p4))"
    			, new Parameter(newFolder, changeDate, expireDate, oldFolders));
    }
    
    public Long countUnread(Long userId, MessageFolder recipientFolder){
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter("user.id", SearchOperator.eq, userId);
        searchable.addSearchFilter("messageFolder", SearchOperator.eq, recipientFolder);
        searchable.addSearchFilter("read", SearchOperator.eq, false);
        
    	return count(searchable);
    }

    public void markRead(Long userId, List<Long> ids){
    	update("update MyMessageRecipient set read=true where (user.id=:p1 and myMessage.id in (:p2))"
    			, new Parameter(userId, ids));
    }
    
    public void markRead(Long userId, Long messageId){
    	update("update MyMessageRecipient set read=true where (user.id=:p1 and myMessage.id = :p2)"
    			, new Parameter(userId, messageId));
    }

	/**
     * {@inheritDoc}
     */
    public MyMessageRecipient saveMyMessageRecipient(MyMessageRecipient myMessageRecipient) {
        if (log.isDebugEnabled()) {
            log.debug("myMessageRecipient's id: " + myMessageRecipient.getId());
        }
        getSession().saveOrUpdate(myMessageRecipient);
        // necessary to throw a DataIntegrityViolation and catch it in MyMessageRecipientManager
        getSession().flush();
        return myMessageRecipient;
    }
}
