package com.mds.i18n.service;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import com.mds.common.service.BaseManagerTestCase;
import com.mds.i18n.model.Culture;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.*;

@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class CultureCacheTest extends BaseManagerTestCase {
    private Logger log = LoggerFactory.getLogger(CultureCacheTest.class);
    @Autowired
    private CultureManager mgr;
        
    @Test
    public void testUserCache() throws Exception {
    	log.debug("get cultures step one");
    	List<Culture> found = mgr.getCultures();
    	log.debug("cultures found: " + found.size());
    	log.debug("get cultures step two");
    	List<Culture> found1 = mgr.getCultures();
    	log.debug("cultures found: " + found1.size());
    	/*User user = mgr.getUserByUsername("john");
        user.setWebsite("www.mmdsplus.com");
        user = mgr.saveUser(user);
        
        log.debug("saving culture with updated website: " + user);
        found = mgr.getCultures();
    	log.debug("cultures found: " + found.size());*/
    	
        // don't assume exact number so tests can run in parallel
        assertFalse(found1.isEmpty());
    }
}
