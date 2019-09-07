package com.mds.sys.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mds.sys.model.User;
import com.mds.common.service.MailEngine;
import com.mds.sys.service.UserManager;
import com.mds.sys.util.UserAccount;
import com.mds.i18n.util.I18nUtils;
import com.mds.common.webapp.controller.BaseFormController;
import com.mds.common.webapp.util.RequestUtil;
import com.mds.common.web.validate.AjaxResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Simple class to retrieve and send a password hint to users.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Controller
@RequestMapping("/sys/passwordHint*")
public class PasswordHintController {
    private final Logger log = LoggerFactory.getLogger(PasswordHintController.class);
    private UserManager userManager = null;
    private MessageSource messageSource = null;
    protected MailEngine mailEngine = null;
    protected SimpleMailMessage message = null;

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setMailEngine(MailEngine mailEngine) {
        this.mailEngine = mailEngine;
    }

    @Autowired
    public void setMessage(SimpleMailMessage message) {
        this.message = message;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequest(HttpServletRequest request) //, RedirectAttributes redirectAttributes
    throws Exception {
        log.debug("entering 'handleRequest' method...");

        String username = request.getParameter("username");
        MessageSourceAccessor text = new MessageSourceAccessor(messageSource, request.getLocale());

        // ensure that the username has been sent
        if (username == null) {
            log.warn("Username not specified, notifying user that it's a required field.");
            request.setAttribute("error", text.getMessage("errors.required", text.getMessage("user.username")));
            return new ModelAndView("login");
        }

        log.debug("Processing Password Hint...");
        Locale locale = request.getLocale();

        String result=null;
        // look up the user's information
        try {
        	User user = userManager.getUserByUsername(username);

            StringBuffer msg = new StringBuffer();
            msg.append("Your password hint is: ").append(user.getPasswordHint());
            msg.append("\n\nLogin at: ").append(RequestUtil.getAppURL(request));

            message.setTo(user.getEmail());
            String subject = '[' + text.getMessage("webapp.name") + "] " + 
                             text.getMessage("user.passwordHint");
            message.setSubject(subject);
            message.setText(msg.toString());
            mailEngine.send(message);

            //saveMessage(request, text.getMessage("login.passwordHint.sent", new Object[] { username, user.getEmail() }));
            //redirectAttributes.addFlashAttribute(BaseFormController.MESSAGES_KEY, I18nUtils.getString("login.passwordHint.sent", locale, username, user.getEmail()));
            //model.getModelMap().addAttribute(BaseFormController.MESSAGES_KEY, I18nUtils.getString("login.passwordHint.sent", locale, username, user.getEmail()));
            result = I18nUtils.getString("login.passwordHint.sent", locale, username, user.getEmail());
        } catch (UsernameNotFoundException e) {
            log.warn(e.getMessage());
            //saveError(request, text.getMessage("login.passwordHint.error", new Object[] { username }));
            //redirectAttributes.addFlashAttribute("errors", I18nUtils.getString("login.passwordHint.error", locale, username));
            //model.getModelMap().addAttribute(BaseFormController.ERRORS_MESSAGES_KEY, I18nUtils.getString("login.passwordHint.error", locale, username));
        } catch (MailException me) {
            log.warn(me.getMessage());
           	//saveError(request, me.getLocalizedMessage());
           	//redirectAttributes.addFlashAttribute("errors", me.getLocalizedMessage());
            //model.getModelMap().addAttribute(BaseFormController.ERRORS_MESSAGES_KEY, me.getLocalizedMessage());
        }

        return new ModelAndView(new RedirectView(request.getContextPath())).addObject(BaseFormController.MESSAGES_KEY, result);
    }
    
    @RequestMapping(value = "send", method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> getPasswordHint(HttpServletRequest request,
            @RequestParam(value = "username", required = false) String username)
    throws Exception {
        log.debug("entering 'getPasswordHint' method...");

        MessageSourceAccessor text = new MessageSourceAccessor(messageSource, request.getLocale());

        // ensure that the username has been sent
        if (username == null) {
            log.warn("Username not specified, notifying user that it's a required field.");
            return AjaxResponse.fail(text.getMessage("errors.required", text.getMessage("user.username"))).result();
        }

        log.debug("Processing Password Hint...");
        Locale locale = request.getLocale();

        // look up the user's information
        try {
            User user = userManager.getUserByUsername(username);

            StringBuffer msg = new StringBuffer();
            msg.append("Your password hint is: ").append(user.getPasswordHint());
            msg.append("\n\nLogin at: ").append(RequestUtil.getAppURL(request));

            message.setTo(user.getEmail());
            String subject = '[' + I18nUtils.getString("webapp.name", locale) + "] " + 
            		I18nUtils.getString("user.passwordHint", locale);
            message.setSubject(subject);
            message.setText(msg.toString());
            mailEngine.send(message);

            return AjaxResponse.success(I18nUtils.getString("login.passwordHint.sent", locale, username, user.getEmail())).result();
        } catch (UsernameNotFoundException e) {
            log.warn(e.getMessage());
            
            return AjaxResponse.fail(I18nUtils.getString("login.passwordHint.error", locale, username)).result();
        } catch (MailException me) {
            log.warn(me.getMessage());
           	//saveError(request, me.getCause().getLocalizedMessage());
           	//redirectAttributes.addFlashAttribute("errors", me.getLocalizedMessage());
            return AjaxResponse.fail(I18nUtils.getString("login.passwordHint.sendemailfailed", locale, me.getCause() == null ? me.getMessage() : me.getCause().getLocalizedMessage())).result();
        }
    }

    @SuppressWarnings("unchecked")
    public void saveError(HttpServletRequest request, String error) {
        List errors = (List) request.getSession().getAttribute("errors");
        if (errors == null) {
            errors = new ArrayList();
        }
        errors.add(error);
        request.getSession().setAttribute("errors", errors);
    }

    // this method is also in BaseForm Controller
    @SuppressWarnings("unchecked")
    public void saveMessage(HttpServletRequest request, String msg) {
        List messages = (List) request.getSession().getAttribute(BaseFormController.MESSAGES_KEY);
        if (messages == null) {
            messages = new ArrayList();
        }
        messages.add(msg);
        request.getSession().setAttribute(BaseFormController.MESSAGES_KEY, messages);
    }
}
