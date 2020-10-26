/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.service;

import com.mds.aiotplayer.common.repository.UserRepository2;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-17 下午7:55
 * <p>Version: 1.0
 */
public class UserServiceWithRespository2ImplTest extends UserServiceTest {


    @Autowired
    private UserService2 userService2;

    @Before
    public void setUp() {
        userService = userService2;
    }


}
