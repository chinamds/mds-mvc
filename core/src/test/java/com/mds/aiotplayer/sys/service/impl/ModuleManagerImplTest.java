package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.ModuleDao;
import com.mds.aiotplayer.sys.model.Module;
import com.mds.aiotplayer.sys.model.ModuleType;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class ModuleManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private ModuleManagerImpl manager;

    @Mock
    private ModuleDao dao;

    @Test
    public void testGetModule() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final Module module = new Module();
        given(dao.get(id)).willReturn(module);

        //when
        Module result = manager.get(id);

        //then
        assertSame(module, result);
    }

    @Test
    public void testGetModules() {
        log.debug("testing getAll...");
        //given
        final List<Module> modules = new ArrayList<>();
        given(dao.getAll()).willReturn(modules);

        //when
        List result = manager.getAll();

        //then
        assertSame(modules, result);
    }

    @Test
    public void testSaveModule() {
        log.debug("testing save...");

        //given
        final Module module = new Module();
        // enter all required fields
        module.setModuleName("NiQtHbPpQjAiSlBhFdZvAmZrNgRlJaQeTjYcZrVuJxCdVoNiSy");
        module.setModuleType(ModuleType.sys);
        module.setEnabled(true);

        given(dao.save(module)).willReturn(module);

        //when
        manager.save(module);

        //then
        verify(dao).save(module);
    }

    @Test
    public void testRemoveModule() {
        log.debug("testing remove...");

        //given
        final Long id = -11L;
        willDoNothing().given(dao).remove(id);

        //when
        manager.remove(id);

        //then
        verify(dao).remove(id);
    }
}
