package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;

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
@RequestMapping("/cm/galleryform*")
public class GalleryFormController extends BaseFormController {
    private GalleryManager galleryManager = null;
    private OrganizationManager organizationManager;

    @Autowired
    public void setGalleryManager(GalleryManager galleryManager) {
        this.galleryManager = galleryManager;
    }
    
    @Autowired
    public void setOrganizationManager(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    public GalleryFormController() {
        setCancelView("redirect:galleries");
        setSuccessView("redirect:galleries");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
        String id = request.getParameter("id");

        Gallery gallery = new Gallery();
        Organization organization = new Organization();
        if (!StringUtils.isBlank(id)) {
        	gallery = galleryManager.get(new Long(id));
        	if (!gallery.getGalleryMappings().isEmpty())
        		organization = gallery.getGalleryMappings().get(0).getOrganization();
        }else {
	        UserAccount user=UserUtils.getUser();
	        if (!user.isSystem()) {
	        	organization = organizationManager.get(user.getOrganizationId());
	        }
        }
        
        model.addAttribute("gallery", gallery);
        model.addAttribute("organization", organization);

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Gallery gallery, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(gallery, errors);
            
            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "cm/galleryform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (gallery.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            galleryManager.remove(gallery.getId());
            saveMessage(request, getText("gallery.deleted", locale));
        } else {
        	long oid = StringUtils.toLong(request.getParameter("organizationId"));
        	if (!UserUtils.hasRoleType(RoleType.sa) && !UserUtils.hasRoleType(RoleType.ad) && (oid == 0 || oid == Long.MIN_VALUE) ) {
        		saveError(request, getText("gallery.organization.required", request.getLocale()));
        		
        		return "cm/galleryform";
            }
        	
        	try {
        		GalleryBo galleryBo = CMUtils.createGalleryInstance();
        		if (!isNew) {
        			galleryBo.setGalleryId(gallery.getId());
        		}
        		String organizationId = request.getParameter("organizationId");
            	if (StringUtils.isNotBlank(organizationId)) {
            		galleryBo.addOrganization(new Long(organizationId));
            	}
        		galleryBo.setCreationDate(DateUtils.Now());
        		galleryBo.setDescription(gallery.getDescription());
        		galleryBo.setName(gallery.getName());
        		galleryBo.save();
        		gallery.setId(galleryBo.getGalleryId());
    			
        		//galleryManager.saveGallery(gallery);
        	}catch(final RecordExistsException e) { //RecordExistsException
        		errors.rejectValue("name", "gallery.existing.error",
                        new Object[] { gallery.getName() }, "gallery name existing");
        		//galleryManager.clear();
        		if (isNew) {
        			gallery.setId(null);
        		}

                return "cm/galleryform";
        	}catch(final Exception e) {
        		log.warn(e.getMessage());
                
        		return null;
        	}
        	
            //galleryManager.save(gallery);
            String key = (isNew) ? "gallery.added" : "gallery.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:galleryform?id=" + gallery.getId();
            }
        }

        return success;
    }
}

