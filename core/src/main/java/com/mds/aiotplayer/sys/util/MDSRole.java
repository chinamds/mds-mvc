/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.content.GalleryBoCollection;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.LongCollection;
import com.mds.aiotplayer.core.LongCollectionEvent;
import com.mds.aiotplayer.core.LongCollectionListener;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;
import com.mds.aiotplayer.sys.model.RoleType;

/// <summary>
/// Represents a role that encapsulates a set of permissions for one or more albums in MDS System. Each user
/// is assigned to zero or more roles.
/// </summary>
public class MDSRole implements Comparable<MDSRole>, LongCollectionListener{
	//#region Private Fields

	private long roleId;
	private String roleName;
	private RoleType roleType; // role type
	private boolean allowViewAlbumOrContentObject;
	private boolean allowViewOriginalImage;
	private boolean allowAddContentObject;
	private boolean allowAddChildAlbum;
	private boolean allowEditContentObject;
	private boolean allowEditAlbum;
	private boolean allowDeleteContentObject;
	private boolean allowApproveContentObject;
	private boolean allowDeleteChildAlbum;
	private boolean allowSynchronize;
	private boolean allowAdministerSite;
	private boolean allowAdministerGallery;
	private boolean hideWatermark;

	private GalleryBoCollection galleries;
	private LongCollection rootAlbumIds;
	private LongCollection allAlbumIds;
	private LongCollection organizationIds;
	private long organizationId=Long.MIN_VALUE; //Owner organization Id
	private Map<Long, List<Pair<Long, Long>>> menuPermissions;

	//#endregion

	//#region Public Properties

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	/// <summary>
	/// Gets or sets a String that uniquely identifies the role.
	/// </summary>
	/// <value>The name of the role.</value>
	public String getRoleName()	{
		return this.roleName;
	}
	
	public void setRoleName(String roleName){
		this.roleName = roleName;
	}
	
	public RoleType getRoleType()	{
		return this.roleType;
	}
	
	public void setRoleType(RoleType roleType){
		this.roleType = roleType;
	}
	
	public LongCollection getOrganizationIds()	{
		return this.organizationIds;
	}
	
	public void setOrganizationIds(LongCollection organizationIds){
		this.organizationIds = organizationIds;
	}
	
	public long getOrganizationId()	{
		return this.organizationId;
	}
	
	public void setOrganizationId(long organizationId){
		this.organizationId = organizationId;
	}
	
	public Map<Long, List<Pair<Long, Long>>> getMenuPermissions(){
		return this.menuPermissions;
	}
	
	public void setMenuPermissions(Map<Long, List<Pair<Long, Long>>> menuPermissions){
		this.menuPermissions = menuPermissions;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to view albums and content objects.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to view albums and content objects; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowViewAlbumOrContentObject(){
		return this.allowViewAlbumOrContentObject;
	}
	
	public void setAllowViewAlbumOrContentObject(boolean allowViewAlbumOrContentObject){
		this.allowViewAlbumOrContentObject = allowViewAlbumOrContentObject;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to view the original,
	/// high resolution version of an image. This setting applies only to images. It has no effect if there are no
	/// high resolution images in the album or albums to which this role applies.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to view the original,
	/// high resolution version of an image; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowViewOriginalImage(){
		return this.allowViewOriginalImage;
	}
	
	public void setAllowViewOriginalImage(boolean allowViewOriginalImage){
		this.allowViewOriginalImage = allowViewOriginalImage;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to add content objects to an album.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to add content objects to an album; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowAddContentObject(){
		return this.allowAddContentObject;
	}
	
	public void setAllowAddContentObject(boolean allowAddContentObject){
		this.allowAddContentObject = allowAddContentObject;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to create child albums.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to create child albums; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowAddChildAlbum(){
		return this.allowAddChildAlbum;
	}
	
	public void setAllowAddChildAlbum(boolean allowAddChildAlbum){
		this.allowAddChildAlbum = allowAddChildAlbum;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to edit a content object.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to edit a content object; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowEditContentObject(){
		return this.allowEditContentObject;
	}
	
	public void setAllowEditContentObject(boolean allowEditContentObject){
		this.allowEditContentObject = allowEditContentObject;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to edit an album.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to edit an album; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowEditAlbum(){
		return this.allowEditAlbum;
	}
	
	public void setAllowEditAlbum(boolean allowEditAlbum){
		this.allowEditAlbum = allowEditAlbum;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to delete content objects within an album.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to delete content objects within an album; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowDeleteContentObject(){
		return this.allowDeleteContentObject;
	}
	
	public void setAllowDeleteContentObject(boolean allowDeleteContentObject){
		this.allowDeleteContentObject = allowDeleteContentObject;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to approve content objects within an album.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to approve content objects within an album; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowApproveContentObject(){
		return this.allowApproveContentObject;
	}
	
	public void setAllowApproveContentObject(boolean allowApproveContentObject){
		this.allowApproveContentObject = allowApproveContentObject;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to delete child albums.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to delete child albums; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowDeleteChildAlbum(){
		return this.allowDeleteChildAlbum;
	}
	
	public void setAllowDeleteChildAlbum(boolean allowDeleteChildAlbum){
		this.allowDeleteChildAlbum = allowDeleteChildAlbum;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has permission to synchronize an album.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has permission to synchronize an album; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowSynchronize(){
		return this.allowSynchronize;
	}
	
	public void setAllowSynchronize(boolean allowSynchronize){
		this.allowSynchronize = allowSynchronize;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user has administrative permission for all albums in the gallery
	/// associated with this role. This permission automatically applies to all albums in the gallery; it cannot be
	/// selectively applied.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user has administrative permission for all albums in the gallery associated with
	/// this role; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowAdministerGallery(){
		return this.allowAdministerGallery;
	}
	
	public void setAllowAdministerGallery(boolean allowAdministerGallery){
		this.allowAdministerGallery = allowAdministerGallery;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user has administrative permission for all albums. This permission
	/// automatically applies to all albums; it cannot be selectively applied.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user has administrative permission for all albums; otherwise, <c>false</c>.
	/// </value>
	public boolean getAllowAdministerSite()	{
		return this.allowAdministerSite;
	}
	
	public void setAllowAdministerSite(boolean allowAdministerSite)	{
		this.allowAdministerSite = allowAdministerSite;
	}

	/// <summary>
	/// Gets or sets a value indicating whether the user assigned to this role has a watermark applied to images.
	/// This setting has no effect if watermarks are not used. A true value means the user does not see the watermark;
	/// a false value means the watermark is applied.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if the user assigned to this role has a watermark applied to images; otherwise, <c>false</c>.
	/// </value>
	public boolean getHideWatermark(){
		return this.hideWatermark;
	}
	
	public void setHideWatermark(boolean hideWatermark){
		this.hideWatermark = hideWatermark;
	}

	/// <summary>
	/// Gets the list of all galleries to which this role applies. This property is dynamically populated based on the
	/// albums in the <see cref="RootAlbumIds"/> property. Calling the Save() method automatically reloads this
	/// property from the data store.
	/// </summary>
	/// <value>The list of all galleries to which this role applies.</value>
	public GalleryBoCollection getGalleries(){
		return this.galleries;
	}

	/// <summary>
	/// Gets the list of all top-level album IDs for which this role applies. Does not include any descendents
	/// of the album. Setting this property causes the AllAlbumIds property to be cleared out (Count = 0) since a different
	/// list of root album IDs implies the exploded list is also different. Validation code in the AllAlbumIds getter
	/// will throw an exception if it is called after it has been cleared. The AllAlbumIds property is automatically reloaded
	/// from the data store during Save(). Note that adding or removing items to this list does not cause AllAlbumIds to
	/// be cleared out, although calling Save() will still reload the list from the data store.
	/// </summary>
	/// <value>The list of all top-level album IDs for which this role applies.</value>
	public LongCollection getRootAlbumIds(){
		return this.rootAlbumIds;
		//set 
		//{
		//  // Clear out the list of all album IDs if setting this property after it was previously set, since a different
		//  // list of root album IDs implies the exploded list is also different. Validation code in the AllAlbumIds getter
		//  // will throw an exception if it is called after it has been cleared.
		//  if ((this.rootAlbumIds != null) && (this.rootAlbumIds.Count > 0))
		//  {
		//    this.allAlbumIds.Clear();
		//  }

		//  this.rootAlbumIds = value; 
		//}
	}

	/// <summary>
	/// Gets the list of all album IDs for which this role applies. Includes all descendents of all applicable albums.
	/// Calling the Save() method automatically reloads this property from the data store.
	/// </summary>
	/// <value>The list of all album IDs for which this role applies.</value>
	/// <exception cref="BusinessException">Thrown when <see cref="RootAlbumIds"/> has more than one item but the internal
	/// field for this property (this.allAlbumIds) is empty.</exception>
	public LongCollection getAllAlbumIds(){
		if ((this.allAlbumIds.size() == 0) && (this.rootAlbumIds.size() > 0))	{
			throw new BusinessException(I18nUtils.getMessage("role.allAlbumIds_Ex_Msg"));
		}

		return this.allAlbumIds;
		//this.allAlbumIds = value;
	}

	//#endregion

	//#region Constructors

	private MDSRole() { } // Hide default constructor

	/// <summary>
	/// Create a MDSRole instance corresponding to the specified parameters. Throws an exception if a role with the
	/// specified name already exists in the data store.
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
	/// <param name="allowApproveContentObject">A value indicating whether the user assigned to this role has permission to approve content objects within an album.</param>
	/// <param name="allowDeleteChildAlbum">A value indicating whether the user assigned to this role has permission to delete child albums.</param>
	/// <param name="allowSynchronize">A value indicating whether the user assigned to this role has permission to synchronize an album.</param>
	/// <param name="allowAdministerSite">A value indicating whether the user has administrative permission for all albums. This permission
	/// automatically applies to all albums across all galleries; it cannot be selectively applied.</param>
	/// <param name="allowAdministerGallery">A value indicating whether the user has administrative permission for all albums. This permission
	/// automatically applies to all albums in a particular gallery; it cannot be selectively applied.</param>
	/// <param name="hideWatermark">A value indicating whether the user assigned to this role has a watermark applied to images.
	/// This setting has no effect if watermarks are not used. A true value means the user does not see the watermark;
	/// a false value means the watermark is applied.</param>
	/// <returns>Returns a MDSRole instance corresponding to the specified parameters.</returns>
	public MDSRole(long roleId, String roleName, RoleType roleType, long oId, boolean allowViewAlbumOrContentObject, boolean allowViewOriginalImage, boolean allowAddContentObject, 
			boolean allowAddChildAlbum, boolean allowEditContentObject, boolean allowEditAlbum, boolean allowDeleteContentObject, boolean allowApproveContentObject, 
			boolean allowDeleteChildAlbum, boolean allowSynchronize, boolean allowAdministerSite, boolean allowAdministerGallery, boolean hideWatermark){
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleType = roleType;
		this.organizationId = oId;
		this.allowViewAlbumOrContentObject = allowViewAlbumOrContentObject;
		this.allowViewOriginalImage = allowViewOriginalImage;
		this.allowAddContentObject = allowAddContentObject;
		this.allowAddChildAlbum = allowAddChildAlbum;
		this.allowEditContentObject = allowEditContentObject;
		this.allowEditAlbum = allowEditAlbum;
		this.allowDeleteContentObject = allowDeleteContentObject;
		this.allowApproveContentObject = allowApproveContentObject;
		this.allowDeleteChildAlbum = allowDeleteChildAlbum;
		this.allowSynchronize = allowSynchronize;
		this.allowAdministerSite = allowAdministerSite;
		this.allowAdministerGallery = allowAdministerGallery;
		this.hideWatermark = hideWatermark;

		this.galleries = new GalleryBoCollection();

		this.rootAlbumIds = new LongCollection();
		//.Cleared += rootAlbumIds_Cleared;
		this.rootAlbumIds.addLongCollectionListener(this);

		this.allAlbumIds = new LongCollection();
	}

	//#endregion

	//#region Event Handlers

	public void cleared(LongCollectionEvent e){
		// We need to smoke the all albums list whenever the list of root albums has been cleared.
		if (this.allAlbumIds != null)
			this.allAlbumIds.clear();
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Populate the <see cref="AllAlbumIds"/> and <see cref="Galleries"/> properties based on the contents of
	/// <see cref="RootAlbumIds"/> and the flattened list of album IDs in <paramref name="galleries"/>.
	/// </summary>
	/// <param name="galleries">A list of all galleries in the current application. The <see cref="Gallery.Albums"/>
	/// property is used as a source for populating the <see cref="AllAlbumIds"/> and <see cref="Galleries"/> properties
	/// of the current instance.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="galleries" /> is null.</exception>
	public void inflate(GalleryBoCollection galleries){
		if (galleries == null)
			throw new ArgumentNullException("galleries");

		// For each root album, get the list of flattened album IDs from the gallery (we don't know which gallery, so
		// iterate through them until you find the right one).
		for (long albumId : this.rootAlbumIds){
			for (GalleryBo gallery : galleries)	{
				List<Long> albumIds;
				if ((albumIds = gallery.getAlbums().get(albumId)) != null){
					this.addToAllAlbumIds(albumIds);

					// If we haven't yet added this gallery, do so now.
					if (!this.galleries.contains(gallery)){
						this.galleries.add(gallery);
					}

					break;
				}
			}
		}
	}

	/// <summary>
	/// Add the specified album to the list of all album IDs. This is used by data and business layer code to
	/// populate the list when it is instantiated or saved.
	/// </summary>
	/// <param name="albumId">The ID that uniquely identifies the album to add to the list.</param>
	public void addToAllAlbumIds(long albumId){
		this.allAlbumIds.add(albumId);
	}

	/// <summary>
	/// Add the specified albums to the list of all album IDs. This is used by data and business layer code to
	/// populate the list when it is instantiated or saved.
	/// </summary>
	/// <param name="albumIds">The IDs that uniquely identify the albums to add to the list.</param>
	public void addToAllAlbumIds(Iterable<Long> albumIds){
		this.allAlbumIds.addRange(albumIds);
	}

	/// <summary>
	/// Clears the list of album IDs stored in the <see cref="AllAlbumIds"/> property.
	/// </summary>
	public void clearAllAlbumIds(){
		this.allAlbumIds.clear();
	}
	
	public void addMenuPermissions(List<MenuFunctionPermission> menuFunctionPermissions){
		this.menuPermissions = new HashMap<Long, List<Pair<Long, Long>>>();
		for (MenuFunctionPermission menuFunctionPermission : menuFunctionPermissions) {
			Pair<Long, Long> menuPermission = new ImmutablePair<Long, Long>(menuFunctionPermission.getId(), menuFunctionPermission.getPermission().getId());
			if (!this.menuPermissions.containsKey(menuFunctionPermission.getMenuFunction().getId())){
				this.menuPermissions.put(menuFunctionPermission.getMenuFunction().getId(), Lists.newArrayList());
			}
			this.menuPermissions.get(menuFunctionPermission.getMenuFunction().getId()).add(menuPermission);
		}
	}
	
	public List<Pair<Long, Long>> getMenuPermissions(long menuFuncionId){
		if (this.menuPermissions != null)
			return this.menuPermissions.get(menuFuncionId);
		
		return null;
	}

	/// <summary>
	/// Persist this MDS System role to the data store. The list of top-level albums this role applies to, which is stored
	/// in the <see cref="RootAlbumIds"/> property, is also saved. If <see cref="RootAlbumIds"/> was modified, the caller must
	/// repopulate the <see cref="AllAlbumIds"/> and <see cref="Galleries"/> properties.
	/// </summary>
	public void save(){
		//Factory.GetDataProvider().Role_Save(this);
	  /*using (var repo = new RoleRepository())
	  {
		repo.Save(this);
	  }*/
	}

	/// <summary>
	/// Permanently delete this MDS System role from the data store, including the list of role/album relationships
	/// associated with this role.
	/// </summary>
/// <remarks>This procedure only deletes it from the custom MDS System tables,
/// not the ASP.NET role membership table(s). The web application code that invokes this procedure also
/// uses the standard ASP.NET technique to delete the role from the membership table(s).</remarks>
	public void delete(){
		//Factory.GetDataProvider().Role_Delete(this);
	 /* using (var repo = new RoleRepository())
	  {
		var roleDto = repo.Find(RoleName);
		if (roleDto != null)
		{
		  repo.Delete(roleDto);
		  repo.Save();
		}
	  }*/
	}

	/// <summary>
	/// Creates a deep copy of this instance, including the Galleries, RootAlbumIds and AllAlbumIds properties.
	/// </summary>
	/// <returns>Returns a deep copy of this instance.</returns>
	public MDSRole copy() throws InvalidMDSRoleException {
		MDSRole role = RoleUtils.createMDSRoleInstance(Long.MIN_VALUE, roleName, roleType, organizationId, allowViewAlbumOrContentObject, allowViewOriginalImage,
																	allowAddContentObject, allowAddChildAlbum, allowEditContentObject, allowEditAlbum,
																	allowDeleteContentObject, allowApproveContentObject, allowDeleteChildAlbum, allowSynchronize,
																	allowAdministerSite, allowAdministerGallery, hideWatermark);
		//role.setRoleName(roleName);
		role.setRoleId(roleId);
		
		for (GalleryBo gallery : galleries)	{
			role.galleries.add(gallery.copy());
		}

		role.allAlbumIds.addRange(allAlbumIds);
		role.rootAlbumIds.addRange(rootAlbumIds);
		role.organizationIds.addRange(organizationIds);
		for(long menuId : menuPermissions.keySet()) {
			role.menuPermissions.put(menuId, Lists.newArrayList(menuPermissions.get(menuId)));
		}

		return role;
	}

	/// <summary>
	/// Verify the role conforms to business rules. Specificially, if the role has administrative permissions
	/// (AllowAdministerSite = true or AllowAdministerGallery = true):
	/// 1. Make sure the role permissions - except HideWatermark - are set to true.
	/// 2. Make sure the root album IDs are a list containing the root album ID for each affected gallery.
	/// If anything needs updating, update the object and persist the changes to the data store. This helps keep the data store
	/// valid in cases where the user is directly editing the tables (for example, adding/deleting records from the gs_Role_Album table).
	/// </summary>
	public void validateIntegrity() {
		if (allowAdministerSite || this.roleType == RoleType.sa){ // || allowAdministerGallery
			validateAdminRoleIntegrity();
		}
	}

	//#endregion

	//#region Private Methods

	/// <summary>
	/// Verify the administrative role contains a list of the root albums for every affected gallery. This corrects potential data integrity
	/// situations, such as when a developer modifies the gs_Role_Album table to give a site administrator access to a child album in a 
	/// gallery. Since site admins, by definition, have permission to ALL albums in ALL galleries, we want to make sure the list of albums
	/// we are storing reflect this. Any problems with integrity are automatically corrected and persisted to the data store.
	/// </summary>
	private void validateAdminRoleIntegrity(){
		// Test 1: Make sure all role permissions - except HideWatermark - are set to true.
		boolean hasChanges = validateRoleAdminPermissions();

		// Test 2: Since admins always have complete access to all albums in a gallery (and site admins have access to all albums
		// in every gallery), admin roles should be assigned the root album for each relevant gallery. We verify this by getting the 
		// root album ID for each relevant gallery and then comparing them to the ones assigned to the role. If they are different, 
		// we update them and save.

		// Step 1: Get the list of root album IDs relevant to the role.
		List<Long> rootAlbumIds = getRootAlbumIdsForRole();

		// Step 2: Determine if the list of root album IDs is different than the list assigned to the role.
		boolean rootAlbumsCountIsDifferent = (this.rootAlbumIds.size() != rootAlbumIds.size());
		boolean roleHasMissingAlbumId = false;

		for (long albumId : rootAlbumIds){
			if (!this.rootAlbumIds.contains(albumId)){
				roleHasMissingAlbumId = true;
				break;
			}
		}

		if (rootAlbumsCountIsDifferent || roleHasMissingAlbumId){
			// Step 3: When the list is different, update the list assigned to the role.
			this.rootAlbumIds.clear();
			this.rootAlbumIds.addAll(rootAlbumIds);
			hasChanges = true;
		}

		// Step 4: Save changes if needed.
		if (hasChanges){
			save();
			clearAllAlbumIds();
			inflate(CMUtils.loadGalleries());
		}
	}

	/// <summary>
	/// Verifies that admin roles have all applicable permissions, returning a value indicating whether any properties were updated. 
	/// Specifically, admin roles should have most sub permissions, such as adding and editing content objects. Does not modify the 
	/// "hide watermark" permission. The changes are made to the object but not persisted to the data store.
	/// </summary>
	/// <returns><c>true</c> if one or more properties were updated; otherwise <c>false</c>.</returns>
	private boolean validateRoleAdminPermissions(){
		boolean hasChanges = false;

		if (allowAdministerSite || this.roleType == RoleType.sa){ //|| allowAdministerGallery
			if (!allowAddChildAlbum){
				allowAddChildAlbum = true;
				hasChanges = true;
			}
			if (!allowAddContentObject){
				allowAddContentObject = true;
				hasChanges = true;
			}
			if (!allowDeleteChildAlbum)	{
				allowDeleteChildAlbum = true;
				hasChanges = true;
			}
			if (!allowDeleteContentObject){
				allowDeleteContentObject = true;
				hasChanges = true;
			}
			if (!allowEditAlbum){
				allowEditAlbum = true;
				hasChanges = true;
			}
			if (!allowEditContentObject) {
				allowEditContentObject = true;
				hasChanges = true;
			}
			if (!allowSynchronize){
				allowSynchronize = true;
				hasChanges = true;
			}
			if (!allowViewAlbumOrContentObject)	{
				allowViewAlbumOrContentObject = true;
				hasChanges = true;
			}
			if (!allowViewOriginalImage){
				allowViewOriginalImage = true;
				hasChanges = true;
			}
		}

		if (allowAdministerSite){
			// Site admins are also gallery admins.
			if (!allowAdministerGallery){
				allowAdministerGallery = true;
				hasChanges = true;
			}
		}

		return hasChanges;
	}

	/// <summary>
	/// Gets a list of album IDs at the top of each gallery associated with the role. Returns values only when the
	/// role has a permission that affects an entire gallery (example: site admin or gallery admin). The IDs can be used to validate
	/// that the list of album IDs assigned to the role are stored in the most efficient manner.
	/// </summary>
	/// <returns>Returns a list of album IDs at the top of each gallery associated with the role.</returns>
	private List<Long> getRootAlbumIdsForRole()
	{
		List<Long> rootAlbumIds = new ArrayList<Long>(1);

		if (allowAdministerSite || this.roleType == RoleType.sa) {
			// Site admins have permission to every gallery, so get root album ID of every gallery.
			List<GalleryBo> galleries = CMUtils.loadGalleries();
			for (GalleryBo gallery : galleries){
				rootAlbumIds.add(gallery.getRootAlbumId());
			}
		}else if (allowAdministerGallery|| this.roleType == RoleType.ga){
			// Loop through each album ID associated with this role. Add the root album for each to our list, but don't duplicate any.
			List<GalleryBo> galleries = CMUtils.loadGalleries();
			for (long topAlbumId : getRootAlbumIds()){
				for(GalleryBo gallery : galleries){
					if (gallery.getAlbums().containsKey(topAlbumId) && (!rootAlbumIds.contains(gallery.getRootAlbumId()))){
						rootAlbumIds.add(gallery.getRootAlbumId());
						break;
					}
				}
			}
		}

		return rootAlbumIds;
	}


	//#endregion

	//#region IComparable Members

	/// <summary>
	/// Compares the current instance with another object of the same type.
	/// </summary>
	/// <param name="obj">An object to compare with this instance.</param>
	/// <returns>
	/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has these meanings: 
	/// Less than zero: This instance is less than <paramref name="obj"/>. Zero: This instance is equal to <paramref name="obj"/>. 
	/// Greater than zero: This instance is greater than <paramref name="obj"/>.
	/// </returns>
	/// <exception cref="T:System.ArgumentException">
	/// 	<paramref name="obj"/> is not the same type as this instance. </exception>
	public int compareTo(MDSRole obj){
		// Sort by role name.
		if (obj == null)
			return 1;
		else{
			return this.roleName.compareToIgnoreCase(obj.getRoleName());
		}
	}

	/// <summary>
	/// Serves as a hash function for a particular type.
	/// </summary>
	/// <returns>
	/// A hash code for the current <see cref="MDSRole"/>.
	/// </returns>
	@Override
	public int hashCode(){
		return   new  HashCodeBuilder( 17 ,  37 )
	             .append(roleName)
	             .append(roleType)
	             .append(allAlbumIds)
	             .append(allowAddChildAlbum)
	             .append(allowAddContentObject)
	             .append(allowAdministerSite)
	             .append(allowDeleteChildAlbum)
	             .append(allowDeleteContentObject)
	             .append(allowEditAlbum)
	             .append(allowEditContentObject)
	             .append(allowSynchronize)
	             .append(allowViewAlbumOrContentObject)
	             .append(allowViewOriginalImage)
	             .append(hideWatermark)
	             .append(allowAdministerGallery)
	             .toHashCode();
	}

	//#endregion

	/// <summary>
	/// Determines whether the specified <see cref="System.Object"/> is equal to this instance.
	/// </summary>
	/// <param name="obj">The <see cref="System.Object"/> to compare with this instance.</param>
	/// <returns>
	/// 	<c>true</c> if the specified <see cref="System.Object"/> is equal to this instance; otherwise, <c>false</c>.
	/// </returns>
	/// <exception cref="T:System.NullReferenceException">
	/// The <paramref name="obj"/> parameter is null.
	/// </exception>
	@Override
	public boolean equals(Object obj){
		if (obj == null){
			return false;
		}

		MDSRole role = Reflections.as(MDSRole.class, obj);
		if (role == null){
			return false;
		}

		return (this.roleName.equalsIgnoreCase(role.getRoleName()));
	}
	
	/// <summary>
	/// Determines whether the specified <paramref name="role" /> is equal to this instance.
	/// </summary>
	/// <param name="role">The role to compare to this instance.</param>
	/// <returns><c>true</c> if the specified <paramref name="role" /> is equal to this instance; otherwise, <c>false</c>.</returns>
	public boolean equals(MDSRole role){
		if (role == null){
			return false;
		}

		return (this.roleName.equalsIgnoreCase(role.getRoleName()));
	}
}
