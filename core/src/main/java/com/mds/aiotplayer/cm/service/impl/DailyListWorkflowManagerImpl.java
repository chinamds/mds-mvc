/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.dao.DailyListWorkflowDao;
import com.mds.aiotplayer.cm.model.DailyListWorkflow;
import com.mds.aiotplayer.cm.service.DailyListWorkflowManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("dailyListWorkflowManager")
@WebService(serviceName = "DailyListWorkflowService", endpointInterface = "com.mds.aiotplayer.service.DailyListWorkflowManager")
public class DailyListWorkflowManagerImpl extends GenericManagerImpl<DailyListWorkflow, Long> implements DailyListWorkflowManager {
    DailyListWorkflowDao dailyListWorkflowDao;

    @Autowired
    public DailyListWorkflowManagerImpl(DailyListWorkflowDao dailyListWorkflowDao) {
        super(dailyListWorkflowDao);
        this.dailyListWorkflowDao = dailyListWorkflowDao;
    }
}