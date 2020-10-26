/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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
