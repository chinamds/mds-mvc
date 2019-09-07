package com.mds.cm.content.nullobjects;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mds.cm.metadata.BitmapMetadata;
import com.mds.cm.metadata.WpfMetadata;

/// <summary>
/// Represents a null version of a <see cref="IWpfMetadata" /> instance. This is used when a valid
/// <see cref="BitmapMetadata" /> instance is not available for a media file. The main advantage
/// to using this class is to reduce the dependency on calling code to check for null when accessing
/// metadata properties.
/// </summary>
public class NullWpfMetadata extends WpfMetadata{	
	public NullWpfMetadata() {
		super(null);
	}

	@Override
	public BitmapMetadata getMetadata() {
		return null;
	}
	
	@Override
	public void setMetadata(BitmapMetadata metadata) {
	}

	/// <summary>
	/// Gets or sets a value that indicates the date that the image was taken.
	/// </summary>
	/// <value>A String.</value>
	@Override
	public String getDateTaken() { 
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setDateTaken(String dateTaken) {

	}

	/// <summary>
	/// Gets or sets a value that indicates the title of an image file.
	/// </summary>
	/// <value>A String.</value>
	@Override
	public String getTitle() { 
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setTitle(String title) {

	}

	/// <summary>
	/// Gets or sets a value that indicates the author of an image.
	/// </summary>
	/// <value>A collection.</value>
	@Override
	public List<String> getAuthor() { 
		return null;
	}
	
	@Override
	public void setAuthor(List<String> author) {
	}

	/// <summary>
	/// Gets or sets a value that identifies the camera model that was used to capture the image.
	/// </summary>
	/// <value>A String.</value>
	@Override
	public String getCameraModel() { 
		return StringUtils.EMPTY; 
	}
	
	@Override
	public void setCameraModel(String cameraModel) {

	}

	/// <summary>
	/// Gets or sets a value that identifies the camera manufacturer that is associated with an image.
	/// </summary>
	/// <value>A String.</value>
	@Override
	public String getCameraManufacturer() { 
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setCameraManufacturer(String cameraManufacturer) {

	}

	/// <summary>
	/// Gets or sets a collection of keywords that describe the image.
	/// </summary>
	/// <value>A collection.</value>
	@Override
	public List<String> getKeywords() { 
		return null; 
	}
	
	@Override
	public void setKeywords(List<String> keywords) {
	}

	/// <summary>
	/// Gets or sets a value that identifies the image rating.
	/// </summary>
	/// <value>An integer.</value>
	@Override
	public int getRating() { 
		return Integer.MIN_VALUE;
	}
	
	@Override
	public void setRating(int rating) {
	}

	/// <summary>
	/// Gets or sets a value that identifies a comment that is associated with an image.
	/// </summary>
	/// <value>A String.</value>
	@Override
	public String getComment() { 
		return StringUtils.EMPTY; 
	}
	
	@Override
	public void setComment(String comment) {
	}

	/// <summary>
	/// Gets or sets a value that identifies copyright information that is associated with an image.
	/// </summary>
	/// <value>A String.</value>
	@Override
	public String getCopyright() { 
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setCopyright(String copyright) {
	}

	/// <summary>
	/// Gets or sets a value that indicates the subject matter of an image.
	/// </summary>
	/// <value>A String.</value>
	@Override
	public String getSubject() {
		return StringUtils.EMPTY; 
	}
	
	@Override
	public void setSubject(String subject) {
	}

	/// <summary>
	/// Provides access to a metadata query reader that can extract metadata from a bitmap image file.
	/// </summary>
	/// <param name="query">Identifies the String that is being queried in the current object.</param>
	/// <returns>The metadata at the specified query location.</returns>
	/// <exception cref="ArgumentNullException">Thrown when query is null.</exception>
	@Override
	public Object getQuery(String query){
		return null;
	}
}
