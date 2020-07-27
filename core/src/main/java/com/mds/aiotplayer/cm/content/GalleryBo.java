package com.mds.aiotplayer.cm.content;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.service.SeedManager;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.util.OrganizationBo;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.GalleryUtils;

/// <summary>
	/// Represents a gallery within MDS System.
	/// </summary>
public class GalleryBo implements Comparable<GalleryBo>{
	////#region Private Fields

	private long id;
	private long rootAlbumId = Long.MIN_VALUE;
	private String name;
	private String description;
	private Date creationDate;
	private Map<Long, List<Long>> albums;
	//owner users
	private List<Long> users;
	//owner organizations
	private List<Long> organizations;

	////#endregion

	////#region Events

	/// <summary>
	/// Occurs when a gallery is first created, just after it is persisted to the data store.
	/// </summary>
	//public static event EventHandler<GalleryCreatedEventArgs> GalleryCreated;
	//#region Public Events
	private List<GalleryCreatedListener> listeners = new ArrayList<>();

    public void addGalleryCreatedListener(GalleryCreatedListener listener) {
        listeners.add(listener);
    }

    public void removeGalleryCreatedListener(GalleryCreatedListener listener) {
        listeners.remove(listener);
    }

	//#endregion

	//#region Public Properties

	/// <summary>
	/// Gets or sets the unique identifier for this gallery.
	/// </summary>
	/// <value>The unique identifier for this gallery.</value>
	public long getGalleryId(){
		return this.id;
	}
	
	public void setGalleryId(long id){
		this.id = id;
	}

	/// <summary>
	/// Gets a value indicating whether this object is new and has not yet been persisted to the data store.
	/// </summary>
	/// <value><c>true</c> if this instance is new; otherwise, <c>false</c>.</value>
	public boolean isNew()	{
		return (this.id == Long.MIN_VALUE);
	}
	
	/// <summary>
	/// Gets or sets the description for this gallery.
	/// </summary>
	/// <value>The description for this gallery.</value>
	public String getName()	{
		return this.name;
	}
	
	public void setName(String name)	{
		this.name = name;
	}	
	
	public String getRootAlbumPrefix() throws InvalidGalleryException{
		String title = this.name;
		if (this.name.startsWith("m_")) {
			if (this.getOrganizations() != null && this.getOrganizations().size() > 0) {
				OrganizationBo org = UserUtils.getOwnerOrganization(this);
				if (org.getOrganizationId() == Organization.getRootId()) {
					title = "";
				}else {
					title = org.getCode();
				}
			}else if(this.getUsers() != null && this.getUsers().size() > 0) {
				title = UserUtils.getUserById(this.getUsers().get(0)).getUsername();
			}else {
				title = Long.toString(this.id);
			}
		}
		
		if (!StringUtils.isBlank(title)) {
			title += ": ";
		}
					
		return title;
	}
	
	public String getTitle() throws InvalidGalleryException{
		if (this.name.startsWith("m_")) {
			if (this.getOrganizations() != null && this.getOrganizations().size() > 0) {
				OrganizationBo org = UserUtils.getOwnerOrganization(this);
				if (org.getOrganizationId() == Organization.getRootId()) {
					return this.name;
				}else {
					return org.getCode();
				}
			}else if(this.getUsers() != null && this.getUsers().size() > 0) {
				return UserUtils.getUserById(this.getUsers().get(0)).getUsername();
			}
		}
		
		return this.name;
	}

	/// <summary>
	/// Gets or sets the description for this gallery.
	/// </summary>
	/// <value>The description for this gallery.</value>
	public String getDescription()	{
		return this.description;
	}
	
	public void setDescription(String description)	{
		this.description = description;
	}

	/// <summary>
	/// Gets or sets the date this gallery was created.
	/// </summary>
	/// <value>The date this gallery was created.</value>
	public Date getCreationDate( ){
		return this.creationDate;
	}
	
	public void setCreationDate(Date creationDate){
		this.creationDate = creationDate;
	}

	/// <summary>
	/// Gets or sets the ID of the root album of this gallery.
	/// </summary>
	/// <value>The ID of the root album of this gallery</value>
	public Long getRootAlbumId()	{
		if (this.rootAlbumId == Long.MIN_VALUE)	{
			// The root album is the item in the Albums dictionary with the most number of child albums.
			long maxCount = Long.MIN_VALUE;
			for(Entry<Long, List<Long>> kvp : this.albums.entrySet()){
				if (kvp.getValue().size() > maxCount){
					maxCount = kvp.getValue().size();
					this.rootAlbumId = kvp.getKey();
				}
			}
		}
		
		return this.rootAlbumId;
	}
	
	public void setRootAlbumId(long rootAlbumId) {
		this.rootAlbumId = rootAlbumId;
	}

	/// <summary>
	/// Gets or sets a dictionary containing a list of album IDs (key) and the flattened list of
	/// all child album IDs below each album.
	/// </summary>
	/// <value>An instance of Dictionary&lt;int, List&lt;int&gt;&gt;.</value>
	public Map<Long, List<Long>> getAlbums(){
		return this.albums;
	}
	
	public void setAlbums(Map<Long, List<Long>> albums){
		this.albums = albums;
	}
	
	/// <summary>
	/// Gets or sets a dictionary containing a list of user IDs (key) and the flattened list of
	/// all owner user IDs below each this gallery.
	/// </summary>
	/// <value>An instance of user&lt;long, List&lt;long&gt;&gt;.</value>
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
	
	/// <summary>
	/// Gets or sets a dictionary containing a list of organization IDs (key) and the flattened list of
	/// all owner organizations IDs below each this gallery.
	/// </summary>
	/// <value>An instance of organization&lt;long, List&lt;long&gt;&gt;.</value>
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
	/// Initializes a new instance of the <see cref="Gallery"/> class.
	/// </summary>
	public GalleryBo(){
		this.id = Long.MIN_VALUE;
		
		addGalleryCreatedListener(new GalleryUtils());
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Creates a deep copy of this instance.
	/// </summary>
	/// <returns>Returns a deep copy of this instance.</returns>
	public GalleryBo copy()	{
		GalleryBo galleryCopy = new GalleryBo();

		galleryCopy.setGalleryId(this.id);
		galleryCopy.setName(this.name);
		galleryCopy.setDescription(this.description);
		galleryCopy.setCreationDate(this.creationDate);

		Map<Long, List<Long>> albums = new HashMap<Long, List<Long>>();
		for(Entry<Long, List<Long>> kvp : this.albums.entrySet()){
			albums.put(kvp.getKey(), new ArrayList<Long>(kvp.getValue()));
		}
		galleryCopy.setAlbums(albums);
		galleryCopy.setUsers(users);
		galleryCopy.setOrganizations(organizations);

		return galleryCopy;
	}

	/// <summary>
	/// Persist this gallery object to the data store.
	/// </summary>
	public void save() throws RecordExistsException{
		boolean isNew = isNew();
		id = CMUtils.saveGallery(this);
		
		//

		// For new galleries, configure it and then trigger the created event.
		if (isNew){
			configure();

			GalleryCreatedEventArgs event = new GalleryCreatedEventArgs(this, this.id);
			listeners.forEach(l -> l.galleryCreated(event));
			
			/*EventHandler<GalleryCreatedEventArgs> galleryCreated = GalleryCreated;
			if (galleryCreated != null)
			{
				galleryCreated(null, new GalleryCreatedEventArgs(GalleryId));
			}*/
		}

		HelperFunctions.clearAllCaches();
	}

	/// <summary>
	/// Permanently delete the current gallery from the data store, including all related records. This action cannot
	/// be undone.
	/// </summary>
	public void delete() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		//Factory.GetDataProvider().Gallery_Delete(this);
		onBeforeDeleteGallery();

		// Cascade delete relationships should take care of any related records not deleted in OnBeforeDeleteGallery.
		CMUtils.deleteGallery(this);

		HelperFunctions.clearAllCaches();
	}

	/// <summary>
	/// Configure the gallery by verifying that a default set of
	/// records exist in the relevant tables (Album, GallerySetting, MimeTypeGallery, Role_Album, UiTemplate,
	/// UiTemplateAlbum). No changes are made to the file system as part of this operation. This method does not overwrite 
	/// existing data, but it does insert missing data. This function can be used during application initialization to validate 
	/// the data integrity for a gallery. For example, if the user has added a record to the MIME types or template gallery 
	/// settings tables, this method will ensure that the new records are associated with this gallery.
	/// </summary>
	public void configure()	{
		//Factory.GetDataProvider().Gallery_Configure(this);

		// Step 1: Check for missing gallery settings, copying them from the template settings if necessary.
		configureGallerySettingsTable();

		// Step 2: Create a new set of gallery MIME types (do nothing if already present).
		configureMimeTypeGalleryTable();

		// Step 3: Create the root album if necessary.
		Album rootAlbumDto = configureAlbumTable();

		// Step 4: For each role with AllowAdministerSite permission, add a corresponding record in gs_Role_Album giving it 
		// access to the root album.
		configureRoleAlbumTable(rootAlbumDto.getId());

		// Step 5: Validate the UI templates.
		configureUiTemplateTable();
		configureUiTemplateAlbumTable(rootAlbumDto);

		// Step 6: Reset the sync table.
		configureSyncTable();

		// Verify each album/content object has a title and caption? This would be a pretty big perf hit, so let's not do it.
		/*try {
			GalleryUtils.createSampleObjects(this.id);
		} catch (UnsupportedContentObjectTypeException | IOException | InvalidGalleryException
				| UnsupportedImageTypeException | InvalidContentObjectException | InvalidAlbumException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/// <summary>
	/// Verify there are gallery settings for the current gallery that match every template gallery setting, creating any
	/// if necessary.
	/// </summary>
	private void configureGallerySettingsTable(){
		SeedManager seedManager = SpringContextHolder.getBean(SeedManager.class);
		seedManager.configureGallerySettingsTable(this);
	}

	/// <summary>
	/// Verify there is a MIME type/gallery mapping for the current gallery for every MIME type, creating any
	/// if necessary.
	/// </summary>
	private void configureMimeTypeGalleryTable(){
		CMUtils.configureMimeTypeGalleryTable(this.id);
	}

	/// <summary>
	/// Verify the current gallery has a root album, creating one if necessary. The root album is returned.
	/// </summary>
	/// <returns>An instance of <see cref="AlbumDto" />.</returns>
	private Album configureAlbumTable(){
		return CMUtils.configureAlbumTable(this.id);
	}

	/// <summary>
	/// Verify there is a site admin role/album mapping for the root album in the current gallery, creating one
	/// if necessary.
	/// </summary>
	/// <param name="albumId">The album ID of the root album in the current gallery.</param>
	private static void configureRoleAlbumTable(long albumId){
		RoleUtils.configureRoleAlbumTable(albumId);
		/*using (var repoR = new RoleRepository())
		{
			using (var repoRa = new RoleAlbumRepository())
			{
				// Get admin roles that aren't assigned to the album, then assign them
				foreach (var rDto in repoR.Where(r => r.AllowAdministerSite && r.RoleAlbums.All(ra => ra.FKAlbumId != albumId)))
				{
					repoRa.Add(new RoleAlbumDto()
											{
												FKRoleName = rDto.RoleName,
												FKAlbumId = albumId
											});
				}

				repoRa.Save();
			}
		}*/
	}

	/// <summary>
	/// Verify there are UI templates for the current gallery that match every UI template associated with
	/// the template gallery, creating any if necessary.
	/// </summary>
	private void configureUiTemplateTable()	{
		CMUtils.configureUiTemplateTable(this.id);
	}

	/// <summary>
	/// Verify there is a UI template/album mapping for the root album in the current gallery, creating them
	/// if necessary.
	/// </summary>
	/// <param name="rootAlbum">The root album.</param>
	private static void configureUiTemplateAlbumTable(Album rootAlbum){
		CMUtils.configureUiTemplateAlbumTable(rootAlbum, rootAlbum.getGallery().getId());
		/*using (var repoUiTmpl = new UiTemplateRepository())
		{
			using (var repoUiTmplA = new UiTemplateAlbumRepository(repoUiTmpl.Context))
			{
				// Make sure each template category has at least one template assigned to the root album.
				// We do this with a union of two queries:
				// 1. For categories where there is at least one album assignment, determine if at least one of
				//    those assignments is the root album.
				// 2. Find categories without any albums at all (this is necessary because the SelectMany() in the first
				//    query won't return any categories that don't have related records in the template/album table).
				var dtos = repoUiTmpl.Where(t => t.FKGalleryId == rootAlbum.FKGalleryId)
														 .SelectMany(t => t.TemplateAlbums, (t, tt) => new { t.TemplateType, tt.FKAlbumId })
														 .GroupBy(t => t.TemplateType)
														 .Where(t => t.All(ta => ta.FKAlbumId != rootAlbum.AlbumId))
														 .Select(t => t.Key)
														 .Union(repoUiTmpl.Where(t => t.FKGalleryId == rootAlbum.FKGalleryId).GroupBy(t => t.TemplateType).Where(t => t.All(t2 => !t2.TemplateAlbums.Any())).Select(t => t.Key))
														 ;

				foreach (var dto in dtos)
				{
					// We have a template type without a root album. Find the default template and assign that one.
					var dto1 = dto;
					repoUiTmplA.Add(new UiTemplateAlbumDto()
					{
						FKUiTemplateId = repoUiTmpl.Where(t => t.FKGalleryId == rootAlbum.FKGalleryId && t.TemplateType == dto1 && t.Name.Equals("default", StringComparison.OrdinalIgnoreCase)).First().UiTemplateId,
						FKAlbumId = rootAlbum.AlbumId
					});
				}

				repoUiTmplA.Save();
			}
		}*/
	}

	/// <summary>
	/// Deletes the synchronization record belonging to the current gallery. When a sync is initiated it will be created.
	/// </summary>
	private void configureSyncTable(){
		CMUtils.configureSyncTable(this.id);
	}

	/// <summary>
	/// Called before deleting a gallery. This function deletes the albums and any related records that won't be automatically
	/// deleted by the cascade delete relationship on the gallery table.
	/// </summary>
	private void onBeforeDeleteGallery() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		deleteRootAlbum();

		deleteUiTemplates();
	}

	/// <summary>
	/// Deletes the root album for the current gallery and all child items, but leaves the directories and original files on disk.
	/// This function also deletes the metadata for the root album, which will leave it in an invalid state. For this reason, 
	/// call this function *only* when also deleting the gallery the album is in.
	/// </summary>
	private void deleteRootAlbum() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		// Step 1: Delete the root album contents
		AlbumBo rootAlbum = CMUtils.loadRootAlbumInstance(this.id);
		rootAlbum.deleteFromGallery();

		// Step 2: Delete all metadata associated with the root album of this gallery
		CMUtils.deleteMetadata(rootAlbum.getId());
	}

	/// <summary>
	/// Deletes the UI templates associated with the current gallery.
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
	public int compareTo(GalleryBo obj)	{
		if (obj == null)
			return 1;
		else{
			return Long.compare(this.id, obj.getGalleryId());
		}
	}

	//#endregion
}

/// <summary>
/// Provides data for the <see cref="Gallery.GalleryCreated" /> event.
/// </summary>
/*public class GalleryCreatedEventArgs : EventArgs
{
	private readonly int _galleryId;

	/// <summary>
	/// Initializes a new instance of the <see cref="GalleryCreatedEventArgs"/> class.
	/// </summary>
	/// <param name="galleryId">The ID of the newly created gallery.</param>
	public GalleryCreatedEventArgs(int galleryId)
	{
		_galleryId = galleryId;
	}

	/// <summary>
	/// Gets the ID of the newly created gallery.
	/// </summary>
	/// <value>The gallery ID.</value>
	public int GalleryId
	{
		return _galleryId;
	}
}*/
