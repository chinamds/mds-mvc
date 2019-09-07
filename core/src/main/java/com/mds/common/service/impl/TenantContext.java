package com.mds.common.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mds.common.service.TenantResolver;
import com.mds.sys.util.UserAccount;

@Component
public class TenantContext implements TenantResolver {

  //private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

  public static String getCurrentTenant() {
    //return currentTenant.get();
	  SecurityContext ctx = SecurityContextHolder.getContext();
	  if (ctx != null) {
		  Authentication auth = ctx.getAuthentication();
		  if (auth != null){
			  if (auth != null && auth.getPrincipal() instanceof UserAccount) {
				  return ((UserAccount)auth.getPrincipal()).getTenantId();
			  }
		  }
	  }
	  
	  return null;
  }

  public static void setCurrentTenant(String tenant) {
    //currentTenant.set(tenant);
  }

  public static void clear() {
    //currentTenant.set(null);
  }
  
  @Override
  public String getTenantId() {
	  return TenantContext.getCurrentTenant();
  }

}