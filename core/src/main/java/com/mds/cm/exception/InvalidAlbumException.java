package com.mds.cm.exception;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.mds.cm.content.ContentObjectBo;
import com.mds.util.FileMisc;
import com.mds.i18n.util.I18nUtils;

/**
 * An exception that is thrown by classes wanting to trap unique 
 * constraint violations.  This is used to wrap Spring's 
 * DataIntegrityViolationException so it's checked in the web layer.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class InvalidAlbumException extends Exception {
   
	private long albumId;
    /// <summary>
	///   Throws an exception to indicate a file that is not recognized as a valid content object supported by
	///   MDS System. This may be because the file is a type that is disabled, or it may have an
	///   unrecognized file extension and the allowUnspecifiedMimeTypes configuration setting is false.
	/// </summary>
	public InvalidAlbumException(){
		super(I18nUtils.getMessage("exception.invalidAlbum_Ex_Msg"));
	}
	
	/// <summary>
	///   Throws an exception to indicate the .NET Framework is unable to load an image file into the System.Drawing.Bitmap
	///   class. This is probably because it is corrupted, not an image supported by the .NET Framework, or the server does
	///   not have enough memory to process the image.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public InvalidAlbumException(String msg){
		super(msg);
	}

	/// <summary>
	///   Throws an exception to indicate the .NET Framework is unable to load an image file into the System.Drawing.Bitmap
	///   class. This is probably because it is corrupted, not an image supported by the .NET Framework, or the server does
	///   not have enough memory to process the image.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	/// <param name="cause">
	///   The exception that is the cause of the current exception. If the
	///   cause parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public InvalidAlbumException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	/// <summary>
	///   Throws an exception to indicate an invalid album.
	/// </summary>
	/// <param name="albumId">The ID of the album that is not valid.</param>
	public InvalidAlbumException(long albumId)	{
		super(I18nUtils.getMessage("exception.invalidAlbum_Ex_Msg2", albumId));
		this.albumId = albumId;
	}

	/// <summary>
	///   Throws an exception to indicate an invalid album.
	/// </summary>
	/// <param name="albumId">The ID of the album that is not valid.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public InvalidAlbumException(long albumId, Throwable cause){
		super(I18nUtils.getMessage("exception.invalidAlbum_Ex_Msg2", albumId), cause);
		this.albumId = albumId;
	}
	
	/// <summary>
	///   Gets the album ID that is causing the exception.
	/// </summary>
	public long getAlbumId(){
		return albumId; 
	}
}
