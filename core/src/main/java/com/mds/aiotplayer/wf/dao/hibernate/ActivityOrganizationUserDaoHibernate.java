/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.wf.dao.hibernate;

import com.mds.aiotplayer.wf.model.ActivityOrganizationUser;
import com.mds.aiotplayer.wf.dao.ActivityOrganizationUserDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("activityOrganizationUserDao")
public class ActivityOrganizationUserDaoHibernate extends GenericDaoHibernate<ActivityOrganizationUser, Long> implements ActivityOrganizationUserDao {

    public ActivityOrganizationUserDaoHibernate() {
        super(ActivityOrganizationUser.class);
    }
}
