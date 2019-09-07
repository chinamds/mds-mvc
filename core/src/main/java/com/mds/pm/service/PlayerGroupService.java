package com.mds.pm.service;

import com.mds.pm.model.PlayerGroup;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.common.exception.RecordExistsException;

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
@Path("/playerGroups")
public interface PlayerGroupService {
    /**
     * Retrieves a playerGroup by playerGroupId.  An exception is thrown if playerGroup not found
     *
     * @param playerGroupId the identifier for the playerGroup
     * @return PlayerGroup
     */
    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    PlayerGroup getPlayerGroup(@PathParam("id") String playerGroupId);

    /**
     * Retrieves a list of all playerGroups.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<PlayerGroup> getPlayerGroups();
    
    /**
     * Retrieves a page of all playerGroups.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<PlayerGroup> searchPlayerGroups(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all playerGroups.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> playerGroupsSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all playerGroups(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> playerGroupsTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all playerGroups(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/{userId}/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> playerGroupsTable(@PathParam("userId") String userId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all playerGroups(boostrap tree table).
     *
     * @return HashMap
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/treeTable")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> playerGroupsTreeTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) throws InvalidMDSRoleException;


    /**
     * Saves a playerGroup's information
     *
     * @param playerGroup the playerGroup's information
     * @return updated playerGroup
     * @throws PlayerGroupExistsException thrown when playerGroup already exists
     */
    @POST
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    PlayerGroup savePlayerGroup(PlayerGroup playerGroup) throws RecordExistsException;
    
    /**
     * Saves a playerGroup's information
     *
     * @param playerGroup the playerGroup's information
     * @return updated playerGroup
     * @throws PlayerGroupExistsException thrown when playerGroup already exists
     */
    @POST
    @Path("/{mobile}/{idNumber}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    PlayerGroup userAppointment(@PathParam("mobile") String mobile, @PathParam("idNumber") String idNumber, PlayerGroup playerGroup) throws RecordExistsException;
    
    /**
     * Saves a playerGroup's information
     *
     * @param playerGroup the playerGroup's information
     * @return updated playerGroup
     * @throws PlayerGroupExistsException thrown when playerGroup already exists
     */
    /*@PUT
    @Path("/{id}/status/{status}")
    @Produces({ MediaType.APPLICATION_JSON })
    Response changePlayerGroupStatus(@PathParam("id") String playerGroupId, @PathParam("status") String playerGroupStatus);   */

    /**
     * Removes a playerGroup(1) or more playerGroups(1, 2, 3) from the database by their playerGroupIds
     *
     * @param playerGroupIds the playerGroup(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removePlayerGroup(@PathParam("ids") String playerGroupIds);
        
    @GET
    @Path("/qrcode/{refno}")
    @Produces({"image/png", "image/jpg", "image/gif", "image/bmp", MediaType.APPLICATION_OCTET_STREAM})
    Response getQRCode(@PathParam("refno") String refNo);
    
    /**
     * Retrieves a page of all playerGroup(tree selector).
     *
     * @return List
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/treeSelector")
    @Produces({ MediaType.APPLICATION_JSON })
    List<HashMap<String, Object>> playerGroupsTreeSelector(@QueryParam("exclude") @DefaultValue("0") Long excludeId) throws InvalidMDSRoleException;
}
