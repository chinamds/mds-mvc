package com.mds.common.dao.hibernate;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import com.mds.common.service.TenantResolver;


public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

	private TenantResolver tenantResolver;

	public CurrentTenantIdentifierResolverImpl(TenantResolver tenantResolver) {
		this.tenantResolver = tenantResolver;
	}

	public String resolveCurrentTenantIdentifier() {
		return this.tenantResolver.getTenantId();
	}

	public boolean validateExistingCurrentSessions() {
		return true;
	}
}
