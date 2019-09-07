package com.mds.sys.webapp.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.common.exception.SearchException;
import com.mds.common.model.TreeEntity;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.utils.SpringContextHolder;
import com.mds.common.webapp.controller.BaseController;
import com.mds.common.webapp.util.excel.ExportExcel;
import com.mds.common.webapp.util.excel.ImportExcel;
import com.mds.sys.service.AreaManager;
import com.mds.util.CacheUtils;
import com.mds.util.DateUtils;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.util.UserUtils;
import com.mds.util.excel.ExcelImportResult;
import com.mds.util.excel.fieldcell.AreaCell;
import com.mds.common.web.validate.AjaxResponse;
import com.mds.sys.model.Area;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;

@Controller
@RequestMapping("/sys/areas*")
public class AreaController  extends BaseController {
    private AreaManager areaManager;

    @Autowired
    public void setAreaManager(AreaManager areaManager) {
        this.areaManager = areaManager;
    }
    
    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequestDefault(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        List<Area> sourcelist = null;
        try {
        	sourcelist = areaManager.search(query, Area.class);
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            sourcelist = areaManager.getAll();
        }
        model.addAttribute("areaList", sourcelist);
        List<Area> list = Lists.newArrayList();
        Area.sortList(list, sourcelist, 0L);//area.getId()
        model.addAttribute("list", list);
        
        return model;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        List<Area> sourcelist = null;
        try {
        	sourcelist = areaManager.search(query, Area.class);
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            sourcelist = areaManager.getAll();
        }
        model.addAttribute("areaList", sourcelist);
        List<Area> list = Lists.newArrayList();
        Area.sortList(list, sourcelist, 0L);//area.getId()
        model.addAttribute("list", list);
        
        return model;
    }
    
    
	//@RequiresPermissions("sys:area:view")
	@RequestMapping(value = {"list", ""})
	public String list(Area area, Model model) {
//		User user = UserUtils.getUser();
//		if(user.isAdmin()){
			area.setId(1L);
//		}else{
//			area.setId(user.getArea().getId());
//		}
		model.addAttribute("area", area);
		List<Area> list = Lists.newArrayList();
		List<Area> sourcelist = areaManager.findAll();
		Area.sortList(list, sourcelist, 0L);//area.getId()
        model.addAttribute("list", list);
        
		return "sys/areas";
	}

	//@RequiresUser
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) Long extId, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
//		User user = UserUtils.getUser();
		List<Area> list = areaManager.findAll();
		List<Long> parentIds = list.stream().filter(c->c.getParentId() != extId).map(Area::getId).collect(Collectors.toList());
		for (int i=0; i<list.size(); i++){
			Area e = list.get(i);
			if (e.isRoot())
				continue;
			
			if (extId == null || (extId!=null && extId != e.getId() && parentIds.contains(e.getId()))){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
//				map.put("pId", !user.isAdmin()&&e.getId().equals(user.getArea().getId())?0:e.getParent()!=null?e.getParent().getId():0);
				map.put("pId", (e.getParent() == null || e.isTop()) ? 0 : e.getParent().getId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		
		return mapList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "export") //, method=RequestMethod.POST
    public String exportFile(Area area, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "area" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
    		//Page<Area> page = systemService.findArea(new Page<Area>(request, response, -1), area);
			Searchable searchable = Searchable.newSearchable();
            //searchable.addSort(Direction.ASC, "parentId", "code");
			searchable.addSearchFilter("id", SearchOperator.ne, TreeEntity.getRootId());
            /*searchable.addSort(Direction.ASC, "sort");*/
            List<Area> sourcelist = areaManager.findAll(searchable);
            // Areas sorting
    		Collections.sort(sourcelist, new Comparator<Area>(){
    			@Override
                public int compare(Area arg0, Area arg1) {
    				if (arg0.getParent() != arg1.getParent())
    					return arg0.getParentCode().compareTo(arg1.getParentCode());
    				
    				return arg0.getCode().compareTo(arg1.getCode());
                }
            });
    		
    		new ExportExcel(request, I18nUtils.getString("areaList.title", locale), Area.class).setDataList(sourcelist).write(response, fileName).dispose();
    		
    		return null;
		} catch (Exception e) {
			saveMessage(request, I18nUtils.getString("message.export.failed.error", locale, e.getMessage()));
			//return AjaxResponse.fail(I18nUtils.getString("message.export.failed", locale, e.getMessage())).result();
		}
		
		return "redirect:/sys/areas/?repage";
    }

	//@RequiresPermissions("sys:area:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> importFile(MultipartFile importFile, final HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			int successNum = 0;
			ImportExcel ei = new ImportExcel(importFile, 1, 0);
			ExcelImportResult<Area> importResult = ei.getDataList(Area.class);
			List<Area> listValidated = Lists.newArrayList();
			Validator validator = SpringContextHolder.getBean(Validator.class);
			for (int row : importResult.dataRow()){
				AreaCell.setParent(importResult.data(row), importResult.toList());

				BindException errors = new BindException(importResult.data(row), "Area");
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
			areaManager.importFrom(listValidated, new String[]{"parent.id", "code"});
			//saveMessage(request, I18nUtils.getString("import.result", locale, successNum, failureNum ));
			//UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
			
			resultMap.put("message", importResult.resultToString(successNum, locale));
	        resultMap.put("status", 200);
			//return "redirect:/home"; 
		} catch (Exception e) {
			//saveError(request, I18nUtils.getString("import.failed", locale, e.getMessage()));
			resultMap.put("status", 404);
			resultMap.put("message", I18nUtils.getString("message.import.failed", locale, e.getMessage()));
		}
		       
        return resultMap;
		
		//return "redirect:/sys/areas/?repage";
    }
	
	//@RequiresPermissions("sys:area:view")
    @RequestMapping("import/template")
    @ResponseBody
    public Map<String,Object> importFileTemplate(final HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		Map<String,Object> resultMap = new LinkedHashMap<String, Object>();
		try {
			String fileName = "areatemplate.xlsx";
			List<Area> list = Lists.newArrayList();
			//list.add(areaManager.getAll());
			new ExportExcel(request, "area data", Area.class, 2).setDataList(list).write(response, fileName).dispose();
			//return null;
			//resultMap.put("message", I18nUtils.getString("myCalendar.added", request.getLocale()));
	        resultMap.put("status", 200);
		} catch (Exception e) {
			//saveError(request, I18nUtils.getString("templatedownload.failed", locale, e.getMessage())); //
			resultMap.put("status", 404);
			resultMap.put("message", I18nUtils.getString("message.templatedownload.failed", locale, e.getMessage()));
		}
		
		return resultMap;
		//return "redirect:/sys/areas/?repage";
    }
}
