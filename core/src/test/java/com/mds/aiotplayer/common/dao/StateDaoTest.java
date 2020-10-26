/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.common.model.State;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StateDaoTest extends BaseDaoTestCase {
    @Autowired
    private StateDao stateDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveState() {
        State state = new State();

        // enter all required fields
        state.setStateCode("MlQbGcLxFaHkDgRgNqQxMnWqJhBfSrHyIpKhSwJbNgFoGtNrLrLeAlAvBoSfLgGqZmAwPlOdIrCtGwLhLoGwPvVdDuAyWuFcXvKd");

        log.debug("adding state...");
        state = stateDao.save(state);

        state = stateDao.get(state.getId());

        assertNotNull(state.getId());

        log.debug("removing state...");

        stateDao.remove(state.getId());

        // should throw DataAccessException 
        stateDao.get(state.getId());
    }
}