package com.mds.aiotplayer.common.dao.hibernate;

import com.mds.aiotplayer.common.model.ZipCode;
import com.mds.aiotplayer.common.dao.ZipCodeDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("zipCodeDao")
public class ZipCodeDaoHibernate extends GenericDaoHibernate<ZipCode, Long> implements ZipCodeDao {

    public ZipCodeDaoHibernate() {
        super(ZipCode.class);
    }
}
