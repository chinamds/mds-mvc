/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao.hibernate;

import com.mds.aiotplayer.common.model.ZipCode;
import com.mds.aiotplayer.common.dao.ZipCodeDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("zipCodeDao")
public class ZipCodeDaoHibernate extends GenericDaoHibernate<ZipCode, Long> implements ZipCodeDao {

    public ZipCodeDaoHibernate() {
        super(ZipCode.class);
    }
}
