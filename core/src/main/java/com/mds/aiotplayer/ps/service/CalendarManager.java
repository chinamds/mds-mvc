package com.mds.aiotplayer.ps.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.ps.model.Calendar;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CalendarManager extends GenericManager<Calendar, Long> {
    
}