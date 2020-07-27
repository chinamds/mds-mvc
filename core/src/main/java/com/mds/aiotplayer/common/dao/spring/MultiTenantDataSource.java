package com.mds.aiotplayer.common.dao.spring;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.mds.aiotplayer.common.service.TenantResolver;


public class MultiTenantDataSource extends AbstractRoutingDataSource {

	private TenantResolver tenantResolver;

	public MultiTenantDataSource(TenantResolver tenantResolver) {
		this.tenantResolver = tenantResolver;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return tenantResolver.getTenantId();
	}
}
