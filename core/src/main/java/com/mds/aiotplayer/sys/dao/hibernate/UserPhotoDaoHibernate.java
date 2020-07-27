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
