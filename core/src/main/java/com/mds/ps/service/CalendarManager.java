package com.mds.ps.service;

import com.mds.common.service.GenericManager;
import com.mds.ps.model.Calendar;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CalendarManager extends GenericManager<Calendar, Long> {
    
}