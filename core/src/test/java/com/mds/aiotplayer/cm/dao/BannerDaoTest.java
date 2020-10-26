/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.cm.model.Banner;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BannerDaoTest extends BaseDaoTestCase {
    @Autowired
    private BannerDao bannerDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveBanner() {
        Banner banner = new Banner();

        // enter all required fields
        banner.setHLColor(Boolean.FALSE);
        banner.setBg(new Byte("79"));
        banner.setContentName("FiFdCaXsJiQvTbBoAsCx");
        banner.setFontBold(Boolean.FALSE);
        banner.setFontItalic(Boolean.FALSE);
        banner.setFontUnderline(Boolean.FALSE);
        banner.setHalign("OqHqYoPqWlXpRvItMuPp");
        banner.setScrollAmount(new Short("21448"));
        banner.setStrikethrough(Boolean.FALSE);
        banner.setTextBKColor(602859470);
        banner.setTextFGColor(1533335875);
        banner.setTextHLColor(2019824612);
        banner.setValignString("HeVfXdFfPjXcFnTnTsKj");

        log.debug("adding banner...");
        banner = bannerDao.save(banner);

        banner = bannerDao.get(banner.getId());

        assertNotNull(banner.getId());

        log.debug("removing banner...");

        bannerDao.remove(banner.getId());

        // should throw DataAccessException 
        bannerDao.get(banner.getId());
    }
}