package com.mds.cm.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.core.ContentObjectType;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.cm.util.CMUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;

/// <summary>
/// Provides functionality for deleting an album from the data store.
/// </summary>
public class AlbumDeleteBehavior implements DeleteBehavior{
	AlbumBo albumObject;

	/// <summary>
	/// Initializes a new instance of the <see cref="AlbumDeleteBehavior"/> class.
	/// </summary>
	/// <param name="albumObject">The album object.</param>
	public AlbumDeleteBehavior(AlbumBo albumObject)	{
		this.albumObject = albumObject;
	}

	/// <summary>
	/// Delete the object to which this behavior belongs from the data store and optionally the file system.
	/// </summary>
	/// <param name="deleteFromFileSystem">Indicates whether to delete the file or directory from the hard drive in addition
	/// to deleting it from the data store. When true, the object is deleted from both the data store and hard drive. When
	/// false, only the record in the data store is deleted.</param>
	public void delete(boolean deleteFromFileSystem) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		if (deleteFromFileSystem){
			deleteFromFileSystem(this.albumObject);
		}else{
			deleteSupportFilesOnly(this.albumObject);
		}

		if (this.albumObject.isRootAlbum()){
			// Don't delete the root album; just its contents.
			deleteAlbumContents(deleteFromFileSystem);
		}else{
			CMUtils.deleteAlbum(this.albumObject);
		}
	}

	/// <summary>
	/// Deletes the albums and content objects in the album, but not the album itself.
	/// </summary>
	/// <param name="deleteFromFileSystem">Indicates whether to delete the file or directory from the hard drive in addition
	/// to deleting it from the data store. When true, the object is deleted from both the data store and hard drive. When
	/// false, only the record in the data store is deleted.</param>
	private void deleteAlbumContents(boolean deleteFromFileSystem) throws InvalidAlbumException, UnsupportedContentObjectTypeException, IOException, UnsupportedImageTypeException, InvalidGalleryException{
		List<ContentObjectBo> itemsToDelete = new ArrayList<ContentObjectBo>();

		// Step 1: Get a list of all items in this album.
		List<ContentObjectBo> childItems = this.albumObject.getChildContentObjects().values();
		for (ContentObjectBo childItem : childItems)	{
			itemsToDelete.add(childItem);
		}

		// Now iterate through each one, deleting it as we go.
		for (ContentObjectBo contentObject : itemsToDelete){
			if (deleteFromFileSystem){
				contentObject.delete();
			}else{
				contentObject.deleteFromGallery();
			}
		}
	}

	/// <summary>
	/// Deletes the thumbnail and optimized images associated with this album and all its children, but do not delete the 
	/// album's directory or the any other files it contains.
	/// </summary>
	/// <param name="album">The album.</param>
	private static void deleteSupportFilesOnly(AlbumBo album) throws InvalidGalleryException{
		List<ContentObjectBo> childContentObjects = album.getChildContentObjects(ContentObjectType.ContentObject).values();
		for (ContentObjectBo childContentObject : childContentObjects){
			deleteThumbnailAndOptimizedImagesFromFileSystem(childContentObject);
		}

		List<ContentObjectBo> childAlbums = album.getChildContentObjects(ContentObjectType.Album).values();
		for (ContentObjectBo childAlbum : childAlbums){
			deleteSupportFilesOnly((AlbumBo)childAlbum);
		}
	}

	private static void deleteThumbnailAndOptimizedImagesFromFileSystem(ContentObjectBo contentObject) throws InvalidGalleryException{
		// Delete thumbnail file.
		FileMisc.deleteFile(contentObject.getThumbnail().getFileNamePhysicalPath());

		// Delete optimized file.
		if (!contentObject.getOptimized().getFileName().equals(contentObject.getOriginal().getFileName())){
			FileMisc.deleteFile(contentObject.getOptimized().getFileNamePhysicalPath());
		}
	}

	private static void deleteFromFileSystem(AlbumBo album) throws UnsupportedContentObjectTypeException, InvalidGalleryException {
		String albumPath = album.getFullPhysicalPath();
		if (album.isRootAlbum()){
			deleteRootAlbumDirectory(albumPath, album.getGalleryId());
		}else{
			try {
				deleteAlbumDirectory(albumPath, album.getGalleryId());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void deleteAlbumDirectory(String albumPath, long galleryId) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		// Delete the directory (recursive).
		FileMisc.deleteDirectory(albumPath);

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		// Delete files and folders from thumbnail cache, if needed.
		String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
		if (thumbnailPath != albumPath){
			FileMisc.deleteDirectory(thumbnailPath);
		}

		// Delete files and folders from optimized image cache, if needed.
		String optimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPath, gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());
		if (optimizedPath != albumPath){
			FileMisc.deleteDirectory(optimizedPath);
		}
	}

	private static void deleteRootAlbumDirectory(String albumPath, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		// User is trying to delete the root album. We only want to delete any subdirectories and files,
		// but not the folder itself.
		deleteChildFilesAndDirectories(albumPath);

		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		// Delete files and folders from thumbnail cache, if needed.
		String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPath, gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
		if (thumbnailPath != albumPath){
			deleteChildFilesAndDirectories(thumbnailPath);
		}

		// Delete files and folders from optimized image cache, if needed.
		String optimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(albumPath, gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());
		if (optimizedPath != albumPath){
			deleteChildFilesAndDirectories(optimizedPath);
		}
	}

	private static void deleteChildFilesAndDirectories(String albumPath){
		FileMisc.cleanDirectory(albumPath);
	}

}
