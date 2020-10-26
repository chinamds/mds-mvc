/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.GenericDao;

import com.mds.aiotplayer.sys.model.MyMessageContent;

/**
 * An interface that provides a data management interface to the MyMessageContent table.
 */
public interface MyMessageContentDao extends GenericDao<MyMessageContent, Long> {
	/**
     * Saves a myMessageContent's information.
     * @param myMessageContent the object to be saved
     * @return the persisted MyMessageContent object
     */
    MyMessageContent saveMyMessageContent(MyMessageContent myMessageContent);
}