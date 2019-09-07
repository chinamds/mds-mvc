package com.mds.common.service;

import com.mds.common.service.GenericManager;
import com.mds.common.model.Country;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CountryManager extends GenericManager<Country, Long> {
    
}