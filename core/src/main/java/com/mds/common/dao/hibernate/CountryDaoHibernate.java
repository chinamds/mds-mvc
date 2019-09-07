package com.mds.common.dao.hibernate;

import com.mds.common.model.Country;
import com.mds.common.dao.CountryDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("countryDao")
public class CountryDaoHibernate extends GenericDaoHibernate<Country, Long> implements CountryDao {

    public CountryDaoHibernate() {
        super(Country.class);
    }
}
