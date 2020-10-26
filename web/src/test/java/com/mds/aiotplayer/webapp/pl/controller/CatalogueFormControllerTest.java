/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.pl.controller;

import com.mds.aiotplayer.webapp.common.controller.BaseControllerTestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class CatalogueFormControllerTest extends BaseControllerTestCase {
    @Autowired
    private CatalogueFormController controller;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();
    }

    @Test
    public void testEdit() throws Exception {
        log.debug("testing edit...");
        mockMvc.perform(get("/pl/catalogueform")
            .param("id", "-1"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("catalogue"));
    }

    @Test
    public void testSave() throws Exception {
        HttpSession session = mockMvc.perform(post("/pl/catalogueform")
            .param("BGColor", "859453056")
            .param("BGMusic", "true")
            .param("bgType", "34")
            .param("btnAlign", "85")
            .param("btnLng", "PnE")
            .param("btnStyle", "97")
            .param("catalogueDesc", "QwRhToKyUtWcXvZqTyNxUkRaGfTlSdMhXwRgYmIuNjVdKcQpSbObAwEvEvEoGiBaYqOyWnNvInReAkDrUqVcGwRaOdAqYvPlSzFt")
            .param("catalogueName", "AkSuPhZuQwEaBsSxNxTo")
            .param("fontBold", "true")
            .param("fontColor", "501071763")
            .param("fontItalic", "true")
            .param("fontName", "OmMdOeBeEqKsKnEuLbNtNkNpRxVzViIxBrHlWfRnNlWxIlHaRm")
            .param("fontSize", "18174")
            .param("fontUnderline", "true")
            .param("imageFile", "VvLnHcCuUnVeXgKsOvUwHyPtXgEcBqYoLzBzUhVuXdLaEvYmDcVpHcKnMyXpKtEsHbWrRsLkMnZiQhHrGuBjZlQpHmHhRuHzPuBbXdMsIuLqMtOiWtQhZaAbJuJzFbHsAeLuMjJmMfIoGhOxOxQqZuAbBuTyBrUnFtWeNyBjJmZyLeSiUaGlRzTiErDxAcBeGuPbFnIuZxFeFtIdVrPsGeDwEkBwYwLdZaHaNxYpFgCaLcTaRmOjWrNxZkXuPvQxPfYoUyKhVkBiWmHhNlCwLmEgQsEoKeQaRoFtOfPjHvNkWoYeCsNyUcKuNdXiNfTzHtAeZfAyZpCdVfOoRrHlYvTdRwKbJcTqJtIxCrBrSjVzNhIwVwVtRgKtJbKhEtPsOlEuQgSwMzCqCzHkCtXbJaXcBdBeVkArUxBiXfDuKvCfUdKnAcWeArGeJvRsCyQsWmIfYvZjYcItMwNpOhImRbWiLsHkAvEeMsFoDkScMoPvZrZtNdGdXuHdTbSwLyEyEuQgSeBaWhCmXkAfItArPwRvQwUaXgLmDyUnUzZgNxSgOtYpDwWmEtZnQaYgXzKtImKsQkQoSyTuLaBtDbPaVpBhUwTaFqDlYuUpYhWeWnSjLyCmAtNuYrBxDaEqLyBtDoUmQcRbJfJuXvAbTxNbGfGfTgNhZfNdUnAyXaBbSdIpIdTgTnRkPhZpRxFfDwCeVePnDwFuKnLnYxLlBpUuLdTvJkImOgPeOzUcRpQiSbVaYaTeWmIqHoWxHvSmUpTnHmExHuBmHbWhKzPrKfTmQrAjTfEeElWqKcUiEsYyBmRjGqBzWhUpBzHwYnZgAcAxIwYuGcSdQiOmNuKaWnCxInJuMlPvYnDdBnTqKwGbRhDxXaQzTiJjGkAkTxSyPfQyXtLqMnCgRqGrJxCfQeHfImTaZiLwKsEsExKoYaRiUlJuInHzDhFxUjVlGsUfUzTrOvYzEyFpHtIqWyCrCnPzBsKxUeOqPjTcKuAkAwZwXnBsElFiQrDgGpEuTwDjOkUx")
            .param("interactive", "true")
            .param("layoutName", "NxHrHyBoNgRkJiTgHxVjZlUfVuUnKkEnXtMjWrCfHiBgLqCaZj")
            .param("musicFile", "JkMuDgOeGeUuExDeTvXfKyRgHhSqCnDhAzVpGvQaLyXxEwNhUfEnGkEiMtZyOqNdHjPuCrItJxUhErAyPvSeNaTrCxQjSsRyIbSzXoQiKjOpQsZdArZbNdAfEhNsFnJeFhJlOwFnXlIoNuLzCkYpAtNdYdBfKqLqTiPgJhQjSiQkMhJuBhLyRmYmSyBlJqSqGjOxWvGxKhBfLsOgErYvNoBdBjEsAxOlXgPaKaViJoFzJoHeImQhYgRwBnFfSqBdBzCiCoReHqTiGkRgHcJtOoVuBgAwJxGgXwBuBxDtCvVgLfBcRnFlVwGzKvKfDbQiNqLgUiBcSeQbEuQqAqHyJkCiRxNnTrAeZyCcAeRuIgKwGtGtAtNeXjYtYpEiQdNmSbUhQkSkCnMzEmXvDpVjLpCoMzXaXdXbJxQkThVxRzUwJmTgIiUnNnUbPoNrXxAvNfMlKkZkGvGyRyBkDwCySeJmTnXnTbSgBaWiAxPhXoXdFoAhDoRdQtTdXlAbHjWgMiByQcKjXeYhJoXpEwSzXaBtFdCxFbNxPeKjVzYmRuCzZaOmNrGxPjXeTzHpMgPvOvVkLnBfOiFnAnYcSlVtRuFgUaExXrRnXfSiDrYyIsImGvCaWaMkXjAxBaDdFjWfAqYtIaYaJyOwGzJgKeUbBvYqLxGwAhHrXuGzQnFkQuPhTuUdPaMnBoNaSlZxHjMkAxSkYiCjRmLsEcSvWeBtPmXuWdZiZkJhMjTjOiCoRuCaIuRsXhSwEqYuGjYaBxAaOfZaUkYfCiVgLxTlKeQdWvImKuBhKjGiFiRqLjYpLzNtNsUtRoIlYfEnSzIxLqWzDnHzDsPaCuGxUnDbWqJtIwOsSmKbRqSjAbHgZfKoTyJqNjNxErKcKtSeItSyPhObRiFxScCpVcHwZcIsKsGpDpDnOrLmWsKxDvAvRyVdCwFjUtXaVuJzNrBuJgKaTqDfEmQcMuUpXoPtKxFsEhPzZxEeSmKtOdBdUyUkRdGxFiBuYwWrZjAiGjTeYlPzWnHl")
            .param("quantity", "16726")
            .param("screenType", "56")
            .param("skin", "8")
            .param("skinCode", "GnPmUfJmAcHeNeAjUqSrZnUnU")
            .param("createdBy", "KpTjBsGcOtCeZmLjOxPtUaCrCfZfSmScQaWaHeWwSvUzQvXlItKbCjMeAwRsWkLsFzGoLnArFiAfIaYrUeQxXtIoZsOqSxSvAgVe")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(model().hasNoErrors())
            .andReturn()
            .getRequest()
            .getSession();

        assertNotNull(session.getAttribute("successMessages"));
    }

    @Test
    public void testRemove() throws Exception {
        HttpSession session = mockMvc.perform((post("/pl/catalogueform"))
            .param("delete", "").param("id", "-2"))
            .andExpect(status().is3xxRedirection())
            .andReturn().getRequest().getSession();

        assertNotNull(session.getAttribute("successMessages"));
    }
}
