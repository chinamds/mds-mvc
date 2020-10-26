/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.dao.hibernate;

import com.mds.aiotplayer.hrm.model.IdentityType;
import com.mds.aiotplayer.hrm.dao.IdentityTypeDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("identityTypeDao")
public class IdentityTypeDaoHibernate extends GenericDaoHibernate<IdentityType, Long> implements IdentityTypeDao {

    public IdentityTypeDaoHibernate() {
        super(IdentityType.class);
    }
}
