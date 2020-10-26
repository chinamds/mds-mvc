/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.service.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.dao.GenericDao;
import com.mds.aiotplayer.common.exception.ImportFromException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.model.AbstractEntity;
//import com.mds.aiotplayer.common.model.Page;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.common.exception.Exceptions;
import com.mds.aiotplayer.sys.util.UserUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * This class serves as the Base class for all other Managers - namely to hold
 * common CRUD methods that they might all use. You should only need to extend
 * this class when your require custom CRUD logic.
 * <p/>
 * <p>To register this class in your Spring context file, use the following XML.
 * <pre>
 *     &lt;bean id="userManager" class="com.mds.aiotplayer.common.service.impl.GenericManagerImpl"&gt;
 *         &lt;constructor-arg&gt;
 *             &lt;bean class="com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate"&gt;
 *                 &lt;constructor-arg value="com.mds.aiotplayer.model.User"/&gt;
 *                 &lt;property name="sessionFactory" ref="sessionFactory"/&gt;
 *             &lt;/bean&gt;
 *         &lt;/constructor-arg&gt;
 *     &lt;/bean&gt;
 * </pre>
 * <p/>
 * <p>If you're using iBATIS instead of Hibernate, use:
 * <pre>
 *     &lt;bean id="userManager" class="com.mds.aiotplayer.common.service.impl.GenericManagerImpl"&gt;
 *         &lt;constructor-arg&gt;
 *             &lt;bean class="com.mds.aiotplayer.common.dao.ibatis.GenericDaoiBatis"&gt;
 *                 &lt;constructor-arg value="com.mds.aiotplayer.model.User"/&gt;
 *                 &lt;property name="dataSource" ref="dataSource"/&gt;
 *                 &lt;property name="sqlMapClient" ref="sqlMapClient"/&gt;
 *             &lt;/bean&gt;
 *         &lt;/constructor-arg&gt;
 *     &lt;/bean&gt;
 * </pre>
 *
 * @param <T>  a type variable
 * @param <PK> the primary key for that type
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *  Updated by jgarcia: added full text search + reindexing
 */
public class GenericManagerImpl<T, PK extends Serializable> implements GenericManager<T, PK> {
    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * GenericDao instance, set by constructor of child classes
     */
    protected GenericDao<T, PK> dao;


    public GenericManagerImpl() {
    }

    public GenericManagerImpl(GenericDao<T, PK> genericDao) {
        this.dao = genericDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getAll() {
        return dao.getAll();
    }
    
    /**
 	 * Paging query
 	 * @param page
 	 * @return
 	 */
    @Override
 	public Page<T> getAllPaging(Pageable page){
        return dao.find(page);
    }

    /**
     * {@inheritDoc}
     */
 	@Override
    public T get(PK id) {
        return dao.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(PK id) {
        return dao.exists(id);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public T save(T object) {
        return dao.save(object);
    }
    
    @Transactional
    @Override
    public void save(List<T> objects) {
    	for (T object : objects) {
    		dao.save(object);
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeEntity(T object) {
        dao.remove(object);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(PK id) {
        dao.remove(id);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Search implementation using Hibernate Search.
     */
    @SuppressWarnings("rawtypes")
    @Override
	public List<T> search(String q, Class clazz) {
        if (q == null || "".equals(q.trim())) {
            return getAll();
        }

        return dao.search(q);
    }
    
    /**
     * {@inheritDoc}
     * <p/>
     * Search implementation using Hibernate Search.
     */
    @SuppressWarnings("rawtypes")
    @Override
	public Page<T> search(Searchable searchable, String q, Class clazz) {
        if (q == null || "".equals(q.trim())) {
            return dao.find(searchable);
        }

        return dao.search(searchable, q);
    }
    
    /**
     * {@inheritDoc}
     * <p/>
     * Page search implementation using Hibernate Search.
     */
    @SuppressWarnings("rawtypes")
    @Override
	public  Page<T> searchPaging(Pageable page,  String q, Class clazz) {
        if (q == null || "".equals(q.trim())) {
            return dao.find(page);
        }

        return dao.search(page, q);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void clear(){ 
		dao.clear();
	}

    /**
     * {@inheritDoc}
     */
	@Transactional
	@Override
    public void reindex() {
        dao.reindex();
    }

    /**
     * {@inheritDoc}
     */
	@Transactional
	@Override
    public void reindexAll(boolean async) {
        dao.reindexAll(async);
    }
    
    @Override
	public Page<T> findPaging(final Searchable searchable) {
		return dao.find(searchable);
	}
    
    @Override
	public List<T> findAll(final Searchable searchable) {
		return dao.findAll(searchable);
	}
    
    @Override
	public T findOne(final Searchable searchable) {
    	return dao.findOne(searchable);
    }
	
	@Override
	public long count(final Searchable searchable) {
		return dao.count(searchable);
	}
	
	/**
     * delete by Primary Keys
     *
     * @param ids
     */
	@Transactional
	@Override
    public void removeByIds(PK[] ids){
    	dao.remove(ids);
    }
    
    /**
     * delete by entities
     *
     * @param entities
     */
	@Transactional
	@Override
    public void remove(final Iterable<T> entities){
    	dao.remove(entities);
    }
    
    /**
     * find by Primary Keys
     *
     * @param ids
     */
	@Override
    public List<T> find(PK[] ids){
    	return dao.find(ids);
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
    public List<PK> getPrimaryKeys(Searchable searchable){
    	return dao.getPrimaryKeys(searchable);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<T> importFrom(List<T> entities, String[] uniqueKeys) throws ImportFromException{
    	if (entities.size() == 0)
    		return entities;
    	
    	Searchable searchable = null;
    	List<T> saved = Lists.newArrayList();
    	List<String> Keys = Lists.newArrayList();
    	try {
	    	for (T entity : entities){
				searchable = Searchable.newSearchable();
				for(String field : uniqueKeys){
					Object key = Reflections.invokeGetter(entity, field);
					Keys.add(key.toString());
					searchable.addSearchFilter(field, SearchOperator.eq, key);	
				}
	            
				saved.add(dao.addOrUpdate(entity, searchable));
			}
    	} catch (final Exception e) {
	        e.printStackTrace();
	        log.warn(e.getMessage());
	        throw new ImportFromException("import record '" + StringUtils.join(Keys, ",") + "' failure!");
	    }
    	
    	return saved;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.notspecified.toString();
    }
}
