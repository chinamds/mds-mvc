/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.service;

import com.mds.aiotplayer.common.model.User;
import com.mds.aiotplayer.common.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-17 下午7:52
 * <p>Version: 1.0
 */
@DependsOn("userRepository")
@Service()
public class UserService extends BaseService<User, Long> {

    @Autowired
    @Qualifier("userRepository")
    @Override
    public void setBaseRepository(BaseRepository<User, Long> baseRepository) {
        super.setBaseRepository(baseRepository);
    }
}
