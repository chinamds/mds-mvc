package com.mds.cm.service;

import com.mds.common.service.GenericManager;
import com.mds.cm.model.DailyListWorkflow;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DailyListWorkflowManager extends GenericManager<DailyListWorkflow, Long> {
    
}