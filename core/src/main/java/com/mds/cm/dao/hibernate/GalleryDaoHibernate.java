package com.mds.cm.dao.hibernate;

import com.mds.cm.model.Gallery;
import com.mds.cm.dao.GalleryDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import com.mds.common.model.Parameter;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("galleryDao")
public class GalleryDaoHibernate extends GenericDaoHibernate<Gallery, Long> implements GalleryDao {

    public GalleryDaoHibernate() {
        super(Gallery.class);
    }

	/**
     * {@inheritDoc}
     */
    public Gallery saveGallery(Gallery gallery) {
        if (log.isDebugEnabled()) {
            log.debug("gallery's id: " + gallery.getId());
        }
        getSession().saveOrUpdate(gallery);
        // necessary to throw a DataIntegrityViolation and catch it in GalleryManager
        getSession().flush();
        return gallery;
    }
    
    public List<Gallery> findGalleries(long organizationId){
    	return find("from Gallery g where exists (from g.galleryMappings gm where gm.organization.id = :p1)", new Parameter(organizationId));
    }
}
