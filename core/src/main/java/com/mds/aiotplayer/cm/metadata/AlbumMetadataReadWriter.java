/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import org.apache.commons.lang3.StringUtils;

import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.MetadataItemName;

/// <summary>
/// Provides functionality for extracting metadata from an album.
/// </summary>
public class AlbumMetadataReadWriter extends ContentObjectMetadataReadWriter{
	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="AlbumMetadataReadWriter" /> class.
	/// </summary>
	/// <param name="album">The album.</param>
	public AlbumMetadataReadWriter(ContentObjectBo album)	{
		super(album);
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Gets the metadata value for the specified <paramref name="metaName" />.
	/// </summary>
	/// <param name="metaName">Name of the metadata item to retrieve.</param>
	/// <returns>An instance that implements <see cref="MetaValue" />.</returns>
	public MetaValue GetMetaValue(MetadataItemName metaName){
		switch (metaName){
			case Title: return getAlbumTitle();
			default:
				return super.getMetaValue(metaName);
		}
	}

	/// <summary>
	/// Gets the album title, which is defined as the directory name, except for the root album,
	/// in which case we return the title property to preserve the original title. Returns null
	/// for new albums, since in those cases the directory name may not yet be known.
	/// </summary>
	/// <returns>An instance that implements <see cref="MetaValue" />.</returns>
	private MetaValue getAlbumTitle(){
		String dirName = getDirectoryName();

		if (getContentObject().getIsNew()){
			// For new albums we may not yet have the directory name, so return null for now.
			// Later code in the Album_Saving event will assign the directory name, which 
			// in turn will update the DirectoryName metadata property.
			return null;
		}else{
			return new MetaValue((!StringUtils.isBlank(dirName) ? dirName : getContentObject().getTitle()));
		}
	}

	private String getDirectoryName(){
		AlbumBo album =Reflections.as(AlbumBo.class, getContentObject());

		return (album != null ? album.getDirectoryName() : null);
	}

	//#endregion
}