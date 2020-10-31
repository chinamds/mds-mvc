/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.content.UiTemplateBo;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.cm.service.ContentObjectManager;
import com.mds.aiotplayer.cm.service.GallerySettingManager;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.GalleryView;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.UiTemplateType;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.Utils;
import com.mds.aiotplayer.webapp.common.controller.AbstractBaseController;
import com.mds.aiotplayer.webapp.common.plupload.Plupload;
import com.mds.aiotplayer.webapp.common.plupload.PluploadService;

@Controller
@RequestMapping("/cm/contentobjects*")
public class ContentObjectController extends AbstractBaseController<ContentObject, Long>{
    private ContentObjectManager contentObjectManager;
    private GallerySettingManager gallerySettingManager;

    @Autowired
    public void setContentObjectManager(ContentObjectManager contentObjectManager) {
        this.contentObjectManager = contentObjectManager;
    }
    
    @Autowired
    public void setGallerySettingManager(GallerySettingManager gallerySettingManager) {
        this.gallerySettingManager = gallerySettingManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(contentObjectManager.search(query, ContentObject.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(contentObjectManager.getAll());
        }
        return model;
    }
    
    @RequestMapping("addobjects")
    public String addContentObject(@RequestParam(value = "gid", required = false) final Long galleryId, @RequestParam(value = "aid", required = false)final Long aid, final HttpServletRequest request, Model model) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException, RecordExistsException {
		try {
			long gId = galleryId == null ? Long.MIN_VALUE : galleryId;
	    	AlbumBo album = null;
	    	if (aid != null && aid > Long.MIN_VALUE){
				album = AlbumUtils.loadAlbumInstance(aid, false);
			}
	    	
	    	if (galleryId == null || galleryId == Long.MIN_VALUE){
				if (album != null){
					gId = album.getGalleryId();
				}
				else
				{
					// There is no album or content object to get the gallery ID from, and no gallery ID has been specified on the control.
					// Just grab the first gallery in the database, creating it if necessary.
					GalleryBo gallery = CMUtils.loadGalleries().stream().findFirst().orElse(null);
					if (gallery != null)
					{
						gId = gallery.getGalleryId();
						//this.GalleryControl.GalleryControlSettings.GalleryId = galleryId;
						//this.GalleryControl.GalleryControlSettings.Save();
					}
					else
					{
						// No gallery found anywhere, including the data store. Create one and assign it to this control instance.
						GalleryBo g = CMUtils.createGalleryInstance();
						g.setName("MDS System");
						g.setDescription(I18nUtils.getString("webapp.name", request.getLocale()));
						g.setCreationDate(DateUtils.Now());
						g.save();
						//this.GalleryControl.GalleryControlSettings.GalleryId = g.GalleryId;
						//this.GalleryControl.GalleryControlSettings.Save();
						gId = g.getGalleryId();
					}
				}
			}
	    	if (album == null) {
				album = CMUtils.loadRootAlbumInstance(gId);
			}
	    	
			//model.addAttribute("gallerySettings", gallerySettingManager.getGallerySettingsMap());
			GallerySettings gallerySettings = CMUtils.loadGallerySetting(gId);
			model.addAttribute("gallerySettings", gallerySettings);
			model.addAttribute("album", album);
			model.addAttribute("fileFilters", GalleryView.getFileFilters(gallerySettings));
			model.addAttribute("redirectToAlbum", Utils.getUrl(request, ResourceId.cm_galleryview, "aid={0}", album.getId()));
		} catch (SearchException se) {
			 model.addAttribute("searchError", se.getMessage());
		}
		
		return viewName("addobjects");
    }
    
    @RequestMapping(value ="mediaview", method = RequestMethod.GET) //value = {"", "main"}
    public String getMediaView(@RequestParam(value = "galleryId", required = false) final long galleryId, @RequestParam(value = "albumId", required = false) final long albumId, Model model) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException {
    	GallerySettings gallerySettings = CMUtils.loadGallerySetting(galleryId);
		model.addAttribute("gallerySettings", gallerySettings);
		AlbumBo album = null;
		if (albumId > Long.MIN_VALUE) {
			album = AlbumUtils.loadAlbumInstance(albumId, true, false);
		}else {
			album = CMUtils.loadRootAlbumInstance(galleryId);
		}
		
		model.addAttribute("album", album);
		UiTemplateBo uiTemplate = CMUtils.loadUiTemplates().get(UiTemplateType.ContentObject, album);
		model.addAttribute("uiTemplate", uiTemplate);
    	
        return viewName("mediaview");
    }
    
    /**Plupload文件上传处理方法
     * @throws WebException 
     * @throws InvalidGalleryException 
     * @throws IOException 
     * @throws GallerySecurityException 
     * @throws InvalidMDSRoleException 
     * @throws InvalidContentObjectException 
     * @throws UnsupportedImageTypeException 
     * @throws InvalidAlbumException 
     * @throws UnsupportedContentObjectTypeException */
    @RequestMapping(value="pluploadUpload")
    public void upload(Plupload plupload, HttpServletRequest request, HttpServletResponse response) throws UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidMDSRoleException, GallerySecurityException, IOException, InvalidGalleryException, WebException {
		//AlbumBo album = AlbumUtils.loadAlbumInstance(aid, false);
		File dir = new File(AppSettings.getInstance().getTempUploadDirectory());
        if(!dir.exists()){
            dir.mkdirs();//可创建多级目录，而mkdir()只能创建一级目录
        }
	
        plupload.setRequest(request);//手动传入Plupload对象HttpServletRequest属性
        plupload.setResponse(response);

        //int userId = ((User)request.getSession().getAttribute("user")).getUserId();

        //文件存储绝对路径，会是一个文件夹，项目相应Servlet容器下的"pluploadDir"文件夹，还会以用户唯一id作划分
        
        //开始上传文件
        PluploadService.upload(plupload, dir);
    }
}
