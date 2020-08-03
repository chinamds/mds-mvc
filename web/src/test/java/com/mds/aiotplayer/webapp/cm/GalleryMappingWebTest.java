package com.mds.aiotplayer.webapp.cm;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

public class GalleryMappingWebTest {

    private ResourceBundle messages;
    private final Logger log = LoggerFactory.getLogger(GalleryMappingWebTest.class);

    @Before
    public void setUp() {
    	setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
        setScriptingEnabled(false);
        if (log.isDebugEnabled()) {
        	log.debug("cargo.host${cargo.host}: " + System.getProperty("cargo.host"));
            log.debug("cargo.port${cargo.port}: " + System.getProperty("cargo.port"));
            log.debug("basepath${basepath}: " + System.getProperty("basepath"));
        }
        //getTestContext().setBaseUrl("http://" + System.getProperty("cargo.host") + ":" + System.getProperty("cargo.port")+  "/" + System.getProperty("basepath"));
        getTestContext().setBaseUrl("http://localhost:8080/mdsplus-web/");
        getTestContext().setResourceBundleName("messages");
        messages = ResourceBundle.getBundle("ApplicationResources");
    }

    @Before
    public void addGalleryMapping() {
        beginAt("/cm/galleryMappingform");
        assertTitleKeyMatches("galleryMappingDetail.title");
        clickButton("save");
        assertTitleKeyMatches("galleryMappingList.title");
        assertKeyPresent("galleryMapping.added");
    }

    @Test
    public void listGalleryMappings() {
        beginAt("/cm/galleryMappings");
        assertTitleKeyMatches("galleryMappingList.title");

        // check that table is present
        assertTablePresent("galleryMappingList");
    }

    @Test
    public void editGalleryMapping() {
        beginAt("/cm/galleryMappingform?id=" + getInsertedId());
        clickButton("save");
        assertTitleKeyMatches("galleryMappingDetail.title");
    }

    @Test
    public void saveGalleryMapping() {
        beginAt("/cm/galleryMappingform?id=" + getInsertedId());
        assertTitleKeyMatches("galleryMappingDetail.title");

        // update some of the required fields
        clickButton("save");
        assertTitleKeyMatches("galleryMappingDetail.title");
        assertKeyPresent("galleryMapping.updated");
    }

    @After
    public void removeGalleryMapping() {
        beginAt("/cm/galleryMappingform?id=" + getInsertedId());
        clickButton("delete");
        assertTitleKeyMatches("galleryMappingList.title");
        assertKeyPresent("galleryMapping.deleted");
    }

    /**
     * Convenience method to get the id of the inserted record
     *
     * @return last id in the table
     */
    protected String getInsertedId() {
        beginAt("/cm/galleryMappings");
        assertTablePresent("galleryMappingList");
        Table table = getTable("galleryMappingList");
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
