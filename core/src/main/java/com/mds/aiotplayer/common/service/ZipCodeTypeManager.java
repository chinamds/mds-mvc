package com.mds.aiotplayer.common.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.common.model.ZipCodeType;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ZipCodeTypeManager extends GenericManager<ZipCodeType, Long> {
    
}