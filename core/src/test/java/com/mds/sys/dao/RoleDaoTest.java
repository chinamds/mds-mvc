package com.mds.sys.dao;

import com.mds.common.Constants;
import com.mds.common.dao.BaseDaoTestCase;
import com.mds.sys.model.Role;
import com.mds.sys.model.RoleType;
import com.mds.sys.util.UserUtils;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class RoleDaoTest extends BaseDaoTestCase {
    @Autowired
    private RoleDao dao;

    @Test
    public void testGetRoleInvalid() throws Exception {
        Role role = dao.getRoleByName("badrolename");
        assertNull(role);
    }

    @Test
    public void testGetRole() throws Exception {
        Role role = dao.getRoleByName(Constants.USER_ROLE);
        assertNotNull(role);
    }

    @Test
    public void testUpdateRole() throws Exception {
        Role role = dao.getRoleByName("ROLE_USER");
        role.setDescription("test descr");
        role.setType(RoleType.sa);
        //role.setTenant(UserUtils.getTenant("212aab68-7fb3-11e9-bc42-526af7764f64"));
        dao.save(role);
        flush();

        role = dao.getRoleByName("ROLE_USER");
        assertEquals("test descr", role.getDescription());
    }

    @Test
    public void testAddAndRemoveRole() throws Exception {
        Role role = new Role("testrole");
        role.setDescription("new role descr");
        role.setType(RoleType.sa);
        //role.setTenant(UserUtils.getTenant("212aab68-7fb3-11e9-bc42-526af7764f64"));
        dao.save(role);
        flush();

        role = dao.getRoleByName("testrole");
        assertNotNull(role.getDescription());

        dao.removeRole("testrole");
        flush();

        role = dao.getRoleByName("testrole");
        assertNull(role);
    }

    @Test
    public void testFindByNamedQuery() {
        HashMap<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("name", Constants.USER_ROLE);
        List<Role> roles = dao.findByNamedQuery("findRoleByName", queryParams);
        assertNotNull(roles);
        assertTrue(roles.size() > 0);
    }
}
