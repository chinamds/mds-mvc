package com.mds.aiotplayer.hrm.service.impl;

import com.mds.aiotplayer.hrm.dao.IdentityTypeDao;
import com.mds.aiotplayer.hrm.model.IdentityType;
import com.mds.aiotplayer.hrm.service.IdentityTypeManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("identityTypeManager")
@WebService(serviceName = "IdentityTypeService", endpointInterface = "com.mds.aiotplayer.hrm.service.IdentityTypeManager")
public class IdentityTypeManagerImpl extends GenericManagerImpl<IdentityType, Long> implements IdentityTypeManager {
    IdentityTypeDao identityTypeDao;

    @Autowired
    public IdentityTypeManagerImpl(IdentityTypeDao identityTypeDao) {
        super(identityTypeDao);
        this.identityTypeDao = identityTypeDao;
    }
}