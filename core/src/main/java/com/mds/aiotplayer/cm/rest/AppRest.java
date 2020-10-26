/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

/// <summary>
/// A client-optimized object that stores application-level properties for the gallery.
/// </summary>
public class AppRest{
	/// <summary>
	/// Gets the path, relative to the current application, to the directory containing the MDS System
	/// resources such as images, user controls, scripts, etc. Examples: "ds", "MDS\resources"
	/// </summary>
	/// <value>
	/// The gallery resources path.
	/// </value>
	public String GalleryResourcesPath;

	/// <summary>
	/// Gets the path, relative to the current application, to the directory containing the MDS System
	/// skin resources for the currently selected skin. Examples: "ds/skins/simple-grey", "/dev/gallery/mds/skins/simple-grey"
	/// </summary>
	/// <value>The skin path.</value>
	public String SkinPath;

	/// <summary>
	/// Gets the URL, relative to the website root and without any query String parameters, 
	/// to the current page. Example: "/dev/ds/gallery.aspx"
	/// </summary>
	/// <value>
	/// The current page URL.
	/// </value>
	public String CurrentPageUrl;

	/// <summary>
	/// Get the URI scheme, DNS host name or IP address, and port number for the current application. 
	/// Examples: http://www.site.com, http://localhost, http://127.0.0.1, http://godzilla
	/// </summary>
	/// <value>The URL to the current web host.</value>
	public String HostUrl;

	/// <summary>
	/// Gets the URL to the current web application. Does not include the containing page or the trailing slash. 
	///  Example: If the gallery is installed in a virtual directory 'gallery' on domain 'www.site.com', this 
	/// returns 'http://www.site.com/gallery'.
	/// </summary>
	/// <value>The URL to the current web application.</value>
	public String AppUrl;

	/// <summary>
	/// Gets the URL to the list of waiting for approval content objects. Requires running an Enterprise license; 
	/// value will be null when running in trial mode or under other licenses. Ex: http://site.com/gallery/default.aspx?latest=50
	/// </summary>
	/// <value>The URL to the list of waiting for approval content objects.</value>
	public String WaitingForApprovalUrl;
	
	/// <summary>
	/// Gets the URL to the list of recently added content objects. Requires running an Enterprise license; 
	/// value will be null when running in trial mode or under other licenses. Ex: http://site.com/gallery/default.aspx?latest=50
	/// </summary>
	/// <value>The URL to the list of recently added content objects.</value>
	public String LatestUrl;

	/// <summary>
	/// Gets the URL to the list of top rated content objects. Requires running an Enterprise license; 
	/// value will be null when running in trial mode or under other licenses. Ex: http://site.com/gallery/default.aspx?latest=50
	/// </summary>
	/// <value>The URL to the list of top rated content objects.</value>
	public String TopRatedUrl;

	/// <summary>
	/// Gets a value indicating whether the initial 30-day trial for the application has expired and no valid product key 
	/// has been entered.
	/// </summary>
	/// <value><c>true</c> if the application is in the trial period; otherwise, <c>false</c>.</value>
	public boolean IsInReducedFunctionalityMode;
}