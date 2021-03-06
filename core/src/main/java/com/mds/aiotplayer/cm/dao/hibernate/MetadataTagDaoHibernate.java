/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.MetadataTag;
import com.mds.aiotplayer.cm.dao.MetadataTagDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result =super.save(metadataTag);
        // necessary to throw a DataIntegrityViolation and catch it in MetadataTagManager
        getEntityManager().flush();
        
        return result;
    }
}
