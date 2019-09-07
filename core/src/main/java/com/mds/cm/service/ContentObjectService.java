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
import com.mds.cm.model.ContentObject;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/contentObjects")
public interface ContentObjectService {
    /**
     * Retrieves a contentObject by contentObjectId.  An exception is thrown if contentObject not found
     *
     * @param contentObjectId the identifier for the contentObject
     * @return ContentObject
     */
    @GET
    @Path("{id}")
    ContentObject getContentObject(@PathParam("id") String contentObjectId);

    /**
     * Retrieves a list of all contentObjects.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<ContentObject> getContentObjects();
    
    /**
     * Retrieves a page of all contentObjects.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<ContentObject> searchContentObjects(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all contentObjects.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> contentObjectsSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all contentObjects(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> contentObjectsTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Saves a contentObject's information
     *
     * @param contentObject the contentObject's information
     * @return updated contentObject
     * @throws ContentObjectExistsException thrown when contentObject already exists
     */
    @POST
    ContentObject saveContentObject(ContentObject contentObject) throws RecordExistsException;

    /**
     * Removes a contentObject(1) or more contentObjects(1, 2, 3) from the database by their contentObjectIds
     *
     * @param contentObjectIds the contentObject(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeContentObject(@PathParam("ids") String contentObjectIds);
}
