/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.ModuleDao;
import com.mds.aiotplayer.sys.model.Module;
import com.mds.aiotplayer.sys.service.ModuleManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("ModuleManager")
@WebService(serviceName = "ModuleService", endpointInterface = "com.mds.aiotplayer.service.ModuleManager")
public class ModuleManagerImpl extends GenericManagerImpl<Module, Long> implements ModuleManager {
    ModuleDao moduleDao;

    @Autowired
    public ModuleManagerImpl(ModuleDao moduleDao) {
        super(moduleDao);
        this.moduleDao = moduleDao;
    }
}