package com.mds.aiotplayer.common.dao.hibernate;

import com.mds.aiotplayer.common.model.Country;
import com.mds.aiotplayer.common.dao.CountryDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("countryDao")
public class CountryDaoHibernate extends GenericDaoHibernate<Country, Long> implements CountryDao {

    public CountryDaoHibernate() {
        super(Country.class);
    }
}
