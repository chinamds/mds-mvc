package com.mds.cm.service.impl;

import com.mds.cm.dao.BannerDao;
import com.mds.cm.model.Banner;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.JTableRequest;
import com.mds.common.model.JTableResult;
import com.mds.cm.service.BannerManager;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.util.ConvertUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import javax.jws.WebService;
import javax.persistence.Table;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("bannerManager")
@WebService(serviceName = "BannerService", endpointInterface = "com.mds.cm.service.BannerManager")
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
    @Override
	public HashMap<Long, String> retrieveAll() {
    	return bannerDao.retrieveAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public JTableResult retrievePage(JTableRequest jTableRequest) {
    	return bannerDao.retrievePage(jTableRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNameById(int id) {
    	return bannerDao.getNameById(id);
    }

		/**
     * {@inheritDoc}
     */
    @Override
	public void removeBanner(Long id) {
		bannerDao.remove(id);
	}

	/**
     * {@inheritDoc}
     */
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