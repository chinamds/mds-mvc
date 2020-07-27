package com.mds.aiotplayer.pl.service.impl;

import com.mds.aiotplayer.pl.dao.LayoutMstDao;
import com.mds.aiotplayer.pl.model.LayoutMst;
import com.mds.aiotplayer.pl.service.LayoutMstManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("layoutMstManager")
@WebService(serviceName = "LayoutMstService", endpointInterface = "com.mds.aiotplayer.pl.service.LayoutMstManager")
public class LayoutMstManagerImpl extends GenericManagerImpl<LayoutMst, Long> implements LayoutMstManager {
    LayoutMstDao layoutMstDao;

    @Autowired
    public LayoutMstManagerImpl(LayoutMstDao layoutMstDao) {
        super(layoutMstDao);
        this.layoutMstDao = layoutMstDao;
    }
}