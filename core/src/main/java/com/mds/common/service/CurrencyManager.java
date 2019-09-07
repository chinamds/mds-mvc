package com.mds.common.service;

import com.mds.common.service.GenericManager;
import com.mds.common.model.Currency;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CurrencyManager extends GenericManager<Currency, Long> {
    
}