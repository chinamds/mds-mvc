package com.mds.aiotplayer.cm.rest;

import java.util.Date;

/// <summary>
/// A client-optimized object containing information about the current user.
/// </summary>
public class UserRest{
	/// <summary>
	/// Gets the logon name of the current user, or null if the current user is anonymous.
	/// </summary>
	/// <value>
	/// The name of the user, or null.
	/// </value>
	public String UserName;

	/// <summary>
	/// Indicates whether the current user is authenticated.
	/// </summary>
	/// <value>
	/// <c>true</c> if the current user is authenticated; otherwise, <c>false</c>.
	/// </value>
	public boolean IsAuthenticated;

	/// <summary>
	/// Gets or sets a value indicating whether the user has permision to add an album to at least one album in the
	/// current gallery.
	/// </summary>
	public Boolean CanAddAlbumToAtLeastOneAlbum;

	/// <summary>
	/// Gets or sets a value indicating whether the user has permision to add a content object to at least one album in the
	/// current gallery.
	/// </summary>
	public Boolean CanAddContentToAtLeastOneAlbum;

	/// <summary>
	/// Gets the ID of the user's album, or 0 if user albums are disabled or the current user
	/// is anonymous.
	/// </summary>
	/// <value>
	/// The user album ID, or 0.
	/// </value>
	public Long UserAlbumId;

	/// <summary>
	/// Gets or sets the gallery ID that is currently in context for the user. For example, this may indicate the gallery
	/// the <see cref="UserAlbumId" /> applies to.
	/// </summary>
	/// <value>The user album gallery ID.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Long GalleryId;

	/// <summary>
	/// Gets or sets application-specific information for the membership user. 
	/// </summary>
	/// <value>Application-specific information for the membership user.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public String Comment;

	/// <summary>
	/// Gets or sets the e-mail address for the membership user.
	/// </summary>
	/// <value>The e-mail address for the membership user.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public String Email;

	/// <summary>
	/// Gets or sets whether the membership user can be authenticated.
	/// </summary>
	/// <value><c>true</c> if user can be authenticated; otherwise, <c>false</c>.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Boolean IsApproved;

	/// <summary>
	/// Gets or sets a value indicating whether the user has enabled or disabled her personal album (aka user album).
	/// </summary>
	/// <value>A value indicating whether the user has enabled or disabled her personal album (aka user album).</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Boolean EnableUserAlbum;

	/// <summary>
	/// Gets a value indicating whether the membership user is locked out and unable to be validated.
	/// </summary>
	/// <value><c>true</c> if the membership user is locked out and unable to be validated; otherwise, <c>false</c>.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Boolean IsLockedOut;

	/// <summary>
	/// Gets the date and time when the user was added to the membership data store.
	/// </summary>
	/// <value>The date and time when the user was added to the membership data store.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Date CreationDate;

	/// <summary>
	/// Gets or sets the date and time when the membership user was last authenticated or accessed the application.
	/// </summary>
	/// <value>The date and time when the membership user was last authenticated or accessed the application.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Date LastActivityDate;

	/// <summary>
	/// Gets or sets the date and time when the user was last authenticated.
	/// </summary>
	/// <value>The date and time when the user was last authenticated.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Date LastLoginDate;

	/// <summary>
	/// Gets the date and time when the membership user's password was last updated.
	/// </summary>
	/// <value>The date and time when the membership user's password was last updated.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Date LastPasswordChangedDate;

	/// <summary>
	/// Gets or sets the roles assigned to the user.
	/// </summary>
	/// <value>The roles.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public String[] Roles;

	/// <summary>
	/// Gets or sets a value indicating whether the user has been persisted to the data store.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Boolean IsNew;

	/// <summary>
	/// Gets or sets the password for the user. This is populated *only* when creating a new user in javascript.
	/// Will be empty in all other cases.
	/// </summary>
	/// <value>The password.</value>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public String Password;

	/// <summary>
	/// Gets or sets a value indicating whether a password reset is being requested. This will be <c>false</c> unless
	/// specifically set to <c>true</c> by client code.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Boolean PasswordResetRequested;

	/// <summary>
	/// Gets or sets a value indicating whether to change the password to the value stored in <see cref="Password" />.
	/// This will be <c>false</c> unless specifically set to <c>true</c> by client code.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Boolean PasswordChangeRequested;

	/// <summary>
	/// Gets or sets a value indicating whether to notify the user during a password change. This will be <c>false</c> unless
	/// specifically set to <c>true</c> by client code.
	/// </summary>
	//[JsonProperty(DefaultValueHandling = DefaultValueHandling.Ignore)]
	public Boolean NotifyUserOnPasswordChange;
}