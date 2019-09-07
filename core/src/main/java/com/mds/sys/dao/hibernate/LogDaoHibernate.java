package com.mds.sys.dao.hibernate;

import com.mds.sys.model.Log;
import com.mds.sys.dao.LogDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("logDao")
public class LogDaoHibernate extends GenericDaoHibernate<Log, Long> implements LogDao {

    public LogDaoHibernate() {
        super(Log.class);
    }

	/**
     * {@inheritDoc}
     */
    public Log saveLog(Log log) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("log's id: " + log.getId());
        }
        getSession().saveOrUpdate(log);
        // necessary to throw a DataIntegrityViolation and catch it in LogManager
        getSession().flush();
        return log;
    }
}
