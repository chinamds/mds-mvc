package com.mds.sys.service.impl;

import com.mds.sys.dao.TenantDao;
import com.mds.sys.model.Tenant;
import com.mds.sys.service.TenantManager;
import com.mds.util.ConvertUtil;
import com.mds.util.DateUtils;
import com.mds.util.StringUtils;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.core.CacheItem;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("tenantManager")
@WebService(serviceName = "TenantService", endpointInterface = "com.mds.sys.service.TenantManager")
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