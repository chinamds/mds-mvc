/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.hrm.dao.IdentityTypeDao;
import com.mds.aiotplayer.hrm.model.IdentityType;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class IdentityTypeManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private IdentityTypeManagerImpl manager;

    @Mock
    private IdentityTypeDao dao;

    @Test
    public void testGetIdentityType() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final IdentityType identityType = new IdentityType();
        given(dao.get(id)).willReturn(identityType);

        //when
        IdentityType result = manager.get(id);

        //then
        assertSame(identityType, result);
    }

    @Test
    public void testGetIdentityTypes() {
        log.debug("testing getAll...");
        //given
        final List<IdentityType> identityTypes = new ArrayList<>();
        given(dao.getAll()).willReturn(identityTypes);

        //when
        List result = manager.getAll();

        //then
        assertSame(identityTypes, result);
    }

    @Test
    public void testSaveIdentityType() {
        log.debug("testing save...");

        //given
        final IdentityType identityType = new IdentityType();
        // enter all required fields
        identityType.setIdentityTypeCode("EtSbSoRxKoJcPoAxWnPnGrHtZcWtGuFjAdAoOaBrEpTdQeCsCqDgDgCdQcDiJxPuZuNsDaCtWwPpOwHnKbUePuNwWqZyCkSsRlSn");

        given(dao.save(identityType)).willReturn(identityType);

        //when
        manager.save(identityType);

        //then
        verify(dao).save(identityType);
    }

    @Test
    public void testRemoveIdentityType() {
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
