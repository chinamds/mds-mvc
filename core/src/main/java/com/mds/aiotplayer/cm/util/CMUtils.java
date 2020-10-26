/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.AlbumDeleteBehavior;
import com.mds.aiotplayer.cm.content.Image;
import com.mds.aiotplayer.cm.content.Video;
import com.mds.aiotplayer.cm.content.Audio;
import com.mds.aiotplayer.cm.content.ContentEncoderSettings;
import com.mds.aiotplayer.cm.content.ContentEncoderSettingsCollection;
import com.mds.aiotplayer.cm.content.ContentObjectApproval;
import com.mds.aiotplayer.cm.content.ContentObjectApprovalCollection;
import com.mds.aiotplayer.cm.content.GenericContentObject;
import com.mds.aiotplayer.cm.content.ExternalContentObject;
import com.mds.aiotplayer.cm.content.AlbumProfile;
import com.mds.aiotplayer.cm.content.AlbumProfileCollection;
import com.mds.aiotplayer.cm.content.AlbumSaveBehavior;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.core.exception.InvalidEnumArgumentException;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.ContentObjectBoCollection;
import com.mds.aiotplayer.cm.content.ContentObjectDeleteBehavior;
import com.mds.aiotplayer.cm.content.ContentObjectProfile;
import com.mds.aiotplayer.cm.content.ContentObjectProfileCollection;
import com.mds.aiotplayer.cm.content.ContentObjectSaveBehavior;
import com.mds.aiotplayer.cm.content.ContentObjectSearchOptions;
import com.mds.aiotplayer.cm.content.ContentObjectSearcher;
import com.mds.aiotplayer.cm.content.ContentQueueItem;
import com.mds.aiotplayer.cm.content.ContentTemplateBo;
import com.mds.aiotplayer.cm.content.ContentTemplateBoCollection;
import com.mds.aiotplayer.sys.util.MDSRole;
import com.mds.aiotplayer.sys.util.MDSRoleCollection;
import com.mds.aiotplayer.cm.content.DeleteBehavior;
import com.mds.aiotplayer.core.exception.DataException;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.content.GalleryBoCollection;
import com.mds.aiotplayer.cm.content.GalleryControlSettings;
import com.mds.aiotplayer.cm.content.GalleryControlSettingsCollection;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.content.GallerySettingsCollection;
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.content.MimeTypeBoCollection;
import com.mds.aiotplayer.cm.content.SaveBehavior;
import com.mds.aiotplayer.cm.content.SynchronizationStatus;
import com.mds.aiotplayer.cm.content.UiTemplateBo;
import com.mds.aiotplayer.cm.content.UiTemplateBoCollection;
import com.mds.aiotplayer.sys.util.UserAccountCollection;
import com.mds.aiotplayer.cm.content.UserGalleryProfile;
import com.mds.aiotplayer.cm.content.UserProfile;
import com.mds.aiotplayer.cm.content.Watermark;
import com.mds.aiotplayer.cm.content.nullobjects.NullContentObject;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.metadata.AlbumMetadataReadWriter;
import com.mds.aiotplayer.cm.metadata.AudioMetadataReadWriter;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItemCollection;
import com.mds.aiotplayer.cm.metadata.ExternalMetadataReadWriter;
import com.mds.aiotplayer.cm.metadata.GenericMetadataReadWriter;
import com.mds.aiotplayer.cm.metadata.ImageMetadataReadWriter;
import com.mds.aiotplayer.cm.metadata.MetadataDefinition;
import com.mds.aiotplayer.cm.metadata.MetadataDefinitionCollection;
import com.mds.aiotplayer.cm.metadata.MetadataReadWriter;
import com.mds.aiotplayer.cm.metadata.VideoMetadataReadWriter;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.cm.model.ContentActivity;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.cm.model.ContentType;
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.cm.model.GalleryControlSetting;
import com.mds.aiotplayer.cm.model.GallerySetting;
import com.mds.aiotplayer.cm.model.Metadata;
import com.mds.aiotplayer.cm.model.MimeType;
import com.mds.aiotplayer.cm.model.MimeTypeGallery;
import com.mds.aiotplayer.cm.model.Synchronize;
import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.service.ContentActivityManager;
import com.mds.aiotplayer.cm.service.ContentObjectManager;
import com.mds.aiotplayer.cm.service.ContentQueueManager;
import com.mds.aiotplayer.cm.service.ContentTemplateManager;
import com.mds.aiotplayer.cm.service.ContentTypeManager;
import com.mds.aiotplayer.cm.service.GalleryControlSettingManager;
import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.cm.service.GalleryMappingManager;
import com.mds.aiotplayer.cm.service.GallerySettingManager;
import com.mds.aiotplayer.cm.service.MetadataManager;
import com.mds.aiotplayer.cm.service.MimeTypeGalleryManager;
import com.mds.aiotplayer.cm.service.MimeTypeManager;
import com.mds.aiotplayer.cm.service.SynchronizeManager;
import com.mds.aiotplayer.cm.service.UiTemplateManager;
import com.mds.aiotplayer.cm.service.UserGalleryProfileManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.mapper.JsonMapper;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.SynchronizationInProgressException;
import com.mds.aiotplayer.core.ApprovalAction;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.core.ContentAlignment;
import com.mds.aiotplayer.core.ContentObjectSearchType;
import com.mds.aiotplayer.core.ContentObjectTransitionType;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.MimeTypeCategory;
import com.mds.aiotplayer.core.PagerPosition;
import com.mds.aiotplayer.core.SlideShowType;
import com.mds.aiotplayer.core.SynchronizationState;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;


/// <summary>
	/// Contains functionality for creating and retrieving various business objects. Use methods in this class instead of instantiating
	/// certain objects directly. This includes instances of <see cref="Image" />, <see cref="Video" />, <see cref="Audio" />, 
	/// <see cref="GenericContentObject" />, and <see cref="Album" />.
	/// </summary>
public class CMUtils{

	//#region Private Fields
	private static Map<Long, SynchronizationStatus> _syncStatuses = new HashMap<Long, SynchronizationStatus>(1);
	private static final Object _sharedLock = new Object();
	private static Long _templateGalleryId;
	private static GalleryBoCollection _galleries = new GalleryBoCollection();
	private static Map<Long, Watermark> _watermarks = new HashMap<Long, Watermark>(1);
	private static GallerySettingsCollection _gallerySettings = new GallerySettingsCollection();

	private static GalleryControlSettingsCollection _galleryControlSettings = new GalleryControlSettingsCollection();
	
	//private static GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
	//private static GalleryMappingManager galleryMappingManager = SpringContextHolder.getBean(GalleryMappingManager.class);
	//private static GallerySettingManager gallerySettingManager = SpringContextHolder.getBean(GallerySettingManager.class);
	//private static GalleryControlSettingManager galleryControlSettingManager = SpringContextHolder.getBean(GalleryControlSettingManager.class);
	//private static AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
	//private static ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);
	//private static MetadataManager metadataManager = SpringContextHolder.getBean(MetadataManager.class);
	//private static UiTemplateManager uiTemplateManager = SpringContextHolder.getBean(UiTemplateManager.class);
	//private static ContentTemplateManager contentTemplateManager = SpringContextHolder.getBean(ContentTemplateManager.class);
	//private static MimeTypeManager mimeTypeManager = SpringContextHolder.getBean(MimeTypeManager.class);
	//private static MimeTypeGalleryManager mimeTypeGalleryManager = SpringContextHolder.getBean(MimeTypeGalleryManager.class);
	//private static ContentQueueManager contentQueueManager = SpringContextHolder.getBean(ContentQueueManager.class);
	//private static UserGalleryProfileManager userGalleryProfileManager = SpringContextHolder.getBean(UserGalleryProfileManager.class);
	//private static SynchronizeManager synchronizeManager = SpringContextHolder.getBean(SynchronizeManager.class);
	//private static ContentActivityManager contentActivityManager = SpringContextHolder.getBean(ContentActivityManager.class);
	//private static ContentTypeManager contentTypeManager = SpringContextHolder.getBean(ContentTypeManager.class);

	//#endregion

	//#region GalleryBo Object Methods

	/// <overloads>Create a fully inflated, properly typed content object instance based on the specified parameters.</overloads>
	/// <summary>
	/// Create a fully inflated, properly typed instance based on the specified <see cref="ContentObjectBo.Id">ID</see>. An 
	/// additional call to the data store is made to determine the object's type. When you know the type you want (<see cref="Album" />,
	/// <see cref="Image" />, etc), use the overload that takes the contentObjectType parameter, or call the specific Factory method that 
	/// loads the desired type, as those are more efficient. This method is guaranteed to not return null. If no object is found
	/// that matches the ID, an <see cref="UnsupportedContentObjectTypeException" /> exception is thrown. If both a content object and an 
	/// album exist with the <paramref name = "id" />, the content object reference is returned.
	/// </summary>
	/// <param name="id">An integer representing the <see cref="ContentObjectBo.Id">ID</see> of the content object or album to retrieve from the
	/// data store.</param>
	/// <returns>Returns an <see cref="ContentObjectBo" /> object for the <see cref="ContentObjectBo.Id">ID</see>. This method is guaranteed to not
	/// return null.</returns>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when no content object with the specified <see cref="ContentObjectBo.Id">ID</see> 
	/// is found in the data store.</exception>
	public static ContentObjectBo loadContentObjectInstance(int id) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		// Figure out what type the ID refers to (album, image, video, etc) and then call the overload of this method.
		return loadContentObjectInstance(id, determineContentObjectType(id));
	}

	/// <summary>
	/// Create a fully inflated, properly typed instance based on the specified parameters. If the contentObjectType
	/// parameter is All, None, or Unknown, then an additional call to the data store is made
	/// to determine the object's type. If no object is found that matches the ID and content object type, an 
	/// <see cref="UnsupportedContentObjectTypeException" /> exception is thrown. When you know the type you want (<see cref="Album" />,
	/// <see cref="Image" />, etc), specify the exact contentObjectType, or call the specific Factory method that 
	/// loads the desired type, as that is more efficient. This method is guaranteed to not return null.
	/// </summary>
	/// <param name="id">An integer representing the <see cref="ContentObjectBo.Id">ID</see> of the content object or album to retrieve from the
	/// data store.</param>
	/// <param name="contentObjectType">The type of content object that the id parameter represents. If the type is 
	/// unknown, the Unknown enum value can be specified. Specify the actual type if possible (e.g. Video, Audio, Image, 
	/// etc.), as it is more efficient.</param>
	/// <returns>Returns an <see cref="ContentObjectBo" /> based on the ID. This method is guaranteed to not return null.</returns>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when a particular content object type is requested (e.g. Image, Video, etc.), 
	/// but no content object with the specified ID is found in the data store.</exception>
	/// <exception cref="InvalidAlbumException">Thrown when an album is requested but no album with the specified ID is found in the data store.</exception>
	public static ContentObjectBo loadContentObjectInstance(int id, ContentObjectType contentObjectType) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException	{
		// If the content object type is vague, we need to figure it out.
		if ((contentObjectType == ContentObjectType.All) || (contentObjectType == ContentObjectType.NotSpecified) || (contentObjectType == ContentObjectType.Unknown)){
			contentObjectType = determineContentObjectType(id);
		}

		ContentObjectBo go;
		switch (contentObjectType){
			case Album:
				{
					go = loadAlbumInstance(id, false);
					break;
				}
			case Image:
			case Video:
			case Audio:
			case Generic:
			case Unknown:
				{
					go = loadContentObjectInstance(id);
					break;
				}
			default:
				{
					throw new UnsupportedContentObjectTypeException();
				}
		}

		return go;
	}

	//#endregion

	//#region Content Object Methods

	//#region General Content Object Methods
	
	// <summary>
	/// Verify the current gallery has a root album, creating one if necessary. The root album is returned.
	/// </summary>
	/// <returns>An instance of <see cref="AlbumDto" />.</returns>
	public static Album configureAlbumTable(long galleryId){
		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		
		Searchable searchable = Searchable.newSearchable();
		//searchable.addSearchFilter("parent.id", SearchOperator.eq, null);
		searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
		Album rootAlbumDto = albumManager.findOne(searchable);

		if (rootAlbumDto == null){
			rootAlbumDto = new Album();
			rootAlbumDto.setGallery(galleryManager.get(galleryId));
			rootAlbumDto.setParent(null);
			rootAlbumDto.setName(StringUtils.EMPTY);
			rootAlbumDto.setThumbnailContentObjectId(0L);
			rootAlbumDto.setSortByMetaName(MetadataItemName.DateAdded);
			rootAlbumDto.setSortAscending(true);
			rootAlbumDto.setSeq(0);
			rootAlbumDto.setDateAdded(DateUtils.Now());
			rootAlbumDto.setCreatedBy(Constants.SystemUserName);
			rootAlbumDto.setLastModifiedBy(Constants.SystemUserName);
			rootAlbumDto.setDateLastModified(DateUtils.Now());
			rootAlbumDto.setOwnedBy(StringUtils.EMPTY);
			rootAlbumDto.setOwnerRoleName(StringUtils.EMPTY);
			rootAlbumDto.setIsPrivate(false);
			rootAlbumDto.getMetadatas().add(new Metadata(null, rootAlbumDto, MetadataItemName.Caption, "", "{album.root_Album_Default_Summary}"));
			rootAlbumDto.getMetadatas().add(new Metadata(null, rootAlbumDto, MetadataItemName.Title, "", "{album.root_Album_Default_Title}"));
			if (UserUtils.isAuthenticated()) {
				rootAlbumDto.setCurrentUser(UserUtils.getLoginName());
			}else {
				rootAlbumDto.setCurrentUser(Constants.SystemUserName);
			}
			
			rootAlbumDto = albumManager.save(rootAlbumDto);
		}

		return rootAlbumDto;
	}
		
	public static void deleteAlbum(AlbumBo album){
		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		
		albumManager.remove(album.getId());
	}
	
	public static Album getAlbum(long albumId){
		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		
		return albumManager.get(albumId);
	}
	
	public static void saveAlbum(AlbumBo album) throws RecordExistsException, InvalidGalleryException{
		ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);
		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		
		Album albumDto =  null;
		if (album.getId() != Long.MIN_VALUE) {
			albumDto = albumManager.get(album.getId());
		}
		if (albumDto == null)
			albumDto = new Album();
		
		albumDto.setGallery(galleryManager.get(album.getGalleryId()));
		albumDto.setParent(album.getParent().getId() > 0 ? albumManager.get(album.getParent().getId()) : null);
		albumDto.setName(album.getDirectoryName());
		//albumDto.setThumbnailContentObjectId(album.getThumbnail().getContentObjectId());
		if (album.getThumbnailContentObjectId() > 0) {
			albumDto.setThumbContentObject(contentObjectManager.get(album.getThumbnailContentObjectId()));
		}
		albumDto.setSortByMetaName(album.getSortByMetaName());
		albumDto.setSortAscending(album.getSortAscending());
		albumDto.setSeq(album.getSequence());
		albumDto.setDateStart(DateUtils.getValidateDate(album.getDateStart()));
		albumDto.setDateEnd(DateUtils.getValidateDate(album.getDateEnd()));
		albumDto.setCreatedBy(album.getCreatedByUserName());
		albumDto.setDateAdded(album.getDateAdded());
		albumDto.setLastModifiedBy(album.getLastModifiedByUserName());
		albumDto.setDateLastModified(album.getDateLastModified());
		albumDto.setOwnedBy(album.getOwnerUserName());
		albumDto.setOwnerRoleName(album.getOwnerRoleName());
		albumDto.setIsPrivate(album.getIsPrivate());
		albumDto.setCurrentUser(album.getLastModifiedByUserName());
		ContentObjectMetadataItemCollection metadataItemsToSave = album.getMetadataItems().getItemsToSave();
		if (!metadataItemsToSave.isEmpty())	{
			for(ContentObjectMetadataItem metaDataItem : metadataItemsToSave){
				if (metaDataItem.getIsDeleted()) {
					albumDto.getMetadatas().removeIf(m->m.getId() == metaDataItem.getContentObjectMetadataId());
				}else {
					Metadata metadata = albumDto.getMetadatas().stream().filter(m->m.getId() != null && m.getId() == metaDataItem.getContentObjectMetadataId()).findFirst().orElse(null);
					if ( metadata == null) {
						metadata = new Metadata();
						metadata.setAlbum(albumDto);
						albumDto.getMetadatas().add(metadata);
					}
					metadata.setMetaName(metaDataItem.getMetadataItemName());
					metadata.setRawValue(metaDataItem.getRawValue());
					metadata.setValue(metaDataItem.getValue());
				}
				metaDataItem.setHasChanges(false);
			}
		}
		
		albumDto = albumManager.saveAlbum(albumDto);
		if (album.getIsNew())
			album.setId(albumDto.getId());
	}
	
	public static ContentObjectBoCollection findAlbums(Searchable searchable) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException {
		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();
		if (searchable != null) {
			List<Album> albumDtos = albumManager.findAll(searchable);
			
			for (Album album : albumDtos){
				contentObjects.add(CMUtils.getAlbumFromDto(album));
			}
		}

		return contentObjects;
	}
	
	public static void deleteContentObject(ContentObjectBo contentObject){
		ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);
		
		contentObjectManager.remove(contentObject.getId());
	}
	
	public static ContentObject getContentObject(long contentObjectId){
		ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);
		
		return contentObjectManager.get(contentObjectId);
	}
	
	public static void saveContentObject(ContentObjectBo contentObject) throws InvalidGalleryException{
		ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);
		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		
		ContentObject contentObjectDto =  null;
		if (contentObject.getId() != Long.MIN_VALUE) {
			contentObjectDto = contentObjectManager.get(contentObject.getId());
		}
		if (contentObjectDto == null)
			contentObjectDto = new ContentObject();
		
		contentObjectDto.setAlbum(albumManager.get(contentObject.getParent().getId()));
		contentObjectDto.setThumbnailFilename(contentObject.getThumbnail().getFileName());
		contentObjectDto.setThumbnailWidth(contentObject.getThumbnail().getWidth());
		contentObjectDto.setThumbnailHeight(contentObject.getThumbnail().getHeight());
		contentObjectDto.setThumbnailSizeKB(contentObject.getThumbnail().getFileSizeKB());
		contentObjectDto.setOptimizedFilename(contentObject.getOptimized().getFileName());
		contentObjectDto.setOptimizedWidth(contentObject.getOptimized().getWidth());
		contentObjectDto.setOptimizedHeight(contentObject.getOptimized().getHeight());
		contentObjectDto.setOptimizedSizeKB(contentObject.getOptimized().getFileSizeKB());
		contentObjectDto.setOriginalFilename(contentObject.getOriginal().getFileName());
		contentObjectDto.setOriginalWidth(contentObject.getOriginal().getWidth());
		contentObjectDto.setOriginalHeight(contentObject.getOriginal().getHeight());
		contentObjectDto.setOriginalSizeKB(contentObject.getOriginal().getFileSizeKB());
		contentObjectDto.setExternalHtmlSource(contentObject.getOriginal().getExternalHtmlSource());
		contentObjectDto.setContent(contentObject.getOriginal().getFileName());
		contentObjectDto.setExternalType((contentObject.getOriginal().getExternalType() == MimeTypeCategory.NotSet ? StringUtils.EMPTY : contentObject.getOriginal().getExternalType().toString()));
		contentObjectDto.setSeq(contentObject.getSequence());
		contentObjectDto.setCreatedBy(contentObject.getCreatedByUserName());
		contentObjectDto.setDateAdded(contentObject.getDateAdded());
		contentObjectDto.setLastModifiedBy(contentObject.getLastModifiedByUserName());
		contentObjectDto.setDateLastModified(contentObject.getDateLastModified());
		contentObjectDto.setIsPrivate(contentObject.getIsPrivate());
		contentObjectDto.setApprovalStatus(contentObject.getApprovalStatus());
		contentObjectDto.setCurrentUser(contentObject.getLastModifiedByUserName());
		ContentObjectMetadataItemCollection metadataItemsToSave = contentObject.getMetadataItems().getItemsToSave();
		if (!metadataItemsToSave.isEmpty())	{
			for(ContentObjectMetadataItem metaDataItem : metadataItemsToSave){
				if (metaDataItem.getIsDeleted()) {
					contentObjectDto.getMetadatas().removeIf(m->m.getId() == metaDataItem.getContentObjectMetadataId());
				}else {
					Metadata metadata = contentObjectDto.getMetadatas().stream().filter(m->m.getId() != null && m.getId() == metaDataItem.getContentObjectMetadataId()).findFirst().orElse(null);
					if ( metadata == null) {
						metadata = new Metadata();
						metadata.setContentObject(contentObjectDto);
						contentObjectDto.getMetadatas().add(metadata);
					}
					metadata.setMetaName(metaDataItem.getMetadataItemName());
					metadata.setRawValue(metaDataItem.getRawValue());
					metadata.setValue(metaDataItem.getValue());
				}
				metaDataItem.setHasChanges(false);
			}
		}
		
		try {
			contentObjectDto = contentObjectManager.saveContentObject(contentObjectDto);
			contentObject.setId(contentObjectDto.getId());
		} catch (RecordExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public static ContentObjectBoCollection findContentObjects(Searchable searchable) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException {
		ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);
		
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();
		if (searchable != null) {
			List<ContentObject> contentObjectDtos = contentObjectManager.findAll(searchable);
			
			for (ContentObject contentObject : contentObjectDtos){
				contentObjects.add(CMUtils.getContentObjectFromDto(contentObject, null));
			}
		}

		return contentObjects;
	}

	/// <overloads>
	/// Create a properly typed GalleryBo Object instance (e.g. <see cref="Image" />, <see cref="Video" />, etc.) from the specified parameters.
	/// </overloads>
	/// <summary>
	/// Create a properly typed GalleryBo Object instance (e.g. <see cref="Image" />, <see cref="Video" />, etc.) for the media file
	/// represented by <paramref name = "contentObjectFilePath" /> and belonging to the album specified by <paramref name = "parentAlbum" />.
	/// </summary>
	/// <param name="contentObjectFilePath">The fully qualified name of the content object file, or the relative filename.
	/// The file must already exist in the album's directory. If the file has a matching record in the data store,
	/// a reference to the existing object is returned. Otherwise, a new instance is returned. For new instances,
	/// call <see cref="ContentObjectBo.Save" /> to persist the object to the data store. A
	/// <see cref="UnsupportedContentObjectTypeException" /> is thrown when the specified file cannot 
	/// be added to MDS System, perhaps because it is an unsupported type or the file is corrupt.</param>
	/// <param name="parentAlbum">The album in which the content object exists (for content objects that already exist
	/// in the data store), or should be added to (for new content objects which need to be inserted into the 
	/// data store).</param>
	/// <returns>Returns a properly typed GalleryBo Object instance corresponding to the specified parameters.</returns>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when <paramref name = "contentObjectFilePath" /> has a file 
	/// extension that MDS System is configured to reject.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the  
	/// contentObjectFilePath parameter refers to a file that is not in the same directory as the parent album's directory.</exception>
	public static ContentObjectBo createContentObjectInstance(String contentObjectFilePath, AlbumBo parentAlbum) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidGalleryException{
		return createContentObjectInstance(new File(contentObjectFilePath), parentAlbum);
	}

	/// <summary>
	/// Create a properly typed GalleryBo Object instance (e.g. <see cref="Image" />, <see cref="Video" />, etc.) for the media file
	/// represented by <paramref name = "contentObjectFile" /> and belonging to the album specified by <paramref name = "parentAlbum" />.
	/// </summary>
	/// <param name="contentObjectFile">A <see cref="System.IO.FileInfo" /> object representing a supported content object type. The file must already
	/// exist in the album's directory. If the file has a matching record in the data store, a reference to the existing 
	/// object is returned; otherwise, a new instance is returned. For new instances, call <see cref="ContentObjectBo.Save" /> 
	///		to persist the object to the data store. A <see cref="UnsupportedContentObjectTypeException" /> is thrown when the specified file cannot 
	/// be added to MDS System, perhaps because it is an unsupported type or the file is corrupt.</param>
	/// <param name="parentAlbum">The album in which the content object exists (for content objects that already exist
	/// in the data store), or should be added to (for new content objects which need to be inserted into the 
	/// data store).</param>
	/// <returns>Returns a properly typed GalleryBo Object instance corresponding to the specified parameters.</returns>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when <paramref name = "contentObjectFile" /> has a file 
	/// extension that MDS System is configured to reject.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when   
	/// <paramref name = "contentObjectFile" /> refers to a file that is not in the same directory as the parent album's directory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name = "parentAlbum" /> is null.</exception>
	/// <remarks>
	/// This method is marked internal to ensure it is not called from the web layer. It was noticed that
	/// calling this method from the web layer caused the file referenced in the contentObjectFile parameter to remain
	/// locked beyond the conclusion of the page lifecycle, preventing manual deletion using Windows Explorer. Note 
	/// that restarting IIS (iisreset.exe) released the file lock, and presumably the next garbage collection would 
	/// have released it as well. The web page was modified to call the overload of this method that takes the filepath
	/// as a String parameter and then instantiates a <see cref="System.IO.FileInfo" /> object. I am not sure why, 
	/// but instantiating the <see cref="System.IO.FileInfo" /> object within this DLL in this way caused the file 
	/// lock to be released at the end of the page lifecycle.
	/// </remarks>
	public static ContentObjectBo createContentObjectInstance(File contentObjectFile, AlbumBo parentAlbum) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidGalleryException{
		return createContentObjectInstance(contentObjectFile, parentAlbum, StringUtils.EMPTY, MimeTypeCategory.NotSet);
	}

	/// <summary>
	/// Create a properly typed GalleryBo Object instance (e.g. <see cref="Image" />, <see cref="Video" />, etc.). If 
	/// <paramref name = "externalHtmlSource" /> is specified, then an <see cref="ExternalContentObject" /> is created with the
	/// specified <paramref name = "mimeTypeCategory" />; otherwise a new instance is created based on <paramref name = "contentObjectFile" />,
	/// where the exact type (e.g. <see cref="Image" />, <see cref="Video" />, etc.) is determined by the file's extension.
	/// </summary>
	/// <param name="contentObjectFile">A <see cref="System.IO.FileInfo" /> object representing a supported content object type. The file must already
	/// exist in the album's directory. If the file has a matching record in the data store, a reference to the existing 
	/// object is returned; otherwise, a new instance is returned. For new instances, call <see cref="ContentObjectBo.Save" /> to 
	///		persist the object to the data store. A <see cref="UnsupportedContentObjectTypeException" /> is thrown when the specified file cannot 
	/// be added to MDS System, perhaps because it is an unsupported type or the file is corrupt. Do not specify this parameter
	/// when using the <paramref name = "externalHtmlSource" /> parameter.</param>
	/// <param name="parentAlbum">The album in which the content object exists (for content objects that already exist
	/// in the data store), or should be added to (for new content objects which need to be inserted into the data store).</param>
	/// <param name="externalHtmlSource">The HTML that defines an externally stored content object, such as one hosted at 
	/// Silverlight.net or youtube.com. Using this parameter also requires specifying <paramref name = "mimeTypeCategory" />
	/// and passing null for <paramref name = "contentObjectFile" />.</param>
	/// <param name="mimeTypeCategory">Specifies the category to which an externally stored content object belongs. 
	/// Must be set to a value other than MimeTypeCategory.NotSet when the <paramref name = "externalHtmlSource" /> is specified.</param>
	/// <returns>Returns a properly typed GalleryBo Object instance corresponding to the specified parameters.</returns>
	/// <exception cref="ArgumentException">Thrown when <paramref name = "contentObjectFile" /> and <paramref name = "externalHtmlSource" />
	/// are either both specified, or neither.</exception>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when <paramref name = "contentObjectFile" /> has a file 
	/// extension that MDS System is configured to reject.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when the  
	/// contentObjectFile parameter refers to a file that is not in the same directory as the parent album's directory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name = "parentAlbum" /> is null.</exception>
	/// <remarks>
	/// This method is marked internal to ensure it is not called from the web layer. It was noticed that
	/// calling this method from the web layer caused the file referenced in the contentObjectFile parameter to remain
	/// locked beyond the conclusion of the page lifecycle, preventing manual deletion using Windows Explorer. Note 
	/// that restarting IIS (iisreset.exe) released the file lock, and presumably the next garbage collection would 
	/// have released it as well. The web page was modified to call the overload of this method that takes the filepath
	/// as a String parameter and then instantiates a FileInfo object. I am not sure why, but instantiating the FileInfo 
	/// object within this DLL in this way caused the file lock to be released at the end of the page lifecycle.
	/// </remarks>
	public static ContentObjectBo createContentObjectInstance(File contentObjectFile, AlbumBo parentAlbum, String externalHtmlSource, MimeTypeCategory mimeTypeCategory) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidGalleryException{
		//#region Validation

		// Either contentObjectFile or externalHtmlSource must be specified, but not both.
		if ((contentObjectFile == null) && (StringUtils.isBlank(externalHtmlSource)))
			throw new ArgumentException("The method MDS.Business.Factory.CreateContentObjectInstance was invoked with invalid parameters. The parameters contentObjectFile and externalHtmlSource cannot both be null or empty. One of these - but not both - must be populated.");

		if ((contentObjectFile != null) && (!StringUtils.isBlank(externalHtmlSource)))
			throw new ArgumentException("The method MDS.Business.Factory.CreateContentObjectInstance was invoked with invalid parameters. The parameters contentObjectFile and externalHtmlSource cannot both be specified.");

		if ((!StringUtils.isBlank(externalHtmlSource)) && (mimeTypeCategory == MimeTypeCategory.NotSet))
			throw new ArgumentException("The method MDS.Business.Factory.CreateContentObjectInstance was invoked with invalid parameters. The parameters mimeTypeCategory must be set to a value other than MimeTypeCategory.NotSet when the externalHtmlSource parameter is specified.");

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		//#endregion

		if (StringUtils.isBlank(externalHtmlSource))
			return createLocalContentObjectInstance(contentObjectFile, parentAlbum);
		else
			return createExternalContentObjectInstance(externalHtmlSource, mimeTypeCategory, parentAlbum);
	}

	/// <summary>
	/// Create a properly typed GalleryBo Object instance (e.g. <see cref="Image" />, <see cref="Video" />, etc.) from the specified parameters.
	/// </summary>
	/// <param name="contentObjectFile">A <see cref="System.IO.FileInfo" /> object representing a supported content object type. The file must already
	/// exist in the album's directory. If the file has a matching record in the data store, a reference to the existing 
	/// object is returned; otherwise, a new instance is returned. For new instances, call <see cref="ContentObjectBo.Save" /> 
	///		to persist the object to the data store.</param>
	/// <param name="parentAlbum">The album in which the content object exists (for content objects that already exist
	/// in the data store), or should be added to (for new content objects which need to be inserted into the 
	/// data store).</param>
	/// <returns>Returns a properly typed GalleryBo Object instance corresponding to the specified parameters.</returns>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when <paramref name = "contentObjectFile" /> has a file 
	/// extension that MDS System is configured to reject.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when   
	/// <paramref name = "contentObjectFile" /> refers to a file that is not in the same directory as the parent album's directory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name = "contentObjectFile" /> or <paramref name = "parentAlbum" /> is null.</exception>
	private static ContentObjectBo createLocalContentObjectInstance(File contentObjectFile, AlbumBo parentAlbum) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidGalleryException{
		if (contentObjectFile == null)
			throw new ArgumentNullException("contentObjectFile");

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		ContentObjectBo go;

		//The name of the file includes the file extension.
		ContentObjectType goType = determineContentObjectType(contentObjectFile.getName());

		if (goType == ContentObjectType.Unknown){
			boolean allowUnspecifiedMimeTypes = loadGallerySetting(parentAlbum.getGalleryId()).getAllowUnspecifiedMimeTypes();
			// If we have an unrecognized content object type (because no MIME type element exists in the configuration
			// file that matches the file extension), then treat the object as a generic content object, but only if
			// the "allowUnspecifiedMimeTypes" configuration setting allows adding unknown content object types.
			// If allowUnspecifiedMimeTypes = false, goType remains "Unknown", and we'll be throwing an 
			// UnsupportedContentObjectTypeException at the end of this method.
			if (allowUnspecifiedMimeTypes)	{
				goType = ContentObjectType.Generic;
			}
		}

		switch (goType)	{
			case Image:
				{
					try
					{
						go = createImageInstance(contentObjectFile, parentAlbum);
						break;
					}
					catch (UnsupportedImageTypeException e)
					{
						go = createGenericObjectInstance(contentObjectFile, parentAlbum);
						break;
					}
				}
			case Video:
				{
					go = createVideoInstance(contentObjectFile, parentAlbum);
					break;
				}
			case Audio:
				{
					go = createAudioInstance(contentObjectFile, parentAlbum);
					break;
				}
			case Generic:
				{
					go = createGenericObjectInstance(contentObjectFile, parentAlbum);
					break;
				}
			default:
				{
					throw new UnsupportedContentObjectTypeException(contentObjectFile);
				}
		}

		return go;
	}

	/// <overloads>
	/// Create a fully inflated, properly typed content object instance.
	/// </overloads>
	/// <summary>
	/// Create a read-only, fully inflated, properly typed content object instance from the specified <paramref name="id" />.
	/// If <paramref name="id" /> is an image, video, audio, etc, then the appropriate object is returned. 
	/// An exception is thrown if the <paramref name="id" /> refers to an <see cref="Album" /> (use the 
	/// <see cref="LoadContentObjectBoInstance(int)" /> or <see cref="LoadAlbumInstance(int, boolean)" /> method if
	/// the <paramref name="id" /> refers to an album). An exception is also thrown if no matching record 
	/// exists for this <paramref name="id" />. This method is guaranteed to never return null.
	/// </summary>
	/// <param name="id">An integer representing the <see cref="ContentObjectBo.Id">ID</see> of the content object to retrieve
	/// from the data store.</param>
	/// <returns>Returns a read-only, fully inflated, properly typed content object instance.</returns>
	/// <exception cref="System.ArgumentException">Thrown when the id parameter refers to an album. This method 
	/// should be used only for content objects (image, video, audio, etc).</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when no record exists in the data store for the specified 
	/// <paramref name="id" />, or when the id parameter refers to an album.</exception>
	public static ContentObjectBo loadContentObjectInstance(long id) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		return loadContentObjectInstance(id, false);
	}

	/// <summary>
	/// Create a fully inflated, properly typed, optionally updateable content object instance from the specified <paramref name="id" />. If 
	/// <paramref name="id" /> is an image, video, audio, etc, then the appropriate object is returned. An 
	/// exception is thrown if the <paramref name="id" /> refers to an <see cref="Album" /> (use the <see 
	/// cref="LoadContentObjectBoInstance(int)" /> or <see cref="LoadAlbumInstance(int, boolean)" /> method if  the <paramref name="id" />
	/// refers to an album). An exception is also thrown if no matching record exists for this <paramref name="id" />.
	/// This method is guaranteed to never return null.
	/// </summary>
	/// <param name="id">An integer representing the <see cref="ContentObjectBo.Id">ID</see> of the content object to retrieve
	/// from the data store.</param>
	/// <param name="isWritable">When set to <c>true</c> then return a unique instance that is not shared across threads.
	/// The resulting instance can be modified and persisted to the data store.</param>
	/// <returns>Returns a read-only, fully inflated, properly typed content object instance.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when no record exists in the data store for the specified 
	/// <paramref name="id" />, or when the id parameter refers to an album.</exception>
	public static ContentObjectBo loadContentObjectInstance(long id, boolean isWritable) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		return (isWritable ? retrieveContentObjectFromDataStore(id, null) : retrieveContentObject(id));
	}

	/// <summary>
	/// Create a fully inflated, properly typed, content object instance from the specified <paramref name="moDto" />.
	/// This method is guaranteed to never return null.
	/// </summary>
	/// <param name="moDto">A content object entity. Typically this is generated from a database query.</param>
	/// <param name="parentAlbum">The album containing the content object. Specify null when it is not known, and the
	/// function will automatically generate it.</param>
	/// <returns>Returns a read-only, fully inflated, properly typed content object instance.</returns>
	/// <exception cref="UnsupportedContentObjectTypeException"></exception>
	public static ContentObjectBo getContentObjectFromDto(ContentObject moDto, AlbumBo parentAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException	{
		if (parentAlbum == null){
			parentAlbum = loadAlbumInstance(moDto.getAlbum().getId(), false);
		}

		ContentObjectBo mo;

		ContentObjectType goType = determineContentObjectType(moDto);
		switch (goType)
		{
			case Image:
				mo = new Image(
					moDto.getId(),
					parentAlbum,
					moDto.getThumbnailFilename(),
					moDto.getThumbnailWidth(),
					moDto.getThumbnailHeight(),
					moDto.getThumbnailSizeKB(),
					moDto.getOptimizedFilename().trim(),
					moDto.getOptimizedWidth(),
					moDto.getOptimizedHeight(),
					moDto.getOptimizedSizeKB(),
					moDto.getOriginalFilename().trim(),
					moDto.getOriginalWidth(),
					moDto.getOriginalHeight(),
					moDto.getOriginalSizeKB(),
					moDto.getSeq(),
					moDto.getCreatedBy().trim(),
					moDto.getDateAdded(),
					moDto.getLastModifiedBy().trim(),
					moDto.getDateLastModified(),
					moDto.isIsPrivate(),
					true,
					null,
					moDto.getMetadatas());
				break;

			case Video:
				{
					mo = new Video(
						moDto.getId(),
						parentAlbum,
						moDto.getThumbnailFilename(),
						moDto.getThumbnailWidth(),
						moDto.getThumbnailHeight(),
						moDto.getThumbnailSizeKB(),
						moDto.getOptimizedFilename().trim(),
						moDto.getOptimizedWidth(),
						moDto.getOptimizedHeight(),
						moDto.getOptimizedSizeKB(),
						moDto.getOriginalFilename().trim(),
						moDto.getOriginalWidth(),
						moDto.getOriginalHeight(),
						moDto.getOriginalSizeKB(),
						moDto.getSeq(),
						moDto.getCreatedBy().trim(),
						moDto.getDateAdded(),
						moDto.getLastModifiedBy().trim(),
						moDto.getDateLastModified(),
						moDto.isIsPrivate(),
						true,
						null,
						moDto.getMetadatas());
					break;
				}
			case Audio:
				{
					mo = new Audio(
						moDto.getId(),
						parentAlbum,
						moDto.getThumbnailFilename(),
						moDto.getThumbnailWidth(),
						moDto.getThumbnailHeight(),
						moDto.getThumbnailSizeKB(),
						moDto.getOptimizedFilename().trim(),
						moDto.getOptimizedWidth(),
						moDto.getOptimizedHeight(),
						moDto.getOptimizedSizeKB(),
						moDto.getOriginalFilename().trim(),
						moDto.getOriginalWidth(),
						moDto.getOriginalHeight(),
						moDto.getOriginalSizeKB(),
						moDto.getSeq(),
						moDto.getCreatedBy().trim(),
						moDto.getDateAdded(),
						moDto.getLastModifiedBy().trim(),
						moDto.getDateLastModified(),
						moDto.isIsPrivate(),
						true,
						null,
						moDto.getMetadatas());
					break;
				}
			case External:
				{
					mo = new ExternalContentObject(
						moDto.getId(),
						parentAlbum,
						moDto.getThumbnailFilename(),
						moDto.getThumbnailWidth(),
						moDto.getThumbnailHeight(),
						moDto.getThumbnailSizeKB(),
						moDto.getExternalHtmlSource().trim(),
						MimeTypeCategory.parseMimeTypeCategory(moDto.getExternalType().trim()),
						moDto.getSeq(),
						moDto.getCreatedBy().trim(),
						moDto.getDateAdded(),
						moDto.getLastModifiedBy().trim(),
						moDto.getDateLastModified(),
						moDto.isIsPrivate(),
						true,
						moDto.getMetadatas());
					break;
				}
			case Generic:
			case Unknown:
				{
					mo = new GenericContentObject(
						moDto.getId(),
						parentAlbum,
						moDto.getThumbnailFilename(),
						moDto.getThumbnailWidth(),
						moDto.getThumbnailHeight(),
						moDto.getThumbnailSizeKB(),
						moDto.getOriginalFilename().trim(),
						moDto.getOriginalWidth(),
						moDto.getOriginalHeight(),
						moDto.getOriginalSizeKB(),
						moDto.getSeq(),
						moDto.getCreatedBy().trim(),
						moDto.getDateAdded(),
						moDto.getLastModifiedBy().trim(),
						moDto.getDateLastModified(),
						moDto.isIsPrivate(),
						true,
						null,
						moDto.getMetadatas());
					break;
				}
			default:
				{
					throw new UnsupportedContentObjectTypeException(FilenameUtils.concat(parentAlbum.getFullPhysicalPath(), moDto.getOriginalFilename()));
				}
		}

		return mo;
	}

	/// <summary>
	/// Returns an object that knows how to persist content objects to the data store.
	/// </summary>
	/// <param name="contentObject">A content object to which the save behavior applies. Must be a valid media
	/// object such as <see cref="Image" />, <see cref="Video" />, etc. Do not pass an <see cref="Album" />.</param>
	/// <returns>Returns an object that implements ISaveBehavior.</returns>
	public static SaveBehavior getContentObjectSaveBehavior(ContentObjectBo contentObject){
		assert (!(contentObject instanceof AlbumBo)) : "It is invalid to pass an album as a parameter to the GetContentObjectSaveBehavior() method.";

		return new ContentObjectSaveBehavior(Reflections.as(ContentObjectBo.class, contentObject));
	}

	/// <summary>
	/// Returns an object that knows how to delete content objects from the data store.
	/// </summary>
	/// <param name="contentObject">A content object to which the delete behavior applies. Must be a valid media
	/// object such as Image, Video, etc. Do not pass an Album; use <see cref="GetAlbumDeleteBehavior" /> for configuring <see cref="Album" /> objects.</param>
	/// <returns>Returns an object that implements <see cref="IDeleteBehavior" />.</returns>
	public static DeleteBehavior getContentObjectDeleteBehavior(ContentObjectBo contentObject){
		assert (!(contentObject instanceof AlbumBo)) : "It is invalid to pass an album as a parameter to the GetContentObjectDeleteBehavior() method.";

		return new ContentObjectDeleteBehavior(contentObject);
	}

	//#endregion

	//#region Image Methods

	/// <summary>
	/// Create a minimally populated <see cref="Image" /> instance from the specified parameters.
	/// </summary>
	/// <param name="imageFile">A <see cref="System.IO.FileInfo" /> object representing a supported image type. The file must already
	/// exist in the album's directory. If the file has a matching record in the data store, a reference to the existing 
	/// object is returned; otherwise, a new instance is returned. Otherwise, a new instance is returned. For new instances, 
	///		call <see cref="ContentObjectBo.Save" /> to persist the object to the data store.</param>
	/// <param name="parentAlbum">The album in which the image exists (for content objects that already exist
	/// in the data store), or should be added to (for new content objects which need to be inserted into the 
	/// data store).</param>
	/// <returns>Returns an <see cref="Image" /> instance corresponding to the specified parameters.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when 
	/// <paramref name = "imageFile" /> refers to a file that is not in the same directory as the parent album's directory.</exception>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when
	/// <paramref name = "imageFile" /> has a file extension that MDS System is configured to reject, or it is
	/// associated with a non-image MIME type.</exception>
	/// <exception cref="UnsupportedImageTypeException">Thrown when the 
	/// .NET Framework is unable to load an image file into the <see cref="System.Drawing.Bitmap" /> class. This is 
	/// probably because it is corrupted, not an image supported by the .NET Framework, or the server does not have 
	/// enough memory to process the image. The file cannot, therefore, be handled using the <see cref="Image" /> 
	/// class; use <see cref="GenericContentObject" /> instead.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name = "imageFile" /> or <paramref name = "parentAlbum" /> is null.</exception>
	public static ContentObjectBo createImageInstance(File imageFile, AlbumBo parentAlbum) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException	{
		if (imageFile == null)
			throw new ArgumentNullException("imageFile");

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		// Validation check: Make sure the configuration settings allow for this particular type of file to be added.
		if (!HelperFunctions.isFileAuthorizedForAddingToGallery(imageFile.getName(), parentAlbum.getGalleryId()))
			throw new UnsupportedContentObjectTypeException(imageFile.getPath());

		// If the file belongs to an existing content object, return a reference to it.
		List<ContentObjectBo> childContentObjects = parentAlbum.getChildContentObjects(ContentObjectType.Image).values();
		for (ContentObjectBo childContentObject : childContentObjects){
			if (childContentObject.getOriginal().getFileNamePhysicalPath() == imageFile.getPath())
				return childContentObject;
		}

		// Create a new image object, which will cause a new record to be inserted in the data store when Save() is called.
		return new Image(imageFile, parentAlbum);
	}

	/// <summary>
	/// Create a fully inflated image instance based on the <see cref="ContentObjectBo.Id">ID</see> of the image parameter. Overwrite
	/// properties of the image parameter with the retrieved values from the data store. The returned image
	/// is the same object reference as the image parameter.
	/// </summary>
	/// <param name="image">The image whose properties should be overwritten with the values from the data store.</param>
	/// <returns>Returns an inflated image instance with all properties set to the values from the data store.
	/// </returns>
	/// <exception cref="InvalidContentObjectException">Thrown when
	/// an image is not found in the data store that matches the <see cref="ContentObjectBo.Id">ID</see> of the image parameter in the current gallery.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="image" /> is null.</exception>
	public static ContentObjectBo loadImageInstance(ContentObjectBo image) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		if (image == null)
			throw new ArgumentNullException("image");

		ContentObjectBo retrievedImage = retrieveContentObject(image.getId(), (AlbumBo)image.getParent());

		image.setGalleryId(retrievedImage.getGalleryId());
		//image.Title(retrievedImage.getTitle());
		image.setCreatedByUserName(retrievedImage.getCreatedByUserName());
		image.setDateAdded(retrievedImage.getDateAdded());
		image.setLastModifiedByUserName(retrievedImage.getLastModifiedByUserName());
		image.setDateLastModified(retrievedImage.getDateLastModified());
		image.setIsPrivate(retrievedImage.getIsPrivate());
		image.setSequence(retrievedImage.getSequence());
		//image.getMetadataItems().clear();
		//image.getMetadataItems().addRange(retrievedImage.getMetadataItems().copy());
		image.replaceMeta(retrievedImage.getMetadataItems().copy());

		String albumPhysicalPath = image.getParent().getFullPhysicalPathOnDisk();

		//#region Thumbnail

		image.getThumbnail().setContentObjectId(retrievedImage.getId());
		image.getThumbnail().setFileName(retrievedImage.getThumbnail().getFileName());
		image.getThumbnail().setHeight(retrievedImage.getThumbnail().getHeight());
		image.getThumbnail().setWidth(retrievedImage.getThumbnail().getWidth());

		GallerySettings gallerySetting = loadGallerySetting(image.getGalleryId());

		// The thumbnail is stored in either the album's physical path or an alternate location (if thumbnailPath config setting is specified) .
		String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPhysicalPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
		image.getThumbnail().setFileNamePhysicalPath(FilenameUtils.concat(thumbnailPath, image.getThumbnail().getFileName()));

		//#endregion

		//#region Optimized

		image.getOptimized().setContentObjectId(retrievedImage.getId());
		image.getOptimized().setFileName(retrievedImage.getOptimized().getFileName());
		image.getOptimized().setHeight(retrievedImage.getOptimized().getHeight());
		image.getOptimized().setWidth(retrievedImage.getOptimized().getWidth());

		// Calcululate the full file path to the optimized image. If the optimized filename is equal to the original filename, then no
		// optimized version exists, and we'll just point to the original. If the names are different, then there is a separate optimized
		// image file, and it is stored in either the album's physical path or an alternate location (if optimizedPath config setting is specified).
		String optimizedPath = albumPhysicalPath;

		if (retrievedImage.getOptimized().getFileName() != retrievedImage.getOriginal().getFileName())
			optimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPhysicalPath, gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());

		image.getOptimized().setFileNamePhysicalPath(FilenameUtils.concat(optimizedPath, image.getOptimized().getFileName()));

		//#endregion

		//#region Original

		image.getOriginal().setContentObjectId(retrievedImage.getId());
		image.getOriginal().setFileName(retrievedImage.getOriginal().getFileName());
		image.getOriginal().setHeight(retrievedImage.getOriginal().getHeight());
		image.getOriginal().setWidth(retrievedImage.getOriginal().getWidth());
		image.getOriginal().setFileNamePhysicalPath(FilenameUtils.concat(albumPhysicalPath, image.getOriginal().getFileName()));
		image.getOriginal().setExternalHtmlSource(retrievedImage.getOriginal().getExternalHtmlSource());
		image.getOriginal().setExternalType(retrievedImage.getOriginal().getExternalType());

		//#endregion

		image.setIsInflated(true);
		image.setHasChanges(false);

		return image;
	}

	/// <summary>
	/// Create a fully inflated image instance based on the contentObjectId.
	/// </summary>
	/// <param name="contentObjectId">An <see cref="ContentObjectBo.Id">ID</see> that uniquely represents an existing image content object.</param>
	/// <returns>Returns an inflated image instance with all properties set to the values from the data store.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when
	/// an image is not found in the data store that matches the contentObjectId parameter and the current gallery.</exception>
	public static ContentObjectBo loadImageInstance(long contentObjectId) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		return loadImageInstance(contentObjectId, null);
	}

	/// <summary>
	/// Create a fully inflated image instance based on the contentObjectId.
	/// </summary>
	/// <param name="contentObjectId">An <see cref="ContentObjectBo.Id">ID</see> that uniquely represents an existing image content object.</param>
	/// <param name="parentAlbum">The album containing the content object specified by contentObjectId. Specify
	/// null if a reference to the album is not available, and it will be created based on the parent album
	/// specified in the data store.</param>
	/// <returns>Returns an inflated image instance with all properties set to the values from the data store.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when
	/// an image is not found in the data store that matches the contentObjectId parameter and the current gallery.</exception>
	public static ContentObjectBo loadImageInstance(long contentObjectId, AlbumBo parentAlbum) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		return retrieveContentObject(contentObjectId, parentAlbum);
	}

	//#endregion

	//#region Video Methods

	/// <summary>
	/// Create a minimally populated <see cref="Video" /> instance from the specified parameters.
	/// </summary>
	/// <param name="videoFile">A <see cref="System.IO.FileInfo" /> object representing a supported video type. The file must already
	/// exist in the album's directory. If the file has a matching record in the data store, a reference to the existing 
	/// object is returned; otherwise, a new instance is returned. Otherwise, a new instance is returned. For new instances, 
	///		call <see cref="ContentObjectBo.Save" /> to persist the object to the data store.</param>
	/// <param name="parentAlbum">The album in which the video exists (for content objects that already exist
	/// in the data store), or should be added to (for new content objects which need to be inserted into the 
	/// data store).</param>
	/// <returns>Returns a <see cref="Video" /> instance corresponding to the specified parameters.</returns>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when
	/// <paramref name = "videoFile" /> has a file extension that MDS System is configured to reject, or it is
	/// associated with a non-video MIME type.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when   
	/// <paramref name = "videoFile" /> refers to a file that is not in the same directory as the parent album's directory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name = "videoFile" /> or <paramref name = "parentAlbum" /> is null.</exception>
	public static ContentObjectBo createVideoInstance(File videoFile, AlbumBo parentAlbum) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidGalleryException{
		if (videoFile == null)
			throw new ArgumentNullException("videoFile");

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		// Validation check: Make sure the configuration settings allow for this particular type of file to be added.
		if (!HelperFunctions.isFileAuthorizedForAddingToGallery(videoFile.getName(), parentAlbum.getGalleryId()))
			throw new UnsupportedContentObjectTypeException(videoFile.getPath());

		// If the file belongs to an existing content object, return a reference to it.
		List<ContentObjectBo> childContentObjects = parentAlbum.getChildContentObjects(ContentObjectType.Video).values();
		for(ContentObjectBo childContentObject : childContentObjects)	{
			if (childContentObject.getOriginal().getFileNamePhysicalPath() == videoFile.getPath())
				return childContentObject;
		}

		// Create a new video object, which will cause a new record to be inserted in the data store when Save() is called.
		return new Video(videoFile, parentAlbum);
	}

	/// <summary>
	/// Create a fully inflated <see cref="Video" /> instance based on the <see cref="ContentObjectBo.Id">ID</see> of the video parameter. Overwrite
	/// properties of the video parameter with the retrieved values from the data store. The returned video
	/// is the same object reference as the video parameter.
	/// </summary>
	/// <param name="video">The video whose properties should be overwritten with the values from the data store.</param>
	/// <returns>Returns an inflated <see cref="Video" /> instance with all properties set to the values from the data store.
	/// </returns>
	/// <exception cref="InvalidContentObjectException">Thrown when a video is not found in the data store that matches the 
	/// <see cref="ContentObjectBo.Id">ID</see> of the video parameter in the current gallery.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="video" /> is null.</exception>
	public static ContentObjectBo loadVideoInstance(ContentObjectBo video) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException
	{
		if (video == null)
			throw new ArgumentNullException("video");

		ContentObjectBo retrievedVideo = retrieveContentObject(video.getId(), (AlbumBo)video.getParent());

		video.setGalleryId(retrievedVideo.getGalleryId());
		//video.setTitle(retrievedVideo.getTitle());
		video.setCreatedByUserName(retrievedVideo.getCreatedByUserName());
		video.setDateAdded(retrievedVideo.getDateAdded());
		video.setLastModifiedByUserName(retrievedVideo.getLastModifiedByUserName());
		video.setDateLastModified(retrievedVideo.getDateLastModified());
		video.setIsPrivate(retrievedVideo.getIsPrivate());
		video.setSequence(retrievedVideo.getSequence());
		//video.getMetadataItems().clear();
		//video.getMetadataItems().addRange(retrievedVideo.getMetadataItems().copy());
		video.replaceMeta(retrievedVideo.getMetadataItems().copy());

		String albumPhysicalPath = video.getParent().getFullPhysicalPathOnDisk();

		//#region Thumbnail

		video.getThumbnail().setContentObjectId(retrievedVideo.getId());
		video.getThumbnail().setFileName(retrievedVideo.getThumbnail().getFileName());
		video.getThumbnail().setHeight(retrievedVideo.getThumbnail().getHeight());
		video.getThumbnail().setWidth(retrievedVideo.getThumbnail().getWidth());

		GallerySettings gallerySetting = loadGallerySetting(video.getGalleryId());

		// The thumbnail is stored in either the album's physical path or an alternate location (if thumbnailPath config setting is specified) .
		String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPhysicalPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
		video.getThumbnail().setFileNamePhysicalPath(FilenameUtils.concat(thumbnailPath, video.getThumbnail().getFileName()));

		//#endregion

		//#region Optimized

		video.getOptimized().setContentObjectId(retrievedVideo.getId());
		video.getOptimized().setFileName(retrievedVideo.getOptimized().getFileName());
		video.getOptimized().setHeight(retrievedVideo.getOptimized().getHeight());
		video.getOptimized().setWidth(retrievedVideo.getOptimized().getWidth());

		//#endregion

		//#region Original

		video.getOriginal().setContentObjectId(retrievedVideo.getId());
		video.getOriginal().setFileName(retrievedVideo.getOriginal().getFileName());
		video.getOriginal().setHeight(retrievedVideo.getOriginal().getHeight());
		video.getOriginal().setWidth(retrievedVideo.getOriginal().getWidth());
		video.getOriginal().setFileNamePhysicalPath(FilenameUtils.concat(albumPhysicalPath, video.getOriginal().getFileName()));
		video.getOriginal().setExternalHtmlSource(retrievedVideo.getOriginal().getExternalHtmlSource());
		video.getOriginal().setExternalType(retrievedVideo.getOriginal().getExternalType());

		//#endregion

		video.setIsInflated(true);
		video.setHasChanges(false);

		return video;
	}

	//#endregion

	//#region Audio Methods

	/// <summary>
	/// Create a minimally populated <see cref="Audio" /> instance from the specified parameters.
	/// </summary>
	/// <param name="audioFile">A <see cref="System.IO.FileInfo" /> object representing a supported audio type. The file must already
	/// exist in the album's directory. If the file has a matching record in the data store, a reference to the existing 
	/// object is returned; otherwise, a new instance is returned. Otherwise, a new instance is returned. For new instances, 
	///		call <see cref="ContentObjectBo.Save" /> to persist the object to the data store.</param>
	/// <param name="parentAlbum">The album in which the audio exists (for content objects that already exist
	/// in the data store), or should be added to (for new content objects which need to be inserted into the 
	/// data store).</param>
	/// <returns>Returns an <see cref="Audio" /> instance corresponding to the specified parameters.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when 
	/// <paramref name = "audioFile" /> refers to a file that is not in the same directory as the parent album's directory.</exception>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when
	/// <paramref name = "audioFile" /> has a file extension that MDS System is configured to reject, or it is
	/// associated with a non-audio MIME type.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name = "audioFile" /> or <paramref name = "parentAlbum" /> is null.</exception>
	public static ContentObjectBo createAudioInstance(File audioFile, AlbumBo parentAlbum) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidGalleryException{
		if (audioFile == null)
			throw new ArgumentNullException("audioFile");

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		// Validation check: Make sure the configuration settings allow for this particular type of file to be added.
		if (!HelperFunctions.isFileAuthorizedForAddingToGallery(audioFile.getName(), parentAlbum.getGalleryId()))
			throw new UnsupportedContentObjectTypeException(audioFile.getPath());

		// If the file belongs to an existing content object, return a reference to it.
		List<ContentObjectBo> childContentObjects =  parentAlbum.getChildContentObjects(ContentObjectType.Audio).values();
		for(ContentObjectBo childContentObject : childContentObjects){
			if (childContentObject.getOriginal().getFileNamePhysicalPath() == audioFile.getPath())
				return childContentObject;
		}

		// Create a new audio object, which will cause a new record to be inserted in the data store when Save() is called.
		return new Audio(audioFile, parentAlbum);
	}

	/// <summary>
	/// Create a fully inflated <see cref="Audio" /> instance based on the <see cref="ContentObjectBo.Id">ID</see> of the audio parameter. Overwrite
	/// properties of the audio parameter with the retrieved values from the data store. The returned audio
	/// is the same object reference as the audio parameter.
	/// </summary>
	/// <param name="audio">The <see cref="Audio" /> instance whose properties should be overwritten with the values from the data store.</param>
	/// <returns>Returns an inflated <see cref="Audio" /> instance with all properties set to the values from the data store.
	/// </returns>
	/// <exception cref="InvalidContentObjectException">Thrown when a audio file is not found in the data store that matches the 
	/// <see cref="ContentObjectBo.Id">ID</see> of the audio parameter in the current gallery.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="audio" /> is null.</exception>
	public static ContentObjectBo loadAudioInstance(ContentObjectBo audio) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		if (audio == null)
			throw new ArgumentNullException("audio");

		ContentObjectBo retrievedAudio = retrieveContentObject(audio.getId(), (AlbumBo)audio.getParent());

		audio.setGalleryId(retrievedAudio.getGalleryId());
		//audio.setTitle(retrievedAudio.getTitle());
		audio.setCreatedByUserName(retrievedAudio.getCreatedByUserName());
		audio.setDateAdded(retrievedAudio.getDateAdded());
		audio.setLastModifiedByUserName(retrievedAudio.getLastModifiedByUserName());
		audio.setDateLastModified(retrievedAudio.getDateLastModified());
		audio.setIsPrivate(retrievedAudio.getIsPrivate());
		audio.setSequence(retrievedAudio.getSequence());
		//audio.getMetadataItems().clear();
		//audio.getMetadataItems().addRange(retrievedAudio.getMetadataItems().copy());
		audio.replaceMeta(retrievedAudio.getMetadataItems().copy());

		String albumPhysicalPath = audio.getParent().getFullPhysicalPathOnDisk();

		//#region Thumbnail

		audio.getThumbnail().setContentObjectId(retrievedAudio.getId());
		audio.getThumbnail().setFileName(retrievedAudio.getThumbnail().getFileName());
		audio.getThumbnail().setHeight(retrievedAudio.getThumbnail().getHeight());
		audio.getThumbnail().setWidth(retrievedAudio.getThumbnail().getWidth());

		GallerySettings gallerySetting = loadGallerySetting(audio.getGalleryId());

		// The thumbnail is stored in either the album's physical path or an alternate location (if thumbnailPath config setting is specified) .
		String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPhysicalPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
		audio.getThumbnail().setFileNamePhysicalPath(FilenameUtils.concat(thumbnailPath, audio.getThumbnail().getFileName()));

		//#endregion

		//#region Optimized

		audio.getOptimized().setContentObjectId(retrievedAudio.getId());
		audio.getOptimized().setFileName(retrievedAudio.getOptimized().getFileName());
		audio.getOptimized().setHeight(retrievedAudio.getOptimized().getHeight());
		audio.getOptimized().setWidth(retrievedAudio.getOptimized().getWidth());

		//#endregion

		//#region Original

		audio.getOriginal().setContentObjectId(retrievedAudio.getId());
		audio.getOriginal().setFileName(retrievedAudio.getOriginal().getFileName());
		audio.getOriginal().setHeight(retrievedAudio.getOriginal().getHeight());
		audio.getOriginal().setWidth(retrievedAudio.getOriginal().getWidth());
		audio.getOriginal().setFileNamePhysicalPath(FilenameUtils.concat(albumPhysicalPath, audio.getOriginal().getFileName()));
		audio.getOriginal().setExternalHtmlSource(retrievedAudio.getOriginal().getExternalHtmlSource());
		audio.getOriginal().setExternalType(retrievedAudio.getOriginal().getExternalType());

		//#endregion

		audio.setIsInflated(true);
		audio.setHasChanges(false);

		return audio;
	}

	//#endregion

	//#region Generic Content Object Methods

	/// <summary>
	/// Create a minimally populated <see cref="GenericContentObject" /> instance from the specified parameters.
	/// </summary>
	/// <param name="file">A <see cref="System.IO.FileInfo" /> object representing a file to be managed by MDS System. The file must 
	/// already exist in the album's directory. If the file has a matching record in the data store, a reference to the existing 
	/// object is returned; otherwise, a new instance is returned. Otherwise, a new instance is returned. For new instances, 
	///		call <see cref="ContentObjectBo.Save" /> to persist the object to the data store.</param>
	/// <param name="parentAlbum">The album in which the file exists (for content objects that already exist
	/// in the data store), or should be added to (for new content objects which need to be inserted into the 
	/// data store).</param>
	/// <returns>Returns a <see cref="GenericContentObject" /> instance corresponding to the specified parameters.</returns>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when
	/// <paramref name = "file" /> has a file extension that MDS System is configured to reject.</exception>
	/// <exception cref="InvalidContentObjectException">Thrown when   
	/// <paramref name = "file" /> refers to a file that is not in the same directory as the parent album's directory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name = "file" /> or <paramref name = "parentAlbum" /> is null.</exception>
	public static ContentObjectBo createGenericObjectInstance(File file, AlbumBo parentAlbum) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidGalleryException
	{
		if (file == null)
			throw new ArgumentNullException("file");

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		// Validation check: Make sure the configuration settings allow for this particular type of file to be added.
		if (!HelperFunctions.isFileAuthorizedForAddingToGallery(file.getName(), parentAlbum.getGalleryId()))
			throw new UnsupportedContentObjectTypeException(file.getPath());

		// If the file belongs to an existing content object, return a reference to it.
		List<ContentObjectBo> childContentObjects =  parentAlbum.getChildContentObjects(ContentObjectType.Generic).values();
		for (ContentObjectBo childContentObject : childContentObjects){
			if (childContentObject.getOriginal().getFileNamePhysicalPath() == file.getPath())
				return childContentObject;
		}

		// Create a new generic content object, which will cause a new record to be inserted in the data store when Save() is called.
		return new GenericContentObject(file, parentAlbum);
	}

	/// <summary>
	/// Create a fully inflated <see cref="GenericContentObject" /> instance based on the <see cref="ContentObjectBo.Id">ID</see> of the 
	/// <paramref name = "genericContentObject" /> parameter. 
	/// Overwrite properties of the <paramref name = "genericContentObject" /> parameter with the retrieved values from the data store. 
	/// The returned instance is the same object reference as the <paramref name = "genericContentObject" /> parameter.
	/// </summary>
	/// <param name="genericContentObject">The object whose properties should be overwritten with the values from 
	/// the data store.</param>
	/// <returns>Returns an inflated <see cref="GenericContentObject" /> instance with all properties set to the values from the 
	/// data store.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when a record is not found in the data store that matches the 
	/// <see cref="ContentObjectBo.Id">ID</see> of the <paramref name = "genericContentObject" /> parameter in the current gallery.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="genericContentObject" /> is null.</exception>
	public static ContentObjectBo loadGenericContentObjectInstance(ContentObjectBo genericContentObject) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		if (genericContentObject == null)
			throw new ArgumentNullException("genericContentObject");

		ContentObjectBo retrievedGenericContentObject = retrieveContentObject(genericContentObject.getId(), (AlbumBo)genericContentObject.getParent());

		genericContentObject.setGalleryId(retrievedGenericContentObject.getGalleryId());
		//genericContentObject.setTitle(retrievedGenericContentObject.getTitle());
		genericContentObject.setCreatedByUserName(retrievedGenericContentObject.getCreatedByUserName());
		genericContentObject.setDateAdded(retrievedGenericContentObject.getDateAdded());
		genericContentObject.setLastModifiedByUserName(retrievedGenericContentObject.getLastModifiedByUserName());
		genericContentObject.setDateLastModified(retrievedGenericContentObject.getDateLastModified());
		genericContentObject.setIsPrivate(retrievedGenericContentObject.getIsPrivate());
		genericContentObject.setSequence(retrievedGenericContentObject.getSequence());
		//genericContentObject.getMetadataItems().clear();
		//genericContentObject.getMetadataItems().addRange(retrievedGenericContentObject.getMetadataItems().copy());
		genericContentObject.replaceMeta(retrievedGenericContentObject.getMetadataItems().copy());

		String albumPhysicalPath = genericContentObject.getParent().getFullPhysicalPathOnDisk();

		//#region Thumbnail

		genericContentObject.getThumbnail().setContentObjectId(retrievedGenericContentObject.getId());
		genericContentObject.getThumbnail().setFileName(retrievedGenericContentObject.getThumbnail().getFileName());
		genericContentObject.getThumbnail().setHeight(retrievedGenericContentObject.getThumbnail().getHeight());
		genericContentObject.getThumbnail().setWidth(retrievedGenericContentObject.getThumbnail().getWidth());

		GallerySettings gallerySetting = loadGallerySetting(genericContentObject.getGalleryId());

		// The thumbnail is stored in either the album's physical path or an alternate location (if thumbnailPath config setting is specified) .
		String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPhysicalPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
		genericContentObject.getThumbnail().setFileNamePhysicalPath(FilenameUtils.concat(thumbnailPath, genericContentObject.getThumbnail().getFileName()));

		//#endregion

		//#region Optimized

		// No optimized object for a generic content object.

		//#endregion

		//#region Original

		genericContentObject.getOriginal().setContentObjectId(retrievedGenericContentObject.getId());
		genericContentObject.getOriginal().setFileName(retrievedGenericContentObject.getOriginal().getFileName());
		genericContentObject.getOriginal().setHeight(retrievedGenericContentObject.getOriginal().getHeight());
		genericContentObject.getOriginal().setWidth(retrievedGenericContentObject.getOriginal().getWidth());
		genericContentObject.getOriginal().setFileNamePhysicalPath(FilenameUtils.concat(albumPhysicalPath, genericContentObject.getOriginal().getFileName()));
		genericContentObject.getOriginal().setExternalHtmlSource(retrievedGenericContentObject.getOriginal().getExternalHtmlSource());
		genericContentObject.getOriginal().setExternalType(retrievedGenericContentObject.getOriginal().getExternalType());

		//#endregion

		genericContentObject.setIsInflated(true);
		genericContentObject.setHasChanges(false);

		return genericContentObject;
	}

	//#endregion

	//#region External Content Object Methods

	/// <summary>
	/// Create a minimally populated <see cref="ExternalContentObject" /> instance from the specified parameters.
	/// </summary>
	/// <param name="externalHtmlSource">The HTML that defines an externally stored content object, such as one hosted at 
	/// YouTube or Silverlight.live.com.</param>
	/// <param name="mimeType">Specifies the category to which this mime type belongs. This usually corresponds to the first portion of 
	/// the full mime type description. (e.g. "image" if the full mime type is "image/jpeg").</param>
	/// <param name="parentAlbum">The album in which the file exists (for content objects that already exist
	/// in the data store), or should be added to (for new content objects which need to be inserted into the 
	/// data store).</param>
	/// <returns>Returns a minimally populated <see cref="ExternalContentObject" /> instance from the specified parameters.</returns>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name = "externalHtmlSource" /> is an empty String or null.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="parentAlbum" /> is null.</exception>
	public static ContentObjectBo createExternalContentObjectInstance(String externalHtmlSource, MimeTypeCategory mimeType, AlbumBo parentAlbum) throws InvalidGalleryException
	{
		if (StringUtils.isBlank(externalHtmlSource))
			throw new ArgumentOutOfRangeException("externalHtmlSource", "The parameter is either null or an empty String.");

		if (parentAlbum == null)
			throw new ArgumentNullException("parentAlbum");

		// Create a new generic content object, which will cause a new record to be inserted in the data store when Save() is called.
		return new ExternalContentObject(externalHtmlSource, mimeType, parentAlbum);
	}

	/// <summary>
	/// Create a fully inflated <see cref="ExternalContentObject" /> instance based on the <see cref="ContentObjectBo.Id">ID</see> of the 
	/// <paramref name = "externalContentObject" /> parameter. 
	/// Overwrite properties of the <paramref name = "externalContentObject" /> parameter with the retrieved values from the data store. 
	/// The returned instance is the same object reference as the <paramref name = "externalContentObject" /> parameter.
	/// </summary>
	/// <param name="externalContentObject">The object whose properties should be overwritten with the values from 
	/// the data store.</param>
	/// <returns>Returns an inflated <see cref="ExternalContentObject" /> instance with all properties set to the values from the 
	/// data store.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when a record is not found in the data store that matches the 
	/// <see cref="ContentObjectBo.Id">ID</see> of the <paramref name = "externalContentObject" /> parameter in the current gallery.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="externalContentObject" /> is null.</exception>
	public static ContentObjectBo loadExternalContentObjectInstance(ContentObjectBo externalContentObject) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		if (externalContentObject == null)
			throw new ArgumentNullException("externalContentObject");

		ContentObjectBo retrievedExternalContentObject = retrieveContentObject(externalContentObject.getId(), (AlbumBo)externalContentObject.getParent());

		externalContentObject.setGalleryId(retrievedExternalContentObject.getGalleryId());
		//externalContentObject.setTitle(retrievedGenericContentObject.getTitle());
		externalContentObject.setCreatedByUserName(retrievedExternalContentObject.getCreatedByUserName());
		externalContentObject.setDateAdded(retrievedExternalContentObject.getDateAdded());
		externalContentObject.setLastModifiedByUserName(retrievedExternalContentObject.getLastModifiedByUserName());
		externalContentObject.setDateLastModified(retrievedExternalContentObject.getDateLastModified());
		externalContentObject.setIsPrivate(retrievedExternalContentObject.getIsPrivate());
		externalContentObject.setSequence(retrievedExternalContentObject.getSequence());
		//externalContentObject.getMetadataItems().clear();
		//externalContentObject.getMetadataItems().addRange(retrievedExternalContentObject.getMetadataItems().copy());
		externalContentObject.replaceMeta(retrievedExternalContentObject.getMetadataItems().copy());

		String albumPhysicalPath = externalContentObject.getParent().getFullPhysicalPathOnDisk();

		//#region Thumbnail

		externalContentObject.getThumbnail().setFileName(retrievedExternalContentObject.getThumbnail().getFileName());
		externalContentObject.getThumbnail().setHeight(retrievedExternalContentObject.getThumbnail().getHeight());
		externalContentObject.getThumbnail().setWidth(retrievedExternalContentObject.getThumbnail().getWidth());

		GallerySettings gallerySetting = loadGallerySetting(externalContentObject.getGalleryId());

		// The thumbnail is stored in either the album's physical path or an alternate location (if thumbnailPath config setting is specified) .
		String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPhysicalPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
		externalContentObject.getThumbnail().setFileNamePhysicalPath(FilenameUtils.concat(thumbnailPath, externalContentObject.getThumbnail().getFileName()));

		//#endregion

		//#region Optimized

		// No optimized image for a generic content object.

		//#endregion

		//#region Original

		externalContentObject.getOriginal().setFileName(retrievedExternalContentObject.getOriginal().getFileName());
		externalContentObject.getOriginal().setHeight(retrievedExternalContentObject.getOriginal().getHeight());
		externalContentObject.getOriginal().setWidth(retrievedExternalContentObject.getOriginal().getWidth());
		externalContentObject.getOriginal().setFileNamePhysicalPath(FilenameUtils.concat(albumPhysicalPath, externalContentObject.getOriginal().getFileName()));
		externalContentObject.getOriginal().setExternalHtmlSource(retrievedExternalContentObject.getOriginal().getExternalHtmlSource());
		externalContentObject.getOriginal().setExternalType(retrievedExternalContentObject.getOriginal().getExternalType());

		//#endregion

		externalContentObject.setIsInflated(true);
		externalContentObject.setHasChanges(false);

		return externalContentObject;
	}

	//#endregion

	//#endregion

	//#region Album Methods

	/// <summary>
	/// Create a new <see cref="Album" /> instance with an unassigned <see cref="ContentObjectBo.Id">ID</see> and properties set to default values.
	/// A valid <see cref="ContentObjectBo.Id">ID</see> will be generated when the object is persisted to the data store during
	/// the <see cref="ContentObjectBo.Save" /> method. Use this overload when creating a new album and it has not yet been persisted
	/// to the data store. Guaranteed to not return null.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// Returns an <see cref="Album" /> instance corresponding to the specified parameters.
	/// </returns>
	/// <overloads>Create a minimally populated <see cref="Album" /> instance.</overloads>
	public static AlbumBo createEmptyAlbumInstance(long galleryId) throws InvalidGalleryException	{
		return new AlbumBo(Long.MIN_VALUE, galleryId);
	}

	/// <summary>
	/// Creates an empty gallery instance. The <see cref="GalleryBo.GalleryId" /> will be set to <see cref="Long.MIN_VALUE" />. 
	/// Generally, gallery instances should be loaded from the data store, but this method can be used to create a new gallery.
	/// </summary>
	/// <returns>Returns an <see cref="GalleryBo" /> instance.</returns>
	public static GalleryBo createGalleryInstance()	{
		return new GalleryBo();
	}

	/// <summary>
	/// Create a minimally populated <see cref="Album" /> instance corresponding to the specified <paramref name = "albumId" />. 
	/// Use this overload when the album already exists in the data store but you do not necessarily need to retrieve its properties. 
	/// A lazy load is performed the first time a property is accessed.
	/// </summary>
	/// <param name="albumId">The <see cref="ContentObjectBo.Id">ID</see> that uniquely identifies an existing album.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// Returns an instance that implements <see cref="AlbumBo" /> corresponding to the specified parameters.
	/// </returns>
	public static AlbumBo createAlbumInstance(long albumId, long galleryId) throws InvalidGalleryException	{
		return new AlbumBo(albumId, galleryId);
	}

	/// <overloads>
	/// Loads an instance of the top-level album from the data store for the specified gallery. 
	/// </overloads>
	///  <summary>
	/// Loads a read-only instance of the top-level album from the data store for the specified gallery. Metadata is
	/// automatically loaded. If this album contains child objects, they are added but not inflated. If this album contains
	/// child objects, they are automatically inflated. Guaranteed to not return null.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// Returns an instance that implements <see cref="AlbumBo" /> with all properties set to the values from the data store.
	/// </returns>
	public static AlbumBo loadRootAlbumInstance(long galleryId) 
			throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException	{
		return loadRootAlbumInstance(galleryId, true, false);
	}

	/// <summary>
	/// Loads an instance of the top-level album from the data store for the specified gallery, optionally specifying
	/// whether to suppress the loading of content object metadata. Suppressing metadata loading offers a performance improvement,
	/// so when this data is not needed, set <paramref name="allowMetadataLoading" /> to <c>false</c>. If this album contains
	/// child objects, they are automatically inflated. Use the <paramref name="isWriteable" /> parameter to specify a writeable, 
	/// thread-safe instance that can be modified and persisted to the data store. Guaranteed to not return null.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="allowMetadataLoading">if set to <c>false</c> the metadata for content objects are not loaded.</param>
	/// <param name="isWriteable">When set to <c>true</c> then return a unique instance that is not shared across threads.</param>
	/// <returns>Returns an instance that implements <see cref="AlbumBo" /> with all properties set to the values from the data store.</returns>
	public static AlbumBo loadRootAlbumInstance(long galleryId, boolean allowMetadataLoading, boolean isWriteable) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		AlbumBo album;
		try{
			album = loadAlbumInstance(loadGallery(galleryId).getRootAlbumId(), true, isWriteable, allowMetadataLoading);
		}catch (InvalidAlbumException ex){
			album = createRootAlbum(galleryId);
		}

		return album;
	}

	/// <summary>
	/// Return all top-level albums in the specified <paramref name = "galleryId">gallery</paramref> where the <paramref name = "roles" /> 
	/// provide view permission to the album. If more than one album is found, they are wrapped in a virtual container 
	/// album where the <see cref="AlbumBo.getIsVirtualAlbum()" /> property is set to true. If the roles do not provide permission to any
	/// objects in the gallery, then a virtual album is returned where <see cref="AlbumBo.getIsVirtualAlbum()" />=<c>true</c> and 
	/// <see cref="ContentObjectBo.Id" />=<see cref="Long.MIN_VALUE" />. Returns null if no matching albums are found.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="roles">The roles belonging to a user.</param>
	/// <param name="isAuthenticated">Indicates whether the user belonging to the <paramref name="roles" /> is authenticated.</param>
	/// <returns>
	/// Returns an <see cref="AlbumBo" /> that is or contains the top-level album(s) that the <paramref name = "roles" />
	/// provide view permission for. Returns null if no matching albums are found.
	/// </returns>
	public static AlbumBo loadRootAlbum(long galleryId, MDSRoleCollection roles, boolean isAuthenticated) throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		ContentObjectSearchOptions searchOptions = new ContentObjectSearchOptions();
		searchOptions.GalleryId = galleryId;
		searchOptions.SearchType = ContentObjectSearchType.HighestAlbumUserCanView;
		searchOptions.Roles = roles;
		searchOptions.IsUserAuthenticated = isAuthenticated;
		searchOptions.Filter = ContentObjectType.Album;
		searchOptions.ApprovalFilter = ApprovalStatus.All;
		
		ContentObjectSearcher contentObjectSearcher = new ContentObjectSearcher(searchOptions);

		return Reflections.as(AlbumBo.class, contentObjectSearcher.findOne());
	}


	/// <overloads>
	/// Generate an inflated <see cref="AlbumBo" /> instance with optionally inflated child content objects.
	/// </overloads>
	/// <summary>
	/// Generate an inflated <see cref="AlbumBo" /> instance with optionally inflated child content objects. The album's <see cref="AlbumBo.ThumbnailContentObjectId" />
	/// property is set to its value from the data store, but the <see cref="ContentObjectBo.Thumbnail" /> property is only inflated when accessed.
	/// </summary>
	/// <param name="album">The album whose properties should be overwritten with the values from the data store.</param>
	/// <param name="inflateChildContentObjects">When true, the child content objects of the album are added and inflated.
	/// Child albums are added but not inflated. When false, they are not added or inflated.</param>
	/// <exception cref="InvalidAlbumException">Thrown when an album is not found in the data store that matches the 
	/// <see cref="ContentObjectBo.Id">ID</see> of the album parameter.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
	/// <exception cref="InvalidOperationException">Thrown when <paramref name="inflateChildContentObjects" /> is <c>false</c> and the album is inflated.</exception>
	public static void loadAlbumInstance(AlbumBo album, boolean inflateChildContentObjects) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException	{
		if (album == null)
			throw new ArgumentNullException("album");

		if (album.getIsInflated() && !inflateChildContentObjects)
			throw new UnsupportedOperationException(I18nUtils.getMessage("cmutils.loadAlbumInstance_Ex_Msg"));

		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		synchronized (_sharedLock){
			//#region Inflate the album, but only if it's not already inflated.

			if (!(album.getIsInflated())){
				if (album.getId() > Long.MIN_VALUE)	{
					
					inflateAlbumFromDto(album, albumManager.get(album.getId()));

					// If the album's parent is an inflated, writeable instance, we want this album to be the same, so don't
					// insert into the cache (since that sets IsWritable to false).
					if (album.getParent().getIsInflated() && !album.getParent().getIsWritable()){
						// OK, to put into cache, so do so if not already there.
						ConcurrentHashMap<Long, AlbumBo> albumCache = (ConcurrentHashMap<Long, AlbumBo>)CacheUtils.get(CacheItem.cm_albums);
						if (albumCache == null)
							albumCache = new ConcurrentHashMap<Long, AlbumBo>();

						if (!albumCache.containsKey(album.getId()))	{
							// The cache exists, but there is no item matching the desired album ID. Add to cache.
							if (!albumCache.containsKey(album.getId()))	{
								album.setIsWritable(false);
								albumCache.put(album.getId(), album);
								CacheUtils.put(CacheItem.cm_albums, albumCache);
							}
						}
					}
				}

				if (!(album.getParent() instanceof NullContentObject)){
					album.setAllowMetadataLoading(((AlbumBo)album.getParent()).getAllowMetadataLoading());
				}

				album.setIsInflated(true);

				assert (album.getThumbnailContentObjectId() > Long.MIN_VALUE) : "The album's ThumbnailContentObjectId should have been assigned in this method.";

				// Since we've just loaded this object from the data store, set the corresponding property.
				album.setFullPhysicalPathOnDisk(album.getFullPhysicalPath());

				album.setHasChanges(false);
			}

			//#endregion

			//#region Add child objects (CreateInstance)

			// Add child albums and objects, if they exist.
			if (inflateChildContentObjects)	{
				addChildObjects(album);
			}

			//#endregion
		}

		if (!album.getIsInflated())
			throw new InvalidAlbumException(album.getId());
	}

	/// <summary>
	/// Generate a read-only, inflated <see cref="AlbumBo" /> instance with optionally inflated child content objects. Metadata 
	/// for content objects are automatically loaded. The album's <see cref="AlbumBo.ThumbnailContentObjectId" /> property is set 
	/// to its value from the data store, but the <see cref="ContentObjectBo.Thumbnail" /> property is only inflated when 
	/// accessed. Guaranteed to not return null.
	/// </summary>
	/// <param name="albumId">The <see cref="ContentObjectBo.Id">ID</see> that uniquely identifies the album to retrieve.</param>
	/// <param name="inflateChildContentObjects">When true, the child content objects of the album are added and inflated.
	/// Child albums are added but not inflated. When false, they are not added or inflated.</param>
	/// <returns>Returns an inflated album instance with all properties set to the values from the data store.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified <paramref name = "albumId" /> 
	/// is not found in the data store.</exception>
	public static AlbumBo loadAlbumInstance(long albumId, boolean inflateChildContentObjects) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException{
		return loadAlbumInstance(albumId, inflateChildContentObjects, false, true);
	}

	/// <summary>
	/// Generate an inflated <see cref="AlbumBo" /> instance with optionally inflated child content objects. Metadata 
	/// for content objects are automatically loaded. Use the <paramref name="isWriteable" /> parameter to specify a writeable, 
	/// thread-safe instance that can be modified and persisted to the data store. The 
	/// album's <see cref="AlbumBo.ThumbnailContentObjectId" /> property is set to its value from the data store, but the 
	/// <see cref="ContentObjectBo.Thumbnail" /> property is only inflated when accessed. Guaranteed to not return null.
	/// </summary>
	/// <param name="albumId">The <see cref="ContentObjectBo.Id">ID</see> that uniquely identifies the album to retrieve.</param>
	/// <param name="inflateChildContentObjects">When true, the child content objects of the album are added and inflated.
	/// Child albums are added but not inflated. When false, they are not added or inflated.</param>
	/// <param name="isWriteable">When set to <c>true</c> then return a unique instance that is not shared across threads.</param>
	/// <returns>Returns an inflated album instance with all properties set to the values from the data store.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified <paramref name = "albumId" /> 
	/// is not found in the data store.</exception>
	public static AlbumBo loadAlbumInstance(long albumId, boolean inflateChildContentObjects, boolean isWriteable) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException	{
		return loadAlbumInstance(albumId, inflateChildContentObjects, isWriteable, true);
	}

	/// <summary>
	/// Generate an inflated <see cref="AlbumBo" /> instance with optionally inflated child content objects, and optionally specifying
	/// whether to suppress the loading of content object metadata. Use the <paramref name="isWritable" />
	/// parameter to specify a writeable, thread-safe instance that can be modified and persisted to the data store. The 
	/// album's <see cref="AlbumBo.ThumbnailContentObjectId" /> property is set to its value from the data store, but the 
	/// <see cref="ContentObjectBo.Thumbnail" /> property is only inflated when accessed. Guaranteed to not return null.
	/// </summary>
	/// <param name="albumId">The <see cref="ContentObjectBo.Id">ID</see> that uniquely identifies the album to retrieve.</param>
	/// <param name="inflateChildContentObjects">When true, the child content objects of the album are added and inflated.
	/// Child albums are added but not inflated. When false, they are not added or inflated.</param>
	/// <param name="isWritable">When set to <c>true</c> then return a unique instance that is not shared across threads.</param>
	/// <param name="allowMetadataLoading">If set to <c>false</c>, the metadata for content objects are not loaded.</param>
	/// <returns>Returns an inflated album instance with all properties set to the values from the data store.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified <paramref name = "albumId" /> 
	/// is not found in the data store.</exception>
	public static AlbumBo loadAlbumInstance(long albumId, boolean inflateChildContentObjects, boolean isWritable, boolean allowMetadataLoading) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException	{
		if (!isWritable && !allowMetadataLoading){
			throw new ArgumentException("Invalid method call. Cannot call LoadAlbumInstance with isWritable and allowMetadataLoading both set to false, since this can cause objects to be stored in the cache with missing metadata.");
		}

		AlbumBo album = (isWritable ? retrieveAlbumFromDataStore(albumId) : retrieveAlbum(albumId));

		album.setAllowMetadataLoading(allowMetadataLoading);

		synchronized (_sharedLock){
			// Add child albums and objects, if they exist, and if needed.
			if ((inflateChildContentObjects) && (!album.getAreChildrenInflated())){
				addChildObjects(album);
			}
		}

		return album;
	}
	
	/// <summary>
	/// Determine the type of the content object (album, image, video, etc) specified by the ID. The object must exist 
	/// in the data store. If no content object is found, or a content object (image, video, etc) is found but 
	/// the file extension does not correspond to a supported MIME type by MDS System, 
	/// <see cref="ContentObjectType.Unknown"/> is returned. If both a content object and an album exist with the 
	/// <paramref name="id"/>, the content object reference is returned.
	/// </summary>
	/// <param name="id">An integer representing a content object that exists in the data store (album, video,
	/// image, etc).</param>
	/// <returns>Returns a ContentObjectType enum indicating the type of content object specified by ID.</returns>
	public static ContentObjectType determineContentType(long id) throws InvalidGalleryException	{
		if (id == Long.MIN_VALUE)
			return ContentObjectType.Unknown;

		//#region Is ID a content object?

		ContentObjectType goType = determineContentObjectType(id);

		//#endregion

		//#region Is ID an album?

		if (goType == ContentObjectType.Unknown){
			AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
			// The ID does not represent a known ContentObject. Check to see if it's an album.
			if (albumManager.exists(id)){
				// If we get here, we found an album.
				goType = ContentObjectType.Album;
			}
		}

		//#endregion

		// If ID is not a content object or album that exists in the data store, return ContentObjectType.Unknown.
		return goType;
	}
	
	/// <overloads>Determine the type of the content object (image, video, audio, generic, etc) specified by the parameter(s). 
	/// This method returns ContentObjectType.Unknown if no matching MIME type can be found. Guaranteed to not 
	/// return null.</overloads>
	/// <summary>
	/// Determine the type of the content object (image, video, audio, generic, etc) based on its ID. 
	/// This method returns ContentObjectType.Unknown if no matching MIME type can be found. Guaranteed to not 
	/// return null.
	/// </summary>
	/// <param name="contentObjectId">An integer representing a content object that exists in the data store. If no 
	/// matching content object is found, an InvalidContentObjectException is thrown. (this will occur when no 
	/// matching record exists in the data store, or the ID actually represents an album ID). If a content object 
	/// is found, but no MIME type is declared in the configuration file that matches the file's extension, 
	/// ContentObjectType.Unknown is returned.</param>
	/// <returns>Returns a ContentObjectType enum indicating the type of content object specified by the 
	/// contentObjectId parameter. Guaranteed to not return null.</returns>
	/// <remarks>Use this method for existing objects that have previously been added to the data store. </remarks>
	/// <exception cref="MDS.EventLogs.CustomExceptions.InvalidContentObjectException">Thrown 
	/// when the contentObjectId parameter does not represent an existing content object in the data store.</exception>
	public static ContentObjectType determineContentObjectType(long contentObjectId) throws InvalidGalleryException{
		ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);
		
		return determineContentObjectType(contentObjectManager.get(contentObjectId));
	}

	public static ContentObjectType determineContentObjectType(ContentObject moDto) throws InvalidGalleryException	{
		return determineContentObjectType(moDto.getOriginalFilename(), moDto.getExternalHtmlSource());
	}

	/// <summary>
	/// Determine the type of the content object (image, video, audio, generic, etc) based on the file's extension. 
	/// This method returns ContentObjectType.Unknown if no matching MIME type can be found. Guaranteed to not 
	/// return null.
	/// </summary>
	/// <param name="fileName">A filename from which to determine its content object type. This is done by comparing
	/// its file extension to the list of extensions known to MDS System. If the file extension 
	/// does not correspond to a known MIME type, ContentObjectType.Unknown is returned.</param>
	/// <returns>Returns a ContentObjectType enum indicating the type of content object specified by the 
	/// filename parameter. Guaranteed to not return null.</returns>
	public static ContentObjectType determineContentObjectType(String fileName) throws InvalidGalleryException	{
		return determineContentObjectType(fileName, StringUtils.EMPTY);
	}

	/// <summary>
	/// Determine the type of the content object (image, video, audio, generic, external etc) based on the file's extension or 
	/// whether external HTML exists. This method returns ContentObjectType.Unknown if <paramref name="externalHtmlSource"/> is 
	/// null or empty and no matching MIME type can be found for <paramref name="fileName"/>. Guaranteed to not return null. 
	/// This overload is intended to be invoked when instantiating an existing content object.
	/// </summary>
	/// <param name="fileName">A filename from which to determine its content object type. This is done by comparing
	/// its file extension to the list of extensions known to MDS System. If the file extension
	/// does not correspond to a known MIME type, ContentObjectType.Unknown is returned.</param>
	/// <param name="externalHtmlSource">The HTML that defines an externally stored content object, such as one hosted at 
	/// YouTube or Silverlight.live.com.</param>
	/// <returns>
	/// Returns a ContentObjectType enum indicating the type of content object. Guaranteed to not return null.
	/// </returns>
	public static ContentObjectType determineContentObjectType(String fileName, String externalHtmlSource) throws InvalidGalleryException	{
		ContentObjectType goType = ContentObjectType.Unknown;

		if (!StringUtils.isBlank(externalHtmlSource)){
			goType = ContentObjectType.External;
		}else{
			MimeTypeBo mimeType = loadMimeType(fileName);
			if (mimeType != null){
				switch (mimeType.getTypeCategory())	{
					case Image: goType = ContentObjectType.Image; break;
					case Video: goType = ContentObjectType.Video; break;
					case Audio: goType = ContentObjectType.Audio; break;
					case Other: goType = ContentObjectType.Generic; break;
					default: throw new InvalidEnumArgumentException(MessageFormat.format("HelperFunctions.determineContentObjectType() encountered a MimeTypeCategory enumeration it does not recognize. The method may need to be updated. (Unrecognized MimeTypeCategory enumeration: MimeTypeCategory.{0})", mimeType.getTypeCategory()));
				}
			}
		}

		return goType;
	}

	/// <summary>
	/// Returns an instance of an object that knows how to persist albums to the data store.
	/// </summary>
	/// <param name="albumObject">An <see cref="AlbumBo" /> to which the save behavior applies.</param>
	/// <returns>Returns an object that implements <see cref="ISaveBehavior" />.</returns>
	public static SaveBehavior getAlbumSaveBehavior(AlbumBo albumObject){
		return new AlbumSaveBehavior(albumObject);
	}

	/// <summary>
	/// Returns an instance of an object that knows how to delete albums from the data store.
	/// </summary>
	/// <param name="albumObject">An <see cref="AlbumBo" /> to which the delete behavior applies.</param>
	/// <returns>Returns an object that implements <see cref="IDeleteBehavior" />.</returns>
	public static DeleteBehavior getAlbumDeleteBehavior(AlbumBo albumObject){
		return new AlbumDeleteBehavior(albumObject);
	}

	/// <summary>
	/// Returns an instance of an object that knows how to read and write metadata to and from a content object.
	/// </summary>
	/// <param name="contentObject">A <see cref="ContentObjectBo" /> for which to retrieve metadata.</param>
	/// <returns>Returns an object that implements <see cref="IMetadataReadWriter" />.</returns>
	public static MetadataReadWriter getMetadataReadWriter(ContentObjectBo contentObject){
		switch (contentObject.getContentObjectType())
		{
			case Album: return new AlbumMetadataReadWriter(contentObject);
			case Image: return new ImageMetadataReadWriter(contentObject);
			case Audio: return new AudioMetadataReadWriter(contentObject);
			case Video: return new VideoMetadataReadWriter(contentObject);
			case External: return new ExternalMetadataReadWriter(contentObject);
			case Generic: return new GenericMetadataReadWriter(contentObject);
			default:
				throw new ArgumentException(MessageFormat.format("Factory.GetMetadataExtractor() does not support content objects with type {0}. A developer may need to update this method.", contentObject.getContentObjectType()));
		}

	}

	/// <summary>
	/// Gets the album from the data transfer object. Guaranteed to not return null.
	/// </summary>
	/// <param name="albumDto">The album data transfer object.</param>
	/// <returns>Returns an <see cref="AlbumBo" />.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when <paramref name="albumDto" /> is null.</exception>
	public static AlbumBo getAlbumFromDto(Album albumDto) throws InvalidAlbumException, InvalidGalleryException{
		if (albumDto == null){
			throw new InvalidAlbumException();
		}

		AlbumBo album = new AlbumBo(albumDto.getId(),
								albumDto.getGallery().getId(),
								albumDto.getParentId()==null? 0 : albumDto.getParentId(),
								albumDto.getName(),
								albumDto.getThumbContentObject()==null?Long.MIN_VALUE : albumDto.getThumbContentObject().getId(),
								albumDto.getSortByMetaName(),
								albumDto.isSortAscending(),
								albumDto.getSeq(),
								albumDto.getDateStart(),
								albumDto.getDateEnd(),
								albumDto.getCreatedBy().trim(),
								albumDto.getDateAdded(),
								albumDto.getLastModifiedBy().trim(),
								albumDto.getDateLastModified(),
								albumDto.getOwnedBy().trim(),
								albumDto.getOwnerRoleName().trim(),
								albumDto.isIsPrivate(),
								true,
								albumDto.getMetadatas());

		//ToContentObjectBoMetadataItemCollection(album, albumDto.Metadata);
		//album.IsInflated = true;
		// Since we've just loaded this object from the data store, set the corresponding property.
		album.setFullPhysicalPathOnDisk(album.getFullPhysicalPath());

		return album;
	}

	//#endregion

	//#region Metadata Methods

	public static void deleteMetadata(long albumId) {
		MetadataManager metadataManager = SpringContextHolder.getBean(MetadataManager.class);
		
		List<Metadata> metadatas = metadataManager.getMetadatas(albumId);
		metadataManager.remove(metadatas);
	}
	/// <summary>
	/// Creates a new, empty metadata collection.
	/// </summary>
	/// <returns>Returns an instance of <see cref="ContentObjectBoMetadataItemCollection" />.</returns>
	public static ContentObjectMetadataItemCollection createMetadataCollection(){
		return new ContentObjectMetadataItemCollection();
	}

	/// <summary>
	/// Create a new <see cref="ContentObjectBoMetadataItem" /> item from the specified parameters.
	/// </summary>
	/// <param name="id">A value that uniquely indentifies this metadata item.</param>
	/// <param name="contentObject">The content object the metadata item applies to.</param>
	/// <param name="rawValue">The raw value of the metadata item. Typically this is the value extracted from 
	/// the metadata of the media file.</param>
	/// <param name="value">The value of the metadata item (e.g. "F5.7", "1/500 sec.").</param>
	/// <param name="hasChanges">A value indicating whether this metadata item has changes that have not been persisted to the database.</param>
	/// <param name="metaDef">The meta definition.</param>
	/// <returns>Returns a reference to the new item.</returns>
	public static ContentObjectMetadataItem createMetadataItem(long id, ContentObjectBo contentObject, String rawValue, String value, boolean hasChanges, MetadataDefinition metaDef){
		return new ContentObjectMetadataItem(id, contentObject, rawValue, value, hasChanges, metaDef);
	}

	/// <summary>
	/// Loads the metadata item for the specified <paramref name="metadataId" />. If no matching 
	/// object is found in the data store, null is returned.
	/// </summary>
	/// <param name="metadataId">The ID that uniquely identifies the metadata item.</param>
	/// <returns>An instance of <see cref="ContentObjectBoMetadataItem" />, or null if not matching
	/// object is found.</returns>
	public static ContentObjectMetadataItem loadContentObjectMetadataItem(long metadataId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidContentObjectException, UnsupportedImageTypeException, InvalidGalleryException{
		//var mDto = GetDataProvider().Metadata_GetMetadataItem(metadataId);
		MetadataManager metadataManager = SpringContextHolder.getBean(MetadataManager.class);
		
		Metadata mDto = metadataManager.get(metadataId);

		if (mDto == null)
			return null;

		ContentObjectBo go = mDto.getAlbum() != null ? loadAlbumInstance(mDto.getAlbum().getId(), false) : loadContentObjectInstance(mDto.getContentObject() == null ? 0 : mDto.getContentObject().getId());

		MetadataDefinitionCollection metaDefs = loadGallerySetting(go.getGalleryId()).getMetadataDisplaySettings();

		return createMetadataItem(mDto.getId(), go, mDto.getRawValue(), mDto.getValue(), false, metaDefs.find(mDto.getMetaName()));
	}

	/// <summary>
	/// Persists the metadata item to the data store, or deletes it when the delete flag is set. 
	/// For certain items (title, filename, etc.), the associated content object's property is also 
	/// updated. For items that are being deleted, it is also removed from the content object's metadata
	/// collection.
	/// </summary>
	/// <param name="md">An instance of <see cref="ContentObjectBoMetadataItem" /> to persist to the data store.</param>
	/// <param name="userName">The user name of the currently logged on user. This will be used for the audit fields.</param>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested meta item  does not exist 
	/// in the data store.</exception>
	public static void saveContentObjectMetadataItem(ContentObjectMetadataItem md, String userName) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, IOException, InvalidGalleryException	{
		MetadataManager metadataManager = SpringContextHolder.getBean(MetadataManager.class);
		
		metadataManager.saveMetadata(md);

		syncWithContentObjectProperties(md, userName);
	}

	//#endregion

	//#region Approval Methods

	/// <summary>
	/// Creates a new, empty Content Object Approval collection.
	/// </summary>
	/// <returns>Returns an instance of <see cref="ContentObjectApprovalCollection" />.</returns>
	public static ContentObjectApprovalCollection createApprovalCollection(){
		return new ContentObjectApprovalCollection();
	}

	/// <summary>
	/// Create a new <see cref="CreateApprovalCollection" /> item from the specified parameters.
	/// </summary>
	/// <param name="id">A value that uniquely indentifies this metadata item.</param>
	/// <param name="contentObject">The content object the metadata item applies to.</param>
	/// <param name="rawValue">The raw value of the metadata item. Typically this is the value extracted from 
	/// the metadata of the media file.</param>
	/// <param name="value">The value of the metadata item (e.g. "F5.7", "1/500 sec.").</param>
	/// <param name="hasChanges">A value indicating whether this metadata item has changes that have not been persisted to the database.</param>
	/// <param name="metaDef">The meta definition.</param>
	/// <returns>Returns a reference to the new item.</returns>
	public static ContentObjectApproval createApprovalItem(long id, ContentObjectBo contentObject, String approveBy, int seq
			, ApprovalAction approvalAction, Date approveDate, boolean hasChanges)	{
		return new ContentObjectApproval(id, contentObject, approveBy, seq, approvalAction, approveDate, hasChanges);
	}

	/// <summary>
	/// Loads the metadata item for the specified <paramref name="metadataId" />. If no matching 
	/// object is found in the data store, null is returned.
	/// </summary>
	/// <param name="metadataId">The ID that uniquely identifies the metadata item.</param>
	/// <returns>An instance of <see cref="ContentObjectBoMetadataItem" />, or null if not matching
	/// object is found.</returns>
	public static ContentObjectApproval loadContentObjectApprovalItem(long approvalId, long contentObjectId, ContentObjectType goType) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException{
		ContentActivityManager contentActivityManager = SpringContextHolder.getBean(ContentActivityManager.class);
		ContentActivity mDto = contentActivityManager.get(approvalId);

		if (mDto == null)
			return null;

		ContentObjectBo go = goType == ContentObjectType.Album ? loadAlbumInstance(contentObjectId, false) : loadContentObjectInstance(contentObjectId);

		return createApprovalItem(mDto.getId(), go, mDto.getUser().getUsername(), mDto.getWorkflowDetail().getSeq()
				, mDto.getApprovalAction(), mDto.getDateAdded(), false);
	}
	
	public static ContentObjectApproval loadContentObjectApprovalItem(long approvalId, ContentObjectBo go){
		ContentActivityManager contentActivityManager = SpringContextHolder.getBean(ContentActivityManager.class);
		ContentActivity mDto = contentActivityManager.get(approvalId);

		if (mDto == null)
			return null;

		return createApprovalItem(mDto.getId(), go, mDto.getUser().getUsername(), mDto.getWorkflowDetail().getSeq()
				, mDto.getApprovalAction(), mDto.getDateAdded(), false);
	}

	/// <summary>
	/// Persists the metadata item to the data store, or deletes it when the delete flag is set. 
	/// For certain items (title, filename, etc.), the associated content object's property is also 
	/// updated. For items that are being deleted, it is also removed from the content object's metadata
	/// collection.
	/// </summary>
	/// <param name="md">An instance of <see cref="ContentObjectBoMetadataItem" /> to persist to the data store.</param>
	/// <param name="userName">The user name of the currently logged on user. This will be used for the audit fields.</param>
	/// <exception cref="InvalidContentObjectException">Thrown when the requested meta item  does not exist 
	/// in the data store.</exception>
	public static void saveContentObjectApprovalItem(ContentObjectApproval ad){
		//contentActivityManager.saveContentActivity(contentActivity)
	}

	//#endregion
	
	//#region ContentType Methods
	public static int getContentTypeByFileName(final String fileName) {
		ContentTypeManager contentTypeManager = SpringContextHolder.getBean(ContentTypeManager.class);
		
		String fileExt = FileMisc.getExt(fileName);
		List<ContentType> contentTypes = contentTypeManager.getContentTypes();
		for (ContentType contentType : contentTypes) {
			if (StringUtils.containsIgnoreCase(contentType.getFileFilter(), fileExt)) {
				return contentType.getType();
			}
		}
		
		if (StringUtils.startsWithIgnoreCase(fileName, "HTTP://") || StringUtils.startsWithIgnoreCase(fileName, "HTTPS://"))
			return Constants.WEBPAGE_TYPE;
		
		return -1;
	}
	
	public static ContentType getContentType(final String fileName) {
		ContentTypeManager contentTypeManager = SpringContextHolder.getBean(ContentTypeManager.class);
		
		String fileExt = FileMisc.getExt(fileName);
		List<ContentType> contentTypes = contentTypeManager.getContentTypes();
		for (ContentType contentType : contentTypes) {
			if (StringUtils.containsIgnoreCase(contentType.getFileFilter(), fileExt)) {
				return contentType;
			}
		}
		
		if (StringUtils.startsWithIgnoreCase(fileName, "HTTP://") || StringUtils.startsWithIgnoreCase(fileName, "HTTPS://"))
			return contentTypes.stream().filter(c->c.getType() == Constants.WEBPAGE_TYPE).findFirst().orElse(null);
		
		return null;
	}
	
	public static ContentType getContentType(final int contentType) {
		ContentTypeManager contentTypeManager = SpringContextHolder.getBean(ContentTypeManager.class);
		
		List<ContentType> contentTypes = contentTypeManager.getContentTypes();
		return contentTypes.stream().filter(c->c.getType() == contentType).findFirst().orElse(null);
	}
	//#endregion

	//#region AppError Methods

	/// <summary>
	/// Gets a collection of all application events from the data store. The items are sorted in descending order on the
	/// <see cref="IEventLog.TimestampUtc" /> property, so the most recent error is first. Returns an empty collection if no
	/// errors exist.
	/// </summary>
	/// <returns>Returns a collection of all application events from the data store.</returns>
	/*public static IEventLogCollection GetAppEvents(){
		var appErrors = (IEventLogCollection)CacheUtils.get(CacheItem.AppEvents);

		if (appErrors != null)
		{
			return appErrors;
		}

		// No events in the cache, so get from data store and add to cache.
		appErrors = EventLogController.GetAppEventLogs();

		CacheUtils.put(CacheItem.AppEvents, appErrors);

		return appErrors;
	}*/

	/// <summary>
	/// Creates an empty event collection.
	/// </summary>
	/// <returns>An instance of <see cref="IEventLogCollection" />.</returns>
	/*public static IEventLogCollection CreateEventCollection()
	{
		return new EventLogCollection();
	}*/

	//#endregion

	//#region GalleryBo and GalleryBo Setting Methods
	public static long saveGallery(GalleryBo gallery) throws RecordExistsException{
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		
		Gallery galleryDto = null;
		if (!gallery.isNew()) {
			galleryDto = galleryManager.get(gallery.getGalleryId());
			if (galleryDto == null) {
				throw new BusinessException(MessageFormat.format("Cannot save gallery: No existing gallery with Gallery ID {0} was found in the database.", gallery.getGalleryId()));
			}
		}
		
		if (galleryDto == null) {
			galleryDto = new Gallery();
			galleryDto.setCreatedBy(UserUtils.getLoginName());
			galleryDto.setDateAdded(gallery.getCreationDate());
		}
		
		galleryDto.setDescription(gallery.getDescription());
		galleryDto.setName(gallery.getName());
		galleryDto.setLastModifiedBy(UserUtils.getLoginName());
		galleryDto.setDateLastModified(DateUtils.Now());
		galleryDto.setCurrentUser(UserUtils.getLoginName());
		
		galleryDto = galleryManager.saveGalleryWithMapping(galleryDto, gallery.getOrganizations(), gallery.getUsers());
			
		return galleryDto.getId();
	}
	
	public static void deleteGallery(GalleryBo gallery) {
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		
		galleryManager.remove(gallery.getGalleryId());
	}
	
	public static Gallery getGallery(long galleryId) {
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		
		return galleryManager.get(galleryId);
	}
	
	/// <summary>
	/// Verify there are gallery settings for the current gallery that match every template gallery setting, creating any
	/// if necessary.
	/// </summary>
	public static void configureGallerySettingsTable(GalleryBo gallery){
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		GallerySettingManager gallerySettingManager = SpringContextHolder.getBean(GallerySettingManager.class);
		
		boolean foundTmplGallerySettings = false;
		// Loop through each template gallery setting.
		Searchable searchable = Searchable.newSearchable();
		searchable.addSearchFilter("gallery.isTemplate", SearchOperator.eq, true);
		List<GallerySetting> gallerySettings = gallerySettingManager.findAll(searchable);
		if (!gallerySettings.isEmpty()) {
			foundTmplGallerySettings = true;
			searchable = Searchable.newSearchable();
    		searchable.addSearchFilter("gallery.id", SearchOperator.eq, gallery.getGalleryId());
    		List<GallerySetting> gsThises = gallerySettingManager.findAll(searchable);
    		Gallery g = galleryManager.get(gallery.getGalleryId());
			for (GallerySetting gsTmpl : gallerySettings){
				if (!gsThises.stream().anyMatch(gs -> gs.getSettingName() == gsTmpl.getSettingName())){
					// This gallery is missing an entry for a gallery setting. Create one by copying it from the template gallery.
					GallerySetting gs = new GallerySetting();
					 gs.setGallery(g);
					 gs.setSettingName(gsTmpl.getSettingName());
					 gs.setSettingValue(gsTmpl.getSettingValue());
				}
    		}
		}


		if (!foundTmplGallerySettings){
			// If there weren't *any* template gallery settings, insert the seed data. Generally this won't be necessary, but it
			// can help recover from certain conditions, such as when a SQL Server connection is accidentally specified without
			// the MultipleActiveResultSets keyword (or it was false). In this situation the galleries are inserted but an error 
			// prevents the remaining data from being inserted. Once the user corrects this and tries again, this code can run to
			// finish inserting the seed data.
			/*using (var ctx = new MDSDB())
			{
				SeedController.InsertSeedData(ctx);
			}*/
		}
	}

	/// <summary>
	/// Gets the ID of the template gallery (that is, the one where <see cref="GalleryBoDto.IsTemplate" /> = <c>true</c>).
	/// </summary>
	/// <returns>System.Int32.</returns>
	public static long getTemplateGalleryId(){
		if (_templateGalleryId == null)	{
			Searchable searchable = Searchable.newSearchable();
    		searchable.addSearchFilter("isTemplate", SearchOperator.eq, true);
    		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
    		List<Gallery> galleries = galleryManager.findAll(searchable);
    		if (galleries != null && !galleries.isEmpty()) {
    			_templateGalleryId = galleries.get(0).getId();
    		}		
		}

		return _templateGalleryId;
	}

	/// <summary>
	/// Loads the gallery specified by the <paramref name = "galleryId" />. Throws a <see cref="InvalidGalleryBoException" /> if no matching 
	/// gallery is found or the requested gallery is the template gallery.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns an instance of <see cref="GalleryBo" /> containing information about the gallery.</returns>
	/// <exception cref="InvalidGalleryBoException">Thrown when no gallery matching <paramref name="galleryId" /> exists in the data store.</exception>
	public static GalleryBo loadGallery(long galleryId) throws InvalidGalleryException{
		if (_galleries.isEmpty()){
			loadGalleries();
		}

		GalleryBo gallery;
		synchronized (_galleries)
		{
			gallery = _galleries.findById(galleryId);
		}

		if (gallery == null){
			// When another application instance creates a gallery, this function might try to load a gallery that doesn't exist in our
			// static variable. Reload all data from the database and try again. Specifically, this can happen when a second DotNetNuke
			// portal creates a gallery, which causes one or more roles to be associated with it. If the first portal then loads that role,
			// the role instantiation tries to load the gallery.
			HelperFunctions.clearAllCaches();

			gallery = loadGalleries().findById(galleryId);

			if (gallery == null){
				throw new InvalidGalleryException(galleryId);
			}
		}

		return gallery;
	}

	/// <summary>
	/// Gets a list of all the galleries in the current application. The returned value is a deep copy of a value stored
	/// in a static variable and is therefore threadsafe. The template gallery is not included. Guaranteed to not be null.
	/// </summary>
	/// <returns>Returns a <see cref="GalleryBoCollection" /> representing the galleries in the current application.</returns>
	public synchronized static GalleryBoCollection loadGalleries()	{
		if (_galleries.isEmpty())	{
			// Ensure that writes related to instantiation are flushed.
			//GetDataProvider().GalleryBo_GetGalleries(_galleries);
			loadGalleries(_galleries);
		}

		return _galleries.copy();
	}

	/// <summary>
	/// Fill the <paramref name="emptyCollection"/> with all the galleries in the current application. The return value is the same reference
	/// as the parameter. The template gallery is not included (that is, the one where <see cref="GalleryBoDto.IsTemplate" /> = <c>true</c>).
	/// </summary>
	/// <param name="emptyCollection">An empty <see cref="GalleryBoCollection"/> object to populate with the list of galleries in the current
	/// application. This parameter is required because the library that implements this interface does not have
	/// the ability to directly instantiate any object that implements <see cref="GalleryBoCollection"/>.</param>
	/// <returns>
	/// Returns an <see cref="GalleryBoCollection"/> representing the galleries in the current application. The returned object is the
	/// same object in memory as the <paramref name="emptyCollection"/> parameter.
	/// </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="emptyCollection" /> is null.</exception>		/// 
	private static GalleryBoCollection loadGalleries(GalleryBoCollection emptyCollection){
		if (emptyCollection == null)
			throw new ArgumentNullException("emptyCollection");

		if (!emptyCollection.isEmpty()){
			emptyCollection.clear();
		}

		//var galleries = from i in ctx.Galleries where i.GalleryId > Long.MIN_VALUE select i;
		//List<Gallery> galleries = galleryManager.getAll();
		Searchable searchable = Searchable.newSearchable();
		searchable.addSearchFilter("isTemplate", SearchOperator.eq, false);
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		List<Gallery> galleries = galleryManager.findAll(searchable);
		GalleryMappingManager galleryMappingManager = SpringContextHolder.getBean(GalleryMappingManager.class);
		List<GalleryMapping> gallerieMappings = galleryMappingManager.getAll();

		for (Gallery gallery : galleries){
			GalleryBo g = emptyCollection.createEmptyGalleryBoInstance();

			g.setGalleryId(gallery.getId());
			g.setName(gallery.getName());
			g.setDescription(gallery.getDescription());
			g.setCreationDate(gallery.getDateAdded());
			g.setAlbums(flattenGallery(gallery.getId()));
			g.setUsers(gallerieMappings.stream().filter(gm->gm.getGallery().getId() == gallery.getId() && gm.getUser() != null).map(gm->gm.getUser().getId()).collect(Collectors.toList()));
			g.setOrganizations(gallerieMappings.stream().filter(gm->gm.getGallery().getId() == gallery.getId() && gm.getOrganization() != null).map(gm->gm.getOrganization().getId()).collect(Collectors.toList()));

			emptyCollection.add(g);
		}

		return emptyCollection;
	}
	
	public static List<Long> loadOrganizationGalleries(List<String> oCodes, boolean ignoreCase) throws InvalidGalleryException, InvalidMDSRoleException{
		List<Long> gIds = Lists.newArrayList();
		List<Long> oIds = UserUtils.getOrganizationIds(oCodes, ignoreCase);
		GalleryBoCollection galleries = CMUtils.loadGalleries();
		for(long oid : oIds) {
			for(GalleryBo gallery : galleries) {
				if (gallery.getOrganizations().contains(oid)) {
					gIds.add(gallery.getGalleryId());
				}
			}
		}
		
		return gIds;
	}
	public static GalleryBoCollection loadLoginUserGalleries() throws InvalidGalleryException, InvalidMDSRoleException{
		UserAccount user = UserUtils.getUser();
	
		if (user == null || user.isSystem())
			return loadGalleries();
		else {
			return loadLoginUserGalleries(user);
		}
	}
	
	public static GalleryBoCollection loadLoginUserGalleries(UserAccount user) throws InvalidGalleryException, InvalidMDSRoleException{
		GalleryBoCollection all = loadGalleries();
		GalleryBoCollection galleries = new GalleryBoCollection();
		for(GalleryBo gallery : all) {
			if (gallery.getUsers() != null && gallery.getUsers().contains(user.getId())) {
				galleries.add(gallery);
			}
			
			if (user.isRoleType(RoleType.oa) && user.getOrganizationId() != Long.MIN_VALUE) {
				if (gallery.getOrganizations() != null && gallery.getOrganizations().contains(user.getOrganizationId())) {
					galleries.add(gallery);
				}
			}
		}
			
		MDSRoleCollection roles = RoleUtils.getMDSRolesForUser(user.getUsername());
		for(MDSRole role : roles) {
			for(GalleryBo gallery : role.getGalleries()) {
				if (!galleries.contains(gallery)) {
					galleries.add(gallery);
				}
			}
		}
		
		return galleries;			
	}

	/// <summary>
	/// A simple class that holds an album's ID and its parent ID. Used by the <see cref="FlattenGalleryBo" /> and 
	/// <see cref="FlattenAlbum" /> functions.
	/// </summary>
	private class AlbumTuple{
		public AlbumTuple(long albumId, Long albumParentId) {
			this.AlbumId = albumId;
			this.AlbumParentId = albumParentId;
		}
		
		public long AlbumId;
		public Long AlbumParentId;
	}

	/// <summary>
	/// Flatten the gallery into a dictionary of album IDs (key) and the flattened list of all albums each album
	/// contains (value).
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>An instance of Dictionary&lt;int, List&lt;int&gt;&gt;.</returns>
	private static Map<Long, List<Long>> flattenGallery(long galleryId)	{
		Map<Long, List<Long>> flatIds = new HashMap<Long, List<Long>>();

		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		Map<Long, List<Album>> albums;
		List<Album> albumDtos = albumManager.getAlbums(galleryId);
		albums = albumDtos.stream().collect(Collectors.groupingBy(a->a.getPId()));
		if (!albums.isEmpty()) {
			//List<Map<Long, Long>> albumsMap = albumManager.findAlbumMap(galleryId);
			//albums = albumsMap.stream().collect(Collectors.groupingBy(a->a.get[]))
			/*, a->{
				List<AlbumTuple> albumTuples = Lists.newArrayList();
				albumTuples.add(new AlbumTuple(a.getId(), a.getParentId()));
				return albumTuples;
			}));*/
	
	
			Long rootAlbumParentId = 0L;
	
			// Get a reference to the root album
			Album rootAlbum = albums.get(rootAlbumParentId).stream().findFirst().orElse(null);
			if (rootAlbum != null){
				// Add the root album to our flat list and set up the child list
				flatIds.put(rootAlbum.getId(), Lists.newArrayList((new Long[] { rootAlbum.getId() })));
	
				// Now add the children of the root album
				List<Album> albumTuples= albums.get(rootAlbum.getId());
				if (albumTuples != null) {
					for(Album albumTuple : albumTuples){
						flattenAlbum(albumTuple, albums, flatIds, Lists.newArrayList(new Long[] { rootAlbum.getId() }));
					}
				}
			}
		}

		return flatIds;
	}

	/// <summary>
	/// Add the <paramref name="album" /> to all albums in <paramref name="flatIds" /> where it is a child. Recursively
	/// process the album's children. The end result is a dictionary of album IDs (key) and the flattened list of all albums 
	/// each album contains (value).
	/// </summary>
	/// <param name="album">The album to flatten. This object is not modified.</param>
	/// <param name="hierarchicalIds">A lookup list where all albums (value) with a particular parent ID (key) can be quickly 
	/// found. This object is not modified.</param>
	/// <param name="flatIds">The flattened list of albums and their child albums. The <paramref name="album" /> and its
	/// children are added to this list.</param>
	/// <param name="currentAlbumFlatIds">The current hierarchy of album IDs we are processing. The function uses this to 
	/// know which items in <paramref name="flatIds" /> to update for each album.</param>
	private static void flattenAlbum(Album album, Map<Long, List<Album>> hierarchicalIds, Map<Long, List<Long>> flatIds, List<Long> currentAlbumFlatIds){
		// First time we get here, ID=2, ParentId=1
		flatIds.put(album.getId(), Lists.newArrayList(new Long[] { album.getId() }));

		// For each album in the current hierarchy, find its match in flatIds and add the album to its list.
		for(long currentAlbumFlatId : currentAlbumFlatIds){
			flatIds.get(currentAlbumFlatId).add(album.getId());
		}

		// Now add this album to the list so it will get updated when any children are processed.
		currentAlbumFlatIds.add(album.getId());

		List<Album> albumTuples= hierarchicalIds.get(album.getId());
		if (albumTuples != null && !albumTuples.isEmpty()) {
			for (Album albumTuple : albumTuples){
				flattenAlbum(albumTuple, hierarchicalIds, flatIds, new ArrayList<Long>(currentAlbumFlatIds));
			}
		}
	}



	/// <overloads>
	///		Loads the gallery settings for the gallery specified by <paramref name = "galleryId" />.
	/// </overloads>
	/// <summary>
	/// Loads a read-only instance of gallery settings for the gallery specified by <paramref name = "galleryId" />. Automatically 
	///		creates the gallery and	gallery settings if the data is not found in the data store. Guaranteed to not return null, except 
	///		for when <paramref name = "galleryId" /> is <see cref="Long.MIN_VALUE" />, in which case it throws an <see cref="ArgumentOutOfRangeException" />.
	///		The returned value is a static instance that is shared across threads, so it should be used only for read-only access. Use
	///		a different overload of this method to return a writeable copy of the instance. Calling this method is the same as calling
	///		the overloaded method with the isWritable parameter set to false.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns a read-only instance of <see cref="GallerySettings" />containing  the gallery settings for the gallery specified by 
	/// <paramref name = "galleryId" />. This is a reference to a static variable that may be shared across threads.</returns>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when the gallery ID is <see cref="Long.MIN_VALUE" />.</exception>
	public static GallerySettings loadGallerySetting(long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if (galleryId == Long.MIN_VALUE){
			throw new ArgumentOutOfRangeException("galleryId", MessageFormat.format("The gallery ID must be a valid ID. Instead, the value passed was {0}.", galleryId));
		}

		GallerySettingsCollection gallerySettings = loadGallerySettings();

		GallerySettings gs = gallerySettings.findByGalleryId(galleryId);

		if (gs == null || (!gs.getIsInitialized()))
		{
			// There isn't an item for the requested gallery ID, *OR* there is an item but it hasn't been initialized (this
			// can happen when an error occurs during initialization, such as a CannotWriteToDirectoryException occurring when checking
			// the content object path).

			// If we didn't find a gallery, create it.
			if (gs == null)		{
				GalleryBo gallery = createGalleryInstance();
				gallery.setGalleryId(galleryId);

				gallery.configure();

				_galleries.clear();

				// Need to clear the MDS System roles so that they are reloaded from the data store, which should now include sys admin
				// permission to the new gallery.
				HelperFunctions.purgeCache();
			}

			// Reload the data from the data store.
			_gallerySettings.clear();
			gallerySettings = loadGallerySettings();

			gs = gallerySettings.findByGalleryId(galleryId);

			if (gs == null){
				throw new BusinessException(MessageFormat.format("Factory.LoadGalleryBoSetting() should have created gallery setting records for gallery {0}, but it has not.", galleryId));
			}
		}

		return gs;
	}

	/// <summary>
	/// Loads the gallery settings for the gallery specified by <paramref name="galleryId"/>. When <paramref name="isWritable"/>
	/// is <c>true</c>, then return a unique instance that is not shared across threads, thus creating a thread-safe object that can
	/// be updated and persisted back to the data store. Calling this method with <paramref name="isWritable"/> set to <c>false</c>
	/// is the same as calling the overload of this method that takes only a gallery ID. Guaranteed to not return null, except for when <paramref name="galleryId"/>
	/// is <see cref="Long.MIN_VALUE"/>, in which case it throws an <see cref="ArgumentOutOfRangeException"/>.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="isWritable">When set to <c>true</c> then return a unique instance that is not shared across threads.</param>
	/// <returns>
	/// Returns a writeable instance of <see cref="GallerySettings"/>containing  the gallery settings for the gallery specified by
	/// <paramref name="galleryId"/>.
	/// </returns>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when the gallery ID is <see cref="Long.MIN_VALUE"/>.</exception>
	public static GallerySettings loadGallerySetting(long galleryId, boolean isWritable) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (galleryId == Long.MIN_VALUE){
			throw new ArgumentOutOfRangeException("galleryId", MessageFormat.format("The gallery ID must be a valid ID. Instead, the value passed was {0}.", galleryId));
		}

		if (isWritable)		{
			GallerySettings gallerySettings = retrieveGallerySettingsFromDataStore().findByGalleryId(galleryId);
			gallerySettings.setIsWritable(true);
			return gallerySettings;
		}else{
			return loadGallerySetting(galleryId);
		}
	}

	/// <summary>
	/// Loads the settings for all galleries in the application. Guaranteed to not return null.
	/// </summary>
	/// <returns>Returns an <see cref="GallerySettingsCollection" /> containing settings for all galleries in the application.</returns>
	public static GallerySettingsCollection loadGallerySettings() throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		synchronized (_gallerySettings)
		{
			if (_gallerySettings.isEmpty()){
				// Ensure that writes related to instantiation are flushed.
				//Thread.MemoryBarrier();

				_gallerySettings.addRange(retrieveGallerySettingsFromDataStore());
			}
		}

		return _gallerySettings;
	}
	
	/// <summary>
	/// Persist the current gallery settings to the data store. Automatically clears and then reloads the gallery settings
	/// from the data store.
	/// </summary>
	/// <overload>
	/// Persist the current gallery settings to the data store.
	/// </overload>
	public static void saveGallerySettings(GallerySettings gallerySettings)	{
		GallerySettingManager gallerySettingManager = SpringContextHolder.getBean(GallerySettingManager.class);
		
		gallerySettingManager.saveGallerySettings(gallerySettings);
	}
	
	/// <summary>
	/// Retrieves the gallery settings from the data store for all galleries.
	/// </summary>
	/// <returns>Returns an <see cref="IGallerySettingsCollection" /> containing the settings for all galleries.</returns>
	public static GallerySettingsCollection retrieveGallerySettingsFromDataStore() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		  GallerySettingsCollection gallerySettings = new GallerySettingsCollection();
		  GallerySettings gs = null;
		  Long prevGalleryId = null;
		
		// Loop through each gallery setting and assign to the relevant property. When we encounter a record with a new gallery ID, 
		// automatically create a new GallerySetting instance and start populating that one. When we are done with the loop we will
		// have created one GallerySetting instance for each gallery and fully populated each one.
		GallerySettingManager gallerySettingManager = SpringContextHolder.getBean(GallerySettingManager.class);

 		Searchable searchable = Searchable.newSearchable();
		searchable.addSort(Direction.ASC, "gallery.id");
		List<GallerySetting> gsDtos = gallerySettingManager.findAll(searchable);
		for (GallerySetting gsDto : gsDtos) {
			//#region Check for new gallery

			if (prevGalleryId == null || (gsDto.getGallery().getId() != prevGalleryId))	{
				// We have encountered settings for a new gallery. Initialize the previous one, then create a new object and add it to our collection.
				if ((gs != null) && (!gs.getIsInitialized())){
					gs.initialize();
				}

				gs = new GallerySettings(gsDto.getGallery().getId(), gsDto.getGallery().isIsTemplate());

				gallerySettings.add(gs);

				prevGalleryId = gsDto.getGallery().getId();
			}

			//#endregion

			//#region Assign property
			String settingName = StringUtils.uncapitalize(gsDto.getSettingName().trim());
			Field f = FieldUtils.getDeclaredField(gs.getClass(), settingName, true);
			if (f==null)
			  continue;

			String settingValue = gsDto.getSettingValue();
			// Get param type and type cast
			Class<?> valType = f.getType();
			if (valType == MetadataDefinitionCollection.class) {
				List<MetadataDefinition> metaDefs = JsonMapper.getInstance().fromJson(settingValue.replace("\\\\", "\\"), JsonMapper.getInstance().createCollectionType(List.class, MetadataDefinition.class));
				if (metaDefs != null) {
					for(MetadataDefinition md : metaDefs) {
						gs.getMetadataDisplaySettings().add(md);
					}
				}
			}else if (valType == ContentEncoderSettingsCollection.class) {
				String[] mediaEncodings = StringUtils.splitByWholeSeparator(settingValue.replace("\\\\", "\\").replace("\"\"", "\""),  "~~"); // }, StringSplitOptions.None
				int seq = 0;
				for (String mediaEncStr : mediaEncodings){
					// Each string item is double-pipe-delimited. Ex: ".avi||.mp4||-i {SourceFilePath} {DestinationFilePath}"
					String[] mediaEncoderItems = StringUtils.splitByWholeSeparatorPreserveAllTokens(mediaEncStr,  "||", 3);

					if (mediaEncoderItems.length != 3){
						throw new ArgumentOutOfRangeException(MessageFormat.format("GallerySetting.RetrieveGallerySettingsFromDataStore cannot parse the media encoder definitions for property {0}. Encountered invalid string: '{1}'", gsDto.getSettingName(), mediaEncStr));
					}

					gs.getContentEncoderSettings().add(new ContentEncoderSettings(mediaEncoderItems[0], mediaEncoderItems[1], mediaEncoderItems[2], seq));
					seq++;
				}
				gs.getContentEncoderSettings().validate();
				
			}else if (valType == MetadataItemName.class){
				GallerySettings.assignMetadataItemNameProperty(gs, settingName, settingValue);
			}else if (valType == ContentObjectTransitionType.class){
				GallerySettings.assignContentObjectTransitionTypeProperty(gs, settingName, settingValue);
			}else if (valType == SlideShowType.class){
				GallerySettings.assignSlideShowTypeProperty(gs, settingName, settingValue);
			}else if (valType == ContentAlignment.class){
				GallerySettings.assignContentAlignmentProperty(gs, settingName, settingValue);
			}else if (valType == PagerPosition.class){
				GallerySettings.assignPagerPositionProperty(gs, settingName, settingValue);
			}else if (valType == UserAccountCollection.class){
				//AssignUserAccountsProperty(gs, prop, gallerySettingDto.SettingValue.Split(new char[] { ',' }, StringSplitOptions.RemoveEmptyEntries));
			}else if (valType.isArray()){
				if (!StringUtils.isBlank(settingValue)) {
					Reflections.invokeSetter(gs, settingName, HelperFunctions.toListFromCommaDelimited(settingValue).toArray(new String[0]));
				}
			}else {
				Optional<Object> val = StringUtils.toValue(settingValue, valType, null);
				if (val.isPresent()) {
					Reflections.invokeSetter(gs, settingName, val.get());
				}
			}

			//#endregion
		}
		
		// The last gallery setting will not be initialized by the previous loop, so when we finish processing the records and
		// get to this point, do one more initialization. It is expected that gs will never be null or initialized, but we
		// check anyway just to be safe.
		if ((gs != null) && (!gs.getIsInitialized())){
			gs.initialize();
		}

		return gallerySettings;
	}

	/// <summary>
	/// Loads the settings for all galleries in the application. Guaranteed to not return null.
	/// </summary>
	/// <returns>Returns an <see cref="GallerySettingsCollection" /> containing settings for all galleries in the application.</returns>
	public synchronized static GalleryControlSettingsCollection loadGalleryControlSettings()	{
		if (_galleryControlSettings.size() == 0)		{
			// Ensure that writes related to instantiation are flushed.
			_galleryControlSettings.addRange(retrieveGalleryControlSettingsFromDataStore());
		}

		return _galleryControlSettings;
	}

	/// <summary>
	/// Clears the in-memory copy of the current set of gallery control settings. This will force a database retrieval the next time
	/// they are requested.
	/// </summary>
	public static void clearGalleryCache()	{
		_galleries.clear();
	}

	/// <summary>
	/// Clears the in-memory copy of the current set of gallery control settings. This will force a database retrieval the next time
	/// they are requested.
	/// </summary>
	public static void clearGalleryControlSettingsCache()	{
		_galleryControlSettings.clear();
	}

	/// <summary>
	/// Clears the in-memory copy of the current set of watermarks.
	/// </summary>
	public static void clearWatermarkCache()	{
		_watermarks.clear();
	}

	/// <overloads>Loads the gallery control settings for the specified <paramref name="controlId"/>.</overloads>
	/// <summary>
	/// Loads the gallery control settings for the specified <paramref name="controlId"/>.
	/// </summary>
	/// <param name="controlId">The value that uniquely identifies the control containing the gallery. Example: "Default.aspx|mds"</param>
	/// <returns>
	/// Returns an instance of <see cref="GalleryControlSettings"/>containing  the gallery control settings for the gallery 
	/// control specified by <paramref name="controlId"/>.
	/// </returns>
	public static GalleryControlSettings loadGalleryControlSetting(String controlId)	{
		return loadGalleryControlSetting(controlId, false);
	}

	/// <summary>
	/// Loads the gallery control settings for the specified <paramref name="controlId"/>. When <paramref name="isWritable"/>
	/// is <c>true</c>, then return a unique instance that is not shared across threads, thus creating a thread-safe object that can
	/// be updated and persisted back to the data store. Calling this method with <paramref name="isWritable"/> set to <c>false</c>
	/// is the same as calling the overload of this method that takes only a control ID. Guaranteed to not return null.
	/// </summary>
	/// <param name="controlId">The value that uniquely identifies the control containing the gallery. Example: "Default.aspx|mds"</param>
	/// <param name="isWritable">When set to <c>true</c> then return a unique instance that is not shared across threads.</param>
	/// <returns>
	/// Returns a writeable instance of <see cref="GalleryControlSettings"/>containing  the gallery control settings for the gallery 
	/// control specified by <paramref name="controlId"/>.
	/// </returns>
	public static GalleryControlSettings loadGalleryControlSetting(String controlId, boolean isWritable)	{
		GalleryControlSettings galleryControlSettings;

		if (isWritable)	{
			galleryControlSettings = retrieveGalleryControlSettingsFromDataStore().findByControlId(controlId);
		}else{
			galleryControlSettings = loadGalleryControlSettings().findByControlId(controlId);
		}

		if (galleryControlSettings == null)	{
			galleryControlSettings = new GalleryControlSettings(Long.MIN_VALUE, controlId);
		}

		return galleryControlSettings;
	}
	
	/// <summary>
	/// Persist the current gallery control settings to the data store.
	/// </summary>
	public static void saveGalleryControlSettings(GalleryControlSettings galleryControlSettings){
		GalleryControlSettingManager galleryControlSettingManager = SpringContextHolder.getBean(GalleryControlSettingManager.class);
		
		galleryControlSettingManager.saveGalleryControlSetting(galleryControlSettings);
				
		//galleryControlSettingManager
	
	   // Clear the settings stored in static variables so they are retrieved from the data store during the next access.
	   //Factory.clearGalleryControlSettingsCache();
	}
		
	/// <summary>
	/// Retrieves the gallery control settings from the data store for all controls containing galleries.
	/// </summary>
	/// <returns>Returns an <see cref="GalleryControlSettingsCollection" /> containing the settings for all controls containing galleries.</returns>
	public static GalleryControlSettingsCollection retrieveGalleryControlSettingsFromDataStore(){
	  GalleryControlSettingManager galleryControlSettingManager = SpringContextHolder.getBean(GalleryControlSettingManager.class);
		
	  GalleryControlSettingsCollection gallerySettings = new GalleryControlSettingsCollection();
	  GalleryControlSettings gs = null;
	  String prevControlId = null;
	
	  // Loop through each gallery control setting and assign to the relevant property. When we encounter a record with a new control ID, 
	  // automatically create a new GalleryControlSetting instance and start populating that one. When we are done with the loop we will
	  // have created one GalleryControlSetting instance for each control that contains a gallery.
	
	  // SQL:
	  // SELECT
	  //  GalleryControlSettingId, ControlId, SettingName, SettingValue
	  // FROM [gs_GalleryControlSetting]
	  // ORDER BY ControlId;
	  List<GalleryControlSetting> gcsDtos = galleryControlSettingManager.getAll();
	  for (GalleryControlSetting gcsDto : gcsDtos) {
		  //#region Check for new gallery
		  String currControlId = gcsDto.getControlId().trim();
		  if (StringUtils.isBlank(prevControlId) || (!currControlId.equals(prevControlId))) {
			// We have encountered settings for a new gallery. Create a new object and add it to our collection.
			gs = new GalleryControlSettings(gcsDto.getId(), currControlId);
			gallerySettings.Add(gs);
	
			prevControlId = currControlId;
		  }
	
		  //#endregion
	
		  //#region Assign property
		  String settingName = StringUtils.uncapitalize(gcsDto.getSettingName().trim());
		  Field f = FieldUtils.getDeclaredField(gs.getClass(), settingName, true);
		  if (f==null)
			  continue;
		  
		  String settingValue = gcsDto.getSettingValue();
		// Get param type and type cast
			Class<?> valType = f.getType();
			//log.debug("Import value type: ["+i+","+column+"] " + valType);
			
			Object val = null;
			try {
				if (valType == String.class){
					val = settingValue;
				}else if (valType == Integer.class){
					val = Double.valueOf(settingValue).intValue();
				}else if (valType == Long.class){
					val = Double.valueOf(settingValue).longValue();
				}else if (valType == Double.class){
					val = Double.valueOf(settingValue);
				}else if (valType == Float.class){
					val = Float.valueOf(settingValue);
				}else if (valType == Boolean.class || valType == boolean.class){
					val = Boolean.parseBoolean(settingValue);	
				}else if (valType == Date.class){
					val = DateUtils.parseDate(settingValue);
				}else if (valType.isEnum()){
					//val = Enum.valueOf((Class<T>) valType, val.toString());
					if (!StringUtils.isBlank(settingValue)) {
						val = valType.getMethod("valueOf", String.class).invoke(null, settingValue);
					}else {
						val = null;
					}
				}
			} catch (Exception ex) {
				val = null;
			}
			Reflections.invokeSetter(gs, settingName, val);
	
		  //#endregion
		}
	
	  	return gallerySettings;
	}

	/// <summary>
	/// Gets the watermark instance for the specified <paramref name="galleryId" />.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns a <see cref="Watermark" /> instance for the specified <paramref name="galleryId" />.</returns>
	public static Watermark getWatermarkInstance(long galleryId)	{
		if (galleryId == Long.MIN_VALUE){
			throw new ArgumentOutOfRangeException("galleryId", MessageFormat.format("The gallery ID must be a valid ID. Instead, the value passed was {0}.", galleryId));
		}

		Watermark watermark;

		if ((watermark = _watermarks.get(galleryId)) == null){
			synchronized (_watermarks){
				if ((watermark = _watermarks.get(galleryId)) == null){
					// A watermark object for the gallery was not found. Create it and add it to the dictionary.
					//Watermark tempWatermark = AppSettings.getInstance().getLicense().IsInReducedFunctionalityMode ? Watermark.GetReducedFunctionalityModeWatermark(galleryId) : Watermark.GetUserSpecifiedWatermark(galleryId);

					// Ensure that writes related to instantiation are flushed.
					//Thread.MemoryBarrier();

					//_watermarks.add(galleryId, tempWatermark);

					//watermark = tempWatermark;
				}
			}
		}

		return watermark;
	}

	//#endregion

	//#region General

	/// <summary>
	/// Gets an instance of the HTML validator.
	/// </summary>
	/// <param name="html">The HTML to pass to the HTML validator.</param>
	/// <param name="galleryId">The gallery ID. This is used to look up the appropriate configuration values for the gallery.</param>
	/// <returns>Returns an instance of the HTML validator.</returns>
	/*public static IHtmlValidator GetHtmlValidator(String html, int galleryId)
	{
		return HtmlValidator.Create(html, galleryId);
	}*/

	/// <summary>
	/// Retrieves a singleton object that represents the current state of a synchronization in the specified gallery. Each gallery uses the
	/// same instance, so any callers must use appropriate locking when updating the object. Guaranteed to not return null.
	/// Note that the properties are NOT updated with the latest values from the data store; to do this call 
	/// <see cref="SynchronizationStatus.RefreshFromDataStore" />.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns an instance of <see cref="SynchronizationStatus" /> that represents the current state of a 
	/// synchronization in a particular gallery.</returns>
	public static SynchronizationStatus loadSynchronizationStatus(long galleryId){
		SynchronizationStatus syncStatus;

		synchronized (_sharedLock){
			if ((syncStatus = _syncStatuses.get(galleryId)) == null){
				// There is no item matching the desired gallery ID. Create a new one and add to the dictionary.
				syncStatus = new SynchronizationStatus(galleryId);

				if (!_syncStatuses.containsKey(galleryId)){
					_syncStatuses.put(galleryId, syncStatus);
				}
			}
		}

		return syncStatus;
	}
	
	/// <summary>
	/// Retrieve the most recent synchronization information from the data store.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// Returns an <see cref="SynchronizationStatus"/> object with the most recent synchronization information from the data store.
	/// </returns>
	public static SynchronizationStatus getFromDataStore(SynchronizationStatus synchronizationStatus) throws RecordExistsException{
		SynchronizeManager synchronizeManager = SpringContextHolder.getBean(SynchronizeManager.class);
		List<Synchronize> syncDtos = synchronizeManager.getSynchronizes(synchronizationStatus.getGalleryId());
		Synchronize sDto = syncDtos.isEmpty() ? null : syncDtos.get(0);
		
		if (sDto == null){
			GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
			
			sDto = new Synchronize();
			sDto.setGallery(galleryManager.get(synchronizationStatus.getGalleryId()));
			sDto.setSynchId(synchronizationStatus.getSynchId());
			sDto.setSynchState(synchronizationStatus.getStatus());
			sDto.setTotalFiles(synchronizationStatus.getTotalFileCount());
			sDto.setCurrentFileIndex(synchronizationStatus.getCurrentFileIndex());
			sDto = synchronizeManager.saveSynchronize(sDto);
		}
	
		return new SynchronizationStatus(synchronizationStatus.getGalleryId(), sDto.getSynchId(), sDto.getSynchState(), sDto.getTotalFiles()
				, StringUtils.EMPTY, sDto.getCurrentFileIndex(), StringUtils.EMPTY);
	}
	
	/// <summary>
	/// Deletes the synchronization record belonging to the current gallery. When a sync is initiated it will be created.
	/// </summary>
	public static void configureSyncTable(long galleryId){
		SynchronizeManager synchronizeManager = SpringContextHolder.getBean(SynchronizeManager.class);
		List<Synchronize> syncDtos = synchronizeManager.getSynchronizes(galleryId);
		synchronizeManager.remove(syncDtos);
	}
	
	/// <summary>
	/// Persist the current state of this instance to the data store.
	/// </summary>
	public static void saveSynchronizationStatus(SynchronizationStatus synchronizationStatus) throws SynchronizationInProgressException, RecordExistsException{
		SynchronizeManager synchronizeManager = SpringContextHolder.getBean(SynchronizeManager.class);
		List<Synchronize> syncDtos = synchronizeManager.getSynchronizes(synchronizationStatus.getGalleryId());
		Synchronize sDto = syncDtos.isEmpty() ? null : syncDtos.get(0);
		
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		if (sDto != null) {
			if ((!sDto.getSynchId().equalsIgnoreCase(synchronizationStatus.getSynchId())) 
					&& ((sDto.getSynchState() == SynchronizationState.SynchronizingFiles) || (sDto.getSynchState() == SynchronizationState.PersistingToDataStore)))	{
			  throw new SynchronizationInProgressException();
			}
		} else {
			  sDto = new Synchronize();
			  sDto.setGallery(galleryManager.get(synchronizationStatus.getGalleryId()));
		}
		  
		sDto.setSynchId(synchronizationStatus.getSynchId());
		sDto.setSynchState(synchronizationStatus.getStatus());
		sDto.setTotalFiles(synchronizationStatus.getTotalFileCount());
		sDto.setCurrentFileIndex(synchronizationStatus.getCurrentFileIndex());
		synchronizeManager.saveSynchronize(sDto);
	}

	/// <summary>
	/// Create a new <see cref="ContentTemplateBo" /> instance with properties set to default values.
	/// A valid <see cref="ContentTemplateBo.ContentTemplateId">ID</see> will be generated when the object is persisted to the data store 
	/// when saved. Guaranteed to not return null.
	/// </summary>
	/// <returns>Returns an <see cref="ContentTemplateBo" /> instance.</returns>
	public static ContentTemplateBo createEmptyContentTemplate(){
		return new ContentTemplateBo();
	}

	/// <summary>
	/// Gets a collection of the media templates from the data store. The items may be returned from a cache.
	/// Returns an empty collection if no items exist.
	/// </summary>
	/// <returns>Returns a <see cref="ContentTemplateBoCollection" /> representing the media templates in the current application.</returns>
	public static ContentTemplateBoCollection loadContentTemplates(){
		ContentTemplateBoCollection tmpl = (ContentTemplateBoCollection)CacheUtils.get(CacheItem.cm_contenttemplates);

		if (tmpl != null){
			return tmpl;
		}

		ContentTemplateManager contentTemplateManager = SpringContextHolder.getBean(ContentTemplateManager.class);
		// Nothing in the cache, so get from data store and add to cache.
		tmpl = contentTemplateManager.getContentTemplates();

		CacheUtils.put(CacheItem.cm_contenttemplates, tmpl);

		return tmpl;
	}

	/// <summary>
	/// Gets a collection of the media templates from the data store. The items may be returned from a cache.
	/// Returns an empty collection if no items exist.
	/// </summary>
	/// <returns>Returns a <see cref="ContentTemplateBoCollection" /> representing the media templates in the current application.</returns>
	public static MimeTypeBoCollection loadMimeTypes() throws InvalidGalleryException {
		return loadMimeTypes(Long.MIN_VALUE);
	}
	
	public static MimeTypeBoCollection loadMimeTypes(long galleryId) throws InvalidGalleryException{
		ConcurrentHashMap<Long, MimeTypeBoCollection> mimeTypesCache = (ConcurrentHashMap<Long, MimeTypeBoCollection>)CacheUtils.get(CacheItem.cm_mimetypes);

		MimeTypeBoCollection mimeTypes;

		if ((mimeTypesCache != null) && ((mimeTypes = mimeTypesCache.get(galleryId)) != null)){
			return mimeTypes;
		}

		// Nothing in the cache, so get from data store and add to cache.
		mimeTypes = MimeTypeBo.loadMimeTypes(galleryId);

		if (mimeTypesCache == null){
			mimeTypesCache = new ConcurrentHashMap<Long, MimeTypeBoCollection>();
		}

		synchronized (_sharedLock){
			if (!mimeTypesCache.containsKey(galleryId))
			{
				mimeTypesCache.put(galleryId, mimeTypes);

				CacheUtils.put(CacheItem.cm_mimetypes, mimeTypesCache);
			}
		}

		return mimeTypes;
	}

	/// <overloads>
	/// Loads a <see cref="MimeTypeBo" /> object corresponding to the extension of the specified file.
	/// </overloads>
	/// <summary>
	/// Loads a <see cref="MimeTypeBo" /> object corresponding to the extension of the specified <paramref name="filePath" />.
	/// The returned instance is not associated with a particular gallery (that is, <see cref="MimeTypeBo.GalleryId" /> is set 
	/// to <see cref="Long.MIN_VALUE" />) and the <see cref="MimeTypeBo.AllowAddToGalleryBo" /> property is <c>false</c>. If 
	/// no matching MIME type is found, this method returns null.
	/// </summary>
	/// <param name="filePath">A String representing the filename or the path to the file
	/// (e.g. "C:\mypics\myprettypony.jpg", "myprettypony.jpg"). It is not case sensitive.</param>
	/// <returns>
	/// Returns a <see cref="MimeTypeBo" /> instance corresponding to the specified filepath, or null if no matching MIME
	/// type is found.
	/// </returns>
	/// <exception cref="System.ArgumentException">Thrown if <paramref name="filePath" /> contains one or more of
	/// the invalid characters defined in <see cref="System.IO.Path.GetInvalidPathChars" />, or contains a wildcard character.</exception>
	public static MimeTypeBo loadMimeType(String filePath) throws InvalidGalleryException{
		return loadMimeType(Long.MIN_VALUE, filePath);
	}

	/// <summary>
	/// Loads a <see cref="MimeTypeBo"/> object corresponding to the specified <paramref name="galleryId" /> and extension 
	/// of the specified <paramref name="filePath"/>. When <paramref name="galleryId" /> is <see cref="Long.MIN_VALUE"/>, the 
	/// returned instance is not associated with a particular gallery (that is, <see cref="MimeTypeBo.GalleryId"/> is set
	/// to <see cref="Long.MIN_VALUE"/>) and the <see cref="MimeTypeBo.AllowAddToGalleryBo"/> property is <c>false</c>. When 
	/// <paramref name="galleryId" /> is specified, then the <see cref="MimeTypeBo.AllowAddToGalleryBo"/> property is set according
	/// to the gallery's configuration. If no matching MIME type is found, this method returns null.
	/// </summary>
	/// <param name="galleryId">The ID representing the gallery associated with the file stored at <paramref name="filePath" />.
	/// Specify <see cref="Long.MIN_VALUE"/> when the gallery is not known or relevant. Setting this parameter will cause the
	/// <see cref="MimeTypeBo.AllowAddToGalleryBo"/> property to be set according to the gallery's configuration.</param>
	/// <param name="filePath">A String representing the filename or the path to the file
	/// (e.g. "C:\mypics\myprettypony.jpg", "myprettypony.jpg"). It is not case sensitive.</param>
	/// <returns>
	/// Returns a <see cref="MimeTypeBo"/> instance corresponding to the specified <paramref name="galleryId" /> and extension 
	/// of the specified <paramref name="filePath"/>, or null if no matching MIME type is found.
	/// </returns>
	/// <exception cref="System.ArgumentException">Thrown if <paramref name="filePath"/> contains one or more of
	/// the invalid characters defined in <see cref="System.IO.Path.GetInvalidPathChars"/>, or contains a wildcard character.</exception>
	public static MimeTypeBo loadMimeType(long galleryId, String filePath) throws InvalidGalleryException{
		return loadMimeTypes(galleryId).find(FileMisc.getExt(filePath));
	}
	
	/// <summary>
	/// Updates the <paramref name="baseMimeTypes" /> with configuration data for the <paramref name="galleryId" />.
	/// Returns <c>true</c> when at least one record in the <see cref="MimeTypeGalleryDto" /> table exists for the 
	/// <paramref name="galleryId" />; otherwise returns <c>false</c>.
	/// </summary>
	/// <param name="baseMimeTypes">A collection of MIME types to be updated with gallery-specific data.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns <c>true</c> when at least one record in the <see cref="MimeTypeGalleryDto" /> table exists for the 
	/// <paramref name="galleryId" />; otherwise returns <c>false</c>.</returns>
	private static boolean configureMimeTypesForGallery(MimeTypeBoCollection baseMimeTypes, long galleryId)	{
		//MimeTypeBoCollection baseMimeTypes = LoadMimeTypes(Long.MIN_VALUE);
		//MimeTypeBoCollection newMimeTypes = new MimeTypeBoCollection();
		ContentTemplateBoCollection mediaTemplates = loadContentTemplates();

		boolean foundRows = false;
		MimeTypeGalleryManager mimeTypeGalleryManager = SpringContextHolder.getBean(MimeTypeGalleryManager.class);
		
		List<MimeTypeGallery> mtgDtos = mimeTypeGalleryManager.getMimeTypeGalleries(galleryId);
		for (MimeTypeGallery mtgDto : mtgDtos){
			foundRows = true;
			MimeTypeBo mimeType = baseMimeTypes.find(mtgDto.getMimeType().getFileExtension());

			if (mimeType == null){
				throw new BusinessException(MessageFormat.format("Could not find a MimeTypeBo with file extension \"{0}\" in the list of base MIME types.", mtgDto.getMimeType().getFileExtension()));
			}

			mimeType.setGalleryId(galleryId);
			mimeType.setMimeTypeGalleryId(mtgDto.getGallery().getId());
			mimeType.setAllowAddToGallery(mtgDto.isIsEnabled());

			// Populate the media template collection.
			mimeType.getContentTemplates().addRange(mediaTemplates.find(mimeType));

			// Validate the media templates. There may not be any, which is OK (for example, there isn't one defined for 'application/msword').
			// But if there *IS* one defined, there must be one with a browser ID of "default".
			if ((mimeType.getContentTemplates().size() > 0) && (mimeType.getContentTemplates().find("default") == null)){
				throw new BusinessException(MessageFormat.format("No default media template. Could not find a media template for MIME type \"{0}\" or \"{1}\" with browser ID = \"default\".", mimeType.getFullType(), mimeType.getMajorType().concat( "/*")));
			}
		}

		return foundRows;
	}
	
	/// <summary>
	/// Verify there is a MIME type/gallery mapping for the current gallery for every MIME type, creating any
	/// if necessary.
	/// </summary>
	public static void configureMimeTypeGalleryTable(long galleryId){
		List<String> defaultEnabledExtensions = null; //Lists.newArrayList(new String[] { ".jpg", ".jpeg" });
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		MimeTypeManager mimeTypeManager = SpringContextHolder.getBean(MimeTypeManager.class);
		MimeTypeGalleryManager mimeTypeGalleryManager = SpringContextHolder.getBean(MimeTypeGalleryManager.class);
		
		//Searchable searchable = Searchable.newSearchable();
		//searchable.addSearchFilter("gallery.id", SearchOperator.ne, galleryId);
		List<Long> mtypes = mimeTypeGalleryManager.getMimeTypeGalleries(galleryId).stream().map(m->m.getMimeType().getId()).collect(Collectors.toList());
		List<MimeType> mts = mimeTypeManager.getAll().stream().filter(mt->!mtypes.contains(mt.getId())).collect(Collectors.toList());
		if (!mts.isEmpty()) {
			Gallery gallery = galleryManager.get(galleryId);
			/*List<MimeType> mts = mimeTypeManager.getAll().stream()
					.filter(mt->mt.getMimeTypeGallerys().stream().allMatch(mtg->mtg.getGallery().getId() != gallery.getId())).collect(Collectors.toList());*/
	
			// Get MIME types that don't have a match in the MIME Type Gallery table   mtg->mtg.getGallery().getId() == galleryId
			List<MimeTypeGallery> mtgs = Lists.newArrayList();
			for (MimeType mtDto : mts){
				mtgs.add(new MimeTypeGallery(gallery, mtDto, (defaultEnabledExtensions == null || defaultEnabledExtensions.contains(mtDto.getFileExtension()))));
			}
			if (!mtgs.isEmpty()) {
				mimeTypeGalleryManager.save(mtgs);
			}
		}
	}

	
	/// <summary>
	/// Loads the collection of MIME types for the specified <paramref name="galleryId" /> from the data store.
	/// When <paramref name="galleryId" /> is <see cref="Long.MIN_VALUE" />, a generic collection that is not 
	/// specific to a particular gallery is returned.
	/// </summary>
	/// <param name="galleryId">The gallery ID. Specify <see cref="Long.MIN_VALUE" /> to retrieve a generic 
	/// collection that is not specific to a particular gallery.</param>
	/// <returns>Returns a <see cref="MimeTypeBoCollection" /> containing MIME types for the specified 
	/// <paramref name="galleryId" /></returns>
	public static MimeTypeBoCollection loadAndConfigureMimeTypes(long galleryId) throws InvalidGalleryException	{
		MimeTypeManager mimeTypeManager = SpringContextHolder.getBean(MimeTypeManager.class);
		
		MimeTypeBoCollection mimeTypes = mimeTypeManager.loadMimeTypesFromDataStore();

		if (galleryId == Long.MIN_VALUE){
			// User wants the master list. Load from data store and return (this also adds it to the static var for next time).
			return mimeTypes;
		}

		// User wants the MIME types for a specific gallery that we haven't yet loaded from disk. Do so now.
		if (configureMimeTypesForGallery(mimeTypes, galleryId))	{
			return mimeTypes;
		}

		// If we get here then no records existed in the data store for the gallery MIME types (gs_MimeTypeGallery). Create
		// the gallery, which will create these records while not harming any pre-existing records that may exist in other
		// tables such as gs_GallerySettings.
		loadGallery(galleryId).configure();

		// Note: If CreateGallery() fails to create records in gs_MimeTypeGallery, we will end up in an infinite loop.
		// But that should never happen, right?
		return loadMimeTypes(galleryId);
	}

	//#endregion

	//#region Profile Methods

	/// <summary>
	/// Retrieves the profile for the specified <paramref name="userName" />. The profile is 
	/// retrieved from the cache if it is there. If not, it is retrieved from the data store and
	/// added to the cache. Guaranteed to not return null.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <returns>Returns the profile for the specified <paramref name="userName" /></returns>
	public static UserProfile loadUserProfile(String userName)	{
		ConcurrentHashMap<String, UserProfile> profileCache = (ConcurrentHashMap<String, UserProfile>)CacheUtils.get(CacheItem.cm_profiles);

		UserProfile profile;
		if ((profileCache == null) || (profile = profileCache.get(userName)) == null){
			//profile = GetDataProvider().Profile_GetUserProfile(userName, new Factory());
			profile = retrieveFromDataStore(userName);

			// Add profile to cache.
			if (profileCache == null)
				profileCache = new ConcurrentHashMap<String, UserProfile>();

			synchronized (_sharedLock)
			{
				if (!profileCache.containsKey(userName))
				{
					profileCache.put(userName, profile);

					CacheUtils.put(CacheItem.cm_profiles, profileCache);
				}
			}
		}

		return profile;
	}
	
	/// <summary>
	/// Retrieves the profile for the specified <paramref name="userName" />. Guaranteed to not return null.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <returns>An instance of <see cref="UserProfile" />.</returns>
	public static UserProfile retrieveFromDataStore(String userName){
		UserProfile profile = new UserProfile();
		profile.UserName = userName;

		UserGalleryProfile gs = null;
		long prevGalleryId = Long.MIN_VALUE;
		UserGalleryProfileManager userGalleryProfileManager = SpringContextHolder.getBean(UserGalleryProfileManager.class);
		List<com.mds.aiotplayer.cm.model.UserGalleryProfile> userGalleryProfiles = userGalleryProfileManager.getUserGalleryProfiles(userName);

		for (com.mds.aiotplayer.cm.model.UserGalleryProfile profileDto : userGalleryProfiles){
			// Loop through each user profile setting and assign to the relevant property. When we encounter a record with a new gallery ID, 
			// automatically create a new UserGalleryProfile instance and start populating that one. When we are done with the loop we will
			// have created one UserGalleryProfile instance for each gallery the user has a profile for.

			//#region Check for application-wide profile setting

			if (profileDto.getGallery().isIsTemplate())	{
				// Profile items associated with the template gallery are application-wide and map to properties
				// on the UserProfile object.
				switch (profileDto.getSettingName().trim())
				{
					case UserProfile.ProfileNameEnableUserAlbum:
					case UserProfile.ProfileNameUserAlbumId:
						throw new DataException(MessageFormat.format("It is invalid for the profile setting '{0}' to be associated with a template gallery (Gallery ID {1}).", profileDto.getSettingName(), profileDto.getGallery().getId()));

					case UserProfile.ProfileNameAlbumProfiles:
						AlbumProfileCollection albumProfiles = JsonMapper.getInstance().fromJson(profileDto.getSettingValue(), AlbumProfileCollection.class);
						//JsonConvert.DeserializeObject<List<AlbumProfile>>(profileDto.SettingValue.Trim());

						if (albumProfiles != null){
							profile.getAlbumProfiles().addRange(albumProfiles.values());
						}

						break;

					case UserProfile.ProfileNameContentObjectProfiles:
						ContentObjectProfileCollection moProfiles = JsonMapper.getInstance().fromJson(profileDto.getSettingValue(), ContentObjectProfileCollection.class);
						//JsonConvert.DeserializeObject<List<ContentObjectProfile>>(profileDto.SettingValue.Trim());

						if (moProfiles != null)	{
							profile.getContentObjectProfiles().addRange(moProfiles.values());
						}

						break;
				}

				continue;
			}

			//#endregion

			//#region Check for new gallery

			long currGalleryId = profileDto.getGallery().getId();

			if ((gs == null) || (currGalleryId != prevGalleryId)){
				// We have encountered settings for a new user gallery profile. Create a new object and add it to our collection.
				gs = profile.getGalleryProfiles().createNewUserGalleryProfile(currGalleryId);

				profile.getGalleryProfiles().add(gs);

				prevGalleryId = currGalleryId;
			}

			//#endregion

			//#region Assign property

			// For each setting in the data store, find the matching property and assign the value to it.
			switch (profileDto.getSettingName().trim()){
				case UserProfile.ProfileNameEnableUserAlbum:
					gs.setEnableUserAlbum(Boolean.valueOf(profileDto.getSettingValue().trim()));
					break;

				case UserProfile.ProfileNameUserAlbumId:
					gs.setUserAlbumId(Long.valueOf(profileDto.getSettingValue().trim()));
					break;

				case UserProfile.ProfileNameAlbumProfiles:
				case UserProfile.ProfileNameContentObjectProfiles:
					throw new DataException(MessageFormat.format("It is invalid for the profile setting '{0}' to be associated with a non-template gallery (Gallery ID {1}).", profileDto.getSettingName(), profileDto.getGallery().getId()));
			}

			//#endregion
		}

		return profile;
	}

	/// <summary>
	/// Persist the user profile to the data store. The profile cache is cleared.
	/// </summary>
	/// <param name="userProfile">The user profile.</param>
	public static void saveUserProfile(UserProfile userProfile) throws JsonProcessingException	{
		if (userProfile == null)
			throw new ArgumentNullException("profile");
		
		UserGalleryProfileManager userGalleryProfileManager = SpringContextHolder.getBean(UserGalleryProfileManager.class);
		userGalleryProfileManager.saveUserGalleryProfile(userProfile, _templateGalleryId);

		CacheUtils.remove(CacheItem.cm_profiles);
	}

	/// <summary>
	/// Permanently delete the profile records for the specified <paramref name="userName" />.
	/// The profile cache is cleared.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	public static void deleteUserProfile(String userName){
		UserGalleryProfileManager userGalleryProfileManager = SpringContextHolder.getBean(UserGalleryProfileManager.class);
		userGalleryProfileManager.removeUserGalleryProfiles(userName);

		CacheUtils.remove(CacheItem.cm_profiles);
	}

	/// <summary>
	/// Permanently delete the profile records associated with the specified <paramref name="gallery" />.
	/// </summary>
	/// <param name="gallery">The gallery.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="gallery" /> is null.</exception>
	public static void deleteProfileForGallery(GalleryBo gallery)	{
		if (gallery == null)
			throw new ArgumentNullException("gallery");

		UserGalleryProfileManager userGalleryProfileManager = SpringContextHolder.getBean(UserGalleryProfileManager.class);
		userGalleryProfileManager.removeUserGalleryProfiles(gallery.getGalleryId());
	}

	/// <summary>
	/// Create a new <see cref="AlbumProfile" /> item from the specified parameters.
	/// </summary>
	/// <param name="albumId">The album ID.</param>
	/// <param name="sortByMetaName">Name of the metadata item to sort by.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort the album in ascending order.</param>
	/// <returns>An instance of <see cref="AlbumProfile" />.</returns>
	public static AlbumProfile createAlbumProfile(long albumId, MetadataItemName sortByMetaName, boolean sortAscending){
		return new AlbumProfile(albumId, sortByMetaName, sortAscending);
	}

	/// <summary>
	/// Create a new <see cref="ContentObjectProfile" /> item from the specified parameters.
	/// </summary>
	/// <param name="mediayObjectId">The mediay object ID.</param>
	/// <param name="rating">The rating a user has assigned to the content object.</param>
	/// <returns>An instance of <see cref="ContentObjectProfile" />.</returns>
	public static ContentObjectProfile createContentObjectProfile(long mediayObjectId, String rating){
		return new ContentObjectProfile(mediayObjectId, rating);
	}

	//#endregion

	//#region UI Template Methods

	/// <summary>
	/// Verify there are UI templates for the current gallery that match every UI template associated with
	/// the template gallery, creating any if necessary.
	/// </summary>
	public static void configureUiTemplateTable(long galleryId)	{
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		UiTemplateManager uiTemplateManager = SpringContextHolder.getBean(UiTemplateManager.class);
		
		Searchable searchable = Searchable.newSearchable();
		//searchable.addSearchFilter("name", SearchOperator.eq, "default");
		searchable.addSearchFilter("gallery.id", SearchOperator.in, new Long[] {getTemplateGalleryId(), galleryId});
		List<UiTemplate> uiTemplates = uiTemplateManager.findAll(searchable);
		List<UiTemplate> uiTmpls = uiTemplates.stream().filter(u->u.getGallery().getId() == getTemplateGalleryId()).collect(Collectors.toList());
		List<UiTemplate> uigTemplates = Lists.newArrayList() ;
		Gallery gallery = galleryManager.get(galleryId);
		for (UiTemplate uiTmpl : uiTmpls) {
			if (!uiTemplates.stream().anyMatch(u->u.getTemplateType()== uiTmpl.getTemplateType() && u.getGallery().getId() == galleryId && u.getName().equals(uiTmpl.getName()))) {
				UiTemplate newTemplate = new UiTemplate(
						uiTmpl.getTemplateType(),
						uiTmpl.getName(),
						gallery,
						uiTmpl.getDescription(),
						uiTmpl.getHtmlTemplate(),
						uiTmpl.getScriptTemplate());
				uigTemplates.add(newTemplate);
			}
		}

		if (!uigTemplates.isEmpty())
			uiTemplateManager.save(uigTemplates);
	}
	
	/// <summary>
	/// Verify there is a UI template/album mapping for the root album in the current gallery, creating them
	/// if necessary.
	/// </summary>
	/// <param name="rootAlbum">The root album.</param>
	public static void configureUiTemplateAlbumTable(Album rootAlbum, long galleryId){
		UiTemplateManager uiTemplateManager = SpringContextHolder.getBean(UiTemplateManager.class);
		
		Searchable searchable = Searchable.newSearchable();
		searchable.addSearchFilter("name", SearchOperator.eqi, "default");
		searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleryId);
		List<UiTemplate> uiTemplateAll = uiTemplateManager.findAll(searchable);
		/*rootAlbum = albumManager.get(rootAlbum.getId());
		for(UiTemplate uiTemplate : uiTemplateAll) {
			if (!rootAlbum.getUiTemplates().stream().anyMatch(t->t.getId()==uiTemplate.getId())) {
				rootAlbum.getUiTemplates().add(uiTemplate);
			}
		}*/
		//List<UiTemplate> uiTemplates = uiTemplateAll.stream().filter(u->!rootAlbum.getUiTemplates().stream().anyMatch(t->t.getId()==u.getId())).collect(Collectors.toList());
		//rootAlbum.getUiTemplates().addAll(uiTemplates);
		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		albumManager.saveUiTemplateAlbumTable(rootAlbum, uiTemplateAll);
		/*rootAlbum.setCurrentUser(Constants.SystemUserName);
		try {
			//albumManager.saveAlbum(rootAlbum);
			albumManager.saveUiTemplateAlbumTable(rootAlbum, uiTemplateAll);
		} catch (RecordExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*for (UiTemplate uiTemplate : uiTemplates) {
			if (rootAlbum.getUiTemplates().stream().anyMatch(u->u.getId() == uiTemplate.getId())) {
				
			}
		}*/
	}
		
	/// <summary>
	/// Gets a collection of all UI templates from the data store. The items may be returned from a cache.
	/// Returns an empty collection if no items exist.
	/// </summary>
	/// <returns>Returns a collection of all UI templates from the data store.</returns>
	public static UiTemplateBoCollection loadUiTemplates(){
		UiTemplateBoCollection tmpl = (UiTemplateBoCollection)CacheUtils.get(CacheItem.cm_uitemplates);

		if (tmpl != null){
			return tmpl;
		}

		// Nothing in the cache, so get from data store and add to cache.
		tmpl = getUiTemplates();

		CacheUtils.put(CacheItem.cm_uitemplates, tmpl);

		return tmpl;
	}
	
	/// <summary>
	/// Gets a collection of all UI templates from the data store. Returns an empty collection if no
	/// items exist.
	/// </summary>
	/// <returns>Returns a collection of all UI templates from the data store.</returns>
	public static UiTemplateBoCollection getUiTemplates(){
		UiTemplateManager uiTemplateManager = SpringContextHolder.getBean(UiTemplateManager.class);
		
		UiTemplateBoCollection tmpl = new UiTemplateBoCollection();
		uiTemplateManager.clear();
		List<UiTemplate> templates = uiTemplateManager.getAll();
		for (UiTemplate jDto : templates){
		  UiTemplateBo t = new UiTemplateBo();
		  t.UiTemplateId = jDto.getId();
		  t.TemplateType = jDto.getTemplateType();
		  t.GalleryId = jDto.getGallery().getId();
		  t.Name = jDto.getName();
		  t.Description = jDto.getDescription();
		  t.HtmlTemplate = jDto.getHtmlTemplate();
		  t.ScriptTemplate = jDto.getScriptTemplate();

		  t.RootAlbumIds.addRange(jDto.getAlbums().stream().map(a->a.getId()).collect(Collectors.toList()));

		  tmpl.add(t);
		}
		
		return tmpl;
	}
	
	/// <summary>
	/// Persist this UI template object to the data store.
	/// </summary>
	public static void saveUiTemplate(UiTemplateBo t){
		AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
		GalleryManager galleryManager = SpringContextHolder.getBean(GalleryManager.class);
		UiTemplateManager uiTemplateManager = SpringContextHolder.getBean(UiTemplateManager.class);
		
		UiTemplate jDto = uiTemplateManager.get(t.UiTemplateId);
		if (jDto == null)
			jDto = new UiTemplate();
		
		jDto.setGallery(galleryManager.get(t.GalleryId));
		jDto.addAlbums(albumManager.find(t.RootAlbumIds.toArray()));
		jDto.setTemplateType(t.TemplateType);
		jDto.setName(t.Name);
		jDto.setDescription(t.Description);
		jDto.setHtmlTemplate(t.HtmlTemplate);
		jDto.setScriptTemplate(t.ScriptTemplate);

		try {
			uiTemplateManager.saveUiTemplate(jDto);
		} catch (RecordExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CacheUtils.remove(CacheItem.cm_uitemplates);
	}

	/// <summary>
	/// Permanently delete the current UI template from the data store. This action cannot be undone.
	/// </summary>
	public static void deleteUiTemplate(UiTemplateBo t){
		UiTemplateManager uiTemplateManager = SpringContextHolder.getBean(UiTemplateManager.class);
		
		uiTemplateManager.remove(t.UiTemplateId);

		CacheUtils.remove(CacheItem.cm_uitemplates);
	}
	
	public static void deleteUiTemplates(long galleryId){
		//uiTemplateManager.remove(t.UiTemplateId);

		CacheUtils.remove(CacheItem.cm_uitemplates);
	}

	//#endregion
	
	//#region Content Queue Methods

	/// <summary>
	/// Gets a collection of all UI templates from the data store. The items may be returned from a cache.
	/// Returns an empty collection if no items exist.
	/// </summary>
	/// <returns>Returns a collection of all UI templates from the data store.</returns>
	public static List<ContentQueueItem> loadContentQueues(){
		// Nothing in the cache, so get from data store and add to cache.
		ContentQueueManager contentQueueManager = SpringContextHolder.getBean(ContentQueueManager.class);
		return ContentQueueItem.toContentQueueItems(contentQueueManager.getAll());
	}
		
	/// <summary>
	/// Persist this UI template object to the data store.
	/// </summary>
	public static void saveContentQueue(ContentQueueItem t){
		ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);
		ContentQueueManager contentQueueManager = SpringContextHolder.getBean(ContentQueueManager.class);
		try {
			contentQueueManager.saveContentQueue(t, contentObjectManager.get(t.ContentObjectId));
		} catch (RecordExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//CacheUtils.remove(CacheItem.cm_uitemplates);
	}

	/// <summary>
	/// Permanently delete the current UI template from the data store. This action cannot be undone.
	/// </summary>
	public static void deleteContentQueue(ContentQueueItem t){
		ContentQueueManager contentQueueManager = SpringContextHolder.getBean(ContentQueueManager.class);
		
		contentQueueManager.remove(t.ContentQueueId);

		//CacheUtils.remove(CacheItem.cm_uitemplates);
	}

	//#endregion

	//#region Private Static Methods

	/// <summary>
	/// Retrieve the specified album as a read-only instance. Child albums and content objects are not added. The album is 
	/// retrieved from the cache if it is there. If not, it is retrieved from the data store. Guaranteed to not return null.
	/// </summary>
	/// <param name="albumId">The <see cref="ContentObjectBo.Id">ID</see> that uniquely identifies the album to retrieve.</param>
	/// <returns>Returns the specified album as a read-only instance without child albums or content objects.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified <paramref name = "albumId" /> 
	/// is not found in the data store.</exception>
	private static AlbumBo retrieveAlbum(long albumId) throws InvalidAlbumException, InvalidGalleryException{
		ConcurrentHashMap<Long, AlbumBo> albumCache = (ConcurrentHashMap<Long, AlbumBo>)CacheUtils.get(CacheItem.cm_albums);

		AlbumBo album;
		if (albumCache != null)	{
			if ((album = albumCache.get(albumId)) == null){
				// The cache exists, but there is no item matching the desired album ID. Retrieve from data store and add to cache.
				album = retrieveAlbumFromDataStore(albumId);

				if (!albumCache.containsKey(albumId)){
					album.setIsWritable(false);
					albumCache.put(albumId, album);
					CacheUtils.put(CacheItem.cm_albums, albumCache);
				}
			}
		}else{
			// There is no cache item. Retrieve from data store and create cache item so it's there next time we want it.
			album = retrieveAlbumFromDataStore(albumId);
			album.setIsWritable(false);

			albumCache = new ConcurrentHashMap<Long, AlbumBo>();
			albumCache.put(albumId, album);

			CacheUtils.put(CacheItem.cm_albums, albumCache);

//#if DEBUG
			//Trace.WriteLine(MessageFormat.format("Album {0} added to cache. (AreChildrenInflated={1})", albumId, album.AreChildrenInflated));
//#endif
		}
		return album;
	}

	/// <summary>
	/// Retrieve the specified content object. It is retrieved from the cache if it is there. 
	/// If not, it is retrieved from the data store and then added to the cache.
	/// </summary>
	/// <param name="contentObjectId">The <see cref="ContentObjectBo.Id">ID</see> that uniquely identifies the content object to retrieve.</param>
	/// <returns>Returns the specified content object.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when
	/// an image is not found in the data store that matches the contentObjectId parameter and the current gallery.</exception>
	private static ContentObjectBo retrieveContentObject(long contentObjectId) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		return retrieveContentObject(contentObjectId, null);
	}

	/// <summary>
	/// Retrieve the specified content object. It is retrieved from the cache if it is there. 
	/// If not, it is retrieved from the data store and then added to the cache.
	/// </summary>
	/// <param name="contentObjectId">The ID that uniquely identifies the content object to retrieve.</param>
	/// <param name="parentAlbum">The album containing the content object specified by contentObjectId. Specify
	/// null if a reference to the album is not available, and it will be created based on the parent album
	/// specified in the data store.</param>
	/// <returns>Returns the specified content object.</returns>
	/// <exception cref="InvalidContentObjectException">Thrown when
	/// an image is not found in the data store that matches the contentObjectId parameter and the current gallery.</exception>
	private static ContentObjectBo retrieveContentObject(long contentObjectId, AlbumBo parentAlbum) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException	{
		ConcurrentHashMap<Long, ContentObjectBo> contentObjectCache = (ConcurrentHashMap<Long, ContentObjectBo>)CacheUtils.get(CacheItem.cm_contentobjects);

		ContentObjectBo contentObject;
		if (contentObjectCache != null)	{
			if ((contentObject = contentObjectCache.get(contentObjectId)) == null){
				// The cache exists, but there is no item matching the desired content object ID. Retrieve from data store and add to cache.
				contentObject = retrieveContentObjectFromDataStore(contentObjectId, parentAlbum);

				addToContentObjectCache(contentObject);
			}
		}else{
			// There is no cache item. Retrieve from data store and create cache item so it's there next time we want it.
			contentObject = retrieveContentObjectFromDataStore(contentObjectId, parentAlbum);

			addToContentObjectCache(contentObject);
		}

		return contentObject;
	}

	private static ContentObjectBo retrieveContentObjectFromDataStore(long contentObjectId, AlbumBo parentAlbum) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException{
		boolean includeMetadata = (parentAlbum == null || parentAlbum.getAllowMetadataLoading());

		//ContentObjectDto moDto = GetDataProvider().ContentObject_GetContentObjectById(contentObjectId, includeMetadata);
		ContentObject moDto = getContentObjectById(contentObjectId, includeMetadata);
		if (moDto == null){
			throw new InvalidContentObjectException(contentObjectId);
		}

		return getContentObjectFromDto(moDto, parentAlbum);
	}

	private static ContentObject getContentObjectById(long contentObjectId, boolean includeMetadata){
		try
		{
			ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);

			return contentObjectManager.get(contentObjectId);
		}catch(Exception e) {return null;}
		
	}

	/// <summary>
	/// Retrieve the specified album from the data store. Child albums and content objects are not added. Guaranteed to not return null.
	/// </summary>
	/// <param name="albumId">The ID that uniquely identifies the album to retrieve.</param>
	/// <returns>Returns the specified album without child albums or content objects.</returns>
	/// <exception cref="InvalidAlbumException">Thrown when an album with the specified album ID is not found in the data store.</exception>
	private static AlbumBo retrieveAlbumFromDataStore(long albumId) throws InvalidAlbumException, InvalidGalleryException	{
		AlbumBo album;

		try	{
			//album = GetAlbumFromDto(GetDataProvider().Album_GetAlbumById(albumId));
			AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
			
			album = getAlbumFromDto(albumManager.get(albumId));
		}catch (InvalidAlbumException e){
			// Throw a new exception instead of the original one, since now we know the album ID and we are able to pass
			// it to the exception constructor.
			throw new InvalidAlbumException(albumId);
		}

		// Since we've just loaded this object from the data store, set the corresponding property.
		album.setFullPhysicalPathOnDisk(album.getFullPhysicalPath());

		assert album.getThumbnailContentObjectId() > Long.MIN_VALUE : "The album's ThumbnailContentObjectId should have been assigned in this method.";

		return album;
	}

	private static void addChildObjects(AlbumBo album) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException{
		if (album == null)
			throw new ArgumentNullException("album");

		//#region Add child albums
		Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter("parent.id", SearchOperator.eq, album.getId());
        AlbumManager albumManager = SpringContextHolder.getBean(AlbumManager.class);
        List<Long> albumIds = albumManager.getPrimaryKeys(searchable);
		for(long albumId : albumIds){
			album.addContentObject(createAlbumInstance(albumId, album.getGalleryId()));
		}

		//#endregion

		//#region Add child content objects
		searchable = Searchable.newSearchable();
        searchable.addSearchFilter("album.id", SearchOperator.eq, album.getId());

        ContentObjectManager contentObjectManager = SpringContextHolder.getBean(ContentObjectManager.class);
        
		List<ContentObject> moDtos;
		if (album.getAllowMetadataLoading())
			moDtos = contentObjectManager.findAll(searchable);
		else
			moDtos = contentObjectManager.findAll(searchable);

		for (ContentObject moDto : moDtos){
			ContentObjectBo contentObject = getContentObjectFromDto(moDto, album);

			addToContentObjectCache(contentObject);

			album.addContentObject(contentObject);
		}

		//#endregion

		album.setAreChildrenInflated(true);
	}

	private static void inflateAlbumFromDto(AlbumBo album, Album albumDto) throws InvalidGalleryException{
		if (album == null)
			throw new ArgumentNullException("album");

		if (albumDto == null)
			throw new ArgumentNullException("albumDto");

		// A parent ID = null indicates the root album. Use Long.MIN_VALUE to send to Album constructor.
		long albumParentId = albumDto.getParentId() == null ? Long.MIN_VALUE : albumDto.getParentId();

		// Assign parent if it hasn't already been assigned.
		if ((album.getParent().getId() == Long.MIN_VALUE) && (albumParentId > Long.MIN_VALUE)){
			album.setParent(createAlbumInstance(albumParentId, albumDto.getGallery().getId()));
		}

		album.setGalleryId(albumDto.getGallery().getId());
		//album.setTitle(albumDto.getTitle());
		album.setDirectoryName(albumDto.getName());
		//album.setSummary(albumDto.getSummary());
		album.setSortByMetaName(albumDto.getSortByMetaName());
		album.setSortAscending(albumDto.isSortAscending());
		album.setSequence(albumDto.getSeq());
		album.setDateStart(albumDto.getDateStart());
		album.setDateEnd(albumDto.getDateEnd());
		album.setCreatedByUserName(albumDto.getCreatedBy().trim());
		album.setDateAdded(albumDto.getDateAdded());
		album.setLastModifiedByUserName(albumDto.getLastModifiedBy().trim());
		album.setDateLastModified(albumDto.getDateLastModified());
		album.setOwnerUserName(albumDto.getOwnedBy().trim());
		album.setOwnerRoleName(albumDto.getOwnerRoleName().trim());
		album.setIsPrivate(albumDto.isIsPrivate());

		// Set the album's thumbnail content object ID. Setting this property sets an internal flag that will cause
		// the content object info to be retrieved when the Thumbnail property is accessed. That's why we don't
		// need to set any of the thumbnail properties.
		// WARNING: No matter what, do not call DisplayObject.CreateInstance() because that creates a new object, 
		// and we might be  executing this method from within our Thumbnail display object. Trust me, this 
		// creates hard to find bugs!
		album.setThumbnailContentObjectId(albumDto.getThumbContentObject() == null ? 0 : albumDto.getThumbContentObject().getId());

		album.addMeta(ContentObjectMetadataItemCollection.fromMetaDtos(album, albumDto.getMetadatas()));
	}

	/// <summary>
	/// Create a new top-level album for the specified <paramref name = "galleryId" /> and persist to the data store. The newly created
	/// album is returned. Guaranteed to not return null.
	/// </summary>
	/// <param name="galleryId">The gallery ID for which the new album is to be the root album.</param>
	/// <returns>Returns an <see cref="Album" /> instance representing the top-level album for the specified <paramref name = "galleryId" />.</returns>
	private static AlbumBo createRootAlbum(long galleryId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		AlbumBo album = createEmptyAlbumInstance(galleryId);

		Date currentTimestamp = DateUtils.Now();

		album.getParent().setId(0); // The parent ID of the root album is always zero.
		album.setTitle("{album.root_Album_Default_Title}");
		album.setDirectoryName(StringUtils.EMPTY); // The root album must have an empty directory name;
		album.setCaption("{album.root_Album_Default_Summary}");
		album.setCreatedByUserName(Constants.SystemUserName);
		album.setDateAdded(currentTimestamp);
		album.setLastModifiedByUserName(Constants.SystemUserName);
		album.setDateLastModified(currentTimestamp);

		album.save();

		return album;
	}

	private static void addIntegersToCollectionIfNotPresent(Collection<Integer> intCollection, Iterable<Integer> integersToAdd){
		for(int intInCollection : integersToAdd){
			if (!intCollection.contains(intInCollection))
				intCollection.add(intInCollection);
		}
	}
	
	private static void addLongsToCollectionIfNotPresent(Collection<Long> longCollection, Iterable<Long> longsToAdd){
		for(long longInCollection : longsToAdd){
			if (!longCollection.contains(longInCollection))
				longCollection.add(longInCollection);
		}
	}

	/// <summary>
	/// Return only those album ID's in <paramref name = "albumIds" /> that belong to the gallery specified by <paramref name = "galleryId" />.
	/// </summary>
	/// <param name="albumIds">The album ID's.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns a collection of integers representing album ID's belonging to albums in the specified gallery.</returns>
	private static List<Long> getAlbumIdsForGallery(List<Long> albumIds, Long galleryId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException{
		List<Long> galleryAlbumIds = Lists.newArrayList();

		for (long id : albumIds){
			ContentObjectBo album = loadAlbumInstance(id, false);

			if (album.getGalleryId() == galleryId)	{
				galleryAlbumIds.add(id);
			}
		}

		return galleryAlbumIds;
	}

	private static void addToContentObjectCache(ContentObjectBo go)	{
		// Add to content object cache, but only if the object's parent is read-only.
		if (go.getParent().getIsWritable()){
			return;
		}

		ConcurrentHashMap<Long, ContentObjectBo> contentObjectCache = (ConcurrentHashMap<Long, ContentObjectBo>)CacheUtils.get(CacheItem.cm_contentobjects);

		ContentObjectBo contentObjectInCache;

		if (contentObjectCache == null)	{
			contentObjectCache = new ConcurrentHashMap<Long, ContentObjectBo>();
		}

		if ((contentObjectInCache = contentObjectCache.get(go.getId())) == null){
			if (!contentObjectCache.containsKey(go.getId())){
				go.setIsWritable(false);
				contentObjectCache.put(go.getId(), go);
				CacheUtils.put(CacheItem.cm_contentobjects, contentObjectCache);
			}
		}
	}

	/// <summary>
	/// Syncs the metadata value with the corresponding property on the album or content object.
	/// The album/content object properties are deprecated in version 3, but we want to sync them
	/// for backwards compatibility. A future version is expected to remove these properties, at
	/// which time this method will no longer be needed.
	/// </summary>
	/// <param name="md">An instance of <see cref="ContentObjectBoMetadataItem" /> being persisted to the data store.</param>
	/// <param name="userName">The user name of the currently logged on user. This will be used for the audit fields.</param>
	private static void syncWithContentObjectProperties(ContentObjectMetadataItem md, String userName) throws InvalidContentObjectException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, IOException, InvalidGalleryException{
		if ((md.getMetadataItemName() == MetadataItemName.Title) && (md.getContentObject().getContentObjectType() == ContentObjectType.Album)){
			AlbumBo album = loadAlbumInstance(md.getContentObject().getId(), false, true);

			// If necessary, sync the directory name with the album title.
			GallerySettings gs = loadGallerySetting(album.getGalleryId());
			if ((!album.isRootAlbum()) && (!album.getIsVirtualAlbum()) && (gs.getSynchAlbumTitleAndDirectoryName())){
				// Root albums do not have a directory name that reflects the albu's title, so only update this property for non-root albums.
				if (!album.getDirectoryName().equalsIgnoreCase(album.getTitle())){
					// We only update the directory name when it is different. Without this check a user saving a 
					// title without any changes would cause the directory name to get changed (e.g. 'Samples'
					// might get changed to 'Sample(1)')
					album.setDirectoryName(HelperFunctions.validateDirectoryName(album.getParent().getFullPhysicalPath(), album.getTitle(), gs.getDefaultAlbumDirectoryNameLength()));

					HelperFunctions.updateAuditFields(album, userName);
					album.save();
				}
			}
		}

		if (md.getMetadataItemName() == MetadataItemName.FileName){
			// We are editing the filename item, so we want to rename the actual media file.
			// Load the content object instance and trigger a save. The save routine will detect
			// the metadata change and perform the rename for us.
			ContentObjectBo contentObject = loadContentObjectInstance(md.getContentObject().getId(), true);
			HelperFunctions.updateAuditFields(contentObject, userName);
			contentObject.save();
		}

		if (md.getMetadataItemName() == MetadataItemName.HtmlSource){
			// We are editing the HTML content. This is the same as the content object's 
			// ExternalHtmlSource, so update that item, too.
			ContentObjectBo contentObject = loadContentObjectInstance(md.getContentObject().getId(), true);

			// Verify the content object is an external one. It always should be, but we'll
			// double check to be sure.
			if (contentObject.getContentObjectType() == ContentObjectType.External)	{
				contentObject.getOriginal().setExternalHtmlSource(md.getValue());
				HelperFunctions.updateAuditFields(contentObject, userName);
				contentObject.save();
			}
		}
	}

	//#endregion
}