 /**
 * Copyright &copy; 2012-2016 <a href="https://github.com/chinamds/mds">mds</a> All rights reserved.
 */
package com.mds.sys.model;

/**
 * <p>User: John Lee
 * <p>Date: 23/08/2017 15:12:07
 * <p>Version: 1.0
 */
public enum UserStatus {
	disabled(0, "user.userstatus.disabled"),
    enabled(1, "user.userstatus.enabled"), 
	accountLocked(2, "user.userstatus.locked"),
	accountExpired(4, "user.userstatus.accountExpired"),
	credentialsExpired(8, "user.userstatus.credentialsExpired");

	private final String info;
    private final int status;
    

    private UserStatus(int status, String info) {
    	this.info = info;
        this.status = status;
    }
    
    public String getInfo() {
        return info;
    }

    public int getStatus() {
        return status;
    }
}
