package com.mds.i18n.dao;

import java.util.List;
import java.util.Map;

import com.mds.common.dao.GenericDao;

import com.mds.i18n.model.LocalizedResource;

/**
 * An interface that provides a data management interface to the LocalizedResource table.
 */
public interface LocalizedResourceDao extends GenericDao<LocalizedResource, Long> {
	List<LocalizedResource> findByCultureId(Long cultureId);
	List<Map<Long, Long>> findNeutralMap(Long cultureId);

	/**
     * Saves a LocalizedResource's information.
     * @param localizedResource the object to be saved
     * @return the persisted LocalizedResource object
     */
    LocalizedResource saveLocalizedResource(LocalizedResource localizedResource);
}