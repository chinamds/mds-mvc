/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.servicemanager.fakeservices;

import java.io.Serializable;

import com.mds.kernel.mixins.InitializedService;


/**
 * Simple fake service 2
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class FakeService2 implements InitializedService, Comparable<FakeService2>, Serializable {
    private static final long serialVersionUID = 1L;

    public String data = "data";

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.mixins.InitializedService#init()
     */
    public void init() {
        data = "initData";
    }

    public int compareTo(FakeService2 o) {
        return data.compareTo(o.data);
    }

}
