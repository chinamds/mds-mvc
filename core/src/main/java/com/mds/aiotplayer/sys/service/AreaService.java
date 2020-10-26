/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.sys.exception.AreaExistsException;
import com.mds.aiotplayer.sys.model.Area;

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

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/areas")
public interface AreaService {
    /**
     * Retrieves a area by areaId.  An exception is thrown if area not found
     *
     * @param areaId the identifier for the area
     * @return Area
     */
    @GET
    @Path("{id}")
    Area getArea(@PathParam("id") String areaId);

    /**
     * Finds a area by their areaname.
     *
     * @param areaname the area's areaname used to login
     * @return Area a populated area object
     */
    Area getAreaByAreaname(@PathParam("areaname") String areaname);

    /**
     * Retrieves a list of all areas.
     *
     * @return List
     */
    @GET
    List<Area> getAreas();

    /**
     * Saves a area's information
     *
     * @param area the area's information
     * @return updated area
     * @throws AreaExistsException thrown when area already exists
     */
    @POST
    Area saveArea(Area area) throws AreaExistsException;

    /**
     * Removes a area from the database by their areaId
     *
     * @param areaId the area's id
     */
    /*@DELETE
    void removeArea(String areaId);*/

	/**
     * Removes a area or more areas from the database by their areaIds(comma-separated string) 
     *
     * @param areaIds the area's id
     */
    @DELETE
    @Path("{ids}")
    void removeArea(@PathParam("ids") String areaIds);

    
    /**
     * Retrieves a page of all areas.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Area> searchAreas(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all areas.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> areasSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all areas(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> areasTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);

    /*@GET
    @Path("/logo/{id}")
    @Produces({"image/png", "image/jpg", "image/gif", "image/bmp"})
    Response getLogo(@PathParam("id") String areaId);*/
    
    /**
     * Retrieves a page of all areas(boostrap tree table).
     *
     * @return List
     */
    @GET
    @Path("/treeTable")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> areasTreeTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all areas(tree selector).
     *
     * @return List
     */
    @GET
    @Path("/treeSelector")
    @Produces({ MediaType.APPLICATION_JSON })
    List<HashMap<String, Object>> areasTreeSelector(@QueryParam("exclude") @DefaultValue("0") Long excludeId);
}
