/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.sys.exception.OrganizationExistsException;
import com.mds.aiotplayer.sys.model.Organization;

import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/organizations")
public interface OrganizationService {
    /**
     * Retrieves a organization by organizationId.  An exception is thrown if organization not found
     *
     * @param organizationId the identifier for the organization
     * @return Organization
     */
    @GET
    @Path("{id}")
    Organization getOrganization(@PathParam("id") String organizationId);

    /**
     * Finds a organization by their organizationname.
     *
     * @param organizationname the organization's organizationname used to login
     * @return Organization a populated organization object
     */
    Organization getOrganizationByOrganizationname(@PathParam("organizationname") String organizationname);

    /**
     * Retrieves a list of all organizations.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<Organization> getOrganizations();

    /**
     * Saves a organization's information
     *
     * @param organization the organization's information
     * @return updated organization
     * @throws OrganizationExistsException thrown when organization already exists
     */
    @POST
    Organization saveOrganization(Organization organization) throws OrganizationExistsException;

    /**
     * Removes a organization from the database by their organizationId
     *
     * @param organizationId the organization's id
     */
    /*@DELETE
    void removeOrganization(String organizationId);*/

	/**
     * Removes a organization or more organizations from the database by their organizationIds(comma-separated string) 
     *
     * @param organizationIds the organization's id
     */
    @DELETE
    @Path("{ids}")
    void removeOrganization(@PathParam("ids") String organizationIds);

	/**
     * Retrieves a page of all organizations.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Organization> searchOrganizations(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all organizations.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> organizationsSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all organizations(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> organizationsTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);

    @GET
    @Path("/logo/{id}")
    @Produces({"image/png", "image/jpg", "image/gif", "image/bmp"})
    Response getLogo(@PathParam("id") String organizationId);
    
    /**
     * Retrieves a page of all organizations(boostrap tree table).
     *
     * @return HashMap
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/treeTable")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> organizationsTreeTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) throws InvalidMDSRoleException;
    
    /**
     * Retrieves a page of all organizations(boostrap tree table).
     *
     * @return HashMap
     */
    @GET
    @Path("/{id}/treeView")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Map<String,Object>> organizationChildrenTreeTable(@PathParam("id") String organizationId, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all organizations(boostrap tree view).
     *
     * @return HashMap
     * @throws InvalidMDSRoleException 
     * @throws InvalidMDSSystemRoleException 
     */
    @GET
    @Path("/organization/treeView")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Map<String,Object>> organizationTreeView(@QueryParam("oid") String organizationId, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) throws InvalidMDSRoleException;
    
    /**
     * Retrieves a page of all organizations(tree selector).
     *
     * @return List
     * @throws InvalidMDSRoleException 
     */
    @GET
    @Path("/treeSelector")
    @Produces({ MediaType.APPLICATION_JSON })
    List<HashMap<String, Object>> organizationsTreeSelector(@QueryParam("exclude") @DefaultValue("0") Long excludeId) throws InvalidMDSRoleException;
}
