/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.AppSetting;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AppSettingDaoTest extends BaseDaoTestCase {
    @Autowired
    private AppSettingDao appSettingDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveAppSetting() {
        AppSetting appSetting = new AppSetting();

        // enter all required fields
        appSetting.setSettingName("GdZhWsZgRtMcDnKtZaOzUfDhDgKeLsGzJtEzGcVhVrLeCnUbCvVkOxVzQzOrCtJkApBuMoGdNaFkRoFpKhRqEiFhCnKwEdGzVjZzGdZzVpKaHpWmNlCxHoNpBeXpFeEyTeZtQaNuWtQxCsCkXyEsUvPnAcQoGfUdBtGaQlEmTcCsYaEoLkJcOmShWwNfTsRzUhGwQyFc");
        appSetting.setSettingValue("JxQeNwZcVgEuDgEzHiVlApFfJeQzNnEmQhYqYnYqGqVcSqOhYpTcWySlPdYkTnTyBhAaLmIhNrHpRaVmZkUbLrTdRpYpWkPjHeVsIwSnIbQjUhDjAqSiIrWyPlXqZzPlDiJzHhUsTrMkJlJxDdNpXhKbXoFtZsDiTdYnUoJqXqWcCxOuGuAvZpRcLiNaLmCgDaJiZdNjGoEiIjTlPtFhQqGwQsPoFiNsWaZsKlGiLzMjSxAaScDqIbQtTaZoZuW");

        log.debug("adding appSetting...");
        appSetting = appSettingDao.save(appSetting);

        appSetting = appSettingDao.get(appSetting.getId());

        assertNotNull(appSetting.getId());

        log.debug("removing appSetting...");

        appSettingDao.remove(appSetting.getId());

        // should throw DataAccessException 
        appSettingDao.get(appSetting.getId());
    }
}