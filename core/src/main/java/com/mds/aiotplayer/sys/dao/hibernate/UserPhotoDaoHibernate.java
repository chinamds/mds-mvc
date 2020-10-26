/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.UserPhoto;
import com.mds.aiotplayer.sys.dao.UserPhotoDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("userPhotoDao")
public class UserPhotoDaoHibernate extends GenericDaoHibernate<UserPhoto, Long> implements UserPhotoDao {

    public UserPhotoDaoHibernate() {
        super(UserPhoto.class);
    }
}
