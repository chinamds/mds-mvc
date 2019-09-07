package com.mds.sys.webapp.controller;

import com.mds.sys.service.NotificationManager;
import com.mds.sys.model.Notification;

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

public class NotificationControllerTest extends BaseControllerTestCase {
    @Autowired
    private NotificationManager notificationManager;
    @Autowired
    private NotificationController controller;

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
        mockMvc.perform(get("/sys/notifications/SearchDefault"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("notificationList"))
            .andExpect(view().name("sys/notifications/SearchDefault"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        notificationManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/sys/notifications/SearchDefault")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("notificationList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("notificationList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
