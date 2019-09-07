package com.mds.sys.service.impl;

import com.mds.sys.dao.MyMessageContentDao;
import com.mds.sys.model.MyMessageContent;
import com.mds.sys.service.MyMessageContentManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("myMessageContentManager")
@WebService(serviceName = "MyMessageContentService", endpointInterface = "com.mds.sys.service.MyMessageContentManager")
public class MyMessageContentManagerImpl extends GenericManagerImpl<MyMessageContent, Long> implements MyMessageContentManager {
    MyMessageContentDao myMessageContentDao;

    @Autowired
    public MyMessageContentManagerImpl(MyMessageContentDao myMessageContentDao) {
        super(myMessageContentDao);
        this.myMessageContentDao = myMessageContentDao;
    }
}