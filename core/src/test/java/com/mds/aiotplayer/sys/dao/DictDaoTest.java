package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.sys.model.Dict;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DictDaoTest extends BaseDaoTestCase {
    @Autowired
    private DictDao dictDao;

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveDict() {
        Dict dict = new Dict();

        // enter all required fields

        log.debug("adding dict...");
        dict = dictDao.save(dict);

        dict = dictDao.get(dict.getId());

        assertNotNull(dict.getId());

        log.debug("removing dict...");

        dictDao.remove(dict.getId());

        // should throw DataAccessException 
        dictDao.get(dict.getId());
    }
}