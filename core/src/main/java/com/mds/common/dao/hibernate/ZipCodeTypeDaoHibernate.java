package com.mds.common.dao.hibernate;

import com.mds.common.model.ZipCodeType;
import com.mds.common.dao.ZipCodeTypeDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("zipCodeTypeDao")
public class ZipCodeTypeDaoHibernate extends GenericDaoHibernate<ZipCodeType, Long> implements ZipCodeTypeDao {

    public ZipCodeTypeDaoHibernate() {
        super(ZipCodeType.class);
    }
}
