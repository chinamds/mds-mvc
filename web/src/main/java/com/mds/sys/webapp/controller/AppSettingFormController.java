package com.mds.sys.webapp.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.sys.service.AppSettingManager;
import com.mds.sys.model.AppSetting;
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
@RequestMapping("/appSettingform*")
public class AppSettingFormController extends BaseFormController {
    private AppSettingManager appSettingManager = null;

    @Autowired
    public void setAppSettingManager(AppSettingManager appSettingManager) {
        this.appSettingManager = appSettingManager;
    }

    public AppSettingFormController() {
        setCancelView("redirect:appSettings");
        setSuccessView("redirect:appSettings");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected AppSetting showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return appSettingManager.get(new Long(id));
        }

        return new AppSetting();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(AppSetting appSetting, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(appSetting, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/appSettingform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (appSetting.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            appSettingManager.remove(appSetting.getId());
            saveMessage(request, getText("appSetting.deleted", locale));
        } else {
            appSettingManager.save(appSetting);
            String key = (isNew) ? "appSetting.added" : "appSetting.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:appSettingform?id=" + appSetting.getId();
            }
        }

        return success;
    }
}
