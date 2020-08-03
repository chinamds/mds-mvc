package com.mds.aiotplayer.webapp.sys;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class RoleWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addRole() {
        beginAt("/sys/roleform");
        assertTitleKeyMatches("roleDetail.title");
        clickButton("save");
        assertTitleKeyMatches("roleList.title");
        assertKeyPresent("role.added");
    }

    @Test
    public void listRoles() {
        beginAt("/sys/roles");
        assertTitleKeyMatches("roleList.title");

        // check that table is present
        assertTablePresent("roleList");
    }

    @Test
    public void editRole() {
        beginAt("/roleform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("roleDetail.title");
    }

    @Test
    public void saveRole() {
        beginAt("/roleform?id=" + getInsertedId());
        assertTitleKeyMatches("roleDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("roleDetail.title");
        assertKeyPresent("role.updated");
    }

    @After
    public void removeRole() {
        beginAt("/roleform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("roleList.title");
        assertKeyPresent("role.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/roles");
        assertTablePresent("roleList");
        Table table = getTable("roleList");
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
