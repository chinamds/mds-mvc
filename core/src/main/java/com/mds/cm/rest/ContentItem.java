package com.mds.cm.rest;

import javax.xml.bind.annotation.XmlRootElement;

import com.mds.core.ContentObjectType;
import com.mds.core.MimeTypeCategory;

/// <summary>
/// A simple object that contains gallery item information. It is essentially a client-optimized
/// version of <see cref="IContentObject" />. This class is used to pass information between 
/// the browser and the web server via AJAX callbacks.
/// </summary>
@XmlRootElement(name = "ContentItem")
public class ContentItem{
	public ContentItem() {}
	
	public ContentItem(long id, String title, String caption, DisplayObjectRest[] views, int viewIndex, MimeTypeCategory mimeType, ContentObjectType itemType) {
		this.Id = id;
		this.Title = title;
		this.Caption = caption;
		this.Views = views;
		this.ViewIndex = viewIndex;
		this.MimeType = mimeType.value();
		this.ItemType = itemType.getValue();
	}
	
	/// <summary>
	/// The gallery item ID.
	/// </summary>
	public long Id;
	public long getId(){
		return this.Id;
	}
	
	public void setId(long id){
		this.Id = id;
	}

	/// <summary>
	/// Gets or sets a value indicating whether this instance is an album.
	/// </summary>
	/// <value>
	///   <c>true</c> if this instance is an album; otherwise, <c>false</c>.
	/// </value>
	public boolean IsAlbum;

	/// <summary>
	/// The MIME type of this gallery item.  Maps to the <see cref="MimeTypeCategory" />
	/// enumeration, so that 0=NotSet, 1=Other, 2=Image, 3=Video, 4=Audio. Will be NotSet (0)
	/// when the current instance is an album.
	/// </summary>
	public int MimeType;

	/// <summary>
	/// The type of this gallery item.  Maps to the <see cref="ContentObjectType" /> enumeration.
	/// </summary>
	public int ItemType;
	public int getItemType() {
		return this.ItemType;
	}
	
	public void setItemType(int itemType) {
		this.ItemType = itemType;
	}

	/// <summary>
	/// The gallery item title.
	/// </summary>
	public String Title;

	/// <summary>
	/// The gallery item caption.
	/// </summary>
	public String Caption;

	/// <summary>
	/// When this instance represents an album, this property indicates the number of child 
	/// albums in this album. Will be zero when this instance is a media item.
	/// </summary>
	public int NumAlbums;

	/// <summary>
	/// When this instance represents an album, this property indicates the number of media 
	/// objects in this album. Will be zero when this instance is a media item.
	/// </summary>
	public int NumContentItems;

	/// <summary>
	/// Gets or sets the views available for this gallery item.
	/// </summary>
	/// <value>The views.</value>
	public DisplayObjectRest[] Views;

	/// <summary>
	/// Gets or sets the index of the view currently being rendered. This value can be used to get 
	/// or set the desired view to display among the possibilities in <see cref="Views" />.
	/// </summary>
	/// <value>The index of the view currently being rendered.</value>
	public int ViewIndex;

	///// <summary>
	///// When this instance represents an album, represents a user-entered beginning date. Will be
	///// null when this instance is a media item.
	///// </summary>
	//[Obsolete("This property has been rendered obsolete in 3.0 and may not be available in future versions.")]
	//public DateTime? DateStart;

	///// <summary>
	///// When this instance represents an album, represents a user-entered beginning date. Will be
	///// null when this instance is a media item.
	///// </summary>
	//[Obsolete("This property has been rendered obsolete in 3.0 and may not be available in future versions.")]
	//public DateTime? DateEnd;
}
