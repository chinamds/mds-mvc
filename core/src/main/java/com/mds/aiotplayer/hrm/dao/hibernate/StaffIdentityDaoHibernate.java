/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.hrm.dao.hibernate;

import com.mds.aiotplayer.hrm.model.StaffIdentity;
import com.mds.aiotplayer.hrm.dao.StaffIdentityDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("staffIdentityDao")
public class StaffIdentityDaoHibernate extends GenericDaoHibernate<StaffIdentity, Long> implements StaffIdentityDao {

    public StaffIdentityDaoHibernate() {
        super(StaffIdentity.class);
    }
}
