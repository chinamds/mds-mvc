/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class AlbumWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addAlbum() {
        beginAt("/albumform");
        assertTitleKeyMatches("albumDetail.title");
        setTextField("isPrivate", "true");
        setTextField("name", "NgSaGlPuSdAgPrQdSgCtWzZhEwYlWlAsDmFcTpEiPfAkYzFfXiYsWhEwCtMrJaYdTkMjRhSqMjFtGbHpTyVkZzFrElSnUdZiMlZiWpIdLeTdXbOlLbIaQbAbJcKbNzEiQlPrSkHuIdFoGiNnCaCeKmGzFqPnIdFkYtMcWvXkYtCjUvCsNqVhVsIkZcHqSdElJiHwKaKxCqLoWwUdVoTaRrGmZwLdLkXmYzOdSvAtFcAlEfTuOoYtAoGnDfXiNgA");
        setTextField("ownedBy", "JaGnUgGuXzQkJcDtRzOlRxLfSxGyDkJeUqDxPzRyXjDsYsDiPoMbWjOoMjXtMeKhSgJfJoRcDxYzKjMlVxQuUeGiUpHlNmVhOoSdMkYhLdJpBoIgOqEkUlZnAfJcMdAbHuPeVdIqVhLfIuYdYwXeEbSsRjQcSlRnFyFsTiKxRiSmTbDxBqHdKpJyVsJzToMcHuJcGoEqCgRoHbAmJrRbHhQrKwWzZbUhSjDdSlVdGkDjCfCnKoZkWmChXyBjEyZr");
        setTextField("ownerRoleName", "OpKsOcEfLlVsEpKnPzFvFcNqLlHvQqQcVhRvIzUqDaOtPeAdKfViRqIsTmOzIoGvVeMsYuYiBkHvVjWqTbRtFzOcWwTkJoDpLnMgJqJlDoQlKuPuNrRcBaTyRaCoUaLeDpItFkOyYfPlOhXtQcQjUzIwWrHjMvOtTwMvWtAdBpMqPeClBsEuHmGmDzKqLvTcZsHpVsGxFwEmCuRdGzCbFkQwSwEfFoBpRmPdFcJrAlMbOfLbRoWtUkWpYtSvSxJs");
        setTextField("seq", "1553339386");
        setTextField("sortAscending", "true");
        setTextField("sortByMetaName", "NdDmZkCpXaKfIqQvQhKoHcKgHjPaOsNaQqWpHuPtVbXdCqPkIrIqCmFnOpQuCcWsTwIeTaLjBsCrZkQkQdCdXfYeHoZsWdFuZzTdHnYvGlUzUuBcKyBtKnDhSnQcSiHsMkGnEwYzNwBvUnAtRlAsHxIfEuAzEiPmHlUiAeKaNnZuTwOtFzPaGxAxKvMjAqMaPpHdNxPbPgHqPgJaFjEuXnTlUkIoOjAmYyDgVqDmKnHsEiHqPcVxEsJnBkVbWpV");
        clickButton("save");
        assertTitleKeyMatches("albumList.title");
        assertKeyPresent("album.added");
    }

    @Test
    public void listAlbums() {
        beginAt("/albums");
        assertTitleKeyMatches("albumList.title");

        // check that table is present
        assertTablePresent("albumList");
    }

    @Test
    public void editAlbum() {
        beginAt("/albumform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("albumDetail.title");
    }

    @Test
    public void saveAlbum() {
        beginAt("/albumform?id=" + getInsertedId());
        assertTitleKeyMatches("albumDetail.title");

        // update some of the required fields
        setTextField("isPrivate", "true");
        setTextField("name", "AgSwVwIpXkIjChGtTyJwZyJsQaOrKtKeBiIdTdTcCdXdLvCpRjCyLuAnBvNnOeSpEoKvXnFhOdEnNmPzGfZnCcRgMgHyExDfXqGmWtItUuAnQjGyQyHhQjRuNhRnNkBaKzMdLoIfIsDqSbIoXsLaQvHtWbEeJzXsKaWbAqOkSaZgEyScOsEpTiZmGwDyDeZhMyPdPkZaWjUiKpHsLbSkZfMbDqAqPkFqRnOdKhFzPmJqJsFaXsHwOxQuFkZeJuE");
        setTextField("ownedBy", "OdJcNvGrTnAoVkZyIfAaEqFvQmIuVaLrQeIvIeIbFaKeYjLyWyBwMqMkBiRuVhReZmDwJfTzQqAmExXfEiLvDnIsHcFiJlUsSsKbQqAwRrZjZgXlExMrGqGyNqXjShZeWfRxBuMhFxNfEwMdVgLnNjAmPmOeJtGlPyAeKuVeSrFuOpPfTjAyJoSoRtHoZmDbNwMiMtIqKjAnMcPsPvEfBvMxPvLfCaBgNlGqOjVwMuBxDkAsWnGwLdFzZyPeFvJw");
        setTextField("ownerRoleName", "KgPbAsHjMcZhNuQrVpFlUwWdZeErGaMvIdOaKfLhCvGsDsNxAkEpRwLgCpQlEqEsBmHpMbUcCeGfPnWgQeEeLzAlEuXtWfJsQoIxTqVrOsUgVrMfHgKySqBhWkWdFnXqInKuFhJiIbTmAwBbDlFhFqZyFhFiLjSiRdGrBnJfVaGrLxEiCsYfTqBwJoSeYaGgDhArCbZgVxTuXhArKwNrThLmVdMaWoCmLjDdEsLsWcPuCsBuKcQyPaMjVhLkNrXq");
        setTextField("seq", "1567458893");
        setTextField("sortAscending", "true");
        setTextField("sortByMetaName", "ZvDsTuRpVrYlUbPnVyRnWiJqTnSnOjJqEeNtIpIoJoByLkYqWrQqUtKuVkKpHxZqLyXdNqQfEnQsZoApUkDjZiCyXcJiEzGuInExZjKzQfFcVvQwPdPxMmWeApJiLgOqHdCcSjGwThHxImKtPoDzFlGvYoZrKqGgMaGnCuHdJmCvAwDpOoUcCsLcHlSlNoIvHmEcDpNgGsWgQzPiYeRrLmDiBcEpWvEfOnReCsBfYsKiDiQcUkMsPqOeEaXeAiU");
        clickButton("save");
        assertTitleKeyMatches("albumDetail.title");
        assertKeyPresent("album.updated");
    }

    @After
    public void removeAlbum() {
        beginAt("/albumform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("albumList.title");
        assertKeyPresent("album.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/albums");
        assertTablePresent("albumList");
        Table table = getTable("albumList");
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
