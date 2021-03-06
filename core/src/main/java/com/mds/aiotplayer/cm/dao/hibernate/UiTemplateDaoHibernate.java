/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.cm.dao.UiTemplateDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("uiTemplateDao")
public class UiTemplateDaoHibernate extends GenericDaoHibernate<UiTemplate, Long> implements UiTemplateDao {

    public UiTemplateDaoHibernate() {
        super(UiTemplate.class);
    }

	/**
     * {@inheritDoc}
     */
    public UiTemplate saveUiTemplate(UiTemplate uiTemplate) {
        if (log.isDebugEnabled()) {
            log.debug("uiTemplate's id: " + uiTemplate.getId());
        }
        var result = super.save(uiTemplate);
        // necessary to throw a DataIntegrityViolation and catch it in UiTemplateManager
        getEntityManager().flush();
        return result;
    }
}
