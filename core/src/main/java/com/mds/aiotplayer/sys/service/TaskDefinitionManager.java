package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.TaskDefinition;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface TaskDefinitionManager extends GenericManager<TaskDefinition, Long> {
    
}