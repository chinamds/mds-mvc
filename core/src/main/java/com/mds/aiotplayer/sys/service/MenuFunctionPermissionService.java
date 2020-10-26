/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/menuFunctionPermissions")
public interface MenuFunctionPermissionService {
    /**
     * Retrieves a menuFunctionPermission by menuFunctionPermissionId.  An exception is thrown if menuFunctionPermission not found
     *
     * @param menuFunctionPermissionId the identifier for the menuFunctionPermission
     * @return MenuFunctionPermission
     */
    @GET
    @Path("{id}")
    MenuFunctionPermission getMenuFunctionPermission(@PathParam("id") String menuFunctionPermissionId);

    /**
     * Retrieves a list of all menuFunctionPermissions.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<MenuFunctionPermission> getMenuFunctionPermissions();

    /**
     * Saves a menuFunctionPermission's information
     *
     * @param menuFunctionPermission the menuFunctionPermission's information
     * @return updated menuFunctionPermission
     * @throws MenuFunctionPermissionExistsException thrown when menuFunctionPermission already exists
     */
    @POST
    MenuFunctionPermission saveMenuFunctionPermission(MenuFunctionPermission menuFunctionPermission) throws RecordExistsException;

    /**
     * Removes a menuFunctionPermission from the database by their menuFunctionPermissionId
     *
     * @param menuFunctionPermissionId the menuFunctionPermission's id
     */
    /*@DELETE
    void removeMenuFunctionPermission(String menuFunctionPermissionId);*/
    
    /**
     * Removes a menuFunctionPermission or more menuFunctionPermissions from the database by their menuFunctionPermissionIds(comma-separated string) 
     *
     * @param menuFunctionPermissionIds the menuFunctionPermission's id
     */
    @DELETE
    @Path("{ids}")
    void removeMenuFunctionPermission(@PathParam("ids") String menuFunctionPermissionIds);
    
    /**
     * Retrieves a page of all nenus(boostrap tree table).
     *
     * @return List
     */
    @GET
    @Path("/treeTable")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> menuFunctionPermissionsTreeTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all nenus(tree selector).
     *
     * @return List
     */
    @GET
    @Path("/treeSelector")
    @Produces({ MediaType.APPLICATION_JSON })
    List<HashMap<String, Object>> menuFunctionPermissionsTreeSelector(@QueryParam("exclude") @DefaultValue("0") Long excludeId);
}
