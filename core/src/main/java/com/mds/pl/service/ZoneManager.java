package com.mds.pl.service;

import com.mds.common.service.GenericManager;
import com.mds.pl.model.Zone;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ZoneManager extends GenericManager<Zone, Long> {
    
}