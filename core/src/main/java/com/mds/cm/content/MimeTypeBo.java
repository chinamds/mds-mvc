package com.mds.cm.content;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.mds.cm.exception.InvalidGalleryException;
import com.mds.core.MimeTypeCategory;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.cm.util.CMUtils;
import com.mds.i18n.util.I18nUtils;

/// <summary>
/// Represents a mime type associated with a file's extension.
/// </summary>
public class MimeTypeBo {
	//#region Private Fields

	private long mimeTypeId;
	private long mimeTypeGalleryId;
	private long galleryId;
	private String extension;
	private MimeTypeCategory typeCategory;
	private String majorType;
	private String subtype;
	private boolean allowAddToGallery;
	private String browserMimeType;
	private ContentTemplateBoCollection mediaTemplates = new ContentTemplateBoCollection();

	private static Object sharedLock = new Object();

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="MimeType"/> class.
	/// </summary>
	/// <param name="mimeTypeId">The value that uniquely identifies the MIME type.</param>
	/// <param name="mimeTypeGalleryId">The value that uniquely identifies the MIME type that applies to a particular gallery.</param>
	/// <param name="galleryId">The gallery ID. Specify <see cref="Int32.MinValue"/> if creating an instance that is not
	/// specific to a particular gallery.</param>
	/// <param name="fileExtension">A String representing the file's extension, including the period (e.g. ".jpg", ".avi").
	/// It is not case sensitive.</param>
	/// <param name="mimeTypeValue">The full mime type. This is the <see cref="MajorType"/> concatenated with the <see cref="Subtype"/>,
	/// with a '/' between them (e.g. image/jpeg, video/quicktime).</param>
	/// <param name="browserMimeType">The MIME type that can be understood by the browser for displaying this content object.  Specify null or
	/// <see cref="StringUtils.EMPTY"/> if the MIME type appropriate for the browser is the same as <paramref name="mimeTypeValue"/>.</param>
	/// <param name="allowAddToGallery">Indicates whether a file having this MIME type can be added to MDS System.
	/// This parameter is only relevant when a valid <paramref name="galleryId"/> is specified.</param>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="fileExtension" /> or <paramref name="mimeTypeValue" /> is
	/// null or an empty String.</exception>
	public MimeTypeBo(long mimeTypeId, long mimeTypeGalleryId, long galleryId, String fileExtension, String mimeTypeValue
			, String browserMimeType, boolean allowAddToGallery){
		//#region Validation

		if (StringUtils.isBlank(fileExtension))
			throw new ArgumentOutOfRangeException("fileExtension", "Parameter cannot be null or empty.");

		if (StringUtils.isBlank(mimeTypeValue))
			throw new ArgumentOutOfRangeException("mimeTypeValue", "Parameter cannot be null or empty.");

		// If browserMimeType is specified, it better be valid.
		if (!StringUtils.isBlank(browserMimeType)){
			validateMimeType(browserMimeType);
		}

		// Validate fullMimeType and separate it into its major and sub types.
		Pair<String, String> mineType = validateMimeType(mimeTypeValue);
		String majorType = mineType.getLeft();
		String subType = mineType.getRight();

		//#endregion

		MimeTypeCategory mimeTypeCategory = MimeTypeCategory.getMimeTypeCategory(majorType);//MimeTypeCategory.Other;
		/*try{
			mimeTypeCategory = MimeTypeCategory.valueOf(majorType);
		}catch (IllegalArgumentException ex) {	 Swallow exception so that we default to MimeTypeCategory.Other 	}*/

		this.mimeTypeId = mimeTypeId;
		this.mimeTypeGalleryId = mimeTypeGalleryId;
		this.galleryId = galleryId;
		this.extension = fileExtension;
		this.typeCategory = mimeTypeCategory;
		this.majorType = majorType;
		this.subtype = subType;
		this.browserMimeType = (StringUtils.isBlank(browserMimeType) ? mimeTypeValue : browserMimeType);
		this.allowAddToGallery = allowAddToGallery;
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="MimeType"/> class with the specified MIME type category. The <see cref="MajorType" /> property is
	/// assigned the String representation of the <paramref name="mimeType"/>. Remaining properties are set to empty Strings or false 
	/// (<see cref="AllowAddToGallery" />). This constructor is intended to be used to help describe an external content object, which is
	/// not represented by a locally stored file but for which it is useful to describe its general type (audio, video, etc).
	/// </summary>
	/// <param name="mimeType">Specifies the category to which this mime type belongs. This usually corresponds to the first portion of 
	/// the full mime type description. (e.g. "image" if the full mime type is "image/jpeg").</param>
	protected MimeTypeBo(MimeTypeCategory mimeType){
		this.galleryId = Long.MIN_VALUE;
		this.typeCategory = mimeType;
		this.majorType = mimeType.toString();
		this.extension = StringUtils.EMPTY;
		this.subtype = StringUtils.EMPTY;
		this.browserMimeType = StringUtils.EMPTY;
		this.allowAddToGallery = false;
	}

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets or sets the value that uniquely identifies this MIME type. Each application has a master list of MIME types it works with;
	/// this value identifies that MIME type.
	/// </summary>
	/// <value>The MIME type ID.</value>
	public long getMimeTypeId(){
		return this.mimeTypeId;
	}
	
	public void setMimeTypeId(long mimeTypeId){
		this.mimeTypeId = mimeTypeId;
	}

	/// <summary>
	/// Gets or sets the value that uniquely identifies the MIME type that applies to a particular gallery. This value is <see cref="Int32.MinValue" />
	/// when the current instance is an application-level MIME type and not associated with a particular gallery. In this case, 
	/// <see cref="MimeTypeBo.GalleryId" /> will also be <see cref="Int32.MinValue" />.
	/// </summary>
	/// <value>The value that uniquely identifies the MIME type that applies to a particular gallery.</value>
	public long getMimeTypeGalleryId(){
		return this.mimeTypeGalleryId;
	}
	
	public void setMimeTypeGalleryId(long mimeTypeGalleryId){
		this.mimeTypeGalleryId = mimeTypeGalleryId;
	}

	/// <summary>
	/// Gets or sets the gallery ID this MIME type is associated with. May be <see cref="Int32.MinValue"/> when the instance is not
	/// assocated with a particular gallery.
	/// </summary>
	/// <value>The gallery ID this MIME type is associated with.</value>
	public long getGalleryId(){
		return this.galleryId;
	}
	
	public void setGalleryId(long galleryId){
		this.galleryId = galleryId;
	}

	/// <summary>
	/// Gets the file extension this mime type is associated with, including the period (e.g. ".jpg", ".avi").
	/// </summary>
	/// <value>The file extension this mime type is associated with.</value>
	public String getExtension(){
		return this.extension;
	}

	/// <summary>
	/// Gets the type category this mime type is associated with (e.g. image, video, other).
	/// </summary>
	/// <value>
	/// The type category this mime type is associated with (e.g. image, video, other).
	/// </value>
	public MimeTypeCategory getTypeCategory(){
		return this.typeCategory;
	}

	/// <summary>
	/// Gets the MIME type that should be sent to the browser. In most cases this is the same as the <see cref="MimeTypeBo.FullType" />,
	/// but in some cases is different. For example, the MIME type for a .wav file is audio/wav, but the browser requires a 
	/// value of application/x-mplayer2.
	/// </summary>
	/// <value>The MIME type that should be sent to the browser.</value>
	public String getBrowserMimeType(){
		return this.browserMimeType;
	}

	/// <summary>
	/// Gets the major type this mime type is associated with (e.g. image, video).
	/// </summary>
	/// <value>
	/// The major type this mime type is associated with (e.g. image, video).
	/// </value>
	public String getMajorType(){
		return this.majorType;
	}

	/// <summary>
	/// Gets the subtype this mime type is associated with (e.g. jpeg, quicktime).
	/// </summary>
	/// <value>
	/// The subtype this mime type is associated with (e.g. jpeg, quicktime).
	/// </value>
	public String getSubtype(){
		return this.subtype;
	}

	/// <summary>
	/// Gets the full mime type. This is the <see cref="MajorType"/> concatenated with the <see cref="Subtype"/>, with a '/' between them
	/// (e.g. image/jpeg, video/quicktime).
	/// </summary>
	/// <value>The full mime type.</value>
	public String getFullType(){
		return MessageFormat.format("{0}/{1}", this.majorType.toLowerCase(), this.subtype);
	}

	/// <summary>
	/// Gets a value indicating whether objects of this MIME type can be added to MDS System.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if objects of this MIME type can be added to MDS System; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowAddToGallery(){
		return this.allowAddToGallery;
	}
	
	public void setAllowAddToGallery(boolean allowAddToGallery){
		this.allowAddToGallery = allowAddToGallery;
	}

	/// <summary>
	/// Gets the collection of media templates for the current MIME type.
	/// </summary>
	/// <value>The media templates for the current MIME type.</value>
	public ContentTemplateBoCollection getContentTemplates(){
		return mediaTemplates;
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Creates a deep copy of this instance.
	/// </summary>
	/// <returns>Returns a deep copy of this instance.</returns>
	public MimeTypeBo Copy(){
		MimeTypeBo copy = new MimeTypeBo(this.mimeTypeId, this.mimeTypeGalleryId, this.galleryId, this.extension, this.getFullType(), this.browserMimeType, this.allowAddToGallery);

		if (!this.mediaTemplates.isEmpty())	{
			copy.mediaTemplates.addAll(this.mediaTemplates.copy());
		}

		return copy;
	}

	/// <summary>
	/// Gets the most specific <see cref="ContentTemplateBo" /> item that matches one of the <paramref name="browserIds" />. This 
	/// method loops through each of the browser IDs in <paramref name="browserIds" />, starting with the most specific item, and 
	/// looks for a match in the current collection. This method is guaranteed to return a <see cref="ContentTemplateBo" /> object, 
	/// provided the collection, at the very least, contains a browser element with id = "default".
	/// </summary>
	/// <param name="browserIds">A <see cref="System.Array"/> of browser ids for the current browser. This is a list of Strings,
	/// ordered from most general to most specific, that represent the various categories of browsers the current
	/// browser belongs to. This is typically populated by calling ToArray() on the Request.Browser.Browsers property.
	/// </param>
	/// <returns>The <see cref="ContentTemplateBo" /> that most specifically matches one of the <paramref name="browserIds" />; 
	/// otherwise, a null reference.</returns>
	/// <example>During a request where the client is Firefox, the Request.Browser.Browsers property returns an ArrayList with these 
	/// five items: default, mozilla, gecko, mozillarv, and mozillafirefox. This method starts with the most specific item 
	/// (mozillafirefox) and looks in the current collection for an item with this browser ID. If a match is found, that item 
	/// is returned. If no match is found, the next item (mozillarv) is used as the search parameter.  This continues until a match 
	/// is found. Since there should always be a browser element with id="default", there will always - eventually - be a match.
	/// </example>
	public ContentTemplateBo getContentTemplate(List browserIds){
		return this.mediaTemplates.find(browserIds);
	}

	//#endregion

	//#region Public static methods

	/// <summary>
	/// Initializes a new instance of the <see cref="MimeType"/> class with the specified MIME type category. The <see cref="MajorType" /> property is
	/// assigned the String representation of the <paramref name="mimeType"/>. Remaining properties are set to empty Strings or false 
	/// (<see cref="AllowAddToGallery" />). This method is intended to be used to help describe an external content object, which is
	/// not represented by a locally stored file but for which it is useful to describe its general type (audio, video, etc).
	/// </summary>
	/// <param name="mimeType">Specifies the category to which this mime type belongs. This usually corresponds to the first portion of 
	/// the full mime type description. (e.g. "image" if the full mime type is "image/jpeg").</param>
	/// <returns>Returns a new instance of <see cref="MimeTypeBo"/>.</returns>
	public static MimeTypeBo createInstance(MimeTypeCategory mimeType){
		return new MimeTypeBo(mimeType);
	}
	
	/// <summary>
	/// Loads the collection of MIME types for the specified <paramref name="galleryId" /> from the data store.
	/// When <paramref name="galleryId" /> is <see cref="Int32.MinValue" />, a generic collection that is not 
	/// specific to a particular gallery is returned.
	/// </summary>
	/// <param name="galleryId">The gallery ID. Specify <see cref="Int32.MinValue" /> to retrieve a generic 
	/// collection that is not specific to a particular gallery.</param>
	/// <returns>Returns a <see cref="IMimeTypeCollection" /> containing MIME types for the specified 
	/// <paramref name="galleryId" /></returns>
	public static MimeTypeBoCollection loadMimeTypes(long galleryId) throws InvalidGalleryException{
		return CMUtils.loadAndConfigureMimeTypes(galleryId);
	}

	//#endregion

	//#region Private methods

	private static Pair<String, String> validateMimeType(String fullMimeType)	{
		int slashLocation = fullMimeType.indexOf("/");
		if (slashLocation < 0){
			throw new ArgumentException(I18nUtils.getMessage("mimeType.ctor_Ex_Msg", fullMimeType), fullMimeType);
		}

		String majorType = fullMimeType.substring(0, slashLocation);
		String subType = fullMimeType.substring(slashLocation + 1);

		if ((StringUtils.isBlank(majorType)) || (StringUtils.isBlank(subType))){
			throw new ArgumentException(I18nUtils.getMessage("mimeType.ctor_Ex_Msg", fullMimeType), fullMimeType);
		}
		
		return new ImmutablePair<String, String>(majorType, subType);
	}

	//#endregion
}
