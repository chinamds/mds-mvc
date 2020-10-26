/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.mds.aiotplayer.sys.dao.UserAddressDao;
import com.mds.aiotplayer.sys.model.UserAddress;
import com.mds.aiotplayer.sys.service.UserAddressManager;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("userAddressManager")
@WebService(serviceName = "UserAddressService1", endpointInterface = "com.mds.aiotplayer.sys.service.UserAddressManager")
public class UserAddressManagerImpl extends GenericManagerImpl<UserAddress, Long> implements UserAddressManager {
    UserAddressDao userAddressDao;

    @Autowired
    public UserAddressManagerImpl(UserAddressDao userAddressDao) {
        super(userAddressDao);
        this.userAddressDao = userAddressDao;
    }
}