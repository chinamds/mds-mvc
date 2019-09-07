package com.mds.sys.webapp.controller;

import com.mds.sys.service.NotificationTemplateManager;
import com.mds.sys.model.NotificationTemplate;

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

public class NotificationTemplateControllerTest extends BaseControllerTestCase {
    @Autowired
    private NotificationTemplateManager notificationTemplateManager;
    @Autowired
    private NotificationTemplateController controller;

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
        mockMvc.perform(get("/notificationTemplates"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("notificationTemplateList"))
            .andExpect(view().name("notificationTemplates"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        notificationTemplateManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/notificationTemplates")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("notificationTemplateList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("notificationTemplateList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
