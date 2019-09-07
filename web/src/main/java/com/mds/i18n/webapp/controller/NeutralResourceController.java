package com.mds.i18n.webapp.controller;

import com.google.common.collect.Lists;
import com.mds.common.exception.SearchException;
import com.mds.common.model.search.Searchable;
import com.mds.common.utils.SpringContextHolder;
import com.mds.i18n.service.NeutralResourceManager;
import com.mds.util.CacheUtils;
import com.mds.util.DateUtils;
import com.mds.i18n.util.I18nUtils;
import com.mds.util.PropertiesLoader;
import com.mds.sys.util.UserUtils;
import com.mds.util.excel.ExcelImportResult;
import com.mds.common.webapp.controller.BaseController;
import com.mds.common.webapp.util.excel.ExportExcel;
import com.mds.common.webapp.util.excel.ImportExcel;
import com.mds.common.web.validate.AjaxResponse;
import com.mds.i18n.model.NeutralResource;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/i18n/neutralResources*")
public class NeutralResourceController extends BaseController {
    private NeutralResourceManager neutralResourceManager;

    @Autowired
    public void setNeutralResourceManager(NeutralResourceManager neutralResourceManager) {
        this.neutralResourceManager = neutralResourceManager;
    }

    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequestDefault(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute(neutralResourceManager.search(query, NeutralResource.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute(neutralResourceManager.getAll());
        }
        return model;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        
        return model;
    }
    
  //@RequiresPermissions("sys:neutralResource:view")
    @RequestMapping(value = "export") //, method=RequestMethod.POST
    @ResponseBody
    public String exportFile(NeutralResource neutralResource, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "neutralResource" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
    		//Page<NeutralResource> page = systemService.findNeutralResource(new Page<NeutralResource>(request, response, -1), neutralResource);
			Searchable searchable = Searchable.newSearchable();
            searchable.addSort(Direction.ASC, "resourceClass", "resourceKey");
            //searchable.addSort(Direction.ASC, "resourceKey");
            List<NeutralResource> sourcelist = neutralResourceManager.findAll(searchable);
    		new ExportExcel(request, I18nUtils.getString("neutralResourceList.title", locale), NeutralResource.class).setDataList(sourcelist).write(response, fileName).dispose();
    		
    		return null; //AjaxResponse.success().result();
		} catch (Exception e) {
			saveMessage(request, I18nUtils.getString("export.failed", e.getMessage(), locale));
			//return AjaxResponse.fail(I18nUtils.getString("export.failed", locale, e.getMessage())).result();
		}
		
		return "redirect:/i18n/neutralResources/?repage";
    }

	//@RequiresPermissions("sys:neutralResource:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> importFile(MultipartFile importFile, final HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			int successNum = 0;
			ImportExcel ei = new ImportExcel(importFile, 1, 0);
			ExcelImportResult<NeutralResource> importResult = ei.getDataList(NeutralResource.class);
			List<NeutralResource> listValidated = Lists.newArrayList();
			Validator validator = SpringContextHolder.getBean(Validator.class);
			for (int row : importResult.dataRow()){
				BindException errors = new BindException(importResult.data(row), "NeutralResource");
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
			//neutralResourceManager.Import(listValidated, new String[]{"resourceClass", "resourceKey"});
			neutralResourceManager.importFrom(listValidated, new String[]{"resourceKey"});
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
		
		//return "redirect:/i18n/neutralResources/?repage";
    }
    
    @RequestMapping(value = "initialize", method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> Initialize(final HttpServletRequest request) {
		Locale locale = request.getLocale();
		try {
			PropertiesLoader loader = new PropertiesLoader("ApplicationResources.properties");
			Properties applicationResources = loader.getProperties();
			if (applicationResources != null)
			{
				List<NeutralResource> neutralResources = Lists.newArrayList();
					// walk values, interpolating any embedded references.
		        for (Enumeration<?> pe = applicationResources.propertyNames(); pe.hasMoreElements(); )
		        {
		            String key = (String)pe.nextElement();
		            int index = key.indexOf('.');
		            String resClass = key;
		            //String resKey = key;
		            if (index > 0){
		            	resClass = key.substring(0, index);
		            	//resKey = key.substring(index + 1, key.length());
		            }
		            
		            NeutralResource resource = new NeutralResource();
		            resource.setValue(applicationResources.getProperty(key));
		            resource.setResourceClass(resClass);
		            resource.setResourceKey(key);
		            neutralResources.add(resource);
		        }
		        neutralResourceManager.importFrom(neutralResources, new String[]{"resourceKey"}); //"resourceClass", 
		        		        
		        //CacheUtils.remove(I18nUtils.CACHE_I18N_NEUTRAL);
		        CacheUtils.remove(I18nUtils.CACHE_I18N_MAP);
		        
		        return AjaxResponse.success(I18nUtils.getString("neutralResource.initialized", locale)).result();
			}else{
				return AjaxResponse.fail(I18nUtils.getString("neutralResource.applicationResourcesNotFound", locale)).result();
			}
		} catch (Exception e) {
			return AjaxResponse.fail(I18nUtils.getString("neutralResource.failtoinitialize", locale, e.getMessage())).result();
		}
    }
	
	//@RequiresPermissions("sys:neutralResource:view")
    @RequestMapping("import/template")
    @ResponseBody
    public Map<String,Object> importFileTemplate(final HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			String fileName = "neutralResourcetemplate.xlsx";
			List<NeutralResource> list = Lists.newArrayList();
			//list.add(neutralResourceManager.getAll());
			new ExportExcel(request, "neutralResource data", NeutralResource.class, 2).setDataList(list).write(response, fileName).dispose();
			//return null;
			//resultMap.put("message", I18nUtils.getString("myCalendar.added", request.getLocale()));
	        resultMap.put("status", 200);
		} catch (Exception e) {
			//saveError(request, I18nUtils.getString("templatedownload.failed", locale, e.getMessage())); //
			resultMap.put("status", 404);
			resultMap.put("message", I18nUtils.getString("templatedownload.failed", locale, e.getMessage()));
		}
		
		return resultMap;
		//return "redirect:/i18n/neutralResources/?repage";
    }
}
