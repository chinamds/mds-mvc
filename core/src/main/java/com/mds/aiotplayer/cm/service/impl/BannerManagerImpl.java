/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.cm.dao.BannerDao;
import com.mds.aiotplayer.cm.model.Banner;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.JTableRequest;
import com.mds.aiotplayer.common.model.JTableResult;
import com.mds.aiotplayer.cm.service.BannerManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import javax.jws.WebService;
import javax.persistence.Table;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("bannerManager")
@WebService(serviceName = "BannerService", endpointInterface = "com.mds.aiotplayer.cm.service.BannerManager")
public class BannerManagerImpl extends GenericManagerImpl<Banner, Long> implements BannerManager {
    BannerDao bannerDao;

    @Autowired
    public BannerManagerImpl(BannerDao bannerDao) {
        super(bannerDao);
        this.bannerDao = bannerDao;
    }
    
	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeBanner(Long id) {
		bannerDao.remove(id);
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Banner saveBanner(final Banner banner) throws RecordExistsException {
    	
        try {
        	Banner result =  bannerDao.save(banner);
           
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Banner '" + banner.getContentName() + "' already exists!");
        }
    }

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeBanner(final String bannerIds) {
        log.debug("removing banner: " + bannerIds);
        try {
        	bannerDao.remove(ConvertUtil.StringtoLongArray(bannerIds));
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Banner(id=" + bannerIds + ") was successfully deleted.");
        return Response.ok().build();
    }
}