package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.DictManager;
import com.mds.aiotplayer.sys.model.Dict;
import com.mds.aiotplayer.sys.model.DictCategory;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/sys/dictform*")
public class DictFormController extends BaseFormController {
    private DictManager dictManager = null;

    @Autowired
    public void setDictManager(DictManager dictManager) {
        this.dictManager = dictManager;
    }

    public DictFormController() {
        setCancelView("redirect:dicts");
        setSuccessView("redirect:dicts");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Dict showForm(HttpServletRequest request, Model model)
    throws Exception {
        String id = request.getParameter("id");
        model.addAttribute("categories", DictCategory.values());
   	 	//model.addAttribute("dictParam", dictParam);
        if (!StringUtils.isBlank(id)) {
            return dictManager.get(new Long(id));
        }

        return new Dict();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Dict dict, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response, Model model)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        model.addAttribute("categories", DictCategory.values());
        if (validator != null) { // validator is null during testing
            validator.validate(dict, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/dictform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (dict.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            dictManager.remove(dict.getId());
            saveMessage(request, getText("dict.deleted", locale));
        } else {
            dictManager.save(dict);
            String key = (isNew) ? "dict.added" : "dict.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:dictform?id=" + dict.getId();
            }
        }

        return success;
    }
}
