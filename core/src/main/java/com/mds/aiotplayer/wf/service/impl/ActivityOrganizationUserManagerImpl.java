package com.mds.aiotplayer.wf.service.impl;

import com.mds.aiotplayer.wf.dao.ActivityOrganizationUserDao;
import com.mds.aiotplayer.wf.model.ActivityOrganizationUser;
import com.mds.aiotplayer.wf.service.ActivityOrganizationUserManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("activityOrganizationUserManager")
@WebService(serviceName = "ActivityOrganizationUserService", endpointInterface = "com.mds.aiotplayer.wf.service.ActivityOrganizationUserManager")
public class ActivityOrganizationUserManagerImpl extends GenericManagerImpl<ActivityOrganizationUser, Long> implements ActivityOrganizationUserManager {
    ActivityOrganizationUserDao activityOrganizationUserDao;

    @Autowired
    public ActivityOrganizationUserManagerImpl(ActivityOrganizationUserDao activityOrganizationUserDao) {
        super(activityOrganizationUserDao);
        this.activityOrganizationUserDao = activityOrganizationUserDao;
    }
}