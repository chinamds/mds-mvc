/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.MyMessageContentManager;
import com.mds.aiotplayer.sys.service.MyMessageManager;
import com.mds.aiotplayer.sys.model.MyMessageContent;
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
@RequestMapping("/myMessageContentform*")
public class MyMessageContentFormController extends BaseFormController {
    private MyMessageContentManager myMessageContentManager = null;
    private MyMessageManager myMessageManager = null;

    @Autowired
    public void setMyMessageContentManager(MyMessageContentManager myMessageContentManager) {
        this.myMessageContentManager = myMessageContentManager;
    }
    
    @Autowired
    public void setMyMessageManager(MyMessageManager myMessageManager) {
        this.myMessageManager = myMessageManager;
    }

    public MyMessageContentFormController() {
        setCancelView("redirect:myMessageContents");
        setSuccessView("redirect:myMessageContents");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected MyMessageContent showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return myMessageContentManager.get(new Long(id));
        }

        return new MyMessageContent();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(MyMessageContent myMessageContent, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(myMessageContent, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "myMessageContentform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (myMessageContent.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            myMessageContentManager.remove(myMessageContent.getId());
            saveMessage(request, getText("myMessageContent.deleted", locale));
        } else {
        	if (myMessageContent.getMyMessage() == null){
        		if (!StringUtils.isBlank(request.getParameter("mymessage_id"))) {
        			myMessageContent.setMyMessage(myMessageManager.get(new Long(request.getParameter("mymessage_id"))));
        		}
        	}
            myMessageContentManager.save(myMessageContent);
            String key = (isNew) ? "myMessageContent.added" : "myMessageContent.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:myMessageContentform?id=" + myMessageContent.getId();
            }
        }

        return success;
    }
}
