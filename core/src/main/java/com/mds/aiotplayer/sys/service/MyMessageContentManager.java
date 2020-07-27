package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.MyMessageContent;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface MyMessageContentManager extends GenericManager<MyMessageContent, Long> {
    
}