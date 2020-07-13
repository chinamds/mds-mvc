package com.mds.common.dao.hibernate;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mds.common.model.Page;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;
import com.mds.common.model.search.filter.Condition;
import com.mds.common.model.search.filter.SearchFilter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.indexes.IndexReaderAccessor;

/**
 * Utility class to generate lucene queries for hibernate search and perform full reindexing.
 *
 * @author jgarcia
 */
class HibernateSearchTools {
    protected static final Logger log = LoggerFactory.getLogger(HibernateSearchTools.class);

    /**
     * Generates a lucene query to search for a given term in all the indexed fields of a class
     *
     * @param searchTerm the term to search for
     * @param searchedEntity the class searched
     * @param sess the hibernate session
     * @param defaultAnalyzer the default analyzer for parsing the search terms
     * @return
     * @throws ParseException
     */
    public static Query generateQuery(String searchTerm, Class searchedEntity, Session sess, Analyzer defaultAnalyzer) throws ParseException {
        Query qry = null;

        if (searchTerm.equals("*")) {
            qry = new MatchAllDocsQuery();
        } else {
            // Search in all indexed fields

            IndexReaderAccessor readerAccessor = null;
            IndexReader reader = null;
            try {
                FullTextSession txtSession = Search.getFullTextSession(sess);

                // obtain analyzer to parse the query:
                Analyzer analyzer;
                if (searchedEntity == null) {
                    analyzer = defaultAnalyzer;
                } else {
                    analyzer = txtSession.getSearchFactory().getAnalyzer(searchedEntity);
                }

                // search on all indexed fields: generate field list, removing internal hibernate search field name: _hibernate_class
                // TODO: possible improvement: cache the fields of each entity
                SearchFactory searchFactory = txtSession.getSearchFactory();
                readerAccessor = searchFactory.getIndexReaderAccessor();
                reader = readerAccessor.open(searchedEntity);
                Collection<String> fieldNames = new HashSet<>();
                for (FieldInfo fieldInfo : MultiFields.getMergedFieldInfos(reader)) {
                    if (fieldInfo.getIndexOptions() != IndexOptions.NONE) {
                        fieldNames.add(fieldInfo.name);
                    }
                }
                fieldNames.remove("_hibernate_class");
                String[] fnames = new String[0];
                fnames = fieldNames.toArray(fnames);

                // To search on all fields, search the term in all fields
                String[] queries = new String[fnames.length];
                for (int i = 0; i < queries.length; ++i) {
                    queries[i] = searchTerm;
                }

                qry = MultiFieldQueryParser.parse(queries, fnames, analyzer);
            } finally {
                if (readerAccessor != null && reader != null) {
                    readerAccessor.close(reader);
                }
            }
        }
        return qry;
    }
    
    public static BooleanQuery generateQuery(Searchable search) {
    	BooleanQuery.Builder query = new BooleanQuery.Builder();
    	for (SearchFilter searchFilter : search.getSearchFilters()) {
            if (searchFilter instanceof Condition) {
                Condition condition = (Condition) searchFilter;
                if (condition.getOperator() == SearchOperator.eq)
                	query.add(new TermQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.eqi)
	       			query.add(new TermQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.ne)
	       			query.add(new TermQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST_NOT);
	       		 else if (condition.getOperator() == SearchOperator.gt)
	       			query.add(new TermQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.gte)
	       			query.add(new TermQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.lt)
	       			query.add(new TermQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.lte)
	       			query.add(new TermQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.bt) 
	       			query.add(TermRangeQuery.newStringRange(condition.getSearchProperty(), condition.getValue().toString(), condition.getValue2().toString(), true, true), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.prefixLike) //PrefixQuery
	       			query.add(new WildcardQuery(new Term(condition.getSearchProperty(), condition.getValue().toString() + "*")), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.prefixNotLike)
	       			query.add(new WildcardQuery(new Term(condition.getSearchProperty(), condition.getValue().toString() + "*")), Occur.MUST_NOT);
	       		 else if (condition.getOperator() == SearchOperator.suffixLike)
	       			query.add(new WildcardQuery(new Term(condition.getSearchProperty(), "*" + condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.suffixNotLike)
	       			query.add(new WildcardQuery(new Term(condition.getSearchProperty(), "*" + condition.getValue().toString())), Occur.MUST_NOT);
	       		 else if (condition.getOperator() == SearchOperator.like)
	       			query.add(new WildcardQuery(new Term(condition.getSearchProperty(), "*" + condition.getValue().toString()+ "*")), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.notLike)
	       			query.add(new WildcardQuery(new Term(condition.getSearchProperty(), "*" + condition.getValue().toString()+ "*")), Occur.MUST_NOT);
	       		 else if (condition.getOperator() == SearchOperator.contain)
	       			query.add(new FuzzyQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.notContain)
	       			query.add(new FuzzyQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST_NOT);
	       		 else if (condition.getOperator() == SearchOperator.isNull)
	       			query.add(new TermQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.isNotNull)
	       			query.add(new TermQuery(new Term(condition.getSearchProperty(), condition.getValue().toString())), Occur.MUST);
	       		 else if (condition.getOperator() == SearchOperator.in) {
	       			 BooleanQuery.Builder queryIn = new BooleanQuery.Builder();
	       			 Object value = condition.getValue();
	       			 if(value instanceof Collection<?>){
	       				 for(Object obj : ((Collection<?>)value)) {
	       					/*if (obj.getClass() == Long.class){
	       						queryIn.add(NumericRangeQuery.newLongRange(condition.getSearchProperty(), (long)obj, (long)obj, true, true), Occur.SHOULD);
	       					}else {*/
	       						queryIn.add(new TermQuery(new Term(condition.getSearchProperty(), obj.toString())), Occur.SHOULD);
	       					//}
	       				 }
	                  }else if(value instanceof Object[]){
	                	 for(Object obj : ((Object[])value)) {
	                		 /*if (obj.getClass() == Long.class){
	       						queryIn.add(NumericRangeQuery.newLongRange(condition.getSearchProperty(), (long)obj, (long)obj, true, true), Occur.SHOULD);
	       					}else {*/
	       						queryIn.add(new TermQuery(new Term(condition.getSearchProperty(), obj.toString())), Occur.SHOULD);
	       					//}
	       				 }
	                  }
	       			  query.add(queryIn.build(), Occur.MUST);
	       		 }
	       		 else if (condition.getOperator() == SearchOperator.notIn){
	       			BooleanQuery.Builder queryIn = new BooleanQuery.Builder();
	       			 Object value = condition.getValue();
	       			 if(value instanceof Collection<?>){
	       				 for(Object obj : ((Collection<?>)value)) {
	       					queryIn.add(new TermQuery(new Term(condition.getSearchProperty(), obj.toString())), Occur.SHOULD);
	       				 }
	                  }else if(value instanceof Object[]){
	                	 for(Object obj : ((Object[])value)) {
	       					queryIn.add(new TermQuery(new Term(condition.getSearchProperty(), obj.toString())), Occur.SHOULD);
	       				 }
	                  }
	       			  query.add(queryIn.build(), Occur.MUST_NOT);
	       		 }
            }
        }
    	
    	return query.build();
    }
    
    /**
     * Generates a lucene query to search for a given term in the specified fields of a class
     *
     * @param searchTerm the term to search for
     * @param searchedEntity the class searched
     * @param fnames the specified fields of searchedEntity searched
     * @param sess the hibernate session
     * @param defaultAnalyzer the default analyzer for parsing the search terms
     * @return
     * @throws ParseException
     */
    public static Query generateQuery(String searchTerm, Class searchedEntity, String[] fnames, Session sess, Analyzer defaultAnalyzer) throws ParseException {
        Query qry = null;

        if (searchTerm.equals("*")) {
            qry = new MatchAllDocsQuery();
        } else {
            // Search in all indexed fields
            FullTextSession txtSession = Search.getFullTextSession(sess);

            // obtain analyzer to parse the query:
            Analyzer analyzer;
            if (searchedEntity == null) {
                analyzer = defaultAnalyzer;
            } else {
                analyzer = txtSession.getSearchFactory().getAnalyzer(searchedEntity);
            }

            // To search on specified fields, search the term in specified fields
            String[] queries = new String[fnames.length];
            for (int i = 0; i < queries.length; ++i) {
                queries[i] = searchTerm + "*";
            }

            qry = MultiFieldQueryParser.parse(queries, fnames, analyzer);
              /*BooleanQuery bQuery = new BooleanQuery();
              for (int i = 0; i < fnames.length; i++)
              {
				Term t = new Term(fnames[i], searchTerm + "*");
				WildcardQuery q = new WildcardQuery(t);
				if (q!=null) {
				  bQuery.add(q, BooleanClause.Occur.SHOULD);
				}
              }
              
             return bQuery;*/

        }
        return qry;
    }
    

    /**
     * Regenerates the index for a given class
     *
     * @param clazz the class
     * @param sess the hibernate session
     */
    public static void reindex(Class clazz, Session sess) {
        FullTextSession txtSession = Search.getFullTextSession(sess);
        MassIndexer massIndexer = txtSession.createIndexer(clazz);
        try {
            massIndexer.startAndWait();
        } catch (InterruptedException e) {
            log.error("mass reindexing interrupted: " + e.getMessage());
        } finally {
            txtSession.flushToIndexes();
        }
    }

    /**
     * Regenerates all the indexed class indexes
     *
     * @param async true if the reindexing will be done as a background thread
     * @param sess the hibernate session
     */
    public static void reindexAll(boolean async, Session sess) {
        FullTextSession txtSession = Search.getFullTextSession(sess);
        MassIndexer massIndexer = txtSession.createIndexer();
        massIndexer.purgeAllOnStart(true);
        try {
            if (!async) {
                massIndexer.startAndWait();
            } else {
                massIndexer.start();
            }
        } catch (InterruptedException e) {
            log.error("mass reindexing interrupted: " + e.getMessage());
        } finally {
            txtSession.flushToIndexes();
        }
    }
    
    /**
     * Regenerates all the indexed class indexes
     *
     * @param async true if the reindexing will be done as a background thread
     * @param sess the hibernate session
     */
    public static SQLQuery databaseQuery(String queryBackup, Session sess) {
        FullTextSession txtSession = Search.getFullTextSession(sess);
        
        return txtSession.createSQLQuery(queryBackup);
    }
}
