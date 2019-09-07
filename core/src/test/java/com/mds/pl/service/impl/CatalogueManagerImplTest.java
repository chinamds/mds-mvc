package com.mds.pl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.pl.dao.CatalogueDao;
import com.mds.pl.model.Catalogue;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class CatalogueManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private CatalogueManagerImpl manager;

    @Mock
    private CatalogueDao dao;

    @Test
    public void testGetCatalogue() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Catalogue catalogue = new Catalogue();
        given(dao.get(id)).willReturn(catalogue);

        //when
        Catalogue result = manager.get(id);

        //then
        assertSame(catalogue, result);
    }

    @Test
    public void testGetCatalogues() {
        log.debug("testing getAll...");
        //given
        final List<Catalogue> catalogues = new ArrayList<>();
        given(dao.getAll()).willReturn(catalogues);

        //when
        List result = manager.getAll();

        //then
        assertSame(catalogues, result);
    }

    @Test
    public void testSaveCatalogue() {
        log.debug("testing save...");

        //given
        final Catalogue catalogue = new Catalogue();
        // enter all required fields
        catalogue.setBGColor(624934468);
        catalogue.setBGMusic(Boolean.FALSE);
        catalogue.setBgType(new Byte("88"));
        catalogue.setBtnAlign(new Byte("28"));
        catalogue.setBtnLng("PvV");
        catalogue.setBtnStyle(new Byte("52"));
        catalogue.setCatalogueDesc("UuNoUoZiYiLkVaBsTmMgZnPbAlOtKfQtLvXvCsTiCeDbIaNkOjQzDeCfBpRxRnIaVlLqAtFdArZwXqXyDvChGbYuBgYoAhNwKkYe");
        catalogue.setCatalogueName("XkLzKaPzOrRtWkPnOfKs");
        catalogue.setFontBold(Boolean.FALSE);
        catalogue.setFontColor(1515799638);
        catalogue.setFontItalic(Boolean.FALSE);
        catalogue.setFontName("EbShEyNzMhOlBuAvAqTlQdTpDzGwOhXpEaUyGkPzTmEoYuEfPs");
        catalogue.setFontSize(new Short("3128"));
        catalogue.setFontUnderline(Boolean.FALSE);
        catalogue.setImageFile("IhAzFbLbJzSlKtCjZbTdXiWeTyOyPaEpTqCeZdTtBfBkKlYfZmCbEjKdGhOtIvXfYzAtXeXvDsFhAcYmPaNrYeRiTsFeFwZdVnQfFkUaWdZlWnYbZmQsOqKsLtGsGjLsYsGlKoOfNjAcXiBnPpXfVaEqAlVuGqIzIpLqFiPpWtNaXdWrNcTeBuKnDdItBrTeXmXcAwLeIpRjCxThPbEwTxJyZlDlShVlNwPoQoGeFvDoBoObBkLwAgYxKxTvFgVbHnEuXbGcHaOuZwLqMsHfDfZfVgDyBxWvZzSeBuSyNrVnDpCnRdDrBsPmRlHbPuOkQhQrYiWiBmDeJyDdGyOhQmAzFtGaBuClSqSgSiIlQhNgHzNmAtHnWiLyHaAdJgIjEvRfRkQuZyOsGtTrZmYyDyFcLjAwSrPaZmVzKvEiSnLkWgWaGlRcHuJmOaQeWeFaWvXeDqAvGwByUyTeOmKqJjHkFhEvEvOpEmIpKuMrCfNuKeWoVpWnCpOtTeWfMsMvHuOaWbRqNgCkGiEeWrMaDmKkMpIbQjOwUzVvLnDzDjWxIsFmQqQzZcVeXqAyStAoNdXvTvCbByDzWcLpLoLrZwQtNjGgFcVeGvOvPjVrDhZsCgAwRkHkLtCxSfFjUfTrClPtXcAgNoUzRwMiNpZtGhIeTuHeXzZwWpYsAxUtHvLcYtDaBeCzPgTjLkVnXdXdXqEsSgRfUuJmPaGwUeLpXxOwOmUkKeDeVsZdDzZkPiDtVyTiZyUdFoAuXqAyWtQcZyKcNfIqEoDbFqCaOcEkMeKkInMyFcLuVqOdIxNxQaQqKjNrKlWtOhObIlZmDvFkQgRkRhOjXdXoRuFmLkEwYhNdJqXyGuMkKzZqYsDxSbTvWrFiIkAhHsOiEoLyTpQkDfMwNrXdJjGrRsBrHsFwRcJcRkVfBwRpJrHcDfPkMqUaWsBjGpOaLwLoHcAsBcWlSjTfBkYiXqDmFxQdEkMeGmVkWhOcKtSjBeQhWeBgCgBfIqApOeVyFxBgLtQkXhHu");
        catalogue.setInteractive(Boolean.FALSE);
        catalogue.setLayoutName("NhIdCmZkRhBjUgAkHoVzNiKdIxNyMwVrDmUwRmMuAuGlPdJxYq");
        catalogue.setMusicFile("OlAbGfPvAnGbFyLuGcAfGuOoRjYgUoUkGqItUwTeNuRvIiMmNbTiCqWtUiBpAfRcJuEwCzOqZcGrCoBcHaReNjFyDdLfIzSgHhPiFkGlGeJkCnCzYgRgRdKhIuHeYzKlBxBkNsZiUlDqEaLwHuJnIhTcKiCfHuYtLoCcShJoNnAyRlYxZtMlDgKmCuTbPtYiCzWiMpJhUrCaPaIoQyUyKjVgUnPoXtCbLdJwVrRfXbBhBzBkUkJvTbPiItXoFdKqAfNbNvUhPnEoDiSkOnUdGcMdInNpQvJyXnTfSuYzZiRbUmFbBfVfSlOfQnXgJbVjThRzCyWnQsFuHkXpPkJpHaZjCmEeIvNwPrExVtZmVwTvOdBcNyLmIvOsMsReJvIwFfLoCrTtKaRhZcNoAiPcMlWjUbOpRzRkSnZqZaLnIyYeMaUkJaRsUqOyOqUzMmYaKyOmDmYqBpStScDzGdNoPfUsWmFxTuKdPvCrEiYhRaHyQiEuNxBiDfBsCuFcOmJkDwByZkGtSrXfEyMcDvYmSgHfGzDxGnLwWsSpPbRnTrJjIkPaFwViZhApKnLwIpDlHtFpYlQtUmGbLkEqOyNxPhZbNmTmByTwXjFrHbNqLzCePePfHrZjWqUeHvIqUhInFdOwKmCoQoBlOdQeSjPpBkDzMiTqKzGyJqGaMjAwGlQtQuFtPfBxNxZcItWbTnOeCdTaBtVnQoWdYpAgNwAhKuYkJxYqQhXrMuLqBpBlNiJlBmOmNdHlKbJkIjFfZjUgMaKjKoSoXzGzPjFlBxEsWlBrLfJgGzOmKbKrSxWtVsQnLaWbEnEpCdVnVdQoYsJbQyBkEyOhMsZsUcNpYdVzYrTbHrYyPiUvTlOnTlAlNuYlUlKwAoSiQzFjIuIlXuYnWoCzRkXhVtOnFxWzUeZjWwTbHqPvCiWjAdEzDdFtDrLyRtHhFeZyBsJvXtIyOzDiBrPlGkWcHuLwOfJkNdYxYeCnZeZzSiRcUlDnDeFkPdIgXmNvWrIoOnUmLbLfFkUb");
        catalogue.setQuantity(new Short("1519"));
        catalogue.setScreenType(new Byte("111"));
        catalogue.setSkin(new Byte("30"));
        catalogue.setSkinCode("UpDdNjYwSvKoKzLbEbZdTlPoQ");
        catalogue.setCreatedBy("IwPoKfZgPwWzVbHhVjUiQlEwFiVcCwEnMoOlXhIwIcVpFkQwUjBjZjTqZvIrVgIgGdJxDrSeKfUxBgTqSeKbDoWqEkJpTaQnLaAl");

        given(dao.save(catalogue)).willReturn(catalogue);

        //when
        manager.save(catalogue);

        //then
        verify(dao).save(catalogue);
    }

    @Test
    public void testRemoveCatalogue() {
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
