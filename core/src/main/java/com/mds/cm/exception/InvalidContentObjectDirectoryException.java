package com.mds.cm.exception;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.mds.cm.content.ContentObjectBo;
import com.mds.util.FileMisc;
import com.mds.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when MDS System cannot find a directory.
/// </summary>
public class InvalidContentObjectDirectoryException extends Exception {
   
	private String contentObjectPath;

	/// <summary>
	///   Throws an exception to indicate an invalid content objects directory.
	/// </summary>
	public InvalidContentObjectDirectoryException(){
		super(I18nUtils.getMessage("exception.invalidContentObjectsDirectory_Ex_Msg"));
	}
		
	/// <summary>
	///   Throws an exception to indicate an invalid content objects directory.
	/// </summary>
	/// <param name="contentObjectPath">The content object directory that is not valid.</param>
	public InvalidContentObjectDirectoryException(String contentObjectPath)	{
		super(I18nUtils.getMessage("exception.invalidContentObjectsDirectory_Ex_Msg2", contentObjectPath));
		this.contentObjectPath = contentObjectPath;
	}


	/// <summary>
	///   Throws an exception to indicate an invalid content objects directory.
	/// </summary>
	/// <param name="contentObjectPath">The content object directory that is not valid.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public InvalidContentObjectDirectoryException(String contentObjectPath, Throwable cause){
		super(I18nUtils.getMessage("exception.invalidContentObjectsDirectory_Ex_Ms2", contentObjectPath), cause);
		this.contentObjectPath = contentObjectPath;
	}
	

	/// <summary>
	///   Gets the content object directory that cannot be written to. Example: C:\inetput\wwwroot\contentobjects
	/// </summary>
	public String getContentObjectPath(){
		return contentObjectPath; 
	}

	public void setContentObjectPath(String contentObjectPath){
		this.contentObjectPath = contentObjectPath; 
	}
}
