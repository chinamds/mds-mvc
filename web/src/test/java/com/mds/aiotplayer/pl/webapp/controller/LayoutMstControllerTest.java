package com.mds.aiotplayer.webapp.pl.controller;

import com.mds.aiotplayer.pl.service.LayoutMstManager;
import com.mds.aiotplayer.pl.model.LayoutMst;

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

public class LayoutMstControllerTest extends BaseControllerTestCase {
    @Autowired
    private LayoutMstManager layoutMstManager;
    @Autowired
    private LayoutMstController controller;

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
        mockMvc.perform(get("/pl/layoutMsts"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("layoutMstList"))
            .andExpect(view().name("pl/layoutMsts"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        layoutMstManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/pl/layoutMsts")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("layoutMstList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("layoutMstList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
