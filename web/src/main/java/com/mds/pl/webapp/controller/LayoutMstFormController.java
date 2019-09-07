package com.mds.pl.webapp.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.pl.service.LayoutMstManager;
import com.mds.pl.model.LayoutMst;
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
@RequestMapping("/pl/layoutMstform*")
public class LayoutMstFormController extends BaseFormController {
    private LayoutMstManager layoutMstManager = null;

    @Autowired
    public void setLayoutMstManager(LayoutMstManager layoutMstManager) {
        this.layoutMstManager = layoutMstManager;
    }

    public LayoutMstFormController() {
        setCancelView("redirect:layoutMsts");
        setSuccessView("redirect:layoutMsts");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected LayoutMst showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return layoutMstManager.get(new Long(id));
        }

        return new LayoutMst();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(LayoutMst layoutMst, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(layoutMst, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "layoutMstform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (layoutMst.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            layoutMstManager.remove(layoutMst.getId());
            saveMessage(request, getText("layoutMst.deleted", locale));
        } else {
            layoutMstManager.save(layoutMst);
            String key = (isNew) ? "layoutMst.added" : "layoutMst.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:layoutMstform?id=" + layoutMst.getId();
            }
        }

        return success;
    }
}
