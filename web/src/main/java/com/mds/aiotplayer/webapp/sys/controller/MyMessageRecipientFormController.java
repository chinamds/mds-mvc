/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.aiotplayer.sys.service.MyMessageManager;
import com.mds.aiotplayer.sys.service.MyMessageRecipientManager;
import com.mds.aiotplayer.sys.model.MyMessageRecipient;
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
@RequestMapping("/myMessageRecipientform*")
public class MyMessageRecipientFormController extends BaseFormController {
    private MyMessageRecipientManager myMessageRecipientManager = null;
    private MyMessageManager myMessageManager = null;

    
    @Autowired
    public void setMyMessageManager(MyMessageManager myMessageManager) {
        this.myMessageManager = myMessageManager;
    }

    @Autowired
    public void setMyMessageRecipientManager(MyMessageRecipientManager myMessageRecipientManager) {
        this.myMessageRecipientManager = myMessageRecipientManager;
    }

    public MyMessageRecipientFormController() {
        setCancelView("redirect:myMessageRecipients");
        setSuccessView("redirect:myMessageRecipients");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected MyMessageRecipient showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return myMessageRecipientManager.get(new Long(id));
        }

        return new MyMessageRecipient();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(MyMessageRecipient myMessageRecipient, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(myMessageRecipient, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "myMessageRecipientform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (myMessageRecipient.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            myMessageRecipientManager.remove(myMessageRecipient.getId());
            saveMessage(request, getText("myMessageRecipient.deleted", locale));
        } else {
        	if (myMessageRecipient.getMyMessage() == null){
        		if (!StringUtils.isBlank(request.getParameter("mymessage_id"))) {
        			myMessageRecipient.setMyMessage(myMessageManager.get(new Long(request.getParameter("mymessage_id"))));
        		}
        	}
        	if (myMessageRecipient.getUser() == null){
        		if (!StringUtils.isBlank(request.getParameter("user_id"))) {
        			myMessageRecipient.setUser(getUserManager().get(new Long(request.getParameter("user_id"))));
        		}
        	}
            myMessageRecipientManager.save(myMessageRecipient);
            String key = (isNew) ? "myMessageRecipient.added" : "myMessageRecipient.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:myMessageRecipientform?id=" + myMessageRecipient.getId();
            }
        }

        return success;
    }
}
