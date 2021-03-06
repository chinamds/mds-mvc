/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.dao;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.cm.model.Banner;
import com.mds.aiotplayer.common.model.JTableRequest;
import com.mds.aiotplayer.common.model.JTableResult;

/**
 * An interface that provides a data management interface to the Banner table.
 */
public interface BannerDao extends GenericDao<Banner, Long> {
	/**
     * Saves a banner's information.
     * @param banner the object to be saved
     * @return the persisted Banner object
     */
    Banner saveBanner(Banner banner);

}