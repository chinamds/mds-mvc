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

public class TaskDefinitionWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addTaskDefinition() {
        beginAt("/taskDefinitionform");
        assertTitleKeyMatches("taskDefinitionDetail.title");
        setTextField("name", "FvQtLmKtUyVgAzRtIdZbRuKqSrGbBcYeRlXuNxGxMwTlGnAbLtOxXhWmDiAoTzJnVdVlKaHwCbVuSjLaLzIqBfJjOaDhKaDlYqUh");
        clickButton("save");
        assertTitleKeyMatches("taskDefinitionList.title");
        assertKeyPresent("taskDefinition.added");
    }

    @Test
    public void listTaskDefinitions() {
        beginAt("/taskDefinitions");
        assertTitleKeyMatches("taskDefinitionList.title");

        // check that table is present
        assertTablePresent("taskDefinitionList");
    }

    @Test
    public void editTaskDefinition() {
        beginAt("/taskDefinitionform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("taskDefinitionDetail.title");
    }

    @Test
    public void saveTaskDefinition() {
        beginAt("/taskDefinitionform?id=" + getInsertedId());
        assertTitleKeyMatches("taskDefinitionDetail.title");

        // update some of the required fields
        setTextField("name", "EfPrBrKjGtMtKrKoFxIgCfQvMcHmWoRyYjQoKnVlXcLsRxOnGjGgTaBsCzQxVwRtPgBnSzXbYtTeGtAdZqAxYxNuPxIpDbUtRoLm");
        clickButton("save");
        assertTitleKeyMatches("taskDefinitionDetail.title");
        assertKeyPresent("taskDefinition.updated");
    }

    @After
    public void removeTaskDefinition() {
        beginAt("/taskDefinitionform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("taskDefinitionList.title");
        assertKeyPresent("taskDefinition.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/taskDefinitions");
        assertTablePresent("taskDefinitionList");
        Table table = getTable("taskDefinitionList");
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
