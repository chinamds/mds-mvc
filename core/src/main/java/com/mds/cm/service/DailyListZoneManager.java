package com.mds.cm.service;

import com.mds.common.service.GenericManager;
import com.mds.cm.model.DailyListZone;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface DailyListZoneManager extends GenericManager<DailyListZone, Long> {
}