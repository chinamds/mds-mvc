package com.mds.pl.dao.hibernate;

import com.mds.pl.model.Catalogue;
import com.mds.pl.dao.CatalogueDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("catalogueDao")
public class CatalogueDaoHibernate extends GenericDaoHibernate<Catalogue, Long> implements CatalogueDao {

    public CatalogueDaoHibernate() {
        super(Catalogue.class);
    }
}
