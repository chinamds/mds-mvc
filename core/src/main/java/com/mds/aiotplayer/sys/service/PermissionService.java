package com.mds.aiotplayer.sys.service;

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
import com.mds.aiotplayer.sys.model.Permission;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/permissions")
public interface PermissionService {
    /**
     * Retrieves a permission by permissionId.  An exception is thrown if permission not found
     *
     * @param permissionId the identifier for the permission
     * @return Permission
     */
    @GET
    @Path("{id}")
    Permission getPermission(@PathParam("id") String permissionId);

    /**
     * Retrieves a list of all permissions.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<Permission> getPermissions();
    
    /**
     * Retrieves a page of all permissions.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Permission> searchPermissions(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all permissions.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> permissionsSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all permissions(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> permissionsTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);


    /**
     * Saves a permission's information
     *
     * @param permission the permission's information
     * @return updated permission
     * @throws PermissionExistsException thrown when permission already exists
     */
    @POST
    Permission savePermission(Permission permission) throws RecordExistsException;

    /**
     * Removes a permission(1) or more permissions(1, 2, 3) from the database by their permissionIds
     *
     * @param permissionIds the permission(s)'s id
     */
    @DELETE
    @Path("{ids}")
    void removePermission(@PathParam("ids") String permissionIds);
}
