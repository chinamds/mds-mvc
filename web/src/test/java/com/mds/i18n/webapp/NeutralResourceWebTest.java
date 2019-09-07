package com.mds.i18n.webapp;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class NeutralResourceWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addNeutralResource() {
        beginAt("/neutralResourceform");
        assertTitleKeyMatches("neutralResourceDetail.title");
        clickButton("save");
        assertTitleKeyMatches("neutralResourceList.title");
        assertKeyPresent("neutralResource.added");
    }

    @Test
    public void listNeutralResources() {
        beginAt("/neutralResources");
        assertTitleKeyMatches("neutralResourceList.title");

        // check that table is present
        assertTablePresent("neutralResourceList");
    }

    @Test
    public void editNeutralResource() {
        beginAt("/neutralResourceform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("neutralResourceDetail.title");
    }

    @Test
    public void saveNeutralResource() {
        beginAt("/neutralResourceform?id=" + getInsertedId());
        assertTitleKeyMatches("neutralResourceDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("neutralResourceDetail.title");
        assertKeyPresent("neutralResource.updated");
    }

    @After
    public void removeNeutralResource() {
        beginAt("/neutralResourceform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("neutralResourceList.title");
        assertKeyPresent("neutralResource.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/neutralResources");
        assertTablePresent("neutralResourceList");
        Table table = getTable("neutralResourceList");
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
