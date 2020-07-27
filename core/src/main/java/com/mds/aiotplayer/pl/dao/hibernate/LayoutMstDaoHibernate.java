package com.mds.aiotplayer.pl.dao.hibernate;

import com.mds.aiotplayer.pl.model.LayoutMst;
import com.mds.aiotplayer.pl.dao.LayoutMstDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("layoutMstDao")
public class LayoutMstDaoHibernate extends GenericDaoHibernate<LayoutMst, Long> implements LayoutMstDao {

    public LayoutMstDaoHibernate() {
        super(LayoutMst.class);
    }
}
