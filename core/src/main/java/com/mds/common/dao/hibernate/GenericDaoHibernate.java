package com.mds.common.dao.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

import com.mds.common.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.common.dao.GenericDao;
import com.mds.common.exception.SearchException;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.model.search.filter.AndCondition;
import com.mds.common.model.search.filter.Condition;
import com.mds.common.model.search.filter.OrCondition;
import com.mds.common.model.search.filter.SearchFilter;
import com.mds.common.service.impl.TenantContext;
import com.mds.common.utils.Reflections;
import com.mds.core.ReloadableEntity;
import com.mds.common.model.AbstractEntity;
import com.mds.common.model.DataEntity;

//import com.mds.common.model.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.mds.common.model.Parameter;
import com.mds.common.model.TenantSupport;
import com.mds.util.StringUtils;

import org.hibernate.*;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.orm.ObjectRetrievalFailureException;

import javax.annotation.Resource;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.persistence.criteria.Expression;

import com.google.common.collect.AbstractIterator;
import org.hibernate.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
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
 *      &lt;bean id="fooDao" class="com.mds.common.dao.hibernate.GenericDaoHibernate"&gt;
 *          &lt;constructor-arg value="com.mds.model.Foo"/&gt;
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
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private Class<T> persistentClass;
    @Resource
    private SessionFactory sessionFactory;
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
     * Constructor that takes in a class and sessionFactory for easy creation of DAO.
     *
     * @param persistentClass the class type you'd like to persist
     * @param sessionFactory  the pre-configured Hibernate SessionFactory
     */
    public GenericDaoHibernate(final Class<T> persistentClass, SessionFactory sessionFactory) {
        this.persistentClass = persistentClass;
        this.sessionFactory = sessionFactory;
        defaultAnalyzer = new StandardAnalyzer();
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public Session getSession() throws HibernateException {
        Session sess = getSessionFactory().getCurrentSession();
        if (sess == null) {
            sess = getSessionFactory().openSession();
        }
        
        if (TenantSupport.class.isAssignableFrom(this.persistentClass) && TenantContext.getCurrentTenant() != null) {
        	sess.enableFilter("tenantFilter")
	        	.setParameter("tenantId", TenantContext.getCurrentTenant())
	        	.validate();
        }
        	
        return sess;
    }

    @Autowired
    @Required
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        //Session sess = getSession();
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
    public List<T> getAllDistinct() {
        Collection<T> result = new LinkedHashSet<T>(getAll());
        return new ArrayList<T>(result);
    }
    
    public org.apache.lucene.search.Sort prepareOrder(Searchable search) {
    	List<SortField> sortFields = Lists.newArrayList();
        for (Sort.Order order : search.getSort()) {
        	if (order.getDirection() == Direction.ASC)
        		sortFields.add(new SortField(order.getProperty(), SortField.Type.DOC));
        	else
        		sortFields.add(new SortField(order.getProperty(), SortField.Type.SCORE));
        }
        
        return new org.apache.lucene.search.Sort(sortFields.toArray(new SortField[0]));
    }
    
    /**
     * {@inheritDoc}
     */
    public List<T> search(String searchTerm) throws SearchException {
        Session sess = getSession();
        FullTextSession txtSession = Search.getFullTextSession(sess);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, sess, defaultAnalyzer);
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        org.hibernate.search.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        int total = hibQuery.getResultSize();
        
        return hibQuery.list();
    }
       
    /**
     * {@inheritDoc}
     */
    public  Page<T> search(Pageable page, String searchTerm) throws SearchException {
        Session sess = getSession();
        FullTextSession txtSession = Search.getFullTextSession(sess);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, sess, defaultAnalyzer);
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        
        int total = hibQuery.getResultSize();
        List<T> content = Collections.<T>emptyList();
        if (total > page.getOffset())
        {
	        hibQuery.setFirstResult((int)page.getOffset());
	    	hibQuery.setMaxResults(page.getPageSize());
	    	content = hibQuery.list();
        }
      
        return new PageImpl<T>(content, page, total);   
    }
    
    public  Page<T> search(Searchable searchable, String searchTerm) throws SearchException {
        Session sess = getSession();
        FullTextSession txtSession = Search.getFullTextSession(sess);

        org.apache.lucene.search.Query qry;
        try {       			
            qry = HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, sess, defaultAnalyzer);
            if (searchable.hasSearchFilter()) {
            	QueryBuilder querybuilder = txtSession.getSearchFactory()
            	        .buildQueryBuilder()
            	        .forEntity(this.persistentClass )
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
        
        org.hibernate.search.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        
        /*if (searchable.hasSearchFilter()) {
        	hibQuery.setFilter(new CachingWrapperFilter(new QueryWrapperFilter(HibernateSearchTools.generateQuery(searchable))));
        }*/
        
        if (searchable.hashSort()) {
        	hibQuery.setSort(prepareOrder(searchable));
        }
        
        int total = hibQuery.getResultSize();
        List<T> content = Collections.<T>emptyList();
        if (searchable.hasPageable()) {
        	if (total > searchable.getPage().getOffset()){
        		hibQuery.setFirstResult((int)searchable.getPage().getOffset());
    	    	hibQuery.setMaxResults(searchable.getPage().getPageSize());
    	    	content = hibQuery.list();
        	}
        }else {
        	content = hibQuery.list();
        }
      
        return new PageImpl<T>(content, searchable.getPage(), total);   
    }
    
    /**
     * {@inheritDoc}
     */
    public Page<T> search(Pageable page, String[] fnames, String searchTerm) throws SearchException {
        Session sess = getSession();
        FullTextSession txtSession = Search.getFullTextSession(sess);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, fnames, sess, defaultAnalyzer);
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        
        int total = hibQuery.getResultSize();
        List<T> content = Collections.<T>emptyList();
        if (total > page.getOffset())
        {
	        hibQuery.setFirstResult((int)page.getOffset());
	    	hibQuery.setMaxResults(page.getPageSize());
	    	content = hibQuery.list();
        }
      
        return new PageImpl<T>(content, page, total);   
    }
    
    /**
     * {@inheritDoc}
     */
    public Page<T> search(Searchable searchable, String[] fnames, String searchTerm) throws SearchException {
        Session sess = getSession();
        FullTextSession txtSession = Search.getFullTextSession(sess);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.persistentClass, fnames, sess, defaultAnalyzer);
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
        
        org.hibernate.search.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        
        /*if (searchable.hasSearchFilter()) {
        	hibQuery.setFilter(new CachingWrapperFilter(new QueryWrapperFilter(HibernateSearchTools.generateQuery(searchable))));
        }*/
                        
        if (searchable.hashSort()) {
        	hibQuery.setSort(prepareOrder(searchable));
        }
        
        int total = hibQuery.getResultSize();
        List<T> content = Collections.<T>emptyList();
        boolean bGetPage = false;
        if (searchable.hasPageable()) {
	        if (total > searchable.getPage().getOffset()) {
		        hibQuery.setFirstResult((int)searchable.getPage().getOffset());
		    	hibQuery.setMaxResults(searchable.getPage().getPageSize());
		    	content = hibQuery.list();
		    	bGetPage = true;
	        }
        }
        
        if (!bGetPage) {
        	content = hibQuery.list();
        }
              
        return new PageImpl<T>(content, searchable.getPage(), total);   
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T get(PK id) {
        Session sess = getSession();
        IdentifierLoadAccess byId = sess.byId(persistentClass);
        T entity = (T) byId.load(id);

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
    public boolean exists(PK id) {
        Session sess = getSession();
        IdentifierLoadAccess byId = sess.byId(persistentClass);
        T entity = (T) byId.load(id);
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
			for (Method method : object.getClass().getMethods()){
				PrePersist pp = method.getAnnotation(PrePersist.class);
				if (pp != null){
					method.invoke(object);
					break;
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
    public T save(T object) {
        Session sess = getSession();
        return (T) sess.merge(preSave(object));
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T update(T object, final Searchable searchable) {
        List<T> entityList = findAll(searchable);
        if (entityList != null && entityList.size() > 0){
        	T entity = entityList.get(0);
        	
        	Session sess = getSession();
        	sess.update(preUpdate(entity, object));
        	
        	return entity;
        	//return (T) sess.update(preUpdate(object));
        }
        
        return null;
    }
        
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
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
    public void remove(T object) {
        Session sess = getSession();
        sess.delete(object);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(PK id) {
        Session sess = getSession();
        IdentifierLoadAccess byId = sess.byId(persistentClass);
        T entity = (T) byId.load(id);
        sess.delete(entity);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAll(){
    	remove(getAll());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<T> findByNamedQuery(String queryName, Map<String, Object> queryParams) {
        Session sess = getSession();
        Query namedQuery = sess.getNamedQuery(queryName);
        for (String s : queryParams.keySet()) {
            Object val = queryParams.get(s);
            if (val instanceof Collection) {
                namedQuery.setParameterList(s, (Collection) val);
            } else {
                namedQuery.setParameter(s, val);
            }
        }
        return namedQuery.list();
    }

    /**
     * {@inheritDoc}
     */
    public void reindex() {
        HibernateSearchTools.reindex(persistentClass, getSessionFactory().getCurrentSession());
    }


    /**
     * {@inheritDoc}
     */
    public void reindexAll(boolean async) {
        HibernateSearchTools.reindexAll(async, getSessionFactory().getCurrentSession());
    }
    
    /**
     * {@inheritDoc}
     */
	public void flush(){
		getSession().flush();
	}
	
	/**
     * {@inheritDoc}
     */
	public void clear(){ 
		getSession().clear();
	}
	
	/**
     * {@inheritDoc}
     */
	public void setIdentityInsert(boolean bOnOff){
		getSession().createNativeQuery("SET IDENTITY_INSERT "+ this.persistentClass.getSimpleName() + (bOnOff ? " ON" : " OFF"));
	}
	
	/**
     * {@inheritDoc}
     */
	public void save(List<T> entityList){
		if (entityList != null && entityList.size() > 0){
			try {
				Method isNewMethod = entityList.get(0).getClass().getMethod("isNew");
				Method  puMethod = Reflections.getAnnotationMethod(entityList.get(0).getClass(), PreUpdate.class);
				Method  ppMethod = Reflections.getAnnotationMethod(entityList.get(0).getClass(), PrePersist.class);
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
	public int update(String qlString){
		return update(qlString, null);
	}
	
	/**
     * {@inheritDoc}
     */
	public int update(String qlString, Parameter parameter){
		return createQuery(qlString, parameter).executeUpdate();
	}
	
	/**
     * {@inheritDoc}
     */
	public int removeById(PK id){
		return update("update "+persistentClass.getSimpleName()+" set delFlag='" + Constants.DEL_FLAG_DELETE + "' where id = :p1", 
				new Parameter(id));
	}
	
	/**
     * {@inheritDoc}
     */
	public int removeById(PK id, String likeParentIds){
		return update("update "+persistentClass.getSimpleName()+" set delFlag = '" + Constants.DEL_FLAG_DELETE + "' where id = :p1 or parentIds like :p2",
				new Parameter(id, likeParentIds));
	}
	
	/**
     * {@inheritDoc}
     */
	public int updateDelFlag(PK id, String delFlag){
		return update("update "+persistentClass.getSimpleName()+" set delFlag = :p2 where id = :p1", 
				new Parameter(id, delFlag));
	}
    
	/**
     * {@inheritDoc}
     */
	public int updateBySql(String sqlString, Parameter parameter){
		return createSqlQuery(sqlString, parameter).executeUpdate();
	}
	
	/**
     * {@inheritDoc}
     */
	public Query createSqlQuery(String sqlString, Parameter parameter){
		Query query = getSession().createNativeQuery(sqlString);
		setParameter(query, parameter);
		return query;
	}
	
	/**
     * {@inheritDoc}
     */
	public Query createQuery(String qlString, Parameter parameter){
		Query query = getSession().createQuery(qlString);
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
	private void setParameter(Query query, Parameter parameter){
		if (parameter != null) {
            Set<String> keySet = parameter.keySet();
            for (String string : keySet) {
                Object value = parameter.get(string);
                //这里考虑传入的参数是什么类型，不同类型使用的方法不同  
                if(value instanceof Collection<?>){
                    query.setParameterList(string, (Collection<?>)value);
                }else if(value instanceof Object[]){
                    query.setParameterList(string, (Object[])value);
                }else{
                    query.setParameter(string, value);
                }
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
	public <E> Page<E> find(Pageable page, String qlString){
    	return find(page, qlString, null);
    }
    
	/**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	public <E> Page<E> find(Pageable page, String qlString, Parameter parameter){
    	String countQlString = "select count(*) " + removeSelect(removeOrders(qlString));  
//      page.setCount(Long.valueOf(createQuery(countQlString, parameter).uniqueResult().toString()));
        Query query = createQuery(countQlString, parameter);
        List<Object> list = query.list();
      
		Long total = Long.valueOf(list.get(0).toString());
      
		query = createQuery(qlString, parameter); 
		query.setFirstResult((int)page.getOffset());
		query.setMaxResults(page.getPageSize());
      
        List<E> content = total > page.getOffset() ? query.list() : Collections.<E>emptyList();

        return new PageImpl<E>(content, page, total);
    }
    

    /**
     * {@inheritDoc}
     */
	public <E> List<E> find(String qlString){
		return find(qlString, null);
	}
    
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	public <E> List<E> find(String qlString, Parameter parameter){
		Query query = createQuery(qlString, parameter);
		return query.list();
	}
	
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	public List<T> findAll(){
		//return getSession().createCriteria(persistentClass).list();
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
	public T getByHql(String qlString){
		return getByHql(qlString, null);
	}
	
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	public T getByHql(String qlString, Parameter parameter){
		Query query = createQuery(qlString, parameter);
		return (T)query.uniqueResult();
	}
    
    // -------------- Criteria --------------
	
    /**
     * {@inheritDoc}
     */
 	public Page<T> find(Pageable page) {
 		return find(page, createDetachedCriteria());
 	}
 	

 	/**
     * {@inheritDoc}
     */
 	public Page<T> find(Pageable page, DetachedCriteria detachedCriteria) {
 		return find(page, detachedCriteria, Criteria.DISTINCT_ROOT_ENTITY);
 	}
 	
 	/**
     * {@inheritDoc}
     */
 	@SuppressWarnings("unchecked")
 	public Page<T> find(Pageable page, DetachedCriteria detachedCriteria, ResultTransformer resultTransformer) {
 		Long total = count(detachedCriteria);
        
        Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
 		criteria.setResultTransformer(resultTransformer);
        criteria.setFirstResult((int)page.getOffset());
        criteria.setMaxResults(page.getPageSize());
        
        List<T> content = total > page.getOffset() ? criteria.list() : Collections.<T>emptyList();

        return new PageImpl<T>(content, page, total);
        
 		/*// get count
 		if (!page.isDisabled() && !page.isNotCount()){
 			page.setCount(count(detachedCriteria));
 			if (page.getCount() < 1) {
 				return page;
 			}
 		}
 		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
 		criteria.setResultTransformer(resultTransformer);
 		// set page
 		if (!page.isDisabled()){
 	        criteria.setFirstResult(page.getFirstResult());
 	        criteria.setMaxResults(page.getMaxResults()); 
 		}
 		// order by
 		if (StringUtils.isNotBlank(page.getOrderBy())){
 			for (String order : StringUtils.split(page.getOrderBy(), ",")){
 				String[] o = StringUtils.split(order, " ");
 				if (o.length==1){
 					criteria.addOrder(Order.asc(o[0]));
 				}else if (o.length==2){
 					if ("DESC".equals(o[1].toUpperCase())){
 						criteria.addOrder(Order.desc(o[0]));
 					}else{
 						criteria.addOrder(Order.asc(o[0]));
 					}
 				}
 			}
 		}
 		page.setList(criteria.list());
 		return page;*/
 	}

 	/**
     * {@inheritDoc}
     */
 	public List<T> find(DetachedCriteria detachedCriteria) {
 		return find(detachedCriteria, Criteria.DISTINCT_ROOT_ENTITY);
 	}
 	
 	/**
     * {@inheritDoc}
     */
 	@SuppressWarnings("unchecked")
 	public List<T> find(DetachedCriteria detachedCriteria, ResultTransformer resultTransformer) {
 		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
 		criteria.setResultTransformer(resultTransformer);
 		return criteria.list(); 
 	}
 	
 	/**
     * {@inheritDoc}
     */
 	@SuppressWarnings("rawtypes")
 	public long count(DetachedCriteria detachedCriteria) {
 		Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());
 		long totalCount = 0;
 		try {
 			// Get orders
 			Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
 			field.setAccessible(true);
 			List orderEntrys = (List)field.get(criteria);
 			// Remove orders
 			field.set(criteria, new ArrayList());
 			// Get count
 			criteria.setProjection(Projections.rowCount());
 			Object result = criteria.uniqueResult();
			totalCount = 0;
			if (result != null) {
				totalCount = Long.valueOf(result.toString());
			}
 			// Clean count
 			criteria.setProjection(null);
 			// Restore orders
 			field.set(criteria, orderEntrys);
 		} catch (NoSuchFieldException e) {
 			e.printStackTrace();
 		} catch (IllegalAccessException e) {
 			e.printStackTrace();
 		}
 		return totalCount;
 	}
    
    /**
     * {@inheritDoc}
     */
	public DetachedCriteria createDetachedCriteria(Criterion... criterions) {
		DetachedCriteria dc = DetachedCriteria.forClass(persistentClass);
		for (Criterion c : criterions) {
			dc.add(c);
		}
		return dc;
	}
	
	/////////////////////////////////////////////////
	///////////////////searchable filter////////////////////
	/////////////////////////////////////////////////
	
	 public DetachedCriteria prepareQL(Searchable search) {
        if (search == null || (!search.hasSearchFilter() && !search.hashSort())) {
            return null;
        }

        Map<String, DetachedCriteria> aliasAssociations= Maps.newHashMap();
        DetachedCriteria dc = DetachedCriteria.forClass(persistentClass);
        for (SearchFilter searchFilter : search.getSearchFilters()) {

            if (searchFilter instanceof Condition) {
                Condition condition = (Condition) searchFilter;
                if (condition.getOperator() == SearchOperator.custom) {
                    continue;
                }
            }

            Criterion criterion = genCondition(dc, aliasAssociations, searchFilter);
            if (criterion != null){
            	dc.add(criterion);
            }
        }
        prepareOrder(dc, search, aliasAssociations);
        
        return dc;
    }
	 	 
	 public void prepareOrder(DetachedCriteria dc, Searchable search, Map<String, DetachedCriteria> aliasAssociations) {
        if (search.hashSort()) {
            for (Sort.Order order : search.getSort()) {
            	String[] properties = StringUtils.split(order.getProperty(), ".");
            	String orderName=order.getProperty();
            	if (properties.length > 1 && !properties[0].equalsIgnoreCase("parent")) {
            		String associationPath = "";
            		for(int i=0; i<properties.length-1; i++) {
            			associationPath += properties[i];
            			orderName = associationPath.replace('.', '_');
            			createAlias(dc, aliasAssociations, associationPath, orderName);
            			associationPath += ".";
            		}
            		orderName = orderName + "." + properties[properties.length-1];
            	}
            	if (order.getDirection() == Direction.ASC)
            		dc.addOrder(Order.asc(orderName));
            	else
            		dc.addOrder(Order.desc(orderName));
            }
        }
    }
	 
	 private Criterion genCondition(Condition condition) {
		 if (condition.getOperator() == SearchOperator.eq)
			 return Restrictions.eq(condition.getSearchProperty(), condition.getValue());
		 else if (condition.getOperator() == SearchOperator.eqi)
			 return Restrictions.eq(condition.getSearchProperty(), condition.getValue()).ignoreCase();
		 else if (condition.getOperator() == SearchOperator.ne)
			 return Restrictions.ne(condition.getSearchProperty(), condition.getValue());
		 else if (condition.getOperator() == SearchOperator.nei)
			 return Restrictions.ne(condition.getSearchProperty(), condition.getValue()).ignoreCase();
		 else if (condition.getOperator() == SearchOperator.gt)
			 return Restrictions.gt(condition.getSearchProperty(), condition.getValue());
		 else if (condition.getOperator() == SearchOperator.gte)
			 return Restrictions.ge(condition.getSearchProperty(), condition.getValue());
		 else if (condition.getOperator() == SearchOperator.lt)
			 return Restrictions.lt(condition.getSearchProperty(), condition.getValue());
		 else if (condition.getOperator() == SearchOperator.lte)
			 return Restrictions.le(condition.getSearchProperty(), condition.getValue());
		 else if (condition.getOperator() == SearchOperator.bt)
			 return Restrictions.between(condition.getSearchProperty(), condition.getValue(), condition.getValue2());
		 else if (condition.getOperator() == SearchOperator.prefixLike)
			 return Restrictions.like(condition.getSearchProperty(), condition.getValue().toString(), MatchMode.START);
		 else if (condition.getOperator() == SearchOperator.prefixNotLike)
			 return Restrictions.not(Restrictions.eq(condition.getSearchProperty(), condition.getValue()));
		 else if (condition.getOperator() == SearchOperator.suffixLike)
			 return Restrictions.like(condition.getSearchProperty(), condition.getValue().toString(), MatchMode.END);
		 else if (condition.getOperator() == SearchOperator.suffixNotLike)
			 return Restrictions.not(Restrictions.eq(condition.getSearchProperty(), condition.getValue()));
		 else if (condition.getOperator() == SearchOperator.like)
			 return Restrictions.like(condition.getSearchProperty(), condition.getValue());
		 else if (condition.getOperator() == SearchOperator.notLike)
			 return Restrictions.not(Restrictions.like(condition.getSearchProperty(), condition.getValue()));
		 else if (condition.getOperator() == SearchOperator.contain)
			 return Restrictions.like(condition.getSearchProperty(), condition.getValue().toString(), MatchMode.ANYWHERE);
		 else if (condition.getOperator() == SearchOperator.notLike)
			 return Restrictions.not(Restrictions.like(condition.getSearchProperty(), condition.getValue().toString(), MatchMode.ANYWHERE));
		 else if (condition.getOperator() == SearchOperator.isNull)
			 return Restrictions.isNull(condition.getSearchProperty());
		 else if (condition.getOperator() == SearchOperator.isNotNull)
			 return Restrictions.isNotNull(condition.getSearchProperty());
		 else if (condition.getOperator() == SearchOperator.in) {
			 Object value = condition.getValue();
			 if(value instanceof Collection<?>){
				 if (((Collection<?>)value).isEmpty()) {
					 return Restrictions.sqlRestriction("(1=0)");
				 }else {
					 return Restrictions.in(condition.getSearchProperty(), (Collection<?>)value);
				 }
             }else if(value instanceof Object[]){
            	 if (((Object[])value).length == 0) {
					 return Restrictions.sqlRestriction("(1=0)");
				 }else {
					 return Restrictions.in(condition.getSearchProperty(), (Object[])value);
				 }
             }			
		 }
		 else if (condition.getOperator() == SearchOperator.notIn){
			 Object value = condition.getValue();
			 if(value instanceof Collection<?>){
				 if (!((Collection<?>)value).isEmpty()) {
					 return Restrictions.not(Restrictions.in(condition.getSearchProperty(), (Collection<?>)value));
				 }
             }else if(value instanceof Object[]){
            	 if (((Object[])value).length > 0) {
            		 return Restrictions.not(Restrictions.in(condition.getSearchProperty(), (Object[])value));
            	 }
             }			
		 }
		 
		 return Restrictions.eq(condition.getSearchProperty(), condition.getValue());
	 }
	 
	 private DetachedCriteria createAlias(DetachedCriteria dc, Map<String, DetachedCriteria> aliasAssociations, Condition condition){
		 if (!StringUtils.isBlank(condition.getSearchAlias())){
			 DetachedCriteria dcAlias = aliasAssociations.get(condition.getSearchAlias());
     		if (dcAlias == null) {
	         	dcAlias = dc.createAlias(condition.getSearchAlias(), condition.getSearchAlias());
	         	aliasAssociations.put(condition.getSearchAlias(), dcAlias);
     		}
         	
         	return  dcAlias;
         }else {
        	DetachedCriteria dcAlias = dc; 
        	String[] properties = StringUtils.split(condition.getSearchProperty(), ".");
        	String searchProp = condition.getSearchProperty();
         	if (properties.length > 1 && !properties[0].equalsIgnoreCase("parent")) {
         		String associationPath = "";
         		for(int i=0; i<properties.length-1; i++) {
         			associationPath += properties[i];
         			searchProp = associationPath.replace('.', '_');
         			if (!aliasAssociations.containsKey(searchProp)) {
         	         	dcAlias = dcAlias.createAlias(associationPath, searchProp);
         	         	aliasAssociations.put(searchProp, dcAlias);
         			}
         			associationPath += ".";
         		}
         		searchProp = searchProp + "." + properties[properties.length-1];
         		condition.setSearchProperty(searchProp);
         		
         		return dcAlias;
         	}
         	
         	return null;
         }
	 }
	 
	 private DetachedCriteria createAlias(DetachedCriteria dc, Map<String, DetachedCriteria> aliasAssociations, String associationPath, String alias){
		 if (!StringUtils.isBlank(associationPath)) {
         	if (!aliasAssociations.containsKey(associationPath)) {
	         	DetachedCriteria dcAlias = dc.createAlias(associationPath, alias);
	         	aliasAssociations.put(associationPath, dcAlias);
	         	
	         	return  dcAlias;
         	}else {
           	    return aliasAssociations.get(associationPath);
            }
         }
		 
		 return null;
	 }
	 
	 private DetachedCriteria createCollectionCriteria(DetachedCriteria dc, Map<String, DetachedCriteria> aliasAssociations, Condition condition){
 		if ( !aliasAssociations.containsKey(condition.getSearchCollection())) {
         	DetachedCriteria dcCollection = dc.createCriteria(condition.getSearchCollection());
         	aliasAssociations.put(condition.getSearchCollection(), dcCollection);
         	if (!StringUtils.isBlank(condition.getSearchAlias()) 
             		&& !aliasAssociations.containsKey(condition.getSearchAlias())) { //condition.getSearchCollection() + "." + 
             	DetachedCriteria dcAlias = dcCollection.createAlias(condition.getSearchAlias(), condition.getSearchAlias());
             	aliasAssociations.put(condition.getSearchAlias(), dcAlias);
             }
         	
         	return  dcCollection;
 		}else{
 			return aliasAssociations.get(condition.getSearchCollection());
 		}
	 }
	 
	 private Criterion genCriterion(DetachedCriteria dc, Map<String, DetachedCriteria> aliasAssociations, SearchFilter searchFilter) {
		 Condition condition = (Condition) searchFilter;
		 if (!StringUtils.isBlank(condition.getSearchCollection())){
			 createCollectionCriteria(dc, aliasAssociations, condition).add(genCondition(condition));
			 
			 return null;
		 }else{
			 createAlias(dc, aliasAssociations, condition);
			 
			 return genCondition(condition);
		 }
	 }
	 

    private Criterion genCondition(DetachedCriteria dc, Map<String, DetachedCriteria> aliasAssociations, SearchFilter searchFilter) {
        if (searchFilter instanceof Condition) {
        	Criterion cr = genCriterion(dc, aliasAssociations, searchFilter);
        	
        	return cr;
        	/*if (cr != null){
        		dc.add(cr);
        	}*/
        } else if (searchFilter instanceof OrCondition) {
        	Criterion criterion = null;
            boolean isFirst = true;
            for (SearchFilter orSearchFilter : ((OrCondition) searchFilter).getOrFilters()) {
            	Criterion cr = genCondition(dc, aliasAssociations, orSearchFilter);
            	if (cr == null)
            		continue;
            	
                if (isFirst) {
                	criterion = cr;
                }
                else
                	criterion = Restrictions.or(criterion, cr);
                isFirst = false;
            }
            
            return criterion;
            //dc.add(criterion);
        } else if (searchFilter instanceof AndCondition) {
            boolean isFirst = true;
            Criterion criterion = null;
            for (SearchFilter andSearchFilter : ((AndCondition) searchFilter).getAndFilters()) {
            	Criterion cr = genCondition(dc, aliasAssociations, andSearchFilter);
            	if (cr == null)
            		continue;
            	
                if (isFirst) {
                	criterion = cr;
                }
                else
                	criterion = Restrictions.or(criterion, cr);
                isFirst = false;
            }
            
            return criterion;
            //dc.add(criterion);
        }
        
        return null;
    }
	
	@Override
	public Page<T> find(final Searchable searchable) {
		if (searchable.hasPageable()) {
			DetachedCriteria dc = prepareQL(searchable);
			if (dc == null) {
				return find(searchable.getPage());
			}else {
				return find(searchable.getPage(), dc);
			}
		}else{
			List<T> list = find(prepareQL(searchable));
			
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
		DetachedCriteria dc = (searchable== null ? createDetachedCriteria() : prepareQL(searchable));
		Criteria criteria = dc.getExecutableCriteria(getSession());
		criteria.setMaxResults(1);
		List<T> list = criteria.list();
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
		return find(prepareQL(searchable));
	}
	
	@Override
	public long count(final Searchable searchable) {
		Criteria criteria = (searchable == null ? DetachedCriteria.forClass(persistentClass) : prepareQL(searchable)).getExecutableCriteria(getSession());
        criteria.setProjection(Projections.rowCount());
        
        return (Long) criteria.uniqueResult();
	}
	
	/**
     * delete by Primary Keys
     *
     * @param ids
     */
    public void remove(PK[] ids){
    	if (!AbstractEntity.class.isAssignableFrom(this.persistentClass))
    		return;
    	
    	DetachedCriteria dc = DetachedCriteria.forClass(persistentClass);
    	dc.add(Restrictions.in("id", ids));
    	
    	List<T> entities = find(dc);
    	remove(entities);
    }
    
    /**
     * delete by entities
     *
     * @param entities
     */
    public void remove(final Iterable<T> entities){
        if (entities != null) {
        	Iterator<T> iter = entities.iterator();
        	if (iter.hasNext()){
        		Session sess = getSession();
            	for (T object : entities) {
            		sess.delete(object);
            	}
        	}
        }
    }
    
        
    /**
     * find by Primary Keys
     *
     * @param ids
     */
    public List<T> find(PK[] ids){
    	if (!AbstractEntity.class.isAssignableFrom(this.persistentClass))
    		return null;
    	
    	DetachedCriteria dc = DetachedCriteria.forClass(persistentClass);
    	if (ids == null || ids.length == 0)
    		return Lists.newArrayList();
    	
    	dc.add(Restrictions.in("id", ids));
    	
    	return find(dc);
    }
    
    /**
	 * Gets de id attribute from metamodel
	 * @return PK SingularAttribute
	 */
	private SingularAttribute<? super T, ?> getIdAttribute() {
		return getIdAttribute(persistentClass);
	}
	
	/**
	 * @param class1
	 * @return
	 */
	private <K> SingularAttribute<? super K, ?> getIdAttribute(Class<K> clazz) {
		Type<?> type = this.getSession().getMetamodel().entity(clazz).getIdType();
		EntityType<K> entity =  this.getSession().getMetamodel().entity(clazz);
		SingularAttribute<? super K, ?> id = entity.getId(type.getJavaType());
		
		return id;
	}
    
    @SuppressWarnings("unchecked")
	public List<PK> getPrimaryKeys(Searchable searchable){
    	if (!AbstractEntity.class.isAssignableFrom(this.persistentClass))
    		return null;
    	
    	SingularAttribute<? super T, ?> id = getIdAttribute();

    	CriteriaBuilder builder = getSession().getCriteriaBuilder();
    	CriteriaQuery criteriaQuery = null;
		if (searchable == null){
	    	criteriaQuery = builder.createQuery(persistentClass);
	    	Root from = criteriaQuery.from(persistentClass);
	        criteriaQuery.select(from.get(id.getName()));
		}else{
			criteriaQuery = JpaUtils.prepareQL(builder, persistentClass, searchable, id.getName());
		}
		// Get id list
		TypedQuery<PK> query = getSession().createQuery(criteriaQuery);
		
		return query.getResultList();
    }
    
    public long getCacheSize() throws SQLException {
        return getSession().getStatistics().getEntityCount();
    }
    
    
    /**
     * Create criteria matching an entity type or a supertype thereof.
     * Use when building a criteria query.
     *
     * @param context         current DSpace context.
     * @param persistentClass specifies the type to be matched by the criteria.
     * @return criteria concerning the type to be found.
     * @throws SQLException passed through.
     */
    public Criteria createCriteria(Class<T> persistentClass) throws SQLException {
        return getSession().createCriteria(persistentClass);
    }

    /**
     * Create criteria matching an entity type or a supertype thereof.
     * Use when building a criteria query.
     *
     * @param context         current DSpace context.
     * @param persistentClass specifies the type to be matched by the criteria.
     * @param alias           alias for the type.
     * @return criteria concerning the type to be found.
     * @throws SQLException passed through.
     */
    public Criteria createCriteria(Class<T> persistentClass, String alias) throws SQLException {
        return getSession().createCriteria(persistentClass, alias);
    }

    /**
     * Get the entities matched by the given Criteria.
     * Use this if you need all results together.
     *
     * @param criteria description of desired entities.
     * @return the entities matched.
     */
    public List<T> list(Criteria criteria) {
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) criteria.list();
        return result;
    }

    /**
     * Get the entities matching a given parsed query.
     * Use this if you need all results together.
     *
     * @param query the query to be executed.
     * @return entities matching the query.
     */
    public List<T> list(org.hibernate.query.Query query) {
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) query.list();
        return result;
    }
    
    /**
     * Retrieve a single result selected by criteria.  Best used if you expect a
     * single result, but this isn't enforced on the database.
     *
     * @param criteria description of the desired entities.
     * @return a DAO specified by the criteria
     */
    public T singleResult(Criteria criteria) {
        criteria.setMaxResults(1);
        List<T> list = criteria.list();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Retrieve a single result matching a query.  Best used if you expect a
     * single result, but this isn't enforced on the database.
     *
     * @param query description of desired entities.
     * @return matched entities.
     */
    public T singleResult(final org.hibernate.query.Query query) {
        query.setMaxResults(1);
        List<T> list = query.list();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * How many rows match these criteria?
     * The same value as {@link countLong(Criteria)}, coerced to {@code int}.
     *
     * @param criteria description of the rows.
     * @return count of matching rows.
     */
    public int count(Criteria criteria) {
        return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    /**
     * How many rows match this query?
     *
     * @param query description of the rows.
     * @return count of matching rows.
     */
    @SuppressWarnings({ "rawtypes"})
	public int count(org.hibernate.query.Query query) {
        return ((Long) query.uniqueResult()).intValue();
    }

    /**
     * How many rows match these criteria?
     *
     * @param criteria description of the rows.
     * @return count of matching rows.
     */
    public long countLong(Criteria criteria) {
        return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public <E extends ReloadableEntity> E reloadEntity(final E entity) throws SQLException {
        if (entity == null) {
            return null;
        } else if (getSession().contains(entity)) {
            return entity;
        } else {
            return (E) getSession().get(HibernateProxyHelper.getClassWithoutInitializingProxy(entity), entity.getId());
        }
    }
    
    @Override
    public T create(T t) throws SQLException {
        getHibernateSession().persist(t);
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
        return getSession();
    }

    @Override
    public void delete(T t) throws SQLException {
        getHibernateSession().delete(t);
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
        T result = (T) createQuery(query).getSingleResult();
        return result;
    }

    @Override
    public T findById(Class clazz, UUID id) throws SQLException {
        if (id == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T result = (T) getHibernateSession().get(clazz, id);
        return result;
    }

    @Override
    public T findById(Class clazz, int id) throws SQLException {
        @SuppressWarnings("unchecked")
        T result = (T) getHibernateSession().get(clazz, id);
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
        return getHibernateSession().createQuery(query);
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
    	javax.persistence.Query query = this.getHibernateSession().createQuery(criteriaQuery);
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
        return (Long) this.getHibernateSession().createQuery(criteriaQuery).getSingleResult();
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
        return this.getHibernateSession().getCriteriaBuilder();
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
        Query query = this.getHibernateSession().createQuery(criteriaQuery);

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
