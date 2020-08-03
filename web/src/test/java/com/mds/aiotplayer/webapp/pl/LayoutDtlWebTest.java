package com.mds.aiotplayer.webapp.pl;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class LayoutDtlWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addLayoutDtl() {
        beginAt("/layoutDtlform");
        assertTitleKeyMatches("layoutDtlDetail.title");
        clickButton("save");
        assertTitleKeyMatches("layoutDtlList.title");
        assertKeyPresent("layoutDtl.added");
    }

    @Test
    public void listLayoutDtls() {
        beginAt("/layoutDtls");
        assertTitleKeyMatches("layoutDtlList.title");

        // check that table is present
        assertTablePresent("layoutDtlList");
    }

    @Test
    public void editLayoutDtl() {
        beginAt("/layoutDtlform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("layoutDtlDetail.title");
    }

    @Test
    public void saveLayoutDtl() {
        beginAt("/layoutDtlform?id=" + getInsertedId());
        assertTitleKeyMatches("layoutDtlDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("layoutDtlDetail.title");
        assertKeyPresent("layoutDtl.updated");
    }

    @After
    public void removeLayoutDtl() {
        beginAt("/layoutDtlform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("layoutDtlList.title");
        assertKeyPresent("layoutDtl.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/layoutDtls");
        assertTablePresent("layoutDtlList");
        Table table = getTable("layoutDtlList");
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
