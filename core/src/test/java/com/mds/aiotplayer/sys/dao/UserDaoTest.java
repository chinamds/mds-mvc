/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao;

import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import com.mds.aiotplayer.common.model.Address;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.util.UserUtils;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class UserDaoTest extends BaseDaoTestCase {
    @Autowired
    private UserDao dao;
    @Autowired
    private RoleDao rdao;

    @Test(expected=DataAccessException.class)
    public void testGetUserInvalid() throws Exception {
        // should throw DataAccessException
        dao.get(1000L);
    }

    @Test
    public void testGetUser() throws Exception {
        User user = dao.get(-1L);

        assertNotNull(user);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.isEnabled());
    }

    @Test
    public void testGetUserPassword() throws Exception {
        User user = dao.get(-1L);
        String password = dao.getUserPassword(user.getId());
        assertNotNull(password);
        log.debug("password: " + password);
    }

    @Test(expected=DataIntegrityViolationException.class)
    public void testUpdateUser() throws Exception {
        User user = dao.get(-1L);

        /*Address address = user.getAddress();
        address.setAddress("new address");

        dao.saveUser(user);
        flush();

        user = dao.get(-1L);
        assertEquals(address, user.getAddress());
        assertEquals("new address", user.getAddress().getAddress());*/

        // verify that violation occurs when adding new user with same username
        User user2 = new User();
        //user2.setAddress(user.getAddress());
        user2.setConfirmPassword(user.getConfirmPassword());
        user2.setEmail(user.getEmail());
        user2.setFirstName(user.getFirstName());
        user2.setLastName(user.getLastName());
        user2.setPassword(user.getPassword());
        user2.setPasswordHint(user.getPasswordHint());
        //user2.setTenant(UserUtils.getTenant("212aab68-7fb3-11e9-bc42-526af7764f64"));
        //user2.setRoles(user.getRoles());
        Set<Role> roles = new HashSet<Role>(user.getRoles());
        user2.setRoles(roles);
        user2.setUsername(user.getUsername());
        user2.setWebsite(user.getWebsite());

        // should throw DataIntegrityViolationException
        dao.saveUser(user2);
    }

    @Test
    public void testAddUserRole() throws Exception {
        User user = dao.get(-1L);
        assertEquals(1, user.getRoles().size());

        Role role = rdao.getRoleByName(Constants.ADMIN_ROLE);
        user.addRole(role);
        dao.saveUser(user);
        flush();

        user = dao.get(-1L);
        assertEquals(2, user.getRoles().size());

        //add the same role twice - should result in no additional role
        user.addRole(role);
        dao.saveUser(user);
        flush();

        user = dao.get(-1L);
        assertEquals("more than 2 roles", 2, user.getRoles().size());

        user.getRoles().remove(role);
        dao.saveUser(user);
        flush();

        user = dao.get(-1L);
        assertEquals(1, user.getRoles().size());
    }

    @Test(expected=DataAccessException.class)
    public void testAddAndRemoveUser() throws Exception {
        User user = new User("testuser");
        user.setPassword("testpass");
        user.setFirstName("Test");
        user.setLastName("Last");
        //user.setTenant(UserUtils.getTenant("212aab68-7fb3-11e9-bc42-526af7764f64"));
        /*Address address = new Address();
        address.setCity("Denver");
        address.setProvince("CO");
        address.setCountry("USA");
        address.setPostalCode("80210");
        user.setAddress(address);*/
        user.setEmail("testuser@appfuse.org");
        user.setWebsite("http://raibledesigns.com");

        Role role = rdao.getRoleByName(Constants.USER_ROLE);
        assertNotNull(role.getId());
        user.addRole(role);

        user = dao.saveUser(user);
        flush();

        assertNotNull(user.getId());
        user = dao.get(user.getId());
        assertEquals("testpass", user.getPassword());

        dao.remove(user);
        flush();

        // should throw DataAccessException
        dao.get(user.getId());
    }

    @Test
    public void testUserExists() throws Exception {
        boolean b = dao.exists(-1L);
        assertTrue(b);
    }

    @Test
    public void testUserNotExists() throws Exception {
        boolean b = dao.exists(111L);
        assertFalse(b);
    }

    @Test
    public void testUserSearch() throws Exception {
        // reindex all the data
        dao.reindex();

        List<User> found = dao.search("John");
        assertEquals(1, found.size());
        User user = found.get(0);
        assertEquals("John", user.getFirstName());

        // test mirroring
        user = dao.get(-2L);
        user.setFirstName("JohnX");
        dao.saveUser(user);
        flush();
        flushSearchIndexes();

        // now verify it is reflected in the index
        found = dao.search("JohnX");
        assertEquals(1, found.size());
        user = found.get(0);
        assertEquals("JohnX", user.getFirstName());
    }
}
