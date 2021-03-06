/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.cm.model.Gallery;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GalleryDaoTest extends BaseDaoTestCase {
    @Autowired
    private GalleryDao galleryDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveGallery() {
        Gallery gallery = new Gallery();

        // enter all required fields
        gallery.setDescription("YbTgTpLpQqIjFbOlHxVzHpDxTgGoXwZoUxQaKpFaTyAhXnDqQzUkZxInTqJhSpIfXnKjWvDuDpPeGfCpHdZeAtGcDuMqBsMeQwTdEjFuHdSmFqBqMbSoXtWaLoLrGdQqOcOjPuCvJnTqOmLxZeGpNmUnGfGcTaTlPdBlPeGlLvGhWiItVwTdPeBdHtIxWvMeBbBpGzUuCiOiSmGlCmEvCdPdKmTkKzXhInTuSmYrDrVrFcGdAiTnWzMgCzYfTnFxGgSlMhNaQjLxMrLcFvHyWqRtDcKzHeVpMbVyYgYoGkFlSlMdMgEjGtZuMhAsXpLiKcXzIkXqTkIuKpPiMkJbWoGsIhFzSwSaTbJgXhGkRrDcQbPzXpQiCrUbRyTzHeBxFzYoDrCfPvRxZbJuUbBfUkGmWfJzUzLnNdDcMfXhDiTiDuKjCjBnLaGxMsBwRdYtQeJrFoIoRsDfKeWyOkCtXrWhQbJkJlYdCpWuPsZwBpBvZbMgUcHmWcXfEaAaKjFkWfMjCpJgQfWmPpDvPaUdBaTyNhRlVdQnHjVdFtJuOeFuPvKkQsNoKlEsKbGmOxRkGwScEpThGkYuZrMqCpKnNlQhFpArHgVqQkXdEpZpGkArYxHbZxNrMaQpDaKpPbHwCjYfSwIvGsTjMqWiKbQnWkNyBjTlDjDoZpJmEaEeHnIvJxCqBdDzWjIcKcMlJsLhQqCtTgPtJwLdJpLkDtXtYaEaFoJyFoWwSbCkOoFqDrKdOoEhNxTnLrLiNwHoDeKcZhJcZkEkBgGiFyTlXoWpSeRqFaVoOnLnJhAgImQsOcJtNwMtQwQvHdLrRfToIqXkZySlRwEfQvAiVhJwIdTsGuOuCnZoMzVjUhKdAeSqNjPoGdFuCwJsIwPsYrNxLgEoChErQgOiWnNdCtCmVzUvOvLsJcAyFmVxUsMpRaSrZnCbYiZzWkDsQfJsIlDmBeUwIgMzAkOhGfWaVbOgRsPkLmGwRzTwCwSyIbThMvOv");
        gallery.setIsTemplate(Boolean.FALSE);
        gallery.setName("AdYwQaKgEjKzWvXiMrQdQvSeFfGjFpGsPaYlUsKxVzQbPlVhVy");

        log.debug("adding gallery...");
        gallery = galleryDao.save(gallery);

        gallery = galleryDao.get(gallery.getId());

        assertNotNull(gallery.getId());

        log.debug("removing gallery...");

        galleryDao.remove(gallery.getId());

        // should throw DataAccessException 
        galleryDao.get(gallery.getId());
    }
}