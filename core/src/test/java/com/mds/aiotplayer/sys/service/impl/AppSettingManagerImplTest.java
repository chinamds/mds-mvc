/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.AppSettingDao;
import com.mds.aiotplayer.sys.model.AppSetting;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class AppSettingManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private AppSettingManagerImpl manager;

    @Mock
    private AppSettingDao dao;

    @Test
    public void testGetAppSetting() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final AppSetting appSetting = new AppSetting();
        given(dao.get(id)).willReturn(appSetting);

        //when
        AppSetting result = manager.get(id);

        //then
        assertSame(appSetting, result);
    }

    @Test
    public void testGetAppSettings() {
        log.debug("testing getAll...");
        //given
        final List<AppSetting> appSettings = new ArrayList<>();
        given(dao.getAll()).willReturn(appSettings);

        //when
        List result = manager.getAll();

        //then
        assertSame(appSettings, result);
    }

    @Test
    public void testSaveAppSetting() {
        log.debug("testing save...");

        //given
        final AppSetting appSetting = new AppSetting();
        // enter all required fields
        appSetting.setSettingName("TwMmQlSdUgEeMaZwAiOeNfYdGpTmYgDmKvVeHvTfGcDoLdKtSyQeDxXcLmSeBoCjOoXjWeKpNcTyKgPvHcXdGeHbFfHnRuUzYsJeTeSnOkLaDrCcZmMbJcDnJoFuHzVkNrHmSrAmCqRiHiLsIvXaFmLvDzJsRcSyYyEePiKyDsFaBbZoYkFnJkWhYjWiNhLtGiDoYcNv");
        appSetting.setSettingValue("RuFvEsLyLiDbHcMmGgMhNkOrFwGbJmMuArRpWxBvRuUbJcZxYgGbPkCmGsJhOqZgZeJnJqYzXsLwPmUzTqHwKpFmFiWeBvZbPzObNjHtTjMdCrYyXeAfFnPwExAnCgFzVoPaRmPqXxVlBdQlEmJlSqMzHdGpMyFeVpAtSoGmJzMaBoOaEcPwKtYiGdAgBbOwGiRsPkCvYfEcKeCdSmYmBdZcUqObIiHjSnIgXbWjScRzWkWbNoAzNoKrMlCrBwS");

        given(dao.save(appSetting)).willReturn(appSetting);

        //when
        manager.save(appSetting);

        //then
        verify(dao).save(appSetting);
    }

    @Test
    public void testRemoveAppSetting() {
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
