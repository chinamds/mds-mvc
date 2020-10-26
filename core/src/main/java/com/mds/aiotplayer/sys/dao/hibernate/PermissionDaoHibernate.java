/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.Permission;
import com.mds.aiotplayer.sys.dao.PermissionDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("permissionDao")
public class PermissionDaoHibernate extends GenericDaoHibernate<Permission, Long> implements PermissionDao {

    public PermissionDaoHibernate() {
        super(Permission.class);
    }

	/**
     * {@inheritDoc}
     */
    public Permission savePermission(Permission permission) {
        if (log.isDebugEnabled()) {
            log.debug("permission's id: " + permission.getId());
        }
        var result = super.save(permission);
        // necessary to throw a DataIntegrityViolation and catch it in PermissionManager
        getEntityManager().flush();
        return result;
    }
}
