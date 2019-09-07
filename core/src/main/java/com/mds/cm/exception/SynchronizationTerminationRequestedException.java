package com.mds.cm.exception;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.mds.cm.content.ContentObjectBo;
import com.mds.util.FileMisc;
import com.mds.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when a user attempts to begin a synchronization when another one is already
///   in progress.
/// </summary>
public class SynchronizationTerminationRequestedException extends Exception {
   
	/// <summary>
	///   Throws an exception to indicate the requested synchronization cannot be started because another one is
	///   in progress.
	/// </summary>
	public SynchronizationTerminationRequestedException(){
		super(I18nUtils.getMessage("exception.synchronizationTerminationRequested_Ex_Msg"));
	}
	
	/// <summary>
	///   Throws an exception to indicate the requested synchronization cannot be started because another one is
	///   in progress.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public SynchronizationTerminationRequestedException(String msg){
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
	public SynchronizationTerminationRequestedException(String msg, Throwable cause){
		super(msg, cause);
	}	
}
