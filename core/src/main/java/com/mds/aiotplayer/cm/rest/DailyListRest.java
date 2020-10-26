/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

import org.apache.http.client.utils.DateUtils;

import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MimeTypeCategory;

/// <summary>
/// A simple object that contains gallery item information. It is essentially a client-optimized
/// version of <see cref="IContentObject" />. This class is used to pass information between 
/// the browser and the web server via AJAX callbacks.
/// </summary>
public class DailyListRest{
	
	private long id;
	private String content; //dailyList name
	private String fileName;//dailyList file name
	private String timeFrom;//dailyList time from
	private String timeTo;
	private String duration;
	private boolean mute;
	private boolean aspectRatio;
	private String contentType;
	private boolean isNew;
	private boolean isDeleted;
		
	/// <summary>
	/// The daily list item zone id.
	/// </summary>
	public long getId(){
		return this.id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	public String getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public boolean isAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(boolean aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the role has been persisted to the data store.
	/// </summary>	
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the daily list item has been deleted by user.
	/// </summary>
	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
