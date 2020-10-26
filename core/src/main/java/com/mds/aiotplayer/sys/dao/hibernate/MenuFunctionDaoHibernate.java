/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.sys.model.Dict;
import com.mds.aiotplayer.sys.model.MenuFunction;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.sys.dao.MenuFunctionDao;
import com.mds.aiotplayer.sys.exception.MenuFunctionNotExistsException;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository("menuFunctionDao")
public class MenuFunctionDaoHibernate extends GenericDaoHibernate<MenuFunction, Long> implements MenuFunctionDao {

    public MenuFunctionDaoHibernate() {
        super(MenuFunction.class);
    }
    
    public List<MenuFunction> findAllActivitiList() {
		//return find("from MenuFunction where delFlag=:p1 and isActiviti = :p2 order by sort", new Parameter(Dict.DEL_FLAG_NORMAL, MenuFunction.YES));
    	return find("from MenuFunction where isActiviti = :p2 order by sort", new Parameter(Constants.YES));
	}
	
	public List<MenuFunction> findByParentIdsLike(String parentIds){
		return find("from MenuFunction where parentIds like :p1", new Parameter(parentIds));
	}

	public List<MenuFunction> findAllList(){
		//return find("from MenuFunction where delFlag=:p1 order by sort", new Parameter(Dict.DEL_FLAG_NORMAL));
		//return getAll();
		return find("from MenuFunction order by sort");
	}
	
	public List<MenuFunction> findByUserId(Long userId){
		/*return find("select distinct m from MenuFunction m, Role r, User u where m in elements (r.menuFunctionList) and r in elements (u.roleList)" +
				" and m.delFlag=:p1 and r.delFlag=:p1 and u.delFlag=:p1 and u.id=:p2" + // or (m.user.id=:p2  and m.delFlag=:p1)" + 
				" order by m.sort", new Parameter(Constants.DEL_FLAG_NORMAL, userId));*/
		return find("select distinct m from MenuFunction m, Role r, User u where m in elements (r.menuFunctions) and r in elements (u.roles)" +
				" and u.id=:p1" + // or (m.user.id=:p2  and m.delFlag=:p1)" + 
				" order by m.sort", new Parameter(userId));
	}
	
	public List<MenuFunction> findAllActivitiList(Long userId) {
		/*return find("select distinct m from MenuFunction m, Role r, User u where m in elements (r.menuFunctionList) and r in elements (u.roleList)" +
				" and m.delFlag=:p1 and r.delFlag=:p1 and u.delFlag=:p1 and m.isActiviti=:p2 and u.id=:p3 order by m.sort", 
				new Parameter(Constants.DEL_FLAG_NORMAL, Constants.YES,userId));*/
		return find("select distinct m from MenuFunction m, Role r, User u where m in elements (r.menuFunctionList) and r in elements (u.roleList)" +
				" and m.isActiviti=:p2 and u.id=:p3 order by m.sort", 
				new Parameter(Constants.YES,userId));
	}

	/**
     * {@inheritDoc}
     */
    public MenuFunction loadMenuFunctionByMenuFunctioncode(String menuFunctioncode) {
    	Query q = getEntityManager().createQuery("select m from MenuFunction m where code=?1");
        q.setParameter(1, menuFunctioncode);
        List menuFunctions = q.getResultList();
        if (menuFunctions == null || menuFunctions.isEmpty()) {
            //return null;
        	throw new MenuFunctionNotExistsException("menuFunction '" + menuFunctioncode + "' not found...");
        }
        
        return (MenuFunction)menuFunctions.get(0);
    }

	/**
     * {@inheritDoc}
     */
    public MenuFunction saveMenuFunction(MenuFunction menuFunction) {
        if (log.isDebugEnabled()) {
            log.debug("menuFunction's id: " + menuFunction.getId());
        }
        var result = super.save(menuFunction);
        // necessary to throw a DataIntegrityViolation and catch it in MenuFunctionManager
        getEntityManager().flush();
        return result;
    }
}
