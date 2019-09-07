package com.mds.sys.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.core.LongCollection;
import com.mds.core.exception.ArgumentNullException;

/// <summary>
/// A collection of <see cref="MDSRole" /> objects.
/// </summary>
public class MDSRoleCollection extends ArrayList<MDSRole>{
	/// <summary>
	/// Initializes a new instance of the <see cref="MDSRoleCollection"/> class.
	/// </summary>
	public MDSRoleCollection(){
	}

	/// <summary>
	/// Adds the roles to the current collection.
	/// </summary>
	/// <param name="roles">The roles to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="roles" /> is null.</exception>
	public void addRange(Iterable<MDSRole> roles)	{
		if (roles == null)
			throw new ArgumentNullException("roles");

		for (MDSRole role : roles){
			add(role);
		}
	}

	/// <summary>
	/// Sort the objects in this collection by the <see cref="MDSRole.RoleName" /> property.
	/// </summary>
	public void sort(){
		Collections.sort(this);
	}

	/// <summary>
	/// Creates a new collection containing deep copies of the items it contains.
	/// </summary>
	/// <returns>
	/// Returns a new collection containing deep copies of the items it contains.
	/// </returns>
	public MDSRoleCollection copy() throws InvalidMDSRoleException{
		MDSRoleCollection copy = new MDSRoleCollection();

		for (MDSRole role : this)	{
			copy.add(role.copy());
		}

		return copy;
	}

	/// <summary>
	/// Verify the roles in the collection conform to business rules. Specificially, if any of the roles have administrative permissions
	/// (AllowAdministerSite = true or AllowAdministerGallery = true):
	/// 1. Make sure the role permissions - except HideWatermark - are set to true.
	/// 2. Make sure the root album IDs are a list containing the root album ID for each affected gallery.
	/// If anything needs updating, update the object and persist the changes to the data store. This helps keep the data store
	/// valid in cases where the user is directly editing the tables (for example, adding/deleting records from the gs_Role_Album table).
	/// </summary>
	public void validateIntegrity()	{
		for (MDSRole role : this)	{
			role.validateIntegrity();
		}
	}

	/// <summary>
	/// Return the role that matches the specified <paramref name="roleName"/>. It is not case sensitive, so that 
	/// "ReadAll" matches "readall". Returns null if no match is found.
	/// </summary>
	/// <param name="roleName">The name of the role to return.</param>
	/// <returns>
	/// Returns the role that matches the specified role name. Returns null if no match is found.
	/// </returns>
	public MDSRole getRole(String roleName, long oId){
		// We know MDSRoles is actually a List<MDSRole> because we passed it to the constructor.
		return this.stream().filter(r->r.getRoleName().equalsIgnoreCase(roleName) && (oId==Long.MIN_VALUE || r.getOrganizationId() == oId)).findFirst().orElse(null);
	}
	
	public MDSRole getRole(long roleId){
		// We know MDSRoles is actually a List<MDSRole> because we passed it to the constructor.
		return this.stream().filter(r->r.getRoleId() == roleId).findFirst().orElse(null);
	}

	/// <summary>
	/// Gets the MDS System roles that match the specified <paramref name="roleName"/>. It is not case sensitive,
	/// so that "ReadAll" matches "readall". Will return multiple roles with the same name when the gallery is assigned
	/// to more than one gallery.
	/// </summary>
	/// <param name="roleName">The name of the role to return.</param>
	/// <returns>
	/// Returns the MDS System roles that match the specified <paramref name="roleName"/>.
	/// </returns>
	/// <overloads>
	/// Gets the MDS System roles that match the specified parameters.
	/// </overloads>
	public MDSRoleCollection getRoles(String roleName, long oId) throws InvalidMDSRoleException{
		return getRoles(new String[] { roleName }, oId);
	}
	
	public MDSRoleCollection getRoles(long roleId) throws InvalidMDSRoleException{
		return getRoles(new long[] { roleId });
	}

	/// <overloads>
	/// Gets the MDS System roles that match the specified parameters.
	/// </overloads>
	/// <summary>
	/// Gets the MDS System roles that match the specified <paramref name="roleNames"/>. It is not case sensitive,
	/// so that "ReadAll" matches "readall".
	/// </summary>
	/// <param name="roleNames">The name of the roles to return.</param>
	/// <returns>
	/// Returns the MDS System roles that match the specified <paramref name="roleNames"/>.
	/// </returns>
	/// <exception cref="InvalidMDSRoleException">Thrown when one or more of the requested role names could not be found
	/// in the current collection.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="roleNames" /> is null.</exception>
	public MDSRoleCollection getRoles(String[] roleNames, long oId) throws InvalidMDSRoleException	{
		if (roleNames == null)
			throw new ArgumentNullException("roleNames");

		// We know MDSRoles is actually a List<MDSRole> because we passed it to the constructor.

		MDSRoleCollection roles = new MDSRoleCollection();
		for(String roleName : roleNames){
			MDSRole role = stream().filter(r->r.getRoleName().equalsIgnoreCase(roleName) && (oId==Long.MIN_VALUE || r.getOrganizationId() == oId)).findFirst().orElse(null);
			if (role == null){
				throw new InvalidMDSRoleException(MessageFormat.format("Could not find a MDS System role named '{0}'. Verify the data table contains a record for this role, and that the cache is being properly managed.", roleName));
			}else{
				roles.add(role);
			}
		}

		return roles;
	}
	
	public MDSRoleCollection getRoles(long[] roleIds) throws InvalidMDSRoleException	{
		if (roleIds == null)
			throw new ArgumentNullException("roleIds");

		// We know MDSRoles is actually a List<MDSRole> because we passed it to the constructor.

		MDSRoleCollection roles = new MDSRoleCollection();
		for(long roleId : roleIds){
			MDSRole role = stream().filter(r->r.getRoleId() == (roleId)).findFirst().orElse(null);
			if (role == null){
				throw new InvalidMDSRoleException(MessageFormat.format("Could not find a MDS System role named '{0}'. Verify the data table contains a record for this role, and that the cache is being properly managed.", roleId));
			}else{
				roles.add(role);
			}
		}

		return roles;
	}

	/// <summary>
	/// Gets the MDS System roles with AllowAdministerGallery permission, including roles with AllowAdministerSite permission.
	/// </summary>
	/// <returns>Returns the MDS System roles with AllowAdministerGallery permission.</returns>
	public MDSRoleCollection getRolesWithGalleryAdminPermission()	{
		MDSRoleCollection roles = new MDSRoleCollection();

		roles.addRange(stream().filter(role -> role.getAllowAdministerGallery() == true).collect(Collectors.toList()));

		return roles;
	}

	/// <summary>
	/// Gets the album IDs for which the roles provide view permission. The list is generated from the album IDs already present 
	/// in the roles. May return album IDs that belong to other galleries, so if the presence of these IDs may cause an issue, 
	/// be sure to filter them out. An example of this can be found in 
	/// <see cref="ContentObjectSearcher.RemoveChildAlbumsAndAlbumsInOtherGalleries(Iterable&lt;int&gt;)" />.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Iterable{System.Int32}.</returns>
	public List<Long> getViewableAlbumIdsForGallery(long galleryId){
		LongCollection albumIds = new LongCollection();

		List<MDSRole> MDSRoles = stream().filter(role -> role.getAllowViewAlbumOrContentObject() 
				&& role.getGalleries().stream().anyMatch(g -> g.getGalleryId() == galleryId)).collect(Collectors.toList()); 
		for (MDSRole role : MDSRoles){
			albumIds.addRange(role.getAllAlbumIds());
		}

		return albumIds.stream().distinct().collect(Collectors.toList());
	}
}
