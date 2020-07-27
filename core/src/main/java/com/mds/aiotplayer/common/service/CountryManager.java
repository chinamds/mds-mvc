package com.mds.aiotplayer.common.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.common.model.Country;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CountryManager extends GenericManager<Country, Long> {
    
}