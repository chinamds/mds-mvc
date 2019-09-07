package com.mds.pl.service.impl;

import com.mds.pl.dao.CatalogueDao;
import com.mds.pl.model.Catalogue;
import com.mds.pl.service.CatalogueManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("catalogueManager")
@WebService(serviceName = "CatalogueService", endpointInterface = "com.mds.pl.service.CatalogueManager")
public class CatalogueManagerImpl extends GenericManagerImpl<Catalogue, Long> implements CatalogueManager {
    CatalogueDao catalogueDao;

    @Autowired
    public CatalogueManagerImpl(CatalogueDao catalogueDao) {
        super(catalogueDao);
        this.catalogueDao = catalogueDao;
    }
}