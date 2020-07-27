package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.NotificationManager;
import com.mds.aiotplayer.sys.model.Notification;
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
@RequestMapping("/notificationform*")
public class NotificationFormController extends BaseFormController {
    private NotificationManager notificationManager = null;

    @Autowired
    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public NotificationFormController() {
        setCancelView("redirect:notifications");
        setSuccessView("redirect:notifications");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Notification showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return notificationManager.get(new Long(id));
        }

        return new Notification();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Notification notification, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(notification, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "notificationform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (notification.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            notificationManager.remove(notification.getId());
            saveMessage(request, getText("notification.deleted", locale));
        } else {
        	if (notification.getUser() == null){
        		if (!StringUtils.isBlank(request.getParameter("user_id"))) {
        			notification.setUser(getUserManager().get(new Long(request.getParameter("user_id"))));
        		}
        	}
            notificationManager.save(notification);
            String key = (isNew) ? "notification.added" : "notification.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:notificationform?id=" + notification.getId();
            }
        }

        return success;
    }
}
