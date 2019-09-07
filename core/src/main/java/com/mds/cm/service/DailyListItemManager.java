package com.mds.cm.service;

import com.mds.common.service.GenericManager;
import com.mds.cm.model.DailyListItem;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DailyListItemManager extends GenericManager<DailyListItem, Long> {
    
}