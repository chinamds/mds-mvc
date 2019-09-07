package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.OrganizationLogo;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface OrganizationLogoManager extends GenericManager<OrganizationLogo, Long> {
    
}