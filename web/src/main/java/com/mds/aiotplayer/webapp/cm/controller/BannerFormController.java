/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.cm.service.BannerManager;
import com.mds.aiotplayer.cm.model.Banner;
import com.mds.aiotplayer.common.model.JTableResult;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/cm/bannerform*")
public class BannerFormController extends BaseFormController {
    private BannerManager bannerManager = null;

    @Autowired
    public void setBannerManager(BannerManager bannerManager) {
        this.bannerManager = bannerManager;
    }

    public BannerFormController() {
        setCancelView("redirect:banners");
        setSuccessView("redirect:banners");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Banner showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return bannerManager.get(new Long(id));
        }

        return new Banner();
    }
        
    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Banner banner, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(banner, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "bannerform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (banner.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            bannerManager.remove(banner.getId());
            saveMessage(request, getText("banner.deleted", locale));
        } else {
            bannerManager.save(banner);
            String key = (isNew) ? "banner.added" : "banner.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:bannerform?id=" + banner.getId();
            }
        }

        return success;
    }
}
