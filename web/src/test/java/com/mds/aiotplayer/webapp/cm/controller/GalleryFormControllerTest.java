package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.cm.service.GalleryManager;
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

import java.util.List;

@Transactional
public class GalleryFormControllerTest extends BaseControllerTestCase {
    @Autowired
    private GalleryFormController controller;
    @Autowired
    private GalleryManager galleryManager;
    
    private MockMvc mockMvc;
    private List<Long> galleryIds;

    @Before
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();
        galleryIds = galleryManager.getPrimaryKeys(null);
    }

    @Test
    public void testEdit() throws Exception {
        log.debug("testing edit...");
        mockMvc.perform(get("/cm/galleryform")
            .param("id", galleryIds.get(0).toString()))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("gallery"));
    }

    @Test
    public void testSave() throws Exception {
        HttpSession session = mockMvc.perform(post("/cm/galleryform")
            .param("description", "VzEbEhNsWoYyTeKjKqUeEeBzQtVdHkOmKmRhReXcAiXoMaSmVhJvVxIyJzEfTfVwDmBxTdKyNkIaOwYjYfWsOuRgBtMbIsBqTsMcUnIkNvYsGtPiTxXdHnXrYjTmWdJmIgPoWuHdGhWrIaLtQpRkHlShOtLqVuHiCmOqCtWkNiTdIrWvQpPtPdNkAeYfMlYgGiGgSwElGvOuFzKrYlCoHoTfHnJgAhWwFmRmXtRvRuZrHhFqTpUaHyLfQpLsDsKnYiWxGzJkNdYrIiFyGyUqGoXiRrNsTwJjQrTwHiFfIgNjYlIkYiLmSfYtLlHqAsVyTzLcGyUvHdVuCjUuCeAfRgWvQaGyQlCsZuTqFyCvIjLqGzWuSuZsDuFeGkPtErGiRkVxJhUrByVtHoZaJlFvDdIzNyPbDqOlTtGcRvPqNqIuWaUiOaThYoQwXiWxQtJqHqFvNqWcTkBfEuQpIvQzLcSrMtCdWfFqWbByZpNbUbGiVlGhXmWpVvVqPzZzPkCuZxXsYdEbWjVwWaVuXmAaQlWrQiKgToCuObVeUmPaAgFoDgBqLlSuWsBiBdUrPwHrDeYsFvGpRfHeDkVbUdKyJpQiZfVhXvPwRoYzGzHtQcWqQzPyEiQuRlYzNbNmPnKxHlUbQcRmJdCkYyQuRiLlWkPlViEwGqWyTdWsHyMuLbGjGcDgQhSaSaEnBzVrGgBxIdZcGaSdFqYnHtRmFcKsFlRkUdEhEiSlDnArQgAuWmGhFmVcAlHkFjAwQmEbZyWrLrSeAtZsQxLzQlIhIbPjKqVtAcKjBjDtIcKcZbLoEfNkCrDeHiPhNxHqIxMyCvXvPrPjZdEwMfOwTsViLbFmRmQmHeSkIvAhZcDbIhRtPwVfBmNoMlNjFuPeMqZcBvMhLnFrMuNpDiLnEmQfJlVgLoBqHfZcMtUkZjFkUaNaLeAnAfGhQkHkCpHnVtRoPeUmMqYtEuQiQxYrEpJgUpBdOuZhNgVjLnZeViDoEwOu")
            .param("isTemplate", "true")
            .param("name", "IqMjQdVpEnZkRjAdPeDsTpNzJaIvIxCrYzKgZyEeCdIoOdZoVa")
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
        HttpSession session = mockMvc.perform((post("/cm/galleryform"))
            .param("delete", "").param("id", galleryIds.get(1).toString()))
            .andExpect(status().is3xxRedirection())
            .andReturn().getRequest().getSession();

        assertNotNull(session.getAttribute("successMessages"));
    }
}
