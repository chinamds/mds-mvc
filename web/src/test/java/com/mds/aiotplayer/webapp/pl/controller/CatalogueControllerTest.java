package com.mds.aiotplayer.webapp.pl.controller;

import com.mds.aiotplayer.pl.service.CatalogueManager;
import com.mds.aiotplayer.pl.model.Catalogue;

import com.mds.aiotplayer.webapp.common.controller.BaseControllerTestCase;
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

public class CatalogueControllerTest extends BaseControllerTestCase {
    @Autowired
    private CatalogueManager catalogueManager;
    @Autowired
    private CatalogueController controller;

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
        mockMvc.perform(get("/pl/catalogues"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("catalogueList"))
            .andExpect(view().name("pl/catalogues"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        catalogueManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/pl/catalogues")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("catalogueList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("catalogueList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
