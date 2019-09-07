package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.MyCalendar;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface MyCalendarManager extends GenericManager<MyCalendar, Long> {
	void copyAndRemove(MyCalendar calendar);
	Long countRecentlyCalendar(Long userId, Integer interval);
}