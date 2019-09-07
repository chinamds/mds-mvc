/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.sys.model;

/**
 * notification trigger by
 * <p>User: Zhang Kaitao
 * <p>Date: 2017/8/18 12:55
 * <p>Version: 1.0
 */
public enum NotificationSource {

    system("system"), excel("excel");

    private final String info;

    private NotificationSource(final String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

}
