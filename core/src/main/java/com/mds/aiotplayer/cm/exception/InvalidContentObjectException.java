/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.exception;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when an invalid content object is referenced.
/// </summary>
public class InvalidContentObjectException extends Exception {
   
	/// <summary>
	///   Throws an exception to indicate an invalid content object.
	/// </summary>
	public InvalidContentObjectException(){
		super(I18nUtils.getMessage("exception.invalidContentObject_Ex_Msg"));
	}
	
	/// <summary>
	///   Throws an exception to indicate an invalid content object.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public InvalidContentObjectException(String msg){
		super(msg);
	}

	/// <summary>
	///   Throws an exception to indicate an invalid content object.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public InvalidContentObjectException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	/// <summary>
	///   Throws an exception to indicate an invalid content object.
	/// </summary>
	/// <param name="contentObjectId">The ID of the content object that is not valid.</param>
	public InvalidContentObjectException(long contentObjectId)	{
		super(I18nUtils.getMessage("exception.invalidContentObject_Ex_Msg2", contentObjectId));
	}

	/// <summary>
	///   Throws an exception to indicate an invalid content object.
	/// </summary>
	/// <param name="contentObjectId">The ID of the content object that is not valid.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public InvalidContentObjectException(long contentObjectId, Throwable cause){
		super(I18nUtils.getMessage("exception.invalidContentObject_Ex_Msg2", contentObjectId), cause);
	}
}
