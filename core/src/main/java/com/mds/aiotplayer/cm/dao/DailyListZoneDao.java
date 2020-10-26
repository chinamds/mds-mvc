/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import java.util.List;

import com.mds.aiotplayer.cm.model.DailyListZone;

/**
 * An interface that provides a data management interface to the DailyListZone table.
 */
public interface DailyListZoneDao extends GenericDao<DailyListZone, Long> {
	List<DailyListZone> getDailyListZones(long dailyListId);
}