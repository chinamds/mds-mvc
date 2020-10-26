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
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;

import java.util.List;


/**
 * <p>用户仓库</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 下午2:18
 * <p>Version: 1.0
 */
public interface UserRepository2 extends BaseRepository<User, Long> {


    ////////////////////////////////////////////////////
    /////////以下实现都委托给UserRepository2Impl///////
    ////////////////////////////////////////////////////

    public BaseInfo findBaseInfoByUserId(Long userId);

    public List<SchoolInfo> findAllSchoolTypeByUserId(Long userId);

    public Page<User> findAllByDefault(final Searchable searchable);

    public long countAllByDefault(final Searchable searchable);

    public long countAllByCustom(final Searchable searchable);

    public Page<User> findAllByCustom(final Searchable searchable);

}
