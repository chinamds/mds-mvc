/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;

import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Provides functionality for reading and writing metadata to or from a gallery object.
/// </summary>
public class MediaObjectMetadataReadWriter extends ContentObjectMetadataReadWriter{
	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="MediaObjectMetadataReadWriter" /> class.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	protected MediaObjectMetadataReadWriter(ContentObjectBo contentObject){
		super(contentObject);
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Gets the metadata value for the specified <paramref name="metaName" />.
	/// </summary>
	/// <param name="metaName">Name of the metadata item to retrieve.</param>
	/// <returns>An instance that implements <see cref="IMetaValue" />.</returns>
	public MetaValue getMetaValue(MetadataItemName metaName)	{
		File fi = getContentObject().getOriginal().getFileInfo();
		BasicFileAttributes attr = null;
		switch (metaName){
			case Title:
				return new MetaValue(getContentObject().getOriginal().getFileName());
			
			case FileName:
				return new MetaValue((fi != null ? fi.getName() : null));

			case FileNameWithoutExtension:
				return new MetaValue((fi != null ? FilenameUtils.getBaseName(fi.getPath()) : null));

			case FileSizeKb:
				if (fi != null) {
					int fileSize = (int)(fi.length() / 1024);
					fileSize = (fileSize < 1 ? 1 : fileSize); // Very small files should be 1, not 0.
					return new MetaValue(StringUtils.join(new String[] {Integer.toString(fileSize), " ", I18nUtils.getMessage("metadata.KB")}), Integer.toString(fileSize));
				}

			case DateFileCreated:
				attr = getBasicFileAttributes(fi);
				return (attr != null ? new MetaValue(DateUtils.getDateTime(dateTimeFormatString, new Date(attr.creationTime().toMillis())), DateUtils.fromFileTime(attr.creationTime()).toString()) : null); //"O", CultureInfo.InvariantCulture

			case DateFileCreatedUtc:
				attr = getBasicFileAttributes(fi);
				//return (fi != null ? new MetaValue(fi.CreationTimeUtc.ToString(DateTimeFormatString, CultureInfo.InvariantCulture), fi.CreationTimeUtc.ToString("O", CultureInfo.InvariantCulture)) : null);
				return (attr != null ? new MetaValue(DateUtils.fromFileTimeUTC(attr.creationTime()).format(DateTimeFormatter.ofPattern(dateTimeFormatString)), attr.creationTime().toString()) : null); //"O", CultureInfo.InvariantCulture

			case DateFileLastModified:
				attr = getBasicFileAttributes(fi);
				return (attr != null ? new MetaValue(DateUtils.getDateTime(dateTimeFormatString, new Date(attr.lastModifiedTime().toMillis())), DateUtils.fromFileTime(attr.lastModifiedTime()).toString()) : null); //"O", CultureInfo.InvariantCulture

			case DateFileLastModifiedUtc:
				attr = getBasicFileAttributes(fi);
				//return (fi != null ? new MetaValue(fi.LastWriteTimeUtc.ToString(DateTimeFormatString, CultureInfo.InvariantCulture), fi.LastWriteTimeUtc.ToString("O", CultureInfo.InvariantCulture)) : null);
				return (attr != null ? new MetaValue(DateUtils.fromFileTimeUTC(attr.lastModifiedTime()).format(DateTimeFormatter.ofPattern(dateTimeFormatString)), attr.lastModifiedTime().toString()) : null); //"O", CultureInfo.InvariantCulture

			default:
				return super.getMetaValue(metaName);
		}
	}
	
	private BasicFileAttributes getBasicFileAttributes(File fi) {
		BasicFileAttributes attr = null;
		try {
			attr = Files.readAttributes(fi.toPath(), BasicFileAttributes.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return attr;
	}

	//#endregion
}