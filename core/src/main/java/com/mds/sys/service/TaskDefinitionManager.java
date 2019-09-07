package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.TaskDefinition;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface TaskDefinitionManager extends GenericManager<TaskDefinition, Long> {
    
}