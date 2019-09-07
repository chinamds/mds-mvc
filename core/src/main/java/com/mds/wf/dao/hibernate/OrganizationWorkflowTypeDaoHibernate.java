package com.mds.wf.dao.hibernate;

import com.mds.wf.model.OrganizationWorkflowType;
import com.mds.wf.dao.OrganizationWorkflowTypeDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("organizationWorkflowTypeDao")
public class OrganizationWorkflowTypeDaoHibernate extends GenericDaoHibernate<OrganizationWorkflowType, Long> implements OrganizationWorkflowTypeDao {

    public OrganizationWorkflowTypeDaoHibernate() {
        super(OrganizationWorkflowType.class);
    }

	/**
     * {@inheritDoc}
     */
    public OrganizationWorkflowType saveOrganizationWorkflowType(OrganizationWorkflowType organizationWorkflowType) {
        if (log.isDebugEnabled()) {
            log.debug("organizationWorkflowType's id: " + organizationWorkflowType.getId());
        }
        getSession().saveOrUpdate(preSave(organizationWorkflowType));
        // necessary to throw a DataIntegrityViolation and catch it in OrganizationWorkflowTypeManager
        getSession().flush();
        return organizationWorkflowType;
    }
    
    /**
     * {@inheritDoc}
     */
    public OrganizationWorkflowType addOrganizationWorkflowType(OrganizationWorkflowType organizationWorkflowType) {
        getSession().save(preSave(organizationWorkflowType));
        // necessary to throw a DataIntegrityViolation and catch it in OrganizationWorkflowTypeManager
        getSession().flush();
        return organizationWorkflowType;
    }
    
    /**
     * Overridden simply to call the saveOrganizationWorkflowType method. This is happening
     * because saveOrganizationWorkflowType flushes the session and saveObject of BaseDaoHibernate
     * does not.
     *
     * @param organizationWorkflowType the organizationWorkflowType to save
     * @return the modified organizationWorkflowType (with a primary key set if they're new)
     */
    @Override
    public OrganizationWorkflowType save(OrganizationWorkflowType organizationWorkflowType) {
        return this.saveOrganizationWorkflowType(organizationWorkflowType);
    } 
    
    /*public String getMaxRefNo(final String appointmentItem){
    	Pageable pageable = PageRequest.of(0, 1);
    	Page<String> refs = find(pageable, "select refNo from OrganizationWorkflowType where apptDatePeriod.apptItemRange.code=:p1 order by refNo desc", new Parameter(appointmentItem));
    	if (refs.hasContent()) {
    		return refs.getContent().get(0);
    	}
    	
    	return null;
	}
    
    public List<Map<String,Object>> getItemRangeAppointment(final List<Long> apptItemRangeIds){
    	List<Map<String,Object>> result = find("select new map(apptDatePeriod.apptItemRange.id as apptItemRangeId, count(*) as appointed) from OrganizationWorkflowType where apptDatePeriod.apptItemRange.id in :p1 group by apptDatePeriod.apptItemRange.id", new Parameter(apptItemRangeIds));
    	
    	return result;
    }*/
}
