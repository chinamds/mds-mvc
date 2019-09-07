package com.mds.ps.webapp.controller;

import com.mds.ps.service.CalendarManager;
import com.mds.ps.model.Calendar;

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

public class CalendarControllerTest extends BaseControllerTestCase {
    @Autowired
    private CalendarManager calendarManager;
    @Autowired
    private CalendarController controller;

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
        mockMvc.perform(get("/sch/calendars"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("calendarList"))
            .andExpect(view().name("sch/calendars"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        calendarManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/sch/calendars")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("calendarList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("calendarList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
