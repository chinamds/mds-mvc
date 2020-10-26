/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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