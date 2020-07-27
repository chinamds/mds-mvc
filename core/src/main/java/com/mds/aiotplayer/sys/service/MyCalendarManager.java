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