/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.sys.service.DictManager;
import com.mds.aiotplayer.util.CacheUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.common.web.bind.annotation.PageableDefaults;
import com.mds.aiotplayer.sys.model.Dict;
import com.mds.aiotplayer.sys.model.DictCategory;

import java.util.List;
import java.util.Locale;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mds.aiotplayer.util.excel.ExcelImportResult;
import com.mds.aiotplayer.webapp.common.controller.BaseController;
import com.mds.aiotplayer.webapp.common.util.excel.ExportExcel;
import com.mds.aiotplayer.webapp.common.util.excel.ImportExcel;
import com.mds.aiotplayer.common.web.validate.AjaxResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sys/dicts*")
public class DictController extends BaseController {
    private DictManager dictManager;

    @Autowired
    public void setDictManager(DictManager dictManager) {
        this.dictManager = dictManager;
    }

    @RequestMapping(method = RequestMethod.GET)
    @PageableDefaults(sort = {"category=asc", "word=asc"})
    public Model handleRequest(@RequestParam(required = false, value = "q") String query, Pageable pageable)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            //model.addAttribute(dictManager.search(query, Dict.class));
        	 model.addAttribute("categories", DictCategory.values());
        	 model.addAttribute("dictParam", new Dict());
        	 model.addAttribute("page", dictManager.searchPaging(pageable, query, Dict.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute("page", dictManager.getAll());
        }
        return model;
    }
    
    @RequestMapping(value = "advancesearch", method=RequestMethod.POST)
    @PageableDefaults(sort = {"category=asc", "word=asc"})
    public ModelAndView advanceSearch(Dict dictParam, Pageable pageable)
    throws Exception {
    	//ModelAndView model = new ModelAndView();
        try {
        	Searchable searchable = Searchable.newSearchable();
        	if (dictParam.getCategory() != DictCategory.notspecified)
        		searchable.addSearchFilter("category", SearchOperator.eq, dictParam.getCategory());
        	if (StringUtils.isNotBlank(dictParam.getWord()))
        		searchable.addSearchFilter("word", SearchOperator.like, dictParam.getWord());
        	searchable.setPage(pageable);
        	Page<Dict> dicts = dictManager.findPaging(searchable);
        	//model.addAttribute("page", dictManager.findPaging(searchable));
        	
        	return new ModelAndView("sys/dicts").addObject("page", dicts).addObject("categories", DictCategory.values()).addObject("dictParam", dictParam);
        } catch (SearchException se) {
            /*model.addAttribute("searchError", se.getMessage());
            model.addAttribute("page", dictManager.getAll());*/
        	return new ModelAndView("sys/dicts").addObject("searchError", se.getMessage()).addObject("categories", DictCategory.values()).addObject("dictParam", dictParam);
        }
       /* model.addAttribute("categories", DictCategory.values());
   	 	model.addAttribute("dictParam", dictParam);
        
        return model;*/
        //return "redirect:/sys/dicts";
    }
    
    
    @RequestMapping(value = "export") //, method=RequestMethod.POST
    public String exportFile(Dict dict, HttpServletRequest request, HttpServletResponse response) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "dict" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
			Searchable searchable = Searchable.newSearchable();
            searchable.addSort(Direction.ASC, "category", "word");
            List<Dict> sourcelist = dictManager.findAll(searchable);
    		new ExportExcel(request, getText("dictList.title", locale), Dict.class).setDataList(sourcelist).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			saveMessage(request, getText("export.failed", e.getMessage(), locale));
		}
		return "redirect:/sys/dicts/?repage";
    }

	//@RequiresPermissions("sys:dict:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> importFile(MultipartFile importFile, final HttpServletRequest request) {
		Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			int successNum = 0;
			ImportExcel ei = new ImportExcel(importFile, 1, 0);
			ExcelImportResult<Dict> importResult = ei.getDataList(Dict.class);
			List<Dict> listValidated = Lists.newArrayList();
			Validator validator = SpringContextHolder.getBean("beanValidator", Validator.class);
			for (int row : importResult.dataRow()){
				BindException errors = new BindException(importResult.data(row), "Dict");
				if (validator != null)
				{
					validator.validate(importResult.data(row), errors);
				}
				if (!errors.hasErrors()){
					importResult.data(row).setCurrentUser(UserUtils.getLoginName());
					listValidated.add(importResult.data(row));
					successNum++;
				}else{
					//saveError(request, errors);
					importResult.addResult(row, errors, request.getLocale());
				}
			}
			dictManager.importFrom(listValidated, new String[]{"name"});
			
			//CacheUtils.remove(I18nUtils.CACHE_I18N_NEUTRAL);
			
			resultMap.put("message", importResult.resultToString(successNum, locale));
	        resultMap.put("status", 200);
			//return "redirect:/home"; 
		} catch (Exception e) {
			//saveError(request, I18nUtils.getString("import.failed", locale, e.getMessage()));
			resultMap.put("status", 404);
			resultMap.put("message", I18nUtils.getString("import.failed", locale, e.getMessage()));
		}
		       
        return resultMap;
		
		//return "redirect:/sys/dicts/?repage";
    }
	
	//@RequiresPermissions("sys:dict:view")
    @RequestMapping("import/template")
    @ResponseBody
    public Map<String,Object> importFileTemplate(final HttpServletRequest request, HttpServletResponse response) {
    	Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			String fileName = "dicttemplate.xlsx";
			List<Dict> list = Lists.newArrayList();
			//list.add(dictManager.getAll());
			new ExportExcel(request, "dict data", Dict.class, 2).setDataList(list).write(response, fileName).dispose();
			//return null;
	        resultMap.put("status", 200);
		} catch (Exception e) {
			saveError(request, I18nUtils.getString("templatedownload.failed", locale, e.getMessage())); //
			resultMap.put("status", 404);
			resultMap.put("message", I18nUtils.getString("templatedownload.failed", locale, e.getMessage()));
		}
		
		return resultMap;
		//return "redirect:/sys/dicts/?repage";
    }
}
