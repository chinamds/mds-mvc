package com.mds.cm.content;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import com.google.common.io.Files;
import com.mds.cm.content.nullobjects.NullDisplayObject;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.metadata.ContentObjectMetadataItem;
import com.mds.core.MetadataItemName;
import com.mds.core.exception.NotSupportedException;
import com.mds.cm.util.CMUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;

/// <summary>
/// Provides functionality for persisting a content object to the data store and file system.
/// </summary>
public class ContentObjectSaveBehavior implements SaveBehavior{
	private ContentObjectBo contentObject;

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectSaveBehavior"/> class.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	public ContentObjectSaveBehavior(ContentObjectBo contentObject){
		this.contentObject = contentObject;
	}

	/// <summary>
	/// Persist the object to which this behavior belongs to the data store. Also persist to the file system, if
	/// the object has a representation on disk, such as albums (stored as directories) and content objects (stored
	/// as files). New objects with ID = int.MinValue will have a new <see cref="ContentObjectBo.Id"/> assigned
	/// and <see cref="ContentObjectBo.IsNew"/> set to false.
	/// All validation should have taken place before calling this method.
	/// </summary>
	public void save() throws IOException, UnsupportedImageTypeException, InvalidGalleryException{
		// If the user requested a rotation, then rotate and save the original. If no rotation is requested,
		// the following line does nothing.
		this.contentObject.getOriginal().generateAndSaveFile();

		// Generate the thumbnail and optimized versions. These must run after the previous statement because when
		// the image is rotated, these methods assume the original has already been rotated.
		try
		{
			this.contentObject.getThumbnail().generateAndSaveFile();
			this.contentObject.getOptimized().generateAndSaveFile();

			try	{
				// Now delete the temp file, but no worries if an error happens. The file is in the temp directory
				// which is cleaned out each time the app starts anyway.
				if (FileMisc.fileExists(contentObject.getOriginal().getTempFilePath()))	{
					FileMisc.deleteFile(contentObject.getOriginal().getTempFilePath());
				}
			}catch (NotSupportedException ex){
				//EventLogs.EventLogController.RecordError(ex, AppSetting.Instance, this.contentObject.GalleryId, CMUtils.LoadGallerySettings());
			}catch (SecurityException ex){
				//EventLogs.EventLogController.RecordError(ex, AppSetting.Instance, this.contentObject.GalleryId, CMUtils.LoadGallerySettings());
			}
		}catch (UnsupportedImageTypeException ex){
			// We'll get here when there is a corrupt image or the server's memory is not sufficient to process the image.
			// When this happens, replace the thumbnail creator object with a GenericThumbnailCreator. That one uses a
			// hard-coded thumbnail image rather than trying to generate a thumbnail from the original image.
			// Also, null out the Optimized object and don't bother to try to create an optimized image.
			this.contentObject.getThumbnail().setDisplayObjectCreator(new GenericThumbnailCreator(this.contentObject));
			this.contentObject.getThumbnail().generateAndSaveFile();

			this.contentObject.setOptimized(new NullDisplayObject());
		}

		syncFilenameToMetadataFilename();

		// Save the data to the data store
		//CMUtils.GetDataProvider().ContentObject_Save(this.contentObject);
		CMUtils.saveContentObject(this.contentObject);

		for (ContentObjectMetadataItem item : this.contentObject.getMetadataItems()){
			item.setHasChanges(false);
		}
	}

	///// <summary>
	///// Syncs the title to the title metadata item, if it exists.
	///// </summary>
	//private void SyncTitleToMetadataTitle()
	//{
	//	ContentObjectBoMetadataItem metaItem;
	//	if (this.contentObject.MetadataItems.TryGetMetadataItem(MetadataItemName.Title, out metaItem))
	//	{
	//		metaItem.Value = this.contentObject.Title;
	//	}
	//}

	/// <summary>
	/// Rename the media file's name if the filename metadata value has changed. The thumbnail
	/// and the optimized file name is not modified, nor is any action taken when a media file
	/// does not exist (such as for external content objects).
	/// </summary>
	private void syncFilenameToMetadataFilename() throws IOException, InvalidGalleryException{
		if (CMUtils.loadGallerySetting(contentObject.getGalleryId()).getContentObjectPathIsReadOnly()){
			return; // Don't change filename when gallery is read only
		}

		ContentObjectMetadataItem metaItem;
		if ((metaItem = this.contentObject.getMetadataItems().tryGetMetadataItem(MetadataItemName.FileName)) != null){
			if (!this.contentObject.getOriginal().getFileName().equalsIgnoreCase(metaItem.getValue())){
				// The filename metadata item has been changed, so update the actual file name.
				boolean optFilenameSameAsOriginal = (this.contentObject.getOriginal().getFileName().equalsIgnoreCase(contentObject.getOptimized().getFileName()));
				String albumPath = this.contentObject.getParent().getFullPhysicalPathOnDisk();

				String prevPath = this.contentObject.getOriginal().getFileNamePhysicalPath();
				this.contentObject.getOriginal().setFileName(HelperFunctions.validateFileName(albumPath, metaItem.getValue()));

				// Uncomment to prevent user from changing extension. In some cases the user
				// may want to do this (such as changing from MP4 to MOV), so we'll allow it for now.
				//if (!this.contentObject.Original.FileName.EndsWith(Path.GetExtension(prevPath) ?? StringUtils.EMPTY))
				//{
				//	// Don't let user change the extension, as this could cause trouble.
				//	this.contentObject.Original.FileName += Path.GetExtension(prevPath);
				//}

				this.contentObject.getOriginal().setFileNamePhysicalPath(FilenameUtils.concat(albumPath, this.contentObject.getOriginal().getFileName()));

				// Need to update the metaitem value in case a filename conflict caused the name to be
				// altered slightly (like with a (1) at the end).
				metaItem.setValue(this.contentObject.getOriginal().getFileName());

				if (FileMisc.fileExists(prevPath)){
					try
					{
						FileMisc.moveFileThrow(prevPath, this.contentObject.getOriginal().getFileNamePhysicalPath());
					}
					catch (IOException ex)
					{
						// Record additional details and re-throw
						/*if (!ex.Data.Contains("Cannot Rename"))
						{
							ex.Data.Add("Cannot Rename", MessageFormat.format("Error occurred renaming file {0} to {1} (directory {2}).", Path.GetFileName(prevPath), Path.GetFileName(this.contentObject.Original.FileNamePhysicalPath), Path.GetDirectoryName(prevPath)));
						}*/

						throw ex;
					}
				}

				// When the optimized filename is the same as the original filename, be sure to 
				// update that one as well.
				if (optFilenameSameAsOriginal){
					this.contentObject.getOptimized().setFileName(this.contentObject.getOriginal().getFileName());
					this.contentObject.getOptimized().setFileNamePhysicalPath(this.contentObject.getOriginal().getFileNamePhysicalPath());
				}
			}
		}
	}

	///// <summary>
	///// If any of the metadata items for this content object has its <see cref="ContentObjectBo.ExtractMetadataOnSave" /> property 
	///// set to true, then open the original file, extract the items, and update the <see cref="ContentObjectBo.MetadataItems" /> 
	///// property on our content object. The <see cref="ContentObjectBo.ExtractMetadataOnSave" /> property is not changed to false 
	///// at this time, since the Save method uses it to know which items to persist to the data store.
	///// </summary>
	//private void UpdateMetadata()
	//{
	//	if (this.contentObject.ExtractMetadataOnSave)
	//	{
	//		// Replace all metadata with the metadata found in the original file.
	//		//Metadata.ContentObjectMetadataExtractor metadata;
	//		try
	//		{
	//			this.contentObject.ExtractMetadata();

	//			//metadata = new Metadata.ContentObjectMetadataExtractor(this.contentObject.Original.FileNamePhysicalPath, this.contentObject);
	//		}
	//		catch (OutOfMemoryException)
	//		{
	//			// Normally, the Dispose method is called during the ImageSaved event. But when we get this exception, it
	//			// never executes and therefore doesn't release the file lock. So we explicitly do so here and then 
	//			// re-throw the exception.
	//			this.contentObject.Original.Dispose();
	//			throw new UnsupportedImageTypeException();
	//		}

	//		//this.contentObject.MetadataItems.Clear();
	//		//this.contentObject.MetadataItems.AddRange(metadata.GetContentObjectMetadataItemCollection());
	//		this.contentObject.ExtractMetadataOnSave = true;
	//	}
	//	else
	//	{
	//		// If any individual metadata items have been set to ExtractFromFileOnSave = true, then update those selected ones with
	//		// the latest metadata from the file. If the metadata item is not found in the file, then set the value to an empty String.
	//		ContentObjectBoMetadataItemCollection metadataItemsToUpdate = this.contentObject.MetadataItems.GetItemsToUpdate();
	//		if (metadataItemsToUpdate.Count > 0)
	//		{
	//			//Metadata.ContentObjectMetadataExtractor metadata;
	//			try
	//			{
	//				//this.contentObject.CreateMetaItem()
	//				//metadata = new Metadata.ContentObjectMetadataExtractor(this.contentObject.Original.FileNamePhysicalPath, this.contentObject);
	//			}
	//			catch (OutOfMemoryException)
	//			{
	//				// Normally, the Dispose method is called during the Image_Saved event. But when we get this exception, it
	//				// never executes and therefore doesn't release the file lock. So we explicitly do so here and then 
	//				// re-throw the exception.
	//				this.contentObject.Original.Dispose();
	//				throw new UnsupportedImageTypeException();
	//			}

	//			for (ContentObjectBoMetadataItem metadataItem in metadataItemsToUpdate)
	//			{
	//				var metaItem = this.contentObject.CreateMetaItem(metadataItem.MetaDefinition);

	//				metadataItem.Description = metaItem.Description;
	//				metadataItem.Value = metaItem.Value;
	//				metadataItem.IsVisible = metaItem.IsVisible;

	//				//ContentObjectBoMetadataItem extractedMetadataItem;
	//				//if (metadata.GetContentObjectMetadataItemCollection().TryGetMetadataItem(metadataItem.MetadataItemName, out extractedMetadataItem))
	//				//{
	//				//	metadataItem.Value = extractedMetadataItem.Value;
	//				//}
	//				//else
	//				//{
	//				//	metadataItem.Value = StringUtils.EMPTY;
	//				//}
	//			}
	//		}
	//	}
	//}
}
