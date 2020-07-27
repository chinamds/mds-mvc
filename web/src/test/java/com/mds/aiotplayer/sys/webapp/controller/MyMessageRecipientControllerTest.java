package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.sys.service.MyMessageRecipientManager;
import com.mds.aiotplayer.sys.model.MyMessageRecipient;

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

public class MyMessageRecipientControllerTest extends BaseControllerTestCase {
    @Autowired
    private MyMessageRecipientManager myMessageRecipientManager;
    @Autowired
    private MyMessageRecipientController controller;

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
        mockMvc.perform(get("/myMessageRecipients"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("myMessageRecipientList"))
            .andExpect(view().name("myMessageRecipients"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        myMessageRecipientManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/myMessageRecipients")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("myMessageRecipientList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("myMessageRecipientList");
        assertNotNull(results);
        //assertEquals(3, results.size());
        assertTrue(results.size()>=3);
    }
}