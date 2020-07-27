package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.Log;
import com.mds.aiotplayer.sys.dao.LogDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(log);
        // necessary to throw a DataIntegrityViolation and catch it in LogManager
        getEntityManager().flush();
        return result;
    }
}
