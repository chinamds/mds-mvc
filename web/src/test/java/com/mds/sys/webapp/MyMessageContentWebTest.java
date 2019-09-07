package com.mds.sys.webapp;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class MyMessageContentWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addMyMessageContent() {
        beginAt("/myMessageContentform");
        assertTitleKeyMatches("myMessageContentDetail.title");
        clickButton("save");
        assertTitleKeyMatches("myMessageContentList.title");
        assertKeyPresent("myMessageContent.added");
    }

    @Test
    public void listMyMessageContents() {
        beginAt("/myMessageContents");
        assertTitleKeyMatches("myMessageContentList.title");

        // check that table is present
        assertTablePresent("myMessageContentList");
    }

    @Test
    public void editMyMessageContent() {
        beginAt("/myMessageContentform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("myMessageContentDetail.title");
    }

    @Test
    public void saveMyMessageContent() {
        beginAt("/myMessageContentform?id=" + getInsertedId());
        assertTitleKeyMatches("myMessageContentDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("myMessageContentDetail.title");
        assertKeyPresent("myMessageContent.updated");
    }

    @After
    public void removeMyMessageContent() {
        beginAt("/myMessageContentform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("myMessageContentList.title");
        assertKeyPresent("myMessageContent.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/myMessageContents");
        assertTablePresent("myMessageContentList");
        Table table = getTable("myMessageContentList");
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
