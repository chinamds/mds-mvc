/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.MyMessageContent;
import com.mds.aiotplayer.sys.dao.MyMessageContentDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("myMessageContentDao")
public class MyMessageContentDaoHibernate extends GenericDaoHibernate<MyMessageContent, Long> implements MyMessageContentDao {

    public MyMessageContentDaoHibernate() {
        super(MyMessageContent.class);
    }

	/**
     * {@inheritDoc}
     */
    public MyMessageContent saveMyMessageContent(MyMessageContent myMessageContent) {
        if (log.isDebugEnabled()) {
            log.debug("myMessageContent's id: " + myMessageContent.getId());
        }
        var result = super.save(myMessageContent);
        // necessary to throw a DataIntegrityViolation and catch it in MyMessageContentManager
        getEntityManager().flush();
        return result;
    }
}
