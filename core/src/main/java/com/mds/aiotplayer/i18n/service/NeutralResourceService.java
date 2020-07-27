package com.mds.aiotplayer.i18n.service;

import javax.ws.rs.core.Response;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.i18n.model.NeutralResource;

import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/neutralResources")
public interface NeutralResourceService {
    /**
     * Retrieves a neutralResource by neutralResourceId.  An exception is thrown if neutralResource not found
     *
     * @param neutralResourceId the identifier for the neutralResource
     * @return NeutralResource
     */
    @GET
    @Path("{id}")
    NeutralResource getNeutralResource(@PathParam("id") String neutralResourceId);

    /**
     * Retrieves a list of all neutralResource.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<NeutralResource> getNeutralResources();
    
    /**
     * Retrieves a list of all neutralResources.
     *
     * @return List
     */
    @GET
    @Path("/show")
    @Produces({ MediaType.APPLICATION_JSON })
    List<NeutralResource> getShowNeutralResources(@QueryParam("limit") @DefaultValue("-1") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all neutralResources.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<NeutralResource> searchNeutralResources(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all neutralResources.
     *
     * @return List
     */
    @GET
    @Path("/{category}/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> neutralResourcesSelect2(@PathParam("category") String category, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all neutralResources.
     *
     * @return List
     */
    @GET
    @Path("/{culture}/notlocalized/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> notLocalizedNeutralResourcesSelect2(@PathParam("culture") String culture, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all neutralResources(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/{category}/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> neutralResourcesTable(@PathParam("category") String category, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all neutralResources(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> neutralResourcesTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Saves a neutralResource's information
     *
     * @param neutralResource the neutralResource's information
     * @return updated neutralResource
     * @throws NeutralResourceExistsException thrown when neutralResource already exists
     */
    @POST
    NeutralResource saveNeutralResource(NeutralResource neutralResource) throws RecordExistsException;

    /**
     * Removes a neutralResource or more neutralResources from the database by their neutralResourceIds
     *
     * @param neutralResourceIds the neutralResource's id
     */
    @DELETE
    @Path("{ids}")
    Response removeNeutralResource(@PathParam("ids") String neutralResourceIds);
}
