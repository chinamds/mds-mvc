/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.service.impl;

import com.mds.aiotplayer.common.dao.ZipCodeTypeDao;
import com.mds.aiotplayer.common.model.ZipCodeType;
import com.mds.aiotplayer.common.service.ZipCodeTypeManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("zipCodeTypeManager")
@WebService(serviceName = "ZipCodeTypeService", endpointInterface = "com.mds.aiotplayer.common.service.ZipCodeTypeManager")
public class ZipCodeTypeManagerImpl extends GenericManagerImpl<ZipCodeType, Long> implements ZipCodeTypeManager {
    ZipCodeTypeDao zipCodeTypeDao;

    @Autowired
    public ZipCodeTypeManagerImpl(ZipCodeTypeDao zipCodeTypeDao) {
        super(zipCodeTypeDao);
        this.zipCodeTypeDao = zipCodeTypeDao;
    }
}