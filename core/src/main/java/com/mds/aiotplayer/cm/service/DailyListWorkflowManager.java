package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.model.DailyListWorkflow;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DailyListWorkflowManager extends GenericManager<DailyListWorkflow, Long> {
    
}