/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;

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

import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.common.exception.RecordExistsException;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/albums")
public interface AlbumService {
    /**
     * Retrieves a album by albumId.  An exception is thrown if album not found
     *
     * @param albumId the identifier for the album
     * @return Album
     */
    @GET
    @Path("{id}")
    Album getAlbum(@PathParam("id") String albumId);

    /**
     * Retrieves a list of all albums.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<Album> getAlbums();
    
    /**
     * Retrieves a page of all albums.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<Album> searchAlbums(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all albums.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> albumsSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all albums(boostrap table).
     *
     * @return List
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> albumsTable(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);


    /**
     * Saves a album's information
     *
     * @param album the album's information
     * @return updated album
     * @throws AlbumExistsException thrown when album already exists
     */
    @POST
    Album saveAlbum(Album album) throws RecordExistsException;

    /**
     * Removes a album(1) or more albums(1, 2, 3) from the database by their albumIds
     *
     * @param albumIds the album(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeAlbum(@PathParam("ids") String albumIds);
    
    /**
     * Retrieves a page of all albums(tree selector).
     *
     * @return List
     * @throws InvalidGalleryException 
     * @throws InvalidAlbumException 
     */
    @GET
    @Path("/treeSelector")
    @Produces({ MediaType.APPLICATION_JSON })
    List<HashMap<String, Object>> albumsTreeSelector(@QueryParam("excludeId") String excludeIdStr) throws InvalidAlbumException, InvalidGalleryException;
}
