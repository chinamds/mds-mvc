package com.mds.sys.model;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.annotations.Key;
import org.hibernate.search.filter.FilterKey;
import org.hibernate.search.filter.StandardFilterKey;

public class NotificationUserFilterFactory {
    private Long userId;

    /**
     * injected parameter
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Key
    public FilterKey getKey() {
    	StandardFilterKey key = new StandardFilterKey();  
        key.addParameter(userId);   
        
        return key;
    }

    @Factory
    public Filter getFilter() {
        Query query = new TermQuery( new Term("user.id", userId.toString()));
        return new CachingWrapperFilter( new QueryWrapperFilter(query) );
    }
}
