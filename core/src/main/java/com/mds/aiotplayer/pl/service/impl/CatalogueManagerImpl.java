/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.pl.service.impl;

import com.mds.aiotplayer.pl.dao.CatalogueDao;
import com.mds.aiotplayer.pl.model.Catalogue;
import com.mds.aiotplayer.pl.service.CatalogueManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("catalogueManager")
@WebService(serviceName = "CatalogueService", endpointInterface = "com.mds.aiotplayer.pl.service.CatalogueManager")
public class CatalogueManagerImpl extends GenericManagerImpl<Catalogue, Long> implements CatalogueManager {
    CatalogueDao catalogueDao;

    @Autowired
    public CatalogueManagerImpl(CatalogueDao catalogueDao) {
        super(catalogueDao);
        this.catalogueDao = catalogueDao;
    }
}