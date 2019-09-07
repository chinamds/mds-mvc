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
public class UnsupportedImageTypeException extends Exception {
   
    /// <summary>
	///   Throws an exception to indicate a file that is not recognized as a valid content object supported by
	///   MDS System. This may be because the file is a type that is disabled, or it may have an
	///   unrecognized file extension and the allowUnspecifiedMimeTypes configuration setting is false.
	/// </summary>
	public UnsupportedImageTypeException(){
		super(I18nUtils.getMessage("exception.unsupportedImageType_Ex_Msg"));
	}
	
	/// <summary>
	///   Throws an exception to indicate the .NET Framework is unable to load an image file into the System.Drawing.Bitmap
	///   class. This is probably because it is corrupted, not an image supported by the .NET Framework, or the server does
	///   not have enough memory to process the image.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	public UnsupportedImageTypeException(String msg){
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
	public UnsupportedImageTypeException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	/// <summary>
	///   Throws an exception to indicate the .NET Framework is unable to load an image file into the System.Drawing.Bitmap
	///   class. This is probably because it is corrupted, not an image supported by the .NET Framework, or the server does
	///   not have enough memory to process the image.
	/// </summary>
	/// <param name="contentObject">The content object that contains the unsupported image file.</param>
	public UnsupportedImageTypeException(ContentObjectBo contentObject)	{
		super(I18nUtils.getMessage("exception.unsupportedImageType_Ex_Msg2", ((contentObject != null) && (contentObject.getOriginal() != null) ? contentObject.getOriginal().getFileName() : StringUtils.EMPTY)));
	}

	/// <summary>
	///   Throws an exception to indicate the .NET Framework is unable to load an image file into the System.Drawing.Bitmap
	///   class. This is probably because it is corrupted, not an image supported by the .NET Framework, or the server does
	///   not have enough memory to process the image.
	/// </summary>
	/// <param name="contentObject">The content object that contains the unsupported image file.</param>
	/// <parcauseException">
	///   The exception that is the cause of the current exception. If the
	///   cause parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public UnsupportedImageTypeException(ContentObjectBo contentObject, Throwable cause){
		super(I18nUtils.getMessage("exception.unsupportedImageType_Ex_Msg2", ((contentObject != null) && (contentObject.getOriginal() != null) ? contentObject.getOriginal().getFileName() : StringUtils.EMPTY)), cause);
	}
}
