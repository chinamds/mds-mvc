/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.service;

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
import com.mds.aiotplayer.i18n.model.Culture;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/cultures")
public interface CultureService {
    /**
     * Retrieves a culture by cultureId.  An exception is thrown if culture not found
     *
     * @param cultureId the identifier for the culture
     * @return Culture
     */
    @GET
    @Path("{id}")
    Culture getCulture(@PathParam("id") String cultureId);

    /**
     * Retrieves a list of all cultures.
     *
     * @return List
     */
    @GET
    List<Culture> getCultures();
    
    /**
     * Retrieves a page of all cultures.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Culture> searchCultures(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all cultures.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> culturesSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all cultures.
     *
     * @return List
     */
    @GET
    @Path("/available/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> availableCulturesSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all cultures(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> culturesTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);


    /**
     * Saves a culture's information
     *
     * @param culture the culture's information
     * @return updated culture
     * @throws CultureExistsException thrown when culture already exists
     */
    @POST
    Culture saveCulture(Culture culture) throws RecordExistsException;

    /**
     * Removes a culture(1) or more cultures(1, 2, 3) from the database by their cultureIds
     *
     * @param cultureIds the culture(s)'s id
     */
    @DELETE
    @Path("{ids}")
    void removeCulture(@PathParam("ids") String cultureIds);
}
