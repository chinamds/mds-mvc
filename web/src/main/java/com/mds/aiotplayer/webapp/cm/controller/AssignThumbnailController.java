package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.service.ContentObjectManager;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.cm.util.TransferType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.exception.CannotDeleteAlbumException;
import com.mds.aiotplayer.cm.exception.CannotTransferAlbumToNestedDirectoryException;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.core.MessageType;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.exception.ArgumentException;
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
import java.util.Map;

@Controller
@RequestMapping("/cm/contentobjects/assignthumbnail*")
public class AssignThumbnailController extends BaseFormController {		
    public AssignThumbnailController() {
        setCancelView("redirect:galleryview");
        setSuccessView("redirect:galleryview");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@RequestParam(value = "albumId", required = true) long aid
    		, @RequestParam(value = "hdnCheckedContentObjectIds", required = false) String[] ids, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
        	return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");        	
        }
                
        if (ids.length == 0) {
        	saveError(request, getText("task.no_Objects_Selected_Dtl", request.getLocale()));
        	
        	return "redirect:/cm/galleryview?g=cm_assignthumbnail&aid=" + request.getParameter("albumId"); 
        }
        
        long thumbnailContentObjectId = parseAndValidateIdCode(ids[0], request);
        if (thumbnailContentObjectId != Long.MIN_VALUE) {
        	AlbumBo album = AlbumUtils.loadAlbumInstance(aid, true, true);
			album.setThumbnailContentObjectId(thumbnailContentObjectId);
			ContentObjectUtils.saveContentObject(album);
			
			HelperFunctions.purgeCache();
			
			return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId") + StringUtils.format("&msg={0}", MessageType.ThumbnailSuccessfullyAssigned.value());
        }
            	
        return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");
    }
    
    private long parseAndValidateIdCode(String idCode, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidAlbumException, InvalidMDSRoleException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, GallerySecurityException, IOException, WebException{
		long id = Long.MIN_VALUE;
		if (StringUtils.isBlank(idCode) || (idCode.length() < 2)){
			return id;
		}

		// Step 1: Parse object type and ID from ID code
		long idToTest;

		// Make sure value is a valid content object ID
		idToTest = StringUtils.toLong(idCode.substring(1));
		if (idToTest == Long.MIN_VALUE) {
			return id;
		}

		// Step 2: Validate the ID. If it is an album, first get the thumbnail content object ID for the album.
		if (idCode.startsWith("a"))	{
			try	{
				idToTest = AlbumUtils.loadAlbumInstance(idToTest, false).getThumbnailContentObjectId();
				if (idToTest == 0){
					// User selected an album with a blank thumbnail. There is nothing to validate, so just return.
					id = idToTest;
					return id;
				}
			}catch (InvalidAlbumException ie){
				return id;
			}
		}

		try
		{
			ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(idToTest);
			String gid = request.getParameter("gid");
			if (Utils.isUserAuthorized(SecurityActions.ViewAlbumOrContentObject, contentObject.getParent().getId(), new Long(gid), contentObject.getIsPrivate(), false)){
				// VALID! Assign to output parameter and return.
				id = idToTest;
				return id;
			}
		}catch (ArgumentException ae){
			return id;
		}catch (InvalidContentObjectException ce){
			return id;
		}

		return id;
	}
}
