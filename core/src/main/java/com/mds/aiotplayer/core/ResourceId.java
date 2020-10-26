/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

import java.util.List;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.core.exception.ArgumentException;

/// <summary>
/// Specifies a distinct resource access id within MDS System.
/// </summary>
public enum ResourceId {
	none,
	home,
	
	sys_roles,
	sys_users,
	sys_menuFunctions,
	sys_areas,
	sys_dicts,
	sys_organizations,
	sys_permissions,
	sys_eventLogs,
	
	sys_myMessages,
	sys_myCalendars,
	sys_notifications,
	sys_myProfiles,
	
	cm_albums,
	cm_galleries,
	cm_galleryview,
	cm_contenteditor,
	
	cm_banners,
	cm_slideshows,
	cm_clocks,
	cm_contentLists,
		
	cm_dailyLists,
	
	pm_playerGroups,
	pm_players,
	pm_playerGroup2Players,
	
	pl_layouts,
	pl_catalogues,
	pl_playlists,
	
	ps_channels,
	ps_playerTuners,
	ps_calendars,
	
	i18n_cultures,
	i18n_neutralResources,
	i18n_localizedResources,
	
	wf_organizationWorkflowTypes,
	wf_activities,
	wf_workflows,
	
	personal_message,
	personal_calendar,
	
	last_selectable,
	/// <summary>
	/// Represents an album view
	/// </summary>
	album,
	albumtreeview,
	changepassword,
	createaccount,
	error_cannotwritetodirectory,
	error_generic,
	install,
	login,
	/// <summary>
	/// Represents the content object view
	/// </summary>
	contentobject,
	myaccount,
	recoverpassword,
	
	sys_backuprestore,
	sys_css,
	sys_eventlog,
	sys_sitesettings,
	sys_usersettings,
	
	cm_gallerysettings,
	cm_gallerycontrolsettings,
	cm_images,
	cm_mediaobjects,
	cm_metadata,
	cm_mediaobjecttypes,
	cm_mediatemplates,
	cm_videoaudioother,
	cm_uitemplates,
	cm_addobjects,
	cm_assignthumbnail,
	cm_createalbum,
	cm_deletealbum,
	cm_deleteoriginals,
	cm_deleteobjects,
	cm_downloadobjects,
	cm_editcaptions,
	cm_rotateimage,
	cm_rotateimages,
	cm_synchronize,
	cm_transferobject,
	
	cm_contentobjects_addobjects,
	cm_dailyListform,
	
	//search,
	upgrade;
		
	/*private final int resourceId;
    private ResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
    	
	public int getResourceId() {
		return resourceId;
	}*/
	
	public static ResourceId getResourceId(String resourceId, ResourceId defaValue) {
		for(ResourceId rid : ResourceId.values()) {
			if (rid.toString().equalsIgnoreCase(resourceId))
				return rid;
		}
		
		return defaValue;
	}
	
	public static ResourceId getResourceId(String resourceId) {	
		return getResourceId(resourceId, ResourceId.none);
	}
	
	public static List<ResourceId> getResourceIds() {
		List<ResourceId> resourceIds = Lists.newArrayList();
		for(ResourceId rid : ResourceId.values()) {
			if (rid.ordinal() < ResourceId.last_selectable.ordinal())
				resourceIds.add(rid);
		}
		
		return resourceIds;
	}
}

