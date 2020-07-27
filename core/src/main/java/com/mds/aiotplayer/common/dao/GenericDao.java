package com.mds.aiotplayer.common.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

//import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.transform.ResultTransformer;

import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.core.ReloadableEntity;

//import com.mds.aiotplayer.common.model.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.common.model.Parameter;

/**
 * Generic DAO (Data Access Object) with common methods to CRUD POJOs.
 *
 * <p>Extend this interface if you want typesafe (no casting necessary) DAO's for your
 * domain objects.
 *
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 * @author jgarcia (update: added full text search + reindexing)
 *
 * @param <T> a type variable
 * @param <PK> the primary key for that type
 */
public interface GenericDao <T, PK extends Serializable> {

    /**
     * Generic method used to get all objects of a particular type. This
     * is the same as lookup up all rows in a table.
     * @return List of populated objects
     */
    List<T> getAll();

    /**
     * Gets all records without duplicates.
     * <p>Note that if you use this method, it is imperative that your model
     * classes correctly implement the hashcode/equals methods</p>
     * @return List of populated objects
     */
    List<T> getAllDistinct();

    /**
     * Gets all records that match a search term. "*" will get them all.
     * @param searchTerm the term to search for
     * @return the matching records
     * @throws SearchException
     */
    List<T> search(String searchTerm) throws SearchException;
    
    /**
     * Gets  a page object that contain records match a search term. "*" will get them all.
     * @param page page information for search result
     * @param searchTerm the term to search for
     * @return the matching records
     * @throws SearchException
     */
     Page<T> search(Pageable page,  String searchTerm) throws SearchException;
     
     /**
      * Gets  a page object that contain records match a search term. "*" will get them all.
      * @param searchable searchable information for search result
      * @param searchTerm the term to search for
      * @return the matching records
      * @throws SearchException
      */
      Page<T> search(Searchable searchable,  String searchTerm) throws SearchException;
    
    /**
     * Gets  a page object that contain records match a search term. "*" will get them all.
     * @param page page information for search result
     * @param fnames the specified fields search by  
     * @param searchTerm the term to search for
     * @return the matching records
     * @throws SearchException
     */
    Page<T> search(Pageable page, String[] fnames,  String searchTerm) throws SearchException;
    
    /**
     * Gets  a page object that contain records match a search term. "*" will get them all.
     * @param searchable searchable information for search result
     * @param fnames the specified fields search by 
     * @param searchTerm the term to search for
     * @return the matching records
     * @throws SearchException
     */
    Page<T> search(Searchable searchable, String[] fnames,  String searchTerm) throws SearchException;

    /**
     * Generic method to get an object based on class and identifier. An
     * ObjectRetrievalFailureException Runtime Exception is thrown if
     * nothing is found.
     *
     * @param id the identifier (primary key) of the object to get
     * @return a populated object
     * @see org.springframework.orm.ObjectRetrievalFailureException
     */
    T get(PK id);

    /**
     * Checks for existence of an object of type T using the id arg.
     * @param id the id of the entity
     * @return - true if it exists, false if it doesn't
     */
    boolean exists(PK id);

    /**
     * Generic method to save an object - handles both update and insert.
     * @param object the object to save
     * @return the persisted object
     */
    T save(T object);
    
    /**
     * Generic method to update an object - handles update by searchable.
     * @param object the object to save
     * @param searchable locate object to update
     * @return the persisted object
     */
    T update(T object, final Searchable searchable);
    
    /**
     * Generic method to add an object - handles update by searchable if exists.
     * @param object the object to save
     * @param searchable locate object to update
     * @return the persisted object
     */
    T addOrUpdate(T object, final Searchable searchable);
    
    /**
     * Generic method to delete an object
     * @param object the object to remove
     */
    void remove(T object);

    /**
     * Generic method to delete an object
     * @param id the identifier (primary key) of the object to remove
     */
    void remove(PK id);
    
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
    List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams);

    /**
     * Generic method to regenerate full text index of the persistent class T
     */
    void reindex();

    /**
     * Generic method to regenerate full text index of all indexed classes
     * @param async true to perform the reindexing asynchronously
     */
    void reindexAll(boolean async);
    
    /**
	 * force database synchronization
	 */
	void flush();

	/**
	 * clear data cache
	 */
	void clear();
	
	/**
	 * Set IDENTITY_INSERT to ON/OFF
	 * For MSSQL Database
	 */
	void setIdentityInsert(boolean bOnOff);
	
	/**
	 * save entity list
	 * @param entityList
	 */
	void save(List<T> entityList);
		
	/**
	 * update by ql string
	 * @param qlString
	 * @return
	 */
	int update(String qlString);
	
	/**
	 * update by ql string with parameter
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
	int removeById(PK id);
	
	/**
	 * Logical deletion
	 * @param id
	 * @param likeParentIds
	 * @return
	 */
	int removeById(PK id, String likeParentIds);
	
	/**
	 * update deletion flag
	 * @param id
	 * @param delFlag
	 * @return
	 */
	int updateDelFlag(PK id, String delFlag);
	
	/**
	 * SQL Update
	 * @param sqlString
	 * @param parameter
	 * @return
	 */
	int updateBySql(String sqlString, Parameter parameter);
	
	/**
	 * create SQL query object
	 * @param sqlString
	 * @param parameter
	 * @return
	 */
	Query createSqlQuery(String sqlString, Parameter parameter);
	
	/**
	 * Create QL query object
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	Query createQuery(String qlString, Parameter parameter);
	
	// -------------- QL Query --------------

	/**
	 * QL paging query
	 * @param page
	 * @param qlString
	 * @return
	 */
	<E> Page<E> find(Pageable page, String qlString);
	
	/**
	 * QL paging query
	 * @param page
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	<E> Page<E> find(Pageable page, String qlString, Parameter parameter);
    
    /**
	 * QL query
	 * @param qlString
	 * @return
	 */
	<E> List<E> find(String qlString);
	
	/**
	 * QL query
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	<E> List<E> find(String qlString, Parameter parameter);
	
	/**
	 * QL find all
	 * @return
	 */
	List<T> findAll();
	
	/**
	 * retrieve entity
	 * @param qlString
	 * @return
	 */
	T getByHql(String qlString);
	
	/**
	 * retrieve entity
	 * @param qlString
	 * @param parameter
	 * @return
	 */
	T getByHql(String qlString, Parameter parameter);
    
    // -------------- Criteria --------------
    
    /**
 	 * Paging query
 	 * @param page
 	 * @return
 	 */
 	Page<T> find(Pageable page);
 	
 	/**
 	 * Paging query by standard object
 	 * @param page
 	 * @param criteriaQuery
 	 * @return
 	 */
 	Page<T> find(Pageable page, CriteriaQuery criteriaQuery);
 	 	
 	/**
 	 * Paging query by standard object
 	 * @param criteriaQuery
 	 * @return
 	 */
 	List<T> find(CriteriaQuery criteriaQuery);
 	
 	
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
    Page<T> find(Searchable searchable);
    
    
    /**
     * find one query by search filters
     * 
     *
     * @param searchable
     * @return
     */
    T findOne(Searchable searchable);
    
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
    List<T> findAll(Searchable searchable);


    /**
     * Recoords count by search filters
     *
     * @param searchable
     * @return
     */
    long count(Searchable searchable);
    
    /**
     * delete by Primary Keys
     *
     * @param ids
     */
    void remove(PK[] ids);
    
    /**
     * delete by entities
     *
     * @param ids
     */
    void remove(final Iterable<T> entities);
    
    /**
     * find by Primary Keys
     *
     * @param ids
     * @throws SQLException 
     */
    List<T> find(PK[] ids);
    
    /**
     * find Primary Keys by search filters
     *
     * @param searchable
     * @return List of Primary Keys
     */
    List<PK> getPrimaryKeys(Searchable searchable);
    
    long getCacheSize() throws SQLException;
    
    /**
     * How many rows match this query?
     *
     * @param query description of the rows.
     * @return count of matching rows.
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
	int count(Query query);
    
    @SuppressWarnings("unchecked")
    <E extends ReloadableEntity> E reloadEntity(final E entity) throws SQLException;
    
    /**
     * Create a new instance of this type in the database.
     *
     * @param t       type to be created.
     * @return entity tracking the created instance.
     * @throws SQLException
     */
    public T create(T t) throws SQLException;

    /**
     * Persist this instance in the database.
     *
     * @param context current DSpace context.
     * @param t       type created here.
     * @throws SQLException passed through.
     */
    //public void save(T t) throws SQLException;

    /**
     * Remove an instance from the database.
     *
     * @param context current DSpace context.
     * @param t       type of the instance to be removed.
     * @throws SQLException passed through.
     */
    public void delete(T t) throws SQLException;

    /**
     * Fetch all persisted instances of a given object type.
     *
     * @param context The relevant DSpace Context.
     * @param clazz   the desired type.
     * @return list of DAOs of the same type as clazz
     * @throws SQLException if database error
     */
    public List<T> findAll(Class<T> clazz) throws SQLException;

    /**
     * Fetch all persisted instances of a given object type.
     *
     * @param context The relevant DSpace Context.
     * @param clazz   the desired type.
     * @param limit   paging limit
     * @param offset  paging offset
     * @return list of DAOs of the same type as clazz
     * @throws SQLException if database error
     */
    List<T> findAll(Class<T> clazz, Integer limit, Integer offset) throws SQLException;

    /**
     * Execute a JPQL query returning a unique result.
     *
     * @param context The relevant DSpace Context.
     * @param query   JPQL query string
     * @return a DAO specified by the query string
     * @throws SQLException if database error
     */
    public T findUnique(String query) throws SQLException;

    /**
     * Fetch the entity identified by its legacy database identifier.
     *
     * @param context current DSpace context.
     * @param clazz   class of entity to be found.
     * @param id      legacy database record ID.
     * @return the found entity.
     * @throws SQLException passed through.
     */
    public T findById(Class clazz, int id) throws SQLException;

    /**
     * Fetch the entity identified by its UUID primary key.
     *
     * @param context current DSpace context.
     * @param clazz   class of entity to be found.
     * @param id      primary key of the database record.
     * @return the found entity.
     * @throws SQLException
     */
    public T findById(Class clazz, UUID id) throws SQLException;

    /**
     * Execute a JPQL query and return a collection of results.
     *
     * @param context The relevant DSpace Context.
     * @param query   JPQL query string
     * @return list of DAOs specified by the query string
     * @throws SQLException if database error
     */
    public List<T> findMany(String query) throws SQLException;
}