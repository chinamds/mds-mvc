/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.i18n.controller;

import com.mds.aiotplayer.i18n.service.NeutralResourceManager;
import com.mds.aiotplayer.util.PropertiesLoader;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.i18n.model.NeutralResource;

import com.mds.aiotplayer.webapp.common.controller.BaseControllerTestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NeutralResourceControllerTest extends BaseControllerTestCase {
    @Autowired
    private NeutralResourceManager neutralResourceManager;
    @Autowired
    private NeutralResourceController controller;

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
        mockMvc.perform(get("/i18n/neutralResources"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("neutralResourceList"))
            .andExpect(view().name("i18n/neutralResources"));
    }

    @Test
    public void testSearch() throws Exception {
        // regenerate indexes
        neutralResourceManager.reindex();

        Map<String,Object> model = mockMvc.perform((get("/i18n/neutralResources")).param("q", "*"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("neutralResourceList"))
            .andReturn()
            .getModelAndView()
            .getModel();

        List results = (List) model.get("neutralResourceList");
        assertNotNull(results);
        //assertEquals(15, results.size());
        assertTrue(results.size()>3);
    }
    
    /*@Test
	public void testImport() {
		PropertiesLoader loader = new PropertiesLoader("ApplicationResources.properties");
		Properties applicationResources = loader.getProperties();
		if (applicationResources != null)
		{
			List<NeutralResource> neutralResources = Lists.newArrayList();
				// walk values, interpolating any embedded references.
	        for (Enumeration<?> pe = applicationResources.propertyNames(); pe.hasMoreElements(); )
	        {
	            String key = (String)pe.nextElement();
	            int index = key.indexOf('.');
	            String resClass = key;
	            String resKey = key;
	            if (index > 0){
	            	resClass = key.substring(0, index);
	            	resKey = key.substring(index + 1, key.length());
	            }
	            
	            NeutralResource resource = new NeutralResource();
	            resource.setValue(applicationResources.getProperty(key));
	            resource.setResourceClass(resClass);
	            resource.setResourceKey(resKey);
	            neutralResources.add(resource);
	        }
	        neutralResourceManager.importFrom(neutralResources, new String[]{"resourceClass", "resourceKey"});
		}
	}*/
}
