/**
 * Copyright (c) 2016-2017 https://github.com/chinamds
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.model;

/**
 * Authorization type
 * <p>User: John Lee
 * <p>Date: 07/09/2017 17:36:34
 * <p>Version: 1.0
 */
public enum AuthType {

    user("User"), user_group("User Group"), department("Department"), company("Company"), area("Area");

    private final String info;

    private AuthType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
