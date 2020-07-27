package com.mds.aiotplayer.wf.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.wf.model.WorkflowDetail;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface WorkflowDetailManager extends GenericManager<WorkflowDetail, Long> {
    
}