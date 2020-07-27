package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.cm.service.BannerManager;
import com.alibaba.fastjson.JSON;
import com.mds.aiotplayer.cm.model.Banner;

import com.mds.aiotplayer.webapp.common.controller.BaseControllerTestCase;
import com.mds.aiotplayer.common.web.bind.method.annotation.PageableMethodArgumentResolver;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BannerControllerTest extends BaseControllerTestCase {
    @Autowired
    private BannerManager bannerManager;
    @Autowired
    private BannerController controller;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver)
        		 .setCustomArgumentResolvers(new PageableMethodArgumentResolver()) 
        		.build();
    }

    @Test
    public void testHandleRequest() throws Exception {
    	//Pageable pageable = new PageRequest(0, 10);
        mockMvc.perform(get("/cm/banners")
        		//.contentType(MediaType.APPLICATION_JSON)
        		//.content(JSON.toJSONString(pageable)))
        		.param("page.pn", "1")
        		.param("page.size", "10"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("page"))
            .andExpect(view().name("cm/banners"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        bannerManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/cm/banners"))
        		.param("q", "*")
        		.param("page.pn", "1")
        		.param("page.size", "10"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("page"))
            .andReturn()
            .getModelAndView()
            .getModel();

        Page results = (Page) model.get("page");
        assertNotNull(results);
        assertEquals(3, results.getContent().size());
    }
}
