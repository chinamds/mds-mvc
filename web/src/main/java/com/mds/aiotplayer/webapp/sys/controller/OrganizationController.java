package com.mds.aiotplayer.webapp.sys.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.excel.ExcelImportResult;
import com.mds.aiotplayer.webapp.common.controller.BaseController;
import com.mds.aiotplayer.webapp.common.util.excel.ExportExcel;
import com.mds.aiotplayer.webapp.common.util.excel.ImportExcel;
import com.mds.aiotplayer.common.web.validate.AjaxResponse;
import com.mds.aiotplayer.sys.model.Area;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.User;

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
@RequestMapping("/sys/organizations*")
public class OrganizationController extends BaseController {
    private OrganizationManager organizationManager;

    @Autowired
    public void setOrganizationManager(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }
    
    @RequestMapping(value = "SearchDefault", method = RequestMethod.GET)
    public Model handleRequestDefault(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        List<Organization> sourcelist = null;
        try {
        	Searchable searchable = Searchable.newSearchable();
            searchable.addSort(Direction.ASC, "parent.id", "code");
            sourcelist = organizationManager.findAll(searchable);
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            sourcelist = organizationManager.getAll();
        }
        
        List<Organization> list = Lists.newArrayList();
        Organization.sortList(list, sourcelist, 0L);
        model.addAttribute("organizationList", list);
                
        return model;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Model handleRequest(@RequestParam(required = false, value = "q") String query)
    throws Exception {
        Model model = new ExtendedModelMap();
        List<Organization> sourcelist = null;
        try {
        	Searchable searchable = Searchable.newSearchable();
            searchable.addSort(Direction.ASC, "parent.id", "code");
            sourcelist = organizationManager.findAll(searchable);
        } catch (SearchException se) {
            model.addAttribute("searchError", se.getMessage());
            sourcelist = organizationManager.getAll();
        }
        
        List<Organization> list = Lists.newArrayList();
        Organization.sortList(list, sourcelist, 0L);
        model.addAttribute("organizationList", list);
                
        return model;
    }
    
    @ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) Long extId, HttpServletResponse response) throws InvalidMDSRoleException {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
//		User user = UserUtils.getUser();
		Searchable searchable = Searchable.newSearchable();
    	//searchable.addSearchFilter("id", SearchOperator.ne, 1L);
    	searchable.addSearchFilter("available", SearchOperator.eq, true);	
        searchable.addSort(Direction.ASC, "parent.id", "code");
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
        	userOrganizationIds.add(1L);
        	searchable.addSearchFilter("id", SearchOperator.in, userOrganizationIds);
        }
		List<Organization> list = organizationManager.findAll(searchable);
		List<Long> organizationIds = list.stream().map(c->c.getId()).collect(Collectors.toList());
        List<Organization> organizations = list.stream().filter(c->c.getParent() == null || c.getParent().getId() == null || !organizationIds.contains(c.getParent().getId())).collect(Collectors.toList());
        
		List<Long> parentIds = list.stream().filter(c->c.getParentId() != extId).map(Organization::getId).collect(Collectors.toList());
		for (int i=0; i<list.size(); i++){
			Organization e = list.get(i);
			if (e.isRoot())
				continue;
			
			if (extId == null || (extId!=null && extId != e.getId() && parentIds.contains(e.getId()))){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
//				map.put("pId", !user.isAdmin()&&e.getId().equals(user.getArea().getId())?0:e.getParent()!=null?e.getParent().getId():0);
				map.put("pId", e.isTop() ? 0 : (organizations.contains(e) ? 1 : e.getParent().getId()));
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		
		return mapList;
	}
    
  //@RequiresPermissions("sys:organization:view")
    @ResponseBody
    @RequestMapping(value = "export") //, method=RequestMethod.POST
    public String exportFile(Organization organization, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "organization" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx"; 
    		//Page<Organization> page = systemService.findOrganization(new Page<Organization>(request, response, -1), organization);
			Searchable searchable = Searchable.newSearchable();
            searchable.addSort(Direction.ASC, "parent.id", "code");
            List<Organization> sourcelist = organizationManager.findAll(searchable);
    		new ExportExcel(request, getText("organizationList.title", locale), Organization.class).setDataList(sourcelist).write(response, fileName).dispose();
    		
    		return null;//AjaxResponse.success().result();
		} catch (Exception e) {
			saveMessage(request, getText("message.export.failed.error", e.getMessage(), locale));
			//return AjaxResponse.fail(I18nUtils.getString("export.failed", locale, e.getMessage())).result();
		}
		
		return "redirect:/sys/companies/?repage";
    }

	//@RequiresPermissions("sys:organization:edit")
    @ResponseBody
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public Map<String,Object> importFile(MultipartFile file, final HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Locale locale = request.getLocale();
		try {
			int successNum = 0;
			ImportExcel ei = new ImportExcel(file, 1, 0);
			ExcelImportResult<Organization> importResult = ei.getDataList(Organization.class);
			List<Organization> listValidated = Lists.newArrayList();
			Validator validator = SpringContextHolder.getBean(Validator.class);
			for (int row : importResult.dataRow()){
				BindException errors = new BindException(importResult.data(row), "Organization");
				if (validator != null)
				{
					validator.validate(importResult.data(row), errors);
				}
				if (!errors.hasErrors()){
					listValidated.add(importResult.data(row));
				}else{
					saveError(request, errors);
				}
			}
			organizationManager.importFrom(listValidated, new String[] {"code"});
			//saveMessage(request, I18nUtils.getString("import.result", locale, successNum, failureNum ));
			
			return AjaxResponse.success(importResult.resultToString(successNum, locale)).result();			
			
		} catch (Exception e) {
			saveError(request, getText("message.import.failed", e.getMessage(), locale));
			return AjaxResponse.fail(I18nUtils.getString("message.import.failed", locale, e.getMessage())).result();
		}
    }
	
	//@RequiresPermissions("sys:organization:view")
    @ResponseBody
    @RequestMapping("import/template")
    public Map<String,Object> importFileTemplate(final HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
    	Locale locale = request.getLocale();
		try {
			String fileName = "organizationtemplate.xlsx";
			List<Organization> list = Lists.newArrayList();
			//list.add(organizationManager.getAll());
			new ExportExcel("organization data", Organization.class, 2).setDataList(list).write(response, fileName).dispose();
			
			return AjaxResponse.success().result();
		} catch (Exception e) {
			saveError(request, getText("message.templatedownload.failed", e.getMessage(), locale)); //
			return AjaxResponse.fail(I18nUtils.getString("message.templatedownload.failed", locale, e.getMessage())).result();
		}
    }
}
