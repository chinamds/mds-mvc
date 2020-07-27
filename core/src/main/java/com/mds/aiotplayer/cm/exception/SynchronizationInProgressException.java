package com.mds.aiotplayer.cm.exception;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when a user attempts to begin a synchronization when another one is already
///   in progress.
/// </summary>
public class SynchronizationInProgressException extends Exception {
   
	/// <summary>
	///   Throws an exception to indicate the requested synchronization cannot be started because another one is
	///   in progress.
	/// </summary>
	public SynchronizationInProgressException(){
		super(I18nUtils.getMessage("exception.synchronizationInProgress_Ex_Msg"));
	}
	
	/// <summary>
	///   Throws an exception to indicate the requested synchronization cannot be started because another one is
	///   in progress.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public SynchronizationInProgressException(String msg){
		super(msg);
	}

	/// <summary>
	///   Throws an exception to indicate the requested synchronization cannot be started because another one is
	///   in progress.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public SynchronizationInProgressException(String msg, Throwable cause){
		super(msg, cause);
	}	
}
