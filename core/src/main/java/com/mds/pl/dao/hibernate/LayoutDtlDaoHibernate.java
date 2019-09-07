package com.mds.pl.dao.hibernate;

import com.mds.pl.model.LayoutDtl;
import com.mds.pl.dao.LayoutDtlDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("layoutDtlDao")
public class LayoutDtlDaoHibernate extends GenericDaoHibernate<LayoutDtl, Long> implements LayoutDtlDao {

    public LayoutDtlDaoHibernate() {
        super(LayoutDtl.class);
    }
}
