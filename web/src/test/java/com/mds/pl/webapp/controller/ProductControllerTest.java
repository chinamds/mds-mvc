package com.mds.pl.webapp.controller;

import com.mds.pl.service.ProductManager;
import com.mds.pl.model.Product;

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

public class ProductControllerTest extends BaseControllerTestCase {
    @Autowired
    private ProductManager productManager;
    @Autowired
    private ProductController controller;

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
        mockMvc.perform(get("/pl/products"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("productList"))
            .andExpect(view().name("pl/products"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        productManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/pl/products")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("productList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("productList");
        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
