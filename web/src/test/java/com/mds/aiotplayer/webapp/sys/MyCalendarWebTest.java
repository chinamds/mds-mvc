package com.mds.aiotplayer.webapp.sys;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class MyCalendarWebTest {

    private ResourceBundle messages;

    @Before
    public void setUp() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port"));
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("messages");
    }

    @Before
    public void addMyCalendar() {
        beginAt("/myCalendarform");
        assertTitleKeyMatches("myCalendarDetail.title");
        clickButton("save");
        assertTitleKeyMatches("myCalendarList.title");
        assertKeyPresent("myCalendar.added");
    }

    @Test
    public void listMyCalendars() {
        beginAt("/myCalendars");
        assertTitleKeyMatches("myCalendarList.title");

        // check that table is present
        assertTablePresent("myCalendarList");
    }

    @Test
    public void editMyCalendar() {
        beginAt("/myCalendarform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("myCalendarDetail.title");
    }

    @Test
    public void saveMyCalendar() {
        beginAt("/myCalendarform?id=" + getInsertedId());
        assertTitleKeyMatches("myCalendarDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("myCalendarDetail.title");
        assertKeyPresent("myCalendar.updated");
    }

    @After
    public void removeMyCalendar() {
        beginAt("/myCalendarform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("myCalendarList.title");
        assertKeyPresent("myCalendar.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/myCalendars");
        assertTablePresent("myCalendarList");
        Table table = getTable("myCalendarList");
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
