package com.mds.cm.content;

import java.util.EventListener;

import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.metadata.ContentObjectMetadataItem;
import com.mds.core.MetadataItemName;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;

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

