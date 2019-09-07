package com.mds.sys.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.sys.model.UserPhoto;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserPhotoDaoTest extends BaseDaoTestCase {
    @Autowired
    private UserPhotoDao userPhotoDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveUserPhoto() {
        UserPhoto userPhoto = new UserPhoto();

        // enter all required fields

        log.debug("adding userPhoto...");
        userPhoto = userPhotoDao.save(userPhoto);

        userPhoto = userPhotoDao.get(userPhoto.getId());

        assertNotNull(userPhoto.getId());

        log.debug("removing userPhoto...");

        userPhotoDao.remove(userPhoto.getId());

        // should throw DataAccessException 
        userPhotoDao.get(userPhoto.getId());
    }
}