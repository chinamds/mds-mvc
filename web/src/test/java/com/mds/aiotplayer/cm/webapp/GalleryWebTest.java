package com.mds.aiotplayer.webapp.cm;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class GalleryWebTest {

    private ResourceBundle messages;
    private final Logger log = LoggerFactory.getLogger(GalleryWebTest.class);

    @Before
    public void setUp() {
    	setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        setScriptingEnabled(false);
        if (log.isDebugEnabled()) {
            log.debug("cargo.host${cargo.host}: " + System.getProperty("cargo.host"));
            log.debug("cargo.port${cargo.port}: " + System.getProperty("cargo.port"));
            log.debug("basepath${basepath}: " + System.getProperty("basepath"));
        }
        //getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port") +  "/" + System.getProperty("basepath"));
        getTestContext().setBaseUrl("http://localhost:8080/mdsplus-web/");
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("ApplicationResources");
    }

    @Before
    public void addGallery() {
        beginAt("/cm/galleryform");
        assertTitleKeyMatches("galleryDetail.title");
        setTextField("description", "PbJbZaCsSrNmCqRrTlMdHeBxKtVvPjNoXaUvWsBaOwAzVpJwIoRqOrEkFeKkYjCdLfLoDfKmQbSvUjZxVeCzBtBpDxJyJbYfAlAyLnTnMeZlGfRwPnLlKwThIgVpClGyInZzMqFaPyPcZiCmLaZkLoRrJpMsAoZxYvBxCwFnAxCbLpTwCrBcUnEgEhZaIfYcArPoQuKpExWlStZeMvVkEvAuToKqEjJoQmSzMcUuRtBwBqSqYeXcGwCqQzYtWiJvGjSaDxHhVbBrOsYtZvUfLxRmSuTfAsUtJcCpCfGiBlBhDxByUxFtOiPyOuMpHcOxLdKuIqQnQwVtNtRdTfRrPaWtSiKkZlVsGwQpYnApUlYpXoSxIgMwKaMeJrJoFhReFkSoXaAkWdAbLbMuBiXuSiLkGjScFvDxCfIvGuFyAcKgGeIpZdOxDcFnSeMfVgAzCiXpMnTcSwXyReJiFlZxFwDkHzVkLqSnYrXkBcWbCiAiOvJfSySnJiJjDfXaUqIjIrKdJtDkXgGcZbGuJsIhRfVjCdQbNwZtTaWcVpFiHsPyFtGpKjWzLhOcRyDyPhSrIuEaFkTyJjNyWvByZbPoJsKyRnByEwYeAwUxHpPfJgBpJmNtWgUlMySgPvIoNkByHlPsQjIrWlCxNuByRbXwZeIcRmDjMyIhOwJiWjUuGkYrNpBzDqCnRvDzYkEuZcBfFtYzWiEhQjIdFjSoRxUoFtKvVyEoTqVvXjPuMdCzTxGtHsBoIoQqDwUlKfLbMoJzBdJjJyFqFxRoDwCoSgOmMaEdSkAbBfDeInZbEvPdDqTdVkLaJbJjLeHjRtUuCuTrVdZgRmCcWjAoCmYgLeHhNtZxNoBcVzEvDbSsSdEhXrDbVzVlLyWwMeFeKkDwCtJxYlEpUzMzIsQgXeVvHbDaDvDwKpMyRvGsPhQjIlQrFbEjWnTfSwCjXgFqOeSyUySaCqSfAbBzNjNxIrHsXkIkUhBiKtJnYlErKvQjJnEa");
        setTextField("isTemplate", "true");
        setTextField("name", "WuTpGuWyEbWmTxBeKvHwZnYlTfVdPtVoDcJjQeAbDzMhEzNlBk");
        clickButton("save");
        assertTitleKeyMatches("galleryList.title");
        assertKeyPresent("gallery.added");
    }

    @Test
    public void listGalleries() {
        beginAt("/cm/galleries");
        assertTitleKeyMatches("galleryList.title");

        // check that table is present
        assertTablePresent("galleryList");
    }

    @Test
    public void editGallery() {
        beginAt("/cm/galleryform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("galleryDetail.title");
    }

    @Test
    public void saveGallery() {
        beginAt("/cm/galleryform?id=" + getInsertedId());
        assertTitleKeyMatches("galleryDetail.title");

        // update some of the required fields
        setTextField("description", "XnIaLvLvRwVqCcRqFsNaXiZkWqLlZgWqGeRfZdWnMeInDiHyXkClAaGlOcCrTpYzNbQgKyNmYnDkJyFeKgGxEhKdTmZcSuKiVjWeQvPoMbDbXmHeXgZqTlBlIcWyLwLtAyJdLaShCqVqKmPoVxRnIlGzLjArPqMbEkBuUsVyNdYiVdDcLwXfHwEmDyGaGlKyTeLaAkLtIxAjQuXnXuFgWoSxFqEtXwJfQdXuPpAtPqEiKyFdXlPvLgWsLaUpNzRiUgTlXmTiOkIpHmBhYaKnAwWcXaYiViUhUpZzNkPoVzLcFeCnKtPhZhUzRwSbWaWmBpXzLxHkOjGiTgJjMwExCnKvWgGxUxLaSmIhSqXgZmDpYkYhRpWlTuJtOqNyAkBjMaUiLbQaFiClRcEqHmXaNcJiLdZhLaUaZoFoWrQyHiGoTvMgJcKzWaVwGiPiCrHnEdLzQlHbMtLpDyNaRySfCtQuViZoXySvAcRkPhPvVlYnFoHuEjBnThLoJjYhUoJtPrKhPcBxVeUcTuVpOaLsZySgNkHxZiCvDeHkOxJfPpBpPeVxAoYlLeMiJaTdMfLeRzPjZcVlCxKvYkQlNiAnCoGlDoErDnMbQfSqNfUjQuAlXeSyCpHrSaWvMqCcKtQbYjXvUtHyUfTfZmAmGvJdCmYqVtLbWbVfDhKwPjZiHxJdEcUrDjBuCjOvBfPlTqVsWmVyHeKbJuHbDeNmKfWoNyXvJiLfRwBmPnJhKnOyGxKePgKaQfPuGhMbNsCeXbFmRwDlLrIqBgDkZmQwYiEgBzMhGfLcLzToMqQoDeQuUhWgOaHfUiDbZvEfUnHbQiYoWeOgUbSoLjPpKrLsDoTbRuYrXoLfGrUkYfYnWpIyVoQgZcFjUeUhKjGsCuImDjUwCtKsWeTvFqYcTqLoYiNxScZaWzSvWaCfBbPiNiLzHnHcGdMrAwUgCeEsMyQlEzYeNwEgDgMkOqAgDiBmXhPaBqOjWzQpTgFjVwVeGjAw");
        setTextField("isTemplate", "true");
        setTextField("name", "YuVuRbSfDqOyIoYaSzIhKkKsWiLjJfUyIhXoLeHsOzZnJoJwAk");
        clickButton("save");
        assertTitleKeyMatches("galleryDetail.title");
        assertKeyPresent("gallery.updated");
    }

    @After
    public void removeGallery() {
        beginAt("/cm/galleryform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("galleryList.title");
        assertKeyPresent("gallery.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/cm/galleries");
        assertTablePresent("galleryList");
        Table table = getTable("galleryList");
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
