package com.mds.aiotplayer.common.dao.hibernate;

import com.mds.aiotplayer.common.model.ZipCodeType;
import com.mds.aiotplayer.common.dao.ZipCodeTypeDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("zipCodeTypeDao")
public class ZipCodeTypeDaoHibernate extends GenericDaoHibernate<ZipCodeType, Long> implements ZipCodeTypeDao {

    public ZipCodeTypeDaoHibernate() {
        super(ZipCodeType.class);
    }
}
