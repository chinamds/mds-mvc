/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao.hibernate;

import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.cm.dao.GalleryDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;

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
        var result = super.save(gallery);
        // necessary to throw a DataIntegrityViolation and catch it in GalleryManager
        getEntityManager().flush();
        
        return result;
    }
    
    public List<Gallery> findGalleries(long organizationId){
    	return find("from Gallery g where exists (from g.galleryMappings gm where gm.organization.id = :p1)", new Parameter(organizationId));
    }
}
