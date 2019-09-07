package com.mds.sys.dao.hibernate;

import com.mds.common.model.Parameter;
import com.mds.sys.model.Area;
import com.mds.common.Constants;
import com.mds.sys.dao.AreaDao;
import com.mds.sys.exception.AreaNotExistsException;
import com.mds.common.dao.hibernate.GenericDaoHibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository("areaDao")
public class AreaDaoHibernate extends GenericDaoHibernate<Area, Long> implements AreaDao {

    public AreaDaoHibernate() {
        super(Area.class);
    }
    
    public List<Area> findByParentIdsLike(String parentIds){
		return find("from Area where parentIds like :p1", new Parameter(parentIds));
	}

	public List<Area> findAllList(){
		//return find("from Area where delFlag=:p1 order by code", new Parameter(Area.DEL_FLAG_NORMAL));
		return getAll();
	}
	
	public List<Area> findAllChild(Long parentId, String likeParentIds){
		/*return find("from Area where delFlag=:p1 and (id=:p2 or parent.id=:p2 or parentIds like :p3) order by code", 
				new Parameter(Area.DEL_FLAG_NORMAL, parentId, likeParentIds));*/
		return find("from Area where delFlag=:p1 and (id=:p2 or parent.id=:p2 or parentIds like :p3) order by code", 
				new Parameter(Constants.DEL_FLAG_NORMAL, parentId, likeParentIds));
	}
	
	/**
     * {@inheritDoc}
     */
    public Area loadAreaByAreaname(String areaname) {
        List areas = getSession().createCriteria(Area.class).add(Restrictions.eq("areaname", areaname)).list();
        if (areas == null || areas.isEmpty()) {
            //return null;
        	throw new AreaNotExistsException("area '" + areaname + "' not found...");
        }
        
        return (Area)areas.get(0);
    }

	/**
     * {@inheritDoc}
     */
    public Area saveArea(Area area) {
        if (log.isDebugEnabled()) {
            log.debug("area's id: " + area.getId());
        }
        getSession().saveOrUpdate(area);
        // necessary to throw a DataIntegrityViolation and catch it in AreaManager
        getSession().flush();
        return area;
    }
}
