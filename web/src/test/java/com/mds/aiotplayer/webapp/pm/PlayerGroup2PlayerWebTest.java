package com.mds.aiotplayer.webapp.pm;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class PlayerGroup2PlayerWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addPlayerGroup2Player() {
        beginAt("/playerGroup2Playerform");
        assertTitleKeyMatches("playerGroup2PlayerDetail.title");
        clickButton("save");
        assertTitleKeyMatches("playerGroup2PlayerList.title");
        assertKeyPresent("playerGroup2Player.added");
    }

    @Test
    public void listPlayerGroup2Players() {
        beginAt("/playerGroup2Players");
        assertTitleKeyMatches("playerGroup2PlayerList.title");

        // check that table is present
        assertTablePresent("playerGroup2PlayerList");
    }

    @Test
    public void editPlayerGroup2Player() {
        beginAt("/playerGroup2Playerform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("playerGroup2PlayerDetail.title");
    }

    @Test
    public void savePlayerGroup2Player() {
        beginAt("/playerGroup2Playerform?id=" + getInsertedId());
        assertTitleKeyMatches("playerGroup2PlayerDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("playerGroup2PlayerDetail.title");
        assertKeyPresent("playerGroup2Player.updated");
    }

    @After
    public void removePlayerGroup2Player() {
        beginAt("/playerGroup2Playerform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("playerGroup2PlayerList.title");
        assertKeyPresent("playerGroup2Player.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/playerGroup2Players");
        assertTablePresent("playerGroup2PlayerList");
        Table table = getTable("playerGroup2PlayerList");
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
