package com.mds.aiotplayer.webapp.ps.controller;

import com.mds.aiotplayer.ps.service.PlayerTunerManager;
import com.mds.aiotplayer.ps.model.PlayerTuner;

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

public class PlayerTunerControllerTest extends BaseControllerTestCase {
    @Autowired
    private PlayerTunerManager playerTunerManager;
    @Autowired
    private PlayerTunerController controller;

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
        mockMvc.perform(get("/playerTuners"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerTunerList"))
            .andExpect(view().name("playerTuners"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        playerTunerManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/playerTuners")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerTunerList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("playerTunerList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
