package com.mds.aiotplayer.webapp.pm;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class PlayerMappingWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addPlayerMapping() {
        beginAt("/playerMappingform");
        assertTitleKeyMatches("playerMappingDetail.title");
        clickButton("save");
        assertTitleKeyMatches("playerMappingList.title");
        assertKeyPresent("playerMapping.added");
    }

    @Test
    public void listPlayerMappings() {
        beginAt("/playerMappings");
        assertTitleKeyMatches("playerMappingList.title");

        // check that table is present
        assertTablePresent("playerMappingList");
    }

    @Test
    public void editPlayerMapping() {
        beginAt("/playerMappingform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("playerMappingDetail.title");
    }

    @Test
    public void savePlayerMapping() {
        beginAt("/playerMappingform?id=" + getInsertedId());
        assertTitleKeyMatches("playerMappingDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("playerMappingDetail.title");
        assertKeyPresent("playerMapping.updated");
    }

    @After
    public void removePlayerMapping() {
        beginAt("/playerMappingform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("playerMappingList.title");
        assertKeyPresent("playerMapping.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/playerMappings");
        assertTablePresent("playerMappingList");
        Table table = getTable("playerMappingList");
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
