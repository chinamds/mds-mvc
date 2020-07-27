package com.mds.aiotplayer.pl.dao.hibernate;

import com.mds.aiotplayer.pl.model.LayoutDtl;
import com.mds.aiotplayer.pl.dao.LayoutDtlDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("layoutDtlDao")
public class LayoutDtlDaoHibernate extends GenericDaoHibernate<LayoutDtl, Long> implements LayoutDtlDao {

    public LayoutDtlDaoHibernate() {
        super(LayoutDtl.class);
    }
}
