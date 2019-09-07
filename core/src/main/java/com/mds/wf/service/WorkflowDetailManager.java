package com.mds.wf.service;

import com.mds.common.service.GenericManager;
import com.mds.wf.model.WorkflowDetail;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface WorkflowDetailManager extends GenericManager<WorkflowDetail, Long> {
    
}