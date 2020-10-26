/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;

/// <summary>
/// A collection of <see cref="ContentTemplateBo" /> objects.
/// </summary>
public class ContentTemplateBoCollection extends ArrayList<ContentTemplateBo>{
	/// <summary>
	/// Initializes a new instance of the <see cref="ContentTemplateCollection"/> class.
	/// </summary>
	public ContentTemplateBoCollection(){
		super(new ArrayList<ContentTemplateBo>());
	}

	/// <summary>
	/// Adds the specified item.
	/// </summary>
	/// <param name="item">The item.</param>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addContentTemplate(ContentTemplateBo item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing ContentTemplateCollection. Items.Count = " + size());

		add(item);
	}

	/// <summary>
	/// Adds the media templates to the current collection.
	/// </summary>
	/// <param name="mediaTemplates">The media templates to add to the current collection.</param>
	/// <exception cref="System.ArgumentNullException">mediaTemplates</exception>
	public void addRange(Iterable<ContentTemplateBo> mediaTemplates){
		if (mediaTemplates == null)
			throw new ArgumentNullException("mediaTemplates");

		for (ContentTemplateBo item : mediaTemplates){
			add(item);
		}
	}

	/// <overloads>
	/// Finds the matching media template in the collection, or null if no match is found.
	/// </overloads>
	/// <summary>
	/// Gets the most specific <see cref="ContentTemplateBo" /> item that matches one of the <paramref name="browserIds" />, or 
	/// null if no match is found. This method loops through each of the browser IDs in <paramref name="browserIds" />, 
	/// starting with the most specific item, and looks for a match in the current collection.
	/// </summary>
	/// <param name="browserIds">A <see cref="System.Array"/> of browser ids for the current browser. This is a list of Strings,
	/// ordered from most general to most specific, that represent the various categories of browsers the current
	/// browser belongs to. This is typically populated by calling ToArray() on the Request.Browser.Browsers property.
	/// </param>
	/// <returns>The <see cref="ContentTemplateBo" /> that most specifically matches one of the <paramref name="browserIds" />; 
	/// otherwise, a null reference.</returns>
	/// <example>During a request where the client is Firefox, the Request.Browser.Browsers property returns an ArrayList with these 
	/// five items: default, mozilla, gecko, mozillarv, and mozillafirefox. This method starts with the most specific item 
	/// (mozillafirefox) and looks in the current collection for an item with this browser ID. If a match is found, that item 
	/// is returned. If no match is found, the next item (mozillarv) is used as the search parameter.  This continues until a match 
	/// is found. If no match is found, a null is returned.
	/// </example>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="browserIds" /> is null.</exception>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="browserIds" /> does not have any items.</exception>
	public ContentTemplateBo find(List browserIds){
		if (browserIds == null)
			throw new ArgumentNullException("browserIds");

		// If there is only a single item in our collection, there is no need to search, so just return it. This should be the most
		// common situation - the item will have a BrowserId of "default", meaning it matches all browsers.
		if (size() == 1){
			return get(0);
		}

		if (size() == 0){
			return null;
		}

		if (browserIds.size() == 0)
			throw new ArgumentOutOfRangeException("browserIds", "The Array parameter \"browserIds\" must have at least one item, but it was passed with 0 items.");

		ContentTemplateBo matchingBrowser = null;

		// We want to iterate through each browserId, starting with the most specific id and ending with the most general (id="Default"). However, we can't
		// be sure whether the first or last item has the most specific ID, so we check the first item. If it is "default", then we loop backwards;
		// otherwise we loop forwards.
		if (browserIds.get(0).toString().equalsIgnoreCase("default")){
			// Loop backwards. For each item, do we have an item in our collection with a matching browser ID?
			for (int index = browserIds.size() - 1; index >= 0; index--){
				String browserId = browserIds.get(index).toString();
				matchingBrowser = find(browserId);
				if (matchingBrowser != null){
					break;
				}
			}
		}else{
			// Loop forwards. For each item, do we have an item in our collection with a matching browser ID?
			for (int index = 0; index < browserIds.size(); index++)	{
				String browserId = browserIds.get(index).toString();
				matchingBrowser = find(browserId);
				if (matchingBrowser != null){
					break;
				}
			}
		}

		return matchingBrowser;
	}

	/// <summary>
	/// Gets the <see cref="ContentTemplateBo" /> item that matches the <paramref name="browserId" />, or null if no match is found.
	/// </summary>
	/// <param name="browserId">The identifier of a browser as specified in the .Net Framework's browser definition file. Typically
	/// this parameter is populated from one of the entries in the Browsers property of the HttpContext.Current.Request.Browser object.</param>
	/// <returns>Returns the <see cref="ContentTemplateBo" /> item that matches the <paramref name="browserId" />, or null if no match is found.</returns>
	public ContentTemplateBo find(String browserId)	{
		return stream().filter(item -> item.BrowserId.equalsIgnoreCase(browserId)).findFirst().orElse(null);
	}

	/// <summary>
	/// Gets one or more media templates in the collection that match the <paramref name="mimeType" />. If no item is found, then
	/// the MIME type that matches the major portion is returned. For example, if the collection does not contain a specific item 
	/// for "image/jpeg", then the MIME type for "image/*" is returned. This method returns multiple items when more than one 
	/// template has been specified for browsers. That is, all returned items will have the same value for 
	/// <see cref="ContentTemplateBo.MimeType" /> but the <see cref="ContentTemplateBo.BrowserId" /> property will vary. At least one
	/// item in the collection will have the <see cref="ContentTemplateBo.BrowserId" /> property set to "default". Guaranteed to not
	/// return null. If no items are found (which shouldn't happen), an empty collection is returned.
	/// </summary>
	/// <param name="mimeType">The MIME type for which to retrieve matching media templates.</param>
	/// <returns>Returns a <see cref="ContentTemplateBoCollection" /> containing media templates that match the 
	/// <paramref name="mimeType" />. </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="mimeType" /> is null.</exception>
	public ContentTemplateBoCollection find(MimeTypeBo mimeType)	{
		if (mimeType == null)
			throw new ArgumentNullException("mimeType");

		ContentTemplateBoCollection copy = new ContentTemplateBoCollection();
		String fullType = mimeType.getFullType();
		for (ContentTemplateBo item : this)	{
			if (item.MimeType.equalsIgnoreCase(fullType)){
				copy.add(item);
			}
		}
		
		if (!hasDefaultTemplate(copy)){
			// No specific MIME type was found (such as "video/mp4"), or one was found but it
			// didn't have a default variant. Find the generic ones (such as "video/*").
			String genericMimeType = mimeType.getMajorType().concat("/*");
			for(ContentTemplateBo item : this){
				if (item.MimeType.equalsIgnoreCase(genericMimeType)){
					copy.add(item);
				}
			}
		}

		return copy;
	}

	private boolean hasDefaultTemplate(ContentTemplateBoCollection items){
		return items.stream().anyMatch(t -> t.BrowserId.equalsIgnoreCase("default"));
	}

	/// <summary>
	/// Creates a deep copy of this instance.
	/// </summary>
	/// <returns>Returns a deep copy of this instance.</returns>
	public ContentTemplateBoCollection copy(){
		ContentTemplateBoCollection copy = new ContentTemplateBoCollection();
		for (ContentTemplateBo item : this)	{
			copy.add(item.Copy());
		}

		return copy;
	}

	/// <summary>
	/// Creates a new, empty instance of an <see cref="ContentTemplateBo" /> object. This method can be used by code that only has a 
	/// reference to the interface layer and therefore cannot create a new instance of an object on its own.
	/// </summary>
	/// <returns>Returns a new, empty instance of an <see cref="ContentTemplateBo" /> object.</returns>
	public ContentTemplateBo CreateEmptyContentTemplateInstance(){
		return new ContentTemplateBo();
	}
}
