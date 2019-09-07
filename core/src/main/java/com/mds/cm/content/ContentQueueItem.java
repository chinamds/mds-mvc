package com.mds.cm.content;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.mds.cm.model.ContentObject;
import com.mds.cm.model.ContentQueue;
import com.mds.core.ContentObjectRotation;
import com.mds.core.ContentQueueItemConversionType;
import com.mds.core.ContentQueueItemStatus;

/// <summary>
/// Represents a content object that is queued for some kind of processing, such as transcoding a video.
/// </summary>
public class ContentQueueItem{
	//#region Properties

	/// <summary>
	/// Gets or sets the media queue ID.
	/// </summary>
	/// <value>The media queue ID.</value>
	public long ContentQueueId = Long.MIN_VALUE;

	/// <summary>
	/// Gets or sets the ID of the content object this queue item applies to.
	/// </summary>
	/// <value>The content object ID.</value>
	public long ContentObjectId;

	/// <summary>
	/// Specifies the status of the content object in the content object conversion queue.
	/// </summary>
	/// <value>The status.</value>
	//[Newtonsoft.Json.JsonConverter(typeof(Newtonsoft.Json.Converters.StringEnumConverter))]
	public ContentQueueItemStatus Status;

	/// <summary>
	/// Gets or sets the status detail.
	/// </summary>
	/// <value>The status detail.</value>
	public String StatusDetail;

	/// <summary>
	/// Specifies the type of processing to be executed on a content object in the content object conversion queue.
	/// </summary>
	/// <value>The type of the conversion.</value>
	//[Newtonsoft.Json.JsonConverter(typeof(Newtonsoft.Json.Converters.StringEnumConverter))]
	public ContentQueueItemConversionType ConversionType;

	/// <summary>
	/// Gets or sets the amount of rotation to be applied to the content object.
	/// </summary>
	/// <value>The rotation amount.</value>
	//[Newtonsoft.Json.JsonConverter(typeof(Newtonsoft.Json.Converters.StringEnumConverter))]
	public ContentObjectRotation RotationAmount;

	/// <summary>
	/// Gets or sets the date and time this queue item was created.
	/// </summary>
	/// <value>The date added.</value>
	public Date DateAdded;

	/// <summary>
	/// Gets or sets the date and time processing began on this queue item.
	/// </summary>
	/// <value>The date conversion started.</value>
	public Date DateConversionStarted;

	/// <summary>
	/// Gets or sets the date and time processing finished on this queue item.
	/// </summary>
	/// <value>The date conversion completed.</value>
	public Date DateConversionCompleted;

	//#endregion
	
	public Date getDateAdded() {
		return DateAdded;
	}
	
	public long getId() {
		return ContentQueueId;
	}

	//#region Static Methods
	
	/// <summary>
	/// Converts the <paramref name="item" /> to an instance of <see cref="ContentQueueItem" />.
	/// </summary>
	/// <param name="item">The item.</param>
	/// <returns>An instance of <see cref="ContentQueueItem" />.</returns>
	private static ContentQueueItem toContentQueueItem(ContentQueue item){
		ContentQueueItem contentQueueItem = new ContentQueueItem();
		
	    contentQueueItem.ContentQueueId = item.getId();
	    contentQueueItem.ContentObjectId = item.getContentObject().getId();
	    contentQueueItem.Status = item.getStatus();
	    contentQueueItem.StatusDetail = item.getStatusDetail();
	    contentQueueItem.ConversionType = item.getConversionType();
	    contentQueueItem.RotationAmount = item.getRotationAmount();
	    contentQueueItem.DateAdded = item.getDateAdded();
	    contentQueueItem.DateConversionStarted = item.getDateConversionStarted();
	    contentQueueItem.DateConversionCompleted = item.getDateConversionCompleted();
	   
	    return contentQueueItem;
	}
	
	/// <summary>
	/// Converts the <paramref name="mediaQueueDtos" /> to an enumerable collection of <see cref="ContentQueueItem" /> instances.
	/// </summary>
	/// <param name="mediaQueueDtos">The media queue DTO instances.</param>
	/// <returns>Iterable{ContentQueueItem}.</returns>
	public static List<ContentQueueItem> toContentQueueItems(Iterable<ContentQueue> mediaQueueDtos)	{
		List<ContentQueueItem> contentQueueItems = Lists.newArrayList();
		
		mediaQueueDtos.forEach(item->{
			contentQueueItems.add(toContentQueueItem(item));
		});

		return contentQueueItems;
	}

	//#endregion
}
