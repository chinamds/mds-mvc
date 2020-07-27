package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.sys.service.RoleManager;
import com.mds.aiotplayer.sys.model.Role;

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

public class RoleControllerTest extends BaseControllerTestCase {
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private RoleController controller;

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
        mockMvc.perform(get("/sys/roles/SearchDefault"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("roleList"))
            .andExpect(view().name("sys/roles/SearchDefault"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        roleManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/sys/roles/SearchDefault")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("roleList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("roleList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
