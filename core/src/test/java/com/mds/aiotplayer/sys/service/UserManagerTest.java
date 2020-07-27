package com.mds.aiotplayer.sys.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.common.service.BaseManagerTestCase;
import com.mds.aiotplayer.sys.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class UserManagerTest extends BaseManagerTestCase {
	private final Logger log = LoggerFactory.getLogger(UserManagerTest.class);
    @Autowired
    private UserManager mgr;
    @Autowired
    private RoleManager roleManager;

    @Before
    public void before() throws Exception {
        User user = new User();

        // call populate method in super class to populate test data
        // from a properties file matching this class name
        user = (User) populate(user);

        user.addRole(roleManager.getRoleByRolename(Constants.USER_ROLE));

        user = mgr.saveUser(user);
        assertEquals("john", user.getUsername());
        assertEquals(1, user.getRoles().size());
    }

    @After
    public void after() {
    	LocaleContextHolder.setLocale(new Locale("nl")); 
        User user = mgr.getUserByUsername("john");
        mgr.removeUser(user.getId().toString());

        try {
            mgr.getUserByUsername("john");
            fail("Expected 'Exception' not thrown");
        } catch (Exception e) {
            log.debug(e.getMessage());
            assertNotNull(e);
        }
    }

    @Test
    public void testGetUser() throws Exception {
        User user = mgr.getUserByUsername("john");
        assertNotNull(user);
        //assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testSaveUser() throws Exception {
        User user = mgr.getUserByUsername("john");
        user.setPhoneNumber("303-555-1212");

        log.debug("saving user with updated phone number: " + user);

        user = mgr.saveUser(user);
        assertEquals("303-555-1212", user.getPhoneNumber());
        //assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testGetAll() throws Exception {
        List<User> found = mgr.getAll();
        log.debug("Users found: " + found.size());
        // don't assume exact number so tests can run in parallel
        assertFalse(found.isEmpty());
    }
    
    @Test
    public void testUserCache() throws Exception {
    	log.debug("get users step one");
    	List<User> found = mgr.getUsers();
    	log.debug("Users found: " + found.size());
    	log.debug("get users step two");
    	List<User> found1 = mgr.getUsers();
    	log.debug("Users found: " + found1.size());
    	User user = mgr.getUserByUsername("john");
        user.setWebsite("www.mmdsplus.com");
        user = mgr.saveUser(user);
        
        log.debug("saving user with updated website: " + user);
        found = mgr.getUsers();
    	log.debug("Users found: " + found.size());
    	
        // don't assume exact number so tests can run in parallel
        assertFalse(found1.isEmpty());
    }
}
