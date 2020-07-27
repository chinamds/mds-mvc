package com.mds.aiotplayer.cm.content;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.util.CMUtils;

/// <summary>
/// Provides functionality for deleting a content object from the data store.
/// </summary>
public class ContentObjectDeleteBehavior implements DeleteBehavior{
	ContentObjectBo contentObject;

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectDeleteBehavior"/> class.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	public ContentObjectDeleteBehavior(ContentObjectBo contentObject){
		this.contentObject = contentObject;
	}

	/// <summary>
	/// Delete the object to which this behavior belongs from the data store and optionally the file system.
	/// </summary>
	/// <param name="deleteFromFileSystem">Indicates whether to delete the original file from the hard drive in addition
	/// to deleting it from the data store. When true, the object is deleted from both the data store and hard drive. When
	/// false, only the record in the data store and the thumbnail and optimized images are deleted; the original file
	/// is untouched.</param>
	@Override
	public void delete(boolean deleteFromFileSystem) throws InvalidGalleryException	{
		deleteFromFileSystem(this.contentObject, deleteFromFileSystem);

		//CMUtils.GetDataProvider().ContentObject_Delete(this.contentObject);
		CMUtils.deleteContentObject(this.contentObject);
	}

	private static void deleteFromFileSystem(ContentObjectBo contentObject, boolean deleteAllFromFileSystem) throws InvalidGalleryException{
		// Delete thumbnail file.
		try {
			Files.deleteIfExists(new File(contentObject.getThumbnail().getFileNamePhysicalPath()).toPath());
			
			// Delete optimized file.
			if (!contentObject.getOptimized().getFileName().equals(contentObject.getOriginal().getFileName())){
				Files.deleteIfExists(new File(contentObject.getOptimized().getFileNamePhysicalPath()).toPath());
			}

			// Delete original file.
			if (deleteAllFromFileSystem){
				Files.deleteIfExists(new File(contentObject.getOriginal().getFileNamePhysicalPath()).toPath());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
