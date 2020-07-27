package com.mds.aiotplayer.ps.service.impl;

import com.mds.aiotplayer.ps.dao.ChannelDao;
import com.mds.aiotplayer.ps.model.Channel;
import com.mds.aiotplayer.ps.service.ChannelManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("channelManager")
@WebService(serviceName = "ChannelService", endpointInterface = "com.mds.aiotplayer.ps.service.ChannelManager")
public class ChannelManagerImpl extends GenericManagerImpl<Channel, Long> implements ChannelManager {
    ChannelDao channelDao;

    @Autowired
    public ChannelManagerImpl(ChannelDao channelDao) {
        super(channelDao);
        this.channelDao = channelDao;
    }
}