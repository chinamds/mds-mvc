package com.mds.common.service.impl;

import com.mds.common.dao.ZipCodeDao;
import com.mds.common.model.ZipCode;
import com.mds.common.service.ZipCodeManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("zipCodeManager")
@WebService(serviceName = "ZipCodeService", endpointInterface = "com.mds.service.ZipCodeManager")
public class ZipCodeManagerImpl extends GenericManagerImpl<ZipCode, Long> implements ZipCodeManager {
    ZipCodeDao zipCodeDao;

    @Autowired
    public ZipCodeManagerImpl(ZipCodeDao zipCodeDao) {
        super(zipCodeDao);
        this.zipCodeDao = zipCodeDao;
    }
}