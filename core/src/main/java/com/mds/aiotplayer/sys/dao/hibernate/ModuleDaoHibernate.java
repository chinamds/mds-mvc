package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.Module;
import com.mds.aiotplayer.sys.dao.ModuleDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(module);
        // necessary to throw a DataIntegrityViolation and catch it in ModuleManager
        getEntityManager().flush();
        return result;
    }
}
