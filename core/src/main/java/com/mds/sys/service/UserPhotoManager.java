package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.UserPhoto;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface UserPhotoManager extends GenericManager<UserPhoto, Long> {
    
}