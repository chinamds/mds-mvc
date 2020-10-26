/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.ContentTemplate;
import com.mds.aiotplayer.cm.dao.ContentTemplateDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("contentTemplateDao")
public class ContentTemplateDaoHibernate extends GenericDaoHibernate<ContentTemplate, Long> implements ContentTemplateDao {

    public ContentTemplateDaoHibernate() {
        super(ContentTemplate.class);
    }

	/**
     * {@inheritDoc}
     */
    public ContentTemplate saveContentTemplate(ContentTemplate contentTemplate) {
        if (log.isDebugEnabled()) {
            log.debug("contentTemplate's id: " + contentTemplate.getId());
        }
        var result = super.save(contentTemplate);
        // necessary to throw a DataIntegrityViolation and catch it in ContentTemplateManager
        getEntityManager().flush();
        
        return result;
    }
}
