package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.MyMessageRecipientDao;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.MyMessageRecipient;
import com.mds.aiotplayer.sys.service.MyMessageRecipientManager;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;

@Service("myMessageRecipientManager")
@WebService(serviceName = "MyMessageRecipientService", endpointInterface = "com.mds.aiotplayer.sys.service.MyMessageRecipientManager")
public class MyMessageRecipientManagerImpl extends GenericManagerImpl<MyMessageRecipient, Long> implements MyMessageRecipientManager {
    MyMessageRecipientDao myMessageRecipientDao;

    @Autowired
    public MyMessageRecipientManagerImpl(MyMessageRecipientDao myMessageRecipientDao) {
        super(myMessageRecipientDao);
        this.myMessageRecipientDao = myMessageRecipientDao;
    }
    
    /**
     * 改变收件人人 消息的原状态为目标状态
     *
     * @param receiverId
     * @param oldFolder
     * @param newFolder
     */
    @Transactional
    @Override
    public Integer changeRecipientFolder(Long receiverId, MessageFolder oldFolder, MessageFolder newFolder) {
        Date changeDate = new Date();
        return myMessageRecipientDao.changeRecipientFolder(receiverId, oldFolder, newFolder, changeDate);
    }
    
    /**
     * 更改状态
     *
     * @param oldFolders
     * @param newFolder
     * @param expireDays 当前时间-过期天数 时间之前的消息将改变状态
     */
    @Transactional
    @Override
    public Integer changeRecipientFolder(ArrayList<MessageFolder> oldFolders, MessageFolder newFolder, int expireDays) {
        Date changeDate = new Date();
        Integer count = myMessageRecipientDao.changeRecipientFolder(oldFolders, newFolder, changeDate, DateUtils.addDays(changeDate, -expireDays));
        return count;
    }

    
    /**
     * count user inbox unread messages
     *
     * @param userId
     * @return
     */
    @Override
    public Long countUnread(Long userId) {
        return myMessageRecipientDao.countUnread(userId, MessageFolder.inbox);
    }


    @Transactional
    @Override
    public void markRead(final Long userId, final Long[] ids) {
        if(ArrayUtils.isEmpty(ids)) {
            return;
        }
        myMessageRecipientDao.markRead(userId, Arrays.asList(ids));
    }
    
    @Transactional
    @Override
    public void markRead(final Long userId, final Long messageId) {
        myMessageRecipientDao.markRead(userId, messageId);
    }
}