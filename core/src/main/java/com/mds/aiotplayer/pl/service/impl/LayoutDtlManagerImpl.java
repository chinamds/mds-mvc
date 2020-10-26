/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.service.impl;

import com.mds.aiotplayer.pl.dao.LayoutDtlDao;
import com.mds.aiotplayer.pl.model.LayoutDtl;
import com.mds.aiotplayer.pl.service.LayoutDtlManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("layoutDtlManager")
@WebService(serviceName = "LayoutDtlService", endpointInterface = "com.mds.aiotplayer.pl.service.LayoutDtlManager")
public class LayoutDtlManagerImpl extends GenericManagerImpl<LayoutDtl, Long> implements LayoutDtlManager {
    LayoutDtlDao layoutDtlDao;

    @Autowired
    public LayoutDtlManagerImpl(LayoutDtlDao layoutDtlDao) {
        super(layoutDtlDao);
        this.layoutDtlDao = layoutDtlDao;
    }
}