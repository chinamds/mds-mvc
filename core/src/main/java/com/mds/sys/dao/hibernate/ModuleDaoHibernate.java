package com.mds.sys.dao.hibernate;

import com.mds.sys.model.Module;
import com.mds.sys.dao.ModuleDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("moduleDao")
public class ModuleDaoHibernate extends GenericDaoHibernate<Module, Long> implements ModuleDao {

    public ModuleDaoHibernate() {
        super(Module.class);
    }

	/**
     * {@inheritDoc}
     */
    public Module saveModule(Module module) {
        if (log.isDebugEnabled()) {
            log.debug("module's id: " + module.getId());
        }
        getSession().saveOrUpdate(module);
        // necessary to throw a DataIntegrityViolation and catch it in ModuleManager
        getSession().flush();
        return module;
    }
}
