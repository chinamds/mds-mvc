/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.UserPhotoDao;
import com.mds.aiotplayer.sys.model.UserPhoto;
import com.mds.aiotplayer.sys.service.UserPhotoManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("userPhotoManager")
@WebService(serviceName = "UserPhotoService", endpointInterface = "com.mds.aiotplayer.service.UserPhotoManager")
public class UserPhotoManagerImpl extends GenericManagerImpl<UserPhoto, Long> implements UserPhotoManager {
    UserPhotoDao userPhotoDao;

    @Autowired
    public UserPhotoManagerImpl(UserPhotoDao userPhotoDao) {
        super(userPhotoDao);
        this.userPhotoDao = userPhotoDao;
    }
}