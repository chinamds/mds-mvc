/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.dao;

import java.util.List;
import java.util.Map;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.i18n.model.LocalizedResource;

/**
 * An interface that provides a data management interface to the LocalizedResource table.
 */
public interface LocalizedResourceDao extends GenericDao<LocalizedResource, Long> {
	List<LocalizedResource> findByCultureId(Long cultureId);
	List<LocalizedResource> findByCultureCode(String cultureCode);
	List<Map<Long, Long>> findNeutralMap(Long cultureId);
	List<Long> findNeutralIds(Long cultureId);

	/**
     * Saves a LocalizedResource's information.
     * @param localizedResource the object to be saved
     * @return the persisted LocalizedResource object
     */
    LocalizedResource saveLocalizedResource(LocalizedResource localizedResource);
}