package com.mds.cm.exception;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.mds.cm.content.ContentObjectBo;
import com.mds.util.FileMisc;
import com.mds.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when the user album feature is enabled but the album ID that is specified for
///   containing the user albums does not exist.
/// </summary>
public class CannotDeleteAlbumException extends Exception {
   
	private long albumId;

	/// <summary>
	///   Throws an exception when an album cannot be deleted.
	/// </summary>
	public CannotDeleteAlbumException(){
		super(I18nUtils.getMessage("exception.cannotDeleteAlbum_Ex_Msg"));
	}
	
	/// <summary>
	///   Throws an exception when an album cannot be deleted.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public CannotDeleteAlbumException(String msg){
		super(msg);
	}


	/// <summary>
	///   Throws an exception when an album cannot be deleted.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public CannotDeleteAlbumException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	/// <summary>
	///   Throws an exception when an album cannot be deleted.
	/// </summary>
	/// <param name="albumId">The ID of the album that cannot be deleted.</param>
	public CannotDeleteAlbumException(long albumId)	{
		super(I18nUtils.getMessage("exception.cannotDeleteAlbum_Ex_Msg2", albumId));
		this.albumId = albumId;
	}

	/// <summary>
	///   Throws an exception when an album cannot be deleted.
	/// </summary>
	/// <param name="albumId">The ID of the album that cannot be deleted.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public CannotDeleteAlbumException(long albumId, Throwable cause){
		super(I18nUtils.getMessage("exception.cannotDeleteAlbum_Ex_Msg2", albumId), cause);
		this.albumId = albumId;
	}
	
	/// <summary>
	///   Gets the album ID that is causing the exception.
	/// </summary>
	public long getAlbumId(){
		return albumId; 
	}
}
