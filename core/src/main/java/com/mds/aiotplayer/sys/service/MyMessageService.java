package com.mds.aiotplayer.sys.service;

import javax.ws.rs.core.Response;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.MyMessage;

import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/myMessages")
public interface MyMessageService {
    /**
     * Retrieves a myMessage by myMessageId.  An exception is thrown if myMessage not found
     *
     * @param myMessageId the identifier for the myMessage
     * @return MyMessage
     */
    @GET
    @Path("{id}")
    MyMessage getMyMessage(@PathParam("id") String myMessageId);

    /**
     * Retrieves a list of all myMessage.
     *
     * @return List
     */
    @GET
    List<MyMessage> getMyMessages();
    
    /**
     * Retrieves a list of all myMessages.
     *
     * @return List
     */
    @GET
    @Path("/show")
    @Produces({ MediaType.APPLICATION_JSON })
    List<MyMessage> getShowMyMessages(@QueryParam("limit") @DefaultValue("-1") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all myMessages.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<MyMessage> searchMyMessages(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all myMessages.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> myMessagesSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all messages(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/{userId}/{messageFolder}/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> myMessagesTable(@PathParam("userId") String userId, @PathParam("messageFolder") String messageFolder, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Saves a myMessage's information
     *
     * @param myMessage the myMessage's information
     * @return updated myMessage
     * @throws MyMessageExistsException thrown when myMessage already exists
     */
    @POST
    MyMessage saveMyMessage(MyMessage myMessage) throws RecordExistsException;

    /**
     * Removes a myMessage or more myMessages from the database by their myMessageIds
     *
     * @param myMessageIds the myMessage's id
     */
    @DELETE
    @Path("{ids}")
    Response removeMyMessage(@PathParam("ids") String myMessageIds);
    
    /**
     * Mark user's messages as read
     *
     * @param userId user Id
     * @param ids message Ids
     */
    @PUT
    @Path("/markread/{userId}/{ids}")
    Response markRead(@PathParam("userId")String userId, @PathParam("ids")String ids);
        
    /**
     * move user's messages to deleted folder
     *
     * @param userId
     * @param messageIds
     * @return
     */
    @PUT
    @Path("/recycle/{userId}/{ids}")
    Response recycle(@PathParam("userId")String userId, @PathParam("ids")String ids);

    /**
     * move user's messages to archive folder
     *
     * @param userId
     * @param messageIds
     * @return
     */
    @PUT
    @Path("/archive/{userId}/{ids}")
    Response archive(@PathParam("userId")String userId, @PathParam("ids")String ids);

    /**
     * delete user's messages from deleted folder
     *
     * @param userId
     * @param messageIds
     */
    @PUT
    @Path("/delete/{userId}/{ids}")
    Response delete(@PathParam("userId")String userId, @PathParam("ids")String ids);

    /**
     * Empty the specified folder for user
     *
     * @param userId
     * @param state
     */
    @PUT
    @Path("/clear/{userId}/{messageFolder}")
    Response clearFolder(@PathParam("userId")String userId, @PathParam("messageFolder")String messageFolder);

    /**
     * Empty the draft folder for user
     *
     * @param userId
     */
    @PUT
    @Path("/cleardraft/{userId}")
    Response clearDraft(@PathParam("userId")String userId);

    /**
     * Empty the inbox folder for user
     *
     * @param userId
     */
    @PUT
    @Path("/clearinbox/{userId}")
    Response clearInbox(@PathParam("userId")String userId);

    /**
     * Empty the outbox folder for user
     *
     * @param userId
     */
    @PUT
    @Path("/clearoutbox/{userId}")
    Response clearOutbox(@PathParam("userId")String userId);

    /**
     * Empty the archive folder for user
     *
     * @param userId
     */
    @PUT
    @Path("/cleararchive/{userId}")
    Response clearArchive(@PathParam("userId")String userId);

    /**
     * Empty the junk folder for user
     *
     * @param userId
     */
    @PUT
    @Path("/clearjunk/{userId}")
    Response clearJunk(@PathParam("userId")String userId);

    /**
     * Total unread Inbox messages
     *
     * @param userId
     */
    @GET
    @Path("/unread/{userId}/{messageFolder}")
    //@Produces({ MediaType.APPLICATION_JSON })
    Long countUnread(@PathParam("userId")String userId, @PathParam("messageFolder")String messageFolder);
}
