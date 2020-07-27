package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.sys.exception.UserExistsException;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.util.UserAccount;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/users")
public interface UserService {
    /**
     * Retrieves a user by userId.  An exception is thrown if user not found
     *
     * @param userId the identifier for the user
     * @return User
     */
    @GET
    @Path("{id}")
    User getUser(@PathParam("id") String userId);

    /**
     * Finds a user by their username.
     *
     * @param username the user's username used to login
     * @return User a populated user object
     */
    User getUserByUsername(@PathParam("username") String username);

    /**
     * Retrieves a list of all users.
     *
     * @return List
     */
    @GET
    List<User> getUsers();
    
    /**
     * Retrieves a page of all users.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<User> searchUsers(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all users.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> usersSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all users(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> usersTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws InvalidMDSRoleException;

    /**
     * Saves a user's information
     *
     * @param user the user's information
     * @return updated user
     * @throws UserExistsException thrown when user already exists
     */
    @POST
    User saveUser(User user) throws UserExistsException;

   /* *//**
     * Removes a user from the database by their userId
     *
     * @param userId the user's id
     *//*
    @DELETE
    void removeUser(String userId);*/
    
    /**
     * Removes a usr(1) or more users(1, 2, 3) from the database by their userIds
     *
     * @param userIds the user(s)'s id
     */
    @DELETE
    @Path("{ids}")
    void removeUser(@PathParam("ids") String userIds);
    
    @GET
    @Path("jcaptcha")
    @Produces({"image/png", "image/jpg", "image/gif", "image/bmp", MediaType.APPLICATION_OCTET_STREAM})
    Response getJCaptcha(@Context HttpServletRequest request);
}
