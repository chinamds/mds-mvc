package com.mds.aiotplayer.pl.dao.hibernate;

import com.mds.aiotplayer.pl.model.Catalogue;
import com.mds.aiotplayer.pl.dao.CatalogueDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("catalogueDao")
public class CatalogueDaoHibernate extends GenericDaoHibernate<Catalogue, Long> implements CatalogueDao {

    public CatalogueDaoHibernate() {
        super(Catalogue.class);
    }
}
