package com.mds.cm.content;

import com.mds.cm.exception.InvalidGalleryException;
import com.mds.common.exception.BaseException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.ArgumentNullException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kevin on 16/07/15 for Podcast Server
 */
public class ContentEncoderSettingsCollection extends ArrayList<ContentEncoderSettings> {
   
    /// <summary>
  	/// Initializes a new instance of the <see cref="ContentEncoderSettingsCollection"/> class.
  	/// </summary>
	public ContentEncoderSettingsCollection(){
		super(new ArrayList<ContentEncoderSettings>());
	}

	public ContentEncoderSettingsCollection(Iterable<ContentEncoderSettings> encoderSettings){
		addRange(encoderSettings);
	}

    /**
     * {@inheritDoc}
     */
	public void addRange(Iterable<ContentEncoderSettings> mediaEncoderSettings) {
    	
		if (mediaEncoderSettings == null)
			throw new ArgumentNullException("mediaEncoderSettings");

		for (ContentEncoderSettings mediaEncoderSetting : mediaEncoderSettings)	{
			add(mediaEncoderSetting);
		}
	}

	 /**
     * {@inheritDoc}
     */
	public void addContentEncoderSettings(ContentEncoderSettings item){
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing ContentEncoderSettingsCollection. Items.Count = " + size());

		add(item);
	}

	 /**
     * {@inheritDoc}
	 * @throws UnsupportedContentObjectTypeException 
	 * @throws InvalidGalleryException 
     */
	public void validate() throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		for (ContentEncoderSettings setting : this)	{
			setting.validate();
		}
	}

	 /**
     * {@inheritDoc}
     */
	public String serialize(){
		StringBuilder sb = new StringBuilder();
		Collections.sort(this);

		// Now that it is sorted, we can iterate in increasing sequence. Validate as we go along to ensure each 
		// sequence is equal to or higher than the one before.
		int lastSeq = 0;
		for(ContentEncoderSettings encoderSetting : this){
			if (encoderSetting.getSequence() < lastSeq)	{
				throw new BaseException(getClass().getSimpleName(), "Cannot serialize contentEncoderSettings because the underlying collection is not in ascending sequence.");
			}

			sb.append(MessageFormat.format("{0}||{1}||{2}~~", encoderSetting.getSourceFileExtension(), encoderSetting.getDestinationFileExtension(), encoderSetting.getEncoderArguments()));

			lastSeq = encoderSetting.getSequence();
		}

		sb.delete(sb.length() - 2, 2); // Remove the final ~~

		return sb.toString();
	}
}
