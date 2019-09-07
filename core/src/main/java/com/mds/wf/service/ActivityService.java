package com.mds.wf.service;

import com.mds.wf.model.Activity;
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
import java.util.Map;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/activities")
public interface ActivityService {
    /**
     * Retrieves a activity by activityId.  An exception is thrown if activity not found
     *
     * @param activityId the identifier for the activity
     * @return Activity
     */
    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    Activity getActivity(@PathParam("id") String activityId);

    /**
     * Retrieves a list of all activities.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<Activity> getActivities();
    
    /**
     * Retrieves a page of all activities.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Activity> searchActivities(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all activities.
     *
     * @return List
     * @throws InvalidMDSRoleException 
     * @throws SearchException 
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> activitiesSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) throws SearchException, InvalidMDSRoleException;
    
    /**
     * Retrieves a page of all activities(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> activitiesTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all activities(boostrap table).
     *
     * @return List
     * @throws InvalidMDSRoleException 
     * @throws SearchException 
     */
    @GET
    @Path("/organization/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> activitiesTable(@QueryParam("oid") String organizationId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) throws SearchException, InvalidMDSRoleException;


    /**
     * Saves a activity's information
     *
     * @param activity the activity's information
     * @return updated activity
     * @throws ActivityExistsException thrown when activity already exists
     */
    @POST
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    Activity saveActivity(Activity activity) throws RecordExistsException;
    
    /**
     * Saves a activity's information
     *
     * @param activity the activity's information
     * @return updated activity
     * @throws ActivityExistsException thrown when activity already exists
     */
    @POST
    @Path("/{mobile}/{idNumber}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    Activity userAppointment(@PathParam("mobile") String mobile, @PathParam("idNumber") String idNumber, Activity activity) throws RecordExistsException;
    
    /**
     * Saves a activity's information
     *
     * @param activity the activity's information
     * @return updated activity
     * @throws ActivityExistsException thrown when activity already exists
     */
    /*@PUT
    @Path("/{id}/status/{status}")
    @Produces({ MediaType.APPLICATION_JSON })
    Response changeActivityStatus(@PathParam("id") String activityId, @PathParam("status") String activityStatus);   */
    
    /**
     * Retrieves all organizations(boostrap treeview) and assign to activity.
     *
     * @return List
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/{id}/organizations/treeView")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Map<String,Object>> organizationsTreeView(@PathParam("id") String activityId, @QueryParam("oid") String organizationId, @Context HttpServletRequest request) throws InvalidMDSRoleException;
    
    /**
     * Retrieves all users(boostrap treeview) and assign to activity.
     *
     * @return List
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/{id}/users/dualListbox")
    @Produces({ MediaType.APPLICATION_JSON })
    List<HashMap<String,Object>> usersDualListbox(@PathParam("id") String activityId, @QueryParam("oid") String organizationId, @QueryParam("oids") String organizationIds, @QueryParam("uids") String userIds, @Context HttpServletRequest request) throws InvalidMDSRoleException;

    /**
     * Removes a activity(1) or more activities(1, 2, 3) from the database by their activityIds
     *
     * @param activityIds the activity(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeActivity(@PathParam("ids") String activityIds);
        
    @GET
    @Path("/qrcode/{refno}")
    @Produces({"image/png", "image/jpg", "image/gif", "image/bmp", MediaType.APPLICATION_OCTET_STREAM})
    Response getQRCode(@PathParam("refno") String refNo);
}
