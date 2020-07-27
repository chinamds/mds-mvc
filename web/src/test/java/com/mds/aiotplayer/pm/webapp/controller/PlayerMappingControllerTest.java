package com.mds.aiotplayer.webapp.pm.controller;

import com.mds.aiotplayer.pm.service.PlayerMappingManager;
import com.mds.aiotplayer.pm.model.PlayerMapping;

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

public class PlayerMappingControllerTest extends BaseControllerTestCase {
    @Autowired
    private PlayerMappingManager playerMappingManager;
    @Autowired
    private PlayerMappingController controller;

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
        mockMvc.perform(get("/pm/playerMappings"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerMappingList"))
            .andExpect(view().name("pm/playerMappings"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        playerMappingManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/pm/playerMappings")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerMappingList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("playerMappingList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
