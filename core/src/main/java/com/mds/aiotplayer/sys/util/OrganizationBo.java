/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.util;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.cm.util.CMUtils;

/// <summary>
	/// Represents a organization within MDS System.
	/// </summary>
public class OrganizationBo implements Comparable<OrganizationBo>{
	////#region Private Fields

	private long id;
	private Long pid;
	private String code;
	private String name;
	private String description;
	private Date creationDate;
	private List<Long> galleries;
	private List<Long> users;
	private List<Long> organizations;

	////#endregion


	//#region Public Properties

	/// <summary>
	/// Gets or sets the unique identifier for this organization.
	/// </summary>
	/// <value>The unique identifier for this organization.</value>
	public long getOrganizationId(){
		return this.id;
	}
	
	public void setOrganizationId(long id){
		this.id = id;
	}
	
	/// <summary>
	/// Gets or sets the parent id for this organization.
	/// </summary>
	/// <value>The parent id for this organization.</value>
	public Long getParentId(){
		return this.pid;
	}
	
	public void setParentId(Long pid){
		this.pid = pid;
	}

	/// <summary>
	/// Gets a value indicating whether this object is new and has not yet been persisted to the data store.
	/// </summary>
	/// <value><c>true</c> if this instance is new; otherwise, <c>false</c>.</value>
	public boolean isNew()	{
		return (this.id == Long.MIN_VALUE);
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/// <summary>
	/// Gets or sets the description for this organization.
	/// </summary>
	/// <value>The description for this organization.</value>
	public String getName()	{
		return this.name;
	}
	
	public void setName(String name)	{
		this.name = name;
	}	

	/// <summary>
	/// Gets or sets the description for this organization.
	/// </summary>
	/// <value>The description for this organization.</value>
	public String getDescription()	{
		return this.description;
	}
	
	public void setDescription(String description)	{
		this.description = description;
	}

	/// <summary>
	/// Gets or sets the date this organization was created.
	/// </summary>
	/// <value>The date this organization was created.</value>
	public Date getCreationDate( ){
		return this.creationDate;
	}
	
	public void setCreationDate(Date creationDate){
		this.creationDate = creationDate;
	}


	/// <summary>
	/// Gets or sets a dictionary containing a list of album IDs (key) and the flattened list of
	/// all child album IDs below each album.
	/// </summary>
	/// <value>An instance of Dictionary&lt;int, List&lt;int&gt;&gt;.</value>
	public List<Long> getGalleries(){
		return this.galleries;
	}
	
	public void setGalleries(List<Long> galleries){
		this.galleries = galleries;
	}
	
	public void addGallery(long galleryId){
		if (this.galleries == null)
			this.galleries = Lists.newArrayList();
		
		this.galleries.add(galleryId);
	}
	
	public List<Long> getUsers(){
		return this.users;
	}
	
	public void setUsers(List<Long> users){
		this.users = users;
	}
	
	public void addUser(long userId){
		if (this.users == null)
			this.users = Lists.newArrayList();
		
		this.users.add(userId);
	}
	
	public List<Long> getOrganizations(){
		return this.organizations;
	}
	
	public void setOrganizations(List<Long> organizations){
		this.organizations = organizations;
	}
	
	public void addOrganization(long organizationId){
		if (this.organizations == null)
			this.organizations = Lists.newArrayList();
		
		this.organizations.add(organizationId);
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="Organization"/> class.
	/// </summary>
	public OrganizationBo(){
		this.id = Long.MIN_VALUE;
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Creates a deep copy of this instance.
	/// </summary>
	/// <returns>Returns a deep copy of this instance.</returns>
	public OrganizationBo copy()	{
		OrganizationBo organizationCopy = new OrganizationBo();

		organizationCopy.setParentId(this.pid);
		organizationCopy.setOrganizationId(this.id);
		organizationCopy.setName(this.name);
		organizationCopy.setDescription(this.description);
		organizationCopy.setCreationDate(this.creationDate);

		organizationCopy.setGalleries(galleries);
		organizationCopy.setUsers(users);
		organizationCopy.setOrganizations(organizations);

		return organizationCopy;
	}

	/// <summary>
	/// Persist this organization object to the data store.
	/// </summary>
	public void save() throws RecordExistsException{
		boolean isNew = isNew();
		//id = CMUtils.saveOrganization(this);
		
		//

		

		HelperFunctions.clearAllCaches();
	}

	/// <summary>
	/// Permanently delete the current organization from the data store, including all related records. This action cannot
	/// be undone.
	/// </summary>
	public void delete() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException {
		onBeforeDeleteOrganization();
		// Cascade delete relationships should take care of any related records not deleted in OnBeforeDeleteOrganization.
		//CMUtils.deleteOrganization(this);

		HelperFunctions.clearAllCaches();
	}

	/// <summary>
	/// Called before deleting a organization. This function deletes the galleries and any related records that won't be automatically
	/// deleted by the cascade delete relationship on the organization table.
	/// </summary>
	private void onBeforeDeleteOrganization() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		deleteGalleries();

		deleteUiTemplates();
	}

	/// <summary>
	/// Deletes the root album for the current organization and all child items, but leaves the directories and original files on disk.
	/// This function also deletes the metadata for the root album, which will leave it in an invalid state. For this reason, 
	/// call this function *only* when also deleting the organization the album is in.
	/// </summary>
	private void deleteGalleries() throws InvalidGalleryException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		// Step 1: Delete the root album contents
		for(long id : this.galleries) {
			GalleryBo gallery = CMUtils.loadGallery(id);
			
			gallery.delete();
		}

		// Step 2: Delete all metadata associated with the root album of this organization
		//CMUtils.deleteMetadata(rootAlbum.getId());
	}

	/// <summary>
	/// Deletes the UI templates associated with the current organization.
	/// </summary>
	private void deleteUiTemplates(){
		CMUtils.deleteUiTemplates(this.id);
	}

	//#endregion

	//#region IComparable Members

	/// <summary>
	/// Compares the current instance with another object of the same type.
	/// </summary>
	/// <param name="obj">An object to compare with this instance.</param>
	/// <returns>
	/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has these meanings: Value Meaning Less than zero This instance is less than <paramref name="obj"/>. Zero This instance is equal to <paramref name="obj"/>. Greater than zero This instance is greater than <paramref name="obj"/>.
	/// </returns>
	/// <exception cref="T:System.ArgumentException">
	/// 	<paramref name="obj"/> is not the same type as this instance. </exception>
	@Override
	public int compareTo(OrganizationBo obj)	{
		if (obj == null)
			return 1;
		else{
			return Long.compare(this.id, obj.getOrganizationId());
		}
	}

	//#endregion
}

