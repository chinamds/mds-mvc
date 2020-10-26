/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.service.impl;

import com.mds.aiotplayer.common.dao.ZipCodeDao;
import com.mds.aiotplayer.common.model.ZipCode;
import com.mds.aiotplayer.common.service.ZipCodeManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("zipCodeManager")
@WebService(serviceName = "ZipCodeService", endpointInterface = "com.mds.aiotplayer.service.ZipCodeManager")
public class ZipCodeManagerImpl extends GenericManagerImpl<ZipCode, Long> implements ZipCodeManager {
    ZipCodeDao zipCodeDao;

    @Autowired
    public ZipCodeManagerImpl(ZipCodeDao zipCodeDao) {
        super(zipCodeDao);
        this.zipCodeDao = zipCodeDao;
    }
}