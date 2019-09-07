package com.mds.cm.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.cm.service.GalleryManager;
import com.mds.cm.util.AlbumTreeViewBuilder;
import com.mds.cm.util.AlbumUtils;
import com.mds.cm.util.CMUtils;
import com.mds.cm.util.GalleryUtils;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.GalleryBo;
import com.mds.cm.content.GalleryBoCollection;
import com.mds.cm.content.GalleryControlSettings;
import com.mds.cm.content.GallerySettings;
import com.mds.cm.content.UiTemplateBo;
import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.exception.InvalidAlbumException;
import com.mds.cm.exception.InvalidContentObjectException;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.cm.model.Gallery;
import com.mds.cm.rest.CMData;
import com.mds.cm.rest.CMDataLoadOptions;
import com.mds.cm.rest.SettingsRest;
import com.mds.cm.rest.TreeView;
import com.mds.cm.rest.TreeViewOptions;

import com.mds.common.webapp.controller.AbstractBaseController;
import com.mds.common.webapp.controller.BaseController;
import com.mds.core.LongCollection;
import com.mds.core.ResourceId;
import com.mds.core.SecurityActions;
import com.mds.core.SecurityActionsOption;
import com.mds.core.SlideShowType;
import com.mds.core.UiTemplateType;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.ArgumentOutOfRangeException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.core.exception.WebException;
import com.mds.sys.util.RoleUtils;
import com.mds.sys.util.UserUtils;
import com.mds.util.DateUtils;
import com.mds.util.StringUtils;
import com.mds.util.Utils;

import java.io.IOException;
import java.text.MessageFormat;

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
@RequestMapping("/cm/galleries*")
public class GalleryController extends AbstractBaseController<Gallery, Long> {
    private GalleryManager galleryManager;

    @Autowired
    public void setGalleryManager(GalleryManager galleryManager) {
        this.galleryManager = galleryManager;
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequestDefault(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(galleryManager.search(query, Gallery.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(galleryManager.getAll());
        }
        return model;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        
        return model;
    }
}
