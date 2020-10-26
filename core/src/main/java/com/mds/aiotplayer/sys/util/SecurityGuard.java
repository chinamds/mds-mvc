/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.SecurityActionsOption;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.core.exception.InvalidEnumArgumentException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.cm.util.CMUtils;

/// <summary>
/// contains security-related functionality.
/// </summary>
public class SecurityGuard{
	//#region Public Static Methods

	/// <summary>
	/// Throws a <see cref="MDS.EventLogs.CustomExceptions.GallerySecurityException" /> if the user belonging to the
	/// specified <paramref name="roles" /> does not have at least one of the requested permissions for the specified album.
	/// </summary>
	/// <param name="securityRequest">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />).
	/// If multiple actions are specified, the method is successful when the user has permission for at least one of the actions.</param>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. This parameter is ignored
	/// for anonymous users (i.e. <paramref name="isAuthenticated" />=false). The parameter may be null.</param>
	/// <param name="albumId">The album for which the requested permission applies.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in
	/// this gallery. This parameter is not required <paramref name="securityRequest" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="Integer.MIN_VALUE" />).</param>
	/// <param name="isAuthenticated">A value indicating whether the current user is logged in. If true, the
	/// <paramref name="roles" /> parameter must be given the names of the roles for the current user. If
	/// <paramref name="isAuthenticated" />=true and the <paramref name="roles" /> parameter
	/// is either null or an empty collection, this method thows a <see cref="MDS.EventLogs.CustomExceptions.GallerySecurityException" /> exception.</param>
	/// <param name="isPrivateAlbum">A value indicating whether the album is hidden from anonymous users. This parameter is ignored for
	/// logged-on users.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is a virtual album.</param>
	/// <remarks>
	/// This method handles both anonymous and logged on users. Note that when <paramref name="isAuthenticated" />=true, the <paramref name="isPrivateAlbum" /> parameter is
	/// ignored. When it is false, the <paramref name="roles" /> parameter is ignored.
	/// </remarks>
	/// <exception cref="EventLogs.CustomExceptions.GallerySecurityException">Thrown when user is not authorized.</exception>
/*	public static void throwIfUserNotAuthorized(SecurityActions[] securityRequest, MDSRoleCollection roles, long albumId, long galleryId, boolean isAuthenticated, boolean isPrivateAlbum, boolean isVirtualAlbum) throws GallerySecurityException, InvalidAlbumException, UnsupportedContentObjectTypeException	{
		throwIfUserNotAuthorized(SecurityActions[] securityRequest, MDSRoleCollection roles, long albumId, long galleryId, boolean isAuthenticated, boolean isPrivateAlbum, boolean isVirtualAlbum)
	}*/
	
	public static void throwIfUserNotAuthorized(SecurityActions securityRequest, MDSRoleCollection roles, long albumId, long galleryId, boolean isAuthenticated, boolean isPrivateAlbum, boolean isVirtualAlbum) throws GallerySecurityException, InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if (!(isUserAuthorized(securityRequest, roles, albumId, galleryId, isAuthenticated, isPrivateAlbum, SecurityActionsOption.RequireOne, isVirtualAlbum)))	{
			throw new GallerySecurityException(MessageFormat.format("You do not have permission '{0}' for album ID {1}.", securityRequest.toString(), albumId));
		}
	}

	/// <overloads>
	/// Determine if a user has permission to perform the requested action.
	/// </overloads>
	/// <summary>
	/// Determine whether the user belonging to the specified <paramref name="roles" /> has permission to perform at least one of the specified security 
	/// actions against the specified <paramref name="albumId" />. The user may be anonymous or logged on.
	/// When the the user is logged on (i.e. <paramref name="isAuthenticated"/> = true), this method determines whether the user is authorized by
	/// validating that at least one role has the requested permission to the specified album. When the user is anonymous,
	/// the <paramref name="roles"/> parameter is ignored and instead the <paramref name="isPrivateAlbum"/> parameter is used.
	/// Anonymous users do not have any access to private albums. When the the user is logged on (i.e. <paramref name="isAuthenticated"/> = true),
	/// the <paramref name="roles"/> parameter must contain the roles belonging to the user.
	/// </summary>
	/// <param name="securityRequests">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />). 
	/// If multiple actions are specified, the method is successful if the user has permission for at least one of the actions. If you require 
	/// that all actions be satisfied to be successful, call one of the overloads that accept a <see cref="SecurityActionsOption" /> and 
	/// specify <see cref="SecurityActionsOption.RequireAll" />.</param>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. This parameter is ignored
	/// 	for anonymous users (i.e. <paramref name="isAuthenticated"/>=false). The parameter may be null.</param>
	/// <param name="albumId">The album for which the requested permission applies. This parameter does not apply when the requested permission
	/// 	is <see cref="SecurityActions.AdministerSite" />.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in this 
	/// gallery. This parameter is not required <paramref name="securityRequests" /> is SecurityActions.AdministerSite (you can specify 
	/// <see cref="Integer.MIN_VALUE" />).</param>
	/// <param name="isAuthenticated">A value indicating whether the current user is logged on. If true, the
	/// 	<paramref name="roles"/> parameter should contain the names of the roles for the current user. If <paramref name="isAuthenticated"/>=true
	/// 	and the <paramref name="roles"/> parameter is either null or an empty collection, this method returns false.</param>
	/// <param name="isPrivateAlbum">A value indicating whether the album is hidden from anonymous users. This parameter is ignored for
	/// 	logged-on users.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is a virtual album.</param>		/// 
	/// <returns>
	/// Returns true if the user has the requested permission; returns false if not.
	/// </returns>
	/// <remarks>This method handles both anonymous and logged on users. Note that when <paramref name="isAuthenticated"/>=true, the
	/// <paramref name="isPrivateAlbum"/> parameter is ignored. When it is false, the <paramref name="roles" /> parameter is ignored.</remarks>
	public static boolean isUserAuthorized(SecurityActions securityRequests, MDSRoleCollection roles, long albumId, long galleryId, boolean isAuthenticated, boolean isPrivateAlbum, boolean isVirtualAlbum) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		return isUserAuthorized(securityRequests, roles, albumId, galleryId, isAuthenticated, isPrivateAlbum, SecurityActionsOption.RequireOne, isVirtualAlbum);
	}

	/// <summary>
	/// Determine whether the user belonging to the specified <paramref name="roles" /> is a site administrator. The user is considered a site
	/// administrator if at least one role has Allow Administer Site permission.
	/// </summary>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. The parameter may be null.</param>
	/// <returns>
	/// 	<c>true</c> if the user is a site administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isUserSiteAdministrator(MDSRoleCollection roles) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		return isUserAuthorized(SecurityActions.AdministerSite, roles, Long.MIN_VALUE, Long.MIN_VALUE, true, false, false);
	}

	/// <summary>
	/// Determine whether the user belonging to the specified <paramref name="roles" /> is a gallery administrator for the specified 
	/// <paramref name="galleryId" />. The user is considered a gallery administrator if at least one role has Allow Administer Gallery permission.
	/// </summary>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. The parameter may be null.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// 	<c>true</c> if the user is a gallery administrator; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isUserGalleryAdministrator(MDSRoleCollection roles, long galleryId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		return isUserAuthorized(SecurityActions.AdministerGallery, roles, Long.MIN_VALUE, galleryId, true, false, false);
	}

	/// <summary>
	/// Determine whether the user belonging to the specified <paramref name="roles" /> is a approval user. The user is considered a content
	/// approval if at least one role has Allow approval content permission.
	/// </summary>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. The parameter may be null.</param>
	/// <returns>
	/// 	<c>true</c> if the user is a approval user; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isUserApprovalContent(MDSRoleCollection roles) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		return isUserAuthorized(SecurityActions.ApproveContentObject, roles, Long.MIN_VALUE, Long.MIN_VALUE, true, false, false);
	}

	/// <summary>
	/// Determine whether the user belonging to the specified <paramref name="roles" /> has permission to perform all of the specified security
	/// actions against the specified <paramref name="albumId" />. The user may be anonymous or logged on.
	/// When the the user is logged on (i.e. <paramref name="isAuthenticated" /> = true), this method determines whether the user is authorized by
	/// validating that at least one role has the requested permission to the specified album. When the user is anonymous,
	/// the <paramref name="roles" /> parameter is ignored and instead the <paramref name="isPrivateAlbum" /> parameter is used.
	/// Anonymous users do not have any access to private albums. When the the user is logged on (i.e. <paramref name="isAuthenticated" /> = true),
	/// the <paramref name="roles" /> parameter must contain the roles belonging to the user.
	/// </summary>
	/// <param name="securityRequests">Represents the permission or permissions being requested. Multiple actions can be specified by using
	/// a bitwise OR between them (example: <see cref="SecurityActions.AdministerSite" /> | <see cref="SecurityActions.AdministerGallery" />).
	/// If multiple actions are specified, use <paramref name="secActionsOption" /> to specify whether all of the actions must be satisfied
	/// to be successful or only one item must be satisfied.</param>
	/// <param name="roles">A collection of MDS System roles to which the currently logged-on user belongs. This parameter is ignored
	/// for anonymous users (i.e. <paramref name="isAuthenticated" />=false). The parameter may be null.</param>
	/// <param name="albumId">The album for which the requested permission applies. This parameter does not apply when the requested permission
	/// is <see cref="SecurityActions.AdministerSite" /> or <see cref="SecurityActions.AdministerGallery" />.</param>
	/// <param name="galleryId">The ID for the gallery the user is requesting permission in. The <paramref name="albumId" /> must exist in this
	/// gallery. This parameter is not required <paramref name="securityRequests" /> is SecurityActions.AdministerSite (you can specify
	/// <see cref="Integer.MIN_VALUE" />).</param>
	/// <param name="isAuthenticated">A value indicating whether the current user is logged on. If true, the
	/// <paramref name="roles" /> parameter should contain the names of the roles for the current user. If <paramref name="isAuthenticated" />=true
	/// and the <paramref name="roles" /> parameter is either null or an empty collection, this method returns false.</param>
	/// <param name="isPrivateAlbum">A value indicating whether the album is hidden from anonymous users. This parameter is ignored for
	/// logged-on users.</param>
	/// <param name="secActionsOption">Specifies whether the user must have permission for all items in <paramref name="securityRequests" />
	/// to be successful or just one. This parameter defaults to SecurityActionsOption.RequireAll when not specified, and is applicable only
	/// when <paramref name="securityRequests" /> contains more than one item.</param>
	/// <param name="isVirtualAlbum">if set to <c>true</c> the album is a virtual album.</param>
	/// <returns>
	/// Returns true if the user has the requested permission; returns false if not.
	/// </returns>
	/// <exception cref="System.ArgumentOutOfRangeException"></exception>
	/// <exception cref="System.ComponentModel.InvalidEnumArgumentException"></exception>
	/// <remarks>
	/// This method handles both anonymous and logged on users. Note that when <paramref name="isAuthenticated" />=true, the
	/// <paramref name="isPrivateAlbum" /> parameter is ignored. When it is false, the <paramref name="roles" /> parameter is ignored.
	/// </remarks>
	public static boolean isUserAuthorized(int securityRequests, MDSRoleCollection roles, long albumId, long galleryId, boolean isAuthenticated, boolean isPrivateAlbum, SecurityActionsOption secActionsOption, boolean isVirtualAlbum) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		//return SecurityGuard.isUserAuthorized(securityActions, roles, albumId, galleryId, isAuthenticated(), isPrivate, secActionsOption, isVirtualAlbum);
		if (isAuthenticated && !isVirtualAlbum && ((roles == null) || (roles.isEmpty())))
			return false;

		boolean userIsRequestingSysAdminPermission = (securityRequests & SecurityActions.AdministerSite.value()) == SecurityActions.AdministerSite.value();
		boolean userIsRequestingApprovalPermission = (securityRequests & SecurityActions.ApproveContentObject.value()) == SecurityActions.ApproveContentObject.value();
		boolean userIsRequestingGalleryAdminPermission = (securityRequests & SecurityActions.AdministerGallery.value()) == SecurityActions.AdministerGallery.value();

		if (galleryId == Long.MIN_VALUE){
			boolean isMoreThanOnePermissionRequest = !SecurityActions.isSingleSecurityAction(securityRequests);
			if (isMoreThanOnePermissionRequest || (!userIsRequestingSysAdminPermission && !userIsRequestingApprovalPermission)){
				throw new ArgumentOutOfRangeException("galleryId", MessageFormat.format("A valid gallery ID must be specified. Instead, the value was {0}.", galleryId));
			}
		}

		//#endregion

		if (isVirtualAlbum && (!userIsRequestingSysAdminPermission && !userIsRequestingGalleryAdminPermission && !userIsRequestingApprovalPermission)){
			return true; // Virtual albums are always allowed, but only for non-admin requests. This feels hacky and non-intuitive; should try to improve someday
		}

		// Handle anonymous users.
		if (!isAuthenticated){
			return isAnonymousUserAuthorized(securityRequests, isPrivateAlbum, galleryId, secActionsOption);
		}

		// If we get here we are dealing with an authenticated (logged on) user. Authorization for authenticated users is
		// given if the user is a member of at least one role that provides permission.
		if (SecurityActions.isSingleSecurityAction(securityRequests)){
			// Iterate through each MDSRole. If at least one allows the action, return true. Note that the
			// AdministerSite security action, if granted, applies to all albums and allows all actions (except HideWatermark).
			for (MDSRole role : roles){
				if (isAuthenticatedUserAuthorized(SecurityActions.getSecurityAction(securityRequests), role, albumId, galleryId))
					return true;
			}
			
			return false;
		}else{
			// There are multiple security actions in securityRequest enum. Iterate through each one and determine if the user
			// has permission for it.
			List<Boolean> authResults = new ArrayList<Boolean>();
			for (SecurityActions securityAction : SecurityActions.parseSecurityAction(securityRequests)){
				// Iterate through each role. If at least one role allows the action, permission is granted.
				for (MDSRole role : roles){
					boolean authResult = isAuthenticatedUserAuthorized(securityAction, role, albumId, galleryId);

					authResults.add(authResult);

					if (authResult)
						break; // We found a role that provides permission, so no need to check the other roles. Just move on to the next security request.
				}
			}

			// Determine the return value based on what the calling method wanted.
			if (secActionsOption == SecurityActionsOption.RequireAll){
				if (authResults.isEmpty())
					return false;
				
				return authResults.stream().allMatch(a->a == true);
			}else if (secActionsOption == SecurityActionsOption.RequireOne){
				return authResults.contains(true);
			}else{
				throw new InvalidEnumArgumentException("secActionsOption", secActionsOption.value(), SecurityActionsOption.class);
			}
		}
	}
	
	public static boolean isUserAuthorized(SecurityActions securityRequests, MDSRoleCollection roles, long albumId, long galleryId, boolean isAuthenticated, boolean isPrivateAlbum, SecurityActionsOption secActionsOption, boolean isVirtualAlbum) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		return SecurityGuard.isUserAuthorized(securityRequests.value(), roles, albumId, galleryId, isAuthenticated, isPrivateAlbum, secActionsOption, isVirtualAlbum);
		//#region Validation

		/*if (isAuthenticated && !isVirtualAlbum && ((roles == null) || (roles.isEmpty())))
			return false;

		boolean userIsRequestingSysAdminPermission = (securityRequests.value() & SecurityActions.AdministerSite.value()) == SecurityActions.AdministerSite.value();
		boolean userIsRequestingApprovalPermission = (securityRequests.value() & SecurityActions.ApproveContentObject.value()) == SecurityActions.ApproveContentObject.value();
		boolean userIsRequestingGalleryAdminPermission = (securityRequests.value() & SecurityActions.AdministerGallery.value()) == SecurityActions.AdministerGallery.value();

		if (galleryId == Long.MIN_VALUE){
			boolean isMoreThanOnePermissionRequest = !SecurityActions.isSingleSecurityAction(securityRequests);
			if (isMoreThanOnePermissionRequest || (!userIsRequestingSysAdminPermission && !userIsRequestingApprovalPermission)){
				throw new ArgumentOutOfRangeException("galleryId", MessageFormat.format("A valid gallery ID must be specified. Instead, the value was {0}.", galleryId));
			}
		}

		//#endregion

		if (isVirtualAlbum && (!userIsRequestingSysAdminPermission && !userIsRequestingGalleryAdminPermission && !userIsRequestingApprovalPermission)){
			return true; // Virtual albums are always allowed, but only for non-admin requests. This feels hacky and non-intuitive; should try to improve someday
		}

		// Handle anonymous users.
		if (!isAuthenticated){
			return isAnonymousUserAuthorized(securityRequests, isPrivateAlbum, galleryId, secActionsOption);
		}

		// If we get here we are dealing with an authenticated (logged on) user. Authorization for authenticated users is
		// given if the user is a member of at least one role that provides permission.
		if (SecurityActions.isSingleSecurityAction(securityRequests)){
			// Iterate through each MDSRole. If at least one allows the action, return true. Note that the
			// AdministerSite security action, if granted, applies to all albums and allows all actions (except HideWatermark).
			for (MDSRole role : roles){
				if (isAuthenticatedUserAuthorized(securityRequests, role, albumId, galleryId))
					return true;
			}
			
			return false;
		}else{
			// There are multiple security actions in securityRequest enum. Iterate through each one and determine if the user
			// has permission for it.
			List<Boolean> authResults = new ArrayList<Boolean>();
			for (SecurityActions securityAction : SecurityActions.parseSecurityAction(securityRequests)){
				// Iterate through each role. If at least one role allows the action, permission is granted.
				for (MDSRole role : roles){
					boolean authResult = isAuthenticatedUserAuthorized(securityAction, role, albumId, galleryId);

					authResults.add(authResult);

					if (authResult)
						break; // We found a role that provides permission, so no need to check the other roles. Just move on to the next security request.
				}
			}

			// Determine the return value based on what the calling method wanted.
			if (secActionsOption == SecurityActionsOption.RequireAll){
				if (authResults.isEmpty())
					return false;
				
				return authResults.stream().allMatch(a->a == true);
			}
			else if (secActionsOption == SecurityActionsOption.RequireOne){
				return authResults.contains(true);
			}
			else{
				throw new InvalidEnumArgumentException("secActionsOption", secActionsOption.value(), SecurityActionsOption.class);
			}
		}*/
	}

	/// <summary>
	/// Gets an object describing whether a user having the specified <paramref name="roles" /> has permission to add albums
	/// and content objects to at least one album in the gallery having ID <paramref name="galleryId" />. Item1 in the return 
	/// value indicates permission to add an album. Item2 represents permission to add a content object. This method works
	/// by iterating through the roles and looking for <see cref="MDSRole.AllowAddContentObject" /> and 
	/// <see cref="MDSRole.AllowAddChildAlbum" /> permission, so it is quite efficient.
	/// </summary>
	/// <param name="roles">The roles a user belongs to.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Tuple{System.BooleanSystem.Boolean}.</returns>
	public static Pair<Boolean, Boolean> getUserAddObjectPermissions(Iterable<MDSRole> roles, long galleryId)	{
		boolean userCanAddAlbumToAtLeastOneAlbum = false;
		boolean userCanAddContentObjectToAtLeastOneAlbum = false;

		GalleryBo gallery = null;
		try {
			gallery = CMUtils.loadGallery(galleryId);
		} catch (InvalidGalleryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (MDSRole role : roles){
			if (role.getAllowAdministerSite()){
				userCanAddContentObjectToAtLeastOneAlbum = true;
				userCanAddAlbumToAtLeastOneAlbum = true;
				break;
			}
			
			if (role.getGalleries().contains(gallery)){
				if (role.getAllowAddContentObject())
					userCanAddContentObjectToAtLeastOneAlbum = true;

				if (role.getAllowAddChildAlbum())
					userCanAddAlbumToAtLeastOneAlbum = true;
			}
		}

		return new ImmutablePair<Boolean, Boolean>(userCanAddAlbumToAtLeastOneAlbum, userCanAddContentObjectToAtLeastOneAlbum);
	}

	//#endregion

	//#region Private Static Methods

	private static boolean isAnonymousUserAuthorized(int securityRequests, boolean isPrivateAlbum, long galleryId, SecurityActionsOption secActionsOption) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// Anonymous user. Return true for viewing-related permission requests on PUBLIC albums; return false for all others.
		GallerySettings gallerySettings = CMUtils.loadGallerySetting(galleryId);

		if (SecurityActions.isSingleSecurityAction(securityRequests)){
			return isAnonymousUserAuthorizedForSingleSecurityAction(SecurityActions.getSecurityAction(securityRequests), isPrivateAlbum, gallerySettings);
		}else{
			return isAnonymousUserAuthorizedForMultipleSecurityActions(securityRequests, isPrivateAlbum, gallerySettings, secActionsOption);
		}
	}

	private static boolean isAnonymousUserAuthorizedForSingleSecurityAction(SecurityActions securityRequests, boolean isPrivateAlbum, GallerySettings gallerySettings){
		return (securityRequests == SecurityActions.ViewAlbumOrContentObject) && !isPrivateAlbum && gallerySettings.getAllowAnonymousBrowsing() ||
			(securityRequests == SecurityActions.ViewOriginalContentObject) && !isPrivateAlbum && gallerySettings.getAllowAnonymousBrowsing() && gallerySettings.getEnableAnonymousOriginalContentObjectDownload();
	}

	private static boolean isAnonymousUserAuthorizedForMultipleSecurityActions(int securityRequests, boolean isPrivateAlbum, GallerySettings gallerySettings, SecurityActionsOption secActionsOption){
		// There are multiple security actions in securityAction enum.  Iterate through each one and determine if the user
		// has permission for it.
		List<Boolean> authResults = new ArrayList<Boolean>();
		for (SecurityActions securityAction : SecurityActions.parseSecurityAction(securityRequests)){
			authResults.add(isAnonymousUserAuthorizedForSingleSecurityAction(securityAction, isPrivateAlbum, gallerySettings));
		}

		if (secActionsOption == SecurityActionsOption.RequireAll){
			if (authResults.isEmpty())
				return false;
			
			return authResults.stream().allMatch(a->a == true);
		}else if (secActionsOption == SecurityActionsOption.RequireOne){
			return authResults.contains(true);
		}else{
			throw new InvalidEnumArgumentException("secActionsOption", secActionsOption.value(), SecurityActionsOption.class);
		}
	}

	private static boolean isAuthenticatedUserAuthorized(SecurityActions securityRequest, MDSRole role, long albumId, long galleryId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if (role.getAllowAdministerSite() && (securityRequest != SecurityActions.HideWatermark)){
			// Administer permissions imply permissions to carry out all other actions, except for hide watermark, which is more of 
			// a preference assigned to the user.
			return true;
		}
		
		if (galleryId > Long.MIN_VALUE) {
			/*if (role.getRoleType() == RoleType.ga && role.getGalleries().stream().anyMatch(g->g.getGalleryId() == galleryId)) {
				return true;
			}
			
			if (role.getRoleType() == RoleType.oa && role.getOrganizationId() > Long.MIN_VALUE && CMUtils.loadGallery(galleryId).getOrganizations().contains(role.getOrganizationId())) {
				return true;
			}*/
			if ((role.getRoleType() == RoleType.ga || role.getRoleType() == RoleType.oa || role.getRoleType() == RoleType.ou) && role.getGalleries().stream().anyMatch(g->g.getGalleryId() == galleryId)) {
				switch (securityRequest){
					case AdministerSite: if (role.getAllowAdministerSite()) return true; break;
					case AdministerGallery: if (role.getAllowAdministerGallery() && (role.getGalleries().findById(galleryId) != null)) return true; break;
					case ViewAlbumOrContentObject: if (role.getAllowViewAlbumOrContentObject()) return true; break;
					case ViewOriginalContentObject: if (role.getAllowViewOriginalImage()) return true; break;
					case AddChildAlbum: if (role.getAllowAddChildAlbum()) return true; break;
					case AddContentObject: if (role.getAllowAddContentObject()) return true; break;
					case DeleteAlbum:
						{
							if (role.getAllowDeleteChildAlbum()){
								return true;
							}
							break;
						}
					case DeleteChildAlbum: if (role.getAllowDeleteChildAlbum()) return true; break;
					case DeleteContentObject: if (role.getAllowDeleteContentObject()) return true; break;
					case ApproveContentObject: if (role.getAllowApproveContentObject()) return true; break; // && role.getAllAlbumIds.contains(albumId)
					case EditAlbum: if (role.getAllowEditAlbum()) return true; break;
					case EditContentObject: if (role.getAllowEditContentObject()) return true; break;
					case HideWatermark: if (role.getHideWatermark()) return true; break;
					case Synchronize: if (role.getAllowSynchronize()) return true; break;
					default: throw new BusinessException(MessageFormat.format("The isUserAuthorized function is not designed to handle the {0} SecurityActions. It must be updated by a developer.", securityRequest.toString()));
				}
				
				return false;
			}
		}

		switch (securityRequest){
			case AdministerSite: if (role.getAllowAdministerSite()) return true; break;
			case AdministerGallery: if (role.getAllowAdministerGallery() && (role.getGalleries().findById(galleryId) != null)) return true; break;
			case ViewAlbumOrContentObject: if (role.getAllowViewAlbumOrContentObject() && role.getAllAlbumIds().contains(albumId)) return true; break;
			case ViewOriginalContentObject: if (role.getAllowViewOriginalImage() && role.getAllAlbumIds().contains(albumId)) return true; break;
			case AddChildAlbum: if (role.getAllowAddChildAlbum() && role.getAllAlbumIds().contains(albumId)) return true; break;
			case AddContentObject: if (role.getAllowAddContentObject() && role.getAllAlbumIds().contains(albumId)) return true; break;
			case DeleteAlbum:
				{
					// It is OK to delete the album if the AllowDeleteChildAlbum permission is true and one of the following is true:
					// 1. The album is the root album and its ID is in the list of targeted albums (Note that we never actually delete the root album.
					//    Instead, we delete all objects within the album. But the idea of deleting the top level album to clear out all objects in the
					//    gallery is useful to the user.)
					// 2. The album is not the root album and its parent album's ID is in the list of targeted albums.
					if (role.getAllowDeleteChildAlbum()){
						AlbumBo album = null;
						try {
							album = CMUtils.loadAlbumInstance(albumId, false);
						} catch (UnsupportedImageTypeException | InvalidContentObjectException | InvalidGalleryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (album.isRootAlbum()){
							if (role.getAllAlbumIds().contains(album.getId())) return true; break;
						}else{
							if (role.getAllAlbumIds().contains(album.getParent().getId())) return true; break;
						}
					}
					break;
				}
			case DeleteChildAlbum: if (role.getAllowDeleteChildAlbum() && role.getAllAlbumIds().contains(albumId)) return true; break;
			case DeleteContentObject: if (role.getAllowDeleteContentObject() && role.getAllAlbumIds().contains(albumId)) return true; break;
			case ApproveContentObject: if (role.getAllowApproveContentObject()) return true; break; // && role.getAllAlbumIds.contains(albumId)
			case EditAlbum: if (role.getAllowEditAlbum() && role.getAllAlbumIds().contains(albumId)) return true; break;
			case EditContentObject: if (role.getAllowEditContentObject() && role.getAllAlbumIds().contains(albumId)) return true; break;
			case HideWatermark: if (role.getHideWatermark() && role.getAllAlbumIds().contains(albumId)) return true; break;
			case Synchronize: if (role.getAllowSynchronize() && role.getAllAlbumIds().contains(albumId)) return true; break;
			default: throw new BusinessException(MessageFormat.format("The isUserAuthorized function is not designed to handle the {0} SecurityActions. It must be updated by a developer.", securityRequest.toString()));
		}
		return false;
	}

	//#endregion
}
