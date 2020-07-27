package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.cm.dao.ContentObjectDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("contentObjectDao")
public class ContentObjectDaoHibernate extends GenericDaoHibernate<ContentObject, Long> implements ContentObjectDao {

    public ContentObjectDaoHibernate() {
        super(ContentObject.class);
    }

	/**
     * {@inheritDoc}
     */
    public ContentObject saveContentObject(ContentObject contentObject) {
        if (log.isDebugEnabled()) {
            log.debug("contentObject's id: " + contentObject.getId());
        }
        var result = super.save(contentObject);
        // necessary to throw a DataIntegrityViolation and catch it in ContentObjectManager
        getEntityManager().flush();
        
        return result;
    }
}
