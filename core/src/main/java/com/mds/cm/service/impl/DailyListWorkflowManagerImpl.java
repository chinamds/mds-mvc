package com.mds.cm.service.impl;

import com.mds.cm.dao.DailyListWorkflowDao;
import com.mds.cm.model.DailyListWorkflow;
import com.mds.cm.service.DailyListWorkflowManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("dailyListWorkflowManager")
@WebService(serviceName = "DailyListWorkflowService", endpointInterface = "com.mds.service.DailyListWorkflowManager")
public class DailyListWorkflowManagerImpl extends GenericManagerImpl<DailyListWorkflow, Long> implements DailyListWorkflowManager {
    DailyListWorkflowDao dailyListWorkflowDao;

    @Autowired
    public DailyListWorkflowManagerImpl(DailyListWorkflowDao dailyListWorkflowDao) {
        super(dailyListWorkflowDao);
        this.dailyListWorkflowDao = dailyListWorkflowDao;
    }
}