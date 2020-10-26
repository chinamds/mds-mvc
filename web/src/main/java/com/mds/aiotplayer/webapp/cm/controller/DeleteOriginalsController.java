/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.service.ContentObjectManager;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.cm.util.TransferType;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.exception.CannotDeleteAlbumException;
import com.mds.aiotplayer.cm.exception.CannotTransferAlbumToNestedDirectoryException;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.core.MessageType;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/cm/contentobjects/deleteoriginals*")
public class DeleteOriginalsController extends BaseFormController {
	private ContentObjectManager contentObjectManager;

    @Autowired
    public void setContentObjectManager(ContentObjectManager contentObjectManager) {
        this.contentObjectManager = contentObjectManager;
    }
		
    public DeleteOriginalsController() {
        setCancelView("redirect:galleryview");
        setSuccessView("redirect:galleryview");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@RequestParam(value = "hdnCheckedContentObjectIds", required = false) String[] ids
    		, @RequestParam(value = "chkDeleteDbRecordsOnly", required = false)boolean chkDeleteDbRecordsOnly
    		, @RequestParam(value = "userCanDeleteContentObject", required = false)boolean userCanDeleteContentObject
    		, @RequestParam(value = "userCanDeleteChildAlbum", required = false)boolean userCanDeleteChildAlbum, HttpServletRequest request,  
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
        	return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");        	
        }
        
        if (ids.length == 0) {
        	saveError(request, getText("task.no_Objects_Selected_Dtl", request.getLocale()));
        	
        	return "redirect:/cm/galleryview?g=cm_deleteoriginals&aid=" + request.getParameter("albumId"); 
        }
        
        List<Long> albumIds = Lists.newArrayList();
		List<Long> contentObjectIds = Lists.newArrayList();
		for(String selectedItem : ids){
			long id = StringUtils.toLong(selectedItem.substring(1));// 'a' or 'm'

			if (selectedItem.startsWith("m")){
				contentObjectIds.add(id);
			}else if (selectedItem.startsWith("a")){
				albumIds.add(id);
			}
		}
		
		contentObjectManager.deleteOriginalFile(contentObjectIds, albumIds);
		HelperFunctions.purgeCache();
        
        /*try
		{
        	DeleteOriginals.validateBeforeObjectDeletion(ids);
		}
		catch (CannotDeleteAlbumException ex)
		{
			AppEventLogUtils.LogError(ex);

			saveError(request, ex.getMessage());
        	
        	return "redirect:/cm/galleryview?g=cm_deleteoriginals&aid=" + request.getParameter("albumId");
		}
                
		if (DeleteOriginals.deleteObjects(ids, userCanDeleteContentObject, userCanDeleteChildAlbum, chkDeleteDbRecordsOnly)) {
			HelperFunctions.purgeCache();

			return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");
		}*/
		    	
		return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId") + StringUtils.format("&msg={0}", MessageType.OriginalFilesSuccessfullyDeleted.value());   
    }
}
