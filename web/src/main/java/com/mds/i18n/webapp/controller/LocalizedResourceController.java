package com.mds.i18n.webapp.controller;

import com.google.common.collect.Lists;
import com.mds.common.exception.SearchException;
import com.mds.common.model.search.Searchable;
import com.mds.common.utils.MessageUtils;
import com.mds.common.utils.SpringContextHolder;
import com.mds.common.webapp.util.excel.ExportExcel;
import com.mds.common.webapp.util.excel.ImportExcel;
import com.mds.i18n.service.LocalizedResourceManager;
import com.mds.util.CacheUtils;
import com.mds.util.DateUtils;
import com.mds.i18n.util.I18nUtils;
import com.mds.util.PropertiesLoader;
import com.mds.util.excel.ExcelImportResult;
import com.mds.common.web.validate.AjaxResponse;
import com.mds.i18n.model.Culture;
import com.mds.i18n.model.LocalizedResource;
import com.mds.i18n.model.NeutralResource;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/i18n/localizedResources*")
public class LocalizedResourceController {
    private LocalizedResourceManager localizedResourceManager;

    @Autowired
    public void setLocalizedResourceManager(LocalizedResourceManager localizedResourceManager) {
        this.localizedResourceManager = localizedResourceManager;
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequestDefault(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(localizedResourceManager.search(query, LocalizedResource.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(localizedResourceManager.getAll());
        }
        return model;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        return model;
    }
    
    @ResponseBody
	@RequestMapping(value = "jsResource")
    public Map<String, Object> getLocalizedResource(final HttpServletRequest request, @RequestParam(required = false, value = "type") String type)
    throws Exception {
        return I18nUtils.getStrings(null, request.getLocale());
    }
    
    @RequestMapping(value = "export") //, method=RequestMethod.POST
    @ResponseBody
    public String exportFile(LocalizedResource localizedResource, HttpServletRequest request, HttpServletResponse response) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "localizedResource" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
    		//Page<LocalizedResource> page = systemService.findLocalizedResource(new Page<LocalizedResource>(request, response, -1), localizedResource);
			Searchable searchable = Searchable.newSearchable();
            searchable.addSort(Direction.ASC, "resourceClass", "resourceKey");
            //searchable.addSort(Direction.ASC, "resourceKey");
            List<LocalizedResource> sourcelist = localizedResourceManager.findAll(searchable);
    		new ExportExcel(request, I18nUtils.getString("localizedResourceList.title", locale), LocalizedResource.class).setDataList(sourcelist).write(response, fileName).dispose();
    		
    		return null; //AjaxResponse.success().result();
		} catch (Exception e) {
			MessageUtils.saveMessage(request, I18nUtils.getString("export.failed", e.getMessage(), locale));
		}
		
		return "redirect:/i18n/localizedResources/?repage";
    }

	//@RequiresPermissions("sys:localizedResource:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> importFile(MultipartFile importFile, final HttpServletRequest request) {
		Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			int successNum = 0;
			ImportExcel ei = new ImportExcel(importFile, 1, 0);
			ExcelImportResult<LocalizedResource> importResult = ei.getDataList(LocalizedResource.class);
			List<LocalizedResource> listValidated = Lists.newArrayList();
			Validator validator = SpringContextHolder.getBean(Validator.class);
			for (int row : importResult.dataRow()){
				BindException errors = new BindException(importResult.data(row), "LocalizedResource");
				if (validator != null)
				{
					validator.validate(importResult.data(row), errors);
				}
				if (!errors.hasErrors()){
					listValidated.add(importResult.data(row));
					successNum++;
				}else{
					//saveError(request, errors);
					importResult.addResult(row, errors);
				}
			}
			//localizedResourceManager.Import(listValidated, new String[]{"resourceClass", "resourceKey"});
			localizedResourceManager.importFrom(listValidated, new String[]{"resourceKey"});
			//saveMessage(request, I18nUtils.getString("import.result", locale, successNum, failureNum ));
			//UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
			//CacheUtils.remove(I18nUtils.CACHE_I18N_NEUTRAL);
			//I18nUtils.removeCache(I18nUtils.CACHE_I18N_NEUTRAL);
			
			resultMap.put("message", importResult.resultToString(successNum, locale));
	        resultMap.put("status", 200);
			//return "redirect:/home"; 
		} catch (Exception e) {
			//saveError(request, I18nUtils.getString("import.failed", locale, e.getMessage()));
			resultMap.put("status", 404);
			resultMap.put("message", I18nUtils.getString("import.failed", locale, e.getMessage()));
		}
		       
        return resultMap;
		
		//return "redirect:/i18n/localizedResources/?repage";
    }
    
    @RequestMapping(value = "initialize", method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> Initialize(final HttpServletRequest request) {
		Locale locale = request.getLocale();
		try {
			String langTag = I18nUtils.getLanguageTag(locale);
			Culture culture = I18nUtils.getCulture(langTag);
			PropertiesLoader loader = new PropertiesLoader("ApplicationResources_" + langTag + ".properties");
			Properties applicationResources = loader.getProperties();
			List<NeutralResource> neutralResources = I18nUtils.getNeutralResources();
			List<LocalizedResource> localizedResources = Lists.newArrayList();
			for (NeutralResource neutralResource : neutralResources){
				String label = applicationResources!= null ? applicationResources.getProperty(neutralResource.getResourceKey()) : null;
				if (label == null)
					label = neutralResource.getValue();
				
				LocalizedResource resource = new LocalizedResource();
	            resource.setValue(label);
	            resource.setNeutralResource(neutralResource);
	            resource.setCulture(culture);
	            localizedResources.add(resource);
			}
			
	        localizedResourceManager.importFrom(localizedResources, new String[]{"culture.id", "neutralResource.id"}); //"resourceClass", 
	        		        
	        //CacheUtils.remove(I18nUtils.CACHE_I18N_NEUTRAL);
	        CacheUtils.remove(I18nUtils.CACHE_I18N_MAP);
	        
	        return AjaxResponse.success(I18nUtils.getString("localizedResource.initialized", locale)).result();

		} catch (Exception e) {
			return AjaxResponse.fail(I18nUtils.getString("localizedResource.failtoinitialize", locale, e.getMessage())).result();
		}
    }
	
	//@RequiresPermissions("sys:localizedResource:view")
    @RequestMapping("import/template")
    @ResponseBody
    public Map<String,Object> importFileTemplate(final HttpServletRequest request, HttpServletResponse response) {
    	Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			String fileName = "localizedResourcetemplate.xlsx";
			List<LocalizedResource> list = Lists.newArrayList();
			new ExportExcel(request, "localizedResource data", LocalizedResource.class, 2).setDataList(list).write(response, fileName).dispose();
	        resultMap.put("status", 200);
		} catch (Exception e) {
			resultMap.put("status", 404);
			resultMap.put("message", I18nUtils.getString("templatedownload.failed", locale, e.getMessage()));
		}
		
		return resultMap;
    }
}
