/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.ps.dao.hibernate;

import com.mds.aiotplayer.ps.model.Channel;
import com.mds.aiotplayer.ps.dao.ChannelDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("channelDao")
public class ChannelDaoHibernate extends GenericDaoHibernate<Channel, Long> implements ChannelDao {

    public ChannelDaoHibernate() {
        super(Channel.class);
    }
}
