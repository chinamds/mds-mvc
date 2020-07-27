package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.Tag;
import com.mds.aiotplayer.cm.dao.TagDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(tag);
        // necessary to throw a DataIntegrityViolation and catch it in TagManager
        getEntityManager().flush();
        return result;
    }
}
