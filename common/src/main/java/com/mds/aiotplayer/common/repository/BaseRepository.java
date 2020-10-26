/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.repository;

import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.common.model.search.Searchable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

/**
 * <p>抽象DAO层基类 提供一些简便方法<br/>
 * 具体使用请参考测试用例：{@see com.sishuok.es.common.repository.UserRepository}
 * <p/>
 * 想要使用该接口需要在spring配置文件的jpa:repositories中添加
 * factory-class="com.sishuok.es.common.repository.support.SimpleBaseRepositoryFactoryBean"
 * <p/>
 * <p>泛型 ： M 表示实体类型；ID表示主键类型
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-12 下午4:46
 * <p>Version: 1.0
 */
@NoRepositoryBean
public interface BaseRepository<M, ID extends Serializable> extends JpaRepository<M, ID> {

	public void setConnectionMode(final boolean readOnlyOptimized) throws SQLException;
	public boolean isReadOnlyEnabled();
    /**
     * 根据主键删除
     *
     * @param ids
     */
    public void delete(ID[] ids);
    
    /*Compatible with 1.6.1*/
    /*
	 * (non-Javadoc) 
	 * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
	 */
    public void delete(final ID id);
    
    /*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
	 */
    public M findOne(ID id);
    
    /*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#existsById(java.io.Serializable)
	 */
    public boolean existsById(ID id);
    
    /* end Compatible with 1.6.1*/

    /*
   * (non-Javadoc)
   * @see org.springframework.data.repository.CrudRepository#findAll()
   */
    List<M> findAll();

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
     */
    List<M> findAll(Sort sort);


    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    Page<M> findAll(Pageable pageable);

    /**
     * 根据条件查询所有
     * 条件 + 分页 + 排序
     *
     * @param searchable
     * @return
     */
    public Page<M> findBySearchable(Searchable searchable);


    /**
     * 根据条件统计所有记录数
     *
     * @param searchable
     * @return
     */
    public long count(Searchable searchable);
    
    /**
     * Gets all records that match a search term. "*" will get them all.
     * @param searchTerm the term to search for
     * @return the matching records
     * @throws SearchException
     */
    List<M> search(String searchTerm) throws SearchException;
    
    /**
     * Gets  a page object that contain records match a search term. "*" will get them all.
     * @param page page information for search result
     * @param searchTerm the term to search for
     * @return the matching records
     * @throws SearchException
     */
     Page<M> search(Pageable page,  String searchTerm) throws SearchException;
     
     /**
      * Gets  a page object that contain records match a search term. "*" will get them all.
      * @param searchable searchable information for search result
      * @param searchTerm the term to search for
      * @return the matching records
      * @throws SearchException
      */
      Page<M> search(Searchable searchable,  String searchTerm) throws SearchException;
    
    /**
     * Gets  a page object that contain records match a search term. "*" will get them all.
     * @param page page information for search result
     * @param fnames the specified fields search by  
     * @param searchTerm the term to search for
     * @return the matching records
     * @throws SearchException
     */
    Page<M> search(Pageable page, String[] fnames,  String searchTerm) throws SearchException;
    
    /**
     * Gets  a page object that contain records match a search term. "*" will get them all.
     * @param searchable searchable information for search result
     * @param fnames the specified fields search by 
     * @param searchTerm the term to search for
     * @return the matching records
     * @throws SearchException
     */
    Page<M> search(Searchable searchable, String[] fnames,  String searchTerm) throws SearchException;
    
    /**
     * Generic method to regenerate full text index of the persistent class M
     */
    void reindex();

    /**
     * Generic method to regenerate full text index of all indexed classes
     * @param async true to perform the reindexing asynchronously
     */
    void reindexAll(boolean async);
    
    /**
     * Generic method used to get all objects of a particular type. This
     * is the same as lookup up all rows in a table.
     * @return List of populated objects
     */
    List<M> getAll();

    /**
     * Gets all records without duplicates.
     * <p>Note that if you use this method, it is imperative that your model
     * classes correctly implement the hashcode/equals methods</p>
     * @return List of populated objects
     */
    List<M> getAllDistinct();


    /**
     * Generic method to get an object based on class and identifier. An
     * ObjectRetrievalFailureException Runtime Exception is thrown if
     * nothing is found.
     *
     * @param id the identifier (primary key) of the object to get
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    M get(ID id);
    
    /**
     * Generic method to update an object - handles update by searchable.
     * @param object the object to save
     * @param searchable locate object to update
     * @return the persisted object
     */
    M update(M object, final Searchable searchable);
    
    /**
     * Generic method to add an object - handles update by searchable if exists.
     * @param object the object to save
     * @param searchable locate object to update
     * @return the persisted object
     */
    M addOrUpdate(M object, final Searchable searchable);
    
    /**
     * Create a new instance of this type in the database.
     *
     * @param t       type to be created.
     * @return entity tracking the created instance.
     */
    M create(M t);

    /**
     * Generic method to delete an object
     * @param object the object to remove
     */
    void remove(M object);

    /**
     * Generic method to delete an object
     * @param id the identifier (primary key) of the object to remove
     */
    void remove(ID id);
    
    /**
     * Generic method to delete all objects
     */
    void removeAll();

    /**
     * Find a list of records by using a named query
     * @param queryName query name of the named query
     * @param queryParams a map of the query names and the values
     * @return a list of the records found
     */
    List<M> findByNamedQuery(String queryName, Map<String, Object> queryParams);
    
    /**
	 * clear data cache
	 */
	void clear();
	
	/**
     * Remove the given entity from the persistence context, causing
     * a managed entity to become detached.  Unflushed changes made
     * to the entity if any (including removal of the entity),
     * will not be synchronized to the database.  Entities which
     * previously referenced the detached entity will continue to
     * reference it.
     * @param entity  entity instance
     * @throws IllegalArgumentException if the instance is not an
     *         entity
     * @since Java Persistence 2.0
     */
    void detach(Object entity);
    
    /**
     * Check if the instance is a managed entity instance belonging
     * to the current persistence context.
     * @param entity  entity instance
     * @return boolean indicating if entity is in persistence context
     * @throws IllegalArgumentException if not an entity
     */
    public boolean contains(Object entity);
    
    /**
     * Utility interface between the application and the persistence
     * provider managing the persistence unit.
     * <p/>
     * @since Java Persistence 2.0
     */
    PersistenceUnitUtil getPersistenceUnitUtil();

	/**
	 * save entity list
	 * @param entityList
	 */
	void save(List<M> entityList);
		
	/**
	 * update by JPQL string
	 * @param qlString
	 * @return
	 */
	int update(String qlString);
	
	/**
	 * update by JPQL string with parameter
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	int update(String qlString, Parameter parameter);
	
	/**
	 * Logical deletion
	 * @param id
	 * @return
	 */
	int removeById(ID id);
	
	/**
	 * Logical deletion
	 * @param id
	 * @param likeParentIds
	 * @return
	 */
	int removeById(ID id, String likeParentIds);
	
	/**
	 * update deletion flag
	 * @param id
	 * @param delFlag
	 * @return
	 */
	int updateDelFlag(ID id, String delFlag);
	
	/**
	 * Native SQL Update
	 * @param sqlString
	 * @param parameter
	 * @return
	 */
	int updateBySql(String sqlString, Parameter parameter);
	
	/**
	 * create Native SQL query object
	 * @param sqlString
	 * @param parameter
	 * @return
	 */
	Query createSqlQuery(String sqlString, Parameter parameter);
	
	/**
	 * Create JPQL query object
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	Query createQuery(String qlString, Parameter parameter);
	
	// -------------- QL Query --------------

	/**
	 * JPQL paging query
	 * @param page
	 * @param qlString
	 * @return
	 */
	<E> Page<E> find(Pageable page, String qlString);
	
	/**
	 * JPQL paging query
	 * @param page
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	<E> Page<E> find(Pageable page, String qlString, Parameter parameter);
    
    /**
	 * JPQL query
	 * @param qlString
	 * @return
	 */
	<E> List<E> find(String qlString);
	
	/**
	 * JPQL query
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	<E> List<E> find(String qlString, Parameter parameter);
		
	/**
	 * retrieve entity by JPQL
	 * @param qlString
	 * @return
	 */
	<E> E getByHql(String qlString);
	
	/**
	 * retrieve entity by JPQL
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	<E> E getByHql(String qlString, Parameter parameter);
    
    // -------------- Criteria --------------
    
    /**
 	 * Paging query
 	 * @param page
 	 * @return
 	 */
 	Page<M> find(Pageable page);
 	
 	/**
 	 * Paging query by standard object
 	 * @param page
 	 * @param criteriaQuery
 	 * @return
 	 */
 	Page<M> find(Pageable page, CriteriaQuery criteriaQuery);
 	
 	/**
 	 * Paging query by standard object
 	 * @param criteriaQuery
 	 * @return
 	 */
 	List<M> find(CriteriaQuery criteriaQuery);
 	 	
 	/**
 	 * Record count by standard object
 	 * @param criteriaQuery
 	 * @return
 	 */
 	long count(CriteriaQuery criteriaQuery);
 	
 	 /**
	 * Create a query standard object independent of the session
	 * @param criterions Restrictions.eq("name", value);
	 * @return 
	 */
 	CriteriaQuery createCriteriaQuery(Predicate... predicates);
    
    /**
     * Paging query by search filters
     * 
     *
     * @param searchable
     * @return
     */
    Page<M> find(Searchable searchable);
    
    
    /**
     * find one query by search filters
     * 
     *
     * @param searchable
     * @return
     */
    M findOne(Searchable searchable);
    
    /**
     * find one query by search filters
     * 
     *
     * @param searchable
     * @return
     */
    boolean findAny(Searchable searchable);
    
    /**
     * Query by search filters
     * 
     *
     * @param searchable
     * @return
     */
    List<M> findAll(Searchable searchable);
    
    /**
     * delete by Primary Keys
     *
     * @param ids
     */
    void remove(ID[] ids);
    
    /**
     * delete by entities
     *
     * @param ids
     */
    void remove(final Iterable<M> entities);
    
    /**
     * find by Primary Keys
     *
     * @param ids
     */
    List<M> find(ID[] ids);
    
    /**
     * find Primary Keys by search filters
     *
     * @param searchable
     * @return List of Primary Keys
     */
    List<ID> getPrimaryKeys(Searchable searchable);
}
