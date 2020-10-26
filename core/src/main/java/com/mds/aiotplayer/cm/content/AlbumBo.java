/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.cm.content.nullobjects.NullContentObject;
import com.mds.aiotplayer.cm.content.nullobjects.NullDisplayObjectCreator;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItemCollection;
import com.mds.aiotplayer.cm.metadata.MetadataDefinition;
import com.mds.aiotplayer.cm.model.Metadata;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.DisplayObjectType;
import com.mds.aiotplayer.core.LongCollection;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.VirtualAlbumType;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.MDSRole;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.cm.util.CMUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/// <summary>
/// Represents an album in MDS System. An album is a container for zero or more content objects. A content object 
/// may be a content object such as image, video, audio file, or document, or it may be another album.
/// </summary>
public class AlbumBo extends ContentObjectBo implements ContentObjectListener{
	//#region Private Fields

	private String directoryName;
	private String fullPhysicalPathOnDisk;
	private Date dateStart;
	private Date dateEnd;
	private String ownerUsername;
	private String ownerRoleName;
	private ContentObjectBoCollection contentObjects;
	private long thumbnailContentObjectId;
	private boolean areChildrenInflated;
	private boolean isThumbnailInflated;
	private boolean isVirtualAlbum;
	private boolean allowMetadataLoading;
	private MetadataItemName sortByMetaName = MetadataItemName.NotSpecified;
	private Boolean sortAscending;
	private List<String> inheritedOwners;
	private VirtualAlbumType virtualAlbumType;
	private FeedFormatterOptions feedFormatterOptions;

	private final Object _lock = new Object();
	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="Album"/> class.
	/// </summary>
	/// <param name="albumId">The album ID.</param>
	/// <param name="galleryId">The gallery ID.</param>
	public AlbumBo(long albumId, long galleryId) throws InvalidGalleryException{
		this(albumId, galleryId, Long.MIN_VALUE, StringUtils.EMPTY, Long.MIN_VALUE, MetadataItemName.NotSpecified, null, Integer.MIN_VALUE
				, DateUtils.MinValue, DateUtils.MinValue, StringUtils.EMPTY, Calendar.getInstance().getTime(), StringUtils.EMPTY
				, DateUtils.MinValue, StringUtils.EMPTY, StringUtils.EMPTY, false, false, null);
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="Album" /> class.
	/// </summary>
	/// <param name="id">The album ID.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="parentId">The ID of the parent album that contains this album.</param>
	/// <param name="directoryName">Name of the directory.</param>
	/// <param name="thumbnailContentObjectId">The thumbnail content object id.</param>
	/// <param name="sortByMetaName">The metadata item to sort the album by.</param>
	/// <param name="sortAscending">Indicates whether the contents of the album are sorted in ascending order. Specify null if 
	///   the sort order is not known.</param>
	/// <param name="sequence">The sequence.</param>
	/// <param name="dateStart">The starting date for this album.</param>
	/// <param name="dateEnd">The ending date for this album.</param>
	/// <param name="createdByUserName">The user name of the user who created this content object.</param>
	/// <param name="dateAdded">The date this content object was created.</param>
	/// <param name="lastModifiedByUserName">The user name of the user who last modified this content object.</param>
	/// <param name="dateLastModified">The date and time this content object was last modified.</param>
	/// <param name="ownerUsername">The user name of this content object's owner.</param>
	/// <param name="ownerRoleName">The name of the role associated with this content object's owner.</param>
	/// <param name="isPrivate"><c>true</c> this content object is hidden from anonymous users; otherwise <c>false</c>.</param>
	/// <param name="isInflated">A boolean indicating whether this object is fully inflated.</param>
	/// <param name="metadata">A collection of <see cref="Data.MetadataDto" /> instances containing metadata for the
	///   object. Specify null if not available.</param>
	/// <exception cref="System.ArgumentOutOfRangeException">galleryId</exception>
	public AlbumBo(long id, long galleryId, long parentId, String directoryName, Long thumbnailContentObjectId, MetadataItemName sortByMetaName
			, Boolean sortAscending, int sequence, Date dateStart, Date dateEnd, String createdByUserName, Date dateAdded, String lastModifiedByUserName
			, Date dateLastModified, String ownerUsername, String ownerRoleName, boolean isPrivate, boolean isInflated, Iterable<Metadata> metadata) throws InvalidGalleryException	{
		
		super();
		
		if (galleryId == Long.MIN_VALUE){
			throw new ArgumentOutOfRangeException("galleryId", MessageFormat.format("Gallery ID must be set to a valid value. Instead, the value was {0}.", galleryId));
		}

		this.contentObjects = new ContentObjectBoCollection();
		assert this.areChildrenInflated == false : MessageFormat.format("The private booleanean field _areChildrenInflated should have been initialized to false, but instead it was {0}.", this.areChildrenInflated);

		this.setId(id);

		// Specify gallery ID: Use galleryID parm if specified, otherwise, use gallery ID of parent. If no parent, use Long.MIN_VALUE
		//this.GalleryId = (galleryId > Long.MIN_VALUE ? galleryId : (parentId >= 0 ? this.Parent.GalleryId : Long.MIN_VALUE));
		this.setGalleryId(galleryId);

		if (parentId > 0){
			this.setParent(CMUtils.createAlbumInstance(parentId, galleryId));
			//todo
		}else if (parentId == 0){
			this.getParent().setId(parentId); // Parent ID of root album is always 0.
			//todo
		}

		//this.Title = title;
		this.directoryName = directoryName;
		//this.summary = summary;
		this.setSequence(sequence);
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.setCreatedByUserName(createdByUserName);
		this.setDateAdded(dateAdded);
		this.setLastModifiedByUserName(lastModifiedByUserName);
		this.ownerUsername = ownerUsername;
		this.ownerRoleName = ownerRoleName;
		this.setDateLastModified(dateLastModified);
		this.setIsPrivate(isPrivate);
		this.setAllowMetadataLoading(true);
		this.fullPhysicalPathOnDisk = StringUtils.EMPTY;

		//this.thumbnailContentObjectId = (thumbnailContentObjectId == Long.MIN_VALUE ? 0 : thumbnailContentObjectId);
		this.setThumbnailContentObjectId((thumbnailContentObjectId == Long.MIN_VALUE ? 0 : thumbnailContentObjectId));
		this.isThumbnailInflated = false;

		if (this.thumbnailContentObjectId > 0){
			this.setThumbnail(DisplayObject.createInstance(this, this.thumbnailContentObjectId, DisplayObjectType.Thumbnail));
		}else{
			this.setThumbnail(getDefaultAlbumThumbnail());
		}

		this.virtualAlbumType = (id > Long.MIN_VALUE ? VirtualAlbumType.NotVirtual : VirtualAlbumType.NotSpecified);

		this.saveBehavior = CMUtils.getAlbumSaveBehavior(this);
		this.deleteBehavior = CMUtils.getAlbumDeleteBehavior(this);
		this.setMetadataReadWriter(CMUtils.getMetadataReadWriter(this));

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		if (this.getIsNew())	{
			if (sortByMetaName == MetadataItemName.NotSpecified) {
				sortByMetaName = gallerySetting.getDefaultAlbumSortMetaName();
			}

			if (sortAscending == null){
				sortAscending = gallerySetting.getDefaultAlbumSortAscending();
			}

			if (gallerySetting.getExtractMetadata()){
				extractMetadata();
			}
		}

		if (sortByMetaName != MetadataItemName.NotSpecified)
			this.setSortByMetaName(sortByMetaName);

		if (sortAscending != null)
			this.setSortAscending(sortAscending);

		if (metadata != null)
			addMeta(ContentObjectMetadataItemCollection.fromMetaDtos(this, metadata));

		this.setIsInflated(isInflated);

		// Setting the previous properties has caused hasChanges = true, but we don't want this while
		// we're instantiating a new object. Reset to false.
		this.hasChanges = false;
		
		listeners.add(this);
	}

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets or sets the name of the directory where the album is stored. Example: summervacation.
	/// </summary>
	/// <value>
	/// The directory where the album is stored. Example: summervacation..
	/// </value>
	public String getDirectoryName(){
			verifyObjectIsInflated(this.directoryName);
			return this.directoryName;
	}
	
	public void setDirectoryName(String directoryName){
			this.hasChanges = (this.directoryName == directoryName ? this.hasChanges : true);

			this.directoryName = directoryName;
	}

	/// <summary>
	/// Gets or sets the starting date for this album.
	/// </summary>
	/// <value>The starting date for this album.</value>
	public Date getDateStart()	{
			verifyObjectIsInflated(this.dateStart);
			return this.dateStart;
	}
	
	public void setDateStart(Date dateStart)	{
			this.hasChanges = (this.dateStart == dateStart ? this.hasChanges : true);

			this.dateStart = dateStart;
	}

	/// <summary>
	/// Gets or sets the ending date for this album.
	/// </summary>
	/// <value>The ending date for this album.</value>
	public Date getDateEnd(){
			verifyObjectIsInflated(this.dateEnd);
			return this.dateEnd;
	}
	
	public void setDateEnd(Date dateEnd){
			this.hasChanges = (this.dateEnd == dateEnd ? this.hasChanges : true);

			this.dateEnd = dateEnd;
	}

	/// <summary>
	/// Gets or sets the user name of this content object's owner. This property and OwnerRoleName
	/// are closely related and both should be populated or both be empty.
	/// </summary>
	/// <value>The user name of this content object's owner.</value>
	public String getOwnerUserName(){
			verifyObjectIsInflated(this.ownerUsername);
			return this.ownerUsername;
	}
	
	public void setOwnerUserName(String OwnerRoleName){
			this.hasChanges = (this.ownerUsername == OwnerRoleName ? this.hasChanges : true);
			this.ownerUsername = OwnerRoleName;

			if (StringUtils.isBlank(this.ownerUsername))
				this.ownerRoleName = StringUtils.EMPTY;
	}

	/// <summary>
	/// Gets the owners the current album inherits from parent albums. Guaranteed to not return null.
	/// Will be empty when there aren't any inherited owners.
	/// </summary>
	/// <value>A collection of Strings.</value>
	public String[] getInheritedOwners()	{
		if (inheritedOwners == null){
			this.inheritedOwners = Lists.newArrayList();

			AlbumBo album = Reflections.as(AlbumBo.class, this.parent);
			while (album != null){
				if (!StringUtils.isBlank(album.getOwnerUserName()))
				{
					inheritedOwners.add(album.getOwnerUserName());
				}

				album = Reflections.as(AlbumBo.class, album.parent); // Will be null when it gets to the top album, since NullContentObjectBo can't cast to AlbumBo
			}
		}

		return inheritedOwners.toArray(new String[0]);
	}

	/// <summary>
	/// Gets or sets the name of the role associated with this content object's owner. This property and
	/// OwnerUserName are closely related and both should be populated or both be empty.
	/// </summary>
	/// <value>
	/// The name of the role associated with this content object's owner.
	/// </value>
	public String getOwnerRoleName()	{
		verifyObjectIsInflated(this.ownerRoleName);
		return this.ownerRoleName;
	}
	
	public void setOwnerRoleName(String ownerRoleName)	{
		this.hasChanges = (this.ownerRoleName == ownerRoleName ? this.hasChanges : true);
		this.ownerRoleName = ownerRoleName;
	}

	/// <summary>
	/// Gets or sets the content object ID whose thumbnail image should be used as the thumbnail image to represent this album.
	/// </summary>
	/// <value>The thumbnail content object id.</value>
	public long getThumbnailContentObjectId() throws InvalidGalleryException	{
		// If the int = 0, and this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		// Don't use verifyObjectIsInflated() method because we need to compare the value
		// to 0, not Long.MIN_VALUE.
		if ((this.thumbnailContentObjectId == 0) && (!this.getIsNew()) && (!this.isInflated)){
			try {
				CMUtils.loadAlbumInstance(this, false);
			} catch (InvalidAlbumException | UnsupportedContentObjectTypeException | UnsupportedImageTypeException
					| InvalidContentObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// The value could still be 0, even after inflating from the data store, because
		// 0 is a valid value that indicates no thumbanil has been assigned to this album.
		return this.thumbnailContentObjectId;
	}
	
	public void setThumbnailContentObjectId(long thumbnailContentObjectId)	{
		if (this.thumbnailContentObjectId != thumbnailContentObjectId){
			// Reset the thumbnail flag so next time the album's thumbnail properties are accessed, 
			// verifyThumbnailIsInflated() will know to refresh the properties.
			this.isThumbnailInflated = false;
		}
		this.hasChanges = (this.thumbnailContentObjectId == thumbnailContentObjectId ? this.hasChanges : true);

		this.thumbnailContentObjectId = thumbnailContentObjectId;
	}

	/// <summary>
	/// Gets or sets the metadata property to sort the album by.
	/// </summary>
	/// <value>The metadata property to sort the album by.</value>
	public MetadataItemName getSortByMetaName()	{
		verifyObjectIsInflated(this.sortByMetaName);
		return sortByMetaName;
	}
	
	public void setSortByMetaName(MetadataItemName sortByMetaName)	{
		this.hasChanges = ((this.sortByMetaName != null && this.sortByMetaName == sortByMetaName) ? this.hasChanges : true);

		this.sortByMetaName = sortByMetaName;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the contents of the album are sorted in ascending order. A <c>false</c> value indicates
	/// a descending sort.
	/// </summary>
	/// <value><c>true</c> if an album's contents are sorted in ascending order; <c>false</c> if descending order.</value>
	public boolean getSortAscending()	{
			verifyObjectIsInflated(this.sortAscending);

			if (sortAscending == null)
				throw new BusinessException("The Album.SortAscending value was null. It should have been assigned a value by the verifyObjectIsInflated() function.");

			return sortAscending;
	}
	
	public void setSortAscending(boolean sortAscending)	{
		this.hasChanges = ((this.sortAscending != null && this.sortAscending == sortAscending) ? this.hasChanges : true);

		this.sortAscending = sortAscending;
	}

	/// <summary>
	/// Gets a value indicating whether this album is the top level album in the gallery.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance is a root album; otherwise, <c>false</c>.
	/// </value>
	public boolean isRootAlbum()	{
			return (this.parent instanceof NullContentObject);
	}

	/// <summary>
	/// Gets or sets a value indicating whether this album is a virtual album used only as a container for objects that are
	/// spread across multiple albums. A virtual album does not map to a physical folder and cannot be saved to the
	/// data store. Virtual albums are used as containers for search results and to contain the top level albums
	/// that a user has authorization to view.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance is a virtual album; otherwise, <c>false</c>.
	/// </value>
	public boolean getIsVirtualAlbum()	{
		return (this.isVirtualAlbum);
	}
	
	public void setIsVirtualAlbum(boolean isVirtualAlbum)	{
		if ((this.id > Long.MIN_VALUE) && isVirtualAlbum){
			throw new BusinessException("Cannot mark an existing album as virtual.");
		}
		this.isVirtualAlbum = isVirtualAlbum;

		if (isVirtualAlbum)	{
			// Clear any meta items that were created. In a future version we might enable permanent
			// storage of meta items for virtual albums, but today we don't have that capability.
			metadataItems.clear();

			// Mark object as inflated. This can save a call to the DB later if inflate() is called.
			areChildrenInflated = true;
		}
	}

	/// <summary>
	/// Gets or sets the type of the virtual album for this instance. Applies only when <see cref="IsVirtualAlbum" /> is <c>true</c>.
	/// </summary>
	/// <value>The type of the virtual album.</value>
	public VirtualAlbumType getVirtualAlbumType() { 
		return virtualAlbumType; 
	}
	
	public void setVirtualAlbumType(VirtualAlbumType virtualAlbumType) { 
		this.virtualAlbumType = virtualAlbumType; 
	}

	/// <summary>
	/// Gets or sets a value indicating whether metadata is to be loaded from the data store when an object is inflated. Setting
	/// this to false when metadata is not needed can improve performance, especially when large numbers of objects are being
	/// loading, such as during maintenance and synchronizations. The default value is <c>true</c>. When <c>false</c>, metadata
	/// is not extracted from the database and the <see cref="ContentObjectBo.MetadataItems"/> collection is empty. As objects are lazily loaded,
	/// this value is inherited from its parent object.
	/// </summary>
	/// <value>
	/// 	<c>true</c> to allow metadata to be retrieved from the data store; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowMetadataLoading(){
		return this.allowMetadataLoading; 
	}
	
	public void setAllowMetadataLoading(boolean allowMetadataLoading){
		this.allowMetadataLoading = allowMetadataLoading;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the child albums have been added, and for content objects, whether they have been
	/// added and inflated for this album. Note that it is possible for child albums to have been added to this album but not
	/// inflated, while the child content objects have been added but not inflated. This is because the
	/// <see cref="Album.Inflate"/> method adds both child albums and content objects, but inflates only the media
	/// objects.
	/// </summary>
	/// <value><c>true</c> if this album is inflated; otherwise, <c>false</c>.</value>
	public boolean getAreChildrenInflated()	{
			return this.areChildrenInflated;
	}
	
	public void setAreChildrenInflated(boolean areChildrenInflated)	{
			this.areChildrenInflated = areChildrenInflated;
	}

	/// <summary>
	/// Gets or sets the feed formatter options. This property is used when generating an RSS/Atom feed.
	/// </summary>
	/// <value>The feed formatter options.</value>
	public FeedFormatterOptions getFeedFormatterOptions() { 
		return feedFormatterOptions; 
	}
	
	public void setFeedFormatterOptions(FeedFormatterOptions feedFormatterOptions) { 
		this.feedFormatterOptions = feedFormatterOptions; 
	}

	//#endregion

	//#region Override Properties

	/// <summary>
	/// Gets or sets the title for this content object. This property is a pass-through to the 
	/// underlying <see cref="MetadataItemName.AlbumTitle" /> item in the 
	/// <see cref="ContentObjectBo.MetadataItems" /> collection.
	/// </summary>
	/// <value>The title for this content object.</value>
	public String getTitle(){
		ContentObjectMetadataItem metaItem = getMetadataItems().tryGetMetadataItem(MetadataItemName.Title);
		if (metaItem != null)
			return metaItem.getValue();
		else
			//throw new BusinessException(MessageFormat.format("No meta item 'Title' exists for album {0}.", Id));
			return StringUtils.EMPTY;
	}
	
	public void setTitle(String title) throws UnsupportedContentObjectTypeException, InvalidGalleryException{			
		ContentObjectMetadataItem metaItem = getMetadataItems().tryGetMetadataItem(MetadataItemName.Title);
		if (metaItem != null){
			metaItem.setValue(title);
			hasChanges = metaItem.getHasChanges();
		}else{
			ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
			MetadataDefinition metadataDef = getMetaDefinitions().find(MetadataItemName.Title);
			metaItems.add(CMUtils.createMetadataItem(Long.MIN_VALUE, this, null, title, true, metadataDef));
			addMeta(metaItems);
			hasChanges = true;
		}
	}

	/// <summary>
	/// Gets the content object type.
	/// </summary>
	/// <value>
	/// An instance of <see cref="ContentObjectType" />.
	/// </value>
	public ContentObjectType getContentObjectType()	{
		return ContentObjectType.Album; 
	}

	/// <summary>
	/// Gets the physical path to this object. Does not include the trailing slash.
	/// Example: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets\
	/// </summary>
	/// <value>The full physical path to this object.</value>
	public String getFullPhysicalPath() throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
			this.inflate(false);

			if (this.isRootAlbum())	{
				if (!(StringUtils.isBlank(this.directoryName)))
					throw new BusinessException(I18nUtils.getMessage("exception.album_FullPhysicalPath_Ex_Msg", this.directoryName));

				if (StringUtils.isBlank(this.fullPhysicalPathOnDisk)){
					this.fullPhysicalPathOnDisk = CMUtils.loadGallerySetting(galleryId).getFullContentObjectPath();
				}

				return this.fullPhysicalPathOnDisk;
			}else{
				return FilenameUtils.concat(this.parent.getFullPhysicalPath(), this.directoryName);
			}
	}

	/// <summary>
	/// Gets or sets the full physical path for this object as it currently exists on the hard drive. This property
	/// is updated when the object is loaded from the hard drive and when it is saved to the hard drive.
	/// <note type="caution"> Do not set this property from any class other than one that implements <see cref="ContentObjectBo"/>!
	/// Does not include the trailing slash.
	/// Example: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets</note>
	/// </summary>
	/// <value>The full physical path on disk.</value>
	public String getFullPhysicalPathOnDisk()	{
			if (this.fullPhysicalPathOnDisk.length() > 0){
				return this.fullPhysicalPathOnDisk;
			}else if (this.getIsNew()){
				// Return an empty String for new albums that haven't been persisted to the data store.
				return StringUtils.EMPTY;
			}else if ((!this.getIsNew()) && (!this.isInflated)){
				// Album exists on disk but is not inflated. Load it now, which will set the private variable.
				try {
					CMUtils.loadAlbumInstance(this, false);
				} catch (InvalidAlbumException | UnsupportedContentObjectTypeException | UnsupportedImageTypeException
						| InvalidContentObjectException | InvalidGalleryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				assert this.fullPhysicalPathOnDisk.length() > 0;

				return this.fullPhysicalPathOnDisk;
			}
			//If we get here isNew must be false and isInflated must be true. Throw assertion.
			throw new BusinessException(MessageFormat.format("Invalid object state. Album.isNew = {0}, Album.isInflated = {1}, and the private member variable _fullPhysicalPathOnDisk is either null or empty.", this.getIsNew(), this.isInflated));
	}
	
	public void setFullPhysicalPathOnDisk(String fullPhysicalPathOnDisk){
			this.fullPhysicalPathOnDisk = fullPhysicalPathOnDisk;
	}

	//#endregion

	//#region Override Methods

	/// <summary>
	/// Verify the properties have been set for the thumbnail image in this album, retrieving the information
	/// from the data store if necessary. This method also inflates the album if it is not already inflated 
	/// (but doesn't inflate the children objects).
	/// </summary>
	/// <param name="thumbnail">A reference to the thumbnail display object for this album. The instance
	/// is passed as a parameter rather than directly addressed as a property of our base class because we don't 
	/// want to trigger the property get {} code, which calls this method (and would thus result in an infinite
	/// loop).</param>
	/// <remarks>To be perfectly clear, let me say again that the thumbnail parameter is the same instance
	/// as album.Thumbnail. They both refer to the same memory space. This method updates the albumThumbnail 
	/// parameter, which means that album.Thumbnail is updated as well.</remarks>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="thumbnail" /> is null.</exception>
	protected void verifyThumbnailIsInflated(DisplayObject thumbnail) throws InvalidGalleryException{
		if (thumbnail == null)
			throw new ArgumentNullException("thumbnail");

		// Verify album is inflated (the method only inflates the album if it's not already inflated).
		inflate(false);

		assert this.thumbnailContentObjectId >= 0 : MessageFormat.format("Album.inflate(false) should have set ThumbnailContentObjectId >= 0. Instead, it is {0}.", this.thumbnailContentObjectId);

		if (!this.isThumbnailInflated){
			// Need to inflate thumbnail.
			if (this.thumbnailContentObjectId > 0){
				// ID has been specified. Find content object and retrieve it's thumbnail properties.

				//#region Get reference to the content object used for the album's thumbnail

				// If thumbnail content object is one of the album's children, use that. Otherwise, load from data store.
				ContentObjectBo thumbnailContentObject = null;
				if (this.areChildrenInflated){
					for(ContentObjectBo contentObject : this.getChildContentObjects(ContentObjectType.ContentObject).values()){
						if (this.thumbnailContentObjectId == contentObject.getId())	{
							thumbnailContentObject = contentObject;
							break;
						}
					}
				}

				if (thumbnailContentObject == null)	{
					// this.thumbnailContentObjectId does not refer to a content object that is a direct child of this 
					// album, so just go to the data store and retrieve it.
					try{
						thumbnailContentObject = CMUtils.loadContentObjectInstance(this.thumbnailContentObjectId);
					}catch (InvalidContentObjectException e){
						// Get default thumbnail. Copy properties instead of reassigning the albumThumbnail parameter
						// so we don't lose the reference.
						DisplayObject defaultAlbumThumb = getDefaultAlbumThumbnail();
						thumbnail.setContentObjectId(defaultAlbumThumb.getContentObjectId());
						thumbnail.setDisplayType(defaultAlbumThumb.getDisplayType());
						thumbnail.setFileName(defaultAlbumThumb.getFileName());
						thumbnail.setWidth(defaultAlbumThumb.getWidth());
						thumbnail.setHeight(defaultAlbumThumb.getHeight());
						thumbnail.setFileSizeKB(defaultAlbumThumb.getFileSizeKB());
						thumbnail.setFileNamePhysicalPath(defaultAlbumThumb.getFileNamePhysicalPath());
					} catch (UnsupportedContentObjectTypeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidAlbumException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedImageTypeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidGalleryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				//#endregion

				if (thumbnailContentObject != null)	{
					thumbnail.setContentObjectId(this.thumbnailContentObjectId);
					thumbnail.setDisplayType(DisplayObjectType.Thumbnail);
					thumbnail.setFileName(thumbnailContentObject.getThumbnail().getFileName());
					thumbnail.setWidth(thumbnailContentObject.getThumbnail().getWidth());
					thumbnail.setHeight(thumbnailContentObject.getThumbnail().getHeight());
					thumbnail.setFileSizeKB(thumbnailContentObject.getThumbnail().getFileSizeKB());
					thumbnail.setFileNamePhysicalPath(thumbnailContentObject.getThumbnail().getFileNamePhysicalPath());
				}
			}else{
				// ID = 0. Set to default values. This is a repeat of what happens in the Album() constructor,
				// but we need it again just in case the user changes it to 0 and immediately retrieves its properties.
				// Copy properties instead of reassigning the albumThumbnail parameter so we don't lose the reference.
				DisplayObject defaultAlbumThumb = getDefaultAlbumThumbnail();
				thumbnail.setContentObjectId(defaultAlbumThumb.getContentObjectId());
				thumbnail.setDisplayType(defaultAlbumThumb.getDisplayType());
				thumbnail.setFileName(defaultAlbumThumb.getFileName());
				thumbnail.setWidth(defaultAlbumThumb.getWidth());
				thumbnail.setHeight(defaultAlbumThumb.getHeight());
				thumbnail.setFileSizeKB(defaultAlbumThumb.getFileSizeKB());
				thumbnail.setFileNamePhysicalPath(defaultAlbumThumb.getFileNamePhysicalPath());
			}

			this.isThumbnailInflated = true;
		}

	}

	/// <summary>
	/// Overrides the method from <see cref="ContentObjectBo" />. This implementation  is empty, because albums don't have thumbnail
	/// images, at least not in the strictest sense.
	/// </summary>
	protected void checkForThumbnailImage()	{
		// Do nothing: Strictly speaking, albums don't have thumbnail images. Only the content object that is assigned
		// as the thumbnail for an album has a thumbnail image. The code that verifies the content object has a thumbnail
		// image during a save is sufficient.
	}

	/// <summary>
	/// Gets a value indicating whether the administrator has indicated the specified <paramref name="metaDef" />
	/// applies to the current content object.
	/// </summary>
	/// <param name="metaDef">The metadata definition.</param>
	/// <returns><c>true</c> when the specified metadata item should be displayed; otherwise <c>false</c>.</returns>
	public boolean metadataDefinitionApplies(MetadataDefinition metaDef){
		if (metaDef.getMetadataItemName() == MetadataItemName.Title || metaDef.getMetadataItemName() == MetadataItemName.Caption)
			return true; // We *ALWAYS* want to create a Title and Caption item.
		else
			return metaDef.IsVisibleForAlbum;
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Adds the specified content object as a child of this content object.
	/// </summary>
	/// <param name="ContentObjectBo">The <see cref="ContentObjectBo" /> to add as a child of this
	/// content object.</param>
	/// <exception cref="System.NotSupportedException">Thrown when an inherited type
	/// does not allow the addition of child content objects.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="ContentObjectBo" /> is null.</exception>
	public void addContentObject(ContentObjectBo contentObject)	{
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		// Do not add object if it already exists in our collection. An object is uniquely identified by its ID and type.
		// For example, this album may contain a content object of type Image with ID=25 and also a child album of type Album
		// with ID = 25.
		if (contentObject.getId() > Long.MIN_VALUE)	{
			//System.Diagnostics.Debug.Assert(this.contentObjects.Count == 0, MessageFormat.format"this.ContentObjects.Count = {0}", this.ContentObjects.Count));
			synchronized (this.contentObjects){
				for (ContentObjectBo go : this.contentObjects.values()){
					if ((go.getId() == contentObject.getId()) && (go.getClass() == contentObject.getClass()))
						return;
				}
			}
		}

		// If the current album is virtual, meaning that it is a temporary container for one or more objects and not the actual
		// parent album, then we want to add the object as a child of this album but we don't want to set the Parent property
		// of the child object, since that will cause the filepaths to recalculate and become inaccurate.
		if (this.isVirtualAlbum){
			doAddContentObject(contentObject);
		}else{
			contentObject.setParent(this);
		}
	}

	/// <summary>
	/// Adds the specified content object as a child of this content object. This method is called by the <see cref="AddContentObjectBo"/> 
	/// method and should not be called directly.
	/// </summary>
	/// <param name="ContentObjectBo">The content object to add as a child of this content object.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="ContentObjectBo" /> is null.</exception>
	public void doAddContentObject(ContentObjectBo contentObject){
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		// Contains() compares based on ID, which doesn't work when adding multiple new objects all having
		// ID = int.MinVAlue.
		synchronized (this.contentObjects)
		{
			if ((contentObject.getIsNew()) || ((!contentObject.getIsNew()) && !(this.contentObjects.contains(contentObject)))){
				this.contentObjects.add(contentObject);
			}
		}
	}

	/// <summary>
	/// Removes the specified content object from the collection of child objects
	/// of this content object.
	/// </summary>
	/// <param name="ContentObjectBo">The <see cref="ContentObjectBo" /> to remove as a child of this
	/// content object.</param>
	/// <exception cref="System.NotSupportedException">Thrown when an inherited type
	/// does not allow the addition of child content objects.</exception>
	/// <exception cref="System.ArgumentException">Thrown when the specified
	/// content object is not child of this content object.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="ContentObjectBo" /> is null.</exception>
	public void removeContentObject(ContentObjectBo contentObject){
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		if (!this.contentObjects.contains(contentObject))
			throw new ArgumentException(I18nUtils.getMessage("album.remove_Ex_Msg", this.id, contentObject.getId(), contentObject.getParent().getId()));

		contentObject.setParentToNullObject();

		synchronized (this.contentObjects)
		{
			this.contentObjects.remove(contentObject);
		}
	}

	/// <summary>
	/// Permanently delete the original file for this content object. Requires that an optimized version exists.
	/// If no optimized version exists, no action is taken.
	/// </summary>
	public void deleteOriginalFile(){
		// Do nothing, since albums do not have original files.
	}

	/// <summary>
	/// Inflate the current object by loading all properties from the data store. If the object is already inflated (<see cref="ContentObjectBo.isInflated"/>=true), no action is taken.
	/// </summary>
	public void inflate(){
		inflate(false);
	}

	/// <summary>
	/// Sorts the content objects in this album by the <see cref="SortByMetaName" /> field in the order specified by
	/// <see cref="SortAscending" />, optionally persisting the changes to the database, activing recursively and - when
	/// acting recursively, optionally replacing the sort field and direction on child albums with the values from the 
	/// current album. This method updates the <see cref="ContentObjectBo.Sequence" /> property of each content object.
	/// </summary>
	/// <param name="persistToDataStore">if set to <c>true</c> persist the album and the new sequence of each child
	/// content object to the database.</param>
	/// <param name="userName">Name of the user. This is for auditing and used only when <paramref name="persistToDataStore" />
	/// is <c>true</c>; otherwise you may specify null.</param>
	/// <param name="isRecursive">If set to <c>true</c> act recursively on child albums. Defaults to <c>false</c>
	/// when not specified. This value is ignored when <paramref name="persistToDataStore" /> is <c>false</c>.</param>
	/// <param name="replaceChildSortFields">When <c>true</c>, replace the sort field and direction on child albums with 
	/// the values from the current album. This value is applied only when <paramref name="persistToDataStore" /> and
	/// <paramref name="isRecursive" /> are both <c>true</c>.</param>
	/// <exception cref="System.ArgumentException">Thrown when <paramref name="persistToDataStore" /> is <c>true</c>
	/// and <paramref name="userName" /> is null or empty.</exception>
	//public void Sort(boolean persistToDataStore, String userName, boolean isRecursive = false, boolean replaceChildSortFields = false)
	public void sort(boolean persistToDataStore, String userName) throws InvalidGalleryException{
		sort(persistToDataStore, userName, false, false); 
	}
	
	public void sort(boolean persistToDataStore, String userName, boolean isRecursive) throws InvalidGalleryException{
		sort(persistToDataStore, userName, isRecursive, false); 
	}
	
	public void sort(boolean persistToDataStore, String userName, boolean isRecursive, boolean replaceChildSortFields) throws InvalidGalleryException{
		if (persistToDataStore && StringUtils.isBlank(userName))
			throw new ArgumentException("The parameter userName must be specified when persistToDataStore is true.");

		// Step 1: Sort the content objects and update the Sequence property
		int seq = 1;
		List<ContentObjectBo> contentObjects = getChildContentObjects().toSortedList(sortByMetaName, sortAscending, galleryId);
		for (ContentObjectBo contentObject : contentObjects){
			Optional<ContentObjectBo> go = this.contentObjects.stream().filter(g -> g.getId() == contentObject.getId() && g.getContentObjectType() == contentObject.getContentObjectType()).findFirst();
			if (go.isPresent())	{
				go.get().setSequence(seq++);
			}
		}

		// Step 2: If specified, save to database and act recursively.
		if (persistToDataStore)	{
			lastModifiedByUserName = userName;
			dateLastModified = Calendar.getInstance().getTime();

			try {
				save();
			} catch (InvalidAlbumException | UnsupportedContentObjectTypeException | IOException | UnsupportedImageTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for(ContentObjectBo go : getChildContentObjects().values()){
				go.setLastModifiedByUserName(userName);
				go.setDateLastModified(Calendar.getInstance().getTime());

				try {
					go.save();
				} catch (InvalidAlbumException | UnsupportedContentObjectTypeException | IOException | UnsupportedImageTypeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (isRecursive && go.getContentObjectType() == ContentObjectType.Album){
					AlbumBo album = (AlbumBo)go;
					if (replaceChildSortFields){
						album.setSortByMetaName(sortByMetaName);
						album.setSortAscending(sortAscending);
					}

					album.sort(persistToDataStore, userName, isRecursive, replaceChildSortFields);
				}
			}
		}
	}

	/// <summary>
	/// Sorts the content objects in this album by the <see cref="SortByMetaName" /> field in the order specified by
	/// <see cref="SortAscending" />, optionally persisting the changes to the database, activing recursively and - when
	/// acting recursively, optionally replacing the sort field and direction on child albums with the values from the 
	/// current album. This method updates the <see cref="ContentObjectBo.Sequence" /> property of each content object. 
	/// It runs asynchronously and returns immediately.
	/// </summary>
	/// <param name="persistToDataStore">if set to <c>true</c> persist the album and the new sequence of each child
	/// content object to the database.</param>
	/// <param name="userName">Name of the user. This is for auditing and used only when <paramref name="persistToDataStore" />
	/// is <c>true</c>; otherwise you may specify null.</param>
	/// <param name="isRecursive">If set to <c>true</c> act recursively on child albums. Defaults to <c>false</c>
	/// when not specified. This value is ignored when <paramref name="persistToDataStore" /> is <c>false</c>.</param>
	/// <param name="replaceChildSortFields">When <c>true</c>, replace the sort field and direction on child albums with 
	/// the values from the current album. This value is applied only when <paramref name="persistToDataStore" /> and
	/// <paramref name="isRecursive" /> are both <c>true</c>.</param>
	/// <exception cref="System.ArgumentException">Thrown when <paramref name="persistToDataStore" /> is <c>true</c>
	/// and <paramref name="userName" /> is null or empty.</exception>
	public void sortAsync(boolean persistToDataStore, String userName)	{
		sortAsync(persistToDataStore, userName, false, false);
	}
	
	public void sortAsync(boolean persistToDataStore, String userName, boolean isRecursive)	{
		sortAsync(persistToDataStore, userName, isRecursive, false);
	}
	
	//void SortAsync(bool persistToDataStore, string userName, bool isRecursive = false, bool replaceChildSortFields = false);
	public void sortAsync(boolean persistToDataStore, String userName, boolean isRecursive, boolean replaceChildSortFields)	{
		//Task.CMUtils.StartNew(() => SortAsyncBegin(persistToDataStore, userName, isRecursive, replaceChildSortFields));
		runAsync(new Runnable() {
	           public void run() {
	        	   sortAsyncBegin(persistToDataStore, userName, isRecursive, replaceChildSortFields);
			   }
		}); 
	}

	/// <summary>
	/// Inflate the current object by loading all properties from the data store. If the object is already inflated (<see cref="ContentObjectBo.isInflated"/>=true), no action is taken.
	/// </summary>
	/// <param name="inflateChildContentObjects">When true, the child content objects are added and inflated. Note that child albums are added
	/// but not inflated.</param>
	public void inflate(boolean inflateChildContentObjects)	{
		// If this is not a new object, and it has not been inflated from the database,
		// OR we want to force the inflation of the child content objects (which might be happening even though
		// the album properties are already inflated), go to the data store and retrieve the info for this object.

		boolean existingAlbumThatIsNotInflated = ((!this.getIsNew()) && (!this.isInflated));
		boolean needToLoadChildAlbumsAndObjects = (inflateChildContentObjects && !this.areChildrenInflated);

		if (existingAlbumThatIsNotInflated || needToLoadChildAlbumsAndObjects){
			try {
				CMUtils.loadAlbumInstance(this, inflateChildContentObjects);
			} catch (InvalidAlbumException | UnsupportedContentObjectTypeException | UnsupportedImageTypeException
					| InvalidContentObjectException | InvalidGalleryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			assert !existingAlbumThatIsNotInflated || (existingAlbumThatIsNotInflated && ((this.isInflated) || (!this.hasChanges))) : 
				MessageFormat.format("Album.inflate() was invoked on an existing, uninflated album (isNew = false, isInflated = false), which should have triggered the CMUtils.LoadAlbumInstance() method to set isInflated=true and hasChanges=false. Instead, this album currently has these values: isInflated={0}; hasChanges={1}.", this.isInflated, this.hasChanges);

			assert inflateChildContentObjects == this.areChildrenInflated : MessageFormat.format("The inflateChildren parameter must match the AreChildrenInflated property. inflateChildren={0}; AreChildrenInflated={1}", inflateChildContentObjects, this.areChildrenInflated);

			assert this.thumbnailContentObjectId > Long.MIN_VALUE : "The album's ThumbnailContentObjectId should have been assigned in this method.";
		}

	}

	/// <summary>
	/// Returns an unsorted collection of content objects that are direct children of the current content object or
	/// an empty list (Count = 0) if there are no child objects. Use the <paramref name="excludePrivateObjects" />
	/// parameter to optionally filter out private objects (if not specified, private objects are returned).
	/// </summary>
	/// <param name="ContentObjectType">A <see cref="ContentObjectType" /> enum indicating the
	/// desired type of child objects to return.</param>
	/// <param name="contentObjectApproval">A <see cref="ContentObjectApproval" /> enum indicating the
	/// desired approval status of child objects to return.</param>
	/// <param name="excludePrivateObjects">Indicates whether to exclude objects that are marked as private
	/// (<see cref="ContentObjectBo.IsPrivate" /> = <c>true</c>). Objects that are private should not be shown to anonymous users.</param>
	/// <returns>An instance of <see cref="ContentObjectBoCollection" />.</returns>
/*	public ContentObjectBoCollection getChildContentObjects(ContentObjectType contentObjectType, boolean excludePrivateObjects){
		this.inflate(true);

		
	}*/
	
	public ContentObjectBoCollection getChildContentObjects(ContentObjectType contentObjectType, ApprovalStatus approvalStatus, boolean excludePrivateObjects){
		this.inflate(true);

		if (approvalStatus == ApprovalStatus.All){
			switch (contentObjectType){
				case All:
					return new ContentObjectBoCollection(contentObjects.stream().filter(g -> !g.getIsPrivate() || !excludePrivateObjects).collect(Collectors.toList()));
				case ContentObject:
					return new ContentObjectBoCollection(contentObjects.stream().filter(g -> (!g.getIsPrivate() || !excludePrivateObjects) && g.getContentObjectType() != ContentObjectType.Album).collect(Collectors.toList()));
				case Album:
					return new ContentObjectBoCollection(contentObjects.stream().filter(g -> (!g.getIsPrivate() || !excludePrivateObjects) && g.getContentObjectType() == ContentObjectType.Album).collect(Collectors.toList()));
				default:
					return new ContentObjectBoCollection(contentObjects.stream().filter(g -> (!g.getIsPrivate() || !excludePrivateObjects) && g.getContentObjectType() == contentObjectType).collect(Collectors.toList()));
			}
		}else{
			switch (contentObjectType){
				case All:
					return new ContentObjectBoCollection(contentObjects.stream().filter(g -> !g.getIsPrivate() || !excludePrivateObjects).collect(Collectors.toList()));
				case ContentObject:
					return new ContentObjectBoCollection(contentObjects.stream().filter(g -> (!g.getIsPrivate() || !excludePrivateObjects) && g.getContentObjectType() != ContentObjectType.Album && g.getApprovalStatus() == approvalStatus).collect(Collectors.toList()));
				case Album:
					return new ContentObjectBoCollection(contentObjects.stream().filter(g -> (!g.getIsPrivate() || !excludePrivateObjects) && g.getContentObjectType() == ContentObjectType.Album && g.getApprovalStatus() == approvalStatus).collect(Collectors.toList()));
				default:
					return new ContentObjectBoCollection(contentObjects.stream().filter(g -> (!g.getIsPrivate() || !excludePrivateObjects) && g.getContentObjectType() == contentObjectType && g.getApprovalStatus() == approvalStatus).collect(Collectors.toList()));
			}
		}
	}

	/// <summary>
	/// Move the current object to the specified destination album. This method moves the physical files associated with this
	/// object to the destination album's physical directory. This instance's <see cref="ContentObjectBo.Save"/> method is invoked to persist the changes to the
	/// data store. When moving albums, all the album's children, grandchildren, etc are also moved.
	/// </summary>
	/// <param name="destinationAlbum">The album to which the current object should be moved.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="destinationAlbum" /> is null.</exception>
	public void moveTo(AlbumBo destinationAlbum) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException	{
		if (destinationAlbum == null)
			throw new ArgumentNullException("destinationAlbum");

		// Step 1: Get list of albums whose thumbnails we'll update after the move operation.
		LongCollection albumsNeedingNewThumbnails = getAlbumHierarchy(destinationAlbum.getId());

		// Step 2: Assign the new parent album and gallery ID to this album and save.
		this.setParent(destinationAlbum);
		this.setGalleryId(destinationAlbum.getGalleryId());
		this.setSequence(Integer.MIN_VALUE); // Reset the sequence so that it will be assigned a new value placing it at the end.
		save();

		// Step 3: Remove any explicitly defined roles that the album may now be inheriting in its new location.
		updateRoleSecurityForMovedAlbum(this);

		// Step 4: Now assign new thumbnails (if needed) to the albums we moved FROM. (The thumbnail for the destination album was updated in 
		// the Save() method.)
		try {
			for (long albumId : albumsNeedingNewThumbnails){
				AlbumBo.assignAlbumThumbnail(CMUtils.loadAlbumInstance(albumId, false, true), false, false, this.lastModifiedByUserName);
			}
		} catch (UnsupportedImageTypeException | InvalidContentObjectException | InvalidGalleryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/// <summary>
	/// Copy the current object and place it in the specified destination album. This method creates a completely separate copy
	/// of the original, including copying the physical files associated with this object. The copy is persisted to the data
	/// store and then returned to the caller. When copying albums, all the album's children, grandchildren, etc are copied,
	/// and any role permissions that are explicitly assigned to the source album are copied to the destination album, unless
	/// the copied album inherits the role throught the destination parent album. Inherited role permissions are not copied.
	/// </summary>
	/// <param name="destinationAlbum">The album to which the current object should be copied.</param>
	/// <param name="userName">The user name of the currently logged on user. This will be used for the audit fields of the
	/// copied objects.</param>
	/// <returns>
	/// Returns a new content object that is an exact copy of the original, except that it resides in the specified
	/// destination album, and of course has a new ID. Child objects are recursively copied.
	/// </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="destinationAlbum" /> is null.</exception>
	public ContentObjectBo copyTo(AlbumBo destinationAlbum, String userName) throws InvalidGalleryException	{
		if (destinationAlbum == null)
			throw new ArgumentNullException("destinationAlbum");

		// Step 1: Copy the album.

		AlbumBo albumCopy = CMUtils.createEmptyAlbumInstance(destinationAlbum.getGalleryId());

		//albumCopy.Title = this.Title;
		//albumCopy.Summary = this.Summary;
		albumCopy.setDateStart(this.dateStart);
		albumCopy.setDateEnd(this.dateEnd);
		//albumCopy.OwnerUserName = this.OwnerUserName; // Do not copy this one
		//albumCopy.OwnerRoleName = this.OwnerRoleName; // Do not copy this one

		albumCopy.getMetadataItems().clear();
		albumCopy.getMetadataItems().addRange(metadataItems.copy());

		// Associate the new meta items with the copied object.
		for (ContentObjectMetadataItem metadataItem : albumCopy.getMetadataItems()){
			metadataItem.setContentObject(albumCopy);
		}

		ContentObjectMetadataItem metaItem = albumCopy.getMetadataItems().tryGetMetadataItem(MetadataItemName.DateAdded);
		if (metaItem != null){
			GallerySettings gallerySetting = CMUtils.loadGallerySetting(destinationAlbum.galleryId);

			metaItem.setValue(DateUtils.getDateTime(gallerySetting.getMetadataDateTimeFormatString(), Calendar.getInstance().getTime()));
		}

		albumCopy.setParent(destinationAlbum);
		albumCopy.setIsPrivate(destinationAlbum.getIsPrivate());

		HelperFunctions.updateAuditFields(albumCopy, userName);
		try {
			albumCopy.save();
		} catch (InvalidAlbumException | UnsupportedContentObjectTypeException | IOException | UnsupportedImageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Step 2: Copy any roles that are explicitly assigned to the original album.
		updateRoleSecurityForCopiedAlbum(albumCopy, this);

		// Step 3: Copy all child content objects of this album (including child albums).
		try {
			for (ContentObjectBo contentObject : this.getChildContentObjects().values()){
				ContentObjectBo copiedObject = contentObject.copyTo(albumCopy, userName);
	
				//If we just copied the content object that is the thumbnail for this album, then set the newly assigned ID of the
				//copied content object to the new album's ThumbnailContentObjectId property.
				if ((this.thumbnailContentObjectId == contentObject.getId()) && (!(contentObject instanceof AlbumBo))){
					albumCopy.setThumbnailContentObjectId(copiedObject.getId());
					albumCopy.save();
				}
			}
		} catch (UnsupportedContentObjectTypeException | InvalidContentObjectException | InvalidAlbumException | IOException | UnsupportedImageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return albumCopy;
	}

	//#endregion

	//#region Public Static Methods

	/// <summary>
	/// Assign a thumbnail image to the album. Use the thumbnail image of the first content object in the album or,
	/// if no objects exist in the album, the first image in any child albums, searching recursively. If no images
	/// can be found, set <see cref="ThumbnailContentObjectId" /> = 0.
	/// </summary>
	/// <param name="album">The album whose thumbnail image is to be assigned.</param>
	/// <param name="recursivelyAssignParentAlbums">Specifies whether to recursively iterate through the
	/// parent, grandparent, and so on until the root album, assigning a thumbnail, if necessary, to each
	/// album along the way.</param>
	/// <param name="recursivelyAssignChildrenAlbums">Specifies whether to recursively iterate through
	/// all children albums of this album, assigning a thumbnail to each child album, if necessary, along
	/// the way.</param>
	/// <param name="userName">The user name for the logged on user. This is used for the audit fields.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
	public static void assignAlbumThumbnail(AlbumBo album, boolean recursivelyAssignParentAlbums, boolean recursivelyAssignChildrenAlbums, String userName) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException	{
		if (album == null)
			throw new ArgumentNullException("album");

		if (!album.getIsWritable()){
			try {
				album = CMUtils.loadAlbumInstance(album.getId(), false, true);
			} catch (UnsupportedImageTypeException | InvalidContentObjectException | InvalidGalleryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if ((!album.isRootAlbum()) && (!FileMisc.fileExists(album.getThumbnail().getFileNamePhysicalPath()))){
			album.setThumbnailContentObjectId(getIdOfFirstContentObject(album));
			HelperFunctions.updateAuditFields(album, userName);
			album.save();
		}

		if (recursivelyAssignChildrenAlbums){
			Iterable<ContentObjectBo> childAlbums = album.getChildContentObjects(ContentObjectType.Album).values();
			for (ContentObjectBo childAlbum : childAlbums){
				assignAlbumThumbnail((AlbumBo)childAlbum, false, recursivelyAssignChildrenAlbums, userName);
			}
		}

		if (recursivelyAssignParentAlbums){
			while (!(album.getParent() instanceof NullContentObject))	{
				AlbumBo.assignAlbumThumbnail((AlbumBo)album.getParent(), recursivelyAssignParentAlbums, false, userName);
				album = (AlbumBo)album.getParent();
			}
		}
	}

	private static long getIdOfFirstContentObject(AlbumBo album){
		long firstContentObjectId = 0;

		for (ContentObjectBo contentObject : album.getChildContentObjects(ContentObjectType.ContentObject).toSortedList()){
			if (!contentObject.getIsNew()) {// We might encounter new, unsaved objects while synchronizing. Need to skip these since their ID=Long.MIN_VALUE
				firstContentObjectId = contentObject.getId();
				break;
			}
		}

		if (firstContentObjectId == 0)	{
			for (ContentObjectBo childAlbum : album.getChildContentObjects(ContentObjectType.Album).toSortedList()){
				firstContentObjectId = getIdOfFirstContentObject((AlbumBo)childAlbum);
				if (firstContentObjectId > 0)
					break;
			}
		}

		return firstContentObjectId;
	}

	//#endregion

	//#region Private Functions

	private void verifyObjectIsInflated(String propertyValue){
		// If the String is empty, and this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		synchronized (_lock) {
			if (StringUtils.isBlank(propertyValue) && (!this.getIsNew()) && (!this.isInflated))	{
				inflate();
			}
		}
	}

	private void verifyObjectIsInflated(MetadataItemName propertyValue){
		// If no meta name has been specified, and this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		synchronized (_lock) {
			if ((propertyValue == MetadataItemName.NotSpecified) && (!this.getIsNew()) && (!this.isInflated)){
				inflate();
			}
		}
	}

	private void verifyObjectIsInflated(Boolean propertyValue){
		// If no value has been specified, and this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		synchronized (_lock) {
			if ((propertyValue == null) && (!this.getIsNew()) && (!this.isInflated)){
				inflate();
			}
		}
	}

	private void verifyObjectIsInflated(int propertyValue)	{
		// If the int = Long.MIN_VALUE, and this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		synchronized (_lock) {
			if ((propertyValue == Integer.MIN_VALUE) && (!this.getIsNew()) && (!this.isInflated))	{
				inflate();
			}
		}
	}

	private void verifyObjectIsInflated(Date propertyValue){
		// If the property value is not the default Date value, and this is not a new object,
		// and it has not been inflated from the database, go to the database and retrieve 
		// the info for this object.
		synchronized (_lock) {
			if ((propertyValue == DateUtils.MinValue) && (!this.getIsNew()) && (!this.isInflated)){
				inflate();
			}
		}
	}

	/// <summary>
	/// Verify the directory name for this album is valid by checking that it satisfies the max length criteria,
	/// OS requirements for valid directory names, and that the name is unique in the specified parent directory.
	/// If the DirectoryName property is empty, it is assigned the title value, shortening it if necessary. If the
	/// DirectoryName property is specified, its length is checked to ensure it does not exceed the configuration
	/// setting AlbumDirectoryNameLength. If it does, a BusinessException is thrown. 
	/// This function automatically removes invalid characters and generates a unique name if needed.
	/// </summary>
	/// <exception cref="MDS.EventLogs.CustomExceptions.BusinessException">Thrown when the DirectoryName
	/// property has a value and its length exceeds the value set in the AlbumDirectoryNameLength configuration setting.</exception>
	private void validateDirectoryName() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if ((this.isRootAlbum()) || (this.isVirtualAlbum))
			return;

		if (StringUtils.isBlank(this.directoryName)){
			this.directoryName = this.getTitle();
			String dirPath = this.getParent().getFullPhysicalPath();
			String dirName = this.directoryName;

			String newDirName = HelperFunctions.validateDirectoryName(dirPath, dirName, CMUtils.loadGallerySetting(this.galleryId).getDefaultAlbumDirectoryNameLength());

			if (!this.directoryName.equals(newDirName)){
				this.directoryName = newDirName;
			}
		}

		if (this.directoryName.length() > Constants.AlbumDirectoryNameLength)
			throw new BusinessException(MessageFormat.format("Invalid directory name. The maximum length for a directory name is {0} characters, but one was specified that is {1} characters. More info: album ID = {2}; album title = '{3}'", Constants.AlbumDirectoryNameLength, this.directoryName.length(), this.id, this.getTitle()));
	}

	private DisplayObject getDefaultAlbumThumbnail() throws InvalidGalleryException{
		String defaultFilename = StringUtils.EMPTY;

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(getGalleryId());

		int maxLength = gallerySetting.getMaxThumbnailLength();
		float ratio = gallerySetting.getEmptyAlbumThumbnailWidthToHeightRatio();

		int width, height;
		if (ratio > 1){
			width = maxLength;
			height = new Float((float)maxLength / ratio).intValue();
		}else{
			height = maxLength;
			width = new Float((float)maxLength * ratio).intValue();
		}

		NullContentObject nullContentObjectBo = new NullContentObject();
		DisplayObject albumThumbnail = DisplayObject.createInstance(nullContentObjectBo, defaultFilename, width, height, DisplayObjectType.Thumbnail, new NullDisplayObjectCreator());

		albumThumbnail.setContentObjectId(this.thumbnailContentObjectId);
		albumThumbnail.setFileNamePhysicalPath(defaultFilename);

		return albumThumbnail;
	}

	/// <summary>
	/// Validate album-specific fields before saving to data store.
	/// </summary>
	private static void validateAuditFields(){
	}

	/// <summary>
	/// Any roles explicitly assigned to the moved album automatically "follow" it to the new location.
	/// But if the moved album has an explicitly assigned role permission and also inherits that role in the 
	/// new location, then the explicit role assignment is removed. We do this to enforce the rule that 
	/// child albums are never explicitly assigned a role permission if an ancestor already has that permission.
	/// </summary>
	/// <param name="movedAlbum">The album that has just been moved to a new destination album.</param>
	private static void updateRoleSecurityForMovedAlbum(AlbumBo movedAlbum)	{
		for (MDSRole role : RoleUtils.loadMDSRoles()){
			// This role applies to this object.
			if (role.getRootAlbumIds().contains(movedAlbum.getId())){
				// The album is directly specified in this role, but if any of this album's new parents are explicitly
				// specified, then it is not necessary to specify it at this level. Iterate through all the album's new 
				// parent albums to see if this is the case.
				if (role.getAllAlbumIds().contains(movedAlbum.getParent().getId())){
					role.getRootAlbumIds().remove(movedAlbum.getId());
					role.save();
				}
			}
		}
	}

	/// <summary>
	/// Make sure the newly copied album has the same role permissions that are explicitly assigned to the 
	/// source album. Do not copy role permissions that are inherited in the source album.
	/// </summary>
	/// <param name="copiedAlbum">The album that was just copied.</param>
	/// <param name="sourceAlbum">The album the copy was made from.</param>
	private static void updateRoleSecurityForCopiedAlbum(AlbumBo copiedAlbum, AlbumBo sourceAlbum){
		for (MDSRole role : RoleUtils.loadMDSRoles())	{
			if (role.getRootAlbumIds().contains(sourceAlbum.getId())){
				// The original album is explicitly assigned this role, so assign it also to the copied album, unless
				// the copied album is already inheriting the role from an ancestor album.
				if (!role.getAllAlbumIds().contains(copiedAlbum.getParent().getId())){
					role.getRootAlbumIds().add(copiedAlbum.getId());
					role.save();
				}
			}
		}
	}

	/// <summary>
	/// Handles the asynchronous request to sort the current album, optionally persisting the changes to the data store and acting
	/// recursively. This function is a wrapper around the call to <see cref="Sort(boolean, String, boolean, boolean)" />, adding logging,
	/// error handling, and cache purging.
	/// </summary>
	/// <param name="persistToDataStore">if set to <c>true</c> persist the album and the new sequence of each child
	///   content object to the database.</param>
	/// <param name="userName">Name of the user. This is for auditing and used only when <paramref name="persistToDataStore" />
	///   is <c>true</c>; otherwise you may specify null.</param>
	/// <param name="isRecursive">If set to <c>true</c> act recursively on child albums. Defaults to <c>false</c>
	/// when not specified. This value is ignored when <paramref name="persistToDataStore" /> is <c>false</c>.</param>
	/// <param name="replaceChildSortFields">When <c>true</c>, replace the sort field and direction on child albums with 
	/// the values from the current album. This value is applied only when <paramref name="persistToDataStore" /> and
	/// <paramref name="isRecursive" /> are both <c>true</c>.</param>
	/// <exception cref="System.ArgumentException">Thrown when <paramref name="persistToDataStore" /> is <c>true</c>
	/// and <paramref name="userName" /> is null or empty.</exception>
	private void sortAsyncBegin(boolean persistToDataStore, String userName, boolean isRecursive, boolean replaceChildSortFields){
		try	{
			//EventLogController.RecordEvent(MessageFormat.format("INFO: Beginning sort of album {0} ({1}) by property '{2}' (ascending={3}; recursive={4}, replaceChildSortFields={5}).", Id, Title, SortByMetaName, SortAscending, isRecursive, replaceChildSortFields), EventType.Info, GalleryId, CMUtils.LoadGallerySettings(), AppSetting.Instance);

			sort(persistToDataStore, userName, isRecursive, replaceChildSortFields);

			//EventLogController.RecordEvent(MessageFormat.format("INFO: Successfully finished sorting album {0}.", Id), EventType.Info, GalleryId, CMUtils.LoadGallerySettings(), AppSetting.Instance);
		}catch (Exception ex){
			//EventLogController.RecordError(ex, AppSetting.Instance, GalleryId, CMUtils.LoadGallerySettings());
			//EventLogController.RecordEvent(MessageFormat.format("CANCELLED: The sorting of album '{0}' has been cancelled due to the previously logged error.", Id), EventType.Info, GalleryId, CMUtils.LoadGallerySettings(), AppSetting.Instance);
			//throw;
		}

		//HelperFunctions.PurgeCache();
	}

	//#endregion
	
	//#region Event Handlers

	@Override
	public void saving(ContentObjectEvent event) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// Raised after validation but before persisting to data store. This is our chance to do validation
		// for album-specific properties.
		if (this.getIsNew()) {
			validateAuditFields();

			validateDirectoryName();

			if ((StringUtils.isEmpty(this.getTitle())) && (!StringUtils.isEmpty(this.getDirectoryName()))){
				// No title is specified but we have a directory name. Use that for the title.
				this.setTitle(this.getDirectoryName());
			}
		}
	}

	@Override
	public void saved(ContentObjectEvent event) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// Raised after the album is persisted to the data store.
		this.fullPhysicalPathOnDisk = this.getFullPhysicalPath();

		// Delete all albums from the cache so they are reloaded from the data store.
		//HelperFunctions.CacheManager.Remove(CacheItem.Albums.ToString());
	}

	//#endregion

}