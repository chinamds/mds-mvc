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
