package com.mds.aiotplayer.cm.content.nullobjects;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.ContentObjectBoCollection;
import com.mds.aiotplayer.cm.content.ContentObjectEvent;
import com.mds.aiotplayer.cm.content.DisplayObject;
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItemCollection;
import com.mds.aiotplayer.cm.metadata.MetadataDefinition;
import com.mds.aiotplayer.cm.metadata.MetadataDefinitionCollection;
import com.mds.aiotplayer.cm.metadata.MetadataReadWriter;
import com.mds.aiotplayer.core.ContentObjectRotation;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.Orientation;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.DateUtils;

/// <summary>
/// Represents a <see cref="ContentObject" /> that is equivalent to null. This class is used instead of null to prevent 
/// <see cref="NullReferenceException" /> errors if the calling code accesses a property or executes a method.
/// </summary>
public class NullContentObject extends ContentObjectBo {
	public NullContentObject() {
		super("NullContentObject");
		this.id = Long.MIN_VALUE;
	}
	
	@Override
	public long getId(){
		return this.id;
	}
	
	@Override
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public ContentObjectBo getParent(){
		return new NullContentObject();
	}
	
	@Override
	public void setParent(ContentObjectBo parent){
	}


	@Override
	public String getTitle(){
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setTitle(String title){
	}
	
	@Override
	public String getCaption()	{
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setCaption(String caption) {
	}
	
	@Override
	public long getGalleryId() {
		return Long.MIN_VALUE;
	}
	
	@Override
	public void setGalleryId(long galleryId) {
	}

	@Override
	public boolean isGalleryIdHasChanged()	{
		return false;
	}

	@Override
	public DisplayObject getThumbnail()	{
		return new NullDisplayObject();
	}
	
	@Override
	public void setThumbnail(DisplayObject thumbnail)	{
	}

	@Override
	public DisplayObject getOptimized() {
		return new NullDisplayObject();
	}
	
	@Override
	public void setOptimized(DisplayObject optimized) {
	}

	@Override
	public DisplayObject getOriginal(){
		return new NullDisplayObject();
	}
	
	@Override
	public void setOriginal(DisplayObject original)	{
	}

	/// <summary>
	/// Gets or sets a value indicating whether the current instance can be modified. Objects that are stored in a cache must
	/// be treated as read-only. Only objects that are instantiated right from the database and not shared across threads
	/// should be updated.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if this instance can be modified; otherwise, <c>false</c>.
	/// </value>
	/// <exception cref="ArgumentException">Thrown when there is an attempt to assign a new value to this property once it has
	/// been assigned.</exception>
	@Override
	public boolean getIsWritable()	{
		return false;
	}
	
	@Override
	public void setIsWritable(boolean isWritable) {
	}

	@Override
	public void addContentObject(ContentObjectBo contentObject)	{
	}

	@Override
	public void doAddContentObject(ContentObjectBo contentObject)	{
	}

	@Override
	public void removeContentObject(ContentObjectBo contentObject){
	}
	
	@Override
	public ContentObjectBoCollection getChildContentObjects(){
		return getChildContentObjects(ContentObjectType.All, false);
	}
	
	@Override
	public ContentObjectBoCollection getChildContentObjects(ContentObjectType contentObjectType){
		return getChildContentObjects(contentObjectType, false);
	}
	
	@Override
	public ContentObjectBoCollection getChildContentObjects(ContentObjectType ContentObjectType, boolean excludePrivateObjects)	{
		return new ContentObjectBoCollection();
	}

	@Override
	public void addMeta(ContentObjectMetadataItemCollection metaItems){
	}

	/*public void AddApproval(ContentObjectApprovalCollection approvalItems)
	{
	}*/

	@Override
	public void save(){
		ContentObjectEvent event = new ContentObjectEvent(this, this);
        listeners.forEach(l -> {
			try {
				l.saving(event);
			} catch (UnsupportedContentObjectTypeException | InvalidGalleryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

        listeners.forEach(l -> {
			try {
				l.saved(event);
			} catch (InvalidGalleryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@Override
	public void deleteOriginalFile(){
	}

	/*public void approvalFileAction(ContentObjectApproval approval)	{
	}*/

	@Override
	public void inflate()	{
	}

	public boolean metadataDefinitionApplies(MetadataDefinition metaDef)	{
		return false;
	}

	@Override
	public String getFullPhysicalPath()	{
		return StringUtils.EMPTY;
	}

	@Override
	public String getFullPhysicalPathOnDisk(){
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setFullPhysicalPathOnDisk(String fullPhysicalPathOnDisk) {
	}

	@Override
	public boolean getHasChanges(){
		return false;
	}
	
	@Override
	public void setHasChanges(boolean hasChanges){
	}

	@Override
	public boolean getIsNew(){
		return false;
	}

	@Override
	public void delete(){
	}

	@Override
	public void deleteFromGallery()
	{
	}

	@Override
	public boolean getIsInflated(){
		return false;
	}
	
	@Override
	public void setIsInflated(boolean isInflated){
	}

	@Override
	public ContentObjectType getContentObjectType()	{
		return ContentObjectType.None;
	}

	/// <summary>
	/// Gets the gallery object approval status.
	/// </summary>
	/// <value>An instance of <see cref="ContentObjectApproval" />.</value>
	/*public ContentObjectApproval ApprovalStatus
	{
		get
		{
			return ContentObjectApproval.NotSpecified;
		}
	}*/

	@Override
	public MimeTypeBo getMimeType(){
		return new NullMimeType();
	}

	@Override
	public int getSequence(){
		return Integer.MIN_VALUE;
	}
	
	@Override
	public void setSequence(int sequence){
	}

	@Override
	public boolean getRegenerateThumbnailOnSave(){
		return false;
	}
	
	@Override
	public void setRegenerateThumbnailOnSave(boolean regenerateThumbnailOnSave)	{
	}

	@Override
	public boolean getRegenerateOptimizedOnSave(){
		return false;
	}
	
	@Override
	public void setRegenerateOptimizedOnSave(boolean regenerateOptimizedOnSave)	{
	}

	@Override
	public Date getDateAdded(){
		return DateUtils.MinValue;
	}
	
	@Override
	public void setDateAdded(Date dateAdded){
	}

	@Override
	public String getCreatedByUserName(){
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setCreatedByUserName(String createdByUserName) { 
	}

	@Override
	public String getLastModifiedByUserName(){
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setLastModifiedByUserName(String lastModifiedByUserName) { 
	}

	@Override
	public Date getDateLastModified(){
		return DateUtils.MinValue;
	}
	
	@Override
	public void setDateLastModified(Date dateLastModified) {
	}

	@Override
	public boolean getIsPrivate(){
		return false;
	}
	
	@Override
	public void setIsPrivate(boolean isPrivate)	{
	}

	@Override
	public boolean getIsSynchronized(){
		return false;
	}
	
	@Override
	public void setIsSynchronized(boolean isSynchronized){
	}

	@Override
	public MetadataReadWriter getMetadataReadWriter(){
		return null;
	}
	
	@Override
	public void setMetadataReadWriter(MetadataReadWriter metadataReadWriter) {
	}

	@Override
	public void setParentToNullObject()
	{
	}

	@Override
	public MetadataDefinitionCollection getMetaDefinitions(){
		return new MetadataDefinitionCollection();
	}

	@Override
	public ContentObjectMetadataItemCollection getMetadataItems(){
		return new ContentObjectMetadataItemCollection();
	}

	/*public ContentObjectApprovalCollection ApprovalItems
	{
		get
		{
			return new ContentObjectApprovalCollection();
		}
	}*/
	
	public boolean getIsMetadataLoaded(){
		return false;
	}
	
	public void setIsMetadataLoaded(boolean isMetadataLoaded){
	}

	@Override
	public ContentObjectRotation getRotation()	{
		return ContentObjectRotation.NotSpecified;
	}
	
	@Override
	public void setRotation(ContentObjectRotation rotation)	{
	}

	@Override
	public ContentObjectBo copyTo(AlbumBo destinationAlbum, String userName){
		return new NullContentObject();
	}

	/// <summary>
	/// Build the set of metadata for the current gallery object and assign to the <see cref="ContentObject.MetadataItems" />
	/// property.
	/// </summary>
	@Override
	public void extractMetadata(){
	}

	@Override
	public void extractMetadata(MetadataDefinition metaDef)	{
	}

	/// <summary>
	/// Creates a metadata item for the current gallery object. The parameter <paramref name="metaDef" />
	/// contains the template and display name to use. Guaranteed to not return null.
	/// </summary>
	/// <param name="metaDef">The metadata definition.</param>
	/// <returns>An instance of <see cref="ContentObjectMetadataItem" />.</returns>
	@Override
	public ContentObjectMetadataItem createMetaItem(MetadataDefinition metaDef)	{
		return CMUtils.createMetadataItem(Long.MIN_VALUE, new NullContentObject(), null, StringUtils.EMPTY, false
				, new MetadataDefinition(MetadataItemName.AudioBitRate, StringUtils.EMPTY, false, false, false, Integer.MIN_VALUE, StringUtils.EMPTY));
	}

	/*public ContentObjectApproval CreateApproval(String strUserName, short approvalLevel, ContentObjectApproval approvalStatus)
	{
		return Factory.CreateApprovalItem(long.MinValue, new NullContentObject(), strUserName, approvalLevel, approvalStatus, StringUtils.EMPTY, StringUtils.EMPTY, null, null, false);
	}*/

	@Override
	public ContentObjectRotation calculateNeededRotation(){
		return ContentObjectRotation.NotSpecified;
	}

	@Override
	public Orientation getOrientation()	{
		return Orientation.NotInitialized;
	}

	public ContentObjectMetadataItemCollection getMetadata(){
		return new ContentObjectMetadataItemCollection();
	}

	@Override
	public void moveTo(AlbumBo destinationAlbum)	{
	}

	//#region IComparable Members

	@Override
	public int compareTo(ContentObjectBo obj){
		if (obj == null)
			return 1;
		else{
			return Integer.compare(this.getSequence(), obj.getSequence());
		}
	}

	//#endregion
}
