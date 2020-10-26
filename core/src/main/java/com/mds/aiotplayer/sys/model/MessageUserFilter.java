/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

import org.hibernate.search.filter.FilterKey;

public class MessageUserFilter extends FilterKey {
    private String userId;

    @Override
    public boolean equals(Object otherKey) {
        if(this.userId == null || !(otherKey instanceof MessageUserFilter)) {
            return false;
        }
        MessageUserFilter otherMessageUserFilterKey = (MessageUserFilter) otherKey;
        return otherMessageUserFilterKey.userId != null && this.userId.equals(otherMessageUserFilterKey.userId);
    }

    @Override
    public int hashCode() {
        if(this.userId == null) {
            return 0;
        }
        return this.userId.hashCode();
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return this.userId;
    }
}