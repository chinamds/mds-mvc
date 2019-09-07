package com.mds.sys.dao.hibernate;

import com.mds.sys.model.TaskDefinition;
import com.mds.sys.dao.TaskDefinitionDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("taskDefinitionDao")
public class TaskDefinitionDaoHibernate extends GenericDaoHibernate<TaskDefinition, Long> implements TaskDefinitionDao {

    public TaskDefinitionDaoHibernate() {
        super(TaskDefinition.class);
    }

	/**
     * {@inheritDoc}
     */
    public TaskDefinition saveTaskDefinition(TaskDefinition taskDefinition) {
        if (log.isDebugEnabled()) {
            log.debug("taskDefinition's id: " + taskDefinition.getId());
        }
        getSession().saveOrUpdate(taskDefinition);
        // necessary to throw a DataIntegrityViolation and catch it in TaskDefinitionManager
        getSession().flush();
        return taskDefinition;
    }
}
