package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.Module;

/**
 * An interface that provides a data management interface to the Module table.
 */
public interface ModuleDao extends GenericDao<Module, Long> {
	/**
     * Saves a module's information.
     * @param module the object to be saved
     * @return the persisted Module object
     */
    Module saveModule(Module module);
}