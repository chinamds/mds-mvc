package com.mds.aiotplayer.common.dao.hibernate;

import com.mds.aiotplayer.common.model.Currency;
import com.mds.aiotplayer.common.dao.CurrencyDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("currencyDao")
public class CurrencyDaoHibernate extends GenericDaoHibernate<Currency, Long> implements CurrencyDao {

    public CurrencyDaoHibernate() {
        super(Currency.class);
    }
}
