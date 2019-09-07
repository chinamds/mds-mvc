package com.mds.sys.dao;

import java.util.List;

import com.mds.common.dao.GenericDao;
import com.mds.sys.model.Role;

/**
 * Role Data Access Object (DAO) interface.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public interface RoleDao extends GenericDao<Role, Long> {
    /**
     * Gets role information based on rolename
     * @param rolename the rolename
     * @return populated role object
     */
    Role getRoleByName(String rolename);
    
    boolean roleExists(String rolename);

    /**
     * Removes a role from the database by name
     * @param rolename the role's rolename
     */
    void removeRole(String rolename);

	/**
     * Saves a role's information.
     * @param role the object to be saved
     * @return the persisted Role object
     */
    Role saveRole(Role role);
    
    List<Role> findSARoleNotOwnerAlbum(Long albumId);
}
