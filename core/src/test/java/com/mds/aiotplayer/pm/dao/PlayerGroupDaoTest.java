/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pm.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.pm.model.PlayerGroup;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlayerGroupDaoTest extends BaseDaoTestCase {
    @Autowired
    private PlayerGroupDao playerGroupDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemovePlayerGroup() {
        PlayerGroup playerGroup = new PlayerGroup();

        // enter all required fields
        playerGroup.setCode("EaZvNjGwPkOkSxWoLsIaFoUgYzKyCiMyWdOmXd");
        playerGroup.setDescription("UaDaUnKpWfEePoVbTaIzEzTfFnDpOrPdIoCwMuPxOxOaHjVcSsGzZgKgOoIsYuWgMpTqCrSzCtMdBwPjTcGyHlWuOyDlSeNbArRqItLjWuMhHbToCiTvYkUeZeZaCqEwVaLvGfJtKvZbVgVgXxWzQkXyYqAoWoFeGeBoHrIjObOhCcArAuRpZhUaOdBwBpLbPrXwUxJaTeVkGuHbJyUtFdRkZnYxVsQgWhSdSfLfQyEoRsVaDxAlYdQsPsYwNaPz");

        log.debug("adding playerGroup...");
        playerGroup = playerGroupDao.save(playerGroup);

        playerGroup = playerGroupDao.get(playerGroup.getId());

        assertNotNull(playerGroup.getId());

        log.debug("removing playerGroup...");

        playerGroupDao.remove(playerGroup.getId());

        // should throw DataAccessException 
        playerGroupDao.get(playerGroup.getId());
    }
}