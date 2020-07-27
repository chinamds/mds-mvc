package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.NotificationTemplateManager;
import com.mds.aiotplayer.sys.model.NotificationTemplate;
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
@RequestMapping("/notificationTemplateform*")
public class NotificationTemplateFormController extends BaseFormController {
    private NotificationTemplateManager notificationTemplateManager = null;

    @Autowired
    public void setNotificationTemplateManager(NotificationTemplateManager notificationTemplateManager) {
        this.notificationTemplateManager = notificationTemplateManager;
    }

    public NotificationTemplateFormController() {
        setCancelView("redirect:notificationTemplates");
        setSuccessView("redirect:notificationTemplates");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected NotificationTemplate showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return notificationTemplateManager.get(new Long(id));
        }

        return new NotificationTemplate();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(NotificationTemplate notificationTemplate, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(notificationTemplate, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "notificationTemplateform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (notificationTemplate.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            notificationTemplateManager.remove(notificationTemplate.getId());
            saveMessage(request, getText("notificationTemplate.deleted", locale));
        } else {
            notificationTemplateManager.save(notificationTemplate);
            String key = (isNew) ? "notificationTemplate.added" : "notificationTemplate.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:notificationTemplateform?id=" + notificationTemplate.getId();
            }
        }

        return success;
    }
}
