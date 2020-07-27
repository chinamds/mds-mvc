package com.mds.aiotplayer.wf.service;

import com.mds.aiotplayer.wf.model.Workflow;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.exception.SearchException;

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
@Path("/workflows")
public interface WorkflowService {
    /**
     * Retrieves a workflow by workflowId.  An exception is thrown if workflow not found
     *
     * @param workflowId the identifier for the workflow
     * @return Workflow
     */
    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    Workflow getWorkflow(@PathParam("id") String workflowId);

    /**
     * Retrieves a list of all workflows.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<Workflow> getWorkflows();
    
    /**
     * Retrieves a page of all workflows.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Workflow> searchWorkflows(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all workflows.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> workflowsSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all workflows(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> workflowsTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all workflows(boostrap table).
     *
     * @return List
     * @throws InvalidMDSRoleException 
     * @throws SearchException 
     */
    @GET
    @Path("/organization/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> workflowsTable(@QueryParam("oid") String organizationId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws SearchException, InvalidMDSRoleException;


    /**
     * Saves a workflow's information
     *
     * @param workflow the workflow's information
     * @return updated workflow
     * @throws WorkflowExistsException thrown when workflow already exists
     */
    @POST
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    Workflow saveWorkflow(Workflow workflow) throws RecordExistsException;
    
    /**
     * Saves a workflow's information
     *
     * @param workflow the workflow's information
     * @return updated workflow
     * @throws WorkflowExistsException thrown when workflow already exists
     */
    @POST
    @Path("/{mobile}/{idNumber}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    Workflow userAppointment(@PathParam("mobile") String mobile, @PathParam("idNumber") String idNumber, Workflow workflow) throws RecordExistsException;
    
    /**
     * Saves a workflow's information
     *
     * @param workflow the workflow's information
     * @return updated workflow
     * @throws WorkflowExistsException thrown when workflow already exists
     */
/*    @PUT
    @Path("/{id}/status/{status}")
    @Produces({ MediaType.APPLICATION_JSON })
    Response changeWorkflowStatus(@PathParam("id") String workflowId, @PathParam("status") String workflowStatus);   */

    /**
     * Removes a workflow(1) or more workflows(1, 2, 3) from the database by their workflowIds
     *
     * @param workflowIds the workflow(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeWorkflow(@PathParam("ids") String workflowIds);
    
    /**
     * Retrieves a page of all workflows(tree selector).
     *
     * @return List
     * @throws Exception 
     */
    @GET
    @Path("/workflowdetail/{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> getWorkflowDetail(@PathParam("id") String workflowId, @Context HttpServletRequest request) throws Exception;
        
    @GET
    @Path("/qrcode/{refno}")
    @Produces({"image/png", "image/jpg", "image/gif", "image/bmp", MediaType.APPLICATION_OCTET_STREAM})
    Response getQRCode(@PathParam("refno") String refNo);
}
