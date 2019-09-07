package com.mds.pl.dao.hibernate;

import com.mds.pl.model.LayoutMst;
import com.mds.pl.dao.LayoutMstDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("layoutMstDao")
public class LayoutMstDaoHibernate extends GenericDaoHibernate<LayoutMst, Long> implements LayoutMstDao {

    public LayoutMstDaoHibernate() {
        super(LayoutMst.class);
    }
}
