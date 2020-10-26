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
///   The exception that is thrown when MDS System is unable to write to, or delete from, a directory.
/// </summary>
public class CannotReadFromDirectoryException extends Exception {
   
	private String directoryPath;

	/// <summary>
	///   Throws an exception when MDS System is unable to write to, or delete from, a directory.
	/// </summary>
	public CannotReadFromDirectoryException(){
		super(I18nUtils.getMessage("exception.cannotReadFromDirectory_Ex_Msg"));
	}
	
	
	/// <summary>
	///   Throws an exception when MDS System is unable to write to, or delete from, a directory.
	/// </summary>
	/// <param name="directoryPath">The directory that cannot be written to.</param>
	public CannotReadFromDirectoryException(String directoryPath)	{
		super(I18nUtils.getMessage("exception.cannotReadFromDirectory_Ex_Msg2", directoryPath));
		this.directoryPath = directoryPath;
	}

	/// <summary>
	///   Throws an exception when MDS System is unable to write to, or delete from, a directory.
	/// </summary>
	/// <param name="directoryPath">The directory that cannot be written to.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public CannotReadFromDirectoryException(String directoryPath, Throwable cause){
		super(I18nUtils.getMessage("exception.cannotReadFromDirectory_Ex_Msg2", directoryPath), cause);
		this.directoryPath = directoryPath;
	}
	
	/// <summary>
	///   Gets the directory that cannot be written to. Example: C:\inetput\wwwroot\contentobjects
	/// </summary>
	public String getDirectoryPath(){
		return directoryPath; 
	}

	public void setDirectoryPath(String directoryPath){
		this.directoryPath = directoryPath; 
	}
}
