package com.mds.sys.service.impl;

import com.mds.sys.dao.OrganizationLogoDao;
import com.mds.sys.model.OrganizationLogo;
import com.mds.sys.service.OrganizationLogoManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("organizationLogoManager")
@WebService(serviceName = "OrganizationLogoService", endpointInterface = "com.mds.service.OrganizationLogoManager")
public class OrganizationLogoManagerImpl extends GenericManagerImpl<OrganizationLogo, Long> implements OrganizationLogoManager {
    OrganizationLogoDao organizationLogoDao;

    @Autowired
    public OrganizationLogoManagerImpl(OrganizationLogoDao organizationLogoDao) {
        super(organizationLogoDao);
        this.organizationLogoDao = organizationLogoDao;
    }
}