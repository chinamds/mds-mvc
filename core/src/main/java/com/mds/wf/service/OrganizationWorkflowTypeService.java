package com.mds.wf.service;

import com.mds.wf.model.OrganizationWorkflowType;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.search.exception.SearchException;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/organizationWorkflowTypes")
public interface OrganizationWorkflowTypeService {
    /**
     * Retrieves a organizationWorkflowType by organizationWorkflowTypeId.  An exception is thrown if organizationWorkflowType not found
     *
     * @param organizationWorkflowTypeId the identifier for the organizationWorkflowType
     * @return OrganizationWorkflowType
     */
    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    OrganizationWorkflowType getOrganizationWorkflowType(@PathParam("id") String organizationWorkflowTypeId);

    /**
     * Retrieves a list of all organizationWorkflowTypes.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<OrganizationWorkflowType> getOrganizationWorkflowTypes();
    
    /**
     * Retrieves a page of all organizationWorkflowTypes.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<OrganizationWorkflowType> searchOrganizationWorkflowTypes(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all organizationWorkflowTypes.
     *
     * @return List
     * @throws InvalidMDSRoleException 
     * @throws SearchException 
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> organizationWorkflowTypesSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws SearchException, InvalidMDSRoleException;
    
    /**
     * Retrieves a page of all organizationWorkflowTypes(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> organizationWorkflowTypesTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all organizationWorkflowTypes(boostrap table).
     *
     * @return List
     * @throws InvalidMDSRoleException 
     * @throws SearchException 
     */
    @GET
    @Path("/organization/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> organizationWorkflowTypesTable(@QueryParam("oid") String organizationId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws SearchException, InvalidMDSRoleException;


    /**
     * Saves a organizationWorkflowType's information
     *
     * @param organizationWorkflowType the organizationWorkflowType's information
     * @return updated organizationWorkflowType
     * @throws OrganizationWorkflowTypeExistsException thrown when organizationWorkflowType already exists
     */
    @POST
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    OrganizationWorkflowType saveOrganizationWorkflowType(OrganizationWorkflowType organizationWorkflowType) throws RecordExistsException;
    
    /**
     * Saves a organizationWorkflowType's information
     *
     * @param organizationWorkflowType the organizationWorkflowType's information
     * @return updated organizationWorkflowType
     * @throws OrganizationWorkflowTypeExistsException thrown when organizationWorkflowType already exists
     */
    @POST
    @Path("/{mobile}/{idNumber}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    OrganizationWorkflowType userAppointment(@PathParam("mobile") String mobile, @PathParam("idNumber") String idNumber, OrganizationWorkflowType organizationWorkflowType) throws RecordExistsException;
    
    /**
     * Saves a organizationWorkflowType's information
     *
     * @param organizationWorkflowType the organizationWorkflowType's information
     * @return updated organizationWorkflowType
     * @throws OrganizationWorkflowTypeExistsException thrown when organizationWorkflowType already exists
     */
/*    @PUT
    @Path("/{id}/status/{status}")
    @Produces({ MediaType.APPLICATION_JSON })
    Response changeOrganizationWorkflowTypeStatus(@PathParam("id") String organizationWorkflowTypeId, @PathParam("status") String organizationWorkflowTypeStatus);   */

    /**
     * Removes a organizationWorkflowType(1) or more organizationWorkflowTypes(1, 2, 3) from the database by their organizationWorkflowTypeIds
     *
     * @param organizationWorkflowTypeIds the organizationWorkflowType(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeOrganizationWorkflowType(@PathParam("ids") String organizationWorkflowTypeIds);
        
    @GET
    @Path("/qrcode/{refno}")
    @Produces({"image/png", "image/jpg", "image/gif", "image/bmp", MediaType.APPLICATION_OCTET_STREAM})
    Response getQRCode(@PathParam("refno") String refNo);
}
