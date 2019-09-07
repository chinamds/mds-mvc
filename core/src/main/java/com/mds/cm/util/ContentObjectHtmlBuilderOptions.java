package com.mds.cm.util;

import java.util.Collection;
import java.util.List;

import com.mds.cm.content.ContentObjectBo;
import com.mds.core.DisplayObjectType;

/// <summary>
/// Contains options that direct the creation of HTML and URLs for a content object.
/// </summary>
public class ContentObjectHtmlBuilderOptions{
	/// <summary>
	/// Gets or sets the content object. Must be assigned to a value before this instance can be passed to the 
	/// <see cref="ContentObjectHtmlBuilder" /> constructor.
	/// </summary>
	public ContentObjectBo ContentObject;

	/// <summary>
	/// Gets or sets the display type. Must be assigned to a value other than <see cref="DisplayObjectType.Unknown" />
	/// before this instance can be passed to the <see cref="ContentObjectHtmlBuilder" /> constructor.
	/// </summary>
	public DisplayObjectType DisplayType;

	/// <summary>
	/// Gets the browser IDs for current request.
	/// </summary>
	public List Browsers ;

	/// <summary>
	/// Gets or sets the URL, relative to the website root and optionally including any query String parameters,
	/// to the page any generated URLs should point to. Examples: "/dev/ds/gallery.aspx",
	/// "/dev/ds/gallery.aspx?g=admin_email&amp;aid=2389"
	/// </summary>
	public String DestinationPageUrl;

	/// <summary>
	/// Gets a value indicating whether the current user is authenticated.
	/// </summary>
	/// <value><c>true</c> if the current user is authenticated; otherwise, <c>false</c>.</value>
	public boolean IsAuthenticated ;

	/// <summary>
	/// Gets the URI scheme, DNS host name or IP address, and port number for the current application. 
	/// Examples: "http://www.site.com", "http://localhost", "http://127.0.0.1", "http://godzilla"
	/// </summary>
	public String HostUrl ;

	/// <summary>
	/// Gets the path, relative to the web site root, to the current web application.
	/// Example: "/dev/gallery".
	/// </summary>
	public String AppRoot ;

	/// <summary>
	/// Gets the path, relative to the web site root, to the directory containing the MDS System user 
	/// controls and other resources. Example: "/dev/gallery/ds".
	/// </summary>
	public String GalleryRoot ;

	/// <summary>
	/// Private constructor. Prevents a default instance of the <see cref="ContentObjectHtmlBuilderOptions"/> class from being created.
	/// </summary>
	private ContentObjectHtmlBuilderOptions() { }

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectHtmlBuilderOptions"/> class.
	/// </summary>
	/// <param name="contentObject">The content object. May be null. If null, <see cref="ContentObjectHtmlBuilderOptions.ContentObject" />
	/// must be assigned before passing this instance to the <see cref="ContentObjectHtmlBuilder" /> constructor.</param>
	/// <param name="displayType">The display type. Optional. If not assigned or set to <see cref="DisplayObjectType.Unknown" />,
	/// <see cref="ContentObjectHtmlBuilderOptions.DisplayType" /> must be assigned before passing this instance to the 
	/// <see cref="ContentObjectHtmlBuilder" /> constructor.</param>
	/// <param name="browsers">The browser IDs for current request.</param>
	/// <param name="destinationPageUrl">The URL, relative to the website root and optionally including any query String parameters,
	/// to the page any generated URLs should point to. Examples: "/dev/ds/gallery.aspx", 
	/// "/dev/ds/gallery.aspx?g=admin_email&amp;aid=2389"</param>
	/// <param name="isAuthenticated">If set to <c>true</c> the current user is authenticated.</param>
	/// <param name="hostUrl">The URI scheme, DNS host name or IP address, and port number for the current application. 
	/// Examples: "http://www.site.com", "http://localhost", "http://127.0.0.1", "http://godzilla"</param>
	/// <param name="appRoot">The path, relative to the web site root, to the current web application.
	/// Example: "/dev/gallery".</param>
	/// <param name="galleryRoot">The path, relative to the web site root, to the directory containing the MDS System user 
	/// controls and other resources. Example: "/dev/gallery/ds".</param>
	public ContentObjectHtmlBuilderOptions(ContentObjectBo contentObject, DisplayObjectType displayType, List browsers, String destinationPageUrl, boolean isAuthenticated, String hostUrl, String appRoot, String galleryRoot){
		ContentObject = contentObject;
		DisplayType = displayType;
		Browsers = browsers;
		DestinationPageUrl = destinationPageUrl;
		IsAuthenticated = isAuthenticated;
		HostUrl = hostUrl;
		AppRoot = appRoot;
		GalleryRoot = galleryRoot;
	}
}
