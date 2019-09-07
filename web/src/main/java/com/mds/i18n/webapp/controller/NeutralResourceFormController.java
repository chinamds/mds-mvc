package com.mds.i18n.webapp.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.i18n.service.NeutralResourceManager;
import com.mds.i18n.model.NeutralResource;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.webapp.controller.BaseFormController;

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
@RequestMapping("/i18n/neutralResourceform*")
public class NeutralResourceFormController extends BaseFormController {
    private NeutralResourceManager neutralResourceManager = null;

    @Autowired
    public void setNeutralResourceManager(NeutralResourceManager neutralResourceManager) {
        this.neutralResourceManager = neutralResourceManager;
    }

    public NeutralResourceFormController() {
        setCancelView("redirect:neutralResources");
        setSuccessView("redirect:neutralResources");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected NeutralResource showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return neutralResourceManager.get(new Long(id));
        }

        return new NeutralResource();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(NeutralResource neutralResource, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(neutralResource, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "neutralResourceform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (neutralResource.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            neutralResourceManager.remove(neutralResource.getId());
            saveMessage(request, getText("neutralResource.deleted", locale));
        } else {
        	try {
        		neutralResourceManager.saveNeutralResource(neutralResource);
        	}catch(final RecordExistsException e) {
        		errors.rejectValue("resourceKey", "neturalResource.existing.error",
                        new Object[] { neutralResource.getResourceKey() }, "netural resource existing");
        		neutralResourceManager.clear();
        		if (isNew) {
        			neutralResource.setId(null);
        		}

                return "neutralResourceform";
        	}
            String key = (isNew) ? "neutralResource.added" : "neutralResource.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:neutralResourceform?id=" + neutralResource.getId();
            }
        }

        return success;
    }
}
