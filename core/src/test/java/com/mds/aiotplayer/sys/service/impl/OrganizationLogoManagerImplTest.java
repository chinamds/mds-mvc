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

import com.mds.aiotplayer.sys.dao.OrganizationLogoDao;
import com.mds.aiotplayer.sys.model.OrganizationLogo;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class OrganizationLogoManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private OrganizationLogoManagerImpl manager;

    @Mock
    private OrganizationLogoDao dao;

    @Test
    public void testGetOrganizationLogo() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final OrganizationLogo organizationLogo = new OrganizationLogo();
        given(dao.get(id)).willReturn(organizationLogo);

        //when
        OrganizationLogo result = manager.get(id);

        //then
        assertSame(organizationLogo, result);
    }

    @Test
    public void testGetOrganizationLogoes() {
        log.debug("testing getAll...");
        //given
        final List<OrganizationLogo> organizationLogoes = new ArrayList<>();
        given(dao.getAll()).willReturn(organizationLogoes);

        //when
        List result = manager.getAll();

        //then
        assertSame(organizationLogoes, result);
    }

    @Test
    public void testSaveOrganizationLogo() {
        log.debug("testing save...");

        //given
        final OrganizationLogo organizationLogo = new OrganizationLogo();
        // enter all required fields

        given(dao.save(organizationLogo)).willReturn(organizationLogo);

        //when
        manager.save(organizationLogo);

        //then
        verify(dao).save(organizationLogo);
    }

    @Test
    public void testRemoveOrganizationLogo() {
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
