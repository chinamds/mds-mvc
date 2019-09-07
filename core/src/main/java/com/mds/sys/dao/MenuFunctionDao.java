package com.mds.sys.dao;

import java.util.List;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.MenuFunction;

/**
 * An interface that provides a data management interface to the MenuFunction table.
 */
public interface MenuFunctionDao extends GenericDao<MenuFunction, Long> {
	List<MenuFunction> findAllActivitiList();
	
	List<MenuFunction> findByParentIdsLike(String parentIds);

	List<MenuFunction> findAllList();
	
	List<MenuFunction> findByUserId(Long userId);
	
	List<MenuFunction> findAllActivitiList(Long userId);

		/**
     * Gets menuFunctions information based on code.
     * @param menuFunctioncode the menuFunction's code
     * @return MenuFunction populated MenuFunction object
     * @throws MenuFunctioncodeNotFoundException thrown when menuFunction code not
     * found in database
     */
	MenuFunction loadMenuFunctionByMenuFunctioncode(String menuFunctioncode);
	/**
     * Saves a menuFunction's information.
     * @param menuFunction the object to be saved
     * @return the persisted MenuFunction object
     */
    MenuFunction saveMenuFunction(MenuFunction menuFunction);
}