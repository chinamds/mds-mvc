/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.ps.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.ps.service.ChannelManager;
import com.mds.aiotplayer.ps.model.Channel;
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
@RequestMapping("/sch/channelform*")
public class ChannelFormController extends BaseFormController {
    private ChannelManager channelManager = null;

    @Autowired
    public void setChannelManager(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    public ChannelFormController() {
        setCancelView("redirect:channels");
        setSuccessView("redirect:channels");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Channel showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return channelManager.get(new Long(id));
        }

        return new Channel();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Channel channel, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(channel, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "channelform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (channel.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            channelManager.remove(channel.getId());
            saveMessage(request, getText("channel.deleted", locale));
        } else {
            channelManager.save(channel);
            String key = (isNew) ? "channel.added" : "channel.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:sch/channelform?id=" + channel.getId();
            }
        }

        return success;
    }
}
