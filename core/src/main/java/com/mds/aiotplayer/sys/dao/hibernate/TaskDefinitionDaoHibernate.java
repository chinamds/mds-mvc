package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.TaskDefinition;
import com.mds.aiotplayer.sys.dao.TaskDefinitionDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(taskDefinition);
        // necessary to throw a DataIntegrityViolation and catch it in TaskDefinitionManager
        getEntityManager().flush();
        return result;
    }
}
