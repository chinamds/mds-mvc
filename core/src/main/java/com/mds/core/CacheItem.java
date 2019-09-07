package com.mds.core;

/// <summary>
/// Defines a list that uniquely identifies cache items stored in the cache.
/// </summary>
public enum CacheItem
{
	/// <summary>
	/// not specified cache for it
	/// </summary>
	notspecified,
	
	/// <summary>
	/// A <see cref="IPlayerCollection"/> containing a list of all permisssion as reported by the permission provider (PermissionDao.getAll()).
	/// </summary>
	sys_permissions,
	
	sys_tenants,
	
	/// <summary>
	/// all areas cache name
	/// </summary>
	sys_areas,
	
	/// <summary>
	/// all organizations cache name
	/// </summary>
	sys_organizations,
	
	sys_dicts,
	
	/// <summary>
	/// all menu and functions cache name
	/// </summary>
	sys_menufunctions,
	sys_menufunctionpermissions,
		
	/// <summary>
	/// A System.Collections.Concurrent.ConcurrentDictionary&lt;<see cref="string" />, <see cref="com.dcm.Interfaces.IMDSRoleCollection" />&gt;
	/// stored in cache. The key is a concatenation of the user's session ID and user name. The corresponding value stores the roles that 
	/// user belongs to. The first item in the dictionary will have a key = "AllRoles", and its dictionary entry holds all 
	/// roles used in the current gallery.
	/// </summary>
	sys_roles,
	/// <summary>
	/// A <see cref="IUserAccountCollection"/> containing a list of all users as reported by the membership provider (Membership.GetAllUsers()).
	/// </summary>
	sys_users,
	/// <summary>
	/// A System.Collections.Concurrent.ConcurrentDictionary&lt;<see cref="string" />, <see cref="com.dcm.Interfaces.IUserAccountCollection" />&gt;
	/// stored in cache. The key is a concatenation of the user's session ID and user name. The corresponding value stores the users that 
	/// the current user has permission to view.
	/// </summary>
	UsersCurrentUserCanView,
	
	/// <summary>
	/// A System.Collections.Concurrent.ConcurrentDictionary&lt;<see cref="string" />, <see cref="MDS.Business.Interfaces.IMDSRoleCollection" />&gt;
	/// stored in cache. The key is a concatenation of the user's session ID and user name. The corresponding value stores the roles that 
	/// user belongs to. The first item in the dictionary will have a key = "AllRoles", and its dictionary entry holds all 
	/// roles used in the current gallery.
	/// </summary>
	MDSRoles,
	/// <summary>
	/// A <see cref="IUserAccountCollection"/> containing a list of all users as reported by the membership provider (Membership.GetAllUsers()).
	/// </summary>
	Users,
			
	/// <summary>
	/// A System.Collections.Concurrent.ConcurrentDictionary&lt;<see cref="int" />, <see cref="com.dcm.Interfaces.IAlbum" />&gt; 
	/// stored in cache. The key specifies the ID of the album stored in the dictionary entry.
	/// </summary>
	sys_gallerySettings,
	cm_galleries,
	cm_albums,
	cm_dailylists,
	cm_contenttypes,

	/// <summary>
	/// A System.Collections.Concurrent.ConcurrentDictionary&lt;<see cref="int" />, <see cref="com.dcm.Interfaces.IGalleryObject" />&gt; 
	/// stored in cache. The key specifies the ID of the content object stored in the dictionary entry.
	/// </summary>
	cm_contentobjects,
	/// <summary>
	/// An <see cref="com.dcm.Interfaces.IEventLogCollection" /> stored in cache.
	/// </summary>
	AppEvents,
	/// <summary>
	/// A System.Collections.Concurrent.ConcurrentDictionary&lt;<see cref="string" />, <see cref="com.dcm.Interfaces.IUserProfile" />&gt; 
	/// stored in cache. The key specifies the username of the profile stored in the dictionary entry.
	/// </summary>
	cm_profiles,
	/// <summary>
	/// An <see cref="com.dcm.Interfaces.IUiTemplateCollection" /> stored in cache.
	/// </summary>
	cm_uitemplates,
	/// <summary>
	/// An <see cref="com.dcm.Interfaces.IContentTemplateCollection" /> stored in cache.
	/// </summary>
	cm_contenttemplates,
	/// <summary>
	/// A System.Collections.Concurrent.ConcurrentDictionary&lt;<see cref="int" />, <see cref="com.dcm.Interfaces.IMimeTypeCollection" />&gt; 
	/// stored in cache. The key specifies the gallery ID of the MIME types stored in the dictionary entry.
	/// </summary>
	cm_mimetypes,
    /// <summary>
	/// A <see cref="IPlayerCollection"/> containing a list of all players as reported by the player provider (Player.GetAllPlayers()).
	/// </summary>
	pm_players,
	
	/// <summary>
	/// all cultures cache name
	/// </summary>
	i18n_cultures,
	
	/// <summary>
	/// all neutral resources cache name
	/// </summary>
	i18n_neutralresources,
	
	/// <summary>
	/// all localized resources cache name
	/// </summary>
	i18n_localizedresources,
}

