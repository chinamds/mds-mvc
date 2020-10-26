/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.MyCalendar;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface MyCalendarManager extends GenericManager<MyCalendar, Long> {
	void copyAndRemove(MyCalendar calendar);
	Long countRecentlyCalendar(Long userId, Integer interval);
}