/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.util;

public class TenantContext {

  private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

  public static String getCurrentTenant() {
    return currentTenant.get();
  }

  public static void setCurrentTenant(String tenant) {
    currentTenant.set(tenant);
  }

  public static void clear() {
    currentTenant.set(null);
  }

}