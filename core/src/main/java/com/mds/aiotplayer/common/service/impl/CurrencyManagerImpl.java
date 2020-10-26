/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.service.impl;

import com.mds.aiotplayer.common.dao.CurrencyDao;
import com.mds.aiotplayer.common.model.Currency;
import com.mds.aiotplayer.common.service.CurrencyManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("currencyManager")
@WebService(serviceName = "CurrencyService", endpointInterface = "com.mds.aiotplayer.common.service.CurrencyManager")
public class CurrencyManagerImpl extends GenericManagerImpl<Currency, Long> implements CurrencyManager {
    CurrencyDao currencyDao;

    @Autowired
    public CurrencyManagerImpl(CurrencyDao currencyDao) {
        super(currencyDao);
        this.currencyDao = currencyDao;
    }
}