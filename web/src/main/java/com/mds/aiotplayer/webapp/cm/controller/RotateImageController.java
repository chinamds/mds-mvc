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
import com.mds.aiotplayer.cm.metadata.ContentObjectMetadataItem;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.cm.model.ContentObject;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.core.ContentObjectRotation;
import com.mds.aiotplayer.core.MessageType;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.UnexpectedFormValueException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/cm/contentobjects/rotateimage*")
public class RotateImageController extends BaseFormController {
	
    public RotateImageController() {
        setCancelView("redirect:galleryview");
        setSuccessView("redirect:galleryview");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@RequestParam(value = "moid", required = true) long moid
    		, @RequestParam(value = "hdnSelectedSide", required = false) String rotateTag, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null || rotateTag.trim().length() < 1) {
        	return "redirect:/cm/galleryview?g=contentobject&moid=" + moid;        	
        }
        
		String newOrientation = rotateTag.trim().substring(0, 1);
		// If the orientation value isn't valid, throw an exception.
		if ((!newOrientation.equals("t")) && (!newOrientation.equals("r")) 
				&& (!newOrientation.equals("b")) && (!newOrientation.equals("l"))) {
			throw new UnexpectedFormValueException();
		}

		// User selected an orientation other than t(top). Add to array.
		ContentObjectRotation r;
		switch (newOrientation)	{
			case "t": r = ContentObjectRotation.Rotate0; break;
			case "r": r = ContentObjectRotation.Rotate270; break;
			case "b": r = ContentObjectRotation.Rotate180; break;
			case "l": r = ContentObjectRotation.Rotate90; break;
			default: r = ContentObjectRotation.Rotate0; break; // Should never get here because of our if condition above, but let's be safe
		}
		
		int msg = rotateImage(moid, r);
		if (msg > Integer.MIN_VALUE)
			return "redirect:" + Utils.getUrl("/cm/galleryview", ResourceId.contentobject, "moid={0}&msg={1}", moid, Integer.toString(msg));
		else
			return "redirect:" + Utils.getUrl("/cm/galleryview", ResourceId.contentobject, "moid={0}&msg={1}", moid, Integer.toString(MessageType.ObjectsSuccessfullyRotated.value()));
    }
        
    private int rotateImage(long moid, ContentObjectRotation r) throws InvalidContentObjectException, com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException, InvalidAlbumException, UnsupportedImageTypeException, InvalidGalleryException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException{
		// Rotate any images on the hard drive according to the user's wish.
		int returnValue = Integer.MIN_VALUE;

		ContentObjectBo mo = CMUtils.loadContentObjectInstance(moid, true);

		ContentObjectMetadataItem metaOrientation;
		if (r == ContentObjectRotation.Rotate0 
				&& ((metaOrientation = mo.getMetadataItems().tryGetMetadataItem(MetadataItemName.Orientation)) != null)){
			return returnValue;
		}

		mo.setRotation(r);
		
		try{
			ContentObjectUtils.saveContentObject(mo);
		}catch (UnsupportedImageTypeException ue){
			returnValue = MessageType.CannotRotateInvalidImage.value();
		}

		HelperFunctions.purgeCache();

		return returnValue;
	}
}
