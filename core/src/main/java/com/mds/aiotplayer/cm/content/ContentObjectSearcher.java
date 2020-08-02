package com.mds.aiotplayer.cm.content;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.ContentObjectSearchType;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.SecurityActionsOption;
import com.mds.aiotplayer.core.VirtualAlbumType;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.MDSRoleCollection;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Provides functionality for finding one or more gallery objects.
/// </summary>
public class ContentObjectSearcher{
	//#region Fields

	private AlbumBo rootAlbum;
	private Boolean userCanViewRootAlbum;

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets or sets the search options.
	/// </summary>
	/// <value>The search options.</value>
	private ContentObjectSearchOptions SearchOptions;

	/// <summary>
	/// Gets the type of the tag to search for. Applies only when the search type is <see cref="ContentObjectSearchType.SearchByTag" />
	/// or <see cref="ContentObjectSearchType.SearchByPeople" />.
	/// </summary>
	/// <value>The type of the tag.</value>
	private MetadataItemName getTagType(){
		return (SearchOptions.SearchType == ContentObjectSearchType.SearchByTag ? MetadataItemName.Tags : MetadataItemName.People);
	}

	/// <summary>
	/// Gets a value indicating whether the current user can view the root album.
	/// </summary>
	/// <returns><c>true</c> if the user can view the root album; otherwise, <c>false</c>.</returns>
	private boolean getUserCanViewRootAlbum() throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (userCanViewRootAlbum == null){
			userCanViewRootAlbum = HelperFunctions.canUserViewAlbum(rootAlbum, SearchOptions.Roles, SearchOptions.IsUserAuthenticated);
		}

		return userCanViewRootAlbum;
	}

	/// <summary>
	/// Gets the root album for the gallery identified in the <see cref="SearchOptions" />.
	/// </summary>
	private AlbumBo getRootAlbum() throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		if (rootAlbum == null)
			 rootAlbum = CMUtils.loadRootAlbumInstance(SearchOptions.GalleryId); 
			 
		return rootAlbum;
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectSearcher" /> class.
	/// </summary>
	/// <param name="searchOptions">The search options.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="searchOptions" /> is null.</exception>
	/// <exception cref="System.ArgumentException">Thrown when one or more properties of the <paramref name="searchOptions" /> parameter is invalid.</exception>
	/// <exception cref="EventLogs.CustomExceptions.InvalidGalleryException">Thrown when the gallery ID specified in the <paramref name="searchOptions" />
	/// parameter is invalid.</exception>
	public ContentObjectSearcher(ContentObjectSearchOptions searchOptions) throws InvalidGalleryException{
		validate(searchOptions);

		SearchOptions = searchOptions;

		if (SearchOptions.Roles == null){
			SearchOptions.Roles = new MDSRoleCollection();
		}
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Finds the first gallery object that matches the criteria. Use this method when a single item is expected.
	/// Returns null when no matching items are found.
	/// </summary>
	/// <returns>An instance of <see cref="ContentObjectBo" /> or null.</returns>
	public ContentObjectBo findOne() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		List<ContentObjectBo> contentObjects = find();
		if (!contentObjects.isEmpty())
			return contentObjects.get(0);
		
		return null;
	}

	/// <summary>
	/// Finds all gallery objects that match the search criteria. Guaranteed to not return null.
	/// </summary>
	/// <returns>ContentObjectBoCollection.</returns>
	/// <exception cref="System.InvalidOperationException">Thrown when an implementation is not found for one of the 
	/// search types.</exception>
	public List<ContentObjectBo> find() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException	{
		switch (SearchOptions.SearchType){
			case SearchByTitleOrCaption:
				return findItemsMatchingTitleOrCaption();
			case SearchByKeyword:
				return findItemsMatchingKeywords();
			case SearchByTag:
			case SearchByPeople:
				return findItemsMatchingTags();
			case HighestAlbumUserCanView:
				return wrapInContentObjectBoCollection(loadRootAlbumForUser());
			case MostRecentlyAdded:
				return findRecentlyAdded();
			case SearchByRating:
				return findContentObjectsMatchingRating();
			/*case SearchByApproval:
				return findContentObjectsMatchingApproval();*/
			default:
				throw new UnsupportedOperationException(MessageFormat.format("The method ContentObjectSearcher.Find was not designed to handle SearchType={0}. The developer must update this method.", SearchOptions.SearchType));
		}
	}

	//#endregion

	//#region Functions

	private List<ContentObjectBo> findItemsMatchingTitleOrCaption()	{
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		if (SearchOptions.Filter == ContentObjectType.All || SearchOptions.Filter == ContentObjectType.Album){
			contentObjects.addRange(getAlbumsHavingTitleOrCaption());
		}

		if (SearchOptions.Filter != ContentObjectType.Album){
			contentObjects.addRange(getContentObjectsHavingTitleOrCaption());
		}

		ContentObjectBoCollection filteredContentObjects = filterContentObjects(contentObjects);
		//filteredContentObjects = filterContentObjectsByApproval(filteredContentObjects);

		return (SearchOptions.MaxNumberResults > 0 ? filteredContentObjects.toSortedList().stream().limit(SearchOptions.MaxNumberResults).collect(Collectors.toList()) : filteredContentObjects.values());
	}

	private List<ContentObjectBo> getAlbumsHavingTitleOrCaption(){
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		/*MetadataItemName[] metaTagsToSearch = new MetadataItemName[] { MetadataItemName.Title, MetadataItemName.Caption };

		var qry = repo.Where(a => true, a => a.Metadata);

		qry = SearchOptions.SearchTerms.Aggregate(qry, (current, searchTerm) => current.Where(a =>
			a.FKGalleryId == SearchOptions.GalleryId &&
			a.Metadata.Any(md => metaTagsToSearch.contains(md.MetaName) && md.Value.contains(searchTerm))));

		qry = restrictForCurrentUser(qry);

		for (var album in qry)
		{
			contentObjects.Add(CMUtils.GetAlbumFromDto(album));
		}*/

		return contentObjects.values();
	}

	private List<ContentObjectBo> getContentObjectsHavingTitleOrCaption(){
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		/*String[] metaTagsToSearch = new String[] { MetadataItemName.Title, MetadataItemName.Caption };

		var qry = repo.Where(a => true, a => a.Metadata, a => a.ApprovalData);

		qry = restrictForCurrentUser(qry);

		qry = SearchOptions.SearchTerms.Aggregate(qry, (current, searchTerm) => current.Where(mo =>
			mo.Album.FKGalleryId == SearchOptions.GalleryId &&
			mo.Metadata.Any(md => metaTagsToSearch.contains(md.MetaName) && md.Value.contains(searchTerm))));

		for (var contentObject in qry)
		{
			contentObjects.Add(CMUtils.GetContentObjectFromDto(contentObject, null));
		}*/

		return contentObjects.values();
	}

	private List<ContentObjectBo> findItemsMatchingKeywords(){
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		if (SearchOptions.Filter == ContentObjectType.All || SearchOptions.Filter == ContentObjectType.Album){
			contentObjects.addRange(getAlbumsMatchingKeywords());
		}

		if (SearchOptions.Filter != ContentObjectType.Album){
			contentObjects.addRange(getContentObjectsMatchingKeywords());
		}

		ContentObjectBoCollection filteredContentObjects = filterContentObjects(contentObjects);
		//filteredContentObjects = filterContentObjectsByApproval(filteredContentObjects);

		return (SearchOptions.MaxNumberResults > 0 ? filteredContentObjects.toSortedList().stream().limit(SearchOptions.MaxNumberResults).collect(Collectors.toList()) : filteredContentObjects.values());
	}

	private List<ContentObjectBo> getAlbumsMatchingKeywords(){
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		/*using (var repo = new AlbumRepository())
		{
			var qry = repo.Where(a => true, a => a.Metadata);

			qry = SearchOptions.SearchTerms.Aggregate(qry, (current, searchTerm) => current.Where(a =>
				a.FKGalleryId == SearchOptions.GalleryId &&
				a.Metadata.Any(md => md.Value.contains(searchTerm))));

			qry = restrictForCurrentUser(qry);

			for (var album in qry)
			{
				contentObjects.Add(CMUtils.GetAlbumFromDto(album));
			}
		}*/

		return contentObjects.values();
	}

	private List<ContentObjectBo> getContentObjectsMatchingKeywords()
	{
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		/*using (var repo = new ContentObjectRepository())
		{
			var qry = repo.Where(a => true, a => a.Metadata, a => a.ApprovalData);

			qry = SearchOptions.SearchTerms.Aggregate(qry, (current, searchTerm) => current.Where(mo =>
				mo.Album.FKGalleryId == SearchOptions.GalleryId &&
				mo.Metadata.Any(md => md.Value.contains(searchTerm))));

			qry = restrictForCurrentUser(qry);

			for (var contentObject in qry)
			{
				contentObjects.Add(CMUtils.GetContentObjectFromDto(contentObject, null));
			}
		}*/

		return contentObjects.values();
	}

	/// <summary>
	/// Validates the specified search options. Throws an exception if not valid.
	/// </summary>
	/// <param name="searchOptions">The search options.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="searchOptions" /> is null.</exception>
	/// <exception cref="System.ArgumentException">Thrown when one or more properties of the <paramref name="searchOptions" /> parameter is invalid.</exception>
	/// <exception cref="EventLogs.CustomExceptions.InvalidGalleryException">Thrown when the gallery ID specified in the <paramref name="searchOptions" />
	/// parameter is invalid.</exception>
	private static void validate(ContentObjectSearchOptions searchOptions) throws InvalidGalleryException{
		if (searchOptions == null)
			throw new ArgumentNullException("searchOptions");

		if (searchOptions.SearchType == ContentObjectSearchType.NotSpecified)
			throw new ArgumentException("The SearchType property of the searchOptions parameter must be set to a valid search type.");

		if (searchOptions.IsUserAuthenticated && searchOptions.Roles == null)
			throw new ArgumentException("The Roles property of the searchOptions parameter must be specified when IsUserAuthenticated is true.");

		if (searchOptions.GalleryId < 0) // v3+ galleries start at 1, but galleries from earlier versions begin at 0
			throw new ArgumentException("Invalid gallery ID. The GalleryId property of the searchOptions parameter must refer to a valid gallery.");

		if ((searchOptions.SearchType == ContentObjectSearchType.SearchByTag || searchOptions.SearchType == ContentObjectSearchType.SearchByPeople) 
				&& (searchOptions.Tags == null || searchOptions.Tags.length == 0))
			throw new ArgumentException("The Tags property of the searchOptions parameter must be specified when SearchType is SearchByTag or SearchByPeople.");

		if (searchOptions.SearchType == ContentObjectSearchType.SearchByRating && (searchOptions.SearchTerms == null || searchOptions.SearchTerms.length != 1))
			throw new ArgumentException("The SearchTerms property of the searchOptions parameter must contain a single String matching one of these values: highest, lowest, none, or a number from 0 to 5.");
		
		// This throws an exception when gallery ID doesn't exist or is the template gallery.
		CMUtils.loadGallery(searchOptions.GalleryId);

		if (searchOptions.Filter == ContentObjectType.Unknown || searchOptions.Filter == ContentObjectType.NotSpecified)
			throw new ArgumentException(MessageFormat.format("The Filter property of the searchOptions parameter cannot be ContentObjectType.{0}.", searchOptions.Filter));
	}

	/// <summary>
	/// Finds the gallery objects matching tags. Guaranteed to not return null. Call this function only when the search type
	/// is <see cref="ContentObjectSearchType.SearchByTag" /> or <see cref="ContentObjectSearchType.SearchByPeople" />.
	/// Only items the user has permission to view are returned.
	/// </summary>
	/// <returns>An instance of <see cref="ContentObjectBoCollection" />.</returns>
	private List<ContentObjectBo> findItemsMatchingTags(){
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		if (SearchOptions.Filter == ContentObjectType.All || SearchOptions.Filter == ContentObjectType.Album){
			contentObjects.addRange(getAlbumsHavingTags().values());
		}

		if (SearchOptions.Filter != ContentObjectType.Album){
			contentObjects.addRange(getContentObjectsHavingTags().values());
		}

		ContentObjectBoCollection filteredContentObjects = filterContentObjects(contentObjects);
		//filteredContentObjects = filterContentObjectsByApproval(filteredContentObjects);

		return (SearchOptions.MaxNumberResults > 0 ? filteredContentObjects.toSortedList().stream().limit(SearchOptions.MaxNumberResults).collect(Collectors.toList()) : filteredContentObjects.values());
	}

	/// <summary>
	/// Gets the albums having all tags specified in the search options. Guaranteed to not return null. Only albums the 
	/// user has permission to view are returned.
	/// </summary>
	/// <returns>An instance of <see cref="ContentObjectBoCollection" />.</returns>
	private ContentObjectBoCollection getAlbumsHavingTags()
	{
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		/*using (var repo = new AlbumRepository())
		{
			var qry = repo.Where(a =>
				a.FKGalleryId == SearchOptions.GalleryId &&
				a.Metadata.Any(md => md.MetaName == TagType && md.MetadataTags.Any(mdt => SearchOptions.Tags.contains(mdt.FKTagName))), a => a.Metadata);

			for (var albumDto in restrictForCurrentUser(qry))
			{
				var album = CMUtils.GetAlbumFromDto(albumDto);

				// We have an album that contains at least one of the tags. If we have multiple tags, do an extra test to ensure
				// album matches ALL of them. (I wasn't able to write the LINQ to do this for me, so it's an extra step.)
				if (SearchOptions.Tags.length == 1)
				{
					contentObjects.Add(album);
				}
				else if (MetadataItemcontainsAllTags(album.MetadataItems.First(md => md.MetadataItemName == TagType)))
				{
					contentObjects.Add(album);
				}
			}
		}*/

		return contentObjects;
	}

	/// <summary>
	/// Determines whether the current user can view the specified <paramref name="album" />.
	/// </summary>
	/// <param name="album">The album.</param>
	/// <returns><c>true</c> if the user can view the album; otherwise, <c>false</c>.</returns>
	private boolean canUserViewAlbum(AlbumBo album) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		return SecurityGuard.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject, SearchOptions.Roles, album.getId()
				, SearchOptions.GalleryId, SearchOptions.IsUserAuthenticated, album.getIsPrivate(), SecurityActionsOption.RequireOne, album.getIsVirtualAlbum());
	}

	/// <summary>
	/// Returns a value indicating whether the <paramref name="mdItem" /> contains ALL the tags contained in SearchOptions.Tags.
	/// The comparison is case insensitive.
	/// </summary>
	/// <param name="mdItem">The metadata item.</param>
	/// <returns><c>true</c> if the metadata item contains all the tags, <c>false</c> otherwise</returns>
	private boolean metadataItemcontainsAllTags(ContentObjectMetadataItem mdItem){
		// First split the meta value into the separate tag items, trimming and converting to lower case.
		/*String[] albumTags = Lists.newArrayList(StringUtils.split(mdItem.getValue().toLowerCase(), "," ))
				.stream().filter(s->!StringUtils.isBlank(s)).map(s -> StringUtils.trim(s)).toArray(String[]::new);*/
		List<String> albumTags = Lists.newArrayList(StringUtils.split(mdItem.getValue().toLowerCase(), "," ))
				.stream().filter(s->!StringUtils.isBlank(s)).map(s -> StringUtils.trim(s)).collect(Collectors.toList());

		// Now make sure that albumTags contains ALL the items in SearchOptions.Tags.
		//return SearchOptions.Tags.aggregate(true, (current, tag) -> current & albumTags.contains(tag.ToLowerInvariant()));
		return Arrays.asList(SearchOptions.Tags).stream().allMatch(tag ->  albumTags.contains(tag.toLowerCase()));
	}

	/// <summary>
	/// Gets the content objects having all tags specified in the search options. Guaranteed to not return null. 
	/// Only content objects the user has permission to view are returned.
	/// </summary>
	/// <returns>An instance of <see cref="ContentObjectBoCollection" />.</returns>
	private ContentObjectBoCollection getContentObjectsHavingTags(){
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		/*using (var repo = new ContentObjectRepository())
		{
			var qry = repo.Where(m =>
				m.Album.FKGalleryId == SearchOptions.GalleryId &&
				m.Metadata.Any(md => md.MetaName == TagType && md.MetadataTags.Any(mdt => SearchOptions.Tags.contains(mdt.FKTagName))), m => m.Metadata, m => m.ApprovalData);

			for (var moDto in restrictForCurrentUser(qry))
			{
				var contentObject = CMUtils.GetContentObjectFromDto(moDto, null);

				// We have a content object that contains at least one of the tags. If we have multiple tags, do an extra test to ensure
				// content object matches ALL of them. (I wasn't able to write the LINQ to do this for me, so it's an extra step.)
				if (SearchOptions.Tags.Length == 1)
				{
					contentObjects.Add(contentObject);
				}
				else if (MetadataItemcontainsAllTags(contentObject.MetadataItems.First(md => md.MetadataItemName == TagType)))
				{
					contentObjects.Add(contentObject);
				}
			}
		}*/

		return contentObjects;
	}

	/// <summary>
	/// Gets the top level album the current user has permission to view. Returns null when the user does not 
	/// have permission to view any albums.
	/// </summary>
	/// <returns>An instance of <see cref="AlbumBo" /> or null.</returns>
	private AlbumBo loadRootAlbumForUser() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		// Get list of root album IDs with view permission.

		// Step 1: Compile a list of album IDs having the requested permissions.
		List<AlbumBo> rootAlbums = getRootAlbumsUserCanView();

		// Step 3: Package results into an album container. If there is only one viewable root album, then just create an instance of that album.
		// Otherwise, create a virtual root album to contain the multiple viewable albums.
		AlbumBo rootAlbum;

		if (rootAlbums.size() == 0)
			return null;

		if (rootAlbums.size() == 1)	{
			rootAlbum = rootAlbums.get(0);
		}else{
			// Create virtual album to serve as a container for the child albums the user has permission to view.
			rootAlbum = CMUtils.createEmptyAlbumInstance(SearchOptions.GalleryId);
			rootAlbum.setIsVirtualAlbum(true);
			rootAlbum.setVirtualAlbumType(VirtualAlbumType.Root);
			rootAlbum.setTitle(I18nUtils.getMessage("album.virtual_Album_Title"));
			rootAlbum.setCaption(StringUtils.EMPTY);
			for (AlbumBo album : rootAlbums){
				rootAlbum.addContentObject(album);
			}
		}

		return rootAlbum;
	}

	/// <summary>
	/// Gets a list of the top-level albums the current user can view. Guaranteed to not return null. Will be empty 
	/// if user does not have access to any albums.
	/// </summary>
	/// <returns>An instance of <see cref="List{AlbumBo}" />.</returns>
	private List<AlbumBo> getRootAlbumsUserCanView() throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		// If user can view the root album, just return that.
		AlbumBo rootAlbum = CMUtils.loadRootAlbumInstance(SearchOptions.GalleryId);

		if (canUserViewAlbum(rootAlbum)){
			return Lists.newArrayList(rootAlbum);
		}else if (!SearchOptions.IsUserAuthenticated){
			// Anonymous user can't view any albums, so just return an empty list.
			return Lists.newArrayList();
		}

		// Logged on user can't see root album, so calculate the top-level list of album IDs they *can* see.
		List<Long> allRootAlbumIds = SearchOptions.Roles.getViewableAlbumIdsForGallery(SearchOptions.GalleryId);

		// Step 2: Convert previous list to contain ONLY top-level albums in the current gallery.
		List<AlbumBo> rootAlbums = removeChildAlbumsAndAlbumsInOtherGalleries(allRootAlbumIds);

		return rootAlbums;
	}

	/// <summary>
	/// Generate a new list containing a subset of <paramref name="allRootAlbumIds" /> that contains only a list of 
	/// top-level album IDs and albums belonging to the gallery specified in the search options.
	/// Any albums that have a parent - at any level - in the list are not included. Guaranteed to not return null.
	/// </summary>
	/// <param name="allRootAlbumIds">All album IDs to process.</param>
	/// <returns>Returns an enumerable list of integers representing the album IDs that satisfy the criteria.</returns>
	private List<AlbumBo> removeChildAlbumsAndAlbumsInOtherGalleries(List<Long> allRootAlbumIds) throws InvalidAlbumException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidGalleryException{
		// Loop through our list of album IDs. If any album has an ancestor that is also in the list, then remove it. 
		// We only want a list of top level albums.
		List<AlbumBo> rootAlbums = new ArrayList<AlbumBo>();
		List<AlbumBo> albumsToRemove = new ArrayList<AlbumBo>();
		for (long viewableAlbumId : allRootAlbumIds){
			AlbumBo album = CMUtils.loadAlbumInstance(viewableAlbumId, false);

			if (album.getGalleryId() != SearchOptions.GalleryId)	{
				// The album belongs to a different gallery, so skip it. It won't get included in the returned collection.
				continue;
			}

			rootAlbums.add(album);

			AlbumBo albumParent = album;

			while (true){
				albumParent = Reflections.as(AlbumBo.class, albumParent.getParent());
				if (albumParent == null)
					break;

				if (allRootAlbumIds.contains(albumParent.getId())){
					albumsToRemove.add(album);
					break;
				}
			}
		}
		
		for (AlbumBo album : albumsToRemove){
			rootAlbums.remove(album);
		}

		return rootAlbums;
	}

	/// <summary>
	/// Wraps the <paramref name="album" /> in a gallery object collection. When <paramref name="album" /> is null,
	/// an empty collection is returned. Guaranteed to no return null. 
	/// </summary>
	/// <param name="album">The album.</param>
	/// <returns>An instance of <see cref="ContentObjectBoCollection" />.</returns>
	private static List<ContentObjectBo> wrapInContentObjectBoCollection(AlbumBo album)	{
		ContentObjectBoCollection result = new ContentObjectBoCollection();

		if (album != null)
			result.add(album);

		return result.values();
	}

	/// <summary>
	/// Finds the gallery objects that have been recently added to the gallery. Guaranteed to not return null.
	/// Only items the current user is authorized to view are returned.
	/// </summary>
	/// <returns><see cref="Iterable&lt;ContentObjectBo&gt;" />.</returns>
	private List<ContentObjectBo> findRecentlyAdded() 
			throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		if (SearchOptions.Filter == ContentObjectType.All || SearchOptions.Filter == ContentObjectType.Album){
			contentObjects.addRange(getRecentlyAddedAlbums(SearchOptions.MaxNumberResults));
		}

		if (SearchOptions.Filter != ContentObjectType.Album){
			contentObjects.addRange(getRecentlyAddedContentObjects(SearchOptions.MaxNumberResults));
		}

		ContentObjectBoCollection filteredContentObjects = filterContentObjects(contentObjects);
		//filteredContentObjects = FilterContentObjectsByApproval(filteredContentObjects);

		if (filteredContentObjects.count() != contentObjects.count() && filteredContentObjects.count() < SearchOptions.MaxNumberResults 
				&& contentObjects.count() >= SearchOptions.MaxNumberResults) {
			// We lost some objects in the filter and now we have less than the desired MaxNumberResults. Get more.
			// Note: Performance can be very poor for large galleries when using a filter. For example, a gallery where 20 videos
			// were added and then 200,000 images were added, a search for the most recent 20 videos causes this algorithm
			// to load all 200,000 images into memory before finding the videos. The good news is that by default the filter
			// is for content objects, which will be very fast. If filters end up being commonly used, this algorithm should be improved.
			int max = SearchOptions.MaxNumberResults * 2;
			int skip = SearchOptions.MaxNumberResults;
			final int maxTries = 5;

			for (int i = 0; i < maxTries; i++){
				// Add items up to maxTries times, each time doubling the number of items to retrieve.
				filteredContentObjects.addRange(getRecentlyAddedAlbums(max, skip));
				filteredContentObjects.addRange(getRecentlyAddedContentObjects(max, skip));

				filteredContentObjects = filterContentObjects(filteredContentObjects);
				//filteredContentObjects = FilterContentObjectsByApproval(filteredContentObjects);

				if (filteredContentObjects.count() >= SearchOptions.MaxNumberResults)
				{
					break;
				}

				if (i < (maxTries - 1))
				{
					skip = skip + max;
					max = max * 2;
				}
			}

			if (filteredContentObjects.count() < SearchOptions.MaxNumberResults){
				// We still don't have enough objects. Search entire set of albums and content objects.
				filteredContentObjects.addRange(getRecentlyAddedAlbums(Integer.MAX_VALUE, skip));
				filteredContentObjects.addRange(getRecentlyAddedContentObjects(Integer.MAX_VALUE, skip));

				filteredContentObjects = filterContentObjects(filteredContentObjects);
				//filteredContentObjects = FilterContentObjectsByApproval(filteredContentObjects);
			}
		}

		if (SearchOptions.MaxNumberResults > 0 && filteredContentObjects.count() > SearchOptions.MaxNumberResults){
			return filteredContentObjects.stream().sorted(Comparator.comparing(ContentObjectBo::getDateAdded).reversed()).limit(SearchOptions.MaxNumberResults).collect(Collectors.toList());
		}

		return filteredContentObjects.values();
	}

	/// <summary>
	/// Gets the <paramref name="top" /> most recently added albums, skipping the first
	/// <paramref name="skip" /> objects. Only albums the current user is authorized to
	/// view are returned.
	/// </summary>
	/// <param name="top">The number of items to retrieve.</param>
	/// <param name="skip">The number of items to skip over in the data store.</param>
	/// <returns><see cref="Iterable&lt;ContentObjectBo&gt;" />.</returns>
	private List<ContentObjectBo> getRecentlyAddedAlbums(int top) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		return getRecentlyAddedAlbums(top, 0);
	}
	
	private List<ContentObjectBo> getRecentlyAddedAlbums(int top, int skip) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException	{
		Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter("gallery.id", SearchOperator.eq, SearchOptions.GalleryId);
        searchable.addSort(Direction.DESC, "dateAdded");
        searchable = restrictAlbumForCurrentUser(searchable);
        if (searchable != null) {
        	searchable.setPage(PageRequest.of(skip, top));
        }

		return CMUtils.findAlbums(searchable).values();
	}

	/// <summary>
	/// Gets the <paramref name="top" /> most recently added content objects, skipping the first
	/// <paramref name="skip" /> objects. Only content objects the current user is authorized to
	/// view are returned.
	/// </summary>
	/// <param name="top">The number of items to retrieve.</param>
	/// <param name="skip">The number of items to skip over in the data store.</param>
	/// <returns><see cref="Iterable&lt;ContentObjectBo&gt;" />.</returns>
	private List<ContentObjectBo> getRecentlyAddedContentObjects(int top) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		return getRecentlyAddedContentObjects(top, 0);
	}
	
	private List<ContentObjectBo> getRecentlyAddedContentObjects(int top, int skip) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, IOException{
		Searchable searchable = Searchable.newSearchable();
        searchable.addSearchFilter("album.gallery.id", SearchOperator.eq, SearchOptions.GalleryId);
        searchable.addSort(Direction.DESC, "dateAdded");
        searchable = restrictContentObjectForCurrentUser(searchable);
        if (searchable != null) {
        	searchable.setPage(PageRequest.of(skip, top));
        }

		return CMUtils.findContentObjects(searchable).values();
	}

	/// <summary>
	/// Finds the gallery objects with the specified rating. Guaranteed to not return null. Albums cannot be 
	/// rated and are thus not returned. Only items the current user is authorized to view are returned.
	/// </summary>
	/// <returns><see cref="Iterable&lt;ContentObjectBo&gt;" />.</returns>
	private List<ContentObjectBo> findContentObjectsMatchingRating(){
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		if (SearchOptions.Filter != ContentObjectType.Album) {
			contentObjects.addRange(getRatedContentObjects(SearchOptions.MaxNumberResults));
		}

		ContentObjectBoCollection filteredContentObjects = filterContentObjects(contentObjects);
		//filteredContentObjects = filterContentObjectsByApproval(filteredContentObjects);

		if (filteredContentObjects.count() != contentObjects.count() && filteredContentObjects.count() < SearchOptions.MaxNumberResults && contentObjects.count() >= SearchOptions.MaxNumberResults)
		{
			// We lost some objects in the filter and now we have less than the desired MaxNumberResults. Get more.
			// Note: Performance can be very poor for large galleries when using a filter. For example, a gallery where 20 videos
			// were added and then 200,000 images were added, a search for the most recent 20 videos causes this algorithm
			// to load all 200,000 images into memory before finding the videos. The good news is that by default the filter
			// is for content objects, which will be very fast. If filters end up being commonly used, this algorithm should be improved.
			int max = SearchOptions.MaxNumberResults * 2;
			int skip = SearchOptions.MaxNumberResults;
			final int maxTries = 5;

			for (int i = 0; i < maxTries; i++){
				// Add items up to maxTries times, each time doubling the number of items to retrieve.
				filteredContentObjects.addRange(getRatedContentObjects(max, skip));

				filteredContentObjects = filterContentObjects(filteredContentObjects);
				//filteredContentObjects = filterContentObjectsByApproval(filteredContentObjects);

				if (filteredContentObjects.count() >= SearchOptions.MaxNumberResults){
					break;
				}

				if (i < (maxTries - 1))	{
					skip = skip + max;
					max = max * 2;
				}
			}

			if (filteredContentObjects.count() < SearchOptions.MaxNumberResults)	{
				// We still don't have enough objects. Search entire set of albums and content objects.
				filteredContentObjects.addRange(getRatedContentObjects(Integer.MAX_VALUE, skip));

				filteredContentObjects = filterContentObjects(filteredContentObjects);
				//filteredContentObjects = FilterContentObjectsByApproval(filteredContentObjects);
			}
		}

		if (SearchOptions.MaxNumberResults > 0 && filteredContentObjects.count() > SearchOptions.MaxNumberResults)	{
			return filteredContentObjects.stream().sorted(Comparator.comparing(ContentObjectBo::getDateAdded).reversed()).limit(SearchOptions.MaxNumberResults).collect(Collectors.toList());
		}

		return filteredContentObjects.values();
	}

	/// <summary>
	/// Gets the <paramref name="top" /> content objects having the specified rating, skipping the first
	/// <paramref name="skip" /> objects. Only content objects the current user is authorized to
	/// view are returned. Albums cannot be rated and are thus not returned.
	/// </summary>
	/// <param name="top">The number of items to retrieve.</param>
	/// <param name="skip">The number of items to skip over in the data store.</param>
	/// <returns><see cref="Iterable&lt;ContentObjectBo&gt;" />.</returns>
	private List<ContentObjectBo> getRatedContentObjects(int top)	{
		return getRatedContentObjects(top, 0);
	}
	
	private List<ContentObjectBo> getRatedContentObjects(int top, int skip)	{
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		/*using (var repo = new ContentObjectRepository())
		{
			IQueryable<ContentObjectDto> qry; 

			switch (SearchOptions.SearchTerms[0].ToLowerInvariant())
			{
				case "highest": // Highest rated objects
					qry = restrictForCurrentUser(repo.Where(mo =>
						mo.Album.FKGalleryId == SearchOptions.GalleryId
						&& mo.Metadata.Any(md => md.MetaName == MetadataItemName.Rating && !StringUtils.isBlank(md.Value)))
						.OrderByDescending(mo => mo.Metadata.Where(md => md.MetaName == MetadataItemName.Rating).Select(md => md.Value).FirstOrDefault())
						.Include(mo => mo.Metadata)
						.Skip(skip).Take(top));
					break;

				case "lowest": // Lowest rated objects
					qry = restrictForCurrentUser(repo.Where(mo =>
						mo.Album.FKGalleryId == SearchOptions.GalleryId
							//&& mo.Metadata.Any(md => md.MetaName == MetadataItemName.Rating && md.Value == "5"))
						&& mo.Metadata.Any(md => md.MetaName == MetadataItemName.Rating && !StringUtils.isBlank(md.Value)))
						.OrderBy(mo => mo.Metadata.Where(md => md.MetaName == MetadataItemName.Rating).Select(md => md.Value).FirstOrDefault())
						.Include(mo => mo.Metadata)
						.Skip(skip).Take(top));
					break;

				case "none": // Having no rating
					qry = restrictForCurrentUser(repo.Where(mo =>
						mo.Album.FKGalleryId == SearchOptions.GalleryId
						&& mo.Metadata.Any(md => md.MetaName == MetadataItemName.Rating && StringUtils.isBlank(md.Value)))
						.OrderBy(mo => mo.DateAdded)
						.Include(mo => mo.Metadata)
						.Skip(skip).Take(top));
					break;

				default: // Look for a specific rating
					var r = ParseRating(SearchOptions.SearchTerms[0]);
					if (r != null)
					{
						qry = restrictForCurrentUser(repo.Where(mo =>
							mo.Album.FKGalleryId == SearchOptions.GalleryId
							&& mo.Metadata.Any(md => md.MetaName == MetadataItemName.Rating && r.contains(md.Value)))
							.OrderBy(mo => mo.DateAdded)
							.Include(mo => mo.Metadata)
							.Skip(skip).Take(top));
					}
					else
					{
						// The search term is a String other than highest, lowest, none or a decimal. Don't return anything.
						qry = repo.Where(mo => false);
					}
					break;
			}

			for (var contentObject in qry)
			{
				contentObjects.Add(CMUtils.GetContentObjectFromDto(contentObject, null));
			}
		}*/

		return contentObjects.values();
	}

	/// <summary>
	/// Finds the gallery objects with the specified rating. Guaranteed to not return null. Albums cannot be 
	/// rated and are thus not returned. Only items the current user is authorized to view are returned.
	/// </summary>
	/// <returns><see cref="Iterable&lt;ContentObjectBo&gt;" />.</returns>
	/*private List<ContentObjectBo> FindContentObjectsMatchingApproval()
	{
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		if (SearchOptions.Filter != ContentObjectType.Album)
		{
			contentObjects.addRange(GetApprovalContentObjects(SearchOptions.MaxNumberResults));
		}

		var filteredContentObjects = FilterContentObjects(contentObjects);
		filteredContentObjects = FilterContentObjectsByApproval(filteredContentObjects);

		if (filteredContentObjects.size() != contentObjects.size() && filteredContentObjects.size() < SearchOptions.MaxNumberResults && contentObjects.size() >= SearchOptions.MaxNumberResults)
		{
			// We lost some objects in the filter and now we have less than the desired MaxNumberResults. Get more.
			// Note: Performance can be very poor for large galleries when using a filter. For example, a gallery where 20 videos
			// were added and then 200,000 images were added, a search for the most recent 20 videos causes this algorithm
			// to load all 200,000 images into memory before finding the videos. The good news is that by default the filter
			// is for content objects, which will be very fast. If filters end up being commonly used, this algorithm should be improved.
			var max = SearchOptions.MaxNumberResults * 2;
			var skip = SearchOptions.MaxNumberResults;
			const int maxTries = 5;

			for (var i = 0; i < maxTries; i++)
			{
				// Add items up to maxTries times, each time doubling the number of items to retrieve.
				filteredContentObjects.addRange(GetRatedContentObjects(max, skip));

				filteredContentObjects = FilterContentObjects(filteredContentObjects);
				filteredContentObjects = FilterContentObjectsByApproval(filteredContentObjects);

				if (filteredContentObjects.size() >= SearchOptions.MaxNumberResults)
				{
					break;
				}

				if (i < (maxTries - 1))
				{
					skip = skip + max;
					max = max * 2;
				}
			}

			if (filteredContentObjects.size() < SearchOptions.MaxNumberResults)
			{
				// We still don't have enough objects. Search entire set of albums and content objects.
				filteredContentObjects.addRange(GetRatedContentObjects(Integer.MAX_VALUE, skip));

				filteredContentObjects = filterContentObjects(filteredContentObjects);
				filteredContentObjects = filterContentObjectsByApproval(filteredContentObjects);
			}
		}

		if (SearchOptions.MaxNumberResults > 0 && filteredContentObjects.size() > SearchOptions.MaxNumberResults)
		{
			return filteredContentObjects.OrderByDescending(g => g.DateAdded).Take(SearchOptions.MaxNumberResults);
		}

		return filteredContentObjects;
	}*/

	/// <summary>
	/// Gets the <paramref name="top" /> content objects having the specified rating, skipping the first
	/// <paramref name="skip" /> objects. Only content objects the current user is authorized to
	/// view are returned. Albums cannot be rated and are thus not returned.
	/// </summary>
	/// <param name="top">The number of items to retrieve.</param>
	/// <param name="skip">The number of items to skip over in the data store.</param>
	/// <returns><see cref="Iterable&lt;ContentObjectBo&gt;" />.</returns>
/*	private List<ContentObjectBo> GetApprovalContentObjects(int top, int skip = 0)
	{
		ContentObjectBoCollection contentObjects = new ContentObjectBoCollection();

		using (var repo = new ContentObjectRepository())
		{
			IQueryable<ContentObjectDto> qry;

			qry = restrictForCurrentUser(repo.Where(mo =>
				mo.Album.FKGalleryId == SearchOptions.GalleryId, mo => mo.Metadata, mo => mo.ApprovalData));
				//&& mo.ApprovalData.Any(ad => ad.ApprovalStatus == (short)SearchOptions.ApprovalFilter)));
				//.OrderByDescending(mo => mo.ApprovalData.Where(ad => ad.ApprovalStatus == (short)SearchOptions.ApprovalFilter).Select(ad => ad.dtLastModify).FirstOrDefault())
				//.Include(mo => mo.ApprovalData)
				//.Skip(skip).Take(top));

			for (var contentObject in qry)
			{
				contentObjects.Add(CMUtils.GetContentObjectFromDto(contentObject, null));
			}
		}

		return contentObjects;
	}*/

	/// <summary>
	/// Parses the <paramref name="rating" /> into an array of Strings that may exist in the database.
	/// For example, a rating of "3" returns {"3", "3.", "3.0", "3.00", "3.000", "3.0000"}. A rating of
	/// "4.5" returns {"4.5", "4.50", "4.500", "4.5000"}. If the rating cannot be parsed into a decimal,
	/// null is returned.
	/// </summary>
	/// <param name="rating">The rating, in half step increments from 0 to 5. (eg. "3", "3.0000", "4.5", "4.5000").</param>
	/// <returns>Iterable&lt;System.String&gt;.</returns>
	private static Iterable<String> parseRating(String rating){
		String[] ratings = null;

		int ratingInt = StringUtils.toInteger(rating, Integer.MIN_VALUE);
		if (ratingInt != Integer.MIN_VALUE){
			ratings = new String[]
				{
					String.valueOf(ratingInt), // Eg. "3"
					StringUtils.join(new Object[] {ratingInt, "."}), // Eg. "3."
				};
		}

		BigDecimal ratingDecimal = StringUtils.toDecimal(rating, BigDecimal.ZERO);
		if (ratingDecimal != BigDecimal.ZERO ){
			if (ratings == null){
				ratings = new String[] {};
			}
			
			List<String> rateList = Arrays.asList(ratings);
			rateList.addAll(Lists.newArrayList(
				  new DecimalFormat("#.0").format(ratingDecimal), // Eg. "3.0"
				  new DecimalFormat("#.00").format(ratingDecimal), // Eg. "3.00"
				  new DecimalFormat("#.000").format(ratingDecimal), // Eg. "3.000"
				  new DecimalFormat("#.0000").format(ratingDecimal) // Eg. "3.0000"
			  ));

			return rateList;
		}

		return null;
	}

	/// <summary>
	/// Filters the <paramref name="contentObjects" /> by the filter specified in <see cref="SearchOptions" />.
	/// </summary>
	/// <param name="contentObjects">The gallery objects.</param>
	/// <returns>An instance of <see cref="ContentObjectBoCollection" />.</returns>
	private ContentObjectBoCollection filterContentObjects(ContentObjectBoCollection contentObjects){
		switch (SearchOptions.Filter){
			case Album:
				return new ContentObjectBoCollection(contentObjects.stream().filter(go -> go.getContentObjectType() == ContentObjectType.Album).collect(Collectors.toList()));

			case ContentObject:
				return new ContentObjectBoCollection(contentObjects.stream().filter(go -> go.getContentObjectType() != ContentObjectType.Album).collect(Collectors.toList()));

			case NotSpecified:
			case All:
				return contentObjects;

			case None:
				return new ContentObjectBoCollection();

			default:
				return new ContentObjectBoCollection(contentObjects.stream().filter(go -> go.getContentObjectType() == SearchOptions.Filter).collect(Collectors.toList()));
		}
	}

	/// <summary>
	/// Filters the <paramref name="contentObjects" /> by the filter specified in <see cref="SearchOptions" />.
	/// </summary>
	/// <param name="contentObjects">The gallery objects.</param>
	/// <returns>An instance of <see cref="ContentObjectBoCollection" />.</returns>
/*	private ContentObjectBoCollection filterContentObjectsByApproval(ContentObjectBoCollection contentObjects)	{
		switch (SearchOptions.ApprovalFilter)
		{
			case ContentObjectApproval.All:
				return contentObjects;

			default:
				return new ContentObjectBoCollection(contentObjects.Where(go => go.ApprovalStatus == SearchOptions.ApprovalFilter));
		}
	}*/

	/// <summary>
	/// Modify the <paramref name="qry" /> so that it only returns albums the current user has permission
	/// to view.
	/// </summary>
	/// <param name="qry">The query.</param>
	/// <returns><see cref="IQueryable&lt;AlbumDto&gt;" />.</returns>
	public Searchable restrictAlbumForCurrentUser(Searchable qry) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		if (SearchOptions.IsUserAuthenticated){
			AlbumBo rootAlbum = CMUtils.loadRootAlbumInstance(SearchOptions.GalleryId);

			if (!canUserViewAlbum(rootAlbum)){
				// User can't view the root album, so get a list of the albums she *can* see and make sure our 
				// results only include albums that are viewable.
				List<Long> albumIds = SearchOptions.Roles.getViewableAlbumIdsForGallery(SearchOptions.GalleryId);

				qry.addSearchFilter("id", SearchOperator.in, albumIds);
			}
		}
		else if (CMUtils.loadGallerySetting(SearchOptions.GalleryId).getAllowAnonymousBrowsing())
		{
			// Anonymous user, so don't include any private albums in results.
			qry.addSearchFilter("isPrivate", SearchOperator.eq, false);
		}
		else
		{
			// Anonymous user & gallery is configured to prevent anonymous users, so force query to return nothing.
			return null;
		}

		return qry;
	}

	/// <summary>
	/// Modify the <paramref name="qry" /> so that it only returns content objects the current user has permission
	/// to view.
	/// </summary>
	/// <param name="qry">The query.</param>
	/// <returns><see cref="IQueryable&lt;ContentObjectDto&gt;" />.</returns>
	public Searchable restrictContentObjectForCurrentUser(Searchable qry) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException{
		if (SearchOptions.IsUserAuthenticated){
			AlbumBo rootAlbum = CMUtils.loadRootAlbumInstance(SearchOptions.GalleryId);

			if (!canUserViewAlbum(rootAlbum)){
				// User can't view the root album, so get a list of the albums she *can* see and make sure our 
				// results only include content objects that are viewable.
				List<Long> albumIds = SearchOptions.Roles.getViewableAlbumIdsForGallery(SearchOptions.GalleryId);

				qry.addSearchFilter("album.id", SearchOperator.in, albumIds);
			}
		}else if (CMUtils.loadGallerySetting(SearchOptions.GalleryId).getAllowAnonymousBrowsing()){
			// Anonymous user, so don't include any private albums in results.
			qry.addSearchFilter("isPrivate", SearchOperator.eq, false);
		}else{
			// Anonymous user & gallery is configured to prevent anonymous users, so force query to return nothing.
			return null;
		}

		return qry;
	}

	//#endregion
}
