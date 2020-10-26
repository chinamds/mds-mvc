/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.EventListener;

import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;

/// <summary>
/// Provides functionality for creating and saving the files associated with gallery objects.
/// </summary>
public interface ContentObjectListener extends EventListener{
	 default void saving(ContentObjectEvent event)  throws UnsupportedContentObjectTypeException, InvalidGalleryException { }
	 default void saved(ContentObjectEvent event)  throws InvalidGalleryException{ }
	 default void onBeforeAddMetaItem(AddMetaItemEvent event) { }
	 default void removeMetadataItem(MetadataItemName metaName) { }
	 default void updateInternalMetaItem(ContentObjectMetadataItem metaItem) throws UnsupportedContentObjectTypeException, InvalidGalleryException {}
}

