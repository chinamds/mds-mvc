package com.mds.wf.dao.hibernate;

import com.mds.wf.model.ActivityOrganizationUser;
import com.mds.wf.dao.ActivityOrganizationUserDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("activityOrganizationUserDao")
public class ActivityOrganizationUserDaoHibernate extends GenericDaoHibernate<ActivityOrganizationUser, Long> implements ActivityOrganizationUserDao {

    public ActivityOrganizationUserDaoHibernate() {
        super(ActivityOrganizationUser.class);
    }
}
