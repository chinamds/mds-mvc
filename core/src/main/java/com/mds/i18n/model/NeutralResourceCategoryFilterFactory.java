package com.mds.i18n.model;

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

public class NeutralResourceCategoryFilterFactory {
    private String category;

    /**
     * injected parameter
     */
    public void setCategory(String category) {
        this.category = category;
    }

    @Key
    public FilterKey getKey() {
        StandardFilterKey key = new StandardFilterKey();
        key.addParameter( category );
        return key;
    }

    @Factory
    public Filter getFilter() {
        Query query = new TermQuery( new Term("resourceClass", category));
        return new CachingWrapperFilter( new QueryWrapperFilter(query) );
    }
}
