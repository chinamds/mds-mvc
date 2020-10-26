/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.sys.exception.RoleExistsException;
import com.mds.aiotplayer.sys.model.Role;

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
@Path("/roles")
public interface RoleService {
    /**
     * Retrieves a role by roleId.  An exception is thrown if role not found
     *
     * @param roleId the identifier for the role
     * @return Role
     */
    @GET
    @Path("{id}")
    Role getRole(@PathParam("id") String roleId);

    /**
     * Finds a role by their rolename.
     *
     * @param rolename the role's rolename used to login
     * @return Role a populated role object
     */
    Role getRoleByRolename(@PathParam("rolename") String rolename);

    /**
     * Retrieves a list of all roles.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<Role> getRoles();

    /**
     * Saves a role's information
     *
     * @param role the role's information
     * @return updated role
     * @throws RoleExistsException thrown when role already exists
     */
    @POST
    Role saveRole(Role role) throws RoleExistsException;
    
    /**
     * Saves a role's information
     *
     * @param role the role's information
     * @return updated role
     * @throws RoleExistsException thrown when role already exists
     */
    /*@POST
    @Path("{id}")
    Role saveMenuPermission(@PathParam("id") Long roleId, List<Long> menuPermissions);*/

	/**
     * Retrieves a page of all roles.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Role> searchRoles(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all roles.
     *
     * @return List
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> rolesSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) throws InvalidMDSRoleException;
    
    @GET
    @Path("/organization/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> rolesOrganizationSelect2(@QueryParam("oid") String organizationId, @QueryParam("rtype") String rtype,  @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) throws InvalidMDSRoleException;
    
    /**
     * Retrieves a page of all roles(boostrap table).
     *
     * @return List
     * @throws InvalidMDSRoleException 
     * @throws Exception 
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> rolesTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws InvalidMDSRoleException, Exception;
    
    /**
     * Retrieves all nenus and permissions(boostrap treeview) and assign to role.
     *
     * @return List
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/{id}/menuPermissions/treeView")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Map<String,Object>> menuFunctionPermissionTreeView(@PathParam("id") String roleId, @Context HttpServletRequest request) throws InvalidMDSRoleException;
    
    /**
     * Retrieves all organizations(boostrap treeview) and assign to role.
     *
     * @return List
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/{id}/organizations/treeView")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Map<String,Object>> organizationsTreeView(@PathParam("id") String roleId, @Context HttpServletRequest request) throws InvalidMDSRoleException;


    /**
     * Removes a role(1) or more roles(1, 2, 3) from the database by their roleIds
     *
     * @param roleIds the role(s)'s id
     */
    @DELETE
    @Path("{ids}")
    void removeRole(@PathParam("ids") String roleIds);
}
