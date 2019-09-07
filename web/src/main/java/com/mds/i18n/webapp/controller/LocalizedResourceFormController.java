package com.mds.i18n.webapp.controller;

import org.apache.commons.lang.StringUtils;

import com.mds.i18n.service.CultureManager;
import com.mds.i18n.service.LocalizedResourceManager;
import com.mds.i18n.service.NeutralResourceManager;
import com.mds.sys.util.UserUtils;
import com.mds.i18n.model.LocalizedResource;
import com.mds.i18n.model.ResourceCategory;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.webapp.controller.BaseFormController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/i18n/localizedResourceform*")
public class LocalizedResourceFormController extends BaseFormController {
    private LocalizedResourceManager localizedResourceManager = null;
    private CultureManager cultureManager = null;
    private NeutralResourceManager neutralResourceManager = null;

    @Autowired
    public void setLocalizedResourceManager(LocalizedResourceManager localizedResourceManager) {
        this.localizedResourceManager = localizedResourceManager;
    }
    
    @Autowired
    public void setCultureManager(CultureManager cultureManager) {
        this.cultureManager = cultureManager;
    }
    
    @Autowired
    public void setNeutralResourceManager(NeutralResourceManager neutralResourceManager) {
        this.neutralResourceManager = neutralResourceManager;
    }

    public LocalizedResourceFormController() {
        setCancelView("redirect:localizedResources");
        setSuccessView("redirect:localizedResources");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
    	model.addAttribute("resourceCategories", ResourceCategory.values());
    	
        String id = request.getParameter("id");

        LocalizedResource localizedResource = new LocalizedResource();
        if (!StringUtils.isBlank(id)) {
        	localizedResource = localizedResourceManager.get(new Long(id));
        }
        model.addAttribute("localizedResource", localizedResource);

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(LocalizedResource localizedResource, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(localizedResource, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "i18n/localizedResourceform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (localizedResource.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            localizedResourceManager.remove(localizedResource.getId());
            saveMessage(request, getText("localizedResource.deleted", locale));
        } else {
        	if (localizedResource.getCulture() == null){
        		if (!StringUtils.isBlank(request.getParameter("cultureId"))) {
        			localizedResource.setCulture(cultureManager.get(new Long(request.getParameter("cultureId"))));
        		}
        	}else {
        		Searchable searchable = Searchable.newSearchable();
        		searchable.addSearchFilter("cultureCode", SearchOperator.eq, localizedResource.getCulture().getCultureCode());	
        		localizedResource.setCulture(cultureManager.findAll(searchable).get(0));
        	}
        	if (localizedResource.getNeutralResource() == null){
        		if (!StringUtils.isBlank(request.getParameter("neutralResourceId"))) {
        			localizedResource.setNeutralResource(neutralResourceManager.get(new Long(request.getParameter("neutralResourceId"))));
        		}
        	}else {
        		Searchable searchable = Searchable.newSearchable();
        		searchable.addSearchFilter("resourceKey", SearchOperator.eq, localizedResource.getNeutralResource().getResourceKey());	
        		localizedResource.setNeutralResource(neutralResourceManager.findAll(searchable).get(0));
        		//localizedResource.setNeutralResource(neutralResourceManager.get(localizedResource.getNeutralResource().getId()));
        	}
        	
        	try {
        		localizedResourceManager.saveLocalizedResource(localizedResource);
        	}catch(final RecordExistsException e) {
        		errors.rejectValue("neutralResource", "localizedResource.existing.error",
                        new Object[] { localizedResource.getNeutralResource().getResourceKey()}, "Resource key existing");
        		localizedResourceManager.clear();
        		if (isNew) {
        			localizedResource.setId(null);
        		}

                return "localizedResourceform";
        	}
        	
            String key = (isNew) ? "localizedResource.added" : "localizedResource.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:localizedResourceform?id=" + localizedResource.getId();
            }
        }

        return success;
    }
}
