/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.io.Serializable;
import java.util.Comparator;

import com.mds.aiotplayer.common.utils.Reflections;

/**
 * Compares the names of two {@link Collection}s.
 */
public class ContentObjectBoComparator implements Comparator<ContentObjectBo>, Serializable {
    @Override
    public int compare(ContentObjectBo contentObject1, ContentObjectBo contentObject2) {
    	if (contentObject2 == null)
			return 1;
		else
		{
			AlbumBo thisAsAlbum = Reflections.as(AlbumBo.class, contentObject1);
			AlbumBo otherAsAlbum = Reflections.as(AlbumBo.class, contentObject2);
			ContentObjectBo otherAsGalleryObj = Reflections.as(ContentObjectBo.class, contentObject2);
				
			boolean thisIsContentObj = (thisAsAlbum == null); // If it's not an album, it must be a content object (or a NullContentObjectBo, but that shouldn't happen)
			boolean otherIsContentObj = ((otherAsGalleryObj != null) && (otherAsAlbum == null));
			boolean bothObjectsAreContentObjects = (thisIsContentObj && otherIsContentObj);
			boolean bothObjectsAreAlbums = ((thisAsAlbum != null) && (otherAsAlbum != null));

			if (otherAsGalleryObj == null)
				return 1;

			if (bothObjectsAreAlbums || bothObjectsAreContentObjects)
			{
				return contentObject1.getSequence() - otherAsGalleryObj.getSequence();
			}
			else if (thisIsContentObj && (otherAsAlbum != null))
			{
				return 1;
			}
			else
			{
				return -1; // Current instance must be album and other is content object. Albums always come first.
			}
		}
    }
}
