/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.cm.service.BannerManager;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.common.exception.Exceptions;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.util.excel.ExcelImportResult;
import com.mds.aiotplayer.common.web.bind.annotation.PageableDefaults;
import com.mds.aiotplayer.common.web.validate.AjaxResponse;
import com.mds.aiotplayer.webapp.common.controller.BaseController;
import com.mds.aiotplayer.webapp.common.controller.BaseI18nController;
//import com.mds.aiotplayer.webapp.common.util.Page;
import com.mds.aiotplayer.webapp.common.util.excel.ExportExcel;
import com.mds.aiotplayer.webapp.common.util.excel.ImportExcel;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.model.Banner;
import com.mds.aiotplayer.common.model.JTableRequest;
import com.mds.aiotplayer.common.model.JTableResult;
import com.mds.aiotplayer.common.utils.SpringContextHolder;

import java.util.Locale;
import java.util.Map;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cm/banners*")
public class BannerController extends BaseController {
    private BannerManager bannerManager;

    @Autowired
    public void setBannerManager(BannerManager bannerManager) {
        this.bannerManager = bannerManager;
    }
    
    @RequestMapping(value = "texts")
    public String index() {
        return "cm/texts";
    }

    @RequestMapping(method = RequestMethod.GET)
    @PageableDefaults(sort = "id=desc")
    public Model handleRequest(@RequestParam(required = false, value = "q") String query, Pageable pageable)
    throws Exception {
        /*Model model = new ExtendedModelMap();
        try {
            model.addAttribute(bannerManager.search(query, Banner.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(bannerManager.getAll());
        }*/
    	//for test
    	if (pageable == null)
    		pageable = PageRequest.of(0, 10);
    	
    	Model model = new ExtendedModelMap();
        try {
            model.addAttribute("page", bannerManager.searchPaging(pageable, query, Banner.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute("page", bannerManager.getAllPaging(pageable));
        }
    	
        return model;
    }
    
  //@RequiresPermissions("sys:banner:view")
    @ResponseBody
    @RequestMapping(value = "export") //, method=RequestMethod.POST
    public Map<String,Object> exportFile(Banner banner, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "banner" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
    		//Page<Banner> page = systemService.findBanner(new Page<Banner>(request, response, -1), banner);
			List<Banner> page = bannerManager.getAll();
    		new ExportExcel(getText("bannerList.title", locale), Banner.class).setDataList(page).write(response, fileName).dispose();
    		
    		return AjaxResponse.success().result();
		} catch (Exception e) {
			saveMessage(request, getText("export.failed", e.getMessage(), locale));
			return AjaxResponse.fail(I18nUtils.getString("export.failed", locale, e.getMessage())).result();
		}
    }

	//@RequiresPermissions("sys:banner:edit")
    @ResponseBody
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public Map<String,Object> importFile(MultipartFile file, final HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Locale locale = request.getLocale();
		try {
			int successNum = 0;
			ImportExcel ei = new ImportExcel(file, 1, 0);
			ExcelImportResult<Banner> importResult = ei.getDataList(Banner.class);
			Validator validator = SpringContextHolder.getBean("beanValidator", Validator.class);
			List<Banner> listValidated = Lists.newArrayList();
			for (int row : importResult.dataRow()){
				BindException errors = new BindException(importResult.data(row), "Banner");
				if (validator != null)
				{
					validator.validate(importResult.data(row), errors);
				}
				if (!errors.hasErrors()){
					listValidated.add(importResult.data(row));
				}else{
					importResult.addResult(row, errors, request.getLocale());
				}
			}
			bannerManager.importFrom(listValidated, new String[] {"strContent"});
			//saveMessage(request, getText("import.result", new Object[] { successNum,  failureNum}, locale));
			
			return AjaxResponse.success(importResult.resultToString(successNum, locale)).result();		
		} catch (Exception e) {
			//saveError(request, getText("import.failed", e.getMessage(), locale));
			return AjaxResponse.fail(I18nUtils.getString("import.failed", locale, e.getMessage())).result();
		}
    }
	
	//@RequiresPermissions("sys:banner:view")
    @ResponseBody
    @RequestMapping("import/template")
    public Map<String,Object> importFileTemplate(final HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "bannertemplate.xlsx";
			List<Banner> list = Lists.newArrayList();
			//list.add(bannerManager.getAll());
			new ExportExcel("banner data", Banner.class, 2).setDataList(list).write(response, fileName).dispose();
			
			return AjaxResponse.success().result();
		} catch (Exception e) {
			saveError(request, getText("templatedownload.failed", e.getMessage(), request.getLocale())); //
			return AjaxResponse.fail(I18nUtils.getString("templatedownload.failed", request.getLocale(), e.getMessage())).result();
		}
    }
}
