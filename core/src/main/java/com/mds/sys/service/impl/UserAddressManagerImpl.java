package com.mds.sys.service.impl;

import com.mds.sys.dao.UserAddressDao;
import com.mds.sys.model.UserAddress;
import com.mds.sys.service.UserAddressManager;
import com.mds.common.service.impl.GenericManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.jws.WebService;

@Service("userAddressManager")
@WebService(serviceName = "UserAddressService1", endpointInterface = "com.mds.sys.service.UserAddressManager")
public class UserAddressManagerImpl extends GenericManagerImpl<UserAddress, Long> implements UserAddressManager {
    UserAddressDao userAddressDao;

    @Autowired
    public UserAddressManagerImpl(UserAddressDao userAddressDao) {
        super(userAddressDao);
        this.userAddressDao = userAddressDao;
    }
}