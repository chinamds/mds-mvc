/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class AppSettingWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addAppSetting() {
        beginAt("/appSettingform");
        assertTitleKeyMatches("appSettingDetail.title");
        setTextField("settingName", "SdWoKhXgQuIlDiBsPlWaNmTxEpMdYoYcHbBkHxDrAbMaXyMjFvNeIrKgVeMfIrEyVnBjUeLlQiHaIhUmTwMzHlFzDqEjTpGvEgEiIrXtNhLqMqNsEcWoZtKbLnEtEgGoOkJoVeUePtNxSdWhIhTlZwQgMvPcYzPzRsRoNmCfRsNtDmKmXvRjFoSbLsBwPgFhRbCnJyLj");
        setTextField("settingValue", "RgUiKpYzXiRbXlRvIwLuTnBxQeUnBqElDrCpTwQuBiMkRqUgQuHmAoXhKqCpKjTcXjDaFiOkPqVuTpGvEnHgTuNeHxLgOoGmGfBrGeFzOkJoShUxDnBnKaPcKaXjZnIkZyJoKuOzTzMfGoSuOrBaRjWyBhLwDbFlAfCcLpAdPmNeHqBaYbYoKlFdJbDiWzFqCfEzMlDfTdFhGiGfBgBzFtJqJdUdXhCcTlBeQgBtGnJnSrMvZkFfYnPlZhSkJkE");
        clickButton("save");
        assertTitleKeyMatches("appSettingList.title");
        assertKeyPresent("appSetting.added");
    }

    @Test
    public void listAppSettings() {
        beginAt("/appSettings");
        assertTitleKeyMatches("appSettingList.title");

        // check that table is present
        assertTablePresent("appSettingList");
    }

    @Test
    public void editAppSetting() {
        beginAt("/appSettingform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("appSettingDetail.title");
    }

    @Test
    public void saveAppSetting() {
        beginAt("/appSettingform?id=" + getInsertedId());
        assertTitleKeyMatches("appSettingDetail.title");

        // update some of the required fields
        setTextField("settingName", "DpSoHwSmFvCvRfUcHtHoWrLvJmSxYzRhLvNcAsJuDjEtOiEnKqZaSyZxXkIfMbBcQxKaZvAqDwYxJcTxDhBuUyWbNfQbAeEvBqZdXnDjCgUmHlXsFrAxMiQuBmObVrMbVgSuQgUdXbZyHfWwOoZlWbQxJpYkFuZvYeKxAzTyFoKjJaMvNbLsPoGsKcNwVtRwYlNhKgPc");
        setTextField("settingValue", "PwTtSwUdOmSuOeHsJxFnEiWlRyGkWtNxSxUsNnMdBoXfJqSgDhOqInDcGoFfMeAoBqAnTiBaQsDbKfOpYiMoXlBoIrAfUaXgRvMiFoOxKtVfVsBhMtEfGrUmPxQyTeTsJoVkBkIkQpMtJuGqXrJoDoGsQdHoOeKtUnUbEvGjSpLwKxMlKwVkEbEgVtItFeUhJfPmHiEvDtGtNvKfYfBpCrUnUzRvVvVjTnYzVwIfBjVzEeGfBiFgTpJjQyIdQpC");
        clickButton("save");
        assertTitleKeyMatches("appSettingDetail.title");
        assertKeyPresent("appSetting.updated");
    }

    @After
    public void removeAppSetting() {
        beginAt("/appSettingform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("appSettingList.title");
        assertKeyPresent("appSetting.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/appSettings");
        assertTablePresent("appSettingList");
        Table table = getTable("appSettingList");
        // Find link in last row, skip header row
        for (int i = 1; i < table.getRows().size(); i++) {
            Row row = table.getRows().get(i);
            if (i == table.getRowCount() - 1) {
                return row.getCells().get(0).getValue();
            }
        }
        return "";
    }

    private void assertTitleKeyMatches(String title) {
        assertTitleEquals(messages.getString(title) + " | " + messages.getString("webapp.name"));
    }
}
