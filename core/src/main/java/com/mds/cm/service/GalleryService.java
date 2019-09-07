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
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.model.Gallery;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/galleries")
public interface GalleryService {
    /**
     * Retrieves a gallery by galleryId.  An exception is thrown if gallery not found
     *
     * @param galleryId the identifier for the gallery
     * @return Gallery
     */
    @GET
    @Path("{id}")
    Gallery getGallery(@PathParam("id") String galleryId);

    /**
     * Retrieves a list of all galleries.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<Gallery> getGalleries();
    
    /**
     * Retrieves a page of all galleries.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Gallery> searchGalleries(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all galleries.
     *
     * @return List
     * @throws InvalidGalleryException 
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> galleriesSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws InvalidGalleryException, InvalidMDSRoleException;
    
    @GET
    @Path("/organization/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> galleriesOrganizationSelect2(@QueryParam("oid")String organizationId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws InvalidGalleryException, InvalidMDSRoleException;
    
    /**
     * Retrieves a page of all galleries(boostrap table).
     *
     * @return List
     * @throws InvalidGalleryException 
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> galleriesTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws InvalidGalleryException, InvalidMDSRoleException;
    
    /**
     * Retrieves a page of all galleries for user(boostrap table).
     *
     * @return List
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/{userId}/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> galleriesTable(@PathParam("userId") String userId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws InvalidMDSRoleException;


    /**
     * Saves a gallery's information
     *
     * @param gallery the gallery's information
     * @return updated gallery
     * @throws GalleryExistsException thrown when gallery already exists
     */
    @POST
    Gallery saveGallery(Gallery gallery) throws RecordExistsException;

    /**
     * Removes a gallery(1) or more galleries(1, 2, 3) from the database by their galleryIds
     *
     * @param galleryIds the gallery(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeGallery(@PathParam("ids") String galleryIds);
}
