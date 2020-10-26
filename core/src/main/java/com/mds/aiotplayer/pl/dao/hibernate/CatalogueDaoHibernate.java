/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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
