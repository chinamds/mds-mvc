/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.aiotplayer.pm.rest.PlayerItem;

/// <summary>
/// A client-optimized object that contains gallery data. This class is designed to be sent to
/// the client (e.g. as JSON) and used by javascript, including as the data source for a client
/// templating engine.
/// </summary>
public class CMData{
	/// <summary>
	/// Gets the application-level properties for the gallery.
	/// </summary>
	/// <value>
	/// An instance of <see cref="App" />.
	/// </value>
	private AppRest app;
	@JsonProperty(value = "App")
	public AppRest getApp(){return this.app;}
	public void setApp(AppRest app){this.app = app;}

	/// <summary>
	/// Gets the properties that affect the user experience.
	/// </summary>
	/// <value>
	/// An instance of <see cref="Settings" />.
	/// </value>
	private SettingsRest settings;
	@JsonProperty(value = "Settings")
	public SettingsRest getSettings() {
		return settings;
	}
	
	public void setSettings(SettingsRest settings) {
		this.settings = settings;
	}

	/// <summary>
	/// Gets information about the current user.
	/// </summary>
	/// <value>
	/// An instance of <see cref="User" />.
	/// </value>
	private UserRest user;

	/// <summary>
	/// Gets information about an album. Child properties <see cref="Entity.Album.GalleryItems" />
	/// and <see cref="Entity.Album.ContentItems" /> may be null in certain situations to keep the 
	/// object size as small as possible.
	/// </summary>
	/// <value>
	/// An instance of <see cref="Album" />.
	/// </value>
	private AlbumRest album;

	/// <summary>
	/// Gets information about a content object.
	/// </summary>
	/// <value>
	/// An instance of <see cref="MediaItem" />.
	/// </value>
	private MediaItem mediaItem;

	/// <summary>
	/// Gets or sets the currently active metadata. For a single content object or album, it is the 
	/// metadata associated with it. When multiple items are selected on the thumbnail view, it
	/// is a combination of merged data (for tagged items such as keywords) and the metadata for
	/// the last item in the array.
	/// </summary>
	/// <value>An array of <see cref="MetaItem" /> instances.</value>
	private MetaItemRest[] activeMetaItems;

	/// <summary>
	/// Gets or sets the currently active Approval data. For a single content object or album, it is the 
	/// approval data associated with it. When multiple items are selected on the thumbnail view, it
	/// is a combination of merged data (for tagged items such as keywords) and the approval data for
	/// the last item in the array.
	/// </summary>
	/// <value>An array of <see cref="ApprovalItem" /> instances.</value>
	private ApprovalItem[] activeApprovalItems;

	/// <summary>
	/// Gets or sets the currently selected or displayed gallery item(s).
	/// </summary>
	/// <value>An array of <see cref="ContentItem" /> instances.</value>
	private ContentItem[] activeContentItems;

	/// <summary>
	/// Gets language resources.
	/// </summary>
	/// <value>
	/// An instance of <see cref="Resource" />.
	/// </value>
	private ResourceRest resource;

	/// <summary>
	/// Gets or sets players.
	/// </summary>
	/// <value>
	/// An instance of <see cref="Player" />.
	/// </value>
	private PlayerItem[] playerItems;
	
	@JsonProperty(value = "User")
	public UserRest getUser() {
		return user;
	}
	public void setUser(UserRest user) {
		this.user = user;
	}
	
	@JsonProperty(value = "Album")
	public AlbumRest getAlbum() {
		return album;
	}
	
	public void setAlbum(AlbumRest album) {
		this.album = album;
	}
	
	@JsonProperty(value = "MediaItem")
	public MediaItem getMediaItem() {
		return mediaItem;
	}
	public void setMediaItem(MediaItem mediaItem) {
		this.mediaItem = mediaItem;
	}
	
	@JsonProperty(value = "ActiveMetaItems")
	public MetaItemRest[] getActiveMetaItems() {
		return activeMetaItems;
	}
	public void setActiveMetaItems(MetaItemRest[] activeMetaItems) {
		this.activeMetaItems = activeMetaItems;
	}
	
	@JsonProperty(value = "ActiveApprovalItems")
	public ApprovalItem[] getActiveApprovalItems() {
		return activeApprovalItems;
	}
	public void setActiveApprovalItems(ApprovalItem[] activeApprovalItems) {
		this.activeApprovalItems = activeApprovalItems;
	}
	
	@JsonProperty(value = "ActiveContentItems")
	public ContentItem[] getActiveContentItems() {
		return activeContentItems;
	}
	public void setActiveContentItems(ContentItem[] activeContentItems) {
		this.activeContentItems = activeContentItems;
	}
	
	@JsonProperty(value = "Resource")
	public ResourceRest getResource() {
		return resource;
	}
	public void setResource(ResourceRest resource) {
		this.resource = resource;
	}
	
	@JsonProperty(value = "PlayerItems")
	public PlayerItem[] getPlayerItems() {
		return playerItems;
	}
	public void setPlayerItems(PlayerItem[] playerItems) {
		this.playerItems = playerItems;
	}
}

