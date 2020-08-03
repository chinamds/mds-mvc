package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.cm.model.Gallery;

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

public class GalleryControllerTest extends BaseControllerTestCase {
    @Autowired
    private GalleryManager galleryManager;
    @Autowired
    private GalleryController controller;

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
        mockMvc.perform(get("/cm/galleries/SearchDefault"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("galleryList"))
            .andExpect(view().name("cm/galleries/SearchDefault"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        galleryManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/cm/galleries/SearchDefault")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("galleryList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("galleryList");
        assertNotNull(results);
        assertEquals(2, results.size());
    }
}
