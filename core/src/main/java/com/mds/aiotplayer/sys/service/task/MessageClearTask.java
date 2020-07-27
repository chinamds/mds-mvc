/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.sys.service.task;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.sys.model.MyMessage;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.MessageType;
import com.mds.aiotplayer.sys.service.MyMessageManager;
import com.mds.aiotplayer.sys.service.MyMessageRecipientManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 清理 过期的/删除的消息
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-25 上午11:23
 * <p>Version: 1.0
 */
@Service
public class MessageClearTask {

    public static final int EXPIRE_DAYS_OF_ONE_YEAR = 366;
    public static final int EXPIRE_DAYS_OF_ONE_MONTH = 31;
    
    private static final Logger log = LoggerFactory.getLogger(MessageClearTask.class);

    @Autowired
    private MyMessageManager messageManager;
    
    @Autowired
    private MyMessageRecipientManager messageRecipientManager;

    public void autoClearExpiredOrDeletedmMessage() {
    	log.debug("auto Clean Expired Or Deleted Message...");
    	
        MessageClearTask messageClearTask = (MessageClearTask) AopContext.currentProxy();
        //1. Messsage in inbox or Outbox folder, transfer to junk folder
        messageClearTask.doClearInOrOutBox();
        //2.Message in junk folder, transfer to deleted folder
        messageClearTask.doClearTrashBox();
        //3、Clear all message in deleted folder
        messageClearTask.doClearDeletedMessage();
        
        log.debug("Clean Finished...");
    }


    public void doClearDeletedMessage() {
        messageManager.clearDeletedMessage(MessageFolder.deleted);
    }


    public void doClearInOrOutBox() {
        messageManager.changeMessageFolder(
                Lists.newArrayList(MessageFolder.inbox, MessageFolder.outbox),
                MessageFolder.junk,
                EXPIRE_DAYS_OF_ONE_YEAR
        );
        messageRecipientManager.changeRecipientFolder(
                Lists.newArrayList(MessageFolder.inbox, MessageFolder.outbox),
                MessageFolder.junk,
                EXPIRE_DAYS_OF_ONE_YEAR
        );
    }

    public void doClearTrashBox() {
        messageManager.changeMessageFolder(
                Lists.newArrayList(MessageFolder.junk),
                MessageFolder.deleted,
                EXPIRE_DAYS_OF_ONE_MONTH
        );
        
        messageRecipientManager.changeRecipientFolder(
                Lists.newArrayList(MessageFolder.junk),
                MessageFolder.deleted,
                EXPIRE_DAYS_OF_ONE_MONTH
        );
    }

}
