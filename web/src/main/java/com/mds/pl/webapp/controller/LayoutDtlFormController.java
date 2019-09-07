package com.mds.pl.webapp.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.pl.service.LayoutDtlManager;
import com.mds.pl.service.LayoutMstManager;
import com.mds.sys.util.UserUtils;
import com.mds.pl.model.LayoutDtl;
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
@RequestMapping("/pl/layoutDtlform*")
public class LayoutDtlFormController extends BaseFormController {
    private LayoutDtlManager layoutDtlManager = null;
    private LayoutMstManager layoutMstManager = null;

    @Autowired
    public void setLayoutDtlManager(LayoutDtlManager layoutDtlManager) {
        this.layoutDtlManager = layoutDtlManager;
    }
    
    @Autowired
    public void setLayoutMstManager(LayoutMstManager layoutMstManager) {
        this.layoutMstManager = layoutMstManager;
    }

    public LayoutDtlFormController() {
        setCancelView("redirect:layoutDtls");
        setSuccessView("redirect:layoutDtls");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected LayoutDtl showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return layoutDtlManager.get(new Long(id));
        }

        return new LayoutDtl();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(LayoutDtl layoutDtl, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(layoutDtl, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "layoutDtlform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (layoutDtl.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            layoutDtlManager.remove(layoutDtl.getId());
            saveMessage(request, getText("layoutDtl.deleted", locale));
        } else {
        	if (layoutDtl.getLayoutMst() == null){
        		if (!StringUtils.isBlank(request.getParameter("layoutId"))) {
        			layoutDtl.setLayoutMst(layoutMstManager.get(new Long(request.getParameter("layoutId"))));
        		}
        	}
        	layoutDtl.fillLog(UserUtils.getLoginName(), isNew);
            layoutDtlManager.save(layoutDtl);
            String key = (isNew) ? "layoutDtl.added" : "layoutDtl.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:layoutDtlform?id=" + layoutDtl.getId();
            }
        }

        return success;
    }
}
