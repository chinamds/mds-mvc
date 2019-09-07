package com.mds.pl.service;

import com.mds.common.service.GenericManager;
import com.mds.pl.model.Catalogue;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CatalogueManager extends GenericManager<Catalogue, Long> {
    
}