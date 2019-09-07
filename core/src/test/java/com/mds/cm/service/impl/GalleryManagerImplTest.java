package com.mds.cm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.cm.dao.GalleryDao;
import com.mds.cm.model.Gallery;
import com.mds.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class GalleryManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private GalleryManagerImpl manager;

    @Mock
    private GalleryDao dao;

    @Test
    public void testGetGallery() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Gallery gallery = new Gallery();
        given(dao.get(id)).willReturn(gallery);

        //when
        Gallery result = manager.get(id);

        //then
        assertSame(gallery, result);
    }

    @Test
    public void testGetGalleries() {
        log.debug("testing getAll...");
        //given
        final List<Gallery> galleries = new ArrayList<>();
        given(dao.getAll()).willReturn(galleries);

        //when
        List result = manager.getAll();

        //then
        assertSame(galleries, result);
    }

    @Test
    public void testSaveGallery() {
        log.debug("testing save...");

        //given
        final Gallery gallery = new Gallery();
        // enter all required fields
        gallery.setDescription("FlHeIcFuIpDvXmNoRmHqJtKqZtNxGfQmUqPaWqOzKlYnSxCiUhReWnTxQpHyFlViWjUaUqAnKgHwWaFbTwQrNgXvTdUhZvRfSbZdPiMgUdYsOoHbCySyFiMdWjLyYwPhFmGpSdCdReSoGoPyWaBtTaDuUrHqKwVqAmMfWvIlIcAlJiTrKrWuXfLxQyMiUrXwZqZwPxLbSwEkYoQtJyOuJhEnBaCnCeSxMtMfYzKdNrVaYqFpUjQxHlKgUaDaTlKfRcRtLwPeIgIdKlYkTwOhTbClNqSsHcEyFdRcOmEaYhXzAeIbZeYbUlGhXvOcUrKtYkQiYgMhDhOnAjPjZhYhNsOoUlUgGlElGuEzKpVdNiNbHpSnBiAqLeBeWvZbDnVgBxJvXzJzUpYyOuVuZlWrFpRnJmHpPyWbVrLiQtLzZqPpChRpPkLzIbZjFkXwOlDcClPzUpWbKeEdMaIqXxRfLuGwEkBtAzGqXaAdDdVuHeOmSwHkFlWmSmZvOqTfYyFiRdFzBuSjDyLrMfUxRkVwLrIfWlCpGgHhLbPxYqJpAuHlTyFpYiApXnZaYsWnSbFxCtFwNbQiIpJySpAtHkWvMrHhGvDtUiQoYrVdScZiSvCcRsQqRkQgEoPgItLiBcPuUqKlTkLvEzUkMnUpJrErEuTdTmVbFmRpTbIcEhYcMtLeBsCuNkHuPxCrZlMgLkYcWgOqPaTlXaWrGuSkJdAoXjKjUsLnPfLiMiMiIlAgDtBtAhNjFuKgCqIpTsPdDuXiJwFmMuZqJfWrMnFtDjJkJtXfDyWkWwLoMhHmCwBqVnSeTlAzOqAlCzOkTgWjIkZlTdMlVeIvHjZvQuJzHhXzVvYrKdLsFmNgFyPvPrErGxEmUlXoTdVdVeMaKiYyWgRcHwCmOpKrVdNgYdVhNwTeOuXsJtBzOhWmOgOzNmKmIjBjNjKmYlYiSbBvElUpKyFsKpJdIkNeVxMuNlUeErOzGuZlLeUqZiZvKuWnQwWc");
        gallery.setIsTemplate(Boolean.FALSE);
        gallery.setName("GzUmMyWwMbBcWsKwWvRyYfKrHiUvBgHrIeSiXjUeNsIuMoBfCl");

        given(dao.save(gallery)).willReturn(gallery);

        //when
        manager.save(gallery);

        //then
        verify(dao).save(gallery);
    }

    @Test
    public void testRemoveGallery() {
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
