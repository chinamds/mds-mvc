/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.pl.model.Catalogue;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CatalogueDaoTest extends BaseDaoTestCase {
    @Autowired
    private CatalogueDao catalogueDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveCatalogue() {
        Catalogue catalogue = new Catalogue();

        // enter all required fields
        catalogue.setBGColor(840408505);
        catalogue.setBGMusic(Boolean.FALSE);
        catalogue.setBgType(new Byte("39"));
        catalogue.setBtnAlign(new Byte("61"));
        catalogue.setBtnLng("PhW");
        catalogue.setBtnStyle(new Byte("52"));
        catalogue.setCatalogueDesc("VoCeTcUfPxNkJdEkGdVpShMkGsAoNoRmXfTrByOpGeSaOeQlGrHdKvYhZeXzGkZzWfOkZnXqOsOvOvIjVgCaRzRuClVdDjXrMcYz");
        catalogue.setCatalogueName("KgQjCiSaDdNtAoJlLyEs");
        catalogue.setFontBold(Boolean.FALSE);
        catalogue.setFontColor(846928029);
        catalogue.setFontItalic(Boolean.FALSE);
        catalogue.setFontName("JzVzJzTrTrJkZuCoWzLuOqXkTrSzHcGgGaAlZiQjCoQjKfRlPp");
        catalogue.setFontSize(new Short("32095"));
        catalogue.setFontUnderline(Boolean.FALSE);
        catalogue.setImageFile("VoQgQeCvPeRqPtAwRtNfElNhIqUbPiEdGyXdTtGhTpBkOyGzXyEdWlYaRnJyMjXyOxEzGfZlBeKnWkPuQkLoSbFgTqHoDdUzEuTjBcOxCwUfXzUkXvZyFeJiJvCfBmIwJuEqZkCbWdLpWlOoWgBeXtHrTcOiScFrPaTqBgGcVlNsZnXsOnVcXhWnOlQmOcPyTxStPtCeIrWcJgEtZjExZcRoIeCgNzKvZqLdEiIvXeWbUhTnIrJiQoOcZjHhItVeNzZfBcOpBtCvVaKaSsIoSvXfXsVxHgHnXmSoPtEaGdRhKjTkSaZqXlSkTkZvKzNqKpMsHhAtFnCnEbXoRdXfXtHhDkBwQwWtRiUpRlUaIpKjRfOxUaCkMyZwYzBoHnWwRbGzUzEgMlJeHgEhWhHuDxEmMlUcZmGoYpMgSnTyXaFyTqKhZyDaJrOrGeToMcVlWmXjInWaOwFoTjXoOjAhHbUtVsWkBdZsLmGfOiRaOvAnUmDtSoUhEmEzTmAzBgFeInVcNxQzWgVoLsWiSiAhMzTvEkBvQdAaSrMkGgDeKiXxRsJwYgFrZrNkJwZkQkZrAkKvNqKkEuNnKeIpUmPiJfBnLdIaCeCtMgOiUrUgUsYsPiSaAwZvVsEkSpYdQpKqBqCyRtAbHpWwFyAzIpUiBwEhVtVrFaTyQuBkOwVnZwSqSdWvDpNaMeXsPcNwGpZlLfObHoDaTjUgTtKyRsJxRrKmShGzTlJaScVkIzVaPjCzXuTlEtCmCsSqXxWwYpYgQgYhCpNtImVdYjUfObCzAxVuGvTzAjQxNdHyOtZbOhSzGpVcEaYuUvBqXpWmDfDsGbXtAqFrYsDdIwKiUcVzFeOtWlJxSkLsYyLuGpKoPeYeWmBkCzNiJxAqFuEnXkHgVyVaUmPoZjWlShSwYcQiLhWaGbYsLbDzMqBiOlCkUxCaLwDuMgRsZdKjJsJsOkDkGfBkRxPvGxCfVrNaZmAwKmLvRlMmKmReXeAyMsFzOsQzYqRzDiQbRiZyIoIzJoRi");
        catalogue.setInteractive(Boolean.FALSE);
        catalogue.setLayoutName("PzWkBpXdOdCyBxAuViEgFwJkVzBqClUzZiDkNaEmXwEfGxHiRn");
        catalogue.setMusicFile("JqFuMdLaBzFgEeSeUdCzYoFyNzZqBrFnVhYsKjKsIpXfCwCoPhEiVjMvWwXdXdFsQaPiZsTkElEwKlZhQuLfHhXhLvDhIvEqTnFeDfWfGbVkIgXaZlUtQtWyJjPwRgEoErVeXcXlTpHoUnSnUyWaPrEyQpPtKnUrSjZeCmYkRkAbKgYiMhGiUsNpBcXpJiRbUfLuDmThToBhNxClGaVqXzFrRlSmXsUfPtCmYmKpEdQjRtLnZgJgLyGuFnByJkLtPxHvJnViZvIuWvScXoHtQoLxBuPhUfFeVmBjPkRfIfHpYaSbGhVkRwZiEuJfYzIjDeCbBmIdIoTgClFtNyHiZkEtMbAjLgDrDoNtJaMyXoUeUaCpFdRnYhJqKcDoHuZyPrEfRtMkZzLjUjYwAlNoWjGwUtXzRwFcBpCyEtNlZlWjQwMxXuZzZpJpUyBtUaNpUdDxXiKsMxWhAbRcRwDtCuUkJdArEfLvXhYqTlEhDwSiWfWxSxPvBjDoStDgDtFoPkHqTcOrWdDjIfEbCuJiTuTvEjPyLbTgYwKvPkAzLwRxDqFiQfTnEqIeFfCuKhAyUaRoPiPpZsBfDdYfOfAnZqPzVeCeZfQtRhSpKdPtYeZsHbZyFxRxChEjCpErIsNqLpMmNyYoKmJqQgVyBpLoNgZvYzTqNxZjTxReXmDlMjIwSfTgYaHdQoSdFgVkLeElZgQtLeKhWjVgEkQiOsViOnLlCrIjDbLxKsErIhZnZjMxMyAiHyWqUlToOzGsDjHsKySbDrWxDzOuGhWcBqEjDvYvEpPzJxJvJtZgKmMxTxDuJqUzTqQwVyLsIkCkXxRjIzKpMeZcJvTtTyImSjRwAhQbJhHtLhGaFpJsNwNyKyGvDtBlHiPjLrWlHmCiOsZvFgFqRtVvHtNhDzWfDhRkFhNwKsKiLcOxJtUqWdEfCwBjQbUiFoZoBeSlQwWfDlTwOxDtHfLhCkHjLeXiLlZaTwYtRtEsJqFdKyKeRpLnTeLaAcJqLyYsUhBaNfIqIwWq");
        catalogue.setQuantity(new Short("31709"));
        catalogue.setScreenType(new Byte("63"));
        catalogue.setSkin(new Byte("30"));
        catalogue.setSkinCode("LmQqVxQpIqUcDmXwYeAnVlLmH");
        catalogue.setCreatedBy("IaKsCqYaIkWsFqVgUqGmTgKwSpWbWrPxPjFiKmMgBqOwUwUbQxEdXjKsJhNvXsSmTrEpNmFhMaDtFyOnBbRfTbKrZxSzSjKxCgJj");

        log.debug("adding catalogue...");
        catalogue = catalogueDao.save(catalogue);

        catalogue = catalogueDao.get(catalogue.getId());

        assertNotNull(catalogue.getId());

        log.debug("removing catalogue...");

        catalogueDao.remove(catalogue.getId());

        // should throw DataAccessException 
        catalogueDao.get(catalogue.getId());
    }
}