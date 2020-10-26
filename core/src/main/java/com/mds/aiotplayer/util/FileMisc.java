/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.StringUtils;

import com.mds.aiotplayer.common.utils.Reflections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class FileMisc
{
	static public boolean fileExists(String strFilePath)
	{
		if (StringUtils.isBlank(strFilePath))
			return false;
		
		File file = new File(strFilePath);
		
		return file.exists();
	}
	
	static public String getExt(String strFilePath) // Ex: .bmp
	{
		String originalExtension = FilenameUtils.getExtension(strFilePath); 
		if (StringUtils.isNotBlank(originalExtension) && !StringUtils.startsWith(originalExtension, FilenameUtils.EXTENSION_SEPARATOR_STR))
			originalExtension = FilenameUtils.EXTENSION_SEPARATOR_STR + originalExtension;
		
		return originalExtension;
	}
	
	static public boolean hasExt(String strFilePath)
	{
		String originalExtension = FilenameUtils.getExtension(strFilePath);
		
		return !StringUtils.isBlank(originalExtension);
	}
	
	static public String changeExt(String strFilePath, String newExt)
	{
		String newFilePath = FilenameUtils.removeExtension(strFilePath);
		newFilePath += (newExt.startsWith(FilenameUtils.EXTENSION_SEPARATOR_STR) ? newExt : FilenameUtils.EXTENSION_SEPARATOR_STR + newExt);
		
		return newFilePath;
	}
	
	static public boolean deleteFile(String strFilePath){
		File file = new File(strFilePath);
		if (file.exists())
			return file.delete();
		
		return false;
	}
	
	static public boolean rename(String source, String dest){
		File sourcefile = new File(source);
		if (sourcefile.exists()){
			File destfile = new File(dest);
			try {
				Files.move(sourcefile.toPath(), destfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	static public File getParent(String strFilePath){
		File file = new File(strFilePath);
		//if (file.exists())
		return file.getParentFile();
		//return null;
	}
	
	static public boolean cleanDirectory(String directory){
		File file = new File(directory);
		
		try {
			FileUtils.cleanDirectory(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	static public boolean deleteExistsFile(String strFilePath){
		File file = new File(strFilePath);
		
		return file.delete();
	}
	
	static public void deleteDirectory(String directory) throws IOException{
		File file = new File(directory);
		FileUtils.deleteDirectory(file);
	}
	
	static public boolean deleteQuietly(String directory){
		File file = new File(directory);
		
		return FileUtils.deleteQuietly(file);
	}

	/********************************************************************/
	/*																	*/
	/* Function name : MakeSureDirectoryPathExists						*/
	/* Description   : This function creates all the directories in		*/
	/*				   the specified DirPath, beginning with the root.	*/
	/*				   This is a clone a Microsoft function with the	*/
	/*			       same name.										*/
	/*																	*/
	/********************************************************************/
	static public boolean makeSureDirectoryPathExists(String path)
	{
		if (path.isEmpty() || path.length() < 2)
			return false;

		File file = new File(path);
		if (!file.exists())
		{
			return file.mkdirs();
		}
	
		return true;
	}
	
	static public boolean moveFile(String src, String dst){
		try {
			FileUtils.moveFile(new File(src), new File(dst));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	static public void moveFileThrow(String src, String dst) throws IOException{
		FileUtils.moveFile(new File(src), new File(dst));
	}
	
	static public boolean copyFile(String src, String dst){
		try {
			FileUtils.copyFile(new File(src), new File(dst));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	static public void copy(String src, String dst) throws IOException {
	    InputStream in = new FileInputStream(new File(src));
	    OutputStream out = new FileOutputStream(new File(dst));

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
		
	static public void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	// ---------------------------------------------------------------- get resource

	/**
	 * Retrieves given resource as URL.
	 * @see #getResourceUrl(String, ClassLoader)
	 */
	public static URL getResourceUrl(String resourceName) {
		return getResourceUrl(resourceName, null);
	}

	/**
	 * Retrieves given resource as URL. Resource is always absolute and may
	 * starts with a slash character.
	 * <p>
	 * Resource will be loaded using class loaders in the following order:
	 * <ul>
	 * <li>{@link Thread#getContextClassLoader() Thread.currentThread().getContextClassLoader()}</li>
	 * <li>{@link Class#getClassLoader() ClassLoaderUtil.class.getClassLoader()}</li>
	 * <li>if <code>callingClass</code> is provided: {@link Class#getClassLoader() callingClass.getClassLoader()}</li>
	 * </ul>
	 */
	public static URL getResourceUrl(String resourceName, ClassLoader classLoader) {

		if (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		
		URL resourceUrl;

		// try #1 - using provided class loader
		if (classLoader != null) {
			resourceUrl = classLoader.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
		}

		// try #2 - using thread class loader
		ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
		if ((currentThreadClassLoader != null) && (currentThreadClassLoader != classLoader)) {
			resourceUrl = currentThreadClassLoader.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
		}

		// try #3 - using caller classloader, similar as Class.forName()
		Class callerClass = Reflections.getCallerClass(2);
		ClassLoader callerClassLoader = callerClass.getClassLoader();

		if ((callerClassLoader != classLoader) && (callerClassLoader != currentThreadClassLoader)) {
			resourceUrl = callerClassLoader.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
		}

		return null;
	}
	
	public static URL getClassResUrl(String resourceName, Class<?> clazz) {
				
		if (clazz != null) {
			URL resourceUrl;
			
			resourceUrl = clazz.getResource(resourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
			
			String classResourceName = resourceName;
			if (!resourceName.startsWith("/")) {			
				classResourceName = "/" + resourceName;
			}else {
				classResourceName = resourceName.substring(1);
			}
				
			resourceUrl = clazz.getResource(classResourceName);
			if (resourceUrl != null) {
				return resourceUrl;
			}
			
			return getResourceUrl(resourceName, clazz.getClassLoader());
		}
		
		return getResourceUrl(resourceName, null);		
	}

	// ---------------------------------------------------------------- get resource file

	/**
	 * Retrieves resource as file.
	 * @see #getResourceFile(String) 
	 */
	public static File getResourceFile(String resourceName) {
		return getResourceFile(resourceName, null);
	}

	/**
	 * Retrieves resource as file. Resource is retrieved as {@link #getResourceUrl(String, ClassLoader) URL},
	 * than it is converted to URI so it can be used by File constructor.
	 */
	public static File getResourceFile(String resourceName, ClassLoader classLoader) {
		try {
			URL resourceUrl = getResourceUrl(resourceName, classLoader);
			if (resourceUrl == null) {
				return null;
			}
			return new File(resourceUrl.toURI());
		} catch (URISyntaxException ignore) {
			return null;
		}
	}
	
	public static File getClassResFile(String resourceName, Class<?> clazz) {
		try {
			URL resourceUrl = getClassResUrl(resourceName, clazz);
			if (resourceUrl == null) {
				return null;
			}
			return new File(resourceUrl.toURI());
		} catch (URISyntaxException ignore) {
			return null;
		}
	}
	
	public static InputStream getClassResInputStream(String resourceName, Class<?> clazz) {
		return clazz.getResourceAsStream(resourceName);
	}
}
