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
@RequestMapping("/cm/contentobjects/rotateimages*")
public class RotateImagesController extends BaseFormController {
	private ContentObjectManager contentObjectManager;

    @Autowired
    public void setContentObjectManager(ContentObjectManager contentObjectManager) {
        this.contentObjectManager = contentObjectManager;
    }
    
    public RotateImagesController() {
        setCancelView("redirect:galleryview");
        setSuccessView("redirect:galleryview");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@RequestParam(value = "hdnCheckedContentObjectIds", required = true) long[] ids
    		, @RequestParam(value = "hdnSelectedSide", required = false) String[] rotateTags
    		, @RequestParam(value = "albumId", required = true) long aid, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null || rotateTags.length < 1) {
        	return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");
        }
        
        Map<Long, ContentObjectRotation> imagesToRotate = 	retrieveUserSelections(ids, rotateTags);
        
        int msg = Integer.MIN_VALUE;
        if (!imagesToRotate.isEmpty()) {
        	msg = contentObjectManager.saveImageRotates(retrieveUserSelections(ids, rotateTags));
        }
		HelperFunctions.purgeCache();
		
		if (msg > Integer.MIN_VALUE)
			return "redirect:" + Utils.getUrl("/cm/galleryview", ResourceId.album, "aid={0}&msg={1}", aid, Integer.toString(msg));
		else
			return "redirect:" + Utils.getUrl("/cm/galleryview", ResourceId.album, "aid={0}&msg={1}", aid, Integer.toString(MessageType.ObjectsSuccessfullyRotated.value()));
    }
    
    private Map<Long, ContentObjectRotation> retrieveUserSelections(long[] ids, String[] rotateTags){
		// Iterate through all the objects, retrieving the orientation of each image. If the
		// orientation has changed (it is no longer set to 't' for top), then add it to an array.
		// The content object IDs are stored in a hidden input tag.

    	Map<Long, ContentObjectRotation> imagesToRotate = new HashMap<Long, ContentObjectRotation>();

    	int i=0;
		for (long id : ids)	{
			String rotateTag = rotateTags[i];

			if (rotateTag.trim().length() < 1)
				continue;

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
			
			imagesToRotate.put(id, r);
		}
		
		return imagesToRotate;
	}
}
