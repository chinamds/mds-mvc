package com.mds.aiotplayer.webapp.pm.controller;

import com.mds.aiotplayer.pm.service.PlayerManager;
import com.mds.aiotplayer.pm.model.Player;

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

public class PlayerControllerTest extends BaseControllerTestCase {
    @Autowired
    private PlayerManager playerManager;
    @Autowired
    private PlayerController controller;

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
        mockMvc.perform(get("/pm/players"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerList"))
            .andExpect(view().name("pm/players"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        playerManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/pm/players")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("playerList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
