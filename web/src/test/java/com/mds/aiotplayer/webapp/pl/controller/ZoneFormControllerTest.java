/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.pl.controller;

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

@Transactional
public class ZoneFormControllerTest extends BaseControllerTestCase {
    @Autowired
    private ZoneFormController controller;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setViewResolvers(viewResolver).build();
    }

    @Test
    public void testEdit() throws Exception {
        log.debug("testing edit...");
        mockMvc.perform(get("/pl/zoneform")
            .param("id", "-1"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("zone"));
    }

    @Test
    public void testSave() throws Exception {
        HttpSession session = mockMvc.perform(post("/pl/zoneform")
        	.param("productId", "-1")
            .param("BAlpha", "40")
            .param("chkZone", "true")
            .param("zoneBGColor", "1245267096")
            .param("zoneFile", "LiUeWrTqBcPuOkEeTaIqHpBkUqIfJzEyCtFrPrZqKeBbKlDkJxVgKmToYkZjViYjLsCtTwJxAgUqAlIcDsEzAdVkNkGzNqRgAqByYqMmVuBlIfAdWgNsFdTzDnIxWoUnNcHmAuFiQzZdHiDuQmJuCvCxJiMnJzTvWgLqStSiGjZnTuEaTvRyLmRnVhXgCzZfKhMvKuCjXgBtExBlIbTfSuJcZuXaXtAyPlXlGtYqTvWeUpOaWeObKtSgLvFcSaAqTaClNyPkVdJgFuVlFpKaFmJwIvQcUoHkCuDyEeXxCsLoWcCnVsSlAqXaLpYeXaHqDjTxSfJlYxBiWtWyBoYhTsChPoBxBzUsBkLkGkBrQeKgKtZaAtFnOcDfNkHrUiFdSkYyWmKnIwKbYkKgGuPqIrIdUwFaVsLwOfXcEuTsDbPcWyVmEmDfJnUeAqGuWkPvAmZhDmJgJqAkMnWqKqTbIzTkOsCtJlFuBsTzGmRqJxMqEhFxKdRjOuSrZaYiBsUmWcLyTyUmHzEuRxEpKzAgLsGyEzAtLwHwBeMcIrDnEaHpViRySxMkBdSqRuTfMuDrXcMzUmMqIzCrMmYnYcQfJkItAnQoOyKkWsKfKpDjAlGlMtEjXaZvDlRmPuUzAjEgNnBsZvPlEzEyKgUdJwSuRcEuOjNcKqEjMdRiPlYrUdZzVnJpKtMrIaZtDjChKhUsTtCqKrDfYqIpLmQfPtMeNkCjCmFgUdFrHdSdSkCwStUtFqTfZmSfBsRpLnXqSoUcVpAqKvErWyHfSnYvSuRmMoTrNbQhHxRwWiJrHvXyBwVsSjDpNsZgSbXkCtWoIlXeBdWrGdZgDiSlFaCeXrRgFhNhAiJpHmIpScLtCeOzVyNrPfTiXcKxCcZeYiMmRnFbRwNhArOtPpTqVwPyBuYyMbOvCuVzMwWeFvPmCaOpTkRuJyQsOkEzJrWdWfGbZnKvNvEdHoSuSzCgVgHtSdCoPyIhAaShTeFcDwZxGeYoVqXgIsWnOsBlXdIiQzAuAoBc")
            .param("zoneIndex", "28334")
            .param("zoneSelectBgPic", "true")
            .param("zoneType", "13733")
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
        HttpSession session = mockMvc.perform((post("/pl/zoneform"))
            .param("delete", "").param("id", "-2"))
            .andExpect(status().is3xxRedirection())
            .andReturn().getRequest().getSession();

        assertNotNull(session.getAttribute("successMessages"));
    }
}
