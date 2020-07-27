package com.mds.aiotplayer.pl.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.pl.model.Catalogue;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface CatalogueManager extends GenericManager<Catalogue, Long> {
    
}