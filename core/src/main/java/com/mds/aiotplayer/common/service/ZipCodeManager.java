package com.mds.aiotplayer.common.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.common.model.ZipCode;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ZipCodeManager extends GenericManager<ZipCode, Long> {
    
}