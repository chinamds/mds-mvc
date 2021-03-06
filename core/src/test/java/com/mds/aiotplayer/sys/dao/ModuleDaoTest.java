/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.Module;
import com.mds.aiotplayer.sys.model.ModuleType;

import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ModuleDaoTest extends BaseDaoTestCase {
    @Autowired
    private ModuleDao moduleDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveModule() {
        Module module = new Module();

        // enter all required fields
        module.setModuleName("OiHtVyQiBfSyMzYtXoYcVyAwFnCyWpQhCxRjTjJwRlViIvNyIv");
        module.setModuleType(ModuleType.opt);
        module.setEnabled(true);

        log.debug("adding module...");
        module = moduleDao.save(module);

        module = moduleDao.get(module.getId());

        assertNotNull(module.getId());

        log.debug("removing module...");

        moduleDao.remove(module.getId());

        // should throw DataAccessException 
        moduleDao.get(module.getId());
    }
}