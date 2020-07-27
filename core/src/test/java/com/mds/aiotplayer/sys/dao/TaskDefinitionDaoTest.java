package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.TaskDefinition;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TaskDefinitionDaoTest extends BaseDaoTestCase {
    @Autowired
    private TaskDefinitionDao taskDefinitionDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveTaskDefinition() {
        TaskDefinition taskDefinition = new TaskDefinition();

        // enter all required fields
        taskDefinition.setName("ZeQrAgBpUhBrUuRfJjKsRnYjVkUjBqSzAnZsDfXiDsEyFtCrPlUlEwPvJjVjGhXqQjVaLrGkSjOkDfXbQzAwDpYsOsWbQdKhQdAz");

        log.debug("adding taskDefinition...");
        taskDefinition = taskDefinitionDao.save(taskDefinition);

        taskDefinition = taskDefinitionDao.get(taskDefinition.getId());

        assertNotNull(taskDefinition.getId());

        log.debug("removing taskDefinition...");

        taskDefinitionDao.remove(taskDefinition.getId());

        // should throw DataAccessException 
        taskDefinitionDao.get(taskDefinition.getId());
    }
}