package com.mds.sys.service.impl;

import com.mds.sys.dao.MyMessageDao;
import com.mds.sys.dao.MyMessageRecipientDao;
import com.mds.sys.model.MessageAction;
import com.mds.sys.model.MessageFolder;
import com.mds.sys.model.MyMessage;
import com.mds.sys.model.MyMessageContent;
import com.mds.sys.model.MyMessageReFw;
import com.mds.sys.model.MyMessageRecipient;
import com.mds.sys.model.RecipientType;
import com.mds.sys.service.MyMessageManager;
import com.mds.sys.service.MyMessageService;
import com.mds.util.ConvertUtil;
import com.mds.util.DateUtils;
import com.google.common.collect.Lists;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.model.search.filter.SearchFilter;
import com.mds.common.model.search.filter.SearchFilterHelper;
import com.mds.common.service.impl.GenericManagerImpl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("myMessageManager")
@WebService(serviceName = "MyMessageService", endpointInterface = "com.mds.sys.service.MyMessageService")
public class MyMessageManagerImpl extends GenericManagerImpl<MyMessage, Long> implements MyMessageManager, MyMessageService {
    MyMessageDao myMessageDao;
    MyMessageRecipientDao myMessageRecipientDao;

    @Autowired
    public MyMessageManagerImpl(MyMessageDao myMessageDao) {
        super(myMessageDao);
        this.myMessageDao = myMessageDao;
    }
    
    @Autowired
    public void setMyMessageRecipientDao(MyMessageRecipientDao myMessageRecipientDao) {
        this.myMessageRecipientDao = myMessageRecipientDao;
    }
    
    @Override
    public Page<MyMessage> findUserMyMessage(Long userId, String state, String searchTerm, Pageable pageable) {
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);
        searchable.addSort(Direction.ASC, "sendDate");

        if (StringUtils.isBlank(searchTerm)) {
        	searchable.addSearchFilter("user.id", SearchOperator.eq, userId);
            searchable.addSearchFilter("messageFolder", SearchOperator.eq, MessageFolder.valueOf(state));
            
            return findPaging(searchable);
	        /*switch (state) {
	            //for sender
	            case drafts:
	            case outbox:
	            case inbox:
	                searchable.addSearchFilter("user.id", SearchOperator.eq, userId);
	                searchable.addSearchFilter("messageFolder", SearchOperator.eq, state);
	                break;
	            //for recipient
	            case inbox:
	                searchable.addSearchFilter("myMessageRecipients.[].user.id", SearchOperator.eq, userId);
	                searchable.addSearchFilter("myMessageRecipients.[].messageFolder", SearchOperator.eq, state);
	                break;
	            //for sender or recipient
	            case archive:
	            case junk:
	                //sender
	                SearchFilter senderFilter = SearchFilterHelper.newCondition("user.id", SearchOperator.eq, userId);
	                SearchFilter senderFolderFilter = SearchFilterHelper.newCondition("sendFolder", SearchOperator.eq, state);
	                SearchFilter and1 = SearchFilterHelper.and(senderFilter, senderFolderFilter);
	
	                //recipient
	                SearchFilter recipientFilter = SearchFilterHelper.newCondition("myMessageRecipients.[].user.id", SearchOperator.eq, userId);
	                SearchFilter recipientFolderFilter = SearchFilterHelper.newCondition("myMessageRecipients.[].messageFolder", SearchOperator.eq, state);
	                SearchFilter and2 = SearchFilterHelper.and(recipientFilter, recipientFolderFilter);
	
	                searchable.or(and1, and2);
	        }*/
        }else {
        	searchable.addSearchFilter("userId", SearchOperator.ftqFilter, userId, "user");
            searchable.addSearchFilter("folder", SearchOperator.ftqFilter, state, "messageFolder");
            
        	return myMessageDao.search(searchable, searchTerm);	
        }
    }
    
    @Override
    public List<MyMessage> findAncestorsAndDescendants(MyMessage message) {
    	List<MyMessage> result = Lists.newArrayList();
    	if (message.getReplies() == null || message.getReplies().size() == 0){
    		result = getOriginalMessages(message, result);
    	}else{
    		result = getReplyMessages(message, result);
    	}
       
        result.remove(message);

        //删除 不可见的消息 如垃圾箱/已删除
        for (int i = result.size() - 1; i >= 0; i--) {
            MyMessage m = result.get(i);

            if ((m.getMessageFolder() == MessageFolder.junk || m.getMessageFolder() == MessageFolder.deleted)){
            	if (m.getUser().getId() == message.getUser().getId()) {
                    result.remove(i);
                }
                
                boolean removeRecipient = false;  
                for (int j = m.getMyMessageRecipients().size() - 1; j >= 0; j--) {
                	MyMessageRecipient recipient = m.getMyMessageRecipients().get(j);
                
                	if (recipient.getUser().getId() == message.getUser().getId()) {
                		m.getMyMessageRecipients().remove(j);
                		removeRecipient = true;
                    }
                }
                if (removeRecipient && m.getMyMessageRecipients().size() == 0){
                	result.remove(i);
                }
                
               /* if (m.getRecipientId() == message.getSenderId() &&
                        (m.getSenderFolder() == MessageFolder.junk || m.getSenderFolder() == MessageFolder.deleted)) {
                    result.remove(i);
                }*/
            }
        }

        return result;
    }
    
    private List<MyMessage> getOriginalMessages(MyMessage message, List<MyMessage> result){
    	if (message.getOriginals() != null && message.getOriginals().size() > 0){
	    	List<MyMessageReFw> originals = message.getOriginals();
			for (MyMessageReFw original : originals) {
				if (original.getOriginal() != null && original.getOriginal() != message) {
					result.add(original.getOriginal());
					result = getOriginalMessages(original.getOriginal(), result);
				}
			}
    	}
    	
    	return result;
    }
        
    private List<MyMessage> getReplyMessages(MyMessage message, List<MyMessage> result){
    	if (message.getReplies() != null && message.getReplies().size() > 0){
	    	List<MyMessageReFw> replies = message.getReplies();
			for (MyMessageReFw reply : replies) {
				if (reply.getMyMessage() != null && reply.getMyMessage() != message) {
					result.add(reply.getMyMessage());
					result = getReplyMessages(reply.getMyMessage(), result);
				}
			}
    	}
    	
    	return result;
    }
    
    /**
     * 改变发件人 消息的原状态为目标状态
     *
     * @param senderId
     * @param oldFolder
     * @param newFolder
     */
    public Integer changeMessageFolder(Long senderId, MessageFolder oldFolder, MessageFolder newFolder) {
        Date changeDate = new Date();
        return myMessageDao.changeMessageFolder(senderId, oldFolder, newFolder, changeDate);
    }

    /**
     * 物理删除那些已删除的（即收件人和发件人 同时都删除了的）
     *
     * @param deletedFolder
     */
    public Integer clearDeletedMessage(MessageFolder deletedFolder) {
        return myMessageDao.clearDeletedMessage(deletedFolder);
    }

    /**
     * 更改状态
     *
     * @param oldFolders
     * @param newFolder
     * @param expireDays 当前时间-过期天数 时间之前的消息将改变状态
     */
    public Integer changeMessageFolder(ArrayList<MessageFolder> oldFolders, MessageFolder newFolder, int expireDays) {
        Date changeDate = new Date();
        Integer count = myMessageDao.changeMessageFolder(oldFolders, newFolder, changeDate, DateUtils.addDays(changeDate, -expireDays));
        //count += myMessageDao.changeRecipientFolder(oldFolders, newFolder, changeDate, DateUtils.addDays(changeDate, -expireDays));
        return count;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void send(MyMessage message) {
        Date now = new Date();
        message.setSendDate(now);
        message.setMessageFolder(MessageFolder.outbox);
        message.setSentDate(now);
        for (MyMessageRecipient recipient : message.getMyMessageRecipients()) {
        	recipient.setMessageFolder(MessageFolder.inbox);
        	recipient.setRecievedTime(now);
        	recipient.setRecipientType(RecipientType.to);
        }
        message.getContent().setMyMessage(message);
        myMessageDao.saveMyMessage(message);
        
        for (MyMessageRecipient recipient : message.getMyMessageRecipients()) {
        	MyMessage copyMyMessage = new MyMessage();

            BeanUtils.copyProperties(message, copyMyMessage);
            
            copyMyMessage.setId(null);
            copyMyMessage.setReplies(null);
            MyMessageContent copyMyMessageContent = new MyMessageContent();
            copyMyMessageContent.setContent(message.getContent().getContent());
            //copyMyMessageContent.setMyMessage(copyMyMessage);
            Set<MyMessageContent> contents = new HashSet<MyMessageContent>();
            contents.add(copyMyMessageContent);
            copyMyMessage.setContents(contents);
            copyMyMessage.getContent().setMyMessage(copyMyMessage);
            
            copyMyMessage.setMessageFolder(MessageFolder.inbox);
            copyMyMessage.setUser(recipient.getUser());
            MyMessageReFw messageReFw = new MyMessageReFw(copyMyMessage, message);
            messageReFw.setMessageAction(MessageAction.rt);
            List<MyMessageReFw> originals = Lists.newArrayList();
            originals.add(messageReFw);
            copyMyMessage.setOriginals(originals);
            
            List<MyMessageRecipient> myMessageRecipients = Lists.newArrayList();
            for (MyMessageRecipient recipient1 : message.getMyMessageRecipients()) {
            	MyMessageRecipient copyrecipient = new MyMessageRecipient();
            	BeanUtils.copyProperties(recipient1, copyrecipient);
            	copyrecipient.setId(null);
            	copyrecipient.setMyMessage(copyMyMessage);
            	myMessageRecipients.add(copyrecipient);
            }
            copyMyMessage.setMyMessageRecipients(myMessageRecipients);

            myMessageDao.saveMyMessage(copyMyMessage);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void markRead(MyMessage message) {
        if (Boolean.TRUE.equals(message.getRead())) {
            return;
        }
        message.setRead(Boolean.TRUE);
        myMessageDao.save(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markReplied(MyMessage message) {
        if (Boolean.TRUE.equals(message.getReplied())) {
            return;
        }
        message.setReplied(Boolean.TRUE);
        myMessageDao.save(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markRead(final Long userId, final Long[] ids) {
    	myMessageDao.markRead(userId, Arrays.asList(ids));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void markRead(final Long userId, final Long messageId) {
    	MyMessage message = myMessageDao.get(messageId);
    	if (Boolean.TRUE.equals(message.getRead())) {
            return;
        }
        message.setRead(Boolean.TRUE);
        myMessageDao.save(message);
    	//myMessageRecipientDao.markRead(userId, messageId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void recycle(Long userId, Long messageId) {
        changeFolder(userId, messageId, MessageFolder.junk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recycle(Long userId, Long[] messageIds) {
        for (Long messageId : messageIds) {
            recycle(userId, messageId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void archive(Long userId, Long messageId) {
        changeFolder(userId, messageId, MessageFolder.archive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void archive(Long userId, Long[] messageIds) {
        for (Long messageId : messageIds) {
        	archive(userId, messageId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long userId, Long messageId) {
        changeFolder(userId, messageId, MessageFolder.deleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long userId, Long[] messageIds) {
        for (Long messageId : messageIds) {
            delete(userId, messageId);
        }
    }
    
    /**
     * chnage user's message folder
     * 
     *
     * @param userId
     * @param messageId
     * @param state
     */
    private void changeFolder(Long userId, Long messageId, MessageFolder state) {
        MyMessage message = get(messageId);
        if (message == null) {
            return;
        }
        
        message.setMessageFolder(state);
        myMessageDao.save(message);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearFolder(Long userId, MessageFolder state) {
        switch (state) {
            case drafts:
            	clearFolder(userId, MessageFolder.drafts, MessageFolder.deleted);
                break;
            case inbox:
            	clearFolder(userId, MessageFolder.inbox, MessageFolder.deleted);
                break;
            case outbox:
            	clearFolder(userId, MessageFolder.outbox, MessageFolder.deleted);
                break;
            case archive:
            	clearFolder(userId, MessageFolder.archive, MessageFolder.deleted);
                break;
            case junk:
            	clearFolder(userId, MessageFolder.junk, MessageFolder.deleted);
                break;
            default:
                //none;
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearDraft(Long userId) {
    	clearFolder(userId, MessageFolder.drafts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearInbox(Long userId) {
    	clearFolder(userId, MessageFolder.inbox);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearOutbox(Long userId) {
    	clearFolder(userId, MessageFolder.outbox);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearArchive(Long userId) {
    	clearFolder(userId, MessageFolder.archive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearJunk(Long userId) {
    	clearFolder(userId, MessageFolder.junk);
    }

    private void clearFolder(Long userId, MessageFolder oldFolder, MessageFolder newFolder) {
            changeMessageFolder(userId, oldFolder, newFolder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long countUnread(Long userId, MessageFolder messageFolder) {
    	Searchable searchable = Searchable.newSearchable();
		searchable.addSearchFilter("read", SearchOperator.eq, false);	
		searchable.addSearchFilter("messageFolder", SearchOperator.eq, messageFolder);
		searchable.addSearchFilter("user.id", SearchOperator.eq, userId);
		
    	return myMessageDao.count(searchable);
    }
    
    public Response markRead(String userId, String ids) {
        log.debug("mark message as read: " + ids);
        markRead(new Long(userId), ConvertUtil.StringtoLongArray(ids));
        
        log.info("message(id=" + ids + ") was successfully mark as read.");
        return Response.ok().build();
    }
    
    public Response recycle(String userId, String ids) {
    	log.debug("recycle message: " + ids);
    	recycle(new Long(userId), ConvertUtil.StringtoLongArray(ids));
        
        log.info("message(id=" + ids + ") was successfully recycle.");
        return Response.ok().build();
    }


    public Response archive(String userId, String ids) {
    	log.debug("archive message: " + ids);
    	archive(new Long(userId), ConvertUtil.StringtoLongArray(ids));
        
        log.info("message(id=" + ids + ") was successfully archive.");
        return Response.ok().build();
    }

    public Response delete(String userId, String ids) {
    	log.debug("delete message: " + ids);
    	delete(new Long(userId), ConvertUtil.StringtoLongArray(ids));
        
        log.info("message(id=" + ids + ") was successfully delete.");
        return Response.ok().build();
    }

    public Response clearFolder(String userId, String messageFolder) {
    	log.debug("clear message folder: " + messageFolder + "user: " + userId);
    	clearFolder(new Long(userId), MessageFolder.valueOf(messageFolder));
        
        log.info("message(messageFolder=" + messageFolder + ") was successfully clear.");
        return Response.ok().build();
    }

    public Response clearDraft(String userId) {
    	log.debug("clear draft folder for user: " + userId);
    	clearDraft(new Long(userId));
        
        log.info("message(draft folder for user=" + userId + ") was successfully clear.");
        return Response.ok().build();
    }

    public Response clearInbox(String userId) {
    	log.debug("clear inbox folder for user: " + userId);
    	clearInbox(new Long(userId));
        
        log.info("message(inbox folder for user=" + userId + ") was successfully clear.");
        return Response.ok().build();
    }

    public Response clearOutbox(String userId) {
    	log.debug("clear outbox folder for user: " + userId);
    	clearOutbox(new Long(userId));
        
        log.info("message(outbox folder for user=" + userId + ") was successfully clear.");
        return Response.ok().build();
    }

    public Response clearArchive(String userId) {
    	log.debug("clear archive folder for user: " + userId);
    	clearArchive(new Long(userId));
        
        log.info("message(archive folder for user=" + userId + ") was successfully clear.");
        return Response.ok().build();
    }

    public Response clearJunk(String userId) {
    	log.debug("clear junk folder for user: " + userId);
    	clearJunk(new Long(userId));
        
        log.info("message(junk folder for user=" + userId + ") was successfully clear.");
        return Response.ok().build();
    }

    public Long countUnread(String userId, String messageFolder) {
    	log.debug("count unread message with folder: " + messageFolder + "  for user: " + userId);
    	Long count = countUnread(new Long(userId), MessageFolder.valueOf(messageFolder));
        
        log.info("unread message(messageFolder=" + messageFolder + ") was successfully count.");
        
        return count;
        //return Response.ok().build();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public MyMessage getMyMessage(final String myMessageId) {
        return myMessageDao.get(new Long(myMessageId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MyMessage> getMyMessages() {
        return myMessageDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<MyMessage> getShowMyMessages(Integer limit, Integer offset){
    	Searchable searchable = Searchable.newSearchable();
		searchable.addSearchFilter("show", SearchOperator.eq, true);	
		searchable.addSearchFilter("dateFrom", SearchOperator.lte, Calendar.getInstance().getTime());
		//searchable.addSearchFilter("dateTo", SearchOperator.gte, Calendar.getInstance().getTime());
		
		 //sender
        SearchFilter dateToFilter = SearchFilterHelper.newCondition("dateTo", SearchOperator.gte, Calendar.getInstance().getTime());
        SearchFilter dateToFilter2 = SearchFilterHelper.newCondition("dateTo", SearchOperator.isNull, null);
        SearchFilter and1 = SearchFilterHelper.or(dateToFilter, dateToFilter2);
        searchable.addSearchFilter(and1);

		if (limit<0){
			return myMessageDao.findAll(searchable);
		}else {
			searchable.setPage(PageRequest.of(offset/limit, limit));
			return myMessageDao.find(searchable).getContent();
		}
    }
    
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<MyMessage> searchMyMessages(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return myMessageDao.search(pageable, new String[]{"content", "faceDefine.code"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> myMessagesSelect2(String searchTerm, Integer limit, Integer offset) {
    	if (StringUtils.isBlank(searchTerm))
    		return null;
    	
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return toSelect2Data(myMessageDao.search(pageable, new String[]{"content", "faceDefine.code"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> myMessagesTable(String userId, String messageFolder, String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Page<MyMessage> list =  findUserMyMessage(new Long(userId), messageFolder, searchTerm, pageable);
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", list.getContent());
		
		return resultData;
		
    }   

    /**
     * {@inheritDoc}
     */
    @Override
    public MyMessage saveMyMessage(final MyMessage myMessage) throws RecordExistsException {       
        try {
            return myMessageDao.saveMyMessage(myMessage);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("MyMessage '" + myMessage.getContent() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response removeMyMessage(final String myMessageIds) {
        log.debug("removing myMessage: " + myMessageIds);
        myMessageDao.remove(ConvertUtil.StringtoLongArray(myMessageIds));
        
        log.info("Content Mapping(id=" + myMessageIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
	 * convert myMessage data to select2 format(https://select2.org/data-sources/formats)
	 * {
	 *	  "results": [
	 *	    {
	 *	      "id": 1,
	 *	      "text": "Option 1"
	 *    	},
	 *	    {
	 *	      "id": 2,
	 *	      "text": "Option 2",
	 *	      "selected": true
	 *	    },
	 *	    {
	 *	      "id": 3,
	 *	      "text": "Option 3",
	 *	      "disabled": true
	 *	    }
	 *	  ]
	 *	}
	 * @param myMessages
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<MyMessage> myMessages){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (MyMessage u : myMessages) {
			//myMessage list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getContent());//myMessage name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//myMessage id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
}