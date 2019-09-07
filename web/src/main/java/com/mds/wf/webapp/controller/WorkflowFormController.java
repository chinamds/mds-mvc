package com.mds.wf.webapp.controller;

import com.mds.wf.service.ActivityManager;
import com.mds.wf.service.OrganizationWorkflowTypeManager;
import com.mds.wf.service.WorkflowManager;
import com.mds.wf.model.Workflow;
import com.mds.wf.model.WorkflowDetail;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.mapper.JsonMapper;
import com.mds.common.webapp.controller.BaseFormController;
import com.mds.core.ApprovalStatus;
import com.mds.core.LongCollection;
import com.mds.core.SecurityActions;
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
@RequestMapping("/wf/workflowform*")
public class WorkflowFormController extends BaseFormController {
    private WorkflowManager workflowManager = null;
    private OrganizationWorkflowTypeManager organizationWorkflowTypeManager = null;

    @Autowired
    public void setWorkflowManager(WorkflowManager workflowManager) {
        this.workflowManager = workflowManager;
    }
    
    private ActivityManager activityManager = null;

    @Autowired
    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }
  
    @Autowired
    public void setOrganizationWorkflowTypeManager(OrganizationWorkflowTypeManager organizationWorkflowTypeManager) {
        this.organizationWorkflowTypeManager = organizationWorkflowTypeManager;
    }

    public WorkflowFormController() {
        setCancelView("redirect:workflows");
        setSuccessView("redirect:workflows");
    }

    //@ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected Model showForm(HttpServletRequest request)
    throws Exception {
    	Model model = new ExtendedModelMap();
        String id = request.getParameter("id");

        Workflow workflow  = new Workflow();
        if (!StringUtils.isBlank(id)) {
        	workflow = workflowManager.get(new Long(id));
        }

        
        model.addAttribute("workflow", workflow);
        model.addAttribute("method", "");

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView onSubmit(Workflow workflow, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return new ModelAndView(getCancelView());
        }

        workflow.setCurrentUser(UserUtils.getLoginName());        
        if (validator != null) { // validator is null during testing
            validator.validate(workflow, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return new ModelAndView("wf/workflowform").addObject("method", "");
            }
        }
        
        log.debug("entering 'onSubmit' method...");

        boolean isNew = (workflow.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            workflowManager.remove(workflow.getId());
            saveMessage(request, getText("workflow.deleted", locale));
        } else {
        	if (StringUtils.isNotBlank(request.getParameter("tblAppendGrid_rowOrder"))) {
    	        Long[] uniqueIndexes = ConvertUtil.StringtoLongArray(request.getParameter("tblAppendGrid_rowOrder"));
    		     // Process on each row by using for-loop
    		     for (int i=0; i<uniqueIndexes.length; i++) {
    		    	 Long row = uniqueIndexes[i];
    		    	 WorkflowDetail workflowDetail = new WorkflowDetail();
    		         workflowDetail.setSeq(i);
    		         workflowDetail.setWorkflow(workflow);
    		         
    		         // Get the posted values by using `grid ID` + `column name` + `unique index` syntax
    		         //ContentObjectBo contentObject = CMUtils.loadContentObjectInstance(new Long(request.getParameter("tblAppendGrid_activityId_" + row)));
    		         //workflowDetail.setZoneFile(contentObject.getOriginal().getFileName());
    		         long activityId = StringUtils.toLong(request.getParameter("tblAppendGrid_activity_" + row));
    		         long workflowDetailId = StringUtils.toLong(request.getParameter("tblAppendGrid_id_" + row));
    		         if (workflowDetailId > 0) {
    		        	 workflowDetail.setId(workflowDetailId);
    		         }
    		         
    		         //String apply = request.getParameter("tblAppendGrid_apply_" + row);
    		         //workflowDetail.setApply(request.getParameter("tblAppendGrid_apply_" + row)== null ? false : (boolean)StringUtils.toValue(request.getParameter("tblAppendGrid_apply_" + row), boolean.class).get());
    		         workflowDetail.setApply(request.getParameter("tblAppendGrid_apply_" + row)== null ? false : true);
    		         workflowDetail.setApproval(request.getParameter("tblAppendGrid_approval_" + row)== null ? false : true);
    		         workflowDetail.setEmail(request.getParameter("tblAppendGrid_email_" + row)== null ? false : true);
    		         //workflowDetail.setApproval(request.getParameter("tblAppendGrid_approval_" + row)== null ? false : (boolean)StringUtils.toValue(request.getParameter("tblAppendGrid_approval_" + row), boolean.class).get());
    		         //workflowDetail.setEmail(request.getParameter("tblAppendGrid_email_" + row)== null ? false : (boolean)StringUtils.toValue(request.getParameter("tblAppendGrid_email_" + row), boolean.class).get());
    		         workflowDetail.setActivity(activityManager.get(activityId));
    		         
    		         //workflowDetail.setContentObject(contentObject);
    		         //workflowDetail.setZoneMute(StringUtilsTrequest.getParameter("tblAppendGrid_approval_" + row));
    		         // Do whatever to fit your needs, such as save to database
    		         
    		         workflow.getWorkflowDetails().add(workflowDetail);
    		     }
            }
        	
        	if (workflow.getWorkflowDetails().isEmpty()) {
        		saveError(request, getText("workflowDetail.required", locale));
        		return new ModelAndView("wf/workflowform");
        	}
        	
        	if (workflow.getWorkflowType() != null && workflow.getWorkflowType().getId() != null) {
        		workflow.setWorkflowType(organizationWorkflowTypeManager.get(workflow.getWorkflowType().getId()));
        	}
        	
        	try {
        		workflowManager.saveWorkflow(workflow);
	        } catch (final RecordExistsException e) {
	        	workflowManager.clear();
	        	if (isNew) {
	        		workflow.setId(null);
	        	}
	        	
	        	errors.rejectValue("workflowName", "workflow.existing.error",
                        new Object[] { workflow.getWorkflowName()}, "Workflow existing");
	        	
	        	return new ModelAndView("wf/workflowform").addObject("method", JsonMapper.getInstance().toJson(workflowManager.toAppendGridData(workflow.getWorkflowDetails(), request)));
	        }
            
            String key = (isNew) ? "workflow.added" : "workflow.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:workflowform?id=" + workflow.getId();
            }
        }

        return new ModelAndView(success);
    }
}
