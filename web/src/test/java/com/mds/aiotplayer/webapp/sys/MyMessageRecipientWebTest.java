/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class MyMessageRecipientWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addMyMessageRecipient() {
        beginAt("/myMessageRecipientform");
        assertTitleKeyMatches("myMessageRecipientDetail.title");
        clickButton("save");
        assertTitleKeyMatches("myMessageRecipientList.title");
        assertKeyPresent("myMessageRecipient.added");
    }

    @Test
    public void listMyMessageRecipients() {
        beginAt("/myMessageRecipients");
        assertTitleKeyMatches("myMessageRecipientList.title");

        // check that table is present
        assertTablePresent("myMessageRecipientList");
    }

    @Test
    public void editMyMessageRecipient() {
        beginAt("/myMessageRecipientform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("myMessageRecipientDetail.title");
    }

    @Test
    public void saveMyMessageRecipient() {
        beginAt("/myMessageRecipientform?id=" + getInsertedId());
        assertTitleKeyMatches("myMessageRecipientDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("myMessageRecipientDetail.title");
        assertKeyPresent("myMessageRecipient.updated");
    }

    @After
    public void removeMyMessageRecipient() {
        beginAt("/myMessageRecipientform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("myMessageRecipientList.title");
        assertKeyPresent("myMessageRecipient.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/myMessageRecipients");
        assertTablePresent("myMessageRecipientList");
        Table table = getTable("myMessageRecipientList");
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
