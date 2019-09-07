package com.mds.cm.content;

import java.util.EventListener;

import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;

/// <summary>
/// Provides functionality for creating and saving the files associated with gallery objects.
/// </summary>
public interface GalleryCreatedListener extends EventListener{
	 default void galleryCreated(GalleryCreatedEventArgs event) { }
	 default void gallerySettingsSaved(GallerySettingsEventArgs event)  throws UnsupportedContentObjectTypeException, InvalidGalleryException{ }
}

