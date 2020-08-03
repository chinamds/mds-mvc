package com.mds.aiotplayer.webapp.i18n;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class LocalizedResourceWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addLocalizedResource() {
        beginAt("/localizedResourceform");
        assertTitleKeyMatches("localizedResourceDetail.title");
        clickButton("save");
        assertTitleKeyMatches("localizedResourceList.title");
        assertKeyPresent("localizedResource.added");
    }

    @Test
    public void listLocalizedResources() {
        beginAt("/localizedResources");
        assertTitleKeyMatches("localizedResourceList.title");

        // check that table is present
        assertTablePresent("localizedResourceList");
    }

    @Test
    public void editLocalizedResource() {
        beginAt("/localizedResourceform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("localizedResourceDetail.title");
    }

    @Test
    public void saveLocalizedResource() {
        beginAt("/localizedResourceform?id=" + getInsertedId());
        assertTitleKeyMatches("localizedResourceDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("localizedResourceDetail.title");
        assertKeyPresent("localizedResource.updated");
    }

    @After
    public void removeLocalizedResource() {
        beginAt("/localizedResourceform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("localizedResourceList.title");
        assertKeyPresent("localizedResource.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/localizedResources");
        assertTablePresent("localizedResourceList");
        Table table = getTable("localizedResourceList");
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
