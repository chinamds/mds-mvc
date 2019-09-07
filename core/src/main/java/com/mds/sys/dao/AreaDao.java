package com.mds.sys.dao;

import java.util.List;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.Area;

/**
 * An interface that provides a data management interface to the Area table.
 */
public interface AreaDao extends GenericDao<Area, Long> {

	List<Area> findByParentIdsLike(String parentIds);
	
	List<Area> findAllList();
	
	List<Area> findAllChild(Long parentId, String likeParentIds);
	
	/**
     * Gets areas information based on name.
     * @param areaname the area's name
     * @return Area populated Area object
     * @throws AreanameNotFoundException thrown when area name not
     * found in database
     */
	Area loadAreaByAreaname(String areaname);
	/**
     * Saves a area's information.
     * @param area the object to be saved
     * @return the persisted Area object
     */
    Area saveArea(Area area);
}