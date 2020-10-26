/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.cm.service.GalleryMappingManager;
import com.mds.aiotplayer.webapp.common.controller.BaseControllerTestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@Transactional
public class GalleryMappingFormControllerTest extends BaseControllerTestCase {
	@Autowired
    private GalleryMappingManager galleryMappingManager;
	@Autowired
    private GalleryManager galleryManager;
    @Autowired
    private GalleryMappingFormController controller;
    private MockMvc mockMvc;
    private List<Long> galleryMappingIds;

    @Before
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();
        galleryMappingIds = galleryMappingManager.getPrimaryKeys(null);
    }

    @Test
    public void testEdit() throws Exception {
        log.debug("testing edit...");
        mockMvc.perform(get("/cm/galleryMappingform")
            .param("id", galleryMappingIds.get(0).toString()))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("galleryMapping"));
    }

    @Test
    public void testSave() throws Exception {
        HttpSession session = mockMvc.perform(post("/cm/galleryMappingform")
        		.param("galleryId", galleryManager.getAll().get(0).getId().toString())
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(model().hasNoErrors())
            .andReturn()
            .getRequest()
            .getSession();

        assertNotNull(session.getAttribute("successMessages"));
    }

    @Test
    public void testRemove() throws Exception {
        HttpSession session = mockMvc.perform((post("/cm/galleryMappingform"))
            .param("delete", "").param("id", galleryMappingIds.get(1).toString()))
            .andExpect(status().is3xxRedirection())
            .andReturn().getRequest().getSession();

        assertNotNull(session.getAttribute("successMessages"));
    }
}
