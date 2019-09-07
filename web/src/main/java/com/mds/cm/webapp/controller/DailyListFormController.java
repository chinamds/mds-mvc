package com.mds.cm.webapp.controller;

import com.mds.cm.service.DailyListManager;
import com.mds.cm.util.AlbumUtils;
import com.mds.cm.util.CMUtils;
import com.mds.cm.content.AlbumBo;
import com.mds.cm.content.ContentObjectBo;
import com.mds.cm.content.GalleryBo;
import com.mds.cm.model.DailyList;
import com.mds.cm.model.DailyListItem;
import com.mds.cm.model.DailyListZone;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.mapper.JsonMapper;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.webapp.controller.BaseFormController;
import com.mds.core.ApprovalAction;
import com.mds.core.ApprovalStatus;
import com.mds.core.LongCollection;
import com.mds.core.SecurityActions;
import com.mds.core.exception.BusinessException;
import com.mds.sys.model.Organization;
import com.mds.sys.model.RoleType;
import com.mds.sys.service.OrganizationManager;
import com.mds.sys.util.RoleUtils;
import com.mds.sys.util.UserUtils;
import com.mds.util.ConvertUtil;
import com.mds.util.DateUtils;
import com.mds.util.StringUtils;
import com.mds.util.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/cm/dailyListform*")
public class DailyListFormController extends BaseFormController {
    private DailyListManager dailyListManager = null;
    private OrganizationManager organizationManager;

    @Autowired
    public void setDailyListManager(DailyListManager dailyListManager) {
        this.dailyListManager = dailyListManager;
    }
    
    @Autowired
    public void setOrganizationManager(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    public DailyListFormController() {
        setCancelView("redirect:dailyLists");
        setSuccessView("redirect:dailyLists");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
        String id = request.getParameter("id");

        GalleryBo gallery = null;
        DailyList dailyList  = new DailyList();
        if (request.getParameter("defaultList") == null) {
        if (!StringUtils.isBlank(id)) {
        	dailyList = dailyListManager.get(new Long(id));
        	if (dailyList.getGallery() != null) {
        		gallery = CMUtils.loadGallery(dailyList.getGallery().getId());
        	}
        }else {
        	if (Utils.isIndependentSpaceForDailyList()) {
	        	gallery = CMUtils.loadLoginUserGalleries().stream().findFirst().orElse(null);
	        	if (gallery != null)	{
	   				dailyList.setGallery(CMUtils.getGallery(gallery.getGalleryId()));
	    		}
        	}else {
        		dailyList.setOrganization(organizationManager.get(UserUtils.getUserOrganizationId()));
        	}
        }
	        model.addAttribute("method", request.getParameter("method"));
        }else {
        	if (!StringUtils.isBlank(id)) {
        		dailyList = dailyListManager.get(new Long(id));
	        	if (dailyList.getGallery() != null) {
	        		gallery = CMUtils.loadGallery(dailyList.getGallery().getId());
	        	}
	        	model.addAttribute("method", "Edit");
        	}else {
	        	Searchable searchable = Searchable.newSearchable();
	        	String gid = request.getParameter("gid");
	        	searchable.addSearchFilter("date", SearchOperator.eq, DateUtils.MinValue);
	        	searchable.addSearchFilter("gallery.id", SearchOperator.eq, new Long(gid));
	        	dailyList = dailyListManager.findOne(searchable);
	        	if (dailyList == null) {
	        		dailyList  = new DailyList(DateUtils.MinValue);
	        		dailyList.setGallery(CMUtils.getGallery(new Long(gid)));
	        		model.addAttribute("method", "Add");
	        	}else {
	        		model.addAttribute("method", "Edit");
	        	}
	        	gallery = CMUtils.loadGallery(new Long(gid));
	        	
        	}
        	model.addAttribute("defaultList", true);
        }
        
        model.addAttribute("dailyList", dailyList);
        model.addAttribute("requiredSecurityPermissions", SecurityActions.ViewAlbumOrContentObject.value());
        model.addAttribute("jsonItems", "");
        model.addAttribute("independentSpaceForDailyList", Utils.isIndependentSpaceForDailyList());

        AlbumBo albumToSelect = null;
		if (gallery != null)	{
			model.addAttribute("galleryId", gallery.getGalleryId());
			albumToSelect = CMUtils.loadRootAlbum(gallery.getGalleryId(), RoleUtils.getMDSRolesForUser(), UserUtils.isAuthenticated());
		}
		if (albumToSelect != null) {
			model.addAttribute("selectedAlbumIds", new LongCollection(new long[] { albumToSelect.getId() }));
		}else {
			model.addAttribute("selectedAlbumIds", new LongCollection(new long[] { 1 }));
		}

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView onSubmit(DailyList dailyList, String approvalOpinion, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
        	if ("Approve".equals(request.getParameter("method"))) {
        		return new ModelAndView("redirect:dailyListsApproval");
        	} else if ("true".equalsIgnoreCase(request.getParameter("defaultList"))) {
        		return new ModelAndView("redirect:galleries");
        	}
        	
            return new ModelAndView(getCancelView());
        }
        if ("true".equalsIgnoreCase(request.getParameter("defaultList")) && dailyList.getDate() == null) {
        	dailyList.setDate(DateUtils.MinValue);
        }

        Locale locale = request.getLocale();
        if (request.getParameter("approve") != null || request.getParameter("reject") != null) {
        	try {
        		dailyListManager.dailyListApprove(dailyList, request.getParameter("approve") != null ? ApprovalAction.Approve : ApprovalAction.Reject, approvalOpinion);
        	} catch (final BusinessException be) {
        		saveError(request, getText("dailyList.workflow.required", locale));
        		
        		return new ModelAndView("cm/dailyListform").addObject("jsonItems", JsonMapper.getInstance().toJson(dailyListManager.toAppendGridData(dailyList.getDailyListItems(), request)))
        				.addObject("method", request.getParameter("method"))
        				.addObject("independentSpaceForDailyList", request.getParameter("independentSpaceForDailyList"))
        				.addObject("currentStep", request.getParameter("currentStep"));
	        }
        	
        	return new ModelAndView("redirect:dailyListsApproval");
        }

        dailyList.setCurrentUser(UserUtils.getLoginName());
        dailyList.setApprovalStatus(ApprovalStatus.NotSpecified);
        if (Utils.isIndependentSpaceForDailyList()) {
	        if (request.getParameter("method").equals("Add") && !"true".equalsIgnoreCase(request.getParameter("defaultList"))) {
		        if (dailyList.getGallery() != null && dailyList.getGallery().getId() != null) {
		        	dailyList.setGallery(CMUtils.getGallery(dailyList.getGallery().getId()));
		        }else {
		        	dailyList.setGallery(CMUtils.getGallery(CMUtils.loadLoginUserGalleries().get(0).getGalleryId()));
		        }
	        }else {
	        	dailyList.setGallery(CMUtils.getGallery(new Long(request.getParameter("galleryId"))));
	        }
        }
        
        long oid = StringUtils.toLong(request.getParameter("organizationId"));
        if (validator != null) { // validator is null during testing
            validator.validate(dailyList, errors);
            
            if (!Utils.isIndependentSpaceForDailyList()) {
            	if (!UserUtils.hasRoleType(RoleType.sa) && !UserUtils.hasRoleType(RoleType.ad) && (oid == Long.MIN_VALUE || oid == 0) ) {
                    errors.rejectValue("organization", "errors.required", new Object[] { getText("dailyList.organization", request.getLocale()) },
                            "Organization is a required field.");
                }
            }

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return new ModelAndView("cm/dailyListform").addObject("jsonItems", "").addObject("method", request.getParameter("method"))
                		.addObject("independentSpaceForDailyList", request.getParameter("independentSpaceForDailyList"))
                		.addObject("currentStep", request.getParameter("currentStep"))
                		.addObject("defaultList", request.getParameter("defaultList"));
            }
        }
        

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (dailyList.getId() == null);
        String success = "true".equalsIgnoreCase(request.getParameter("defaultList")) ? "redirect:galleries" : getSuccessView();

        if (request.getParameter("delete") != null) {
            dailyListManager.remove(dailyList.getId());
            saveMessage(request, getText("dailyList.deleted", locale));
        } else {
        	if (!Utils.isIndependentSpaceForDailyList()) {
	    	    if (oid != Long.MIN_VALUE && oid != 0) {
	    	    	dailyList.setOrganization(organizationManager.get(oid));
	 			}else {
	 				if (UserUtils.hasRoleType(RoleType.sa) || UserUtils.hasRoleType(RoleType.ad)) {
	 					dailyList.setOrganization(organizationManager.get(Organization.getRootId()));
	 				}
	 			}
    	    }
        	
        	 Long[] uniqueIndexes = ConvertUtil.StringtoLongArray(request.getParameter("tblAppendGrid_rowOrder"));
    	     // Process on each row by using for-loop
    	     for (int i=0; i<uniqueIndexes.length; i++) {
    	    	 Long row = uniqueIndexes[i];
    	    	 DailyListItem dailyListItem = new DailyListItem();
    	    	 long dailyListItemId = StringUtils.toLong(request.getParameter("tblAppendGrid_dailyListItemId_" + row));
    	    	 if (dailyListItemId > 0) {
    	    		 dailyListItem.setId(dailyListItemId);
    	         }
    	         dailyListItem.setIndex(i);
    	         dailyListItem.setLayout("H01");
    	         dailyListItem.setDailyList(dailyList);
    	         
    	         DailyListZone dailyListZone = new DailyListZone();
    	         // Get the posted values by using `grid ID` + `column name` + `unique index` syntax
    	         dailyListZone.setContentName(request.getParameter("tblAppendGrid_content_" + row));
    	         dailyListZone.setZoneFile(request.getParameter("tblAppendGrid_fileName_" + row));
    	         dailyListZone.setZoneType((short)(StringUtils.toShort(request.getParameter("tblAppendGrid_contentType_" + row), (short)0)));
    	         //ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(new Long(request.getParameter("tblAppendGrid_contentObjectId_" + row)));
    	         //dailyListZone.setZoneFile(contentObject.getOriginal().getFileName());
    	         long contentObjectId = StringUtils.toLong(request.getParameter("tblAppendGrid_contentObjectId_" + row));
    	         long dailyListZoneId = StringUtils.toLong(request.getParameter("tblAppendGrid_id_" + row));
    	         if (dailyListZoneId > 0) {
    	        	 dailyListZone.setId(dailyListZoneId);
    	         }
    	         dailyListZone.setZoneDuration(StringUtils.toFloat(request.getParameter("tblAppendGrid_duration_" + row), 15.0f));
    	         //dailyListZone.setZoneMute(request.getParameter("tblAppendGrid_mute_" + row)== null ? false : (boolean)StringUtils.toValue(request.getParameter("tblAppendGrid_mute_" + row), boolean.class).get());
    	         dailyListZone.setZoneMute(request.getParameter("tblAppendGrid_mute_" + row)== null ? false : true);
    	         dailyListZone.setVolume(dailyListZone.isZoneMute() ? 0 : 100.0f);
    	         //dailyListZone.setZoneRatio(request.getParameter("tblAppendGrid_aspectRatio_" + row)== null ? false : (boolean)StringUtils.toValue(request.getParameter("tblAppendGrid_aspectRatio_" + row), boolean.class).get());
    	         dailyListZone.setZoneRatio(request.getParameter("tblAppendGrid_aspectRatio_" + row)== null ? false : true);
    	         dailyListZone.setStartTime(DateUtils.getFromTimeString(request.getParameter("tblAppendGrid_timeFrom_" + row)));
    	         dailyListZone.setEndTime(DateUtils.getFromTimeString(request.getParameter("tblAppendGrid_timeTo_" + row)));
    	         dailyListZone.setContentObject(CMUtils.getContentObject(contentObjectId));
    	         dailyListZone.setDailyListItem(dailyListItem);
    	         dailyListItem.getDailyListZones().add(dailyListZone);
    	         //dailyListZone.setContentObject(contentObject);
    	         //dailyListZone.setZoneMute(StringUtilsTrequest.getParameter("tblAppendGrid_mute_" + row));
    	         // Do whatever to fit your needs, such as save to database
    	         
    	         dailyList.getDailyListItems().add(dailyListItem);
    	     }
    	     
    	     if (dailyList.getDailyListItems().isEmpty()) {
         		saveError(request, getText("dailyListItem.required", locale));
         		return new ModelAndView("cm/dailyListform").addObject("method", request.getParameter("method"))
         				.addObject("independentSpaceForDailyList", request.getParameter("independentSpaceForDailyList"))
         				.addObject("currentStep", request.getParameter("currentStep"))
         				.addObject("defaultList", request.getParameter("defaultList"));
         	}
    	     
        	try {
        		dailyListManager.saveDailyList(dailyList);
        	} catch (final BusinessException be) {
        		saveError(request, getText("dailyList.workflow.required", locale));
        		
        		return new ModelAndView("cm/dailyListform").addObject("jsonItems", JsonMapper.getInstance().toJson(dailyListManager.toAppendGridData(dailyList.getDailyListItems(), request)))
        				.addObject("method", request.getParameter("method"))
        				.addObject("independentSpaceForDailyList", request.getParameter("independentSpaceForDailyList"))
        				.addObject("currentStep", request.getParameter("currentStep"))
        				.addObject("defaultList", request.getParameter("defaultList"));
        		
	        } catch (final RecordExistsException e) {
	        	dailyListManager.clear();
	        	if (isNew) {
	        		dailyList.setId(null);
	        	}
	        	
	        	errors.rejectValue("contentName", "dailyList.existing.error",
                        new Object[] { dailyList.getContentName(), DateUtils.formatDate(dailyList.getDate(), "yyyy-MM-dd")}, "Daily List existing");
	        	
	        	return new ModelAndView("cm/dailyListform").addObject("jsonItems", JsonMapper.getInstance().toJson(dailyListManager.toAppendGridData(dailyList.getDailyListItems(), request)))
	        			.addObject("method", request.getParameter("method"))
	        			.addObject("independentSpaceForDailyList", request.getParameter("independentSpaceForDailyList"))
	        			.addObject("currentStep", request.getParameter("currentStep"))
	        			.addObject("defaultList", request.getParameter("defaultList"));
	        }
            
            String key = (isNew) ? "dailyList.added" : "dailyList.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
            	if ("true".equalsIgnoreCase(request.getParameter("defaultList"))) {
            		success = "redirect:dailyListform?method=Edit&defaultList=true&id=" + dailyList.getId();
            	}else {
            		success = "redirect:dailyListform?method=Edit&id=" + dailyList.getId();
            	}
            }
        }

        return new ModelAndView(success);
    }
}
