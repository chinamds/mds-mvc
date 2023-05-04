/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;

import java.util.HashMap;

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
import javax.ws.rs.core.Response;

import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.common.exception.RecordExistsException;

@WebService
@Path("/uiTemplates")
public interface UiTemplateService {
    /**
     * Retrieves a uiTemplate by uiTemplateId.  An exception is thrown if uiTemplate not found
     *
     * @param uiTemplateId the identifier for the uiTemplate
     * @return UiTemplate
     */
    @GET
    @Path("{id}")
    UiTemplate getUiTemplate(@PathParam("id") String uiTemplateId);
    
    /**
     * Retrieves a page of all uiTemplates.
     *
     * @return List
     * @throws InvalidMDSSystemRoleException 
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> uiTemplatesSelect2(@QueryParam("o") String oId, @QueryParam("f") String facilityType, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) throws InvalidMDSRoleException;
    
    /**
     * Retrieves a page of all uiTemplates(boostrap table).
     *
     * @return List
     * @throws InvalidMDSSystemRoleException 
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> uiTemplatesTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) throws InvalidMDSRoleException;
    
    /**
     * Saves a uiTemplate's information
     *
     * @param uiTemplate the uiTemplate's information
     * @return updated uiTemplate
     * @throws UiTemplateExistsException thrown when uiTemplate already exists
     */
    @POST
    UiTemplate saveUiTemplate(UiTemplate uiTemplate) throws RecordExistsException;

    /**
     * Removes a uiTemplate(1) or more uiTemplates(1, 2, 3) from the database by their uiTemplateIds
     *
     * @param uiTemplateIds the uiTemplate(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeUiTemplate(@PathParam("ids") String uiTemplateIds);
}