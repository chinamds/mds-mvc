/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.SortField;
import com.mds.aiotplayer.common.Constants;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.dao.GenericDao;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.repository.hibernate.HibernateSearchTools;
import com.mds.aiotplayer.common.repository.support.JpaUtils;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.core.ReloadableEntity;
import com.mds.aiotplayer.common.model.AbstractEntity;
//import com.mds.aiotplayer.common.model.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.util.StringUtils;

import org.hibernate.query.NativeQuery;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import com.google.common.collect.AbstractIterator;

import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * This class serves as the Base class for all other DAOs - namely to hold
 * common CRUD methods that they might all use. You should only need to extend
 * this class when your require custom CRUD logic.
 * <p/>
 * <p>To register this class in your Spring context file, use the following XML.
 * <pre>
 *      &lt;bean id="fooDao" class="com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate"&gt;
 *          &lt;constructor-arg value="com.mds.aiotplayer.model.Foo"/&gt;
 *      &lt;/bean&gt;
 * </pre>
 *
 * @param <T>  a type variable
 * @param <PK> the primary key for that type
 * @author <a href="mailto:bwnoll@gmail.com">Bryan Noll</a>
 *         Updated by jgarcia: update hibernate3 to hibernate5
 * @author jgarcia (update: added full text search + reindexing)
 */
public class GenericDaoHibernate<T, PK extends Serializable> implements GenericDao<T, PK> {
    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging ApplicationEntityManager
     */
	public static final String PERSISTENCE_UNIT_NAME = "default";
	
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private Class<T> persistentClass;
    /*@Resource
    private EntityManager entityManager;*/
    
    /**
     * Entity manager, injected by Spring using @PersistenceContext annotation on setEntityManager()
     */
    @PersistenceContext(unitName=PERSISTENCE_UNIT_NAME)
    private EntityManager entityManager;
    
    private Analyzer defaultAnalyzer;

    /**
     * Constructor that takes in a class to see which type of entity to persist.
     * Use this constructor when subclassing.
     *
     * @param persistentClass the class type you'd like to persist
     */
    public GenericDaoHibernate(final Class<T> persistentClass) {
        this.persistentClass = persistentClass;
        defaultAnalyzer = new StandardAnalyzer();
    }

    /**
     * Constructor that takes in a class and entityManager for easy creation of DAO.
     *
     * @param persistentClass the class type you'd like to persist
     * @param entityManager  the pre-configured Hibernate EntityManager
     */
    public GenericDaoHibernate(final Class<T> persistentClass, EntityManager entityManager) {
        this.persistentClass = persistentClass;
        this.entityManager = entityManager;
        defaultAnalyzer = new StandardAnalyzer();
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    @Autowired
    @Required
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> getAll() {
        //EntityManager sess = getEntityManager();
        //return sess.createCriteria(persistentClass).list();
    	try {
			return findAll(persistentClass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return Lists.newArrayList();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> getAllDistinct() {
        Collection<T> result = new LinkedHashSet<T>(getAll());
        return new ArrayList<T>(result);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<T> search(String searchTerm) throws SearchException {
    	EntityManager sess = getEntityManager();
        FullTextEntityManager txtSession = Search.getFullTextEntityManager(sess);

        org.apache.lucene.search.Query qry;
        try {
            qry = com.mds.aiotplayer.common.repository.hibernate.HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, txtSession, defaultAnalyzer);
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        org.hibernate.search.jpa.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        int total = hibQuery.getResultSize();
        
        return hibQuery.getResultList();
    }
       
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public  Page<T> search(Pageable page, String searchTerm) throws SearchException {
    	EntityManager sess = getEntityManager();
        FullTextEntityManager txtSession = Search.getFullTextEntityManager(sess);

        org.apache.lucene.search.Query qry;
        try {
            qry = com.mds.aiotplayer.common.repository.hibernate.HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, txtSession, defaultAnalyzer);
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.jpa.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        
        int total = hibQuery.getResultSize();
        List<T> content = Collections.<T>emptyList();
        if (total > page.getOffset())
        {
	        hibQuery.setFirstResult((int)page.getOffset());
	    	hibQuery.setMaxResults(page.getPageSize());
	    	content = hibQuery.getResultList();
        }
      
        return new PageImpl<T>(content, page, total);   
    }
    
    @Transactional
    @Override
    public  Page<T> search(Searchable searchable, String searchTerm) throws SearchException {
    	EntityManager sess = getEntityManager();
        FullTextEntityManager txtSession = Search.getFullTextEntityManager(sess);

        org.apache.lucene.search.Query qry;
        try {       			
            qry = HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, txtSession, defaultAnalyzer);
            if (searchable.hasSearchFilter()) {
            	QueryBuilder querybuilder = txtSession.getSearchFactory()
            	        .buildQueryBuilder()
            	        .forEntity(this.persistentClass)
            	        .get();
            	
				/*
				 * BooleanQuery.Builder bQuery = new BooleanQuery.Builder(); bQuery.add(qry,
				 * Occur.MUST); bQuery.add(HibernateSearchTools.generateQuery(searchable),
				 * Occur.FILTER); qry = bQuery.build();
				 */           	
				qry = HibernateSearchTools.generateQuery(querybuilder, searchable).must(qry).createQuery();
            }
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.jpa.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        
        /*if (searchable.hasSearchFilter()) {
        	hibQuery.setFilter(new CachingWrapperFilter(new QueryWrapperFilter(HibernateSearchTools.generateQuery(searchable))));
        }*/
        
        if (searchable.hashSort()) {
        	hibQuery.setSort(HibernateSearchTools.prepareOrder(searchable, this.persistentClass));
        }
        
        int total = hibQuery.getResultSize();
        List<T> content = Collections.<T>emptyList();
        if (searchable.hasPageable()) {
        	if (total > searchable.getPage().getOffset()){
        		hibQuery.setFirstResult((int)searchable.getPage().getOffset());
    	    	hibQuery.setMaxResults(searchable.getPage().getPageSize());
    	    	content = hibQuery.getResultList();
        	}
        }else {
        	content = hibQuery.getResultList();
        }
      
        return new PageImpl<T>(content, searchable.getPage(), total);   
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Page<T> search(Pageable page, String[] fnames, String searchTerm) throws SearchException {
    	EntityManager sess = getEntityManager();
        FullTextEntityManager txtSession = Search.getFullTextEntityManager(sess);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, fnames, txtSession, defaultAnalyzer);
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.jpa.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        
        int total = hibQuery.getResultSize();
        List<T> content = Collections.<T>emptyList();
        if (total > page.getOffset())
        {
	        hibQuery.setFirstResult((int)page.getOffset());
	    	hibQuery.setMaxResults(page.getPageSize());
	    	content = hibQuery.getResultList();
        }
      
        return new PageImpl<T>(content, page, total);   
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Page<T> search(Searchable searchable, String[] fnames, String searchTerm) throws SearchException {
    	EntityManager sess = getEntityManager();
        FullTextEntityManager txtSession = Search.getFullTextEntityManager(sess);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, fnames, txtSession, defaultAnalyzer);
			if (searchable.hasSearchFilter()) { 
				  /*BooleanQuery.Builder bQuery = new BooleanQuery.Builder(); 
				  bQuery.add(qry, Occur.MUST);
				  bQuery.add(HibernateSearchTools.generateQuery(searchable), Occur.FILTER); 
				  qry =	  bQuery.build();*/
				  QueryBuilder querybuilder = txtSession.getSearchFactory()
            	        .buildQueryBuilder()
            	        .forEntity(this.persistentClass )
            	        .get();
            	
				  qry = HibernateSearchTools.generateQuery(querybuilder, searchable).must(qry).createQuery();
			}
			 
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.jpa.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        
        /*if (searchable.hasSearchFilter()) {
        	hibQuery.setFilter(new CachingWrapperFilter(new QueryWrapperFilter(HibernateSearchTools.generateQuery(searchable))));
        }*/
                        
        if (searchable.hashSort()) {
        	hibQuery.setSort(HibernateSearchTools.prepareOrder(searchable, this.persistentClass));
        }
        
        int total = hibQuery.getResultSize();
        List<T> content = Collections.<T>emptyList();
        boolean bGetPage = false;
        if (searchable.hasPageable()) {
	        if (total > searchable.getPage().getOffset()) {
		        hibQuery.setFirstResult((int)searchable.getPage().getOffset());
		    	hibQuery.setMaxResults(searchable.getPage().getPageSize());
		    	content = hibQuery.getResultList();
		    	bGetPage = true;
	        }
        }
        
        if (!bGetPage) {
        	content = hibQuery.getResultList();
        }
              
        return new PageImpl<T>(content, searchable.getPage(), total);   
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public T get(PK id) {
    	EntityManager sess = getEntityManager();
    	T entity = this.entityManager.find(this.persistentClass, id);

        if (entity == null) {
            log.warn("Uh oh, '" + this.persistentClass + "' object with id '" + id + "' not found...");
            throw new ObjectRetrievalFailureException(this.persistentClass, id);
        }

        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean exists(PK id) {
        EntityManager sess = getEntityManager();
        T entity = this.entityManager.find(this.persistentClass, id);
        
        return entity != null;
    }
    
    protected T preSave(T object) {
    	try {
			// get entity id
			/*Object id = null;
			for (Method method : object.getClass().getMethods()){
				Id idAnn = method.getAnnotation(Id.class);
				if (idAnn != null){
					id = method.invoke(object);
					break;
				}
			}*/
    		boolean isNew = false;
			Method isNewMethod = object.getClass().getMethod("isNew");
			if (isNewMethod != null){
				isNew = (boolean)isNewMethod.invoke(object);
			}
			// invoke method prePersist
			if (isNew){ //StringUtils.isBlank((String)id)
				return preAdd(object);
			}else{// invoke method preUpdate
				return preUpdate(object, null);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return object;
    }
    
    protected T preAdd(T object) {
    	try {
			// invoke method prePersist
			/*for (Method method : object.getClass().getMethods()){
				PrePersist pp = method.getAnnotation(PrePersist.class);
				if (pp != null){
					method.invoke(object);
					break;
				}
			}*/
    		Method method = Reflections.getAccessibleMethodByName(object, "prePersist");
    		if (method != null) {
    			method.invoke(object);
    		}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return object;
    }
    
    protected T preUpdate(T object, T source) {
    	try {
			// invoke method preUpdate
			/*for (Method method : object.getClass().getMethods()){
				PreUpdate pu = method.getAnnotation(PreUpdate.class);
				if (pu != null){
					method.invoke(object, source);
					break;
				}
			}*/
    		Reflections.invokeMethodByName(object, "beforeUpdate", new Object[] {source});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return object;
    }
        
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public T save(T object) {
        EntityManager sess = getEntityManager();
        return (T) sess.merge(preSave(object));
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public T update(T object, final Searchable searchable) {
        List<T> entityList = findAll(searchable);
        if (entityList != null && entityList.size() > 0){
        	T entity = entityList.get(0);
        	
        	EntityManager sess = getEntityManager();
        	sess.merge(preUpdate(entity, object));
        	
        	return entity;
        	//return (T) sess.update(preUpdate(object));
        }
        
        return null;
    }
        
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public T addOrUpdate(T object, final Searchable searchable) {
    	T result = update(object, searchable);
    	if (result == null){
        	return save(object);
        }
    	
    	return result;
     }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(T object) {
        entityManager.remove(entityManager.contains(object) ? object : entityManager.merge(object));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(PK id) {
    	this.entityManager.remove(this.get(id));
    }
    
    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeAll(){
    	remove(getAll());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams) {
    	TypedQuery<T> query = getEntityManager().createNamedQuery(queryName, persistentClass);   		  
        for (String s : queryParams.keySet()) {
            Object val = queryParams.get(s);
           	query.setParameter(s, val);
        }
        
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void reindex() {
    	com.mds.aiotplayer.common.repository.hibernate.HibernateSearchTools.reindex(persistentClass, getEntityManager());
    }


    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void reindexAll(boolean async) {
    	com.mds.aiotplayer.common.repository.hibernate.HibernateSearchTools.reindexAll(async, getEntityManager());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public void flush(){
		getEntityManager().flush();
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
	public void clear(){ 
		getEntityManager().clear();
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
	public void setIdentityInsert(boolean bOnOff){
		getEntityManager().createNativeQuery("SET IDENTITY_INSERT "+ this.persistentClass.getSimpleName() + (bOnOff ? " ON" : " OFF"));
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public void save(List<T> entityList){
		if (entityList != null && entityList.size() > 0){
			try {
				Method isNewMethod = entityList.get(0).getClass().getMethod("isNew");
				//Method  puMethod = Reflections.getAnnotationMethod(entityList.get(0).getClass(), PreUpdate.class);
				//Method  ppMethod = Reflections.getAnnotationMethod(entityList.get(0).getClass(), PrePersist.class);
				Method puMethod = entityList.get(0).getClass().getMethod("beforeUpdate"); //Reflections.getAccessibleMethodByName(entityList.get(0), "beforeUpdate");
				Method ppMethod = entityList.get(0).getClass().getMethod("prePersist"); //Reflections.getAccessibleMethodByName(entityList.get(0), "prePersist");
				boolean isNew = false;
				for (T entity : entityList){
					isNew = false;
					if (isNewMethod != null){
						isNew = (boolean)isNewMethod.invoke(entity);
					}
					if (!isNew){
						if (puMethod != null){
							puMethod.invoke(entity);
						}
					}else{
						if (ppMethod != null){
							ppMethod.invoke(entity);
						}
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (T entity : entityList){
				save(entity);
			}
		}
	}

	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public int update(String qlString){
		return update(qlString, null);
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public int update(String qlString, Parameter parameter){
		return createQuery(qlString, parameter).executeUpdate();
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public int removeById(PK id){
		return update("update "+persistentClass.getSimpleName()+" set delFlag='" + Constants.DEL_FLAG_DELETE + "' where id = :p1", 
				new Parameter(id));
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public int removeById(PK id, String likeParentIds){
		return update("update "+persistentClass.getSimpleName()+" set delFlag = '" + Constants.DEL_FLAG_DELETE + "' where id = :p1 or parentIds like :p2",
				new Parameter(id, likeParentIds));
	}
	
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public int updateDelFlag(PK id, String delFlag){
		return update("update "+persistentClass.getSimpleName()+" set delFlag = :p2 where id = :p1", 
				new Parameter(id, delFlag));
	}
    
	/**
     * {@inheritDoc}
     */
	@Transactional
	@Override
	public int updateBySql(String sqlString, Parameter parameter){
		return createSqlQuery(sqlString, parameter).executeUpdate();
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Query createSqlQuery(String sqlString, Parameter parameter){
		javax.persistence.Query query = getEntityManager().createNativeQuery(sqlString);
		setParameter(query, parameter);
		
		return query;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public javax.persistence.Query createQuery(String qlString, Parameter parameter){
		javax.persistence.Query query = getEntityManager().createQuery(qlString);
		setParameter(query, parameter);
		
		return query;
	}
	
	// -------------- Query Tools --------------

	/**
	 * set query result type
	 * @param query
	 * @param resultClass
	 */
	private void setResultTransformer(NativeQuery query, Class<?> resultClass){
		if (resultClass != null){
			if (resultClass == Map.class){
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			}else if (resultClass == List.class){
				query.setResultTransformer(Transformers.TO_LIST);
			}else{
				query.addEntity(resultClass);
			}
		}
	}
	
	/**
	 * set query parameters
	 * @param query
	 * @param parameter
	 */
	private void setParameter(javax.persistence.Query query, Parameter parameter){
		if (parameter != null) {
            Set<String> keySet = parameter.keySet();
            for (String string : keySet) {
                Object value = parameter.get(string);
                query.setParameter(string, value);
            }
        }
	}
	
    /** 
     * remove Select clause in qlString。 
     * @param qlString
     * @return 
     */  
    private String removeSelect(String qlString){  
        int beginPos = qlString.toLowerCase().indexOf("from");  
        return qlString.substring(beginPos);  
    }  
      
    /** 
     * remove orderby clause in hql。 
     * @param qlString
     * @return 
     */  
    private String removeOrders(String qlString) {  
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);  
        Matcher m = p.matcher(qlString);  
        StringBuffer sb = new StringBuffer();  
        while (m.find()) {  
            m.appendReplacement(sb, "");  
        }
        m.appendTail(sb);
        return sb.toString();  
    }
	
	// -------------- QL Query --------------

    /**
     * {@inheritDoc}
     */
    @Override
	public <E> Page<E> find(Pageable page, String qlString){
    	return find(page, qlString, null);
    }
    
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
	public <E> Page<E> find(Pageable page, String qlString, Parameter parameter){
    	String countQlString = "select count(*) " + removeSelect(removeOrders(qlString));  
//      page.setCount(Long.valueOf(createQuery(countQlString, parameter).uniqueResult().toString()));
        Query query = createQuery(countQlString, parameter);
        List<Object> list = query.getResultList();
      
		Long total = Long.valueOf(list.get(0).toString());
      
		query = createQuery(qlString, parameter); 
		query.setFirstResult((int)page.getOffset());
		query.setMaxResults(page.getPageSize());
      
        List<E> content = total > page.getOffset() ? query.getResultList() : Collections.<E>emptyList();

        return new PageImpl<E>(content, page, total);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
	public <E> List<E> find(String qlString){
		return find(qlString, null);
	}
    
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	@Override
	public <E> List<E> find(String qlString, Parameter parameter){
		Query query = createQuery(qlString, parameter);
		return query.getResultList();
	}
	
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll(){
		//return getEntityManager().createCriteria(persistentClass).list();
		try {
			return findAll(persistentClass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Lists.newArrayList();
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public T getByHql(String qlString){
		return getByHql(qlString, null);
	}
	
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	@Override
	public T getByHql(String qlString, Parameter parameter){
		log.debug("SimpleBaseRepository getByHql: " + qlString + "; Parameter: " + parameter.toString());
		
		Query query = createQuery(qlString, parameter);
		query.setMaxResults(1);
		
		List<T> list = query.getResultList();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
        	log.warn("no record found.");
        	
            return null;
        }
	}
    
    // -------------- Criteria --------------
	
    /**
     * {@inheritDoc}
     */
	@Override
 	public Page<T> find(Pageable page) {
 		return find(page, createCriteriaQuery());
 	}
 	

 	/**
     * {@inheritDoc}
     */
	@Override
 	public Page<T> find(Pageable page, CriteriaQuery criteriaQuery) {
 		Long total = count(criteriaQuery);
        
 		Query criteria = getEntityManager().createQuery(criteriaQuery);
        criteria.setFirstResult((int)page.getOffset());
        criteria.setMaxResults(page.getPageSize());
        
        List<T> content = total > page.getOffset() ? criteria.getResultList() : Collections.<T>emptyList();

        return new PageImpl<T>(content, page, total);        
 	}
 	
 	

 	/**
     * {@inheritDoc}
     */
 	@SuppressWarnings("unchecked")
 	@Override
 	public List<T> find(CriteriaQuery criteriaQuery) {
 		Query query = getEntityManager().createQuery(criteriaQuery);
 		
 		return query.getResultList(); 
 	}
 	 	
 	/**
     * {@inheritDoc}
     */
 	@SuppressWarnings("rawtypes")
 	@Override
 	public long count(CriteriaQuery criteriaQuery) {
 		return JpaUtils.count(getEntityManager(), criteriaQuery);
 	}
        
    /**
     * {@inheritDoc}
     */
 	@Override
	public CriteriaQuery createCriteriaQuery(Predicate... predicates) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(this.persistentClass);
        Root from = criteriaQuery.from(this.persistentClass);
        criteriaQuery.select(from);
		criteriaQuery.where(predicates);

		return criteriaQuery;
	}
	
	@Override
	public Page<T> find(final Searchable searchable) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		if (searchable.hasPageable()) {
			CriteriaQuery dc = JpaUtils.prepareQL(builder, this.persistentClass, searchable, null);
			if (dc == null) {
				return find(searchable.getPage());
			}else {
				return find(searchable.getPage(), dc);
			}
		}else{
			List<T> list = find(JpaUtils.prepareQL(builder, this.persistentClass, searchable, null));
			
	        return new PageImpl<T>(
	                list,
	                searchable.getPage(),
	                list.size()
	        );
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T findOne(final Searchable searchable) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();	
		CriteriaQuery dc = (searchable== null ? createCriteriaQuery() : JpaUtils.prepareQL(builder, this.persistentClass, searchable, null));
		Query criteria = getEntityManager().createQuery(dc);
		
		criteria.setMaxResults(1);
		List<T> list = criteria.getResultList();
		if (list.isEmpty())
			return null;
		
		return list.get(0);
	}
	
	@Override
	public boolean findAny(Searchable searchable) {
		return (count(searchable) > 0);
	}
	
	@Override
	public List<T> findAll(final Searchable searchable) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		return find(JpaUtils.prepareQL(builder, this.persistentClass, searchable, null));
	}
	
	@Override
	public long count(final Searchable searchable) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery criteria = (searchable== null ? createCriteriaQuery() : JpaUtils.prepareQL(builder, this.persistentClass, searchable, null));
		
		return JpaUtils.count(getEntityManager(), criteria);
	}
	
	/**
     * delete by Primary Keys
     *
     * @param ids
     */
	@Transactional
    public void remove(PK[] ids){
    	if (!AbstractEntity.class.isAssignableFrom(this.persistentClass))
    		return;
    	
    	if (ArrayUtils.isEmpty(ids)) {
            return;
        }
        List<T> models = find(ids);
        remove(models);
    }
    
    /**
     * delete by entities
     *
     * @param entities
     */
    @Transactional
    public void remove(final Iterable<T> entities){
        if (entities != null) {
        	Iterator<T> iter = entities.iterator();
        	if (iter.hasNext()){
            	for (T object : entities) {
            		remove(object);
            	}
        	}
        }
    }
    
        
    /**
     * find by Primary Keys
     *
     * @param ids
     * @throws SQLException 
     */
    public List<T> find(PK[] ids){
    	SingularAttribute<? super T, ?> id = JpaUtils.getIdAttribute(getEntityManager(), this.persistentClass);
    	CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery criteriaQuery = builder.createQuery(this.persistentClass);
    	Root from = criteriaQuery.from(this.persistentClass);
    	
    	//from.get(id).in(ids);
    	
    	criteriaQuery.where(from.get(id).in(ids));
    	criteriaQuery.select(from);
    	
    	Query query = getEntityManager().createQuery(criteriaQuery);
    	
        return query.getResultList();
       
    }
           
    @SuppressWarnings("unchecked")
	public List<PK> getPrimaryKeys(Searchable searchable){
    	if (!AbstractEntity.class.isAssignableFrom(this.persistentClass))
    		return null;
    	
    	SingularAttribute<? super T, ?> id = JpaUtils.getIdAttribute(getEntityManager(), this.persistentClass);

    	CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery criteriaQuery = null;
		if (searchable == null){
	    	criteriaQuery = builder.createQuery(this.persistentClass);
	    	Root from = criteriaQuery.from(this.persistentClass);
	        criteriaQuery.select(from.get(id.getName()));
		}else{
			criteriaQuery = JpaUtils.prepareQL(builder, this.persistentClass, searchable, id.getName());
		}
		// Get id list
		TypedQuery<PK> query = getEntityManager().createQuery(criteriaQuery);
		
		return query.getResultList();
    }
    
    public long getCacheSize() throws SQLException {
        return getEntityManager().unwrap(Session.class).getStatistics().getEntityCount();
    }
   
    @SuppressWarnings("unchecked")
    public <E extends ReloadableEntity> E reloadEntity(final E entity) throws SQLException {
        if (entity == null) {
            return null;
        } else if (getEntityManager().contains(entity)) {
            return entity;
        } else {
            return (E) getEntityManager().find(HibernateProxyHelper.getClassWithoutInitializingProxy(entity), entity.getId());
        }
    }
    
    @Override
    public T create(T t) throws SQLException {
        getEntityManager().persist(t);
        return t;
    }

	/*
	 * @Override public void save(T t) throws SQLException { //Isn't required, is
	 * just here for other DB implementation. Hibernate auto keeps track of changes.
	 * }
	 */

   /**
    * The Session used to manipulate entities of this type.
    *
    * @param context current DSpace context.
    * @return the current Session.
    * @throws SQLException
    */
    protected Session getHibernateSession() throws SQLException {
        return getEntityManager().unwrap(Session.class);
    }

    @Override
    public void delete(T t) throws SQLException {
        getEntityManager().remove(t);
    }

    @Override
    public List<T> findAll(Class<T> clazz) throws SQLException {
        return findAll(clazz, -1, -1);
    }

    @Override
    public List<T> findAll(Class<T> clazz, Integer limit, Integer offset) throws SQLException {
        CriteriaQuery criteriaQuery = getCriteriaQuery(getCriteriaBuilder(), clazz);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);
        return executeCriteriaQuery(criteriaQuery, false, limit, offset);
    }

    @Override
    public T findUnique(String query) throws SQLException {
        @SuppressWarnings("unchecked")
        var qry = createQuery(query);
        qry.setMaxResults(1);
		
		List<T> list = qry.getResultList();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
        	log.warn("no record found.");
        	
            return null;
        }
    }

    @Override
    public T findById(Class clazz, UUID id) throws SQLException {
        if (id == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T result = (T) getEntityManager().find(clazz, id);
        return result;
    }

    @Override
    public T findById(Class clazz, int id) throws SQLException {
        @SuppressWarnings("unchecked")
        T result = (T) getEntityManager().find(clazz, id);
        return result;
    }

    @Override
    public List<T> findMany(String query) throws SQLException {
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) createQuery(query).getResultList();
        return result;
    }

    /**
     * Execute a JPA Criteria query and return a collection of results.
     *
     * @param query
     *     JPQL query string
     * @return list of DAOs specified by the query string
     * @throws SQLException if database error
     */
    public List<T> findMany(javax.persistence.Query query) throws SQLException {
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) query.getResultList();
        return result;
    }

    /**
     * Create a parsed query from a query expression.
     *
     * @param query   textual form of the query.
     * @return parsed form of the query.
     * @throws SQLException
     */
    public javax.persistence.Query createQuery(String query) throws SQLException {
        return getEntityManager().createQuery(query);
    }

    /**
     * This method will return a list with unique results, no duplicates, made by the given CriteriaQuery and parameters
     *
     * @param criteriaQuery
     *         The CriteriaQuery for which this list will be retrieved
     * @param cacheable
     *         Whether or not this query should be cacheable
     * @param clazz
     *         The clazz for which this CriteriaQuery will be executed on
     * @param maxResults
     *         The maxmimum amount of results that will be returned for this CriteriaQuery
     * @param offset
     *         The offset to be used for the CriteriaQuery
     * @return A list of distinct results as depicted by the CriteriaQuery and parameters
     * @throws SQLException
     */
    public List<T> list(CriteriaQuery criteriaQuery, boolean cacheable, Class<T> clazz, int maxResults,
                        int offset) throws SQLException {
        criteriaQuery.distinct(true);
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) executeCriteriaQuery(criteriaQuery, cacheable, maxResults, offset);
        return result;
    }

    /**
     * This method will return a list of results for the given CriteriaQuery and parameters
     *
     * @param criteriaQuery
     *         The CriteriaQuery to be used to find the list of results
     * @param cacheable
     *         A boolean value indicating whether this query should be cached or not
     * @param clazz
     *         The class on which the CriteriaQuery will search
     * @param maxResults
     *         The maximum amount of results to be returned
     * @param offset
     *         The offset to be used for the CriteriaQuery
     * @param distinct
     *         A boolean value indicating whether this list should be distinct or not
     * @return A list of results determined by the CriteriaQuery and parameters
     * @throws SQLException
     */
    public List<T> list(CriteriaQuery criteriaQuery, boolean cacheable, Class<T> clazz, int maxResults,
                        int offset, boolean distinct) throws SQLException {
        criteriaQuery.distinct(distinct);
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) executeCriteriaQuery(criteriaQuery, cacheable, maxResults, offset);
        return result;
    }

    /**
     * This method will be used to return a list of results for the given query
     *
     * @param query
     *         The query for which the resulting list will be returned
     * @return The list of results for the given query
     */
    public List<T> list(javax.persistence.Query query) {
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) query.getResultList();
        return result;
    }

    /**
     * Retrieve a unique result from the query.  If multiple results CAN be
     * retrieved an exception will be thrown,
     * so only use when the criteria state uniqueness in the database.
     * @param criteriaQuery JPA criteria
     * @return a DAO specified by the criteria
     */
    public T uniqueResult(CriteriaQuery criteriaQuery, boolean cacheable, Class<T> clazz,
                          int maxResults, int offset) throws SQLException {
        List<T> list = list(criteriaQuery, cacheable, clazz, maxResults, offset);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() == 1) {
                return list.get(0);
            } else {
                throw new IllegalArgumentException("More than one result found");
            }
        } else {
            return null;
        }
    }

    /**
     * Retrieve a single result from the query.  Best used if you expect a
     * single result, but this isn't enforced on the database.
     * @param criteriaQuery JPA criteria
     * @return a DAO specified by the criteria
     */
    public T singleResult(CriteriaQuery criteriaQuery) throws SQLException {
    	javax.persistence.Query query = this.getEntityManager().createQuery(criteriaQuery);
        return singleResult(query);

    }

    /**
     * This method will return the first result from the given query or null if no results were found
     *
     * @param query
     *         The query that is to be executed
     * @return One result from the given query or null if none was found
     */
    public T singleResult(final javax.persistence.Query query) {
        query.setMaxResults(1);
        List<T> list = list(query);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }

    }

    /**
     * This method will return a singular result for the given query
     *
     * @param query
     *         The query for which a single result will be given
     * @return The single result for this query
     */
    public T uniqueResult(javax.persistence.Query query) {
        @SuppressWarnings("unchecked")
        T result = (T) query.getSingleResult();
        return result;
    }

    /**
     * This method will return an Iterator for the given Query
     *
     * @param query
     *         The query for which an Iterator will be made
     * @return The Iterator for the results of this query
     */
    public Iterator<T> iterate(javax.persistence.Query query) {
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query hquery = query.unwrap(org.hibernate.query.Query.class);
        Stream<T> stream = hquery.stream();
        Iterator<T> iter = stream.iterator();
        return new AbstractIterator<T> () {
            @Override
            protected T computeNext() {
                return iter.hasNext() ? iter.next() : endOfData();
            }
            @Override
            public void finalize() {
                stream.close();
            }
        };
    }

    /**
     * This method will return the amount of results that would be generated for this CriteriaQuery as an integer
     *
     * @param criteriaQuery
     *         The CriteriaQuery for which this result will be retrieved
     * @param criteriaBuilder
     *         The CriteriaBuilder that accompagnies the CriteriaQuery
     * @param root
     *         The root that'll determine on which class object we need to calculate the result
     * @return The amount of results that would be found by this CriteriaQuery as an integer value
     * @throws SQLException
     */
    public int count(CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder, Root<T> root)
        throws SQLException {
        return Math.toIntExact(countLong(criteriaQuery, criteriaBuilder, root));
    }

    /**
     * This method will return the count of items for this query as an integer
     * This query needs to already be in a formate that'll return one record that contains the amount
     *
     * @param query
     *         The query for which the amount of results will be returned.
     * @return The amount of results
     */
    public int count(javax.persistence.Query query) {
        return ((Long) query.getSingleResult()).intValue();
    }

    /**
     * This method will return the count of items for this query as a long
     *
     * @param criteriaQuery
     *         The CriteriaQuery for which the amount of results will be retrieved
     * @param criteriaBuilder
     *         The CriteriaBuilder that goes along with this CriteriaQuery
     * @param root
     *         The root created for a DSpace class on which this query will search
     * @return A long value that depicts the amount of results this query has found
     * @throws SQLException
     */
    public long countLong(CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder, Root<T> root)
        throws SQLException {
        Expression<Long> countExpression = criteriaBuilder.countDistinct(root);
        criteriaQuery.select(countExpression);
        return (Long) this.getEntityManager().createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * This method should always be used in order to retrieve the CriteriaQuery in order to
     * start creating a query that has to be executed
     *
     * @param criteriaBuilder
     *         The CriteriaBuilder for which this CriteriaQuery will be constructed
     * @param clazz
     *         The class that this CriteriaQuery will be constructed for
     * @return A CriteriaQuery on which a query can be built
     */
    public CriteriaQuery<T> getCriteriaQuery(CriteriaBuilder criteriaBuilder, Class<T> clazz) {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        return criteriaQuery;
    }

    /**
     * This method should always be used in order to retrieve a CriteriaBuilder for the given context
     *
     * @return A CriteriaBuilder that can be used to create the query
     * @throws SQLException
     */
    public CriteriaBuilder getCriteriaBuilder() throws SQLException {
        return this.getEntityManager().getCriteriaBuilder();
    }

    /**
     * This method will return a list of objects to be returned that match the given criteriaQuery and parameters.
     * The maxResults and offSet can be circumvented by entering the value -1 for them.
     *
     * @param criteriaQuery
     *         The CriteriaQuery that will be used for executing the query
     * @param cacheable
     *         Whether or not this query is able to be cached
     * @param maxResults
     *         The maximum amount of results that this query will return
     *         This can be circumvented by passing along -1 as the value
     * @param offset
     *         The offset to be used in this query
     *         This can be circumvented by passing along -1 as the value
     * @return This will return a list of objects that conform to the made query
     * @throws SQLException
     */
    public List<T> executeCriteriaQuery(CriteriaQuery<T> criteriaQuery, boolean cacheable,
                                        int maxResults, int offset) throws SQLException {
        Query query = this.getEntityManager().createQuery(criteriaQuery);

        query.setHint("org.hibernate.cacheable", cacheable);
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        if (offset != -1) {
            query.setFirstResult(offset);
        }
        return query.getResultList();

    }

    /**
     * This method can be used to construct a query for which there needs to be a bunch of equal properties
     * These properties can be passed along in the equals hashmap
     *
     * @param clazz
     *         The class on which the criteriaQuery will be built
     * @param equals
     *         A hashmap that can be used to store the String representation of the column
     *         and the value that should match that in the DB
     * @param cacheable
     *         A boolean indicating whether this query should be cacheable or not
     * @param maxResults
     *         The max amount of results to be returned by this query
     * @param offset
     *         The offset to be used in this query
     * @return Will return a list of objects that correspond with the constructed query and parameters
     * @throws SQLException
     */
    public List<T> findByX(Class clazz, Map<String, Object> equals, boolean cacheable, int maxResults,
                           int offset) throws SQLException {
        CriteriaBuilder criteriaBuilder = getCriteriaBuilder();
        CriteriaQuery<T> criteria = getCriteriaQuery(criteriaBuilder, clazz);
        Root root = criteria.from(clazz);
        criteria.select(root);

        for (Map.Entry<String, Object> entry : equals.entrySet()) {
            criteria.where(criteriaBuilder.equal(root.get(entry.getKey()), entry.getValue()));
        }
        return executeCriteriaQuery(criteria, cacheable, maxResults, offset);
    }
}
