package com.mds.pm.webapp;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class PlayerWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addPlayer() {
        beginAt("/playerform");
        assertTitleKeyMatches("playerDetail.title");
        setTextField("MACAddress", "ByPhFzMvYcTmUqLwTsFu");
        setTextField("MACAddress1", "DmSsNbPmMzGvPyKeFmLk");
        setTextField("MACID", "QjOkZeNsQkXdUsRyQeWr");
        setTextField("beforeDay", "24312");
        setTextField("binary", "true");
        setTextField("connectionTimeout", "1884017421");
        setTextField("dbLimit", "9190303754426446848");
        setTextField("deviceID", "AgKkPyVoAxJeByRlSaYmIgIcSmEuMzItTkQpDwPlPvDxQaPaQa");
        setTextField("diskSerial", "CfFuDfGeIlGqEnQdXzYvYtKgTwQoJiKeVvYqXcLwEpEsUnRpZi");
        setTextField("ftpContent", "1371883840");
        setTextField("ftpPeriod", "8273");
        setTextField("lastSyncTime", "06/18/2017");
        setTextField("localAddress", "PnGmZkEaToNbCwVaYuVn");
        setTextField("localLogin", "RlHrXgFvQbWuGbZhElSi");
        setTextField("localPassword", "KzZhDbCoCnRfWbRlBoJr");
        setTextField("localPort", "965515297");
        setTextField("login", "BqIwUbLnSxTqLiKaXkQuNiIsTdVtDwDdIlAsOuZlRsUsNtMjWxGoQtQbYrClAqQuFySgBjFjXyJzTyOjHhBoPmSbCvQhQjCkMsPj");
        setTextField("online", "true");
        setTextField("password", "PlSlUpGnHoPyAnOqHtQnZtIoZrOaHnDaCvNaLaYcMnQjCcRhJsYbGtRzXtUpKeFaDnVvQyHbEeYxOpOhCeRtAaKlXmKkAvLwRuVtLwDxTxXwTeMcMjZvDtJbZlXxHaXgPxNxRrLrUwTwUsGyOdTzEjPiSkLgPlZoVpHoXqBvUbPhEwQyCeKhDvOoVxJrUrMlIfGuKiUnJrYbSrFmHvFcZvZqViFtTgGbWzEzSgOqIlIoUcIuBtRsZiHzSxAcGdLj");
        setTextField("phoneNumber", "NhLzFjVqHtXzVbHbDbHmP");
        setTextField("phoneNumberServer", "EcTmHwDzAuLsQoCyYhHnK");
        setTextField("playerName", "YwWqEhYvGaFkGjEbKiWlLmNmIpVuWuXzXpPnFbHmVyVdPdUtBjQqQrYmBkYnZbIoEtOqCuKuTlCmRgDaEcSaBnVbJzFjXkZgXaMe");
        setTextField("port", "306323169");
        setTextField("publicIP", "AmAxRmXvByAmCdQzMwBm");
        setTextField("replaceFile", "true");
        setTextField("retries", "20589");
        setTextField("retryDelay", "12728");
        setTextField("serverAddress", "WlGlKdYcVvFcCcOwQsKhCyZzChPnTzUyDbKoUeNaDpLeJmAiNqMiSpQaPyNwBkWrGxFrJfFyFtYaNuCyYlUcPyYgEmYbBiXfUlFiTaIdUwWsKcPjGuFtDgSgMdAmQvExVyBxNlIaGgTpTtXaAfPyCmOjIuBuQvGaBrOtKiTfGsPiMmZaBwMmPfOhCkSnVzVaByXkWlAsIrUvSyVeGkZhDwRwTuHuRtSjDqMeVfVgVfTpWgUeYwCnBdSfOiZfXtWdZwMxUwRcFvIkZaQeIeCyIwLaYgBfNeBkCaDzAuLzIdAfHmLkJsGrGwYaMaMlZpIlUwCrHeNfJsTbDzZkQaXbWkGyJwNmTfZoGcVsMvCeNtSoCoEcRcLvFkEoSuDxCyWqCyRyRhKzKcOlGwDiOmClBiOwRrRiXcSrRgUkOfVoMmRmXnYqNaIwUgKwJqCbZbWwKpOsTbQqPfAmHiCiBqCmPyTfGwCwPnFiLuDmWwGhCpYsVbEoElTeXkLiPeRrEoIzOnJdIhYqAlJlLjJcYbVrYrFtLlYaFlYrEkUcTeHtMtBwByWtCdNlXsDsRzCnOeQqZeAtNzLyHwMdPyYrHfUjWtDvExVmAsSyJbKgNuEaEsOtAtMlEzKcGsZiQlPyVgOnIoQhGiMdReYcSsBqYhSfRyJjFtSxVsUiUkEyChHuVaFiAmRpFzZgQgFkQlDtRpFnStIfTdNsSyVpGmIhWqSfUbLfOgWaFbOiYqXiFsEsLqScPiKvJrPaLbPxFxMlLbVsCrDqRqCaKpSzPcNsKrDvBtIeZeUyDeQtObStYiKkTqSzTgQuMyFqInKzVkZkKdVtXrCpUzSlXaNsKbJbZjHfFaOgIfXeAbPbRsCeAfJtUsQtRnNdNoJlLiHaIcNcErNvDfXdFeTvSaLoAaTpHaPkFzZrWxUuLwChLcVbIzIxCcQaTqZuZyAiYyFrEaNbDmGzAkZnYjIhBfWuYoIxTvJjQpOfXdWqNmNlGuSsHkHoIxNlKtXcJsNsGnHhYaReWcBd");
        setTextField("shutdown", "06/18/2017");
        setTextField("startup", "06/18/2017");
        setTextField("timeOuts", "IzJhCuHo");
        setTextField("uniqueName", "NoDgEwNbXmTeGcPdAvJmDlDmIcQhEbWaMcPwCmWwJkNrQlWtVg");
        setTextField("useFirewall", "true");
        setTextField("usePASVMode", "true");
        clickButton("save");
        assertTitleKeyMatches("playerList.title");
        assertKeyPresent("player.added");
    }

    @Test
    public void listPlayers() {
        beginAt("/players");
        assertTitleKeyMatches("playerList.title");

        // check that table is present
        assertTablePresent("playerList");
    }

    @Test
    public void editPlayer() {
        beginAt("/playerform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("playerDetail.title");
    }

    @Test
    public void savePlayer() {
        beginAt("/playerform?id=" + getInsertedId());
        assertTitleKeyMatches("playerDetail.title");

        // update some of the required fields
        setTextField("MACAddress", "XxMiUiSoPoOhZcEcUpRj");
        setTextField("MACAddress1", "OuWaNhEvIqLnEdNiVnXt");
        setTextField("MACID", "NdQzFmKyPnCkCtQeVgQy");
        setTextField("beforeDay", "3735");
        setTextField("binary", "true");
        setTextField("connectionTimeout", "1994589709");
        setTextField("dbLimit", "7552587764623641600");
        setTextField("deviceID", "YjFdYwVfOpDcBiIfLbIrIfPsJgNnVbSqFxRmLaZxNbTcJyAeQq");
        setTextField("diskSerial", "BnXjCtHgJeUhQiXhAgJiHqVoOaEqPfQcEfOtVxWwIrOwNnWpMb");
        setTextField("ftpContent", "1518598069");
        setTextField("ftpPeriod", "24768");
        setTextField("lastSyncTime", "06/18/2017");
        setTextField("localAddress", "WcXsAbEaXmDcYcSyLbAt");
        setTextField("localLogin", "IjYlUmJwBgGnYyDyJlKl");
        setTextField("localPassword", "NpSsAlAwCoGwAqLrLaCv");
        setTextField("localPort", "1160229991");
        setTextField("login", "TaIsTtQmPuQqCtMiHmNjTbBpPhIuNoCfNfGuRbIhTsQcBjIdCrPdCyAqOqBgIvJmWdLxVyNhStEeFrIkXrXwLjSgDdHjHhXsGnNq");
        setTextField("online", "true");
        setTextField("password", "InIhZzCvYmXkUvEcLaLmDoFgNyItEdXsKbQnWhBbXvFmJaFiMrAaKzIzOsUdNoNhUiLuFuTvQcVjPaBvNrIzZtVmRaAxZfTpHyMeDaTqNiOfDgQyMgWiNeUtLxYpWtNvUdBtBvMdZsTlIqQsRwYzZgAxOxQnYjXgFoAoYtNcAiOoEuJnPoQsNoNxXpBcSzSpDtHzXsZrSfQdYlHeZcPxToDjWhNqAxJsGjHgCwMsLoChJgLzZvGtZbMqZtEjQlEl");
        setTextField("phoneNumber", "IbYeOqTpWiPcAlHoTcJuC");
        setTextField("phoneNumberServer", "ZwArOtPhFuSjHnHpFkZkA");
        setTextField("playerName", "SsWhTgFuUiGkWmJiRhWcVyXpHbKaVgBmRhDjPlZfGpHpWiVkGoAiTqScXlRrQtHpIgMgXpSgStYuYjStGxSoFjScFqYbYoDhAeFk");
        setTextField("port", "1950092216");
        setTextField("publicIP", "UoKvHqRzEjGqHiRjUbEn");
        setTextField("replaceFile", "true");
        setTextField("retries", "6506");
        setTextField("retryDelay", "15570");
        setTextField("serverAddress", "TdLuUjHdOcQaVoWiNaTaVqQxNnBmHvGfOcZiGoWbWlAwRiAsJxFzHaPmUhMdHkAgJfClHnKgTbQkPzKsQeXgDpKaOlJfOpTdKwSvKzIpUyFbTmGyPeTiTcIgSqGfAdOyLkRrTtSgVaOcRmXwPkWjVzWyTaBkLeUyLyYeDrLlPnHqWkXvHdFkXyQlXzYiQdQlMrCoHdRsDlIwAoYmZiVbJhKfJqEtLhTqWhYmNoFeWnDqHpNmMsAeMxCdVaBtNeNcKyLcHsPcUdVeXjJlBeBaYzLlKyMgRrDhCyCdHeYwVpUeAsNaRkYrKoTiNtPmCqEeAfDoXnPxOyEwSuKkLtIlJzPrJsLxKxMnJvIyJgJiDlLuLoGxNgCsKqKbJeKlHeDzVnZxWkTfKwYjNiTaXfTeNzSyYqKvWmSwHxAtJaTsLtYzAyCkSqHzVwVcRiLeDsYrOqNjBbJaDwBfVxWnTcGlGaBmNoTxSnHhQxHqPtEuDeOkYtTaHdAtBxScJcSySiDhYdZpFtCsBsHjDwZyVeLdVpQhYyEaYuDlZeClKyPiXdLkRnIuPpQbKfYeReLjMgOlGhPhMhYiTaCrPxIuVhAbQkClDeRoIqEdHvMhYaZqNwNmUsOuTzRfObWgQaKsFqHoBwRjGzSfMaZhTvJbGlUpWkBoVfTuCuCtOjMrXkPuJdWoKiVnJeRfLhEmZqAjJmAhTfTrPoVdDoRbDqUbUlTyGrGdCpQiSiTqPvSfZhZgCiUlDaLhHbUbQlIoQnIeArCbKxUcDzVaSaCwZiVnEbMoOhQaDjJjMpHoOmBtUqIcLgYfMxUsCfMhHhOiKtJxHtRhGzIeMlFzAdQeTvGdUwCqIfMuTyAiDbFzRxDbGbJlRcOtOkKxVbZiOgBzQnHqGvXyKnOjDdVbGzIgYwQxIzMkOfReGyUaOfQrXvJyMsTaLgMzWiFcQnLuKtFpWlKcGqCuNvPoYjXkUuZuPzOrJeKoIvWkXhEfKrIyNaCyGeDiHjMjUpQzMdVbDpPmOeLgXfWa");
        setTextField("shutdown", "06/18/2017");
        setTextField("startup", "06/18/2017");
        setTextField("timeOuts", "LdQlVgGu");
        setTextField("uniqueName", "UyHdEhPhDsStLgKsRyEcUsAaUeVmVkGgJaHgJhTbVwKqGoGoQl");
        setTextField("useFirewall", "true");
        setTextField("usePASVMode", "true");
        clickButton("save");
        assertTitleKeyMatches("playerDetail.title");
        assertKeyPresent("player.updated");
    }

    @After
    public void removePlayer() {
        beginAt("/playerform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("playerList.title");
        assertKeyPresent("player.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/players");
        assertTablePresent("playerList");
        Table table = getTable("playerList");
        // Find link in last row, skip header row
        for (int i = 1; i < table.getRows().size(); i++) {
            Row row = table.getRows().get(i);
            if (i == table.getRowCount() - 1) {
                return row.getCells().get(0).getValue();
            }
        }
        return "";
    }

    private void assertTitleKeyMatches(String title) {
        assertTitleEquals(messages.getString(title) + " | " + messages.getString("webapp.name"));
    }
}
