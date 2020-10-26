/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.cm.service.GalleryMappingManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.cm.model.GalleryMapping;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/cm/galleryMappingform*")
public class GalleryMappingFormController extends BaseFormController {
    private GalleryMappingManager galleryMappingManager = null;
    private GalleryManager galleryManager = null;

    @Autowired
    public void setGalleryMappingManager(GalleryMappingManager galleryMappingManager) {
        this.galleryMappingManager = galleryMappingManager;
    }
    
    @Autowired
    public void setGalleryManager(GalleryManager galleryManager) {
        this.galleryManager = galleryManager;
    }

    public GalleryMappingFormController() {
        setCancelView("redirect:galleryMappings");
        setSuccessView("redirect:galleryMappings");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected GalleryMapping showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return galleryMappingManager.get(new Long(id));
        }

        return new GalleryMapping();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(GalleryMapping galleryMapping, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(galleryMapping, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "galleryMappingform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (galleryMapping.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            galleryMappingManager.remove(galleryMapping.getId());
            saveMessage(request, getText("galleryMapping.deleted", locale));
        } else {
        	if (galleryMapping.getGallery() == null){
        		if (!StringUtils.isBlank(request.getParameter("galleryId"))) {
        			galleryMapping.setGallery(galleryManager.get(new Long(request.getParameter("galleryId"))));
        		}
        	}
        	//galleryMapping.fillLog(UserUtils.getLoginName(), isNew);
            galleryMappingManager.save(galleryMapping);
            String key = (isNew) ? "galleryMapping.added" : "galleryMapping.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:galleryMappingform?id=" + galleryMapping.getId();
            }
        }

        return success;
    }
}
