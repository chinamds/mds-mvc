package com.mds.sys.dao.hibernate;

import com.mds.sys.model.UserPhoto;
import com.mds.sys.dao.UserPhotoDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("userPhotoDao")
public class UserPhotoDaoHibernate extends GenericDaoHibernate<UserPhoto, Long> implements UserPhotoDao {

    public UserPhotoDaoHibernate() {
        super(UserPhoto.class);
    }
}
