/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.sys.service.MyMessageContentManager;
import com.mds.aiotplayer.sys.model.MyMessageContent;

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

public class MyMessageContentControllerTest extends BaseControllerTestCase {
    @Autowired
    private MyMessageContentManager myMessageContentManager;
    @Autowired
    private MyMessageContentController controller;

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
        mockMvc.perform(get("/myMessageContents"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("myMessageContentList"))
            .andExpect(view().name("myMessageContents"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        myMessageContentManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/myMessageContents")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("myMessageContentList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("myMessageContentList");
        assertNotNull(results);
        //assertEquals(3, results.size());
        assertTrue(results.size()>=3);
    }
}
