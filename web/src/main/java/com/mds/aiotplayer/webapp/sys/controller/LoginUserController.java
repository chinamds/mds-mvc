/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.common.model.enums.BooleanEnum;
import com.mds.aiotplayer.webapp.common.controller.AbstractBaseController;
import com.mds.aiotplayer.webapp.common.util.RequestUtil;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.common.web.controller.BaseController;
import com.mds.aiotplayer.sys.exception.UserExistsException;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.model.UserStatus;
//import com.mds.aiotplayer.sys.service.UserLastOnlineManager;
import com.mds.aiotplayer.sys.service.UserManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.webapp.sys.bind.annotation.CurrentUser;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 登录用户的个人信息
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-30 下午2:00
 * <p>Version: 1.0
 */
@Controller()
@RequestMapping("/sys/users/loginUser")
public class LoginUserController extends AbstractBaseController<User, Long> {

    private UserManager userManager = null;
    //private UserLastOnlineManager userLastOnlineManager = null;

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
    
    /*@Autowired
    public void setUserLastOnlineManager(UserLastOnlineManager userLastOnlineManager) {
        this.userLastOnlineManager = userLastOnlineManager;
    }*/

    @RequestMapping("/viewInfo")
    public String viewInfo(Model model) {
        model.addAttribute(Constants.OP_NAME, "userProfile.viewprofile");
        User user = userManager.get(UserUtils.getUser().getId());
        model.addAttribute("user", user);
        //model.addAttribute("lastOnline", userLastOnlineManager.findByUserId(user.getId()));
        //model.addAttribute("lastOnline", userLastOnlineManager.findByUserId(user.getId()));
        
        return viewName("editForm");
    }

    @RequestMapping(value = "/updateInfo", method = RequestMethod.GET)
    public String updateInfoForm(Model model) {
        model.addAttribute(Constants.OP_NAME, "userProfile.editprofile");
        User user = userManager.get(UserUtils.getUser().getId());
        model.addAttribute("user", user);
        return viewName("editForm");
    }

    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
    public String updateInfo(
            //@CurrentUser User user,
            @RequestParam("email") String email,
            @RequestParam("mobile") String mobile,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        /*if (email == null || !email.matches(User.EMAIL_PATTERN)) {
            model.addAttribute(Constants.ERRORS_MESSAGES_KEY, "请输入正确的邮箱地址");
            return updateInfoForm(user, model);
        }

        if (mobilePhoneNumber == null || !mobilePhoneNumber.matches(User.MOBILE_PHONE_NUMBER_PATTERN)) {
            model.addAttribute(Constants.ERRORS_MESSAGES_KEY, "请输入正确的手机号");
            return updateInfoForm(user, model);
        }

        User emailDbUser = userManager.findByEmail(email);
        if (emailDbUser != null && !emailDbUser.equals(user)) {
            model.addAttribute(Constants.ERRORS_MESSAGES_KEY, "邮箱地址已经被其他人使用，请换一个");
            return updateInfoForm(user, model);
        }

        User mobilePhoneNumberDbUser = userManager.findByMobilePhoneNumber(mobilePhoneNumber);
        if (mobilePhoneNumberDbUser != null && !mobilePhoneNumberDbUser.equals(user)) {
            model.addAttribute(Constants.ERRORS_MESSAGES_KEY, "手机号已经被其他人使用，请换一个");
            return updateInfoForm(user, model);
        }
*/
    	User user = userManager.get(UserUtils.getUser().getId());
        user.setEmail(email);
        user.setMobile(mobile);
        user.setCurrentUser(UserUtils.getLoginName());
        userManager.save(user);

        redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, I18nUtils.getString("user.saved", request.getLocale()));

        return redirectToUrl(viewName("updateInfo"));

    }


    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePasswordForm(Model model) {
    	preModelData(model);
        model.addAttribute(Constants.OP_NAME, "changepassword");
        User user = userManager.get(UserUtils.getUser().getId());
        model.addAttribute("user", user);
        return viewName("changePasswordForm");
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(
            @RequestParam(value = "oldPassword") String oldPassword,
            @RequestParam(value = "newPassword1") String newPassword1,
            @RequestParam(value = "newPassword2") String newPassword2,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) throws UserExistsException {

    	User user = userManager.get(UserUtils.getUser().getId());
        if (!HelperFunctions.validatePassword(oldPassword, user.getPassword())) {
            model.addAttribute(Constants.ERRORS_MESSAGES_KEY, I18nUtils.getString("updatePassword.invalidPassword", request.getLocale()));
            return changePasswordForm(model);
        }

        if (StringUtils.isBlank(newPassword1)) {
            model.addAttribute(Constants.ERRORS_MESSAGES_KEY, I18nUtils.getString("change.pwd.NewPasswordRequiredErrorMessage", request.getLocale()));
            return changePasswordForm(model);
        }
        
        if (StringUtils.isBlank(newPassword2)) {
            model.addAttribute(Constants.ERRORS_MESSAGES_KEY, I18nUtils.getString("change.pwd.ConfirmPasswordRequiredErrorMessage", request.getLocale()));
            return changePasswordForm(model);
        }

        if (!newPassword1.equals(newPassword2)) {
            model.addAttribute(Constants.ERRORS_MESSAGES_KEY, I18nUtils.getString("change.pwd.ConfirmPasswordCompareErrorMessage", request.getLocale()));
            return changePasswordForm(model);
        }

        //userManager.changePassword(user, newPassword1);
        userManager.updatePassword(user.getUsername(), oldPassword, null, newPassword1,
                RequestUtil.getAppURL(request));

        redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, I18nUtils.getString("updatePassword.success", request.getLocale()));
        
        return redirectToUrl(viewName("changePassword"));
    }
}
