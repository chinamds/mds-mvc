package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.dao.UserDao;
import com.mds.aiotplayer.sys.exception.UserNotExistsException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.model.UserStatus;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.Table;
import javax.sql.DataSource;

import java.util.Date;
import java.util.List;

/**
 * This class interacts with Hibernate session to save/delete and
 * retrieve User objects.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *         Modified by <a href="mailto:dan@getrolling.com">Dan Kibler</a>
 *         Extended to implement Acegi UserDetailsService interface by David Carter david@carter.net
 *         Modified by <a href="mailto:bwnoll@gmail.com">Bryan Noll</a> to work with
 *         the new BaseDaoHibernate implementation that uses generics.
 *         Modified by jgarcia (updated to hibernate 4)
 */
@Repository("userDao")
public class UserDaoHibernate extends GenericDaoHibernate<User, Long> implements UserDao, UserDetailsService {
	@Autowired
    private DataSource dataSource;

    /**
     * Constructor that sets the entity to User.class.
     */
    public UserDaoHibernate() {
        super(User.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<User> getUsers() {
    	Query q = getEntityManager().createQuery("from User u order by upper(u.username)");
        
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    public User saveUser(User user) {
        if (log.isDebugEnabled()) {
            log.debug("user's id: " + user.getId());
        }
        var result = super.save(preSave(user));
        // necessary to throw a DataIntegrityViolation and catch it in UserManager
        getEntityManager().flush();
        return result;
    }

    /**
     * Overridden simply to call the saveUser method. This is happening
     * because saveUser flushes the session and saveObject of BaseDaoHibernate
     * does not.
     *
     * @param user the user to save
     * @return the modified user (with a primary key set if they're new)
     */
    @Override
    public User save(User user) {
        return this.saveUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	Query q = getEntityManager().createQuery("select u from User u where username=?1");
        q.setParameter(1, username);
        
        List users = q.getResultList();
        User user = null;
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("user '" + username + "' not found..."); //"user '" + username + "' not found..."
        } else {
            user = (User)users.get(0);
        }
        
        return UserUtils.toUserAccount(user);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByUsername(String username) throws UsernameNotFoundException {
    	Query q = getEntityManager().createQuery("select u from User u where username=?1");
        q.setParameter(1, username);
        
        List users = q.getResultList();
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("user '" + username + "' not found...");
        } else {
            return (User) users.get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getUserPassword(Long userId) {
        JdbcTemplate jdbcTemplate =
                new JdbcTemplate(dataSource);
        Table table = AnnotationUtils.findAnnotation(User.class, Table.class);
        return jdbcTemplate.queryForObject(
                "select password from " + table.name() + " where id=?", String.class, userId);
    }
    
    public List<User> findAllList() {
		//return find("from User where delFlag=:p1 order by id", new Parameter(User.DEL_FLAG_NORMAL));
    	return getAll();
	}
    
    public List<User> findByCompanyId(Long companyId){
		return find("select distinct u from User u, Staff s, Company c where u in elements (s.users) and s in elements (c.staffs)" +
				" and c.id=:p1" + " order by u.username", new Parameter(companyId));
	}
    
    public List<User> findByRoleIds(List<Long> roleIds){
		return find("select distinct u from User u where not exists (from u.roles r where r.id not in (:p1))", new Parameter(roleIds));
	}
	
	public User findByLoginName(String loginName){
		//return getByHql("from User where loginName = :p1 and delFlag = :p2", new Parameter(loginName, User.DEL_FLAG_NORMAL));
		return getByHql("from User u where u.username = :p1", new Parameter(loginName));
	}

	public int updatePasswordById(String newPassword, Long id){
		return update("update User u set u.password=:p1 where u.id = :p2", new Parameter(newPassword, id));
	}
	
	public int updateLoginInfo(String loginIp, Date loginDate, Long id){
		return update("update User u set u.loginIp=:p1, u.loginDate=:p2 where u.id = :p3", new Parameter(loginIp, loginDate, id));
	}
}
