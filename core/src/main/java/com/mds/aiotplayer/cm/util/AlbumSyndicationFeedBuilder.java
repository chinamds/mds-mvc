/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.util;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.service.FeedService;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.DisplayObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.mds.aiotplayer.util.HtmlValidator;
import com.mds.aiotplayer.util.StringUtils;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.SyndPersonImpl;

/// <summary>
/// Contains functionality for building a syndication feed for an album and it's immediate children.
/// </summary>
public class AlbumSyndicationFeedBuilder{
	private AlbumBo album;
	private ContentObjectHtmlBuilderOptions options;
	/// <summary>
	/// Gets or sets the album.
	/// </summary>
	private AlbumBo getAlbum() {
		return this.album; 
	}
	
	private void setAlbum(AlbumBo album) {
		this.album = album; 
	}

	/// <summary>
	/// Gets the content objects belonging to the <see cref="Album" />. If a sort field is specified on the
	/// album, the children are sorted accordingly; otherwise they are returned in the order defined by the
	/// <see cref="AlbumBo.Sequence" /> property.
	/// </summary>
	private List<ContentObjectBo> getContentObjects() throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (MetadataItemName.isValidFormattedMetadataItemName(album.getFeedFormatterOptions().SortByMetaName) && album.getFeedFormatterOptions().SortByMetaName != MetadataItemName.NotSpecified){
			return album.getChildContentObjects(ContentObjectType.All, ApprovalStatus.All, !options.IsAuthenticated).toSortedList(album.getFeedFormatterOptions().SortByMetaName, album.getFeedFormatterOptions().SortAscending, album.getGalleryId());
		}else{
			return album.getChildContentObjects(ContentObjectType.All, ApprovalStatus.All, !options.IsAuthenticated).toSortedList();
		}
	}

	/// <summary>
	/// Gets or sets the options that direct the creation of HTML and URLs for a content object.
	/// </summary>
	private ContentObjectHtmlBuilderOptions getOptions(){
		return this.options; 
	}
	
	private void setOptions(ContentObjectHtmlBuilderOptions options){
		this.options = options; 
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="AlbumSyndicationFeedBuilder"/> class.
	/// </summary>
	/// <param name="album">The album used to generate the syndication feed.</param>
	/// <param name="moBuilderOptions">The options that direct the creation of HTML and URLs for a content object.</param>
	public AlbumSyndicationFeedBuilder(AlbumBo album, ContentObjectHtmlBuilderOptions moBuilderOptions)	{
		this.album = album;

		this.options = moBuilderOptions;
	}

	/// <summary>
	/// Generates the syndication feed.
	/// </summary>
	/// <returns>An instance of <see cref="SyndicationFeed" />.</returns>
	public SyndFeed generate(String feedType) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedEncodingException, WebException, InvalidAlbumException, UnsupportedImageTypeException{
		String galleryTitle = CMUtils.loadGallerySetting(this.options.ContentObject.getGalleryId()).getGalleryTitle();
		String feedTitle = StringUtils.join(galleryTitle, ": ", rssEncode(HtmlValidator.removeHtml(this.album.getTitle(), false)));
		String feedDesc = rssEncode(HtmlValidator.removeHtml(this.album.getCaption(), false));

		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType( feedType );
	    feed.setTitle( feedTitle );
	    feed.setLink(StringUtils.join(this.options.HostUrl, this.options.DestinationPageUrl));
	    feed.setDescription(feedDesc);
	    
		String email = AppSettings.getInstance().getEmailFromAddress();
		if (!StringUtils.isBlank(email)){
			SyndPerson author = new SyndPersonImpl();
			author.setEmail(email);
			feed.getAuthors().add(author);
		}

		SyndCategory category = new SyndCategoryImpl();
		category.setName("Content");
		feed.getCategories().add(category);

		// Get the album thumbnail image.
		if (!this.album.getIsVirtualAlbum())	{
			feed.setUri(new ContentObjectHtmlBuilder(this.options).getContentObjectUrl());
		}

		feed.setEntries(getContentObjects().stream().map(contentObject -> buildSyndicationItem(contentObject, this.options)).collect(Collectors.toList()));

		return feed;
	}

	/// <summary>
	/// Builds the syndication item from the <paramref name="contentObject" /> and having the properties specified
	/// in <paramref name="options" />.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	/// <param name="options">The options that direct the creation of HTML and URLs for a content object.</param>
	/// <returns>An instance of <see cref="SyndicationItem" />.</returns>
	private static SyndEntry buildSyndicationItem(ContentObjectBo contentObject, ContentObjectHtmlBuilderOptions options) {
		options.ContentObject = contentObject;
		options.DisplayType = (contentObject.getContentObjectType() == ContentObjectType.External ? DisplayObjectType.External : DisplayObjectType.Optimized);

		ContentObjectHtmlBuilder moBuilder = new ContentObjectHtmlBuilder(options);

		String pageUrl = moBuilder.getPageUrl();

		String content = StringUtils.EMPTY;
		try {
			content = getContentObjectContent(contentObject, pageUrl, moBuilder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SyndEntry item = new SyndEntryImpl();
		item.setTitle(rssEncode(HtmlValidator.removeHtml(contentObject.getTitle(), false)));
		SyndContent syndContent = new SyndContentImpl();
		syndContent.setType("text/html");
		syndContent.setValue(content);
		item.getContents().add(syndContent);
		item.setLink(pageUrl);
		item.setUri(Long.toString(contentObject.getId()));
		item.setUpdatedDate(contentObject.getDateLastModified());

		item.setPublishedDate(contentObject.getDateAdded());
		SyndPerson author = new SyndPersonImpl();
		author.setName(contentObject.getCreatedByUserName());
		item.getAuthors().add(author);
		SyndCategory category = new SyndCategoryImpl();
		category.setName(contentObject.getContentObjectType().toString());
		item.getCategories().add(category);

		return item;
	}

	/// <summary>
	/// Gets an HTML String representing the content of the <paramref name="contentObject" />. For example,
	/// albums contain the title and caption while images contain a hyperlinked img tag pointing to 
	/// <paramref name="pageUrl" />. Other content objects contain the HTML generated by <paramref name="moBuilder" />.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	/// <param name="pageUrl">An URL pointing to a gallery page for the <paramref name="contentObject" />
	/// Images use this value to create a hyperlink that is wrapped around the img tag.</param>
	/// <param name="moBuilder">An instance of <see cref="ContentObjectHtmlBuilder" />.</param>
	/// <returns><see cref="System.String" />.</returns>
	private static String getContentObjectContent(ContentObjectBo contentObject, String pageUrl, ContentObjectHtmlBuilder moBuilder) throws Exception{
		switch (contentObject.getContentObjectType()){
			case Image:
				return MessageFormat.format("<div><a href='{0}'>{1}</a></div><p>{2}</p><p>{3}</p>", pageUrl, moBuilder.generateHtml(), contentObject.getTitle(), contentObject.getCaption());

			case Album:
				return MessageFormat.format("<p>{0}</p><p>{1}</p>", contentObject.getTitle(), contentObject.getCaption());

			default:
				// Don't include the hyperlink around the MO HTML because that interferes with audio/video controls.
				return MessageFormat.format("<div>{0}</div><p>{1}</p><p>{2}</p>", moBuilder.generateHtml(), contentObject.getTitle(), contentObject.getCaption());
		}
	}

	/// <summary>
	/// Encode the <paramref name="text" /> for use in a syndication item.
	/// </summary>
	/// <param name="text">The text.</param>
	/// <returns>System.String.</returns>
	private static String rssEncode(String text){
		// Recommendation from http://www.rssboard.org/rss-profile. We don't bother encoding < and > because we
		// stripped them using HtmlValidator.removeHtml().
		return text.replace("&", "&#x26;");
	}
}

/*/// <summary>
/// Contains functionality for specifying that a particular <see cref="ApiUtils" /> use the 
/// <see cref="AlbumSyndicationFeedFormatter" /> when generating the output.
/// </summary>
/// <remarks>
/// Inspired from http://blogs.msdn.com/b/jmstall/archive/2012/05/11/per-controller-configuration-in-webapi.aspx
/// </remarks>
public class AlbumSyndicationFeedFormatterAttribute : Attribute, IUtilsConfiguration
{
	/// <summary>
	/// Callback invoked to set per-controller overrides for this <paramref name="controllerDescriptor" />.
	/// </summary>
	/// <param name="controllerSettings">The controller settings to initialize.</param>
	/// <param name="controllerDescriptor">The controller descriptor. Note that the 
	/// <see cref="T:System.Web.Http.Utilss.HttpUtilsDescriptor" /> can be associated with the derived controller 
	/// type given that <see cref="T:System.Web.Http.Utilss.IUtilsConfiguration" /> is inherited.</param>
	public void Initialize(HttpUtilsSettings controllerSettings, HttpUtilsDescriptor controllerDescriptor)
	{
		controllerSettings.Formatters.Clear();
		controllerSettings.Formatters.Add(new AlbumSyndicationFeedFormatter());
	}
}*/
