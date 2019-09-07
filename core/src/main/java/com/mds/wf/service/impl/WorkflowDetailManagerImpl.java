package com.mds.wf.service.impl;

import com.mds.wf.dao.WorkflowDetailDao;
import com.mds.wf.model.WorkflowDetail;
import com.mds.wf.service.WorkflowDetailManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("workflowDetailManager")
@WebService(serviceName = "WorkflowDetailService", endpointInterface = "com.mds.service.WorkflowDetailManager")
public class WorkflowDetailManagerImpl extends GenericManagerImpl<WorkflowDetail, Long> implements WorkflowDetailManager {
    WorkflowDetailDao workflowDetailDao;

    @Autowired
    public WorkflowDetailManagerImpl(WorkflowDetailDao workflowDetailDao) {
        super(workflowDetailDao);
        this.workflowDetailDao = workflowDetailDao;
    }
}