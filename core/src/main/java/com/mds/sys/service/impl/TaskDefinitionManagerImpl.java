package com.mds.sys.service.impl;

import com.mds.sys.dao.TaskDefinitionDao;
import com.mds.sys.model.TaskDefinition;
import com.mds.sys.service.TaskDefinitionManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("taskDefinitionManager")
@WebService(serviceName = "TaskDefinitionService", endpointInterface = "com.mds.sys.service.TaskDefinitionManager")
public class TaskDefinitionManagerImpl extends GenericManagerImpl<TaskDefinition, Long> implements TaskDefinitionManager {
    TaskDefinitionDao taskDefinitionDao;

    @Autowired
    public TaskDefinitionManagerImpl(TaskDefinitionDao taskDefinitionDao) {
        super(taskDefinitionDao);
        this.taskDefinitionDao = taskDefinitionDao;
    }
}