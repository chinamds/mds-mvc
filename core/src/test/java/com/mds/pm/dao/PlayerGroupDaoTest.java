package com.mds.pm.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.pm.model.PlayerGroup;
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