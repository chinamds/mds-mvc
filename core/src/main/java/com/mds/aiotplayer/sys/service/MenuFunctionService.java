/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.exception.RecordNotExistsException;
import com.mds.aiotplayer.sys.exception.MenuFunctionExistsException;
import com.mds.aiotplayer.sys.exception.MenuFunctionNotExistsException;
import com.mds.aiotplayer.sys.model.MenuFunction;

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
@Path("/menuFunctions")
public interface MenuFunctionService {
    /**
     * Retrieves a menuFunction by menuFunctionId.  An exception is thrown if menuFunction not found
     *
     * @param menuFunctionId the identifier for the menuFunction
     * @return MenuFunction
     */
    @GET
    @Path("{id}")
    MenuFunction getMenuFunction(@PathParam("id") String menuFunctionId);

    /**
     * Finds a menuFunction by their menuFunctioncode.
     *
     * @param menuFunctioncode the menuFunction's menuFunctioncode used to login
     * @return MenuFunction a populated menuFunction object
     * @throws RecordNotExistsException 
     */
    MenuFunction getMenuFunctionByMenuFunctioncode(@PathParam("menuFunctioncode") String menuFunctioncode) throws MenuFunctionNotExistsException;

    /**
     * Retrieves a list of all menuFunctions.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<MenuFunction> getMenuFunctions();

    /**
     * Saves a menuFunction's information
     *
     * @param menuFunction the menuFunction's information
     * @return updated menuFunction
     * @throws RecordExistsException thrown when menuFunction already exists
     */
    @POST
    MenuFunction saveMenuFunction(MenuFunction menuFunction) throws MenuFunctionExistsException;

    /**
     * Removes a menuFunction from the database by their menuFunctionId
     *
     * @param menuFunctionId the menuFunction's id
     */
    /*@DELETE
    void removeMenuFunction(String menuFunctionId);*/
    
    /**
     * Removes a menuFunction or more menuFunctions from the database by their menuFunctionIds(comma-separated string) 
     *
     * @param menuFunctionIds the menuFunction's id
     */
    @DELETE
    @Path("{ids}")
    void removeMenuFunction(@PathParam("ids") String menuFunctionIds);
    
    /**
     * Retrieves a page of all nenus(boostrap tree table).
     *
     * @return List
     */
    @GET
    @Path("/treeTable")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> menuFunctionsTreeTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
        
    /**
     * Retrieves a page of all nenus(tree selector).
     *
     * @return List
     */
    @GET
    @Path("/treeSelector")
    @Produces({ MediaType.APPLICATION_JSON })
    List<HashMap<String, Object>> menuFunctionsTreeSelector(@QueryParam("exclude") @DefaultValue("0") Long excludeId);
}
