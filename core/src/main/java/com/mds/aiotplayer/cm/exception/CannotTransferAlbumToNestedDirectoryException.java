package com.mds.aiotplayer.cm.exception;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when the user album feature is enabled but the album ID that is specified for
///   containing the user albums does not exist.
/// </summary>
public class CannotTransferAlbumToNestedDirectoryException extends Exception {
   
	/// <summary>
	///   Throws an exception when an album cannot be deleted.
	/// </summary>
	public CannotTransferAlbumToNestedDirectoryException(){
		super(I18nUtils.getMessage("exception.cannotTransferAlbumToNestedDirectoryException_Ex_msg"));
	}
	
	/// <summary>
	///   Throws an exception when an album cannot be deleted.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public CannotTransferAlbumToNestedDirectoryException(String msg){
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
	public CannotTransferAlbumToNestedDirectoryException(String msg, Throwable cause){
		super(msg, cause);
	}
}
