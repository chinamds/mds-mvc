/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.MimeType;
import com.mds.aiotplayer.cm.dao.MimeTypeDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("mimeTypeDao")
public class MimeTypeDaoHibernate extends GenericDaoHibernate<MimeType, Long> implements MimeTypeDao {

    public MimeTypeDaoHibernate() {
        super(MimeType.class);
    }

	/**
     * {@inheritDoc}
     */
    public MimeType saveMimeType(MimeType mimeType) {
        if (log.isDebugEnabled()) {
            log.debug("mimeType's id: " + mimeType.getId());
        }
        var result = super.save(mimeType);
        // necessary to throw a DataIntegrityViolation and catch it in MimeTypeManager
        getEntityManager().flush();
        return result;
    }
}
