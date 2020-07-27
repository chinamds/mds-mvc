package com.mds.aiotplayer.webapp.pl;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class LayoutMstWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addLayoutMst() {
        beginAt("/layoutMstform");
        assertTitleKeyMatches("layoutMstDetail.title");
        clickButton("save");
        assertTitleKeyMatches("layoutMstList.title");
        assertKeyPresent("layoutMst.added");
    }

    @Test
    public void listLayoutMsts() {
        beginAt("/layoutMsts");
        assertTitleKeyMatches("layoutMstList.title");

        // check that table is present
        assertTablePresent("layoutMstList");
    }

    @Test
    public void editLayoutMst() {
        beginAt("/layoutMstform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("layoutMstDetail.title");
    }

    @Test
    public void saveLayoutMst() {
        beginAt("/layoutMstform?id=" + getInsertedId());
        assertTitleKeyMatches("layoutMstDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("layoutMstDetail.title");
        assertKeyPresent("layoutMst.updated");
    }

    @After
    public void removeLayoutMst() {
        beginAt("/layoutMstform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("layoutMstList.title");
        assertKeyPresent("layoutMst.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/layoutMsts");
        assertTablePresent("layoutMstList");
        Table table = getTable("layoutMstList");
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
