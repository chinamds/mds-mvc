package com.mds.hrm.service.impl;

import com.mds.hrm.dao.IdentityTypeDao;
import com.mds.hrm.model.IdentityType;
import com.mds.hrm.service.IdentityTypeManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("identityTypeManager")
@WebService(serviceName = "IdentityTypeService", endpointInterface = "com.mds.hrm.service.IdentityTypeManager")
public class IdentityTypeManagerImpl extends GenericManagerImpl<IdentityType, Long> implements IdentityTypeManager {
    IdentityTypeDao identityTypeDao;

    @Autowired
    public IdentityTypeManagerImpl(IdentityTypeDao identityTypeDao) {
        super(identityTypeDao);
        this.identityTypeDao = identityTypeDao;
    }
}