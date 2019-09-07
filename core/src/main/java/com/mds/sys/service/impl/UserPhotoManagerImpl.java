package com.mds.sys.service.impl;

import com.mds.sys.dao.UserPhotoDao;
import com.mds.sys.model.UserPhoto;
import com.mds.sys.service.UserPhotoManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("userPhotoManager")
@WebService(serviceName = "UserPhotoService", endpointInterface = "com.mds.service.UserPhotoManager")
public class UserPhotoManagerImpl extends GenericManagerImpl<UserPhoto, Long> implements UserPhotoManager {
    UserPhotoDao userPhotoDao;

    @Autowired
    public UserPhotoManagerImpl(UserPhotoDao userPhotoDao) {
        super(userPhotoDao);
        this.userPhotoDao = userPhotoDao;
    }
}