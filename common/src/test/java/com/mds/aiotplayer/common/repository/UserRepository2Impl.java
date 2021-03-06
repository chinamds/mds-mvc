/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.repository;

import com.mds.aiotplayer.common.model.BaseInfo;
import com.mds.aiotplayer.common.model.SchoolInfo;
import com.mds.aiotplayer.common.model.User;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.repository.callback.DefaultSearchCallback;
import com.mds.aiotplayer.common.repository.callback.SearchCallback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * <p>跟以前普通DAO实现一样，无需加@Repository，系统会自动扫描，并加到相关的Repository接口中，实现格式：</p>
 * <pre>
 *     JpaRepository接口+<jpa:repositories repository-impl-postfix="Impl"></jpa:repositories>中的repository-impl-postfix
 *     repository-impl-postfix默认为Impl
 * </pre>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 下午4:30
 * <p>Version: 1.0
 */
public class UserRepository2Impl {

    private String findAllQL = "from User o where 1=1 ";
    private String countAllQL = "select count(o) from User o where 1=1 ";


    @PersistenceContext
    private EntityManager entityManager;

    private RepositoryHelper repositoryHelper = new RepositoryHelper(User.class);

    public BaseInfo findBaseInfoByUserId(Long userId) {
        String ql = "select bi from BaseInfo bi where bi.user.id=?1";
        Query query = entityManager.createQuery(ql);
        query.setParameter(1, userId);
        query.setMaxResults(1);
        List<BaseInfo> baseInfoList = query.getResultList();
        if (baseInfoList.size() > 0) {
            return baseInfoList.get(0);
        }
        return null;
    }

    public List<SchoolInfo> findAllSchoolTypeByUserId(Long userId) {
        String ql = "select si from SchoolInfo si where si.user.id=?1";
        Query query = entityManager.createQuery(ql);
        query.setParameter(1, userId);
        return query.getResultList();
    }


    /**
     * 按条件分页/排序查询，
     *
     * @param searchable
     * @return
     */
    public Page<User> findAllByDefault(final Searchable searchable) {
        long total = countAllByDefault(searchable);
        List<User> contentList = repositoryHelper.findAll(findAllQL, searchable, SearchCallback.DEFAULT);
        return new PageImpl(contentList, searchable.getPage(), total);
    }

    /**
     * 按条件统计
     *
     * @param searchable
     * @return
     */
    public long countAllByDefault(final Searchable searchable) {
        return repositoryHelper.count(countAllQL, searchable, SearchCallback.DEFAULT);
    }


    private SearchCallback customSearchCallback = new DefaultSearchCallback() {
        @Override
        public void prepareQL(StringBuilder hql, Searchable search) {
            if (search.containsSearchKey("realname")) {
                hql.append(" and exists(select 1 from BaseInfo bi where o = bi.user and bi.realname like :realname )");
            }
        }

        @Override
        public void setValues(Query query, Searchable search) {
            if (search.containsSearchKey("realname")) {
                query.setParameter("realname", "%" + search.getValue("realname") + "%");
            }
        }
    };

    /**
     * 按条件统计
     *
     * @param searchable
     * @return
     */
    public long countAllByCustom(final Searchable searchable) {
        return repositoryHelper.count(countAllQL, searchable, customSearchCallback);
    }

    /**
     * 按条件分页/排序查询，
     *
     * @param searchable
     * @return
     */
    public Page<User> findAllByCustom(final Searchable searchable) {
        long total = countAllByCustom(searchable);
        List<User> contentList = repositoryHelper.findAll(findAllQL, searchable, customSearchCallback);
        return new PageImpl(contentList, searchable.hasPageable() ? searchable.getPage() :  Pageable.unpaged(), total);
    }


}
