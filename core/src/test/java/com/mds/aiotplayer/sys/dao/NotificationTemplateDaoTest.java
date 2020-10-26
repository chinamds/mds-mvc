/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.NotificationTemplate;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NotificationTemplateDaoTest extends BaseDaoTestCase {
    @Autowired
    private NotificationTemplateDao notificationTemplateDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveNotificationTemplate() {
        NotificationTemplate notificationTemplate = new NotificationTemplate();

        // enter all required fields
        notificationTemplate.setName("EeIpQhDwAgRdSeSsEfLyWvPtXaDuKfMiTvFmBsApWpGdQaOsYwGyVqOeLeNdLjZaOaShVsHnUnBwHuXvPuXqQzJoStCuNkNsBnQk");

        log.debug("adding notificationTemplate...");
        notificationTemplate = notificationTemplateDao.save(notificationTemplate);

        notificationTemplate = notificationTemplateDao.get(notificationTemplate.getId());

        assertNotNull(notificationTemplate.getId());

        log.debug("removing notificationTemplate...");

        notificationTemplateDao.remove(notificationTemplate.getId());

        // should throw DataAccessException 
        notificationTemplateDao.get(notificationTemplate.getId());
    }
}