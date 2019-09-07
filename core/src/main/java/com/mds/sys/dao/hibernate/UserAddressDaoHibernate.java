package com.mds.sys.dao.hibernate;

import com.mds.sys.model.UserAddress;
import com.mds.sys.dao.UserAddressDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import org.springframework.stereotype.Repository;

@Repository("userAddressDao")
public class UserAddressDaoHibernate extends GenericDaoHibernate<UserAddress, Long> implements UserAddressDao {

    public UserAddressDaoHibernate() {
        super(UserAddress.class);
    }

	/**
     * {@inheritDoc}
     */
    public UserAddress saveUserAddress(UserAddress userAddress) {
        if (log.isDebugEnabled()) {
            log.debug("userAddress's id: " + userAddress.getId());
        }
        getSession().saveOrUpdate(userAddress);
        // necessary to throw a DataIntegrityViolation and catch it in UserAddressManager
        getSession().flush();
        return userAddress;
    }
}
