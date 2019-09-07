package com.mds.sys.webapp.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.common.exception.SearchException;
import com.mds.common.model.TreeEntity;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.utils.SpringContextHolder;
import com.mds.sys.service.MenuFunctionManager;
import com.mds.util.CacheUtils;
import com.mds.util.DateUtils;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.util.UserUtils;
import com.mds.util.excel.ExcelImportResult;
import com.mds.util.excel.fieldcell.MenuFunctionCell;
import com.mds.common.webapp.controller.BaseController;
import com.mds.common.webapp.util.Page;
import com.mds.common.webapp.util.excel.ExportExcel;
import com.mds.common.webapp.util.excel.ImportExcel;
import com.mds.sys.model.MenuFunction;
import com.mds.sys.model.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort.Direction;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sys/menuFunctions*")
public class MenuFunctionController extends BaseController {
    private MenuFunctionManager menuFunctionManager;

    @Autowired
    public void setMenuFunctionManager(MenuFunctionManager menuFunctionManager) {
        this.menuFunctionManager = menuFunctionManager;
    }

    /*@RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        Model model = new ExtendedModelMap();
        try {
            model.addAttribute("page", menuFunctionManager.search(new Page<MenuFunction>(request, response), query, MenuFunction.class));
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            model.addAttribute("page", menuFunctionManager.getAll(new Page<MenuFunction>(request, response)));
        }
        return model;
    }*/
    
    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequestDefault(@RequestParam(required = false, value = "q") String query)
    throws Exception {
    	Model model = new ExtendedModelMap();
        List<MenuFunction> sourcelist = null;
        try {
        	Searchable searchable = Searchable.newSearchable();
            searchable.addSort(Direction.ASC, "parent.id", "sort");
            sourcelist = menuFunctionManager.findAll(searchable);
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            sourcelist = menuFunctionManager.getAll();
        }
        //model.addAttribute("menuFunctionList", sourcelist);
        List<MenuFunction> list = Lists.newArrayList();
        MenuFunction.sortList(list, sourcelist, 0L);
        model.addAttribute("menuFunctionList", list);
        
        return model;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
    	Model model = new ExtendedModelMap();
        
        return model;
    }
    
    @ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) Long extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<MenuFunction> list = menuFunctionManager.getAll();
		List<Long> parentIds = list.stream().filter(c->c.getParentId() != extId).map(MenuFunction::getId).collect(Collectors.toList());
		for (int i=0; i<list.size(); i++){
			MenuFunction e = list.get(i);
			if (e.isRoot())
				continue;
			
			if (extId == null || (extId!=null && extId != e.getId() && parentIds.contains(e.getId()))){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.isTop() ? 0:e.getParent().getId());
				map.put("name", e.getCode());
				mapList.add(map);
			}
		}
		
		return mapList;
	}
    
    //@RequiresPermissions("sys:menuFunction:view")
    @RequestMapping(value = "export") //, method=RequestMethod.POST
    public String exportFile(MenuFunction menuFunction, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "menuFunction" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
    		//Page<MenuFunction> page = systemService.findMenuFunction(new Page<MenuFunction>(request, response, -1), menuFunction);
			Searchable searchable = Searchable.newSearchable();
            //searchable.addSort(Direction.ASC, "parent.id", "sort");
			searchable.addSearchFilter("id", SearchOperator.ne, TreeEntity.getRootId());
            searchable.addSort(Direction.ASC, "sort");
            List<MenuFunction> sourcelist = menuFunctionManager.findAll(searchable);
    		new ExportExcel(request, getText("menuFunctionList.title", locale), MenuFunction.class).setDataList(sourcelist).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			saveMessage(request, getText("message.export.failed.error", e.getMessage(), locale));
		}
		return "redirect:/sys/menuFunctions/?repage";
    }

	//@RequiresPermissions("sys:menuFunction:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> importFile(MultipartFile importFile, final HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			int successNum = 0;
			ImportExcel ei = new ImportExcel(importFile, 1, 0);
			ExcelImportResult<MenuFunction> importResult = ei.getDataList(MenuFunction.class);
			List<MenuFunction> listValidated = Lists.newArrayList();
			Validator validator = SpringContextHolder.getBean(Validator.class);
			for (int row : importResult.dataRow()){
				MenuFunctionCell.setParent(importResult.data(row), importResult.toList());
				
				BindException errors = new BindException(importResult.data(row), "MenuFunction");
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
					importResult.addResult(row, errors, locale);
				}
			}
			menuFunctionManager.importFrom(listValidated, new String[]{"parent.id", "code"});
			//saveMessage(request, I18nUtils.getString("import.result", locale, successNum, failureNum ));
			//UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
			CacheUtils.remove(UserUtils.CACHE_MENU_LIST);
			UserUtils.removeCache(UserUtils.CACHE_USER_MENU_LIST);
			
			resultMap.put("message", importResult.resultToString(successNum, locale));
	        resultMap.put("status", 200);
			//return "redirect:/home"; 
		} catch (Exception e) {
			//saveError(request, I18nUtils.getString("import.failed", locale, e.getMessage()));
			resultMap.put("status", 404);
			resultMap.put("message", I18nUtils.getString("message.import.failed", locale, e.getMessage()));
		}
		       
        return resultMap;
		
		//return "redirect:/sys/menuFunctions/?repage";
    }
	
	//@RequiresPermissions("sys:menuFunction:view")
    @RequestMapping("import/template")
    @ResponseBody
    public Map<String,Object> importFileTemplate(final HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			String fileName = "menuFunctiontemplate.xlsx";
			List<MenuFunction> list = Lists.newArrayList();
			//list.add(menuFunctionManager.getAll());
			new ExportExcel(request, "menuFunction data", MenuFunction.class, 2).setDataList(list).write(response, fileName).dispose();
			//return null;
			//resultMap.put("message", I18nUtils.getString("myCalendar.added", request.getLocale()));
	        resultMap.put("status", 200);
		} catch (Exception e) {
			saveError(request, I18nUtils.getString("message.templatedownload.failed", locale, e.getMessage())); //
			resultMap.put("status", 404);
			resultMap.put("message", I18nUtils.getString("message.templatedownload.failed", locale, e.getMessage()));
		}
		
		return resultMap;
		//return "redirect:/sys/menuFunctions/?repage";
    }
}
