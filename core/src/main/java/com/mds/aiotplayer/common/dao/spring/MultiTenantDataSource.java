/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
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
