/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.cm.util.AlbumTreeViewBuilder;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.GalleryUtils;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.content.GalleryBoCollection;
import com.mds.aiotplayer.cm.content.GalleryControlSettings;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.content.UiTemplateBo;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.cm.rest.CMData;
import com.mds.aiotplayer.cm.rest.CMDataLoadOptions;
import com.mds.aiotplayer.cm.rest.SettingsRest;
import com.mds.aiotplayer.cm.rest.TreeView;
import com.mds.aiotplayer.cm.rest.TreeViewOptions;

import com.mds.aiotplayer.webapp.common.controller.AbstractBaseController;
import com.mds.aiotplayer.webapp.common.controller.BaseController;
import com.mds.aiotplayer.core.LongCollection;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.SecurityActionsOption;
import com.mds.aiotplayer.core.SlideShowType;
import com.mds.aiotplayer.core.UiTemplateType;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;

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
