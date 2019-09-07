package com.mds.common.service;

import com.mds.common.service.GenericManager;
import com.mds.common.model.ZipCodeType;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ZipCodeTypeManager extends GenericManager<ZipCodeType, Long> {
    
}