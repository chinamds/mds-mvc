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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.DailyList;
import com.mds.aiotplayer.cm.rest.CMData;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;

import java.util.HashMap;
import java.util.List;

/**
 * Web Service interface so hierarchy of Generic Manager isn't carried through.
 */
@WebService
@Path("/dailyLists")
public interface DailyListService {
    /**
     * Retrieves a dailyList by dailyListId.  An exception is thrown if dailyList not found
     *
     * @param dailyListId the identifier for the dailyList
     * @return DailyList
     */
    @GET
    @Path("{id}")
    DailyList getDailyList(@PathParam("id") String dailyListId);

    /**
     * Retrieves a list of all dailyLists.
     *
     * @return List
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    List<DailyList> getDailyLists();
    
    /**
     * Retrieves a page of all dailyLists.
     *
     * @return List
     */
    @GET
    @Path("/search")
    @Produces({ MediaType.APPLICATION_JSON })
    List<DailyList> searchDailyLists(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset);
    
    /**
     * Retrieves a page of all dailyLists.
     *
     * @return List
     */
    @GET
    @Path("/select2")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> dailyListsSelect2(@QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request);
    
    /**
     * Retrieves a page of all dailyLists(boostrap table).
     *
     * @return List
     * @throws Exception 
     */
    @GET
    @Path("/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> dailyListsTable(@QueryParam("g") String galleryId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws Exception;
    
    /**
     * Retrieves a page of all dailyLists approval Todo List(boostrap table).
     *
     * @return List
     * @throws Exception 
     */
    @GET
    @Path("/approval/table")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> dailyListsApprovalTable(@QueryParam("g") String galleryId, @QueryParam("q") String searchTerm, @QueryParam("limit") @DefaultValue("100") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset, @Context HttpServletRequest request) throws Exception;


    /**
     * Saves a dailyList's information
     *
     * @param dailyList the dailyList's information
     * @return updated dailyList
     * @throws DailyListExistsException thrown when dailyList already exists
     */
    @POST
    DailyList saveDailyList(DailyList dailyList) throws RecordExistsException, UnsupportedContentObjectTypeException, InvalidGalleryException;
    
    /**
     * Saves a dailyList's information
     *
     * @param dailyList the dailyList's information
     * @return updated dailyList
     * @throws DailyListExistsException thrown when dailyList already exists
     */
    @POST
    @Path("/approve")
    Response dailyListApprove(List<HashMap<String,Object>> dailyListIds) throws RecordExistsException, UnsupportedContentObjectTypeException, InvalidGalleryException;

    /**
     * Removes a dailyList(1) or more dailyLists(1, 2, 3) from the database by their dailyListIds
     *
     * @param dailyListIds the dailyList(s)'s id
     */
    @DELETE
    @Path("{ids}")
    Response removeDailyList(@PathParam("ids") String dailyListIds);
    
    @GET
    @Path("/contentpreview")
    @Produces({ MediaType.APPLICATION_JSON })
    CMData getContentPreviewData(@QueryParam("moid") String contentObjectId, @QueryParam("id") String dailyListId, @Context HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException, Exception;
    
    /**
     * Retrieves a page of all dailyLists(tree selector).
     *
     * @return List
     * @throws InvalidGalleryException 
     * @throws UnsupportedImageTypeException 
     * @throws InvalidAlbumException 
     * @throws InvalidContentObjectException 
     * @throws UnsupportedContentObjectTypeException 
     * @throws Exception 
     */
    @GET
    @Path("/daylistitem/{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    HashMap<String, Object> getDailyListItem(@PathParam("id") String dailyListId, @Context HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException, Exception;
    
    @GET
    @Path("/gendaylistitems")
    @Produces({ MediaType.APPLICATION_JSON })
    List<HashMap<String, Object>> genDailyListItemsFromContent(@QueryParam("ids") String contentObjectIds, @Context HttpServletRequest request);
    
    @GET
    @Path("/filelist")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    List<DailyList> getFileList(@QueryParam("o") String organizations, @QueryParam("g") String galleries, @QueryParam("ds") String startDate
    		, @QueryParam("d") @DefaultValue("7") Integer days, @QueryParam("userIP") String user_ip, @QueryParam("userAgent") String user_agent,
            @QueryParam("xforwardedfor") String xforwardedfor, @Context HttpHeaders headers, @Context HttpServletRequest request) throws InvalidGalleryException, InvalidMDSRoleException;
    
}
