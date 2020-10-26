/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.sys.service.UserManager;
import com.mds.aiotplayer.webapp.common.controller.BaseControllerTestCase;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends BaseControllerTestCase {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testHandleRequest() throws Exception {
        mockMvc.perform(get("/sys/users/SearchDefault"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists(Constants.USER_LIST))
            .andExpect(view().name("sys/userList"));
    }

    @Test
    public void testSearch() throws Exception {
        // reindex before searching
        UserManager userManager = (UserManager) applicationContext.getBean("userManager");
        userManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/sys/users/SearchDefault")).param("q", "admin"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists(Constants.USER_LIST))
            .andExpect(view().name("sys/userList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get(Constants.USER_LIST);
        assertNotNull(results);
        assertTrue(results.size() >= 1);
    }
}
