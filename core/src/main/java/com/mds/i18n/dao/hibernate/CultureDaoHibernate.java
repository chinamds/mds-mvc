package com.mds.i18n.dao.hibernate;

import com.mds.i18n.model.Culture;
import com.mds.i18n.dao.CultureDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("culturesDao")
public class CultureDaoHibernate extends GenericDaoHibernate<Culture, Long> implements CultureDao {

    public CultureDaoHibernate() {
        super(Culture.class);
    }

	
    /**
     * {@inheritDoc}
     */
    public Culture saveCulture(Culture culture) {
        if (log.isDebugEnabled()) {
            log.debug("culture's id: " + culture.getId());
        }
        getSession().saveOrUpdate(culture);
        // necessary to throw a DataIntegrityViolation and catch it in CultureManager
        getSession().flush();
        return culture;
    }
}
