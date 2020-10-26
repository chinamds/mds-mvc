/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.pl.model.Zone;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ZoneDaoTest extends BaseDaoTestCase {
    @Autowired
    private ZoneDao zoneDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveZone() {
        Zone zone = new Zone();

        // enter all required fields
        zone.setAlpha(new Byte("0"));
        zone.setChkZone(Boolean.FALSE);
        zone.setZoneBGColor(842087817);
        zone.setZoneFile("EbQfLiUeTxMmHjFnSrWnBvLgNaXgVkBrPyQkEbCzYrGmNyDiZiCsShKyUzTcYxVrDhPwFeOzDvSnCtGlFfDlRsTeYhOgCkAwTsFjIpPxQuKlXeRnItCpQkRiUnJeSnFjReOnZlHfEzSzJsJkLrIhXiCtVyTsCsVsAsPaTaEzFvCyOqXmXqHmAaSyTfFnKnTrRoUvCrHuHuDtMmQdCrBhPtXkArJrFqVjSiDfIfIyAvCbMdRqXlNzMbHbSaPnGqYuMqJkVnExVxVaFoWaIzUvYiFxUrDxAkAgSyGwMiOdJuOfMsEoBnQpKxQtGcXjHlSoAjOsLgFvYiJiUsEzWuMzGcGhXvExGbRyLfVmAfXiKrQwMcVqPiHnWtUpFaCuItXnSmQxCyBlWxRbXpOcAdAlAwTnLeOpYbQzZmYfIsKiHxAhGwOwKsGhPsFqBbKtVrXlSbBnArAhTmLkVtJmScVfDsVgEbTzIlDqYvHlExPaFoOnSqNrYiDwFgIxRlRjBdCgRsHeNaWdScQbIbVaOcIlRbDsEkDfZbBqPqPpCgHwJqPyToMrKrWbEkBlNrBpFbJrZuEqXeEzAnFbUyQgScMfCcRlGbRaVwTzYqMaAeTyZhRcMmIlOoDpRcIaPtJbFoSfSsOfQeWpYgJnCrJbFaKgGbEaQcJuDhHuQfQqQdNoKhXqAdUhAhZsXkQtOnWvXjTtSgRyHjXmXaJkYrFlQzSuIlSyJyRfPkEyDxNwEwTcBdSxRrVeCbKcWuLdRzNdAaGkNxXbOqQqKyVkPtHhMmHiZfFiXxFbJjTzRkZgQuWsQiFtYrEfVcSmPlYqTlRfJgQiMzBvLmKeEvLpZkIgAuRjGjXrPeGzXlXwEzOjShBhWvSkOpVsEaFoAoJoMxOcNcMkTaCoDrBiClKhVcBnHgOgUyZuIjFjJoLrPuEnVxScVdJgLzUwWdTuBpTvEiXjGiFfKcPcAdNbIxYuRoYkFlLyRhBkWpCxGkPoOcRoDrEnTrQnYvHkNrCaUpEsXvIyGrLo");
        zone.setZoneIndex(new Short("10864"));
        zone.setZoneSelectBgPic(Boolean.FALSE);
        zone.setZoneType(new Short("30286"));

        log.debug("adding zone...");
        zone = zoneDao.save(zone);

        zone = zoneDao.get(zone.getId());

        assertNotNull(zone.getId());

        log.debug("removing zone...");

        zoneDao.remove(zone.getId());

        // should throw DataAccessException 
        zoneDao.get(zone.getId());
    }
}