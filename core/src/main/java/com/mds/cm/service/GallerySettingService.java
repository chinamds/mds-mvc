package com.mds.cm.service;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mds.common.exception.RecordExistsException;
import com.mds.cm.model.GallerySetting;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/gallerySettings")
public interface GallerySettingService {
    /**
     * Retrieves a gallerySetting by gallerySettingId.  An exception is thrown if gallerySetting not found
     *
     * @param gallerySettingId the identifier for the gallerySetting
     * @return GallerySetting
     */
    @GET
    @Path("{id}")
    GallerySetting getGallerySetting(@PathParam("id") String gallerySettingId);

    /**
     * Retrieves a list of all gallerySettings.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<GallerySetting> getGallerySettings();
    
    /**
     * Retrieves a page of all gallerySettings.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<GallerySetting> searchGallerySettings(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all gallerySettings.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> gallerySettingsSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all gallerySettings(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> gallerySettingsTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Saves a gallerySetting's information
     *
     * @param gallerySetting the gallerySetting's information
     * @return updated gallerySetting
     * @throws GallerySettingExistsException thrown when gallerySetting already exists
     */
    @POST
    GallerySetting saveGallerySetting(GallerySetting gallerySetting) throws RecordExistsException;

    /**
     * Removes a gallerySetting(1) or more gallerySettings(1, 2, 3) from the database by their gallerySettingIds
     *
     * @param gallerySettingIds the gallerySetting(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeGallerySetting(@PathParam("ids") String gallerySettingIds);
}
