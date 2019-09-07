package com.mds.cm.webapp.controller;

import com.mds.cm.service.AlbumManager;
import com.mds.cm.service.ContentObjectManager;
import com.mds.cm.util.AlbumUtils;
import com.mds.cm.util.AppEventLogUtils;
import com.mds.cm.util.CMUtils;
import com.mds.cm.util.ContentObjectUtils;
import com.mds.cm.util.TransferType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.exception.CannotDeleteAlbumException;
import com.mds.cm.exception.CannotTransferAlbumToNestedDirectoryException;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.model.Album;
import com.mds.cm.model.ContentObject;
import com.mds.common.utils.Reflections;
import com.mds.common.webapp.controller.BaseFormController;
import com.mds.core.SecurityActions;
import com.mds.core.exception.ArgumentNullException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.sys.util.UserUtils;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;
import com.mds.util.Utils;

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
import java.util.Map;

@Controller
@RequestMapping("/cm/contentobjects/editcaptions*")
public class EditCaptionsController extends BaseFormController {
	private ContentObjectManager contentObjectManager;

    @Autowired
    public void setContentObjectManager(ContentObjectManager contentObjectManager) {
        this.contentObjectManager = contentObjectManager;
    }
		
    public EditCaptionsController() {
        setCancelView("redirect:galleryview");
        setSuccessView("redirect:galleryview");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@RequestParam(value = "hdnCheckedContentObjectIds", required = false) String[] ids
    					   ,@RequestParam(value = "hdnTitles", required = false) String[] titles, HttpServletRequest request,  
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
        	return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");        	
        }
        
        //List<Map<Long, String>> contentObjectIds = Lists.newArrayList();
        Map<Long, String> map = Maps.newHashMap();
        for(int i=0; i<ids.length; i++){
			long id = StringUtils.toLong(ids[i].substring(1));// 'a' or 'm'

			if (!ContentObject.isIllegalId(id)) {
				map.put(id, titles[i]);
			}
		}
        
        if (map.isEmpty()) {
        	saveError(request, getText("task.no_Objects_Selected_Dtl", request.getLocale()));
        	
        	return "redirect:/cm/galleryview?g=cm_editcaptions&aid=" + request.getParameter("albumId"); 
        }
        
        contentObjectManager.saveCaptions(map);
        HelperFunctions.purgeCache();
        
        /*if (!Utils.isUserAuthorized(SecurityActions.EditContentObject)) {
        	return "redirect:/cm/galleryview?g=cm_editcaptions&aid=" + request.getParameter("albumId");
        }*/
        
        /*try
		{
        	EditCaptions.validateBeforeObjectDeletion(ids);
		}
		catch (CannotDeleteAlbumException ex)
		{
			AppEventLogUtils.LogError(ex);

			saveError(request, ex.getMessage());
        	
        	return "redirect:/cm/galleryview?g=cm_editcaptions&aid=" + request.getParameter("albumId");
		}
                
		if (EditCaptions.deleteObjects(ids, userCanDeleteContentObject, userCanDeleteChildAlbum, chkDeleteDbRecordsOnly)) {
			HelperFunctions.purgeCache();

			return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");
		}*/
    	
        return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");
    }
}
