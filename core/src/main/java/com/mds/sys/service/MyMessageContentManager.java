package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.MyMessageContent;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface MyMessageContentManager extends GenericManager<MyMessageContent, Long> {
    
}