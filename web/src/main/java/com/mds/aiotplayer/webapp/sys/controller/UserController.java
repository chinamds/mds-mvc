package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.common.Constants;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.sys.dao.UserDao;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.UserManager;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.excel.ExcelImportResult;
import com.mds.aiotplayer.webapp.common.controller.BaseController;
import com.mds.aiotplayer.webapp.common.controller.BaseListController;
import com.mds.aiotplayer.webapp.common.util.excel.ExportExcel;
import com.mds.aiotplayer.webapp.common.util.excel.ImportExcel;
import com.mds.aiotplayer.common.web.validate.AjaxResponse;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Simple class to retrieve a list of users from the database.
 * <p/>
 * <p>
 * <a href="UserController.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Controller
@RequestMapping("/sys/users*")
public class UserController extends BaseController {
    private UserManager userManager = null;

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        //setGenericManager(userManager);
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public ModelAndView handleRequestDefault(@RequestParam(required = false, value = "q") String query) throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(Constants.USER_LIST, userManager.search(query));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(userManager.getUsers());
        }
        return new ModelAndView("sys/userList", model.asMap());
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequest(@RequestParam(required = false, value = "q") String query) throws Exception {
        Model model = new ExtendedModelMap();

        return new ModelAndView("sys/userList", model.asMap());
    }
    
    //@RequiresPermissions("sys:user:view")
    @ResponseBody
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public  Map<String,Object> exportFile(User user, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "user" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
			List<User> page = userManager.getAll();
    		new ExportExcel(I18nUtils.getString("userList.title", locale), User.class).setDataList(page).write(response, fileName).dispose();
    		
    		return AjaxResponse.success().result();
		} catch (Exception e) {
			return AjaxResponse.fail(I18nUtils.getString("export.failed", locale, e.getMessage())).result();
		}
    }

	//@RequiresPermissions("sys:user:edit")
    @ResponseBody
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public Map<String,Object> importFile(MultipartFile file, final HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Locale locale = request.getLocale();
		try {
			int successNum = 0;
			ImportExcel ei = new ImportExcel(file, 1, 0);
			ExcelImportResult<User> excelResult = ei.getDataList(User.class);
			List<User> listValidated = Lists.newArrayList();
			Validator validator = SpringContextHolder.getBean(Validator.class);
			for (int row : excelResult.dataRow()){
				BindException errors = new BindException(excelResult.data(row), "User");
				if (validator != null)
				{
					validator.validate(excelResult.data(row), errors);
				}
				if (!errors.hasErrors()){
					listValidated.add(excelResult.data(row));
				}else{
					//saveError(request, errors);
					excelResult.addResult(row, errors);
				}
			}
			userManager.importFrom(listValidated, new String[] {"username"});
			//saveMessage(request, I18nUtils.getString("import.result", locale, successNum, failureNum ));
			
			return AjaxResponse.success(excelResult.resultToString(successNum, locale)).result();			
		} catch (Exception e) {
			//saveError(request, I18nUtils.getString("import.failed", locale, e.getMessage()));
			return AjaxResponse.fail(I18nUtils.getString("import.failed", locale, e.getMessage())).result();
		}
    }
	
	//@RequiresPermissions("sys:user:view")
    @ResponseBody
    @RequestMapping("import/template")
    public Map<String,Object> importFileTemplate(final HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "usertemplate.xlsx";
			List<User> list = Lists.newArrayList();
			list.add(userManager.get(UserUtils.getUserId()));
			new ExportExcel("user data", User.class, 2).setDataList(list).write(response, fileName).dispose();
			
			return AjaxResponse.success().result();
		} catch (Exception e) {
			return AjaxResponse.fail(I18nUtils.getString("templatedownload.failed", locale, e.getMessage())).result();
		}
    }
}
