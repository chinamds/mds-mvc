package com.mds.aiotplayer.cm.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.List;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Service;

import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.FeedFormatterOptions;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.service.FeedService;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.core.DisplayObjectType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;

/// <summary>
/// An implementation of <see cref="ContentTypeFormatter" /> that formats one ore more <see cref="AlbumBo" />
/// instances in ATOM or RSS syntax. Clients should specify "application/atom+xml" or "application/rss+xml" in 
/// their ACCEPT headers. Defaults to "application/rss+xml" if one of these is not specified.
/// NOTE: This class throws a <see cref="GallerySecurityException" /> when the application is not running
/// an Enterprise License.
/// </summary>
/// <remarks>
/// This class was inspired by the following articles: 
/// http://www.strathweb.com/2012/04/different-mediatypeformatters-for-same-mediaheadervalue-in-asp-net-web-api/
/// http://blogs.msdn.com/b/jmstall/archive/2012/05/11/per-controller-configuration-in-webapi.aspx
/// </remarks>
@Provider 
@Produces({"application/atom+xml", "application/rss+xml"}) 
//@Consumes({"application/atom+xml", "application/rss+xml"}) 
public class AlbumSyndicationFeedFormatter implements MessageBodyWriter<Object>{ // : MediaTypeFormatter , MessageBodyReader<Object>
	private final String AtomContentType = "application/atom+xml";
	private final String RssContentType = "application/rss+xml";
	
	@Context HttpServletRequest request;
	
	/*public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, 
			MediaType mediaType) { 
       return true; 
   } 
 
   public Object readFrom(Class<Object> type, Type genericType, 
           Annotation[] annotations, MediaType mediaType, 
           MultivaluedMap<String, String> httpHeaders, InputStream entityStream) 
           throws IOException, WebApplicationException { 
       return gson.fromJson(new InputStreamReader(entityStream, "UTF-8"), type); 
   } */
 
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, 
           MediaType mediaType) { 
       return true; 
   } 
 
   public long getSize(Object obj, Class<?> type, Type genericType, 
           Annotation[] annotations, MediaType mediaType) { 
       return -1; 
   } 
 
   public void writeTo(Object obj, Class<?> type, Type genericType, 
           Annotation[] annotations, MediaType mediaType, 
           MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) 
           throws IOException, WebApplicationException { 

	   AlbumBo album = (AlbumBo)obj;
	   ContentObjectHtmlBuilderOptions options = ContentObjectHtmlBuilder.getContentObjectHtmlBuilderOptions(album, DisplayObjectType.Thumbnail, request);
		
	   AlbumSyndicationFeedBuilder fb = new AlbumSyndicationFeedBuilder(album, options);

		SyndFeed feed = null;
		try {
			feed = fb.generate(AtomContentType.equalsIgnoreCase(mediaType.toString()) ? "rss_1.0" : "rss_2.0");
		} catch (UnsupportedContentObjectTypeException | InvalidGalleryException | WebException | InvalidAlbumException
				| UnsupportedImageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		if (feed == null) {
			throw new WebApplicationException();
		}
			
	   // Write the SyndFeed to a Writer.
       final SyndFeedOutput output = new SyndFeedOutput();
       OutputStreamWriter writer = new OutputStreamWriter(entityStream);
       try {
			output.output(feed, writer);
			writer.flush();
		} catch (FeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IOException();
		}finally {
			writer.close();
		}
       
        //entityStream.write(writer.toString().getBytes("UTF-8")); 
   } 

	/*private Func<Type, Boolean> _supportedType = (type) => type == typeof(AlbumBo);

	/// <summary>
	/// Initializes a new instance of the <see cref="AlbumSyndicationFeedFormatter"/> class.
	/// </summary>
	public AlbumSyndicationFeedFormatter()
	{
		SupportedMediaTypes.Add(new MediaTypeHeaderValue(AtomContentType));
		SupportedMediaTypes.Add(new MediaTypeHeaderValue(RssContentType));
	}

	/// <summary>
	/// Queries whether this <see cref="T:System.Net.Http.Formatting.ContentTypeFormatter" /> can deserialize an object of the specified type.
	/// </summary>
	/// <param name="type">The type to deserialize.</param>
	/// <returns>true if the <see cref="T:System.Net.Http.Formatting.ContentTypeFormatter" /> can deserialize the type; otherwise, false.</returns>
	public boolean CanReadType(Type type)
	{
		return _supportedType(type);
	}

	/// <summary>
	/// Queries whether this <see cref="T:System.Net.Http.Formatting.ContentTypeFormatter" /> can serialize an object of the specified type.
	/// </summary>
	/// <param name="type">The type to serialize.</param>
	/// <returns>true if the <see cref="T:System.Net.Http.Formatting.ContentTypeFormatter" /> can serialize the type; otherwise, false.</returns>
	public boolean CanWriteType(Type type)
	{
		return _supportedType(type);
	}

	/// <summary>
	/// Asynchronously writes an object of the specified type.
	/// </summary>
	/// <param name="type">The type of the object to write.</param>
	/// <param name="value">The object value to write.  It may be null. If not null, must be able to 
	/// be cast to an <see cref="AlbumBo" />.</param>
	/// <param name="writeStream">The <see cref="T:System.IO.Stream" /> to which to write.</param>
	/// <param name="content">The <see cref="T:System.Net.Http.HttpContent" /> if available. It may be null.</param>
	/// <param name="transportContext">The <see cref="T:System.Net.TransportContext" /> if available. It may be null.</param>
	/// <returns>A <see cref="T:System.Threading.Tasks.Task" /> that will perform the write.</returns>
	/// <exception cref="GallerySecurityException">Thrown when the application is not running an Enterprise License.
	/// </exception>
	public Task WriteToStreamAsync(Type type, object value, Stream writeStream, HttpContent content, TransportContext transportContext)
	{
		if (value == null)
			return null;

		var album = (AlbumBo)value;
		var options = ContentObjectHtmlBuilder.GetContentObjectHtmlBuilderOptions(album, DisplayObjectType.Thumbnail);

		options.DestinationPageUrl = album.FeedFormatterOptions.DestinationUrl; // Ex: "/dev/ds/default.aspx"

		ValidateEnterpriseLicense(album);

		return Task.CMUtils.startNew(() => BuildSyndicationFeed(album, writeStream, content.Headers.ContentType.MediaType, options));
	}

	/// <summary>
	/// Builds the syndication feed from the specified <paramref name="album" /> and write it to the <paramref name="stream" />.
	/// </summary>
	/// <param name="album">The album from which to build the syndication feed.</param>
	/// <param name="stream">The <see cref="T:System.IO.Stream" /> to which to write.</param>
	/// <param name="contentType">Type of the requested content. Examples: "application/atom+xml", "application/rss+xml"</param>
	/// <param name="moBuilderOptions">The options that direct the creation of HTML and URLs for a content object.</param>
	private static void BuildSyndicationFeed(AlbumBo album, Stream stream, String contentType, ContentObjectHtmlBuilderOptions moBuilderOptions)
	{
		var fb = new AlbumSyndicationFeedBuilder(album, moBuilderOptions);

		var feed = fb.Generate();

		using (var writer = XmlWriter.Create(stream))
		{
			if (String.Equals(contentType, AtomContentType, StringComparison.InvariantCultureIgnoreCase))
			{
				var atomFormatter = new Atom10FeedFormatter(feed);
				atomFormatter.WriteTo(writer);
			}
			else
			{
				var rssFormatter = new Rss20FeedFormatter(feed);
				rssFormatter.WriteTo(writer);
			}
		}
	}

	/// <summary>
	/// Verifies the application is running an Enterprise License, throwing a <see cref="GallerySecurityException" />
	/// if it is not.
	/// </summary>
	/// <param name="album">The album.</param>
	/// <exception cref="GallerySecurityException">Thrown when the application is not running an Enterprise License.
	/// </exception>
	private static void ValidateEnterpriseLicense(AlbumBo album){
		if (AppSetting.Instance.License.LicenseType != LicenseLevel.Enterprise)
		{
			AppEventLogUtils.LogEvent("RSS/Atom feeds require an Enterprise License.", album.getGalleryId(), EventType.Warning);

			throw new GallerySecurityException("RSS/Atom feeds require an Enterprise License.");
		}
	}*/
}
