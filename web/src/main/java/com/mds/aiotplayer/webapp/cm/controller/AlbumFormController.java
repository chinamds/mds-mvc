package com.mds.aiotplayer.webapp.cm.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
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
@RequestMapping("/cm/albumform*")
public class AlbumFormController extends BaseFormController {
    private AlbumManager albumManager = null;

    @Autowired
    public void setAlbumManager(AlbumManager albumManager) {
        this.albumManager = albumManager;
    }

    public AlbumFormController() {
        setCancelView("redirect:albums");
        setSuccessView("redirect:albums");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
        String id = request.getParameter("id");
        String pid = request.getParameter("pid");
        /* String gid = request.getParameter("gid");*/
        String title = StringUtils.EMPTY;

        Album album;
        if (!StringUtils.isBlank(id)) {
            album = albumManager.get(new Long(id));
            title = CMUtils.loadAlbumInstance(new Long(id), true).getTitle();
        }else {
        	album = new Album(albumManager.get(new Long(pid)));
        }
        /*model.addAttribute("pid", pid);
        model.addAttribute("gid", gid);*/
        model.addAttribute("album", album);
        model.addAttribute("title", title);

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Album album, String title, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        Locale locale = request.getLocale();
        if (StringUtils.isBlank(title)) {
        	saveError(request, getText("errors.required", getText("album.title", locale), locale));
        	
        	return "cm/albumform";
        }
                
        String pid = request.getParameter("pid");
        String gid = request.getParameter("gid");
        
        if (StringUtils.isBlank(gid) && StringUtils.isBlank(pid) && album.getParent() == null) {
            saveError(request, getText("errors.required", getText("album.parent", locale), locale));
            
            return "cm/albumform";
        }
        
        if (StringUtils.isBlank(album.getName())) {
        	album.setName(title);
        }

        if (validator != null) { // validator is null during testing
            validator.validate(album, errors);
            
            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "cm/albumform";
            }
        }

        log.debug("entering 'onSubmit' method...");
        
        boolean isNew = (album.getId() == null);
        String success = getSuccessView();

        if (request.getParameter("delete") != null) {
            //albumManager.remove(album.getId());
        	AlbumUtils.deleteAlbum(album.getId());
            saveMessage(request, getText("album.deleted", locale));
        } else {
			AlbumBo newAlbum = null;
			if (isNew) {
	        	//User clicked 'Create album'. Create the new album and return the new album ID.
	        	AlbumBo parentAlbum = null;
	        	if (album.getParent() != null)
	        		parentAlbum = CMUtils.loadAlbumInstance(album.getParent().getId(), true, true);
	        	else if (pid != null)
	        		parentAlbum = CMUtils.loadAlbumInstance(new Long(pid), true, true);
	        	else if (gid != null)
	        		parentAlbum = CMUtils.loadRootAlbumInstance(new Long(gid));
	        	
				//this.CheckUserSecurity(SecurityActions.AddChildAlbum, parentAlbum);
				//int newAlbumId;
	        	if (parentAlbum.getId() > 0){
					newAlbum = CMUtils.createEmptyAlbumInstance(parentAlbum.getGalleryId());
					newAlbum.setTitle(Utils.cleanHtmlTags(album.getName().trim(), parentAlbum.getGalleryId()));
					//newAlbum.ThumbnailContentObjectId = 0; // not needed
					newAlbum.setParent(parentAlbum);
					newAlbum.setIsPrivate(parentAlbum.getIsPrivate());
	        	}
			}else {
				newAlbum = CMUtils.loadAlbumInstance(album.getId(), true, true);
				newAlbum.setTitle(Utils.cleanHtmlTags(title.trim(), newAlbum.getGalleryId()));
			}
			ContentObjectUtils.saveContentObject(newAlbum);
			//newAlbumId = newAlbum.Id;

			// Re-sort the items in the album. This will put the content object in the right position relative to its neighbors.
			//((IAlbum)newAlbum.Parent).Sort(true, Utils.UserName);

			HelperFunctions.purgeCache();
			
            //albumManager.save(album);
            String key = (isNew) ? "album.added" : "album.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:albumform?id=" + album.getId();
            }
        }

        return success;
    }
}
