package com.mds.aiotplayer.common.dao.hibernate;

import java.io.Serializable;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.EmptyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mds.aiotplayer.common.exception.DataSegmentationException;
import com.mds.aiotplayer.common.service.TenantResolver;
import com.mds.aiotplayer.sys.model.Tenant;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.StringUtils;

public class MultiTenantEntityInterceptor extends EmptyInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2372143420877673397L;

	private TenantResolver tenantResolver;

	@Autowired
	public void setTenantResolver(TenantResolver tenantResolver) {
		this.tenantResolver = tenantResolver;
	}
		
	public MultiTenantEntityInterceptor() {
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, org.hibernate.type.Type[] types) {
		return this.handleTenant(entity, id, currentState, propertyNames, types);
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames,
			org.hibernate.type.Type[] types) {
		return this.handleTenant(entity, id, state, propertyNames, types);
	}

	private boolean handleTenant(Object entity, Serializable id, Object[] currentState, String[] propertyNames,
			org.hibernate.type.Type[] types) {

		int index = ArrayUtils.indexOf(propertyNames, "tenant");
		if (index < 0) {
			return false;
		}

		String activeTenantId = this.tenantResolver.getTenantId();
		Tenant tenant = (Tenant)currentState[index];

		// on a new entity, set tenant id to current tenant
		if (tenant == null) {
			currentState[index] = UserUtils.getTenant(activeTenantId);
			return true;
		}
		/*if (tenantId == null || StringUtils.isEmpty(tenantId.toString())) {
			currentState[index] = activeTenantId;
			return true;
		}*/

		// on update, block cross tenant attempt
		else if (!tenant.getId().equals(activeTenantId)) {
			throw new DataSegmentationException(
					"cross tenant update, tenantId=" + tenant.getId() + ", activeTenantId=" + activeTenantId);
		}

		return true;
	}
}
