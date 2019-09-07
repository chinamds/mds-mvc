package com.mds.common.dao.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.QueryWrapperFilter;
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
        Session sess = getSession();
        return sess.createCriteria(persistentClass).list();
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
    
    public org.hibernate.search.FullTextQuery applyFilters(org.hibernate.search.FullTextQuery hibQuery, Searchable search) {
        for (SearchFilter searchFilter : search.getSearchFilters()) {

            if (searchFilter instanceof Condition) {
                Condition condition = (Condition) searchFilter;
                if (condition.getOperator() == SearchOperator.ftqFilter) {
                	hibQuery.enableFullTextFilter(condition.getValue2().toString()).setParameter(condition.getSearchProperty(), condition.getValue());
                }
            }
        }
        
        return hibQuery;
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
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
        
        if (searchable.hasSearchFilter()) {
        	hibQuery = applyFilters(hibQuery, searchable);
        }
        
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
            	BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
            	bQuery.add(qry, Occur.MUST);
            	bQuery.add(HibernateSearchTools.generateQuery(searchable), Occur.FILTER);
            	qry = bQuery.build();
            }
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.FullTextQuery hibQuery = txtSession.createFullTextQuery(qry,
                this.persistentClass);
                
        if (searchable.hashSort()) {
        	hibQuery.setSort(prepareOrder(searchable));
        }
        
        int total = hibQuery.getResultSize();
        List<T> content = Collections.<T>emptyList();
        if (searchable.hasPageable()) {
	        if (total > searchable.getPage().getOffset())
	        {
		        hibQuery.setFirstResult((int)searchable.getPage().getOffset());
		    	hibQuery.setMaxResults(searchable.getPage().getPageSize());
		    	content = hibQuery.list();
	        }
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
    @Override
    public T create(T t) {
    	getSession().persist(t);
    	
        return t;
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
      
		/*// get count
    	if (!page.isDisabled() && !page.isNotCount()){
	        String countQlString = "select count(*) " + removeSelect(removeOrders(qlString));  
//	        page.setCount(Long.valueOf(createQuery(countQlString, parameter).uniqueResult().toString()));
	        Query query = createQuery(countQlString, parameter);
	        List<Object> list = query.list();
	        if (list.size() > 0){
	        	page.setCount(Long.valueOf(list.get(0).toString()));
	        }else{
	        	page.setCount(list.size());
	        }
			if (page.getCount() < 1) {
				return page;
			}
    	}
    	// order by
    	String ql = qlString;
		if (StringUtils.isNotBlank(page.getOrderBy())){
			ql += " order by " + page.getOrderBy();
		}
        Query query = createQuery(ql, parameter); 
    	// set page
        if (!page.isDisabled()){
	        query.setFirstResult(page.getFirstResult());
	        query.setMaxResults(page.getMaxResults()); 
        }
        page.setList(query.list());
		return page;*/
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
		return getSession().createCriteria(persistentClass).list();
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
    
    @SuppressWarnings("unchecked")
	public List<PK> getPrimaryKeys(Searchable searchable){
    	if (!AbstractEntity.class.isAssignableFrom(this.persistentClass))
    		return null;

    	List<PK> lst = null;
		Criteria criteria = null;
		Session sess = getSession();
		if (searchable == null){
			criteria = sess.createCriteria(persistentClass);
		}else{
    		DetachedCriteria detachedCriteria = prepareQL(searchable);
    		criteria = detachedCriteria.getExecutableCriteria(sess);
		}
		// Get id list
		criteria.setProjection(Projections.id());
		lst = criteria.list();
		// Clean id list
		criteria.setProjection(null);
  	    	
    	return lst;
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
     * Create a parsed query from a query expression.
     *
     * @param context current DSpace context.
     * @param query   textual form of the query.
     * @return parsed form of the query.
     * @throws SQLException
     */
    public Query createQuery(String query) throws SQLException {
        return getSession().createQuery(query);
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
    public List<T> list(Query query) {
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
    public T singleResult(final Query query) {
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
	public int count(Query query) {
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
}
