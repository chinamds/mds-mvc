package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.Module;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface ModuleManager extends GenericManager<Module, Long> {
    
}