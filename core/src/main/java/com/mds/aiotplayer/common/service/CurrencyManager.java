package com.mds.aiotplayer.common.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.common.model.Currency;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CurrencyManager extends GenericManager<Currency, Long> {
    
}