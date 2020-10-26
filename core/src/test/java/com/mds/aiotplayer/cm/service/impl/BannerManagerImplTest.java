/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.cm.dao.BannerDao;
import com.mds.aiotplayer.cm.model.Banner;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class BannerManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private BannerManagerImpl manager;

    @Mock
    private BannerDao dao;

    @Test
    public void testGetBanner() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Banner banner = new Banner();
        given(dao.get(id)).willReturn(banner);

        //when
        Banner result = manager.get(id);

        //then
        assertSame(banner, result);
    }

    @Test
    public void testGetBanners() {
        log.debug("testing getAll...");
        //given
        final List<Banner> banners = new ArrayList<>();
        given(dao.getAll()).willReturn(banners);

        //when
        List result = manager.getAll();

        //then
        assertSame(banners, result);
    }

    @Test
    public void testSaveBanner() {
        log.debug("testing save...");

        //given
        final Banner banner = new Banner();
        // enter all required fields
        banner.setHLColor(Boolean.FALSE);
        banner.setBg(new Byte("12"));
        banner.setContentName("CuRrObGfJwIeTuZtNgNf");
        banner.setFontBold(Boolean.FALSE);
        banner.setFontItalic(Boolean.FALSE);
        banner.setFontUnderline(Boolean.FALSE);
        banner.setHalign("LiHvGbOdYiHaEgRkMqUf");
        banner.setScrollAmount(new Short("7861"));
        banner.setStrikethrough(Boolean.FALSE);
        banner.setTextBKColor(1750904682);
        banner.setTextFGColor(1625015150);
        banner.setTextHLColor(2109249764);
        banner.setValignString("LaJaTjTzFtWqMkPuKbNu");

        given(dao.save(banner)).willReturn(banner);

        //when
        manager.save(banner);

        //then
        verify(dao).save(banner);
    }

    @Test
    public void testRemoveBanner() {
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
