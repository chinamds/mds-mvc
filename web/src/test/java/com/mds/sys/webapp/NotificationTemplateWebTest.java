package com.mds.sys.webapp;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class NotificationTemplateWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addNotificationTemplate() {
        beginAt("/notificationTemplateform");
        assertTitleKeyMatches("notificationTemplateDetail.title");
        setTextField("name", "DbQmRcXbKsKoEiJwYsDgMlJlXzEdGcHmUqWzBpObNbLiKxCxZzZpHsGeUuYxHhMrTnGaNuDqBbCsSwLbIyBqTkRvUyEuWhPaBlQk");
        clickButton("save");
        assertTitleKeyMatches("notificationTemplateList.title");
        assertKeyPresent("notificationTemplate.added");
    }

    @Test
    public void listNotificationTemplates() {
        beginAt("/notificationTemplates");
        assertTitleKeyMatches("notificationTemplateList.title");

        // check that table is present
        assertTablePresent("notificationTemplateList");
    }

    @Test
    public void editNotificationTemplate() {
        beginAt("/notificationTemplateform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("notificationTemplateDetail.title");
    }

    @Test
    public void saveNotificationTemplate() {
        beginAt("/notificationTemplateform?id=" + getInsertedId());
        assertTitleKeyMatches("notificationTemplateDetail.title");

        // update some of the required fields
        setTextField("name", "TsNmHbYrYhLoKmKlPyFwKtXdEyYoVzVgXiJzSkTkZyDxBpMrQdJiSsDoWjFhQfLdMoGjLaBcHxIiNfYqBpGtFtPhIcDfJeJlMpUh");
        clickButton("save");
        assertTitleKeyMatches("notificationTemplateDetail.title");
        assertKeyPresent("notificationTemplate.updated");
    }

    @After
    public void removeNotificationTemplate() {
        beginAt("/notificationTemplateform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("notificationTemplateList.title");
        assertKeyPresent("notificationTemplate.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/notificationTemplates");
        assertTablePresent("notificationTemplateList");
        Table table = getTable("notificationTemplateList");
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
