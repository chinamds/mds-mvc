package com.mds.aiotplayer.cm.content;

import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectSearchType;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.sys.util.MDSRoleCollection;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;

/// <summary>
/// An object that specifies options for retrieving gallery objects. Used in conjunction with the
/// <see cref="ContentObjectSearcher" /> class.
/// </summary>
public class ContentObjectSearchOptions{
	public ContentObjectSearchOptions() {}
	
	public ContentObjectSearchOptions(long galleryId, ContentObjectSearchType searchType, String[] searchTerms, boolean isUserAuthenticated
			, MDSRoleCollection roles, ContentObjectType filter, ApprovalStatus approvalFilter) {
		this.GalleryId = galleryId;
		this.SearchType = searchType;
		this.SearchTerms = searchTerms;
		this.IsUserAuthenticated = isUserAuthenticated;
		this.Roles = roles;
		this.Filter = filter;
		this.ApprovalFilter = approvalFilter;
	}
	
	public ContentObjectSearchOptions(ContentObjectSearchType searchType, String[] tags, long galleryId
			, MDSRoleCollection roles, boolean isUserAuthenticated, ContentObjectType filter, ApprovalStatus approvalFilter) {
		this.SearchType = searchType;
		this.Tags = tags;
		this.GalleryId = galleryId;
		this.Roles = roles;
		this.IsUserAuthenticated = isUserAuthenticated;
		this.Filter = filter;
		this.ApprovalFilter = approvalFilter;
	}
	
	public ContentObjectSearchOptions(ContentObjectSearchType searchType, long galleryId, MDSRoleCollection roles, boolean isUserAuthenticated
			, int maxNumberResults, ContentObjectType filter, ApprovalStatus approvalFilter) {
		this.SearchType = searchType;
		this.GalleryId = galleryId;
		this.Roles = roles;
		this.IsUserAuthenticated = isUserAuthenticated;
		this.MaxNumberResults = maxNumberResults;
		this.Filter = filter;
		this.ApprovalFilter = approvalFilter;
	}
	
	public ContentObjectSearchOptions(ContentObjectSearchType searchType, String[] searchTerms, long galleryId, MDSRoleCollection roles, boolean isUserAuthenticated
			, int maxNumberResults, ContentObjectType filter, ApprovalStatus approvalFilter) {
		this.SearchType = searchType;
		this.SearchTerms = searchTerms;
		this.GalleryId = galleryId;
		this.Roles = roles;
		this.IsUserAuthenticated = isUserAuthenticated;
		this.MaxNumberResults = maxNumberResults;
		this.Filter = filter;
		this.ApprovalFilter = approvalFilter;
	}
	
	/// <summary>
	/// Indicates the type of search being performed.
	/// </summary>
	public ContentObjectSearchType SearchType;

	/// <summary>
	/// Specifies the tags to search for. Applies only when <see cref="SearchType" /> is
	/// <see cref="ContentObjectSearchType.SearchByTag" /> or <see cref="ContentObjectSearchType.SearchByPeople" />.
	/// </summary>
	public String[] Tags;
	
	/// <summary>
	/// Specifies the text to search for. Applies only when <see cref="SearchType" /> is
	/// <see cref="ContentObjectSearchType.SearchByKeyword" />, <see cref="ContentObjectSearchType.SearchByTitleOrCaption" />,
	/// or <see cref="ContentObjectSearchType.SearchByRating" />.
	/// </summary>
	public String[] SearchTerms;

	/// <summary>
	/// The gallery ID. Only items in this gallery are returned.
	/// </summary>
	public long GalleryId;

	/// <summary>
	/// The roles the current user belongs to. Required when <see cref="IsUserAuthenticated" />=<c>true</c>; 
	/// otherwise, the value can be left null.
	/// </summary>
	public MDSRoleCollection Roles;

	/// <summary>
	/// Indicates whether the current user has been authenticated.
	/// </summary>
	public boolean IsUserAuthenticated;

	/// <summary>
	/// The maximum number of results to return. When zero, all matching results are returned.
	/// </summary>
	public int MaxNumberResults;

	/// <summary>
	/// A filter specifying the type of gallery objects to return. Required. Specify
	/// <see cref="ContentObjectType.All" /> to return all matching items.
	/// </summary>
	public ContentObjectType Filter;

	/// <summary>
	/// A filter specifying the approval status of content objects to return. Required. Specify
	/// <see cref="ApprovalStatus.All" /> to return all matching items.
	/// </summary>
	public ApprovalStatus ApprovalFilter;
}
