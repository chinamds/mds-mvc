package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.UserPhoto;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface UserPhotoManager extends GenericManager<UserPhoto, Long> {
    
}