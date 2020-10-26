/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.Tenant;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@WebService
public interface TenantManager extends GenericManager<Tenant, String> {
	/**
     * Retrieves a list of all tenants.
     *
     * @return List
     */
	@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<Tenant> getTenants();
    
   	/**
     * Saves a tenant's information
     *
     * @param tenant the tenant's information
     * @return updated tenant
     * @throws RecordExistsException thrown when tenant already exists
     */
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    Tenant saveTenant(Tenant tenant) throws RecordExistsException;
    
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeTenant(String tenantIds);
	
	String getCacheKey();
}