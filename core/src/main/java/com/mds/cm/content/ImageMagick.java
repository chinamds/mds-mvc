package com.mds.cm.content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mds.util.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import com.google.common.collect.Lists;
import com.mds.core.exception.BusinessException;
import com.mds.sys.util.AppSettings;

/// <summary>
/// Contains functionality for interacting with ImageMagick, the open source utility. Specifically, MDS System uses it to generate
/// thumbnail images for .eps and .pdf files. See http://www.imagemagick.org for more information.
/// </summary>
/// <remarks>Requires Visual C++ 2008 Redistributable Package (x86). 64-bit OS requires both x86 and x64 versions. These are installed with
/// .NET 3.5 but I am not sure about .NET 4.0.
/// To read .eps and .pdf files, GhostScript must be installed on the server.</remarks>
public class ImageMagick {
	private StrBuilder _sbOutput;
	private StrBuilder _sbError;

	/// <summary>
	/// Generates an image for the media file at the specified <paramref name="sourceFilePath" /> and returns the output from the
	/// execution of the convert utility. The thumbnail is saved to <paramref name="destFilePath" />. The <paramref name="galleryId" />
	/// is used during error handling to associate the error, if any, with the gallery. Requires the application to be running at 
	/// Full Trust and GhostScript to be installed on the server. Returns <see cref="StringUtils.EMPTY" /> when the 
	/// application is running at less than Full Trust or when the convert utility is not present in the bin directory.
	/// </summary>
	/// <param name="sourceFilePath">The full file path to the source media file. Example: D:\media\myfile.eps</param>
	/// <param name="destFilePath">The full file path to store the image to. If a file with this name is already present,
	/// it is overwritten.</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns the text output from the execution of the convert.exe utility.</returns>
	public static String generateImage(String sourceFilePath, String destFilePath, long galleryId){
		String convertOutput = StringUtils.EMPTY;

		/*if ((AppSetting.Instance.AppTrustLevel != ApplicationTrustLevel.Full) || (StringUtils.isBlank(AppSetting.Instance.ImageMagickConvertPath)))
		{
			return convertOutput;
		}*/
		if ((StringUtils.isBlank(AppSettings.getInstance().getImageMagickConvertPath()))){
			return convertOutput;
		}

		// Create arguments. The [0] tells it to generate one image from the first page for PDF files (otherwise we get one image for every page)
		// Example: "D:\media\pic.eps[0]" "D:\media\pic.jpg"
		String args = StringUtils.format("\"\"{0}[0]\"\" \"\"{1}\"\"", sourceFilePath, destFilePath);

		ImageMagick imageMagick = new ImageMagick();
		convertOutput = imageMagick.executeConvert(args, galleryId);

		if (!StringUtils.isBlank(convertOutput)){
			// The utility returns an empty String when it is successful, so something went wrong. Log it.
			BusinessException ex = new BusinessException(StringUtils.format("ImageMagick (convert.exe) threw an error while trying to generate an image for the file {0}.", sourceFilePath));
			//ex.Data.Add("convert.exe output", convertOutput);

			//EventLogs.EventLogController.RecordError(ex, AppSetting.Instance, galleryId, Factory.LoadGallerySettings());
		}

		return convertOutput;
	}

	/// <summary>
	/// Execute the ImageMagick convert.exe utility with the given <paramref name="arguments" /> and return the text output generated by it.
	/// See http://www.imagemagick.org for documentation.
	/// </summary>
	/// <param name="arguments">The argument values to pass to the ImageMagick convert.exe utility. 
	/// Example: -density 300 "D:\media\myimage.eps[0]" "D:\media\zThumb_myimage.jpg"</param>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns the text output from the execution of the convert.exe utility.</returns>
	private String executeConvert(String arguments, long galleryId)	{
		List<String> cmds = Lists.newArrayList(AppSettings.getInstance().getImageMagickConvertPath());
		String[] args = StringUtils.splitPreserveAllTokens(arguments, "\"");
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
		
		ProcessBuilder info = new ProcessBuilder(cmds); 
		info.redirectErrorStream(true);
		//info.redirectOutput(Redirect.appendTo(log));

		Process p = null;
		try
		{
			_sbOutput = new StrBuilder();
			_sbError = new StrBuilder();
			
			p = info.start();
			
			 final InputStream is1 = p.getInputStream();   
			 final InputStream is2 = p.getErrorStream();  
			 new Thread() {  
			    public void run() {  
			        try {
			        	BufferedReader br1 = new BufferedReader(new InputStreamReader(is1, "utf-8"));
			            String line1 = null;  
			            while ((line1 = br1.readLine()) != null) {  
		                	  _sbOutput.appendln(line1); 
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
		                	  _sbError.appendln(line2); 
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
			 
			p.waitFor();
			
		}catch (Exception ex){
			/*if (!ex.Data.Contains("args"))
			{
				ex.Data.Add("args", arguments);
			}

			EventLogs.EventLogController.RecordError(ex, AppSetting.Instance, galleryId, Factory.LoadGallerySettings());*/
			try{ 
				if (p != null) {
	                p.getErrorStream().close();  
	                p.getInputStream().close();  
	                p.getOutputStream().close();
				}
            }catch(Exception ee){}  
		}

		// If we have error data, append it to the output String.
		if (_sbError.length() > 0){
			_sbOutput.append(_sbError.toString());
		}

		// Return the output from the utility.
		return _sbOutput.toString();
	}

	/*void Convert_ErrorDataReceived(object sender, DataReceivedEventArgs e)
	{
		if (!StringUtils.isBlank(e.Data))
		{
			_sbError.AppendLine(e.Data);
		}
	}

	void Convert__OutputDataReceived(object sender, DataReceivedEventArgs e)
	{
		if (!StringUtils.isBlank(e.Data))
		{
			_sbOutput.AppendLine(e.Data);
		}
	}*/
}
