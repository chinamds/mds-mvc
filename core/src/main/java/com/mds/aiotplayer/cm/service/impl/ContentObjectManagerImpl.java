package com.mds.aiotplayer.cm.service.impl;

import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.cm.dao.AlbumDao;
import com.mds.aiotplayer.cm.dao.ContentObjectDao;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.cm.content.AddContentObjectSettings;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.core.ContentObjectRotation;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.cm.service.ContentObjectManager;
import com.mds.aiotplayer.cm.service.ContentObjectService;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.core.ActionResult;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.core.ContentQueueItemConversionType;
import com.mds.aiotplayer.core.DisplayObjectType;
import com.mds.aiotplayer.core.MessageType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.Utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("contentObjectManager")
@WebService(serviceName = "ContentObjectService", endpointInterface = "com.mds.aiotplayer.cm.service.ContentObjectService")
public class ContentObjectManagerImpl extends GenericManagerImpl<ContentObject, Long> implements ContentObjectManager, ContentObjectService {
    ContentObjectDao contentObjectDao;

    @Autowired
    public ContentObjectManagerImpl(ContentObjectDao contentObjectDao) {
        super(contentObjectDao);
        this.contentObjectDao = contentObjectDao;
    }

		/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
	public void removeContentObject(Long id) {
		contentObjectDao.remove(id);
		//CacheUtils.remove(CacheItem.ContentObjects.toString());
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ContentObject saveContentObject(final ContentObject contentObject) throws RecordExistsException {
    	
        try {
        	ContentObject result =  contentObjectDao.save(contentObject);
        	//CacheUtils.remove(CacheItem.ContentObjects.toString());
            
            return result;
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("ContentObject '" + contentObject.getOriginalFilename() + "' already exists!");
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void saveCaptions(Map<Long, String> contentCAptions) {
    	ContentObjectBo mo;
    	try
		{
			// Loop through each item in the repeater control. If an item is checked, extract the ID.
			for (long id : contentCAptions.keySet()){

				// Retrieve new title. Since the Value property of <TEXTAREA> HTML ENCODEs the text,
				// and we want to store the actual text, we must decode to get back to the original.
				String newTitle = Utils.htmlDecode(contentCAptions.get(id));

				try
				{
					mo = CMUtils.loadContentObjectInstance(id, true);
				}
				catch (InvalidContentObjectException ce)
				{
					continue; // Gallery object may have been deleted by someone else, so just skip it.
				}

				String previousTitle = mo.getTitle();

				mo.setTitle(Utils.cleanHtmlTags(newTitle, mo.getGalleryId()));

				if (!mo.getTitle().equals(previousTitle))	{
					ContentObjectUtils.saveContentObject(mo);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
            log.warn(e.getMessage());
		}
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public int saveImageRotates(Map<Long, ContentObjectRotation> contentRotates) {
    	// Rotate any images on the hard drive according to the user's wish.
    	int returnValue = Integer.MIN_VALUE;
    	try
		{
			// Loop through each item in the repeater control. If an item is checked, extract the ID.
			for (long id : contentRotates.keySet()){
				ContentObjectBo mo;
				try
				{
					mo = CMUtils.loadContentObjectInstance(id, true);
				}
				catch (InvalidContentObjectException ce)
				{
					continue; // Gallery object may have been deleted by someone else, so just skip it.
				}

				ContentObjectMetadataItem metaOrientation;
				if (contentRotates.get(id) == ContentObjectRotation.Rotate0 
						&& ((metaOrientation = mo.getMetadataItems().tryGetMetadataItem(MetadataItemName.Orientation)) != null)){
					return returnValue;
				}

				mo.setRotation(contentRotates.get(id));
				
				try{
					ContentObjectUtils.saveContentObject(mo);
				}catch (UnsupportedImageTypeException ue){
					returnValue = MessageType.CannotRotateInvalidImage.value();
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
            log.warn(e.getMessage());
		}
    	
    	return returnValue;
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidGalleryException 
     * @throws UnsupportedImageTypeException 
     * @throws InvalidAlbumException 
     * @throws UnsupportedContentObjectTypeException 
     * @throws WebException 
     * @throws GallerySecurityException 
     * @throws InvalidMDSRoleException 
     * @throws InvalidContentObjectException 
     * @throws IOException 
     */
    @Transactional
    @Override
    public void deleteOriginalFile(List<Long> contentObjectIds, List<Long> albumIds) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException, IOException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, WebException {
    	// Convert the string array of IDs to integers. Also assign whether each is an album or content object.
		// (Determined by the first character of each id's string: a=album; m=content object)
		for (long id : contentObjectIds){
			ContentObjectBo contentObject;
			try
			{
				contentObject = CMUtils.loadContentObjectInstance(id, true);
			}catch (InvalidContentObjectException ce){
				continue; // Item may have been deleted by someone else, so just skip it.
			}

			contentObject.deleteOriginalFile();

			ContentObjectUtils.saveContentObject(contentObject);
		}
		
		for (long id : albumIds){
			deleteOriginalFilesFromAlbum(AlbumUtils.loadAlbumInstance(id, true, true));
		}
    }
    
    private void deleteOriginalFilesFromAlbum(AlbumBo album) throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, WebException {
		// Delete the original file for each item in the album. Then recursively do the same thing to all child albums.
		for (ContentObjectBo contentObject : album.getChildContentObjects(ContentObjectType.ContentObject).values()) {
			contentObject.deleteOriginalFile();

			ContentObjectUtils.saveContentObject(contentObject);
		}

		for (AlbumBo childAlbum : album.getChildContentObjects(ContentObjectType.Album).toAlbums())	{
			deleteOriginalFilesFromAlbum(childAlbum);
		}
	}

	/**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Response removeContentObject(final String contentObjectIds) {
        log.debug("removing contentObject: " + contentObjectIds);
        try {
	        contentObjectDao.remove(ConvertUtil.StringtoLongArray(contentObjectIds));
	        //CacheUtils.remove(CacheItem.ContentObjects.toString());
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Content Object(id=" + contentObjectIds + ") was successfully deleted.");
        return Response.ok().build();
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public ContentObject getContentObject(final String contentObjectId) {
        return contentObjectDao.get(new Long(contentObjectId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentObject> getContentObjects() {
    	log.debug("get all contentObjects from db");
        return contentObjectDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<ContentObject> searchContentObjects(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return contentObjectDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> contentObjectsSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(contentObjectDao.find(pageable).getContent(), request);
       
        return toSelect2Data(contentObjectDao.search(pageable, new String[]{"name", "description"}, searchTerm).getContent(), request);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> contentObjectsTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "name");
        searchable.setPage(pageable);
        
    	Page<ContentObject> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = contentObjectDao.find(searchable);
    	}else {
    		list = contentObjectDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
		
    }
    
    /**
	 * convert contentObject data to select2 format(https://select2.org/data-sources/formats)
	 * {
	 *	  "results": [
	 *	    {
	 *	      "id": 1,
	 *	      "text": "Option 1"
	 *    	},
	 *	    {
	 *	      "id": 2,
	 *	      "text": "Option 2",
	 *	      "selected": true
	 *	    },
	 *	    {
	 *	      "id": 3,
	 *	      "text": "Option 3",
	 *	      "disabled": true
	 *	    }
	 *	  ]
	 *	}
	 * @param contentObjects
	 * @return
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<ContentObject> contentObjects, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (ContentObject u : contentObjects) {
			//contentObject list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("content", u.getContent());//contentObject name
			mapData.put("album", u.getAlbum().getFullName());//contentObject album
			mapData.put("originalFilename", u.getOriginalFilename());//contentObject file name
			mapData.put("originalSizeKB", u.getOptimizedSizeKB());//contentObject size(KB)
			mapData.put("createdBy", u.getCreatedBy());//contentObject upload by
			mapData.put("dateAdded", u.getDateAdded());//contentObject upload by
			mapData.put("id", u.getId());//contentObject id
			list.add(mapData);
		}
				
		return list;
	}
    
    /**
	 * convert contentObject data to select2 format(https://select2.org/data-sources/formats)
	 * {
	 *	  "results": [
	 *	    {
	 *	      "id": 1,
	 *	      "text": "Option 1"
	 *    	},
	 *	    {
	 *	      "id": 2,
	 *	      "text": "Option 2",
	 *	      "selected": true
	 *	    },
	 *	    {
	 *	      "id": 3,
	 *	      "text": "Option 3",
	 *	      "disabled": true
	 *	    }
	 *	  ]
	 *	}
	 * @param contentObjects
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<ContentObject> contentObjects, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (ContentObject u : contentObjects) {
			//contentObject list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getOriginalFilename());//contentObject name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//contentObject id
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	public String getCacheKey() {
    	return CacheItem.cm_contentobjects.toString();
    }
}