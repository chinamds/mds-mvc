package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;

import com.mds.sys.model.MyMessageContent;

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