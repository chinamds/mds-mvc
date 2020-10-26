/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.pm.dao.PlayerDao;
import com.mds.aiotplayer.pm.model.Player;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class PlayerManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private PlayerManagerImpl manager;

    @Mock
    private PlayerDao dao;

    @Test
    public void testGetPlayer() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Player player = new Player();
        given(dao.get(id)).willReturn(player);

        //when
        Player result = manager.get(id);

        //then
        assertSame(player, result);
    }

    @Test
    public void testGetPlayers() {
        log.debug("testing getAll...");
        //given
        final List<Player> players = new ArrayList<>();
        given(dao.getAll()).willReturn(players);

        //when
        List result = manager.getAll();

        //then
        assertSame(players, result);
    }

    @Test
    public void testSavePlayer() {
        log.debug("testing save...");

        //given
        final Player player = new Player();
        // enter all required fields
        player.setMACAddress("VgZpWfSmOeHbTgUrMxHi");
        player.setMACAddress1("CcDwWiVwSyJiLzZbDzZf");
        player.setMACID("TzDxRxRlWbDfReXzYhXo");
        player.setBeforeDay(new Short("5991"));
        player.setBinary(Boolean.FALSE);
        player.setConnectionTimeout(611496970);
        player.setDbLimit((long) 2.0501371569687493E9);
        player.setDeviceID("CgDlKjPvLqJeMeOpBxXhKlZiDfGwXsJmGhQkFmRgEaVzUrPrLk");
        player.setDiskSerial("NeZhXgMtGpFaTkAvAaUpQsPjBjEeNmTeRtMiRpSxNmAvNhTzQd");
        player.setFtpContent(376599366);
        player.setFtpPeriod(new Short("3466"));
        player.setLastSyncTime(new java.util.Date());
        player.setLocalAddress("KlRvDgRdGoVbYeXwArBe");
        player.setLocalLogin("FqNaMjGeNoCpExBiLpTx");
        player.setLocalPassword("KuLgNdVtYeBnTxOxSiJd");
        player.setLocalPort(1866155747);
        player.setLogin("FhKoTdCbYzAaOwQeEvUnTyCiHiZjErZfRdKxVhHkWiCiPbVjHgTqKzYmYxQrPpStWlRvKqCbWmObQmWpYbDrWjBjSlIeJqNwGkTa");
        player.setOnline(Boolean.FALSE);
        player.setPassword("VuTwLmEtStRhDdRgGxTtUgVfCyEwZbEmGzAkJmHhHfNmUoUqAoGnWpBwJzRcNwZqSkZmLcRaHwTdOlKfFqLpWxQrTvZhGgTcNtYyErMsJnWoLuZyTsArEmHeKcIyScPkReNzWtLzSdRaFtUrEnUrLaXiZuOhSsVaUgGiCgKaSoXqPyLaPdDbMwKhHwSgXlJxBmFxDjZkIkWtDiTzYnAsOxJgUjXuPrWvPsZtXaOgDhPbYwSqFdYvRmJcAtOkRtSj");
        player.setPhoneNumber("BbTaAnPpHaXnUaSbHcFdR");
        player.setPhoneNumberServer("WaNiHqYbHoXiByLbIaBbO");
        player.setPlayerName("EeSaHoWfTlQgZbVxMrZsXbRyUwIiXuFrUjOnSdPlOkQgJySgLnBfDdYgGdMbPnGdLbTqDdNwCjUcFlSaGuZqMyFqHgUnWxCiErSd");
        player.setPort(1889143806);
        player.setPublicIP("FrImUhQqKwGeScYpCrAg");
        player.setReplaceFile(Boolean.FALSE);
        player.setRetries(new Short("21611"));
        player.setRetryDelay(new Short("12121"));
        player.setServerAddress("ViKvRaWaUwOgXaNbDlZkMyQsShBlZsXuNgXzOsTeCpMnFdSiSdQeKiIzVeXaEdLxVnAvRsJnFyTjFiSqQvMzLlOwVbDyJlVbJvJxGjIgGtZbGoJcHpQlBdRoXhQkKbAeYfKbTqRaObPaBwKsBiExSnYtTlDiPyApVsAlWpDkLdFcKbHhNhBpUaSrEfTvJkRuXjBxMrMxGkOaVwBgCiLkXhYwEmTqAeWlLmIrIrIcIrJxKiSwRjAjMtZrJzUxWdUuAdMcMoWxRwKnGeQuStDtLgTsNsZjPsShNuWxFzFmMqEcSwIrKgKfJuVnGaUnTaPjNhQmJkRwCmLaJnJgEgInOwFsHpPfScKmCfBlOiOhIzGkSgRkPvMbXpAgNcYmVbLqKgLtZtBoTmFyQuZsCrRwHpMxVrTsOeWrCyPlDcHoKrTjQhQdNrMaAtJzTxYqGmPlNpKiEzZlUvZoUkNcKfAyRiKpOzJeVhMpIsYmGsYlBxSiPnElVlAgBlEqQdSfDiPeOgEcCpYmLgJoDnOcKuYuPqCkXgXmZqVyNlYqZuDvVjUaQiPuNbTsSrToUvSbXmVlFtYeYjFwXuRcGgDxWfHfXyMwFdQsBnBaEdGcBlHkFpSjVeGsDbTzHmBqYrUaEfDbBnYtGvQxPvPlHdVxAoOlKyVgCrKbApLqYmZwAqZgVgTiDdCgNxFwKgBuZfIzVeDmRgHsTjXqVmSsYfSpMlRyAwHgEiYmKdAwKxFtChOqLfJrQwAzEdWmJbJuVhCpZwBaOaDpNrKsFxYsKlOfNeJiPqJbFlPsSmYsBcLaJuPvWgPuKcQpBuNsZwWfOcQzDyZfJtLjXuKgKoDuKxOtUhRvDjOtDoSuEcEcOzDeTcQxFvTiRyPxRpRhPvAaNrSoChPxUkKaPrVmKlPmJdUlSpPlRkTkDmGxYyZlQfIsZcIlTuWdTxHlEgXiIrOlFcDwBoQwNdAoRhSoIlShGjTtDoOlNtXrPuHkZwKoEmEbUxKkZkDyCnGgGvPgUtYvKqXhPhGq");
        player.setShutdown(new java.util.Date());
        player.setStartup(new java.util.Date());
        player.setTimeOuts("NwQsSwTk");
        player.setUniqueName("OpFrUxYdQnWiAjKpFyOeIsUlBrHdTcYwRtPfJjTvHwFnKqZfEi");
        player.setUseFirewall(Boolean.FALSE);
        player.setUsePASVMode(Boolean.FALSE);

        given(dao.save(player)).willReturn(player);

        //when
        manager.save(player);

        //then
        verify(dao).save(player);
    }

    @Test
    public void testRemovePlayer() {
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
