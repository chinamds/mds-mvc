package com.mds.aiotplayer.wf.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.wf.model.ActivityOrganizationUser;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ActivityOrganizationUserManager extends GenericManager<ActivityOrganizationUser, Long> {
    
}