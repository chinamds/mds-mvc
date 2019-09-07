package com.mds.sys.webapp;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class PermissionWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addPermission() {
        beginAt("/sys/permissionform");
        assertTitleKeyMatches("permissionDetail.title");
        clickButton("save");
        assertTitleKeyMatches("permissionList.title");
        assertKeyPresent("permission.added");
    }

    @Test
    public void listPermissions() {
        beginAt("/sys/permissions");
        assertTitleKeyMatches("permissionList.title");

        // check that table is present
        assertTablePresent("permissionList");
    }

    @Test
    public void editPermission() {
        beginAt("/sys/permissionform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("permissionDetail.title");
    }

    @Test
    public void savePermission() {
        beginAt("/sys/permissionform?id=" + getInsertedId());
        assertTitleKeyMatches("permissionDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("permissionDetail.title");
        assertKeyPresent("permission.updated");
    }

    @After
    public void removePermission() {
        beginAt("/sys/permissionform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("permissionList.title");
        assertKeyPresent("permission.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/sys/permissions");
        assertTablePresent("permissionList");
        Table table = getTable("permissionList");
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
