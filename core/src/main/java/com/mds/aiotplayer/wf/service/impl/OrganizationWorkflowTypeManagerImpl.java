/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.wf.service.impl;

import com.mds.aiotplayer.wf.dao.OrganizationWorkflowTypeDao;
import com.mds.aiotplayer.wf.model.OrganizationWorkflowType;
import com.mds.aiotplayer.wf.service.OrganizationWorkflowTypeManager;
import com.mds.aiotplayer.wf.service.OrganizationWorkflowTypeService;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.model.search.exception.SearchException;
import com.mds.aiotplayer.common.model.search.filter.SearchFilter;
import com.mds.aiotplayer.common.model.search.filter.SearchFilterHelper;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.StringUtils;

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

@Service("organizationWorkflowTypeManager")
@WebService(serviceName = "OrganizationWorkflowTypeService", endpointInterface = "com.mds.aiotplayer.wf.service.OrganizationWorkflowTypeService")
public class OrganizationWorkflowTypeManagerImpl extends GenericManagerImpl<OrganizationWorkflowType, Long> implements OrganizationWorkflowTypeManager, OrganizationWorkflowTypeService {
    OrganizationWorkflowTypeDao organizationWorkflowTypeDao;

    @Autowired
    public OrganizationWorkflowTypeManagerImpl(OrganizationWorkflowTypeDao organizationWorkflowTypeDao) {
        super(organizationWorkflowTypeDao);
        this.organizationWorkflowTypeDao = organizationWorkflowTypeDao;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationWorkflowType getOrganizationWorkflowType(final String organizationWorkflowTypeId) {
        return organizationWorkflowTypeDao.get(new Long(organizationWorkflowTypeId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrganizationWorkflowType> getOrganizationWorkflowTypes() {
        return organizationWorkflowTypeDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<OrganizationWorkflowType> searchOrganizationWorkflowTypes(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
       
        return organizationWorkflowTypeDao.search(pageable, new String[]{"workflowType", "description"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidMDSRoleException 
     * @throws SearchException 
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> organizationWorkflowTypesSelect2(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws SearchException, InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);
        UserAccount user = UserUtils.getUser();
    	if (!user.isSystem()) {
    		searchable.addSearchFilter("organization.id", SearchOperator.in, UserUtils.getUserOrganizationIds(user.getUsername()));
    	}
    	if (!StringUtils.isBlank(searchTerm)) {
    		/*SearchFilter codeFilter = SearchFilterHelper.newCondition("workflowType", SearchOperator.contain, searchTerm);
            SearchFilter descriptionFilter = SearchFilterHelper.newCondition("description", SearchOperator.contain, searchTerm);
            SearchFilter and1 = SearchFilterHelper.or(codeFilter, descriptionFilter);
            searchable.addSearchFilter(and1);*/
            return toSelect2Data(organizationWorkflowTypeDao.search(searchable, new String[]{"workflowType", "description"}, searchTerm).getContent(), request);
    	}
    	
    	return toSelect2Data(organizationWorkflowTypeDao.find(searchable).getContent(), request);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> organizationWorkflowTypesTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.setPage(pageable);
        
        Page<OrganizationWorkflowType> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		searchable.addSort(Direction.DESC, "apptDate", "apptPeriod.timeFrom", "apptPeriod.apptItem.code");
    		list = organizationWorkflowTypeDao.find(searchable);
    	}else {
    		searchable.addSort(Direction.DESC, "apptDate", "timeRange", "apptCode");
    		list = organizationWorkflowTypeDao.search(searchable, searchTerm);
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
    public HashMap<String, Object> organizationWorkflowTypesTable(String organizationId, String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws SearchException, InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "organization.id", "workflowType");
        searchable.setPage(pageable);
        
    	long oId = StringUtils.toLong(organizationId);
    	if (oId != Long.MIN_VALUE && oId != 0) {
			searchable.addSearchFilter("organization.id", SearchOperator.eq, oId);
		}else {
			UserAccount user = UserUtils.getUser();
	    	if (!user.isSystem()) {
	    		searchable.addSearchFilter("organization.id", SearchOperator.in, UserUtils.getUserOrganizationIds(user.getUsername()));
	    	}
		}
    	
    	Page<OrganizationWorkflowType> page = null;
    	if (!StringUtils.isBlank(searchTerm)) {
    		/*SearchFilter codeFilter = SearchFilterHelper.newCondition("workflowType", SearchOperator.contain, searchTerm);
            SearchFilter descriptionFilter = SearchFilterHelper.newCondition("description", SearchOperator.contain, searchTerm);
            SearchFilter and1 = SearchFilterHelper.or(codeFilter, descriptionFilter);
            searchable.addSearchFilter(and1);*/
    		page = organizationWorkflowTypeDao.search(searchable, searchTerm);
    	}else {
    		page = organizationWorkflowTypeDao.find(searchable);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", page.getTotalElements());
		resultData.put("rows", toBootstrapTableData(page.getContent(), request));
		
		return resultData;
		
    }
    
    /**
	 * convert OrganizationWorkflowType data to Bootstrap Table Data format
	 * @param organizationWorkflowTypes
	 * @return
     * @throws Exception 
	 */
	private  List<HashMap<String,Object>> toBootstrapTableData(List<OrganizationWorkflowType> organizationWorkflowTypes, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (OrganizationWorkflowType u : organizationWorkflowTypes) {
			//Organization's Workflow Type list		
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("workflowType", I18nUtils.getWorkflowType(u.getWorkflowType(), request));//workflow type title
			mapData.put("organizationCode", u.getOrganizationCode());//organization Code
			mapData.put("id", u.getId());//role id
			mapData.put("createdBy", u.getCreatedBy());
			mapData.put("dateAdded", DateUtils.jsonSerializer(u.getDateAdded()));//create date
			mapData.put("description", u.getDescription());//
			list.add(mapData);
		}
				
		return list;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationWorkflowType saveOrganizationWorkflowType(final OrganizationWorkflowType organizationWorkflowType) throws RecordExistsException {

        try {
            return organizationWorkflowTypeDao.saveOrganizationWorkflowType(organizationWorkflowType);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("OrganizationWorkflowType type: '" + organizationWorkflowType.getWorkflowType() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationWorkflowType addOrganizationWorkflowType(final OrganizationWorkflowType organizationWorkflowType) throws RecordExistsException {

        try {
            return organizationWorkflowTypeDao.addOrganizationWorkflowType(organizationWorkflowType);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new RecordExistsException("OrganizationWorkflowType type: '" + organizationWorkflowType.getWorkflowType() + "' already exists!");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public OrganizationWorkflowType userAppointment(String mobile, String idNumber, OrganizationWorkflowType organizationWorkflowType) 
    		throws RecordExistsException {
		
		return saveOrganizationWorkflowType(organizationWorkflowType);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Response removeOrganizationWorkflowType(final String organizationWorkflowTypeIds) throws WebApplicationException{
        log.debug("removing organizationWorkflowType: " + organizationWorkflowTypeIds);
        try {
        	organizationWorkflowTypeDao.remove(ConvertUtil.StringtoLongArray(organizationWorkflowTypeIds));
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("Face Define(id=" + organizationWorkflowTypeIds + ") was successfully deleted.");
        return Response.ok().build();
    }
    
    /**
     * {@inheritDoc}
     */
/*    @Override
    public Response changeOrganizationWorkflowTypeStatus(String organizationWorkflowTypeId, String organizationWorkflowTypeStatus) {
    	try {
        	OrganizationWorkflowType organizationWorkflowType = organizationWorkflowTypeDao.get(new Long(organizationWorkflowTypeId));
        	if (organizationWorkflowType != null){
        		organizationWorkflowType.setOrganizationWorkflowTypeType(OrganizationWorkflowTypeType.valueOf(organizationWorkflowTypeStatus));
        		organizationWorkflowTypeDao.save(organizationWorkflowType);
        	}
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    	
    	return Response.ok().build();
    }*/
        
    /**
	 * convert organizationWorkflowType data to select2 format(https://select2.org/data-sources/formats)
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
	 * @param organizationWorkflowTypes
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<OrganizationWorkflowType> organizationWorkflowTypes, HttpServletRequest request){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (OrganizationWorkflowType u : organizationWorkflowTypes) {
			//organizationWorkflowType list
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", I18nUtils.getWorkflowType(u.getWorkflowType(), request));//organizationWorkflowType name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//organizationWorkflowType id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}
	
	@Override
    public Response getQRCode(String refNo){
		Searchable searchable = Searchable.newSearchable();
	    searchable.addSearchFilter("refNo", SearchOperator.eq, refNo);
	    OrganizationWorkflowType organizationWorkflowType = organizationWorkflowTypeDao.findOne(searchable);
        // uncomment line below to send non-streamed
		 //QRCode.from("Hello World").to(ImageType.PNG).writeTo(outputStream);

        //return Response.ok(QRCode.from(organizationWorkflowType.getRefNo()).withSize(250, 250).stream()).build();
        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
	    ByteArrayInputStream bas = null;
    	try {
	    	File file =QRCode.from(organizationWorkflowType.getWorkflowType().toString()).to(ImageType.PNG).withSize(250, 250).file();
	    	bas =new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    	} catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        
        return Response.ok(bas).build();
    }
}