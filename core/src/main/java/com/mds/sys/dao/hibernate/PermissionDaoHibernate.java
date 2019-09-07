package com.mds.sys.dao.hibernate;

import com.mds.sys.model.Permission;
import com.mds.sys.dao.PermissionDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
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
        getSession().saveOrUpdate(permission);
        // necessary to throw a DataIntegrityViolation and catch it in PermissionManager
        getSession().flush();
        return permission;
    }
}
