package com.mds.aiotplayer.webapp.cm.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/cm/createalbum*")
public class CreateAlbumController extends BaseFormController {
    public CreateAlbumController() {
        setCancelView("redirect:galleryview");
        setSuccessView("redirect:galleryview");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Album album, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }
        
        String pid = request.getParameter("hdnCheckedAlbumIds");
        String gid = request.getParameter("gid");

        if (validator != null) { // validator is null during testing
            validator.validate(album, errors);
            
            if (StringUtils.isBlank(gid) && StringUtils.isBlank(pid) && album.getParent() == null) {
                errors.rejectValue("parent", "errors.required", new Object[] { getText("album.parent", request.getLocale()) },
                        "Parent is a required field.");
            }

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "cm/createalbum";
            }
        }

        log.debug("entering 'onSubmit' method...");
        
        boolean isNew = (album.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

    	//User clicked 'Create album'. Create the new album and return the new album ID.
    	AlbumBo parentAlbum = null;
    	if (album.getParent() != null)
    		parentAlbum = CMUtils.loadAlbumInstance(album.getParent().getId(), true, true);
    	else if (pid != null)
    		parentAlbum = CMUtils.loadAlbumInstance(new Long(pid), true, true);
    	else if (gid != null)
    		parentAlbum = CMUtils.loadRootAlbumInstance(new Long(gid));
    	
		//this.CheckUserSecurity(SecurityActions.AddChildAlbum, parentAlbum);
		long newAlbumId;

		if (parentAlbum.getId() > 0){
			AlbumBo newAlbum = CMUtils.createEmptyAlbumInstance(parentAlbum.getGalleryId());
			newAlbum.setTitle(Utils.cleanHtmlTags(album.getName().trim(), parentAlbum.getGalleryId()));
			//newAlbum.ThumbnailContentObjectId = 0; // not needed
			newAlbum.setParent(parentAlbum);
			newAlbum.setIsPrivate(parentAlbum.getIsPrivate());
			ContentObjectUtils.saveContentObject(newAlbum);
			newAlbumId = newAlbum.getId();

			// Re-sort the items in the album. This will put the content object in the right position relative to its neighbors.
			((AlbumBo)newAlbum.getParent()).sort(true, UserUtils.getLoginName());

			HelperFunctions.purgeCache();
		}else {
			errors.rejectValue("parent", "errors.required", new Object[] { getText("album.parent", request.getLocale()) },
                    "Parent album is a required field.");
			
			return "cm/createalbum";
		}
		
        //albumManager.save(album);
        //String key = (isNew) ? "album.added" : "album.updated";
        //saveMessage(request, getText(key, locale));

        success = "redirect:galleryview?aid=" + newAlbumId;

        return success;
    }
}
