/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.mds.aiotplayer.cm.model.UiTemplate;
import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.cm.service.UiTemplateManager;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.web.validate.AjaxResponse;
import com.mds.aiotplayer.core.UiTemplateType;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

@Controller
@RequestMapping("/cm/uiTemplateform*")
public class UiTemplateFormController extends BaseFormController {
    private UiTemplateManager uiTemplateManager = null;
    private GalleryManager galleryManager;

    @Autowired
    public void setUiTemplateManager(UiTemplateManager uiTemplateManager) {
        this.uiTemplateManager = uiTemplateManager;
    }
    
    @Autowired
    public void setGalleryManager(GalleryManager galleryManager) {
        this.galleryManager = galleryManager;
    }

    public UiTemplateFormController() {
        setCancelView("redirect:uiTemplates");
        setSuccessView("redirect:uiTemplates");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
    	
        String id = request.getParameter("id");

        UiTemplate uiTemplate = new UiTemplate();
        if (!StringUtils.isBlank(id)) {
        	uiTemplate = uiTemplateManager.get(Long.valueOf(id));
        }
        
        model.addAttribute("templateTypes", getTemplateTypes(request));
        model.addAttribute("uiTemplate", uiTemplate);
        
        return model;
    }
    
    private List<Map<String, Object>> getTemplateTypes(HttpServletRequest request) {
    	List<Map<String, Object>> templateTypes = new ArrayList<>();
		for(UiTemplateType templateType : UiTemplateType.values()) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("templateType", templateType);
			map.put("info", I18nUtils.getString(UiTemplateType.class, templateType.toString(), request));
			templateTypes.add(map);
		}
   	
    	return templateTypes;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> onSubmit(UiTemplate uiTemplate, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response, Model model)
    throws Exception {
    	Locale locale = request.getLocale();
    	if (request.getParameter("delete") != null) {
    		uiTemplateManager.remove(uiTemplate.getId());
            saveMessage(request, getText("uiTemplate.deleted", locale));
            
            return AjaxResponse.success(I18nUtils.getString("uiTemplate.deleted", locale)).result();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(uiTemplate, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return AjaxResponse.fail().result();
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (uiTemplate.getId() == null);
        
        try {
        	if (uiTemplate.getGallery() != null) {
            	uiTemplate.setGallery(galleryManager.get(uiTemplate.getGallery().getId()));
            }
        	
    		uiTemplateManager.saveUiTemplate(uiTemplate);
    		HelperFunctions.purgeCache();
    		
    		return AjaxResponse.success(I18nUtils.getString(isNew ? "uiTemplate.added" : "uiTemplate.updated", locale)).result();
    	}catch(final RecordExistsException e) {
    		errors.rejectValue("name", "uiTemplate.existing.error",
                    new Object[] { uiTemplate.getName() }, "uiTemplate name existing");
    		uiTemplateManager.clear();
    		if (isNew) {
    			uiTemplate.setId(null);
    		}

    		return AjaxResponse.fail(I18nUtils.getString("exception.data_Ex_Msg", locale, e.getMessage())).result();
    	}
    }
}
