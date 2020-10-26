/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mds.aiotplayer.sys.dao.TaskDefinitionDao;
import com.mds.aiotplayer.sys.model.TaskDefinition;
import com.mds.aiotplayer.common.service.impl.BaseManagerMockTestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

public class TaskDefinitionManagerImplTest extends BaseManagerMockTestCase {

    @InjectMocks
    private TaskDefinitionManagerImpl manager;

    @Mock
    private TaskDefinitionDao dao;

    @Test
    public void testGetTaskDefinition() {
        log.debug("testing get...");
        //given
        final Long id = 7L;
        final TaskDefinition taskDefinition = new TaskDefinition();
        given(dao.get(id)).willReturn(taskDefinition);

        //when
        TaskDefinition result = manager.get(id);

        //then
        assertSame(taskDefinition, result);
    }

    @Test
    public void testGetTaskDefinitions() {
        log.debug("testing getAll...");
        //given
        final List<TaskDefinition> taskDefinitions = new ArrayList<>();
        given(dao.getAll()).willReturn(taskDefinitions);

        //when
        List result = manager.getAll();

        //then
        assertSame(taskDefinitions, result);
    }

    @Test
    public void testSaveTaskDefinition() {
        log.debug("testing save...");

        //given
        final TaskDefinition taskDefinition = new TaskDefinition();
        // enter all required fields
        taskDefinition.setName("IaRaByBwMcRyRfRgTxVxJsFgYzNvMuQyViBlEoNnItDdCzOqZuElPoPoElEgYrUcWrBhItHjYyMlPpFmJuLaJwFrFmRqDmVwHwEv");

        given(dao.save(taskDefinition)).willReturn(taskDefinition);

        //when
        manager.save(taskDefinition);

        //then
        verify(dao).save(taskDefinition);
    }

    @Test
    public void testRemoveTaskDefinition() {
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
