package com.mds.aiotplayer.webapp.pm.controller;

import com.mds.aiotplayer.pm.service.PlayerGroupManager;
import com.mds.aiotplayer.pm.model.PlayerGroup;

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

public class PlayerGroupControllerTest extends BaseControllerTestCase {
    @Autowired
    private PlayerGroupManager playerGroupManager;
    @Autowired
    private PlayerGroupController controller;

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
        mockMvc.perform(get("/pm/playerGroups"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerGroupList"))
            .andExpect(view().name("pm/playerGroups"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        playerGroupManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/pm/playerGroups")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerGroupList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("playerGroupList");
        assertNotNull(results);
        //assertEquals(3, results.size());
        assertTrue(results.size()>=3);
    }
}
