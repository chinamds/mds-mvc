/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.ps.controller;

import com.mds.aiotplayer.ps.service.ChannelManager;
import com.mds.aiotplayer.ps.model.Channel;

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

public class ChannelControllerTest extends BaseControllerTestCase {
    @Autowired
    private ChannelManager channelManager;
    @Autowired
    private ChannelController controller;

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
        mockMvc.perform(get("/sch/channels"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("channelList"))
            .andExpect(view().name("sch/channels"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        channelManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/sch/channels")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("channelList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("channelList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
