package com.mds.cm.dao.hibernate;

import com.mds.cm.model.Metadata;
import com.mds.cm.dao.MetadataDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("metadataDao")
public class MetadataDaoHibernate extends GenericDaoHibernate<Metadata, Long> implements MetadataDao {

    public MetadataDaoHibernate() {
        super(Metadata.class);
    }

	/**
     * {@inheritDoc}
     */
    public Metadata saveMetadata(Metadata metadata) {
        if (log.isDebugEnabled()) {
            log.debug("metadata's id: " + metadata.getId());
        }
        getSession().saveOrUpdate(metadata);
        // necessary to throw a DataIntegrityViolation and catch it in MetadataManager
        getSession().flush();
        return metadata;
    }
}
