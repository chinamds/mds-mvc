/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.pl;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class ZoneWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addZone() {
        beginAt("/zoneform");
        assertTitleKeyMatches("zoneDetail.title");
        setTextField("BAlpha", "9");
        setTextField("chkZone", "true");
        setTextField("zoneBGColor", "2088642636");
        setTextField("zoneFile", "BuNpQzRgKjNjWhHkNkMiHuIaQzLqXdXgJdAyBoCgSfMoWpAgKaIpRhIzBnPcQaNgJfYjPxKyObObJfOkOpGySxVtEzEmVaWxZuJnHuFaLsSiFxCyWtLrWsZsNmRbEuGkUmXzOcNkGwZzMhSfJmYsDiGyUsKkTkNtJjOrOjIvZdIzUxHbFzPuKqDeRmUtGuPxIaRsOoBaPtVtSjOnQuInZpOuBdHiVzDoPtGfPxNsKiVqNyQmCzJjHjIjGpDaTtVcMuJoIgTiJqTyHmMuCuZaEmPbNmXdPqErPhHyJjSpWbWsAfXnQlMzVsCqDjOtQzPrTdEvRvJoSdJbEfSpLdGiUsDnHnPyNlOmYyNdDaZvHaTqLuXnEnIwKgDdJqQmYmEfVxQlPbVbBwKoSlQkZbVkAkIoUfCsAnXcLcPfArUtFsMhGsUiGzCjHgPkQqDsKxPtApBtWoPbThAwJpExPhUqSsWtTcZtDiVwErRaXpEfOpFoYzDzEcFpMqIuGdHtHqEeGfPxYyAxYyDnQnXsFeBlLcJfVcXiInGsChIpFnGcJdMwYpPyWuIuKcHdWgYyEwNaTiSkRuMwHeSyUqJsZrNpYiQhIqVdEaRzWbEoRsHvPtMsGpUzRnZbTeQfGtYkRnBsWeIpZvSmVmAvOvKtSjNbEjWbFsHvIaMsRvUsZyDaByGgIaVkFnMtFeEhEuKlIjEgTrEjXtEfRiUuApNcVoWhCpYvRuTnEdDeAbJnUsIuWeFpUdNtYqPaGgOoAfAfIbNtJlCmElFuVhUvJdYzDdKtFtXsZaXkPgNrIaSrKaPmOcEtJeSjDlMjYkPxPjFaHcIwZvFdAvJbYbTxRdAcPxAqNyYcHaWgWtHtArQqLnYdSmTtJrLsPhFhUrNeCiOeZvPlFwJrZdTdElYiPbReBgEhKsWgKgLvIiYwAsFeWpBzSePjVgIdMhVnHvHvRuBeFsObPmGyTqEfWkJnIaHnCnZpCbHsPqOpDhKzSrEuLkObOhTtZtViZjWxAvUmIbHnPzUj");
        setTextField("zoneIndex", "15691");
        setTextField("zoneSelectBgPic", "true");
        setTextField("zoneType", "11251");
        clickButton("save");
        assertTitleKeyMatches("zoneList.title");
        assertKeyPresent("zone.added");
    }

    @Test
    public void listZones() {
        beginAt("/zones");
        assertTitleKeyMatches("zoneList.title");

        // check that table is present
        assertTablePresent("zoneList");
    }

    @Test
    public void editZone() {
        beginAt("/zoneform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("zoneDetail.title");
    }

    @Test
    public void saveZone() {
        beginAt("/zoneform?id=" + getInsertedId());
        assertTitleKeyMatches("zoneDetail.title");

        // update some of the required fields
        setTextField("BAlpha", "100");
        setTextField("chkZone", "true");
        setTextField("zoneBGColor", "1667703159");
        setTextField("zoneFile", "VyObKeVjFxLoTtQwVlAbGzKfGgAyRyYlWrFrRbWsGiNsFhMrFmGmQiVcHbNvVbJtJxEkZcPuQiTfKyQzHwGfUtRyAnZeKtEwIbGxScNlXjHfXsUcNsLhNkMsSiYeYaFnAqWoIwKjSsNrJyZePxWcMeCiXvOcDhXhGyVeGfKyXpVqHgDzYnYdFyOtNeOwGpLkQpLaOnVrEjVtXoPmCsTdBhKoPpNrRpVtIqYxOnTqRbRfSjHeOwLmSkCmDfJvCcPzZpWvYhMcCiUnVlNgYzZsPbVfJuEaEsPyYfAtPmNvXfRdRnWgFbApWcIuUcEsMvWnRwNjPwMbRqAsHjItDdFxIdHrEbWvSrEkJsCmLzShEwDdIePtIjNcYgSrCrUqZaGoXyQnLgOtZfQkTbCpDgZtIgRpIsBzLoYiDdCfLfPtZhFaMwMzWjQmPyKqYaNpJbYlDzLoVzKpUzVmOiCjQcUzKxGoFuWzAlFvCiBcRxDlKgDcGeElVuInLgLlGjGbKiNwBlJlIwYuItYcTzTcMdPlVrSoEbBqQoHtDhOxZfArUyKvVsGiGsEaBdDlRhVnWhLjDlQnRkPwEkWjYaAuHtBrBcQlHbPlOeXbXxVjOuGhTfIhKiMdWjJfNrEfOoIxWrAxPnDxImPcUwFsBpUaLyTmPrTqZdSgFvKgPgDvXtEgSiArWyAcZlKgMxCaNiFgEgYhDxBbKmIfOeRnByAkOqJiTzDmBlHqRdDdViBvKxVcVhUyLsYvCwYvPzJnLcWdMzNwRjYjJnJuKlIsUpZdNqGzLtOyJcEoRzOeGkUmInJqUoNgTyFuYzKuSeVaAsRhQdTyEgCqXfCxRwWbUgToGoNdYtUxMaIwEkTePwGeLqDgNkTnKiFtZoEiFtAfSmHbBeYiJsWsPuTmUxXzXyHoIbGqQyUzQbHvJwLqDgRzXtApZsFzVrVnLtSzFhErMdByTyTwRnStWgTaFeDxGyUjYrZiXkBvLcGoHaMjShOeZgWdZcVqZgXgWhCuAnVjOwFeBmNb");
        setTextField("zoneIndex", "24799");
        setTextField("zoneSelectBgPic", "true");
        setTextField("zoneType", "4377");
        clickButton("save");
        assertTitleKeyMatches("zoneDetail.title");
        assertKeyPresent("zone.updated");
    }

    @After
    public void removeZone() {
        beginAt("/zoneform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("zoneList.title");
        assertKeyPresent("zone.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/zones");
        assertTablePresent("zoneList");
        Table table = getTable("zoneList");
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
