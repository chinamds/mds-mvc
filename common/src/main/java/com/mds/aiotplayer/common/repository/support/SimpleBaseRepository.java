/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.repository.support;

import com.google.common.collect.Sets;
import com.mds.aiotplayer.common.repository.hibernate.HibernateSearchTools;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.common.exception.SearchException;
import com.mds.aiotplayer.common.model.AbstractEntity;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.plugin.entity.LogicDeleteable;
import com.mds.aiotplayer.common.repository.BaseRepository;
import com.mds.aiotplayer.common.repository.RepositoryHelper;
import com.mds.aiotplayer.common.repository.callback.SearchCallback;
import com.mds.aiotplayer.common.repository.support.annotation.QueryJoin;
import com.mds.aiotplayer.common.utils.Reflections;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.FlushMode;
import org.hibernate.query.NativeQuery;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 * <p>抽象基础Custom Repository 实现</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-15 下午7:33
 * <p>Version: 1.0
 * @author John Lee (update: added full text search + reindexing)
 */
public class SimpleBaseRepository<M, ID extends Serializable> extends SimpleJpaRepository<M, ID>
        implements BaseRepository<M, ID> {
	
    /**
     * Log variable for all child classes. Uses LogFactory.getLog(getClass()) from Commons Logging
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public static final String LOGIC_DELETE_ALL_QUERY_STRING = "update %s x set x.deleted=true where x in (?1)";
    public static final String DELETE_ALL_QUERY_STRING = "delete from %s x where x in (?1)";
    public static final String FIND_QUERY_STRING = "from %s x where 1=1 ";
    public static final String COUNT_QUERY_STRING = "select count(x) from %s x where 1=1 ";

    private final EntityManager em;
    private final JpaEntityInformation<M, ID> entityInformation;

    private final RepositoryHelper repositoryHelper;

    private CrudMethodMetadata crudMethodMetadata;

    private Class<M> entityClass;
    private String entityName;
    private String idName;
    private boolean readOnlyEnabled = false;
    
    //full text search Analyzer
    private Analyzer defaultAnalyzer;


    /**
     * 查询所有的QL
     */
    private String findAllQL;
    /**
     * 统计QL
     */
    private String countAllQL;

    private QueryJoin[] joins;

    private SearchCallback searchCallback = SearchCallback.DEFAULT;

    public SimpleBaseRepository(JpaEntityInformation<M, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityInformation = entityInformation;
        this.entityClass = this.entityInformation.getJavaType();
        this.entityName = this.entityInformation.getEntityName();
        this.idName = this.entityInformation.getIdAttributeNames().iterator().next();
        this.em = entityManager;

        repositoryHelper = new RepositoryHelper(entityClass);

        findAllQL = String.format(FIND_QUERY_STRING, entityName);
        countAllQL = String.format(COUNT_QUERY_STRING, entityName);
        
        defaultAnalyzer = new StandardAnalyzer();
    }
    
    protected Class<M> getEntityClass() {
		return entityClass;
	}
    
    protected EntityManager getEntityManager() {
		return this.em;
	}

    /**
     * Configures a custom {@link org.springframework.data.jpa.repository.support.LockMetadataProvider} to be used to detect {@link javax.persistence.LockModeType}s to be applied to
     * queries.
     *
     * @param lockMetadataProvider
     */
    public void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata) {
        super.setRepositoryMethodMetadata(crudMethodMetadata);
        this.crudMethodMetadata = crudMethodMetadata;
    }

    /**
     * 设置searchCallback
     *
     * @param searchCallback
     */
    public void setSearchCallback(SearchCallback searchCallback) {
        this.searchCallback = searchCallback;
    }

    /**
     * 设置查询所有的ql
     *
     * @param findAllQL
     */
    public void setFindAllQL(String findAllQL) {
        this.findAllQL = findAllQL;
    }

    /**
     * 设置统计的ql
     *
     * @param countAllQL
     */
    public void setCountAllQL(String countAllQL) {
        this.countAllQL = countAllQL;
    }

    public void setJoins(QueryJoin[] joins) {
        this.joins = joins;
    }
    
    @Override
    public boolean isReadOnlyEnabled() {
        return readOnlyEnabled;
    }
    
    @Override
    public void setConnectionMode(final boolean readOnlyOptimized) throws SQLException {
        this.readOnlyEnabled = readOnlyOptimized;
        configureDatabaseMode();
    }

    private void configureDatabaseMode() throws SQLException {
    	if (readOnlyEnabled) {
    		em.setFlushMode(FlushModeType.COMMIT);
        } else {
        	em.setFlushMode(FlushModeType.AUTO);
        }
    }

    /////////////////////////////////////////////////
    ////////覆盖默认spring data jpa的实现////////////
    /////////////////////////////////////////////////

    /**
     * 根据主键删除相应实体
     *
     * @param id 主键
     */
    @Transactional
    @Override
    public void delete(final ID id) {
        M m = getOne(id);
        delete(m);
    }

    /**
     * 删除实体
     *
     * @param m 实体
     */
    @Transactional
    @Override
    public void delete(final M m) {
        if (m == null) {
            return;
        }
        if (m instanceof LogicDeleteable) {
            ((LogicDeleteable) m).markDeleted();
            save(m);
        } else {
            super.delete(m);
        }
    }


    /**
     * 根据主键删除相应实体
     *
     * @param ids 实体
     */
    @Transactional
    @Override
    public void delete(final ID[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return;
        }
        List<M> models = new ArrayList<M>();
        for (ID id : ids) {
            M model = null;
            try {
                model = entityClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("batch delete " + entityClass + " error", e);
            }
            try {
                BeanUtils.setProperty(model, idName, id);
            } catch (Exception e) {
                throw new RuntimeException("batch delete " + entityClass + " error, can not set id", e);
            }
            models.add(model);
        }
        deleteInBatch(models);
    }

    @Transactional
    @Override
    public void deleteInBatch(final Iterable<M> entities) {
        Iterator<M> iter = entities.iterator();
        if (entities == null || !iter.hasNext()) {
            return;
        }

        Set models = Sets.newHashSet(iter);

        boolean logicDeleteableEntity = LogicDeleteable.class.isAssignableFrom(this.entityClass);

        if (logicDeleteableEntity) {
            String ql = String.format(LOGIC_DELETE_ALL_QUERY_STRING, entityName);
            repositoryHelper.batchUpdate(ql, models);
        } else {
            String ql = String.format(DELETE_ALL_QUERY_STRING, entityName);
            repositoryHelper.batchUpdate(ql, models);
        }
    }

    /**
     * 按照主键查询
     *
     * @param id 主键
     * @return 返回id对应的实体
     */
    @Transactional
    @Override
    public M findOne(ID id) {
        if (id == null) {
            return null;
        }
        if (id instanceof Integer && ((Integer) id).intValue() == 0) {
            return null;
        }
        if (id instanceof Long && ((Long) id).longValue() == 0L) {
            return null;
        }
        Optional<M> m = super.findById(id);
        return m.isPresent() ? m.get() : null;
    }


/*    ////////根据Specification查询 直接从SimpleJpaRepository复制过来的///////////////////////////////////
    @Override
    public Optional<M> findOne(Specification<M> spec) {
        try {
            //return getQuery(spec, (Sort) null).getSingleResult();
        	return findOne(spec);
        } catch (NoResultException e) {
            return null;
        }
    }*/


    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll(ID[])
     */
    public List<M> findAll(Iterable<ID> ids) {

        return getQuery(new Specification<M>() {
            public Predicate toPredicate(Root<M> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path<?> path = root.get(entityInformation.getIdAttribute());
                return path.in(cb.parameter(Iterable.class, "ids"));
            }
        }, (Sort) null).setParameter("ids", ids).getResultList();
    }


    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification)
     */
    public List<M> findAll(Specification<M> spec) {
        return getQuery(spec, (Sort) null).getResultList();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Pageable)
     */
    public Page<M> findAll(Specification<M> spec, Pageable pageable) {

        TypedQuery<M> query = getQuery(spec, pageable);
        return pageable == null ? new PageImpl<M>(query.getResultList()) : readPage(query, pageable, spec);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Sort)
     */
    public List<M> findAll(Specification<M> spec, Sort sort) {

        return getQuery(spec, sort).getResultList();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#count(org.springframework.data.jpa.domain.Specification)
     */
    public long count(Specification<M> spec) {

        return getCountQuery(spec).getSingleResult();
    }
    ////////根据Specification查询 直接从SimpleJpaRepository复制过来的///////////////////////////////////


    ///////直接从SimpleJpaRepository复制过来的///////////////////////////////

    /**
     * Reads the given {@link javax.persistence.TypedQuery} into a {@link org.springframework.data.domain.Page} applying the given {@link org.springframework.data.domain.Pageable} and
     * {@link org.springframework.data.jpa.domain.Specification}.
     *
     * @param query    must not be {@literal null}.
     * @param spec     can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     */
    protected Page<M> readPage(TypedQuery<M> query, Pageable pageable, Specification<M> spec) {

        query.setFirstResult((int)pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long total = count();//SimpleJpaRepository.executeCountQuery(getCountQuery(spec));
        List<M> content = total > pageable.getOffset() ? query.getResultList() : Collections.<M>emptyList();

        return new PageImpl<M>(content, pageable, total);
    }

    /**
     * Creates a new count query for the given {@link org.springframework.data.jpa.domain.Specification}.
     *
     * @param spec can be {@literal null}.
     * @return
     */
    protected TypedQuery<Long> getCountQuery(Specification<M> spec) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);


        Root<M> root = applySpecificationToCriteria(spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        TypedQuery<Long> q = em.createQuery(query);
        repositoryHelper.applyEnableQueryCache(q);
        return q;
    }

    /**
     * Creates a new {@link javax.persistence.TypedQuery} from the given {@link org.springframework.data.jpa.domain.Specification}.
     *
     * @param spec     can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     */
    protected TypedQuery<M> getQuery(Specification<M> spec, Pageable pageable) {

        Sort sort = pageable == null ? null : pageable.getSort();
        return getQuery(spec, sort);
    }

    /**
     * Creates a {@link javax.persistence.TypedQuery} for the given {@link org.springframework.data.jpa.domain.Specification} and {@link org.springframework.data.domain.Sort}.
     *
     * @param spec can be {@literal null}.
     * @param sort can be {@literal null}.
     * @return
     */
    protected TypedQuery<M> getQuery(Specification<M> spec, Sort sort) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<M> query = builder.createQuery(entityClass);

        Root<M> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        applyJoins(root);

        if (sort != null) {
            query.orderBy(toOrders(sort, root, builder));
        }

        TypedQuery<M> q = em.createQuery(query);

        repositoryHelper.applyEnableQueryCache(q);

        return applyLockMode(q);
    }
    
    protected CriteriaQuery prepareQL(Searchable search, String section) {
    	return repositoryHelper.prepareQL(search, section);
    }

    private void applyJoins(Root<M> root) {
        if(joins == null) {
            return;
        }

        for(QueryJoin join : joins) {
            root.join(join.property(), join.joinType());
        }
    }


    /**
     * Applies the given {@link org.springframework.data.jpa.domain.Specification} to the given {@link javax.persistence.criteria.CriteriaQuery}.
     *
     * @param spec  can be {@literal null}.
     * @param query must not be {@literal null}.
     * @return
     */
    private <S> Root<M> applySpecificationToCriteria(Specification<M> spec, CriteriaQuery<S> query) {

        Assert.notNull(query);
        Root<M> root = query.from(entityClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    private TypedQuery<M> applyLockMode(TypedQuery<M> query) {
        LockModeType type = crudMethodMetadata == null ? null : crudMethodMetadata.getLockModeType();
        return type == null ? query : query.setLockMode(type);
    }
    ///////直接从SimpleJpaRepository复制过来的///////////////////////////////


    @Override
    public List<M> findAll() {
        return repositoryHelper.findAll(findAllQL);
    }

    @Override
    public List<M> findAll(final Sort sort) {
        return repositoryHelper.findAll(findAllQL, sort);
    }

    @Override
    public Page<M> findAll(final Pageable pageable) {
        return new PageImpl<M>(
                repositoryHelper.<M>findAll(findAllQL, pageable),
                pageable,
                repositoryHelper.count(countAllQL)
        );
    }

    @Override
    public long count() {
        return repositoryHelper.count(countAllQL);
    }


    /////////////////////////////////////////////////
    ///////////////////自定义实现////////////////////
    /////////////////////////////////////////////////

    @Override
    public Page<M> findBySearchable(final Searchable searchable) {
        List<M> list = repositoryHelper.findAll(findAllQL, searchable, searchCallback);
        long total = searchable.hasPageable() ? count(searchable) : list.size();
        return new PageImpl<M>(
                list,
                searchable.hasPageable() ? searchable.getPage() : Pageable.unpaged(),
                total
        );
    }

    @Override
    public long count(final Searchable searchable) {
        return repositoryHelper.count(countAllQL, searchable, searchCallback);
    }

    /**
     * 重写默认的 这样可以走一级/二级缓存
     *
     * @param id
     * @return
     */
    @Override
    public boolean existsById(ID id) {
        return findOne(id) != null;
    }

    
    /**
     * {@inheritDoc}
     */
    public List<M> search(String searchTerm) throws SearchException {
    	FullTextEntityManager sess = Search.getFullTextEntityManager(em);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.entityClass, sess, defaultAnalyzer);
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        org.hibernate.search.jpa.FullTextQuery hibQuery = sess.createFullTextQuery(qry,
                this.entityClass);
        
        return hibQuery.getResultList();
    }
       
    /**
     * {@inheritDoc}
     */
    public  Page<M> search(Pageable page, String searchTerm) throws SearchException {
    	FullTextEntityManager sess = Search.getFullTextEntityManager(em);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.entityClass, sess, defaultAnalyzer);
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.jpa.FullTextQuery hibQuery = sess.createFullTextQuery(qry,
                this.entityClass);
        
        int total = hibQuery.getResultSize();
        List<M> content = Collections.<M>emptyList();
        if (total > page.getOffset())
        {
	        hibQuery.setFirstResult((int)page.getOffset());
	    	hibQuery.setMaxResults(page.getPageSize());
	    	content = hibQuery.getResultList();
        }
      
        return new PageImpl<M>(content, page, total);   
    }
    
    public  Page<M> search(Searchable searchable, String searchTerm) throws SearchException {
        FullTextEntityManager sess = Search.getFullTextEntityManager(em);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.entityClass, sess, defaultAnalyzer);
            
            QueryBuilder querybuilder = sess.getSearchFactory()
        	        .buildQueryBuilder()
        	        .forEntity(this.entityClass )
        	        .get();
        	
			  qry = HibernateSearchTools.generateQuery(querybuilder, searchable).must(qry).createQuery();
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.jpa.FullTextQuery hibQuery = sess.createFullTextQuery(qry,
                this.entityClass);
        
        if (searchable.hashSort()) {
        	hibQuery.setSort(HibernateSearchTools.prepareOrder(searchable, this.entityClass));
        }
        
        int total = hibQuery.getResultSize();
        List<M> content = Collections.<M>emptyList();
        if (searchable.hasPageable()) {
        	if (total > searchable.getPage().getOffset()){
        		hibQuery.setFirstResult((int)searchable.getPage().getOffset());
    	    	hibQuery.setMaxResults(searchable.getPage().getPageSize());
    	    	content = hibQuery.getResultList();
        	}
        }else {
        	content = hibQuery.getResultList();
        }
      
        return new PageImpl<M>(content, searchable.getPage(), total);   
    }
    
    /**
     * {@inheritDoc}
     */
    public Page<M> search(Pageable page, String[] fnames, String searchTerm) throws SearchException {
    	FullTextEntityManager sess = Search.getFullTextEntityManager(em);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.entityClass, fnames, sess, defaultAnalyzer);
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.jpa.FullTextQuery hibQuery = sess.createFullTextQuery(qry,
                this.entityClass);
        
        int total = hibQuery.getResultSize();
        List<M> content = Collections.<M>emptyList();
        if (total > page.getOffset())
        {
	        hibQuery.setFirstResult((int)page.getOffset());
	    	hibQuery.setMaxResults(page.getPageSize());
	    	content = hibQuery.getResultList();
        }
      
        return new PageImpl<M>(content, page, total);   
    }
    
    /**
     * {@inheritDoc}
     */
    public Page<M> search(Searchable searchable, String[] fnames, String searchTerm) throws SearchException {
    	FullTextEntityManager sess = Search.getFullTextEntityManager(em);

        org.apache.lucene.search.Query qry;
        try {
            qry = HibernateSearchTools.generateQuery(searchTerm, this.entityClass, fnames, sess, defaultAnalyzer);
            if (searchable.hasSearchFilter()) {
            	BooleanQuery.Builder bQuery = new BooleanQuery.Builder();
            	bQuery.add(qry, Occur.MUST);
            	bQuery.add(HibernateSearchTools.generateQuery(searchable), Occur.FILTER);
            	qry = bQuery.build();
            }
        } catch (ParseException ex) {
            throw new SearchException(ex);
        }
        
        org.hibernate.search.jpa.FullTextQuery hibQuery = sess.createFullTextQuery(qry,
                this.entityClass);
                
        if (searchable.hashSort()) {
        	hibQuery.setSort(HibernateSearchTools.prepareOrder(searchable, this.entityClass));
        }
        
        int total = hibQuery.getResultSize();
        List<M> content = Collections.<M>emptyList();
        if (searchable.hasPageable()) {
	        if (total > searchable.getPage().getOffset())
	        {
		        hibQuery.setFirstResult((int)searchable.getPage().getOffset());
		    	hibQuery.setMaxResults(searchable.getPage().getPageSize());
		    	content = hibQuery.getResultList();
	        }
        }
      
        return new PageImpl<M>(content, searchable.getPage(), total);   
    }
    
    /**
     * {@inheritDoc}
     */
    public void reindex() {
        HibernateSearchTools.reindex(entityClass, em);
    }


    /**
     * {@inheritDoc}
     */
    public void reindexAll(boolean async) {
        HibernateSearchTools.reindexAll(async, em);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<M> getAll() {
        return findAll();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<M> getAllDistinct() {
        Collection<M> result = new LinkedHashSet<M>(getAll());
        return new ArrayList<M>(result);
    }
    

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public M get(ID id) {
        M entity = getOne(id);

        if (entity == null) {
            log.warn("Uh oh, '" + this.getEntityClass() + "' object with id '" + id + "' not found...");
            throw new ObjectRetrievalFailureException(this.getEntityClass(), id);
        }

        return entity;
    }
    
    protected M preUpdate(M object, M source) {
    	try {
			// invoke method preUpdate
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
    public M update(M object, final Searchable searchable) {
        List<M> entityList = findAll(searchable);
        if (entityList != null && entityList.size() > 0){
        	M entity = entityList.get(0);
        	
        	this.getEntityManager().merge(preUpdate(entity, object));
        	
        	return entity;
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public M create(M t) {
    	this.getEntityManager().persist(t);
    	
        return t;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public M addOrUpdate(M object, final Searchable searchable) {
    	M result = update(object, searchable);
    	if (result == null){
        	return save(object);
        }
    	
    	return result;
     }

    /**
     * {@inheritDoc}
     */
    public void remove(M object) {
        super.delete(object);
    }

    /**
     * {@inheritDoc}
     */
    public void remove(ID id) {
    	delete(id);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeAll(){
    	super.deleteAllInBatch();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<M> findByNamedQuery(String queryName, Map<String, Object> queryParams) {
    	 TypedQuery<M> query = getEntityManager().createNamedQuery(queryName, getEntityClass());   		  
        for (String s : queryParams.keySet()) {
            Object val = queryParams.get(s);
           	query.setParameter(s, val);
        }
        
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
	public void clear(){ 
		getEntityManager().clear();
	}
	
	/**
     * {@inheritDoc}
     */
	public void detach(Object entity) {
		getEntityManager().detach(entity);
	}
	
	/**
     * {@inheritDoc}
     */
	public boolean contains(Object entity) {
		return getEntityManager().contains(entity);
	}
	
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		return getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();
	}
      
	/**
     * {@inheritDoc}
     */
	public void save(List<M> entityList){
		if (entityList != null && entityList.size() > 0){
			/*try {
				Method isNewMethod = entityList.get(0).getClass().getMethod("isNew");
				Method  puMethod = Reflections.getAnnotationMethod(entityList.get(0).getClass(), PreUpdate.class);
				Method  ppMethod = Reflections.getAnnotationMethod(entityList.get(0).getClass(), PrePersist.class);
				boolean isNew = false;
				for (M entity : entityList){
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
			}*/
			
			for (M entity : entityList){
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
	public int removeById(ID id){
		return update("update "+getEntityClass().getSimpleName()+" set delFlag='" + Constants.DEL_FLAG_DELETE + "' where id = :p1", 
				new Parameter(id));
	}
	
	/**
     * {@inheritDoc}
     */
	public int removeById(ID id, String likeParentIds){
		return update("update "+getEntityClass().getSimpleName()+" set delFlag = '" + Constants.DEL_FLAG_DELETE + "' where id = :p1 or parentIds like :p2",
				new Parameter(id, likeParentIds));
	}
	
	/**
     * {@inheritDoc}
     */
	public int updateDelFlag(ID id, String delFlag){
		return update("update "+getEntityClass().getSimpleName()+" set delFlag = :p2 where id = :p1", 
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
		Query query = getEntityManager().createNativeQuery(sqlString);
		setParameter(query, parameter);
		
		return query;
	}
	
	/**
     * {@inheritDoc}
     */
	public Query createQuery(String qlString, Parameter parameter){
		Query query = getEntityManager().createQuery(qlString);
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
	public <E> List<E> find(String qlString){
		return find(qlString, null);
	}
    
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	public <E> List<E> find(String qlString, Parameter parameter){
		Query query = createQuery(qlString, parameter);
		return query.getResultList();
	}
	
	
	/**
     * {@inheritDoc}
     */
	public <E> E getByHql(String qlString){
		return getByHql(qlString, null);
	}
	
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	public <E> E getByHql(String qlString, Parameter parameter){
		Query query = createQuery(qlString, parameter);
		
		return (E)query.getResultList();
	}
    
    // -------------- Criteria --------------
	
    /**
     * {@inheritDoc}
     */
 	public Page<M> find(Pageable page) {
 		return find(page, createCriteriaQuery());
 	}
 	
 	 	
 	/**
     * {@inheritDoc}
     */
 	@SuppressWarnings("unchecked")
 	public Page<M> find(Pageable page, CriteriaQuery criteriaQuery) {
 		Long total = count(criteriaQuery);
        
 		Query criteria = getEntityManager().createQuery(criteriaQuery);
        criteria.setFirstResult((int)page.getOffset());
        criteria.setMaxResults(page.getPageSize());
        
        List<M> content = total > page.getOffset() ? criteria.getResultList() : Collections.<M>emptyList();

        return new PageImpl<M>(content, page, total);        
 	}
 	
 	/**
     * {@inheritDoc}
     */
 	@SuppressWarnings("unchecked")
 	public List<M> find(CriteriaQuery criteriaQuery) {
 		Query query = getEntityManager().createQuery(criteriaQuery);
 		
 		return query.getResultList(); 
 	}
 	
 	/**
     * {@inheritDoc}
     */
 	@SuppressWarnings("rawtypes")
 	public long count(CriteriaQuery criteriaQuery) {
 		return JpaUtils.count(getEntityManager(), criteriaQuery);
 	}
    
    /**
     * {@inheritDoc}
     */
	public CriteriaQuery createCriteriaQuery(Predicate... predicates) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(getEntityClass());
        Root from = criteriaQuery.from(getEntityClass());
        criteriaQuery.select(from);
		criteriaQuery.where(predicates);

		return criteriaQuery;
	}
		
	@Override
	public Page<M> find(final Searchable searchable) {
		if (searchable.hasPageable()) {
			CriteriaQuery dc = prepareQL(searchable, null);
			if (dc == null) {
				return find(searchable.getPage());
			}else {
				return find(searchable.getPage(), dc);
			}
		}else{
			List<M> list = find(prepareQL(searchable, null));
			
	        return new PageImpl<M>(
	                list,
	                searchable.getPage(),
	                list.size()
	        );
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public M findOne(final Searchable searchable) {
		CriteriaQuery dc = (searchable== null ? createCriteriaQuery() : prepareQL(searchable, null));
		Query criteria = getEntityManager().createQuery(dc);
		criteria.setMaxResults(1);
		List<M> list = criteria.getResultList();
		if (list.isEmpty())
			return null;
		
		return list.get(0);
	}
	
	@Override
	public boolean findAny(Searchable searchable) {
		return (searchable == null ? count() > 0 : count(searchable) > 0);
	}
	
	@Override
	public List<M> findAll(final Searchable searchable) {
		return find(prepareQL(searchable, null));
	}
		
	/**
     * delete by Primary Keys
     *
     * @param ids
     */
    public void remove(ID[] ids){
    	delete(ids);
    }
    
    /**
     * delete by entities
     *
     * @param entities
     */
    public void remove(final Iterable<M> entities){
    	deleteInBatch(entities);
    }
    
        
    /**
     * find by Primary Keys
     *
     * @param ids
     */
    public List<M> find(ID[] ids){    	
    	return findAll(Arrays.asList(ids));
    }
    
    /**
	 * Gets de id attribute from metamodel
	 * @return PK SingularAttribute
	 */
	private SingularAttribute<? super M, ?> getIdAttribute() {
		return getIdAttribute(getEntityClass());
	}
	
	/**
	 * @param class1
	 * @return
	 */
	private <K> SingularAttribute<? super K, ?> getIdAttribute(Class<K> clazz) {
		Type<?> type = this.getEntityManager().getMetamodel().entity(clazz).getIdType();
		EntityType<K> entity =  this.getEntityManager().getMetamodel().entity(clazz);
		SingularAttribute<? super K, ?> id = entity.getId(type.getJavaType());
		
		return id;
	}
    
    @SuppressWarnings("unchecked")
	public List<ID> getPrimaryKeys(Searchable searchable){
    	if (!AbstractEntity.class.isAssignableFrom(this.getEntityClass()))
    		return null;
    	
		SingularAttribute<? super M, ?> id = getIdAttribute();

    	CriteriaQuery criteriaQuery = null;
		if (searchable == null){
			CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
	    	criteriaQuery = builder.createQuery(this.getEntityClass());
	    	Root from = criteriaQuery.from(this.getEntityClass());
	        criteriaQuery.select(from.get(id.getName()));
		}else{
			criteriaQuery = prepareQL(searchable, id.getName());
		}
		// Get id list
		TypedQuery<ID> query = getEntityManager().createQuery(criteriaQuery);
		
		return query.getResultList();
    }
        
    
    /**
     * Create criteria matching an entity type or a supertype thereof.
     * Use when building a criteria query.
     *
     * @param context         current MDSPlus context.
     * @param getEntityClass() specifies the type to be matched by the criteria.
     * @return criteria concerning the type to be found.
     * @throws SQLException passed through.
     */
    public CriteriaQuery createCriteria(Class<M> entityClass) throws SQLException {
    	CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery criteriaQuery = builder.createQuery(entityClass);
    	Root from = criteriaQuery.from(entityClass);
        criteriaQuery.select(from);
    	
		return criteriaQuery;
    }

    /**
     * Create criteria matching an entity type or a supertype thereof.
     * Use when building a criteria query.
     *
     * @param context         current MDSPlus context.
     * @param entityClass specifies the type to be matched by the criteria.
     * @param alias           alias for the type.
     * @return criteria concerning the type to be found.
     * @throws SQLException passed through.
     */
    public CriteriaQuery createCriteria(Class<M> entityClass, String alias) throws SQLException {
    	CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery criteriaQuery = builder.createQuery(entityClass);
    	Root from = criteriaQuery.from(entityClass);
    	from.alias(alias);
        criteriaQuery.select(from);
    	
		return criteriaQuery;
    }

    /**
     * Create a parsed query from a query expression.
     *
     * @param context current MDSPlus context.
     * @param query   textual form of the query.
     * @return parsed form of the query.
     * @throws SQLException
     */
    public Query createQuery(String query) throws SQLException {
        return getEntityManager().createQuery(query);
    }

    /**
     * Get the entities matched by the given Criteria.
     * Use this if you need all results together.
     *
     * @param criteria description of desired entities.
     * @return the entities matched.
     */
    public List<M> list(CriteriaQuery criteria) {
        @SuppressWarnings("unchecked")
        Query query = getEntityManager().createQuery(criteria);

        return query.getResultList();
    }

    /**
     * Get the entities matching a given parsed query.
     * Use this if you need all results together.
     *
     * @param query the query to be executed.
     * @return entities matching the query.
     */
    public List<M> list(Query query) {
        @SuppressWarnings("unchecked")
        List<M> result = (List<M>) query.getResultList();
        return result;
    }
    
    /**
     * Retrieve a single result selected by criteria.  Best used if you expect a
     * single result, but this isn't enforced on the database.
     *
     * @param criteria description of the desired entities.
     * @return a DAO specified by the criteria
     */
    public M singleResult(CriteriaQuery criteria) {
    	Query query = getEntityManager().createQuery(criteria);
    	query.setMaxResults(1);
        List<M> list = query.getResultList();
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
    public M singleResult(final Query query) {
        query.setMaxResults(1);
        List<M> list = query.getResultList();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
