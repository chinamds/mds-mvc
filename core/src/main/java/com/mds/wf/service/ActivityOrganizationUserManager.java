package com.mds.wf.service;

import com.mds.common.service.GenericManager;
import com.mds.wf.model.ActivityOrganizationUser;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ActivityOrganizationUserManager extends GenericManager<ActivityOrganizationUser, Long> {
    
}