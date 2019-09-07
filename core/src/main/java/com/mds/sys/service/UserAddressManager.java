package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.UserAddress;

import java.util.List;
import javax.jws.WebService;

@WebService
public interface UserAddressManager extends GenericManager<UserAddress, Long> {
    
}