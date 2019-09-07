package com.mds.cm.metadata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.FFmpegWrapper;
import com.mds.core.MetadataItemName;

/// <summary>
/// Provides functionality for reading and writing metadata to or from an audio file.
/// </summary>
public class AudioMetadataReadWriter extends MediaObjectMetadataReadWriter
{
	//#region Fields

	private String ffmpegOutput;

	//#endregion

	//#region Properties

	private String getFfmpegOutput(){
		if (ffmpegOutput == null)
			ffmpegOutput = getFFmpegOutput();
		
		return ffmpegOutput;
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="AudioMetadataReadWriter" /> class.
	/// </summary>
	/// <param name="contentObject">The gallery object.</param>
	public AudioMetadataReadWriter(ContentObjectBo contentObject){
		super(contentObject);
	}
	

	//#endregion

	//#region Methods

	/// <summary>
	/// Gets the metadata value for the specified <paramref name="metaName" />.
	/// </summary>
	/// <param name="metaName">Name of the metadata item to retrieve.</param>
	/// <returns>An instance that implements <see cref="MetaValue" />.</returns>
	public MetaValue getMetaValue(MetadataItemName metaName){
		switch (metaName)
		{
			case Duration: return getDuration();
			case BitRate: return getBitRate();
			case AudioFormat: return getAudioFormat();
			default:
				return super.getMetaValue(metaName);
		}
	}

	//#endregion

	//#region Metadata Functions

	private MetaValue getDuration()	{
		Pattern re = Pattern.compile("[D|d]uration:.((\\d|:|\\.)*)");
		Matcher m = re.matcher(ffmpegOutput);

		return (m.matches() ? new MetaValue(m.group(1).trim(), m.group(1).trim()) : null);
	}

	private MetaValue getBitRate(){
		Pattern re = Pattern.compile("[B|b]itrate:.((\\d|:)*)");
		Matcher m = re.matcher(ffmpegOutput);
		if (m.matches())
		{
			Double kb = NumberUtils.toDouble(m.group(1));
			//TODO: Parse bitrate units instead of assuming they are kb/s
			// Line we are parsing looks like this: Duration: 00:00:25.27, start: 0.000000, bitrate: 932 kb/s
			return new MetaValue(StringUtils.join(new Object[] {kb, " kb/s"}), kb.toString());
		}
		else
		{
			return null;
		}
	}

	private MetaValue getAudioFormat(){
		Pattern re = Pattern.compile("[A|a]udio:.*");
		Matcher m = re.matcher(ffmpegOutput);
		return (m.matches() ? new MetaValue(m.group(1).trim(), m.group(1).trim()) : null);
	}

	//#endregion

	//#region Functions

	private String getFFmpegOutput(){
		return FFmpegWrapper.getOutput(contentObject.getOriginal().getFileNamePhysicalPath(), contentObject.getGalleryId());
	}

	//#endregion
}