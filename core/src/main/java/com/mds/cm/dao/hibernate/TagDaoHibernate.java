package com.mds.cm.dao.hibernate;

import com.mds.cm.model.Tag;
import com.mds.cm.dao.TagDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("tagDao")
public class TagDaoHibernate extends GenericDaoHibernate<Tag, Long> implements TagDao {

    public TagDaoHibernate() {
        super(Tag.class);
    }

	/**
     * {@inheritDoc}
     */
    public Tag saveTag(Tag tag) {
        if (log.isDebugEnabled()) {
            log.debug("tag's id: " + tag.getTagName());
        }
        getSession().saveOrUpdate(tag);
        // necessary to throw a DataIntegrityViolation and catch it in TagManager
        getSession().flush();
        return tag;
    }
}
