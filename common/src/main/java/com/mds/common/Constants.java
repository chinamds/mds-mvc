package com.mds.common;


/**
 * Constant values used throughout the application.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public final class Constants {

    private Constants() {
        // hide me
    }
    //~ Static fields/initializers =============================================

    /**
     * Assets Version constant
     */
    public static final String ASSETS_VERSION = "assetsVersion";
    /**
     * The name of the ResourceBundle used in this application
     */
    public static final String BUNDLE_KEY = "ApplicationResources";

    /**
     * File separator from System properties
     */
    public static final String FILE_SEP = System.getProperty("file.separator");

    /**
     * User home from System properties
     */
    public static final String USER_HOME = System.getProperty("user.home") + FILE_SEP;

    /**
     * The name of the configuration hashmap stored in application scope.
     */
    public static final String CONFIG = "appConfig";

    /**
     * Session scope attribute that holds the locale set by the user. By setting this key
     * to the same one that Struts uses, we get synchronization in Struts w/o having
     * to do extra work or have two session-level variables.
     */
    public static final String PREFERRED_LOCALE_KEY = "org.apache.struts2.action.LOCALE";

    /**
     * The request scope attribute under which an editable user form is stored
     */
    public static final String USER_KEY = "userForm";

    /**
     * The request scope attribute that holds the user list
     */
    public static final String USER_LIST = "userList";

    /**
     * The request scope attribute for indicating a newly-registered user
     */
    public static final String REGISTERED = "registered";

    /**
     * The name of the Administrator role, as specified in web.xml
     */
    public static final String ADMIN_ROLE = "ROLE_ADMIN";

    /**
     * The name of the User role, as specified in web.xml
     */
    public static final String USER_ROLE = "ROLE_USER";

    /**
     * The name of the user's role list, a request-scoped attribute
     * when adding/editing a user.
     */
    public static final String USER_ROLES = "userRoles";
    
    public static final String CURRENT_USER = "user";

    /**
     * The name of the available roles list, a request-scoped attribute
     * when adding/editing a user.
     */
    public static final String AVAILABLE_ROLES = "availableRoles";
    
 // 显示/隐藏
 	public static final String SHOW = "1";
 	public static final String HIDE = "0";
 	
 	// 是/否
 	public static final String YES = "1";
 	public static final String NO = "0";
    
    // Logical deletion flag （0：normal；1：deleted；2：approval；）
 	public static final String FIELD_DEL_FLAG = "delFlag";
 	public static final String DEL_FLAG_NORMAL = "0";
 	public static final String DEL_FLAG_DELETE = "1";
 	public static final String DEL_FLAG_AUDIT = "2";
 	
 	/**
     * 操作名称
     */
 	public static final String OP_NAME = "op";


    /**
     * message key
     */
 	public static final String MESSAGES_KEY = "successMessages";

    /**
     * error message key
     */
    public static final String ERRORS_MESSAGES_KEY = "errors";

    /**
     * pre page URL
     */
 	public static final String BACK_URL = "BackURL";

 	public static final String IGNORE_BACK_URL = "ignoreBackURL";

    /**
     * 当前请求的地址 带参数
     */
 	public static final String CURRENT_URL = "currentURL";

    /**
     * 当前请求的地址 不带参数
     */
 	public static final String NO_QUERYSTRING_CURRENT_URL = "noQueryStringCurrentURL";

 	public static final String CONTEXT_PATH = "ctx";

    /**
     * 当前登录的用户
     */
 	public static final String CURRENT_USERNAME = "username";
 	public static final String ENCODING = "UTF-8";
    
    /**
     * The name of the i18n key.
     * menu title/description
     */
    public static final String Suffix_Title = ".title";
    public static final String Suffix_Desc = ".description";
    
	/// <summary>
	/// The default name for a user when no actual user account is available. For example, this value is used when remotely
	/// invoking a synchronization.
	/// </summary>
	public static final String SystemUserName = "System";
	/// <summary>
	/// The default name for a directory when a valid name cannot be generated from the album title. This occurs
	/// when a user enters an album title consisting entirely of characters that are invalid for a directory
	/// name, such as ?, *, :.
	/// </summary>
	public static final String DefaultAlbumDirectoryName = "Album";
	
	/// <summary>
	/// Gets the name of the dictionary key that references the <see cref="Interfaces.IMDSRoleCollection" /> item containing
	/// all roles for the current gallery in the cache item named <see cref="CacheItem.MDSRoles" />. Note that other items 
	/// in the dictionary have keys identified by a concatenation of the user's session ID and username.
	/// </summary>
	public static final String MDSRoleAllRolesCacheKey = "AllRoles";

	/// <summary>
	/// Gets the String that is used for the beginning of every role name used for album ownership. The role name has
	/// this format: {RoleNamePrefix} - {AlbumOwnerUserName} - {AlbumTitle} (album {AlbumID}) For example:
	/// "Album Owner - rdmartin - rdmartin's album (album 193)" Current value: "Album Owner"
	/// </summary>
	public static final String AlbumOwnerRoleNamePrefix = "Album Owner";

	/// <summary>
	/// Gets the name of the role that defines the permissions to use for album ownership roles.
	/// Current value: _Album Owner Template"
	/// </summary>
	public static final String AlbumOwnerRoleTemplateName = "_Album Owner Template";

	/// <summary>
	/// Gets the name of the session variable that stores a List&lt;String&gt; of filenames that were skipped
	/// when the user added one or more files to MDS System on the Add objects page.
	/// </summary>
	public static final String SkippedFilesDuringUploadSessionKey = "SkippedFiles";

	/// <summary>
	/// Gets the name of the thumbnail file that is created to represent an external content object.
	/// </summary>
	public static final String ExternalContentObjectFilename = "external";

	/// <summary>
	/// Gets the maximum number of skipped objects to display to the user after a synchronization. If the number is too high, 
	/// it can take a long time to transmit the data to the browser, or it it can exceed the maxJsonLength value set in web.config,
	/// which causes a "maximum length exceed" error.
	/// </summary>
	public static final int MaxNumberOfSkippedObjectsToDisplayAfterSynch = 500;

	/// <summary>
	/// Gets the maximum number of users to display in a list on the manage users page. When the number of users exceeds
	/// this number, the layout of the page changes to be more efficient with large numbers of users.
	/// </summary>
	public static final int MaxNumberOfUsersToDisplayOnManageUsersPage = 1000;

	/// <summary>
	/// Gets the path, relative to the web application root, where files may be temporarily persisted. Ex: "App_Data\\_Temp"
	/// </summary>
	public static final String TempUploadDirectory = "App_Data\\_Temp";

	/// <summary>
	/// Gets the path, relative to the web application root, of the application data directory. Ex: "App_Data"
	/// </summary>
	public static final String AppDataDirectory = "App_Data";

	/// <summary>
	/// Gets the name of the file that, when present in the App_Data directory, causes the Install Wizard to automatically run.
	/// Ex: "install.txt"
	/// </summary>
	public static final String InstallTriggerFileName = "install.txt";

	/// <summary>
	/// Gets the name of the Active Directory membership provider.
	/// </summary>
	public static final String ActiveDirectoryMembershipProviderName = "System.Web.Security.ActiveDirectoryMembershipProvider";

	/// <summary>
	/// Gets the number of days MDS System is fully functional before it requires a product key to be entered.
	/// Default value = 30.
	/// </summary>
	public static final int TrialNumberOfDays = 30;

	/// <summary>
	/// The maximum allowed length for an album directory name.
	/// </summary>
	public static final int AlbumDirectoryNameLength = 255;

	/// <summary>
	/// The maximum allowed length for a content object file name.
	/// </summary>
	public static final int ContentObjectFileNameLength = 255;
	
	public static final String APP_NAME = "MDS System";
	public static final String SAMPLE_IMAGE_FILENAME = "mdsplus.jpg"; // The name of an embedded resource in the App_GlobalResources directory
	public static final String ENCRYPTION_KEY = "mNU-h7:5f_)3=c%@^}#U9Tn*"; // The default encryption key as stored in a new installation. It is updated
	
	// Note this field is also defined in MDS.Business.DataConstants. We also define it here because DotNetNuke has a 50-char
	// limit but we don't want to change the value going to the stored procs, so we "override" it here.
	public static final int RoleNameLength = 256;
	
	public static final double DEFAULT_DURATION  = 10.0f;
	public static final int MDS_MAX_DURATION  = 8640000;
	public static final int MDS_MAX_CONTENT = 1024;
	public static final double EPSINON = 0.00001;
	
	//Content type define
	public static final int	IMAGE_TYPE = 0;
	public static final int	VIDEO_TYPE = 1;
	//public static final int	VCD_TYPE = 1;
	public static final int	DVD_TYPE = 2;
	public static final int	POWERPOINT_TYPE = 3;
	public static final int	WEBPAGE_TYPE = 4;
	public static final int	FLASH_TYPE = 5;
	public static final int	TVCAPTURE_TYPE = 6;
	public static final int	TEXT_TYPE = 7;
	public static final int	STREAMING_TYPE = 8;
	public static final int	ONLINE_TYPE = 9;
	public static final int	CLOCK_TYPE = 10;
	public static final int	WEBCAM_TYPE = 11;
	public static final int	DDE_TYPE = 12;
	public static final int	WEATHER_TYPE = 13;
	public static final int	DIRECTPLAY_TYPE = 14;
	public static final int	EXPLORER_TYPE = 15;
	public static final int	LINKAGE_TYPE = 16;
	public static final int	EVENT_TYPE = 17;
	public static final int	PLUGIN_TYPE = 18;
	public static final int	CAROUSEL_TYPE = 19;
	public static final int	QUEUE_TYPE = 20;
	public static final int	SITE_PLAYLIST = 21;
	public static final int	LIGHTBOX_TYPE = 22;
	public static final int	WMEDIA_TYPE = 23;
	public static final int	QUICKTIME_TYPE = 24;
	public static final int	AUDIO_TYPE = 25;
	public static final int	PDF_TYPE = 26;
	public static final int	AMELEMENT_TYPE = 27;
	public static final int	AMCONTENT_TYPE = 28;
	public static final int	RSS_TYPE = 29;
}
