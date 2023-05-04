/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service.impl;


import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.cm.exception.CannotDeleteAlbumException;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.rest.AlbumAction;
import com.mds.aiotplayer.cm.rest.AlbumRest;
import com.mds.aiotplayer.cm.rest.CMData;
import com.mds.aiotplayer.cm.rest.CMDataLoadOptions;
import com.mds.aiotplayer.cm.rest.ContentItem;
import com.mds.aiotplayer.cm.rest.JsTreeNode;
import com.mds.aiotplayer.cm.rest.MediaItem;
import com.mds.aiotplayer.cm.rest.MetaItemRest;
import com.mds.aiotplayer.cm.rest.PermissionsRest;
import com.mds.aiotplayer.cm.rest.TreeView;
import com.mds.aiotplayer.cm.service.AlbumsManager;
import com.mds.aiotplayer.cm.service.AlbumsService;
import com.mds.aiotplayer.cm.util.AlbumTreePickerBuilder;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.cm.util.GalleryUtils;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.exception.NotSupportedException;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Contains methods for Web API access to albums.
/// </summary>
@Service("albumsManager")
@WebService(serviceName = "AlbumsService", endpointInterface = "com.mds.aiotplayer.cm.service.AlbumsService")
public class AlbumsManagerImpl implements AlbumsManager, AlbumsService{
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	/// <summary>
	/// Gets the album with the specified <paramref name="id" />. The properties 
	/// <see cref="AlbumRest.ContentItems" /> and <see cref="AlbumRest.ContentItems" /> 
	/// are set to null to keep the instance small. Example: api/albums/4/
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <returns>An instance of <see cref="AlbumRest" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	@Override
	public AlbumRest get(long id, HttpServletRequest request){
	  AlbumBo album = null;
	  Long galleryId = null;
	  try
	  {
		album = AlbumUtils.loadAlbumInstance(id, true);
		galleryId = (album != null ? album.getGalleryId() : null);
		SecurityGuard.throwIfUserNotAuthorized(SecurityActions.ViewAlbumOrContentObject, RoleUtils.getMDSRolesForUser(), album.getId(), album.getGalleryId(), UserUtils.isAuthenticated(), album.getIsPrivate(), album.getIsVirtualAlbum());
		PermissionsRest permissionsEntity = new PermissionsRest();
	
		return AlbumUtils.toAlbumEntity(album, permissionsEntity, new CMDataLoadOptions(), request);
	  }catch (InvalidAlbumException ae) {
		throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
		//ReasonPhrase = "Album Not Found"
	  }catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
	  }
	  catch (Exception ex)
	  {
		AppEventLogUtils.LogError(ex, galleryId);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	/// <summary>
	/// Gets a comprehensive set of data about the specified album.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="top">Specifies the number of child content objects to retrieve. Specify 0 to retrieve all items.</param>
	/// <param name="skip">Specifies the number of child content objects to skip.</param>
	/// <returns>An instance of <see cref="CMData" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">
	/// </exception>
	/// <exception cref="Response">
	/// </exception>
	/// <exception cref="StringContent"></exception>
	@Override
	public CMData getInflatedAlbum(long id, int top, int skip, HttpServletRequest request){
	  // GET /api/albums/12/inflated // Return data for album # 12
	  AlbumBo album = null;
	  Long galleryId = null;
	  try
	  {
		  album = CMUtils.loadAlbumInstance(id, true);
		  galleryId = (album != null ? album.getGalleryId() : null);
		  CMDataLoadOptions loadOptions = new CMDataLoadOptions();
		  loadOptions.LoadContentItems = true;
		  loadOptions.NumContentItemsToRetrieve = top;
		  loadOptions.NumContentItemsToSkip = skip;
	
		  return GalleryUtils.getCMDataForAlbum(album, loadOptions, request);
	  }catch (InvalidAlbumException ae) {
		throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
		//ReasonPhrase = "Album Not Found"
	  }catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
	  } catch (Exception ex) {
		AppEventLogUtils.LogError(ex, galleryId);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	/// <summary>
	/// Gets the gallery items for the specified album, optionally sorting the results.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <returns>List{ContentItem}.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	@Override
	public List<ContentItem> getContentItemsForAlbumId(long id, String sortByMetaNameId, boolean sortAscending, HttpServletRequest request){
	  // GET /api/albums/12/contentItems?sortByMetaNameId=11&sortAscending=true - Gets gallery items for album #12
	  try{
		  return ContentObjectUtils.getContentItemsInAlbum(id, MetadataItemName.parse(sortByMetaNameId), sortAscending, request);
	  }catch (InvalidAlbumException ae) {
		  throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
		  //ReasonPhrase = "Album Not Found"
	  }catch (GallerySecurityException ge){
		  throw new WebApplicationException(Response.Status.FORBIDDEN);
	  } catch (Exception ex) {
		AppEventLogUtils.LogError(ex);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	@Override
	public void getTreeView(long id, int secaction, boolean sc, String navurl, HttpServletRequest request, HttpServletResponse response){
	  try{
		  TreeView tv =  new TreeView();
		  if (SecurityActions.isValidSecurityAction(secaction)) {
			  tv = AlbumUtils.getTreeView(id, secaction, sc, navurl, request);
		  }
	      /*response.setContentType("text/json");

          PrintWriter out = response.getWriter();*/
          
          //return Response.ok(tv.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
		  response.setCharacterEncoding("UTF-8");		
		  response.setContentType("application/json; charset=utf-8");		
		  PrintWriter out = null;		
		  try {			
			  out = response.getWriter();			
			  out.append(tv.toJson());			
			 } catch (IOException e) {			
				 e.printStackTrace();		
			} finally {			
				if (out != null) {				
					out.close();			
					}		
				}
		  
		  //return Response.ok().build();
          
	  }catch (InvalidAlbumException ae) {
		  throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
		  //ReasonPhrase = "Album Not Found"
	  }catch (GallerySecurityException ge){
		  throw new WebApplicationException(Response.Status.FORBIDDEN);
	  } catch (Exception ex) {
		AppEventLogUtils.LogError(ex);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	/// <summary>
	/// Gets the gallery items for the specified album, optionally sorting the results.
	/// </summary>
	/// <param name="id">The album ID or content object ID.</param>
	/// <param name="showContentType">show 'album' or 'contentobject' or 'contentobjectid' or 'all'.</param>
	/// <param name="secaction">If set to <c>true</c> sort in ascending order.</param>
	/// <param name="sc">Whether checkboxes are being used.</param>	
	/// <param name="navurl">nav url.</param>
	/// <returns>thumbview data.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@Override
	public List<HashMap<String,Object>> getTreePicker(long id, String showContentType, String thumbSize, int secaction, boolean sc, String navurl, HttpServletRequest request, HttpServletResponse response){
		  try{
			  if (!SecurityActions.isValidSecurityAction(secaction)) {
				  return Lists.newArrayList();
			  }
			  AlbumTreePickerBuilder albumTreePickerBuilder =  new AlbumTreePickerBuilder();

			  return albumTreePickerBuilder.generate(id, showContentType, thumbSize, sc, request);
	          
		  }catch (InvalidAlbumException ae) {
			  throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
			  //ReasonPhrase = "Album Not Found"
		  }catch (GallerySecurityException ge){
			  throw new WebApplicationException(Response.Status.FORBIDDEN);
		  } catch (Exception ex) {
			AppEventLogUtils.LogError(ex);
		
			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		  }
	}
	
	@Override
	public HashMap<String, Object> albumsTreeTable(String id, HttpServletRequest request){
		  try{
		      return AlbumUtils.albumsTreeTable(StringUtils.toLong(id), request);
		  }catch (InvalidAlbumException ae) {
			  throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
			  //ReasonPhrase = "Album Not Found"
		  }catch (GallerySecurityException ge){
			  throw new WebApplicationException(Response.Status.FORBIDDEN);
		  } catch (Exception ex) {
			AppEventLogUtils.LogError(ex);
		
			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		  }
		}
	
	/// <summary>
	/// Gets the media items for the specified album.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <returns>List{MediaItem}.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	@Override
	public List<MediaItem> getMediaItemsForAlbumId(long id, String sortByMetaNameId, boolean sortAscending, HttpServletRequest request){
	  // GET /api/albums/12/mediaitems - Gets media items for album #12
	  try {
		  return Lists.newArrayList(ContentObjectUtils.getMediaItemsInAlbum(id, MetadataItemName.valueOf(sortByMetaNameId), sortAscending, request));
	  }catch (InvalidAlbumException ae){
		  throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
		  //ReasonPhrase = "Album Not Found"
	  }catch (GallerySecurityException ge){
		  throw new WebApplicationException(Response.Status.FORBIDDEN);
	  }catch (Exception ex){
		AppEventLogUtils.LogError(ex);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	/// <summary>
	/// Gets the meta items for the specified album <paramref name="id" />.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <returns></returns>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	@Override
	public List<MetaItemRest> getMetaItemsForAlbumId(long id, HttpServletRequest request){
	  // GET /api/albums/12/meta - Gets metadata items for album #12
	  try {
		return Lists.newArrayList(AlbumUtils.getMetaItemsForAlbum(id, request));
	  } catch (InvalidAlbumException ae){
		throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
		//ReasonPhrase = "Album Not Found"
	  }catch (GallerySecurityException ge){
		throw new WebApplicationException(Response.Status.FORBIDDEN);
	  }catch (Exception ex) {
		AppEventLogUtils.LogError(ex);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	/// <summary>
	/// Persists the <paramref name="album" /> to the data store. Only the following properties are persisted: 
	/// <see cref="AlbumRest.DateStart" />, <see cref="AlbumRest.DateEnd" />, <see cref="AlbumRest.SortById" />,
	/// <see cref="AlbumRest.SortUp" />, <see cref="AlbumRest.getIsPrivate()" />, <see cref="AlbumRest.Owner" />
	/// </summary>
	/// <param name="album">The album to persist.</param>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the album isn't found in the data store,
	/// the current user doesn't have permission to edit the album, or some other error occurs.
	/// </exception>
	@Override
	public void post(AlbumRest album){
	  try {
		AlbumUtils.updateAlbumInfo(album);
	  }catch (InvalidAlbumException ae) {
		throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", album.Id), Response.Status.NOT_FOUND);
		//ReasonPhrase = "Album Not Found"
	  }catch (GallerySecurityException ge){
		throw new WebApplicationException(Response.Status.FORBIDDEN);
	  }catch (NotSupportedException ex) {
		throw new WebApplicationException("Business Rule Violation:" + ex.getMessage());
	  } catch (Exception ex) {
		AppEventLogUtils.LogError(ex, album.GalleryId);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	/// <summary>
	/// Deletes the album with the specified <paramref name="id" /> from the data store.
	/// </summary>
	/// <param name="id">The ID of the album to delete.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the current user doesn't have
	/// permission to delete the album, deleting the album would violate a business rule, or some other
	/// error occurs.
	/// </exception>
	@Override
	public Response delete(long id){
	  try{
		AlbumUtils.deleteAlbum(id);
	
		//return Response.status(200, MessageFormat.format("Album {0} deleted...", id)).build();//new Response(HttpStatusCode.OK) { Content = new StringContent(MessageFormat.format("Album {0} deleted...", id)) };
		log.info("Album {} deleted...", id);
		
		return Response.ok().build();
	  }catch (InvalidAlbumException ae) {
		// HTTP specification says the DELETE method must be idempotent, so deleting a nonexistent item must have 
		// the same effect as deleting an existing one. So we simply return HttpStatusCode.OK.
		log.info("Album with ID = {} does not exist.", id);
		//return Response.status(200, MessageFormat.format("Album with ID = {0} does not exist.", id)).build();//new Response(HttpStatusCode.OK) { Content = new StringContent(MessageFormat.format("Album with ID = {0} does not exist.", id)) };
		return Response.ok().build();
	  }catch (GallerySecurityException ge){
		  throw new WebApplicationException(Response.Status.FORBIDDEN);
	  }catch (CannotDeleteAlbumException ex){
		AppEventLogUtils.LogError(ex);
	
		throw new WebApplicationException(Response.Status.FORBIDDEN);
	  }catch (Exception ex) {
		AppEventLogUtils.LogError(ex);
		
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
    public Response deleteAlbums(String albumIds) {
        try {
        	Long[] ids = ConvertUtil.stringtoLongArray(albumIds);
        	for(long id : ids) {
        		AlbumUtils.deleteAlbum(id);
        	}
        	//return Response.status(200, MessageFormat.format("Albums {0} deleted...", albumIds)).build();//new Response(HttpStatusCode.OK) { Content = new StringContent(MessageFormat.format("Album {0} deleted...", id)) };
        	log.info("Albums {} deleted...", albumIds);
        	
        	return Response.ok().build();
	  	}catch (InvalidAlbumException ae) {
	  		// HTTP specification says the DELETE method must be idempotent, so deleting a nonexistent item must have 
	  		// the same effect as deleting an existing one. So we simply return HttpStatusCode.OK.
	  		//return Response.status(200, MessageFormat.format("Album with ID = {0} does not exist.", albumIds)).build();//new Response(HttpStatusCode.OK) { Content = new StringContent(MessageFormat.format("Album with ID = {0} does not exist.", id)) };
	  		log.info("Albums with IDs = {} does not exist.", albumIds);
	  		  
	  		return Response.ok().build();
	  	  }catch (GallerySecurityException ge){
	  		  throw new WebApplicationException(Response.Status.FORBIDDEN);
	  	  }catch (CannotDeleteAlbumException ex){
	  		AppEventLogUtils.LogError(ex);
	  	
	  		throw new WebApplicationException(Response.Status.FORBIDDEN);
	  	  }catch (Exception ex) {
	  		AppEventLogUtils.LogError(ex);
	  	
	  		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  	  }
    }
	
	/// <summary>
	/// Sorts the <paramref name="contentItems" /> in the order in which they are passed.
	/// This method is used when a user is manually sorting an album and has dragged an item to a new position.
	/// The operation occurs asynchronously and returns immediately.
	/// </summary>
	/// <param name="contentItems">The content objects to sort. Their position in the array indicates the desired
	/// sequence. Only <see cref="ContentItem.getId()" /> and <see cref="ContentItem.ItemType" /> need be 
	/// populated.</param>
    @Override
	public void sort(ContentItem[] contentItems){
	  try{
		String userName = UserUtils.getLoginName();
		//Task.CMUtils.startNew(() => AlbumUtils.Sort(contentItems, userName));
		CompletableFuture
			.runAsync(()->{
	            	try {
	            		AlbumUtils.sort(contentItems, userName);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			});
	  }catch (Exception ex){
		AppEventLogUtils.LogError(ex);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	/// <summary>
	/// Re-sort the items in the album according to the criteria and store this updated sequence in the
	/// database. Callers must have <see cref="SecurityActions.EditAlbum" /> permission.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="sortByMetaNameId">The name of the metadata item to sort on.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
    @Override
	public void sort(long id, String sortByMetaNameId, boolean sortAscending){
	  try{
		AlbumUtils.sort(id, MetadataItemName.parse(sortByMetaNameId).value(), sortAscending);
	  }catch (InvalidAlbumException ae){
		throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", id), Response.Status.NOT_FOUND);
		//ReasonPhrase = "Album Not Found"
	  }catch (GallerySecurityException ge){
		throw new WebApplicationException(Response.Status.FORBIDDEN);
	  }catch (Exception ex){
		AppEventLogUtils.LogError(ex);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
	
	/// <summary>
	/// Sorts the gallery items passed to this method and return. No changes are made to the data store.
	/// When the album is virtual, the <see cref="AlbumRestAction.Album.ContentItems" /> property
	/// must be populated with the items to sort. For non-virtual albums (those with a valid ID), the 
	/// content objects are retrieved based on the ID and then sorted. The sort preference is saved to 
	/// the current user's profile, except when the album is virtual. The method incorporates security to
	/// ensure only authorized items are returned to the user.
	/// </summary>
	/// <param name="albumAction">An instance containing the album to sort and the sort preferences.</param>
	/// <returns>List{ContentItem}.</returns>
    @Override
	public List<ContentItem> sort(AlbumAction albumAction, HttpServletRequest request){
	  // POST /api/albums/getsortedalbum -
	  try {
		return ContentObjectUtils.sortContentItems(albumAction, request);
	  }catch (InvalidAlbumException ae){
		throw new WebApplicationException(MessageFormat.format("Could not find album with ID = {0}", albumAction.Album.Id), Response.Status.NOT_FOUND);
		//ReasonPhrase = "Album Not Found"
	  }catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
	  }catch (Exception ex){
		AppEventLogUtils.LogError(ex);
	
		throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
	  }
	}
}