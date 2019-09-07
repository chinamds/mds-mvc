package com.mds.cm.webapp.controller;

import com.mds.cm.service.AlbumManager;
import com.mds.cm.util.AlbumUtils;
import com.mds.cm.util.CMUtils;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.exception.SearchException;
import com.mds.common.webapp.controller.AbstractBaseController;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.model.Organization;
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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cm/albums*")
public class AlbumController extends AbstractBaseController<Album, Long> {
    private AlbumManager albumManager;

    @Autowired
    public void setAlbumManager(AlbumManager albumManager) {
        this.albumManager = albumManager;
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(albumManager.search(query, Album.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(albumManager.getAll());
        }
        return model;
    }
    
    @RequestMapping(value ="albumtreeview", method = RequestMethod.GET) //value = {"", "main"}
    public String getTreeView(Model model) {
    	TreeViewOptions treeViewOptions = new TreeViewOptions();
    	model.addAttribute("treeViewOptions", treeViewOptions);
    	
        return viewName("albumtreeview");
    }
    
    @RequestMapping(method = RequestMethod.GET) //value = {"", "main"}
    public Model handleRequest(@RequestParam(value = "galleryId", required = false) final Long galleryId, @RequestParam(value = "aid", required = false)final Long aid, HttpServletRequest request) throws UnsupportedContentObjectTypeException, InvalidGalleryException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, IOException, InvalidMDSRoleException, GallerySecurityException, WebException, RecordExistsException {
    	Model model = new ExtendedModelMap();
    	/*long gId = galleryId == null ? Long.MIN_VALUE : galleryId;
    	AlbumBo album = null;
    	if (galleryId == null || galleryId == Long.MIN_VALUE)
		{
			if (aid != null && aid > Long.MIN_VALUE){
				album = AlbumUtils.loadAlbumInstance(aid, false);
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
					g.addOrganization(UserUtils.getUserOrganizationId());
					g.setDescription(I18nUtils.getString("webapp.name", request.getLocale()));
					g.setCreationDate(DateUtils.Now);
					
					g.setName("MDS System");
					g.setDescription("MDS System");
					g.setCreationDate(DateUtils.Now);
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
    	model.addAttribute("album", album);*/
    	
        return model;
    }
}
