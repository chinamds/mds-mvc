package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.MyMessage;
import com.mds.aiotplayer.sys.dao.MyMessageDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("myMessageDao")
public class MyMessageDaoHibernate extends GenericDaoHibernate<MyMessage, Long> implements MyMessageDao {

    public MyMessageDaoHibernate() {
        super(MyMessage.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MyMessage saveMyMessage(MyMessage myMessage) {
        if (log.isDebugEnabled()) {
            log.debug("myMessage's id: " + myMessage.getId());
        }
        var result = super.save(preSave(myMessage));
        // necessary to throw a DataIntegrityViolation and catch it in MyMessageManager
        getEntityManager().flush();
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int changeMessageFolder(Long senderId, MessageFolder oldFolder, MessageFolder newFolder, Date changeDate){
    	return update("update MyMessage set sendFolder=:p1, messageFolder=:p2  where (user.id=:p3 and messageFolder=:p4)"
    			, new Parameter(newFolder, changeDate, senderId, oldFolder));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int changeMessageFolder(ArrayList<MessageFolder> states, MessageFolder newFolder, Date changeDate, Date expireDate){
    	return update("update MyMessage set messageFolder=:p1, sentDate=:p2  where (sentDate<:p3 and messageFolder in (:p4))"
    			, new Parameter(newFolder, changeDate, expireDate, states));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int clearDeletedMessage(MessageFolder deletedFolder){
    	/*return update("delete from MyMessage m where (sendFolder=:p1 and not exists (from m.myMessageRecipients where messageFolder <>:p1))"
    			, new Parameter(deletedFolder));*/
    	return update("delete from MyMessage m where messageFolder=:p1"
    			, new Parameter(deletedFolder));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<MyMessage> getReplyMessages(Long replyMessageId){
    	return find("from MyMessage m where (exists (from m.replies where id == :p1))"
    			, new Parameter(replyMessageId));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void markRead(Long userId, List<Long> ids){
    	update("update MyMessage set read=true where (user.id=:p1 and id in (:p2))"
    			, new Parameter(userId, ids));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void markRead(Long userId, Long messageId){
    	update("update MyMessage set read=true where (user.id=:p1 and id = :p2)"
    			, new Parameter(userId, messageId));
    }
}
