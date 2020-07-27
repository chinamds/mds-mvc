package com.mds.aiotplayer.cm.exception;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.common.exception.BaseException;
import com.mds.aiotplayer.i18n.util.I18nUtils;

/// <summary>
///   The exception that is thrown when MDS System encounters a file it does not recognize as
///   a valid content object (e.g. image, video, audio, etc.). This may be because the file is a type that
///   is disabled, or it may have an unrecognized file extension and the allowUnspecifiedMimeTypes configuration
///   setting is false.
/// </summary>
public class UnsupportedContentObjectTypeException extends BaseException {
    private static final long serialVersionUID = 4050482305178810162L;
   
    /// <summary>
	///   Throws an exception to indicate a file that is not recognized as a valid content object supported by
	///   MDS System. This may be because the file is a type that is disabled, or it may have an
	///   unrecognized file extension and the allowUnspecifiedMimeTypes configuration setting is false.
	/// </summary>
	public UnsupportedContentObjectTypeException(){
		super(I18nUtils.getMessage("exception.unsupportedContentObjectType_Ex_Msg"));
	}
	
	/// <summary>
	///   Throws an exception to indicate a file that is not recognized as a valid content object supported by
	///   MDS System. This may be because the file is a type that is disabled, or it may have an
	///   unrecognized file extension and the allowUnspecifiedMimeTypes configuration setting is false.
	/// </summary>
	/// <param name="msg">A message that describes the error.</param>
	/// <param name="innerException">
	///   The exception that is the cause of the current exception. If the
	///   innerException parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public UnsupportedContentObjectTypeException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	/// <summary>
	///   Throws an exception to indicate a file that is not recognized as a valid content object supported by
	///   MDS System. This may be because the file is a type that is disabled, or it may have an
	///   unrecognized file extension and the allowUnspecifiedMimeTypes configuration setting is false.
	/// </summary>
	/// <param name="filePath">
	///   The full filepath to the file that is not recognized as a valid content object
	///   (ex: C:\inetpub\wwwroot\ds\contentobjects\myvacation\\utah\bikingslickrock.jpg).
	/// </param>
	public UnsupportedContentObjectTypeException(String filePath){
		super(I18nUtils.getMessage("exception.unsupportedContentObjectType_Ex_Msg2", FileMisc.getExt(filePath)));
	}

	/// <summary>
	///   Throws an exception to indicate a file that is not recognized as a valid content object supported by
	///   MDS System. This may be because the file is a type that is disabled, or it may have an
	///   unrecognized file extension and the allowUnspecifiedMimeTypes configuration setting is false.
	/// </summary>
	/// <param name="file">The FileInfo object that is not recognized as a valid content object.</param>
	public UnsupportedContentObjectTypeException(File file)	{
		super(I18nUtils.getMessage("exception.unsupportedContentObjectType_Ex_Msg2", (file != null ? FileMisc.getExt(file.getPath()) : StringUtils.EMPTY)));
	}

	/// <summary>
	///   Throws an exception to indicate a file that is not recognized as a valid content object supported by
	///   MDS System. This may be because the file is a type that is disabled, or it may have an
	///   unrecognized file extension and the allowUnspecifiedMimeTypes configuration setting is false.
	/// </summary>
	/// <param name="file">The FileInfo object that is not recognized as a valid content object.</param>
	/// <param name="cause">
	///   The exception that is the cause of the current exception. If the
	///   cause parameter is not a null reference, the current exception is raised in a catch
	///   block that handles the inner exception.
	/// </param>
	public UnsupportedContentObjectTypeException(File file, Throwable cause){
		super(I18nUtils.getMessage("exception.unsupportedContentObjectType_Ex_Msg2", (file != null ? FileMisc.getExt(file.getPath()) : StringUtils.EMPTY)), cause);
	}
}
