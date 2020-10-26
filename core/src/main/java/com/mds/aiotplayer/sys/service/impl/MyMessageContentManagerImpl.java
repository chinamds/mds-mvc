/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.MyMessageContentDao;
import com.mds.aiotplayer.sys.model.MyMessageContent;
import com.mds.aiotplayer.sys.service.MyMessageContentManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("myMessageContentManager")
@WebService(serviceName = "MyMessageContentService", endpointInterface = "com.mds.aiotplayer.sys.service.MyMessageContentManager")
public class MyMessageContentManagerImpl extends GenericManagerImpl<MyMessageContent, Long> implements MyMessageContentManager {
    MyMessageContentDao myMessageContentDao;

    @Autowired
    public MyMessageContentManagerImpl(MyMessageContentDao myMessageContentDao) {
        super(myMessageContentDao);
        this.myMessageContentDao = myMessageContentDao;
    }
}