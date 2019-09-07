package com.mds.common.dao.hibernate;

import com.mds.common.model.ZipCode;
import com.mds.common.dao.ZipCodeDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("zipCodeDao")
public class ZipCodeDaoHibernate extends GenericDaoHibernate<ZipCode, Long> implements ZipCodeDao {

    public ZipCodeDaoHibernate() {
        super(ZipCode.class);
    }
}
