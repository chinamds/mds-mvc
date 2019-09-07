package com.mds.cm.dao.hibernate;

import com.mds.cm.model.ContentTemplate;
import com.mds.cm.dao.ContentTemplateDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
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
        getSession().saveOrUpdate(contentTemplate);
        // necessary to throw a DataIntegrityViolation and catch it in ContentTemplateManager
        getSession().flush();
        return contentTemplate;
    }
}
