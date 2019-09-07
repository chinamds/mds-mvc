package com.mds.sys.dao.hibernate;

import com.mds.sys.model.MyMessageContent;
import com.mds.sys.dao.MyMessageContentDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
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
        getSession().saveOrUpdate(myMessageContent);
        // necessary to throw a DataIntegrityViolation and catch it in MyMessageContentManager
        getSession().flush();
        return myMessageContent;
    }
}
