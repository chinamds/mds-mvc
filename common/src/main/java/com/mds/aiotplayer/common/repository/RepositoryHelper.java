/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.common.repository;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.common.model.Parameter;
import com.mds.aiotplayer.common.repository.support.JpaUtils;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.model.search.filter.AndCondition;
import com.mds.aiotplayer.common.model.search.filter.Condition;
import com.mds.aiotplayer.common.model.search.filter.OrCondition;
import com.mds.aiotplayer.common.model.search.filter.SearchFilter;
import com.mds.aiotplayer.common.repository.callback.SearchCallback;
import com.mds.aiotplayer.common.repository.support.annotation.EnableQueryCache;
import com.mds.aiotplayer.common.utils.Reflections;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 仓库辅助类
 * <p>User: Zhang Kaitao
 * <p>Date: 13-4-14 下午5:28
 * <p>Version: 1.0
 */
public class RepositoryHelper {

    private static EntityManager entityManager;
    private Class<?> entityClass;
    private boolean enableQueryCache = false;

    /**
     * @param entityClass 是否开启查询缓存
     */
    public RepositoryHelper(Class<?> entityClass) {
        this.entityClass = entityClass;

        EnableQueryCache enableQueryCacheAnnotation =
                AnnotationUtils.findAnnotation(entityClass, EnableQueryCache.class);

        boolean enableQueryCache = false;
        if (enableQueryCacheAnnotation != null) {
            enableQueryCache = enableQueryCacheAnnotation.value();
        }
        this.enableQueryCache = enableQueryCache;
    }

    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

    public static EntityManager getEntityManager() {
        Assert.notNull(entityManager, "entityManager must not be null, please see " +
                "[com.mds.aiotplayer.common.repository.RepositoryHelper#setEntityManagerFactory]");

        return entityManager;
    }


    public static void flush() {
        getEntityManager().flush();
    }

    public static void clear() {
        flush();
        getEntityManager().clear();
    }

    /**
     * <p>ql条件查询<br/>
     * searchCallback默认实现请参考 {@see com.mds.aiotplayer.common.repository.callback.DefaultSearchCallback}<br/>
     * <p/>
     * 测试用例请参考：{@see com.mds.aiotplayer.common.repository.UserRepositoryImplForCustomSearchIT}
     * 和{@see com.mds.aiotplayer.common.repository.UserRepositoryImplForDefaultSearchIT}
     *
     * @param ql
     * @param searchable     查询条件、分页 排序
     * @param searchCallback 查询回调  自定义设置查询条件和赋值
     * @return
     */
    public <M> List<M> findAll(final String ql, final Searchable searchable, final SearchCallback searchCallback) {

        assertConverted(searchable);
        StringBuilder s = new StringBuilder(ql);
        searchCallback.prepareQL(s, searchable);
        searchCallback.prepareOrder(s, searchable);
        Query query = getEntityManager().createQuery(s.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);
        searchCallback.setPageable(query, searchable);

        return query.getResultList();
    }

    /**
     * <p>按条件统计<br/>
     * 测试用例请参考：{@see com.mds.aiotplayer.common.repository.UserRepositoryImplForCustomSearchIT}
     * 和{@see com.mds.aiotplayer.common.repository.UserRepositoryImplForDefaultSearchIT}
     *
     * @param ql
     * @param searchable
     * @param searchCallback
     * @return
     */
    public long count(final String ql, final Searchable searchable, final SearchCallback searchCallback) {

        assertConverted(searchable);

        StringBuilder s = new StringBuilder(ql);
        searchCallback.prepareQL(s, searchable);
        Query query = getEntityManager().createQuery(s.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);

        return (Long) query.getSingleResult();
    }

    /**
     * 按条件查询一个实体
     *
     * @param ql
     * @param searchable
     * @param searchCallback
     * @return
     */
    public <M> M findOne(final String ql, final Searchable searchable, final SearchCallback searchCallback) {

        assertConverted(searchable);

        StringBuilder s = new StringBuilder(ql);
        searchCallback.prepareQL(s, searchable);
        searchCallback.prepareOrder(s, searchable);
        Query query = getEntityManager().createQuery(s.toString());
        applyEnableQueryCache(query);
        searchCallback.setValues(query, searchable);
        searchCallback.setPageable(query, searchable);
        query.setMaxResults(1);
        List<M> result = query.getResultList();

        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }


    /**
     * @param ql
     * @param params
     * @param <M>
     * @return
     * @see RepositoryHelper#findAll(String, org.springframework.data.domain.Pageable, Object...)
     */
    public <M> List<M> findAll(final String ql, final Object... params) {

        //此处必须 (Pageable) null  否则默认有调用自己了 可变参列表
        return findAll(ql, (Pageable) null, params);

    }

    /**
     * <p>根据ql和按照索引顺序的params执行ql，pageable存储分页信息 null表示不分页<br/>
     * 具体使用请参考测试用例：{@see com.mds.aiotplayer.common.repository.UserRepository2ImplIT#testFindAll()}
     *
     * @param ql
     * @param pageable null表示不分页
     * @param params
     * @param <M>
     * @return
     */
    public <M> List<M> findAll(final String ql, final Pageable pageable, final Object... params) {

        Query query = getEntityManager().createQuery(ql + prepareOrder(pageable != null ? pageable.getSort() : null));
        applyEnableQueryCache(query);
        setParameters(query, params);
        if (pageable != null) {
            query.setFirstResult((int)pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return query.getResultList();
    }

    /**
     * <p>根据ql和按照索引顺序的params执行ql，sort存储排序信息 null表示不排序<br/>
     * 具体使用请参考测试用例：{@see com.mds.aiotplayer.common.repository.UserRepository2ImplIT#testFindAll()}
     *
     * @param ql
     * @param sort   null表示不排序
     * @param params
     * @param <M>
     * @return
     */
    public <M> List<M> findAll(final String ql, final Sort sort, final Object... params) {

        Query query = getEntityManager().createQuery(ql + prepareOrder(sort));
        applyEnableQueryCache(query);
        setParameters(query, params);

        return query.getResultList();
    }


    /**
     * <p>根据ql和按照索引顺序的params查询一个实体<br/>
     * 具体使用请参考测试用例：{@see com.mds.aiotplayer.common.repository.UserRepository2ImplIT#testFindOne()}
     *
     * @param ql
     * @param params
     * @param <M>
     * @return
     */
    public <M> M findOne(final String ql, final Object... params) {

        List<M> list = findAll(ql, PageRequest.of(0, 1), params);

        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }


    /**
     * <p>根据ql和按照索引顺序的params执行ql统计<br/>
     * 具体使用请参考测试用例：com.mds.aiotplayer.common.repository.UserRepository2ImplIT#testCountAll()
     *
     * @param ql
     * @param params
     * @return
     */
    public long count(final String ql, final Object... params) {

        Query query = entityManager.createQuery(ql);
        applyEnableQueryCache(query);
        setParameters(query, params);

        return (Long) query.getSingleResult();
    }

    /**
     * <p>执行批处理语句.如 之间insert, update, delete 等.<br/>
     * 具体使用请参考测试用例：{@see com.mds.aiotplayer.common.repository.UserRepository2ImplIT#testBatchUpdate()}
     *
     * @param ql
     * @param params
     * @return
     */
    public int batchUpdate(final String ql, final Object... params) {

        Query query = getEntityManager().createQuery(ql);
        setParameters(query, params);

        return query.executeUpdate();
    }


    /**
     * 按顺序设置Query参数
     *
     * @param query
     * @param params
     */
    public void setParameters(Query query, Object[] params) {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
    }

    /**
     * 拼排序
     *
     * @param sort
     * @return
     */
    public String prepareOrder(Sort sort) {
        if (sort == null || !sort.iterator().hasNext()) {
            return "";
        }
        StringBuilder orderBy = new StringBuilder("");
        orderBy.append(" order by ");
        orderBy.append(sort.toString().replace(":", " "));
        return orderBy.toString();
    }


    public <M> JpaEntityInformation<M, ?> getMetadata(Class<M> entityClass) {
        return JpaEntityInformationSupport.getEntityInformation(entityClass, entityManager);
    }

    public String getEntityName(Class<?> entityClass) {
        return getMetadata(entityClass).getEntityName();
    }


    private void assertConverted(Searchable searchable) {
        if (!searchable.isConverted()) {
            searchable.convert(this.entityClass);
        }
    }


    public void applyEnableQueryCache(Query query) {
        if (enableQueryCache) {
            query.setHint("org.hibernate.cacheable", true);//开启查询缓存
        }
    }

	/////////////////////////////////////////////////
	///////////////////searchable filter////////////////////
	/////////////////////////////////////////////////
	
	 @SuppressWarnings("unchecked")
	public CriteriaQuery prepareQL(Searchable search, String selection) {
        if (search == null || (!search.hasSearchFilter() && !search.hashSort())) {
            return null;
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root from = criteriaQuery.from(entityClass);
        criteriaQuery.select(StringUtils.isBlank(selection) ? from : from.get(selection));
        
        List<Predicate> predicates = Lists.newArrayList();
        Map<String, Join> aliasAssociations= Maps.newHashMap();
        for (SearchFilter searchFilter : search.getSearchFilters()) {

            if (searchFilter instanceof Condition) {
                Condition condition = (Condition) searchFilter;
                if (condition.getOperator() == SearchOperator.custom) {
                    continue;
                }
            }

            Predicate criterion = genCondition(criteriaBuilder, criteriaQuery, from, aliasAssociations, searchFilter);
            if (criterion != null){
            	predicates.add(criterion);
            }
        }
        List<javax.persistence.criteria.Order> qOrders = prepareOrder(criteriaBuilder, from, search, aliasAssociations);
        
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        if (!qOrders.isEmpty())
        	criteriaQuery.orderBy(qOrders);
        
        return criteriaQuery;
    }
	 	 
	 public List<javax.persistence.criteria.Order> prepareOrder(CriteriaBuilder criteriaBuilder, Root root, Searchable search, Map<String, Join> aliasAssociations) {
		 List<javax.persistence.criteria.Order> qOrders = Lists.newArrayList();
        if (search.hashSort()) {
            for (Sort.Order order : search.getSort()) {
            	String[] properties = StringUtils.split(order.getProperty(), ".");
            	String orderName=order.getProperty();
            	From from = root;
            	if (properties.length > 1) { //&& !properties[0].equalsIgnoreCase("parent")
            		String associationPath = "";
            		for(int i=0; i<properties.length-1; i++) {
            			associationPath += properties[i];
            			//orderName = associationPath.replace('.', '_');
            			from = createJoin(criteriaBuilder, from, aliasAssociations, associationPath, properties[i]);
            			associationPath += ".";
            		}            			 
            		orderName = properties[properties.length-1];
            	}
            	if (order.getDirection() == Direction.ASC)
    				qOrders.add(criteriaBuilder.asc(from.get(orderName)));
            	else
            		qOrders.add(criteriaBuilder.desc(from.get(orderName)));
            }
        }
        
        return qOrders;
    }
	 
	 @SuppressWarnings("unchecked")
	private Predicate genPredicate(CriteriaBuilder criteriaBuilder, AbstractQuery criteriaQuery, From from, Map<String, Join> aliasAssociations, Condition condition) {
		 if (condition.getOperator() == SearchOperator.eq)
			 return criteriaBuilder.equal(from.get(condition.getSearchJpaProperty()), condition.getValue());
		 else if (condition.getOperator() == SearchOperator.eqi) //String only
			 return criteriaBuilder.equal(criteriaBuilder.upper(from.get(condition.getSearchJpaProperty())), condition.getValue().toString().toUpperCase());
		 else if (condition.getOperator() == SearchOperator.ne)
			 return criteriaBuilder.notEqual(from.get(condition.getSearchJpaProperty()), condition.getValue());
		 else if (condition.getOperator() == SearchOperator.nei)
			 return criteriaBuilder.notEqual(criteriaBuilder.upper(from.get(condition.getSearchJpaProperty())), condition.getValue().toString().toUpperCase());
		 else if (condition.getOperator() == SearchOperator.gt) {
			 Class<?> valType = condition.getValue().getClass();
			 if (valType == String.class){
				 return criteriaBuilder.greaterThan(from.get(condition.getSearchJpaProperty()), condition.getValue().toString());
			}else if (valType == Integer.class || valType == int.class){
				return criteriaBuilder.greaterThan(from.get(condition.getSearchJpaProperty()), (Integer)condition.getValue());
			}else if (valType == Long.class || valType == long.class){
				return criteriaBuilder.greaterThan(from.get(condition.getSearchJpaProperty()), (Long)condition.getValue());
			}else if (valType == Double.class || valType == double.class){
				return criteriaBuilder.greaterThan(from.get(condition.getSearchJpaProperty()), (Double)condition.getValue());
			}else if (valType == Float.class || valType == float.class){
				return criteriaBuilder.greaterThan(from.get(condition.getSearchJpaProperty()), (Float)condition.getValue());
			}else if (valType == Boolean.class || valType == boolean.class){
				return criteriaBuilder.greaterThan(from.get(condition.getSearchJpaProperty()), (Boolean)condition.getValue());	
			}else if (valType == Short.class || valType == short.class){
				return criteriaBuilder.greaterThan(from.get(condition.getSearchJpaProperty()), (Short)condition.getValue());
			}else if (valType == Byte.class || valType == byte.class){
				return criteriaBuilder.greaterThan(from.get(condition.getSearchJpaProperty()), (Byte)condition.getValue());
			}else if (valType == Date.class){
				return criteriaBuilder.greaterThan(from.get(condition.getSearchJpaProperty()), (Date)condition.getValue());
			}			 
		 } else if (condition.getOperator() == SearchOperator.gte) {
			 Class<?> valType = condition.getValue().getClass();
			 if (valType == String.class){
				 return criteriaBuilder.greaterThanOrEqualTo(from.get(condition.getSearchJpaProperty()), condition.getValue().toString());
			}else if (valType == Integer.class || valType == int.class){
				return criteriaBuilder.greaterThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Integer)condition.getValue());
			}else if (valType == Long.class || valType == long.class){
				return criteriaBuilder.greaterThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Long)condition.getValue());
			}else if (valType == Double.class || valType == double.class){
				return criteriaBuilder.greaterThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Double)condition.getValue());
			}else if (valType == Float.class || valType == float.class){
				return criteriaBuilder.greaterThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Float)condition.getValue());
			}else if (valType == Boolean.class || valType == boolean.class){
				return criteriaBuilder.greaterThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Boolean)condition.getValue());	
			}else if (valType == Short.class || valType == short.class){
				return criteriaBuilder.greaterThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Short)condition.getValue());
			}else if (valType == Byte.class || valType == byte.class){
				return criteriaBuilder.greaterThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Byte)condition.getValue());
			}else if (valType == Date.class){
				return criteriaBuilder.greaterThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Date)condition.getValue());
			}			 
		 }else if (condition.getOperator() == SearchOperator.lt) {
			 Class<?> valType = condition.getValue().getClass();
			 if (valType == String.class){
				 return criteriaBuilder.lessThan(from.get(condition.getSearchJpaProperty()), condition.getValue().toString());
			}else if (valType == Integer.class || valType == int.class){
				return criteriaBuilder.lessThan(from.get(condition.getSearchJpaProperty()), (Integer)condition.getValue());
			}else if (valType == Long.class || valType == long.class){
				return criteriaBuilder.lessThan(from.get(condition.getSearchJpaProperty()), (Long)condition.getValue());
			}else if (valType == Double.class || valType == double.class){
				return criteriaBuilder.lessThan(from.get(condition.getSearchJpaProperty()), (Double)condition.getValue());
			}else if (valType == Float.class || valType == float.class){
				return criteriaBuilder.lessThan(from.get(condition.getSearchJpaProperty()), (Float)condition.getValue());
			}else if (valType == Boolean.class || valType == boolean.class){
				return criteriaBuilder.lessThan(from.get(condition.getSearchJpaProperty()), (Boolean)condition.getValue());	
			}else if (valType == Short.class || valType == short.class){
				return criteriaBuilder.lessThan(from.get(condition.getSearchJpaProperty()), (Short)condition.getValue());
			}else if (valType == Byte.class || valType == byte.class){
				return criteriaBuilder.lessThan(from.get(condition.getSearchJpaProperty()), (Byte)condition.getValue());
			}else if (valType == Date.class){
				return criteriaBuilder.lessThan(from.get(condition.getSearchJpaProperty()), (Date)condition.getValue());
			}			 
		 }else if (condition.getOperator() == SearchOperator.lte){
			 Class<?> valType = condition.getValue().getClass();
			 if (valType == String.class){
				 return criteriaBuilder.lessThanOrEqualTo(from.get(condition.getSearchJpaProperty()), condition.getValue().toString());
			}else if (valType == Integer.class || valType == int.class){
				return criteriaBuilder.lessThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Integer)condition.getValue());
			}else if (valType == Long.class || valType == long.class){
				return criteriaBuilder.lessThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Long)condition.getValue());
			}else if (valType == Double.class || valType == double.class){
				return criteriaBuilder.lessThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Double)condition.getValue());
			}else if (valType == Float.class || valType == float.class){
				return criteriaBuilder.lessThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Float)condition.getValue());
			}else if (valType == Boolean.class || valType == boolean.class){
				return criteriaBuilder.lessThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Boolean)condition.getValue());	
			}else if (valType == Short.class || valType == short.class){
				return criteriaBuilder.lessThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Short)condition.getValue());
			}else if (valType == Byte.class || valType == byte.class){
				return criteriaBuilder.lessThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Byte)condition.getValue());
			}else if (valType == Date.class){
				return criteriaBuilder.lessThanOrEqualTo(from.get(condition.getSearchJpaProperty()), (Date)condition.getValue());
			}			 
		 }else if (condition.getOperator() == SearchOperator.bt) {
			 Class<?> valType = condition.getValue().getClass();
			 if (valType == String.class){
				 return criteriaBuilder.between(from.get(condition.getSearchJpaProperty()), condition.getValue().toString(), condition.getValue2().toString());
			}else if (valType == Integer.class || valType == int.class){
				return criteriaBuilder.between(from.get(condition.getSearchJpaProperty()), (Integer)condition.getValue(), (Integer)condition.getValue2());
			}else if (valType == Long.class || valType == long.class){
				return criteriaBuilder.between(from.get(condition.getSearchJpaProperty()), (Long)condition.getValue(), (Long)condition.getValue2());
			}else if (valType == Double.class || valType == double.class){
				return criteriaBuilder.between(from.get(condition.getSearchJpaProperty()), (Double)condition.getValue(), (Double)condition.getValue2());
			}else if (valType == Float.class || valType == float.class){
				return criteriaBuilder.between(from.get(condition.getSearchJpaProperty()), (Float)condition.getValue(), (Float)condition.getValue2());
			}else if (valType == Boolean.class || valType == boolean.class){
				return criteriaBuilder.between(from.get(condition.getSearchJpaProperty()), (Boolean)condition.getValue(), (Boolean)condition.getValue2());	
			}else if (valType == Short.class || valType == short.class){
				return criteriaBuilder.between(from.get(condition.getSearchJpaProperty()), (Short)condition.getValue(), (Short)condition.getValue2());
			}else if (valType == Byte.class || valType == byte.class){
				return criteriaBuilder.between(from.get(condition.getSearchJpaProperty()), (Byte)condition.getValue(), (Byte)condition.getValue2());
			}else if (valType == Date.class){
				return criteriaBuilder.between(from.get(condition.getSearchJpaProperty()), (Date)condition.getValue(), (Date)condition.getValue2());
			}			 
		 }else if (condition.getOperator() == SearchOperator.prefixLike)
			 return criteriaBuilder.like(from.get(condition.getSearchJpaProperty()), condition.getValue().toString() + "%");
		 else if (condition.getOperator() == SearchOperator.prefixNotLike)
			 return criteriaBuilder.notLike(from.get(condition.getSearchJpaProperty()), condition.getValue().toString() + "%");
		 else if (condition.getOperator() == SearchOperator.suffixLike)
			 return criteriaBuilder.like(from.get(condition.getSearchJpaProperty()), "%" + condition.getValue().toString());
		 else if (condition.getOperator() == SearchOperator.suffixNotLike)
			 return criteriaBuilder.notLike(from.get(condition.getSearchJpaProperty()), "%" + condition.getValue().toString());
		 else if (condition.getOperator() == SearchOperator.like)
			 return criteriaBuilder.like(from.get(condition.getSearchJpaProperty()), condition.getValue().toString() );
		 else if (condition.getOperator() == SearchOperator.notLike)
			 return criteriaBuilder.notLike(from.get(condition.getSearchJpaProperty()), condition.getValue().toString());
		 else if (condition.getOperator() == SearchOperator.contain)
			 return criteriaBuilder.like(from.get(condition.getSearchJpaProperty()), "%" + condition.getValue().toString() + "%");
		 else if (condition.getOperator() == SearchOperator.notLike)
			 return criteriaBuilder.notLike(from.get(condition.getSearchJpaProperty()), "%" + condition.getValue().toString() + "%");
		 else if (condition.getOperator() == SearchOperator.isNull)
			 return criteriaBuilder.isNull(from.get(condition.getSearchJpaProperty()));
		 else if (condition.getOperator() == SearchOperator.isNotNull)
			 return criteriaBuilder.isNotNull(from.get(condition.getSearchJpaProperty()));
		 else if (condition.getOperator() == SearchOperator.in) {
			 Object value = condition.getValue();
			 if(value instanceof Collection<?>){
				 if (((Collection<?>)value).isEmpty()) {
					 //return criteriaBuilder.isTrue(criteriaBuilder.literal(false));
					 return criteriaBuilder.disjunction();
				 }else {
					 //return criteriaBuilder.in(from.get(condition.getSearchJpaProperty()).in((Collection<?>)value));
					 return from.get(condition.getSearchJpaProperty()).in((Collection<?>)value);
				 }
             }else if(value instanceof Object[]){
            	 if (((Object[])value).length == 0) {
					 //return criteriaBuilder.isTrue(criteriaBuilder.literal(false));
            		 return criteriaBuilder.disjunction();
				 }else {
					 //return criteriaBuilder.in(from.get(condition.getSearchJpaProperty()).in((Object[])value));
					 return from.get(condition.getSearchJpaProperty()).in((Object[])value);
				 }
             }			
		 }else if (condition.getOperator() == SearchOperator.notIn){
			 Object value = condition.getValue();
			 if(value instanceof Collection<?>){
				 if (!((Collection<?>)value).isEmpty()) {
					 return criteriaBuilder.not(from.get(condition.getSearchJpaProperty()).in((Collection<?>)value));
				 }
             }else if(value instanceof Object[]){
            	 if (((Object[])value).length > 0) {
            		 return criteriaBuilder.not(from.get(condition.getSearchJpaProperty()).in((Object[])value));
            	 }
             }			
		 }else if (condition.getOperator() == SearchOperator.exists || condition.getOperator() == SearchOperator.notExists){
			 Subquery<?> sq = criteriaQuery.subquery(this.entityClass);
			 Root ab = sq.from(Reflections.getClassGenricType(from.get(condition.getSearchCollection()).getJavaType()));
			 sq.select(ab.get(condition.getSearchAlias()).get(condition.getSearchJpaProperty()));
			 Map<String, Join> subAliasAssociations= Maps.newHashMap();
			 sq.where(genCondition(criteriaBuilder, sq, ab, subAliasAssociations, condition)); //cb.equal(ab.get(AB_.id).get(ABPK_.b).get(B_.status), status)
			 return condition.getOperator() == SearchOperator.exists ? criteriaBuilder.exists(sq) : criteriaBuilder.exists(sq).not();
		 }
		 
		 return criteriaBuilder.equal(from.get(condition.getSearchJpaProperty()), condition.getValue());
	 }
	 
	 private Join createJoin(CriteriaBuilder criteriaBuilder, Root from, Map<String, Join> aliasAssociations, Condition condition){
		 if (!StringUtils.isBlank(condition.getSearchAlias())){
			Join dcAlias = aliasAssociations.get(condition.getSearchAlias());
     		if (dcAlias == null) {
	         	dcAlias = from.join(condition.getSearchAlias());
	         	aliasAssociations.put(condition.getSearchAlias(), dcAlias);
     		}
         	
         	return  dcAlias;
         }else {
        	Join dcAlias = null; 
        	String[] properties = StringUtils.split(condition.getSearchProperty(), ".");
        	//String searchProp = condition.getSearchProperty();
         	if (properties.length > 1) { //&& !properties[0].equalsIgnoreCase("parent")
         		String associationPath = "";
         		for(int i=0; i<properties.length-1; i++) {
         			associationPath += properties[i];
         			//searchProp = associationPath.replace('.', '_');
         			dcAlias = createJoin(criteriaBuilder, dcAlias==null ? from : dcAlias , aliasAssociations, associationPath, properties[i]);
         			associationPath += ".";
         		}
         		//searchProp = searchProp + "." + properties[properties.length-1];
         		//searchProp = properties[properties.length-1];
         		
         		//condition.setSearchProperty(searchProp);
         		
         		return dcAlias;
         	}
         	
         	return null;
         }
	 }
	 
	 private Join createJoin(CriteriaBuilder criteriaBuilder, From from,  Map<String, Join> aliasAssociations, String associationPath, String alias){
		 if (!StringUtils.isBlank(associationPath)) {
         	if (!aliasAssociations.containsKey(associationPath)) {
         		Join dcAlias = from.join(alias);
	         	aliasAssociations.put(associationPath, dcAlias);
	         	
	         	return  dcAlias;
         	}else {
           	    return aliasAssociations.get(associationPath);
            }
         }
		 
		 return null;
	 }
	 	 
	 private Predicate genCriterion(CriteriaBuilder criteriaBuilder, AbstractQuery criteriaQuery, Root from,  Map<String, Join> aliasAssociations, SearchFilter searchFilter) {
		 Condition condition = (Condition) searchFilter;
		 Join join = createJoin(criteriaBuilder, from, aliasAssociations, condition);
			 
		 return genPredicate(criteriaBuilder, criteriaQuery, join == null ? from : join, aliasAssociations, condition);
	 }

    private Predicate genCondition(CriteriaBuilder criteriaBuilder, AbstractQuery criteriaQuery, Root from, Map<String, Join> aliasAssociations, SearchFilter searchFilter) {
        if (searchFilter instanceof Condition) {
        	Predicate cr = genCriterion(criteriaBuilder, criteriaQuery, from, aliasAssociations, searchFilter);
        	
        	return cr;
        } else if (searchFilter instanceof OrCondition) {
        	Predicate criterion = null;
            boolean isFirst = true;
            for (SearchFilter orSearchFilter : ((OrCondition) searchFilter).getOrFilters()) {
            	Predicate cr = genCondition(criteriaBuilder, criteriaQuery, from, aliasAssociations, orSearchFilter);
            	if (cr == null)
            		continue;
            	
                if (isFirst) {
                	criterion = cr;
                }
                else
                	criterion = criteriaBuilder.or(criterion, cr);
                isFirst = false;
            }
            
            return criterion;
        } else if (searchFilter instanceof AndCondition) {
            boolean isFirst = true;
            Predicate criterion = null;
            for (SearchFilter andSearchFilter : ((AndCondition) searchFilter).getAndFilters()) {
            	Predicate cr = genCondition(criteriaBuilder, criteriaQuery, from, aliasAssociations, andSearchFilter);
            	if (cr == null)
            		continue;
            	
                if (isFirst) {
                	criterion = cr;
                }
                else
                	criterion = criteriaBuilder.or(criterion, cr);
                isFirst = false;
            }
            
            return criterion;
        }
        
        return null;
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
	public void setResultTransformer(Query query, Class<?> resultClass){
		if (resultClass != null){
			if (resultClass == Map.class){
				query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			}else if (resultClass == List.class){
				query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.TO_LIST);
			}else{
				query.unwrap(NativeQueryImpl.class).addEntity(resultClass);
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
	public <M> M getByHql(String qlString){
		return getByHql(qlString, null);
	}
	
	/**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	public <M> M getByHql(String qlString, Parameter parameter){
		Query query = createQuery(qlString, parameter);
		return (M)query.getResultList();
	}
    
    // -------------- Criteria --------------
	
    /**
     * {@inheritDoc}
     */
 	public <M> Page<M> find(Pageable page) {
 		return find(page, createCriteriaQuery());
 	}
 	
 	 	
 	/**
     * {@inheritDoc}
     */
 	@SuppressWarnings("unchecked")
 	public <M> Page<M> find(Pageable page, CriteriaQuery criteriaQuery) {
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
 	public <M> List<M> find(CriteriaQuery criteriaQuery) {
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
 	
 	public long countLong(Query query) {
 		return (long)query.getSingleResult();
 	}
 	
 	public int count(Query query) {
 		return (int)query.getSingleResult();
 	}
    
    /**
     * {@inheritDoc}
     */
	public CriteriaQuery createCriteriaQuery(Predicate... predicates) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root from = criteriaQuery.from(entityClass);
        criteriaQuery.select(from);
		criteriaQuery.where(predicates);

		return criteriaQuery;
	}
					            
    /**
     * Create criteria matching an entity type or a supertype thereof.
     * Use when building a criteria query.
     *
     * @param context         current MDSPlus context.
     * @param entityClass specifies the type to be matched by the criteria.
     * @return criteria concerning the type to be found.
     * @throws SQLException passed through.
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public <M> CriteriaQuery createCriteria(Class<M> entityClass) throws SQLException {
    	CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery criteriaQuery = builder.createQuery(entityClass);
    	Root from = criteriaQuery.from(entityClass);
        criteriaQuery.select(from);
    	
		return criteriaQuery;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <M> CriteriaQuery createCriteria(Class<M> entityClass, Sort sort) throws SQLException {
    	CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery criteriaQuery = builder.createQuery(entityClass);
    	Root from = criteriaQuery.from(entityClass);
        criteriaQuery.select(from);
        criteriaQuery.orderBy(prepareOrder(builder, from, sort));
    	
		return criteriaQuery;
    }
    
    public List<javax.persistence.criteria.Order> prepareOrder(CriteriaBuilder criteriaBuilder, Root root, Sort sort) {
    	List<javax.persistence.criteria.Order> qOrders = Lists.newArrayList();
    	Map<String, Join> aliasAssociations= Maps.newHashMap();
	    for (Sort.Order order : sort) {
		   	String[] properties = StringUtils.split(order.getProperty(), ".");
		   	String orderName=order.getProperty();
		   	From from = root;
		   	if (properties.length > 1 && !properties[0].equalsIgnoreCase("parent")) {
		   		String associationPath = "";
		   		for(int i=0; i<properties.length-1; i++) {
		   			associationPath += properties[i];
		   			from = createJoin(criteriaBuilder, from, aliasAssociations, associationPath, properties[i]);
		   			associationPath += ".";
		   		}            			 
		   		orderName = properties[properties.length-1];
		   	}
		   	
		   	if (order.getDirection() == Direction.ASC)
				qOrders.add(criteriaBuilder.asc(from.get(orderName)));
		   	else
		   		qOrders.add(criteriaBuilder.desc(from.get(orderName)));
	   }
       
       return qOrders;
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
    public <M> CriteriaQuery createCriteria(Class<M> entityClass, String alias) throws SQLException {
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
    @SuppressWarnings("rawtypes")
	public <M> List<M> list(CriteriaQuery criteria) {
        return list(criteria, false);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public <M> List<M> list(CriteriaQuery criteria, boolean enableQueryCache) {
        Query query = getEntityManager().createQuery(criteria);
        if (enableQueryCache) {
    		applyEnableQueryCache(query);
    	}

        return query.getResultList();
    }

    /**
     * Get the entities matching a given parsed query.
     * Use this if you need all results together.
     *
     * @param query the query to be executed.
     * @return entities matching the query.
     */
    public <M> List<M> list(Query query) {
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
    @SuppressWarnings("rawtypes")
	public <M> M singleResult(CriteriaQuery criteria) {
        return singleResult(criteria, false);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public <M> M singleResult(CriteriaQuery criteria, boolean enableQueryCache) {
    	Query query = getEntityManager().createQuery(criteria);
    	if (enableQueryCache) {
    		applyEnableQueryCache(query);
    	}
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
    public <M> M singleResult(final Query query) {
        query.setMaxResults(1);
        List<M> list = query.getResultList();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
