/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao.hibernate;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import com.mds.aiotplayer.common.service.TenantResolver;


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
