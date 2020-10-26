/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.repository.support;

import com.mds.aiotplayer.common.repository.BaseRepository;
import com.mds.aiotplayer.common.repository.callback.SearchCallback;
import com.mds.aiotplayer.common.repository.support.annotation.SearchableQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Optional;

/**
 * 基础Repostory简单实现 factory bean
 * 请参考 spring-data-jpa-reference [1.4.2. Adding custom behaviour to all repositories]
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-5 上午11:57
 * <p>Version: 1.0
 */
public class SimpleBaseRepositoryFactoryBean<R extends JpaRepository<M, ID>, M, ID extends Serializable>
        extends JpaRepositoryFactoryBean<R, M, ID> {

    public SimpleBaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
    	super(repositoryInterface);
    }

    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new SimpleBaseRepositoryFactory(entityManager);
    }
}

class SimpleBaseRepositoryFactory<M, ID extends Serializable> extends JpaRepositoryFactory {

    private EntityManager entityManager;

    public SimpleBaseRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
    }

    protected Object getTargetRepository(RepositoryMetadata metadata) {
        Class<?> repositoryInterface = metadata.getRepositoryInterface();

        if (isBaseRepository(repositoryInterface)) {

            JpaEntityInformation<M, ID> entityInformation = getEntityInformation((Class<M>) metadata.getDomainType());
            SimpleBaseRepository repository = new SimpleBaseRepository<M, ID>(entityInformation, entityManager);

            SearchableQuery searchableQuery = AnnotationUtils.findAnnotation(repositoryInterface, SearchableQuery.class);
            if (searchableQuery != null) {
                String countAllQL = searchableQuery.countAllQuery();
                if (!StringUtils.isEmpty(countAllQL)) {
                    repository.setCountAllQL(countAllQL);
                }
                String findAllQL = searchableQuery.findAllQuery();
                if (!StringUtils.isEmpty(findAllQL)) {
                    repository.setFindAllQL(findAllQL);
                }
                Class<? extends SearchCallback> callbackClass = searchableQuery.callbackClass();
                if (callbackClass != null && callbackClass != SearchCallback.class) {
                    repository.setSearchCallback(BeanUtils.instantiateClass(callbackClass));
                }

                repository.setJoins(searchableQuery.joins());

            }

            return repository;
        }
        return super.getTargetRepository((RepositoryInformation)metadata);
    }

    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isBaseRepository(metadata.getRepositoryInterface())) {
            return SimpleBaseRepository.class;
        }
        return super.getRepositoryBaseClass(metadata);
    }

    private boolean isBaseRepository(Class<?> repositoryInterface) {
        return BaseRepository.class.isAssignableFrom(repositoryInterface);
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return super.getQueryLookupStrategy(key, evaluationContextProvider);
    }
}
