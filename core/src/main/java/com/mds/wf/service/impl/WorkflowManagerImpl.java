package com.mds.wf.service.impl;

import com.mds.wf.dao.WorkflowDao;
import com.mds.wf.dao.WorkflowDetailDao;
import com.mds.wf.model.Workflow;
import com.mds.wf.model.WorkflowDetail;
import com.mds.wf.service.WorkflowManager;
import com.mds.wf.service.WorkflowService;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.common.exception.RecordExistsException;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.model.search.exception.SearchException;
import com.mds.common.model.search.filter.SearchFilter;
import com.mds.common.model.search.filter.SearchFilterHelper;
import com.mds.common.service.impl.GenericManagerImpl;
import com.mds.core.WorkflowType;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.model.User;
import com.mds.sys.util.UserAccount;
import com.mds.sys.util.UserUtils;
import com.mds.util.ConvertUtil;
import com.mds.util.DateUtils;
import com.mds.util.StringUtils;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Service("workflowManager")
@WebService(serviceName = "WorkflowService", endpointInterface = "com.mds.service.WorkflowService")
public class WorkflowManagerImpl extends GenericManagerImpl<Workflow, Long> implements WorkflowManager, WorkflowService {
    WorkflowDao workflowDao;
    WorkflowDetailDao workflowDetailDao;

    @Autowired
    public WorkflowManagerImpl(WorkflowDao workflowDao) {
        super(workflowDao);
        this.workflowDao = workflowDao;
    }
    
    @Autowired
    public void setWorkflowDetailDao(WorkflowDetailDao workflowDetailDao) {
        this.workflowDetailDao = workflowDetailDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Workflow getWorkflow(final String workflowId) {
        return workflowDao.get(new Long(workflowId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Workflow> getWorkflows() {
        return workflowDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Workflow> searchWorkflows(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return workflowDao.search(pageable, new String[]{"code", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> workflowsSelect2(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	if (StringUtils.isBlank(searchTerm))
    		return toSelect2Data(workflowDao.find(pageable).getContent());
       
        return toSelect2Data(workflowDao.search(pageable, new String[]{"code", "description"}, searchTerm).getContent());
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> workflowsTable(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);
        
        Page<Workflow> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		searchable.addSort(Direction.DESC, "apptDate", "apptPeriod.timeFrom", "apptPeriod.apptItem.code");
    		list = workflowDao.find(searchable);
    	}else {
    		searchable.addSort(Direction.DESC, "apptDate", "timeRange", "apptCode");
    		list = workflowDao.search(searchable, searchTerm);
    	}
    	    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", list.getContent());
		
		return resultData;
		
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidMDSRoleException 
     * @throws SearchException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> workflowsTable(String organizationId, String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws SearchException, InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "workflowType.workflowType", "workflowName");
        searchable.setPage(pageable);
        
    	long oId = StringUtils.toLong(organizationId);
    	if (oId != Long.MIN_VALUE && oId != 0) {
			searchable.addSearchFilter("workflowType.organization.id", SearchOperator.eq, oId);
		}else {
			UserAccount user = UserUtils.getUser();
	    	if (!user.isSystem()) {
	    		searchable.addSearchFilter("workflowType.organization.id", SearchOperator.in, UserUtils.getUserOrganizationIds(user.getUsername()));
	    	}
    	}
        
    	Page<Workflow> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = workflowDao.find(searchable);
    	}else {
    		SearchFilter codeFilter = SearchFilterHelper.newCondition("workflowType.organization.code", SearchOperator.contain, searchTerm);
            SearchFilter nameFilter = SearchFilterHelper.newCondition("workflowName", SearchOperator.contain, searchTerm);
            SearchFilter and1 = SearchFilterHelper.or(codeFilter, nameFilter);
            searchable.addSearchFilter(and1);
            
    		list = workflowDao.find(searchable);
      	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
		
    }
    
    /**
   	 * convert Workflow data to Bootstrap Table Data format
   	 * @param workflows
   	 * @return
        * @throws Exception 
   	 */
   	private  List<HashMap<String,Object>> toBootstrapTableData(List<Workflow> workflows, HttpServletRequest request){
   		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
   		for (Workflow u : workflows) {
   			//Organization's Workflow Type list		
   			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
   			mapData.put("workflowName", u.getWorkflowName());//workflow name
   			mapData.put("flowchart", u.getFlowchartScript());//workflow name
   			mapData.put("workflowType", I18nUtils.getWorkflowType(u.getWorkflowType().getWorkflowType(), request));//workflow type title
   			mapData.put("organizationCode", u.getWorkflowType().getOrganizationCode());//organization Code
   			//mapData.put("workflowImage", "");//workflow name
   			mapData.put("id", u.getId());//role id
   			mapData.put("createdBy", u.getCreatedBy());
   			mapData.put("dateAdded", DateUtils.jsonSerializer(u.getDateAdded()));//create date
   			//mapData.put("description", u.getDescription());//
   			list.add(mapData);
   		}
   				
   		return list;
   	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Workflow saveWorkflow(final Workflow workflow) throws RecordExistsException {

        try {
            return workflowDao.saveWorkflow(workflow);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Workflow type: '" + workflow.getWorkflowType() + "' workflow name: '" + workflow.getWorkflowName() +  "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Workflow addWorkflow(final Workflow workflow) throws RecordExistsException {

        try {
            return workflowDao.addWorkflow(workflow);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("Workflow type: '" + workflow.getWorkflowType() + "' workflow name: '" + workflow.getWorkflowName() +  "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Workflow userAppointment(String mobile, String idNumber, Workflow workflow) 
    		throws RecordExistsException {
		
		return saveWorkflow(workflow);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Response removeWorkflow(final String workflowIds) throws WebApplicationException{
        log.debug("removing workflow: " + workflowIds);
        try {
        	workflowDao.remove(ConvertUtil.StringtoLongArray(workflowIds));
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + workflowIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     */
/*    @Override
    public Response changeWorkflowStatus(String workflowId, String workflowStatus) {
    	try {
        	Workflow workflow = workflowDao.get(new Long(workflowId));
        	if (workflow != null){
        		workflow.setWorkflowType(WorkflowType.valueOf(workflowStatus));
        		workflowDao.save(workflow);
        	}
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    	
    	return Response.ok().build();
    }*/
        
    /**
	 * convert workflow data to select2 format(https://select2.org/data-sources/formats)
	 * {
	 *	  "results": [
	 *	    {
	 *	      "id": 1,
	 *	      "text": "Option 1"
	 *    	},
	 *	    {
	 *	      "id": 2,
	 *	      "text": "Option 2",
	 *	      "selected": true
	 *	    },
	 *	    {
	 *	      "id": 3,
	 *	      "text": "Option 3",
	 *	      "disabled": true
	 *	    }
	 *	  ]
	 *	}
	 * @param workflows
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<Workflow> workflows){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (Workflow u : workflows) {
			//workflow list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getWorkflowName());//workflow name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//workflow id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	@Override
	public HashMap<String, Object> getWorkflowDetail(String workflowId, HttpServletRequest request) throws Exception{
		Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "seq");
        searchable.addSearchFilter("workflow.id", SearchOperator.eq, new Long(workflowId));
        List<WorkflowDetail> workflowDetails = workflowDetailDao.findAll(searchable);
       		
		return toAppendGridData(workflowDetails, request);
	}
	
	@Override
	public  HashMap<String,Object> toAppendGridData(List<WorkflowDetail> workflowDetails, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (WorkflowDetail u : workflowDetails) {
			//workflow list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("seq", u.getSeq());//workflow detail seq
			mapData.put("activity", u.getActivity().getId() + "," + u.getActivity().getCode());//activity code
			mapData.put("id", u.getId());//workflow  detail id
			mapData.put("email", u.isEmail() ? 1 : 0);//is email
			mapData.put("apply", u.isApply() ? 1 : 0);//is apply activity
			mapData.put("approval", u.isApproval() ? 1 : 0);//is approval activity
			mapData.put("activityId", u.getActivity().getId());//activity id
			
			list.add(mapData);
		}
		
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
        
    	resultData.put("total", workflowDetails.size());
		resultData.put("rows", list);
		
		return resultData;
	}
	
	public Workflow getApplyWorkflow(WorkflowType workflowType, String userName) {
		UserAccount user = UserUtils.getUser();
    	//Searchable searchable = Searchable.newSearchable();
    	return workflowDao.findApplyWorkflow(workflowType, user.getId(), user.getOrganizationId());
    }
	
	@Override
    public Response getQRCode(String refNo){
		Searchable searchable = Searchable.newSearchable();
	    searchable.addSearchFilter("refNo", SearchOperator.eq, refNo);
	    Workflow workflow = workflowDao.findOne(searchable);
        // uncomment line below to send non-streamed
		 //QRCode.from("Hello World").to(ImageType.PNG).writeTo(outputStream);

        //return Response.ok(QRCode.from(workflow.getRefNo()).withSize(250, 250).stream()).build();
        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
	    ByteArrayInputStream bas = null;
    	try {
	    	File file =QRCode.from(workflow.getWorkflowName()).to(ImageType.PNG).withSize(250, 250).file();
	    	bas =new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    	} catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        
        return Response.ok(bas).build();
    }
}