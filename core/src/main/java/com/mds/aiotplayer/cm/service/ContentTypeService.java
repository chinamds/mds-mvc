package com.mds.aiotplayer.cm.service;

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

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.cm.model.ContentType;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/contentTypes")
public interface ContentTypeService {
    /**
     * Retrieves a contentType by contentTypeId.  An exception is thrown if contentType not found
     *
     * @param contentTypeId the identifier for the contentType
     * @return ContentType
     */
    @GET
    @Path("{id}")
    ContentType getContentType(@PathParam("id") String contentTypeId);

    /**
     * Retrieves a list of all contentTypes.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<ContentType> getContentTypes();
    
    /**
     * Retrieves a page of all contentTypes.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<ContentType> searchContentTypes(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all contentTypes.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> contentTypesSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all contentTypes(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> contentTypesTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all contentTypes for user(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/{userId}/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> contentTypesTable(@PathParam("userId") String userId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);


    /**
     * Saves a contentType's information
     *
     * @param contentType the contentType's information
     * @return updated contentType
     * @throws ContentTypeExistsException thrown when contentType already exists
     */
    @POST
    ContentType saveContentType(ContentType contentType) throws RecordExistsException;

    /**
     * Removes a contentType(1) or more contentTypes(1, 2, 3) from the database by their contentTypeIds
     *
     * @param contentTypeIds the contentType(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeContentType(@PathParam("ids") String contentTypeIds);
}
