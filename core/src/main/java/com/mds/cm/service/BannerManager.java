package com.mds.cm.service;

import com.mds.common.service.GenericManager;
import com.mds.cm.model.Banner;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.JTableRequest;
import com.mds.common.model.JTableResult;

import java.util.HashMap;
import java.util.List;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@WebService
public interface BannerManager extends GenericManager<Banner, Long> {
	
	/**
     * Retrieves a list of all banners.
     *
     * @return List
     */
    //@GET
	HashMap<Long, String> retrieveAll();

    /**
     * Retrieves a list of banners by page.
     *
     * @return List
     */
    //@GET
	JTableResult retrievePage(JTableRequest jTableRequest);

    /**
     * Retrieves a banner name by bannerId.  An exception is thrown if banner not found
     *
     * @param bannerId the identifier for the banner name
     * @return banner name
     */
    //@GET
    //@Path("{id}")
    String getNameById(int id);
    
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