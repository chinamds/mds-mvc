package com.mds.cm.dao;

import com.mds.common.dao.GenericDao;

import java.util.List;

import com.mds.cm.model.DailyListZone;

/**
 * An interface that provides a data management interface to the DailyListZone table.
 */
public interface DailyListZoneDao extends GenericDao<DailyListZone, Long> {
	List<DailyListZone> getDailyListZones(long dailyListId);
}