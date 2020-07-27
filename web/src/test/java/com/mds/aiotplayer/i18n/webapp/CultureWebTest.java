package com.mds.aiotplayer.webapp.i18n;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class CultureWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addCulture() {
        beginAt("/cultureform");
        assertTitleKeyMatches("cultureDetail.title");
        clickButton("save");
        assertTitleKeyMatches("cultureList.title");
        assertKeyPresent("culture.added");
    }

    @Test
    public void listCultures() {
        beginAt("/cultures");
        assertTitleKeyMatches("cultureList.title");

        // check that table is present
        assertTablePresent("cultureList");
    }

    @Test
    public void editCulture() {
        beginAt("/cultureform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("cultureDetail.title");
    }

    @Test
    public void saveCulture() {
        beginAt("/cultureform?id=" + getInsertedId());
        assertTitleKeyMatches("cultureDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("cultureDetail.title");
        assertKeyPresent("culture.updated");
    }

    @After
    public void removeCulture() {
        beginAt("/cultureform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("cultureList.title");
        assertKeyPresent("culture.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/cultures");
        assertTablePresent("cultureList");
        Table table = getTable("cultureList");
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
