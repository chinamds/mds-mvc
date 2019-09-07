package com.mds.pm.webapp;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class PlayerOutputWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addPlayerOutput() {
        beginAt("/playerOutputform");
        assertTitleKeyMatches("playerOutputDetail.title");
        setTextField("output", "1788");
        clickButton("save");
        assertTitleKeyMatches("playerOutputList.title");
        assertKeyPresent("playerOutput.added");
    }

    @Test
    public void listPlayerOutputs() {
        beginAt("/playerOutputs");
        assertTitleKeyMatches("playerOutputList.title");

        // check that table is present
        assertTablePresent("playerOutputList");
    }

    @Test
    public void editPlayerOutput() {
        beginAt("/playerOutputform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("playerOutputDetail.title");
    }

    @Test
    public void savePlayerOutput() {
        beginAt("/playerOutputform?id=" + getInsertedId());
        assertTitleKeyMatches("playerOutputDetail.title");

        // update some of the required fields
        setTextField("output", "25483");
        clickButton("save");
        assertTitleKeyMatches("playerOutputDetail.title");
        assertKeyPresent("playerOutput.updated");
    }

    @After
    public void removePlayerOutput() {
        beginAt("/playerOutputform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("playerOutputList.title");
        assertKeyPresent("playerOutput.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/playerOutputs");
        assertTablePresent("playerOutputList");
        Table table = getTable("playerOutputList");
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
