package com.mds.aiotplayer.cm.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.cm.model.Banner;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.JTableRequest;
import com.mds.aiotplayer.common.model.JTableResult;

import java.util.HashMap;
import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@WebService
public interface BannerManager extends GenericManager<Banner, Long> {
	    
	/**
     * Saves a banner's information
     *
     * @param banner the banner's information
     * @return updated banner
     * @throws RecordExistsException thrown when banner already exists
     */
    Banner saveBanner(Banner banner) throws RecordExistsException;

	void removeBanner(Long id) ;

	Response removeBanner(final String bannerIds);
}