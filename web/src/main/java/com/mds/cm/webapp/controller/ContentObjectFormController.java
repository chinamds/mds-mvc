package com.mds.cm.webapp.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.cm.service.ContentObjectManager;
import com.mds.cm.model.ContentObject;
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
@RequestMapping("/cm/contentObjectform*")
public class ContentObjectFormController extends BaseFormController {
    private ContentObjectManager contentObjectManager = null;

    @Autowired
    public void setContentObjectManager(ContentObjectManager contentObjectManager) {
        this.contentObjectManager = contentObjectManager;
    }

    public ContentObjectFormController() {
        setCancelView("redirect:galleries");
        setSuccessView("redirect:galleries");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected ContentObject showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return contentObjectManager.get(new Long(id));
        }

        return new ContentObject();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(ContentObject contentObject, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(contentObject, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "contentObjectform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (contentObject.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            contentObjectManager.remove(contentObject.getId());
            saveMessage(request, getText("contentObject.deleted", locale));
        } else {
            contentObjectManager.save(contentObject);
            String key = (isNew) ? "contentObject.added" : "contentObject.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:contentObjectform?id=" + contentObject.getId();
            }
        }

        return success;
    }
}
