/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.ContentType;
import com.mds.aiotplayer.cm.dao.ContentTypeDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("contentTypeDao")
public class ContentTypeDaoHibernate extends GenericDaoHibernate<ContentType, Long> implements ContentTypeDao {

    public ContentTypeDaoHibernate() {
        super(ContentType.class);
    }

	/**
     * {@inheritDoc}
     */
    public ContentType saveContentType(ContentType contentType) {
        if (log.isDebugEnabled()) {
            log.debug("contentType's id: " + contentType.getId());
        }
        var result = super.save(contentType);
        // necessary to throw a DataIntegrityViolation and catch it in ContentTypeManager
        getEntityManager().flush();
        
        return result;
    }
}
