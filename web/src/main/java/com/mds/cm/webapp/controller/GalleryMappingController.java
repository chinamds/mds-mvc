package com.mds.cm.webapp.controller;

import com.mds.common.exception.SearchException;
import com.mds.cm.service.GalleryMappingManager;
import com.mds.cm.model.GalleryMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cm/galleryMappings*")
public class GalleryMappingController {
    private GalleryMappingManager galleryMappingManager;

    @Autowired
    public void setGalleryMappingManager(GalleryMappingManager galleryMappingManager) {
        this.galleryMappingManager = galleryMappingManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(galleryMappingManager.search(query, GalleryMapping.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(galleryMappingManager.getAll());
        }
        return model;
    }
}
