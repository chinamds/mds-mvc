/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.ps.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.ps.service.CalendarManager;
import com.mds.aiotplayer.pl.service.CatalogueManager;
import com.mds.aiotplayer.ps.service.ChannelManager;
import com.mds.aiotplayer.ps.model.Calendar;
import com.mds.aiotplayer.pl.model.Catalogue;
import com.mds.aiotplayer.ps.model.Channel;
import com.mds.aiotplayer.pl.model.Product;
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

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/sch/calendarform*")
public class CalendarFormController extends BaseFormController {
    private CalendarManager calendarManager = null;
    private ChannelManager channelManager = null;

    @Autowired
    public void setCalendarManager(CalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }
    
    @Autowired
    public void setChannelManager(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    public CalendarFormController() {
        setCancelView("redirect:calendars");
        setSuccessView("redirect:calendars");
    }
    
    @ModelAttribute("channelList")
    protected List<Channel> loadChannels(final HttpServletRequest request) {
        return channelManager.getAll();
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Calendar showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return calendarManager.get(new Long(id));       	
        }
        
        Calendar calendar = new Calendar();
        String channelId = request.getParameter("channelId");
        if (!StringUtils.isBlank(channelId)) {
        	calendar.setChannel(channelManager.get(new Long(channelId)));
        }

        return calendar;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Calendar calendar, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(calendar, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "calendarform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (calendar.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            calendarManager.remove(calendar.getId());
            saveMessage(request, getText("calendar.deleted", locale));
        } else {
        	//calendar.setChannel(channelManager.get(calendar.getChannelId()));
        	if (calendar.getChannel() == null){
        		if (!StringUtils.isBlank(request.getParameter("channelId"))) {
        			calendar.setChannel(channelManager.get(new Long(request.getParameter("channelId"))));
        		}
        	}
            calendarManager.save(calendar);
            String key = (isNew) ? "calendar.added" : "calendar.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:sch/calendarform?id=" + calendar.getId();
            }
        }

        return success;
    }
}
