package com.mds.pl.webapp.controller;

import com.mds.pl.service.LayoutDtlManager;
import com.mds.pl.model.LayoutDtl;

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

public class LayoutDtlControllerTest extends BaseControllerTestCase {
    @Autowired
    private LayoutDtlManager layoutDtlManager;
    @Autowired
    private LayoutDtlController controller;

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
        mockMvc.perform(get("/pl/layoutDtls"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("layoutDtlList"))
            .andExpect(view().name("pl/layoutDtls"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        layoutDtlManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/pl/layoutDtls")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("layoutDtlList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("layoutDtlList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
