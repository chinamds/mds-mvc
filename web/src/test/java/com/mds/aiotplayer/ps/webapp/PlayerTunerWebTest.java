package com.mds.aiotplayer.webapp.ps;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class PlayerTunerWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addPlayerTuner() {
        beginAt("/playerTunerform");
        assertTitleKeyMatches("playerTunerDetail.title");
        setTextField("channelName", "AmJpOhSgQkUqFtVyXkZhKfJlDmGfIsPwKjZaPxYtQhJaPzRhBe");
        setTextField("output", "108");
        setTextField("startTime", "06/18/2017");
        clickButton("save");
        assertTitleKeyMatches("playerTunerList.title");
        assertKeyPresent("playerTuner.added");
    }

    @Test
    public void listPlayerTuners() {
        beginAt("/playerTuners");
        assertTitleKeyMatches("playerTunerList.title");

        // check that table is present
        assertTablePresent("playerTunerList");
    }

    @Test
    public void editPlayerTuner() {
        beginAt("/playerTunerform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("playerTunerDetail.title");
    }

    @Test
    public void savePlayerTuner() {
        beginAt("/playerTunerform?id=" + getInsertedId());
        assertTitleKeyMatches("playerTunerDetail.title");

        // update some of the required fields
        setTextField("channelName", "YdRqTrZwTrQlEjZgJjEoKkLoVjEmBkJxPyLiAmOcYcLeGvGkWv");
        setTextField("output", "99");
        setTextField("startTime", "06/18/2017");
        clickButton("save");
        assertTitleKeyMatches("playerTunerDetail.title");
        assertKeyPresent("playerTuner.updated");
    }

    @After
    public void removePlayerTuner() {
        beginAt("/playerTunerform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("playerTunerList.title");
        assertKeyPresent("playerTuner.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/playerTuners");
        assertTablePresent("playerTunerList");
        Table table = getTable("playerTunerList");
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
