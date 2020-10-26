/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Maps;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.webapp.common.util.CookieUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.UserStatusHistoryManager;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;

/**
 * Login Controller
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
public class LoginController extends BaseFormController  {
	
	/*@Value(value = "${shiro.login.url}")
    private String loginUrl;*/
	
	private boolean jcaptchaEnabled;
	
	/**
     * Enable/Disable jcaptcha
     *
     * @param jcaptchaEnabled
     */
	@Value("${spring.security.jcaptchaEnabled:true}")
    public void setJcaptchaEnabled(boolean jcaptchaEnabled) {
        this.jcaptchaEnabled = jcaptchaEnabled;
    }
	
	public boolean getJcaptchaEnabled() {
        return this.jcaptchaEnabled;
    }

    //@Autowired
    private UserStatusHistoryManager userStatusHistoryManager;
    
    @Autowired
    public void setUserStatusHistoryManager(UserStatusHistoryManager userStatusHistoryManager) {
        this.userStatusHistoryManager = userStatusHistoryManager;
    }

    @RequestMapping(value = {"/{login:login;?.*}"}) //spring3.2.2 bug see  http://jinnianshilongnian.iteye.com/blog/1831408
    public String loginForm(HttpServletRequest request, ModelMap model) {

    	/*Locale locale = request.getLocale();
        //logout
        if (!StringUtils.isEmpty(request.getParameter("logout"))) {
            model.addAttribute(MESSAGES_KEY, getText("user.logout.success", locale));
        }

        //user not exists @see org.apache.shiro.web.filter.user.SysUserFilter
        if (!StringUtils.isEmpty(request.getParameter("notfound"))) {
            model.addAttribute(ERRORS_MESSAGES_KEY, getText("user.notfound", locale));
        }

        //user has been kicked out
        if (!StringUtils.isEmpty(request.getParameter("kickedout"))) {
            model.addAttribute(ERRORS_MESSAGES_KEY, getText("user.kickedout", locale));
        }

        //Verification code Authentication failed
        if (!StringUtils.isEmpty(request.getParameter("jcaptchaError"))) {
            model.addAttribute(ERRORS_MESSAGES_KEY, getText("jcaptcha.validate.error", locale));
        }

        //user has been locked @see org.apache.shiro.web.filter.user.SysUserFilter
        if (!StringUtils.isEmpty(request.getParameter("blocked"))) {
            User user = (User) request.getAttribute(Constants.CURRENT_USER);
            String reason = userStatusHistoryManager.getLastReason(user);
            model.addAttribute(ERRORS_MESSAGES_KEY, getText("user.blocked", new Object[]{reason}, null));
        }

        if (!StringUtils.isEmpty(request.getParameter("unknown"))) {
            model.addAttribute(ERRORS_MESSAGES_KEY, getText("user.unknown.error", locale));
        }

        //login failure, retrieve error message
        Exception shiroLoginFailureEx =
                (Exception) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        if (shiroLoginFailureEx != null) {
            model.addAttribute(ERRORS_MESSAGES_KEY, shiroLoginFailureEx.getMessage());
        }

        //如果用户直接到登录页面 先退出一下
        //原因：isAccessAllowed实现是subject.isAuthenticated()---->即如果用户验证通过 就允许访问
        // 这样会导致登录一直死循环
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            subject.logout();
        }

        //如果同时存在错误消息 和 普通消息  只保留错误消息
        if (model.containsAttribute(ERRORS_MESSAGES_KEY)) {
            model.remove(MESSAGES_KEY);
        }*/
    	model.addAttribute("jcaptchaEnabled", this.jcaptchaEnabled);

        return "login";
    }
    
	/**
	 *  Administrator login
	 */
	/*@RequestMapping(value = "/login", method = RequestMethod.GET) //${adminPath}
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		// 如果已经登录，则跳转到管理首页
		if(user.getId() != null){
			return "redirect:home";//+Global.getAdminPath();
		}
		
		return "login";
		//return "modules/sys/sysLogin";
	}*/

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	/*@RequestMapping(value = "/login", method = RequestMethod.POST) //${adminPath}
	public String login(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String username, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		// 如果已经登录，则跳转到管理首页
		if(user.getId() != null){
			return "redirect:home";//+Global.getAdminPath();
		}
		model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
		model.addAttribute("isValidateCodeLogin", isValidateCodeLogin(username, true, false));
		
		return "login";
		//return "modules/sys/sysLogin";
	}*/

	/**
	 * 登录成功，进入管理首页
	 */
	//@RequiresUser
	@RequestMapping(value = "${adminPath}")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		UserAccount user = UserUtils.getUser();
		// 未登录，则跳转到登录页
		if(user.getId() == null){
			return "redirect:" + "/login"; //+Global.getAdminPath()
		}
		// 登录成功后，验证码计算器清零
		isValidateCodeLogin(user.getUsername(), false, true);
		// 登录成功后，获取上次登录的当前站点ID
		UserUtils.putCache("siteId", CookieUtils.getCookie(request, "siteId"));
		return "modules/sys/sysIndex";
	}
	
	/**
	 * 获取主题方案
	 */
	@RequestMapping(value = "/theme/{theme}")
	public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response){
		if (StringUtils.isNotBlank(theme)){
			CookieUtils.setCookie(response, "theme", theme);
		}else{
			theme = CookieUtils.getCookie(request, "theme");
		}
		return "redirect:"+request.getParameter("url");
	}
	
	/**
	 * 是否是验证码登录
	 * @param useruame 用户名
	 * @param isFail 计数加1
	 * @param clean 计数清零
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean){
		Map<String, Integer> loginFailMap = (Map<String, Integer>)CacheUtils.get("loginFailMap");
		if (loginFailMap==null){
			loginFailMap = Maps.newHashMap();
			CacheUtils.put("loginFailMap", loginFailMap);
		}
		Integer loginFailNum = loginFailMap.get(useruame);
		if (loginFailNum==null){
			loginFailNum = 0;
		}
		if (isFail){
			loginFailNum++;
			loginFailMap.put(useruame, loginFailNum);
		}
		if (clean){
			loginFailMap.remove(useruame);
		}
		return loginFailNum >= 3;
	}
	

	@SuppressWarnings("resource")
	@RequestMapping("${adminPath}/download")
	public String download(@RequestParam String filePath,HttpServletResponse response) {
		File file = new File(filePath);
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(filePath);
			response.reset();
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
			OutputStream outputStream = new BufferedOutputStream(
					response.getOutputStream());
			byte data[] = new byte[1024];
			while (inputStream.read(data, 0, 1024) >= 0) {
				outputStream.write(data);
			}
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
