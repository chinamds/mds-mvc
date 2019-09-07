package com.mds.cm.content;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.util.CMUtils;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;

/// <summary>
/// Represents the settings used to control the encoding of one media type to another. For example, an
/// instance might store the FFmpeg command line arguments to use when converting .AVI files to .MP4.
/// </summary>
//[System.Diagnostics.DebuggerDisplay("{_sourceFileExtension} => {_destinationFileExtension}, Seq={_sequence}, Args={_encoderArguments}")]
public class ContentEncoderSettings implements Comparable<ContentEncoderSettings>
{
	private String sourceFileExtension;
	private String destinationFileExtension;
	private String encoderArguments;
	private int sequence;

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentEncoderSettings"/> class.
	/// </summary>
	/// <param name="sourceFileExtension">The source file extension.</param>
	/// <param name="destinationFileExtension">The destination file extension.</param>
	/// <param name="encoderArguments">The encoder arguments.</param>
	/// <param name="sequence">The sequence.</param>
	public ContentEncoderSettings(String sourceFileExtension, String destinationFileExtension, String encoderArguments, int sequence)
	{
		this.sourceFileExtension = sourceFileExtension;
		this.destinationFileExtension = destinationFileExtension;
		this.encoderArguments = encoderArguments;
		this.sequence = sequence;
	}


	/// <summary>
	/// Verifies the item contains valid data.
	/// </summary>
	/// <exception cref="UnsupportedContentObjectTypeException">Thrown when the instance references
	/// a file type not recognized by the application.</exception>
	public void validate() throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if ((shouldValidate(this.sourceFileExtension)) && CMUtils.loadMimeType(this.sourceFileExtension) == null){
			throw new UnsupportedContentObjectTypeException(String.format("The media encoder setting references a file extension ({0}) not recognized by the application.", this.sourceFileExtension));
		}

		if ((shouldValidate(this.destinationFileExtension)) && CMUtils.loadMimeType(this.destinationFileExtension) == null)	{
			throw new UnsupportedContentObjectTypeException(String.format("The media encoder setting references a file extension ({0}) not recognized by the application.", this.destinationFileExtension));
		}
	}

	/// <summary>
	/// Returns a value indicating whether the <paramref name="fileExtension" /> should be 
	/// validated.
	/// </summary>
	/// <param name="fileExtension">The file extension (e.g. ".avi").</param>
	/// <returns><c>true</c> if the value should be validated; otherwise <c>false</c>.</returns>
	private boolean shouldValidate(String fileExtension){
		String[] extensionsNotNeedingValidation = new String[] {"*audio", "*video"};
		return ArrayUtils.indexOf(extensionsNotNeedingValidation, fileExtension)<0;
		//return !Lists.newArrayList(extensionsNotNeedingValidation).contains(fileExtension);
	}


	/// <summary>
	/// Compares the current object with another object of the same type.
	/// </summary>
	/// <param name="other">An object to compare with this object.</param>
	/// <returns>
	/// A 32-bit signed integer that indicates the relative order of the objects being compared. The return value has the following meanings: Value Meaning Less than zero This object is less than the <paramref name="other"/> parameter.Zero This object is equal to <paramref name="other"/>. Greater than zero This object is greater than <paramref name="other"/>.
	/// </returns>
	@Override
	public int compareTo(ContentEncoderSettings o) {
		if (o == null)
			return 1;
		else
		{
			return Integer.compare(this.sequence, o.sequence);
		}
	}


	/**
	 * @return the encoderArguments
	 */
	public String getEncoderArguments() {
		return encoderArguments;
	}


	/**
	 * @param encoderArguments the encoderArguments to set
	 */
	public void setEncoderArguments(String encoderArguments) {
		this.encoderArguments = encoderArguments;
	}


	public int getSequence() {
		return sequence;
	}


	public String getSourceFileExtension() {
		return sourceFileExtension;
	}


	public String getDestinationFileExtension() {
		return destinationFileExtension;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ContentEncoderSettings)) {
            return false;
        } else {
        	ContentEncoderSettings castObj = (ContentEncoderSettings) obj;
            if (null == this.getSourceFileExtension() || null == castObj.getSourceFileExtension()) {
                return false;
            } else {
                return (this.getSourceFileExtension().equals(castObj.getSourceFileExtension()));
            }
        }
    }

    @Override
    public int hashCode() {
        if (null == this.getSourceFileExtension()) {
            return super.hashCode();
        }
        String hashStr = this.getClass().getName() + ":" + this.getSourceFileExtension().hashCode();
        return hashStr.hashCode();
    }

    @Override
    public String toString() {
        return "{" + this.getSourceFileExtension() + "} => {" + getDestinationFileExtension() 
        	+ "}, Seq={" + getSequence() + "}, Args={" + getEncoderArguments() + "}";
    }
}