/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.util;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.model.search.filter.SearchFilter;
import com.mds.aiotplayer.common.model.search.filter.SearchFilterHelper;
//import com.sishuok.es.maintain.push.service.PushApi;
import com.mds.aiotplayer.sys.model.MyMessage;
import com.mds.aiotplayer.sys.model.MyMessageContent;
import com.mds.aiotplayer.sys.model.MyMessageReFw;
import com.mds.aiotplayer.sys.model.MyMessageRecipient;
import com.mds.aiotplayer.sys.service.MyMessageManager;
import com.mds.aiotplayer.sys.service.MyMessageRecipientManager;
//import com.mds.aiotplayer.common.model.Page;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.MessageType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.model.UserStatus;
import com.mds.aiotplayer.sys.service.UserManager;
import com.mds.aiotplayer.util.LoggerUtils;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-22 下午2:52
 * <p>Version: 1.0
 */
@Service
public class MyMessageApiImpl implements MyMessageApi {

    @Autowired
    private MyMessageManager messageManager;
    
    @Autowired
    private MyMessageRecipientManager messageRecipientManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private PushApi pushApi;

    @Override
    public Page<MyMessage> findUserMyMessage(Long userId, MessageFolder state, Pageable pageable) {
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);

        switch (state) {
            //for sender
            case drafts:
            case outbox:
                searchable.addSearchFilter("user.id", SearchOperator.eq, userId);
                searchable.addSearchFilter("sendFolder", SearchOperator.eq, state);
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
        }

        return messageManager.findPaging(searchable);
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
				result = getOriginalMessages(original.getOriginal(), result);
			}
			
			for (MyMessageReFw original : originals) {
				result.add(original.getOriginal());
			}
    	}
    	
    	return result;
    }
        
    private List<MyMessage> getReplyMessages(MyMessage message, List<MyMessage> result){
    	if (message.getReplies() != null && message.getReplies().size() > 0){
	    	List<MyMessageReFw> replies = message.getReplies();
			for (MyMessageReFw reply : replies) {
				result = getReplyMessages(reply.getMyMessage(), result);
			}
			
			for (MyMessageReFw reply : replies) {
				result.add(reply.getMyMessage());
			}
    	}
    	
    	return result;
    }
    

    @Override
    public void saveDraft(MyMessage message) {
        message.setMessageFolder(MessageFolder.drafts);
        for (MyMessageRecipient recipient : message.getMyMessageRecipients()) {
        	recipient.setMessageFolder(null);
        }
        if (message.getContent() != null) {
            message.getContent().setMyMessage(message);
        }
        messageManager.save(message);
    }

    @Override
    public void send(MyMessage message) {
        Date now = new Date();
        message.setSendDate(now);
        message.setMessageFolder(MessageFolder.outbox);
        message.setSentDate(now);
        for (MyMessageRecipient recipient : message.getMyMessageRecipients()) {
        	recipient.setMessageFolder(MessageFolder.inbox);
        	recipient.setRecievedTime(now);
        }
        message.getContent().setMyMessage(message);
        
        /*if (message.getParentId() != null) {
            MyMessage parent = messageManager.get(message.getParentId());
            if (parent != null) {
                message.setParentIds(parent.makeSelfAsParentIds());
            }
        }*/

        messageManager.save(message);

        for (MyMessageRecipient recipient : message.getMyMessageRecipients()) {
        	pushApi.pushUnreadMessage(recipient.getUser().getId(), countUnread(recipient.getUser().getId()));
        }
        //pushApi.pushUnreadMyMessage(message.getRecipientId(), countUnread(message.getRecipientId()));
    }
    
    @Override
    public void pushUnreadMessage(MyMessage message) {
    	for (MyMessageRecipient recipient : message.getMyMessageRecipients()) {
        	pushApi.pushUnreadMessage(recipient.getUser().getId(), countUnread(recipient.getUser().getId()));
        }
    }

    @Override
    public void sendSystemMessage(Long[] recipientIds, MyMessage message) {
        message.setType(MessageType.sys);

        for (Long recipientId : recipientIds) {
            if (recipientId == null) {
                continue;
            }
            /*MyMessage copyMyMessage = new MyMessage();
            MyMessageContent copyMyMessageContent = new MyMessageContent();
            copyMyMessageContent.setContent(message.getContent().getContent());

            BeanUtils.copyProperties(message, copyMyMessage);

            copyMyMessage.setContent(copyMyMessageContent);
            copyMyMessageContent.setMyMessage(copyMyMessage);

            copyMyMessage.setRecipientId(recipientId);

            send(copyMyMessage);*/
            MyMessageRecipient recipient = new MyMessageRecipient();
            recipient.setUser(userManager.get(recipientId));
            message.getMyMessageRecipients().add(recipient);
        }
        send(message);
    }

    @Async
    @Override
    public void sendSystemMessageToAllUser(MyMessage message) {
        //TODO 变更实现策略 使用异步发送

        int pn = 0;
        int pageSize = 100;

        Pageable pageable = null;
        Page<User> page = null;

        do {
            pageable = PageRequest.of(pn++, pageSize);
            page = userManager.getAllPaging(pageable);

            try {
                ((MyMessageApiImpl) AopContext.currentProxy()).doSendSystemMyMessage(page.getContent(), message);
            } catch (Exception e) {
            	LoggerUtils.error(this.getClass(), "send system message to all user error", e);
            }
        } while (page.hasNext());

    }

    public void doSendSystemMyMessage(List<User> recipients, MyMessage message) {
        List<Long> recipientIds = Lists.newArrayList();

        for (User recipient : recipients) {
            /*if (Boolean.TRUE.equals(recipient.getDeleted()) || recipient.getStatus() != UserStatus.enabled) {
                continue;
            }*/

            recipientIds.add(recipient.getId());
        }

        sendSystemMessage(recipientIds.toArray(new Long[0]), message);
    }

    @Override
    public void recycle(Long userId, Long messageId) {
        changeFolder(userId, messageId, MessageFolder.junk);
    }

    @Override
    public void recycle(Long userId, Long[] messageIds) {
        for (Long messageId : messageIds) {
            recycle(userId, messageId);
        }
    }

    @Override
    public void store(Long userId, Long messageId) {
        changeFolder(userId, messageId, MessageFolder.archive);
    }

    @Override
    public void store(Long userId, Long[] messageIds) {
        for (Long messageId : messageIds) {
            store(userId, messageId);
        }
    }

    @Override
    public void delete(Long userId, Long messageId) {
        changeFolder(userId, messageId, MessageFolder.deleted);
    }

    @Override
    public void delete(Long userId, Long[] messageIds) {
        for (Long messageId : messageIds) {
            delete(userId, messageId);
        }
    }


    /**
     * 变更状态
     * 根据用户id是收件人/发件人 决定更改哪个状态
     *
     * @param userId
     * @param messageId
     * @param state
     */
    private void changeFolder(Long userId, Long messageId, MessageFolder state) {
        MyMessage message = messageManager.get(messageId);
        if (message == null) {
            return;
        }
        if (userId.equals(message.getUser().getId())) {
            changeSenderFolder(message, state);
        } else {
            changeRecipientFolder(message, state);
        }
        messageManager.save(message);
    }

    @Override
    public void clearBox(Long userId, MessageFolder state) {
        switch (state) {
            case drafts:
                clearBox(userId, MessageFolder.drafts, MessageFolder.junk);
                break;
            case inbox:
                clearBox(userId, MessageFolder.inbox, MessageFolder.junk);
                break;
            case outbox:
                clearBox(userId, MessageFolder.outbox, MessageFolder.junk);
                break;
            case archive:
                clearBox(userId, MessageFolder.archive, MessageFolder.junk);
                break;
            case junk:
                clearBox(userId, MessageFolder.junk, MessageFolder.deleted);
                break;
            default:
                //none;
                break;
        }
    }

    @Override
    public void clearDraftBox(Long userId) {
        clearBox(userId, MessageFolder.drafts);
    }

    @Override
    public void clearInBox(Long userId) {
        clearBox(userId, MessageFolder.inbox);
    }

    @Override
    public void clearOutBox(Long userId) {
        clearBox(userId, MessageFolder.outbox);
    }

    @Override
    public void clearStoreBox(Long userId) {
        clearBox(userId, MessageFolder.archive);
    }

    @Override
    public void clearTrashBox(Long userId) {
        clearBox(userId, MessageFolder.junk);
    }

    private void clearBox(Long userId, MessageFolder oldFolder, MessageFolder newFolder) {
        if (oldFolder == MessageFolder.drafts
                || oldFolder == MessageFolder.outbox
                || oldFolder == MessageFolder.archive
                || oldFolder == MessageFolder.junk) {

            messageManager.changeMessageFolder(userId, oldFolder, newFolder);
        }

        if (oldFolder == MessageFolder.inbox
                || oldFolder == MessageFolder.archive
                || oldFolder == MessageFolder.junk) {
        	messageRecipientManager.changeRecipientFolder(userId, oldFolder, newFolder);
        }

    }

    @Override
    public Long countUnread(Long userId) {
        return messageRecipientManager.countUnread(userId);
    }

    @Override
    public void markRead(MyMessage message) {
        if (Boolean.TRUE.equals(message.getRead())) {
            return;
        }
        message.setRead(Boolean.TRUE);
        messageManager.save(message);
    }

    @Override
    public void markReplied(MyMessage message) {
        if (Boolean.TRUE.equals(message.getReplied())) {
            return;
        }
        message.setReplied(Boolean.TRUE);
        messageManager.save(message);
    }

    @Override
    public void markRead(final Long userId, final Long[] ids) {
    	messageRecipientManager.markRead(userId, ids);
    }
    
    @Override
    public void markRead(final Long userId, final Long messageId) {
    	messageRecipientManager.markRead(userId, messageId);
    }

    private void changeSenderFolder(MyMessage message, MessageFolder state) {
        message.setMessageFolder(state);
        message.setSentDate(new Date());
    }

    private void changeRecipientFolder(MyMessage message, MessageFolder state) {
    	for (MyMessageRecipient recipient : message.getMyMessageRecipients()) {
        	recipient.setMessageFolder(state);
        	recipient.setRecievedTime(new Date());
        }
    }
}
