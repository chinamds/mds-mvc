package com.mds.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.common.dao.ZipCodeDao;
import com.mds.common.model.ZipCode;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class ZipCodeManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private ZipCodeManagerImpl manager;

    @Mock
    private ZipCodeDao dao;

    @Test
    public void testGetZipCode() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final ZipCode zipCode = new ZipCode();
        given(dao.get(id)).willReturn(zipCode);

        //when
        ZipCode result = manager.get(id);

        //then
        assertSame(zipCode, result);
    }

    @Test
    public void testGetZipCodes() {
        log.debug("testing getAll...");
        //given
        final List<ZipCode> zipCodes = new ArrayList<>();
        given(dao.getAll()).willReturn(zipCodes);

        //when
        List result = manager.getAll();

        //then
        assertSame(zipCodes, result);
    }

    @Test
    public void testSaveZipCode() {
        log.debug("testing save...");

        //given
        final ZipCode zipCode = new ZipCode();
        // enter all required fields
        zipCode.setCode("YuWjUkIlJuMlMxOsTeDiLjRoBeIhVcKySqXnIuBtXkUfErSkEfWyXwDaMkSqNyCgVnAiQgBpUlJiBpCcAeFiNjAbZoIsVrYlSzNf");

        given(dao.save(zipCode)).willReturn(zipCode);

        //when
        manager.save(zipCode);

        //then
        verify(dao).save(zipCode);
    }

    @Test
    public void testRemoveZipCode() {
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
