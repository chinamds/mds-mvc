package com.mds.aiotplayer.webapp.pm.controller;

import com.mds.aiotplayer.pm.service.PlayerOutputManager;
import com.mds.aiotplayer.pm.model.PlayerOutput;

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

public class PlayerOutputControllerTest extends BaseControllerTestCase {
    @Autowired
    private PlayerOutputManager playerOutputManager;
    @Autowired
    private PlayerOutputController controller;

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
        mockMvc.perform(get("/pm/playerOutputs"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerOutputList"))
            .andExpect(view().name("pm/playerOutputs"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        playerOutputManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/pm/playerOutputs")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerOutputList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("playerOutputList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
