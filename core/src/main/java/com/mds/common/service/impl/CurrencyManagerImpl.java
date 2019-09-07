package com.mds.common.service.impl;

import com.mds.common.dao.CurrencyDao;
import com.mds.common.model.Currency;
import com.mds.common.service.CurrencyManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("currencyManager")
@WebService(serviceName = "CurrencyService", endpointInterface = "com.mds.common.service.CurrencyManager")
public class CurrencyManagerImpl extends GenericManagerImpl<Currency, Long> implements CurrencyManager {
    CurrencyDao currencyDao;

    @Autowired
    public CurrencyManagerImpl(CurrencyDao currencyDao) {
        super(currencyDao);
        this.currencyDao = currencyDao;
    }
}