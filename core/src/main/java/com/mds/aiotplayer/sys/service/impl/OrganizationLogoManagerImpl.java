/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.OrganizationLogoDao;
import com.mds.aiotplayer.sys.model.OrganizationLogo;
import com.mds.aiotplayer.sys.service.OrganizationLogoManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("organizationLogoManager")
@WebService(serviceName = "OrganizationLogoService", endpointInterface = "com.mds.aiotplayer.service.OrganizationLogoManager")
public class OrganizationLogoManagerImpl extends GenericManagerImpl<OrganizationLogo, Long> implements OrganizationLogoManager {
    OrganizationLogoDao organizationLogoDao;

    @Autowired
    public OrganizationLogoManagerImpl(OrganizationLogoDao organizationLogoDao) {
        super(organizationLogoDao);
        this.organizationLogoDao = organizationLogoDao;
    }
}