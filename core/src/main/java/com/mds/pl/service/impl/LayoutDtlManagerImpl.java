package com.mds.pl.service.impl;

import com.mds.pl.dao.LayoutDtlDao;
import com.mds.pl.model.LayoutDtl;
import com.mds.pl.service.LayoutDtlManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("layoutDtlManager")
@WebService(serviceName = "LayoutDtlService", endpointInterface = "com.mds.pl.service.LayoutDtlManager")
public class LayoutDtlManagerImpl extends GenericManagerImpl<LayoutDtl, Long> implements LayoutDtlManager {
    LayoutDtlDao layoutDtlDao;

    @Autowired
    public LayoutDtlManagerImpl(LayoutDtlDao layoutDtlDao) {
        super(layoutDtlDao);
        this.layoutDtlDao = layoutDtlDao;
    }
}