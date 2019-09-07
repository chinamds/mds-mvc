package com.mds.cm.dao.hibernate;

import com.mds.cm.model.MetadataTag;
import com.mds.cm.dao.MetadataTagDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("metadataTagDao")
public class MetadataTagDaoHibernate extends GenericDaoHibernate<MetadataTag, Long> implements MetadataTagDao {

    public MetadataTagDaoHibernate() {
        super(MetadataTag.class);
    }

	/**
     * {@inheritDoc}
     */
    public MetadataTag saveMetadataTag(MetadataTag metadataTag) {
        if (log.isDebugEnabled()) {
            log.debug("metadataTag's id: " + metadataTag.getId());
        }
        getSession().saveOrUpdate(metadataTag);
        // necessary to throw a DataIntegrityViolation and catch it in MetadataTagManager
        getSession().flush();
        return metadataTag;
    }
}
