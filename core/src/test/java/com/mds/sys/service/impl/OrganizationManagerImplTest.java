package com.mds.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.sys.dao.OrganizationDao;
import com.mds.sys.model.Organization;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class OrganizationManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private OrganizationManagerImpl manager;

    @Mock
    private OrganizationDao dao;

    @Test
    public void testGetOrganization() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Organization organization = new Organization();
        given(dao.get(id)).willReturn(organization);

        //when
        Organization result = manager.get(id);

        //then
        assertSame(organization, result);
    }

    @Test
    public void testGetCompanies() {
        log.debug("testing getAll...");
        //given
        final List<Organization> companies = new ArrayList<>();
        given(dao.getAll()).willReturn(companies);

        //when
        List result = manager.getAll();

        //then
        assertSame(companies, result);
    }

    @Test
    public void testSaveOrganization() {
        log.debug("testing save...");

        //given
        final Organization organization = new Organization();
        // enter all required fields

        given(dao.save(organization)).willReturn(organization);

        //when
        manager.save(organization);

        //then
        verify(dao).save(organization);
    }

    @Test
    public void testRemoveOrganization() {
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
