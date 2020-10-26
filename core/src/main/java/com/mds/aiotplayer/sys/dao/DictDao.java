/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import java.util.List;
import java.util.Map;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.Dict;

/**
 * An interface that provides a data management interface to the Dict table.
 */
public interface DictDao extends GenericDao<Dict, Long> {

	List<Dict> findAllList();

	List<String> findTypeList();
	
	void saveDicts(String category, Map<String, Object> mapDict);
	/**
     * Saves a dict's information.
     * @param dict the object to be saved
     * @return the persisted Dict object
     */
    Dict saveDict(Dict dict);
}