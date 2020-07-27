package com.mds.aiotplayer.sys.dao;

import java.util.List;

import com.mds.aiotplayer.common.dao.GenericDao;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;

/**
 * An interface that provides a data management interface to the MenuFunctionPermission table.
 */
public interface MenuFunctionPermissionDao extends GenericDao<MenuFunctionPermission, Long> {
	List<MenuFunctionPermission> findByMenuFunctionIds(List menuFunctionIds);
	List<MenuFunctionPermission> findByRoleIds(List roleIds);
	/**
     * Saves a menuFunctionPermission's information.
     * @param menuFunctionPermission the object to be saved
     * @return the persisted MenuFunctionPermission object
     */
    MenuFunctionPermission saveMenuFunctionPermission(MenuFunctionPermission menuFunctionPermission);
}