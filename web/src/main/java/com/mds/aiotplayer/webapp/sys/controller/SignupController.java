/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.RoleManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.sys.exception.UserExistsException;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.webapp.common.util.RequestUtil;
import com.mds.aiotplayer.security.model.MdsAuthenticationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

/**
 * Controller to signup new users.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Controller
@RequestMapping("/sys/signup*")
public class SignupController extends BaseFormController {
    private RoleManager roleManager;

    @Autowired
    public void setRoleManager(final RoleManager roleManager) {
        this.roleManager = roleManager;
    }

    public SignupController() {
        setCancelView("redirect:/login");
        setSuccessView("redirect:/home");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    public User showForm() {
        return new User();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(final User user, final BindingResult errors, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(user, errors);

            if (StringUtils.isBlank(user.getPassword())) {
                errors.rejectValue("password", "errors.required", new Object[] { getText("user.password", request.getLocale()) },
                        "Password is a required field.");
            }

            if (errors.hasErrors()) {
                return "sys/signup";
            }
        }

        final Locale locale = request.getLocale();

        user.setEnabled(true);

        // Set the default user role on this new user
        user.addRole(roleManager.getRolesByRoleType(RoleType.gt, Long.MIN_VALUE).get(0));

        // unencrypted users password to log in user automatically
        final String password = user.getPassword();

        try {
        	this.getUserManager().saveUser(user);
        } catch (final AccessDeniedException ade) {
            // thrown by UserSecurityAdvice configured in aop:advisor userManagerSecurity
            log.warn(ade.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        } catch (final UserExistsException e) {
            errors.rejectValue("username", "errors.existing.user",
                    new Object[] { user.getUsername(), user.getEmail() }, "duplicate user");
            
            this.getUserManager().clear();
           	user.setId(null);

            return "sys/signup";
        }

        saveMessage(request, getText("user.registered", user.getUsername(), locale));
        request.getSession().setAttribute(Constants.REGISTERED, Boolean.TRUE);

        // log user in automatically
        final String mobileDevice = request.getParameter("mobileDevice");
        //UserUtils.login(user, password, false, (mobileDevice != null && mobileDevice.equalsIgnoreCase("true")));
        // log user in automatically
        MdsAuthenticationToken auth = new MdsAuthenticationToken(user.getUsername(), password, true, (mobileDevice != null && mobileDevice.equalsIgnoreCase("true")));
        auth.setDetails(UserUtils.toUserAccount(user)); 
		SecurityContextHolder.getContext().setAuthentication(auth);

        // Send user an e-mail
        if (log.isDebugEnabled()) {
            log.debug("Sending user '" + user.getUsername() + "' an account information e-mail");
        }

        // Send an account information e-mail
        message.setSubject(getText("signup.email.subject", locale));

        try {
            sendUserMessage(user, getText("signup.email.message", locale), RequestUtil.getAppURL(request));
        } catch (final MailException me) {
            saveError(request, me.getMostSpecificCause().getMessage());
        }

        return "redirect:/cm/galleryview";// + UserUtils.getHomePage(request);
    }
}
