package com.mds.i18n.webapp.controller;

import com.mds.i18n.service.LocalizedResourceManager;
import com.mds.i18n.model.LocalizedResource;

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

public class LocalizedResourceControllerTest extends BaseControllerTestCase {
    @Autowired
    private LocalizedResourceManager localizedResourceManager;
    @Autowired
    private LocalizedResourceController controller;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/i18n/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();
    }

    @Test
    public void testHandleRequest() throws Exception {
        mockMvc.perform(get("/i18n/localizedResources/SearchDefault"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("localizedResourceList"))
            .andExpect(view().name("i18n/localizedResources/SearchDefault"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        localizedResourceManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/i18n/localizedResources/SearchDefault")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("localizedResourceList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("localizedResourceList");
        assertNotNull(results);
        //assertEquals(6, results.size());
        assertTrue(results.size()>3);
    }
}
