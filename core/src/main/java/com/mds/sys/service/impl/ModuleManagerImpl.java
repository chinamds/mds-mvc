package com.mds.sys.service.impl;

import com.mds.sys.dao.ModuleDao;
import com.mds.sys.model.Module;
import com.mds.sys.service.ModuleManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("ModuleManager")
@WebService(serviceName = "ModuleService", endpointInterface = "com.mds.service.ModuleManager")
public class ModuleManagerImpl extends GenericManagerImpl<Module, Long> implements ModuleManager {
    ModuleDao moduleDao;

    @Autowired
    public ModuleManagerImpl(ModuleDao moduleDao) {
        super(moduleDao);
        this.moduleDao = moduleDao;
    }
}