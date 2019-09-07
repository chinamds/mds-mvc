package com.mds.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mds.common.Constants;
import com.mds.sys.util.MDSRoleCollection;
import com.mds.sys.util.SecurityGuard;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.util.CMUtils;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.common.mapper.JsonMapper;
import com.mds.core.ActionResult;
import com.mds.core.ApprovalStatus;
import com.mds.core.ApprovalSwitch;
import com.mds.core.MDSDataSchemaVersion;
import com.mds.core.ResourceId;
import com.mds.core.SecurityActions;
import com.mds.core.SecurityActionsOption;
import com.mds.core.exception.ArgumentNullException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.sys.util.AppSettings;
import com.mds.sys.util.RoleUtils;
import com.mds.sys.util.UserUtils;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;

/// <summary>
/// Contains general purpose routines useful for this website as well as a convenient
/// gateway to functionality provided in other business layers.
/// </summary>
public final class Utils{
	//#region Private Static Fields
	
	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	private static final Object sharedLock = new Object();
	private static String galleryRoot;
	private static String galleryResourcesPath;
	private static String skinPath;
	private static String webConfigFilePath;

	//#endregion

	//#region Public Static Properties
	
	/// <summary>
	/// Gets or sets the name of the current user. This property becomes available immediately after a user logs in, even within
	/// the current page's life cycle. This property is preferred over HttpContext.Current.User.Identity.Name, which does not
	/// contain the user's name until the next page load. This property should be set only when the user logs in. When the 
	/// property is not explicitly assigned, it automatically returns the value of HttpContext.Current.User.Identity.Name.
	/// </summary>
	/// <value>The name of the current user.</value>
	public static String getUserName(){
		return UserUtils.getLoginName();
	}

	/// <summary>
	/// Gets a value indicating whether the current user is authenticated. This property becomes true available immediately after 
	/// a user logs in, even within the current page's life cycle. This property is preferred over 
	/// HttpContext.Current.User.Identity.IsAuthenticated, which does not become true until the next page load. 
	/// This property should be set only when the user logs in. When the property is not explicitly assigned, it automatically 
	/// returns the value of HttpContext.Current.User.Identity.IsAuthenticated.
	/// </summary>
	public static boolean isAuthenticated()	{
		return UserUtils.isAuthenticated();
	}

	/// <summary>
	/// Gets a value indicating whether the current request is from the local computer. Returns <c>false</c> if 
	/// <see cref="HttpContext.Current" /> is null.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the current request is from the local computer; otherwise, <c>false</c>.
	/// </value>
	public static boolean isLocalRequest(HttpServletRequest request){
		//if (HttpContext.Current == null)
		//	return false;

		return false;
	}

	/// <summary>
	/// Gets a value indicating whether the current request is in debug mode. That is, it returns <c>true</c> when 
	/// debug = "true" in web.config and returns <c>false</c> when debug = "false".
	/// </summary>
	/// <value><c>true</c> if the current request is in debug mode; otherwise, <c>false</c>.</value>
	public static boolean isDebugEnabled(){
		return false;
	}

	/// <summary>
	/// Get the path, relative to the web site root, to the directory containing the MDS System user controls and 
	/// other resources. Does not include the containing page or the trailing slash. Example: If MDS is installed at 
	/// C:\inetpub\wwwroot\dev\gallery, where C:\inetpub\wwwroot\ is the parent web site, and the gallery support files are in 
	/// the mds directory, this property returns /dev/gallery/mds. Guaranteed to not return null.
	/// </summary>
	/// <value>Returns the path, relative to the web site root, to the directory containing the MDS System user 
	/// controls and other resources.</value>
	public static String getGalleryRoot(HttpServletRequest request){
		if (galleryRoot == null){
			galleryRoot = calculateGalleryRoot(request);
		}

		return galleryRoot;
	}

	/// <summary>
	/// Gets the path, relative to the current application, to the directory containing the MDS System
	/// resources such as images, user controls, scripts, etc. This value is pulled from the AppSettings value "GalleryResourcesPath"
	/// if present; otherwise it defaults to "ds". Examples: "ds", "MDS\resources"
	/// </summary>
	/// <value>Returns the path, relative to the current application, to the directory containing the MDS System
	/// resources such as images, user controls, scripts, etc.</value>
	public static String getGalleryResourcesPath()	{
		if (galleryResourcesPath == null){
			galleryResourcesPath = getGalleryResourcesPathInternal();
		}

		return galleryResourcesPath;
	}

	/// <summary>
	/// Gets the path, relative to the current application, to the directory containing the MDS System
	/// skin resources for the currently selected skin. Examples: "ds/skins/dark", "/dev/gallery/mds/skins/light"
	/// </summary>
	/// <value>Returns the path, relative to the current application, to the directory containing the MDS System
	/// skin resources.</value>
	public static String getSkinPath(HttpServletRequest request){
		if (skinPath == null){
			skinPath = StringUtils.join(getGalleryRoot(request), "/skins/", getSkin());
		}

		return skinPath;
	}

	/// <summary>
	/// Gets the name of the currently selected skin. Examples: "Dark", "Light"
	/// </summary>
	/// <value>Returns the name of the currently selected skin.</value>
	public static String getSkin(){
		return AppSettings.getInstance().getSkin();
	}

	/// <summary>
	/// Get the path, relative to the web site root, to the current web application. Does not include the containing page 
	/// or the trailing slash. Example: If MDS is installed at C:\inetpub\wwwroot\dev\gallery, and C:\inetpub\wwwroot\ is 
	/// the parent web site, this property returns /dev/gallery. Guaranteed to not return null.
	/// </summary>
	/// <value>Get the path, relative to the web site root, to the current web application.</value>
	public static String getAppRoot(HttpServletRequest request){
		return request.getContextPath();
	}

	/// <summary>
	/// Gets or sets the URI of the previous page the user was viewing. The value is stored in the user's session, and 
	/// can be used after a user has completed a task to return to the original page. If the Session object is not available,
	/// no value is saved in the setter and a null is returned in the getter.
	/// </summary>
	/// <value>The URI of the previous page the user was viewing.</value>
	public static URI getPreviousUri(HttpServletRequest request){
		if (request.getSession(false) != null)
			return (URI)request.getSession(false).getAttribute("ReferringUrl");
		else
			return null;
	}

	public static void setPreviousUri(HttpServletRequest request, URI uri){
		if (request.getSession(false) == null)
			return; // Session is disabled for this page.

		request.getSession(false).setAttribute("ReferringUrl", uri);
	}

	public static boolean isPostBack(HttpServletRequest request) {
		/*if ( "POST".equalsIgnoreCase(request.getMethod()) &&
			   ((request.getRequestURL() != null && 
			      request.getRequestURL().toString().equalsIgnoreCase("http://yoursite.com")))) {

			    //postback call 
		}*/
			   
		return true;
	}


	/// <summary>
	/// Gets the path to the install trigger file. Example: "C:\websites\gallery\App_Data\install.txt". This file is expected to be
	/// an empty text file. When present, it is a signal to the application that an installation is being requested.
	/// </summary>
	/// <value>A <see cref="String" />.</value>
	public static String getInstallFilePath(ServletContext context){
		String appDataDirectory = FilenameUtils.concat(context.getRealPath("/"), Constants.AppDataDirectory);
		
		return FilenameUtils.concat(appDataDirectory, Constants.InstallTriggerFileName);
	}

	/// <summary>
	/// Gets a value indicating whether an installation is being requested. Returns <c>true</c> when a text file
	/// named install.txt is present in the App_Data directory.
	/// </summary>
	/// <value><c>true</c> if an install is requested; otherwise, <c>false</c>.</value>
	public static boolean isInstallRequested(ServletContext context){
		return FileMisc.fileExists(getInstallFilePath(context));
	}

	//#endregion

	//#region Public Static Methods

	/// <summary>
	/// Determine whether user has permission to perform at least one of the specified security actions. Un-authenticated users
	/// (anonymous users) are always considered NOT authorized (that is, this method returns false) except when the requested
	/// security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or <see cref="SecurityActions.ViewOriginalContentObject" />,
	/// since MDS System is configured by default to allow anonymous viewing access
	/// but it does not allow anonymous editing of any kind. This method will continue to work correctly if the webmaster configures
	/// MDS System to require users to log in in order to view objects, since at that point there will be no such thing as
	/// un-authenticated users, and the standard MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: SecurityActions.AdministerSite | SecurityActions.AdministerGallery). If multiple actions are
	/// specified, the method is successful if the user has permission for at least one of the actions. If you require that all actions
	/// be satisfied to be successful, call one of the overloads that accept a SecurityActionsOption and
	/// specify <see cref="SecurityActionsOption.RequireAll" />.</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in
	/// this gallery. This parameter is not required <paramref name="securityActions" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="int.MinValue" />).</param>
	/// <param name="isPrivate">Indicates whether the specified album is private (hidden from anonymous users). The parameter
	/// is ignored for logged on users.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is virtual album.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	/// <overloads>
	/// Determine if the current user has permission to perform the requested action.
	///   </overloads>
	public static boolean isUserAuthorized(SecurityActions securityActions, long albumId, long galleryId, boolean isPrivate, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException{
		return isUserAuthorized(securityActions, RoleUtils.getMDSRolesForUser(), albumId, galleryId, isPrivate, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether user has permission to perform the specified security actions. Un-authenticated users
	/// (anonymous users) are always considered NOT authorized (that is, this method returns false) except when the requested
	/// security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or <see cref="SecurityActions.ViewOriginalContentObject" />,
	/// since MDS System is configured by default to allow anonymous viewing access
	/// but it does not allow anonymous editing of any kind. This method will continue to work correctly if the webmaster configures
	/// MDS System to require users to log in in order to view objects, since at that point there will be no such thing as
	/// un-authenticated users, and the standard MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: SecurityActions.AdministerSite | SecurityActions.AdministerGallery).</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in
	/// this gallery. This parameter is not required <paramref name="securityActions" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="int.MinValue" />).</param>
	/// <param name="isPrivate">Indicates whether the specified album is private (hidden from anonymous users). The parameter
	/// is ignored for logged on users.</param>
	/// <param name="secActionsOption">Specifies whether the user must have permission for all items in <paramref name="securityActions" />
	/// to be successful or just one.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is virtual album.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	public static boolean isUserAuthorized(SecurityActions securityActions, long albumId, long galleryId, boolean isPrivate, SecurityActionsOption secActionsOption, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException{
		return isUserAuthorized(securityActions, RoleUtils.getMDSRolesForUser(), albumId, galleryId, isPrivate, secActionsOption, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether user has permission to perform at least one of the specified security actions. Un-authenticated users
	/// (anonymous users) are always considered NOT authorized (that is, this method returns false) except when the requested
	/// security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or <see cref="SecurityActions.ViewOriginalContentObject" />,
	/// since MDS System is configured by default to allow anonymous viewing access
	/// but it does not allow anonymous editing of any kind. This method will continue to work correctly if the webmaster configures
	/// MDS System to require users to log in in order to view objects, since at that point there will be no such thing as
	/// un-authenticated users, and the standard MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: SecurityActions.AdministerSite | SecurityActions.AdministerGallery). If multiple actions are
	/// specified, the method is successful if the user has permission for at least one of the actions. If you require that all actions
	/// be satisfied to be successful, call one of the overloads that accept a SecurityActionsOption and
	/// specify <see cref="SecurityActionsOption.RequireAll" />.</param>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. This parameter is ignored
	/// for anonymous users. The parameter may be null.</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in
	/// this gallery. This parameter is not required <paramref name="securityActions" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="int.MinValue" />).</param>
	/// <param name="isPrivate">Indicates whether the specified album is private (hidden from anonymous users). The parameter
	/// is ignored for logged on users.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is virtual album.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	public static boolean isUserAuthorized(SecurityActions securityActions, MDSRoleCollection roles, long albumId, long galleryId, boolean isPrivate, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException{
		return isUserAuthorized(securityActions, roles, albumId, galleryId, isPrivate, SecurityActionsOption.RequireOne, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether user has permission to perform the specified security actions. When multiple security actions are passed, use
	/// <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied to be successful or only one item
	/// must be satisfied. Un-authenticated users (anonymous users) are always considered NOT authorized (that is, this method returns
	/// false) except when the requested security action is <see cref="SecurityActions.ViewAlbumOrContentObject" /> or
	/// <see cref="SecurityActions.ViewOriginalContentObject" />, since MDS System is configured by default to allow anonymous viewing access
	/// but it does not allow anonymous editing of any kind. This method will continue to work correctly if the webmaster configures
	/// MDS System to require users to log in in order to view objects, since at that point there will be no such thing as
	/// un-authenticated users, and the standard MDS System role functionality applies.
	/// </summary>
	/// <param name="securityActions">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: SecurityActions.AdministerSite | SecurityActions.AdministerGallery). If multiple actions are
	/// specified, use <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied to be successful or
	/// only one item must be satisfied.</param>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. This parameter is ignored
	/// for anonymous users. The parameter may be null.</param>
	/// <param name="albumId">The album ID to which the security action applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in
	/// this gallery. This parameter is not required <paramref name="securityActions" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="int.MinValue" />).</param>
	/// <param name="isPrivate">Indicates whether the specified album is private (hidden from anonymous users). The parameter
	/// is ignored for logged on users.</param>
	/// <param name="secActionsOption">Specifies whether the user must have permission for all items in <paramref name="securityActions" />
	/// to be successful or just one.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is a virtual album.</param>
	/// <returns>
	/// Returns true when the user is authorized to perform the specified security action against the specified album;
	/// otherwise returns false.
	/// </returns>
	public static boolean isUserAuthorized(SecurityActions securityActions, MDSRoleCollection roles, long albumId, long galleryId, boolean isPrivate, SecurityActionsOption secActionsOption, boolean isVirtualAlbum) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return SecurityGuard.isUserAuthorized(securityActions, roles, albumId, galleryId, isAuthenticated(), isPrivate, secActionsOption, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether the user belonging to the specified <paramref name="roles" /> is a site administrator. The user is considered a site
	/// administrator if at least one role has Allow Administer Site permission.
	/// </summary>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. The parameter may be null.</param>
	/// <returns>
	/// 	<c>true</c> if the user is a site administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isUserSiteAdministrator(MDSRoleCollection roles) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException{
		return SecurityGuard.isUserSiteAdministrator(roles);
	}

	/// <summary>
	/// Determine whether the currently logged-on user is a approval content user. The user is considered a approval
	/// content user if at least one role has Allow approval content object.
	/// </summary>
	/// <returns>
	/// 	<c>true</c> if the user is a approval content user; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isCurrentUserApprovalContent() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException	{
		return SecurityGuard.isUserApprovalContent(RoleUtils.getMDSRolesForUser());
	}

	/// <summary>
	/// Determine whether the user belonging to the specified <paramref name="roles"/> is a gallery administrator for the specified
	/// <paramref name="galleryId"/>. The user is considered a gallery administrator if at least one role has Allow Administer Gallery permission.
	/// </summary>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. The parameter may be null.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// 	<c>true</c> if the user is a gallery administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isUserGalleryAdministrator(MDSRoleCollection roles, long galleryId) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return SecurityGuard.isUserGalleryAdministrator(roles, galleryId);
	}

	/// <summary>
	/// Determine whether the currently logged-on user is a site administrator. The user is considered a site
	/// administrator if at least one role has Allow Administer Site permission.
	/// </summary>
	/// <returns>
	/// 	<c>true</c> if the user is a site administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isCurrentUserSiteAdministrator() throws InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException	{
		return isUserSiteAdministrator(RoleUtils.getMDSRolesForUser());
	}

	/// <summary>
	/// Determine whether the currently logged-on user is a gallery administrator for the specified <paramref name="galleryId"/>. 
	/// The user is considered a gallery administrator if at least one role has Allow Administer Gallery permission.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// 	<c>true</c> if the user is a gallery administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isCurrentUserGalleryAdministrator(long galleryId) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException	{
		return SecurityGuard.isUserGalleryAdministrator(RoleUtils.getMDSRolesForUser(), galleryId);
	}

	/// <summary>
	/// Determines whether the current request is a Web.API request.
	/// </summary>
	/// <returns><c>true</c> if it is web API request; otherwise, <c>false</c>.</returns>
	public static boolean isWebApiRequest(HttpServletRequest request)	{
		if (request == null)
			return false;

		String urlPath = request.getRequestURI(); //.AppRelativeCurrentExecutionFilePath;

		return (urlPath != null && urlPath.startsWith("/services/api"));
	}
	
	public static boolean isWebAppRequest(HttpServletRequest request)	{
		if (request == null)
			return false;

		String urlPath = request.getRequestURI(); //.AppRelativeCurrentExecutionFilePath;

		return (urlPath != null && urlPath.startsWith("/app"));
	}
	
	public static boolean isIndependentSpaceForDailyList()	{
		return (AppSettings.getInstance().getIndependentSpaceForDailyList() == null ? true : AppSettings.getInstance().getIndependentSpaceForDailyList());
	}

	/// <summary>
	/// Determine the trust level of the currently running application.
	/// </summary>
	/// <returns>Returns the trust level of the currently running application.</returns>
	/*public static ApplicationTrustLevel GetCurrentTrustLevel()
	{
		AspNetHostingPermissionLevel aspnetTrustLevel = AspNetHostingPermissionLevel.None;

		for (AspNetHostingPermissionLevel aspnetTrustLevelIterator in
			new AspNetHostingPermissionLevel[] {
												AspNetHostingPermissionLevel.Unrestricted,
												AspNetHostingPermissionLevel.High,
												AspNetHostingPermissionLevel.Medium,
												AspNetHostingPermissionLevel.Low,
												AspNetHostingPermissionLevel.Minimal 
												})
		{
			try
			{
				new AspNetHostingPermission(aspnetTrustLevelIterator).Demand();
				aspnetTrustLevel = aspnetTrustLevelIterator;
				break;
			}
			catch (SecurityException)
			{
				continue;
			}
		}

		ApplicationTrustLevel trustLevel = ApplicationTrustLevel.None;

		switch (aspnetTrustLevel)
		{
			case AspNetHostingPermissionLevel.Minimal: trustLevel = ApplicationTrustLevel.Minimal; break;
			case AspNetHostingPermissionLevel.Low: trustLevel = ApplicationTrustLevel.Low; break;
			case AspNetHostingPermissionLevel.Medium: trustLevel = ApplicationTrustLevel.Medium; break;
			case AspNetHostingPermissionLevel.High: trustLevel = ApplicationTrustLevel.High; break;
			case AspNetHostingPermissionLevel.Unrestricted: trustLevel = ApplicationTrustLevel.Full; break;
			default: trustLevel = ApplicationTrustLevel.Unknown; break;
		}

		return trustLevel;
	}*/

	/// <summary>
	/// Get the path, relative to the web site root, to the specified resource. Example: If the web application is at
	/// /dev/gsweb/, the directory containing the resources is /ds/, and the desired resource is /images/info.gif, this function
	/// will return /dev/gsweb/ds/images/info.gif.
	/// </summary>
	/// <param name="resource">A path relative to the directory containing the MDS System resource files (ex: images/info.gif).
	/// The leading forward slash ('/') is optional.</param>
	/// <returns>Returns the path, relative to the web site root, to the specified resource.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="resource" /> is null.</exception>
	public static String getUrl(HttpServletRequest request, String resource){
		if (resource == null)
			throw new ArgumentNullException("resource");

		if (!resource.startsWith("/"))
			resource = StringUtils.insert(resource, 0, "/"); // Make sure it starts with a '/'

		resource = StringUtils.join(getGalleryRoot(request), resource);

		//#if DEBUG
		//      if (!System.IO.File.Exists(HttpContext.Current.Server.MapPath(resource)))
		//        throw new System.IO.FileNotFoundException(MessageFormat.format(CultureInfo.CurrentCulture, "No file exists at {0}.", resource), resource);
		//#endif

		return resource;
	}

	/// <summary>
	/// Get the path, relative to the web site root, to the specified resource in the current skin directory. Example: 
	/// If the web application is at /dev/gsweb/, the directory containing the skin resources is /ds/skins/simple-grey,
	/// and the desired resource is /images/info.gif, this function will return /dev/gsweb/ds/skins/simple-grey/images/info.gif.
	/// </summary>
	/// <param name="resource">A path relative to the skin directory containing the MDS System resource files (ex: images/info.gif).
	/// The leading forward slash ('/') is optional but recommended for readability and a slight performance improvement.</param>
	/// <returns>Returns the path, relative to the web site root, to the specified skin resource.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="resource" /> is null.</exception>
	public static String getSkinnedUrl(HttpServletRequest request, String resource){
		if (resource == null)
			throw new ArgumentNullException("resource");

		if (!resource.startsWith("/"))
			resource = StringUtils.insert(resource, 0, "/"); // Make sure it starts with a '/'

		resource = StringUtils.join(getSkinPath(request), resource);

		//#if DEBUG
		//      if (!System.IO.File.Exists(HttpContext.Current.Server.MapPath(resource)))
		//        throw new System.IO.FileNotFoundException(MessageFormat.format(CultureInfo.CurrentCulture, "No file exists at {0}.", resource), resource);
		//#endif

		return resource;
	}

	/// <overloads>Get an URL relative to the website root for the requested page.</overloads>
	/// <summary>
	/// Get an URL relative to the website root for the requested <paramref name="page"/>. Example: If 
	/// <paramref name="page"/> is ResourceId.album and the current page is /dev/ds/gallery.aspx, this function 
	/// returns /dev/ds/gallery.aspx?g=album. Returns null if <see cref="HttpContext.Current" /> is null.
	/// </summary>
	/// <param name="page">A <see cref="ResourceId"/> enumeration that represents the desired <see cref="GalleryPage"/>.</param>
	/// <returns>Returns an URL relative to the website root for the requested <paramref name="page"/>, or null 
	/// if <see cref="HttpContext.Current" /> is null.</returns>
	public static String getUrl(HttpServletRequest request, ResourceId page){
		if (request == null)
			return null;

		return addQueryStringParameter(getCurrentPageUrl(request), StringUtils.join("g=", page));
	}

	/// <summary>
	/// Get an URL relative to the website root for the requested <paramref name="page"/> and with the specified 
	/// <paramref name="args"/> appended as query String parameters. Example: If <paramref name="page"/> is ResourceId.task_addobjects, 
	/// the current page is /dev/ds/gallery.aspx, <paramref name="format"/> is "aid={0}", and <paramref name="args"/>
	/// is "23", this function returns /dev/ds/gallery.aspx?g=task_addobjects&amp;aid=23. If the <paramref name="page"/> is
	/// <see cref="ResourceId.album"/> or <see cref="ResourceId.contentobject"/>, don't include the "g" query String parameter, since 
	/// we can deduce it by looking for the aid or moid query String parms. Returns null if <see cref="HttpContext.Current" /> is null.
	/// </summary>
	/// <param name="page">A <see cref="ResourceId"/> enumeration that represents the desired <see cref="GalleryPage"/>.</param>
	/// <param name="format">A format String whose placeholders are replaced by values in <paramref name="args"/>. Do not use a '?'
	/// or '&amp;' at the beginning of the format String. Example: "msg={0}".</param>
	/// <param name="args">The values to be inserted into the <paramref name="format"/> String.</param>
	/// <returns>Returns an URL relative to the website root for the requested <paramref name="page"/>, or 
	/// null if <see cref="HttpContext.Current" /> is null.</returns>
	public static String getUrl(HttpServletRequest request, ResourceId page, String format, Object... args)	{
		if (request == null)
			return null;

		String queryString = StringUtils.format(format, args);

		if ((page != ResourceId.album) && (page != ResourceId.contentobject)){
			// Don't use the "g" parameter for album or contentobject pages, since we can deduce it by looking for the 
			// aid or moid query String parms. This results in a shorter, cleaner URL.
			queryString = StringUtils.join("g=", page.toString(), "&", queryString);
		}

		return addQueryStringParameter(getCurrentPageUrl(request), queryString);
	}
	
	public static String getUrl(String currentPageUrl, ResourceId page, String format, Object... args)	{
		String queryString = StringUtils.format(format, args);

		if ((page != ResourceId.album) && (page != ResourceId.contentobject)){
			// Don't use the "g" parameter for album or contentobject pages, since we can deduce it by looking for the 
			// aid or moid query String parms. This results in a shorter, cleaner URL.
			queryString = StringUtils.join("g=", page.toString(), "&", queryString);
		}

		return addQueryStringParameter(currentPageUrl, queryString);
	}

	/// <summary>
	/// Get the physical path to the <paramref name="resource"/>. Example: If the web application is at
	/// C:\inetpub\wwwroot\dev\gsweb\, the directory containing the resources is \ds\, and the desired resource is
	/// /templates/AdminNotificationAccountCreated.txt, this function will return 
	/// C:\inetpub\wwwroot\dev\gsweb\ds\templates\AdminNotificationAccountCreated.txt.
	/// </summary>
	/// <param name="resource">A path relative to the directory containing the MDS System resource files (ex: images/info.gif).
	/// The slash may be forward (/) or backward (\), although there is a slight performance improvement if it is forward (/).
	/// The parameter does not require a leading slash, although there is a slight performance improvement if it is present.</param>
	/// <returns>Returns the physical path to the requested <paramref name="resource"/>.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="resource" /> is null.</exception>
	public static String getPath(HttpServletRequest request, String resource){
		if (resource == null)
			throw new ArgumentNullException("resource");

		// Convert back slash (\) to forward slash, if present.
		resource = resource.replace(File.separatorChar, '/');
		String path = StringUtils.join(request.getServletContext().getRealPath("/"), getUrl(request, resource));
		path.replace('/', File.separatorChar);
		path.replace('\\', File.separatorChar);

		return path;
	}

	/// <summary>
	/// Gets the URI of the current page request. Automatically handles port forwarding configurations by incorporating the port in the
	/// HTTP_HOST server variable in the URI. Ex: "http://75.135.92.12:8080/dev/ds/default.aspx?moid=770"
	/// Returns null if <see cref="HttpContext.Current" /> is null.
	/// </summary>
	/// <returns>Returns the URI of the current page request, or null if <see cref="HttpContext.Current" /> is null.</returns>
	public static URI getCurrentPageURI(HttpServletRequest request)	{
		if (request == null)
			return null;

		StringBuffer url = new StringBuffer();
        int port = request.getServerPort();
        if (port < 0) {
            port = 80; // Work around java.net.URL bug
        }
        String scheme = request.getScheme();
        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
            url.append(':');
            url.append(port);
        }
        url.append(request.getContextPath());
        String servletPath = request.getServletPath();
        if (servletPath.contains("/app/"))
        	servletPath = servletPath.replace("/app/", "/");
        else {
        	if (servletPath.equals("/app"))
        		servletPath = StringUtils.EMPTY;
        }
        url.append(servletPath);
        if (request.getPathInfo() != null)
        	url.append(request.getPathInfo());
        url.append("?");
        url.append(request.getQueryString());
        URI uri = URI.create(url.toString());
        
		return uri;
	}

	/// <summary>
	/// Gets the URL, relative to the website root and optionally including any query String parameters, to the current page.
	/// This method is a wrapper for a call to request.Url. If the current URL is an API call (i.e. it starts
	/// with "~/api", the referrer is used instead. Returns null if <see cref="HttpContext.Current" /> is null.
	/// Examples: "/dev/ds/gallery.aspx", "/dev/ds/gallery.aspx?g=admin_email&amp;aid=2389" 
	/// </summary>
	/// <param name="includeQueryString">When <c>true</c> the query String is included.</param>
	/// <returns>Returns the URL, relative to the website root and including any query String parameters, to the current page,
	/// or null if <see cref="HttpContext.Current" /> is null.</returns>
	public static String getCurrentPageUrl(HttpServletRequest request){
		return getCurrentPageUrl(request, false);
	}
	
	public static String getCurrentPageUrl(HttpServletRequest request, boolean includeQueryString){
		if (request == null)
			return null;

		//String urlPath = request.getServletPath();
		String urlContext = request.getContextPath();
		//String urlPath = request.getRequestURI();
		String servletPath = request.getServletPath();
        if (servletPath.contains("/app/"))
        	servletPath = servletPath.replace("/app/", "/");
        else {
        	if (servletPath.equals("/app"))
        		servletPath = StringUtils.EMPTY;
        }
		String urlPath = request.getPathInfo();
		String query = request.getQueryString();

		/*if (isWebApiRequest(request)){
			if (request.UrlReferrer != null)
			{
				urlPath = request.UrlReferrer.AbsolutePath;
				query = request.UrlReferrer.Query;
			}
			else
			{
				// We don't know the current web page, so just return an empty String. This should not typically occur.
				urlPath = query = StringUtils.EMPTY;
			}
		}*/

		if (includeQueryString)
			return StringUtils.join(urlContext, servletPath, urlPath, "?", query);
		else
			return StringUtils.join(urlContext, servletPath, urlPath);
	}

	/// <summary>
	/// Get the full path to the current web page. Does not include any query String parms. Returns null if 
	/// <see cref="HttpContext.Current" /> is null. Example: "http://www.techinfosystems.com/ds/default.aspx"
	/// </summary>
	/// <returns>Returns the full path to the current web page, or null if <see cref="HttpContext.Current" /> is null.</returns>
	/// <remarks>This value is calculated each time it is requested because the URL may be different for different users 
	/// (a local admin's URL may be http://localhost/ds/default.aspx, someone on the intranet may get the server's name
	/// (http://Server1/ds/default.aspx), and someone on the internet may get the full name (http://www.bob.com/ds/default.aspx).</remarks>
	public static String getCurrentPageUrlFull(HttpServletRequest request){
		if (request == null)
			return null;

		return StringUtils.join(getHostUrl(request), getCurrentPageUrl(request));
	}

	/// <summary>
	/// Get the URI scheme, DNS host name or IP address, and port number for the current application. 
	/// Examples: http://www.site.com, http://localhost, http://127.0.0.1, http://godzilla
	/// Returns null if <see cref="HttpContext.Current" /> is null.
	/// </summary>
	/// <returns>Returns the URI scheme, DNS host name or IP address, and port number for the current application, 
	/// or null if <see cref="HttpContext.Current" /> is null.</returns>
	/// <remarks>This value is retrieved from the user's session. If not present in the session, such as when the user first arrives, it
	/// is calculated by parsing the appropriate pieces from request.Url and the HTTP_HOST server variable. The path is 
	/// calculated on a per-user basis because the URL may be different for different users (a local admin's URL may be 
	/// http://localhost, someone on the intranet may get the server's name (http://Server1), and someone on the internet may get 
	/// the full name (http://www.site.com).</remarks>
	public static String getHostUrl(HttpServletRequest request)	{
		if (request == null) return null;
        
        StringBuffer url = new StringBuffer();
        int port = request.getServerPort();
        if (port < 0) {
            port = 80; // Work around java.net.URL bug
        }
        String scheme = request.getScheme();
        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());

        return url.toString();
	}

	/// <summary>
	/// Gets the URL to the current web application. Does not include the containing page or the trailing slash. 
	/// Guaranteed to not return null. Example: If the gallery is installed in a virtual directory 'gallery'
	/// on domain 'www.site.com', this returns 'http://www.site.com/gallery'.
	/// </summary>
	/// <returns>Returns the URL to the current web application.</returns>
	public static String getAppUrl(HttpServletRequest request){
		return StringUtils.join(getHostUrl(request), getAppRoot(request));
	}
	
	/// <summary>
	/// Gets the URL to the list of recently added content objects. Ex: http://site.com/gallery/default.aspx?latest=50
	/// Requires gallery to be running an Enterprise license; otherwise it returns null.
	/// </summary>
	/// <returns>Returns the URL to the recently added content objects.</returns>
	public static String getWaitingForApprovalUrl(long galleryId, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidGalleryException, InvalidAlbumException, InvalidMDSRoleException{
		if ((CMUtils.loadGallerySetting(galleryId).getApprovalSwitch() & ApprovalSwitch.content.value()) > 0 && isCurrentUserApprovalContent())
			return addQueryStringParameter(getCurrentPageUrl(request), StringUtils.format("approval={1}&gid={0}", galleryId, ApprovalStatus.Waiting.toString()));
		else
			return null;
	}
	
	/// <summary>
	/// Gets the URL to the list of recently added content objects. Ex: http://site.com/gallery/default.aspx?latest=50
	/// Requires gallery to be running an Enterprise license; otherwise it returns null.
	/// </summary>
	/// <returns>Returns the URL to the recently added content objects.</returns>
	public static String getLatestUrl(long galleryId, HttpServletRequest request){
		/*if (AppSettings.getInstance().License.LicenseType == LicenseLevel.Enterprise)
			return AddQueryStringParameter(GetCurrentPageUrl(), "latest=50");
		else
			return null;*/
		return addQueryStringParameter(getCurrentPageUrl(request), StringUtils.format("latest=50&gid={0}", galleryId));
	}

	/// <summary>
	/// Gets the URL to the list of top rated content objects. Ex: http://site.com/gallery/default.aspx?latest=50
	/// Requires gallery to be running an Enterprise license; otherwise it returns null.
	/// </summary>
	/// <returns>Returns the URL to the top rated content objects.</returns>
	public static String getTopRatedUrl(long galleryId, HttpServletRequest request){
		/*if (AppSettings.getInstance().License.LicenseType == LicenseLevel.Enterprise)
			return AddQueryStringParameter(GetCurrentPageUrl(), "rating=highest&top=50");
		else
			return null;*/
		return addQueryStringParameter(getCurrentPageUrl(request), StringUtils.format("rating=highest&top=50&gid={0}", galleryId));
	}

	/// <summary>
	/// Gets the full URL to the directory containing the gallery resources. Does not include the containing page or 
	/// the trailing slash. Guaranteed to not return null. Example: If the gallery is installed in a virtual directory 'gallery'
	/// on domain 'www.site.com' and the resources are in directory 'ds', this returns 'http://www.site.com/gallery/ds'.
	/// </summary>
	/// <returns>Returns the full URL to the directory containing the gallery resources.</returns>
	public static String getGalleryResourcesUrl(HttpServletRequest request){
		return StringUtils.join(getHostUrl(request), getGalleryRoot(request));
	}

	/// <summary>
	/// Gets the Domain Name System (DNS) host name or IP address and the port number for the current web application. Includes the
	/// port number if it differs from the default port. The value is generated from the HTTP_HOST server variable if present; 
	/// otherwise request.Url.Authority is used. Ex: "www.site.com", "www.site.com:8080", "192.168.0.50", "75.135.92.12:8080"
	/// </summary>
	/// <returns>A <see cref="String" /> containing the authority component of the URI for the current web application.</returns>
	/// <remarks>This function correctly handles configurations where the web application is port forwarded through a router. For 
	/// example, if the router is configured to map incoming requests at www.site.com:8080 to an internal IP 192.168.0.100:8056,
	/// this function returns "www.site.com:8080". This is accomplished by using the HTTP_HOST server variable rather than 
	/// request.Url.Authority (when HTTP_HOST is present).</remarks>
	public static String getHostNameAndPort(HttpServletRequest request)	{

        StringBuffer url = new StringBuffer();
        int port = request.getServerPort();
        if (port < 0) {
            port = 80; // Work around java.net.URL bug
        }
        String scheme = request.getScheme();
        url.append(request.getServerName());
        if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
            url.append(':');
            url.append(port);
        }

        return url.toString();
	}

	/// <summary>
	/// Gets the host name for the current request. Does not include port number or scheme. The value is generated from the 
	/// HTTP_HOST server variable if present; otherwise request.Url.Authority is used. 
	/// Ex: "www.site.com", "75.135.92.12"
	/// </summary>
	/// <returns>Returns the host name for the current request.</returns>
	public static String getHostName(HttpServletRequest request){
		return request.getServerName();
	}

	/// <summary>
	/// Gets the port for the current request if one is specified; otherwise returns null. The value is generated from the 
	/// HTTP_HOST server variable if present; otherwise request.Url.Authority is used. 
	/// </summary>
	/// <returns>Returns the port for the current request if one is specified; otherwise returns null.</returns>
	public static int getPort(HttpServletRequest request){
		return request.getServerPort();
	}

	/// <overloads>Redirects the user to the specified <paramref name="page"/>.</overloads>
	/// <summary>
	/// Redirects the user to the specified <paramref name="page"/>. The redirect occurs immediately.
	/// </summary>
	/// <param name="page">A <see cref="ResourceId"/> enumeration that represents the desired <see cref="GalleryPage"/>.</param>
	public static void redirect(HttpServletRequest request, HttpServletResponse response, ResourceId page) throws ServletException, IOException{
		request.getRequestDispatcher(getUrl(request, page)).forward(request, response);
	}

	/// <summary>
	/// Redirects the user, using Response.Redirect, to the specified <paramref name="page"/>. If <paramref name="endResponse"/> is true, the redirect occurs 
	/// when the page has finished processing all events. When false, the redirect occurs immediately.
	/// </summary>
	/// <param name="page">A <see cref="ResourceId"/> enumeration that represents the desired <see cref="GalleryPage"/>.</param>
	/// <param name="endResponse">When <c>true</c> the redirect occurs immediately. When false, the redirect is delayed until the
	/// page processing is complete.</param>
	public static void redirect(HttpServletRequest request, HttpServletResponse response, ResourceId page, boolean endResponse) throws ServletException, IOException	{
		request.getRequestDispatcher(getUrl(request, page)).forward(request, response);
		/*if (endResponse)
			request.getRequestDispatcher(getUrl(request, page)).forward(request, response);
		else
			request.getRequestDispatcher(getUrl(request, page)).include(request, response);*/
	}

	/// <summary>
	/// Redirects the user, using Response.Redirect, to the specified <paramref name="page"/> and with the specified 
	/// <paramref name="args"/> appended as query String parameters. Example: If <paramref name="page"/> is ResourceId.album, 
	/// the current page is /dev/ds/gallery.aspx, <paramref name="format"/> is "aid={0}", and <paramref name="args"/>
	/// is "23", this function redirects to /dev/ds/gallery.aspx?g=album&amp;aid=23.
	/// </summary>
	/// <param name="page">A <see cref="ResourceId"/> enumeration that represents the desired <see cref="GalleryPage"/>.</param>
	/// <param name="format">A format String whose placeholders are replaced by values in <paramref name="args"/>. Do not use a '?'
	/// or '&amp;' at the beginning of the format String. Example: "msg={0}".</param>
	/// <param name="args">The values to be inserted into the <paramref name="format"/> String.</param>
	public static void redirect(HttpServletRequest request, HttpServletResponse response, ResourceId page, String format, Object... args) throws ServletException, IOException{
		request.getRequestDispatcher(getUrl(request, page, format, args)).forward(request, response);
	}

	/// <summary>
	/// Redirects the user, using Response.Redirect, to the specified <paramref name="url"/>
	/// </summary>
	/// <param name="url">The URL to redirect the user to.</param>
	public static void redirect(HttpServletResponse response, String url) throws IOException	{
		response.sendRedirect(url);
	}

	/// <summary>
	/// Transfers the user, using Server.Transfer, to the specified <paramref name="page"/>.
	/// </summary>
	/// <param name="page">A <see cref="ResourceId"/> enumeration that represents the desired <see cref="GalleryPage"/>.</param>
	public static void transfer(HttpServletRequest request, HttpServletResponse response, ResourceId page){
		try
		{
			response.sendRedirect(getUrl(request, page));
			//request.getServletContext()..Transfer(GetUrl(page));
		}
		catch (Exception ex) { }
	}

	/// <summary>
	/// Redirects the user to the specified <paramref name="page"/> and with the specified 
	/// <paramref name="args"/> appended as query String parameters. Example: If <paramref name="page"/> is ResourceId.album, 
	/// the current page is /dev/ds/gallery.aspx, <paramref name="format"/> is "aid={0}", and <paramref name="args"/>
	/// is "23", this function redirects to /dev/ds/gallery.aspx?g=album&amp;aid=23.
	/// </summary>
	/// <param name="page">A <see cref="ResourceId"/> enumeration that represents the desired <see cref="GalleryPage"/>.</param>
	/// <param name="endResponse">When <c>true</c> the redirect occurs immediately. When false, the redirect is delayed until the
	/// page processing is complete.</param>
	/// <param name="format">A format String whose placeholders are replaced by values in <paramref name="args"/>. Do not use a '?'
	/// or '&amp;' at the beginning of the format String. Example: "msg={0}".</param>
	/// <param name="args">The values to be inserted into the <paramref name="format"/> String.</param>
	public static void redirect(HttpServletRequest request, HttpServletResponse response, ResourceId page, boolean endResponse, String format, Object... args) throws ServletException, IOException{
		//HttpContext.Current.Response.Redirect(GetUrl(page, format, args), endResponse);
		//HttpContext.Current.ApplicationInstance.CompleteRequest();
		request.getRequestDispatcher(getUrl(request, page, format, args)).forward(request, response);
	}

	/// <summary>
	/// Retrieves the specified query String parameter value from the query String. Returns int.MinValue if
	/// the parameter is not found, it is not a valid integer, or it is &lt;= 0.
	/// </summary>
	/// <param name="parameterName">The name of the query String parameter for which to retrieve it's value.</param>
	/// <returns>Returns the value of the specified query String parameter.</returns>
	public static int getQueryStringParameterInt32(HttpServletRequest request, String parameterName){
		String parm = request.getParameter(parameterName);
		
		return StringUtils.toInteger(parm);
	}
	
	public static long getQueryStringParameterInt64(HttpServletRequest request, String parameterName){
		String parm = request.getParameter(parameterName);
		
		return StringUtils.toLong(parm);
	}

	/// <summary>
	/// Retrieves the specified query String parameter value from the query String. If no URI is specified, the current 
	/// request URL is used. Returns int.MinValue if the parameter is not found, it is not a valid integer, or it is &lt;= 0.
	/// </summary>
	/// <param name="uri">The URI containing the query String parameter to retrieve.</param>
	/// <param name="parameterName">The name of the query String parameter for which to retrieve it's value.</param>
	/// <returns>Returns the value of the specified query String parameter.</returns>
	public static int getQueryStringParameterInt32(HttpServletRequest request, URI uri, String parameterName){
		String parm = null;
		if (uri == null){
			parm = request.getParameter(parameterName);
		}else{
			String qs = StringUtils.stripStart(uri.getQuery(), "?");
			String[] nameValuePairs = StringUtils.split(qs, '&');
			for (String nameValuePair : nameValuePairs){
				String[] nameValue = StringUtils.split(nameValuePair, '=');
				if (nameValue.length > 1){
					if (nameValue[0].equals(parameterName))	{
						parm = nameValue[1];
						break;
					}
				}
			}
		}

		if (StringUtils.isEmpty(parm)){
			return Integer.MIN_VALUE;
		}else{		
			return StringUtils.toInteger(parm);
		}
	}
	
	public static long getQueryStringParameterInt64(HttpServletRequest request, URI uri, String parameterName){
		String parm = null;
		if (uri == null){
			parm = request.getParameter(parameterName);
		}else{
			String qs = StringUtils.stripStart(uri.getQuery(), "?");
			String[] nameValuePairs = StringUtils.split(qs, '&');
			for (String nameValuePair : nameValuePairs){
				String[] nameValue = StringUtils.split(nameValuePair, '=');
				if (nameValue.length > 1){
					if (nameValue[0].equals(parameterName))	{
						parm = nameValue[1];
						break;
					}
				}
			}
		}

		if (StringUtils.isEmpty(parm)){
			return Long.MIN_VALUE;
		}else{		
			return StringUtils.toLong(parm);
		}
	}

	/// <summary>
	/// Retrieves the specified query String parameter value from the query String. Returns StringUtils.EMPTY 
	/// if the parameter is not found.
	/// </summary>
	/// <param name="parameterName">The name of the query String parameter for which to retrieve it's value.</param>
	/// <returns>Returns the value of the specified query String parameter.</returns>
	/// <remarks>Do not call UrlDecode on the String, as it appears that .NET already does this.</remarks>
	public static String getQueryStringParameterString(HttpServletRequest request, String parameterName){
		return request.getParameter(parameterName) != null ? request.getParameter(parameterName) : StringUtils.EMPTY;
	}

	/// <summary>
	/// Retrieves the specified query String parameter values from the query String as an array. When the query
	/// String value contains the <paramref name="delimiter" />, the value is split into an array of items.
	/// Returns null if the parameter is not found. Any leading or trailing apostrophes, quotation marks, or 
	/// spaces are removed. Example: If <paramref name="parameterName" />="tag", 
	/// <paramref name="delimiter" />="," and the query String is "tag=misty morning,fox&amp;people=Toby", this method
	/// returns a String array { "misty morning", "fox" }.
	/// </summary>
	/// <param name="parameterName">The name of the query String parameter for which to retrieve it's value.</param>
	/// <param name="delimiter">The delimiter to separate the query String value by. Default value is '+'.
	/// To specify '+' delimiter in the query String, it must be encoded as '%2B'.</param>
	/// <returns>Returns a String[] representing the value(s) of the specified query String parameter.</returns>
	/// <remarks>Do not call UrlDecode on the String, as it appears that .NET already does this.</remarks>
	public static String[] getQueryStringParameterStrings(HttpServletRequest request, String parameterName)	{
		return getQueryStringParameterStrings(request, parameterName, '+');
	}
	
	public static String[] getQueryStringParameterStrings(HttpServletRequest request, String parameterName, char delimiter)	{
		return request.getParameterValues(parameterName);
	}

	/// <summary>
	/// Splits the <paramref name="value" /> into an array based on the <paramref name="delimiter" />.
	/// Any leading or trailing apostrophes, quotation marks, or spaces are removed.
	/// </summary>
	/// <param name="value">The value to convert to an array.</param>
	/// <param name="delimiter">The delimiter to separate the <paramref name="value" /> by. Default value is '+'.
	/// </param>
	/// <returns>System.String[].</returns>
	public static String[] toArray(String value) {
		return toArray(value, '+');
	}
	
	public static String[] toArray(String value, char delimiter){
		return (value == null ?
			null : StringUtils.stripAll(StringUtils.split(value, delimiter), "\"' "));
	}

	/// <summary>
	/// Retrieves the specified query String parameter value from the specified <paramref name="uri"/>. Returns 
	/// StringUtils.EMPTY if the parameter is not found.
	/// </summary>
	/// <param name="uri">The URI to search.</param>
	/// <param name="parameterName">The name of the query String parameter for which to retrieve it's value.</param>
	/// <returns>Returns the value of the specified query String parameter found in the <paramref name="uri"/>.</returns>
	public static String getQueryStringParameterString(HttpServletRequest request, URI uri, String parameterName){
		String parm = null;
		if (uri == null){
			parm = request.getParameter(parameterName);
		}
		else
		{
			String qs = StringUtils.stripStart(uri.getQuery(), "?");
			String[] nameValuePairs = StringUtils.split(qs, '&');
			for (String nameValuePair : nameValuePairs){
				String[] nameValue = StringUtils.split(nameValuePair, '=');
				if (nameValue.length > 1){
					if (nameValue[0].equals(parameterName))	{
						parm = nameValue[1];
						break;
					}
				}
			}
		}

		if (parm == null){
			return StringUtils.EMPTY;
		}else{
			return parm;
		}
	}

	/// <summary>
	/// Retrieves the specified query String parameter value from the query String. The values "true" and "1"
	/// are returned as true; any other value is returned as false. It is not case sensitive. The boolean is not
	/// set if the parameter is not present in the query String (i.e. the HasValue property is false).
	/// </summary>
	/// <param name="parameterName">The name of the query String parameter for which to retrieve it's value.</param>
	/// <returns>Returns the value of the specified query String parameter.</returns>
	public static Boolean getQueryStringParameterBoolean(HttpServletRequest request, String parameterName){
		Boolean parmValue = null;

		String parm = request.getParameter(parameterName);

		if (parm != null){
			if ((parm.equals("1")) || (parm.equalsIgnoreCase("TRUE"))){
				parmValue = true;
			}else{
				parmValue = false;
			}
		}

		return parmValue;
	}
	
	 /**
     * Obtain a parameter from the given request as an int. <code>-1</code> is
     * returned if the parameter is garbled or does not exist.
     *
     * @param request the HTTP request
     * @param param   the name of the parameter
     * @return the integer value of the parameter, or -1
     */
    public static int getIntParameter(HttpServletRequest request, String param) {
        String val = request.getParameter(param);

        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            // Problem with parameter
            return -1;
        }
    }

    /**
     * Obtain a parameter from the given request as a UUID. <code>null</code> is
     * returned if the parameter is garbled or does not exist.
     *
     * @param request the HTTP request
     * @param param   the name of the parameter
     * @return the integer value of the parameter, or -1
     */
    public static UUID getUUIDParameter(HttpServletRequest request, String param) {
        String val = request.getParameter(param);
        if (StringUtils.isEmpty(val)) {
            return null;
        }

        try {
            return UUID.fromString(val.trim());
        } catch (Exception e) {
            // at least log this error to make debugging easier
            // do not silently return null only.
            log.warn("Unable to recoginze UUID from String \""
                         + val + "\". Will return null.", e);
            // Problem with parameter
            return null;
        }
    }

    /**
     * Obtain a List of UUID parameters from the given request as an UUID. null
     * is returned if parameter doesn't exist. <code>null</code> is returned in
     * position of the list if that particular value is garbled.
     *
     * @param request the HTTP request
     * @param param   the name of the parameter
     * @return list of UUID or null
     */
    public static List<UUID> getUUIDParameters(HttpServletRequest request,
                                               String param) {
        String[] request_values = request.getParameterValues(param);

        if (request_values == null) {
            return null;
        }

        List<UUID> return_values = new ArrayList<UUID>(request_values.length);

        for (String s : request_values) {
            try {
                return_values.add(UUID.fromString(s.trim()));
            } catch (Exception e) {
                // Problem with parameter, stuff null in the list
                return_values.add(null);
            }
        }

        return return_values;
    }


    /**
     * Obtain an array of int parameters from the given request as an int. null
     * is returned if parameter doesn't exist. <code>-1</code> is returned in
     * array locations if that particular value is garbled.
     *
     * @param request the HTTP request
     * @param param   the name of the parameter
     * @return array of integers or null
     */
    public static int[] getIntParameters(HttpServletRequest request,
                                         String param) {
        String[] request_values = request.getParameterValues(param);

        if (request_values == null) {
            return null;
        }

        int[] return_values = new int[request_values.length];

        for (int x = 0; x < return_values.length; x++) {
            try {
                return_values[x] = Integer.parseInt(request_values[x]);
            } catch (Exception e) {
                // Problem with parameter, stuff -1 in this slot
                return_values[x] = -1;
            }
        }

        return return_values;
    }
    
    public static long[] getLongParameters(HttpServletRequest request,
    										String param) {
		String[] request_values = request.getParameterValues(param);
		
		if (request_values == null) {
			return null;
		}
		
		long[] return_values = new long[request_values.length];
		
		for (int x = 0; x < return_values.length; x++) {
			try {
				return_values[x] = Long.parseLong(request_values[x]);
			} catch (Exception e) {
				// Problem with parameter, stuff -1 in this slot
				return_values[x] = Long.MIN_VALUE;
			}
		}
		
		return return_values;
	}

    /**
     * Obtain a parameter from the given request as a boolean.
     * <code>false</code> is returned if the parameter is garbled or does not
     * exist.
     *
     * @param request the HTTP request
     * @param param   the name of the parameter
     * @return the integer value of the parameter, or -1
     */
    public static boolean getBoolParameter(HttpServletRequest request,
                                           String param) {
        return ((request.getParameter(param) != null) && request.getParameter(
            param).equalsIgnoreCase("true"));
    }

	public static URI addQueryStringParameter(URI uri, String queryStringParameterNameValue) throws URISyntaxException{
		return new URI(addQueryStringParameter(uri.toString(), queryStringParameterNameValue));
	}

	/// <summary>
	/// Append the String to the url as a query String parameter. If the <paramref name="url" /> already contains the
	/// specified query String parameter, it is replaced with the new one.
	/// Example:
	/// Url = "www.mmdsplus.com/index.aspx?aid=5&amp;msg=3"
	/// QueryStringParameterNameValue = "moid=27"
	/// Return value: www.MDS.com/index.aspx?aid=5&amp;msg=3&amp;moid=27
	/// </summary>
	/// <param name="url">The Url to which the query String parameter should be added
	/// (e.g. www.mmdsplus.com/index.aspx?aid=5&amp;msg=3).</param>
	/// <param name="queryStringParameterNameValue">The query String parameter and value to add to the Url
	/// (e.g. "moid=27").</param>
	/// <returns>Returns a new Url containing the specified query String parameter.</returns>
	public static String addQueryStringParameter(String url, String queryStringParameterNameValue){
		if (StringUtils.isBlank(queryStringParameterNameValue))
			return url;

		String parmName = queryStringParameterNameValue.substring(0, queryStringParameterNameValue.indexOf("="));

		url = removeQueryStringParameter(url, parmName);

		String rv = url;

		if (url.indexOf("?") < 0){
			rv += "?" + queryStringParameterNameValue;
		}else{
			rv += "&" + queryStringParameterNameValue;
		}
		
		return rv;
	}

	/// <overloads>
	/// Remove a query String parameter from an URL.
	/// </overloads>
	/// <summary>
	/// Remove all query String parameters from the url.
	/// Example:
	/// Url = "www.mmdsplus.com/index.aspx?aid=5&amp;msg=3&amp;moid=27"
	/// Return value: www.mmdsplus.com/index.aspx
	/// </summary>
	/// <param name="url">The Url containing the query String parameters to remove
	/// (e.g. www.mmdsplus.com/index.aspx?aid=5&amp;msg=3&amp;moid=27).</param>
	/// <returns>Returns a new Url with all query String parameters removed.</returns>
	public static String removeQueryStringParameter(String url)	{
		return removeQueryStringParameter(url, StringUtils.EMPTY);
	}

	/// <summary>
	/// Remove the specified query String parameter from the url. Specify <see cref="StringUtils.EMPTY" /> for the
	/// <paramref name="queryStringParameterName" /> parameter to remove the entire set of parameters.
	/// Example:
	/// Url = "www.mmdsplus.com/index.aspx?aid=5&amp;msg=3&amp;moid=27"
	/// QueryStringParameterName = "msg"
	/// Return value: www.mmdsplus.com/index.aspx?aid=5&amp;moid=27
	/// </summary>
	/// <param name="url">The Url containing the query String parameter to remove
	/// (e.g. www.mmdsplus.com/index.aspx?aid=5&amp;msg=3&amp;moid=27).</param>
	/// <param name="queryStringParameterName">The query String parameter name to remove from the Url
	/// (e.g. "msg"). Specify <see cref="StringUtils.EMPTY" /> to remove the entire set of parameters.</param>
	/// <returns>Returns a new Url with the specified query String parameter removed.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="url" /> is null.</exception>
	public static String removeQueryStringParameter(String url, String queryStringParameterName){
		if (url == null)
			throw new ArgumentNullException("url");

		String newUrl;

		// Get the location of the question mark so we can separate the base url from the query String
		int separator = url.indexOf("?");
		if (separator < 0){
			// No query String exists on the url. Simply return the original url.
			newUrl = url;
		}else{
			// We have a query String to remove. Separate the base url from the query String, and process the query String.

			// Get the base url (e.g. "www.mmdsplus.com/index.aspx")
			newUrl = url.substring(0, separator);

			if (StringUtils.isBlank(queryStringParameterName)){
				return newUrl;
			}

			newUrl += "?";

			String queryString = url.substring(separator + 1);
			if (queryString.length() > 0){
				// Url has a query String. Split each name/value pair into a String array, and rebuild the
				// query String, leaving out the parm passed to the function.
				String[] queryItems = StringUtils.split(queryString, '&');

				for (int i = 0; i < queryItems.length; i++)	{
					if (!queryItems[i].startsWith(queryStringParameterName)){
						// Query parm doesn't match, so include it as we rebuilt the new query String
						newUrl += queryItems[i].concat("&");
					}
				}
			}
			// Trim any trailing '&' or '?'.
			newUrl = StringUtils.stripEnd(newUrl,  "&?");
		}

		return newUrl;
	}

	/// <summary>
	/// Returns a value indicating whether the specified query String parameter name is part of the query String. 
	/// </summary>
	/// <param name="parameterName">The name of the query String parameter to check for.</param>
	/// <returns>Returns true if the specified query String parameter value is part of the query String; otherwise 
	/// returns false. </returns>
	public static boolean isQueryStringParameterPresent(HttpServletRequest request, String parameterName)	{
		return (request.getParameter(parameterName) != null);
	}

	/// <summary>
	/// Returns a value indicating whether the specified query String parameter name is part of the query String
	/// of the <paramref name="uri"/>. 
	/// </summary>
	/// <param name="uri">The URI to check for the present of the <paramref name="parameterName">query String parameter name</paramref>.</param>
	/// <param name="parameterName">Name of the query String parameter.</param>
	/// <returns>Returns true if the specified query String parameter value is part of the query String; otherwise 
	/// returns false. </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="uri" /> is null.</exception>
	public static boolean isQueryStringParameterPresent(URI uri, String parameterName)	{
		if (uri == null)
			throw new ArgumentNullException("uri");

		if (StringUtils.isEmpty(parameterName))
			return false;

		return (uri.getQuery().contains("?" + parameterName + "=") || uri.getQuery().contains("&" + parameterName + "="));
	}

	/// <overloads>Remove all HTML tags from the specified String.</overloads>
	/// <summary>
	/// Remove all HTML tags from the specified String.
	/// </summary>
	/// <param name="html">The String containing HTML tags to remove.</param>
	/// <returns>Returns a String with all HTML tags removed.</returns>
	public static String removeHtmlTags(String html){
		return removeHtmlTags(html, false);
	}

	/// <summary>
	/// Remove all HTML tags from the specified String. If <paramref name="escapeQuotes"/> is true, then all 
	/// apostrophes and quotation marks are replaced with &quot; and &apos; so that the String can be specified in HTML 
	/// attributes such as title tags. If the escapeQuotes parameter is not specified, no replacement is performed.
	/// </summary>
	/// <param name="html">The String containing HTML tags to remove.</param>
	/// <param name="escapeQuotes">When true, all apostrophes and quotation marks are replaced with &quot; and &apos;.</param>
	/// <returns>Returns a String with all HTML tags removed.</returns>
	public static String removeHtmlTags(String html, boolean escapeQuotes){
		return Jsoup.parse(html).text(); //HtmlValidator.RemoveHtml(html, escapeQuotes);
	}

	/// <summary>
	/// Removes potentially dangerous HTML and Javascript in <paramref name="html"/>.
	/// When the current user is a gallery or site admin, no validation is performed and the 
	/// <paramref name="html" /> is returned without any processing. If the configuration
	/// setting <see cref="IGallerySettings.AllowUserEnteredHtml" /> is true, then the input is cleaned so that all 
	/// HTML tags that are not in a predefined list are HTML-encoded and invalid HTML attributes are deleted. If 
	/// <see cref="IGallerySettings.AllowUserEnteredHtml" /> is false, then all HTML tags are deleted. If the setting 
	/// <see cref="IGallerySettings.AllowUserEnteredJavascript" /> is true, then script tags and the text "javascript:"
	/// is allowed. Note that if script is not in the list of valid HTML tags defined in <see cref="IGallerySettings.AllowedHtmlTags" />,
	/// it will be deleted even when <see cref="IGallerySettings.AllowUserEnteredJavascript" /> is true. When the setting 
	/// is false, all script tags and instances of the text "javascript:" are deleted.
	/// </summary>
	/// <param name="html">The String containing the HTML tags.</param>
	/// <param name="galleryId">The gallery ID. This is used to look up the appropriate configuration values for the gallery.</param>
	/// <returns>
	/// Returns a String with potentially dangerous HTML tags deleted.
	/// </returns>
	/// <remarks>TODO: Refactor this so that the Clean method knows whether the user is a gallery admin, rendering this
	/// function unnecessary. When this is done, update <see cref="GalleryObject.MetadataRegExEvaluator" /> so that all meta items are
	/// passed to the Clean method.</remarks>
	public static String cleanHtmlTags(String html, long galleryId)	{
		/*if (IsCurrentUserGalleryAdministrator(galleryId))
			return html;
		else
			return HtmlValidator.Clean(html, galleryId);*/
		return html;
	}

	/// <summary>
	/// Returns the current version of MDS System.
	/// </summary>
	/// <returns>Returns a String representing the version (e.g. "1.0.0").</returns>
	public static String getMDSSystemVersion(HttpServletRequest request){
		String appVersion;
		Object version = request.getServletContext().getAttribute("MDSSystemVersion");
		if (version != null){
			// Version was found in Application cache. Return.
			appVersion = version.toString();
		}else{
			// Version was not found in application cache.
			appVersion = MDSDataSchemaVersion.convertMDSDataSchemaVersionToString(HelperFunctions.getMDSSystemVersion());

			request.getServletContext().setAttribute("MDSSystemVersion", appVersion);
		}

		return appVersion;
	}

	/// <summary>
	/// Truncate the specified String to the desired length. Any HTML tags that exist in the beginning portion
	/// of the String are preserved as long as no HTML tags exist in the part that is truncated.
	/// </summary>
	/// <param name="text">The String to be truncated. It may contain HTML tags.</param>
	/// <param name="maxLength">The maximum length of the String to be returned. If HTML tags are returned,
	/// their length is not counted - only the length of the "visible" text is counted.</param>
	/// <returns>Returns a String whose length - not counting HTML tags - does not exceed the specified length.</returns>
	public static String truncateTextForWeb(String text, int maxLength)	{
		// Example 1: Because no HTML tags are present in the truncated portion of the String, the HTML at the
		// beginning is preserved. (We know we won't be splitting up HTML tags, so we don't mind including the HTML.)
		// text = "Meet my <a href='http://www.cnn.com'>friend</a>. He works at the YMCA."
		// maxLength = 20
		// returns: "Meet my <a href='http://www.cnn.com'>friend</a>. He w"
		//
		// Example 2: The truncated portion has <b> tags, so all HTML is stripped. (This function isn't smart
		// enough to know whether it might be truncating in the middle of a tag, so it takes the safe route.)
		// text = "Meet my <a href='http://www.cnn.com'>friend</a>. He works at the <b>YMCA<b>."
		// maxLength = 20
		// returns: "Meet my friend. He w"
		if (text == null)
			return StringUtils.EMPTY;

		if (text.length() < maxLength)
			return text;

		// Remove all HTML tags from entire String.
		String cleanText = removeHtmlTags(text);

		// If the clean text length is less than our maximum, return the raw text.
		if (cleanText.length() <= maxLength)
			return text;

		// Get the text that will be removed.
		String cleanTruncatedPortion = cleanText.substring(maxLength);

		// If the clean truncated text doesn't match the end of the raw text, the raw text must have HTML tags.
		boolean truncatedPortionHasHtml = (!(StringUtils.endsWithIgnoreCase(text, cleanTruncatedPortion)));

		String truncatedText;
		if (truncatedPortionHasHtml){
			// Since the truncated portion has HTML tags, and we don't want to risk returning malformed HTML,
			// return text without ANY HTML.
			truncatedText = cleanText.substring(0, maxLength);
		}else{
			// Since the truncated portion does not have HTML tags, we can safely return the first part of the
			// String, even if it has HTML tags.
			truncatedText = text.substring(0, text.length() - cleanTruncatedPortion.length());
		}
		
		return truncatedText;
	}

	/// <summary>
	/// Generates a pseudo-random 24 character String that can be as an encryption key.
	/// </summary>
	/// <returns>A pseudo-random 24 character String that can be as an encryption key.</returns>
	public static String generateNewEncryptionKey()	{
		final int encryptionKeyLength = 24;
		final int numberOfNonAlphaNumericCharactersInEncryptionKey = 3;
		
		RandomStringGenerator randomStringGenerator =
		        new RandomStringGenerator.Builder()
		                .withinRange('0', '~')
		                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
		                .build();
		String encryptionKey = randomStringGenerator.generate(encryptionKeyLength);
		
		// An ampersand (&) is invalid, since it is used as an escape character in XML files. Replace any instances with an 'X'.
		return encryptionKey.replace("&", "X");
	}
	
	/// <summary>
	/// HtmlEncodes a String using System.Web.HttpUtility.HtmlEncode().
	/// </summary>
	/// <param name="html">The text to HTML encode.</param>
	/// <returns>Returns <paramref name="html"/> as an HTML-encoded String.</returns>
	public static String htmlEncode(String html){
		return StringEscapeUtils.escapeHtml4(html); //.e.HtmlEncode(html);
	}

	/// <summary>
	/// HtmlDecodes a String using System.Web.HttpUtility.HtmlDecode().
	/// </summary>
	/// <param name="html">The text to HTML decode.</param>
	/// <returns>Returns <paramref name="html"/> as an HTML-decoded String.</returns>
	public static String htmlDecode(String html){
		return StringEscapeUtils.unescapeHtml4(html);
	}

	/// <overloads>UrlEncodes a String using System.URI.EscapeDataString().</overloads>
	/// <summary>
	/// UrlEncodes a String using System.URI.EscapeDataString().
	/// </summary>
	/// <param name="text">The text to URL encode.</param>
	/// <returns>Returns <paramref name="text"/> as an URL-encoded String.</returns>
	public static String urlEncode(String text)	{
		if (StringUtils.isBlank(text)){
			return text;
		}

		return URLEncoder.encode(text);
	}

	/// <summary>
	/// Encodes the <paramref name="text" /> so that it can be assigned to a javascript variable.
	/// </summary>
	/// <param name="text">The text to encode.</param>
	/// <returns>Returns <paramref name="text" /> as an encoded String.</returns>
	public static String jsEncode(String text)	{
		if (StringUtils.isBlank(text)){
			return text;
		}

		return text.replace("\r\n", "<br>").replace("\\", "\\\\").replace("'", "\\'").replace("\"\"", "\\\"\\\"");
	}

	/// <summary>
	/// UrlEncodes a String using System.URI.EscapeDataString(), excluding the character specified in <paramref name="charNotToEncode"/>.
	/// This overload is useful for encoding URLs or file paths where the forward or backward slash is not to be encoded.
	/// </summary>
	/// <param name="text">The text to URL encode</param>
	/// <param name="charNotToEncode">The character that, if present in <paramref name="text"/>, is not encoded.</param>
	/// <returns>Returns <paramref name="text"/> as an URL-encoded String.</returns>
	public static String urlEncode(String text, char charNotToEncode){
		if (StringUtils.isBlank(text)){
			return text;
		}

		String[] tokens = StringUtils.split(text, charNotToEncode);
		for (int i = 0; i < tokens.length; i++)	{
			tokens[i] = urlEncode(tokens[i]);
		}

		return String.join(String.valueOf(charNotToEncode), tokens);
	}

	/// <summary>
	/// UrlDecodes a String using System.URI.UnescapeDataString().
	/// </summary>
	/// <param name="text">The text to URL decode.</param>
	/// <returns>Returns text as an URL-decoded String.</returns>
	public static String urlDecode(String text)	{
		if (StringUtils.isBlank(text))
			return text;

		// Pre-process for + sign space formatting since System.Uri doesn't handle it
		// plus literals are encoded as %2b normally so this should be safe.
		text = text.replace("+", " ");
		
		return URLDecoder.decode(text); 
	}

	/// <summary>
	/// Force the current application to recycle by updating the last modified timestamp on web.config.
	/// </summary>
	/// <exception cref="FileNotFoundException">Thrown when the application incorrectly calculates the current application's
	/// web.config file location.</exception>
	/// <exception cref="UnauthorizedAccessException">Thrown when the application does not have write permission to the
	/// current application's web.config file.</exception>
	/// <exception cref="NotSupportedException">Thrown when the path to the web.config file as calculated by the application is
	/// in an invalid format.</exception>
	/*public static void ForceAppRecycle()
	{
		File.SetLastWriteTime(WebConfigFilePath, DateTime.Now);
	}
*/
	/// <summary>
	/// Excecute a maintenance routine to help ensure data integrity and eliminate unused data. The task is run on a background
	/// thread and this method returns immediately. No action is taken when app is in debug mode (debug="true" in web.config).
	/// Roles are synchronized between the membership system and the MDS roles. 
	/// Also, albums with owners that no longer exist are reset to not have an owner. This method is intended to be called 
	/// periodically; for example, once each time the application starts. Code in the Render method of the base class 
	/// <see cref="GalleryPage" /> is responsible for knowing when and how to invoke this method.
	/// </summary>
	/// <remarks>The background thread cannot access HttpContext.Current, so this method will probably fail under DotNetNuke.
	/// To fix that, figure out what DNN needs (portal ID?), and pass it in as a parameter.
	/// so that approach was replaced with this one.</remarks>
	/*public static void PerformMaintenance()	{
		if (!IsDebugEnabled)
			Task.Factory.StartNew(PerformMaintenanceInternal);
	}*/

	/// <summary>
	/// Nulls out the cached value of <see cref="Utils.SkinPath" /> so that it is recalculated the next time the property is accessed.
	/// </summary>
	public static void recalculateSkinPath(){
		skinPath = null;
	}

	/// <summary>
	/// Gets the browser IDs for current request. In many cases this will be equal to request.Browser.Browsers.
	/// However, Internet Explorer versions 1 through 8 include the ID "ie1to8", which is added by MDS System. This allows
	/// the application to treat those versions differently than later versions.
	/// </summary>
	/// <returns>Returns the browser IDs for current request.</returns>
	public static List getBrowserIdsForCurrentRequest(HttpServletRequest request){
		List browserIds = Lists.newArrayList();
		//if (request == null )
		//browserIds = Lists.newArrayList(new String[] { "default" });
		/*for(Browser browser : Browser.values()) {
			browserIds.add(browser.toString());
		}*/

		if (request != null) {
			String  browserDetails  =   request.getHeader("User-Agent");        
			Browser browser = UserAgent.parseUserAgentString(browserDetails).getBrowser();
			if (browser != null) {
				browserIds.add(browser.toString());
			}
			
			if (browserIds.isEmpty() || !browserIds.contains("default")) {
				browserIds.add(0, "default");
			}
			
			addBrowserIdForInternetExplorer(request, browserIds);
	
			addBrowserIdForChromeAndroid(request, browserIds);
		}
		
		return browserIds;
	}

	/// <summary>
	/// Determines whether the <paramref name="url" /> is an absolute URL rather than a relative one. An URL is considered absolute if
	/// it starts with "http" or "//".
	/// </summary>
	/// <param name="url">The URL to check.</param>
	/// <returns>
	/// 	<c>true</c> if the <paramref name="url" /> is absolute; otherwise, <c>false</c>.
	/// </returns>
	public static boolean IsAbsoluteUrl(String url)	{
		if (StringUtils.isEmpty(url))
			return false;

		return (StringUtils.startsWithIgnoreCase(url, "http") || StringUtils.startsWithIgnoreCase(url, "//"));
	}

	/// <summary>
	/// Gets the database file path from the connection String. Applies only to data providers that specify a file path
	/// in the connection String (SQLite, SQL CE). Returns null if no file path is found.
	/// </summary>
	/// <param name="cnString">The cn String.</param>
	/// <returns>Returns the full file path to the database file, or null if no file path is found.</returns>
	/*public static String getDbFilePathFromConnectionString(String cnString)
	{
		// Ex: "data source=|DataDirectory|\MDS_Data.sdf;Password =a@3!7f$dQ;"
		final String dataSourceKeyword = "data source";
		int dataSourceStartPos = cnString.IndexOf(dataSourceKeyword, StringComparison.OrdinalIgnoreCase) + dataSourceKeyword.Length + 1;

		if (dataSourceStartPos < 0)
			return null;

		int dataSourceLength = cnString.IndexOf(";", dataSourceStartPos, StringComparison.Ordinal) - dataSourceKeyword.Length;

		if (dataSourceLength < 0)
			dataSourceLength = cnString.Length - dataSourceStartPos;

		String cnFilePath = cnString.SubString(dataSourceStartPos, dataSourceLength).Replace("|DataDirectory|", "App_Data");

		String filePath = HelperFunctions.IsRelativeFilePath(cnFilePath) ? request.MapPath(cnFilePath) : cnFilePath;

		if (File.Exists(filePath))
			return filePath;
		else
			return null;
	}*/

	/// <summary>
	/// Adds the <paramref name="results" /> to the current user's session. If an object already exists,
	/// the results are added to the existing collection. No action is taken if the session is unavailable. 
	/// The session object is given the name stored in <see cref="GlobalConstants.SkippedFilesDuringUploadSessionKey" />.
	/// </summary>
	/// <param name="results">The results to store in the user's session.</param>
	public static void addResultToSession(HttpServletRequest request, List<ActionResult> results){
		if (request == null || request.getSession(false) == null)
			return;

		String objResults = (String)request.getSession().getAttribute(Constants.SkippedFilesDuringUploadSessionKey);

		List<ActionResult> uploadResults = (objResults == null ? new ArrayList<ActionResult>() : JsonMapper.getInstance().fromJson(objResults, JsonMapper.getInstance().createCollectionType(List.class, ActionResult.class)));

		synchronized (uploadResults)
		{
			uploadResults.addAll(results);
			request.getSession().setAttribute(Constants.SkippedFilesDuringUploadSessionKey, JsonMapper.getInstance().toJson(uploadResults));
		}
	}

	/// <summary>
	/// Generates a list of key/value pairs for the specified <paramref name="enumeration" /> where the key  is the
	/// enumeration value and the value is a friendly, human readable description. The value is assigned from a language resource
	/// value if it exists; otherwise, the String representation of the value is returned. The language resource key must
	/// be in this format: "Enum_{EnumTypeName}_{EnumValue}". For example, the expected resource key for the enum value
	/// JQueryTemplateType.Album is "Enum_JQueryTemplateType_Album".
	/// </summary>
	/// <param name="enumeration">An enumeration from which to generate a collection of key/value pairs.</param>
	/// <returns>Returns an enumerable list of key/value pairs.</returns>
	/*public static Collection<ImmutablePair<String, String>> GetEnumList(Type enumeration)
	{
		Array enumNames = Enum.GetNames(enumeration);
		List<ImmutablePair<String, String>> items = new List<ImmutablePair<String, String>>(enumNames.Length);

		for (String enumName in enumNames)
		{
			String resourceKey = String.Concat("Enum_", enumeration.Name, "_", enumName);

			String resDesc = Resources.MDS.ResourceManager.GetString(resourceKey, CultureInfo.CurrentCulture);

			items.Add(new ImmutablePair<String, String>(enumName, resDesc ?? enumName));
		}

		return items;
	}*/

	///// <summary>
	///// Gets a friendly, human readable description of the enumeration <paramref name="value" />. If a language resource
	///// exists, it is returned; otherwise, the String representation of the value is returned. The language resource key must
	///// be in this format: "Enum_{EnumTypeName}_{EnumValue}". For example, the expected resource key for the enum value
	///// JQueryTemplateType.Album is "Enum_JQueryTemplateType_Album".
	///// </summary>
	///// <param name="value">An enumeration value.</param>
	///// <returns>Returns a friendly, human readable description of the enumeration <paramref name="value" />.</returns>
	//public static String getDescription(this Enum value)
	//{
	//	String resourceKey = String.Concat("Enum_", value.GetType().Name, "_", value.toString());

	//	return Resources.MDS.ResourceManager.GetString(resourceKey, CultureInfo.CurrentCulture) ?? value.toString();
	//}

	/// <summary>
	/// Gets a <see cref="StringContent" /> instance with details about the specified <paramref name="ex" />.
	/// </summary>
	/// <param name="ex">The exception.</param>
	/// <returns>An instance of <see cref="StringContent" />.</returns>
	public static String getExStringContent(Exception ex){
		String msg = "An error occurred on the server. Check the gallery's event log for details. ";

		if (log.isDebugEnabled())	{
			msg += StringUtils.join(ex.getClass(), ": ", ex.getMessage());
		}

		return msg;
	}


	//#endregion

	//#region Private Static Methods

	/// <summary>
	/// Calculates the path, relative to the web site root, to the directory containing the MDS System user 
	/// controls and other resources. Does not include the default page or the trailing slash. Ex: /dev/gsweb/mds
	/// </summary>
	/// <returns>Returns the path to the directory containing the MDS System user controls and other resources.</returns>
	private static String calculateGalleryRoot(HttpServletRequest request){
		String appPath = getAppRoot(request);
		String galleryPath = StringUtils.stripEnd(getGalleryResourcesPathInternal(), File.separator  +  "/");

		if (!StringUtils.isEmpty(galleryPath)){
			galleryPath = galleryPath.replace("\\", "/");

			if (!galleryPath.startsWith("/"))
				galleryPath = StringUtils.join("/", galleryPath); // Make sure it starts with a '/'

			appPath = StringUtils.join(appPath, StringUtils.stripEnd(galleryPath, "/"));
		}

		return appPath;
	}

	/// <summary>
	/// Gets the path, relative to the current application, to the directory containing the MDS System
	/// resources such as images, user controls, scripts, etc. This value is pulled from the AppSettings value "GalleryResourcesPath"
	/// if present; otherwise it defaults to "ds". Examples: "ds", "MDS\resources"
	/// </summary>
	/// <returns>Returns the path, relative to the current application, to the directory containing the MDS System
	/// resources such as images, user controls, scripts, etc.</returns>
	private static String getGalleryResourcesPathInternal()	{
		//return ConfigurationManager.AppSettings["GalleryResourcesPath"] ?? "ds";
		return "static";
	}

	/// <summary>
	/// When the current browser is Internet Explorer 1 to 8, add a "ie1to8" element to <paramref name="browserIds" />.
	/// </summary>
	/// <param name="browserIds">The browser IDs.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="browserIds" /> is null.</exception>
	private static void addBrowserIdForInternetExplorer(HttpServletRequest request, List browserIds){
		if (browserIds == null)
			throw new ArgumentNullException("browserIds");

		String  browserDetails  =   request.getHeader("User-Agent");        
		String  userAgent       =   browserDetails;        
		String  user            =   userAgent.toLowerCase();

		boolean isIe = user.contains("msie");

		if (user.contains("msie"))	{
			final String browserIdForIE1to8 = "ie1to8";
			Browser browser = UserAgent.parseUserAgentString(browserDetails).getBrowser();		
			if ( (Browser.IE5.equals(browser) || Browser.IE6.equals(browser)
					|| Browser.IE7.equals(browser) || Browser.IE8.equals(browser)) && (!browserIds.contains(browserIdForIE1to8))){
				browserIds.add(browserIdForIE1to8);
			}
		}
	}

	/// <summary>
	/// When the current browser is Chrome running on Android, add a "chromeandroid" element to <paramref name="browserIds" />.
	/// </summary>
	/// <param name="browserIds">The browser IDs.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="browserIds" /> is null.</exception>
	private static void addBrowserIdForChromeAndroid(HttpServletRequest request, List browserIds)	{
		if (browserIds == null)
			throw new ArgumentNullException("browserIds");

		String  browserDetails  =   request.getHeader("User-Agent");        
		UserAgent userAgent = UserAgent.parseUserAgentString(browserDetails);

		if ((userAgent.getBrowser() != null) && StringUtils.containsIgnoreCase(userAgent.getBrowser().toString(), "Chrome")){
			final String browserIdForChromeAndroid = "chromeandroid";
			if (userAgent.getOperatingSystem() != null && StringUtils.containsIgnoreCase(userAgent.getOperatingSystem().toString(), "Android"))	{
				browserIds.add(browserIdForChromeAndroid);
			}
		}
	}

	/// <summary>
	/// Gets the path, relative to the current application, to the directory containing the MDS System
	/// resources such as images, user controls, scripts, etc. The value is calculated based on the path to the
	/// MDS.config file specified in web.config. For example, if the config file is at "ds\config\MDS.config",
	/// then the path to the resources is "ds".
	/// </summary>
	/// <returns>Returns the path, relative to the current application, to the directory containing the MDS System
	/// resources such as images, user controls, scripts, etc.</returns>
	/// <remarks>This method assumes that MDS.config is in a directory named "config" and that it is at
	/// the same directory level as the other folders, such as controls, handler, images, pages, script, etc. This
	/// assumption will be valid as long as MDS System is always deployed with the entire contents of the "ds"
	/// directory as a single block.</remarks>
	/*private static String getGalleryPathFromWebConfig()
	{
		String MDSConfigPath = StringUtils.EMPTY;

		// Search web.config for <MDS configSource="..." />
		using (FileStream fs = new FileStream(HttpContext.Current.Server.MapPath("~/web.config"), FileMode.Open, FileAccess.Read, FileShare.Read))
		{
			using (StreamReader sr = new StreamReader(fs))
			{
				XmlReader r = XmlReader.Create(sr);
				while (r.Read())
				{
					if ((r.Name == "MDS") && r.MoveToAttribute("configSource"))
					{
						MDSConfigPath = r.Value; // "ds\config\MDS.config"
						break;
					}
				}
			}
		}

		if (StringUtils.isEmpty(MDSConfigPath))
			throw new WebException("The web.config file for this application does not contain a <MDS ...> configuration element. This is required for MDS System.");

		const String gallerySubPath = @"config\MDS.config";
		if (!MDSConfigPath.EndsWith(gallerySubPath, StringComparison.Ordinal))
			throw new WebException(MessageFormat.format(CultureInfo.CurrentCulture, "The configuration file MDS.config must reside in a directory named config. The path discovered in web.config was {0}.", MDSConfigPath));

		// Remove the "\config\MDS.config" from the path, so we are left with, for example, "ds".
		return MDSConfigPath.Remove(MDSConfigPath.IndexOf(gallerySubPath, StringComparison.Ordinal)).TrimEnd(new char[] { Path.DirectorySeparatorChar });
	}*/

	private static void performMaintenanceInternal()
	{
		/*boolean mustRunMaintenance = false;

		lock (_sharedLock)
		{
			if (AppSettings.getInstance().MaintenanceStatus == MaintenanceStatus.NotStarted)
			{
				mustRunMaintenance = true;
				AppSettings.getInstance().MaintenanceStatus = MaintenanceStatus.InProgress;
			}
		}

		if (mustRunMaintenance)
		{
			try
			{
				AppEventLogController.LogEvent("Maintenance routine has started on a background thread.");

				HelperFunctions.BeginTransaction();

				// Make sure the list of ASP.NET roles is synchronized with the MDS System roles.
				RoleUtils.ValidateRoles();

				ContentConversionQueue.Instance.DeleteOldQueueItems();

				HelperFunctions.CommitTransaction();

				AppSettings.getInstance().MaintenanceStatus = MaintenanceStatus.Complete;

				AppEventLogController.LogEvent("Maintenance routine complete.");
			}
			catch (Exception ex)
			{
				HelperFunctions.RollbackTransaction();
				AppEventLogController.LogError(ex);
				throw;
			}
		}*/
	}

	//#endregion
}
