package com.mds.sys.dao.hibernate;

import com.mds.common.model.Parameter;
import com.mds.sys.model.MenuFunctionPermission;
import com.mds.sys.dao.MenuFunctionPermissionDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("menuFunctionPermissionDao")
public class MenuFunctionPermissionDaoHibernate extends GenericDaoHibernate<MenuFunctionPermission, Long> implements MenuFunctionPermissionDao {

    public MenuFunctionPermissionDaoHibernate() {
        super(MenuFunctionPermission.class);
    }
    
    public List<MenuFunctionPermission> findByMenuFunctionIds(List menuFunctionIds){
    	return find("from MenuFunctionPermission mp where mp.menuFunction.id in (:p1)", new Parameter(menuFunctionIds));
    }
    
    public List<MenuFunctionPermission> findByRoleIds(List roleIds){
    	return find("from MenuFunctionPermission mp where exists (from mp.roles where id in (:p1))", new Parameter(roleIds));
    }

	/**
     * {@inheritDoc}
     */
    public MenuFunctionPermission saveMenuFunctionPermission(MenuFunctionPermission menuFunctionPermission) {
        if (log.isDebugEnabled()) {
            log.debug("menuFunctionPermission's id: " + menuFunctionPermission.getId());
        }
        getSession().saveOrUpdate(menuFunctionPermission);
        // necessary to throw a DataIntegrityViolation and catch it in MenuFunctionPermissionManager
        getSession().flush();
        return menuFunctionPermission;
    }
}
