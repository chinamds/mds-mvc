package com.mds.aiotplayer.webapp.sys;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class NotificationWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addNotification() {
        beginAt("/notificationform");
        assertTitleKeyMatches("notificationDetail.title");
        clickButton("save");
        assertTitleKeyMatches("notificationList.title");
        assertKeyPresent("notification.added");
    }

    @Test
    public void listNotifications() {
        beginAt("/notifications");
        assertTitleKeyMatches("notificationList.title");

        // check that table is present
        assertTablePresent("notificationList");
    }

    @Test
    public void editNotification() {
        beginAt("/notificationform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("notificationDetail.title");
    }

    @Test
    public void saveNotification() {
        beginAt("/notificationform?id=" + getInsertedId());
        assertTitleKeyMatches("notificationDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("notificationDetail.title");
        assertKeyPresent("notification.updated");
    }

    @After
    public void removeNotification() {
        beginAt("/notificationform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("notificationList.title");
        assertKeyPresent("notification.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/notifications");
        assertTablePresent("notificationList");
        Table table = getTable("notificationList");
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
