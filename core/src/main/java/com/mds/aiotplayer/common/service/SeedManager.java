/**
 * Copyright &copy; 2016-2017 <a href="https://github.com/chinamds/mds">MDSPlus</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.common.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.dao.AlbumDao;
import com.mds.aiotplayer.cm.dao.ContentTemplateDao;
import com.mds.aiotplayer.cm.dao.ContentTypeDao;
import com.mds.aiotplayer.cm.dao.GalleryDao;
import com.mds.aiotplayer.cm.dao.GalleryMappingDao;
import com.mds.aiotplayer.cm.dao.GallerySettingDao;
import com.mds.aiotplayer.cm.dao.MetadataDao;
import com.mds.aiotplayer.cm.dao.MimeTypeDao;
import com.mds.aiotplayer.cm.dao.MimeTypeGalleryDao;
import com.mds.aiotplayer.cm.dao.UiTemplateDao;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.cm.model.ContentTemplate;
import com.mds.aiotplayer.cm.model.ContentType;
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.cm.model.GallerySetting;
import com.mds.aiotplayer.cm.model.Metadata;
import com.mds.aiotplayer.cm.model.MimeType;
import com.mds.aiotplayer.cm.model.MimeTypeGallery;
import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.core.MDSDataSchemaVersion;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.UiTemplateType;
import com.mds.aiotplayer.core.UserAction;
import com.mds.aiotplayer.sys.dao.AppSettingDao;
import com.mds.aiotplayer.sys.dao.MenuFunctionDao;
import com.mds.aiotplayer.sys.dao.MenuFunctionPermissionDao;
import com.mds.aiotplayer.sys.dao.PermissionDao;
import com.mds.aiotplayer.sys.dao.UserDao;
import com.mds.aiotplayer.sys.model.AppSetting;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.model.MenuFunctionPermission;
import com.mds.aiotplayer.sys.model.Permission;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Contains functionality for seeding the gallery database.
/// </summary>
@Service
public class SeedManager
{
	private static String DefaultTmplName = "Default";
	private static MDSDataSchemaVersion DataSchemaVersion = MDSDataSchemaVersion.V1_0_0;
	/*private static AppSettingDao appSettingDao = SpringContextHolder.getBean(AppSettingDao.class);
	private static GalleryDao galleryDao = SpringContextHolder.getBean(GalleryDao.class);
	private static GallerySettingDao gallerySettingDao = SpringContextHolder.getBean(GallerySettingDao.class);
	private static AlbumDao albumDao = SpringContextHolder.getBean(AlbumDao.class);
	private static UiTemplateDao uiTemplateDao = SpringContextHolder.getBean(UiTemplateDao.class);
	private static ContentTemplateDao contentTemplateDao = SpringContextHolder.getBean(ContentTemplateDao.class);
	private static MimeTypeDao mimeTypeDao = SpringContextHolder.getBean(MimeTypeDao.class);
	private static MimeTypeGalleryDao mimeTypeGalleryDao = SpringContextHolder.getBean(MimeTypeGalleryDao.class);
	private static MetadataDao metadataDao = SpringContextHolder.getBean(MetadataDao.class);*/
	private AppSettingDao appSettingDao;
	private GalleryDao galleryDao;
	private GallerySettingDao gallerySettingDao;
	private GalleryMappingDao galleryMappingDao;
	private AlbumDao albumDao;
	private UiTemplateDao uiTemplateDao;
	private ContentTemplateDao contentTemplateDao;
	private MimeTypeDao mimeTypeDao;
	private MimeTypeGalleryDao mimeTypeGalleryDao;
	private MetadataDao metadataDao;
	private UserDao userDao;
	private PermissionDao permissionDao;
	private ContentTypeDao contentTypeDao;
	private MenuFunctionDao menuFunctionDao;
    private MenuFunctionPermissionDao menuFunctionPermissionDao;
	
	@Autowired
    public void setGalleryDao(final GalleryDao galleryDao) {
        this.galleryDao = galleryDao;
    }
	
	@Autowired
    public void setAppSettingDao(final AppSettingDao appSettingDao) {
        this.appSettingDao = appSettingDao;
    }
	
	@Autowired
    public void setGallerySettingDao(final GallerySettingDao gallerySettingDao) {
        this.gallerySettingDao = gallerySettingDao;
    }
	
	@Autowired
    public void setGalleryMappingDao(final GalleryMappingDao galleryMappingDao) {
        this.galleryMappingDao = galleryMappingDao;
    }
	
	@Autowired
    public void setAlbumDao(final AlbumDao albumDao) {
        this.albumDao = albumDao;
    }
	
	@Autowired
    public void setUiTemplateDao(final UiTemplateDao uiTemplateDao) {
        this.uiTemplateDao = uiTemplateDao;
    }
	
	@Autowired
    public void setContentTemplateDao(final ContentTemplateDao contentTemplateDao) {
        this.contentTemplateDao = contentTemplateDao;
    }
	
	@Autowired
    public void setMimeTypeDao(final MimeTypeDao mimeTypeDao) {
        this.mimeTypeDao = mimeTypeDao;
    }
	
	@Autowired
    public void setMimeTypeGalleryDao(final MimeTypeGalleryDao mimeTypeGalleryDao) {
        this.mimeTypeGalleryDao = mimeTypeGalleryDao;
    }
	
	@Autowired
    public void setMetadataDao(final MetadataDao metadataDao) {
        this.metadataDao = metadataDao;
    }
	
	@Autowired
    public void setUserDao(final UserDao userDao) {
        this.userDao = userDao;
    }
	
	@Autowired
    public void setPermissionDao(final PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }
	
	@Autowired
    public void setContentTypeDao(final ContentTypeDao contentTypeDao) {
        this.contentTypeDao = contentTypeDao;
    }
	
    @Autowired
    public void setMenuFunctionDao(MenuFunctionDao menuFunctionDao) {
        this.menuFunctionDao = menuFunctionDao;
    }
    
    @Autowired
    public void setMenuFunctionPermissionDao(MenuFunctionPermissionDao menuFunctionPermissionDao) {
        this.menuFunctionPermissionDao = menuFunctionPermissionDao;
    }

	/// <summary>
	/// Gets the default HTML template for the header UI template. The replacement token {PayPalCartWidget} must be replaced with the HTML
	/// for the PayPal 'view cart' widget or an empty string if not required.
	/// </summary>
	private String HeaderHtmlTmpl()
	{
			return "<nav class='mds_usernav'>\n" + 
"{{if Settings.ShowSearch}}\n" + 
" <div class='mds_searchlink mds_useroption'>\n" + 
"	<img class='mds_search_trigger' src='{{:App.SkinPath}}/images/search-l.png' alt='{{:Resource.HdrSearchButtonTt}}' title='{{:Resource.HdrSearchButtonTt}}' />\n" + 
" </div>\n" + 
"{{/if}}\n" + 
"\n" +
"{{if User.UserAlbumId > 0}}\n" +
" <div class='mds_homealbumlink mds_useroption'>\n" +
"	<a href='{{:App.CurrentPageUrl}}?aid={{:User.UserAlbumId}}' title='{{:Resource.HdrUserAlbumLinkTt}}'>\n" +
"	 <img alt='{{:Resource.HdrUserAlbumLinkTt}}' src='{{:App.SkinPath}}/images/home-l.png' title='{{:Resource.HdrUserAlbumLinkTt}}'></a></div>\n" +
"{{/if}}\n" +
"\n" +
"{PayPalCartWidget}\n" +
"\n" +
"{{if Settings.ShowLogin}}\n" +
" {{if User.IsAuthenticated}}\n" +
"	 <div class='mds_logoffLinkCtr mds_useroption'>\n" +
"		<a class='mds_logoffLink' href='javascript:void(0);' title='{{:Resource.HdrLogoutTt}}'>\n" +
"		 <img alt='{{:Resource.HdrLogoutTt}}' src='{{:App.SkinPath}}/images/logoff-l.png' title='{{:Resource.HdrLogoutTt}}'></a></div>\n" +
"	 <div class='loggedonview mds_useroption'>\n" +
"	 {{if Settings.AllowManageOwnAccount}}\n" +
"		<a id='{{:Settings.ClientId}}_userwelcome' href='{{:App.CurrentPageUrl}}?g=myaccount&aid={{:Album.Id}}' class='mds_welcome' title='{{:Resource.HdrMyAccountTt}}'>{{:User.UserName}}</a>\n" +
"	 {{else}}\n" +
"		<span id='{{:Settings.ClientId}}_userwelcome' class='mds_welcome'>{{:User.UserName}}</span>\n" +
"	 {{/if}}\n" +
"	 </div>\n" +
" {{else}}\n" +
"	{{if Settings.EnableSelfRegistration}}\n" +
"	 <div class='mds_createaccount mds_useroption'>\n" +
"		<a href='{{:App.CurrentPageUrl}}?g=createaccount' title='{{:Resource.HdrCreateAccountLinkText}}'>{{:Resource.HdrCreateAccountLinkText}}</a></div>\n" +
"	{{/if}}\n" +
"	 <div class='mds_login mds_useroption'>\n" +
"		<a href='javascript:void(0);' class='mds_login_trigger mds_addrightmargin3'>{{:Resource.HdrLoginLinkText}}</a></div>\n" +
" {{/if}}\n" +
"{{/if}}\n" +
"</nav>\n" +
"{{if Settings.Title}}\n" +
" <p class='mds_bannertext'>\n" +
" {{if Settings.TitleUrl}}<a title='{{:Settings.TitleUrlTt}}' href='{{:Settings.TitleUrl}}'>{{:Settings.Title}}</a>{{else}}{{:Settings.Title}}{{/if}}</p>\n" +
"{{/if}}";
		}

		/// <summary>
		/// Gets the default JavaScript template for the header UI template. The replacement token {PayPalCartJs} must be replaced with the JavaScript
		/// for the PayPal 'view cart' widget or an empty string if not required.
		/// </summary>
		private String HeaderJsTmpl()
		{
				return "// Call the mdsHeader plug-in, which adds the HTML to the page and then configures it\n" +
"$('#{{:Settings.HeaderClientId}}').mdsHeader('{{:Settings.HeaderTmplName}}', window.{{:Settings.ClientId}}.mdsData);\n" +
"{PayPalCartJs}";
		}

		/// <summary>
		/// Gets the default HTML template for the content object UI template. The replacement token {FacebookCommentWidget} must be replaced with the HTML
		/// for the Facebook Comment widget or an empty string if not required.
		/// </summary>
		private String ContentObjectHtmlTmpl()
		{
				return "<div class='mds_mvContentView'>\n" +
" <div class='mds_mvContentHeader'>\n" +
"	<div class='mds_mvContentHeaderRow'>\n" +
"	{{if Settings.ShowContentObjectNavigation}}\n" +
"	 <div class='mds_mvContentHeaderCell mds_mvPrevCell'>\n" +
"		<a href='{{: ~prevUrl() }}'><img src='{{:App.SkinPath}}/images/arrow-left-l.png' class='mds_mvPrevBtn' alt='{{:Resource.MoPrev}}' title='{{:Resource.MoPrev}}' /></a>\n" +
"	 </div>\n" +
"	{{/if}}\n" +
"	 <div class='mds_mvContentHeaderCell mds_mvToolbarCell'>\n" +
"		<span class='mds_mvToolbar ui-widget-header ui-corner-all'>\n" +
"		<button class='mds_mvTbEmbed'>{{:Resource.MoTbEmbed}}</button>\n" +
"		{{if Settings.SlideShowType == 'Inline'}}\n" +
"		<input type='checkbox' id='{{:Settings.ClientId}}_slideshow' class='mds_mvTbSlideshow' /><label for='{{:Settings.ClientId}}_slideshow' class='mds_mvTbSlideshowLbl'>{{:Resource.MoTbSsStart}}</label>\n" +
"		{{else}}\n" +
"		<button class='mds_mvTbSlideshow mds_mvTbSlideshowLbl'>{{:Resource.MoTbSsStart}}</button>\n" +
"		{{/if}}\n" +
"		<button class='mds_mvTbMove'>{{:Resource.MoTbMove}}</button>\n" +
"		<button class='mds_mvTbCopy'>{{:Resource.MoTbCopy}}</button>\n" +
"		<button class='mds_mvTbRotate'>{{:Resource.MoTbRotate}}</button>\n" +
"		<button class='mds_mvTbDelete'>{{:Resource.MoTbDelete}}</button>\n" +
"		</span>\n" +
"	 </div>\n" +
"	{{if Settings.ShowContentObjectIndexPosition}}\n" +
"	 <div class='mds_mvContentHeaderCell mds_mvPosition'>\n" +
"		(<span class='mds_mvPositionIdx'>{{:MediaItem.Index}}</span> {{:Resource.MoPosSptr}} <span class='mds_mvPosAlbumCount'>{{:Album.NumMediaItems}}</span>)\n" +
"	 </div>\n" +
"	{{/if}}\n" +
"	{{if Settings.ShowContentObjectNavigation}}\n" +
"	 <div class='mds_mvContentHeaderCell mds_mvNextCell'>\n" +
"		<a href='{{: ~nextUrl() }}'><img src='{{:App.SkinPath}}/images/arrow-right-l.png' class='mds_mvNextBtn' alt='{{:Resource.MoNext}}' title='{{:Resource.MoNext}}' /></a>\n" +
"	 </div>\n" +
"	{{/if}}\n" +
" </div>\n" +
"</div>\n" +
"\n" +
"<div class='mds_moContainer'>\n" +
"{{:MediaItem.Views[MediaItem.ViewIndex].HtmlOutput}}</div>\n" +
"{{if Settings.ShowContentObjectTitle}}\n" +
"<div class='mds_contentObjectTitle'>{{:MediaItem.Title}}</div>\n" +
"{{/if}}\n" +
"</div>\n" +
"\n" +
"{FacebookCommentWidget}\n" +
"\n" +
"<div class='mds_mo_share_dlg mds_dlg'>\n" +
" <p class='mds_mo_share_dlg_t'>{{:Resource.MoShare}}</p>\n" +
"{{if Settings.AllowDownload || Settings.AllowZipDownload}}\n" +
" <p class='mds_mo_share_dlg_s'>{{:Resource.MoShareDwnld}}</p>\n" +
"{{/if}}\n" +
" <p class='mds_addleftmargin5'>\n" +
"{{if Settings.AllowDownload}}\n" +
" <select class='mds_mo_share_dlg_ipt mds_mo_share_dlg_ipt_select'>\n" +
"	<option value='1'>{{:Resource.MoShareSlctThmb}}</option>\n" +
"	<option value='2' selected='selected'>{{:Resource.MoShareSlctOpt}}</option>\n" +
"	{{if Album.Permissions.ViewOriginalContentObject}}\n" +
"	<option value='3'>{{:Resource.MoShareSlctOrg}}</option>\n" +
"	{{/if}}\n" +
" </select><a href='{{: ~getContentUrl(MediaItem.Id, true) }}' class='mds_mo_share_dwnld'>{{:Resource.MoShareDwnldFile}}</a>\n" +
"{{/if}}\n" +
"{{if Settings.AllowZipDownload}}\n" +
" <a href='{{: ~getDownloadUrl(Album.Id) }}' class='mds_mo_share_dwnld_zip' title='{{:Resource.MoShareDwnldZipTt}}'>{{:Resource.MoShareDwnldZip}}</a>\n" +
"{{/if}}\n" +
" </p>\n" +
" <p class='mds_mo_share_dlg_s'>{{:Resource.MoShareThisPage}}</p>\n" +
" <p class='mds_addleftmargin5'><input type='text' class='mds_mo_share_dlg_ipt mds_mo_share_dlg_ipt_url' value='{{: ~getContentUrl(MediaItem.Id, true) }}' /></p>\n" +
" <p class='mds_mo_share_dlg_s'>{{:Resource.MoShareHtml}}</p>\n" +
" <p class='mds_addleftmargin5'><textarea class='mds_mo_share_dlg_ipt mds_mo_share_dlg_ipt_embed'>{{: ~getEmbedCode() }}</textarea></p>\n" +
"</div>";
		}

		/// <summary>
		/// Gets the default JavaScript template for the content object UI template. The replacement token {FacebookJs} must be replaced with the JavaScript
		/// required to interact with the Facebook API or an empty string if not required.
		/// </summary>
		private String ContentObjectJsTmpl()
		{
				return "// Call the mdsContent plug-in, which adds the HTML to the page and then configures it\n" +
"$('#{{:Settings.ContentClientId}}').mdsContent('{{:Settings.ContentTmplName}}', window.{{:Settings.ClientId}}.mdsData);\n" +
"{FacebookJs}";
		}

		/// <summary>
		/// Gets the default HTML template for the left pane UI template. The replacement tokens {TagTrees} and {TagClouds} 
		/// must be replaced with the HTML for the tag trees and tag clouds or an empty string if not required.
		/// </summary>
		private String LeftPaneHtmlTmpl()
		{
				return "<div id='{{:Settings.ClientId}}_lptv' class='mds_lpalbumtree'></div>\n" +
"{{if App.WaitingForApprovalUrl}}<p class='mds_lpapproval'><a href='{{:App.WaitingForApprovalUrl}}' class='jstree-anchor'><i class='jstree-icon'></i>{{:Resource.LpWaitingForApproval}}</a></p>{{/if}}\n" +		
"{{if App.LatestUrl}}<p class='mds_lplatest'><a href='{{:App.LatestUrl}}' class='jstree-anchor'><i class='jstree-icon'></i>{{:Resource.LpRecent}}</a></p>{{/if}}\n" +
"{{if App.TopRatedUrl}}<p class='mds_lptoprated'><a href='{{:App.TopRatedUrl}}' class='jstree-anchor'><i class='jstree-icon'></i>{{:Resource.LpTopRated}}</a></p>{{/if}}\n" +
"{TagTrees}\n" +
"{TagClouds}";
		}

		/// <summary>
		/// Gets the default JavaScript template for the left pane UI template. The replacement tokens {TagTrees} and {TagClouds} 
		/// must be replaced with the JavaScript for the tag trees and tag clouds or an empty string if not required.
		/// </summary>
		private String LeftPaneJsTmpl()
		{
				return "// Render the left pane if it exists\n" +
"var $lp = $('#{{:Settings.LeftPaneClientId}}');\n" +
"\n" +
"if ($lp.length > 0) {\n" +
" $lp.html( $.render [ '{{:Settings.LeftPaneTmplName}}' ]( window.{{:Settings.ClientId}}.mdsData ));\n" +
"\n" +
" var options = {\n" +
"  albumIdsToSelect: [{{:Album.Id}}],\n" +
"  navigateUrl: '{{:App.CurrentPageUrl}}'\n" +
" };\n" +
"\n" +
" // Call the mdsTreeView plug-in, which adds an album treeview\n" +
"$('#{{:Settings.ClientId}}_lptv').mdsTreeView(window.{{:Settings.ClientId}}.mdsAlbumTreeData, options);\n" +
"}\n" +
"{TagTrees}\n" +
"{TagClouds}";
		}

		/// <summary>
		/// Gets the default HTML template for the right pane UI template. The replacement tokens {PayPalAddToCartWidget} and {FacebookLikeWidget} 
		/// must be replaced with the HTML for the PayPal 'add to cart' widget and the Facebook Like widget or an empty string if not required.
		/// </summary>
		private String RightPaneHtmlTmpl()
		{
				return "{PayPalAddToCartWidget}{FacebookLikeWidget}<table class='mds_meta'>\n" +
"{{if Album.VirtualType != 1 && MediaItem != null}}\n" +
" <tr class='mds_m1Row'><td colspan='2'>{{:Resource.AbmPfx}} <a href='{{: ~getAlbumUrl(MediaItem.AlbumId) }}'>{{:MediaItem.AlbumTitle}}</a></td></tr>\n" +
"{{/if}}\n" +
"{{for ActiveMetaItems}}\n" +
"{{if MTypeId == 113 || MTypeId == 29}}\n" +
" <tr class='mds_m1Row mds_mRowHdr'><td colspan='2' class='mds_k'>{{:Desc}}</td></tr>\n" +
" <tr class='mds_m1Row mds_mRowDtl' data-id='{{:Id}}' data-iseditable='{{:IsEditable}}'><td colspan='2' class='mds_v'>{{:Value}}</td></tr>\n" +
" {{else MTypeId == 114 || MTypeId == 41}}\n" +
" <tr class='mds_m1Row mds_mRowHdr'><td colspan='2' class='mds_k'>{{:Desc}}</td></tr>\n" +
" <tr class='mds_m1Row mds_mRowDtl' data-id='{{:Id}}' data-iseditable='{{:IsEditable}}'><td colspan='2' class='mds_v mds_mCaption'>{{:Value}}</td></tr>\n" +
"{{else MTypeId == 42 || MTypeId == 22}}\n" +
" <tr class='mds_m1Row mds_mRowHdr'><td colspan='2' class='mds_k'>{{:Desc}}</td></tr>\n" +
" <tr class='mds_m1Row mds_mRowDtl' data-id='{{:Id}}' data-iseditable='{{:IsEditable}}'><td colspan='2' class='mds_v {{if MTypeId == 22}}mds_mtag{{else}}mds_mpeople{{/if}}'>{{:Value}}</td></tr>\n" +
"{{else MTypeId == 112}}\n" +
" <tr class='mds_m1Row mds_mRowHdr'><td colspan='2' class='mds_k'>{{:Desc}}</td></tr>\n" +
" <tr class='mds_m1Row mds_mRowDtl' data-id='{{:Id}}' data-iseditable='{{:IsEditable}}'><td colspan='2' class='mds_v mds_mCaption'>{{:Value}}</td></tr>\n" +
"{{else MTypeId == 26}}\n" +
" <tr class='mds_m2Row' data-id='{{:Id}}' data-iseditable='{{:IsEditable}}'><td class='mds_k'>{{:Desc}}:</td><td class='mds_v mds_mrating'><div class='mds_rating' data-rateit-value='{{:Value}}'></div></td></tr>\n" +
"{{else}}\n" +
" <tr class='mds_m2Row' data-id='{{:Id}}' data-iseditable='{{:IsEditable}}'><td class='mds_k'>{{:Desc}}:</td><td class='mds_v'>{{:Value}}</td></tr>\n" +
"{{/if}}\n" +
"{{/for}}\n" +
"</table>";
		}

		/// <summary>
		/// Gets the default JavaScript template for the right pane UI template. The replacement tokens {PayPalAddToCartJs} and {FacebookJs} 
		/// must be replaced with the JavaScript for the PayPal 'add to cart' widget and the Facebook API or an empty string if not required.
		/// </summary>
		private String RightPaneJsTmpl()
		{
				return "var options = {\n" +
" tmplName : '{{:Settings.RightPaneTmplName}}'\n" +
"};\n" +
"\n" +
"$('#{{:Settings.RightPaneClientId}}').mdsMeta({{:Settings.ClientId}}.mdsData, options);\n" +
"{PayPalAddToCartJs}{FacebookJs}";
		}

		/// <summary>
		/// Gets the JavaScript required to interact with the Facebook API. Includes additional script to activate Facebook each time the
		/// next and previous functions are invoked when browsing through content objects.
		/// </summary>
		private String FacebookJs()
		{
				return "\n" +
"(function(d, s, id) {\n" +
"	var js, fjs = d.getElementsByTagName(s)[0];\n" +
"	if (d.getElementById(id)) return;\n" +
"	js = d.createElement(s); js.id = id;\n" +
"	js.src = '//connect.facebook.net/en_US/all.js#xfbml=1';\n" +
"	fjs.parentNode.insertBefore(js, fjs);\n" +
"}(document, 'script', 'facebook-jssdk'));\n" +
"\n" +
"$('#{{:Settings.ContentClientId}}').on('next.{{:Settings.ClientId}} previous.{{:Settings.ClientId}}', function() {\n" +
" if (typeof (FB) != 'undefined') FB.XFBML.parse();\n" +
"});";
		}
		
		@Transactional
		public void removeAll(){
			appSettingDao.removeAll();
			galleryDao.removeAll();
			albumDao.removeAll();
			metadataDao.removeAll();
			gallerySettingDao.removeAll();
			galleryMappingDao.removeAll();
			mimeTypeDao.removeAll();
			contentTemplateDao.removeAll();
			uiTemplateDao.removeAll();
			permissionDao.removeAll();
			contentTypeDao.removeAll();
		}

		/// <summary>
		/// Inserts the seed data into the MDS System tables. This will reset all data to their default values.
		/// </summary>
		/// <param name="ctx">The data context.</param>
		@Transactional
		public void insertSeedData(){			
			User user = userDao.findByLoginName("admin");
			InsertAppSettings();
			Gallery gallery = InsertGalleries(user.getUsername());
			InsertAlbums(gallery, user.getUsername());
			InsertMetadata();
			InsertGallerySettings(user);
			InsertMimeTypes();
			InsertMimeTypeGalleries();
			InsertContentTemplates();
			InsertUiTemplates();
			InsertUiTemplateAlbums();
			InsertPermissions(user.getUsername());
			InsertContentType();
		}
		
		/// <summary>
		/// Verify there are gallery settings for the current gallery that match every template gallery setting, creating any
		/// if necessary.
		/// </summary>
		public void configureGallerySettingsTable(GalleryBo gallery){
			boolean foundTmplGallerySettings = false;
			// Loop through each template gallery setting.
			Searchable searchable = Searchable.newSearchable();
			searchable.addSearchFilter("gallery.isTemplate", SearchOperator.eq, true);
			List<GallerySetting> gallerySettings = gallerySettingDao.findAll(searchable);
			if (!gallerySettings.isEmpty()) {
				foundTmplGallerySettings = true;
				searchable = Searchable.newSearchable();
	    		searchable.addSearchFilter("gallery.id", SearchOperator.eq, gallery.getGalleryId());
	    		List<GallerySetting> gsThises = gallerySettingDao.findAll(searchable);
	    		Gallery g = galleryDao.get(gallery.getGalleryId());
				for (GallerySetting gsTmpl : gallerySettings){
					if (!gsThises.stream().anyMatch(gs -> StringUtils.equals(gs.getSettingName(), gsTmpl.getSettingName()))){
						// This gallery is missing an entry for a gallery setting. Create one by copying it from the template gallery.
						GallerySetting gs = new GallerySetting();
						gs.setGallery(g);
						gs.setSettingName(gsTmpl.getSettingName());
						if (gsTmpl.getSettingName().equalsIgnoreCase("ContentObjectPath")){
							gs.setSettingValue(gsTmpl.getSettingValue().replace("{UUID}", UUID.randomUUID().toString().replace("-", "")).replace("{Gallery}", gallery.getName()));
						}else {
							 gs.setSettingValue(gsTmpl.getSettingValue());
						}
						gallerySettingDao.save(gs);
					}
	    		}
			}


			if (!foundTmplGallerySettings){
				// If there weren't *any* template gallery settings, insert the seed data. Generally this won't be necessary, but it
				// can help recover from certain conditions, such as when a SQL Server connection is accidentally specified without
				// the MultipleActiveResultSets keyword (or it was false). In this situation the galleries are inserted but an error 
				// prevents the remaining data from being inserted. Once the user corrects this and tries again, this code can run to
				// finish inserting the seed data.
				insertSeedData();
			}
		}

		/// <summary>
		/// Adds or updates the templates available to Enterprise license holders. This includes Facebook and PayPal templates.
		/// Any existing enterprise templates are replaced. Note that only UI templates associated with the template gallery are
		/// updated. The calling code must ensure that these templates are propagated to the remaining galleries.
		/// </summary>
		public void InsertEnterpriseTemplates()
		{
			InsertLeftPaneEnterpriseTemplates();

			InsertFacebookTemplates();

			InsertPayPalTemplates();
		}
		
		private void InsertContentType(){
			ContentType[] contenttypes = new ContentType[]{
				new ContentType(0, "Image", "Image", "|.JPG|.JPEG|.BMP|.GIF|.PNG|TIFF|.WMF|.EMF|.PCX|", 0, 1, 0, ""),
				new ContentType(1, "Video file", "Video file", "|.DAT|.WMV|.WMA|.AVI|.MPG|.MPEG|.VOB|.ASF|.RM|.MOV|.HDMOV|.MP4|.DV|.FLV|.FLC|.FLI|", 1, 2, 0, ""),
				new ContentType(3, "PowerPoint", "PowerPoint", "|.PPT|.PPS|.POT|.PPTX|.PPTM|.PPSX|.PPSM|",  3, 4, 0, ""),
				new ContentType(4, "Web Page", "Web Page", "|.HTM|.HTML|",  4, 8, 0, ""),
				new ContentType(5, "Flash", "Flash", "|.SWF|", 5, 16, 0, ""),
				new ContentType(6, "TV Capture", "TV Capture", "", 6, 32, 0, ""),
				new ContentType(7, "Text", "Text", "", 2, 64, 0, ""),
				new ContentType(8, "Media Streaming", "Media Streaming", "", 7, 128, 0, ""),
				new ContentType(9, "Live Desktop Streaming", "Live Desktop Streaming", "", 8, 256, 0, ""),
				new ContentType(10, "Clock", "Clock", "", 9, 512, 0, ""),
				new ContentType(11, "Web Camera", "Web Camera", "", 10, 1024, 0, ""),
				new ContentType(12, "DDE Live Data", "DDE Live Data", "", 11, 2048, 31, ""),
				new ContentType(13, "Weather", "Weather", "", 12, 4096, 0, ""),
				new ContentType(14, "Mixed Contents", "Mixed Contents", "", 13, 8192, 31, ""),
				new ContentType(15, "Explorer", "Explorer", "", 14, 16384, 0, ""),
				new ContentType(16, "Add-on text", "Add-on text", "", 15, 32768, 0, ""),
				new ContentType(17, "Event", "Event", "", 16, 65536, 0, ""),
				new ContentType(18, "Plugin", "Extension", "", 17, 131072, 0, ""),
				new ContentType(19, "Carousel SlideShow", "Carousel SlideShow", "", 18, 262144, 0, ""),
				new ContentType(20, "Queue", "Queue", "", 19, 524288, 0, ""),
				new ContentType(21, "Site Playlist", "Site Playlist", "", 20, 1048576, 31, ""),
				new ContentType(22, "LightBox", "LightBox", "", 21, 2097152, 0, ""),
				new ContentType(23, "Windows Media", "Windows Media", "|.AAC|.ASF|.AVI|.M4A|.MP3|.MP4|.WAV|.WMA|.WMV|.3GP|.3G2|", -1, 0, 0, ""),
				new ContentType(24, "QuickTime", "QuickTime", "|.MOV|.HDMOV|.MP4|", -1, 0, 0, ""),
				new ContentType(25, "Audio file", "Audio file", "|.MP3|.WMA|.WAV|.MID|.MPG|.MPEG|.ASF|.RM|.DAT|.WMV|.AVI|", -1, 0, 0, ""),
				new ContentType(26, "PDF File", "PDF File", "|.PDF|", 22, 16777216, 0, ""),
				new ContentType(27, "Animation Elements", "Animation Elements", "|.XML|", 23, 4194304, 0, "AMElements"),
				new ContentType(28, "Animation Contents", "Animation Contents", "|.XML|", 24, 8388608, 579, "AMContents"),
				new ContentType(29, "RSS Feed", "RSS Feed", "", 25, 33554432, 0, "RSSFeed"),
				new ContentType(102, "Catalogue", "Catalogue", "|.MDS|", -1, 0, 12582911, ""),
				new ContentType(108, "Emergency Message", "Emergency Message", "", -1, 0, 6143, ""),
			};
			contentTypeDao.save(Arrays.asList(contenttypes));
		}

		private void InsertAppSettings()
		{
			AppSetting[] appSettings = new AppSetting[]
							    {
								    new AppSetting("Skin", "light"),
								    new AppSetting("ContentObjectDownloadBufferSize", "32768"),
								    new AppSetting("EncryptContentObjectUrlOnClient", "False"),
								    new AppSetting("EncryptionKey", "mNU-h7:5f_)3=c%@^}#U9Tn*"),
								    new AppSetting("JQueryScriptPath", "//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"),
								    new AppSetting("JQueryMigrateScriptPath", "//code.jquery.com/jquery-migrate-1.2.1.js"),
								    new AppSetting("JQueryUiScriptPath", "//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"),
								    new AppSetting("MembershipProviderName", ""),
								    new AppSetting("RoleProviderName", ""),
								    new AppSetting("ProductKey", ""),
								    new AppSetting("EnableCache", "True"),
								    new AppSetting("AllowGalleryAdminToManageUsersAndRoles", "True"),
								    new AppSetting("AllowGalleryAdminToViewAllUsersAndRoles", "True"),
								    new AppSetting("MaxNumberErrorItems", "200"),
								    new AppSetting("EmailFromName", "MDS System"),
								    new AppSetting("EmailFromAddress", "chinamds@hotmail.com"),
								    new AppSetting("EmailServerType", "notspecified"),
								    new AppSetting("SmtpServer", ""),
								    new AppSetting("SmtpDomail", ""),
								    new AppSetting("SmtpServerPort", ""),
								    new AppSetting("SendEmailUsingSsl", "False"),
								    new AppSetting("ApprovalSwitch", "notspecified"),
								    new AppSetting("EnableVerificationCode", "False"),
								    new AppSetting("IndependentSpaceForDailyList", "True"),
								    new AppSetting("DataSchemaVersion", MDSDataSchemaVersion.convertMDSDataSchemaVersionToString(DataSchemaVersion)),
							    };

			appSettingDao.save(Arrays.asList(appSettings));
		}

		private Gallery InsertGalleries(String currentUser)
		{
			Searchable searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("isTemplate", SearchOperator.eq, true);
	        if (!galleryDao.findAny(searchable)) {
	        	galleryDao.save(new Gallery("Template Gallery", "", true, currentUser)); //, DateAdded = DateTime.UtcNow
	        }

			searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("isTemplate", SearchOperator.eq, false);
			// Need to add a non-template datacenter
	        Gallery gallery = galleryDao.findOne(searchable);
	        if (gallery == null) {
	        	gallery = galleryDao.save(new Gallery("Gallery", "", false, currentUser)); //, DateAdded = DateTime.UtcNow
	        }
	        
	        return gallery;
		}

		private void InsertAlbums(Gallery gallery, String currentUser)
		{
			Searchable searchable = Searchable.newSearchable();
			searchable.addSearchFilter("gallery.id", SearchOperator.eq, gallery.getId());
			searchable.addSearchFilter("parent", SearchOperator.eq, null);
			if (!albumDao.findAny(searchable)) {
				Album rootAlbum = new Album(
									gallery,
									null,
									null,
									"",
									MetadataItemName.DateAdded,
									true,
									0,
									Calendar.getInstance().getTime(),
									Calendar.getInstance().getTime(),
									"admin",
									"admin",
									false,
									currentUser
								);
				
				rootAlbum.getMetadatas().add(new Metadata(null, rootAlbum, MetadataItemName.Caption, "", "{album.root_Album_Default_Summary}"));
				rootAlbum.getMetadatas().add(new Metadata(null, rootAlbum, MetadataItemName.Title, "", "{album.root_Album_Default_Title}"));
	
				albumDao.save(rootAlbum);
			}

			// NOTE: The title & summary for this album are validated and inserted if necessary in the function InsertMetadata().

			// Add the title
			//ctx.Metadatas.AddOrUpdate(m => new { m.FKAlbumId, m.MetaName }, new MetadataDto
			//	{
			//		FKAlbumId = rootAlbum.AlbumId,
			//		MetaName = MetadataItemName.Title,
			//		Value = "All albums"
			//	});

			//// Add the caption
			//ctx.Metadatas.AddOrUpdate(m => new { m.FKAlbumId, m.MetaName }, new MetadataDto
			//	{
			//		FKAlbumId = rootAlbum.AlbumId,
			//		MetaName = MetadataItemName.Caption,
			//		Value = "Welcome to MDS System!"
			//	});
		}

		private void InsertGallerySettings(User user)
		{
			List<Gallery> galleries = galleryDao.getAll();
			for (Gallery gallery : galleries){
				galleryMappingDao.save(new GalleryMapping(gallery, null, null));
				if (gallery.isIsTemplate()) {
					InsertGallerySetting(new GallerySetting(gallery, "ContentObjectPath", "mds\\m_{UUID}"));
				}else {
					InsertGallerySetting(new GallerySetting(gallery, "ContentObjectPath", "mds\\m_" + UUID.randomUUID().toString().replace("-", "")));
				}
				InsertGallerySetting(new GallerySetting(gallery, "ThumbnailPath", ""));
				InsertGallerySetting(new GallerySetting(gallery, "OptimizedPath", ""));
				InsertGallerySetting(new GallerySetting(gallery, "ContentObjectPathIsReadOnly", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "ShowHeader", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "GalleryTitle", "MDS System"));
				InsertGallerySetting(new GallerySetting(gallery, "GalleryTitleUrl", "~/"));
				InsertGallerySetting(new GallerySetting(gallery, "ShowLogin", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "ShowSearch", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "ShowErrorDetails", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "EnableExceptionHandler", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultAlbumDirectoryNameLength", "25"));
				InsertGallerySetting(new GallerySetting(gallery, "SynchAlbumTitleAndDirectoryName", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultAlbumSortMetaName", "111"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultAlbumSortAscending", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "EmptyAlbumThumbnailBackgroundColor", "#353535"));
				InsertGallerySetting(new GallerySetting(gallery, "EmptyAlbumThumbnailText", "Empty"));
				InsertGallerySetting(new GallerySetting(gallery, "EmptyAlbumThumbnailFontName", "Verdana"));
				InsertGallerySetting(new GallerySetting(gallery, "EmptyAlbumThumbnailFontSize", "13"));
				InsertGallerySetting(new GallerySetting(gallery, "EmptyAlbumThumbnailFontColor", "White"));
				InsertGallerySetting(new GallerySetting(gallery, "EmptyAlbumThumbnailWidthToHeightRatio", "1.33"));
				InsertGallerySetting(new GallerySetting(gallery, "MaxThumbnailTitleDisplayLength", "50"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowUserEnteredHtml", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowUserEnteredJavascript", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowedHtmlTags", "p,a,div,span,br,ul,ol,li,table,tr,td,th,h1,h2,h3,h4,h5,h6,strong,b,em,i,u,cite,blockquote,address,pre,hr,img,dl,dt,dd,code,tt"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowedHtmlAttributes", "href,class,style,id,src,title,alt,target,name"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowCopyingReadOnlyObjects", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowManageOwnAccount", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowDeleteOwnAccount", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "ContentObjectTransitionType", "Fade"));
				InsertGallerySetting(new GallerySetting(gallery, "ContentObjectTransitionDuration", "0.2"));
				InsertGallerySetting(new GallerySetting(gallery, "SlideshowInterval", "4000"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowUnspecifiedMimeTypes", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "ImageTypesStandardBrowsersCanDisplay", ".jpg,.jpeg,.gif,.png"));
				InsertGallerySetting(new GallerySetting(gallery, "ImageMagickFileTypes", ".pdf,.txt,.eps,.psd,.tif,.tiff,.png"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowAnonymousRating", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "ExtractMetadata", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "ExtractMetadataUsingWpf", "True"));
                InsertGallerySetting(new GallerySetting(gallery, "MetadataDisplaySettings", "[{'MetadataItem':29,'Name':'Title','DisplayName':'TITLE','IsVisibleForAlbum':true,'IsVisibleForContentObject':true,'IsEditable':true,'DefaultValue':'{Title}','Sequence':0},{'MetadataItem':41,'Name':'Caption','DisplayName':'REMARK','IsVisibleForAlbum':true,'IsVisibleForContentObject':true,'IsEditable':true,'DefaultValue':'{Comment}','Sequence':1},{'MetadataItem':22,'Name':'Tags','DisplayName':'TAGS','IsVisibleForAlbum':true,'IsVisibleForContentObject':true,'IsEditable':true,'DefaultValue':'{Tags}','Sequence':2},{'MetadataItem':42,'Name':'People','DisplayName':'PEOPLE','IsVisibleForAlbum':true,'IsVisibleForContentObject':true,'IsEditable':true,'DefaultValue':'{People}','Sequence':3},{'MetadataItem':112,'Name':'HtmlSource','DisplayName':'SOURCE HTML','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':true,'DefaultValue':'{HtmlSource}','Sequence':4},{'MetadataItem':46,'Name':'ApprovalStatus','DisplayName':'APPROVAL STATUS','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{ApprovalStatus}','Sequence':5},{'MetadataItem':47,'Name':'Approval','DisplayName':'APPROVAL','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{Approval}','Sequence':6},{'MetadataItem':48,'Name':'ApprovalDate','DisplayName':'DATE OF APPROVAL','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{ApprovalDate}','Sequence':7},{'MetadataItem':44,'Name':'Player','DisplayName':'PLAYERS','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{Player}','Sequence':8},{'MetadataItem':34,'Name':'FileName','DisplayName':'File name','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{FileName}','Sequence':9},{'MetadataItem':35,'Name':'FileNameWithoutExtension','DisplayName':'File name','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{FileNameWithoutExtension}','Sequence':10},{'MetadataItem':111,'Name':'DateAdded','DisplayName':'Date Added','IsVisibleForAlbum':true,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{DateAdded}','Sequence':11},{'MetadataItem':8,'Name':'DatePictureTaken','DisplayName':'Date photo taken','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{DatePictureTaken}','Sequence':12},{'MetadataItem':26,'Name':'Rating','DisplayName':'Rating','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':true,'DefaultValue':'{Rating}','Sequence':13},{'MetadataItem':102,'Name':'GpsLocationWithMapLink','DisplayName':'Geotag','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'<a href=\\'http://maps.google.com/maps?q={GpsLatitude},{GpsLongitude}\\' target=\\'_blank\\' title=\\'View map\\'>{GpsLocation}</a>','Sequence':14},{'MetadataItem':106,'Name':'GpsDestLocationWithMapLink','DisplayName':'Geotag','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'<a href=\\'http://maps.google.com/maps?q={GpsLatitude},{GpsLongitude}\\' target=\\'_blank\\' title=\\'View map\\'>{GpsLocation}</a>','Sequence':15},{'MetadataItem':43,'Name':'Orientation','DisplayName':'Orientation','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{Orientation}','Sequence':16},{'MetadataItem':14,'Name':'ExposureProgram','DisplayName':'Exposure program','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{ExposureProgram}','Sequence':17},{'MetadataItem':9,'Name':'Description','DisplayName':'Description','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{Description}','Sequence':18},{'MetadataItem':5,'Name':'Comment','DisplayName':'Comment','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{Comment}','Sequence':19},{'MetadataItem':28,'Name':'Subject','DisplayName':'Subject','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{Subject}','Sequence':20},{'MetadataItem':2,'Name':'Author','DisplayName':'Author','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{Author}','Sequence':21},{'MetadataItem':4,'Name':'CameraModel','DisplayName':'Camera model','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{CameraModel}','Sequence':22},{'MetadataItem':6,'Name':'ColorRepresentation','DisplayName':'Color representation','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{ColorRepresentation}','Sequence':23},{'MetadataItem':7,'Name':'Copyright','DisplayName':'Copyright','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{Copyright}','Sequence':24},{'MetadataItem':12,'Name':'EquipmentManufacturer','DisplayName':'Camera maker','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{EquipmentManufacturer}','Sequence':25},{'MetadataItem':13,'Name':'ExposureCompensation','DisplayName':'Exposure compensation','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{ExposureCompensation}','Sequence':26},{'MetadataItem':15,'Name':'ExposureTime','DisplayName':'Exposure time','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{ExposureTime}','Sequence':27},{'MetadataItem':16,'Name':'FlashMode','DisplayName':'Flash mode','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{FlashMode}','Sequence':28},{'MetadataItem':17,'Name':'FNumber','DisplayName':'F-stop','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{FNumber}','Sequence':29},{'MetadataItem':18,'Name':'FocalLength','DisplayName':'Focal length','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{FocalLength}','Sequence':30},{'MetadataItem':21,'Name':'IsoSpeed','DisplayName':'ISO speed','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IsoSpeed}','Sequence':31},{'MetadataItem':23,'Name':'LensAperture','DisplayName':'Aperture','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{LensAperture}','Sequence':32},{'MetadataItem':24,'Name':'LightSource','DisplayName':'Light source','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{LightSource}','Sequence':33},{'MetadataItem':10,'Name':'Dimensions','DisplayName':'Dimensions (pixels)','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{Dimensions}','Sequence':34},{'MetadataItem':25,'Name':'MeteringMode','DisplayName':'Metering mode','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{MeteringMode}','Sequence':35},{'MetadataItem':27,'Name':'SubjectDistance','DisplayName':'Subject distance','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{SubjectDistance}','Sequence':36},{'MetadataItem':11,'Name':'Duration','DisplayName':'Duration','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{Duration}','Sequence':37},{'MetadataItem':1,'Name':'AudioFormat','DisplayName':'Audio format','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{AudioFormat}','Sequence':38},{'MetadataItem':32,'Name':'VideoFormat','DisplayName':'Video format','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{VideoFormat}','Sequence':39},{'MetadataItem':3,'Name':'BitRate','DisplayName':'Bit rate','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{BitRate}','Sequence':40},{'MetadataItem':0,'Name':'AudioBitRate','DisplayName':'AudioBitRate','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{AudioBitRate}','Sequence':41},{'MetadataItem':31,'Name':'VideoBitRate','DisplayName':'VideoBitRate','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{VideoBitRate}','Sequence':42},{'MetadataItem':20,'Name':'HorizontalResolution','DisplayName':'Horizontal resolution','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{HorizontalResolution}','Sequence':43},{'MetadataItem':30,'Name':'VerticalResolution','DisplayName':'Vertical resolution','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{VerticalResolution}','Sequence':44},{'MetadataItem':33,'Name':'Width','DisplayName':'Width','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{Width}','Sequence':45},{'MetadataItem':19,'Name':'Height','DisplayName':'Height','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{Height}','Sequence':46},{'MetadataItem':36,'Name':'FileSizeKb','DisplayName':'File size','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{FileSizeKb}','Sequence':47},{'MetadataItem':37,'Name':'DateFileCreated','DisplayName':'File created','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{DateFileCreated}','Sequence':48},{'MetadataItem':38,'Name':'DateFileCreatedUtc','DisplayName':'File created (UTC)','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{DateFileCreatedUtc}','Sequence':49},{'MetadataItem':39,'Name':'DateFileLastModified','DisplayName':'File last modified','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{DateFileLastModified}','Sequence':50},{'MetadataItem':40,'Name':'DateFileLastModifiedUtc','DisplayName':'File last modified (UTC)','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{DateFileLastModifiedUtc}','Sequence':51},{'MetadataItem':101,'Name':'GpsLocation','DisplayName':'GPS location','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{GpsLocation}','Sequence':52},{'MetadataItem':103,'Name':'GpsLatitude','DisplayName':'GPS latitude','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{GpsLatitude}','Sequence':53},{'MetadataItem':104,'Name':'GpsLongitude','DisplayName':'GPS longitude','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{GpsLongitude}','Sequence':54},{'MetadataItem':105,'Name':'GpsDestLocation','DisplayName':'GPS dest. location','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{GpsDestLocation}','Sequence':55},{'MetadataItem':108,'Name':'GpsDestLongitude','DisplayName':'GPS dest. longitude','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{GpsDestLongitude}','Sequence':56},{'MetadataItem':107,'Name':'GpsDestLatitude','DisplayName':'GPS dest. latitude','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{GpsDestLatitude}','Sequence':57},{'MetadataItem':110,'Name':'GpsVersion','DisplayName':'GPS version','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{GpsVersion}','Sequence':58},{'MetadataItem':109,'Name':'GpsAltitude','DisplayName':'GPS altitude','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'{GpsAltitude}','Sequence':59},{'MetadataItem':113,'Name':'RatingCount','DisplayName':'Number of ratings','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'0','Sequence':60},{'MetadataItem':1012,'Name':'IptcOriginalTransmissionReference','DisplayName':'Transmission ref.','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcOriginalTransmissionReference}','Sequence':61},{'MetadataItem':1013,'Name':'IptcProvinceState','DisplayName':'Province/State','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcProvinceState}','Sequence':62},{'MetadataItem':1010,'Name':'IptcKeywords','DisplayName':'IptcKeywords','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcKeywords}','Sequence':63},{'MetadataItem':1011,'Name':'IptcObjectName','DisplayName':'Object name','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcObjectName}','Sequence':64},{'MetadataItem':1014,'Name':'IptcRecordVersion','DisplayName':'Record version','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcRecordVersion}','Sequence':65},{'MetadataItem':1017,'Name':'IptcSublocation','DisplayName':'Sub-location','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcSublocation}','Sequence':66},{'MetadataItem':1018,'Name':'IptcWriterEditor','DisplayName':'Writer/Editor','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcWriterEditor}','Sequence':67},{'MetadataItem':1015,'Name':'IptcSource','DisplayName':'Source','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcSource}','Sequence':68},{'MetadataItem':1016,'Name':'IptcSpecialInstructions','DisplayName':'Instructions','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcSpecialInstructions}','Sequence':69},{'MetadataItem':1003,'Name':'IptcCaption','DisplayName':'Caption','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcCaption}','Sequence':70},{'MetadataItem':1004,'Name':'IptcCity','DisplayName':'City','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcCity}','Sequence':71},{'MetadataItem':1001,'Name':'IptcByline','DisplayName':'By-line','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcByline}','Sequence':72},{'MetadataItem':1002,'Name':'IptcBylineTitle','DisplayName':'By-line title','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcBylineTitle}','Sequence':73},{'MetadataItem':1005,'Name':'IptcCopyrightNotice','DisplayName':'Copyright','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcCopyrightNotice}','Sequence':74},{'MetadataItem':1008,'Name':'IptcDateCreated','DisplayName':'Date created','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcDateCreated}','Sequence':75},{'MetadataItem':1009,'Name':'IptcHeadline','DisplayName':'Headline','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcHeadline}','Sequence':76},{'MetadataItem':1006,'Name':'IptcCountryPrimaryLocationName','DisplayName':'Country','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcCountryPrimaryLocationName}','Sequence':77},{'MetadataItem':1007,'Name':'IptcCredit','DisplayName':'Credit','IsVisibleForAlbum':false,'IsVisibleForContentObject':true,'IsEditable':false,'DefaultValue':'{IptcCredit}','Sequence':78},{'MetadataItem':2000,'Name':'Custom1','DisplayName':'Custom1','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':79},{'MetadataItem':2001,'Name':'Custom2','DisplayName':'Custom2','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':80},{'MetadataItem':2002,'Name':'Custom3','DisplayName':'Custom3','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':81},{'MetadataItem':2003,'Name':'Custom4','DisplayName':'Custom4','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':82},{'MetadataItem':2004,'Name':'Custom5','DisplayName':'Custom5','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':83},{'MetadataItem':2005,'Name':'Custom6','DisplayName':'Custom6','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':84},{'MetadataItem':2006,'Name':'Custom7','DisplayName':'Custom7','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':85},{'MetadataItem':2007,'Name':'Custom8','DisplayName':'Custom8','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':86},{'MetadataItem':2008,'Name':'Custom9','DisplayName':'Custom9','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':87},{'MetadataItem':2009,'Name':'Custom10','DisplayName':'Custom10','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':88},{'MetadataItem':2010,'Name':'Custom11','DisplayName':'Custom11','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':89},{'MetadataItem':2011,'Name':'Custom12','DisplayName':'Custom12','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':90},{'MetadataItem':2012,'Name':'Custom13','DisplayName':'Custom13','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':91},{'MetadataItem':2013,'Name':'Custom14','DisplayName':'Custom14','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':92},{'MetadataItem':2014,'Name':'Custom15','DisplayName':'Custom15','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':93},{'MetadataItem':2015,'Name':'Custom16','DisplayName':'Custom16','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':94},{'MetadataItem':2016,'Name':'Custom17','DisplayName':'Custom17','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':95},{'MetadataItem':2017,'Name':'Custom18','DisplayName':'Custom18','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':96},{'MetadataItem':2018,'Name':'Custom19','DisplayName':'Custom19','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':97},{'MetadataItem':2019,'Name':'Custom20','DisplayName':'Custom20','IsVisibleForAlbum':false,'IsVisibleForContentObject':false,'IsEditable':false,'DefaultValue':'','Sequence':98}]"));
				InsertGallerySetting(new GallerySetting(gallery, "MetadataDateTimeFormatString", "MMM dd, yyyy h:mm:ss a"));
				InsertGallerySetting(new GallerySetting(gallery, "EnableContentObjectDownload", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "EnableAnonymousOriginalContentObjectDownload", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "EnableContentObjectZipDownload", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "EnableAlbumZipDownload", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "EnableSlideShow", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "SlideShowType", "FullScreen"));
				InsertGallerySetting(new GallerySetting(gallery, "MaxThumbnailLength", "115"));
				InsertGallerySetting(new GallerySetting(gallery, "ThumbnailImageJpegQuality", "70"));
				InsertGallerySetting(new GallerySetting(gallery, "ThumbnailFileNamePrefix", "zThumb_"));
				InsertGallerySetting(new GallerySetting(gallery, "MaxOptimizedLength", "640"));
				InsertGallerySetting(new GallerySetting(gallery, "OptimizedImageJpegQuality", "70"));
				InsertGallerySetting(new GallerySetting(gallery, "OptimizedImageTriggerSizeKb", "50"));
				InsertGallerySetting(new GallerySetting(gallery, "OptimizedFileNamePrefix", "zOpt_"));
				InsertGallerySetting(new GallerySetting(gallery, "OriginalImageJpegQuality", "95"));
				InsertGallerySetting(new GallerySetting(gallery, "DiscardOriginalImageDuringImport", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "ApplyWatermark", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "ApplyWatermarkToThumbnails", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkText", "Copyright 2016, MDS, All Rights Reserved"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkTextFontName", "Verdana"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkTextFontSize", "13"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkTextWidthPercent", "50"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkTextColor", "White"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkTextOpacityPercent", "35"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkTextLocation", "BottomCenter"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkImagePath", "images/mds_logo.png"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkImageWidthPercent", "85"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkImageOpacityPercent", "25"));
				InsertGallerySetting(new GallerySetting(gallery, "WatermarkImageLocation", "MiddleCenter"));
				InsertGallerySetting(new GallerySetting(gallery, "SendEmailOnError", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "AutoStartContentObject", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultVideoPlayerWidth", "640"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultVideoPlayerHeight", "480"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultAudioPlayerWidth", "600"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultAudioPlayerHeight", "60"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultGenericObjectWidth", "640"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultGenericObjectHeight", "480"));
				InsertGallerySetting(new GallerySetting(gallery, "MaxUploadSize", "2097151"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowAddLocalContent", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowAddExternalContent", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "AllowAnonymousBrowsing", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "PageSize", "0"));
				InsertGallerySetting(new GallerySetting(gallery, "PagerLocation", "TopAndBottom"));
				InsertGallerySetting(new GallerySetting(gallery, "EnableSelfRegistration", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "RequireEmailValidationForSelfRegisteredUser", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "RequireApprovalForSelfRegisteredUser", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "UseEmailForAccountName", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "DefaultRolesForSelfRegisteredUser", ""));
				InsertGallerySetting(new GallerySetting(gallery, "UsersToNotifyWhenAccountIsCreated", ""));
				InsertGallerySetting(new GallerySetting(gallery, "UsersToNotifyWhenErrorOccurs", ""));
				InsertGallerySetting(new GallerySetting(gallery, "EnableUserAlbum", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "EnableUserAlbumDefaultForUser", "True"));
				InsertGallerySetting(new GallerySetting(gallery, "UserAlbumParentAlbumId", "0"));
				InsertGallerySetting(new GallerySetting(gallery, "UserAlbumNameTemplate", "{UserName}'s content"));
                InsertGallerySetting(new GallerySetting(gallery, "UserAlbumSummaryTemplate", "Welcome to our presentation of MDS SYSTEM! We offer a variety of digital signage products, such as digital signage software, live weather & RSS news banner, hotel or office room booking display management system, queuing system, office or shopping mall e-directory wayfinder kiosk system."));
				InsertGallerySetting(new GallerySetting(gallery, "RedirectToUserAlbumAfterLogin", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "VideoThumbnailPosition", "3"));
				InsertGallerySetting(new GallerySetting(gallery, "EnableAutoSync", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "AutoSyncIntervalMinutes", "1440"));
				InsertGallerySetting(new GallerySetting(gallery, "LastAutoSync", ""));
				InsertGallerySetting(new GallerySetting(gallery, "EnableRemoteSync", "False"));
				InsertGallerySetting(new GallerySetting(gallery, "RemoteAccessPassword", ""));
                InsertGallerySetting(new GallerySetting(gallery, "ContentEncoderSettings", ".mp3||.mp3||~~.flv||.flv||~~.m4a||.m4a||~~.wmv||.flv||-i \"\"{SourceFilePath}\"\" -vf \"\"scale=min(iw*min(640/iw\\,480/ih)\\,iw):min(ih*min(640/iw\\,480/ih)\\,ih){AutoRotateFilter}\"\" -ar 11025 \"\"{DestinationFilePath}\"\"~~*video||.mp4||-y -i \"\"{SourceFilePath}\"\" -vf \"\"scale=min(iw*min(640/iw\\,480/ih)\\,iw):min(ih*min(640/iw\\,480/ih)\\,ih){AutoRotateFilter}\"\" -vcodec libx264 -movflags +faststart -metadata:s:v:0 rotate=0 \"\"{DestinationFilePath}\"\"~~*video||.flv||-i \"\"{SourceFilePath}\"\" -y \"\"{DestinationFilePath}\"\"~~*video||.mp4||-y -i \"\"{SourceFilePath}\"\" -vf \"\"scale=min(iw*min(640/iw\\,480/ih)\\,iw):min(ih*min(640/iw\\,480/ih)\\,ih){AutoRotateFilter}\"\" -vcodec libx264 -movflags +faststart -metadata:s:v:0 rotate=0 -acodec copy -threads 12 \"\"{DestinationFilePath}\"\"~~*audio||.m4a||-i \"\"{SourceFilePath}\"\" -y \"\"{DestinationFilePath}\"\""));
				InsertGallerySetting(new GallerySetting(gallery, "ContentEncoderTimeoutMs", "900000"));
			}
		}
		
		private void InsertGallerySetting(GallerySetting gallerySetting){
			Searchable searchable = Searchable.newSearchable();
			searchable.addSearchFilter("gallery.id", SearchOperator.eq, gallerySetting.getGallery().getId());
			searchable.addSearchFilter("settingName", SearchOperator.eq, gallerySetting.getSettingName());
			gallerySettingDao.addOrUpdate(gallerySetting, searchable);
		}

		private void InsertMetadata(){
			// Insert default set of data for root album.
			List<Album> rootAlbumWithMissingTitle = albumDao.find("from Album a where parent=null and not exists (from a.metadatas where metaName =:p1)", new Parameter(MetadataItemName.Title));//s.FirstOrDefault(a => a.FKAlbumParentId == null && a.Metadata.All(md => md.MetaName != MetadataItemName.Title));
			if (rootAlbumWithMissingTitle != null && rootAlbumWithMissingTitle.size() > 0){
				metadataDao.save(new Metadata(null, rootAlbumWithMissingTitle.get(0), MetadataItemName.Title, "", "{album.root_Album_Default_Title}" ));
			}

			List<Album> rootAlbumWithMissingCaption = albumDao.find("from Album a where parent=null and not exists (from a.metadatas where metaName =:p1)", new Parameter(MetadataItemName.Caption)); //ctx.Albums.FirstOrDefault(a => a.FKAlbumParentId == null && a.Metadata.All(md => md.MetaName != MetadataItemName.Caption));
			if (rootAlbumWithMissingCaption != null && rootAlbumWithMissingCaption.size()>0){
				//metadataDao.save(new Metadata(null, rootAlbumWithMissingCaption.get(0), MetadataItemName.Caption, "", "Welcome to MDS System! <span class='mds_msgfriendly'>Start by <a href='?g=createaccount' style='color: #7ad199;'>creating an system admin account</a>.</span>" ));
				metadataDao.save(new Metadata(null, rootAlbumWithMissingCaption.get(0), MetadataItemName.Caption, "", "{album.root_Album_Default_Summary}" ));
			}

			//ctx.Metadatas.AddOrUpdate(m => new { m.MetaName, m.Value }, new MetadataDto { MetaName = MetadataItemName.AlbumTitle, FKAlbumId = rootAlbumId, Value = "Welcome to MDS System!" });
			//ctx.Metadatas.AddOrUpdate(m => m.MetaName, new MetadataDto { MetaName = MetadataItemName.AlbumTitle, FKAlbumId = rootAlbumId, Value = "Welcome to MDS System!" });
		}

		private void InsertMimeTypes()
		{
			InsertMimeType(new MimeType(".3gp", "video/mp4", "" ));
			InsertMimeType(new MimeType(".afl", "video/animaflex", "" ));
			InsertMimeType(new MimeType(".aif", "audio/aiff", "" ));
			InsertMimeType(new MimeType(".aifc", "audio/aiff", "" ));
			InsertMimeType(new MimeType(".aiff", "audio/aiff", "" ));
			InsertMimeType(new MimeType(".asf", "video/x-ms-asf", "" ));
			InsertMimeType(new MimeType(".asx", "video/x-ms-asf", "" ));
			InsertMimeType(new MimeType(".au", "audio/basic", "" ));
			InsertMimeType(new MimeType(".avi", "video/x-ms-wvx", "" ));
			InsertMimeType(new MimeType(".avs", "video/avs-video", "" ));
			InsertMimeType(new MimeType(".bm", "image/bmp", "" ));
			InsertMimeType(new MimeType(".bmp", "image/bmp", "" ));
			InsertMimeType(new MimeType(".chm", "application/vnd.ms-htmlhelp", "" ));
			InsertMimeType(new MimeType(".css", "text/css", "" ));
			InsertMimeType(new MimeType(".divx", "video/divx", "" ));
			InsertMimeType(new MimeType(".dl", "video/dl", "" ));
			InsertMimeType(new MimeType(".doc", "application/msword", "" ));
			InsertMimeType(new MimeType(".docm", "application/vnd.ms-word.document.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "" ));
			InsertMimeType(new MimeType(".dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template", "" ));
			InsertMimeType(new MimeType(".dot", "application/msword", "" ));
			InsertMimeType(new MimeType(".dotm", "application/vnd.ms-word.template.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".dtd", "application/xml-dtd", "text/plain" ));
			InsertMimeType(new MimeType(".dv", "video/x-dv", "" ));
			InsertMimeType(new MimeType(".dwg", "image/vnd.dwg", "" ));
			InsertMimeType(new MimeType(".dxf", "image/vnd.dwg", "" ));
			InsertMimeType(new MimeType(".emf", "image/x-emf", "" ));
			InsertMimeType(new MimeType(".eps", "image/postscript", "" ));
			InsertMimeType(new MimeType(".exe", "application/octet-stream", "" ));
			InsertMimeType(new MimeType(".f4v", "video/f4v", "" ));
			InsertMimeType(new MimeType(".fif", "image/fif", "" ));
			InsertMimeType(new MimeType(".fli", "video/fli", "" ));
			InsertMimeType(new MimeType(".flo", "image/florian", "" ));
			InsertMimeType(new MimeType(".flv", "video/x-flv", "" ));
			InsertMimeType(new MimeType(".fpx", "image/vnd.fpx", "" ));
			InsertMimeType(new MimeType(".funk", "audio/make", "" ));
			InsertMimeType(new MimeType(".g3", "image/g3fax", "" ));
			InsertMimeType(new MimeType(".gif", "image/gif", "" ));
			InsertMimeType(new MimeType(".gl", "video/gl", "" ));
			InsertMimeType(new MimeType(".htm", "text/html", "" ));
			InsertMimeType(new MimeType(".html", "text/html", "" ));
			InsertMimeType(new MimeType(".ico", "image/ico", "" ));
			InsertMimeType(new MimeType(".ief", "image/ief", "" ));
			InsertMimeType(new MimeType(".iefs", "image/ief", "" ));
			InsertMimeType(new MimeType(".it", "audio/it", "" ));
			InsertMimeType(new MimeType(".jar", "application/java-archive", "" ));
			InsertMimeType(new MimeType(".jfif", "image/jpeg", "" ));
			InsertMimeType(new MimeType(".jfif-tbnl", "image/jpeg", "" ));
			InsertMimeType(new MimeType(".jpe", "image/jpeg", "" ));
			InsertMimeType(new MimeType(".jpeg", "image/jpeg", "" ));
			InsertMimeType(new MimeType(".jpg", "image/jpeg", "" ));
			InsertMimeType(new MimeType(".js", "text/javascript", "" ));
			InsertMimeType(new MimeType(".jut", "image/jutvision", "" ));
			InsertMimeType(new MimeType(".kar", "audio/midi", "" ));
			InsertMimeType(new MimeType(".la", "audio/nspaudio", "" ));
			InsertMimeType(new MimeType(".lma", "audio/nspaudio", "" ));
			InsertMimeType(new MimeType(".m1v", "video/mpeg", "" ));
			InsertMimeType(new MimeType(".m2a", "audio/mpeg", "" ));
			InsertMimeType(new MimeType(".m2ts", "video/MP2T", "" ));
			InsertMimeType(new MimeType(".m2v", "video/mpeg", "" ));
			InsertMimeType(new MimeType(".m4a", "audio/m4a", "" ));
			InsertMimeType(new MimeType(".m4v", "video/m4v", "" ));
			InsertMimeType(new MimeType(".mcf", "image/vasa", "" ));
			InsertMimeType(new MimeType(".mht", "message/rfc822", "" ));
			InsertMimeType(new MimeType(".mid", "audio/midi", "" ));
			InsertMimeType(new MimeType(".midi", "audio/midi", "" ));
			InsertMimeType(new MimeType(".mod", "audio/mod", "" ));
			InsertMimeType(new MimeType(".moov", "video/mp4", "" ));
			InsertMimeType(new MimeType(".mov", "video/mp4", "" ));
			InsertMimeType(new MimeType(".mp2", "audio/mpeg", "application/x-mplayer2" ));
			InsertMimeType(new MimeType(".mp3", "audio/x-mp3", "" ));
			InsertMimeType(new MimeType(".mp4", "video/mp4", "" ));
			InsertMimeType(new MimeType(".mpa", "audio/mpeg", "application/x-mplayer2" ));
			InsertMimeType(new MimeType(".mpe", "video/mpeg", "" ));
			InsertMimeType(new MimeType(".mpeg", "video/mpeg", "" ));
            InsertMimeType(new MimeType(".vob", "video/mpeg2", "" ));
			InsertMimeType(new MimeType(".mpg", "video/mpeg", "" ));
			InsertMimeType(new MimeType(".mpga", "audio/mpeg", "" ));
			InsertMimeType(new MimeType(".mts", "video/MP2T", "" ));
			InsertMimeType(new MimeType(".my", "audio/make", "" ));
			InsertMimeType(new MimeType(".nap", "image/naplps", "" ));
			InsertMimeType(new MimeType(".naplps", "image/naplps", "" ));
			InsertMimeType(new MimeType(".oga", "audio/ogg", "" ));
			InsertMimeType(new MimeType(".ogg", "video/ogg", "" ));
			InsertMimeType(new MimeType(".ogv", "video/ogg", "" ));
			InsertMimeType(new MimeType(".pdf", "application/pdf", "" ));
			InsertMimeType(new MimeType(".pfunk", "audio/make", "" ));
			InsertMimeType(new MimeType(".pic", "image/pict", "" ));
			InsertMimeType(new MimeType(".pict", "image/pict", "" ));
			InsertMimeType(new MimeType(".png", "image/png", "" ));
			InsertMimeType(new MimeType(".potm", "application/vnd.ms-powerpoint.template.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".potx", "application/vnd.openxmlformats-officedocument.presentationml.template", "" ));
			InsertMimeType(new MimeType(".ppam", "application/vnd.ms-powerpoint.addin.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".pps", "application/vnd.ms-powerpoint", "" ));
			InsertMimeType(new MimeType(".ppsm", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow", "" ));
			InsertMimeType(new MimeType(".ppt", "application/vnd.ms-powerpoint", "" ));
			InsertMimeType(new MimeType(".pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "" ));
			InsertMimeType(new MimeType(".psd", "image/psd", "" ));
			InsertMimeType(new MimeType(".qcp", "audio/vnd.qcelp", "" ));
			InsertMimeType(new MimeType(".qt", "video/mp4", "" ));
			InsertMimeType(new MimeType(".ra", "audio/x-pn-realaudio", "" ));
			InsertMimeType(new MimeType(".ram", "audio/x-pn-realaudio", "" ));
			InsertMimeType(new MimeType(".ras", "image/cmu-raster", "" ));
			InsertMimeType(new MimeType(".rast", "image/cmu-raster", "" ));
			InsertMimeType(new MimeType(".rf", "image/vnd.rn-realflash", "" ));
			InsertMimeType(new MimeType(".rmi", "audio/mid", "" ));
			InsertMimeType(new MimeType(".rp", "image/vnd.rn-realpix", "" ));
			InsertMimeType(new MimeType(".rtf", "application/rtf", "" ));
			InsertMimeType(new MimeType(".rv", "video/vnd.rn-realvideo", "" ));
			InsertMimeType(new MimeType(".sgml", "text/sgml", "" ));
			InsertMimeType(new MimeType(".s3m", "audio/s3m", "" ));
			InsertMimeType(new MimeType(".snd", "audio/basic", "" ));
			InsertMimeType(new MimeType(".svf", "image/vnd.dwg", "" ));
			InsertMimeType(new MimeType(".svg", "image/svg+xml", "" ));
			InsertMimeType(new MimeType(".swf", "application/x-shockwave-flash", "" ));
			InsertMimeType(new MimeType(".tif", "image/tiff", "" ));
			InsertMimeType(new MimeType(".tiff", "image/tiff", "" ));
			InsertMimeType(new MimeType(".tsi", "audio/tsp-audio", "" ));
			InsertMimeType(new MimeType(".tsp", "audio/tsplayer", "" ));
			InsertMimeType(new MimeType(".turbot", "image/florian", "" ));
			InsertMimeType(new MimeType(".txt", "text/plain", "" ));
			InsertMimeType(new MimeType(".vdo", "video/vdo", "" ));
			InsertMimeType(new MimeType(".viv", "video/vivo", "" ));
			InsertMimeType(new MimeType(".vivo", "video/vivo", "" ));
			InsertMimeType(new MimeType(".voc", "audio/voc", "" ));
			InsertMimeType(new MimeType(".vos", "video/vosaic", "" ));
			InsertMimeType(new MimeType(".vox", "audio/voxware", "" ));
			InsertMimeType(new MimeType(".wax", "audio/x-ms-wax", "" ));
			InsertMimeType(new MimeType(".wav", "audio/wav", "application/x-mplayer2" ));
			InsertMimeType(new MimeType(".wbmp", "image/vnd.wap.wbmp", "" ));
			InsertMimeType(new MimeType(".webm", "video/webm", "" ));
			InsertMimeType(new MimeType(".wmf", "image/wmf", "" ));
			InsertMimeType(new MimeType(".wma", "audio/x-ms-wma", "" ));
			InsertMimeType(new MimeType(".wmv", "video/x-ms-wmv", "" ));
			InsertMimeType(new MimeType(".wvx", "video/x-ms-wvx", "" ));
			InsertMimeType(new MimeType(".xbap", "application/x-ms-xbap", "" ));
			InsertMimeType(new MimeType(".xaml", "application/xaml+xml", "" ));
			InsertMimeType(new MimeType(".xlam", "application/vnd.ms-excel.addin.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".xls", "application/vnd.ms-excel", "" ));
			InsertMimeType(new MimeType(".xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "" ));
			InsertMimeType(new MimeType(".xltm", "application/vnd.ms-excel.template.macroEnabled.12", "" ));
			InsertMimeType(new MimeType(".xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template", "" ));
			InsertMimeType(new MimeType(".xif", "image/vnd.xiff", "" ));
			InsertMimeType(new MimeType(".xml", "text/xml", "" ));
			InsertMimeType(new MimeType(".xps", "application/vnd.ms-xpsdocument", "" ));
			InsertMimeType(new MimeType(".x-png", "image/png", "" ));
			InsertMimeType(new MimeType(".zip", "application/octet-stream", "" ));
		}
		
		private void InsertMimeType(MimeType mimeType)
		{
			Searchable searchable = Searchable.newSearchable();
			searchable.addSearchFilter("fileExtension", SearchOperator.eq, mimeType.getFileExtension());
			mimeTypeDao.addOrUpdate(mimeType, searchable);
		}

		private void InsertMimeTypeGalleries()
		{
			Searchable searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("isTemplate", SearchOperator.eq, false);
	        List<Gallery> galleries = galleryDao.findAll(searchable);
			if (galleries != null && galleries.size()>0)
			{
				List<MimeType> mimeTypes = mimeTypeDao.getAll();
				for(MimeType mimeType : mimeTypes)
				{
					searchable = Searchable.newSearchable();
					searchable.addSearchFilter("gallery.id", SearchOperator.eq, galleries.get(0).getId());
					searchable.addSearchFilter("mimeType.id", SearchOperator.eq, mimeType.getId());
					mimeTypeGalleryDao.addOrUpdate(new MimeTypeGallery(galleries.get(0), mimeType, true), searchable); //mimeTypeDto.FileExtension.Equals(".jpg", StringComparison.OrdinalIgnoreCase) || mimeTypeDto.FileExtension.Equals(".jpeg", StringComparison.OrdinalIgnoreCase)
				} 
			}
		}

		private void InsertContentTemplates()
		{
			// Define the Flash and Silverlight templates. We use these for several media types so let's just define them once here.
			String flashHtmlTmpl = "<a href='{ContentObjectUrl}' style='display:block;width:{Width}px;height:{Height}px;margin:0 auto;' id='{UniqueId}_player'></a>";

			String flashScriptTmpl = "window.{UniqueId}RunFlowPlayer=function(){\n" +
" jQuery('#{UniqueId}_player').attr('href',function(){return this.href.replace(/&/g,'%26')});\n" +
" flowplayer('{UniqueId}_player',{src:'{GalleryPath}/script/flowplayer-3.2.16.swf',wmode:'opaque'},{clip:{autoPlay:{AutoStartContentObjectText},scaling:'fit'}})\n" +
"};\n" +
"\n" +
"if (window.flowplayer)\n" +
" {UniqueId}RunFlowPlayer();\n" +
"else\n" +
" jQuery.getScript('{GalleryPath}/script/flowplayer-3.2.12.min.js',{UniqueId}RunFlowPlayer);";

			String silverlightAudioSkin = "AudioGray.xaml";
			String silverlightVideoSkin = "Professional.xaml";
			String silverlightHtmlTmpl = "<div id=\'{UniqueId}_mp1p\'></div>";

			String silverlightScriptTmpl = "var loadScripts=function(files, callback) {\n" +
"	$.getScript(files.shift(), files.length ? function() { loadScripts(files, callback); } : callback);\n" +
"};\n" +
"\n" +		
"var runSilverlight = function () {\n" +
"	Sys.UI.Silverlight.Control.createObject('{UniqueId}_mp1p','<object type=\'application/x-silverlight\' id=\'{UniqueId}_mp1\' style=\'height:{Height}px;width:{Width}px;\'><param name=\'Windowless\' value=\'True\' /><a href=\'http://go2.microsoft.com/fwlink/?LinkID=114576&amp;v=1.0\'><img src=\'http://go2.microsoft.com/fwlink/?LinkID=108181\' alt=\'Get Microsoft Silverlight\' style=\'border-width:0;\' /></a></object>');\n" +
"	Sys.Application.add_init(function() {\n" +
"		$create(Sys.UI.Silverlight.MediaPlayer, {\n" +
"			mediaSource: '{ContentObjectUrl}',\n" +
"			scaleMode: 1,\n" +
"			source: '{GalleryPath}/xaml/mediaplayer/{{0}}',\n" +
"			autoPlay: {AutoStartMediaObjectText}\n" +
"		}, null, null, document.getElementById('{UniqueId}_mp1p'));\n" +
"	});\n" +
"	Sys.Application.initialize();\n" +
"};\n" +
"\n" +		
"window.Mds.msAjaxComponentId='{UniqueId}_mp1';\n" +
"if ((typeof Sys === 'undefined') || !Sys.UI.Silverlight) {\n" +
"	var scripts = ['{GalleryPath}/script/MicrosoftAjax.js', '{GalleryPath}/script/SilverlightControl.js', '{GalleryPath}/script/SilverlightMedia.js'];\n" +
"	loadScripts(scripts, runSilverlight);\n" +
"} else {\n" +
"	runSilverlight();\n" +
"}";

			String pdfScriptTmplIE = "// IE and Safari render Adobe Reader iframes on top of jQuery UI dialogs, so add event handler to hide frame while dialog is visible\n" +
"$('.mds_mo_share_dlg').on('dialogopen', function() {\n" +
" $('#{UniqueId}_frame').css('visibility', 'hidden');\n" +
"}).on('dialogclose', function() {\n" +
"$('#{UniqueId}_frame').css('visibility', 'visible');\n" +
"});";

			String pdfScriptTmplSafari = "// IE and Safari render Adobe Reader iframes on top of jQuery UI dialogs, so add event handler to hide frame while dialog is visible\n" +
"// Safari requires that we clear the iframe src before we can hide it\n" +
"$('.mds_mo_share_dlg').on('dialogopen', function() {\n" +
" $('#{UniqueId}_frame').attr('src', '').css('visibility', 'hidden');\n" +
"}).on('dialogclose', function() {\n" +
"$('#{UniqueId}_frame').attr('src', '{ContentObjectUrl}').css('visibility', 'visible');\n" +
"});";

			InsertContentTemplate(new ContentTemplate("image/*", "default", "<img src='{ContentObjectUrl}' class='mds_mo_img' alt='{TitleNoHtml}' title='{TitleNoHtml}' />", "" ));
			InsertContentTemplate(new ContentTemplate("audio/*", "default", "<object type='{MimeType}' data='{ContentObjectUrl}' style='width:{Width}px;height:{Height}px;' ><param name='autostart' value='{AutoStartContentObjectInt}' /><param name='controller' value='true' /></object>", "" ));
			InsertContentTemplate(new ContentTemplate("audio/*", "ie", "<object classid='clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6' standby='Loading audio...' style='width:{Width}px;height:{Height}px;'><param name='url' value='{ContentObjectUrl}' /><param name='src' value='{ContentObjectUrl}' /><param name='autostart' value='{AutoStartContentObjectText}' /><param name='showcontrols' value='true' /></object>", "" ));
			InsertContentTemplate(new ContentTemplate("audio/ogg", "default", "<audio src='{ContentObjectUrl}' controls autobuffer preload {AutoPlay}><p>Cannot play: Your browser does not support the <code>audio</code> element or the codec of this file. Try another browser or download the file.</p></audio>", "" ));
			InsertContentTemplate(new ContentTemplate("audio/ogg", "ie", "<p>Cannot play: Internet Explorer cannot play Ogg Theora files. Try another browser or download the file.</p>", "" ));
			InsertContentTemplate(new ContentTemplate("audio/wav", "default", "<audio src='{ContentObjectUrl}' controls autobuffer preload {AutoPlay}><p>Cannot play: Your browser does not support the <code>audio</code> element or the codec of this file. Try another browser or download the file.</p></audio>", "" ));
			InsertContentTemplate(new ContentTemplate("audio/wav", "ie", "<object classid='clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6' standby='Loading audio...' style='width:{Width}px;height:{Height}px;'><param name='url' value='{ContentObjectUrl}' /><param name='src' value='{ContentObjectUrl}' /><param name='autostart' value='{AutoStartContentObjectText}' /><param name='showcontrols' value='true' /></object>", "" ));

			InsertContentTemplate(new ContentTemplate("audio/x-mp3", "default", "<audio src='{ContentObjectUrl}' controls autobuffer preload {AutoPlay}><p>Cannot play: Your browser does not support the <code>audio</code> element or the codec of this file. Try another browser or download the file.</p></audio>", "" ));
			InsertContentTemplate(new ContentTemplate("audio/x-mp3", "firefox", silverlightHtmlTmpl, silverlightScriptTmpl.replace("{{0}}", silverlightAudioSkin) ));
			InsertContentTemplate(new ContentTemplate("audio/x-mp3", "ie1to8", silverlightHtmlTmpl, silverlightScriptTmpl.replace("{{0}}", silverlightAudioSkin) ));

			InsertContentTemplate(new ContentTemplate("audio/m4a", "default", silverlightHtmlTmpl, silverlightScriptTmpl.replace("{{0}}", silverlightAudioSkin) ));
			InsertContentTemplate(new ContentTemplate("audio/m4a", "chrome", "<audio src='{ContentObjectUrl}' controls autobuffer preload {AutoPlay}><p>Cannot play: Your browser does not support the <code>audio</code> element or the codec of this file. Try another browser or download the file.</p></audio>", "" ));

			InsertContentTemplate(new ContentTemplate("audio/x-ms-wma", "default", silverlightHtmlTmpl, silverlightScriptTmpl.replace("{{0}}", silverlightAudioSkin) ));
			InsertContentTemplate(new ContentTemplate("video/*", "default", "<object type='{MimeType}' data='{ContentObjectUrl}' style='width:{Width}px;height:{Height}px;' ><param name='src' value='{ContentObjectUrl}' /><param name='autostart' value='{AutoStartContentObjectInt}' /></object>", "" ));
			InsertContentTemplate(new ContentTemplate("video/*", "ie", "<object type='{MimeType}' data='{ContentObjectUrl}' style='width:{Width}px;height:{Height}px;'><param name='src' value='{ContentObjectUrl}' /><param name='autostart' value='{AutoStartContentObjectText}' /></object>", "" ));
			InsertContentTemplate(new ContentTemplate("video/ogg", "default", "<video src='{ContentObjectUrl}' controls autobuffer preload {AutoPlay}><p>Cannot play: Your browser does not support the <code>video</code> element or the codec of this file. Try another browser or download the file.</p></video>", "" ));
			InsertContentTemplate(new ContentTemplate("video/ogg", "ie", "<p>Cannot play: Internet Explorer cannot play Ogg Theora files. Try another browser or download the file.</p>", "" ));

			InsertContentTemplate(new ContentTemplate("video/x-ms-wmv", "default", silverlightHtmlTmpl, silverlightScriptTmpl.replace("{{0}}", silverlightVideoSkin) ));

			InsertContentTemplate(new ContentTemplate("video/mp4", "default", "<video src='{ContentObjectUrl}' controls autobuffer preload {AutoPlay}><p>Cannot play: Your browser does not support the <code>video</code> element or the codec of this file. Try another browser or download the file.</p></video>", "" ));
			InsertContentTemplate(new ContentTemplate("video/mp4", "ie1to8", flashHtmlTmpl, flashScriptTmpl ));
			InsertContentTemplate(new ContentTemplate("video/mp4", "opera", flashHtmlTmpl, flashScriptTmpl ));

			InsertContentTemplate(new ContentTemplate("video/m4v", "default", "<video src='{ContentObjectUrl}' controls autobuffer preload {AutoPlay}><p>Cannot play: Your browser does not support the <code>video</code> element or the codec of this file. Try another browser or download the file.</p></video>", "" ));
			InsertContentTemplate(new ContentTemplate("video/m4v", "ie1to8", flashHtmlTmpl, flashScriptTmpl ));
			InsertContentTemplate(new ContentTemplate("video/m4v", "opera", flashHtmlTmpl, flashScriptTmpl ));

			InsertContentTemplate(new ContentTemplate("video/x-ms-asf", "default", silverlightHtmlTmpl, silverlightScriptTmpl.replace("{{0}}", silverlightVideoSkin) ));
			InsertContentTemplate(new ContentTemplate("video/divx", "default", "<object type='{MimeType}' data='{ContentObjectUrl}' style='width:{Width}px;height:{Height}px;'><param name='src' value='{ContentObjectUrl}' /><param name='mode' value='full' /><param name='minVersion' value='1.0.0' /><param name='allowContextMenuFunction' value='true' /><param name='autoPlay' value='{AutoStartContentObjectText}' /><param name='loop' value='false' /><param name='bannerEnabled' value='false' /><param name='bufferingMode' value='auto' /><param name='previewMessage' value='Click to start video' /><param name='previewMessageFontSize' value='24' /><param name='movieTitle' value='{TitleNoHtml}' /></object>", "" ));
			InsertContentTemplate(new ContentTemplate("video/divx", "ie", "<object classid='clsid:67DABFBF-D0AB-41fa-9C46-CC0F21721616' codebase='http://go.divx.com/plugin/DivXBrowserPlugin.cab' style='width:{Width}px;height:{Height}px;'><param name='src' value='{ContentObjectUrl}' /><param name='mode' value='full' /><param name='minVersion' value='1.0.0' /><param name='allowContextMenuFunction' value='true' /><param name='autoPlay' value='{AutoStartContentObjectText}' /><param name='loop' value='false' /><param name='bannerEnabled' value='false' /><param name='bufferingMode' value='auto' /><param name='previewMessage' value='Click to start video' /><param name='previewMessageFontSize' value='24' /><param name='movieTitle' value='{TitleNoHtml}' /></object>", "" ));
			InsertContentTemplate(new ContentTemplate("video/webm", "default", "<video src='{ContentObjectUrl}' controls autobuffer preload {AutoPlay}><p>Cannot play: Your browser does not support the <code>video</code> element or the codec of this file. Try another browser or download the file.</p></video>", "" ));
			InsertContentTemplate(new ContentTemplate("application/x-shockwave-flash", "default", "<object type='{MimeType}' data='{ContentObjectUrl}' style='width:{Width}px;height:{Height}px;' id='flash_plugin' standby='loading movie...'><param name='movie' value='{ContentObjectUrl}' /><param name='allowScriptAccess' value='sameDomain' /><param name='quality' value='best' /><param name='wmode' value='opaque' /><param name='scale' value='default' /><param name='bgcolor' value='#FFFFFF' /><param name='salign' value='TL' /><param name='FlashVars' value='playerMode=embedded' /><p><strong>Cannot play Flash content</strong> Your browser does not have the Flash plugin or it is disabled. To view the content, install the Macromedia Flash plugin or, if it is already installed, enable it.</p></object>", "" ));
			InsertContentTemplate(new ContentTemplate("application/x-shockwave-flash", "ie", "<object type='{MimeType}' classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0&quot; id='flash_activex' standby='loading movie...' style='width:{Width}px;height:{Height}px;'><param name='movie' value='{ContentObjectUrl}' /><param name='quality' value='high' /><param name='wmode' value='opaque' /><param name='bgcolor' value='#FFFFFF' /><p><strong>Cannot play Flash content</strong> Your browser does not have the Flash plugin or it is disabled. To view the content, install the Macromedia Flash plugin or, if it is already installed, enable it.</p></object>", "" ));
			InsertContentTemplate(new ContentTemplate("application/x-shockwave-flash", "ie5to9mac", "<object type='{MimeType}' data='{ContentObjectUrl}' style='width:{Width}px;height:{Height}px;' id='flash_plugin' standby='loading movie...'><param name='movie' value='{ContentObjectUrl}' /><param name='allowScriptAccess' value='sameDomain' /><param name='quality' value='best' /><param name='scale' value='default' /><param name='bgcolor' value='#FFFFFF' /><param name='wmode' value='opaque' /><param name='salign' value='TL' /><param name='FlashVars' value='playerMode=embedded' /><strong>Cannot play Flash content</strong> Your browser does not have the Flash plugin or it is disabled. To view the content, install the Macromedia Flash plugin or, if it is already installed, enable it.</object>", "" ));
			InsertContentTemplate(new ContentTemplate("video/f4v", "default", flashHtmlTmpl, flashScriptTmpl ));
			InsertContentTemplate(new ContentTemplate("video/x-flv", "default", flashHtmlTmpl, flashScriptTmpl ));

			InsertContentTemplate(new ContentTemplate("application/pdf", "default", "<p><a href='{ContentObjectUrl}'>Enlarge PDF to fit browser window</a></p><iframe id='{UniqueId}_frame' src='{ContentObjectUrl}' frameborder='0' style='width:680px;height:600px;border:1px solid #000;'></iframe>", "" ));
			InsertContentTemplate(new ContentTemplate("application/pdf", "ie", "<p><a href='{ContentObjectUrl}'>Enlarge PDF to fit browser window</a></p><iframe id='{UniqueId}_frame' src='{ContentObjectUrl}' frameborder='0' style='width:680px;height:600px;border:1px solid #000;'></iframe>", pdfScriptTmplIE ));
			InsertContentTemplate(new ContentTemplate("application/pdf", "safari", "<p><a href='{ContentObjectUrl}'>Enlarge PDF to fit browser window</a></p><iframe id='{UniqueId}_frame' src='{ContentObjectUrl}' frameborder='0' style='width:680px;height:600px;border:1px solid #000;'></iframe>", pdfScriptTmplSafari ));

			InsertContentTemplate(new ContentTemplate("text/plain", "default", "<p><a href='{ContentObjectUrl}'>Enlarge file to fit browser window</a></p><iframe src='{ContentObjectUrl}' frameborder='0' style='width:680px;height:600px;border:1px solid #000;background-color:#fff;'></iframe>", "" ));
			InsertContentTemplate(new ContentTemplate("text/html", "default", "<p><a href='{ContentObjectUrl}'>Enlarge file to fit browser window</a></p><iframe src='{ContentObjectUrl}' frameborder='0' style='width:680px;height:600px;border:1px solid #000;background-color:#fff;'></iframe>", "" ));
			InsertContentTemplate(new ContentTemplate("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "default", "<p style='margin-bottom:5em;'><a href='{ContentObjectUrl}&sa=1' title='Download {TitleNoHtml}'>Download {TitleNoHtml}</a></p>", "" ));
			InsertContentTemplate(new ContentTemplate("application/msword", "default", "<p style='margin-bottom:5em;'><a href='{ContentObjectUrl}&sa=1' title='Download {TitleNoHtml}'>Download {TitleNoHtml}</a></p>", "" ));
			InsertContentTemplate(new ContentTemplate("message/rfc822", "default", "<p class='mds_msgfriendly'>This browser cannot display web archive files (.mht). <a href='{ContentObjectUrl}&sa=1' title='Download {TitleNoHtml}'>Download {TitleNoHtml}</a></p>", "" ));
			InsertContentTemplate(new ContentTemplate("message/rfc822", "ie", "<p><a href='{ContentObjectUrl}'>Enlarge to fit browser window</a></p><iframe src='{ContentObjectUrl}' frameborder='0' style='width:680px;height:600px;border:1px solid #000;background-color:#fff;'></iframe>", "" ));
		}
		
		private void InsertContentTemplate(ContentTemplate contentTemplate)
		{
			Searchable searchable = Searchable.newSearchable();
			searchable.addSearchFilter("mimeType", SearchOperator.eq, contentTemplate.getMimeType());
			searchable.addSearchFilter("browserId", SearchOperator.eq, contentTemplate.getBrowserId());
			contentTemplateDao.addOrUpdate(contentTemplate, searchable);
		}

		private void InsertUiTemplates()
		{
			Searchable searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("isTemplate", SearchOperator.eq, true);
	        List<Gallery> galleries = galleryDao.findAll(searchable);
	        Gallery gallery = null;
			if (galleries != null && galleries.size()>0){
				gallery = galleries.get(0);
			}

			InsertUiTemplate(new UiTemplate(									                    
																									UiTemplateType.Album,
																						            DefaultTmplName,
																						            gallery,
																						            "",
																						            "<div class='mds_abm_sum'>\n" +
" <div class='mds_abm_sum_col2'>\n" +
"	<p class='mds_abm_sum_col2_row1'>({{:Album.NumContentItems}}{{:Resource.AbmNumObjSuffix}})</p>\n" +
"	<div class='mds_abm_sum_col2_row2'>\n" +
"	 {{if Settings.ShowSlideShowButton}}\n" +
"	 <a class='mds_abm_sum_ss_trigger mds_abm_sum_btn' href='#'>\n" +
"		<img src='{{:App.SkinPath}}/images/play-ss-m.png' title='{{:Resource.MoTbSsStart}}' alt=''>\n" +
"	 </a>\n" +
"	 {{/if}}\n" +
"	 {{if Settings.ShowUrlsButton}}\n" +
"	 <a class='mds_abm_sum_sa_trigger mds_abm_sum_btn' href='#'>\n" +
"		<img src='{{:App.SkinPath}}/images/link-m.png' title='{{:Resource.AbmShareAlbum}}' alt=''>\n" +
"	 </a>\n" +
"	 {{/if}}\n" +
"	 {{if Settings.AllowZipDownload}}\n" +
"	 <a class='mds_abm_sum_DownloadZip mds_abm_sum_btn' href='{{: ~getDownloadUrl(Album.Id) }}'>\n" +
"		<img src='{{:App.SkinPath}}/images/download-zip-m.png' title='{{:Resource.AbmDwnldZip}}' alt=''>\n" +
"	 </a>\n" +
"	 {{/if}}\n" +
"	 <span>\n" +
"		<button class='mds_abm_sum_rs'>{{:Resource.AbmRvsSortTt}}</button>\n" +
"		<button class='mds_btn_sb'>{{:Resource.AbmSortbyTt}}</button>\n" +
"	 </span>\n" +
"	 <ul class='mds_abm_sum_sbi'>\n" +
"		<li class='mds_abm_sum_sbi_hdr'>{{:Resource.AbmSortbyTt}}</li>\n" +
"{{if Album.VirtualType == 1 && Album.Permissions.EditAlbum}}<li><a href='#' data-id='-2147483648'>{{:Resource.AbmSortbyCustom}}</a></li>{{/if}}\n" +
"		<li><a href='#' data-id='8'>{{:Resource.AbmSortbyDatePictureTaken}}</a></li>\n" +
"		<li><a href='#' data-id='111'>{{:Resource.AbmSortbyDateAdded}}</a></li>\n" +
"		<li><a href='#' data-id='26'>{{:Resource.AbmSortbyRating}}</a></li>\n" +
"		<li><a href='#' data-id='29'>{{:Resource.AbmSortbyTitle}}</a></li>\n" +
"		<li><a href='#' data-id='34'>{{:Resource.AbmSortbyFilename}}</a></li>\n" +
"	 </ul>\n" +
"	</div>\n" +
" </div>\n" +
" <p class='mds_abm_sum_col1_row1'>\n" +
"{{if Album.VirtualType == 1 && Album.Permissions.EditAlbum}}\n" +
"	<a class='mds_abm_sum_pvt_trigger mds_abm_sum_btn' href='#'>\n" +
"		<img src='{{:App.SkinPath}}/images/lock-{{if Album.IsPrivate || !Settings.AllowAnonBrowsing}}active-{{/if}}s.png' title='{{if !Settings.AllowAnonBrowsing}}{{:Resource.AbmAnonDisabledTt}}{{else}}{{if Album.IsPrivate}}{{:Resource.AbmIsPvtTt}}{{else}}{{:Resource.AbmNotPvtTt}}{{/if}}{{/if}}' alt=''>\n" +
"	 </a>\n" +
"{{/if}}\n" +
"{{if Album.VirtualType == 1 && Album.Permissions.AdministerGallery}}\n" +
"	<a class='mds_abm_sum_ownr_trigger mds_abm_sum_btn' href='#'>\n" +
"		<img src='{{:App.SkinPath}}/images/user-s.png' title='{{:Resource.AbmOwnrTt}}' alt=''>\n" +
"	 </a>\n" +
"{{/if}}\n" +
"{{if Album.RssUrl}}\n" +
"	<a class='mds_abm_sum_btn' href='{{:Album.RssUrl}}'>\n" +
"		<img src='{{:App.SkinPath}}/images/rss-s.png' title='{{:Resource.AbmRssTt}}' alt=''>\n" +
"	</a>\n" +
"{{/if}}\n" +
"	<span class='mds_abm_sum_col1_row1_hdr'>{{:Resource.AbmPfx}}</span>\n" +
"	<span class='mds_abm_sum_col1_row1_dtl'>{{:Album.Title}}</span>\n" +
" </p>\n" +
" <div class='mds_abm_sum_col1_row2'>\n" +
"	<span class='mds_abm_sum_col1_row2_hdr'></span>\n" +
"	<span class='mds_abm_sum_col1_row2_dtl'>{{:Album.Caption}}</span>\n" +
" </div>\n" +
"</div>\n" +
"\n" +
"{{if Album.ContentItems.length == 0}}\n" +
" <p class='mds_abm_noobj'>{{:Resource.AbmNoObj}} {{if Album.VirtualType == 1 && Album.Permissions.AddContentObject}}<a href='{{: ~getAddUrl(#data) }}'>{{:Resource.AbmAddObj}}</a>{{/if}}</p>\n" +
"{{/if}}\n" +
"\n" +
"<ul class='mds_floatcontainer mds_abm_thmbs'>\n" +
" {{for Album.ContentItems}}\n" +
" <li class='thmb{{if IsAlbum}} album{{/if}}' data-id='{{:Id}}' data-it='{{:ItemType}}' style='width:{{:Views[ViewIndex].Width + 40}}px;'>\n" +
"	<a class='mds_thmbLink' href='{{: ~getContentItemUrl(#data, !IsAlbum) }}'>\n" +
"	 <img class='mds_thmb_img' style='width:{{:Views[ViewIndex].Width}}px;height:{{:Views[ViewIndex].Height}}px;' src='{{:Views[ViewIndex].Url}}'>\n" +
"	</a>\n" +
"	<p class='mds_go_t' title='{{stripHtml:Title}}'>{{stripHtmlAndTruncate:Title}}</p>\n" +
" </li>\n" +
" {{/for}}\n" +
"</ul>\n" +
"\n" +
"<div class='mds_abm_sum_share_dlg mds_dlg'>\n" +
" <p class='mds_abm_sum_share_dlg_t'>{{:Resource.AbmShareAlbum}}</p>\n" +
" <p class='mds_abm_sum_share_dlg_s'>{{:Resource.AbmLinkToAlbum}}</p>\n" +
" <p><input type='text' class='mds_abm_sum_share_dlg_ipt' value='{{: ~getAlbumUrl(Album.Id, true) }}' /></p>\n" +
"</div>\n" +
"\n" +
"<div class='mds_abm_sum_ownr_dlg mds_dlg'>\n" +
" <p class='mds_abm_sum_ownr_dlg_t'>{{:Resource.AbmOwnr}}</p>\n" +
" <p class='mds_abm_sum_ownr_dlg_s'>{{:Resource.AbmOwnrDtl}}</p>\n" +
"{{if Album.Permissions.AdministerGallery}}\n" +
" <p class='mds_abm_sum_ownr_dlg_o'><span>{{:Resource.AbmOwnrLbl}}</span> <input type='text' class='mds_abm_sum_ownr_dlg_ipt' value='{{:Album.Owner}}' /></p>\n" +
"{{/if}}\n" +
"{{if Album.InheritedOwners}}\n" +
" <p class='mds_abm_sum_ownr_dlg_io'>{{:Resource.AbmOwnrInhtd}} {{:Album.InheritedOwners}}</p>\n" +
"{{/if}}\n" +
"</div>",
																								    "// Call the mdsThumbnails plug-in, which adds the HTML to the page and then configures it\n" +
"$('#{{:Settings.ThumbnailClientId}}').mdsThumbnails('{{:Settings.ThumbnailTmplName}}', window.{{:Settings.ClientId}}.mdsData);"
																																											));

			InsertUiTemplate(new UiTemplate(
																									UiTemplateType.ContentObject,
																									DefaultTmplName,
																									gallery,
																									"",
																									GetContentObjectHtmlTmpl(false),
																									GetContentObjectJsTmpl(false)
																								));

			InsertUiTemplate(new UiTemplate(
																									UiTemplateType.Header,
																									DefaultTmplName,
																									gallery,
																									"",
																									GetHeaderHtmlTmpl(false),
																									GetHeaderJsTmpl(false)
																								));

			InsertUiTemplate(new UiTemplate(
																					                UiTemplateType.LeftPane,
																					                DefaultTmplName,
																					                gallery,
																					                "",
																					                GetLeftPaneHtmlTmpl(false, false),
																					                GetLeftPaneJsTmpl(false, false)
																				                ));

			InsertUiTemplate(new UiTemplate(
																									UiTemplateType.RightPane,
																									DefaultTmplName,
																									gallery,
																									"",
																									GetRightPaneHtmlTmpl(false, false),
																									GetRightPaneJsTmpl(false, false)
																								));

			InsertUiTemplate(new UiTemplate(
																									UiTemplateType.Album,
																									"List View",
																									gallery,
																									"",
																									"<table>\n" +
"<thead style='font-weight:bold;'><td>Title</td><td>Type</td><thead>\n" +
"{{for Album.ContentItems}}\n" +
"<tr>\n" +
"<td>\n" +
" <p><a title='{{:Title}}' href='{{:#parent.parent.data.App.CurrentPageUrl}}{{if IsAlbum}}?aid={{:Id}}'>{{:#parent.parent.parent.data.Resource.AbmPfx}} {{else}}?moid={{:Id}}'>{{/if}}{{:Title}}</a></p>\n" +
"</td>\n" +
"<td><p>{{getItemTypeDesc:ItemType}}</p></td>\n" +
"</tr>\n" +
"{{/for}}\n" +
"</table>",
																									"$('#{{:Settings.ThumbnailClientId}}').html($.render [ '{{:Settings.ThumbnailTmplName}}' ](window.{{:Settings.ClientId}}.mdsData));"
																								));
		}
		
		private void InsertUiTemplate(UiTemplate uiTemplate)
		{
			Searchable searchable = Searchable.newSearchable();
			searchable.addSearchFilter("templateType", SearchOperator.eq, uiTemplate.getTemplateType());
			searchable.addSearchFilter("gallery.id", SearchOperator.eq, uiTemplate.getGallery().getId());
			searchable.addSearchFilter("name", SearchOperator.eq, uiTemplate.getName());
			uiTemplateDao.addOrUpdate(uiTemplate, searchable);
		}

		private void InsertUiTemplateAlbums()
		{
			// Don't do anything. At this point the only UI templates that have been created are for the template gallery. Later, in
			// Gallery.Configure, there is validation code that makes sure both the UI templates and the template/album relationships
			// have been created for each gallery.
		}
		
		private void InsertPermissions(String currentUser){
			List<Permission> permissions = Lists.newArrayList();
			if (!permissionDao.findAny(null)) {
				for(UserAction action : UserAction.values()) {
					Permission permission = new Permission(action);
					permission.setCurrentUser(currentUser);
					permissions.add(permissionDao.save(permission));
				}				
			}else {
				permissions = permissionDao.getAll();
			}
			List<MenuFunction> menuFunctions = menuFunctionDao.getAll();
			for(MenuFunction menuFunction : menuFunctions) {
				if (menuFunction.getResourceId() == null || menuFunction.getResourceId() == ResourceId.home)
					continue;
				
				for(Permission permission : permissions) {
					if (permission.getPermission() == UserAction.All.getValue())
						continue;
					
					if ((permission.getPermission() & UserAction.add.getValue()) > 0
							||(permission.getPermission() & UserAction.view.getValue()) > 0
							||(permission.getPermission() & UserAction.delete.getValue()) > 0
							||(permission.getPermission() & UserAction.edit.getValue()) > 0) {
						MenuFunctionPermission menuFunctionPermission = new MenuFunctionPermission(menuFunction, permission);
						menuFunctionPermissionDao.save(menuFunctionPermission);
					}
				}
			}
		}


		/// <summary>
		/// Adds or updates the left pane templates available to Enterprise users.
		/// </summary>
		private void InsertLeftPaneEnterpriseTemplates()
		{
			Searchable searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("isTemplate", SearchOperator.eq, false);
	        List<Gallery> galleries = galleryDao.findAll(searchable);
	        Gallery gallery = null;
			if (galleries != null && galleries.size()>0){
				gallery = galleries.get(0);
			}

			InsertUiTemplate(new UiTemplate(
					UiTemplateType.LeftPane,
					"Default with Tag and People Trees",
					gallery,
					"",
					GetLeftPaneHtmlTmpl(true, false),
					GetLeftPaneJsTmpl(true, false)
				));

			InsertUiTemplate(new UiTemplate(
					UiTemplateType.LeftPane,
					"Default with Tag and People Clouds",
					gallery,
					"",
					GetLeftPaneHtmlTmpl(false, true),
					GetLeftPaneJsTmpl(false, true)
				));
		}

		/// <summary>
		/// Adds or updates the content object Facebook Comments widget and the right pane Facebook Like widget.
		/// </summary>
		private void InsertFacebookTemplates()
		{
			Searchable searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("isTemplate", SearchOperator.eq, false);
	        List<Gallery> galleries = galleryDao.findAll(searchable);
	        Gallery gallery = null;
			if (galleries != null && galleries.size()>0){
				gallery = galleries.get(0);
			}

			InsertUiTemplate(new UiTemplate(
				UiTemplateType.ContentObject,
				"Default with Facebook Comments Widget",
				gallery,
				"",
				GetContentObjectHtmlTmpl(true),
				GetContentObjectJsTmpl(true)
			));

			InsertUiTemplate(new UiTemplate(
				UiTemplateType.RightPane,
				"Default with Facebook Like Widget",
				gallery,
				"",
				GetRightPaneHtmlTmpl(false, true),
				GetRightPaneJsTmpl(false, true)
			));
		}

		/// <summary>
		/// Adds or updates the PayPal 'view cart' and 'add to cart' widgets.
		/// </summary>
		private void InsertPayPalTemplates()
		{
			Searchable searchable = Searchable.newSearchable();
	        searchable.addSearchFilter("isTemplate", SearchOperator.eq, false);
	        List<Gallery> galleries = galleryDao.findAll(searchable);
	        Gallery gallery = null;
			if (galleries != null && galleries.size()>0){
				gallery = galleries.get(0);
			}

			InsertUiTemplate(new UiTemplate(
				UiTemplateType.Header,
				"Default with PayPal View Cart Widget",
				gallery,
				"",
				GetHeaderHtmlTmpl(true),
				GetHeaderJsTmpl(true)
			));

			InsertUiTemplate(new UiTemplate(
				UiTemplateType.RightPane,
				"Default with PayPal Add To Cart Widget",
				gallery,
				"",
				GetRightPaneHtmlTmpl(true, false), 
				GetRightPaneJsTmpl(true, false)
			));
		}

		/// <summary>
		/// Gets the default HTML template for the header UI template, optionally including HTML to support the PayPal 'view cart' widget.
		/// </summary>
		/// <param name="includePayPalCartWidget">if set to <c>true</c> include HTML to support the PayPal 'view cart' widget.</param>
		/// <returns>System.String.</returns>
		private String GetHeaderHtmlTmpl(boolean includePayPalCartWidget)
		{
			// Note that this snippet is configured for payment to MDS System. The end user will need to replace with
			// their own PayPal HTML snippet.
			String payPalCart = "<input type='hidden' name='cmd' value='_s-xclick'>\n" +
"<input type='hidden' name='encrypted' value='-----BEGIN PKCS7-----MIIG1QYJKoZIhvcNAQcEoIIGxjCCBsICAQExggEwMIIBLAIBADCBlDCBjjELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRQwEgYDVQQKEwtQYXlQYWwgSW5jLjETMBEGA1UECxQKbGl2ZV9jZXJ0czERMA8GA1UEAxQIbGl2ZV9hcGkxHDAaBgkqhkiG9w0BCQEWDXJlQHBheXBhbC5jb20CAQAwDQYJKoZIhvcNAQEBBQAEgYCAgSOJPKlhbi65uXcxqm/144dltnmM3C/x/0OElzcUpMG1Lys8kY0rudkxmi1ZdVcoBflXcZDYdrXekZ19bsyMW6aeFDed4q5U1YyHo6GQtUJm0p7j00AutbeHoUXh6uWWVYRXQe6ceH3m2hfGP45qRuI3rtnLpYnKxX/u8Ht1TzELMAkGBSsOAwIaBQAwUwYJKoZIhvcNAQcBMBQGCCqGSIb3DQMHBAjH1GlAoHdKVYAwJ8oK/d1S5ff6h2l3g0Ah9dNHb7ZlFLRzdVZ7x3z0mH8QJof86n6gzzfI3EO9ygmLoIIDhzCCA4MwggLsoAMCAQICAQAwDQYJKoZIhvcNAQEFBQAwgY4xCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDQTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEUMBIGA1UEChMLUGF5UGFsIEluYy4xEzARBgNVBAsUCmxpdmVfY2VydHMxETAPBgNVBAMUCGxpdmVfYXBpMRwwGgYJKoZIhvcNAQkBFg1yZUBwYXlwYWwuY29tMB4XDTA0MDIxMzEwMTMxNVoXDTM1MDIxMzEwMTMxNVowgY4xCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDQTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEUMBIGA1UEChMLUGF5UGFsIEluYy4xEzARBgNVBAsUCmxpdmVfY2VydHMxETAPBgNVBAMUCGxpdmVfYXBpMRwwGgYJKoZIhvcNAQkBFg1yZUBwYXlwYWwuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDBR07d/ETMS1ycjtkpkvjXZe9k+6CieLuLsPumsJ7QC1odNz3sJiCbs2wC0nLE0uLGaEtXynIgRqIddYCHx88pb5HTXv4SZeuv0Rqq4+axW9PLAAATU8w04qqjaSXgbGLP3NmohqM6bV9kZZwZLR/klDaQGo1u9uDb9lr4Yn+rBQIDAQABo4HuMIHrMB0GA1UdDgQWBBSWn3y7xm8XvVk/UtcKG+wQ1mSUazCBuwYDVR0jBIGzMIGwgBSWn3y7xm8XvVk/UtcKG+wQ1mSUa6GBlKSBkTCBjjELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRQwEgYDVQQKEwtQYXlQYWwgSW5jLjETMBEGA1UECxQKbGl2ZV9jZXJ0czERMA8GA1UEAxQIbGl2ZV9hcGkxHDAaBgkqhkiG9w0BCQEWDXJlQHBheXBhbC5jb22CAQAwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQCBXzpWmoBa5e9fo6ujionW1hUhPkOBakTr3YCDjbYfvJEiv/2P+IobhOGJr85+XHhN0v4gUkEDI8r2/rNk1m0GA8HKddvTjyGw/XqXa+LSTlDYkqI8OwR8GEYj4efEtcRpRYBxV8KxAW93YDWzFGvruKnnLbDAF6VR5w/cCMn5hzGCAZowggGWAgEBMIGUMIGOMQswCQYDVQQGEwJVUzELMAkGA1UECBMCQ0ExFjAUBgNVBAcTDU1vdW50YWluIFZpZXcxFDASBgNVBAoTC1BheVBhbCBJbmMuMRMwEQYDVQQLFApsaXZlX2NlcnRzMREwDwYDVQQDFAhsaXZlX2FwaTEcMBoGCSqGSIb3DQEJARYNcmVAcGF5cGFsLmNvbQIBADAJBgUrDgMCGgUAoF0wGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMTMwMzI3MTk1MjAxWjAjBgkqhkiG9w0BCQQxFgQU1YXTC9Dqu21RZMCKhDX9ztZBwGIwDQYJKoZIhvcNAQEBBQAEgYAhY2gahJQiGyuGZrUb4KN282BuKkz6ex3ArCJvtjgADiYIC7uOnnRR6UbrW9ET83dSHqufueE1Bs9bw2Ccvb+KtBcL6WVI0Ml5F2SDM7rKCtcXk7ccclnvPfHDwqfzJWZQcy9NJYDf5jsh1/+ht1dFjgHJ+1SLDnBCCMdcZVQYAA==-----END PKCS7-----\n" +
"	'>\n" +
"<input id='{{:Settings.ClientId}}_viewCart' type='image' src='https://www.paypalobjects.com/en_US/i/btn/btn_viewcart_LG.gif' border='0' name='btnPayPal' alt='PayPal - The safer, easier way to pay online!' style='float:right;margin-top:5px'>\n" +
"<img alt='' border='0' src='https://www.paypalobjects.com/en_US/i/scr/pixel.gif' width='1' height='1'>";

			return HeaderHtmlTmpl().replace("{PayPalCartWidget}", (includePayPalCartWidget ? payPalCart : ""));
		}

		/// <summary>
		/// Gets the default JavaScript template for the header UI template, optionally including script to support the PayPal 'view cart' widget.
		/// </summary>
		/// <param name="includePayPalViewCartWidget">if set to <c>true</c> include script to support the PayPal 'view cart' widget.</param>
		/// <returns>System.String.</returns>
		private String GetHeaderJsTmpl(boolean includePayPalViewCartWidget)
		{
			String payPalCartJs = "\n" +
"$('#{{:Settings.ClientId}}_viewCart').click(function() {\n" +
" var f = $('form')[0];\n" +
" $('input[name=hosted_button_id]', f).remove(); // Needed to prevent conflict with add to cart widget\n" +
" f.action = 'https://www.paypal.com/cgi-bin/webscr';\n" +
" f.submit();\n" +
" return false;\n" +
"});";

			return HeaderJsTmpl().replace("{PayPalCartJs}", (includePayPalViewCartWidget ? payPalCartJs : ""));
		}

		/// <summary>
		/// Gets the default HTML template for the content object UI template, optionally including HTML to support the Facebook Comment widget.
		/// </summary>
		/// <param name="includeFacebookCommentWidget">if set to <c>true</c> HTML to support the Facebook Comment widget is included.</param>
		/// <returns>System.String.</returns>
		private String GetContentObjectHtmlTmpl(boolean includeFacebookCommentWidget)
		{
			String facebookCommentWidget = (includeFacebookCommentWidget ? "<div class='fb-comments' data-href='{{:App.HostUrl}}{{:App.CurrentPageUrl}}?moid={{:MediaItem.Id}}' data-width='470' data-num-posts='10' data-colorscheme='dark'></div>" : "");

			return ContentObjectHtmlTmpl().replace("{FacebookCommentWidget}", facebookCommentWidget);
		}

		/// <summary>
		/// Gets the default JavaScript template for the content object UI template, optionally including script to support the Facebook API.
		/// </summary>
		/// <param name="includeFacebookJs">if set to <c>true</c> the JavaScript required to invoke the Facebook API is included.</param>
		/// <returns>System.String.</returns>
		private String GetContentObjectJsTmpl(boolean includeFacebookJs)
		{
			return ContentObjectJsTmpl().replace("{FacebookJs}", (includeFacebookJs ? FacebookJs() : ""));
		}

		/// <summary>
		/// Gets the default HTML template for the left pane UI template, optionally including HTML to support the tag trees and
		/// tag clouds.
		/// </summary>
		/// <param name="includeTagTrees">if set to <c>true</c> HTML to support the tag trees is included.</param>
		/// <param name="includeTagClouds">if set to <c>true</c> HTML to support the tag clouds is included.</param>
		/// <returns>System.String.</returns>
		private String GetLeftPaneHtmlTmpl(boolean includeTagTrees, boolean includeTagClouds)
		{
			String tagTrees = "\n" +
"<div id='{{:Settings.ClientId}}_lptagtv' class='mds_lptagtv mds_wait'></div>\n" +
"<div id='{{:Settings.ClientId}}_lppeopletv' class='mds_lppeopletv mds_wait'></div>";

			String tagClouds = "\n" +
"<p class='mds_msgfriendly mds_addtopmargin10 mds_addleftmargin4'>{{:Resource.LpTags}}</p>\n" +
"<div id='{{:Settings.ClientId}}_lptagcloud' class='mds_lptagcloud mds_wait'></div>\n" +
"\n" +
"<p class='mds_msgfriendly mds_addtopmargin10 mds_addleftmargin4'>{{:Resource.LpPeople}}</p>\n" +
"<div id='{{:Settings.ClientId}}_lppeoplecloud' class='mds_lppeoplecloud mds_wait'></div>";

			return LeftPaneHtmlTmpl()
				.replace("{TagTrees}", (includeTagTrees ? tagTrees : ""))
				.replace("{TagClouds}", (includeTagClouds ? tagClouds : ""));
		}

		/// <summary>
		/// Gets the default JavaScript template for the left pane UI template, optionally including script to support the tag trees and
		/// tag clouds.
		/// </summary>
		/// <param name="includeTagTrees">if set to <c>true</c> JavaScript to support the tag trees is included.</param>
		/// <param name="includeTagClouds">if set to <c>true</c> JavaScript to support the tag clouds is included.</param>
		/// <returns>System.String.</returns>
		private String GetLeftPaneJsTmpl(boolean includeTagTrees, boolean includeTagClouds)
		{
			String tagTrees = "\n" +
"var appUrl = window.{{:Settings.ClientId}}.mdsData.App.AppUrl;\n" +
"var galleryId = window.{{:Settings.ClientId}}.mdsData.Album.GalleryId;\n" +
"\n" +
"var tagTreeOptions = {\n" +
" clientId: '{{:Settings.ClientId}}',\n" +
" albumIdsToSelect : [window.Mds.GetQSParm('tag')],\n" +
" treeDataUrl: appUrl  + '/api/meta/gettagtreeasjson?galleryId=' + galleryId + '&top=10&sortBy=count&sortAscending=false&expanded=false'\n" +
"};\n" +
"\n" +
"var peopleTreeOptions = {\n" +
" clientId: '{{:Settings.ClientId}}',\n" +
" albumIdsToSelect : [window.Mds.GetQSParm('people')],\n" +
" treeDataUrl: appUrl + '/api/meta/getpeopletreeasjson?galleryId=' + galleryId + '&top=10&sortBy=count&sortAscending=false&expanded=false'\n" +
"};\n" +
"\n" +
"$('#{{:Settings.ClientId}}_lptagtv').mdsTreeView(null, tagTreeOptions);\n" +
"$('#{{:Settings.ClientId}}_lppeopletv').mdsTreeView(null, peopleTreeOptions );\n" +
"\n" +
"$('#{{:Settings.ClientId}}_lptagtv,#{{:Settings.ClientId}}_lppeopletv').on('select_node.jstree', function (e, data) {\n" + 
" data.instance.toggle_node(data.node);\n" + 
"})";

			String tagClouds = "\n" +
"var appUrl = window.{{:Settings.ClientId}}.mdsData.App.AppUrl;\n" +
"var galleryId = window.{{:Settings.ClientId}}.mdsData.Album.GalleryId;\n" +
"\n" +
"var tagCloudOptions = {\n" +
" clientId: '{{:Settings.ClientId}}',\n" +
" tagCloudType: 'tag',\n" +
" tagCloudUrl: appUrl  + '/api/meta/tags?q=&galleryId=' + galleryId + '&top=20&sortBy=count&sortAscending=false'\n" +
"}\n" +
"\n" +
"var peopleCloudOptions = {\n" +
" clientId: '{{:Settings.ClientId}}',\n" +
" tagCloudType: 'people',\n" +
" tagCloudUrl: appUrl  + '/api/meta/people?q=&galleryId=' + galleryId + '&top=10&sortBy=count&sortAscending=false'\n" +
"}\n" +
"\n" +
"$('#{{:Settings.ClientId}}_lptagcloud').mdsTagCloud(null, tagCloudOptions);\n" +
"$('#{{:Settings.ClientId}}_lppeoplecloud').mdsTagCloud(null, peopleCloudOptions );";

			return LeftPaneJsTmpl()
				.replace("{TagTrees}", (includeTagTrees ? tagTrees : ""))
				.replace("{TagClouds}", (includeTagClouds ? tagClouds : ""));
		}

		/// <summary>
		/// Gets the default HTML template for the right pane UI template, optionally including HTML to support the PayPal 'add to cart' and
		/// Facebook Like widgets.
		/// </summary>
		/// <param name="includePayPalAddToCartWidget">if set to <c>true</c> HTML to support the PayPal 'add to cart' widget is included.</param>
		/// <param name="includeFacebookLikeWidget">if set to <c>true</c> HTML to support the Facebook Like widget is included.</param>
		/// <returns>System.String.</returns>
		private String GetRightPaneHtmlTmpl(boolean includePayPalAddToCartWidget, boolean includeFacebookLikeWidget)
		{
			String payPalAddToCart = "\n" +
"{{if MediaItem != null}}\n" +
"<input type='hidden' name='cmd' value='_s-xclick'>\n" +
"<input type='hidden' name='hosted_button_id' value='JP2UFSSRLBSM8'>\n" +
"<input type='hidden' name='item_name' value='Photograph - {{:MediaItem.Title}} (Item # {{:MediaItem.Id}})'>\n" +
"<input id='{{:Settings.ClientId}}_addToCart' type='image' src='https://www.paypalobjects.com/en_US/i/btn/btn_cart_LG.gif' border='0' name='addToCart' alt='PayPal - The safer, easier way to pay online!' style='padding:5px;'>\n" +
"<span style='display:inline-block;vertical-align:top;margin-top:10px;'>$1.00</span>\n" +
"<img alt='' border='0' src='https://www.paypalobjects.com/en_US/i/scr/pixel.gif' width='1' height='1'>\n" +
"{{/if}}";

			String facebookLike = "\n" +
"{{if MediaItem != null}}\n" +
"<iframe src='//www.facebook.com/plugins/like.php?href={{:App.HostUrl}}{{:App.CurrentPageUrl}}?moid={{:MediaItem.Id}}&amp;width=450&amp;colorscheme=dark&amp;height=80' scrolling='no' frameborder='0' style='border:none; overflow:hidden; width:400px; height:27px;display:block;margin:5px 0 0 5px;' allowTransparency='true'></iframe>\n" +
"{{/if}}";

			return RightPaneHtmlTmpl()
				.replace("{PayPalAddToCartWidget}", (includePayPalAddToCartWidget ? payPalAddToCart : ""))
				.replace("{FacebookLikeWidget}", (includeFacebookLikeWidget ? facebookLike : ""));
		}

		/// <summary>
		/// Gets the default JavaScript template for the right pane UI template, optionally including script to support the PayPal 'add to cart' and
		/// Facebook Like widgets.
		/// </summary>
		/// <param name="includePayPalAddToCartJs">if set to <c>true</c> JavaScript to support the PayPal 'add to cart' widget is included.</param>
		/// <param name="includeFacebookJs">if set to <c>true</c> JavaScript to support the Facebook API is included.</param>
		/// <returns>System.String.</returns>
		private String GetRightPaneJsTmpl(boolean includePayPalAddToCartJs, boolean includeFacebookJs)
		{
			String payPalAddToCartJs = "\n" +
"var bindAddToCartEvent = function() {\n" +
" $('#{{:Settings.ClientId}}_addToCart').click(function() {\n" +
"	var f = $('form')[0];\n" +
"	f.action = 'https://www.paypal.com/cgi-bin/webscr';\n" +
"	f.submit();\n" +
"	return false;\n" +
" });\n" +
"};\n" +
"\n" +
"$('#{{:Settings.ContentClientId}}').on('next.{{:Settings.ClientId}} previous.{{:Settings.ClientId}}', function() {\n" +
" bindAddToCartEvent();\n" +
"});\n" +
"\n" +
"bindAddToCartEvent();";

			return RightPaneJsTmpl()
				.replace("{PayPalAddToCartJs}", (includePayPalAddToCartJs ? payPalAddToCartJs : ""))
				.replace("{FacebookJs}", (includeFacebookJs ? FacebookJs() : ""));
		}
}
