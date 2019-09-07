package com.mds.common.service;

import com.mds.common.service.GenericManager;
import com.mds.common.model.ZipCode;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ZipCodeManager extends GenericManager<ZipCode, Long> {
    
}