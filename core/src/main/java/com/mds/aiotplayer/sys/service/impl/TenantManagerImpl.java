/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.TenantDao;
import com.mds.aiotplayer.sys.model.Tenant;
import com.mds.aiotplayer.sys.service.TenantManager;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.CacheItem;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("tenantManager")
@WebService(serviceName = "TenantService", endpointInterface = "com.mds.aiotplayer.sys.service.TenantManager")
public class TenantManagerImpl extends GenericManagerImpl<Tenant, String> implements TenantManager {
    TenantDao tenantDao;

    @Autowired
    public TenantManagerImpl(TenantDao tenantDao) {
        super(tenantDao);
        this.tenantDao = tenantDao;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public List<Tenant> getTenants() {
    	log.debug("get all tenants from db");
        return tenantDao.getAllDistinct();
    }

	 /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Tenant saveTenant(final Tenant tenant) throws RecordExistsException {

        try {
        	Tenant result = tenantDao.saveTenant(tenant);
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Tenant '" + tenant.getId() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeTenant(final String tenantIds) throws WebApplicationException{
        log.debug("removing tenant: " + tenantIds);
        try {
        	tenantDao.remove(tenantIds);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Tenant(id=" + tenantIds + ") was successfully deleted.");
        //return Response.ok().build();
    }

	public String getCacheKey() {
    	return CacheItem.sys_tenants.toString();
    }
}