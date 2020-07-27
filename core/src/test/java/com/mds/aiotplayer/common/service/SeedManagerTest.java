package com.mds.aiotplayer.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.*;

@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class SeedManagerTest extends BaseManagerTestCase {
	 @Autowired
	 SeedManager seedManager;

	 /*@Before
     public void before() throws Exception {
		 seedManager.removeAll();
     }*/	 
	 	 
     @Test
     public void testSeed() throws Exception {
    	//sessionFactory.getCurrentSession().beginTransaction();
    	//UserUtils.putCache(UserUtils.CACHE_USER, userManager.getUserByUsername("admin"));
    	//seedManager.removeAll();
        //seedManager.insertSeedData();
        //log.debug("Users found: " + found.size());
        // don't assume exact number so tests can run in parallel
        //assertFalse(found.isEmpty());
     }
}
