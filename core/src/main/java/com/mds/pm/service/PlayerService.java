package com.mds.pm.service;

import com.mds.pm.model.Player;
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
@Path("/players")
public interface PlayerService {
    /**
     * Retrieves a player by playerId.  An exception is thrown if player not found
     *
     * @param playerId the identifier for the player
     * @return Player
     */
    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    Player getPlayer(@PathParam("id") String playerId);

    /**
     * Retrieves a list of all players.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<Player> getPlayers();
    
    /**
     * Retrieves a page of all players.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Player> searchPlayers(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all players.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> playersSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all players(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> playersTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all players(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/{userId}/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> playersTable(@PathParam("userId") String userId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);


    /**
     * Saves a player's information
     *
     * @param player the player's information
     * @return updated player
     * @throws PlayerExistsException thrown when player already exists
     */
    @POST
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    Player savePlayer(Player player) throws RecordExistsException;
    
    /**
     * Saves a player's information
     *
     * @param player the player's information
     * @return updated player
     * @throws PlayerExistsException thrown when player already exists
     */
    @POST
    @Path("/{mobile}/{idNumber}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes ( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML} )
    Player userAppointment(@PathParam("mobile") String mobile, @PathParam("idNumber") String idNumber, Player player) throws RecordExistsException;
    
    /**
     * Saves a player's information
     *
     * @param player the player's information
     * @return updated player
     * @throws PlayerExistsException thrown when player already exists
     */
    /*@PUT
    @Path("/{id}/status/{status}")
    @Produces({ MediaType.APPLICATION_JSON })
    Response changePlayerStatus(@PathParam("id") String playerId, @PathParam("status") String playerStatus);   */

    /**
     * Removes a player(1) or more players(1, 2, 3) from the database by their playerIds
     *
     * @param playerIds the player(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removePlayer(@PathParam("ids") String playerIds);
        
    @GET
    @Path("/qrcode/{refno}")
    @Produces({"image/png", "image/jpg", "image/gif", "image/bmp", MediaType.APPLICATION_OCTET_STREAM})
    Response getQRCode(@PathParam("refno") String refNo);
}
