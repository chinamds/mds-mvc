package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.MyCalendarManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.sys.model.MyCalendar;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/sys/myCalendarform*")
public class MyCalendarFormController extends BaseFormController {
    private MyCalendarManager myCalendarManager = null;

    @Autowired
    public void setMyCalendarManager(MyCalendarManager myCalendarManager) {
        this.myCalendarManager = myCalendarManager;
    }

    public MyCalendarFormController() {
        setCancelView("redirect:myCalendars");
        setSuccessView("redirect:myCalendars");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected MyCalendar showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return myCalendarManager.get(Long.valueOf(id));
        }

        return new MyCalendar();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(MyCalendar myCalendar, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(myCalendar, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "myCalendarform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (myCalendar.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            myCalendarManager.remove(myCalendar.getId());
            saveMessage(request, getText("myCalendar.deleted", locale));
        } else {
        	if (myCalendar.getUser() == null){
        		if (!StringUtils.isBlank(request.getParameter("user_id"))) {
        			myCalendar.setUser(getUserManager().get(Long.valueOf(request.getParameter("user_id"))));
        		}else{
        			myCalendar.setUser(getUserManager().get(UserUtils.getUserId()));
        		}
        	}
            myCalendarManager.save(myCalendar);
            String key = (isNew) ? "myCalendar.added" : "myCalendar.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:sys/myCalendarform?id=" + myCalendar.getId();
            }
        }

        return success;
    }
}
