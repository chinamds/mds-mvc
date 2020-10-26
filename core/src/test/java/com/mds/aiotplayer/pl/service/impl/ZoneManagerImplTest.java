/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.pl.dao.ZoneDao;
import com.mds.aiotplayer.pl.model.Zone;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class ZoneManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private ZoneManagerImpl manager;

    @Mock
    private ZoneDao dao;

    @Test
    public void testGetZone() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Zone zone = new Zone();
        given(dao.get(id)).willReturn(zone);

        //when
        Zone result = manager.get(id);

        //then
        assertSame(zone, result);
    }

    @Test
    public void testGetZones() {
        log.debug("testing getAll...");
        //given
        final List<Zone> zones = new ArrayList<>();
        given(dao.getAll()).willReturn(zones);

        //when
        List result = manager.getAll();

        //then
        assertSame(zones, result);
    }

    @Test
    public void testSaveZone() {
        log.debug("testing save...");

        //given
        final Zone zone = new Zone();
        // enter all required fields
        zone.setAlpha(new Byte("31"));
        zone.setChkZone(Boolean.FALSE);
        zone.setZoneBGColor(1501645635);
        zone.setZoneFile("WoIzGvVeSbNjQoCsNrSaQxGrUuUgSmGsSjAyEcVaNtOrSbPmGiOqOeWwIcNmJkKySkByOqBdDqAtIpCbPcAuYlSqPyWeQjCgJwUpZlQfDhFdVhRjOuBbUtScJcHvHqRhYlZcIxEqVnXjYuHkTaAaWvVpCrVzVzVjVhIbIpRqBiXhKcItBiOaDdMeKyIgVlJcJwNjWnYvReKnFtMxLhLhJhSzGuHlUxCxTlNkVkZyYeUjVgThEzIfAgYgRbRwTlBdGpWmZeVtLwXrVtWeXrAaJfVmSjBdAfAeRzRqKbOmYzAgQyTaNtNoVoKqNeEyXmDsEoJoVkNcWsSfOeMdXyCiQcNxWyNiDuWrCtDuNiYgQyJcHyIgAvQzTnKqQsHyDuIaIcFcCaObRbLiGoZvDjFtXdPdVvLuUqMzPaTkZwSpDqRfMzGeSaBjBuCkUzIbKjEkIzUoWiRdOuPrUaWcZuPcYgToMlEkUaUlQuLpGmAaAiKvZwNrBwSdAmVsFpEdVfRrKbLgTmNsHoZfJpGkObQeNnCeOpNbCzLzXdUpDlCyNdFwRbYhTzLfXkSrKnViTrTfIdUbYqBeTcMaDgGqXdMrLkUdNpYmUlFvAiKrErTlJkLrRpQtWkPrBnBmEqSbOpSsLpLjGiEiWkHyQoQxXqQtPcOtVvHyGoQkVhPwJeCdZySxJqOwVtOnZaBkSuMpZtWsUlHoYuFmUgSpNpGgHwIzAsDbUuKzAyNcHvXuKeUxKkHhHrDkNqJqFuEgRgKaAyDtZtHdBeKaTdMyVmPmVeEeWpWsOmIiYbJwOtOkPmVqKlUfHtPsEvChNmTnEtWxXmKwMuXgHsMhFxNeTpJzCkPxMqFvJpGoGjCfAlAtZsWbLaSvPvRoPqMfVzNuRdBcEkDkJnNpZgHlXuElYtZjTwNoKnIoJzQbJqLzFxPwHsYpSsUoRyCyBbJcIqDuIgInVpYbLqDcFkDyUvAsIpAkMwSaLbLdUtDmEjNyCdWjZqWxFbSaMtBwJgKrKmRdSmUyCjCf");
        zone.setZoneIndex(new Short("15103"));
        zone.setZoneSelectBgPic(Boolean.FALSE);
        zone.setZoneType(new Short("31706"));

        given(dao.save(zone)).willReturn(zone);

        //when
        manager.save(zone);

        //then
        verify(dao).save(zone);
    }

    @Test
    public void testRemoveZone() {
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
