package com.mds.cm.webapp.controller;

import com.mds.cm.util.AlbumUtils;
import com.mds.cm.util.CMUtils;
import com.mds.core.LongCollection;
import com.mds.core.SecurityActions;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.sys.util.RoleUtils;
import com.mds.sys.util.UserUtils;
import com.mds.util.DateUtils;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.GalleryBo;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.model.Album;
import com.mds.cm.rest.TreeViewOptions;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cm/albumtreepickers*")
public class AlbumTreePickerController {
   
    @RequestMapping(method = RequestMethod.GET) 
    public Model handleRequest(@RequestParam(value = "gid", required = false) final Long galleryId, @RequestParam(value = "aid", required = false)final Long aid) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException {
    	Model model = new ExtendedModelMap();
    	long gId = galleryId == null ? Long.MIN_VALUE : galleryId;
    	AlbumBo albumToSelect = null;
    	if (galleryId == null || galleryId == Long.MIN_VALUE){
			if (aid != null && aid > Long.MIN_VALUE){
				albumToSelect = AlbumUtils.loadAlbumInstance(aid, false);
				gId = albumToSelect.getGalleryId();
			}else{
				// There is no album or content object to get the gallery ID from, and no gallery ID has been specified on the control.
				// Just grab the first gallery in the database, creating it if necessary.
				GalleryBo gallery = CMUtils.loadLoginUserGalleries().stream().findFirst().orElse(null);
				if (gallery != null){
					gId = gallery.getGalleryId();
					albumToSelect = CMUtils.loadRootAlbum(gallery.getGalleryId(), RoleUtils.getMDSRolesForUser(), UserUtils.isAuthenticated());
				}
				
			}
		}else {
			albumToSelect = CMUtils.loadRootAlbum(gId, RoleUtils.getMDSRolesForUser(), UserUtils.isAuthenticated());
		}

    	model.addAttribute("requiredSecurityPermissions", SecurityActions.ViewAlbumOrContentObject.value());
 		if (gId != Long.MIN_VALUE)	{
 			model.addAttribute("galleryId", gId);
 		}
 		
 		if (albumToSelect != null) {
 			model.addAttribute("selectedAlbumIds", new LongCollection(new long[] { albumToSelect.getId() }));
 		}else {
 			model.addAttribute("selectedAlbumIds", new LongCollection(new long[] { 1 }));
 		}
    	
        return model;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String contentPicker(@RequestParam(value = "gid", required = false) final Long galleryId, @RequestParam(value = "aid", required = false)final Long aid, Model model) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException {
    	long gId = galleryId == null ? Long.MIN_VALUE : galleryId;
    	AlbumBo albumToSelect = null;
    	if (galleryId == null || galleryId == Long.MIN_VALUE){
			if (aid != null && aid > Long.MIN_VALUE){
				albumToSelect = AlbumUtils.loadAlbumInstance(aid, false);
				gId = albumToSelect.getGalleryId();
			}else{
				// There is no album or content object to get the gallery ID from, and no gallery ID has been specified on the control.
				// Just grab the first gallery in the database, creating it if necessary.
				GalleryBo gallery = CMUtils.loadLoginUserGalleries().stream().findFirst().orElse(null);
				if (gallery != null){
					gId = gallery.getGalleryId();
					albumToSelect = CMUtils.loadRootAlbum(gallery.getGalleryId(), RoleUtils.getMDSRolesForUser(), UserUtils.isAuthenticated());
				}
			}
		}else {
			albumToSelect = CMUtils.loadRootAlbum(gId, RoleUtils.getMDSRolesForUser(), UserUtils.isAuthenticated());
		}

    	model.addAttribute("requiredSecurityPermissions", SecurityActions.ViewAlbumOrContentObject.value());
 		if (gId != Long.MIN_VALUE)	{
 			model.addAttribute("galleryId", gId);
 		}
 		
 		if (albumToSelect != null) {
 			model.addAttribute("selectedAlbumIds", new LongCollection(new long[] { albumToSelect.getId() }));
 		}else {
 			model.addAttribute("selectedAlbumIds", new LongCollection(new long[] { 1 }));
 		}
    	
        return "cm/albumtreepickers";
    }
}
