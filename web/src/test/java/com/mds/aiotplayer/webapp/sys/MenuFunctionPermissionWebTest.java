package com.mds.aiotplayer.webapp.sys;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class MenuFunctionPermissionWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addMenuFunctionPermission() {
        beginAt("/menuFunctionPermissionform");
        assertTitleKeyMatches("menuFunctionPermissionDetail.title");
        clickButton("save");
        assertTitleKeyMatches("menuFunctionPermissionList.title");
        assertKeyPresent("menuFunctionPermission.added");
    }

    @Test
    public void listMenuFunctionPermissions() {
        beginAt("/menuFunctionPermissions");
        assertTitleKeyMatches("menuFunctionPermissionList.title");

        // check that table is present
        assertTablePresent("menuFunctionPermissionList");
    }

    @Test
    public void editMenuFunctionPermission() {
        beginAt("/menuFunctionPermissionform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("menuFunctionPermissionDetail.title");
    }

    @Test
    public void saveMenuFunctionPermission() {
        beginAt("/menuFunctionPermissionform?id=" + getInsertedId());
        assertTitleKeyMatches("menuFunctionPermissionDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("menuFunctionPermissionDetail.title");
        assertKeyPresent("menuFunctionPermission.updated");
    }

    @After
    public void removeMenuFunctionPermission() {
        beginAt("/menuFunctionPermissionform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("menuFunctionPermissionList.title");
        assertKeyPresent("menuFunctionPermission.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/menuFunctionPermissions");
        assertTablePresent("menuFunctionPermissionList");
        Table table = getTable("menuFunctionPermissionList");
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
