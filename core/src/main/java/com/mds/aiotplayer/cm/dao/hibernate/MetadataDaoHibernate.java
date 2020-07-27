package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.Metadata;
import com.mds.aiotplayer.cm.dao.MetadataDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(metadata);
        // necessary to throw a DataIntegrityViolation and catch it in MetadataManager
        getEntityManager().flush();
        
        return result;
    }
}
