package com.mds.aiotplayer.webapp.sys.controller;

import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.model.UserAddress;
import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.service.RoleManager;
import com.mds.aiotplayer.sys.service.UserAddressManager;
import com.mds.aiotplayer.sys.exception.UserExistsException;
import com.mds.aiotplayer.sys.service.UserManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.webapp.common.util.RequestUtil;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.UserAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Implementation of <strong>SimpleFormController</strong> that interacts with
 * the {@link UserManager} to retrieve/persist values to the database.
 *
 * <p><a href="UserFormController.java.html"><i>View Source</i></a>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Controller
@RequestMapping("/sys/userform*")
public class UserFormController extends BaseFormController {

    private RoleManager roleManager;
    private UserAddressManager userAddressManager;
    private OrganizationManager organizationManager;

    @Autowired
    public void setRoleManager(final RoleManager roleManager) {
        this.roleManager = roleManager;
    }
    
    @Autowired
    public void setUserAddressManager(final UserAddressManager userAddressManager) {
        this.userAddressManager = userAddressManager;
    }
    @Autowired
    public void setOrganizationManager(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    public UserFormController() {
        setCancelView("redirect:/home");
        setSuccessView("redirect:/sys/users");
    }

    @Override
    @InitBinder
    protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) {
        super.initBinder(request, binder);
        //binder.setDisallowedFields("password", "confirmPassword");
    }

    /**
     * Load user object from db before web data binding in order to keep properties not populated from web post.
     * 
     * @param request
     * @return
     */
    @ModelAttribute("user")
    protected User loadUser(final HttpServletRequest request) {
        final String userId = request.getParameter("id");
        if (isFormSubmission(request) && StringUtils.isNotBlank(userId)) {
            return getUserManager().getUser(userId);
        }
        return new User();
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@ModelAttribute("user") final User user, final BindingResult errors, final HttpServletRequest request,
            final HttpServletResponse response)
            throws Exception {
        if (request.getParameter("cancel") != null) {
            if (!StringUtils.equals(request.getParameter("from"), "list")) {
                return getCancelView();
            } else {
                return getSuccessView();
            }
        }
        
        long oid = StringUtils.toLong(request.getParameter("organizationId"));
        if (validator != null) { // validator is null during testing
            validator.validate(user, errors);
            
            if (!UserUtils.hasRoleType(RoleType.sa) && !UserUtils.hasRoleType(RoleType.ad) && Organization.isIllegalId(oid)) {
                errors.rejectValue("organization", "errors.required", new Object[] { getText("user.organization", request.getLocale()) },
                        "Organization is a required field.");
            }

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/userform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        final Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            getUserManager().removeUser(user.getId().toString());
            saveMessage(request, getText("user.deleted", user.getFullName(), locale));

            return getSuccessView();
        } else {
        	if (oid != 0 && oid != Long.MIN_VALUE) {
				user.setOrganization(organizationManager.get(oid));
			}else {
				if (UserUtils.hasRoleType(RoleType.sa) || UserUtils.hasRoleType(RoleType.ad)) {
					user.setOrganization(organizationManager.get(Organization.getRootId()));
				}
			}
        	
        	// only attempt to change roles if user is admin for other users,
            // showForm() method will handle populating
            if (UserUtils.hasRoleType(RoleType.sa) || UserUtils.hasRoleType(RoleType.ad) 
            		|| UserUtils.hasRoleType(RoleType.oa)|| UserUtils.hasRoleType(RoleType.ga)) {
                final String[] userRoles = request.getParameterValues("userRoles");

                if (userRoles != null) {
                    user.getRoles().clear();
                    for (final String roleName : userRoles) {
                        user.addRole(roleManager.getRole(roleName));
                    }
                    /*if (!ArrayUtils.contains(userRoles, Constants.USER_ROLE))
                    	user.addRole(roleManager.getRole(Constants.USER_ROLE));*/
                }
            } else {
                // if user is not an admin then load roles from the database
                // (or any other user properties that should not be editable
                // by users without admin role)
                final User cleanUser = getUserManager().getUserByUsername(
                		UserUtils.getLoginName());
                user.setRoles(cleanUser.getRoles());
            }
        	
            final Integer originalVersion = user.getVersion();

            // set a random password if user is added by admin
            if (originalVersion == null && StringUtils.isBlank(user.getPassword())) {
                user.setPassword(UUID.randomUUID().toString()); // XXX review if
                // UUID is a
                // good choice
                // here
            }

            try {
                getUserManager().saveUser(user);
                HelperFunctions.purgeCache();
            /*} catch (final AccessDeniedException ade) { 
                // thrown by UserSecurityAdvice configured in aop:advisor userManagerSecurity
                log.warn(ade.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return null;*/
            } catch (final UserExistsException e) {
                errors.rejectValue("username", "errors.existing.user",
                        new Object[] { user.getUsername(), user.getEmail() }, "duplicate user");

                getUserManager().clear();
                // reset the version # to what was passed in
                user.setVersion(originalVersion);
                if (isAdd(request))
                	user.setId(null);

                return "sys/userform";
            }

            if (!StringUtils.equals(request.getParameter("from"), "list")) {
                saveMessage(request, getText("user.saved", user.getFullName(), locale));

                // return to main Menu
                return getCancelView();
            } else {
                if (StringUtils.isBlank(request.getParameter("version"))) {
                    saveMessage(request, getText("user.added", user.getFullName(), locale));

                    // Send an account information e-mail
                    message.setSubject(getText("signup.email.subject", locale));

                    try {
                        final String resetPasswordUrl = getUserManager().buildRecoveryPasswordUrl(user,
                                UpdatePasswordController.RECOVERY_PASSWORD_TEMPLATE);
                        sendUserMessage(user, getText("newuser.email.message", user.getFullName(), locale),
                                RequestUtil.getAppURL(request) + resetPasswordUrl);
                    } catch (final MailException me) {
                        saveError(request, me.getLocalizedMessage());
                    }

                    return getSuccessView();
                } else {
                    saveMessage(request, getText("user.updated.byAdmin", user.getFullName(), locale));
                }
            }
        }

        return "sys/userform";
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected User showForm(final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {
        // If not an administrator, make sure user is not trying to add or edit another user
        if (!UserUtils.isPermitted(ResourceId.sys_users, UserAction.view) && !isFormSubmission(request)) { //!request.isUserInRole(Constants.ADMIN_ROLE) &&
            if (isAdd(request) || request.getParameter("id") != null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                log.warn("User '" + request.getRemoteUser() + "' is trying to edit user with id '" +
                        request.getParameter("id") + "'");

                throw new AccessDeniedException("You do not have permission to modify other users.");
            }
        }

        if (!isFormSubmission(request)) {
            final String userId = request.getParameter("id");

            User user;
            if (userId == null && !isAdd(request)) {
                user = getUserManager().getUserByUsername(request.getRemoteUser());
            } else if (!StringUtils.isBlank(userId) && !"".equals(request.getParameter("version"))) {
                user = getUserManager().getUser(userId);
            } else {
                user = new User();
                user.addRole(new Role(Constants.USER_ROLE));
            }

            return user;
        } else {
            // populate user object from database, so all fields don't need to be hidden fields in form
            return getUserManager().getUser(request.getParameter("id"));
        }
    }

    private boolean isFormSubmission(final HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("post");
    }

    protected boolean isAdd(final HttpServletRequest request) {
        final String method = request.getParameter("method");
        return (method != null && method.equalsIgnoreCase("add"));
    }
}
