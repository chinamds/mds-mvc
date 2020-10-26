/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.TaskDefinitionDao;
import com.mds.aiotplayer.sys.model.TaskDefinition;
import com.mds.aiotplayer.sys.service.TaskDefinitionManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.jws.WebService;

@Service("taskDefinitionManager")
@WebService(serviceName = "TaskDefinitionService", endpointInterface = "com.mds.aiotplayer.sys.service.TaskDefinitionManager")
public class TaskDefinitionManagerImpl extends GenericManagerImpl<TaskDefinition, Long> implements TaskDefinitionManager {
    TaskDefinitionDao taskDefinitionDao;

    @Autowired
    public TaskDefinitionManagerImpl(TaskDefinitionDao taskDefinitionDao) {
        super(taskDefinitionDao);
        this.taskDefinitionDao = taskDefinitionDao;
    }
}