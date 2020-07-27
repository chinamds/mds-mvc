package com.mds.aiotplayer.i18n.service;

import javax.ws.rs.core.Response;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.i18n.model.LocalizedResource;

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

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/localizedResources")
public interface LocalizedResourceService {
    /**
     * Retrieves a localizedResource by localizedResourceId.  An exception is thrown if localizedResource not found
     *
     * @param localizedResourceId the identifier for the localizedResource
     * @return LocalizedResource
     */
    @GET
    @Path("{id}")
    LocalizedResource getLocalizedResource(@PathParam("id") String localizedResourceId);

    /**
     * Retrieves a list of all localizedResource.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<LocalizedResource> getLocalizedResources();
    
    /**
     * Retrieves a list of all localizedResources.
     *
     * @return List
     */
    @GET
    @Path("/show")
    @Produces({ MediaType.APPLICATION_JSON })
    List<LocalizedResource> getShowLocalizedResources(@QueryParam("limit") @DefaultValue("-1") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all localizedResources.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<LocalizedResource> searchLocalizedResources(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all localizedResources.
     *
     * @return List
     */
    @GET
    @Path("/{category}/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> localizedResourcesSelect2(@PathParam("category") String category, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all localizedResources(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/{culture}/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> localizedResourcesTable(@PathParam("culture") String culture, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all localizedResources(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> localizedResourcesTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Saves a localizedResource's information
     *
     * @param localizedResource the localizedResource's information
     * @return updated localizedResource
     * @throws LocalizedResourceExistsException thrown when localizedResource already exists
     */
    @POST
    LocalizedResource saveLocalizedResource(LocalizedResource localizedResource) throws RecordExistsException;

    /**
     * Removes a localizedResource or more localizedResources from the database by their localizedResourceIds
     *
     * @param localizedResourceIds the localizedResource's id
     */
    @DELETE
    @Path("{ids}")
    Response removeLocalizedResource(@PathParam("ids") String localizedResourceIds);
    
    @GET
    @Path("/{category}/resource")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> getLocalizedResource(@PathParam("category") String category, @Context HttpServletRequest request);
}
