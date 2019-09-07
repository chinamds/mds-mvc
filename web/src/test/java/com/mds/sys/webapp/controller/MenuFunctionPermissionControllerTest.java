package com.mds.sys.webapp.controller;

import com.mds.sys.service.MenuFunctionPermissionManager;
import com.mds.sys.model.MenuFunctionPermission;

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

public class MenuFunctionPermissionControllerTest extends BaseControllerTestCase {
    @Autowired
    private MenuFunctionPermissionManager menuFunctionPermissionManager;
    @Autowired
    private MenuFunctionPermissionController controller;

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
        mockMvc.perform(get("/sys/menuFunctionPermissions"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("menuFunctionPermissionList"))
            .andExpect(view().name("sys/menuFunctionPermissions"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        menuFunctionPermissionManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/sys/menuFunctionPermissions")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("menuFunctionPermissionList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("menuFunctionPermissionList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
