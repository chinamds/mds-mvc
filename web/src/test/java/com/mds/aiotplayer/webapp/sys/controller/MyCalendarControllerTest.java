/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.sys.service.MyCalendarManager;
import com.mds.aiotplayer.sys.model.MyCalendar;

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

public class MyCalendarControllerTest extends BaseControllerTestCase {
    @Autowired
    private MyCalendarManager myCalendarManager;
    @Autowired
    private MyCalendarController controller;

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
        mockMvc.perform(get("/sys/myCalendars"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("myCalendarList"))
            .andExpect(view().name("sys/myCalendars"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        myCalendarManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/sys/myCalendars")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("myCalendarList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("myCalendarList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
