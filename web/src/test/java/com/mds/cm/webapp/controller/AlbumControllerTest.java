package com.mds.cm.webapp.controller;

import com.mds.cm.service.AlbumManager;
import com.mds.cm.model.Album;

import com.mds.common.webapp.controller.BaseControllerTestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AlbumControllerTest extends BaseControllerTestCase {
    @Autowired
    private AlbumManager albumManager;
    @Autowired
    private AlbumController controller;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();
    }

    @Test
    public void testHandleRequest() throws Exception {
        mockMvc.perform(get("/cm/albums/SearchDefault"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("albumList"))
            .andExpect(view().name("cm/albums/SearchDefault"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        albumManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/cm/albums/SearchDefault")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("albumList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("albumList");
        assertNotNull(results);
        assertEquals(1, results.size());
    }
}
