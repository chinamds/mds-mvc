package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.Organization;

/**
 * An interface that provides a data management interface to the Organization table.
 */
public interface OrganizationDao extends GenericDao<Organization, Long> {
	/**
     * Saves a organization's information.
     * @param organization the object to be saved
     * @return the persisted Organization object
     */
    Organization saveOrganization(Organization organization);
}