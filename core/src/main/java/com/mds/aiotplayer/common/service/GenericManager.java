/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.exception.ImportFromException;

/**
 * Generic Manager that talks to GenericDao to CRUD POJOs.
 *
 * <p>Extend this interface if you want typesafe (no casting necessary) managers
 * for your domain objects.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *  Updated by jgarcia: added full text search + reindexing
 * @param <T> a type variable
 * @param <PK> the primary key for that type
 */
public interface GenericManager<T, PK extends Serializable> {

    /**
     * Generic method used to get all objects of a particular type. This
     * is the same as lookup up all rows in a table.
     * @return List of populated objects
     */
    List<T> getAll();
    
    /**
 	 * Paging query
 	 * @param page
 	 * @return
 	 */
 	Page<T> getAllPaging(Pageable page);

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
     * @param id the identifier (primary key) of the object to get
     * @return - true if it exists, false if it doesn't
     */
    boolean exists(PK id);

    /**
     * Generic method to save an object - handles both update and insert.
     * @param object the object to save
     * @return the updated object
     */
    T save(T object);
    
    void save(List<T> objects);

    /**
     * Generic method to delete an object
     * @param object the object to remove
     */
    void removeEntity(T object);

    /**
     * Generic method to delete an object based on class and id
     * @param id the identifier (primary key) of the object to remove
     */
    void remove(PK id);

    /**
     * Generic method to search for an object.
     * @param searchTerm the search term
     * @param clazz type of class to search for.
     * @return a list of matched objects
     */
    @SuppressWarnings("rawtypes")
	List<T> search(String searchTerm, Class clazz);
    
    /**
     * Generic method to search for an object.
     * @param searchTerm the search term
     * @param clazz type of class to search for.
     * @return a list of matched objects
     */
    @SuppressWarnings("rawtypes")
    Page<T> search(Searchable searchable, String searchTerm, Class clazz);
    
    /**
     * Generic method to page search for an object.
     * @param searchTerm the search term
     * @param clazz type of class to search for.
     * @return a page object contain matched objects
     */
    @SuppressWarnings("rawtypes")
    Page<T> searchPaging(Pageable page, String searchTerm, Class clazz);
    
    /**
	 * clear data cache
	 */
	void clear();
    
    /**
     * Generic method to regenerate full text index of the persistent class T
     */
    void reindex();

    /**
     * Generic method to regenerate full text index of all indexed classes
     *
     * @param async
     *            true to perform the reindexing asynchronously
     */
    void reindexAll(boolean async);
        
    /**
     * Paging query by search filters
     * 
     *
     * @param searchable
     * @return
     */
    Page<T> findPaging(Searchable searchable);
    
    /**
     * Query by search filters
     * 
     *
     * @param searchable
     * @return
     */
    List<T> findAll(Searchable searchable);
    
    /**
     * find one query by search filters
     * 
     *
     * @param searchable
     * @return
     */
    T findOne(Searchable searchable);

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
    void removeByIds(PK[] ids);
    
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
     */
    List<T> find(PK[] ids);
    
    /**
     * find Primary Keys by search filters
     *
     * @param searchable
     * @return List of Primary Keys
     */
    List<PK> getPrimaryKeys(Searchable searchable);
    
    /**
     * excel import
     *
     * @param entities for import
     */
    @CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    List<T> importFrom(List<T> entities, String[] uniqueKeys) throws ImportFromException;
    
    /**
     * Retrieves a cache key.
     * @return String
     */
	String getCacheKey();
}
