package com.mds.aiotplayer.util;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.sys.util.MDSRoleCollection;
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.sys.util.SecurityGuard;
import com.mds.aiotplayer.cm.exception.CannotReadFromDirectoryException;
import com.mds.aiotplayer.cm.exception.CannotWriteToDirectoryException;
import com.mds.aiotplayer.cm.exception.DirectoryNotFoundException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.common.mapper.JsonMapper;
import com.mds.aiotplayer.common.utils.security.Encodes;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.security.Digests;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.core.ActionResult;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.core.ContentObjectType;
import com.mds.aiotplayer.core.MDSDataSchemaVersion;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.SecurityActionsOption;

/// <summary>
/// Provides general helper functions.
/// </summary>
public class HelperFunctions{
	//#region Private Fields
	private static final Logger log = LoggerFactory.getLogger(HelperFunctions.class);

	private static byte[] _encryptionKey; // Used in Encrypt/Decrypt methods
	private static final Object _fileLock = new Object(); // Used in ValidatePhysicalPathExistsAndIsReadWritable()
	
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;

	//#endregion

	//#region Constructors

	//#endregion

	//#region Extensions
	
	/// <summary>
	/// Returns the first matching set of integers in the <paramref name="input" />, returning <see cref="Int32.MinValue" />
	/// if no matches are found. Example: "Size: 704 px x 480 px" will return 704.
	/// </summary>
	/// <param name="input">The input.</param>
	/// <returns>An instance of <see cref="System.Int32" />.</returns>
	public static int parseInteger(String input){
		Pattern re = Pattern.compile("\\d+");
		Matcher m = re.matcher(input);
		
		if (m.find()){
			return StringUtils.toInteger(m.group(0));
		}

		return Integer.MIN_VALUE;
	}

	/// <summary>
	/// Separates the comma-separated String into a collection of individual String values.
	/// Leading and trailing spaces of each item are trimmed.
	/// </summary>
	/// <param name="value">The value. It is expected to be a comma-delimited String 
	/// (e.g. "dog, cat, house").</param>
	/// <returns>Returns a collection of Strings, or null when <paramref name="value" /> is null.</returns>
	public static List<String> toListFromCommaDelimited(String value){
		if (value == null)
			return null;

		String[] items = StringUtils.split(value, ",");
		List<String> rv = Lists.newArrayList();
		for(String item : items) {
			rv.add(item);
		}

		return rv;
	}

	/// <summary>
	/// Determines whether the specified String is formatted as a valid email address. This is determined by performing 
	/// two tests: (1) Comparing the String to a regular expression. (2) Using the validation built in to the .NET 
	/// constructor for the <see cref="System.Net.Mail.MailAddress"/> class. The method does not determine that the 
	/// email address actually exists.
	/// </summary>
	/// <param name="email">The String to validate as an email address.</param>
	/// <returns>Returns true when the email parameter conforms to the expected format of an email address; otherwise
	/// returns false.</returns>
	public static boolean isValidEmail(String email)
	{
		if (StringUtils.isBlank(email))
			return false;

		return (validateEmailByRegEx(email) && validateEmailByMailAddressCtor(email));
	}

	/// <summary>
	/// Ensure the specified String is a valid name for a directory within the specified path. Invalid
	/// characters are removed and the existing directory is checked to see if it already has a child
	/// directory with the requested name. If it does, the name is slightly altered to make it unique.
	/// The name is shortened if its length exceeds the <paramref name="defaultAlbumDirectoryNameLength" />.
	/// The clean, guaranteed safe directory name is returned. No directory is actually created in the
	/// file system.
	/// </summary>
	/// <param name="dirPath">The path, including the parent directory, in which the specified name
	/// should be checked for validity (e.g. C:\contentobjects\2006).</param>
	/// <param name="dirName">The directory name to be validated against the directory path. It should
	/// represent a proposed directory name and not an actual directory that already exists in the file
	/// system.</param>
	/// <param name="defaultAlbumDirectoryNameLength">Default length of the album directory name. You can
	/// specify the configuration setting DefaultAlbumDirectoryNameLength for this value.</param>
	/// <returns>
	/// Returns a String that can be safely used as a directory name within the path dirPath.
	/// </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="dirPath" /> or <paramref name="dirName" /> is null.</exception>
	/// <exception cref="ArgumentException">Thrown when <paramref name="dirPath" /> or <paramref name="dirName" /> is an empty String.</exception>
	public static String validateDirectoryName(String dirPath, String dirName, int defaultAlbumDirectoryNameLength)	{
		//#region Parameter validaton

		if (dirPath == null)
			throw new ArgumentNullException("dirPath");

		if (dirName == null)
			throw new ArgumentNullException("dirName");

		if (StringUtils.isBlank(dirPath) || StringUtils.isBlank(dirName)){
			throw new ArgumentException(I18nUtils.getMessage("helperFunctions.validateDirectoryName_Ex_Msg", dirPath, dirName));
		}

		// Test 1: Remove any characters that are not valid for directory names on the operating system.
		String newDirName = removeInvalidDirectoryNameCharacters(dirName);

		// If we end up with an empty String, resort to the default value.
		if (newDirName.length() == 0)
			newDirName = Constants.DefaultAlbumDirectoryName;

		// Test 2: Verify length is less than our max allowed length.
		int maxLength = defaultAlbumDirectoryNameLength;
		if (newDirName.length() > maxLength){
			newDirName = newDirName.substring(0, maxLength);
		}

		// Test 3: If the name ends in a period or space, delete it. This is to handle a 8.3 DOS filename compatibility issue where most/all 
		// trailing periods and spaces are stripped from file and folder names by Windows, a holdover from the transition from 8.3 
		// filenames where the dot is not stored but implied. If we did not do this, then Windows would store the directory without
		// the trailing period or space, but MDS System would think it was still there. See bug # #90 for more info.
		newDirName = StringUtils.stripEnd(newDirName,  ". ");

		//#endregion

		// Test 3: Check to make sure the parent directory (specified in dirPath) doesn't contain a directory with
		// the new directory name (newDirName). If it does, keep altering the name until we come up with a unique one.
		String newSuffix = StringUtils.EMPTY;
		int counter = 1;

		while (FileMisc.fileExists(FilenameUtils.concat(dirPath, newDirName))){
			// The parent directory already contains a child directory with our new name. We need to strip off the
			// previous suffix if we added one (e.g. (1), (2), etc), generate a new suffix, and try again.
			if (newSuffix.length() > 0){
				// Remove the previous suffix we appended. Don't remove anything if this is the first time going
				// through this loop (indicated by newSuffix.length() = 0).
				newDirName = StringUtils.remove(newDirName, newDirName.length() - newSuffix.length());
			}

			// Generate the new suffix to append to the filename (e.g. "(3)")
			newSuffix = StringUtils.format("({0})", counter);

			int newTotalLength = newDirName.length() + newSuffix.length();
			if (newTotalLength > maxLength)	{
				// Our new name is going to be longer than our allowed max length. Remove just enough
				// characters from newDirName so that the new length is equal to the max length.
				int numCharactersToRemove = newTotalLength - maxLength;
				newDirName = StringUtils.remove(newDirName, newDirName.length() - numCharactersToRemove);
			}

			// Append the suffix. Place at the end for a directory.
			newDirName += newSuffix;

			counter++;
		}

		return newDirName;
	}

	/// <summary>
	/// Ensure the specified String is a valid name for a file within the specified path. Invalid 
	/// characters are removed and the existing directory is checked to see if it already has a file
	/// with the requested name. If it does, the name is slightly altered to make it unique.
	/// The clean, guaranteed safe filename is returned. No file is actually created in the file system.
	/// </summary>
	/// <param name="dirPath">The path, including the parent directory, in which the specified name
	/// should be checked for validity (e.g. C:\contentobjects\2006\).</param>
	/// <param name="fileName">The filename to be validated against the directory path. It should 
	/// represent a proposed filename and not an actual file that already exists in the file system.</param>
	/// <returns>Returns a String that can be safely used as a filename within the path dirPath.</returns>
	public static String validateFileName(String dirPath, String fileName)	{
		//#region Parameter validation

		if (dirPath == null)
			throw new ArgumentNullException("dirPath");

		if (fileName == null)
			throw new ArgumentNullException("fileName");

		if (StringUtils.isBlank(dirPath) || StringUtils.isBlank(fileName)){
			throw new ArgumentException(I18nUtils.getMessage("helperFunctions.validateFileName_Ex_Msg1", dirPath, fileName));
		}

		if (!(FileMisc.hasExt(fileName))){
			throw new ArgumentException(I18nUtils.getMessage("helperFunctions.validateFileName_Ex_Msg2", fileName));
		}

		//#endregion

		// Test 1: Remove any characters that are not valid for directory names on the operating system.
		String newFilename = removeInvalidFileNameCharacters(fileName);

		// It is very unlikely that the above method stripped every character from the filename, because the filenames
		// should always come from existing files that are uploaded or added. But just in case it does, set a default.
		if (newFilename.length() == 0)
			newFilename = "DefaultFilename";

		// Test 2: Verify length is less than our max allowed length.
		final int maxLength = Constants.ContentObjectFileNameLength;
		if (newFilename.length() > maxLength){
			newFilename = newFilename.substring(0, maxLength);
		}

		// Test 3: Check to make sure the parent directory (specified in dirPath) doesn't contain a file with
		// the new filename (newFilename). If it does, keep altering the name until we come up with a unique one.
		String newSuffix = StringUtils.EMPTY;
		int counter = 1;

		while (FileMisc.fileExists(FilenameUtils.concat(dirPath, newFilename))){
			// The parent directory already contains a file with our new name. We need to strip off the
			// previous suffix if we added one (e.g. (1), (2), etc), generate a new suffix, and try again.
			if (newSuffix.length() > 0)	{
				// Remove the previous suffix we appended. Don't remove anything if this is the first time going
				// through this loop (indicated by newSuffix.length() = 0).
				String newFilenameWithoutExtension = FilenameUtils.getBaseName(newFilename); // e.g. if newFilename=puppy(1).jpg, get "puppy(1)"
				int indexOfSuffixToRemove = newFilenameWithoutExtension.length() - newSuffix.length();
				String newFilenameWithoutExtensionAndSuffix = StringUtils.remove(newFilenameWithoutExtension, indexOfSuffixToRemove); // e.g. "puppy"
				newFilename = newFilenameWithoutExtensionAndSuffix + FileMisc.getExt(newFilename); // e.g. puppy.jpg
			}

			// Generate the new suffix to append to the filename (e.g. "(3)")
			newSuffix = StringUtils.format("({0})", counter);

			int newTotalLength = newFilename.length() + newSuffix.length();
			if (newTotalLength > maxLength)	{
				// Our new name is going to be longer than our allowed max length. Remove just enough
				// characters from newFilename so that the new length is equal to the max length.
				int numCharactersToRemove = newTotalLength - maxLength;
				newFilename = StringUtils.remove(newFilename, newFilename.length() - numCharactersToRemove);
			}

			// Insert the suffix just before the ".".
			newFilename = StringUtils.insert(newFilename, newFilename.lastIndexOf("."), newSuffix);

			counter++;
		}

		return newFilename;
	}

	/// <summary>
	/// Removes all characters from the specified String that are invalid for a directory name
	/// for the operating system. This function uses Path.GetInvalidPathChars() so it may remove 
	/// different characters under different operating systems, depending on the characters returned
	/// from this .NET function.
	/// </summary>
	/// <param name="directoryName">A String representing a proposed directory name
	/// that should have all invalid characters removed.</param>
	/// <returns>Removes a clean version of the directoryName parameter that has all invalid
	/// characters removed.</returns>
	public static String removeInvalidDirectoryNameCharacters(String directoryName)	{
		// Set up our array of invalid characters. Path.GetInvalidPathChars() does not include the wildcard
		// characters *, ?, :, \, and /, so add them manually.
		/*char[] invalidChars = new char[(Path.GetInvalidPathChars().length() + 5)];
		Path.GetInvalidPathChars().CopyTo(invalidChars, 0);
		invalidChars[invalidChars.length() - 5] = '?';
		invalidChars[invalidChars.length() - 4] = '*';
		invalidChars[invalidChars.length() - 3] = ':';
		invalidChars[invalidChars.length() - 2] = '\\';
		invalidChars[invalidChars.length() - 1] = '/';
		
		// Strip out invalid characters that make the OS puke
		return Pattern.Replace(directoryName, "[" + Regex.Escape(new String(invalidChars)) + "]", StringUtils.EMPTY);*/
		String invalidCharRemoved = directoryName.replaceAll("[\\\\/:*?\"<>|]", StringUtils.EMPTY);
		
		return invalidCharRemoved;
	}

	/// <summary>
	/// Removes all characters from the specified String that are invalid for filenames
	/// for the operating system. This function uses Path.GetInvalidFileNameChars() so it may remove 
	/// different characters under different operating systems, depending on the characters returned
	/// from this .NET function.
	/// </summary>
	/// <param name="fileName">A String representing a proposed filename
	/// that should have all invalid characters removed.</param>
	/// <returns>Removes a clean version of the filename parameter that has all invalid
	/// characters removed.</returns>
	/// <remarks>This function also removes the ampersand (&amp;) because this character cannot be used in an URL (even if we try to encode it).
	/// </remarks>
	public static String removeInvalidFileNameCharacters(String fileName)
	{
		// Set up our array of invalid characters. Path.InvalidPathChars does not include the wildcard
		// characters *, ?, and also :, \, /, <, and >, so add them manually.
		/*char[] invalidChars = new char[(Path.GetInvalidFileNameChars().length() + 6)];
		Path.GetInvalidPathChars().CopyTo(invalidChars, 0);
		invalidChars[invalidChars.length() - 6] = '&';
		invalidChars[invalidChars.length() - 5] = '?';
		invalidChars[invalidChars.length() - 4] = '*';
		invalidChars[invalidChars.length() - 3] = ':';
		invalidChars[invalidChars.length() - 2] = '\\';
		invalidChars[invalidChars.length() - 1] = '/';

		// Strip out invalid characters that make the OS puke
		return Regex.Replace(fileName, "[" + Regex.Escape(new String(invalidChars)) + "]", StringUtils.EMPTY);*/
		String invalidCharRemoved = fileName.replaceAll("[\\\\/:*&?\"<>|]", StringUtils.EMPTY);
		
		return invalidCharRemoved;
	}

	/// <summary>
	/// Parse the specified String and return a valid <see cref="System.Drawing.Color" />. The color may be specified as a 
	/// Hex value (e.g. "#336699", "#369"), an RGB color value (e.g. "(100,100,100)"), or one of the
	/// <see cref="System.Drawing.KnownColor" /> enumeration values ("Crimson", "Maroon"). An <see cref="ArgumentOutOfRangeException" />
	/// is thrown if a color cannot be parsed from the parameter.
	/// </summary>
	/// <param name="colorValue">A String representing the desired color. The color may be specified as a 
	/// Hex value (e.g. "#336699", "#369"), an RGB color value (e.g. "(100,100,100)"), or one of the
	/// <see cref="System.Drawing.KnownColor" /> enumeration values ("Crimson", "Maroon").</param>
	/// <returns>Returns a <see cref="System.Drawing.Color" /> struct that matches the color specified in the parameter.</returns>
	/// <exception cref="System.ArgumentNullException">Thrown when <paramref name="colorValue" /> is null.</exception>
	/// <exception cref="System.ArgumentOutOfRangeException">Thrown when the <paramref name="colorValue" /> cannot be converted into a known color.</exception>
	public static Color getColor(String colorValue)	{
		if (colorValue == null)
			throw new ArgumentNullException("colorValue");

		// #336699; (100, 100, 100); WhiteSmoke
		final String hexPattern = "^\\#[0-9A-Fa-f]{3}$|^\\#[0-9A-Fa-f]{6}$";
		final String rgbPattern = "^\\(\\d{1,3},\\d{1,3},\\d{1,3}\\)$";
		final String namePattern = "^[A-Za-z]+$";

		colorValue = colorValue.replace(" ", StringUtils.EMPTY); // Remove all white space

		Color myColor;

		Pattern regExHex = Pattern.compile(hexPattern);
		Pattern regExRgb = Pattern.compile(rgbPattern);
		Pattern regExName = Pattern.compile(namePattern);

		if (regExHex.matcher(colorValue).matches())	{
			// Color is specified as Hex. Parse.
			// If specified in 4-digit shorthand (e.g. #369), expand to full 7 digits (e.g. #336699).
			if (colorValue.length() == 4){
				StringBuilder  sb = new StringBuilder (colorValue);
				sb.insert(1, colorValue.substring(1, 1));
				sb.insert(3, colorValue.substring(3, 1));
				sb.insert(5, colorValue.substring(5, 1));
				colorValue = sb.toString();
			}

			myColor = Color.decode(colorValue);
		}else if (regExRgb.matcher(colorValue).matches()){
			// Color is specified as RGB. Parse.
			String colorVal = colorValue;

			// Strip the opening and closing parentheses.
			colorVal = StringUtils.stripStart(colorVal, "(");
			colorVal = StringUtils.stripStart(colorVal, ")");

			// First verify each value is a number from 0-255. (The reg ex matched 0-999).
			String[] rgbStringValues = StringUtils.split(colorVal, ',' );

			// Convert to integers
			int[] rgbValues = new int[3];
			for (int i = 0; i < rgbStringValues.length; i++){
				rgbValues[i] = StringUtils.toInteger(rgbStringValues[i]);

				if ((rgbValues[i] < 0) || (rgbValues[i] > 255))
					throw new ArgumentOutOfRangeException(StringUtils.format("The color {0} does not represent a valid RGB color.", colorValue));
			}

			myColor = new Color(rgbValues[0], rgbValues[1], rgbValues[2]);
		}else if (regExName.matcher(colorValue).matches()){
			// Color is specified as a name. Parse.
			try {
			    Field field = Class.forName("java.awt.Color").getField(colorValue.toLowerCase());
			    myColor = (Color)field.get(null);
			} catch (Exception e) {
				myColor = null; // Not defined
			}
			
			if (myColor== null || ((myColor.getAlpha() == 0) && (myColor.getRed() == 0) && (myColor.getGreen() == 0) && (myColor.getBlue() == 0)))
				throw new ArgumentOutOfRangeException(StringUtils.format("The color {0} does not represent a color known to the .NET Framework.", colorValue));
		}else{
			throw new ArgumentOutOfRangeException(StringUtils.format("The color {0} does not represent a valid color.", colorValue));
		}

		return myColor;
	}
	
	/// <summary>
	/// Encrypt the specified string using the System.Security.Cryptography.TripleDESCryptoServiceProvider cryptographic
	/// service provider. The secret key used in the encryption is specified in the encryptionKey configuration setting.
	/// The encrypted string can be decrypted to its original string using the Decrypt function in this class.
	/// </summary>
	/// <param name="plainText">A plain text string to be encrypted. If the value is null or empty, the return value is
	/// equal to String.Empty.</param>
	/// <returns>Returns an encrypted version of the plainText parameter.</returns>
	public static String encrypt(String plainText) throws UnsupportedEncodingException	{
		if (StringUtils.isEmpty(plainText))
			return StringUtils.EMPTY;

		final byte[] plainTextBytes = plainText.getBytes("utf-8");
		byte[] result = Encodes.encrypt(plainTextBytes, getEncryptionKey());
		
		return Encodes.encodeBase64(result);
	}

	/// <summary>
	/// Decrypt the specified string using the System.Security.Cryptography.TripleDESCryptoServiceProvider cryptographic
	/// service provider. The secret key used in the decryption is specified in the encryptionKey configuration setting.
	/// </summary>
	/// <param name="encryptedText">A string to be decrypted. The encrypted string should have been encrypted using the
	/// Encrypt function in this class. If the value is null or empty, the return value is equal to String.Empty.</param>
	/// <returns>
	/// Returns the original, unencrypted string contained in the encryptedText parameter.
	/// </returns>
	/// <exception cref="System.FormatException">Thrown when the text cannot be decrypted.</exception>
	public static String decrypt(String encryptedText) throws Exception{
		if (StringUtils.isEmpty(encryptedText))
			return StringUtils.EMPTY;

		// Get the byte code of the string
		byte[] toEncryptArray = Encodes.decodeBase64(encryptedText);
		
		return Encodes.decrypt(toEncryptArray, getEncryptionKey());
	}
			

	/// <summary>
	/// Gets a value indicating whether a user can view the specified <paramref name="album" />.
	/// </summary>
	/// <returns><c>true</c> if the user can view the album; otherwise, <c>false</c>.</returns>
	public static boolean canUserViewAlbum(AlbumBo album, MDSRoleCollection roles, boolean isUserAuthenticated) throws InvalidAlbumException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		return SecurityGuard.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject, roles, album.getId(), album.getGalleryId()
				, isUserAuthenticated, album.getIsPrivate(), SecurityActionsOption.RequireOne, album.getIsVirtualAlbum());
	}
	
	/// <summary>
	/// Clears all in-memory representations of data.
	/// </summary>
	public static void clearAllCaches()	{
		CMUtils.clearGalleryControlSettingsCache();
		CMUtils.clearWatermarkCache();
		purgeCache();
	}
	
	/// <summary>
	/// Remove all items from cache. This includes content objects, albums, MDS System roles, application errors, gallery settings, and more.
	/// </summary>
	public static void purgeCache(){
		CacheUtils.remove(CacheItem.cm_albums);
		CacheUtils.remove(CacheItem.cm_contentobjects);
		CacheUtils.remove(CacheItem.MDSRoles);
		CacheUtils.remove(CacheItem.Users);
		CacheUtils.remove(CacheItem.UsersCurrentUserCanView);
		CacheUtils.remove(CacheItem.AppEvents);
		CacheUtils.remove(CacheItem.cm_profiles);
		CacheUtils.remove(CacheItem.cm_uitemplates);
		CacheUtils.remove(CacheItem.cm_contenttemplates);
		CacheUtils.remove(CacheItem.cm_mimetypes);
		CacheUtils.remove(CacheItem.pm_players);
		CacheUtils.remove(CacheItem.sys_menufunctionpermissions);
		CacheUtils.remove(CacheItem.sys_menufunctions);
		//CacheUtils.remove(CacheItem.sys_permissions);

		CMUtils.clearGalleryCache(); // Since galleries store a list of all albums, we must clear it out anytime an album is added, deleted, or moved.
	}

	/// <summary>
	/// Parse the albumPhysicalPath parameter to find the portion that refers to album folders below the root album, then
	/// append this portion to the alternatePhysicalPath parameter and return the computed String. If alternatePhysicalPath is
	/// null or empty, then return albumPhysicalPath. This is useful when mapping an album's physical location
	/// to the physical location within the thumbnail and/or optimized image cache directory. For example, if an album is located
	/// at C:\mypics\album1\album2, the content object root directory is at C:\mypics (specified by the contentObjectPath configuration
	/// setting), and the thumbnail directory is specified to be C:\thumbnailCache (the thumbnailPath configuration setting),
	/// then return C:\thumbnailCache\album1\album2.
	/// </summary>
	/// <param name="albumPhysicalPath">The full physical path to an existing album. An exception is thrown if the directory is not
	/// a child directory of the root content object directory (GallerySetting.FullContentObjectPath). Ex: C:\mypics\album1\album2</param>
	/// <param name="alternatePhysicalPath">The full physical path to a directory on the hard drive. This is typically (always?)
	/// the path to either the thumbnail or optimized cache (refer to thumbnailPath and optimized configuration setting). Ex: C:\thumbnailCache
	/// This parameter is optional. If not specified, the method returns the albumPhysicalPath parameter without modification.</param>
	/// <param name="fullContentObjectPath">The full physical path to the directory containing the content objects in the current
	/// gallery. You can use CMUtils.LoadGallerySetting(galleryId).FullContentObjectPath to populate this parameter.
	/// Example: "C:\inetpub\wwwroot\MDS\contentobjects"</param>
	/// <returns>
	/// Returns the alternatePhysicalPath parameter with the album directory path appended. Ex: C:\thumbnailCache\album1\album2
	/// If the alternatePhysicalPath parameter is not specified, the method returns the albumPhysicalPath parameter without modification.
	/// </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="albumPhysicalPath" /> or <paramref name="fullContentObjectPath" /> is null.</exception>
	public static String mapAlbumDirectoryStructureToAlternateDirectory(String albumPhysicalPath, String alternatePhysicalPath, String fullContentObjectPath){
		if (albumPhysicalPath == null)
			throw new ArgumentNullException("albumPhysicalPath");

		if (fullContentObjectPath == null)
			throw new ArgumentNullException("fullContentObjectPath");

		if (StringUtils.isBlank(alternatePhysicalPath))	{
			return albumPhysicalPath;
		}

		if (!albumPhysicalPath.startsWith(fullContentObjectPath))
			throw new BusinessException(StringUtils.format("Expected this.Parent.FullPhysicalPathOnDisk (\"{0}\") to start with \"{1}\", but it did not.", albumPhysicalPath, fullContentObjectPath));

		String relativePath = StringUtils.strip(StringUtils.removeStart(albumPhysicalPath, fullContentObjectPath), File.separator);

		return FilenameUtils.concat(alternatePhysicalPath, relativePath);
	}

	/// <summary>
	/// Generate a full physical path, such as "C:\inetpub\wwwroot\MDS\myimages", based on the specified parameters.
	/// If relativeOrFullPath is a relative path, such as "\myimages\", append it to the physicalAppPath and return. If 
	/// relativeOrFullPath is a full path, such as "C:\inetpub\wwwroot\MDS\myimages", ignore the physicalAppPath
	/// and return the full path. In either case, this procedure guarantees that all directory separator characters are valid
	/// for the current operating system and that there is no directory separator character after the final (innermost) directory.
	/// Does not verify to ensure the directory exists or that it is writeable.
	/// </summary>
	/// <param name="physicalAppPath">The physical path of the currently executing application.</param>
	/// <param name="relativeOrFullPath">The relative or full file path. Relative paths should be relative to the root of the
	/// running application so that, when it is combined with physicalAppPath parameter, it creates a valid path.
	/// Examples: "C:\inetpub\wwwroot\MDS\myimages\", "C:/inetpub/wwwroot/MDS/myimages",
	/// "\myimages\", "\myimages", "myimages\", "myimages",	"/myimages/", "/myimages"</param>
	/// <returns>Returns a full physical path, without the trailing slash. For example: 
	/// "C:\inetpub\wwwroot\MDS\myimages"</returns>
	/// <exception cref="ArgumentOutOfRangeException">Thrown when <paramref name="physicalAppPath" /> or <paramref name="relativeOrFullPath" /> is 
	/// null or an empty String.</exception>
	public static String calculateFullPath(String physicalAppPath, String relativeOrFullPath){
		//#region Validation

		if (StringUtils.isBlank(relativeOrFullPath))
			throw new ArgumentOutOfRangeException("relativeOrFullPath");

		if (StringUtils.isBlank(physicalAppPath))
			throw new ArgumentOutOfRangeException("physicalAppPath");

		//#endregion

		String fullPhysicalPath;
		String modifiedContentObjectPath;
		// Delete any leading or trailing slashes, and ensure all slashes are the backward ones (\).  If the user has entered a UNC drive we only remove
		// the trailing slashes and do not append the application directory
		if (isUncPath(relativeOrFullPath)) {//User has entered a UNC directory
			modifiedContentObjectPath = StringUtils.stripEnd(relativeOrFullPath,  "/" + File.separator).replace("/", File.separator);
			fullPhysicalPath = modifiedContentObjectPath;
		}else{
			modifiedContentObjectPath = StringUtils.stripEnd(relativeOrFullPath, "/" + File.separator);
			modifiedContentObjectPath = StringUtils.stripEnd(modifiedContentObjectPath, "/" + File.separator).replace("/", File.separator);

			// If, after the trimming, we have a volume without a directory (e.g. "C:"), then add a trailing slash (e.g. "C:\").
			// We do this because subsequent code might use our return value as a parameter in Path.Combine, and Path.Combine
			// is not smart enough to add a slash when combining a volume and a path (e.g. "C:" and "mypics").
			/*if (!modifiedContentObjectPath.endsWith(File.separator))
				modifiedContentObjectPath += File.separator;*/

			if (isRelativeFilePath(modifiedContentObjectPath)){
				fullPhysicalPath = FilenameUtils.concat(physicalAppPath, modifiedContentObjectPath);
			}else{
				fullPhysicalPath = modifiedContentObjectPath;
			}
		}

		return fullPhysicalPath;
	}

	private static boolean isUncPath(String relativeOrFullPath)	{
		return relativeOrFullPath.startsWith("\\\\");
	}

	/// <summary>
	/// Validates that the specified path exists and that it is writeable. If the path does not exist, we attempt to 
	/// create it. Once we know it exists, we write a tiny file to it and then delete it. If that passes, we know we
	/// have sufficient read/write access for MDS System to read/write files to the directory.
	/// </summary>
	/// <param name="fullPhysicalPath">The full physical path to test (e.g. "C:\inetpub\wwwroot\MDS\myimages")</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.CannotWriteToDirectoryException">
	/// Thrown when MDS System is unable to write to, or delete from, the path <paramref name="fullPhysicalPath"/>.</exception>
	public static void validatePhysicalPathExistsAndIsReadWritable(String fullPhysicalPath) throws CannotWriteToDirectoryException	{
		File directory = new File(fullPhysicalPath);
		// Create directory if it does not exist.
		try{
			if (!directory.exists()){
				if (!directory.mkdirs()) {
					throw new CannotWriteToDirectoryException(fullPhysicalPath);
				}
			}
		}catch (SecurityException ex){
			throw new CannotWriteToDirectoryException(fullPhysicalPath, ex);
		}
		
		// Verify the directory is writeable.
		String testFilePath = StringUtils.EMPTY;
		FileOutputStream fileOuputStream = null;
		try{
			synchronized (_fileLock)
			{
				String uniqueFileName = validateFileName(fullPhysicalPath, "_test_file_okay_to_delete.config");
				testFilePath = FilenameUtils.concat(fullPhysicalPath, uniqueFileName);
				File logFile = new File(testFilePath);

				//writer = new BufferedWriter(new FileWriter(logFile));
				fileOuputStream = new FileOutputStream(logFile);
				fileOuputStream.write(42);
				fileOuputStream.close();
				fileOuputStream = null;
	            
	            logFile.delete();
			}
		}catch (Exception ex){
			try	{
				if (fileOuputStream != null)
					fileOuputStream.close();
				if (FileMisc.fileExists(testFilePath)){
					FileMisc.deleteFile(testFilePath); // Clean up by deleting the file we created
				}
			}catch(Exception ex1) { }

			throw new CannotWriteToDirectoryException(fullPhysicalPath, ex);
		}
	}

	/// <summary>
	/// Validates that the specified path exists and that it is writeable. If the path does not exist, we attempt to 
	/// create it. Once we know it exists, we write a tiny file to it and then delete it. If that passes, we know we
	/// have sufficient read/write access for MDS System to read/write files to the directory.
	/// </summary>
	/// <param name="fullPhysicalPath">The full physical path to test (e.g. "C:\inetpub\wwwroot\MDS\myimages")</param>
	/// <exception cref="MDS.EventLogs.CustomExceptions.CannotWriteToDirectoryException">
	/// Thrown when MDS System is unable to read from the path <paramref name="fullPhysicalPath"/>.</exception>
	public static void validatePhysicalPathExistsAndIsReadable(String fullPhysicalPath) throws DirectoryNotFoundException, CannotReadFromDirectoryException	{
		// Verify the directory exists.
		File directory = new File(fullPhysicalPath);
		if (!directory.exists())
			throw new DirectoryNotFoundException(I18nUtils.getMessage("exception.directoryNotFound_Ex_Msg", fullPhysicalPath));

		// Verify the directory is readable.
		try{
			String[] files = directory.list();
		}catch (Exception ex){
			throw new CannotReadFromDirectoryException(fullPhysicalPath, ex);
		}
	}

	/// <summary>
	/// Determine whether the specified file can be added to MDS System. This is determined by first looking at the
	/// <see cref="GallerySettings.AllowUnspecifiedMimeTypes" /> configuration setting, and returns true if this setting is 
	/// true. If false, the method looks up the MIME type for this file from the configuration file and returns the value 
	/// of the allowAddToGallery attribute. If there isn't a MIME type entry for this file and 
	/// <see cref="GallerySettings.AllowUnspecifiedMimeTypes" /> = <c>false</c>, this method returns false.
	/// </summary>
	/// <param name="fileName">A name of a file that includes the extension.</param>
	/// <param name="galleryId">The gallery ID. This value is used to look up the configuration setting 
	/// <see cref="GallerySettings.AllowUnspecifiedMimeTypes" /></param>
	/// <returns>
	/// Returns true if the file can be added to MDS System; otherwise returns false.
	/// </returns>
	public static boolean isFileAuthorizedForAddingToGallery(String fileName, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		if (CMUtils.loadGallerySetting(galleryId).getAllowUnspecifiedMimeTypes())
			return true;

		MimeTypeBo mimeType = CMUtils.loadMimeType(galleryId, fileName);
		if ((mimeType != null) && mimeType.getAllowAddToGallery())
			return true;
		else
			return false;
	}

	/// <summary>
	/// Update the audit fields of the gallery object. This should be invoked before saving any gallery object within this
	/// class library. Class libraries that use this library are responsible for updating the audit fields themselves.
	/// The audit fields are: CreatedByUsername, DateAdded, LastModifiedByUsername, DateLastModified
	/// </summary>
	/// <param name="contentObject">The gallery object whose audit fields are to be updated.</param>
	/// <param name="userName">The user name of the currently logged on user.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="contentObject" /> or <paramref name="userName" /> is null.</exception>
	public static void updateAuditFields(ContentObjectBo contentObject, String userName){
		if (contentObject == null)
			throw new ArgumentNullException("contentObject");

		if (userName == null)
			throw new ArgumentNullException("userName");

		Date currentTimestamp = DateUtils.Now();

		if (contentObject.getIsNew()){
			contentObject.setCreatedByUserName(userName);
			contentObject.setDateAdded(currentTimestamp);
		}

		contentObject.setLastModifiedByUserName(userName);
		contentObject.setDateLastModified(currentTimestamp);
	}

	///// <summary>
	///// Create and return a deep copy of the specified object. The copy is created by serializing the object to memory and
	///// then deserializing it into a new object. Returns null if the specified parameter is null.
	///// </summary>
	///// <typeparam name="T">The type of object for which to make a deep copy.</typeparam>
	///// <param name="obj">The object for which to make a deep copy. May be null.</param>
	///// <returns>Returns a deep copy of the specified parameter, or null if the parameter is null.</returns>
	///// <remarks>This method requires Full Trust.</remarks>
	//public static T CloneObject<T>(T obj)
	//{
	//  // Create a memory stream and a formatter.
	//  using (System.IO.MemoryStream ms = new System.IO.MemoryStream(1000))
	//  {
	//    BinaryFormatter bf = new BinaryFormatter(null, new StreamingContext(StreamingContextStates.Clone));

	//    // Serialize the object into the stream.
	//    bf.Serialize(ms, obj);

	//    // Position stream pointer back to first byte.
	//    ms.Seek(0, System.IO.SeekOrigin.Begin);

	//    // Deserialize into another object.
	//    return (T) bf.Deserialize(ms);
	//  }
	//}

	/// <summary>
	/// Returns the current version of MDS System.
	/// </summary>
	/// <returns>An instance of <see cref="MDSDataSchemaVersion" /> representing the version (e.g. "1.0.0").</returns>
	public static MDSDataSchemaVersion getMDSSystemVersion(){
		return MDSDataSchemaVersion.V1_0_0;//MDSDB.DataSchemaVersion;
	}

	/// <summary>
	/// Determines whether <paramref name="modifiedContentObjectPath" /> is a relative file path or an absolute one. It is
	/// considered a relative path if <see cref="Path.GetPathRoot" /> returns a null or empty String. 
	/// Examples: "App_Data\MDS_Data.sdf" returns true; "C:\data\MDS_Data.sdf" returns false.
	/// </summary>
	/// <param name="modifiedContentObjectPath">The modified content object path.</param>
	/// <returns>
	/// 	<c>true</c> if <paramref name="modifiedContentObjectPath" /> is a relative file path; otherwise, <c>false</c>.
	/// </returns>
	public static boolean isRelativeFilePath(String modifiedContentObjectPath)	{
		return !(new File(modifiedContentObjectPath).isAbsolute()); //StringUtils.isBlank(FilenameUtils.getBaseName(filename).GetPathRoot(modifiedContentObjectPath));
	}

	//#endregion

	//#region Private Static Properties

	private static byte[] getEncryptionKey() {
		if (_encryptionKey == null)	{
			try {
				_encryptionKey = AppSettings.getInstance().getEncryptionKey().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return _encryptionKey;
	}
	
	public static String entryptPassword(String plainPassword) {
		if (StringUtils.isBlank(plainPassword))
			return plainPassword;
		
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}
	
	public static String generatePassword(String userName)	{
		final int encryptionKeyLength = 8;
		final int numberOfNonAlphaNumericCharactersInEncryptionKey = 3;
		
		RandomStringGenerator randomStringGenerator =
		        new RandomStringGenerator.Builder()
		                .withinRange('0', '~')
		                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
		                .build();
		String encryptionKey = randomStringGenerator.generate(encryptionKeyLength);
		
		// An ampersand (&) is invalid, since it is used as an escape character in XML files. Replace any instances with an 'X'.
		return encryptionKey.replace("&", "X");
	}
	
	public static boolean validatePassword(String password) {
		if (StringUtils.isBlank(password))
			return false;
		
		byte[] hashPassword = null;
		byte[] salt = null;
		try {
			salt = Encodes.decodeHex(password.substring(0,16));
			hashPassword = Encodes.decodeHex(password.substring(16));
		}catch(Exception ex) {
			return false;
		}
		
		return (hashPassword != null && salt != null);
	}
	
	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}
	
	public static URI addQueryStringParameter(URI uri, String queryStringParameterNameValue) throws URISyntaxException{
		return new URI(addQueryStringParameter(uri.toString(), queryStringParameterNameValue));
	}

	/// <summary>
	/// Append the String to the url as a query String parameter. If the <paramref name="url" /> already contains the
	/// specified query String parameter, it is replaced with the new one.
	/// Example:
	/// Url = "www.aiotplayer.com/index.aspx?aid=5&amp;msg=3"
	/// QueryStringParameterNameValue = "moid=27"
	/// Return value: www.MDS.com/index.aspx?aid=5&amp;msg=3&amp;moid=27
	/// </summary>
	/// <param name="url">The Url to which the query String parameter should be added
	/// (e.g. www.aiotplayer.com/index.aspx?aid=5&amp;msg=3).</param>
	/// <param name="queryStringParameterNameValue">The query String parameter and value to add to the Url
	/// (e.g. "moid=27").</param>
	/// <returns>Returns a new Url containing the specified query String parameter.</returns>
	public static String addQueryStringParameter(String url, String queryStringParameterNameValue){
		if (StringUtils.isBlank(queryStringParameterNameValue))
			return url;

		String parmName = queryStringParameterNameValue.substring(0, queryStringParameterNameValue.indexOf("="));

		url = removeQueryStringParameter(url, parmName);

		String rv = url;

		if (url.indexOf("?") < 0){
			rv += "?" + queryStringParameterNameValue;
		}else{
			rv += "&" + queryStringParameterNameValue;
		}
		
		return rv;
	}

	/// <overloads>
	/// Remove a query String parameter from an URL.
	/// </overloads>
	/// <summary>
	/// Remove all query String parameters from the url.
	/// Example:
	/// Url = "www.aiotplayer.com/index.aspx?aid=5&amp;msg=3&amp;moid=27"
	/// Return value: www.aiotplayer.com/index.aspx
	/// </summary>
	/// <param name="url">The Url containing the query String parameters to remove
	/// (e.g. www.aiotplayer.com/index.aspx?aid=5&amp;msg=3&amp;moid=27).</param>
	/// <returns>Returns a new Url with all query String parameters removed.</returns>
	public static String removeQueryStringParameter(String url)	{
		return removeQueryStringParameter(url, StringUtils.EMPTY);
	}

	/// <summary>
	/// Remove the specified query String parameter from the url. Specify <see cref="StringUtils.EMPTY" /> for the
	/// <paramref name="queryStringParameterName" /> parameter to remove the entire set of parameters.
	/// Example:
	/// Url = "www.aiotplayer.com/index.aspx?aid=5&amp;msg=3&amp;moid=27"
	/// QueryStringParameterName = "msg"
	/// Return value: www.aiotplayer.com/index.aspx?aid=5&amp;moid=27
	/// </summary>
	/// <param name="url">The Url containing the query String parameter to remove
	/// (e.g. www.aiotplayer.com/index.aspx?aid=5&amp;msg=3&amp;moid=27).</param>
	/// <param name="queryStringParameterName">The query String parameter name to remove from the Url
	/// (e.g. "msg"). Specify <see cref="StringUtils.EMPTY" /> to remove the entire set of parameters.</param>
	/// <returns>Returns a new Url with the specified query String parameter removed.</returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="url" /> is null.</exception>
	public static String removeQueryStringParameter(String url, String queryStringParameterName){
		if (url == null)
			throw new ArgumentNullException("url");

		String newUrl;

		// Get the location of the question mark so we can separate the base url from the query String
		int separator = url.indexOf("?");
		if (separator < 0){
			// No query String exists on the url. Simply return the original url.
			newUrl = url;
		}else{
			// We have a query String to remove. Separate the base url from the query String, and process the query String.

			// Get the base url (e.g. "www.aiotplayer.com/index.aspx")
			newUrl = url.substring(0, separator);

			if (StringUtils.isBlank(queryStringParameterName)){
				return newUrl;
			}

			newUrl += "?";

			String queryString = url.substring(separator + 1);
			if (queryString.length() > 0){
				// Url has a query String. Split each name/value pair into a String array, and rebuild the
				// query String, leaving out the parm passed to the function.
				String[] queryItems = StringUtils.split(queryString, '&');

				for (int i = 0; i < queryItems.length; i++)	{
					if (!queryItems[i].startsWith(queryStringParameterName)){
						// Query parm doesn't match, so include it as we rebuilt the new query String
						newUrl += queryItems[i].concat("&");
					}
				}
			}
			// Trim any trailing '&' or '?'.
			newUrl = StringUtils.stripEnd(newUrl,  "&?");
		}

		return newUrl;
	}
	
	/// <summary>
	/// Returns a value indicating whether the specified query String parameter name is part of the query String. 
	/// </summary>
	/// <param name="parameterName">The name of the query String parameter to check for.</param>
	/// <returns>Returns true if the specified query String parameter value is part of the query String; otherwise 
	/// returns false. </returns>
	public static boolean isQueryStringParameterPresent(HttpServletRequest request, String parameterName){
		return (request.getParameter(parameterName) != null);
	}

	/// <summary>
	/// Returns a value indicating whether the specified query String parameter name is part of the query String
	/// of the <paramref name="uri"/>. 
	/// </summary>
	/// <param name="uri">The URI to check for the present of the <paramref name="parameterName">query String parameter name</paramref>.</param>
	/// <param name="parameterName">Name of the query String parameter.</param>
	/// <returns>Returns true if the specified query String parameter value is part of the query String; otherwise 
	/// returns false. </returns>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="uri" /> is null.</exception>
	public static boolean isQueryStringParameterPresent(URI uri, String parameterName)	{
		if (uri == null)
			throw new ArgumentNullException("uri");

		if (StringUtils.isBlank(parameterName))
			return false;

		return (uri.getQuery().contains("?" + parameterName + "=") || uri.getQuery().contains("&" + parameterName + "="));
	}

	/// <overloads>Remove all HTML tags from the specified String.</overloads>
	/// <summary>
	/// Remove all HTML tags from the specified String.
	/// </summary>
	/// <param name="html">The String containing HTML tags to remove.</param>
	/// <returns>Returns a String with all HTML tags removed.</returns>
	public static String removeHtmlTags(String html){
		return removeHtmlTags(html, false);
	}

	/// <summary>
	/// Remove all HTML tags from the specified String. If <paramref name="escapeQuotes"/> is true, then all 
	/// apostrophes and quotation marks are replaced with &quot; and &apos; so that the String can be specified in HTML 
	/// attributes such as title tags. If the escapeQuotes parameter is not specified, no replacement is performed.
	/// </summary>
	/// <param name="html">The String containing HTML tags to remove.</param>
	/// <param name="escapeQuotes">When true, all apostrophes and quotation marks are replaced with &quot; and &apos;.</param>
	/// <returns>Returns a String with all HTML tags removed.</returns>
	public static String removeHtmlTags(String html, boolean escapeQuotes){
		return HtmlValidator.removeHtml(html, escapeQuotes);
	}

	/// <summary>
	/// Removes potentially dangerous HTML and Javascript in <paramref name="html"/>.
	/// When the current user is a gallery or site admin, no validation is performed and the 
	/// <paramref name="html" /> is returned without any processing. If the configuration
	/// setting <see cref="IGallerySettings.AllowUserEnteredHtml" /> is true, then the input is cleaned so that all 
	/// HTML tags that are not in a predefined list are HTML-encoded and invalid HTML attributes are deleted. If 
	/// <see cref="IGallerySettings.AllowUserEnteredHtml" /> is false, then all HTML tags are deleted. If the setting 
	/// <see cref="IGallerySettings.AllowUserEnteredJavascript" /> is true, then script tags and the text "javascript:"
	/// is allowed. Note that if script is not in the list of valid HTML tags defined in <see cref="IGallerySettings.AllowedHtmlTags" />,
	/// it will be deleted even when <see cref="IGallerySettings.AllowUserEnteredJavascript" /> is true. When the setting 
	/// is false, all script tags and instances of the text "javascript:" are deleted.
	/// </summary>
	/// <param name="html">The String containing the HTML tags.</param>
	/// <param name="galleryId">The gallery ID. This is used to look up the appropriate configuration values for the gallery.</param>
	/// <returns>
	/// Returns a String with potentially dangerous HTML tags deleted.
	/// </returns>
	/// <remarks>TODO: Refactor this so that the Clean method knows whether the user is a gallery admin, rendering this
	/// function unnecessary. When this is done, update <see cref="GalleryObject.MetadataRegExEvaluator" /> so that all meta items are
	/// passed to the Clean method.</remarks>
	public static String cleanHtmlTags(String html, long galleryId) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException{
		if (UserUtils.isCurrentUserGalleryAdministrator(galleryId))
			return html;
		else
			return HtmlValidator.clean(html, galleryId);
	}

	/// <summary>
	/// Returns the current version of MDS System.
	/// </summary>
	/// <returns>Returns a String representing the version (e.g. "1.0.0").</returns>
	public static String GetMDSSystemVersion(){
		String appVersion = StringUtils.EMPTY;
		/*object version = HttpContext.Current.Application["MDSSystemVersion"];
		if (version != null)
		{
			// Version was found in Application cache. Return.
			appVersion = version.ToString();
		}
		else
		{
			// Version was not found in application cache.
			appVersion = MDSDataSchemaVersionEnumHelper.ConvertMDSDataSchemaVersionToString(HelperFunctions.GetMDSSystemVersion());

			HttpContext.Current.Application["MDSSystemVersion"] = appVersion;
		}*/

		return appVersion;
	}

	/// <summary>
	/// Truncate the specified String to the desired length. Any HTML tags that exist in the beginning portion
	/// of the String are preserved as long as no HTML tags exist in the part that is truncated.
	/// </summary>
	/// <param name="text">The String to be truncated. It may contain HTML tags.</param>
	/// <param name="maxLength">The maximum length of the String to be returned. If HTML tags are returned,
	/// their length is not counted - only the length of the "visible" text is counted.</param>
	/// <returns>Returns a String whose length - not counting HTML tags - does not exceed the specified length.</returns>
	public static String truncateTextForWeb(String text, int maxLength)	{
		// Example 1: Because no HTML tags are present in the truncated portion of the String, the HTML at the
		// beginning is preserved. (We know we won't be splitting up HTML tags, so we don't mind including the HTML.)
		// text = "Meet my <a href='http://www.cnn.com'>friend</a>. He works at the YMCA."
		// maxLength = 20
		// returns: "Meet my <a href='http://www.cnn.com'>friend</a>. He w"
		//
		// Example 2: The truncated portion has <b> tags, so all HTML is stripped. (This function isn't smart
		// enough to know whether it might be truncating in the middle of a tag, so it takes the safe route.)
		// text = "Meet my <a href='http://www.cnn.com'>friend</a>. He works at the <b>YMCA<b>."
		// maxLength = 20
		// returns: "Meet my friend. He w"
		if (text == null)
			return StringUtils.EMPTY;

		if (text.length() < maxLength)
			return text;

		// Remove all HTML tags from entire String.
		String cleanText = removeHtmlTags(text);

		// If the clean text length is less than our maximum, return the raw text.
		if (cleanText.length() <= maxLength)
			return text;

		// Get the text that will be removed.
		String cleanTruncatedPortion = cleanText.substring(maxLength);

		// If the clean truncated text doesn't match the end of the raw text, the raw text must have HTML tags.
		boolean truncatedPortionHasHtml = (!(StringUtils.endsWithIgnoreCase(text, cleanTruncatedPortion)));

		String truncatedText;
		if (truncatedPortionHasHtml){
			// Since the truncated portion has HTML tags, and we don't want to risk returning malformed HTML,
			// return text without ANY HTML.
			truncatedText = cleanText.substring(0, maxLength);
		}else{
			// Since the truncated portion does not have HTML tags, we can safely return the first part of the
			// String, even if it has HTML tags.
			truncatedText = text.substring(0, text.length() - cleanTruncatedPortion.length());
		}
		
		return truncatedText;
	}

	/// <summary>
	/// HtmlEncodes a String using System.Web.HttpUtility.HtmlEncode().
	/// </summary>
	/// <param name="html">The text to HTML encode.</param>
	/// <returns>Returns <paramref name="html"/> as an HTML-encoded String.</returns>
	public static String htmlEncode(String html){
		return StringEscapeUtils.escapeHtml4(html); //.e.HtmlEncode(html);
	}

	/// <summary>
	/// HtmlDecodes a String using System.Web.HttpUtility.HtmlDecode().
	/// </summary>
	/// <param name="html">The text to HTML decode.</param>
	/// <returns>Returns <paramref name="html"/> as an HTML-decoded String.</returns>
	public static String htmlDecode(String html){
		return StringEscapeUtils.unescapeHtml4(html);
	}

	/// <overloads>UrlEncodes a String using System.Uri.EscapeDataString().</overloads>
	/// <summary>
	/// UrlEncodes a String using System.Uri.EscapeDataString().
	/// </summary>
	/// <param name="text">The text to URL encode.</param>
	/// <returns>Returns <paramref name="text"/> as an URL-encoded String.</returns>
	public static String urlEncode(String text)	{
		if (StringUtils.isBlank(text)){
			return text;
		}

		return URLEncoder.encode(text);
	}

	/// <summary>
	/// Encodes the <paramref name="text" /> so that it can be assigned to a javascript variable.
	/// </summary>
	/// <param name="text">The text to encode.</param>
	/// <returns>Returns <paramref name="text" /> as an encoded String.</returns>
	public static String jsEncode(String text){
		if (StringUtils.isBlank(text)){
			return text;
		}

		return text.replace("\r\n", "<br>").replace("\\", "\\\\").replace("'", "\\'").replace("\"\"", "\\\"\\\"");
	}

	/// <summary>
	/// UrlEncodes a String using System.Uri.EscapeDataString(), excluding the character specified in <paramref name="charNotToEncode"/>.
	/// This overload is useful for encoding URLs or file paths where the forward or backward slash is not to be encoded.
	/// </summary>
	/// <param name="text">The text to URL encode</param>
	/// <param name="charNotToEncode">The character that, if present in <paramref name="text"/>, is not encoded.</param>
	/// <returns>Returns <paramref name="text"/> as an URL-encoded String.</returns>
	public static String urlEncode(String text, char charNotToEncode){
		if (StringUtils.isBlank(text)){
			return text;
		}

		String[] tokens = StringUtils.split(text, charNotToEncode);
		for (int i = 0; i < tokens.length; i++)	{
			tokens[i] = urlEncode(tokens[i]);
		}

		return String.join(String.valueOf(charNotToEncode), tokens);
	}

	/// <summary>
	/// UrlDecodes a String using System.Uri.UnescapeDataString().
	/// </summary>
	/// <param name="text">The text to URL decode.</param>
	/// <returns>Returns text as an URL-decoded String.</returns>
	public static String urlDecode(String text)	{
		if (StringUtils.isBlank(text))
			return text;

		// Pre-process for + sign space formatting since System.Uri doesn't handle it
		// plus literals are encoded as %2b normally so this should be safe.
		text = text.replace("+", " ");
		
		return URLDecoder.decode(text); 
	}
	
	/// <summary>
	/// Adds the <paramref name="results" /> to the current user's session. If an object already exists,
	/// the results are added to the existing collection. No action is taken if the session is unavailable. 
	/// The session object is given the name stored in <see cref="GlobalConstants.SkippedFilesDuringUploadSessionKey" />.
	/// </summary>
	/// <param name="results">The results to store in the user's session.</param>
	public static void addResultToSession(HttpServletRequest request, List<ActionResult> results){
		if (request == null || request.getSession(false) == null)
			return;

		String objResults = (String)request.getSession().getAttribute(Constants.SkippedFilesDuringUploadSessionKey);

		List<ActionResult> uploadResults = (objResults == null ? new ArrayList<ActionResult>() : JsonMapper.getInstance().fromJson(objResults, new ArrayList<ActionResult>().getClass()));

		synchronized (uploadResults)
		{
			uploadResults.addAll(results);
			request.getSession().setAttribute(Constants.SkippedFilesDuringUploadSessionKey, JsonMapper.getInstance().toJson(uploadResults));
		}
	}
	
	/// <summary>
	/// Gets a <see cref="StringContent" /> instance with details about the specified <paramref name="ex" />.
	/// </summary>
	/// <param name="ex">The exception.</param>
	/// <returns>An instance of <see cref="StringContent" />.</returns>
	public static String getExStringContent(Exception ex){
		String msg = "An error occurred on the server. Check the gallery's event log for details. ";

		if (log.isDebugEnabled())	{
			msg += StringUtils.join(ex.getClass(), ": ", ex.getMessage());
		}

		return msg;
	}

	//#endregion

	//#region Private Static Methods

	/// <summary>
	/// Validates that the e-mail address conforms to a regular expression pattern for e-mail addresses.
	/// </summary>
	/// <param name="email">The String to validate as an email address.</param>
	/// <returns>Returns true when the email parameter conforms to the expected format of an email address; otherwise
	/// returns false.</returns>
	private static boolean validateEmailByRegEx(String email){
		final String pattern = "\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern emailPattern = Pattern
		        .compile(pattern);

		return emailPattern.matcher(email).matches();
	}

	/// <summary>
	/// Uses the validation built in to the .NET constructor for the <see cref="System.Net.Mail.MailAddress"/> class
	/// to determine if the e-mail conforms to the expected format of an e-mail address.
	/// </summary>
	/// <param name="email">The String to validate as an email address.</param>
	/// <returns>Returns true when the email parameter conforms to the expected format of an email address; otherwise
	/// returns false.</returns>
	private static boolean validateEmailByMailAddressCtor(String email)	{
		boolean passesMailAddressTest = false;
		try{
			new InternetAddress(email);
			passesMailAddressTest = true;
		}catch (AddressException ex) { }

		return passesMailAddressTest;
	}

	//#endregion
}
