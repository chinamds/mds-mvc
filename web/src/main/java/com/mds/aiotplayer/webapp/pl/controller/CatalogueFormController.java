/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.pl.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.pl.service.CatalogueManager;
import com.mds.aiotplayer.pl.model.Catalogue;
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
@RequestMapping("/pl/catalogueform*")
public class CatalogueFormController extends BaseFormController {
    private CatalogueManager catalogueManager = null;

    @Autowired
    public void setCatalogueManager(CatalogueManager catalogueManager) {
        this.catalogueManager = catalogueManager;
    }

    public CatalogueFormController() {
        setCancelView("redirect:catalogues");
        setSuccessView("redirect:catalogues");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Catalogue showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return catalogueManager.get(new Long(id));
        }

        return new Catalogue();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Catalogue catalogue, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(catalogue, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "catalogueform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (catalogue.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            catalogueManager.remove(catalogue.getId());
            saveMessage(request, getText("catalogue.deleted", locale));
        } else {
            catalogueManager.save(catalogue);
            String key = (isNew) ? "catalogue.added" : "catalogue.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:pl/catalogueform?id=" + catalogue.getId();
            }
        }

        return success;
    }
}
