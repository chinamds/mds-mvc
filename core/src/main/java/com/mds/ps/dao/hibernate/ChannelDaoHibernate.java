package com.mds.ps.dao.hibernate;

import com.mds.ps.model.Channel;
import com.mds.ps.dao.ChannelDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("channelDao")
public class ChannelDaoHibernate extends GenericDaoHibernate<Channel, Long> implements ChannelDao {

    public ChannelDaoHibernate() {
        super(Channel.class);
    }
}
