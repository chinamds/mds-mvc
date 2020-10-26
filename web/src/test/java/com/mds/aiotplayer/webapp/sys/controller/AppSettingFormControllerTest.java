/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.sys.service.AppSettingManager;
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
public class AppSettingFormControllerTest extends BaseControllerTestCase {
	@Autowired
    private AppSettingManager appSettingManager;
    @Autowired
    private AppSettingFormController controller;
    private MockMvc mockMvc;
    private List<Long> appSettingIds;

    @Before
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();
        appSettingIds = appSettingManager.getPrimaryKeys(null);
    }

    @Test
    public void testEdit() throws Exception {
        log.debug("testing edit...");
        mockMvc.perform(get("/appSettingform")
            .param("id", appSettingIds.get(0).toString()))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("appSetting"));
    }

    @Test
    public void testSave() throws Exception {
        HttpSession session = mockMvc.perform(post("/appSettingform")
            .param("settingName", "SfMkElZeLeVtDtHjBzJnZjJeEmYqMnTuNwVeKfEgVhNbLlCpOwOoBnByQlKaNnDjQrRwChDpFcFuBoIiJuQnUkSzVmTyIoLvVxWaHxIyGlEhTwDkMdYhDnTjKuRrJlKvCcMtZhAkJhAnJwOeNjBsZlNnBoQcBaCxCqRpRaBkQnGaWdPmFyVpYgBfIwMwUiZsOyPuFkRx")
            .param("settingValue", "YbThTdUyDhPdEgAzYcEpOuJjGtWrXjMyFgUdRuQiKnPlQvXpGeTqRuOdWnYfTrAbFhZiJwSmEtOfPuPbCjPuVgLeMiEcFwCoFgXtVgShTeWwGmWlOgAoHwBwXvQvTeWmHdKpNmYyNeLuTwSjLaVkObDbSlPcEeTdNbVtGsVhRtNxKdEpNtLhYtPqCdAlWuYwRaWmYvZvMeAlZbDqNfHsTvZrFzKjJsVwBqBsUuHqRuTbRjTeYyXpJnKgVdVeCvZ")
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
        HttpSession session = mockMvc.perform((post("/appSettingform"))
            .param("delete", "").param("id", appSettingIds.get(1).toString()))
            .andExpect(status().is3xxRedirection())
            .andReturn().getRequest().getSession();

        assertNotNull(session.getAttribute("successMessages"));
    }
}
