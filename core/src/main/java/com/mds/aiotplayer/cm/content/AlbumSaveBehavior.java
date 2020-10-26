/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.HelperFunctions;

/// <summary>
/// Provides functionality for persisting an album to the data store and file system.
/// </summary>
public class AlbumSaveBehavior implements SaveBehavior{
    AlbumBo albumObject;

    /// <summary>
    /// Initializes a new instance of the <see cref="AlbumSaveBehavior"/> class.
    /// </summary>
    /// <param name="albumObject">The album object.</param>
    public AlbumSaveBehavior(AlbumBo albumObject)  {
      this.albumObject = albumObject;
    }

    /// <summary>
    /// Persist the object to which this behavior belongs to the data store. Also persist to the file system, if
    /// the object has a representation on disk, such as albums (stored as directories) and content objects (stored
    /// as files). New objects with ID = int.MinValue will have a new <see cref="ContentObjectBo.Id"/> assigned
    /// and <see cref="ContentObjectBo.IsNew"/> set to false.
    /// All validation should have taken place before calling this method.
    /// </summary>
    public void save() throws UnsupportedContentObjectTypeException, InvalidGalleryException  {
      if (this.albumObject.getIsVirtualAlbum())
        return; // Don't save virtual albums.

      // Must save to disk first, since the method queries properties that might be updated when it is
      // saved to the data store.
      persistToFileSystemStore(this.albumObject);

      // Save to the data store.
      //CMUtils.GetDataProvider().Album_Save(this.albumObject);
      try {
    	  CMUtils.saveAlbum(this.albumObject);
		
    	  if (this.albumObject.isGalleryIdHasChanged()) {
	        // Album has been assigned to a new gallery, so we need to iterate through all
	        // its children and update those gallery IDs as well.
    		  assignNewGalleryId(this.albumObject.getChildContentObjects(ContentObjectType.Album).values(), this.albumObject.getGalleryId());
	      	}
		} catch (RecordExistsException | InvalidGalleryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    /// <summary>
    /// Assigns the specified <paramref name="galleryId" /> to the <paramref name="albums" />,
    /// acting recursively on all child albums.
    /// </summary>
    /// <param name="albums">The albums whose gallery is to be updated.</param>
    /// <param name="galleryId">The new gallery ID.</param>
    private static void assignNewGalleryId(List<ContentObjectBo> albums, long galleryId) throws RecordExistsException, InvalidGalleryException {
      for (ContentObjectBo childAlbum : albums){
        childAlbum.setGalleryId(galleryId);
        //CMUtils.GetDataProvider().Album_Save(childAlbum);
        CMUtils.saveAlbum((AlbumBo)childAlbum);
        assignNewGalleryId(childAlbum.getChildContentObjects(ContentObjectType.Album).values(), galleryId);
      }
    }

    /// <summary>
    /// Update the directory on disk with the current name and location of the album. A new directory is
    /// created for new albums, and the directory is moved to the location specified by FullPhysicalPath if
    /// that property is different than FullPhysicalPathOnDisk.
    /// </summary>
    /// <param name="album">The album to persist to disk.</param>
    /// <exception cref="ArgumentNullException">Thrown when <paramref name="album" /> is null.</exception>
    private static void persistToFileSystemStore(AlbumBo album) throws UnsupportedContentObjectTypeException, InvalidGalleryException {
      if (album == null)
        throw new ArgumentNullException("album");

      if (album.isRootAlbum()) {
        return; // The directory for the root album is the content objects path, whose existence has already been verified by other code.
      }

      if (album.isNew){
    	  FileMisc.makeSureDirectoryPathExists(album.getFullPhysicalPath());

    	  GallerySettings gallerySetting = CMUtils.loadGallerySetting(album.getGalleryId());

        // Create directory for thumbnail cache, if needed.
        String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(album.getFullPhysicalPath(), gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
        if (!thumbnailPath.equals(album.getFullPhysicalPath())){
          FileMisc.makeSureDirectoryPathExists(thumbnailPath);
        }

        // Create directory for optimized image cache, if needed.
        String optimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(album.getFullPhysicalPath(), gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());
        if (!optimizedPath.equals(album.getFullPhysicalPath())){
          FileMisc.makeSureDirectoryPathExists(optimizedPath);
        }
      }else if (!album.getFullPhysicalPathOnDisk().equals(album.getFullPhysicalPath())) {
        // We need to move the directory to its new location or change its name. Verify that the containing directory doesn't already
        // have a directory with the new name. If it does, alter it slightly to make it unique.
        File di = FileMisc.getParent(album.getFullPhysicalPath());

        GallerySettings gallerySetting = CMUtils.loadGallerySetting(album.getGalleryId());

        String newDirName = HelperFunctions.validateDirectoryName(di.getPath(), album.getDirectoryName(), gallerySetting.getDefaultAlbumDirectoryNameLength());
        if (album.getDirectoryName() != newDirName){
          album.setDirectoryName(newDirName);
        }

        // Now we are guaranteed to have a "safe" directory name, so proceed with the move/rename.
        try {
			FileUtils.moveDirectory(new File(album.getFullPhysicalPathOnDisk()), new File(album.getFullPhysicalPath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Rename directory for thumbnail cache, if needed.
        String thumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(album.getFullPhysicalPath(), gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());
        if (thumbnailPath != album.getFullPhysicalPath()){
          String currentThumbnailPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(album.getFullPhysicalPathOnDisk(), gallerySetting.getFullThumbnailPath(), gallerySetting.getFullContentObjectPath());

          renameDirectory(currentThumbnailPath, thumbnailPath);
        }

        // Rename directory for optimized image cache, if needed.
        String optimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(album.getFullPhysicalPath(), gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());
        if (optimizedPath != album.getFullPhysicalPath()){
          String currentOptimizedPath = HelperFunctions.mapAlbumDirectoryStructureToAlternateDirectory(album.getFullPhysicalPathOnDisk(), gallerySetting.getFullOptimizedPath(), gallerySetting.getFullContentObjectPath());

          renameDirectory(currentOptimizedPath, optimizedPath);
        }
      }
    }

    private static void renameDirectory(String oldDirPath, String newDirPath) {
    	/*if (FileMisc.fileExists(oldDirPath)){
    		FileMisc.moveFile(oldDirPath, newDirPath);
    	}else if (!FileMisc.fileExists(newDirPath))	{
    		FileMisc.makeSureDirectoryPathExists(newDirPath);
    	}*/
    	FileMisc.rename(oldDirPath, newDirPath);
    	if (!FileMisc.fileExists(newDirPath))	{
    		FileMisc.makeSureDirectoryPathExists(newDirPath);
    	}
    }
}

