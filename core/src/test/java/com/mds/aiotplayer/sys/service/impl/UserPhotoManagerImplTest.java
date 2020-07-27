package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.UserPhotoDao;
import com.mds.aiotplayer.sys.model.UserPhoto;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class UserPhotoManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private UserPhotoManagerImpl manager;

    @Mock
    private UserPhotoDao dao;

    @Test
    public void testGetUserPhoto() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final UserPhoto userPhoto = new UserPhoto();
        given(dao.get(id)).willReturn(userPhoto);

        //when
        UserPhoto result = manager.get(id);

        //then
        assertSame(userPhoto, result);
    }

    @Test
    public void testGetUserPhotoes() {
        log.debug("testing getAll...");
        //given
        final List<UserPhoto> userPhotoes = new ArrayList<>();
        given(dao.getAll()).willReturn(userPhotoes);

        //when
        List result = manager.getAll();

        //then
        assertSame(userPhotoes, result);
    }

    @Test
    public void testSaveUserPhoto() {
        log.debug("testing save...");

        //given
        final UserPhoto userPhoto = new UserPhoto();
        // enter all required fields

        given(dao.save(userPhoto)).willReturn(userPhoto);

        //when
        manager.save(userPhoto);

        //then
        verify(dao).save(userPhoto);
    }

    @Test
    public void testRemoveUserPhoto() {
        log.debug("testing remove...");

        //given
        final Long id = -11L;
        willDoNothing().given(dao).remove(id);

        //when
        manager.remove(id);

        //then
        verify(dao).remove(id);
    }
}
