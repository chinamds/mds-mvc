package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.exception.RoleExistsException;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;

import java.util.List;

import javax.ws.rs.PathParam;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * Business Service Interface to handle communication between web and
 * persistence layer.
 *
 * @author <a href="mailto:dan@getrolling.com">Dan Kibler </a>
 */
public interface RoleManager extends GenericManager<Role, Long> {
	
	/**
     * Retrieves a list of all roles.
     *
     * @return List
     */
	//@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<Role> getRoles();
    
    /**
     * Retrieves a list of roles with orgization id.
     *
     * @return List
     */
    List<Role> getRoles(long oId, boolean includeChildOrginaztion);
    
    /**
     * {@inheritDoc}
     */
	//@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    void removeRole( Role role);
    
    /**
     * Finds a role by their rolename.
     *
     * @param rolename the role's rolename used to login
     * @return Role a populated role object
     */
    Role getRoleByRolename(String rolename);
    List<Role> getRolesByRoleType(RoleType roleType, long oId);
    
    boolean roleExists(String rolename);
    
    boolean roleExists(String rolename, long oId);
    
    boolean roleTypeExists(RoleType roleType, long oId);

	/**
     * Saves a role's information
     *
     * @param role the role's information
     * @return updated role
     * @throws RoleExistsException thrown when role already exists
     */
    //@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    Role saveRole(Role role) throws RoleExistsException;
    Role save(final Role role);
    
    //@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeRole(String roleIds);
    
    /**
     * {@inheritDoc}
     */
    List getRoles(Role role);

    /**
     * get a role by rolename.
     *
     * @param rolename the role's rolename
     * @return Role a populated role object
     */
    Role getRole(String rolename);
    
    /**
     * get a role by rolename and owner organization id.
     *
     * @param rolename the role's rolename
     * @param oId the owner organization id
     * @return Role a populated role object
     */
    Role getRole(String rolename, long oId);
    
    List<Role> findSARoleNotOwnerAlbum(Long albumId);
}
