package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.model.DailyListItem;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DailyListItemManager extends GenericManager<DailyListItem, Long> {
    
}