package com.mds.sys.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.mds.common.Constants;
import com.mds.cm.content.GalleryBo;
import com.mds.cm.exception.CannotWriteToDirectoryException;
import com.mds.cm.util.CMUtils;
import com.mds.common.model.LicenseKey;
import com.mds.common.utils.Reflections;
import com.mds.common.utils.SpringContextHolder;
import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.core.exception.InvalidEnumArgumentException;
import com.mds.sys.model.AppSetting;
import com.mds.sys.service.AppSettingManager;
import com.mds.util.DateUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;
import com.mds.core.EmailServerType;
import com.mds.core.MaintenanceStatus;
import com.mds.core.StringCollection;
import com.mds.core.exception.ApplicationNotInitializedException;

/// <summary>
/// Contains application level settings used by MDS System. This class must be initialized by the calling assembly early in the 
/// application life cycle. It is initialized by calling <see cref="Initialize" />. In the case of the Gallery 
/// Server Pro web application, <see cref="Initialize" /> is called from the static constructor of the DcmPage base page.
/// </summary>
public class AppSettings{
	
	public static final String MDS = "mds";
	public static final String EXT_CONFIG = "properties";
    public static final String DOT_CONFIG = "." + EXT_CONFIG;
    
	public static final String MDS_HOME = MDS + ".home";
    public static final String DEFAULT_DATACENTER_DIR = "mmrepo";
    public static final String DEFAULT_CONFIG_DIR = "config";
    public static final String DEFAULT_CONFIG_DEFINITION_FILE = "mds.properties";
    public static final String MDS_CONFIG_DEFINITION_PATH = DEFAULT_CONFIG_DIR + File.separatorChar +
        DEFAULT_CONFIG_DEFINITION_FILE;
    
	//#region Private Static Fields
	
	private static volatile AppSettings _instance;
	private static final Object _sharedLock = new Object();
	
	protected static final Logger log = LoggerFactory.getLogger(AppSettings.class);

	private static AppSettingManager appSettingManager = SpringContextHolder.getBean(AppSettingManager.class);

	//#endregion

	//#region Private Fields

	private int contentObjectDownloadBufferSize;
	private boolean encryptContentObjectUrlOnClient;
	private String encryptionKey;
	private String jQueryScriptPath;
	private String jQueryMigrateScriptPath;
	private String jQueryUiScriptPath;
	private String membershipProviderName;
	private String roleProviderName;
	private LicenseKey license;
	private boolean enableCache;
	private boolean allowGalleryAdminToManageUsersAndRoles;
	private boolean allowGalleryAdminViewAllUsersAndRoles;
	private int maxNumberErrorItems;
	private String emailFromName;
	private String emailFromAddress;
	private String emailPassword;
	private String smtpServer;
	private String smtpServerPort;
	private boolean sendEmailUsingSsl;
	private String tempUploadDirectory;
	private String contentAndSettingCenter;
	private String cscDirectory;
	private String physicalAppPath;
	private String applicationName;
	//private ApplicationTrustLevel trustLevel = ApplicationTrustLevel.None;
	private String javaVMVersion;
	private String iisAppPoolIdentity;
	private String ffmpegPath;
	private String imageMagickConvertPath;
	private boolean isInitialized;
	private MaintenanceStatus maintenanceStatus = MaintenanceStatus.NotStarted;
	private StringCollection verifiedFilePaths = new StringCollection();
	private boolean installationRequested;
	private String dataSchemaVersion;
	private String galleryResourcesPath;
	// Current Home directory
	private String homePath = null;
	
	private String skin = "light";
	
	private Integer approvalSwitch;
	private Boolean enableVerificationCode;
	private Boolean usePdfRenderer;
	private Boolean independentSpaceForDailyList;
	
	private EmailServerType emailServerType;
	private String smtpDomail;

	//#endregion

	//#region Constructors

	private AppSettings(){
	}

	//#endregion

	//#region Public Properties

	/// <summary>
	/// Gets or sets the name of the skin.
	/// </summary>
	/// <value>The name of the skin.</value>
	public String getSkin() {
		return this.skin;
	}
	
	public void setSkin(String skin) {
		this.skin = skin;
	}

	/// <summary>
	/// Gets or sets the size of each block of bytes when transferring files to streams and vice versa. This property was originally
	/// created to specify the buffer size for downloading a content object to the client, but it is now used for all
	/// file/stream copy operations.
	/// </summary>
	public int getContentObjectDownloadBufferSize()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.contentObjectDownloadBufferSize;
	}
	
	public void setContentObjectDownloadBufferSize(int contentObjectDownloadBufferSize)	{
		this.contentObjectDownloadBufferSize = contentObjectDownloadBufferSize;
	}

	/// <summary>
	/// Gets or sets a value indicating whether security-sensitive portions of the URL to the content object are encrypted when it is sent
	/// to the client browser. When false, the URL to the content object is sent in plain text, such as
	/// "handler/getmedia.ashx?moid=34&amp;dt=1&amp;g=1"
	/// These URLs can be seen by viewing the source of the HTML page. From this URL one can determine the album ID
	/// for this content object is 8, (aid=8), the file path to the content object on the server is
	/// C:\ds\mypics\birthday.jpeg, and the requested image is a thumbnail (dt=1, where 1 is the value of the
	/// MDS.Business.DisplayObjectType enumeration for a thumbnail). For enhanced security, this property should
	/// be true, which uses Triple DES encryption to encrypt the the query String.
	/// It is recommended to set this to true except when you are	troubleshooting and it is useful to see the
	/// filename and path in the HTML source. The Triple DES algorithm uses the secret key specified in the
	/// <see cref="EncryptionKey"/> property.
	/// </summary>
	public boolean getEncryptContentObjectUrlOnClient()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.encryptContentObjectUrlOnClient;
	}
	
	public void setEncryptContentObjectUrlOnClient(boolean encryptContentObjectUrlOnClient)	{
		this.encryptContentObjectUrlOnClient = encryptContentObjectUrlOnClient;
	}

	/// <summary>
	/// Gets or sets the secret key used for the Triple DES algorithm. Applicable when the property <see cref="EncryptContentObjectUrlOnClient"/> = true.
	/// The String must be 24 characters in length and be sufficiently strong so that it cannot be easily cracked.
	/// An exception is thrown by the Java VM if the key is considered weak. Change this to a value known only
	/// to you to prevent others from being able to decrypt.
	/// </summary>
	public String getEncryptionKey()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.encryptionKey;
	}
	
	public void setEncryptionKey(String encryptionKey)	{
		this.encryptionKey = encryptionKey;
	}

	/// <summary>
	/// Gets or sets the absolute or relative path to the jQuery script file as stored in the application settings table.
	/// A relative path must be relative to the root of the web application and start with a tilde ("~"). An absolute path must be a full URI
	/// (e.g. http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js, //ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js).
	/// It is not valid to specify a UNC path, mapped drive path, or path to the local file system (e.g. "C:\scripts\jquery.js").
	/// Specify an empty String to indicate to MDS that the containing application is responsible for adding the jQuery
	/// reference. In this case, MDS will not attempt to add a jQuery reference.  Guaranteed to not return null.
	/// </summary>
	/// <value>
	/// The absolute or relative path to the jQuery script file as stored in the application settings table.
	/// </value>
	/// <remarks>The path is returned exactly how it appears in the database.</remarks>
	public String getJQueryScriptPath()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.jQueryScriptPath;
	}
	
	public void setJQueryScriptPath(String jQueryScriptPath )	{
		if (jQueryScriptPath == null)
			this.jQueryScriptPath =	StringUtils.EMPTY;
		else
			this.jQueryScriptPath = jQueryScriptPath;
	}

	/// <summary>
	/// Gets or sets the absolute or relative path to the jQuery Migrate script file as stored in the application settings table.
	/// The jQuery Migrate Plugin is used to provide backwards compatibility when using jQuery 1.9 and higher.
	/// A relative path must be relative to the root of the web application and start with a tilde ("~"). An absolute path must be a full URI
	/// (e.g. http://code.jquery.com/jquery-migrate-1.0.0.min.js, //code.jquery.com/jquery-migrate-1.0.0.min.js).
	/// It is not valid to specify a UNC path, mapped drive path, or path to the local file system (e.g. "C:\scripts\jquery.js").
	/// Specify an empty String when the migrate plugin should not be used.  Guaranteed to not return null.
	/// </summary>
	/// <value>The absolute or relative path to the jQuery Migrate script file as stored in the application settings table.</value>
	/// <exception cref="EventLogs"></exception>
	/// <remarks>The path is returned exactly how it appears in the database.</remarks>
	public String getJQueryMigrateScriptPath()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.jQueryMigrateScriptPath;
	}
	
	public void setJQueryMigrateScriptPath(String jQueryMigrateScriptPath)	{
		this.jQueryMigrateScriptPath = jQueryMigrateScriptPath== null ? StringUtils.EMPTY : jQueryMigrateScriptPath;
	}

	/// <summary>
	/// Gets or sets the absolute or relative path to the jQuery UI script file as stored in the application settings table.
	/// A relative path must be relative to the root of the web application and start with a tilde ("~"). An absolute path must be a full URI
	/// (e.g. http://ajax.googleapis.com/ajax/libs/jqueryui/1.9.1/jquery-ui.min.js.
	/// It is not valid to specify a UNC path, mapped drive path, or path to the local file system (e.g. "C:\scripts\jquery.js").
	/// Specify an empty String to indicate to MDS that the containing application is responsible for adding the jQuery UI
	/// reference. In this case, MDS will not attempt to add a jQuery reference. Guaranteed to not return null.
	/// </summary>
	/// <value>
	/// The absolute or relative path to the jQuery UI script file as stored in the application settings table.
	/// </value>
	/// <remarks>The path is returned exactly how it appears in the database.</remarks>
	public String getJQueryUiScriptPath()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.jQueryUiScriptPath;
	}
	
	public void setJQueryUiScriptPath(String jQueryUiScriptPath)	{
		if (jQueryUiScriptPath == null)
			jQueryUiScriptPath = StringUtils.EMPTY;
		
		this.jQueryUiScriptPath = jQueryUiScriptPath;
	}

	/// <summary>
	/// Gets the data store currently being used for gallery data.
	/// </summary>
	/// <value>An instance of <see cref="ProviderDataStore" />.</value>
	/*public ProviderDataStore ProviderDataStore
	{
			if (CMUtils.GetConnectionStringSettings().ProviderName.StartsWith("System.Data.SqlServerCe"))
				return ProviderDataStore.SqlCe;
			else if (CMUtils.GetConnectionStringSettings().ProviderName.StartsWith("System.Data.SqlClient"))
				return ProviderDataStore.SqlServer;
			else
				return ProviderDataStore.Unknown;
	}*/

	/// <summary>
	/// Gets or sets the name of the Membership provider for the gallery users. Optional. When not specified, the default provider specified
	/// in web.config is used.
	/// </summary>
	/// <remarks>The name of the Membership provider for the gallery users.</remarks>
	public String getMembershipProviderName()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.membershipProviderName;
	}
	
	public void setMembershipProviderName(String membershipProviderName)	{
		this.membershipProviderName = membershipProviderName;
	}

	/// <summary>
	/// Gets or sets the name of the Role provider for the gallery users. Optional. When not specified, the default provider specified
	/// in web.config is used.
	/// </summary>
	/// <remarks>The name of the Role provider for the gallery users.</remarks>
	public String getRoleProviderName()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.roleProviderName;
	}
	
	public void setRoleProviderName(String roleProviderName)	{
		this.roleProviderName = roleProviderName;
	}

	/// <summary>
	/// Gets or sets the license for the current application.
	/// </summary>
	/// <value>The license for the current application.</value>
	public LicenseKey getLicense()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.license;
	}
	
	public void setLicense(LicenseKey license)	{
		this.license = license;
	}

	/// <summary>
	/// Gets or sets the product key for this installation of MDS System.
	/// </summary>
	/*public String ProductKey
	{
			if (!this.isInitialized)
			{
				throw new ApplicationNotInitializedException();
			}

			return this.license.ProductKey;
			this.license = new License
			{
				ProductKey = value,
				InstallDate = GetFirstGalleryInstallationDate(),
			};

			this.license.Validate(false);
	}*/

	/// <summary>
	/// Gets or sets a value indicating whether to store objects in a cache for quicker retrieval. This significantly improves
	/// performance, but cannot be used in web farms because the cache is local to each server and there is not a cross-server
	/// mechanism to expire the cache.
	/// </summary>
	public boolean getEnableCache()	{
		return this.enableCache;
	}
	
	public void setEnableCache(boolean enableCache)	{
		this.enableCache = enableCache;
	}

	/// <summary>
	/// Gets or sets a value indicating whether gallery administrators are allowed to create, edit, and delete users and roles.</summary>
	public boolean getAllowGalleryAdminToManageUsersAndRoles()	{
		return this.allowGalleryAdminToManageUsersAndRoles;
	}
	
	public void setAllowGalleryAdminToManageUsersAndRoles(boolean allowGalleryAdminToManageUsersAndRoles)	{
		this.allowGalleryAdminToManageUsersAndRoles = allowGalleryAdminToManageUsersAndRoles;
	}

	/// <summary>
	/// Gets or sets a value indicating whether gallery administrators are allowed to see users and roles that do not have 
	/// access to current gallery.</summary>
	public boolean getAllowGalleryAdminToViewAllUsersAndRoles()	{
		return this.allowGalleryAdminViewAllUsersAndRoles;
	}
	
	public void setAllowGalleryAdminToViewAllUsersAndRoles(boolean allowGalleryAdminViewAllUsersAndRoles)	{
		this.allowGalleryAdminViewAllUsersAndRoles = allowGalleryAdminViewAllUsersAndRoles;
	}

	/// <summary>
	/// Indicates the maximum number of error objects to persist to the data store. When the number of errors exceeds this
	/// value, the oldest item is purged to make room for the new item. A value of zero means no limit is enforced.
	/// </summary>
	public int getMaxNumberErrorItems()	{
		return this.maxNumberErrorItems;
	}
	
	public void setMaxNumberErrorItems(int maxNumberErrorItems)	{
		if (maxNumberErrorItems < 0){
			throw new ArgumentOutOfRangeException("value", MessageFormat.format("Invalid MaxNumberErrorItems setting: The value must be between 0 and {0}. Instead, the value was {1}.", Integer.MAX_VALUE, maxNumberErrorItems));
		}

		this.maxNumberErrorItems = maxNumberErrorItems;
	}

	/// <summary>
	/// The name associated with the <see cref="EmailFromAddress" /> email address. Emails sent from MDS System
	/// will appear to be sent from this person.
	/// </summary>
	/// <value>The name of the email from.</value>
	public String getEmailFromName(){
		return this.emailFromName;
	}
	
	public void setEmailFromName(String emailFromName){
		this.emailFromName = emailFromName;
	}

	/// <summary>
	/// The email address associated with <see cref="EmailFromName" />. Emails sent from MDS System
	/// will appear to be sent from this email address.
	/// </summary>
	/// <value>The email from address.</value>
	/// <exception cref="System.ArgumentOutOfRangeException">value</exception>
	public String getEmailFromAddress()	{
		return this.emailFromAddress;
	}
	
	public void setEmailFromAddress(String emailFromAddress)	{
		if (!HelperFunctions.isValidEmail(emailFromAddress)){
			throw new ArgumentOutOfRangeException("emailFromAddress", MessageFormat.format("Invalid EmailFromAddress setting: The value must be a valid e-mail address. Instead, the value was {0}.", emailFromAddress));
		}

		this.emailFromAddress = emailFromAddress;
	}
	
	/// <summary>
	/// The email address associated with <see cref="EmailPassword" />. Emails sent from HRIS System
	/// will appear to be sent from this email address.
	/// </summary>
	/// <value>The email password.</value>
	/// <exception cref="System.ArgumentOutOfRangeException">value</exception>
	public String getEmailPassword() {
        return emailPassword; 
    }
	
	public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }
	
	public EmailServerType getEmailServerType() {
		return emailServerType;
	}

	public void setEmailServerType(EmailServerType emailServerType) {
		this.emailServerType = emailServerType;
	}

	/// <summary>
	/// Specifies the IP address or name of the SMTP server used to send emails. (Examples: 127.0.0.1, 
	/// Godzilla, mail.yourisp.com) This value will override the SMTP server setting that may be in the 
	/// system.net mailSettings section of the web.config file (either explicitly or inherited from a 
	/// parent web.config file). Leave this setting blank to use the value in web.config or if you are 
	/// not using the email functionality.
	/// </summary>
	public String getSmtpServer(){
		return this.smtpServer;
	}
	
	public void setSmtpServer(String smtpServer){
		this.smtpServer = smtpServer;
	}
	
	/// <summary>
	/// Specifies the IP address or name of the SMTP domail(exchange server only) used to send emails. (Examples: mmdsplus.com, 
	/// Godzilla, mail.yourisp.com) This value will override the SMTP domail setting that may be in the 
	/// system.net mailSettings section of the web.config file (either explicitly or inherited from a 
	/// parent web.config file). Leave this setting blank to use the value in web.config or if you are 
	/// not using the email functionality.
	/// </summary>
	public String getSmtpDomail(){
		return this.smtpDomail;
	}
	
	public void setSmtpDomail(String smtpDomail){
		this.smtpDomail = smtpDomail;
	}
	
	/// <summary>
	/// Specifies the SMTP server port number used to send emails. This value will override the SMTP 
	/// server port setting that may be in the system.net mailSettings section of the web.config file 
	/// (either explicitly or inherited from a parent web.config file). Leave this setting blank to 
	/// use the value in web.config or if you are not using the email functionality. Defaults to 25 
	/// if not specified here or in web.config.
	/// </summary>
	public String getSmtpServerPort(){
		return this.smtpServerPort;
	}
	
	public void setSmtpServerPort(String smtpServerPort){
		this.smtpServerPort = smtpServerPort;
	}

	/// <summary>
	/// Specifies whether e-mail functionality uses Secure Sockets Layer (SSL) to encrypt the connection.
	/// </summary>
	public boolean getSendEmailUsingSsl(){
		return this.sendEmailUsingSsl;
	}
	
	public void setSendEmailUsingSsl(boolean sendEmailUsingSsl){
		this.sendEmailUsingSsl = sendEmailUsingSsl;
	}
	/// <summary>
	/// Gets the physical application path of the currently running application. For web applications this will be equal to
	/// the Request.PhysicalApplicationPath property.
	/// </summary>
	public String getPhysicalApplicationPath(){
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.physicalAppPath;
	}
	
	public void setPhysicalApplicationPath(String physicalAppPath){
		this.physicalAppPath = physicalAppPath;
	}

	/// <summary>
	/// Gets the trust level of the currently running application. 
	/// </summary>
	/*public ApplicationTrustLevel AppTrustLevel
	{
		get
		{
			if (!this.isInitialized)
			{
				throw new ApplicationNotInitializedException();
			}

			return this.trustLevel;
		}
		protected set
		{
			this.trustLevel = value;
		}
	}*/

	/// <summary>
	/// Gets the name of the currently running application. Default is "MDS System".
	/// </summary>
	public String getApplicationName()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.applicationName;
	}
	
	protected void setApplicationName(String applicationName)	{
		 this.applicationName = applicationName;
	}

	/// <summary>
	/// Gets or sets the path, relative to the current application, to the directory
	/// containing the MDS System resources such as images, user controls,
	/// scripts, etc. When setting the property, the following scrubbing occurs: (a) leading
	/// or trailing slashes are removed, (b) forward slashes ('/') are replaced with path 
	/// separator characters (i.e. the backward slash '\'). The result should be equivalent to
	/// the value of the galleryResourcesPath setting in the MDS/core section 
	/// of web.config. Examples: "ds", "MDS\resources"
	/// </summary>
	/// <value>A String.</value>
	public String getGalleryResourcesPath()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.galleryResourcesPath;
	}
	
	public void setGalleryResourcesPath(String galleryResourcesPath)	{
		if (galleryResourcesPath != null){
			galleryResourcesPath = StringUtils.strip(galleryResourcesPath,  File.separator + '/' ).replace('/', File.separatorChar);
		}

		this.galleryResourcesPath = galleryResourcesPath;
	}
	
	public String getHomePath(){
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}
		
		return this.homePath;
	}

	/// <summary>
	/// Gets the full physical path to the directory where files can be temporarily stored. Example:
	/// "C:\inetpub\wwwroot\MDS\App_Data\_Temp"
	/// </summary>
	public String getTempUploadDirectory()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.tempUploadDirectory;
	}
	
	public void setTempUploadDirectory(String tempUploadDirectory) throws CannotWriteToDirectoryException	{
		// Validate the path. Will throw an exception if a problem is found.
		try	{
			if (!this.verifiedFilePaths.contains(tempUploadDirectory)){
				HelperFunctions.validatePhysicalPathExistsAndIsReadWritable(tempUploadDirectory);
				this.verifiedFilePaths.add(tempUploadDirectory);
			}
		}catch (CannotWriteToDirectoryException ex){
			// Mark this app as not initialized so when user attempts to fix issue and refreshes the page, the initialize 
			// sequence will run again.
			this.isInitialized = false;
			throw ex;
		}
		
		this.tempUploadDirectory = tempUploadDirectory;
	}

	/// <summary>
	/// Gets the full physical path to the directory where MDS Contents and Settings center. Example:
	/// "D:\MDSServer\Contents and Settings Center"
	/// </summary>
	public String getContentAndSettingCenter()	{
		if (!this.isInitialized){
			throw new ApplicationNotInitializedException();
		}

		return this.contentAndSettingCenter;
	}
	
	public void setContentAndSettingCenter(String contentAndSettingCenter) throws CannotWriteToDirectoryException	{	
		this.contentAndSettingCenter = contentAndSettingCenter;
	}

	public String getCscDirectory() {
		return cscDirectory;
	}

	public void setCscDirectory(String cscDirectory) throws CannotWriteToDirectoryException {
		// Validate the path. Will throw an exception if a problem is found.
		try	{
			if (!this.verifiedFilePaths.contains(cscDirectory)){
				HelperFunctions.validatePhysicalPathExistsAndIsReadWritable(cscDirectory);
				this.verifiedFilePaths.add(cscDirectory);
			}
		}catch (CannotWriteToDirectoryException ex){
			throw ex;
		}
				
		this.cscDirectory = cscDirectory;
	}

	/// <summary>
	/// Gets the Java VM version the current application is running under. Contains only the major and minor components.
	/// </summary>
	/// <value>
	/// The Java VM version the current application is running under.
	/// </value>
	/// <example>
	/// To verify the current application is running 3.0 or higher, use this:
	/// <code>
	/// if (AppSetting.Instance.DotNetFrameworkVersion &gt; new Version("2.0"))
	/// { /* App is 3.0 or higher */ }
	/// </code>
	/// </example>
	public String getJavaVMVersion(){
		return this.javaVMVersion;
	}

	/// <summary>
	/// Gets the IIS application pool identity.
	/// </summary>
	/// <value>The application app pool identity.</value>
	/*public String IisAppPoolIdentity
	{
			if (this.iisAppPoolIdentity == null)
			{
				WindowsIdentity identity = WindowsIdentity.GetCurrent();
				this.iisAppPoolIdentity = (identity != null ? identity.Name : StringUtils.EMPTY);
			}

			return this.iisAppPoolIdentity;
	}*/

	/// <summary>
	/// Gets the full file path to the FFmpeg utility. During application initialization the bin directory is inspected for the
	/// presence of ffmpeg.exe. If present, this property is assigned the value of the full path to the utility. If not present,
	/// the property is assigned <see cref="StringUtils.EMPTY" />. FFmpeg is used to extract thumbnails from videos and for video conversion.
	/// Example: C:\inetpub\wwwroot\gallery\bin\ffmpeg.exe
	/// </summary>
	/// <value>
	/// 	Returns the full file path to the FFmpeg utility, or <see cref="StringUtils.EMPTY" /> if the utility is not present.
	/// </value>
	public String getFFmpegPath(){
		return this.ffmpegPath;
	}

	/// <summary>
	/// Gets the full file path to the ImageMagick convert.exe utility. During application initialization the bin directory is inspected for the
	/// presence of convert.exe. If present, this property is assigned the value of the full path to the utility. If not present,
	/// the property is assigned <see cref="StringUtils.EMPTY" />. This utility is used to extract thumbnails from .eps and .pdf files.
	/// Example: C:\inetpub\wwwroot\gallery\bin\convert.exe
	/// </summary>
	/// <value>
	/// 	Returns the full file path to the ImageMagick convert.exe utility, or <see cref="StringUtils.EMPTY" /> if the utility is not present.
	/// </value>
	public String getImageMagickConvertPath(){
		return this.imageMagickConvertPath;
	}

	/// <summary>
	/// Gets or sets the version of the objects in the database as reported by the database. Ex: "2.4.1"
	/// </summary>
	/// <value>The version of the objects in the database as reported by the database.</value>
	public String getDataSchemaVersion(){
		return this.dataSchemaVersion;
	}
	
	public void setDataSchemaVersion(String dataSchemaVersion){
		this.dataSchemaVersion = dataSchemaVersion;
	}

	/// <summary>
	/// Gets a value indicating whether the current library has been populated with data from the calling assembly.
	/// This library is initialized by calling <see cref="Initialize" />.
	/// </summary>
	public boolean isInitialized()	{
		return this.isInitialized;
	}

	/// <summary>
	/// Gets or sets the maintenance status. During each application restart a maintenance routine is run that helps
	/// ensure data integrity and eliminate unused data. This property describes the status of the maintenance routine.
	/// </summary>
	/// <value>The maintenance status.</value>
	public MaintenanceStatus getMaintenanceStatus()	{
		return this.maintenanceStatus;
	}
	
	public void setMaintenanceStatus(MaintenanceStatus maintenanceStatus)	{
		this.maintenanceStatus = maintenanceStatus;
	}

	/// <summary>
	/// Gets or sets a value indicating whether an installation is being requested. This value will be <c>true</c> when a text
	/// file named install.txt is detected in the App_Data directory. This property may be set during application initialization 
	/// so that later in the code path, when the gallery ID is available, the objects can be created.
	/// </summary>
	/// <value><c>true</c> if an installation is being requested; otherwise, <c>false</c>.</value>
	public boolean getInstallationRequested()	{
		return this.installationRequested;
	}
	
	public void setInstallationRequested(boolean installationRequested)	{
		this.installationRequested = installationRequested;
	}
	
	public Integer getApprovalSwitch() {
		return approvalSwitch;
	}

	public void setApprovalSwitch(Integer approvalSwitch) {
		this.approvalSwitch = approvalSwitch;
	}

	public Boolean isEnableVerificationCode() {
		return enableVerificationCode;
	}

	public void setEnableVerificationCode(Boolean enableVerificationCode) {
		this.enableVerificationCode = enableVerificationCode;
	}
	
	public Boolean getUsePdfRenderer() {
		return usePdfRenderer;
	}

	public void setUsePdfRenderer(Boolean usePdfRenderer) {
		this.usePdfRenderer = usePdfRenderer;
	}

	//#endregion

	//#region Public Static Properties

	public Boolean getIndependentSpaceForDailyList() {
		return independentSpaceForDailyList;
	}

	public void setIndependentSpaceForDailyList(Boolean independentSpaceForDailyList) {
		this.independentSpaceForDailyList = independentSpaceForDailyList;
	}

	/// <summary>
	/// Gets a reference to the <see cref="AppSetting" /> singleton for this app domain.
	/// </summary>
	public static AppSettings getInstance()	{
		if (_instance == null){
			synchronized (_sharedLock)
			{
				if (_instance == null){
					AppSettings tempAppSetting = new AppSettings();

					// Ensure that writes related to instantiation are flushed.
					//System.Threading.Thread.MemoryBarrier();
					_instance = tempAppSetting;
				}
			}
		}

		return _instance;
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Assign various application-wide properties to be used during the lifetime of the application. This method
	/// should be called once when the application first starts.
	/// </summary>
	/// <param name="trustLevel">The trust level of the current application.</param>
	/// <param name="physicalAppPath">The physical path of the currently executing application. For web applications
	/// this will be equal to the Request.PhysicalApplicationPath property.</param>
	/// <param name="appName">The name of the currently running application.</param>
	/// <param name="galleryResourcesPath">The path, relative to the current application, to 
	/// the directory containing the MDS System resources such as images, user controls, 
	/// scripts, etc. Use the value of the galleryResourcesPath setting in the 
	/// MDS/core section of web.config. Examples: "ds", "MDS\resources"</param>
	/// <exception cref="System.InvalidOperationException">Thrown when this method is called more than once during
	/// the application's lifetime.</exception>
	/// <exception cref="System.ArgumentOutOfRangeException">Thrown if the trustLevel parameter has the value
	/// ApplicationTrustLevel.None.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="physicalAppPath"/> or <paramref name="appName"/>
	/// is null.</exception>
	/// <exception cref="MDS.CannotWriteToDirectoryException">
	/// Thrown when MDS System is unable to write to, or delete from, a directory. This may be the content objects
	/// directory, thumbnail or optimized directory, the temporary directory (defined in
	/// <see cref="GlobalConstants.TempUploadDirectory"/>), or the App_Data directory.</exception>
	public void initialize(String physicalAppPath, String appName, String galleryResourcesPath) throws CannotWriteToDirectoryException{
		//#region Validation

		if (this.isInitialized)	{
			throw new UnsupportedOperationException("The AppSetting instance has already been initialized. It cannot be initialized more than once.");
		}


		if (StringUtils.isBlank(physicalAppPath))
			throw new ArgumentNullException("physicalAppPath");

		if (StringUtils.isBlank(appName))
			throw new ArgumentNullException("appName");

		//#endregion

		this.homePath= getMDSHome(null);
		this.physicalAppPath = physicalAppPath;
		this.applicationName = appName;
		this.galleryResourcesPath = galleryResourcesPath;

		configureAppDataDirectory(this.homePath);

		initializeDataStore();

		populateAppSettingsFromDataStore();

		this.setCscDirectory(getDataCenter(this.homePath, this.contentAndSettingCenter));
		configureTempDirectory(this.homePath);

		//this.dotNetFrameworkVersion = GetDotNetFrameworkVersion();

		String ffmpegPath = FilenameUtils.concat(this.homePath, "bin" + File.separator + "ffmpeg.exe");
		this.ffmpegPath = (FileMisc.fileExists(ffmpegPath) ? ffmpegPath : StringUtils.EMPTY);

		String imageMagickConvertPath = FilenameUtils.concat(this.homePath, "bin" + File.separator + "convert.exe");
		this.imageMagickConvertPath = (FileMisc.fileExists(imageMagickConvertPath) ? imageMagickConvertPath : StringUtils.EMPTY);

		this.isInitialized = true;

		// Validate the application and gallery settings. This must come after setting _isInitialized to true because the function 
		// accesses properties of the AppSetting singleton, which will throw a ApplicationNotInitializedException when a property is 
		// accessed before initialization is complete.
		validate();
	}

	/// <summary>
	/// Persist the specified application settings to the data store. Specify a null value for each parameter whose value is
	/// not changing.
	/// </summary>
	/// <param name="license">A license instance containing the product key for this installation of MDS System. The
	/// product key must be validated before invoking this method.</param>
	/// <param name="skin">The name of the skin.</param>
	/// <param name="contentObjectDownloadBufferSize">The size of each block of bytes when transferring files to streams and vice versa.</param>
	/// <param name="encryptContentObjectUrlOnClient">Indicates whether security-sensitive portions of the URL to the content object are
	/// encrypted when it is sent to the client browser.</param>
	/// <param name="encryptionKey">The secret key used for the Triple DES algorithm.</param>
	/// <param name="jQueryScriptPath">The absolute or relative path to the jQuery script file.</param>
	/// <param name="jQueryMigrateScriptPath">The absolute or relative path to the jQuery Migrate script file.</param>
	/// <param name="jQueryUiScriptPath">The absolute or relative path to the jQuery UI script file.</param>
	/// <param name="membershipProviderName">The name of the Membership provider for the gallery users.</param>
	/// <param name="roleProviderName">The name of the Role provider for the gallery users.</param>
	/// <param name="enableCache">Indicates whether to store objects in a cache for quicker retrieval.</param>
	/// <param name="allowGalleryAdminToManageUsersAndRoles">Indicates whether gallery administrators are allowed to create, edit, and delete
	/// users and roles.</param>
	/// <param name="allowGalleryAdminViewAllUsersAndRoles">Indicates whether gallery administrators are allowed to see users and roles that
	/// do not have access to current gallery.</param>
	/// <param name="maxNumberErrorItems">The maximum number of error objects to persist to the data store.</param>
	/// <param name="emailFromName">The name associated with the <paramref name="emailFromAddress" /> email address. Emails sent from MDS System
	/// will appear to be sent from this person.</param>
	/// <param name="emailFromAddress">The email address associated with <paramref name="emailFromName" />. Emails sent from MDS System
	/// will appear to be sent from this email address.</param>
	/// <param name="smtpServer">Specifies the IP address or name of the SMTP server used to send emails. (Examples: 127.0.0.1,
	/// Godzilla, mail.yourisp.com)</param>
	/// <param name="smtpServerPort">Specifies the SMTP server port number used to send emails.</param>
	/// <param name="sendEmailUsingSsl">Specifies whether e-mail functionality uses Secure Sockets Layer (SSL) to encrypt the connection.</param>
	public void save(LicenseKey license, String skin, Optional<Integer> contentObjectDownloadBufferSize, Optional<Boolean> encryptContentObjectUrlOnClient, String encryptionKey
			, String jQueryScriptPath, String jQueryMigrateScriptPath, String jQueryUiScriptPath, String membershipProviderName, String roleProviderName
			, Optional<Boolean> enableCache, Optional<Boolean> allowGalleryAdminToManageUsersAndRoles, Optional<Boolean> allowGalleryAdminViewAllUsersAndRoles, Optional<Integer> maxNumberErrorItems
					, String emailFromName, String emailFromAddress, String smtpServer, String smtpServerPort, Optional<Boolean> sendEmailUsingSsl)	{
		boolean productKeyWasChanged = false;

		synchronized (_sharedLock)
		{
			if (license != null){
				productKeyWasChanged = (this.license.getLicenseKey() != license.getLicenseKey());
				this.license = license;

				validateLicenseTypeConfiguration();
			}

			if (!StringUtils.isBlank(skin))
				this.skin = skin;

			if (contentObjectDownloadBufferSize != null && contentObjectDownloadBufferSize.isPresent())
				this.contentObjectDownloadBufferSize = contentObjectDownloadBufferSize.get();

			if (encryptContentObjectUrlOnClient != null && encryptContentObjectUrlOnClient.isPresent())
				this.encryptContentObjectUrlOnClient = encryptContentObjectUrlOnClient.get();

			if (!StringUtils.isBlank(encryptionKey))
				this.encryptionKey = encryptionKey;

			if (jQueryScriptPath != null)
				this.jQueryScriptPath = jQueryScriptPath;

			if (jQueryScriptPath != null)
				this.jQueryMigrateScriptPath = jQueryMigrateScriptPath;

			if (jQueryUiScriptPath != null)
				this.jQueryUiScriptPath = jQueryUiScriptPath;

			if (!StringUtils.isBlank(membershipProviderName))
				this.membershipProviderName = membershipProviderName;

			if (!StringUtils.isBlank(roleProviderName))
				this.roleProviderName = roleProviderName;

			if (enableCache != null && enableCache.isPresent())
				this.enableCache = enableCache.get();

			if (allowGalleryAdminToManageUsersAndRoles != null && allowGalleryAdminToManageUsersAndRoles.isPresent())
				this.allowGalleryAdminToManageUsersAndRoles = allowGalleryAdminToManageUsersAndRoles.get();

			if (allowGalleryAdminViewAllUsersAndRoles != null && allowGalleryAdminViewAllUsersAndRoles.isPresent())
				this.allowGalleryAdminViewAllUsersAndRoles = allowGalleryAdminViewAllUsersAndRoles.get();

			if (maxNumberErrorItems != null && maxNumberErrorItems.isPresent())
				this.maxNumberErrorItems = maxNumberErrorItems.get();

			if (emailFromName != null)
				this.emailFromName = emailFromName;

			if (emailFromAddress != null)
				this.emailFromAddress = emailFromAddress;

			if (smtpServer != null)
				this.smtpServer = smtpServer;

			if (smtpServerPort != null)
				this.smtpServerPort = smtpServerPort;

			if (sendEmailUsingSsl != null && sendEmailUsingSsl.isPresent())
				this.sendEmailUsingSsl = sendEmailUsingSsl.get();

			//CMUtils.GetDataProvider().AppSetting_Save(this);
			/*using (var repo = new AppSettingRepository())
			{
				repo.Save(this);
			}*/
			appSettingManager.saveAppSettings(this);

			if (productKeyWasChanged){
				CMUtils.clearWatermarkCache(); //Changing the product key might cause a different watermark to be rendered
			}
		}
	}

	private void populateAppSettingsFromDataStore(){
		//var asType = typeof(AppSetting);
		List<AppSetting> appSettingDtos = appSettingManager.getAll();
		for (AppSetting appSettingDto : appSettingDtos){
			String settingName = StringUtils.uncapitalize(appSettingDto.getSettingName().trim());
			  Field f = FieldUtils.getDeclaredField(this.getClass(), settingName, true);
			  if (f==null)
				  continue;
			  
			String settingValue = appSettingDto.getSettingValue();
			// Get param type and type cast
			Class<?> valType = f.getType();
			//log.debug("Import value type: ["+i+","+column+"] " + valType);
			
			Object val = null;
			try {
				if (valType == String.class){
					val = settingValue;
				}else if (valType == Integer.class || valType == int.class){
					val = StringUtils.toInteger(settingValue);
				}else if (valType == Long.class || valType == long.class){
					val = StringUtils.toLong(settingValue);
				}else if (valType == Double.class || valType == double.class){
					val = StringUtils.toDouble(settingValue);
				}else if (valType == Float.class || valType == float.class){
					val = Float.valueOf(settingValue);
				}else if (valType == Boolean.class || valType == boolean.class){
					val = Boolean.parseBoolean(settingValue);	
				}else if (valType == Date.class){
					val = DateUtils.parseDate(settingValue);
				}else if (valType.isEnum()){
					//val = Enum.valueOf((Class<T>) valType, val.toString());
					if (!StringUtils.isBlank(settingValue)) {
						val = valType.getMethod("valueOf", String.class).invoke(null, settingValue);
					}else {
						val = null;
					}
				}else{
					throw new ArgumentOutOfRangeException(MessageFormat.format("AppSetting.PopulateAppSettingsFromDataStore is not designed to process a property of type {0} (encountered in AppSetting.{1})", valType, settingName));
				}
				
			} catch (Exception ex) {
				val = null;
			}
			Reflections.invokeSetter(this, settingName, val);
		}
	}

	private void initializeDataStore(){
		/*System.Data.Entity.Database.SetInitializer(new System.Data.Entity.MigrateDatabaseToLatestVersion<MDSDB, MDSDBMigrationConfiguration>());

		var configuration = new MDSDBMigrationConfiguration(ProviderDataStore);
		var migrator = new System.Data.Entity.Migrations.DbMigrator(configuration);
		if (migrator.GetPendingMigrations().Any())
		{
			migrator.Update();
		}*/
	}

	private void configureAppDataDirectory(String physicalAppPath) throws CannotWriteToDirectoryException{
		// Validate that the App_Data path is read-writeable. Will throw an exception if a problem is found.
		String appDataDirectory = FilenameUtils.concat(physicalAppPath, Constants.AppDataDirectory);
		try	{
			HelperFunctions.validatePhysicalPathExistsAndIsReadWritable(appDataDirectory);
		}catch (CannotWriteToDirectoryException ex){
			// Mark this app as not initialized so when user attempts to fix issue and refreshes the page, the initialize 
			// sequence will run again.
			this.isInitialized = false;
			throw ex;
		}
	}
	
	private void configureTempDirectory(String physicalAppPath){
		this.tempUploadDirectory = FilenameUtils.concat(physicalAppPath, Constants.TempUploadDirectory);

		try	{
			// Clear out all directories and files in the temp directory. If an IOException error occurs, perhaps due to a locked file,
			// record it but do not let it propagate up the stack.
			File di = new File(this.tempUploadDirectory);
			if (di.exists()) {
				for (File file : di.listFiles()){
					if (!file.isHidden())	{
						if (file.isDirectory()) {
							FileUtils.deleteDirectory(file);
						}else {
							file.delete();
						}
					}
				}
			}
		}catch (IOException ex){
			//MDS.EventLogs.EventLogController.RecordError(ex, this);
			//HelperFunctions.PurgeCache();
		}
		catch (SecurityException ex)
		{
			//MDS.EventLogs.EventLogController.RecordError(ex, this);
			//HelperFunctions.PurgeCache();
		}
	}

	/*private static Version GetDotNetFrameworkVersion()
	{
		return new Version(Environment.Version.ToString(2));
	}*/

	/// <summary>
	/// Validate the application and gallery settings.
	/// </summary>
	private void validate(){
		validateGalleries();
	}

	/// <summary>
	/// Verifies each gallery has  the required minimum number of records, creating them if necessary.
	/// This function does not create a gallery.
	/// </summary>
	private static void validateGalleries(){
		for (GalleryBo gallery : CMUtils.loadGalleries()){
			gallery.configure();
		}
	}

	/// <summary>
	/// Gets the date/time when the first gallery in the database was created. For practical purposes we can consider this the date 
	/// the application was installed. If no galleries have been created (which may happen the first time we run the app), just
	/// return today's date.
	/// </summary>
	/// <returns>Returns a <see cref="Date" /> representing when the first gallery in the database was created.</returns>
	private static Date getFirstGalleryInstallationDate(){
		Date firstGalleryInstallDate = DateUtils.Now();

		for (GalleryBo gallery : CMUtils.loadGalleries()){
			if (gallery.getCreationDate().before(firstGalleryInstallDate)){
				firstGalleryInstallDate = gallery.getCreationDate();
			}
		}

		return firstGalleryInstallDate;
	}

	/// <summary>
	/// Verifies the application is correctly configured based on the current license type. Specifically, it verifies that
	/// additional UI templates are present for Enterprise license holders.
	/// </summary>
	private void validateLicenseTypeConfiguration(){
		/*if (license.LicenseType == LicenseLevel.Enterprise){
			SeedController.InsertEnterpriseTemplates();

			HelperFunctions.RemoveCache(CacheItem.UiTemplates);

			// The validation will make a copy of each new UI template for every gallery.
			validate();
		}*/
	}

	//#endregion
	/// <summary>
	/// Gets the path to the install trigger file. Example: "C:\websites\mds\install.txt". This file is expected to be
	/// an empty text file. When present, it is a signal to the application that an installation is being requested.
	/// </summary>
	/// <value>A <see cref="String" />.</value>
	public static String getInstallFilePath(){
		return FilenameUtils.concat(getMDSHome(null), Constants.InstallTriggerFileName);
	}
		
	/// <summary>
	/// Gets a value indicating whether an installation is being requested. Returns <c>true</c> when a text file
	/// named install.txt is present in the App_Data directory.
	/// </summary>
	/// <value><c>true</c> if an install is requested; otherwise, <c>false</c>.</value>
	public static boolean getInstallRequested()	{
		return FileMisc.fileExists(getInstallFilePath());
	}
			
	
	public static String getDataCenter(String homePath, String dataCenterDir) {
		//String homePath = getMDSHome(providedHome); 
		if (StringUtils.isBlank(dataCenterDir))
			return FilenameUtils.concat(homePath, DEFAULT_DATACENTER_DIR);
		else
			return FilenameUtils.concat(homePath, dataCenterDir);
	}
	
	public static String getMDSHome(String providedHome) {
		// See if valid home specified as system property (most trusted)
        String sysProperty = System.getProperty(MDS_HOME);
        log.info("Check if the required config exists - sysProperty: " + sysProperty);
        if (isValidMDSHome(sysProperty)) {
            return sysProperty;
        }

        log.info("Check if the required config exists - providedHome: " + providedHome);
        // See if valid home passed in
        if (isValidMDSHome(providedHome)) {
            return providedHome;
        }

        // If still not found, attempt to determine location of our JAR
        String pathRelativeToJar = null;
        try {
            // Check location of our running JAR
            URL jarLocation = UserUtils.class.getProtectionDomain().getCodeSource().getLocation();
            // Convert to a file & get "grandparent" directory
            // This JAR should be running in [mds]/lib/, so its parent is [mds]/lib, and grandparent is [mds]
            pathRelativeToJar = new File(jarLocation.toURI()).getParentFile().getParentFile().getAbsolutePath();
            // Is the grandparent directory of where the JAR resides a valid MDS home?
            log.info("Check if the required config exists - pathRelativeToJar: " + pathRelativeToJar);
            if (isValidMDSHome(pathRelativeToJar)) {
                return pathRelativeToJar;
            }
        } catch (URISyntaxException e) { // do nothing
        }

        // If still not valid, check Catalina
        String catalina = getCatalina();
        log.info("Check if the required config exists - catalina: " + catalina);
        if (isValidMDSHome(catalina)) {
            return catalina;
        }

        // If still not valid, check "user.home" system property
        String userHome = System.getProperty("user.home");
        log.info("Check if the required config exists - userHome: " + userHome);
        if (isValidMDSHome(userHome)) {
            return userHome;
        }

        // Finally, try root path ("/")
        if (isValidMDSHome("/")) {
            return "/";
        }

        // If none of the above worked, MDS Kernel will fail to start.
        throw new RuntimeException("MDS home directory could not be determined. It MUST include a subpath of " +
                                       "'" + File.separatorChar + DEFAULT_DATACENTER_DIR + "'. " +
                                       "Please consider setting the '" + MDS_HOME + "' system property or ensure " +
                                       "the mds-api.jar is being run from [mds]/lib/.");
	}
	
	/**
     * This simply attempts to find the servlet container home for tomcat.
     *
     * @return the path to the servlet container home OR null if it cannot be found
     */
    protected static String getCatalina() {
        String catalina = System.getProperty("catalina.base");
        if (catalina == null) {
            catalina = System.getProperty("catalina.home");
        }
        return catalina;
    }
	
	/**
     * Returns whether a given path seems to have the required MDS configurations
     * in order to make it a valid MDS home directory
     *
     * @param path path to validate
     * @return true if path seems valid, false otherwise
     */
    protected static boolean isValidMDSHome(String path) {
        // If null path, return false immediately
        if (path == null) {
            return false;
        }

        // Based on path get full path to the configuration definition
        String configDefinition = path + File.separatorChar + MDS_CONFIG_DEFINITION_PATH;
        File configDefFile = new File(configDefinition);

        // Check if the required config exists
        if (configDefFile.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
