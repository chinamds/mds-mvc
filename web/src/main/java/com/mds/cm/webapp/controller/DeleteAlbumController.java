package com.mds.cm.webapp.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.cm.service.AlbumManager;
import com.mds.cm.util.AlbumUtils;
import com.mds.cm.util.CMUtils;
import com.mds.cm.util.ContentObjectUtils;
import com.mds.cm.util.TransferType;
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
import com.mds.common.utils.Reflections;
import com.mds.common.webapp.controller.BaseFormController;
import com.mds.core.SecurityActions;
import com.mds.core.exception.ArgumentNullException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.sys.util.UserUtils;
import com.mds.util.HelperFunctions;
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
import java.util.Locale;

@Controller
@RequestMapping("/cm/deletealbum*")
public class DeleteAlbumController extends BaseFormController {
		
    public DeleteAlbumController() {
        setCancelView("redirect:galleryview");
        setSuccessView("redirect:galleryview");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@RequestParam(value = "albumId", required = true) long aid
    		, @RequestParam(value = "chkDeleteDbRecordsOnly", required = false)boolean chkDeleteDbRecordsOnly, HttpServletRequest request,  
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }
        
    	try{
    		AlbumBo album = AlbumUtils.loadAlbumInstance(aid, true, false);
    		long pid = album.isRootAlbum() ? album.getId() : album.getParent().getId();
    		AlbumUtils.deleteAlbum(AlbumUtils.loadAlbumInstance(aid, true, false), !chkDeleteDbRecordsOnly);

			HelperFunctions.purgeCache();

			return "redirect:/cm/galleryview?aid=" + pid;
		}catch (CannotDeleteAlbumException ex) {
			saveError(request, getText("task.deleteAlbum.Cannot_Delete_Contains_User_Album_Parent_Hdr", request.getLocale()) + ": " + ex.getMessage());
		}
    	
    	return "redirect:/cm/galleryview?g=cm_deletealbum";
    }
}
