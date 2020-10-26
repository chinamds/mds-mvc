/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import java.util.List;

/// <summary>
/// Contains functionality for interacting with a file's metadata through the WPF classes.
/// Essentially it is a wrapper for the <see cref="BitmapMetadata" /> class.
/// </summary>
public class WpfMetadata
{
	private BitmapMetadata metadata;
	
	public BitmapMetadata getMetadata() {
		return metadata;
	}
	
	public void setMetadata(BitmapMetadata metadata) {
		this.metadata = metadata;
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="Wpfmetadata" /> class.
	/// </summary>
	/// <param name="bitmapmetadata">An object containing the metadata.</param>
	public WpfMetadata(BitmapMetadata bitmapmetadata){
		this.metadata = bitmapmetadata;
	}

	/// <summary>
	/// Gets or sets a value that indicates the date that the image was taken.
	/// </summary>
	/// <value>A String.</value>
	public String getDateTaken() { 
		return metadata.DateTaken;
	}
	
	public void setDateTaken(String dateTaken) {
		metadata.DateTaken = dateTaken; 
	}

	/// <summary>
	/// Gets or sets a value that indicates the title of an image file.
	/// </summary>
	/// <value>A String.</value>
	public String getTitle() { 
		return metadata.Title;
	}
	
	public void setTitle(String title) {
		metadata.Title = title; 
	}

	/// <summary>
	/// Gets or sets a value that indicates the author of an image.
	/// </summary>
	/// <value>A collection.</value>
	public List<String> getAuthor() { 
		return metadata.Author;
	}
	
	public void setAuthor(List<String> author) {
		metadata.Author = author; 
	}

	/// <summary>
	/// Gets or sets a value that identifies the camera model that was used to capture the image.
	/// </summary>
	/// <value>A String.</value>
	public String getCameraModel() { 
		return metadata.CameraModel; 
	}
	
	public void setCameraModel(String cameraModel) {
		metadata.CameraModel = cameraModel; 
	}

	/// <summary>
	/// Gets or sets a value that identifies the camera manufacturer that is associated with an image.
	/// </summary>
	/// <value>A String.</value>
	public String getCameraManufacturer() { 
		return metadata.CameraManufacturer;
	}
	
	public void setCameraManufacturer(String cameraManufacturer) {
		metadata.CameraManufacturer = cameraManufacturer; 
	}

	/// <summary>
	/// Gets or sets a collection of keywords that describe the image.
	/// </summary>
	/// <value>A collection.</value>
	public List<String> getKeywords() { 
		return metadata.Keywords; 
	}
	
	public void setKeywords(List<String> keywords) {
		metadata.Keywords = keywords; 
	}

	/// <summary>
	/// Gets or sets a value that identifies the image rating.
	/// </summary>
	/// <value>An integer.</value>
	public int getRating() { 
		return metadata.Rating;
	}
	
	public void setRating(int rating) {
		metadata.Rating = rating; 
	}

	/// <summary>
	/// Gets or sets a value that identifies a comment that is associated with an image.
	/// </summary>
	/// <value>A String.</value>
	public String getComment() { 
		return metadata.Comment; 
	}
	
	public void setComment(String comment) {
		metadata.Comment = comment; 
	}

	/// <summary>
	/// Gets or sets a value that identifies copyright information that is associated with an image.
	/// </summary>
	/// <value>A String.</value>
	public String getCopyright() { 
		return metadata.Copyright;
	}
	
	public void setCopyright(String copyright) {
		metadata.Copyright = copyright; 
	}

	/// <summary>
	/// Gets or sets a value that indicates the subject matter of an image.
	/// </summary>
	/// <value>A String.</value>
	public String getSubject() {
		return metadata.Subject; 
	}
	
	public void setSubject(String subject) {
		metadata.Subject = subject; 
	}

	/// <summary>
	/// Provides access to a metadata query reader that can extract metadata from a bitmap image file.
	/// </summary>
	/// <param name="query">Identifies the String that is being queried in the current object.</param>
	/// <returns>The metadata at the specified query location.</returns>
	/// <exception cref="ArgumentNullException">Thrown when query is null.</exception>
	public Object getQuery(String query){
		return metadata.getQuery(query);
	}
}