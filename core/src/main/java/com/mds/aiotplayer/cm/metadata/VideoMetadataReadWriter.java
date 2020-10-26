/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.FFmpegWrapper;
import com.mds.aiotplayer.core.MetadataEnumHelper;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.Orientation;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Provides functionality for reading and writing metadata to or from a video file.
/// </summary>
public class VideoMetadataReadWriter extends MediaObjectMetadataReadWriter{
	//#region Fields

	private String ffmpegOutput;

	//#endregion

	//#region Properties

	private String getffmpegOutput(){
		if (ffmpegOutput == null) {
			ffmpegOutput = getFFmpegOutput();
		}
		
		return ffmpegOutput;
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="VideoMetadataReadWriter" /> class.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	public VideoMetadataReadWriter(ContentObjectBo contentObject){
		super(contentObject);
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Gets the metadata value for the specified <paramref name="metaName" />.
	/// </summary>
	/// <param name="metaName">Name of the metadata item to retrieve.</param>
	/// <returns>An instance that implements <see cref="MetaValue" />.</returns>
	@Override
	public MetaValue getMetaValue(MetadataItemName metaName){
		switch (metaName){
			case Duration: return getDuration();
			case BitRate: return getBitRate();
			case AudioFormat: return getAudioFormat();
			case VideoFormat: return getVideoFormat();
			case Width: return getWidth();
			case Height: return getHeight();
			case Orientation: return getRotation();
			default:
				return super.getMetaValue(metaName);
		}
	}

	//#endregion

	//#region Metadata Functions

	private MetaValue getDuration()	{
		Pattern re = Pattern.compile("[D|d]uration:.((\\d|:|\\.)*)");
		Matcher m = re.matcher(getffmpegOutput());
		
		return (m.find() ? new MetaValue(m.group(1).trim(), m.group(1).trim()) : null);
	}

	private MetaValue getBitRate(){
		Pattern re = Pattern.compile("[B|b]itrate:.((\\d|:)*)");
		Matcher m = re.matcher(getffmpegOutput());
		
		double kb;
		if (m.find() && (kb = StringUtils.toDouble(m.group(1), Double.MIN_VALUE)) != Double.MIN_VALUE){
			//TODO: Parse bitrate units instead of assuming they are kb/s
			// Line we are parsing looks like this: Duration: 00:00:25.27, start: 0.000000, bitrate: 932 kb/s
			return new MetaValue(StringUtils.join(new Object[] {kb, " kb/s"}), Double.toString(kb));
		}else{
			return null;
		}
	}

	private MetaValue getAudioFormat(){
		Pattern re = Pattern.compile("[A|a]udio:.*");
		Matcher m = re.matcher(getffmpegOutput());
		return (m.find() ? new MetaValue(m.group().trim(), m.group().trim()) : null);
	}

	private MetaValue getVideoFormat()	{
		Pattern re = Pattern.compile("[V|v]ideo:.*");
		Matcher m = re.matcher(getffmpegOutput());
		return (m.find() ? new MetaValue(m.group().trim(), m.group().trim()) : null);
	}

	private MetaValue getWidth()	{
		int width = FFmpegWrapper.parseSourceVideoWidth(getffmpegOutput());

		return (width > Integer.MIN_VALUE ? new MetaValue(StringUtils.join(new Object[] {width, " ", "{metadata.width_Units}"}), Integer.toString(width)) : null);
	}

	private MetaValue getHeight(){
		int height = FFmpegWrapper.parseSourceVideoHeight(getffmpegOutput());

		return (height > Integer.MIN_VALUE ? new MetaValue(StringUtils.join(new Object[] {height, " ", "{metadata.height_Units}"}), Integer.toString(height)) : null);
	}

	private MetaValue getRotation()	{
		Orientation orientation = FFmpegWrapper.parseOrientation(getffmpegOutput());

		if (MetadataEnumHelper.isValidOrientation(orientation) && (orientation != Orientation.None)){
			return new MetaValue(orientation.getDescription(), orientation.toString());
		}
		return null;
	}

	//#endregion

	//#region Functions

	private String getFFmpegOutput(){
		return FFmpegWrapper.getOutput(contentObject.getOriginal().getFileNamePhysicalPath(), contentObject.getGalleryId());
	}

	//#endregion
}