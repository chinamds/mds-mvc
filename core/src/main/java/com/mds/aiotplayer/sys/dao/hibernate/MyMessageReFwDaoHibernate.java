package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.MyMessageReFw;
import com.mds.aiotplayer.sys.dao.MyMessageReFwDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("MyMessageReFwDao")
public class MyMessageReFwDaoHibernate extends GenericDaoHibernate<MyMessageReFw, Long> implements MyMessageReFwDao {

    public MyMessageReFwDaoHibernate() {
        super(MyMessageReFw.class);
    }
}
