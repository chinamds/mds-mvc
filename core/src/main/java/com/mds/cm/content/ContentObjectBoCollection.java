package com.mds.cm.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.metadata.ContentObjectMetadataItem;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.BusinessException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.ContentObjectType;
import com.mds.core.MetadataItemName;
import com.mds.cm.util.CMUtils;
import com.mds.util.DateUtils;


/// <summary>
/// An unsorted collection of <see cref="IContentObjectBo" /> objects.
/// </summary>
public class ContentObjectBoCollection{
	//#region Properties

	/// <summary>
	/// Gets or sets the gallery objects in this collection. We prefer a dictionary over <see cref="ConcurrentBag&lt;IContentObjectBo&gt;" />
	/// primarily because the dictionary enforces unique keys, while the bag might allows duplicates.
	/// </summary>
	/// <value>The items.</value>
	private ConcurrentHashMap<String, ContentObjectBo> items;
	
	private ConcurrentHashMap<String, ContentObjectBo> getItems() { 
		return items; 
	}
	
	private void setItems(ConcurrentHashMap<String, ContentObjectBo> items) { 
		this.items = items; 
	}
	
	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectBoCollection"/> class.
	/// </summary>
	public ContentObjectBoCollection()	{
		items = new ConcurrentHashMap<String, ContentObjectBo>();
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectBoCollection" /> class with the specified <paramref name="items" />.
	/// </summary>
	/// <param name="items">The items to add to the collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="items" /> is null.</exception>
	public ContentObjectBoCollection(Iterable<ContentObjectBo> items){
		if (items == null)
			throw new ArgumentNullException("items");

		this.items = new ConcurrentHashMap<String, ContentObjectBo>();
		items.forEach(item->{
			this.items.put(getKey(item), item);
		});
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Gets the number of gallery objects in the collection.
	/// </summary>
	/// <value>The count.</value>
	public int count()	{
		return items.size();
	}
	
	public Stream<ContentObjectBo> stream()	{
		return items.values().stream();
	}
	
	public List<ContentObjectBo> values()	{
		return Lists.newArrayList(items.values());
	}
	
	public List<AlbumBo> toAlbums()	{
		//List<ContentObjectBo> contentObjects = Lists.newArrayList(items.values());
		List<? extends ContentObjectBo> contentObjects = Lists.newArrayList(items.values());
		//List contentObjects = Lists.newArrayList(items.values());
		return (List<AlbumBo>)contentObjects;
	}

	/// <summary>
	/// Adds the specified gallery object.
	/// </summary>
	/// <param name="item">The gallery object.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void add(ContentObjectBo item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing ContentObjectBoCollection. Items.Count = " + items.size());

		items.put(getKey(item), item);
	}

	/// <summary>
	/// Adds the ContentObjectBos to the current collection.
	/// </summary>
	/// <param name="ContentObjectBos">The gallery objects to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="ContentObjectBos" /> is null.</exception>
	public void addRange(Iterable<ContentObjectBo> contentObjectBos){
		if (contentObjectBos == null)
			throw new ArgumentNullException("ContentObjectBos");

		for(ContentObjectBo contentObjectBo : contentObjectBos)	{
			items.put(getKey(contentObjectBo), contentObjectBo);
		}
	}

	public void remove(ContentObjectBo item){
		items.remove(getKey(item));
	}

	/// <summary>
	/// Creates a collection sorted on the <see cref="IContentObjectBo.Sequence" /> property.
	/// </summary>
	/// <returns>An instance of IList{IContentObjectBo}.</returns>
	@SuppressWarnings("unchecked")
	public List<ContentObjectBo> toSortedList()	{
		List<ContentObjectBo> contentObjects = Lists.newArrayList(items.values()); //new List<ContentObjectBo>(items.values());
		Collections.sort(contentObjects);

		return contentObjects;
	}
	
	@SuppressWarnings("unchecked")
	public List<AlbumBo> toAlbumSortedList()	{
		List<? extends ContentObjectBo> contentObjects = Lists.newArrayList(items.values());
		Collections.sort(contentObjects);

		return (List<AlbumBo>)contentObjects;
	}
	
	/// <summary>
	/// Sorts the gallery objects in this collection by <paramref name="sortByMetaName" /> in the order specified by
	/// <paramref name="sortAscending" />. The <paramref name="galleryId" /> is used to look up the applicable
	/// <see cref="IGallerySettings.MetadataDisplaySettings" />.
	/// </summary>
	/// <param name="sortByMetaName">The name of the metadata item to sort on.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>An instance of IList{IContentObjectBo}.</returns>
	public List<ContentObjectBo> toSortedList(MetadataItemName sortByMetaName, boolean sortAscending, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (sortByMetaName == MetadataItemName.NotSpecified){
			// This is a custom sort, so sort based on the Sequence property.
			return toSortedList();
		}

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		if (gallerySetting.getMetadataDisplaySettings().find(sortByMetaName).getDataType() == Date.class)
			return sortByDateTime(sortByMetaName, sortAscending, gallerySetting.getMetadataDateTimeFormatString());
		else
			return sortByString(sortByMetaName, sortAscending);
	}

	/// <summary>
	/// Sorts the gallery objects in the collection by the timestamp specified in <paramref name="sortByMetaName" />.
	/// </summary>
	/// <param name="sortByMetaName">The name of the metadata item to sort on. It is expected this meta item can be
	///  converted to a <see cref="DateTime" /> instance.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <param name="metadataDateTimeFormatString">The format String representing the format that the meta values are stored in.</param>
	/// <returns>Returns a collection of <see cref="IContentObjectBo" /> instances.</returns>
	private List<ContentObjectBo> sortByDateTime(MetadataItemName sortByMetaName, boolean sortAscending, String metadataDateTimeFormatString){
		
		/*Map<Date, ContentObjectBo> sortedMap = new TreeMap<Date, ContentObjectBo>(new Comparator<Date>() {
			public int compare(Date key1, Date key2) {
				return key1.compareTo(key2);
		}});*/
		
		// Step 1: Sort the albums
		List<ContentObjectBo> childAlbums = items.values().stream().filter(g -> g.getContentObjectType() == ContentObjectType.Album)
			.collect(Collectors.toMap(a->a, a->{
				for(ContentObjectMetadataItem md : a.getMetadataItems()){
					if (md.getMetadataItemName() == sortByMetaName)
						return DateUtils.parseDate(md.getValue(), metadataDateTimeFormatString);
				}
				
				return a.getDateAdded();
			})).entrySet().stream().sorted(Map.Entry.comparingByValue()).map(a->a.getKey()).collect(Collectors.toList());
		
/*		sortedMap.putAll(childAlbumsMap);
		List<ContentObjectBo> childAlbums = (List<ContentObjectBo>) childAlbumsMap.values();*/
		
			//.OrderBy(kvp => kvp.Key)
			//.Select(kvp => kvp.Value);

		// Step 2: Sort the content objects
		//var contentObjects = album.GetChildContentObjectBos(ContentObjectBoType.ContentObject, !Utils.IsAuthenticated).AsQueryable()
		List<ContentObjectBo> contentObjects = items.values().stream().filter(g -> g.getContentObjectType() != ContentObjectType.Album)
				.collect(Collectors.toMap(a->a, a->{
					for(ContentObjectMetadataItem md : a.getMetadataItems()){
						if (md.getMetadataItemName() == sortByMetaName)
							return DateUtils.parseDate(md.getValue(), metadataDateTimeFormatString);
					}
					
					return a.getDateAdded();
				})).entrySet().stream().sorted(Map.Entry.comparingByValue()).map(a->a.getKey()).collect(Collectors.toList());
			//.OrderBy(kvp => kvp.Key)
			//.Select(kvp => kvp.Value);

		if (!sortAscending)
		{
			childAlbums = childAlbums.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
			contentObjects = contentObjects.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
		}

		// Step 3: Concatenate the two lists and return.
		//childAlbums.addAll(contentObjects);
		
		return ListUtils.union(childAlbums, contentObjects);
	}

	private static ContentObjectMetadataItem getEmptyMetadataItem(MetadataItemName metaName, ContentObjectBo contentObject, String value) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		return new ContentObjectMetadataItem(Long.MIN_VALUE, contentObject, null, value, false, contentObject.getMetaDefinitions().find(metaName));
	}

	/// <summary>
	/// Sorts the gallery objects in the collection by the property specified in <paramref name="sortByMetaName" />.
	/// A String sort is used.
	/// </summary>
	/// <param name="sortByMetaName">The name of the metadata item to sort on.</param>
	/// <param name="sortAscending">If set to <c>true</c> sort in ascending order.</param>
	/// <returns>Returns a collection of <see cref="IContentObjectBo" /> instances.</returns>
	private List<ContentObjectBo> sortByString(MetadataItemName sortByMetaName, boolean sortAscending){
		List<ContentObjectBo> childAlbums = items.values().stream().filter(g -> g.getContentObjectType() == ContentObjectType.Album).collect(Collectors.toList());
		childAlbums.sort(new Comparator<ContentObjectBo>() {
				@Override
				public int compare(ContentObjectBo o1, ContentObjectBo o2) {
					String val1 = null;
					for(ContentObjectMetadataItem md : o1.getMetadataItems()){
						if (md.getMetadataItemName() == sortByMetaName) {
							val1=md.getValue();
						}
					}
					String val2 = null;
					for(ContentObjectMetadataItem md : o2.getMetadataItems()){
						if (md.getMetadataItemName() == sortByMetaName) {
							val2=md.getValue();
						}
					}
					if (val1 == null)
						return 0;
					if (val2 == null)
						return 1;
					
					return val1.compareTo(val2);
				}
			}); //(g1, g2) -> g1.getMetadataItems()..Where(mi => mi.MetadataItemName == sortByMetaName).Select(mi => mi.Value).Distinct().FirstOrDefault()).Select(a => a);

		
		List<ContentObjectBo> contentObjects = items.values().stream().filter(g -> g.getContentObjectType() != ContentObjectType.Album).collect(Collectors.toList());
		contentObjects.sort(new Comparator<ContentObjectBo>() {
					@Override
					public int compare(ContentObjectBo o1, ContentObjectBo o2) {
						String val1 = null;
						for(ContentObjectMetadataItem md : o1.getMetadataItems()){
							if (md.getMetadataItemName() == sortByMetaName) {
								val1=md.getValue();
							}
						}
						String val2 = null;
						for(ContentObjectMetadataItem md : o2.getMetadataItems()){
							if (md.getMetadataItemName() == sortByMetaName) {
								val2=md.getValue();
							}
						}
						if (val1 == null)
							return 0;
						if (val2 == null)
							return 1;
						
						return val1.compareTo(val2);
					}
				}); 
		
		//var contentObjects = Items.Values.Where(g => g.ContentObjectBoType != ContentObjectBoType.Album)
		//	.OrderBy(g => g.MetadataItems.Where(mi => mi.MetadataItemName == sortByMetaName).Select(mi => mi.Value).Distinct().FirstOrDefault()).Select(mo => mo);

		if (!sortAscending)
		{
			//Collections.reverse(childAlbums);
			childAlbums = childAlbums.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
			contentObjects = contentObjects.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
		}

		return ListUtils.union(childAlbums, contentObjects);
	}

	/// <summary>
	/// Determines whether the <paramref name="item"/> is already a member of the collection. An object is considered a member
	/// of the collection if one of the following scenarios is true: (1) They are both of the same type, each ID is 
	/// greater than Long.MIN_VALUE, and the IDs are equal to each other, or (2) They are new objects that haven't yet
	/// been saved to the data store, the physical path to the original file has been specified, and the paths
	/// are equal to each other.
	/// </summary>
	/// <param name="item">An <see cref="IContentObjectBo"/> to determine whether it is a member of the current collection.</param>
	/// <returns>Returns <c>true</c> if <paramref name="item"/> is a member of the current collection;
	/// otherwise returns <c>false</c>.</returns>
	public boolean contains(ContentObjectBo item){
		if (item == null)
			return false;

		for(ContentObjectBo contentObjectIterator : items.values())	{
			if (contentObjectIterator == null)
				throw new BusinessException("Error in ContentObjectBoCollection.Contains method: One of the objects in the Items property is null. Items.Count = " + items.size());

			boolean existingObjectsAndEqual = ((contentObjectIterator.getId() > Long.MIN_VALUE) && (contentObjectIterator.getId() == item.getId()) && (contentObjectIterator.getClass()== item.getClass()));

			boolean newObjectsAndFilepathsAreEqual = ((contentObjectIterator.isNew) && (item.isNew)
													 && (!StringUtils.isBlank(contentObjectIterator.getOriginal().getFileNamePhysicalPath()))
													 && (!StringUtils.isBlank(item.getOriginal().getFileNamePhysicalPath()))
													 && (contentObjectIterator.getOriginal().getFileNamePhysicalPath().equals(item.getOriginal().getFileNamePhysicalPath())));

			if (existingObjectsAndEqual || newObjectsAndFilepathsAreEqual){
				return true;
			}
		}
		
		return false;
	}

	/// <summary>
	/// Returns an enumerator that iterates through the collection.
	/// </summary>
	/// <returns>A <see cref="T:System.Collections.Generic.Iterable`1" /> that can be used to iterate through the collection.</returns>
	/*public Iterable<ContentObjectBo> getEnumerator(){
		//return (Iterable<ContentObjectBo>) items.values().iterator();
		List<ContentObjectBo> contentObjects = Lists.newArrayList(items.values());
		
		return contentObjects;
	}*/

	//#endregion

	//#region Functions

	/// <summary>
	/// Gets a String that uniquely identifies the gallery object.
	/// </summary>
	/// <param name="item">The item.</param>
	/// <returns>System.String.</returns>
	private String getKey(ContentObjectBo item)	{
		return (item.getId() > Long.MIN_VALUE ? StringUtils.join(new Object[] {item.getId(), item.getContentObjectType()}) : UUID.randomUUID().toString());
	}

	//#endregion
}
