package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.ModuleManager;
import com.mds.aiotplayer.sys.model.Module;
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
@RequestMapping("/sys/moduleform*")
public class ModuleFormController extends BaseFormController {
    private ModuleManager moduleManager = null;

    @Autowired
    public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public ModuleFormController() {
        setCancelView("redirect:modules");
        setSuccessView("redirect:modules");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Module showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return moduleManager.get(new Long(id));
        }

        return new Module();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Module module, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(module, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "moduleform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (module.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            moduleManager.remove(module.getId());
            saveMessage(request, getText("module.deleted", locale));
        } else {
            moduleManager.save(module);
            String key = (isNew) ? "module.added" : "module.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:moduleform?id=" + module.getId();
            }
        }

        return success;
    }
}
