package com.mds.cm.webapp.controller;

import com.mds.cm.service.GalleryMappingManager;
import com.mds.cm.model.GalleryMapping;

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

public class GalleryMappingControllerTest extends BaseControllerTestCase {
    @Autowired
    private GalleryMappingManager galleryMappingManager;
    @Autowired
    private GalleryMappingController controller;

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
        mockMvc.perform(get("/cm/galleryMappings"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("galleryMappingList"))
            .andExpect(view().name("cm/galleryMappings"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        galleryMappingManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/cm/galleryMappings")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("galleryMappingList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("galleryMappingList");
        assertNotNull(results);
        assertEquals(2, results.size());
    }
}
