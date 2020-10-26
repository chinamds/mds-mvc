/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.pm;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class PlayerGroupWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addPlayerGroup() {
        beginAt("/playerGroupform");
        assertTitleKeyMatches("playerGroupDetail.title");
        setTextField("code", "JyLqEcZcOwXoStYuXdOsKrHhIfPuMoSbAwEiWt");
        setTextField("desc", "FrKhNsQtXoQiUdWzEqUzAaZhSzCwFlGoKkGfHfZpOiNeBmEnEuClBePaHfGpQyPdEuRbEzLvPpIgLtRxBzJoOhGmHfRtXvCiIoSrTfRwJmKqGhVhDtWjGmOmAbHeEuSsJgZqMgKsSsXfUtEeXfFjAdHaXjUeGkFzWnRsWvDcAuVeAhFmKfTzIbDgXtHhLnFrFkKoZfVoRfAxWyXxKpEqWbJcXgQhQqYaQzSvHeBeJmOaGmGoGiJsLqIrFeZdDyRk");
        clickButton("save");
        assertTitleKeyMatches("playerGroupList.title");
        assertKeyPresent("playerGroup.added");
    }

    @Test
    public void listPlayerGroups() {
        beginAt("/playerGroups");
        assertTitleKeyMatches("playerGroupList.title");

        // check that table is present
        assertTablePresent("playerGroupList");
    }

    @Test
    public void editPlayerGroup() {
        beginAt("/playerGroupform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("playerGroupDetail.title");
    }

    @Test
    public void savePlayerGroup() {
        beginAt("/playerGroupform?id=" + getInsertedId());
        assertTitleKeyMatches("playerGroupDetail.title");

        // update some of the required fields
        setTextField("code", "OdEhRmEeEsFqAdFqKbSaBcQjUbIsYdZnWsRjTl");
        setTextField("desc", "ZcLaKwNcSuZrHjLpZvGbLgCbUjWyAyHwOwDsGwYpBdSoQiRzYoIlIjQyYyFwLfBdCpKfVpJxObAnQqCfGjPkIhFmYtFkYcZrJdNrUgRdJaImOhKpFlLaEfVuWxDfGqFlJfTnHyDmQpVaPfCzGhDrQxRsShFiPtUzUpTlYiSpSvGsJgFdAkJmYxJlTzOmHdJkKtEkCqEkBuCsYfBuKsXdCkQjJcGeVtHxFpLjPdYyUmVaWfNjBmOzRvTzBnDpFbVg");
        clickButton("save");
        assertTitleKeyMatches("playerGroupDetail.title");
        assertKeyPresent("playerGroup.updated");
    }

    @After
    public void removePlayerGroup() {
        beginAt("/playerGroupform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("playerGroupList.title");
        assertKeyPresent("playerGroup.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/playerGroups");
        assertTablePresent("playerGroupList");
        Table table = getTable("playerGroupList");
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
