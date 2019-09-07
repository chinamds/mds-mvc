package com.mds.pm.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.pm.model.Player;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlayerDaoTest extends BaseDaoTestCase {
    @Autowired
    private PlayerDao playerDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePlayer() {
        Player player = new Player();

        // enter all required fields
        player.setMACAddress("JpKwTlEwFcUhNbOdSwUi");
        player.setMACAddress1("LxTkKoNbBeDoAcUpXgOl");
        player.setMACID("CnXsQtQpHuFjPlAjYjSx");
        player.setBeforeDay(new Short("32405"));
        player.setBinary(Boolean.FALSE);
        player.setConnectionTimeout(2009801722);
        player.setDbLimit((long) 1.0001035192270167E9);
        player.setDeviceID("CkOoKsIqEhOkBuHlDsFcZvSuOdCaJvXtDyFqIrRxTsKqOpDtPe");
        player.setDiskSerial("JwUrEaNjVgUlHdLzCsWxItByRcUgRxPyPfHlCiCkTrErQtSvTc");
        player.setFtpContent(1542538557);
        player.setFtpPeriod(new Short("28243"));
        player.setLastSyncTime(new java.util.Date());
        player.setLocalAddress("AnRzWuRvCiEpAfSaFeGu");
        player.setLocalLogin("BpQsSsLxXoDiWvQtMqRw");
        player.setLocalPassword("CsNjPfTzYeWzWxNyJwUl");
        player.setLocalPort(1726409516);
        player.setLogin("TlUrDjNePpHjTbVhOuBiZwMnGiUaExZiSsZiKfVyNcKuTzQqWaWxBsGfBoBhEfPtNuLhJtMaLzHgDnTkFnOdWbJtAwGoRjApLmXp");
        player.setOnline(Boolean.FALSE);
        player.setPassword("PoYtLkKoIbUoEoIqHmDmVnMjSyEoDfHaHiTsElKrWbRnCgOjKwBuFbGlXwXeEpIhUdObBwGcSmNpXmNcHpKjZiUmUtCbVkKxXzYjSgOpAhPwYxLgEkXhNlNwTtRbAbNyAkYlJfFfUkAxDjLuZaNxQkTtTgJeRwOdTtIkScDhCsCgGqBpZpYbIvOjUkJaEvYkRfYhCqNdLjHsTaYzPxRuBpFsHiFiEtFhJoBoVaYoJmIeMtQhRaBaGiKeVbGwQhFb");
        player.setPhoneNumber("VtJdPjFwHvToZdWdWiWqY");
        player.setPhoneNumberServer("RgTdFyOyRkQwCzLmBdBnC");
        player.setPlayerName("GcUkXlKoWuUaYjKoTuReGdZjSwArMiXdTtAiDcWhXhFkFiQcDzBlBkJlUoJbNpDkLyKmBrLlWsUtRhLdPnHjCyJeLbBmKsSdEnKq");
        player.setPort(989321804);
        player.setPublicIP("KiHlAfEqFaAnXpWtBtCc");
        player.setReplaceFile(Boolean.FALSE);
        player.setRetries(new Short("25747"));
        player.setRetryDelay(new Short("25691"));
        player.setServerAddress("LhLdDfBkGuMlRkHyWzSmIqVvOeFsAtFkFcWfFvHpMuOhGbXpVlTfMeFhPyRxLjPkCfKoVsTwGkLdEuNjDaFqLbPoEfJpGvZiOuJrVeZrMaFnCeAqJuGfNlPyUwBmVyHsYsLvOvLuZuMuGdXcHuKtSkCzDeScHuRcFtYqInIpAgTeVhUtVgGrEzXbZpDhCfHlZuRlUiZiTjGvQrBqCcEcVlNqMoWfRtGqVlSlXcDyHpDhBtFfAsQlLuJhTdGyOnOhVaTmAvZpNhJfCmVoWuDyTkZlKeXlRtTyNlLsExQiVlOfWyHlIpDaAgVySkIiTeOiRsEbSvYxLkOcFuMxMmRaQyXhUgUjEyRvWnXzByNaNgNhQiAtLqMeDhDrBgIbAqZnYxNxZuTnYdEkJtGtXyJzKbJrDoKeInAdSrDeQdNiEwFiUlZsEeSnFcIvOpIdPhPjEjVzKbTcIvHeEtHxHsHeShGyHhEaNsTmSePbQjOkNgTzNxKvIzLvSlBcCfZkCuWgSpQwUbFeOmCfQfFeKoGwQqQfGfCwXzBwIoRxQmZkTtLqRlPyHjFuApQzRcSfOwYgOwQjAeMwCyEqRqJaTeAyKrBpArAsMvNwOsSyNlYjNyBnMzDvYgQmBlOiEeZrAjGiDkNaRxHdRwPiZxWfBlRnJlTvDpBgQbNoRuNzIhPnPuGpIqDtKeVuDhRyXoQmLlLdAfRtBsArAuMpVxDdIgWgNrXvTmXnZqNvAjMuDaMxRtQvOuPxWhPdTeKaRnChJlZkUdDcSiCaUdDpVxClIuEcNrAfGnVeGtEwVmZsVyQxEmExGzQbHjTuJsDsLmSgTzFoUbDdKnEmDhHuRjLpQhGqJlGvBrHhYyWxAoOhSkBtZhMpYdWaFzKpDgLsBaCpXxUdWuDrUhIjPpLsBdWkHgDtCpLgKmBqCzUnVnGpMaXbHuBmOmMdQiDkUuKqCyFdXoUlFaIzHuOsYjEiFhNlOkQcSmRkFhMkGcIiUsWzYnTjJgPhZoTyOlNnGbXsRmWvUlDb");
        player.setShutdown(new java.util.Date());
        player.setStartup(new java.util.Date());
        player.setTimeOuts("CrSgSmXs");
        player.setUniqueName("XyJoZjBsUwOrXhJySgUsAuGkKcIjSbXqGjGnAjDnCcClRnThDy");
        player.setUseFirewall(Boolean.FALSE);
        player.setUsePASVMode(Boolean.FALSE);

        log.debug("adding player...");
        player = playerDao.save(player);

        player = playerDao.get(player.getId());

        assertNotNull(player.getId());

        log.debug("removing player...");

        playerDao.remove(player.getId());

        // should throw DataAccessException 
        playerDao.get(player.getId());
    }
}