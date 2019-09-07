package com.mds.cm.dao.hibernate;

import com.mds.cm.model.UiTemplate;
import com.mds.cm.dao.UiTemplateDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
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
        getSession().saveOrUpdate(uiTemplate);
        // necessary to throw a DataIntegrityViolation and catch it in UiTemplateManager
        getSession().flush();
        return uiTemplate;
    }
}
