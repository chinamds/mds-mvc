/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.EventObject;
import java.util.Objects;

import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;

/// <summary>
/// Provides functionality for creating and saving the files associated with gallery objects.
/// </summary>
public class AddMetaItemEvent extends EventObject {
	/// <summary>
	/// Gets or sets a metadata item for a gallery object.
	/// </summary>
	/// <value>An instance of <see cref="IGalleryObjectMetadataItem" />.</value>
    private final ContentObjectMetadataItem metaItem;
    /// <summary>
	/// Gets or sets a value indicating whether the meta item should not be added to the
	/// gallery object.
	/// </summary>
	/// <value>
	///   <c>true</c> if cancelled; otherwise, <c>false</c>.
	/// </value>
  	public boolean cancel;

    public AddMetaItemEvent(Object source, ContentObjectMetadataItem metaItem) {
        super(source);
        this.metaItem = Objects.requireNonNull(metaItem);
    }

    public ContentObjectMetadataItem getMetaItem() {
        return metaItem;
    }
    
  	public boolean isCancel() {
  		return cancel;
  	}
  	
  	public void setCancel(boolean cancel) {
  		this.cancel = cancel;
  	}
}

