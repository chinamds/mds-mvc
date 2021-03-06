/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.dao.hibernate;

import com.mds.aiotplayer.sys.model.UserAddress;
import com.mds.aiotplayer.sys.dao.UserAddressDao;
import com.mds.aiotplayer.common.dao.hibernate.GenericDaoHibernate;
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
        var result = super.save(userAddress);
        // necessary to throw a DataIntegrityViolation and catch it in UserAddressManager
        getEntityManager().flush();
        return result;
    }
}
