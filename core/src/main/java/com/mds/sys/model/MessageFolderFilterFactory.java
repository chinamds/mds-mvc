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

public class MessageFolderFilterFactory {
    private String folder;

    /**
     * injected parameter
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Key
    public FilterKey getKey() {
    	/*MessageFolderFilter key = new MessageFolderFilter();
        key.setMessageFolder(folder.toString());*/
    	StandardFilterKey key = new StandardFilterKey();  
        key.addParameter(folder);   
                
        return key;
    }

    @Factory
    public Filter getFilter() {
        Query query = new TermQuery( new Term("messageFolder", folder.toString()));
        return new CachingWrapperFilter( new QueryWrapperFilter(query) );
    }
}
