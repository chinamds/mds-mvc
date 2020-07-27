package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.AreaManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.sys.exception.AreaExistsException;
import com.mds.aiotplayer.sys.model.Area;
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
@RequestMapping("/sys/areaform*")
public class AreaFormController extends BaseFormController {
    private AreaManager areaManager = null;

    @Autowired
    public void setAreaManager(AreaManager areaManager) {
        this.areaManager = areaManager;
    }

    public AreaFormController() {
        setCancelView("redirect:areas");
        setSuccessView("redirect:areas");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Area showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return areaManager.get(new Long(id));
        }

        Area area = new Area();
        String parentId = request.getParameter("parentId");
        if (!StringUtils.isBlank(parentId)) {
        	area.setParent(areaManager.get(new Long(parentId)));
        }

        return area;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Area area, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(area, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/areaform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (area.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();
        if (area.getParent() != null && area.getParent().getId() != null){
        	area.setParent(areaManager.get(area.getParent().getId()));
        }else{
        	area.setParent(null);
        }
        area.setCurrentUser(UserUtils.getLoginName());

        if (request.getParameter("delete") != null) {
            areaManager.remove(area.getId());
            saveMessage(request, getText("area.deleted", locale));
        } else {
        	//area.fillLog(UserUtils.getLoginName(), isNew);
        	try {
        		areaManager.saveArea(area);
        	}catch(final AreaExistsException e) {
        		errors.rejectValue("code", "area.existing.error",
                        new Object[] { area.getCode() }, "area code existing");
        		areaManager.clear();
        		if (isNew) {
        			area.setId(null);
        		}

                return "sys/areaform";
        	}
            //areaManager.save(area);
            String key = (isNew) ? "area.added" : "area.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:areaform?id=" + area.getId();
            }
        }

        return success;
    }
}
