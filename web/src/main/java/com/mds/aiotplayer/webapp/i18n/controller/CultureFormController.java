/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.i18n.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.i18n.service.CultureManager;
import com.mds.aiotplayer.i18n.model.Culture;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/i18n/cultureform*")
public class CultureFormController extends BaseFormController {
    private CultureManager cultureManager = null;

    @Autowired
    public void setCultureManager(CultureManager cultureManager) {
        this.cultureManager = cultureManager;
    }

    public CultureFormController() {
        setCancelView("redirect:cultures");
        setSuccessView("redirect:cultures");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
        String id = request.getParameter("id");

        Culture culture = new Culture();
        if (!StringUtils.isBlank(id)) {
        	culture = cultureManager.get(Long.valueOf(id));
        	var tags = StringUtils.split(culture.getCultureCode(), '_'); 
        	model.addAttribute("locale", new Locale(tags[0], tags.length > 1 ? tags[1] : ""));
        }
        
        model.addAttribute("culture", culture);

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Culture culture, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(culture, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "cultureform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (culture.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            cultureManager.remove(culture.getId());
            cultureManager.clear();
            saveMessage(request, getText("culture.deleted", locale));
        } else {
            cultureManager.save(culture);
            String key = (isNew) ? "culture.added" : "culture.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:cultureform?id=" + culture.getId();
            }
        }

        return success;
    }
}
