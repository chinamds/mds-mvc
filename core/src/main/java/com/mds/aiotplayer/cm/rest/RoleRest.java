package com.mds.aiotplayer.cm.rest;

/// <summary>
/// A data object that encapsulates a set of permissions for one or more albums in MDS System. Each user
/// is assigned to zero or more roles.
/// </summary>
public class RoleRest{
	/// <summary>
	/// Gets or sets a role Id for the role.
	/// </summary>
	public long RoleId;
	
	/// <summary>
	/// Gets or sets a owner organization for the role.
	/// </summary>
	public long organizationId;
	
	/// <summary>
	/// Gets or sets a String that uniquely identifies the role.
	/// </summary>
	public String Name;
	
	/// <summary>
	/// Gets or sets a role type for the role.
	/// </summary>
	public String RoleType;

	/// <summary>
	/// Gets or sets a value indicating whether the role has been persisted to the data store.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public boolean IsNew;

	/// <summary>
	/// Gets or sets a value indicating whether the role is an album owner role or album owner template role.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public boolean IsOwner;

	/// <summary>
	/// Gets or sets a data object that contains permissions that apply to the current role.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public PermissionsRest Permissions;

	/// <summary>
	/// Gets or sets a JSON-serialized String of album data that can be used as the data source for the jQuery treeview widget.
	/// It is guaranteed to contain a node for each album ID the role applies to, even when the node is several 
	/// levels deep.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public String AlbumTreeDataJson;

	/// <summary>
	/// Gets or sets the array of all top-level album IDs this role applies to.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public long[] SelectedRootAlbumIds;

	/// <summary>
	/// Gets or sets the array of user account names this role applies to. To improve performance, this property may not 
	/// be assigned a value when large numbers of this class are being instantiated (such as when a list of all roles
	/// is being built). Clients using this class should degrade gracefully in this situation.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public String[] Members;
}