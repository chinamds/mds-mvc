package com.mds.pl.webapp.controller;

import com.mds.pl.service.ZoneManager;
import com.mds.pl.model.Zone;

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

public class ZoneControllerTest extends BaseControllerTestCase {
    @Autowired
    private ZoneManager zoneManager;
    @Autowired
    private ZoneController controller;

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
        mockMvc.perform(get("/pl/zones"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("zoneList"))
            .andExpect(view().name("pl/zones"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        zoneManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/pl/zones")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("zoneList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("zoneList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
