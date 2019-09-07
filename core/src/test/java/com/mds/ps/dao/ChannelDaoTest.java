package com.mds.ps.dao;

import com.mds.common.dao.BaseDaoTestCase;
import com.mds.ps.model.Channel;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ChannelDaoTest extends BaseDaoTestCase {
    @Autowired
    private ChannelDao channelDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveChannel() {
        Channel channel = new Channel();

        // enter all required fields
        channel.setBAllContent(new Byte("35"));
        channel.setBImm(new Byte("49"));
        channel.setBIncludeToday(new Byte("5"));
        channel.setChannelDesc("YhVhUoLiGwIwPdAgCyXtMzPuVnFuTkMbDcZpGuPlPjXaFtYtZoJcUtNnRnInCkWsDpNcKnEsPzKnHtQcJkXaGiCfLmAzTvLsIcGf");
        channel.setChannelName("XzHhHaTuQuVpGgBvWbVcGiXvNbOuDwEjSjItVrCtNrPvNyLyFb");
        channel.setPeriod(new Short("2127"));

        log.debug("adding channel...");
        channel = channelDao.save(channel);

        channel = channelDao.get(channel.getId());

        assertNotNull(channel.getId());

        log.debug("removing channel...");

        channelDao.remove(channel.getId());

        // should throw DataAccessException 
        channelDao.get(channel.getId());
    }
}