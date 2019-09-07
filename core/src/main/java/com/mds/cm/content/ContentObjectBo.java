package com.mds.cm.content;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import com.mds.common.Constants;
import com.mds.cm.content.nullobjects.NullContentObject;
import com.mds.cm.content.nullobjects.NullDisplayObject;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.metadata.ContentObjectMetadataItem;
import com.mds.cm.metadata.ContentObjectMetadataItemCollection;
import com.mds.cm.metadata.MetaValue;
import com.mds.cm.metadata.MetadataDefinition;
import com.mds.cm.metadata.MetadataDefinitionCollection;
import com.mds.cm.metadata.MetadataReadWriter;
import com.mds.cm.model.Album;
import com.mds.common.utils.Reflections;
import com.mds.core.ApprovalStatus;
import com.mds.core.ContentObjectRotation;
import com.mds.core.ContentObjectType;
import com.mds.core.DisplayObjectType;
import com.mds.core.LongCollection;
import com.mds.core.MetadataItemName;
import com.mds.core.Orientation;
import com.mds.core.exception.NotSupportedException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.BusinessException;
import com.mds.cm.util.CMUtils;
import com.mds.util.DateUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.util.HtmlValidator;
import com.mds.util.MathUtil;
import com.mds.util.StringUtils;
import com.mds.i18n.util.I18nUtils;

/// <summary>
/// Represents a content object, which is an item that is managed by MDS System. Examples include
/// albums, images, videos, audio files, and documents.
/// </summary>
public abstract class ContentObjectBo  implements Serializable, Comparable<ContentObjectBo>, ContentObjectListener{
	//#region Private Fields

	private static Pattern metaRegEx;

	protected boolean isNew;
	protected boolean isInflated;
	protected long id;
	protected long galleryId;
	protected boolean galleryIdHasChanged;
	protected int sequence;
	protected Date dateAdded;
	protected boolean hasChanges;
	protected boolean regenerateThumbnailOnSave;
	protected boolean regenerateOptimizedOnSave;
	protected DisplayObject thumbnail;
	protected DisplayObject optimized;
	protected DisplayObject original;
	protected ContentObjectBo parent;
	protected SaveBehavior saveBehavior;
	protected DeleteBehavior deleteBehavior;
	protected MetadataDefinitionCollection metaDefinitions;
	protected ContentObjectMetadataItemCollection metadataItems;
	protected ContentObjectApprovalCollection approvalItems;
	protected ContentObjectRotation rotation = ContentObjectRotation.NotSpecified;
	protected ApprovalStatus approvalStatus = ApprovalStatus.NotSpecified;
	protected String createdByUserName;
	protected String lastModifiedByUserName;
	protected Date dateLastModified;
	protected boolean isPrivate;
	protected boolean isSynchronized;
	protected boolean isWritable;
	
	private final Object _lock = new Object();

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectBo"/> class.
	/// </summary>
	protected ContentObjectBo()	{
		this.parent = new NullContentObject();
		this.thumbnail = new NullDisplayObject();
		this.optimized = new NullDisplayObject();
		this.original = new NullDisplayObject();

		// Default IsSynchronized to false. It is set to true during a synchronization.
		this.isSynchronized = false;
		this.isWritable = true;
		
		listeners.add(this);
	}
	
	public ContentObjectBo(String nullContentObject){	
	}

	//#endregion

	//#region Public Properties

	/// <summary>
	/// Gets or sets the unique identifier for this content object.
	/// </summary>
	/// <value>The unique identifier for this content object.</value>
	public long getId(){
			return this.id;
	}
	
	public void setId(long id) {
		this.isNew = (id == Long.MIN_VALUE ? true : false);
		this.hasChanges = (this.id == id ? this.hasChanges : true);
		this.id = id;
	}

	/// <summary>
	/// Gets or sets the value that uniquely identifies the current gallery.
	/// </summary>
	/// <value>The value that uniquely identifies the current gallery.</value>
	public long getGalleryId() {
		return this.galleryId;
	}
	
	public void setGalleryId(long galleryId) {
		// Check if item is being assigned to another gallery, and set flag if it is. This will
		// be used to ensure data integrity. For example, when the flag is true, and an album is
		// being saved, all child albums will also be updated to the new gallery.
		if (this.galleryId > 0 && this.galleryId != galleryId)
			galleryIdHasChanged = true;

		this.galleryId = galleryId;
	}

	/// <summary>
	/// Gets a value that indicates whether a different gallery has been assigned
	/// to this object since it was retrieved from the data store. It is <c>false</c> at all
	/// other times, including once the new gallery assignment is persisted.
	/// </summary>
	/// <value>
	/// The value that indicates whether a different gallery has been assigned
	/// to this object since it was retrieved from the data store.
	/// </value>
	public boolean isGalleryIdHasChanged()	{
		return this.galleryIdHasChanged;
	}

	/// <summary>
	/// Gets or sets the object that contains this content object.
	/// </summary>
	/// <value>The object that contains this content object.</value>
	/// <exception cref="ArgumentNullException">Thrown when setting this property to a null value.</exception>
	public ContentObjectBo getParent(){
		return this.parent;
	}
	
	public void setParent(ContentObjectBo parent){
		if (parent == null)
			throw new ArgumentNullException("parent", I18nUtils.getMessage("Resources.ContentObjectBoParent_Ex_Msg"));

		this.hasChanges = (this.parent == parent ? this.hasChanges : true);
		this.parent.removeContentObject(this);
		parent.doAddContentObject(this);
		this.parent = parent;

		recalculateFilePaths();
	}

	/// <summary>
	/// Gets or sets the title for this content object. This property is a pass-through to the 
	/// underlying <see cref="MetadataItemName.Title" /> item in the 
	/// <see cref="ContentObjectBo.metadataItems" /> collection.
	/// </summary>
	/// <value>The title for this content object.</value>
	public String getTitle(){
		ContentObjectMetadataItem metaItem = getMetadataItems().tryGetMetadataItem(MetadataItemName.Title);
		if (metaItem != null)
			return metaItem.getValue();
		else{
			//throw new BusinessException(MessageFormat.format("No meta item 'ContentObjectTitle' exists for content object {0} ({1}).", Id, ContentObjectType));
			return StringUtils.EMPTY;
		}
	}
	
	public void setTitle(String title) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		ContentObjectMetadataItem metaItem = getMetadataItems().tryGetMetadataItem(MetadataItemName.Title);
		if (metaItem != null)
		{
			metaItem.setValue(title);
			this.hasChanges = metaItem.getHasChanges();
		}else{
			ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
			MetadataDefinition metadataDef = getMetaDefinitions().find(MetadataItemName.Title);
			metaItems.add(CMUtils.createMetadataItem(Long.MIN_VALUE, this, null, title, true, metadataDef));
			addMeta(metaItems);
			this.hasChanges = true;
		}
	}

	/// <summary>
	/// Gets or sets a long description for this content object. This property is a pass-through to the 
	/// underlying <see cref="MetadataItemName.Caption" /> item in the 
	/// <see cref="ContentObjectBo.metadataItems" /> collection.
	/// </summary>
	/// <value>The long description for this content object.</value>
	public String getCaption()	{
		ContentObjectMetadataItem metaItem = getMetadataItems().tryGetMetadataItem(MetadataItemName.Caption);
		if (metaItem != null)
			return metaItem.getValue();
		else
			//throw new BusinessException(MessageFormat.format("No meta item 'Caption' exists for content object {0} ({1}).", Id, ContentObjectType));
			return StringUtils.EMPTY;
	}
	
	public void setCaption(String caption) throws UnsupportedContentObjectTypeException, InvalidGalleryException {
		ContentObjectMetadataItem metaItem = getMetadataItems().tryGetMetadataItem(MetadataItemName.Caption);
		if (metaItem != null){
			metaItem.setValue(caption);
			hasChanges = metaItem.getHasChanges();
		}else{
			ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
			MetadataDefinition metadataDef = getMetaDefinitions().find(MetadataItemName.Caption);
			metaItems.add(CMUtils.createMetadataItem(Long.MIN_VALUE, this, null, caption, true, metadataDef));
			addMeta(metaItems);
			this.hasChanges = true;
		}
	}

	/// <summary>
	/// Gets or sets a value indicating whether this object has changes that have not been persisted to the database.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance has changes; otherwise, <c>false</c>.
	/// </value>
	public boolean getHasChanges(){
		return this.hasChanges;
	}
	
	public void setHasChanges(boolean hasChanges){
		this.hasChanges = hasChanges;
	}

	/// <summary>
	/// Gets a value indicating whether this object is new and has not yet been persisted to the data store.
	/// </summary>
	/// <value><c>true</c> if this instance is new; otherwise, <c>false</c>.</value>
	public boolean getIsNew(){
		return this.isNew;
	}
	
	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}

	/// <summary>
	/// Gets or sets a value indicating whether this object has been fully populated with data from the data store.
	/// Once assigned a true value, it remains true for the lifetime of the object. Returns false for newly created 
	/// objects that have not been saved to the data store. Set to <c>true</c> after an object is saved if it hadn't 
	/// already been set to <c>true</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance is inflated; otherwise, <c>false</c>.
	/// </value>
	public boolean getIsInflated(){
		return this.isInflated; 
	}
	
	public void setIsInflated(boolean isInflated){
		if (this.isInflated){
			throw new UnsupportedOperationException(I18nUtils.getMessage("ContentObject.isInflated_Ex_Msg"));
		}

		this.isInflated = isInflated;
	}

	/// <summary>
	/// Gets or sets the thumbnail information for this content object.
	/// </summary>
	/// <value>The thumbnail information for this content object.</value>
	public DisplayObject getThumbnail() throws InvalidGalleryException	{
		verifyThumbnailIsInflated(this.thumbnail);

		return this.thumbnail;
	}
	
	public void setThumbnail(DisplayObject thumbnail)	{
		if (thumbnail == null)
			throw new BusinessException("Attempted to set ContentObjectBo.Thumbnail to null for MOID " + this.id);

		this.hasChanges = (this.thumbnail == thumbnail ? this.hasChanges : true);
		this.thumbnail = thumbnail;
	}

	/// <summary>
	/// Gets or sets the optimized information for this content object.
	/// </summary>
	/// <value>The optimized information for this content object.</value>
	public DisplayObject getOptimized()	{
		return this.optimized;
	}
	
	public void setOptimized(DisplayObject optimized)	{
		if (optimized == null)
			throw new BusinessException("Attempted to set ContentObjectBo.Optimized to null for MOID " + this.id);

		this.hasChanges = (this.optimized == optimized ? this.hasChanges : true);
		this.optimized = optimized;
	}

	/// <summary>
	/// Gets or sets the information representing the original content object. (For example, the uncompressed photo, or the video / audio file.)
	/// </summary>
	/// <value>The information representing the original content object.</value>
	public DisplayObject getOriginal() {
		return this.original;
	}
	
	public void setOriginal(DisplayObject original) {
		if (original == null)
			throw new BusinessException("Attempted to set ContentObjectBo.Original to null for MOID " + this.id);

		this.hasChanges = (this.original == original ? this.hasChanges : true);
		this.original = original;
	}

	/// <summary>
	/// Gets the physical path to this object. Does not include the trailing slash.
	/// Example: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets
	/// </summary>
	/// <value>The full physical path to this object.</value>
	public String getFullPhysicalPath()	throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		return this.parent.getFullPhysicalPath();
	}

	/// <summary>
	/// Gets or sets the full physical path for this object as it currently exists on the hard drive. This property
	/// is updated when the object is loaded from the hard drive and when it is saved to the hard drive.
	/// <note type="caution"> Do not set this property from any class other than one that implements <see cref="ContentObjectBo"/>!
	/// Does not include the trailing slash.
	/// Example: C:\Inetpub\wwwroot\MDS\contentobjects\Summer 2005\sunsets\desert sunsets</note>
	/// </summary>
	/// <value>The full physical path on disk.</value>
	public String getFullPhysicalPathOnDisk(){
		return this.parent.getFullPhysicalPathOnDisk();
	}
	
	public void setFullPhysicalPathOnDisk(String fullPhysicalPathOnDisk){
		throw new NotSupportedException();
	}

	/// <summary>
	/// Gets the content object type.
	/// </summary>
	/// <value>
	/// An instance of <see cref="ContentObjectType" />.
	/// </value>
	public abstract ContentObjectType getContentObjectType();

	/// <summary>
	/// Gets the content object approval status.
	/// </summary>
	/// <value>An instance of <see cref="ContentObjectApproval" />.</value>
	/*public ContentObjectApproval ApprovalStatus
	{
		get
		{
			if (ApprovalItems == null || ApprovalItems.Count() == 0)
			{
				return ContentObjectApproval.NotSpecified;
			}
			else
			{
				var approval = (from approvalItem in ApprovalItems orderby approvalItem.dtLastModify descending select approvalItem).FirstOrDefault();

				return approval.ApprovalStatus;
			}
		}
	}*/

	/// <summary>
	/// Gets the MIME type for this content object. The MIME type is determined from the extension of the Filename on the <see cref="Original" /> property.
	/// </summary>
	/// <value>The MIME type for this content object.</value>
	public MimeTypeBo getMimeType(){
		return this.original.getMimeType();
	}

	/// <summary>
	/// Gets or sets the sequence of this content object within the containing album.
	/// </summary>
	/// <value>The sequence of this content object within the containing album.</value>
	public int getSequence(){
		verifyObjectIsInflated(this.sequence);
		return this.sequence;
	}
	
	public void setSequence(int sequence){
		this.hasChanges = (this.sequence == sequence ? this.hasChanges : true);
		this.sequence = sequence;
	}

	/// <summary>
	/// Gets or sets the date this content object was created.
	/// </summary>
	/// <value>The date this content object was created.</value>
	public Date getDateAdded()	{
		verifyObjectIsInflated(this.dateAdded);
		return this.dateAdded;
	}
	
	public void setDateAdded(Date dataAdd)	{
		this.hasChanges = (this.dateAdded == dataAdd ? this.hasChanges : true);
		this.dateAdded = dataAdd;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the thumbnail file is regenerated and overwritten on the file system. This value does not affect whether or how the data store is updated during a Save operation. This property is ignored for instances of the <see cref="Album" /> class.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the thumbnail file is regenerated and overwritten on the file system when this object is saved; otherwise, <c>false</c>.
	/// </value>
	public boolean getRegenerateThumbnailOnSave(){
			return this.regenerateThumbnailOnSave;
	}
	
	public void setRegenerateThumbnailOnSave(boolean regenerateThumbnailOnSave){
			this.hasChanges = (this.regenerateThumbnailOnSave == regenerateThumbnailOnSave ? this.hasChanges : true);
			this.regenerateThumbnailOnSave = regenerateThumbnailOnSave;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the optimized file is regenerated and overwritten on the file system during a Save operation. This value does not affect whether or how the data store is updated. This property is ignored for instances of the <see cref="Album" /> class.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the optimized file is regenerated and overwritten on the file system when this object is saved; otherwise, <c>false</c>.
	/// </value>
	public boolean getRegenerateOptimizedOnSave()	{
		return this.regenerateOptimizedOnSave;
	}
	
	public void setRegenerateOptimizedOnSave(boolean regenerateOptimizedOnSave)	{
		this.hasChanges = (this.regenerateOptimizedOnSave == regenerateOptimizedOnSave ? this.hasChanges : true);
		this.regenerateOptimizedOnSave = regenerateOptimizedOnSave;
	}

	///// <summary>
	///// Gets or sets a value indicating whether, during a <see cref="Save" /> operation, metadata embedded in the original content object file is
	///// extracted and persisted to the data store, overwriting any previous extracted metadata. This property is a pass-through
	///// to the <see cref="ContentObjectMetadataItemCollection.ExtractOnSave" /> property of the <see cref="metadataItems" /> 
	///// property of this object, which in turn is calculated based on the <see cref="ContentObjectMetadataItem.ExtractFromFileOnSave" />
	///// property on each metadata item in the collection. Specifically, this property returns true if <see cref="ContentObjectMetadataItem.ExtractFromFileOnSave" /> =
	///// true for *every* metadata item in the collection; otherwise it returns false. Setting this property causes the
	///// <see cref="ContentObjectMetadataItem.ExtractFromFileOnSave" /> property to be set to the specified value for *every* metadata item in the collection.
	///// This property is ignored for Albums.
	///// </summary>
	///// <value>
	///// 	<c>true</c> if metadata embedded in the original content object file is
	///// extracted and persisted to the data store when this object is saved; otherwise, <c>false</c>.
	///// </value>
	//public boolean ExtractMetadataOnSave
	//{
	//	get
	//	{
	//		return this.metadataItems.ExtractOnSave;
	//	}
	//	set
	//	{
	//		this.hasChanges = (this.metadataItems.ExtractOnSave == value ? this.hasChanges : true);
	//		this.metadataItems.ExtractOnSave = value;
	//	}
	//}

	/// <summary>
	/// Gets or sets a value indicating whether the current object is synchronized with the data store.
	/// This value is set to false at the beginning of a synchronization and set to true when it is
	/// synchronized with its corresponding file(s) on disk. At the conclusion of the synchronization,
	/// all objects where IsSynchronized = false are deleted. This property defaults to true for new instances.
	/// This property is not persisted in the data store, as it is only relevant during a synchronization.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance is synchronized; otherwise, <c>false</c>.
	/// </value>
	public boolean getIsSynchronized()	{
		return this.isSynchronized;
	}
	
	public void setIsSynchronized(boolean isSynchronized)	{
		this.isSynchronized = isSynchronized;
	}

	protected MetadataReadWriter metadataReadWriter;
	/// <summary>
	/// Gets or sets the behavior for reading and writing file metadata.
	/// </summary>
	/// <value>The metadata read/writer behavior.</value>
	public MetadataReadWriter getMetadataReadWriter() {
		return this.metadataReadWriter; 
	}
	
	public void setMetadataReadWriter(MetadataReadWriter metadataReadWriter) {
		this.metadataReadWriter = metadataReadWriter;
	}

	/// <summary>
	/// Gets the metadata definitions. These are used to determine which metadata to create for new
	/// objects and what their behavior should be.
	/// </summary>
	/// <value>An instance of <see cref="IMetadataDefinitionCollection" />.</value>
	public MetadataDefinitionCollection getMetaDefinitions() throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if (this.metaDefinitions == null) {
			this.metaDefinitions = CMUtils.loadGallerySetting(this.galleryId).getMetadataDisplaySettings();
		}
		
		return this.metaDefinitions;
	}

	/// <summary>
	/// Gets the metadata items associated with this content object.
	/// </summary>
	/// <value>The metadata items.</value>
	public ContentObjectMetadataItemCollection getMetadataItems(){
		if (metadataItems == null || metadataItems.size() == 0)	{
			// Only verify inflation when there aren't any meta items. We can't rely on the IsNew or
			// IsInflated properties inside verifyObjectIsInflated() because this property will be 
			// called during a save operation after the content object ID has been assigned, which 
			// causes IsNew to switch to false.
			verifyObjectIsInflated();
		}

		if (metadataItems == null)	{
			metadataItems = CMUtils.createMetadataCollection();
		}

		return this.metadataItems;
	}

	/// <summary>
	/// Gets the approval items associated with this content object.
	/// </summary>
	/// <value>The approval items.</value>
	public ContentObjectApprovalCollection getApprovalItems(){
		if (approvalItems == null){
			approvalItems = CMUtils.createApprovalCollection();
		}

		return this.approvalItems;
	}

	/// <summary>
	/// Gets or sets the amount of rotation to be applied to this content object when it is saved. Applies only to <see cref="Image" />
	/// and <see cref="Video" /> objects; all others throw a <see cref="NotSupportedException" />.
	/// </summary>
	/// <value>
	/// The amount of rotation to be applied to this content object when it is saved.
	/// </value>
	/// <exception cref="System.NotSupportedException">Thrown when an inherited type does not allow rotation.</exception>
	public ContentObjectRotation getRotation()	{
		return this.rotation;
	}
	
	public void setRotation(ContentObjectRotation rotation)	{
		if (this.rotation != rotation){
			this.hasChanges = true;
			this.rotation = rotation;
		}
	}
	
	/// <summary>
	/// Gets the approval staus associated with this content object.
	/// </summary>
	/// <value>The approval status.</value>
	public ApprovalStatus getApprovalStatus()	{
		return this.approvalStatus;
	}
	
	public void setApprovalStatus(ApprovalStatus approvalStatus)	{
		if (this.approvalStatus != approvalStatus){
			this.hasChanges = true;
			this.approvalStatus = approvalStatus;
		}
	}

	/// <summary>
	/// Gets or sets a value indicating whether the current instance can be modified. Objects that are stored in a cache must
	/// be treated as read-only. Only objects that are instantiated right from the database and not shared across threads
	/// should be updated.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance can be modified; otherwise, <c>false</c>.
	/// </value>
	public boolean getIsWritable()	{
		return this.isWritable;
	}
	
	public void setIsWritable(boolean isWritable)	{
		this.isWritable = isWritable;
	}

	/// <summary>
	/// Gets or sets the user name of the user who created this content object.
	/// </summary>
	/// <value>The name of the created by user.</value>
	public String getCreatedByUserName(){
			verifyObjectIsInflated(this.createdByUserName);
			return this.createdByUserName;
	}
	
	public void setCreatedByUserName(String createdByUserName){
			this.hasChanges = (this.createdByUserName != null && this.createdByUserName.equals(createdByUserName) ? this.hasChanges : true);
			this.createdByUserName = createdByUserName;
	}

	/// <summary>
	/// Gets or sets the user name of the user who last modified this content object.
	/// </summary>
	/// <value>The user name of the user who last modified this object.</value>
	public String getLastModifiedByUserName()	{
			verifyObjectIsInflated(this.lastModifiedByUserName);
			return this.lastModifiedByUserName;
	}
	
	public void setLastModifiedByUserName(String lastModifiedByUserName)	{
			this.hasChanges = (this.lastModifiedByUserName != null && this.lastModifiedByUserName.equals(lastModifiedByUserName) ? this.hasChanges : true);
			this.lastModifiedByUserName = lastModifiedByUserName;
	}

	/// <summary>
	/// Gets or sets the date and time this content object was last modified.
	/// </summary>
	/// <value>The date and time this content object was last modified.</value>
	public Date getDateLastModified(){
			verifyObjectIsInflated(this.dateLastModified);
			return this.dateLastModified;
	}
	
	public void setDateLastModified(Date dateLastModified){
			this.hasChanges = (this.dateLastModified == dateLastModified ? this.hasChanges : true);
			this.dateLastModified = dateLastModified;
	}

	/// <summary>
	/// Gets or sets a value indicating whether this content object is hidden from anonymous users.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance is private; otherwise, <c>false</c>.
	/// </value>
	public boolean getIsPrivate(){
		verifyObjectIsInflated();
		return this.isPrivate;
	}
	
	public void setIsPrivate(boolean isPrivate){
		this.hasChanges = (this.isPrivate == isPrivate ? this.hasChanges : true);
		this.isPrivate = isPrivate;
	}

	//#endregion

	/// <summary>
	/// Gets a <see cref="System.Text.RegularExpressions.Regex" /> instance that can be used to match the replacement tokens
	/// in the metadata definition's default value settings.
	/// </summary>
	/// <value>A  <see cref="System.Text.RegularExpressions.Regex" /> instance.</value>
	private static Pattern getMetaRegEx()	{	
		if (metaRegEx == null)	{
			metaRegEx = Pattern.compile(getMetadataRegExPattern());
		}

		return metaRegEx;
	}

	/// <summary>
	/// Gets an array of the required metadata items that all content objects must possess.
	/// </summary>
	/// <value>An array of <see cref="MetadataItemName" /> instances.</value>
	private static MetadataItemName[] requiredMetadataItems(){
		return new MetadataItemName[] { MetadataItemName.Title, MetadataItemName.Caption };//, MetadataItemName.Approval, MetadataItemName.ApprovalDate 
	}

	//#endregion
	
	//#region Public Events
	protected List<ContentObjectListener> listeners = new ArrayList<>();

    public void addContentObjectListener(ContentObjectListener listener) {
        listeners.add(listener);
    }

    public void removeContentObjectListener(ContentObjectListener listener) {
        listeners.remove(listener);
    }

	/// <summary>
	/// Occurs when the <see cref="Save"/> method has been invoked, but before the object has been saved. Validation within
	/// the GalleryObject class has occured prior to this event.
	/// </summary>
	//public event System.EventHandler Saving;

	/// <summary>
	/// Occurs when the <see cref="Save"/> method has been invoked and after the object has been saved.
	/// </summary>
	//public event System.EventHandler Saved;

	/// <summary>
	/// Occurs after a metadata item has been created for an object but before it has been added to
	/// the <see cref="MetadataItems" /> collection.
	/// </summary>
	//public event EventHandler<AddMetaEventArgs> BeforeAddMetaItem;

	//#endregion

	//#region Public Virtual Methods (throw exception)

	/// <summary>
	/// Adds the specified content object as a child of this content object.
	/// </summary>
	/// <param name="contentObject">The ContentObjectBo to add as a child of this
	/// content object.</param>
	/// <exception cref="System.NotSupportedException">Thrown when an inherited type
	/// does not allow the addition of child content objects.</exception>
	public void addContentObject(ContentObjectBo contentObject)	{
		throw new NotSupportedException();
	}

	/// <summary>
	/// Adds the specified content object as a child of this content object. This method is called by the <see cref="AddContentObjectBo"/> method and should not be called directly.
	/// </summary>
	/// <param name="ContentObjectBo">The content object to add as a child of this content object.</param>
	public void doAddContentObject(ContentObjectBo contentObject)	{
		throw new NotSupportedException();
	}

	/// <summary>
	/// Removes the specified content object from the collection of child objects
	/// of this content object.
	/// </summary>
	/// <param name="ContentObjectBo">The ContentObjectBo to remove as a child of this
	/// content object.</param>
	/// <exception cref="System.NotSupportedException">Thrown when an inherited type
	/// does not allow the addition of child content objects.</exception>
	/// <exception cref="System.ArgumentException">Thrown when the specified
	/// content object is not child of this content object.</exception>
	public void removeContentObject(ContentObjectBo contentObject)	{
		throw new NotSupportedException();
	}

	/// <summary>
	/// Returns an unsorted collection of content objects that are direct children of the current content object or
	/// an empty list (Count = 0) if there are no child objects. Use the <paramref name="excludePrivateObjects" />
	/// parameter to optionally filter out private objects (if not specified, private objects are returned).
	/// </summary>
	/// <param name="ContentObjectType">A <see cref="ContentObjectType" /> enum indicating the
	/// desired type of child objects to return.</param>
	/// <param name="excludePrivateObjects">Indicates whether to exclude objects that are marked as private
	/// (<see cref="ContentObjectBo.IsPrivate" /> = <c>true</c>). Objects that are private should not be shown to anonymous users.</param>
	/// <returns>An instance of <see cref="ContentObjectBoCollection" />.</returns>
	/// <exception cref="System.NotSupportedException"></exception>
	public ContentObjectBoCollection getChildContentObjects(){
		return getChildContentObjects(ContentObjectType.All, false);
	}
	
	public ContentObjectBoCollection getChildContentObjects(ContentObjectType contentObjectType){
		return getChildContentObjects(contentObjectType, false);
	}
	
	public ContentObjectBoCollection getChildContentObjects(ContentObjectType contentObjectType, boolean excludePrivateObjects)	{
		return getChildContentObjects(contentObjectType, ApprovalStatus.All, false);
	}
	
	public ContentObjectBoCollection getChildContentObjects(ContentObjectType contentObjectType, ApprovalStatus approvalStatus, boolean excludePrivateObjects){
		throw new NotSupportedException();
	}

	/// <summary>
	/// Adds the specified metadata item to this content object.
	/// </summary>
	/// <param name="metaItems">An instance of <see cref="ContentObjectMetadataItemCollection" /> 
	/// containing the items to add to this content object.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="metaItems" /> is null.</exception>
	public void addMeta(ContentObjectMetadataItemCollection metaItems) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (metaItems == null)
			throw new ArgumentNullException("metaItems");

		if (metadataItems == null)
			metadataItems = metaItems;
		else
			metadataItems.addRange(metaItems);

		metadataItems.applyDisplayOptions(this.getMetaDefinitions());
	}
	
	public void replaceMeta(ContentObjectMetadataItemCollection metaItems) {
		if (metaItems == null)
			throw new ArgumentNullException("metaItems");

		metadataItems = metaItems;
	}

	/// <summary>
	/// Adds the specified approval item to this content object.
	/// </summary>
	/// <param name="approvalItems">An instance of <see cref="ContentObjectApprovalCollection" /> 
	/// containing the items to add to this content object.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="approvalItems" /> is null.</exception>
	public void addApproval(ContentObjectApprovalCollection approvalItems){
		if (approvalItems == null)
			throw new ArgumentNullException("approvalItems");

		if (this.approvalItems == null)
			this.approvalItems = approvalItems;
		else
			this.approvalItems.addAll(approvalItems);
	}

	//#endregion

	//#region Protected Virtual Methods

	/// <summary>
	/// This method provides an opportunity for a derived class to verify the thumbnail information for this instance has 
	/// been retrieved from the data store. This method is empty.
	/// </summary>
	/// <param name="thumbnail">A reference to the thumbnail display object for this instance.</param>
	protected void verifyThumbnailIsInflated(DisplayObject thumbnail) throws InvalidGalleryException{
		// Overridden in Album class.
	}

	/// <summary>
	/// Verifies the sequence of this instance within the album has been assigned. If the sequence has not yet been assigned, 
	/// default it to 1 higher than the highest sequence among its brothers and sisters.
	/// </summary>
	protected void validateSequence(){
		if (this.sequence == Integer.MIN_VALUE)	{
			this.sequence = this.parent.getChildContentObjects().stream().mapToInt(g->g.getSequence()).max().orElse(0) + 1;
		}
	}

	/// <summary>
	/// Verifies that the thumbnail image for this instance maps to an existing image file on disk. If not, set the
	///  <see cref="RegenerateThumbnailOnSave" />
	/// property to true so that the thumbnail image is created during the <see cref="Save" /> operation.
	/// <note type="implementnotes">The <see cref="Album" /> class overrides this method with an empty implementation, because albums don't have thumbnail
	/// images, at least not in the strictest sense.</note>
	/// </summary>
	protected void checkForThumbnailImage() throws InvalidGalleryException	{
		if (!FileMisc.fileExists(this.getThumbnail().getFileNamePhysicalPath())){
			this.regenerateThumbnailOnSave = true;
		}
	}

	///// <summary>
	///// Set the title for this instance based on the title metadata item, if present. No action is 
	///// taken if the metadata item doesn't exist.
	///// </summary>
	//protected void SetTitle()
	//{
	//	ContentObjectMetadataItem metaItem;
	//	if (metadataItems.tryGetMetadataItem(MetadataItemName.Title, out metaItem))
	//	{
	//		this.Title = metaItem.Value;
	//	}
	//}

	/// <summary>
	/// This method provides an opportunity for a derived class to verify the optimized image maps to an existing file on disk.
	/// This method is empty.
	/// </summary>
	protected void checkForOptimizedImage()	{
		// Overridden in Image class.
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Persist this content object to the data store.
	/// </summary>
	public void save() throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException	{
		// Verify it is valid to save this object.
		validateSave();

		// Raise the Saving event.
		ContentObjectEvent event = new ContentObjectEvent(this, this);
        listeners.forEach(l -> {
			try {
				l.saving(event);
			} catch (UnsupportedContentObjectTypeException | InvalidGalleryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		/*if (Saving != null)
		{
			Saving(this, new EventArgs());
		}*/

		// Persist to data store if the object is new (has not yet been saved) or it
		// has unsaved changes. The save behavior also updates the album's thumbnail if needed.
		if ((this.isNew) || (hasChanges))
			this.saveBehavior.save();

		this.hasChanges = false;
		this.galleryIdHasChanged = false;
		this.isNew = false;
		this.regenerateThumbnailOnSave = false;
		this.regenerateOptimizedOnSave = false;
		if (!this.isInflated)
			this.isInflated = true;

		validateThumbnailsAfterSave();

		// Raise the Saved event.
		listeners.forEach(l -> {
			try {
				l.saved(event);
			} catch (InvalidGalleryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		/*if (Saved != null)
		{
			Saved(this, new EventArgs());
		}*/
	}

	/// <summary>
	/// Permanently delete this object from the data store and disk.
	/// </summary>
	public void delete() throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException	{
		this.delete(true);
	}

	/// <summary>
	/// Permanently delete this object from the data store, but leave it's associated file or directory on the hard disk.
	/// </summary>
	public void deleteFromGallery() throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException	{
		this.delete(false);
	}

	/// <summary>
	/// Permanently delete the original file for this content object. Requires that an optimized version exists.
	/// If no optimized version exists, no action is taken.
	/// </summary>
	public void deleteOriginalFile() throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if (StringUtils.isBlank(getOptimized().getFileName()) || (this.getOriginal().getFileName().equalsIgnoreCase(this.getOptimized().getFileName()))){
			return; // No optimized version exists.
		}

		String originalPath = this.getOriginal().getFileNamePhysicalPath();
		String originalExtension = FileMisc.getExt(originalPath); // Ex: .bmp	
		String optimizedExtension = FileMisc.getExt(this.getOptimized().getFileNamePhysicalPath()); // Ex: .jpg

		// Delete the original this file
		FileMisc.deleteFile(originalPath);

		if (!originalExtension.equalsIgnoreCase(optimizedExtension)){
			// The original has a different file extension than the optimized, so update the original file name with 
			// the extension from the optimized file. For example, this can happen when the original does not end with
			// the .jpeg extension (it may be JPG, BMP, TIF, etc).
			originalPath = FileMisc.changeExt(originalPath, optimizedExtension);

			// Now validate that the new path is not already used by an existing file. For example, we might be renaming
			// zOpt_photo.jpeg to photo.jpg. If photo.jpg is already in use, we need to change it to something else.
			String dirPath = FilenameUtils.getPath(originalPath);
			String filename = FilenameUtils.getName(originalPath);
			String newFilename = HelperFunctions.validateFileName(dirPath, filename);

			if (!newFilename.equalsIgnoreCase(filename))
				originalPath = FilenameUtils.concat(dirPath, newFilename);
		}

		// Rename the optimized file to the original file. This is required because
		// optimized file names can be slightly different than the original file names. For example, optimized thiss
		// are prefixed with "zOpt_" and are always a JPEG file type, while the original does not have a special prefix
		// and may be BMP, TIF, etc.
		FileMisc.moveFile(this.optimized.getFileNamePhysicalPath(), originalPath);

		try {
			this.original.setFileInfo(new File(originalPath));
		} catch (InvalidContentObjectException | InvalidGalleryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//this.original.FileName = Path.GetFileName(originalPath);
		//this.original.setFileNamePhysicalPath(originalPath;

		
		try {
			this.optimized.setFileInfo(this.original.getFileInfo());
		} catch (InvalidContentObjectException | InvalidGalleryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.optimized.FileName = this.original.getFileName();
		//this.optimized.setFileNamePhysicalPath(this.original.getFileNamePhysicalPath();

		this.original.setWidth(this.optimized.getWidth());
		this.original.setHeight(this.optimized.getHeight());
		this.original.setFileSizeKB(this.optimized.getFileSizeKB());

		this.refreshMetadataAfterOriginalFileDeletion();
	}

	/// <summary>
	/// Permanently move the original file to data center for this content object. Requires that an optimized version exists.
	/// If no optimized version exists, no action is taken.
	/// </summary>
	public void approvalFileAction(ContentObjectApproval approval)	{
		/*if (approval == ContentObjectApproval.Approved)
		{
			String originalPath = this.original.getFileNamePhysicalPath();
			String originalExtension = Path.GetExtension(originalPath) ?? StringUtils.EMPTY; // Ex: .bmp
			String optimizedExtension = Path.GetExtension(this.optimized.getFileNamePhysicalPath()); // Ex: .jpg

			// Delete the original this file
			GallerySettings gallerySetting = CMUtils.LoadGallerySetting(GalleryId);
			String strDataPath = HelperFunctions.CalculateFullPath(gallerySetting.ContentAndSettingCenter, "Data");
			String strDest = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(originalPath, strDataPath, gallerySetting.getFullContentObjectPath());
			String strDestPath = Path.GetDirectoryName(strDest);
			try
			{
				if (!Directory.Exists(strDestPath))
				{
					Directory.CreateDirectory(strDestPath);
				}

				if (File.Exists(strDest))
					File.Delete(strDest);
				File.Move(originalPath, strDest);
			}
			catch { }
		}
		else if (approval == ContentObjectApproval.Unapprove)
		{
			String originalPath = this.original.getFileNamePhysicalPath();

			// Delete the original this file
			GallerySettings gallerySetting = CMUtils.LoadGallerySetting(GalleryId);
			String strDataPath = HelperFunctions.CalculateFullPath(gallerySetting.ContentAndSettingCenter, "Data");
			String strSource = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(originalPath, strDataPath, gallerySetting.getFullContentObjectPath());
			try
			{
				if (File.Exists(strSource))
				{
					if (File.Exists(originalPath))
						File.Delete(originalPath);

					File.Move(strSource, originalPath);
				}
			}
			catch { }
		}*/
	}
	
	/// <summary>
	/// Set the parent of this content object to an instance of <see cref="NullContentObjectBo" />.
	/// </summary>
	public void setParentToNullObject()	{
		this.parent = new NullContentObject();
	}

	/// <summary>
	/// Copy the current object and place it in the specified destination album. This method creates a completely separate copy
	/// of the original, including copying the physical files associated with this object. The copy is persisted to the data
	/// store and then returned to the caller.
	/// </summary>
	/// <param name="destinationAlbum">The album to which the current object should be copied.</param>
	/// <param name="userName">The user name of the currently logged on user. This will be used for the audit fields of the
	/// copied objects.</param>
	/// <returns>
	/// Returns a new content object that is an exact copy of the original, except that it resides in the specified
	/// destination album, and of course has a new ID.
	/// </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="destinationAlbum" /> is null.</exception>
	public ContentObjectBo copyTo(AlbumBo destinationAlbum, String userName) throws UnsupportedContentObjectTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, UnsupportedImageTypeException, InvalidGalleryException	{
		if (destinationAlbum == null)
			throw new ArgumentNullException("destinationAlbum");

		ContentObjectBo goCopy;

		String destPath = destinationAlbum.getFullPhysicalPathOnDisk();
		boolean doesOptimizedImageExistAndIsDifferentThanOriginalImage = (!StringUtils.isBlank(this.optimized.getFileName()) && (this.optimized.getFileName() != this.original.getFileName()));

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(destinationAlbum.getGalleryId());

		//#region Copy original file

		if (this.original.getDisplayType() == DisplayObjectType.External){
			goCopy = CMUtils.createContentObjectInstance(null, destinationAlbum, this.original.getExternalHtmlSource(), this.original.getExternalType());
		}else{
			String destOriginalFilename = HelperFunctions.validateFileName(destPath, this.original.getFileName());
			String destOriginalPath = FilenameUtils.concat(destPath, destOriginalFilename);
			FileMisc.copyFile(this.original.getFileNamePhysicalPath(), destOriginalPath);

			goCopy = CMUtils.createContentObjectInstance(destOriginalPath, destinationAlbum);
		}

		//#endregion

		//#region Copy optimized file

		// Determine path where optimized should be saved. If no optimized path is specified in the config file,
		// use the same directory as the original. Don't do anything if no optimized filename is specified or it's
		// the same file as the original.
		// FYI: Currently the optimized image is never external (only the original may be), but we test it anyway for future bullet-proofing.
		if ((this.optimized.getDisplayType() != DisplayObjectType.External) && doesOptimizedImageExistAndIsDifferentThanOriginalImage){
			String destOptimizedPathWithoutFilename = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(destPath, gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());
			String destOptimizedFilepath = FilenameUtils.concat(destOptimizedPathWithoutFilename, HelperFunctions.validateFileName(destOptimizedPathWithoutFilename, this.optimized.getFileName()));
			if (FileMisc.fileExists(this.optimized.getFileNamePhysicalPath())){
				FileMisc.copyFile(this.optimized.getFileNamePhysicalPath(), destOptimizedFilepath);
			}

			// Assign newly created copy of optimized image to the copy of our content object instance and update
			// various properties.
			goCopy.optimized.setFileInfo(new File(destOptimizedFilepath));
			goCopy.optimized.setWidth(this.optimized.getWidth());
			goCopy.optimized.setHeight(this.optimized.getHeight());
			goCopy.optimized.setFileSizeKB(this.optimized.getFileSizeKB());
		}

		//#endregion

		//#region Copy thumbnail file

		// Determine path where thumbnail should be saved. If no thumbnail path is specified in the config file,
		// use the same directory as the original.
		// FYI: Currently the thumbnail image is never external (only the original may be), but we test it anyway for future bullet-proofing.
		if (this.thumbnail.getDisplayType() != DisplayObjectType.External){
			String destThumbnailPathWithoutFilename = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(destPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
			String destThumbnailFilepath = FilenameUtils.concat(destThumbnailPathWithoutFilename, HelperFunctions.validateFileName(destThumbnailPathWithoutFilename, this.thumbnail.getFileName()));
			if (FileMisc.fileExists(this.thumbnail.getFileNamePhysicalPath())){
				FileMisc.copyFile(this.thumbnail.getFileNamePhysicalPath(), destThumbnailFilepath);
			}

			// Assign newly created copy of optimized image to the copy of our content object instance and update
			// various properties.
			goCopy.thumbnail.setFileInfo(new File(destThumbnailFilepath));
			goCopy.thumbnail.setWidth(this.thumbnail.getWidth());
			goCopy.thumbnail.setHeight(this.thumbnail.getHeight());
			goCopy.thumbnail.setFileSizeKB(this.thumbnail.getFileSizeKB());
		}

		//#endregion

		//goCopy.Title = this.Title;
		goCopy.setIsPrivate(this.isPrivate);

		goCopy.getMetadataItems().clear();
		goCopy.getMetadataItems().addRange(this.getMetadataItems().copy());

		// Associate the new meta items with the copied object.
		for (ContentObjectMetadataItem metadataItem : goCopy.getMetadataItems()){
			metadataItem.setContentObject(goCopy);
		}

		ContentObjectMetadataItem metaItem;
		if ((metaItem = goCopy.getMetadataItems().tryGetMetadataItem(MetadataItemName.DateAdded)) != null){
			metaItem.setValue(DateUtils.formatDate(DateUtils.Now(), gallerySetting.getMetadataDateTimeFormatString()));
		}

		if (!StringUtils.isBlank(goCopy.original.getFileName()) && (metaItem = goCopy.getMetadataItems().tryGetMetadataItem(MetadataItemName.FileName)) != null){
			metaItem.setValue(goCopy.original.getFileName());
		}

		HelperFunctions.updateAuditFields(goCopy, userName);
		goCopy.save();

		return goCopy;
	}

	/// <summary>
	/// Move the current object to the specified destination album. This method moves the physical files associated with this
	/// object to the destination album's physical directory. This instance's <see cref="Save" /> method is invoked to persist the changes to the
	/// data store. When moving albums, all the album's children, grandchildren, etc are also moved.
	/// </summary>
	/// <param name="destinationAlbum">The album to which the current object should be moved.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="destinationAlbum" /> is null.</exception>
	public void moveTo(AlbumBo destinationAlbum) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidContentObjectException, UnsupportedImageTypeException, IOException, InvalidGalleryException{
		if (destinationAlbum == null)
			throw new ArgumentNullException("destinationAlbum");

		// Get list of albums whose thumbnails we'll update after the move operation.
		LongCollection albumsNeedingNewThumbnails = getAlbumHierarchy(destinationAlbum.getId());

		String destPath = destinationAlbum.getFullPhysicalPathOnDisk();

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(destinationAlbum.getGalleryId());

		//#region Move original file

		String destOriginalPath = StringUtils.EMPTY;
		if (FileMisc.fileExists(this.original.getFileNamePhysicalPath()))
		{
			String destOriginalFilename = HelperFunctions.validateFileName(destPath, this.original.getFileName());
			destOriginalPath = FilenameUtils.concat(destPath, destOriginalFilename);
			FileMisc.moveFile(this.original.getFileNamePhysicalPath(), destOriginalPath);
		}

		//#endregion

		//#region Move optimized file

		// Determine path where optimized should be saved. If no optimized path is specified in the config file,
		// use the same directory as the original.
		String destOptimizedFilepath = StringUtils.EMPTY;
		if ((!StringUtils.isBlank(this.optimized.getFileName())) && (!this.optimized.getFileName().equals(this.original.getFileName()))){
			String destOptimizedPathWithoutFilename = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(destPath, gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());
			destOptimizedFilepath = FilenameUtils.concat(destOptimizedPathWithoutFilename, HelperFunctions.validateFileName(destOptimizedPathWithoutFilename, this.optimized.getFileName()));
			if (FileMisc.fileExists(this.optimized.getFileNamePhysicalPath()))
			{
				FileMisc.moveFile(this.optimized.getFileNamePhysicalPath(), destOptimizedFilepath);
			}
		}

		//#endregion

		//#region Move thumbnail file

		// Determine path where thumbnail should be saved. If no thumbnail path is specified in the config file,
		// use the same directory as the original.
		String destThumbnailPathWithoutFilename = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(destPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
		String destThumbnailFilepath = FilenameUtils.concat(destThumbnailPathWithoutFilename, HelperFunctions.validateFileName(destThumbnailPathWithoutFilename, this.thumbnail.getFileName()));
		if (FileMisc.fileExists(this.thumbnail.getFileNamePhysicalPath()))	{
			FileMisc.moveFile(this.thumbnail.getFileNamePhysicalPath(), destThumbnailFilepath);
		}

		//#endregion

		this.setParent(destinationAlbum);
		this.setGalleryId(destinationAlbum.getGalleryId());
		this.setIsPrivate(destinationAlbum.getIsPrivate());
		this.setSequence(Integer.MIN_VALUE); // Reset the sequence so that it will be assigned a new value placing it at the end.

		// Update the FileInfo properties for the original, optimized and thumbnail objects. This is necessary in order to update
		// the filename, in case they were changed because the destination directory already had files with the same name.
		if (FileMisc.fileExists(destOriginalPath))
			this.original.setFileInfo(new File(destOriginalPath));

		if (FileMisc.fileExists(destOptimizedFilepath))
			this.optimized.setFileInfo(new File(destOptimizedFilepath));

		if (FileMisc.fileExists(destThumbnailFilepath))
			this.thumbnail.setFileInfo(new File(destThumbnailFilepath));

		save();

		// Now assign new thumbnails (if needed) to the albums we moved FROM. (The thumbnail for the destination album was updated in 
		// the Save() method.)
		for (long albumId : albumsNeedingNewThumbnails)	{
			AlbumBo.assignAlbumThumbnail(CMUtils.loadAlbumInstance(albumId, false, true), false, false, this.lastModifiedByUserName);
		}
	}

	/// <summary>
	/// Build the set of metadata for the current content object and assign to the <see cref="MetadataItems" />
	/// property.
	/// </summary>
	public void extractMetadata() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// Iterate through the metadata definitions and add an instance for each one if appropriate.
		Collection<MetadataDefinition> metaDefs = getMetaDefinitions().values();
		for(MetadataDefinition metaDef : metaDefs){
			extractMetadata(metaDef);
		}

		getMetadataItems().applyDisplayOptions(getMetaDefinitions());
	}
	
	public void extractMetadata(MetadataDefinition metaDef) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if (metadataDefinitionApplies(metaDef))	{
			ContentObjectMetadataItem metaItem = createMetaItem(metaDef);

			// Raise the BeforeAddMetaItem event.
			AddMetaItemEvent event = new AddMetaItemEvent(this, metaItem);
			for(ContentObjectListener listener : listeners) {
				listener.onBeforeAddMetaItem(event);
				if (event.isCancel()){
					listener.removeMetadataItem(metaDef.getMetadataItemName());
					continue;
				}

				// Add/update the item, but only when it is defined as being editable or has a value.
				if (metaItem.getMetaDefinition().IsEditable || !StringUtils.isBlank(metaItem.getValue()))
					listener.updateInternalMetaItem(metaItem);
				else if (!ArrayUtils.contains(requiredMetadataItems(), metaDef.getMetadataItemName()))
					listener.removeMetadataItem(metaDef.getMetadataItemName());
			}
		}
		else
			removeMetadataItem(metaDef.getMetadataItemName());
	}

	/// <summary>
	/// Calculates the actual rotation amount that must be applied based on the user's requested rotation 
	/// and the file's actual orientation.
	/// </summary>
	/// <returns>An instance of <see cref="ContentObjectRotation" />.</returns>
	public ContentObjectRotation calculateNeededRotation(){
		Orientation fileRotation = getOrientation(); // Actual rotation of the original file, as discovered via orientation metadata
		ContentObjectRotation userRotation = this.rotation; // Desired rotation by the user

		if (userRotation == ContentObjectRotation.NotSpecified)	{
			userRotation = ContentObjectRotation.Rotate0;
		}

		switch (fileRotation){
			case None:
			case Normal:
				return userRotation;

			case Rotated90:
				switch (userRotation){
					case Rotate0: return ContentObjectRotation.Rotate270;
					case Rotate90: return ContentObjectRotation.Rotate0;
					case Rotate180: return ContentObjectRotation.Rotate90;
					case Rotate270: return ContentObjectRotation.Rotate180;
				}
				break;

			case Rotated180:
				switch (userRotation){
					case Rotate0: return ContentObjectRotation.Rotate180;
					case Rotate90: return ContentObjectRotation.Rotate270;
					case Rotate180: return ContentObjectRotation.Rotate0;
					case Rotate270: return ContentObjectRotation.Rotate90;
				}
				break;

			case Rotated270:
				switch (userRotation){
					case Rotate0: return ContentObjectRotation.Rotate90;
					case Rotate90: return ContentObjectRotation.Rotate180;
					case Rotate180: return ContentObjectRotation.Rotate270;
					case Rotate270: return ContentObjectRotation.Rotate0;
				}
				break;
		}

		return ContentObjectRotation.NotSpecified;
	}

	/// <summary>
	/// Gets the orientation of the original media file. The value is retrieved from the metadata value for 
	/// <see cref="MetadataItemName.Orientation" />. Returns <see cref="Orientation.None" /> if no orientation 
	/// metadata is found, which will be the case for any media file not having orientation metadata embedded
	/// in the media file.
	/// </summary>
	/// <returns>An instance of <see cref="Orientation" />.</returns>
	public Orientation getOrientation()	{
		ContentObjectMetadataItem orientationMeta;
		if ((orientationMeta = getMetadataItems().tryGetMetadataItem(MetadataItemName.Orientation)) != null && !orientationMeta.getIsDeleted()){
			int orientationRaw;
			if ((orientationRaw = NumberUtils.toInt(orientationMeta.getRawValue(), Integer.MIN_VALUE)) != Integer.MIN_VALUE ){
				Orientation orientation = Orientation.getOrientation(orientationRaw);
				switch (orientation){
					case Rotated90:
					case Rotated180:
					case Rotated270:
						return orientation;
				}
			}
		}

		return Orientation.None;
	}
	
	public double getDuration()	{
		ContentObjectMetadataItem durationMeta;
		if ((durationMeta = getMetadataItems().tryGetMetadataItem(MetadataItemName.Duration)) != null && !durationMeta.getIsDeleted()){
			//double durationRaw;
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	        Date date = null;
			try {
				date = dateFormat.parse("1970-01-01 " + durationMeta.getRawValue());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (date != null)
				return MathUtil.round(date.getTime()/1000.00, 2);
			/*if ((durationRaw = StringUtils.toDouble(durationMeta.getRawValue())) != Double.MIN_VALUE ){
				return durationRaw;
			}*/
		}

		return Constants.DEFAULT_DURATION;
	}

	/// <summary>
	/// Creates a metadata item for the current content object. The parameter <paramref name="metaDef" />
	/// contains the template and display name to use. Guaranteed to not return null.
	/// </summary>
	/// <param name="metaDef">The metadata definition.</param>
	/// <returns>An instance of <see cref="ContentObjectMetadataItem" />.</returns>
	public ContentObjectMetadataItem createMetaItem(MetadataDefinition metaDef) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		// Example: metaDef.DefaultValue = "Created at {DateCreated} - {Title}"
		// Loop through all token matches. For each one, extract the metavalue and replace the value. If there is only
		// one, use the raw value in the CreateMetadataItem line; otherwise use a null raw value.
		String rawValue = null;
		String formattedValue = metaDef.DefaultValue;
		Matcher matches = getMetaRegEx().matcher(metaDef.DefaultValue);

		if (matches.find()) {
			for(int match = 0; match<matches.groupCount(); match++){
				MetaValue metaValue = extractMetaValue(matches.group(match));
				if (metaValue != null){
					//formattedValue = formattedValue.replace(StringUtils.join(new String[] {"{", matches.group(match), "}"}), metaValue.FormattedValue);
					formattedValue = formattedValue.replace(matches.group(match), metaValue.FormattedValue);
					rawValue = metaValue.RawValue;
				}else{
					//formattedValue = formattedValue.replace(StringUtils.join(new String[] {"{", matches.group(match), "}"}), StringUtils.EMPTY);
					formattedValue = formattedValue.replace(matches.group(match), StringUtils.EMPTY);
					rawValue = null;
				}
			}
		}

		return CMUtils.createMetadataItem(Long.MIN_VALUE, this, (matches.groupCount() == 1 ? rawValue : null), formattedValue, true, metaDef);
	}

	/// <summary>
	/// Creates a approval item for the current content object. The parameter <paramref name="metaDef" />
	/// contains the template and display name to use. Guaranteed to not return null.
	/// </summary>
	/// <param name="metaDef">The metadata definition.</param>
	/// <returns>An instance of <see cref="ContentObjectBoApproval" />.</returns>
	/*public ContentObjectBoApproval CreateApproval(String strUserName, short approvalLevel, ContentObjectApproval approvalStatus)
	{
		return CMUtils.CreateApprovalItem(Long.MIN_VALUE, this, strUserName, approvalLevel, approvalStatus, StringUtils.EMPTY, StringUtils.EMPTY, null, null, true);
	}*/

	//#endregion

	//#region Public Abstract Methods

	/// <summary>
	/// Inflate the current object by loading all properties from the data store. If the object is already inflated (<see cref="IsInflated" />=true), no action is taken.
	/// </summary>
	public abstract void inflate();

	/// <summary>
	/// Gets a value indicating whether the specified <paramref name="metaDef" />
	/// applies to the current content object.
	/// </summary>
	/// <param name="metaDef">The metadata definition.</param>
	/// <returns><c>true</c> when the specified metadata item should be displayed; otherwise <c>false</c>.</returns>
	public boolean metadataDefinitionApplies(MetadataDefinition metaDef) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (ArrayUtils.contains(requiredMetadataItems(), metaDef.getMetadataItemName()))
			return true; // We *ALWAYS* want to create certain items (such as Title and Caption).
		else
			return CMUtils.loadGallerySetting(galleryId).getExtractMetadata() && metaDef.IsVisibleForContentObject;
	}

	//#endregion

	//#region Public Override Methods

	/// <summary>
	/// Returns a <see cref="T:System.String"/> that represents the current <see cref="ContentObjectBo"/>.
	/// </summary>
	/// <returns>
	/// A <see cref="T:System.String"/> that represents the current <see cref="ContentObjectBo"/>.
	/// </returns>
	@Override
	public String toString(){
		return StringUtils.join(new Object[] {super.toString(), "; ID = ", this.id, "; (", this.getTitle(), ")"});
	}

	/// <summary>
	/// Serves as a hash function for a particular type. The hash code is based on <see cref="Id" />.
	/// </summary>
	/// <returns>
	/// A hash code for the current <see cref="T:System.Object"/>.
	/// </returns>
	@Override
	public int hashCode(){
		return   new  HashCodeBuilder( 17 ,  37 )
	             .append(this.id)
	             .toHashCode();
	}

	//#endregion

	//#region Protected Methods

	/// <summary>
	/// Get a list of album IDs between the current instance and the specified <paramref name="topAlbumId" />. It works by
	/// analyzing the parent albums, recursively, of the current content object, until reaching either the root album or the specified
	/// <paramref name="topAlbumId" />. The caller is responsible for iterating through this list and calling 
	/// <see cref="Album.AssignAlbumThumbnail" /> for each album after the move operation is complete.
	/// This method should be called before the move operation takes place.
	/// </summary>
	/// <param name="topAlbumId">The ID of the album the current content object will be in after the move operation completes.</param>
	/// <returns>Return a list of album IDs whose thumbnail images will need updating after the move operation completes.</returns>
	protected LongCollection getAlbumHierarchy(long topAlbumId)	{
		LongCollection albumsInHierarchy = new LongCollection();
		ContentObjectBo album = this.parent;

		while (!(album instanceof NullContentObject))	{
			// If we're at the same level as the destination album, don't go any further.
			if (album.getId() == topAlbumId)
				break;

			albumsInHierarchy.add(album.getId());

			album = album.getParent();
		}

		return albumsInHierarchy;
	}

	//#endregion

	//#region Private Methods

	/// <summary>
	/// Re-extract several metadata values from the file that may now be inaccurate due to the deletion of the original
	/// media file. The new values are not persisted; it is expected a subsequent function will do that.
	/// </summary>
	private void refreshMetadataAfterOriginalFileDeletion() throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		MetadataItemName[] metadataNames = new MetadataItemName[] { 
					MetadataItemName.DateFileCreated, MetadataItemName.DateFileCreatedUtc, MetadataItemName.DateFileLastModified, 
					MetadataItemName.DateFileLastModifiedUtc, MetadataItemName.FileName, MetadataItemName.FileNameWithoutExtension,
					MetadataItemName.FileSizeKb, MetadataItemName.Width, MetadataItemName.Height, MetadataItemName.Dimensions,
					MetadataItemName.HorizontalResolution, MetadataItemName.VerticalResolution, MetadataItemName.Orientation};

		for(MetadataItemName mi : metadataNames){
			extractMetadata(getMetaDefinitions().find(mi));
		}
	}

	public void updateInternalMetaItem(ContentObjectMetadataItem metaItem) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		ContentObjectMetadataItem existingMetaItem;
		if ((existingMetaItem = getMetadataItems().tryGetMetadataItem(metaItem.getMetadataItemName())) != null){
			existingMetaItem.setDescription(metaItem.getDescription());
			if (okToUpdateMetaItemValue(metaItem)){
				// Update value only when we have some data. This helps prevent overwriting user-entered data.
				existingMetaItem.setValue(metaItem.getValue());
				existingMetaItem.setRawValue(metaItem.getRawValue());
			}
		}else{
			ContentObjectMetadataItemCollection metaItems = CMUtils.createMetadataCollection();
			metaItems.add(metaItem);

			addMeta(metaItems);
		}
	}

	/// <summary>
	/// Determines whether we can retrieve the value from <paramref name="metaItemSource" /> and assign it to the
	/// actual metadata item for the content object. Returns <c>true</c> when the metaitem has a value and when it
	/// does not belong to an album title or caption; otherwise returns <c>false</c>.
	/// </summary>
	/// <param name="metaItemSource">The source metaitem.</param>
	/// <returns>Returns <c>true</c> or <c>false</c>.</returns>
	private static boolean okToUpdateMetaItemValue(ContentObjectMetadataItem metaItemSource){
		boolean hasValue = !StringUtils.isBlank(metaItemSource.getValue());
		boolean isAlbumTitleOrCaption = (metaItemSource.getContentObject().getContentObjectType() == ContentObjectType.Album) 
				&& ((metaItemSource.getMetadataItemName() == MetadataItemName.Title) || (metaItemSource.getMetadataItemName() == MetadataItemName.Caption));

		return hasValue && !isAlbumTitleOrCaption;
	}

	public void removeMetadataItem(MetadataItemName metaName)	{
		ContentObjectMetadataItem metaItem;
		if ((metaItem = getMetadataItems().tryGetMetadataItem(metaName)) != null){
			metaItem.setIsDeleted(true);
		}
	}

	private void recalculateFilePaths()	{
		String albumPath = this.parent.getFullPhysicalPathOnDisk();

		// Thumbnail
		if (!StringUtils.isBlank(this.thumbnail.getFileName()))
			this.thumbnail.setFileNamePhysicalPath(FilenameUtils.concat(albumPath, this.thumbnail.getFileName()));
		else
			this.thumbnail.setFileNamePhysicalPath(StringUtils.EMPTY);

		// Optimized
		if (!StringUtils.isBlank(this.optimized.getFileName()))
			this.optimized.setFileNamePhysicalPath(FilenameUtils.concat(albumPath, this.optimized.getFileName()));
		else
			this.optimized.setFileNamePhysicalPath(StringUtils.EMPTY);

		// Original
		if (!StringUtils.isBlank(this.original.getFileName()))
			this.original.setFileNamePhysicalPath(FilenameUtils.concat(albumPath, this.original.getFileName()));
		else
			this.original.setFileNamePhysicalPath(StringUtils.EMPTY);
	}

	private void verifyObjectIsInflated(String propertyValue)	{
		// If the String is empty, and this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		synchronized (_lock){
			if ((StringUtils.isBlank(propertyValue)) && (!this.isNew) && (!this.isInflated)){
				this.inflate();
			}
		}
	}

	private void verifyObjectIsInflated(Date propertyValue)	{
		// If the String is empty, and this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		synchronized (_lock){
			if ((propertyValue == DateUtils.MinValue) && (!this.isNew) && (!this.isInflated)){
				this.inflate();
			}
		}
	}

	private void verifyObjectIsInflated(int propertyValue)	{
		// If the int = int.MinValue, and this is not a new object, and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		synchronized (_lock){
			if ((propertyValue == Integer.MIN_VALUE) && (!this.isNew) && (!this.isInflated)){
				this.inflate();
			}
		}
	}

	private void verifyObjectIsInflated()	{
		// If this is a pre-existing object (i.e. one that exists in the data store), and it has not been inflated
		// from the database, go to the database and retrieve the info for this object.
		synchronized (_lock){
			if ((!this.isNew) && (!this.isInflated)){
				this.inflate();
			}
		}
	}

	private void validateSave() throws InvalidGalleryException	{
		if ((!this.getIsNew()) && (!this.isInflated)){
			throw new UnsupportedOperationException(I18nUtils.getMessage("contentObject.validateSave_Ex_Msg"));
		}

		verifyInstanceIsUpdateable();

		validateSequence();

		// Set RegenerateThumbnailOnSave to true if thumbnail image doesn't exist.
		checkForThumbnailImage();

		// Set RegenerateOptimizedOnSave to true if optimized image doesn't exist. This is an empty virtual method
		// that is overridden in the Image class. That is, this method does nothing for non-images.
		checkForOptimizedImage();

		// Make sure the audit fields have been set.
		validateAuditFields();
	}

	private void verifyInstanceIsUpdateable(){
		if (!isWritable){
			throw new BusinessException(MessageFormat.format("This content object (ID {0}, {1}) is not updateable.", this.id, this.getClass()));
		}
	}

	private void validateAuditFields()	{
		if (StringUtils.isBlank(this.createdByUserName))
			throw new BusinessException("The property CreatedByUsername must be set to the currently logged on user before this object can be saved.");

		if (this.dateAdded == DateUtils.MinValue)
			throw new BusinessException("The property DateAdded must be assigned a valid date before this object can be saved.");

		if (StringUtils.isBlank(this.lastModifiedByUserName))
			throw new BusinessException("The property LastModifiedByUsername must be set to the currently logged on user before this object can be saved.");

		Date aFewMomentsAgo = DateUtils.addMinutes(DateUtils.Now(), -10); //DateTime.Now.Subtract(new TimeSpan(0, 10, 0)); // 10 minutes ago
		if (this.hasChanges && (this.dateLastModified.before(aFewMomentsAgo)))
			throw new BusinessException("The property DateLastModified must be assigned the current date before this object can be saved.");

		// Make sure a valid date is assigned to the DateAdded property. If it is still DateTime.MinValue,
		// update it with the current date/time.
		//System.Diagnostics.Debug.Assert((this.isNew || ((!this.isNew) && (this.DateAdded > DateTime.MinValue))),
		//  MessageFormat.format("Content objects and albums that have been saved to the data store should never have the property DateAdded=MinValue. IsNew={0}; DateAdded={1}",
		//  this.isNew, this.DateAdded.ToLongDateString()));

		//if (this.DateAdded == DateTime.MinValue)
		//{
		//  this.DateAdded = DateTime.Now;
		//}
	}

	private void validateThumbnailsAfterSave() throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		// Update the album's thumbnail if necessary.
		AlbumBo parentAlbum = Reflections.as(AlbumBo.class, this.parent);
		if ((parentAlbum != null) && (parentAlbum.getThumbnailContentObjectId() == 0)){
			AlbumBo.assignAlbumThumbnail(parentAlbum, true, false, this.getLastModifiedByUserName());
		}
	}

	private void delete(boolean deleteFromFileSystem) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		try {
			ContentConversionQueue.getInstance().remove(getId());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.deleteBehavior.delete(deleteFromFileSystem);

		AlbumBo parentAlbum = Reflections.as(AlbumBo.class, this.parent);
		if (parentAlbum != null){
			this.parent.removeContentObject(this);

			AlbumBo.assignAlbumThumbnail(parentAlbum, true, false, this.lastModifiedByUserName);
		}
	}

	/// <summary>
	/// Gets a regular expression pattern that can be used to match the replacement tokens in 
	/// <see cref="IMetadataDefinition.DefaultValue" />. Ex: "{(AudioBitRate|AudioFormat|Author|...IptcWriterEditor)}"
	/// The replacement tokens must be values of the <see cref="MetadataItemName" /> enumeration.
	/// </summary>
	/// <returns>Returns a String that can be used as a regular expression pattern.</returns>
	private static String getMetadataRegExPattern()	{
		StringBuilder sb = new StringBuilder();
		sb.append("\\{(");

		for (MetadataItemName metadataItemName : MetadataItemName.values())	{
			sb.append(metadataItemName);
			sb.append("|");
		}

		sb.append(")\\}");

		return sb.toString(); // Ex: "{(AudioBitRate|AudioFormat|Author|...IptcWriterEditor)}"
	}

	/// <summary>
	/// Extracts and returns the meta value for the <see cref="MetadataItemName" /> found in the 
	/// <paramref name="match" />. Returns null if no meta item is found. HTML and javascript may be
	/// removed from the meta data.
	/// </summary>
	/// <param name="match">A match from the regular expression <see cref="MetaRegEx" />. The value of
	/// the first group contains the name of the meta data item to extract.</param>
	/// <returns>An instance of <see cref="IMetaValue" />, or null if no meta item is found.</returns>
	private MetaValue extractMetaValue(String metadataNameStr) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// Since the pattern is built from the enum, we are guaranteed to successfully parse the match, so no need to catch a parse exception.
		MetadataItemName metadataName = MetadataItemName.getMetadataItemName(StringUtils.stripEnd(StringUtils.stripStart(metadataNameStr, "{"), "}"));

		MetaValue metaValue = metadataReadWriter.getMetaValue(metadataName);
		if (metaValue != null){
			if (metadataName != MetadataItemName.HtmlSource){
				// Remove HTML/javascript if necessary for all fields other than HTML source. Ideally, we call the clean method
				// for all fields and let the clean method do its job based on whether the current user is an admin (in which case
				// all HTML would be preserved) and whether the setting for user-entered HTML/javascript is enabled (for all other
				// users). However, the clean method has no knowledge of the current user, so it'll strip HTML whenever HTML is disabled,
				// causing the HtmlSource value to lose data.
				metaValue.FormattedValue = HtmlValidator.clean(metaValue.FormattedValue, galleryId);
				//metaValue.FormattedValue = metaValue.FormattedValue;
			}

			metaValue = (!StringUtils.isBlank(metaValue.FormattedValue) ? metaValue : tryGetFromExisting(metadataName));
		}

		return metaValue;
	}

	/// <summary>
	/// Attempts to get the requested <paramref name="metadataName" /> from the existing set of metadata
	/// items. This is used during metadata extraction when generating an item that is based on the 
	/// calculated value of another metadata item. May return null.
	/// </summary>
	/// <param name="metadataName">Name of the metadata.</param>
	/// <returns></returns>
	/// <example>An admin may create a custom meta item with the default value "{Title} - {GpsLocationWithMapLink}".
	/// The title is extracted from the file (and thus does not use this function), but the GPS map link 
	/// is based on a template and cannot be directly extracted from the image file. This function 
	/// will return the map link in this case. Note that this function will only find an item when
	/// it has already been created, so if the item it looks for does not yet exist, a null is
	/// returned. To prevent this, an admin should ensure meta items based on other templated items occur
	/// after them (as ordered on the admin metadata page).</example>
	private MetaValue tryGetFromExisting(MetadataItemName metadataName)	{
		ContentObjectMetadataItem metaItem = getMetadataItems().tryGetMetadataItem(metadataName);
		if (metaItem != null)
			return new MetaValue(metaItem.getValue(), metaItem.getRawValue());
		else
			return null;
	}

	//#endregion

	//#region Event Handlers

	/// <summary>
	/// Called after a metadata item has been created for an object but before it has been added to
	/// the <see cref="ContentObjectBo.metadataItems" /> collection.
	/// </summary>
	/// <param name="sender">The sender.</param>
	/// <param name="e">The <see cref="AddMetaEventArgs" /> instance containing the event data.</param>
	public void onBeforeAddMetaItem(AddMetaItemEvent e)	{
		switch (e.getMetaItem().getMetadataItemName()){
			case GpsLocationWithMapLink:
				if (this.metadataReadWriter.getMetaValue(MetadataItemName.GpsLocation) == null){
					e.getMetaItem().setValue(StringUtils.EMPTY);
				}
				break;

			case GpsDestLocationWithMapLink:
				if (this.metadataReadWriter.getMetaValue(MetadataItemName.GpsDestLocation) == null){
					e.getMetaItem().setValue(StringUtils.EMPTY);
				}
				break;
		}
	}

	//#endregion

	//#region IComparable Members

	/// <summary>
	/// Compares the current instance with another object of the same type.
	/// </summary>
	/// <param name="other">An object to compare with this instance.</param>
	/// <returns>
	/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has these meanings: 
	/// Less than 0: This instance is less than <paramref name="other"/>.
	/// 0: This instance is equal to <paramref name="other"/>.
	/// Greater than 0: This instance is greater than <paramref name="other"/>.
	/// </returns>
	/// <exception cref="T:System.ArgumentException">
	/// 	<paramref name="other"/> is not the same type as this instance. </exception>
	@Override
	public int compareTo(ContentObjectBo other){
		if (other == null)
			return 1;
		else{
			AlbumBo thisAsAlbum = Reflections.as(AlbumBo.class, this);
			AlbumBo otherAsAlbum = Reflections.as(AlbumBo.class, other);
			ContentObjectBo otherAsContentObj = Reflections.as(ContentObjectBo.class, other);

			boolean thisIsContentObj = (thisAsAlbum == null); // If it's not an album, it must be a content object (or a NullGalleryObject, but that shouldn't happen)
			boolean otherIsContentObj = ((otherAsContentObj != null) && (otherAsAlbum == null));
			boolean bothObjectsAreContentObjects = (thisIsContentObj && otherIsContentObj);
			boolean bothObjectsAreAlbums = ((thisAsAlbum != null) && (otherAsAlbum != null));


			if (otherAsContentObj == null)
				return 1;

			if (bothObjectsAreAlbums || bothObjectsAreContentObjects){
				return Integer.compare(this.sequence, otherAsContentObj.getSequence());
			}else if (thisIsContentObj && (otherAsAlbum != null)){
				return 1;
			}else{
				return -1; // Current instance must be album and other is content object. Albums always come first.
			}
		}
	}
	
	public boolean equals(Object other) {
		if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
	
		ContentObjectBo pojo = (ContentObjectBo)other;
        return (new EqualsBuilder()
             .append(sequence, pojo.getSequence())
             ).isEquals();
	}

	//#endregion
}
