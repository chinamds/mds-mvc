package com.mds.cm.service;

import com.mds.common.service.GenericManager;
import com.mds.cm.model.DailyListActivity;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DailyListActivityManager extends GenericManager<DailyListActivity, Long> {
    
}