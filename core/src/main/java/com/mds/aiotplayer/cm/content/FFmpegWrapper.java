package com.mds.aiotplayer.cm.content;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.StrBuilder;

import com.mds.aiotplayer.core.CancelToken;
import com.mds.aiotplayer.core.ContentObjectRotation;
import com.mds.aiotplayer.core.Orientation;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.sys.service.AppSettingManager;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.MathUtil;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.custom.ffmpeg.CustomRunProcessFunc;
import com.mds.aiotplayer.util.custom.ffmpeg.ProcessListener;

import io.vavr.control.Try;

import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ProcessBuilder.Redirect;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;

/// <summary>
/// Contains functionality for interacting with FFmpeg, the open source utility. Specifically, MDS System uses it to generate
/// thumbnail images for video and to extract metadata about video and audio files. See http://www.ffmpeg.org for more information.
/// </summary>
public class FFmpegWrapper {
	private StrBuilder output;
	private StrBuilder outputErr;

	/// <summary>
	/// The name of the automatic rotation filter. Example: "{AutoRotateFilter}"
	/// </summary>
	private final static String AutoRotateFilterName = "{AutoRotateFilter}";
	private final CustomRunProcessFunc runProcessFunc = new CustomRunProcessFunc();

	/// <summary>
	/// The regular expression pattern that can be used to identify the video dimensions in FFmpeg output.
	/// </summary>
	private final static String VideoDimensionsRegExPattern = "Video:.+\\s(\\d+)x(\\d+)";


	private FFmpegWrapper(ContentConversionSettings mediaSettings)	{
		ContentSettings = mediaSettings;
		output = new StrBuilder();
		outputErr = new StrBuilder();
	}


	/// <summary>
	/// Gets or sets the conversion settings used to process the content object.
	/// </summary>
	/// <value>The media settings.</value>
	private ContentConversionSettings ContentSettings;

	private String getOutput(){
		synchronized (this.output){
			if (!this.outputErr.isEmpty())
				this.output.append(this.outputErr);
			
			return this.output.toString();
		}
	}

	/// <summary>
	/// Gets a value indicating whether FFmpeg is available for use by the application. Returns <c>true</c>
	/// when the application is running in full trust and ffmpeg.exe exists; otherwise returns <c>false</c>.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if FFmpeg is available for use by the application; otherwise, <c>false</c>.
	/// </value>
	public static boolean isAvailable()	{
/*		return ((AppSettings.getInstance().AppTrustLevel == ApplicationTrustLevel.Full) &&
			(!StringUtils.isBlank(AppSettings.getInstance().FFmpegPath)));*/
		return (!StringUtils.isBlank(AppSettings.getInstance().getFFmpegPath()));
	}


	/// <summary>
	/// Generates a thumbnail image for the video at the specified <paramref name="mediaFilePath"/> and returns the output from the
	/// execution of the FFmpeg utility. The thumbnail is created at the same width and height as the original video and saved to
	/// <paramref name="thumbnailFilePath"/>. The <paramref name="galleryId"/> is used during error handling to associate the error,
	/// if any, with the gallery. Requires the application to be running at Full Trust. Returns <see cref="StringUtils.EMPTY"/> when the
	/// application is running at less than Full Trust or when the FFmpeg utility is not present in the bin directory.
	/// </summary>
	/// <param name="mediaFilePath">The full file path to the source video file. Example: D:\media\video\myvideo.flv</param>
	/// <param name="thumbnailFilePath">The full file path to store the thumbnail image to. If a file with this name is already present,
	/// it is overwritten.</param>
	/// <param name="videoThumbnailPosition">The position, in seconds, in the video where the thumbnail is generated from a frame.
	/// If the video is shorter than the number of seconds specified here, no thumbnail is created.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>
	/// Returns the text output from the execution of the FFmpeg utility. This data can be parsed to learn more about the media file.
	/// </returns>
	public static String generateThumbnail(String mediaFilePath, String thumbnailFilePath, int videoThumbnailPosition, long galleryId)	{
		String ffmpegOutput = StringUtils.EMPTY;

		if (!isAvailable())	{
			return ffmpegOutput;
		}

		// Call FFmpeg, which will generate the file at the specified location

		//Duration timeSpan = Duration.ofSeconds(videoThumbnailPosition);
		int hours= videoThumbnailPosition/3600;
		int minutes = (videoThumbnailPosition - 3600*hours)/60;
		int seconds = videoThumbnailPosition - 3600*hours - minutes*60;
		String videoThumbnailPositionStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);

		// The -ss parameter must be a String in this format: HH:mm:ss. Ex: "00:00:03" for 3 seconds
		String args = MessageFormat.format("-ss {0} -i \"{1}\" -an -an -r 1 -vframes 1 -y \"{2}\"", videoThumbnailPositionStr, mediaFilePath, thumbnailFilePath);

		return executeFFmpeg(args, galleryId);
	}

	/// <summary>
	/// Creates a media file based on an existing one using the values in the 
	/// <paramref name="mediaSettings" /> parameter. The output from FFmpeg is returned. The 
	/// arguments passed to FFmpeg are stored on the
	/// <see cref="ContentConversionSettings.FFmpegArgs" /> property.
	/// </summary>
	/// <param name="mediaSettings">The settings which dicate the media file creation process.</param>
	/// <returns>Returns the text output from FFmpeg.</returns>
	public static String createContent(ContentConversionSettings mediaSettings)	{
		if (!isAvailable())	{
			return StringUtils.EMPTY;
		}

		if (mediaSettings == null)
			throw new ArgumentNullException("mediaSettings");

		if (mediaSettings.EncoderSetting == null)
			throw new ArgumentNullException("mediaSettings", "The EncoderSetting property on the mediaSettings parameter was null.");

		mediaSettings.FFmpegArgs = replaceTokens(mediaSettings.EncoderSetting.getEncoderArguments(), mediaSettings);

		return executeFFmpeg(mediaSettings);
	}

	/// <summary>
	/// Returns the output from the execution of the FFmpeg utility against the media file stored at 
	/// <paramref name="mediaFilePath" />. This data can be parsed for useful information such as duration, 
	/// width, height, and bit rates. The utility does not alter the file. The <paramref name="galleryId" /> 
	/// is used during error handling to associate the error, if any, with the gallery. Requires the 
	/// application to be running at Full Trust. Returns <see cref="StringUtils.EMPTY" /> when the 
	/// application is running at less than Full Trust or when the FFmpeg utility is not present.
	/// </summary>
	/// <param name="mediaFilePath">The full file path to the source video file. Example: D:\media\video\myvideo.flv</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns the text output from the execution of the FFmpeg utility. This data can be parsed to 
	/// learn more about the media file.</returns>
	public static String getOutput(String mediaFilePath, long galleryId){
		String ffmpegOutput = StringUtils.EMPTY;

		if (!isAvailable()){
			return ffmpegOutput;
		}

		String args = MessageFormat.format("-i \"{0}\"", mediaFilePath);

		return executeFFmpeg(args, galleryId);
	}

	/// <summary>
	/// Parse the FFmpeg output for the width of the source video, returning <see cref="Int32.MinValue" /> if not found.
	/// </summary>
	/// <param name="ffmpegOutput">The FFmpeg output.</param>
	/// <returns><see cref="System.Int32" />.</returns>
	/// <remarks>The width is parsed using this regular expression pattern: @"Video:.+\s(\d+)x(\d+)". It says to look
	/// for a line beginning with "Video:" followed by one or more characters, then a white space character, followed
	/// by a number pattern like "320x240" or "3200x900". The width is assumed to be the first of these numbers. We
	/// also assume the source dimensions come from the first match (the output dimensions can be found on the second
	/// match).</remarks>
	public static int parseSourceVideoWidth(String ffmpegOutput){
		Pattern re = Pattern.compile(VideoDimensionsRegExPattern);
		Matcher m = re.matcher(ffmpegOutput);
		if (m.find()){
			return StringUtils.toInteger(m.group(1), Integer.MIN_VALUE);
		}

		return Integer.MIN_VALUE;
	}

	/// <summary>
	/// Parse the FFmpeg output for the width of the output video, returning <see cref="Int32.MinValue" /> if not found.
	/// </summary>
	/// <param name="ffmpegOutput">The FFmpeg output.</param>
	/// <returns><see cref="System.Int32" />.</returns>
	/// <remarks>The width is parsed using this regular expression pattern: @"Video:.+\s(\d+)x(\d+)". It says to look
	/// for a line beginning with "Video:" followed by one or more characters, then a white space character, followed
	/// by a number pattern like "320x240" or "3200x900". The width is assumed to be the first of these numbers. We
	/// also assume the output dimensions come from the second match (the input dimensions can be found on the first
	/// match).</remarks>
	public static int parseOutputVideoWidth(String ffmpegOutput){
		Pattern re = Pattern.compile(VideoDimensionsRegExPattern);
		Matcher m = re.matcher(ffmpegOutput);
		if (m.find()){
			return StringUtils.toInteger(m.group(1), Integer.MIN_VALUE);
		}

		return Integer.MIN_VALUE;
	}

	/// <summary>
	/// Parse the FFmpeg output for the height of the source video, returning <see cref="Int32.MinValue" /> if not found.
	/// </summary>
	/// <param name="ffmpegOutput">The FFmpeg output.</param>
	/// <returns><see cref="System.Int32" />.</returns>
	/// <remarks>The height is parsed using this regular expression pattern: @"Video:.+\s(\d+)x(\d+)". It says to look
	/// for a line beginning with "Video:" followed by one or more characters, then a white space character, followed
	/// by a number pattern like "320x240" or "3200x900". The height is assumed to be the second of these numbers. We
	/// also assume the source dimensions come from the first match (the output dimensions can be found on the second
	/// match).</remarks>
	public static int parseSourceVideoHeight(String ffmpegOutput){
		Pattern re = Pattern.compile(VideoDimensionsRegExPattern);
		Matcher m = re.matcher(ffmpegOutput);
		if (m.find()){
			return StringUtils.toInteger(m.group(2), Integer.MIN_VALUE);
		}

		return Integer.MIN_VALUE;
	}

	/// <summary>
	/// Parse the FFmpeg output for the height of the output video, returning <see cref="Int32.MinValue" /> if not found.
	/// </summary>
	/// <param name="ffmpegOutput">The FFmpeg output.</param>
	/// <returns><see cref="System.Int32" />.</returns>
	/// <remarks>The height is parsed using this regular expression pattern: @"Video:.+\s(\d+)x(\d+)". It says to look
	/// for a line beginning with "Video:" followed by one or more characters, then a white space character, followed
	/// by a number pattern like "320x240" or "3200x900". The height is assumed to be the second of these numbers. We
	/// also assume the output dimensions come from the second match (the input dimensions can be found on the first
	/// match).</remarks>
	public static int parseOutputVideoHeight(String ffmpegOutput){
		Pattern re = Pattern.compile(VideoDimensionsRegExPattern);
		Matcher m = re.matcher(ffmpegOutput);
		if (m.find()){
			return StringUtils.toInteger(m.group(2), Integer.MIN_VALUE);
		}

		return Integer.MIN_VALUE;
	}

	/// <summary>
	/// Parse the FFmpeg output for the orientation of the media item, returning <see cref="Orientation.None" /> if not found.
	/// </summary>
	/// <param name="ffmpegOutput">The FFmpeg output.</param>
	/// <returns>An instance of <see cref="Orientation" />.</returns>
	/// <remarks>The orientation is parsed using this regular expression pattern: @"[Rr]otate\s*:\s*(\d+)". It says to look
	/// for a line beginning with "rotate:" followed by zero or more spaces, then a colon, then zero or more spaces, followed
	/// by a number like "90" or "180".</remarks>
	public static Orientation parseOrientation(String ffmpegOutput)	{
		Pattern re = Pattern.compile("[Rr]otate\\s*:\\s*(\\d+)");
		Matcher m = re.matcher(ffmpegOutput);
		if (m.find()){
			int rotation = StringUtils.toInteger(m.group(1), Integer.MIN_VALUE);;
			if (rotation != Integer.MIN_VALUE){
				switch (rotation){
					case 0:
						return Orientation.Normal;
					case 90:
						return Orientation.Rotated270;
					case 180:
						return Orientation.Rotated180;
					case 270:
						return Orientation.Rotated90;
				}
			}
		}

		return Orientation.None;
	}


	/// <summary>
	/// Execute the FFmpeg utility with the given <paramref name="arguments"/> and return the text output generated by it.
	/// A default timeout value of 3 seconds is used, which can be overridden with the <paramref name="timeoutMs" /> parameter.
	/// See http://www.ffmpeg.org for documentation.
	/// </summary>
	/// <param name="arguments">The argument values to pass to the FFmpeg utility.
	/// Example: -ss 00:00:03 -i "D:\media\video\myvideo.flv" -an -vframes 1 -y "D:\media\video\zThumb_myvideo.jpg"</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <param name="timeoutMs">Gets or sets the timeout to apply to FFmpeg, in milliseconds. Defaults to 3 seconds if not
	/// specified.</param>
	/// <returns>Returns the text output from the execution of the FFmpeg utility. This data can be parsed to learn more about the media file.</returns>
	private static String executeFFmpeg(String arguments, long galleryId){
		return executeFFmpeg(arguments, galleryId, 3000);
	}
	
	private static String executeFFmpeg(String arguments, long galleryId, int timeoutMs){
		
		ContentConversionSettings mediaSettings = new ContentConversionSettings();
		mediaSettings.FilePathSource = StringUtils.EMPTY;
		mediaSettings.FilePathDestination = StringUtils.EMPTY;
		mediaSettings.EncoderSetting = null;
		mediaSettings.GalleryId = galleryId;
		mediaSettings.ContentQueueId = Long.MIN_VALUE;
		mediaSettings.TimeoutMs = timeoutMs;
		mediaSettings.ContentObjectId = Long.MIN_VALUE;
		mediaSettings.FFmpegArgs = arguments;
		mediaSettings.FFmpegOutput = StringUtils.EMPTY;

		return executeFFmpeg(mediaSettings);
	}

	/// <summary>
	/// Execute the FFmpeg utility with the given <paramref name="mediaSettings"/> and return the text output generated by it.
	/// See http://www.ffmpeg.org for documentation.
	/// </summary>
	/// <param name="mediaSettings">The media settings.</param>
	/// <returns>
	/// Returns the text output from the execution of the FFmpeg utility. This data can be parsed to learn more about the media file.
	/// </returns>
	private static String executeFFmpeg(ContentConversionSettings mediaSettings){
		FFmpegWrapper ffmpeg = new FFmpegWrapper(mediaSettings);
		ffmpeg.execute();
		mediaSettings.FFmpegOutput = ffmpeg.getOutput();

		return mediaSettings.FFmpegOutput;
	}

	private static String replaceTokens(String encoderArguments, ContentConversionSettings mediaSettings){
		encoderArguments = encoderArguments.replace("{SourceFilePath}", mediaSettings.FilePathSource);
		encoderArguments = encoderArguments.replace("{Width}", String.valueOf(mediaSettings.TargetWidth));
		encoderArguments = encoderArguments.replace("{Height}", String.valueOf(mediaSettings.TargetHeight));
		encoderArguments = encoderArguments.replace(AutoRotateFilterName, getAutoRotationFilter(ContentConversionQueue.getInstance().get(mediaSettings.ContentQueueId).RotationAmount, encoderArguments));
		encoderArguments = encoderArguments.replace("{AspectRatio}", String.valueOf(MathUtil.round(mediaSettings.TargetWidth / (double)mediaSettings.TargetHeight, 2)));
		encoderArguments = encoderArguments.replace("{DestinationFilePath}", mediaSettings.FilePathDestination);
		encoderArguments = encoderArguments.replace("{BinPath}", FilenameUtils.concat(AppSettings.getInstance().getPhysicalApplicationPath(), "bin"));
		encoderArguments = encoderArguments.replace("{GalleryResourcesPath}", FilenameUtils.concat(AppSettings.getInstance().getPhysicalApplicationPath(), AppSettings.getInstance().getGalleryResourcesPath()));

		// If the above changes result in an empty filter setting, remove it altogether.
		encoderArguments = encoderArguments.replace("-vf \"\"\"\"", StringUtils.EMPTY);
		encoderArguments = encoderArguments.replace("-vf \"\"", StringUtils.EMPTY);
		
		return encoderArguments;
	}

	/// <summary>
	/// Gets the rotation filter corresponding to the requested rotation amount. Returns an empty String when
	/// no rotation is requested.
	/// </summary>
	/// <param name="rotation">The amount of rotation being requested.</param>
	/// <param name="encoderArguments"></param>
	/// <returns>System.String.</returns>
	/// <remarks>Documentation: http://ffmpeg.org/ffmpeg-filters.html#transpose, http://ffmpeg.org/ffmpeg-filters.html#hflip,
	/// http://ffmpeg.org/ffmpeg-filters.html#vflip</remarks>
	private static String getAutoRotationFilter(ContentObjectRotation rotation, String encoderArguments){
		String filter = StringUtils.EMPTY;

		if (!encoderArguments.contains(AutoRotateFilterName))
			return filter;

		switch (rotation){
			case Rotate90: filter = "transpose=clock"; break;
			case Rotate180: filter = "hflip,vflip"; break;
			case Rotate270: filter = "transpose=cclock"; break;
		}

		return applyCommaToRotationFilter(filter, encoderArguments);
	}

	/// <summary>
	/// Conditionally apply a comma to the <paramref name="filter" />. When no filter is specified or the filter
	/// is the only value in the filter arguments section, no comma is needed. But when there is more than one, 
	/// it is required. This function checks for this condition and adds it to the beginning or end as required.
	/// </summary>
	/// <param name="filter">The filter. Examples: "transpose=clock", "hflip,vflip"</param>
	/// <param name="encoderArguments">The encoder arguments. </param>
	/// <returns>Returns the <paramref name="filter" /> parameter with either a leading or trailing comma
	/// as required.</returns>
	private static String applyCommaToRotationFilter(String filter, String encoderArguments){
		String fixedFilter = filter;
		int idx = encoderArguments.indexOf(AutoRotateFilterName);

		if (idx < 0 || StringUtils.isBlank(filter))
			return fixedFilter;

		String[] quotes = new String[] {"\"", "'"};
		String firstChar = encoderArguments.substring(idx - 1, 1);
		String lastChar = encoderArguments.substring(idx + AutoRotateFilterName.length(), 1);

		if (!ArrayUtils.contains(quotes, firstChar)){
			fixedFilter = StringUtils.join(new String[] {",", fixedFilter});
		}

		if (!ArrayUtils.contains(quotes, lastChar))	{
			fixedFilter = StringUtils.join(new String[] {fixedFilter, ","});
		}

		return fixedFilter;
	}

	/// <summary>
	/// Run the FFmpeg executable with the specified command line arguments.
	/// </summary>
	private void execute()	{
		boolean processCompletedSuccessfully = false;

		initializeOutput();
		
/*		ProcessListener pl = new ProcessListener(AppSettings.getInstance().getFFmpegPath());
        runProcessFunc.add(pl);


        Future<Process> process = pl.getProcess();*/

		//Runtime runtime = Runtime.getRuntime(); //.exec()
		List<String> cmds = Lists.newArrayList(AppSettings.getInstance().getFFmpegPath());
		ContentSettings.FFmpegArgs = ContentSettings.FFmpegArgs.replace("\"", "\"\"");
		String[] args = StringUtils.splitPreserveAllTokens(ContentSettings.FFmpegArgs, "\"");
		boolean startText = false;
		boolean endText = false;
		for(String arg : args) {
			if (!StringUtils.isBlank(arg)) {
				if (!startText) {
					cmds.addAll(Lists.newArrayList(StringUtils.split(arg)));
				}else {
					cmds.add("\"" + arg + "\"");				
					startText = false;
					endText = true;
				}
			}else {
				if (!endText) {
					startText = true;
				}else {
					endText = false;
				}
			}
		}
		//cmds.addAll(Lists.newArrayList(StringUtils.split(ContentSettings.FFmpegArgs)));
        //ProcessBuilder info = new ProcessBuilder(AppSettings.getInstance().getFFmpegPath(), ContentSettings.FFmpegArgs); 
		ProcessBuilder info = new ProcessBuilder(cmds);
		info.redirectErrorStream(true);
		//info.redirectOutput(Redirect.appendTo(log));

		Process p = null;
		try
		{
			p = info.start();

			 final InputStream is1 = p.getInputStream();   
			 final InputStream is2 = p.getErrorStream();  
			 new Thread() {  
			    public void run() {  
			        try {
			        	BufferedReader br1 = new BufferedReader(new InputStreamReader(is1, "utf-8"));
			            String line1 = null;  
			            while ((line1 = br1.readLine()) != null) {  
			                  if (line1 != null){
			                	  output.appendln(line1);
			                  }  
			              }
			            
			              ContentQueueItem item = ContentConversionQueue.getInstance().getCurrentContentQueueItem();
			     		  if ((item != null) && (item.ContentQueueId == ContentSettings.ContentQueueId)){
			     		 	 item.StatusDetail = output.toString();
			     			// Don't save to database, as the overhead the server/DB chatter it would create is not worth it.
			     		  }
				        } catch (IOException e) {  
				             e.printStackTrace();  
				        }  
				        finally{  
				             try {  
				               is1.close();  
				             } catch (IOException e) {  
				                e.printStackTrace();  
				            }  
				          }  
				        }  
				     }.start();  
			                                
			   new Thread() {   
			      public void  run() {   
			          try {
			        	 BufferedReader br2 = new  BufferedReader(new  InputStreamReader(is2, "utf-8"));
			             String line2 = null ;   
			             while ((line2 = br2.readLine()) !=  null ) {   
			                  if (line2 != null){
			                	  outputErr.appendln(line2);
			                  }  
			             }
			           } catch (IOException e) {   
			                 e.printStackTrace();  
			           }   
			          finally{  
			             try {  
			                 is2.close();  
			             } catch (IOException e) {  
			                 e.printStackTrace();  
			             }  
			           }  
			        }   
			      }.start();    
			processCompletedSuccessfully =p.waitFor(ContentSettings.TimeoutMs, TimeUnit.MILLISECONDS);
			//p = Runtime.getRuntime().exec(AppSettings.getInstance().getFFmpegPath() + " " +ContentSettings.FFmpegArgs);
			 
			/*InputStream inputStream = p.getInputStream();
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				String line = null;
				while((line = br.readLine()) != null) {
					output.appendln(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
						
			if (!processCompletedSuccessfully)
				p.destroy();

			p.waitFor();

			///if (!processCompletedSuccessfully || ContentSettings.CancellationToken.IsCancellationRequested)
			if (!processCompletedSuccessfully || (ContentSettings.CancellationToken != null && ContentSettings.CancellationToken.getState() == CancelToken.State.Cancelled)){
				FileMisc.deleteFile(ContentSettings.FilePathDestination);
			}
		}catch (Exception ex){
			ex.printStackTrace();
			errorDataReceived(p);
			/*if (!ex.Data.Contains("args"))
			{
				ex.Data.Add("args", ContentSettings.FFmpegArgs);
			}

			EventLogs.EventLogController.RecordError(ex, AppSettings.getInstance(), ContentSettings.GalleryId, CMUtils.loadGallerySettings());*/
			try{  
				if (p != null) {
	                p.getErrorStream().close();  
	                p.getInputStream().close();  
	                p.getOutputStream().close();
				}
            }catch(Exception ee){}  
		}

		if (!processCompletedSuccessfully)
		{
			/*Exception ex = new BusinessException(MessageFormat.format("FFmpeg timed out while processing the video or audio file. Consider increasing the timeout value. It is currently set to {0} milliseconds.", ContentSettings.TimeoutMs));
			ex.Data.Add("FFmpeg args", ContentSettings.FFmpegArgs);
			ex.Data.Add("FFmpeg output", Output);
			ex.Data.Add("StackTrace", Environment.StackTrace);
			EventLogs.EventLogController.RecordError(ex, AppSettings.getInstance(), ContentSettings.GalleryId, CMUtils.LoadGallerySettings());*/
		}
	}

	/// <summary>
	/// Seed the output String builder with any data from a previous conversion of this
	/// content object and the basic settings of the conversion.
	/// </summary>
	private void initializeOutput()	{
		ContentQueueItem item = ContentConversionQueue.getInstance().getCurrentContentQueueItem();
		if ((item != null) && (item.ContentQueueId == ContentSettings.ContentQueueId))	{
			// Seed the log with the existing data; this will prevent us from losing the data
			// when we save the output to the media queue instance.
			output.append(item.StatusDetail);
		}

		ContentEncoderSettings mes = ContentSettings.EncoderSetting;
		if (mes != null){
			output.appendln(StringUtils.format("{0} => {1}; {2}", mes.getSourceFileExtension(), mes.getDestinationFileExtension(), mes.getEncoderArguments()));
		}

		output.appendln("Argument String:");
		output.appendln(ContentSettings.FFmpegArgs);
	}

	/// <summary>
	/// Handle the data received event. Collect the command line output and cancel if requested.
	/// </summary>
	/// <param name="sender">The sender.</param>
	/// <param name="e">The <see cref="System.Diagnostics.DataReceivedEventArgs"/> instance 
	/// containing the event data.</param>
	private void errorDataReceived(Process process) {
		if (process != null) {
			InputStream inputStream = process.getInputStream();
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
				String line = null;
				while((line = br.readLine()) != null) {
					output.appendln(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ContentQueueItem item = ContentConversionQueue.getInstance().getCurrentContentQueueItem();
		if ((item != null) && (item.ContentQueueId == ContentSettings.ContentQueueId)){
			item.StatusDetail = output.toString();
			// Don't save to database, as the overhead the server/DB chatter it would create is not worth it.
		}

		cancelIfRequested(process);
	}

	/// <summary>
	/// Kill the FFmpeg process if requested. This will happen when the user deletes a media
	/// object that is being processed or deletes the media queue item in the site admin area.
	/// </summary>
	/// <param name="process">The process running FFmpeg.</param>
	private void cancelIfRequested(Process process)	{
		CancelToken ct = ContentSettings.CancellationToken;
		if (ct != null && ct.getState() == CancelToken.State.Cancelled){
			if (process != null){
				process.destroy();
				try {
					process.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//ct.ThrowIfCancellationRequested();
		}
	}
}