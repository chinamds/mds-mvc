package com.mds.common.dao.hibernate;

import com.mds.common.model.Currency;
import com.mds.common.dao.CurrencyDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("currencyDao")
public class CurrencyDaoHibernate extends GenericDaoHibernate<Currency, Long> implements CurrencyDao {

    public CurrencyDaoHibernate() {
        super(Currency.class);
    }
}
