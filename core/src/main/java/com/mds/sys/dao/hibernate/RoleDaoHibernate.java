package com.mds.sys.dao.hibernate;

import com.mds.sys.dao.RoleDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import com.mds.common.model.Parameter;
import com.mds.sys.model.Role;
import com.mds.sys.model.RoleType;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * This class interacts with hibernate session to save/delete and
 * retrieve Role objects.
 *
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 * @author jgarcia (updated to hibernate 4)
 */
@Repository("roleDao")
public class RoleDaoHibernate extends GenericDaoHibernate<Role, Long> implements RoleDao {

    /**
     * Constructor to create a Generics-based version using Role as the entity
     */
    public RoleDaoHibernate() {
        super(Role.class);
    }

    /**
     * {@inheritDoc}
     */
    public Role getRoleByName(String rolename) {
        List roles = getSession().createCriteria(Role.class).add(Restrictions.eq("name", rolename)).list();
        if (roles.isEmpty()) {
            return null;
        } else {
            return (Role) roles.get(0);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean roleExists(String rolename) {
    	List roles = getSession().createCriteria(Role.class).add(Restrictions.eq("name", rolename)).list();
    	
    	return (!roles.isEmpty());
    }

    /**
     * {@inheritDoc}
     */
    public void removeRole(String rolename) {
        Object role = getRoleByName(rolename);
        Session session = getSessionFactory().getCurrentSession();
        session.delete(role);
    }

	/**
     * {@inheritDoc}
     */
    public Role saveRole(Role role) {
        if (log.isDebugEnabled()) {
            log.debug("role's id: " + role.getId());
        }
        getSession().saveOrUpdate(preSave(role));
        // necessary to throw a DataIntegrityViolation and catch it in RoleManager
        getSession().flush();
        return role;
    }
    /**
     * Overridden simply to call the saveRole method. This is happening
     * because saveRole flushes the session and saveObject of BaseDaoHibernate
     * does not.
     *
     * @param role the role to save
     * @return the modified role (with a primary key set if they're new)
     */
    @Override
    public Role save(Role role) {
        return this.saveRole(role);
    }
    
    public List<Role> findSARoleNotOwnerAlbum(Long albumId){
		return find("select distinct r from Role r where r.type=:p2 and not exists(from Album a where a.id=:p1 and a not in elements (r.albums))"
    			, new Parameter(albumId, RoleType.sa));
	}
}
