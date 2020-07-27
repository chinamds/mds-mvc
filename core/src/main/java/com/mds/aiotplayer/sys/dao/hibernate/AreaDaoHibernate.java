package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.sys.model.Area;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.sys.dao.AreaDao;
import com.mds.aiotplayer.sys.exception.AreaNotExistsException;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;

import java.util.List;

import javax.persistence.Query;

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
    	Query q = getEntityManager().createQuery("select a from Area a where areaname=?1");
        q.setParameter(1, areaname);
        
        List areas = q.getResultList();
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
        var result = super.save(area);
        // necessary to throw a DataIntegrityViolation and catch it in AreaManager
        getEntityManager().flush();
        
        return result;
    }
}
