/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.pm.controller;

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
public class PlayerFormControllerTest extends BaseControllerTestCase {
    @Autowired
    private PlayerFormController controller;
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
        mockMvc.perform(get("/pm/playerform")
            .param("id", "-1"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("player"));
    }

    @Test
    public void testSave() throws Exception {
        HttpSession session = mockMvc.perform(post("/pm/playerform")
            .param("MACAddress", "GgWqFuYfSqViGmXgTzAv")
            .param("MACAddress1", "RiTgWhFlIvSySiWkIoXx")
            .param("MACID", "FfBxZxMcLePgBnRgTkKy")
            .param("beforeDay", "29390")
            .param("binary", "true")
            .param("connectionTimeout", "680486473")
            .param("dbLimit", "1053488392427286528")
            .param("deviceID", "IdLiSnLqHeGyIrQkBwTxVoXuYvUgLvVqAiAfQsGtNmQxKxTbJb")
            .param("diskSerial", "BqDwWuHtNvOrRxQbLzJaEwDkJoRzMkLcCqZcEoEnZuUiXdUhHe")
            .param("ftpContent", "198538523")
            .param("ftpPeriod", "5526")
            .param("lastSyncTime", "06/18/2017")
            .param("localAddress", "WhHcUsLaXkHnIfSrZtTs")
            .param("localLogin", "JvXiQaXeRoGpViQxPmRh")
            .param("localPassword", "LfSiTtMeMjNhUsSwZpEs")
            .param("localPort", "112097606")
            .param("login", "BbKxNaJnAjMzVgYeNdZlXdIhJwWbRfMbNkZhHwFzCgMpEeVxSsGmKvMeFgFgHlOsDrKmUnQcGmKqFoUjUcItDxDsVkNtPaXaNePr")
            .param("online", "true")
            .param("password", "ZvWbWvUpGxElCvFeDqKaZcVjVaRlYoIkChBkZwZwUxUlRkHkJdRdOlToPrDyRqPiAaGaUuYaGiGaHmJjUyGmAbUrObNfVlLjKgSaQyDaIhUuTsCcXeGlBkBnAbLaDkRbRbNyPeWiGuAkOnInPvWdPtVeBfGzHyBeGgIlPcQdPlXxTfXvWhUvQxYaXkUoJfXlKmNwLnVgClLjUnVyKdWaYlZzKeLaKbLvYuJiSlWvVsCmCdKeQhZfOiVbTvMeOnXs")
            .param("phoneNumber", "IgSyCdDtSpPzOoVfXeJvQ")
            .param("phoneNumberServer", "KqEqJgUdAqRdUtKuLtTbM")
            .param("playerName", "GoImWiEhYgRpVeOzHuCjLdMmArLfBiPtFuWwDzFdZfVwMdGpBgVrJqBaWxYlSmNaDsKvBiKeEmRvWwOtHzAuNaFeUyWcXbCvZzIj")
            .param("port", "1377435883")
            .param("publicIP", "JcDrRxLjRlSmCtMeGdZl")
            .param("replaceFile", "true")
            .param("retries", "18577")
            .param("retryDelay", "30447")
            .param("serverAddress", "OqXdBeEeRcCwNqNqXlOiXrAeQfZtSqJrFsUaEcKpLyAmOtArXdFqJjSdWyXgKoUqDoDwJmBtLiUtAkGgDnUmTvBaErZmOlGlMkIvAlXwOkHvUrFvWpSwKnNzCdBdAjUmMuYaOpLaViUpFuXjAnNvTaFpTvRkBeUnFjOdGjJgXbKoZyTnHoHdWuZfSpFwBcXuMsRwRpOxRtYpFbHjTeBxIoWgKoHnYwJhRrRdAnYcDjQmPdVlImVvIvVdLiGgDzNgIsMyWvYuNhQaFoWpZmJjFnRyPjCgYlNuQuDaHaBnVkIdCuHpVnPhUcHxRqNdSeKfEdPiYzObIkCnIjArNoDkMsXpVlHuRqFyDpChSzTsKkGfNcZkDlCwFtBwDtXzUoSgQcIjGtCjZoJrDkCyWoBdXoJoHlIqAaZyFfJfDvVjPbQaKcUhNoLsDlSaRbDcQtJkRkHrBcVbFsQjIhPaWpXyVaTcOoYvZtZwIxQhGvUeCcIvBlYaEnHyMlOiSrXeKzYuDoNxKtEsYpHeJdKxMqPzPnRoEeNnHoIvHcPqJhGcIiBhMiZcNhGcDiBhDqWqTyAsHoTySkMjGkAqFpUsJjOqLvWaJdYhAqCpSdGgYhZvCoIxBhAfBcTpMaVdRqBaOvHkTxIkLoLyUuDzYuUvPfSsAyKoSgAnFoPvTiSqHqAgCnZhYiXqAgMcIpWkWzWnPbPeZjQxRjAvMtQfJzDlEeRaAdUuSmMxSfLqTrWbFvYuTrZtZrJoZdIyHjShLmEdAjJxWaDsWoWdJlHrStNyVrYtZpBcQuItOnRwYzAaTwXvTeToGqCpMzSlPnLzFjRiHoLnNbMxMkGcZhZkItThRnCvUoAcWyQmWsBnYsKpSfYbRwGkTsCfUyPoShEgLhViOnMiXvSzRoJnErQrFsYkUyUkJeLfVnBzJuDhSrFxJrXaWaHuZgPyLwRyBqVpWzGnObYkKoKbTzLeVaNdWeNqVfBeKiBtQvXaDwKuVgSoEiUjViMoRpIzYiOqMkNyAkQxBwJa")
            .param("shutdown", "06/18/2017")
            .param("startup", "06/18/2017")
            .param("timeOuts", "RkWmEwVh")
            .param("uniqueName", "IpMlSqQfVtMuApYsMrCpEsMbLdNxKmYjAvMaOvSpNkBjEzTtBq")
            .param("useFirewall", "true")
            .param("usePASVMode", "true")
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
        HttpSession session = mockMvc.perform((post("/pm/playerform"))
            .param("delete", "").param("id", "-2"))
            .andExpect(status().is3xxRedirection())
            .andReturn().getRequest().getSession();

        assertNotNull(session.getAttribute("successMessages"));
    }
}
