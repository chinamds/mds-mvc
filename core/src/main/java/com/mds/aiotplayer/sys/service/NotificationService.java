/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import javax.ws.rs.core.Response;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.Notification;

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
@Path("/notifications")
public interface NotificationService {
    /**
     * Retrieves a notification by notificationId.  An exception is thrown if notification not found
     *
     * @param notificationId the identifier for the notification
     * @return Notification
     */
    @GET
    @Path("{id}")
    Notification getNotification(@PathParam("id") String notificationId);

    /**
     * Retrieves a list of all notification.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<Notification> getNotifications();
    
    /**
     * Retrieves a list of all notifications.
     *
     * @return List
     */
    @GET
    @Path("/show")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Notification> getShowNotifications(@QueryParam("limit") @DefaultValue("-1") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all notifications.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Notification> searchNotifications(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all notifications.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> notificationsSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all messages(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/{userId}/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> notificationsTable(@PathParam("userId") String userId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Saves a notification's information
     *
     * @param notification the notification's information
     * @return updated notification
     * @throws NotificationExistsException thrown when notification already exists
     */
    @POST
    Notification saveNotification(Notification notification) throws RecordExistsException;

    /**
     * Removes a notification or more notifications from the database by their notificationIds
     *
     * @param notificationIds the notification's id
     */
    @DELETE
    @Path("{ids}")
    Response removeNotification(@PathParam("ids") String notificationIds);

}
