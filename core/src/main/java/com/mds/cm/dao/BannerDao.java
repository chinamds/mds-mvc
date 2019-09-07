package com.mds.cm.dao;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import com.mds.common.dao.GenericDao;

import com.mds.cm.model.Banner;
import com.mds.common.model.JTableRequest;
import com.mds.common.model.JTableResult;

/**
 * An interface that provides a data management interface to the Banner table.
 */
public interface BannerDao extends GenericDao<Banner, Long> {
	HashMap<Long, String> retrieveAll();

	JTableResult retrievePage(JTableRequest jTableRequest);

    String getNameById(int id);

	/**
     * Saves a banner's information.
     * @param banner the object to be saved
     * @return the persisted Banner object
     */
    Banner saveBanner(Banner banner);

}