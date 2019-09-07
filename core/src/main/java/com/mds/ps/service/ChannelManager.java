package com.mds.ps.service;

import com.mds.common.service.GenericManager;
import com.mds.ps.model.Channel;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ChannelManager extends GenericManager<Channel, Long> {
    
}