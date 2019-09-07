package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.exception.OrganizationExistsException;
import com.mds.sys.model.Organization;

import java.util.List;
import javax.jws.WebService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

//@WebService
public interface OrganizationManager extends GenericManager<Organization, Long> {
	
	/**
     * Retrieves a list of all organizations.
     *
     * @return List
     */
	//@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<Organization> getOrganizations();
    
	/**
     * Saves a organization's information
     *
     * @param organization the organization's information
     * @return updated organization
     * @throws OrganizationExistsException thrown when organization already exists
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    Organization saveOrganization(Organization organization) throws OrganizationExistsException;

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeOrganization(Long id) ;

	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeOrganization(final String organizationIds);
}