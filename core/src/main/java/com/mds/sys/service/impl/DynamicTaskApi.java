package com.mds.sys.service.impl;

import com.mds.sys.model.TaskDefinition;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-17
 * <p>Version: 1.0
 */
public interface DynamicTaskApi {

    public void addTaskDefinition(TaskDefinition taskDefinition);
    public void updateTaskDefinition(TaskDefinition taskDefinition);
    public void removeTaskDefinition(boolean forceTermination, Long... taskDefinitionIds);


    public void startTask(Long... taskDefinitionIds);
    public void stopTask(boolean forceTermination, Long... taskDefinitionId);


}
