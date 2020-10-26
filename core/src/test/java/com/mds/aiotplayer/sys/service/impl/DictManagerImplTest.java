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

import com.mds.aiotplayer.sys.dao.DictDao;
import com.mds.aiotplayer.sys.model.Dict;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class DictManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private DictManagerImpl manager;

    @Mock
    private DictDao dao;

    @Test
    public void testGetDict() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Dict dict = new Dict();
        given(dao.get(id)).willReturn(dict);

        //when
        Dict result = manager.get(id);

        //then
        assertSame(dict, result);
    }

    @Test
    public void testGetDicts() {
        log.debug("testing getAll...");
        //given
        final List<Dict> dicts = new ArrayList<>();
        given(dao.getAll()).willReturn(dicts);

        //when
        List result = manager.getAll();

        //then
        assertSame(dicts, result);
    }

    @Test
    public void testSaveDict() {
        log.debug("testing save...");

        //given
        final Dict dict = new Dict();
        // enter all required fields

        given(dao.save(dict)).willReturn(dict);

        //when
        manager.save(dict);

        //then
        verify(dao).save(dict);
    }

    @Test
    public void testRemoveDict() {
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
