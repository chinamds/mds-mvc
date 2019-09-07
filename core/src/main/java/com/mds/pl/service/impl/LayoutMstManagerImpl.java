package com.mds.pl.service.impl;

import com.mds.pl.dao.LayoutMstDao;
import com.mds.pl.model.LayoutMst;
import com.mds.pl.service.LayoutMstManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("layoutMstManager")
@WebService(serviceName = "LayoutMstService", endpointInterface = "com.mds.pl.service.LayoutMstManager")
public class LayoutMstManagerImpl extends GenericManagerImpl<LayoutMst, Long> implements LayoutMstManager {
    LayoutMstDao layoutMstDao;

    @Autowired
    public LayoutMstManagerImpl(LayoutMstDao layoutMstDao) {
        super(layoutMstDao);
        this.layoutMstDao = layoutMstDao;
    }
}