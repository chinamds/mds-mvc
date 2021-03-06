/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.service.impl;

import com.mds.aiotplayer.common.dao.CountryDao;
import com.mds.aiotplayer.common.model.Country;
import com.mds.aiotplayer.common.service.CountryManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("countryManager")
@WebService(serviceName = "CountryService", endpointInterface = "com.mds.aiotplayer.common.service.CountryManager")
public class CountryManagerImpl extends GenericManagerImpl<Country, Long> implements CountryManager {
    CountryDao countryDao;

    @Autowired
    public CountryManagerImpl(CountryDao countryDao) {
        super(countryDao);
        this.countryDao = countryDao;
    }
}