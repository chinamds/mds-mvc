/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.wf.controller;

import com.mds.aiotplayer.wf.service.ActivityManager;
import com.mds.aiotplayer.wf.model.Activity;
import com.mds.aiotplayer.wf.model.ActivityOrganizationUser;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.core.ApprovalStatus;
import com.mds.aiotplayer.core.LongCollection;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.sys.model.Organization;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/wf/activityform*")
public class ActivityFormController extends BaseFormController {
    private ActivityManager activityManager = null;
    private OrganizationManager organizationManager;

    @Autowired
    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }   
    
    @Autowired
    public void setOrganizationManager(OrganizationManager organizationManager) {
        this.organizationManager = organizationManager;
    }

    public ActivityFormController() {
        setCancelView("redirect:activities");
        setSuccessView("redirect:activities");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
        String id = request.getParameter("id");

        Activity activity  = new Activity();
        if (!StringUtils.isBlank(id)) {
        	activity = activityManager.get(new Long(id));
        }else {
        	activity.setOrganization(organizationManager.get(UserUtils.getUserOrganizationId()));
        }

        List<Long> selectOrganizations = activity.getActivityOrganizationUsers().stream().filter(a->a.getOrganization() != null).map(a->a.getOrganization().getId()).collect(Collectors.toList());
        Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "username");
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
        	if (activity.getOrganization() != null) {
        		userOrganizationIds = (List<Long>) CollectionUtils.intersection(userOrganizationIds, UserUtils.getOrganizationChildren(activity.getOrganization().getId()));
        	}
           	userOrganizationIds.removeAll(selectOrganizations);

       		searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
        }else {
        	if (activity.getOrganization() != null) {
	        	List<Long> childOrganizationIds = UserUtils.getOrganizationChildren(activity.getOrganization().getId());
	        	if (!childOrganizationIds.contains(activity.getOrganization().getId())) {
	        		childOrganizationIds.add(activity.getOrganization().getId());
	        	}
	        	childOrganizationIds.removeAll(selectOrganizations);
	       		searchable.addSearchFilter("organization.id", SearchOperator.in, childOrganizationIds);
        	}
        }
        
        model.addAttribute("activity", activity);
        List<User> users = getUserManager().findAll(searchable); 
        model.addAttribute("users", users);
        model.addAttribute("selectedUsers", activity.getActivityOrganizationUsers().stream().filter(u->u.getUser() != null).collect(Collectors.toList()));

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Activity activity, Long[] organizationIds, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        long oid = StringUtils.toLong(request.getParameter("organizationId"));
        if (validator != null) { // validator is null during testing
            validator.validate(activity, errors);

            if (!UserUtils.hasRoleType(RoleType.sa) && !UserUtils.hasRoleType(RoleType.ad) && (oid == Long.MIN_VALUE || oid == 0)) {
                errors.rejectValue("organization", "errors.required", new Object[] { getText("role.organization", request.getLocale()) },
                        "Organization is a required field.");
            }
            
            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "cm/activityform";
            }
        }
        

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (activity.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            activityManager.remove(activity.getId());
            saveMessage(request, getText("activity.deleted", locale));
        } else {
        	List<ActivityOrganizationUser> activityOrganizationUsers = Lists.newArrayList();
        	List<Organization> organizations = organizationManager.find(organizationIds);
        	for (Organization organization : organizations) {
        		activityOrganizationUsers.add(new ActivityOrganizationUser(activity, organization, null));
        	}
        	final long[] userIds = Utils.getLongParameters(request, "users");
        	List<User> users = getUserManager().find(ArrayUtils.toObject(userIds));
        	for (User user : users) {
        		activityOrganizationUsers.add(new ActivityOrganizationUser(activity, null, user));
        	}
        	activity.setActivityOrganizationUsers(activityOrganizationUsers);
        	
        	if (oid != 0 && oid != Long.MIN_VALUE) {
				activity.setOrganization(organizationManager.get(oid));
			}else {
				if (UserUtils.hasRoleType(RoleType.sa) || UserUtils.hasRoleType(RoleType.ad)) {
					activity.setOrganization(organizationManager.get(Organization.getRootId()));
				}
			}
        	
        	activity.setCurrentUser(UserUtils.getLoginName());
            
        	try {
        		activityManager.saveActivity(activity);
	        } catch (final RecordExistsException e) {
	        	activityManager.clear();
	        	if (isNew) {
	        		activity.setId(null);
	        	}
	        	
	        	errors.rejectValue("code", "activity.existing.error",
                        new Object[] { activity.getCode()}, "Activity existing");
	        	
	        	return "cm/activityform";
	        }
            
            String key = (isNew) ? "activity.added" : "activity.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:activityform?id=" + activity.getId();
            }
        }

        return success;
    }
}
