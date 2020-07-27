package com.mds.aiotplayer.webapp.i18n.controller;

import com.mds.aiotplayer.i18n.service.CultureManager;
import com.mds.aiotplayer.i18n.model.Culture;

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

public class CultureControllerTest extends BaseControllerTestCase {
    @Autowired
    private CultureManager cultureManager;
    @Autowired
    private CultureController controller;

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
        mockMvc.perform(get("/i18n/cultures"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("cultureList"))
            .andExpect(view().name("i18n/cultures"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        cultureManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/i18n/cultures")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("cultureList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("cultureList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
