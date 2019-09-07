package com.mds.sys.dao;

import com.mds.common.dao.GenericDao;
import com.mds.sys.exception.UserNotExistsException;
import com.mds.sys.model.User;
import com.mds.sys.util.UserAccount;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * User Data Access Object (GenericDao) interface.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public interface UserDao extends GenericDao<User, Long> {

    /**
     * Gets users information based on login name.
     * @param username the user's username
     * @return userDetails populated userDetails object
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException thrown when user not
     * found in database
     */
    @Transactional
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    
    @Transactional
    User getUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Gets a list of users ordered by the uppercase version of their username.
     *
     * @return List populated list of users
     */
    List<User> getUsers();

    /**
     * Saves a user's information.
     * @param user the object to be saved
     * @return the persisted User object
     */
    User saveUser(User user);

    /**
     * Retrieves the password in DB for a user
     * @param userId the user's id
     * @return the password in DB, if the user is already persisted
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getUserPassword(Long userId);
    
    List<User> findAllList() ;
    
    List<User> findByCompanyId(Long companyId);
    List<User> findByRoleIds(List<Long> roleIds);
	
	User findByLoginName(String loginName);

	int updatePasswordById(String newPassword, Long id);
	
	int updateLoginInfo(String loginIp, Date loginDate, Long id);
}
