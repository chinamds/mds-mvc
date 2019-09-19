package com.mds.sys.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.CollectionUtils;

import com.mds.common.Constants;
import com.mds.cm.content.AlbumBo;
import com.mds.sys.util.MDSRole;
import com.mds.sys.util.MDSRoleCollection;
import com.mds.cm.content.GalleryBo;
import com.mds.cm.content.GalleryBoCollection;
import com.mds.cm.content.nullobjects.NullContentObject;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.model.Album;
import com.mds.cm.model.Gallery;
import com.mds.cm.rest.PermissionsRest;
import com.mds.cm.rest.RoleRest;
import com.mds.cm.rest.TreeView;
import com.mds.cm.rest.TreeViewOptions;
import com.mds.common.mapper.JsonMapper;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.utils.SpringContextHolder;
import com.mds.core.CacheItem;
import com.mds.core.LongCollection;
import com.mds.core.ResourceId;
import com.mds.core.SecurityActions;
import com.mds.core.UserAction;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.sys.model.MenuFunction;
import com.mds.sys.model.MenuFunctionPermission;
import com.mds.sys.model.Organization;
import com.mds.sys.model.Permission;
import com.mds.sys.model.Role;
import com.mds.sys.model.RoleType;
import com.mds.sys.model.User;
import com.mds.sys.service.RoleManager;
import com.mds.sys.util.AppSettings;
import com.mds.cm.util.AlbumTreeViewBuilder;
import com.mds.cm.util.AlbumUtils;
import com.mds.cm.util.AppEventLogUtils;
import com.mds.cm.util.CMUtils;
import com.mds.cm.util.ContentObjectUtils;
import com.mds.cm.util.GalleryUtils;
import com.mds.util.CacheUtils;
import com.mds.util.Collections3;
import com.mds.util.HelperFunctions;
import com.mds.i18n.util.I18nUtils;
import com.mds.util.StringUtils;
import com.mds.sys.util.UserUtils;

/// <summary>
/// Contains functionality for managing roles.
/// </summary>
public final class RoleUtils{
	//#region Private Fields

	private static final Object _sharedLock = new Object();
	//private static RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
	
	public static final String CACHE_ROLE_LIST = "roleList";

	// RegEx pattern to match "_{PortalId}" portion of MDS role name. Not used in stand-alone version of MDS.
	//private static readonly System.Text.RegularExpressions.Regex _mdsRoleNameSuffixRegEx = new System.Text.RegularExpressions.Regex(@"_\d+$", System.Text.RegularExpressions.RegexOptions.Compiled);

	// RegEx pattern to match the album owner role template name. The gallery ID is assigned the group name "galleryId".
	// Ex: Given "_Album Owner Template (Gallery ID 723: My gallery)", match will be a success and group name "galleryId" will contain "723"
	private static final String _mdsAlbumOwnerTemplateRoleNameRegExPattern = StringUtils.join(Constants.AlbumOwnerRoleTemplateName, " \\(Gallery ID (?<galleryId>\\d+): .*\\)$");
	private static final Pattern _mdsAlbumOwnerTemplateRoleNameRegEx = Pattern.compile(_mdsAlbumOwnerTemplateRoleNameRegExPattern);

	//#endregion

	//#region Public Methods

	public static List<Role> getUserCanManagerRoles(){
		@SuppressWarnings("unchecked")
		List<Role> list = (List<Role>)UserUtils.getCache(CACHE_ROLE_LIST);
		if (list == null){
			RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
			
			UserAccount user = UserUtils.getUser();
			if (user.isSystem()) {
				list = roleManager.getRoles();
			}else {
				long organizationId = user.getOrganizationId();
				if (organizationId == Long.MIN_VALUE) {
					
				}else {
					Searchable searchable = Searchable.newSearchable();
					searchable.addSearchFilter("organization.id", SearchOperator.in, UserUtils.getOrganizationChildren(organizationId));
			        searchable.addSort(Direction.ASC, "name");
			        list = roleManager.findAll(searchable);
				}
			}
			UserUtils.putCache(CACHE_ROLE_LIST, list);
		}
		return list;
	}
	
	public static List<Role> getRoles(){
		/*@SuppressWarnings("unchecked")
		List<Role> roles = (List<Role>)CacheUtils.get(CACHE_ROLE_LIST);
		if (roles == null){
			roles = roleDao.getAll();
			CacheUtils.put(CACHE_ROLE_LIST, roles);
		}
		
		return roles;*/
		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		return roleManager.getRoles();
	}
		
	/// <summary>
	/// Verify there is a site admin role/album mapping for the root album in the current gallery, creating one
	/// if necessary.
	/// </summary>
	/// <param name="albumId">The album ID of the root album in the current gallery.</param>
	public static void configureRoleAlbumTable(long albumId){
		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		List<Role> roles = roleManager.findSARoleNotOwnerAlbum(albumId);
		Album album = CMUtils.getAlbum(albumId);
		for (Role role : roles) {
			role.getAlbums().add(album);
		}
		roleManager.save(roles);
	}
	
	public static MDSRoleCollection getRolesFromRoleDtos(List<Role> roleDtos){
		MDSRoleCollection roles = new MDSRoleCollection();
		//List roleIds = Collections3.extractToList(roleDtos, "id");
		//List<MenuFunctionPermission> menuFunctionPermissionForRoles = UserUtils.getMenuFunctionPermissions(roleIds);
		GalleryBoCollection galleries = CMUtils.loadGalleries();
		for (Role roleDto : roleDtos){
			List<MenuFunctionPermission> menuFunctionPermissions = roleDto.getMenuFunctionPermissions();
			if (roleDto.getType() == RoleType.sa && (menuFunctionPermissions == null || menuFunctionPermissions.isEmpty())) {
				menuFunctionPermissions = UserUtils.getMenuFunctionPermissions();
			}
			
			MDSRole role = new MDSRole(
					roleDto.getId(),
					roleDto.getName(),
					roleDto.getType(),
					Long.MIN_VALUE,
					isPermitted(menuFunctionPermissions, ResourceId.cm_galleryview, UserAction.view) || isPermitted(roleDto, ResourceId.cm_albums, UserAction.view),
					isPermitted(menuFunctionPermissions, ResourceId.cm_galleryview, UserAction.vieworiginalmedia),
					isPermitted(menuFunctionPermissions, ResourceId.cm_galleryview, UserAction.add),
					isPermitted(menuFunctionPermissions, ResourceId.cm_albums, UserAction.addchild),
					isPermitted(menuFunctionPermissions, ResourceId.cm_galleryview, UserAction.edit),
					isPermitted(menuFunctionPermissions, ResourceId.cm_albums, UserAction.edit),
					isPermitted(menuFunctionPermissions, ResourceId.cm_galleryview, UserAction.delete),
					isPermitted(menuFunctionPermissions, ResourceId.cm_galleryview, UserAction.approve),
					isPermitted(menuFunctionPermissions, ResourceId.cm_albums, UserAction.removechild),
					isPermitted(menuFunctionPermissions, ResourceId.cm_galleryview, UserAction.synchronize),
					roleDto.getType() == RoleType.sa, //roleDto.isPermitted(ResourceId.sys_sitesettings, UserAction.edit),
					roleDto.getType() == RoleType.oa || roleDto.getType() == RoleType.ga,//roleDto.isPermitted(ResourceId.cm_galleries, UserAction.edit),
					isPermitted(menuFunctionPermissions, ResourceId.cm_galleryview, UserAction.remove));
			
			for (long organizationId : roleDto.getOrganizationIds()){
				List<GalleryBo> items = galleries.stream().filter(g->g.getOrganizations().contains(organizationId)).collect(Collectors.toList());
				for (GalleryBo item : items){
					if (item != null && !role.getGalleries().contains(item)) {
						role.getGalleries().add(item);
					}
				}
			}
						
			for (Gallery gallery : roleDto.getGalleries()){
				GalleryBo item = galleries.stream().filter(g->g.getGalleryId() == gallery.getId()).findFirst().orElse(null);
				if (item != null && !role.getGalleries().contains(item)) {
					role.getGalleries().add(item);
				}
			}
			
			role.setOrganizationIds(new LongCollection(roleDto.getOrganizationIds()));
			if (roleDto.getType() == RoleType.oa && roleDto.getOrganizationIds().isEmpty()) {
				List<Long> organizationIds = UserUtils.getOrganizationChildren(roleDto.getOrganization().getId());
				for (long organizationId : organizationIds){
					List<GalleryBo> items = galleries.stream().filter(g->g.getOrganizations().contains(organizationId)).collect(Collectors.toList());
					for (GalleryBo item : items){
						if (item != null && !role.getGalleries().contains(item)) {
							role.getGalleries().add(item);
						}
					}
				}
				role.setOrganizationIds(new LongCollection(organizationIds));
			}
			
			/*if (roleDto.getType() == RoleType.oa) {
				List<GalleryBo> items = galleries.stream().filter(g->g.getOrganizations().contains(roleDto.getOrganization().getId())).collect(Collectors.toList());
				for (GalleryBo item : items){
					if (item != null && !role.getGalleries().contains(item)) {
						role.getGalleries().add(item);
					}
				}
			}*/
			
			role.getRootAlbumIds().addRange(roleDto.getAlbums().stream().map(a->a.getId()).collect(Collectors.toList()));
			if (RoleType.getUpwardRoleTypes(RoleType.gu).contains(roleDto.getType()) && roleDto.getAlbums().isEmpty()) {
				for (GalleryBo gallery : role.getGalleries()){
				  if (gallery.getRootAlbumId() != null && gallery.getRootAlbumId() != Long.MIN_VALUE && !role.getRootAlbumIds().contains(gallery.getRootAlbumId()))
					  role.getRootAlbumIds().add(gallery.getRootAlbumId());
				}
			}			

			if (roleDto.getOrganization() != null) {
				role.setOrganizationId(roleDto.getOrganization().getId());
			}
			role.addMenuPermissions(menuFunctionPermissions);

			roles.add(role);
		}

		return roles;
	}
	
	private static boolean isPermitted(List<MenuFunctionPermission> menuFunctionPermissions, ResourceId resourceId, UserAction userAction) {
		long permissionId = UserUtils.getPermissions().stream().filter(p->p.contains(userAction)).map(p->p.getId()).findFirst().orElse(Long.MIN_VALUE);
		long menuFunctionId = UserUtils.getMenuFunctionId(resourceId);
		
		return menuFunctionPermissions.stream().anyMatch(mp->mp.getMenuFunction().getId() == menuFunctionId && mp.getPermission().getId() == permissionId);
	}
	
	public static boolean isPermitted(Role role, ResourceId resourceId, UserAction userAction) {
		List<MenuFunctionPermission> menuFunctionPermissions = role.getMenuFunctionPermissions();
		if (role.getType() == RoleType.sa && (menuFunctionPermissions == null || menuFunctionPermissions.isEmpty())) {
			menuFunctionPermissions = UserUtils.getMenuFunctionPermissions();
		}
				
		return isPermitted(menuFunctionPermissions, resourceId, userAction);
	}

	/// <summary>
	/// Get all MDS System roles for the current gallery. Guaranteed to not return null.
	/// </summary>
	/// <returns>Returns all MDS System roles for the current gallery.</returns>
	public static MDSRoleCollection getMDSRolesFromDataStore()	{
		// Create the roles.
		MDSRoleCollection roles;
		//MDSRoleCollection roles = GetRolesFromRoleDtos(GetDataProvider().Roles_GetRoles());
		roles = getRolesFromRoleDtos(RoleUtils.getRoles());

		GalleryBoCollection galleries = CMUtils.loadGalleries();
		for (MDSRole role : roles){
			role.inflate(galleries);
		}

		roles.sort();

		return roles;
	}
	

	//#region Security Methods

	/// <summary>
	/// Create a MDS System role corresponding to the specified parameters. Throws an exception if a role with the
	/// specified name already exists in the data store. The role is not persisted to the data store until the
	/// <see cref="MDSRole.Save"/> method is called.
	/// </summary>
	/// <param name="oId">A Id that uniquely identifies the role.</param>
	/// <param name="roleName">A String that uniquely identifies the role in an organization.</param>
	/// <param name="oId">A Id that uniquely identifies the role owner organization.</param>
	/// <param name="allowViewAlbumOrContentObject">A value indicating whether the user assigned to this role has permission to view albums
	/// and content objects.</param>
	/// <param name="allowViewOriginalImage">A value indicating whether the user assigned to this role has permission to view the original,
	/// high resolution version of an image. This setting applies only to images. It has no effect if there are no
	/// high resolution images in the album or albums to which this role applies.</param>
	/// <param name="allowAddContentObject">A value indicating whether the user assigned to this role has permission to add content objects to an album.</param>
	/// <param name="allowAddChildAlbum">A value indicating whether the user assigned to this role has permission to create child albums.</param>
	/// <param name="allowEditContentObject">A value indicating whether the user assigned to this role has permission to edit a content object.</param>
	/// <param name="allowEditAlbum">A value indicating whether the user assigned to this role has permission to edit an album.</param>
	/// <param name="allowDeleteContentObject">A value indicating whether the user assigned to this role has permission to delete content objects within an album.</param>
	/// <param name="allowDeleteChildAlbum">A value indicating whether the user assigned to this role has permission to delete child albums.</param>
	/// <param name="allowSynchronize">A value indicating whether the user assigned to this role has permission to synchronize an album.</param>
	/// <param name="allowAdministerSite">A value indicating whether the user has administrative permission for all albums. This permission
	/// automatically applies to all albums across all galleries; it cannot be selectively applied.</param>
	/// <param name="allowAdministerGalleryBo">A value indicating whether the user has administrative permission for all albums. This permission
	/// automatically applies to all albums in a particular gallery; it cannot be selectively applied.</param>
	/// <param name="hideWatermark">A value indicating whether the user assigned to this role has a watermark applied to images.
	/// This setting has no effect if watermarks are not used. A true value means the user does not see the watermark;
	/// a false value means the watermark is applied.</param>
	/// <returns>
	/// Returns an <see cref="MDSRole"/> object corresponding to the specified parameters.
	/// </returns>
	/// <exception cref="InvalidMDSRoleException">Thrown when a role with the specified role name already exists in the data store.</exception>
	public static MDSRole createMDSRoleInstance(long roleId, String roleName, RoleType roleType, long oId, boolean allowViewAlbumOrContentObject,
																boolean allowViewOriginalImage, boolean allowAddContentObject,
																boolean allowAddChildAlbum, boolean allowEditContentObject,
																boolean allowEditAlbum, boolean allowDeleteContentObject,
																boolean allowApproveContentObject, boolean allowDeleteChildAlbum, 
																boolean allowSynchronize, boolean allowAdministerSite, 
																boolean allowAdministerGalleryBo, boolean hideWatermark) throws InvalidMDSRoleException{
		if (loadMDSRole(roleName, oId) != null){
			throw new InvalidMDSRoleException(I18nUtils.getMessage("roleutils.createMDSRoleInstance_Ex_Msg"));
		}

		return new MDSRole(roleId, roleName, roleType, oId, allowViewAlbumOrContentObject, allowViewOriginalImage, allowAddContentObject,
																 allowAddChildAlbum, allowEditContentObject, allowEditAlbum, allowDeleteContentObject,
																 allowApproveContentObject, allowDeleteChildAlbum, allowSynchronize, allowAdministerSite,
																 allowAdministerGalleryBo, hideWatermark);
	}

	/// <overloads>Retrieve a collection of MDS System roles.</overloads>
	/// <summary>
	/// Retrieve a collection of all MDS System roles. The roles may be returned from a cache. Guaranteed to not return null.
	/// </summary>
	/// <returns>Returns an <see cref="MDSRoleCollection" /> object that contains all MDS System roles.</returns>
	/// <remarks>
	/// The collection of all MDS System roles are stored in a cache to improve
	/// performance. <note type = "implementnotes">Note to developer: Any code that modifies the roles in the data store should purge the cache so 
	///              	that they can be freshly retrieved from the data store during the next request. The cache is identified by the
	///              	<see cref="CacheItem.MDSRoles" /> enum.</note>
	/// </remarks>
	public static MDSRoleCollection loadMDSRoles(){
		ConcurrentHashMap<String, MDSRoleCollection> rolesCache = (ConcurrentHashMap<String, MDSRoleCollection>)CacheUtils.get(CacheItem.MDSRoles);

		MDSRoleCollection roles;

		if ((rolesCache != null) && ((roles = rolesCache.get(Constants.MDSRoleAllRolesCacheKey)) != null)){
			return roles;
		}

		// No roles in the cache, so get from data store and add to cache.
		roles = getMDSRolesFromDataStore();

		roles.sort();

		roles.validateIntegrity();

		rolesCache = new ConcurrentHashMap<String, MDSRoleCollection>();
		rolesCache.put(Constants.MDSRoleAllRolesCacheKey, roles);
		CacheUtils.put(CacheItem.MDSRoles, rolesCache);

		return roles;
	}

	/// <summary>
	/// Retrieve a collection of MDS System roles that match the specified <paramref name = "roleNames" />. 
	/// It is not case sensitive, so that "ReadAll" matches "readall". The roles may be returned from a cache.
	///  Guaranteed to not return null.
	/// </summary>
	/// <param name="roleNames">The name of the roles to return.</param>
	/// <returns>
	/// Returns an <see cref="MDSRoleCollection" /> object that contains all MDS System roles that
	/// match the specified role names.
	/// </returns>
	/// <remarks>
	/// The collection of all MDS System roles for the current gallery are stored in a cache to improve
	/// performance. <note type = "implementnotes">Note to developer: Any code that modifies the roles in the data store should purge the cache so 
	///              	that they can be freshly retrieved from the data store during the next request. The cache is identified by the
	///              	<see cref="CacheItem.MDSRoles" /> enum.</note>
	/// </remarks>
	public static MDSRoleCollection loadMDSRoles(Collection<String> roleNames, long oId) throws InvalidMDSRoleException{
		return loadMDSRoles().getRoles(roleNames.toArray(new String[0]), oId);
	}
	
	public static MDSRoleCollection loadMDSRoles(Collection<Long> roleIds) throws InvalidMDSRoleException{
		return loadMDSRoles().getRoles(ArrayUtils.toPrimitive(roleIds.toArray(new Long[0])));
	}

	/// <overloads>
	/// Retrieve the MDS System role that matches the specified role name. The role may be returned from a cache.
	/// Returns null if no matching role is found.
	/// </overloads>
	/// <summary>
	/// Retrieve the MDS System role that matches the specified role name. The role may be returned from a cache.
	/// Returns null if no matching role is found.
	/// </summary>
	/// <param name="roleName">The name of the role to return.</param>
	/// <returns>
	/// Returns an <see cref="MDSRole" /> object that matches the specified role name, or null if no matching role is found.
	/// </returns>
	public static MDSRole loadMDSRole(String roleName, long oId) throws InvalidMDSRoleException {
		return loadMDSRole(roleName, oId, false);
	}
	
	public static MDSRole loadMDSRole(long roleId) throws InvalidMDSRoleException	{
		return loadMDSRole(roleId, false);
	}

	/// <summary>
	/// Retrieve the MDS System role that matches the specified role name. When <paramref name="isWritable"/>
	/// is <c>true</c>, then return a unique instance that is not shared across threads, thus creating a thread-safe object that can
	/// be updated and persisted back to the data store. Calling this method with <paramref name="isWritable"/> set to <c>false</c>
	/// is the same as calling the overload of this method that takes only a role name. Returns null if no matching role is found.
	/// </summary>
	/// <param name="roleName">The name of the role to return.</param>
	/// <param name="isWritable">When set to <c>true</c> then return a unique instance that is not shared across threads.</param>
	/// <returns>
	/// Returns a writeable instance of <see cref="MDSRole"/> that matches the specified role name, or null if no matching role is found.
	/// </returns>
	public static MDSRole loadMDSRole(String roleName, long oId, boolean isWritable) throws InvalidMDSRoleException	{
		MDSRole role = loadMDSRoles().getRole(roleName, oId);
		
		if ((role == null) || (!isWritable)){
			return role;
		}else{
			return role.copy();
		}
	}
	
	public static MDSRole loadMDSRole(long roleId, boolean isWritable) throws InvalidMDSRoleException	{
		MDSRole role = loadMDSRoles().getRole(roleId);
		
		if ((role == null) || (!isWritable)){
			return role;
		}else{
			return role.copy();
		}
	}

	//#endregion
	
	/// <overloads>
	/// Persist the role to the data store. Prior to saving, validation is performed and a 
	/// <see cref="GallerySecurityException" /> is thrown if a business rule is violated.
	/// </overloads>
	/// <summary>
	/// Persist the <paramref name="role" /> to the data store. Prior to saving, validation is performed and a 
	/// <see cref="GallerySecurityException" /> is thrown if a business rule is violated.
	/// </summary>
	/// <param name="role">The role to save.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="role" /> is null.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be saved because doing so would violate a business rule.</exception>
	/// <exception cref="InvalidMDSRoleException">Thrown when an existing role cannot be found in the database that matches the 
	/// role name of the <paramref name="role" /> parameter, or one is found and the role to save is specifed as new.</exception>
	public static void save(RoleRest role) throws InvalidMDSRoleException, GallerySecurityException, UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, WebException	{
		if (role == null)
			throw new ArgumentNullException("role");

		MDSRole r = loadMDSRole(role.RoleId, true);

		if (r == null && !role.IsNew)
			throw new InvalidMDSRoleException(MessageFormat.format("A role with the name '{0}' does not exist.", role.RoleId));

		if (role.IsNew)	{
			if (r != null)
				throw new InvalidMDSRoleException(I18nUtils.getMessage("Admin_Manage_Roles_Cannot_Create_Role_Already_Exists_Msg"));

			PermissionsRest p = role.Permissions;
			RoleUtils.createRole(role.RoleId, role.Name, RoleType.getRoleType(role.RoleType), p.ViewAlbumOrContentObject, p.ViewOriginalContentObject, p.AddContentObject, p.AddChildAlbum,
				p.EditContentObject, p.EditAlbum, p.DeleteContentObject, p.ApproveContentObject, p.DeleteChildAlbum, p.Synchronize, p.AdministerSite,
				p.AdministerGallery, p.HideWatermark, new LongCollection(role.SelectedRootAlbumIds));
		}else{
			r.setAllowAddChildAlbum(role.Permissions.AddChildAlbum);
			r.setAllowAddContentObject(role.Permissions.AddContentObject);
			r.setAllowAdministerSite(role.Permissions.AdministerSite);
			r.setAllowAdministerGallery(role.Permissions.AdministerGallery);
			r.setAllowDeleteChildAlbum(role.Permissions.DeleteChildAlbum);
			r.setAllowDeleteContentObject(role.Permissions.DeleteContentObject);
			r.setAllowApproveContentObject(role.Permissions.ApproveContentObject);
			r.setAllowEditAlbum(role.Permissions.EditAlbum);
			r.setAllowEditContentObject(role.Permissions.EditContentObject);
			r.setAllowSynchronize(role.Permissions.Synchronize);
			r.setAllowViewOriginalImage(role.Permissions.ViewOriginalContentObject);
			r.setAllowViewAlbumOrContentObject(role.Permissions.ViewAlbumOrContentObject);
			r.setHideWatermark(role.Permissions.HideWatermark);

			RoleUtils.save(r, new LongCollection(role.SelectedRootAlbumIds));
		}
	}

	/// <summary>
	/// Persist the <paramref name="roleToSave" /> to the data store, associating any album IDs listed in <paramref name="topLevelCheckedAlbumIds" />
	/// with it. Prior to saving, validation is performed and a <see cref="GallerySecurityException" /> is thrown if a business rule
	/// is violated.
	/// </summary>
	/// <param name="roleToSave">The role to save.</param>
	/// <param name="topLevelCheckedAlbumIds">The top level album IDs. May be null.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="roleToSave" /> is null.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be saved because doing so would violate a business rule.</exception>
	/// <exception cref="InvalidMDSRoleException">Thrown when an existing role cannot be found in the database that matches the 
	/// role name of the <paramref name="roleToSave" /> parameter.</exception>
	public static void save(MDSRole roleToSave, LongCollection topLevelCheckedAlbumIds) throws GallerySecurityException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, WebException{
		if (roleToSave == null)
			throw new ArgumentNullException("roleToSave");

		validateSaveRole(roleToSave);

		updateRoleAlbumRelationships(roleToSave, topLevelCheckedAlbumIds);

		roleToSave.save();
	}

	/// <summary>
	/// Add the specified user to the specified role.
	/// </summary>
	/// <param name="userName">The user name to add to the specified role.</param>
	/// <param name="roleName">The role to add the specified user name to.</param>
	public static void addUserToRole(String userName, String roleName){
		if (!StringUtils.isBlank(userName) && !StringUtils.isBlank(roleName)){
			addUserToRoles(userName, new String[] { roleName.trim() });
		}
	}

	/// <summary>
	/// Add the specified user to the specified roles.
	/// </summary>
	/// <param name="userName">The user name to add to the specified role.</param>
	/// <param name="roleNames">The roles to add the specified user name to.</param>
	public static void addUserToRoles(String userName, String[] roleNames){
		if (!StringUtils.isBlank(userName) && (roleNames != null) && (roleNames.length > 0)){
			//RoleMds.AddUsersToRoles(new String[] { userName.Trim() }, roleNames);
		}
	}

	/// <summary>
	/// Removes the specified user from the specified role.
	/// </summary>
	/// <param name="userName">The user to remove from the specified role.</param>
	/// <param name="roleName">The role to remove the specified user from.</param>
	public static void removeUserFromRole(String userName, String roleName) throws InvalidMDSRoleException, InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidGalleryException, WebException	{
		if (!StringUtils.isBlank(userName) && !StringUtils.isBlank(roleName)){
			removeUserFromRoles(userName, new String[] { roleName.trim() });
		}
	}

	/// <summary>
	/// Removes the specified user from the specified roles.
	/// </summary>
	/// <param name="userName">The user to remove from the specified role.</param>
	/// <param name="roleNames">The roles to remove the specified user from.</param>
	public static void removeUserFromRoles(String userName, String[] roleNames) throws InvalidMDSRoleException, InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidGalleryException, WebException{
		if (!StringUtils.isBlank(userName) && (roleNames != null) && (roleNames.length > 0)){
			//RoleMds.removeUsersFromRoles(new String[] { userName.Trim() }, roleNames);
		}

		validateRemoveUserFromRole(userName, Arrays.asList(roleNames), Long.MIN_VALUE);
	}

	/// <summary>
	/// Gets a role entity corresponding to <paramref name="roleName" />. If the role does not exist, an instance with 
	/// a set of default values is returned that can be used to create a new role. The instance can be serialized to JSON and
	/// subsequently used in the browser as a data object. A <see cref="GallerySecurityException" /> is thrown if the current
	/// user doesn't have permission to view the role.
	/// </summary>
	/// <param name="roleName">Name of the role.</param>
	/// <returns>Returns an <see cref="RoleRest" /> instance.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the current user does not have permission to view the role.</exception>
	public static RoleRest getRoleEntity(long roleId) throws InvalidGalleryException, UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		MDSRole role = loadMDSRole(roleId, true);

		// Throw exception if user can't view role. Note that MDS doesn't differentiate between permission to view and permission to
		// edit, so we use the UserCanEditRole function, even though we are just getting a role, not editing it.
		if (role != null && !userCanViewRole(role))
			throw new GallerySecurityException("Insufficient permission to view role.");

		RoleRest r = new RoleRest();
		PermissionsRest p = new PermissionsRest();

		if (role != null){
			r.RoleId = role.getRoleId();
			r.organizationId = role.getOrganizationId();
			r.Name = role.getRoleName();
			r.IsNew = false;
			r.IsOwner = (isRoleAnAlbumOwnerRole(r.Name) || isRoleAnAlbumOwnerTemplateRole(r.Name));
			p.ViewAlbumOrContentObject = role.getAllowViewAlbumOrContentObject();
			p.ViewOriginalContentObject = role.getAllowViewOriginalImage();
			p.AddChildAlbum = role.getAllowAddChildAlbum();
			p.AddContentObject = role.getAllowAddContentObject();
			p.EditAlbum = role.getAllowEditAlbum();
			p.EditContentObject = role.getAllowEditContentObject();
			p.DeleteAlbum = false; // This permission exists only in the context of a particular album and not as a stand-alone permission
			p.DeleteChildAlbum = role.getAllowDeleteChildAlbum();
			p.DeleteContentObject = role.getAllowDeleteContentObject();
			p.ApproveContentObject = role.getAllowApproveContentObject();
			p.Synchronize = role.getAllowSynchronize();
			p.AdministerGallery = role.getAllowAdministerGallery();
			p.AdministerSite = role.getAllowAdministerSite();
			p.HideWatermark = role.getHideWatermark();
		}else{
			r.IsNew = true;
		}

		r.Permissions = p;
		LongCollection rootAlbumIds = (role != null ? role.getRootAlbumIds() : new LongCollection());

		TreeViewOptions tvOptions = new TreeViewOptions();
		tvOptions.EnableCheckboxPlugin = true;
		tvOptions.RequiredSecurityPermissions = new SecurityActions[] {SecurityActions.AdministerSite , SecurityActions.AdministerGallery};
		tvOptions.Galleries = CMUtils.loadGalleries();
		tvOptions.RootAlbumPrefix = StringUtils.join(I18nUtils.getMessage("site.Gallery_Text"), " '{GalleryDescription}': ");
		tvOptions.SelectedAlbumIds = rootAlbumIds;

		TreeView tv = AlbumTreeViewBuilder.getAlbumsAsTreeView(tvOptions);

		r.AlbumTreeDataJson = JsonMapper.getInstance().toJson(tv);
		r.SelectedRootAlbumIds = ArrayUtils.toPrimitive(rootAlbumIds.toArray());

		r.Members = RoleUtils.getUsersInRole(r.RoleId);

		return r;
	}

	/// <summary>
	/// Gets a list of all the ASP.NET roles for the current application.
	/// </summary>
	/// <returns>A list of all the ASP.NET roles for the current application.</returns>
	public static String[] getAllRoles(){
		//return RoleMds.GetAllRoles();
		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		return roleManager.getAll().stream().map(r->r.getName()).toArray(String[]::new);
	}
	
	public static Long[] getAllRoleIds(){
		//return RoleMds.GetAllRoles();
		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		return roleManager.getPrimaryKeys(null).toArray(new Long[0]);
	}

	/// <summary>
	/// Gets a list of the roles that a specified user is in for the current application.
	/// </summary>
	/// <param name="userName">The user name.</param>
	/// <returns>A list of the roles that a specified user is in for the current application.</returns>
	public static String[] getRoleNamesForUser(String userName)	{
		if (StringUtils.isBlank(userName))
			return new String[] { };

		//return RoleMds.GetRolesForUser(userName.Trim());
		return UserUtils.getUser(userName).getRoles().stream().map(r->r.getName()).toArray(String[]::new);
	}
	
	public static Long[] getRolesForUser(String userName)	{
		if (StringUtils.isBlank(userName))
			return new Long[] { };

		//return RoleMds.GetRolesForUser(userName.Trim());
		return UserUtils.getUser(userName).getRoles().stream().map(r->r.getId()).toArray(Long[]::new);
	}

	/// <summary>
	/// Gets a list of users in the specified role for the current application.
	/// </summary>
	/// <param name="roleId">The name of the role.</param>
	/// <returns>A list of users in the specified role for the current application.</returns>
	public static String[] getUsersInRole(String roleName, long oId)	{
		if (StringUtils.isBlank(roleName))
			return new String[] { };

		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		//return RoleMds.GetUsersInRole(roleId.Trim());
		Role role = roleManager.getRole(roleName, oId);
		return role.getUsers().stream().map(u->u.getUsername()).toArray(String[]::new);
	}
	
	public static String[] getUsersInRole(long roleId)	{
		if (Role.isIllegalId(roleId))
			return new String[] { };

		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		//return RoleMds.GetUsersInRole(roleId.Trim());
		Role role = roleManager.get(roleId);
		return role.getUsers().stream().map(u->u.getUsername()).toArray(String[]::new);
	}

	/// <summary>
	/// Adds a role to the data source for the current application. If the role already exists, no action is taken.
	/// </summary>
	/// <param name="roleName">Name of the role. Any leading or trailing spaces are removed.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="roleName" /> is null.</exception>
	public static void createRole(long roleId)	{
		if (Role.isIllegalId(roleId))
			throw new ArgumentNullException("roleId");

		synchronized (_sharedLock){
			if (!roleExists(roleId)){
				//RoleMds.CreateRole(roleName.Trim());
			}
		}
	}
	
	public static void createRole(String roleName, long oId)	{
		if (StringUtils.isBlank(roleName))
			throw new ArgumentNullException("roleName");

		synchronized (_sharedLock){
			if (!roleExists(roleName, oId)){
				//RoleMds.CreateRole(roleName.Trim());
			}
		}
	}

	/// <summary>
	/// Removes a role from the data source for the current application.
	/// </summary>
	/// <param name="roleName">Name of the role.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="roleName" /> is null.</exception>
	private static void deleteRole(String roleName, long oId)	{
		if (StringUtils.isBlank(roleName))
			throw new ArgumentNullException("roleName");
		
		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		roleManager.removeRole(roleManager.getRole(roleName, oId));

		//RoleMds.DeleteRole(roleName.Trim(), false);
	}
	
	private static void deleteRole(long roleId)	{
		if (Role.isIllegalId(roleId))
			throw new ArgumentNullException("roleId");
		
		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		roleManager.remove(roleId);

		//RoleMds.DeleteRole(roleName.Trim(), false);
	}

	/// <summary>
	/// Gets a value indicating whether the specified role name already exists in the data source for the current application.
	/// </summary>
	/// <param name="roleName">Name of the role.</param>
	/// <returns><c>true</c> if the role exists; otherwise <c>false</c>.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="roleName" /> is null.</exception>
	public static boolean roleExists(long roleId){
		if (Role.isIllegalId(roleId))
			throw new ArgumentNullException("roleId");

		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		return roleManager.exists(roleId);
	}
	
	public static boolean roleExists(String roleName, long oId){
		if (StringUtils.isBlank(roleName))
			throw new ArgumentNullException("roleName");

		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		return roleManager.roleExists(roleName, oId);
	}
	
	public static boolean roleTypeExists(RoleType roleType, long oId){
		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		return roleManager.roleTypeExists(roleType, oId);
	}

	/// <summary>
	/// Gets a value indicating whether the specified user is in the specified role for the current application.
	/// </summary>
	/// <param name="userName">The user name to search for.</param>
	/// <param name="roleName">The role to search in.</param>
	/// <returns>
	/// 	<c>true</c> if the specified user is in the specified role for the configured applicationName; otherwise, <c>false</c>.
	/// </returns>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="userName" /> or <paramref name="roleName" /> is null
	/// or an empty String.</exception>
	public static boolean isUserInRole(String userName, String roleName){
		if (StringUtils.isBlank(userName))
			throw new ArgumentOutOfRangeException("userName", "The parameter 'userName' cannot be null or an empty String.");

		if (StringUtils.isBlank(roleName))
			throw new ArgumentOutOfRangeException("roleName", "The parameter 'roleName' cannot be null or an empty String.");

		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		//return RoleMds.IsUserInRole(userName.Trim(), roleName.Trim());
		Role role = roleManager.getRole(roleName);
		return role.getUsers().stream().anyMatch(u->u.getUsername() == userName.trim());
	}
	
	public static boolean isUserInRole(String userName, long roleId){
		if (StringUtils.isBlank(userName))
			throw new ArgumentOutOfRangeException("userName", "The parameter 'userName' cannot be null or an empty String.");

		if (Role.isIllegalId(roleId))
			throw new ArgumentOutOfRangeException("roleId", "The parameter 'roleName' cannot be null or an empty String.");

		RoleManager roleManager = SpringContextHolder.getBean(RoleManager.class);
		
		//return RoleMds.IsUserInRole(userName.Trim(), roleName.Trim());
		Role role = roleManager.get(roleId);
		return role.getUsers().stream().anyMatch(u->u.getUsername() == userName.trim());
	}

	/// <overloads>Retrieve MDS System roles.</overloads>
	/// <summary>
	/// Retrieve MDS System roles matching the specified role names. The roles may be  returned from a cache.
	/// </summary>
	/// <returns>Returns all roles.</returns>
	public static MDSRoleCollection getMDSRoles(Collection<String> roleNames, long oId) throws InvalidMDSRoleException	{
		return loadMDSRoles(roleNames, oId);
	}
	
	public static MDSRoleCollection getMDSRoles(Collection<Long> roleIds) throws InvalidMDSRoleException	{
		return loadMDSRoles(roleIds);
	}

	/// <summary>
	/// Retrieve MDS System roles, optionally excluding roles that were programmatically
	/// created to assist with the album ownership and user album functions. Excluding the owner roles may be useful
	/// in reducing the clutter when an administrator is viewing the list of roles, as it hides those not specifically created
	/// by the administrator. The roles may be returned from a cache.
	/// </summary>
	/// <param name="includeOwnerRoles">If set to <c>true</c> include all roles that serve as an album owner role.
	/// When <c>false</c>, exclude owner roles from the result set.</param>
	/// <returns>
	/// Returns the MDS System roles, optionally excluding owner roles.
	/// </returns>
	public static MDSRoleCollection getMDSRoles() {
		return getMDSRoles(true);
	}
	
	public static MDSRoleCollection getMDSRoles(boolean includeOwnerRoles){
		if (includeOwnerRoles){
			return loadMDSRoles();
		}else{
			MDSRoleCollection roles = new MDSRoleCollection();

			for (MDSRole role : loadMDSRoles())	{
				if (!isRoleAnAlbumOwnerRole(role.getRoleName())){
					roles.add(role);
				}
			}

			return roles;
		}
	}

	/// <overloads>
	/// Gets a collection of MDS System roles.
	/// </overloads>
	/// <summary>
	/// Gets MDS System roles representing the roles for the currently logged-on user. Returns an empty collection if 
	/// no user is logged in or the user is logged in but not assigned to any roles (Count = 0).
	/// The roles may be returned from a cache. Guaranteed to not return null.
	/// </summary>
	/// <returns>
	/// Returns an <see cref="MDSRoleCollection" /> representing the roles for the currently logged-on user.
	/// </returns>
	public static MDSRoleCollection getMDSRolesForUser() throws InvalidMDSRoleException{
		return getMDSRolesForUser(UserUtils.getLoginName());
	}

	/// <summary>
	/// Gets MDS System roles representing the roles for the specified <paramref name="userName"/>. Returns an empty collection if 
	/// the user is not assigned to any roles (Count = 0). The roles may be returned from a cache. Guaranteed to not return null.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <returns>
	/// Returns an <see cref="MDSRoleCollection"/> representing the roles for the specified <paramref name="userName" />.
	/// </returns>
	/// <remarks>This method may run on a background thread and is therefore tolerant of the inability to access HTTP context 
	/// or the current user's session.</remarks>
	public static MDSRoleCollection getMDSRolesForUser(String userName) throws InvalidMDSRoleException	{
		if (StringUtils.isBlank(userName))
			return new MDSRoleCollection();

		// Get cached dictionary entry matching logged on user. If not found, retrieve from business layer and add to cache.
		//ConcurrentHashMap<String, MDSRoleCollection> rolesCache = (ConcurrentHashMap<String, MDSRoleCollection>)CacheUtils.get(CacheItem.MDSRoles);

		MDSRoleCollection roles = null;

		if (UserUtils.getDetail() != null) {
			if (((UserAccount)UserUtils.getDetail()).getRoles() != null) {
				return ((UserAccount)UserUtils.getDetail()).getRoles();
			}
		}
		/*if (UserUtils.getSession() != null){
			if ((rolesCache != null) && ((roles = rolesCache.get(getCacheKeyNameForRoles(userName))) != null))	{
				return roles;
			}
		}*/

		// No roles in the cache, so get from business layer and add to cache.
		try	{
			roles = loadMDSRoles(Arrays.asList(getRolesForUser(userName)));
		}catch (InvalidMDSRoleException ex){
			// We could not find one or more MDS roles for the ASP.NET roles we passed to CMUtils.loadMDSRoles(). Things probably
			// got out of synch. For example, this can happen if an admin adds an ASP.NET role outside of MDS (such as when using the 
			// DNN control panel). Purge the cache, then run the validation routine, and try again. If the same exception is thrown again,
			// let it bubble up - there isn't anything more we can do.
			HelperFunctions.purgeCache();

			validateRoles();

			roles = loadMDSRoles(Arrays.asList(getRolesForUser(userName)));
		}
		
		if (UserUtils.getDetail() != null) {
			((UserAccount)UserUtils.getDetail()).setRoles(roles);
		}

		/*if (rolesCache == null)	{
			// The factory method should have created a cache item, so try again.
			rolesCache = (ConcurrentHashMap<String, MDSRoleCollection>)CacheUtils.get(CacheItem.MDSRoles);
			if (rolesCache == null)	{
				if (AppSettings.getInstance().getEnableCache()){
					AppEventLogUtils.LogError(new WebException("The method CMUtils.loadMDSRoles() should have created a cache entry, but none was found. This is not an issue if it occurs occasionally, but should be addressed if it is frequent."));
				}

				return roles;
			}
		}

		// Add to the cache, but only if we have access to the session ID.
		if (UserUtils.getSession() != null){
			synchronized (rolesCache)
			{
				if (!rolesCache.containsKey(getCacheKeyNameForRoles(userName)))	{
					rolesCache.put(getCacheKeyNameForRoles(userName), roles);
				}
			}
			CacheUtils.put(CacheItem.MDSRoles, rolesCache);
		}*/

		return roles;
	}
	
	public static MDSRoleCollection getMDSRolesForRoleIds(Collection<Long> roleIds) throws InvalidMDSRoleException	{
		if (CollectionUtils.isEmpty(roleIds))
			return new MDSRoleCollection();

		MDSRoleCollection roles = null;

		// No roles in the cache, so get from business layer and add to cache.
		try	{
			roles = loadMDSRoles(roleIds);
		}catch (InvalidMDSRoleException ex){
			// We could not find one or more MDS roles for the ASP.NET roles we passed to CMUtils.loadMDSRoles(). Things probably
			// got out of synch. For example, this can happen if an admin adds an ASP.NET role outside of MDS (such as when using the 
			// DNN control panel). Purge the cache, then run the validation routine, and try again. If the same exception is thrown again,
			// let it bubble up - there isn't anything more we can do.
			HelperFunctions.purgeCache();

			validateRoles();

			roles = loadMDSRoles(roleIds);
		}
		
		return roles;
	}

	/// <summary>
	/// Gets all the MDS System roles that apply to the specified <paramref name="gallery" />.
	/// </summary>
	/// <param name="gallery">The gallery.</param>
	/// <returns>Returns an <see cref="MDSRoleCollection"/> representing the roles that apply to the specified 
	/// <paramref name="gallery" />.</returns>
	public static MDSRoleCollection getMDSRolesForGallery(GalleryBo gallery){
		MDSRoleCollection roles = new MDSRoleCollection();

		for (MDSRole role : getMDSRoles())	{
			if (role.getGalleries().contains(gallery) && (!roles.contains(role))){
				roles.add(role);
			}
		}

		return roles;
	}

	/// <summary>
	/// Gets the list of roles the user has permission to view. Users who have administer site permission can view all roles.
	/// Users with administer gallery permission can only view roles they have been associated with or roles that aren't 
	/// associated with *any* gallery, unless the application setting <see cref="IAppSetting.AllowGalleryAdminToViewAllUsersAndRoles" />
	/// is true, in which case they can see all roles.
	/// </summary>
	/// <param name="userIsSiteAdmin">If set to <c>true</c>, the currently logged on user is a site administrator.</param>
	/// <param name="userIsGalleryAdmin">If set to <c>true</c>, the currently logged on user is a gallery administrator for the current gallery.</param>
	/// <returns>Returns an <see cref="MDSRoleCollection" /> containing a list of roles the user has permission to view.</returns>
	public static MDSRoleCollection getRolesCurrentUserCanView(boolean userIsSiteAdmin, boolean userIsGalleryAdmin) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException{
		//if (userIsSiteAdmin || (userIsGalleryAdmin && AppSetting.Instance.AllowGalleryAdminToViewAllUsersAndRoles))
		if (userIsSiteAdmin){ // Modify by John 14/10/2014
			return RoleUtils.getMDSRoles();
		}
		else if (userIsGalleryAdmin){
			MDSRoleCollection roles = RoleUtils.getMDSRoles();
			MDSRoleCollection filteredRoles = new MDSRoleCollection();

			// Build up a list of roles where (1) the current user is a gallery admin for at least one gallery, 
			// (2) the role is an album owner template role and the current user is a gallery admin for its associated gallery, or
			// (3) the role isn't associated with any albums/galleries.
			for (MDSRole role : roles){
				if (userIsSiteAdmin || (!userIsSiteAdmin && role.getRoleName() != "System Administrator")){// add by John 14/10/2014
					if (role.getGalleries().size() > 0)	{
						if (isUserGalleryAdminForRole(role)){
							// Current user has gallery admin permissions for at least one galley associated with the role.
							filteredRoles.add(role);
						}
					}else if (isRoleAnAlbumOwnerTemplateRole(role.getRoleName())){
						if (isUserGalleryAdminForAlbumOwnerTemplateRole(role)){
							// The role is an album owner template role and the current user is a gallery admin for it's associated gallery.
							filteredRoles.add(role);
						}
					}else{
						// Role isn't an album owner role and it isn't assigned to any albums. Add it.
						filteredRoles.add(role);
					}
				}
			}

			return filteredRoles;
		}else{
			return new MDSRoleCollection();
		}
	}

	/// <summary>
	/// Create a MDS System role corresponding to the specified parameters. Also creates the corresponding ASP.NET role.
	/// Throws an exception if a role with the specified name already exists in the data store. The role is persisted to the data store.
	/// </summary>
	/// <param name="roleName">A String that uniquely identifies the role.</param>
	/// <param name="allowViewAlbumOrContentObject">A value indicating whether the user assigned to this role has permission to view albums
	/// and content objects.</param>
	/// <param name="allowViewOriginalImage">A value indicating whether the user assigned to this role has permission to view the original,
	/// high resolution version of an image. This setting applies only to images. It has no effect if there are no
	/// high resolution images in the album or albums to which this role applies.</param>
	/// <param name="allowAddContentObject">A value indicating whether the user assigned to this role has permission to add content objects to an album.</param>
	/// <param name="allowAddChildAlbum">A value indicating whether the user assigned to this role has permission to create child albums.</param>
	/// <param name="allowEditContentObject">A value indicating whether the user assigned to this role has permission to edit a content object.</param>
	/// <param name="allowEditAlbum">A value indicating whether the user assigned to this role has permission to edit an album.</param>
	/// <param name="allowDeleteContentObject">A value indicating whether the user assigned to this role has permission to delete content objects within an album.</param>
	/// <param name="allowDeleteChildAlbum">A value indicating whether the user assigned to this role has permission to delete child albums.</param>
	/// <param name="allowSynchronize">A value indicating whether the user assigned to this role has permission to synchronize an album.</param>
	/// <param name="allowAdministerSite">A value indicating whether the user has administrative permission for all albums. This permission
	/// automatically applies to all albums across all galleries; it cannot be selectively applied.</param>
	/// <param name="allowAdministerGallery">A value indicating whether the user has administrative permission for all albums. This permission
	/// automatically applies to all albums in a particular gallery; it cannot be selectively applied.</param>
	/// <param name="hideWatermark">A value indicating whether the user assigned to this role has a watermark applied to images.
	/// This setting has no effect if watermarks are not used. A true value means the user does not see the watermark;
	/// a false value means the watermark is applied.</param>
	/// <param name="topLevelCheckedAlbumIds">The top level checked album ids. May be null.</param>
	/// <returns>
	/// Returns an <see cref="MDSRole"/> object corresponding to the specified parameters.
	/// </returns>
	/// <exception cref="InvalidMDSRoleException">Thrown when a role with the specified role name already exists in the data store.</exception>
	public static MDSRole createRole(long roleId, String roleName, RoleType roleType, boolean allowViewAlbumOrContentObject, boolean allowViewOriginalImage, boolean allowAddContentObject, boolean allowAddChildAlbum, boolean allowEditContentObject, boolean allowEditAlbum, boolean allowDeleteContentObject, boolean allowApproveContentObject, boolean allowDeleteChildAlbum, boolean allowSynchronize, boolean allowAdministerSite, boolean allowAdministerGallery, boolean hideWatermark, LongCollection topLevelCheckedAlbumIds) throws InvalidMDSRoleException, GallerySecurityException, UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, WebException	{
		synchronized (_sharedLock){
			String uniqueRoleName = makeRoleNameUnique(roleName);

			// Create the ASP.NET role.
			createRole(uniqueRoleName, Long.MIN_VALUE);

			// Create the MDS System role that extends the functionality of the ASP.NET role.
			MDSRole role = createMDSRoleInstance(roleId, uniqueRoleName, roleType, Long.MIN_VALUE, allowViewAlbumOrContentObject, allowViewOriginalImage, allowAddContentObject, allowAddChildAlbum, allowEditContentObject, allowEditAlbum, allowDeleteContentObject, allowApproveContentObject, allowDeleteChildAlbum, allowSynchronize, allowAdministerSite, allowAdministerGallery, hideWatermark);

			updateRoleAlbumRelationships(role, topLevelCheckedAlbumIds);

			validateSaveRole(role);

			role.save();

			return role;
		}
	}

	/// <summary>
	/// Delete the specified role. Both components of the role are deleted: the MDSRole and ASP.NET role.
	/// </summary>
	/// <param name="roleName">Name of the role. Must match an existing <see cref="MDSRole.getRoleName()"/>. If no match
	/// if found, no action is taken.</param>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be deleted because doing so violates one of the business rules.</exception>
	public static void deleteSystemRole(String roleName, long oId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		validateDeleteRole(roleName, oId);

		try{
			deleteMDSRole(roleName, oId);
		}finally{
			try	{
				deleteADRole(roleName);
			}finally{
				//HelperFunctions.PurgeCache();
			}
		}
	}
	
	public static void deleteSystemRole(long roleId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException{
		validateDeleteRole(roleId);

		String roleName = loadMDSRole(roleId).getRoleName();
		try{
			deleteMDSRole(roleId);
		}finally{
			try	{
				deleteADRole(roleName);
			}finally{
				//HelperFunctions.PurgeCache();
			}
		}
	}

	/// <summary>
	/// Throws an exception if the role cannot be deleted, such as when deleting the only role with Administer site permission
	/// or deleting a role that would lessen the logged-on users own level of administrative access.
	/// </summary>
	/// <param name="roleName">Name of the role to be deleted.</param>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be deleted because doing so violates one of the business rules.</exception>
	public static void validateDeleteRole(String roleName, long oId) throws GallerySecurityException, InvalidMDSRoleException{
		MDSRole roleToDelete = loadMDSRole(roleName, oId);

		if (roleToDelete == null)
			return;

		// Test 1: Don't let user delete the only role with Administer site permission.
		validatePreventLastSysAdminRoleDeletion(roleToDelete);

		// Test 2: Don't let user delete a role with site admin or gallery admin permissions if that means the user will 
		// lose their own administrative access.
		validatePreventLoggedOnUserFromLosingAdminAccess(roleToDelete);

		// Test 3: User can delete role only if he is a site admin or a gallery admin in every gallery this role is associated with.
		validatePreventRoleDeletionAffectingOtherGalleries(roleToDelete);
	}
	
	public static void validateDeleteRole(long roleId) throws GallerySecurityException, InvalidMDSRoleException{
		MDSRole roleToDelete = loadMDSRole(roleId);

		if (roleToDelete == null)
			return;

		// Test 1: Don't let user delete the only role with Administer site permission.
		validatePreventLastSysAdminRoleDeletion(roleToDelete);

		// Test 2: Don't let user delete a role with site admin or gallery admin permissions if that means the user will 
		// lose their own administrative access.
		validatePreventLoggedOnUserFromLosingAdminAccess(roleToDelete);

		// Test 3: User can delete role only if he is a site admin or a gallery admin in every gallery this role is associated with.
		validatePreventRoleDeletionAffectingOtherGalleries(roleToDelete);
	}

	/// <summary>
	/// Make sure the list of ASP.NET roles is synchronized with the MDS System roles. If any are missing from 
	/// either, add it.
	/// </summary>
	public static void validateRoles() throws InvalidMDSRoleException{
		List<MDSRole> validatedRoles = new ArrayList<MDSRole>();
		MDSRoleCollection galleryRoles = loadMDSRoles();
		boolean needToPurgeCache = false;

		for (String roleName : getAllRoles()){
			MDSRole galleryRole = galleryRoles.getRole(roleName, Long.MIN_VALUE);
			if (galleryRole == null){
				// This is an ASP.NET role that doesn't exist in our list of MDS System roles. Add it with minimum permissions
				// applied to zero albums.
				MDSRole newRole = createMDSRoleInstance(Long.MIN_VALUE, roleName, RoleType.gu, Long.MIN_VALUE, false, false, false, false, false, false, false, false, false, false, false, false, false);
				newRole.save();
				needToPurgeCache = true;
			}
			validatedRoles.add(galleryRole);
		}

		// Now check to see if there are gallery roles that are not ASP.NET roles. Add if necessary.
		for (MDSRole galleryRole : galleryRoles){
			if (!validatedRoles.contains(galleryRole)){
				// Need to create an ASP.NET role for this gallery role.
				createRole(galleryRole.getRoleName(), Long.MIN_VALUE);
				needToPurgeCache = true;
			}
		}

		if (needToPurgeCache){
			HelperFunctions.purgeCache();
		}
	}

	/// <summary>
	/// Verify a role with AllowAdministerSite permission exists, creating it if necessary. Return the role name.
	/// </summary>
	/// <returns>A <see cref="System.String" />.</returns>
	public static String validateSysAdminRole() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException{
		// Create the Sys Admin role if needed. If it already exists, make sure it has AllowAdministerSite permission.
		String sysAdminRoleName = I18nUtils.getMessage("site.Sys_Admin_Role_Name");
		if (!roleTypeExists(RoleType.sa, Long.MIN_VALUE))	{
			createRole(sysAdminRoleName, Long.MIN_VALUE);
		}

		MDSRole role = loadMDSRole(sysAdminRoleName, Long.MIN_VALUE);
		if (role == null){
			role = createMDSRoleInstance(Long.MIN_VALUE, sysAdminRoleName, RoleType.sa, Long.MIN_VALUE, true, true, true, true, true, true, true, true, true, true, true, true, false);
		}else if (!role.getAllowAdministerSite()){
			// Role already exists. Make sure it has Sys Admin permission.
			role.setAllowAdministerSite(true);
		}

		for (GalleryBo gallery : CMUtils.loadGalleries()){
			AlbumBo album = CMUtils.loadRootAlbumInstance(gallery.getGalleryId());
			if (!role.getRootAlbumIds().contains(album.getId()))	{
				role.getRootAlbumIds().add(album.getId());
			}
		}

		role.save();

		return role.getRoleName();
	}

	/// <summary>
	/// Verify that any role needed for album ownership exists and is properly configured. If an album owner
	/// is specified and the album is new (IsNew == true), the album is persisted to the data store. This is 
	/// required because the ID is not assigned until it is saved, and a valid ID is required to configure the
	/// role.
	/// </summary>
	/// <param name="album">The album to validate for album ownership. If a null value is passed, the function
	/// returns without error or taking any action.</param>
	public static void validateRoleExistsForAlbumOwner(AlbumBo album) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException{
		// For albums, verify that any needed roles for album ownership are present. Create/update as needed.
		if (album == null)
			return;

		long oId = UserUtils.getOwnerOrganizationId(album.getGalleryId());
		if (StringUtils.isBlank(album.getOwnerUserName())){
			// If owner role is specified, delete it.
			if (!StringUtils.isBlank(album.getOwnerRoleName())){
				deleteSystemRole(album.getOwnerRoleName(), oId);
				album.setOwnerRoleName(StringUtils.EMPTY);
			}
		}else{
			// If this is a new album, save it before proceeding. We will need its album ID to configure the role, 
			// and it is not assigned until it is saved.
			if (album.getIsNew())
				album.save();

			// Verify that a role exists that corresponds to the owner.
			MDSRole role = loadMDSRoles().getRole(album.getOwnerRoleName(), oId);
			if (role == null){
				// No role exists. Create it.
				album.setOwnerRoleName(createAlbumOwnerRole(album));
			}else{
				// Role exists. Make sure album is assigned to role and owner is a member.
				if (!role.getRootAlbumIds().contains(album.getId()))	{
					// Current album is not a member. This should not typically occur, but just in case
					// it does let's add the current album to it and save it.
					role.getRootAlbumIds().add(album.getId());
					role.save();
				}

				Long[] rolesForUser = getRolesForUser(album.getOwnerUserName());
				if (ArrayUtils.indexOf(rolesForUser, role.getRoleId()) < 0){
					// Owner is not a member. Add.
					addUserToRole(album.getOwnerUserName(), role.getRoleName());
				}
			}
		}
	}

	/// <summary>
	/// Determines whether the <paramref name="roleName"/> is a role that serves as an album owner role. Returns <c>true</c> if the
	/// <paramref name="roleName"/> starts with the same String as the global finalant <see cref="GlobalConstants.AlbumOwnerRoleNamePrefix"/>.
	/// Album owner roles are roles that are programmatically created to provide the security context used for the album ownership
	/// and user album features.
	/// </summary>
	/// <param name="roleName">Name of the role.</param>
	/// <returns>
	/// 	<c>true</c> if <paramref name="roleName"/> is a role that serves as an album owner role; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isRoleAnAlbumOwnerRole(String roleName){
		if (StringUtils.isBlank(roleName))
			return false;

		return roleName.trim().startsWith(Constants.AlbumOwnerRoleNamePrefix);
	}

	/// <summary>
	/// Determines whether the <paramref name="roleName"/> is a role that serves as an album owner template role. Returns <c>true</c> if the
	/// <paramref name="roleName"/> matches a regular expression that defines the pattern for the template role name.
	/// Album owner roles are created from the album owner template role.
	/// </summary>
	/// <param name="roleName">Name of the role.</param>
	/// <returns>
	/// 	<c>true</c> if <paramref name="roleName"/> is a role that serves as an album owner template role; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isRoleAnAlbumOwnerTemplateRole(String roleName){
		return _mdsAlbumOwnerTemplateRoleNameRegEx.matcher(roleName).matches();
	}

	/// <summary>
	/// Removes the roles belonging to the current user from cache. This cache item has a unique name based on the session ID and logged-on 
	/// user's name. This function is not critical for security or correctness, but is useful in keeping the cache cleared of unused items. When
	/// a user logs on or off, their username changes - and therefore the name of the cache item changes, which causes the next call to 
	/// retrieve the user's roles to return nothing from the cache, which forces a retrieval from the database. Thus the correct roles will
	/// always be retrieved, even if this function is not invoked during a logon/logoff event.
	/// </summary>
	public static void removeRolesFromCache(){
		ConcurrentHashMap<String, MDSRoleCollection> rolesCache = (ConcurrentHashMap<String, MDSRoleCollection>)CacheUtils.get(CacheItem.MDSRoles);

		if ((rolesCache != null) && (UserUtils.getSession() != null)){
			rolesCache.remove(getCacheKeyNameForRoles(UserUtils.getLoginName()));
		}
	}

	/// <overloads>
	/// Modify the name of the role to ensure it is unique to the portal (applies only to DotNetNuke versions).
	/// </overloads>
	/// <summary>
	/// This function returns the <paramref name="roleName" /> parameter without modification. It serves as a placeholder function
	/// for the DotNetNuke implementation.
	/// </summary>
	/// <param name="roleName">Name of the role.</param>
	/// <returns>Returns the <paramref name="roleName" /> with the portal ID appended to it.</returns>
	public static String makeRoleNameUnique(String roleName){
		return roleName;
	}

	/// <summary>
	/// This function returns the <paramref name="roleName" /> parameter without modification. It serves as a placeholder function
	/// for the DotNetNuke implementation.
	/// </summary>
	/// <param name="roleName">Name of the role.</param>
	/// <param name="portalId">The portal ID. Specify <see cref="Integer.MIN_VALUE" /> for non-DotNetNuke versions of this code.</param>
	/// <returns>Returns the <paramref name="roleName" /> without modification.</returns>
	public static String makeRoleNameUnique(String roleName, int portalId){
		return roleName;
	}

	/// <summary>
	/// Parses the name of the role from the <paramref name="roleNames" />. Example: If role name = "Administrators_0", return
	/// "Administrators". This function works by using a regular expression to remove all text that matches the "_{GalleryID}"
	/// pattern. If the role name does not have this suffix, the original role name is returned. This function is useful when
	/// MDS is used in an application where the role provider allows multiple roles with the same name, such as DotNetNuke.
	/// The contents of this function is commented out in the trunk (stand-alone) version of MDS and enabled in branched versions
	/// where required (such as DotNetNuke).
	/// </summary>
	/// <param name="roleNames">Name of the roles.</param>
	/// <returns>Returns a copy of the <paramref name="roleNames" /> parameter with the "_{GalleryID}" portion removed from each 
	/// role name.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="" /> is null.</exception>
	public static String[] parseRoleNameFromMdsRoleNames(String[] roleNames){
		if (roleNames == null)
			throw new ArgumentNullException("roleNames");

		String[] roleNamesCopy = new String[roleNames.length];

		for (int i = 0; i < roleNames.length; i++)	{
			roleNamesCopy[i] = parseRoleNameFromMdsRoleName(roleNames[i]);
		}

		return roleNamesCopy;
	}

	/// <summary>
	/// Parses the name of the role from the <paramref name="roleName" />. Example: If role name = "Administrators_0", return
	/// "Administrators". This function works by using a regular expression to remove all text that matches the "_{GalleryID}"
	/// pattern. If the role name does not have this suffix, the original role name is returned. This function is useful when
	/// MDS is used in an application where the role provider allows multiple roles with the same name, such as DotNetNuke.
	/// The contents of this function is commented out in the trunk (stand-alone) version of MDS and enabled in branched versions
	/// where required (such as DotNetNuke).
	/// </summary>
	/// <param name="roleName">Name of the role. Example: "Administrators_0"</param>
	/// <returns>Returns the role name with the "_{GalleryID}" portion removed.</returns>
	public static String parseRoleNameFromMdsRoleName(String roleName){
		return roleName;
		//return _mdsRoleNameSuffixRegEx.Replace(roleName, StringUtils.EMPTY); // DotNetNuke only
	}

	//#endregion

	//#region Private Methods

	/// <summary>
	/// Make sure the loggod-on person has authority to save the role and that h/she isn't doing anything stupid, like removing
	/// Administer site permission from the only role that has it.
	/// </summary>
	/// <param name="roleToSave">The role to be saved.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="roleToSave"/> is null.</exception>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be saved because doing so would violate a business rule.</exception>
	/// <exception cref="InvalidMDSRoleException">Thrown when an existing role cannot be found in the database that matches the 
	/// role name of the <paramref name="roleToSave" /> parameter.</exception>
	private static void validateSaveRole(MDSRole roleToSave) throws GallerySecurityException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException{
		//#region Parameter Validation

		if (roleToSave == null)
			throw new ArgumentNullException("roleToSave");

		if (StringUtils.isBlank(roleToSave.getRoleName()))
			return; // Role name will be empty when adding a new one, so the validation below doesn't apply.

		MDSRole existingRole = loadMDSRole(roleToSave.getRoleName(), roleToSave.getOrganizationId());
		if (existingRole == null)
			existingRole = roleToSave;

		//#endregion

		validateCanRemoveSiteAdminPermission(roleToSave, existingRole);

		validateUserHasPermissionToSaveRole(roleToSave, existingRole);

		validateUserDoesNotLoseAbilityToAdminCurrentGallery(roleToSave, existingRole);
	}

	/// <summary>
	/// If administer site permission is being removed from the <paramref name="roleToSave" />, verify that this action does not violate
	/// business rules. Specifically, ensure that at least one other role has the same permission to prevent the user from removing their
	/// ability to administer the site. Throws a <see cref="GallerySecurityException" /> if the role should not be saved.
	/// </summary>
	/// <param name="roleToSave">The role to save. It's role name must match the role name of <paramref name="existingRole" />.</param>
	/// <param name="existingRole">The existing role, as it is stored in the database. It's role name must match the role name of
	/// <paramref name="roleToSave" />.</param>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be saved because doing so would violate a business rule.</exception>
	private static void validateCanRemoveSiteAdminPermission(MDSRole roleToSave, MDSRole existingRole) throws GallerySecurityException	{
		//if (!roleToSave.getRoleName().equalsIgnoreCase(existingRole.getRoleName()))	{
		if (roleToSave.getRoleId() != existingRole.getRoleId())	{
			throw new ArgumentOutOfRangeException(MessageFormat.format("The role name of the roleToSave and existingRole parameters must match, but they do not. roleToSave='{0}'; existingRole='{1}'", roleToSave, existingRole));
		}

		if (existingRole.getAllowAdministerSite() && !roleToSave.getAllowAdministerSite()){
			// User is trying to remove administer site permission from this role. Make sure
			// at least one other role has this permission, and that the role has at least one member.
			boolean atLeastOneOtherRoleHasAdminSitePermission = false;
			for (MDSRole role : getMDSRoles()){
				if ((role.getRoleId() != existingRole.getRoleId() && role.getAllowAdministerSite()))	{
					if (getUsersInRole(role.getRoleId()).length > 0){
						atLeastOneOtherRoleHasAdminSitePermission = true;
						break;
					}
				}
			}

			if (!atLeastOneOtherRoleHasAdminSitePermission)	{
				throw new GallerySecurityException(I18nUtils.getMessage("Admin_Manage_Roles_Cannot_Remove_Admin_Perm_Msg"));
			}
		}
	}

	/// <summary>
	/// Verify the user has permission to save the role.
	/// Specifically, the user is not allowed to add administer site permission or save any gallery she is not a gallery
	/// administrator for. It is up to the caller to verify that only site or gallery administrators call this function!
	/// </summary>
	/// <param name="roleToSave">The role to save. It's role name must match the role name of <paramref name="existingRole" />.</param>
	/// <param name="existingRole">The existing role, as it is stored in the database. It's role name must match the role name of
	/// <paramref name="roleToSave" />.</param>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be saved because doing so would violate a business rule.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="roleToSave" /> or <paramref name="existingRole" /> is null.</exception>
	private static void validateUserHasPermissionToSaveRole(MDSRole roleToSave, MDSRole existingRole) throws GallerySecurityException, InvalidMDSRoleException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException{
		if (roleToSave == null)
			throw new ArgumentNullException("roleToSave");

		if (existingRole == null)
			throw new ArgumentNullException("existingRole");

		if (!roleToSave.getRoleName().equalsIgnoreCase(existingRole.getRoleName() )){
			throw new ArgumentOutOfRangeException(MessageFormat.format("The role name of the roleToSave and existingRole parameters must match, but they do not. roleToSave='{0}'; existingRole='{1}'", roleToSave, existingRole));
		}

		MDSRoleCollection roles = getMDSRolesForUser();

		validateUserCanEditRole(roleToSave, existingRole, roles);

		if (!UserUtils.isUserSiteAdministrator(roles)){
			// User is a gallery admin but not a site admin (we deduce this because ONLY site or gallery admins will get this 
			// far in the function. The user CANNOT save add AllowAdminSite permission.
			if (roleToSave.getAllowAdministerSite())	{
				throw new GallerySecurityException(I18nUtils.getMessage("Admin_Manage_Roles_Cannot_Delete_Role_Insufficient_Permission_Msg"));
			}
		}
	}

	/// <summary>
	/// Verify the the current user isn't jeopardizing their ability to administer the site or current gallery. Specifically, if
	/// the user is a member of the role being saved and admin site or gallery permissions are being removed from it, make sure
	/// the user is in at least one other role with similar permissions. Verifies only the current gallery: That is, it is possible
	/// for the user to remove their ability to administer another gallery.
	/// </summary>
	/// <param name="roleToSave">The role to save. It's role name must match the role name of <paramref name="existingRole" />.</param>
	/// <param name="existingRole">The existing role, as it is stored in the database. It's role name must match the role name of
	/// <paramref name="roleToSave" />.</param>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be saved because doing so would violate a business rule.</exception>
	private static void validateUserDoesNotLoseAbilityToAdminCurrentGallery(MDSRole roleToSave, MDSRole existingRole) throws GallerySecurityException, InvalidMDSRoleException{
		if (!roleToSave.getRoleName().equalsIgnoreCase(existingRole.getRoleName() )){
			throw new ArgumentOutOfRangeException(MessageFormat.format("The role name of the roleToSave and existingRole parameters must match, but they do not. roleToSave='{0}'; existingRole='{1}'", roleToSave, existingRole));
		}

		if (isUserInRole(UserUtils.getLoginName(), roleToSave.getRoleName()))
		{
			boolean adminSitePermissionBeingRevoked = (!roleToSave.getAllowAdministerSite() && existingRole.getAllowAdministerSite());
			boolean adminGalleryPermissionBeingRevoked = (!roleToSave.getAllowAdministerGallery() && existingRole.getAllowAdministerGallery());

			boolean userHasAdminSitePermissionThroughAtLeastOneOtherRole = false;
			boolean userHasAdminGalleryPermissionThroughAtLeastOneOtherRole = false;

			for (MDSRole roleForUser : getMDSRolesForUser()){
				if (!roleForUser.getRoleName().equals(roleToSave.getRoleName()))
				{
					if (roleForUser.getAllowAdministerSite()){
						userHasAdminSitePermissionThroughAtLeastOneOtherRole = true;
					}
					if (roleForUser.getAllowAdministerGallery()){
						userHasAdminGalleryPermissionThroughAtLeastOneOtherRole = true;
					}
				}
			}

			if (adminSitePermissionBeingRevoked && !userHasAdminSitePermissionThroughAtLeastOneOtherRole){
				throw new GallerySecurityException(I18nUtils.getMessage("Admin_Manage_Roles_Cannot_Save_Role_User_Would_Lose_Admin_Ability_Msg"));
			}

			if (adminGalleryPermissionBeingRevoked && !userHasAdminGalleryPermissionThroughAtLeastOneOtherRole)	{
				throw new GallerySecurityException(I18nUtils.getMessage("Admin_Manage_Roles_Cannot_Save_Role_User_Would_Lose_Admin_Ability_Msg"));
			}
		}
	}


	/// <summary>
	/// Don't let user delete the only role with Administer site permission. This should be called before a role is deleted as a validation step.
	/// </summary>
	/// <param name="roleToDelete">The role to be deleted.</param>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be deleted because doing so violates one of the business rules.</exception>
	private static void validatePreventLastSysAdminRoleDeletion(MDSRole roleToDelete) throws GallerySecurityException	{
		if (roleToDelete.getAllowAdministerSite()){
			// User is trying to delete a role with administer site permission. Make sure
			// at least one other role has this permission, and that the role has at least one member.
			boolean atLeastOneOtherRoleHasAdminSitePermission = false;
			for (MDSRole role : loadMDSRoles()){
				if (role.getRoleId() != roleToDelete.getRoleId() && role.getAllowAdministerSite()){
					if (getUsersInRole(role.getRoleId()).length > 0){
						atLeastOneOtherRoleHasAdminSitePermission = true;
						break;
					}
				}
			}

			if (!atLeastOneOtherRoleHasAdminSitePermission)	{
				throw new GallerySecurityException(I18nUtils.getMessage("admin.manageRoles.Cannot_Delete_Role_Msg"));
			}
		}
	}

	/// <summary>
	/// Don't let user delete a role with site admin or gallery admin permissions if that means the user will 
	/// lose their own administrative access. This should be called before a role is deleted as a validation step.
	/// </summary>
	/// <param name="roleToDelete">The role to be deleted.</param>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be deleted because doing so violates one of the business rules.</exception>
	private static void validatePreventLoggedOnUserFromLosingAdminAccess(MDSRole roleToDelete) throws GallerySecurityException, InvalidMDSRoleException{
		String roleName = roleToDelete.getRoleName();

		if (roleToDelete.getAllowAdministerSite() || roleToDelete.getAllowAdministerGallery()){
			boolean needToVerify = false;
			MDSRoleCollection roles = getMDSRolesForUser(UserUtils.getLoginName());
			for (MDSRole role : roles){
				if (role.getRoleName().equalsIgnoreCase(roleName)){
					needToVerify = true;
					break;
				}
			}

			if (needToVerify){
				// User is deleting a role he is a member of. Make sure user is in at least one other role with the same type of access.
				boolean userIsInAnotherRoleWithAdminAccess = false;
				if (roleToDelete.getAllowAdministerSite()){
					for (MDSRole role : roles){
						if (role.getAllowAdministerSite() && (!role.getRoleName().equalsIgnoreCase(roleName))){
							userIsInAnotherRoleWithAdminAccess = true;
							break;
						}
					}
				}else if (roleToDelete.getAllowAdministerGallery()){
					for (MDSRole role : roles){
						if (role.getAllowAdministerGallery() && (!role.getRoleName().equalsIgnoreCase(roleName))){
							userIsInAnotherRoleWithAdminAccess = true;
							break;
						}
					}
				}

				if (!userIsInAnotherRoleWithAdminAccess){
					throw new GallerySecurityException(I18nUtils.getMessage("Admin_Cannot_Delete_Role_Remove_Self_Admin_Msg"));
				}
			}
		}
	}

	/// <summary>
	/// Don't let user delete a role that affects any gallery where the user is not a site admin or gallery admin. This should be called before 
	/// a role is deleted as a validation step. The only exception is that we allow a user to delete an album owner role, since that will typically
	/// be assigned to a single album, and we have logic elsewhere that verifies the user has permission to delete the album.
	/// </summary>
	/// <param name="roleToDelete">The role to be deleted.</param>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be deleted because doing so violates one of the business rules.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="roleToDelete" /> is null.</exception>
	private static void validatePreventRoleDeletionAffectingOtherGalleries(MDSRole roleToDelete) throws GallerySecurityException, InvalidMDSRoleException	{
		if (roleToDelete == null)
			throw new ArgumentNullException("roleToDelete");

		if (isRoleAnAlbumOwnerRole(roleToDelete.getRoleName()))	{
			return;
		}

		GalleryBoCollection adminGalleries = UserUtils.getGalleriesCurrentUserCanAdminister();

		for (GalleryBo gallery : roleToDelete.getGalleries())	{
			if (!adminGalleries.contains(gallery))	{
				throw new GallerySecurityException(I18nUtils.getMessage("Admin_Cannot_Delete_Role_Insufficient_Permission_Msg", roleToDelete.getRoleName(), gallery.getDescription()));
			}
		}
	}

	private static void deleteADRole(String roleName){
		if (StringUtils.isBlank(roleName))
			return;

		if (roleExists(roleName, Long.MIN_VALUE))
			deleteRole(roleName, Long.MIN_VALUE); // This also deletes any user/role relationships
	}

	private static void deleteMDSRole(long roleId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, IOException, GallerySecurityException, InvalidGalleryException, WebException{
		MDSRole role = loadMDSRole(roleId);

		if (role != null){
			updateAlbumOwnerBeforeRoleDelete(role);
			role.delete();
		}
	}
	
	private static void deleteMDSRole(String roleName, long oId) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, IOException, GallerySecurityException, InvalidGalleryException, WebException{
		MDSRole role = loadMDSRole(roleName, oId);

		if (role != null){
			updateAlbumOwnerBeforeRoleDelete(role);
			role.delete();
		}
	}

	/// <summary>
	/// For roles that provide album ownership functionality, remove users belonging to this role from the OwnedBy 
	/// property of any albums this role is assigned to. Since we are deleting the role that provides the ownership
	/// functionality, it is necessary to clear the owner field of all affected albums.
	/// </summary>
	/// <param name="role">Name of the role to be deleted.</param>
	private static void updateAlbumOwnerBeforeRoleDelete(MDSRole role) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException{
		// Proceed only when dealing with an album ownership role.
		if (!isRoleAnAlbumOwnerRole(role.getRoleName()))
			return;

		// Loop through each album assigned to this role. If this role is assigned as the owner role,
		// clear the OwnerUserName property.
		for (long albumId : role.getRootAlbumIds()){
			// Load the album and clear the owner role name. Warning: Do not load the album with
			// AlbumUtils.loadAlbumInstance(), as it will create a recursive loop, leading to a stack overflow
			AlbumBo album = CMUtils.loadAlbumInstance(albumId, false, true, false);
			if (album.getOwnerRoleName() == role.getRoleName())	{
				album.setOwnerUserName(StringUtils.EMPTY);
				ContentObjectUtils.saveContentObject(album);
			}
		}
	}

	/// <summary>
	/// Creates the album owner role template. This is the role that is used as the template for roles that define
	/// a user's permission level when the user is assigned as an album owner. Call this method when the role does
	/// not exist. It is set up with all permissions except Administer Site and Administer Gallery. The HideWatermark 
	/// permission is not applied, so this role allows its members to view watermarks if that functionality is enabled.
	/// </summary>
	/// <param name="galleryId">The ID of the gallery for which the album owner template role is to belong.</param>
	/// <returns>
	/// Returns an <see cref="MDSRole"/> that can be used as a template for all album owner roles.
	/// </returns>
	/// <remarks>Note that we explicitly create the role in this method rather than call 
	/// <see cref="CreateRole(String, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, LongCollection)" />
	/// to avoid the validation that exists in that method. There may be times when an anonymous user is creating an account
	/// having a user album. If an album template role doesn't exist, this method will create the necessary template role.</remarks>
	private static MDSRole createAlbumOwnerRoleTemplate(long galleryId) throws InvalidMDSRoleException, InvalidGalleryException{
		synchronized (_sharedLock){
			String roleName = getAlbumOwnerTemplateRoleName(galleryId);

			long oId = UserUtils.getOwnerOrganizationId(galleryId);
			// Create the ASP.NET role.
			createRole(roleName, oId);

			// Create the MDS System role that extends the functionality of the ASP.NET role.
			MDSRole role = createMDSRoleInstance(Long.MIN_VALUE, roleName, RoleType.gu, oId, true, true, true, true, true, true, true, true, true, true, false, false, false);

			role.save();

			return role;
		}
	}

	/// <summary>
	/// Validates the album owner. If an album is being removed from the <paramref name="roleName"/> and that album is
	/// using this role for album ownership, remove the ownership setting from the album.
	/// </summary>
	/// <param name="roleName">Name of the role that is being modified.</param>
	/// <param name="rootAlbumIdsOld">The list of album ID's that were previously assigned to the role. If an album ID exists
	/// in this object and not in <paramref name="rootAlbumIdsNew"/>, that means the album is being removed from the role.</param>
	/// <param name="rootAlbumIdsNew">The list of album ID's that are now assigned to the role. If an album ID exists
	/// in this object and not in <paramref name="rootAlbumIdsOld"/>, that means it is a newly added album.</param>
	private static void validateAlbumOwnerRoles(String roleName, Collection<Long> rootAlbumIdsOld, Collection<Long> rootAlbumIdsNew) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, IOException, InvalidMDSRoleException, GallerySecurityException, InvalidGalleryException, WebException {
		for (long albumId : rootAlbumIdsOld){
			if (!rootAlbumIdsNew.contains(albumId))	{
				// Album has been removed from role. Remove owner from the album if the album owner role matches the one we are dealing with.
				AlbumBo album = AlbumUtils.loadAlbumInstance(albumId, false, true);
				if (album.getOwnerRoleName() == roleName){
					album.setOwnerUserName(StringUtils.EMPTY);
					ContentObjectUtils.saveContentObject(album);
				}
			}
		}
	}

	/// <summary>
	/// Create a role to manage the ownership permissions for the <paramref name="album"/> and user specified in the OwnerUserName
	/// property of the album. The permissions of the new role are copied from the album owner role template. The new role
	/// is persisted to the data store and the user specified as the album owner is added as its sole member. The album is updated
	/// so that the OwnerRoleName property contains the role's name, but the album is not persisted to the data store.
	/// </summary>
	/// <param name="album">The album for which a role to represent owner permissions is to be created.</param>
	/// <returns>Returns the name of the role that is created.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
	/// <exception cref="ArgumentException">Thrown when <paramref name="album" /> is new and has not yet been persisted to the data store.</exception>
	private static String createAlbumOwnerRole(AlbumBo album) throws InvalidMDSRoleException, InvalidGalleryException, WebException{
		// Create a role modeled after the template owner role, attach it to the album, then add the specified user as its member.
		// Role name: Album Owner - rdmartin - rdmartin's album (album 193)
		if (album == null)
			throw new ArgumentNullException("album");

		if (album.getIsNew())
			throw new ArgumentException("Album must be persisted to data store before calling RoleUtils.CreateAlbumOwnerRole.");

		String roleName = generateAlbumOwnerRoleName(album);
		long oId = UserUtils.getOwnerOrganization(album.getGalleryId()).getOrganizationId();

		if (!roleExists(roleName, oId))
			createRole(roleName, oId);

		if (!isUserInRole(album.getOwnerUserName(), roleName))
			addUserToRole(album.getOwnerUserName(), roleName);

		// Remove the roles from the cache. We do this because may may have just created a user album (that is, 
		// AlbumUtils.CreateUserAlbum() is in the call stack) and we want to make sure the AllAlbumIds property
		// of the album owner template role has the latest list of albums, including potentially the new album 
		// (which will be the case if the administrator has selected a parent album of the user album in the template
		// role).
		CacheUtils.remove(CacheItem.MDSRoles);

		MDSRole role = loadMDSRole(roleName, Long.MIN_VALUE);
		if (role == null){
			MDSRole roleSource = loadMDSRole(getAlbumOwnerTemplateRoleName(album.getGalleryId()), Long.MIN_VALUE);

			if (roleSource == null)
				roleSource = createAlbumOwnerRoleTemplate(album.getGalleryId());

			role = roleSource.copy();
			role.setRoleName(roleName);
		}

		if (!role.getAllAlbumIds().contains(album.getId()))
			role.getRootAlbumIds().add(album.getId());

		role.save();

		return roleName;
	}

	/// <summary>
	/// Generates the name of the album owner role. Some gymnastics are performed to ensure the length of the role name is less than the 
	/// maximum allowed.
	/// </summary>
	/// <param name="album">The album for which an album owner role is to be created.</param>
	/// <returns>Returns a role name whose length is less than or equal to a value defined in the function.</returns>
	private static String generateAlbumOwnerRoleName(AlbumBo album) throws WebException{
		final int maxRoleNameLength = 256;
		final int minAlbumTitleLength = 10;
		final String ellipse = "...";

		String roleNameTemplate = makeRoleNameUnique(StringUtils.format("{0} - {{UserName}} - {{AlbumTitle}} (album {1})", Constants.AlbumOwnerRoleNamePrefix, album.getId()));

		String albumTitle = album.getTitle().replace(",", StringUtils.EMPTY); // Commas are not allowed in a role name

		String roleName = roleNameTemplate.replace("{UserName}", album.getOwnerUserName()).replace("{AlbumTitle}", albumTitle);

		if (roleName.length() > maxRoleNameLength){
			// Role name is too long. Trim the album title and/or user name.
			String newAlbumTitle = albumTitle;
			String newUserName = album.getOwnerUserName();
			int numCharsToTrim = roleName.length() - maxRoleNameLength;
			int numCharsTrimmed = 0;

			if ((albumTitle.length() - numCharsToTrim) >= minAlbumTitleLength){
				// We can do all the trimming we need by shortening the album title.
				newAlbumTitle = StringUtils.join(albumTitle.substring(0, albumTitle.length() - numCharsToTrim - ellipse.length()), ellipse);
				numCharsTrimmed = numCharsToTrim;
			}else{
				// Trim max chars from album title while leaving minAlbumTitleLength chars left. We'll have to trim the username to 
				// get as short as we need.
				try{
					newAlbumTitle = StringUtils.join(albumTitle.substring(0, minAlbumTitleLength - ellipse.length()), ellipse);
					numCharsTrimmed = albumTitle.length() - newAlbumTitle.length();
				}
				catch (ArgumentOutOfRangeException ex) { }
			}

			if (numCharsTrimmed < numCharsToTrim){
				// We still need to shorten things up. Trim the user name.
				numCharsToTrim = numCharsToTrim - numCharsTrimmed;
				if (album.getOwnerUserName().length() > numCharsToTrim)	{
					newUserName = StringUtils.join(album.getOwnerUserName().substring(0, album.getOwnerUserName().length() - numCharsToTrim - ellipse.length()), ellipse);
				}else{
					// It is not expected we ever get to this path.
					throw new WebException(MessageFormat.format("Invalid role name length. Unable to shorten the album owner role name enough to satisfy maximum length restriction. Proposed name='{0}' (length={1}); Max length={2}", roleName, roleName.length(), maxRoleNameLength));
				}
			}

			roleName = roleNameTemplate.replace("{UserName}", newUserName).replace("{AlbumTitle}", newAlbumTitle);

			// Perform one last final check to ensure we shortened things up correctly.
			if (roleName.length() > maxRoleNameLength){
				throw new WebException(MessageFormat.format("Unable to shorten the album owner role name enough to satisfy maximum length restriction. Proposed name='{0}' (length={1}); Max length={2}", roleName, roleName.length(), maxRoleNameLength));
			}
		}

		return roleName;
	}

	/// <summary>
	/// Gets the name of the album owner template role. Example: "_Album Owner Template (Gallery ID 2: 'Engineering')"
	/// </summary>
	/// <param name="galleryId">The ID of the gallery to which the album owner template role is to belong.</param>
	/// <returns>Returns the name of the album owner template role.</returns>
	private static String getAlbumOwnerTemplateRoleName(long galleryId) throws InvalidGalleryException	{
		String galleryDescription = CMUtils.loadGallery(galleryId).getDescription();

		if (galleryDescription.length() > 100){
			// Too long - shorten up... (role name can be only 256 chars)
			galleryDescription = StringUtils.join(galleryDescription.substring(0, 100), "...");
		}

		// Note: If you change this, be sure to update _mdsAlbumOwnerTemplateRoleNameRegExPattern to that it will match!
		return makeRoleNameUnique(MessageFormat.format("{0} (Gallery ID {1}: '{2}')", Constants.AlbumOwnerRoleTemplateName, galleryId, galleryDescription));
	}

	private static String getCacheKeyNameForRoles(String userName){
		return StringUtils.join(((WebAuthenticationDetails)UserUtils.getSession().getDetails()).getSessionId(), "_", userName, "_Roles");
	}

	/// <summary>
	/// Verify data integrity after removing a user from one or more roles. Specifically, if a role is an album owner role, 
	/// then check all albums in that role to see if current user is an owner for any. If he is, clear out the ownership field.
	/// </summary>
	/// <param name="userName">Name of the user who was removed from one or more roles.</param>
	/// <param name="roleNames">The names of the roles the user were removed from.</param>
	private static void validateRemoveUserFromRole(String userName, Collection<String> roleNames, long oId) throws InvalidMDSRoleException, InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidGalleryException, WebException{
		if (StringUtils.isBlank(userName))
			return;

		if (roleNames == null)
			return;

		for (String roleName : roleNames){
			MDSRole role = loadMDSRole(roleName, oId);
			if (role == null){
				// Normally shouldn't be null, but might be if role has been deleted outside MDS.
				continue;
			}
			
			if (isRoleAnAlbumOwnerRole(role.getRoleName())){
				
				for (long albumId : role.getRootAlbumIds())	{
					AlbumBo album = AlbumUtils.loadAlbumInstance(albumId, false, true);
					if (album.getOwnerUserName().equalsIgnoreCase(userName )){
						album.setOwnerUserName(StringUtils.EMPTY);
						ContentObjectUtils.saveContentObject(album);
					}
				}
			}
		}
	}
	
	private static void validateRemoveUserFromRole(String userName, Collection<Long> roleIds) throws InvalidMDSRoleException, InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, InvalidGalleryException, WebException{
		if (StringUtils.isBlank(userName))
			return;

		if (roleIds == null)
			return;

		for (long roleId : roleIds){
			MDSRole role = loadMDSRole(roleId);
			if (role == null){
				// Normally shouldn't be null, but might be if role has been deleted outside MDS.
				continue;
			}
			
			if (isRoleAnAlbumOwnerRole(role.getRoleName())){
				
				for (long albumId : role.getRootAlbumIds())	{
					AlbumBo album = AlbumUtils.loadAlbumInstance(albumId, false, true);
					if (album.getOwnerUserName().equalsIgnoreCase(userName )){
						album.setOwnerUserName(StringUtils.EMPTY);
						ContentObjectUtils.saveContentObject(album);
					}
				}
			}
		}
	}

	/// <summary>
	/// Replace the list of root album IDs for the <paramref name="role"/> with the album ID's specified in
	/// <paramref name="topLevelCheckedAlbumIds"/>. Note that this function will cause the AllAlbumIds property 
	/// to be cleared out (Count = 0). The property can be repopulated by calling <see cref="MDSRole.Save"/>.
	/// </summary>
	/// <param name="role">The role whose root album/role relationships should be updated. When editing
	/// an existing role, specify this.GalleryRole. For new roles, pass the newly created role before
	/// saving it.</param>
	/// <param name="topLevelCheckedAlbumIds">The top level album IDs. May be null.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="role" /> is null.</exception>
	private static void updateRoleAlbumRelationships(MDSRole role, LongCollection topLevelCheckedAlbumIds) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		if (role == null)
			throw new ArgumentNullException("role");

		if (topLevelCheckedAlbumIds == null)
			topLevelCheckedAlbumIds = new LongCollection();

		//Long[] rootAlbumIdsOld = role.getRootAlbumIds().copyFromIndex(0);
		List<Long> rootAlbumIdsOld = role.getRootAlbumIds().subList(0);

		role.getRootAlbumIds().clear();

		if (role.getAllowAdministerSite()){
			// Administer site permission automatically applies to all albums, so all we need to do is get
			// a reference to the root album ID in each gallery.
			for (GalleryBo gallery : role.getGalleries()){
				role.getRootAlbumIds().add(CMUtils.loadRootAlbumInstance(gallery.getGalleryId()).getId());
			}
		}else if (role.getAllowAdministerGallery()){
			// Administer gallery permission automatically applies to all albums in a gallery, so get a reference
			// to the root album for each checked album ID.
			for (long albumId : topLevelCheckedAlbumIds){
				AlbumBo album = AlbumUtils.loadAlbumInstance(albumId, false);

				while (!(album.getParent() instanceof NullContentObject)){
					album = (AlbumBo)album.getParent();
				}

				if (!role.getRootAlbumIds().contains(album.getId())){
					role.getRootAlbumIds().add(album.getId());
				}
			}
		}else{
			role.getRootAlbumIds().addRange(topLevelCheckedAlbumIds);
		}

		if (isRoleAnAlbumOwnerRole(role.getRoleName()))
			validateAlbumOwnerRoles(role.getRoleName(), rootAlbumIdsOld, role.getRootAlbumIds());
	}

	/// <summary>
	/// Determines whether the user has permission to view the specified role. Determines this by checking
	/// whether the logged on user is a site administrator or a gallery administrator for at least
	/// one gallery associated with the role. If the role is not assigned to any albums, it verifies the user is 
	/// a gallery admin to at least one gallery (doesn't matter which one).
	/// </summary>
	/// <param name="role">The role to evaluate.</param>
	/// <returns><c>true</c> if the user has permission to edit the specified role; otherwise <c>false</c>.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="role" /> is null.</exception>
	private static boolean userCanViewRole(MDSRole role) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException{
		if (role == null)
			throw new ArgumentNullException("role");

		if (UserUtils.isCurrentUserSysAdministrator())	{
			return true;
		}

		if (role.getGalleries().isEmpty())	{
			// The role isn't assigned to any albums, so let's make sure the user is a gallery admin to at
			// least one gallery.
			return (GalleryUtils.getGalleriesCurrentUserCanAdminister().size() > 0);
		}else{
			return isUserGalleryAdminForRole(role);
		}
	}

	/// <summary>
	/// Determines whether the user has permission to edit the specified role. Determines this by checking
	/// whether the logged on user is a site administrator or a gallery administrator for every
	/// gallery associated with the role. If the role is not assigned to any albums, it verifies the user is
	/// a gallery admin to at least one gallery (doesn't matter which one).
	/// </summary>
	/// <param name="roleToSave">The role to save. It's role name must match the role name of <paramref name="existingRole" />.</param>
	/// <param name="existingRole">The existing role, as it is stored in the database. It's role name must match the role name of
	/// <paramref name="roleToSave" />.</param>
	/// <param name="rolesForCurrentUser">The roles for current user.</param>
	/// <exception cref="GallerySecurityException">Thrown when the role cannot be saved because doing so would violate a business rule.</exception>
	private static void validateUserCanEditRole(MDSRole roleToSave, MDSRole existingRole, MDSRoleCollection rolesForCurrentUser) throws GallerySecurityException, UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException{
		if (UserUtils.isCurrentUserSysAdministrator()){
			return;
		}

		if (roleToSave.getGalleries().isEmpty()){
			// The role isn't assigned to any albums, so let's make sure the user is a gallery admin to at
			// least one gallery.
			if (GalleryUtils.getGalleriesCurrentUserCanAdminister().isEmpty())	{
				throw new GallerySecurityException("Your account does not have permission to make changes to roles.");
			}
		}

		if (existingRole.getGalleries().stream().anyMatch(gallery -> {
			try {
				return !UserUtils.isUserGalleryAdministrator(rolesForCurrentUser, gallery.getGalleryId());
			} catch (UnsupportedContentObjectTypeException | InvalidAlbumException | InvalidGalleryException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return false;
			}
		})){
			throw new GallerySecurityException(I18nUtils.getMessage("Admin_Manage_Roles_Cannot_Delete_Role_Insufficient_Permission_Msg2"));
		}
	}

	/// <summary>
	/// Determines whether the logged on user has gallery admin permissions for at least one gallery associated with the
	/// <paramref name="role" />.
	/// </summary>
	/// <param name="role">The role to evaluate.</param>
	/// <returns>
	/// 	<c>true</c> if the logged on user has gallery admin permissions for at least one galley associated with the
	/// <paramref name="role" />; otherwise, <c>false</c>.
	/// </returns>
	private static boolean isUserGalleryAdminForRole(MDSRole role) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException{
		for (GalleryBo gallery : role.getGalleries()){
			if (UserUtils.isCurrentUserGalleryAdministrator(gallery.getGalleryId())){
				return true;
			}
		}

		return false;
	}

	/// <summary>
	/// Determines whether the logged on user is a gallery admin for the album owner template role specified in <paramref name="role" />.
	/// This is done by verifying that the gallery ID specified in the role's name is for a gallery the user can administer.
	/// Returns <c>true</c> when the role is an album owner template role and the current user is a gallery admin for it's
	/// associated gallery; otherwise returns <c>false</c>.
	/// </summary>
	/// <param name="role">The role to evaluate. It is expected that the role is an album owner template role, but this is
	/// not a requirement (function always returns false for non-template roles).</param>
	/// <returns>
	/// 	Returns <c>true</c> when the role is an album owner template role and the current user is a gallery admin for it's
	/// associated gallery; otherwise returns <c>false</c>.
	/// </returns>
	private static boolean isUserGalleryAdminForAlbumOwnerTemplateRole(MDSRole role) throws InvalidMDSRoleException{
		Matcher  match = _mdsAlbumOwnerTemplateRoleNameRegEx.matcher(role.getRoleName());
		if (match.matches()){
			// Parse out the gallery ID from the role name. Ex: "_Album Owner Template (Gallery ID 723: My gallery)" yields "723"
			long galleryId = StringUtils.toLong(match.group(1)); //match.["galleryId"].Value

			GalleryBo gallery = null;
			try	{
				gallery = CMUtils.loadGallery(galleryId);
			}catch (InvalidGalleryException ge) { }

			if ((gallery != null) && GalleryUtils.getGalleriesCurrentUserCanAdminister().contains(gallery))	{
				return true;
			}
		}

		return false;
	}

	//#endregion
}
