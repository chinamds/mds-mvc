/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.repository.hibernate;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.model.search.filter.Condition;
import com.mds.aiotplayer.common.model.search.filter.SearchFilter;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.SearchFactory;

/**
 * Utility class to generate lucene queries for hibernate search and perform full reindexing.
 *
 * @author jgarcia
 */
public class HibernateSearchTools {
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
    public static Query generateQuery(String searchTerm, Class searchedEntity, FullTextEntityManager txtSession, Analyzer defaultAnalyzer) throws ParseException {
        Query qry = null;

        if (searchTerm.equals("*")) {
            qry = new MatchAllDocsQuery();
        } else {
            // Search in all indexed fields

            //IndexReaderAccessor readerAccessor = null;
            IndexReader reader = null;           
        	SearchFactory searchFactory = txtSession.getSearchFactory();
        	
            // obtain analyzer to parse the query:
            Analyzer analyzer;
            if (searchedEntity == null) {
                analyzer = defaultAnalyzer;
            } else {
                analyzer = searchFactory.getAnalyzer(searchedEntity);
            }

            // search on all indexed fields: generate field list, removing internal hibernate search field name: _hibernate_class
            // TODO: possible improvement: cache the fields of each entity
            reader = searchFactory.getIndexReaderAccessor().open(searchedEntity);
            Collection<String> fieldNames = new HashSet<>();
            try {
            	FieldInfos fieldInfos = MultiFields.getMergedFieldInfos(reader);
                for (FieldInfo fieldInfo : fieldInfos) {
                	String[] properties = StringUtils.split(fieldInfo.name, ".");
                    if (fieldInfo.getIndexOptions() != IndexOptions.NONE 
                    		&& !properties[properties.length-1].equalsIgnoreCase("id")) {
                        fieldNames.add(fieldInfo.name);
                    }
                }
            }finally {
               searchFactory.getIndexReaderAccessor().close(reader);
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
    
    public static BooleanJunction generateQuery(QueryBuilder querybuilder, Searchable search) {
    	BooleanJunction query = querybuilder.bool();
    	for (SearchFilter searchFilter : search.getSearchFilters()) {
            if (searchFilter instanceof Condition) {
            	Query q = null;
            	boolean mustNot = false;
                Condition condition = (Condition) searchFilter;
                if (condition.getOperator() == SearchOperator.eq)
                	q = querybuilder.keyword().onField(condition.getSearchProperty()).matching(condition.getValue().toString()).createQuery();
	       		 else if (condition.getOperator() == SearchOperator.eqi)
	       			q = querybuilder.keyword().onField(condition.getSearchProperty()).matching(condition.getValue().toString()).createQuery();
	       		 else if (condition.getOperator() == SearchOperator.ne) {
	       			mustNot = true;
	       			q = querybuilder.keyword().onField(condition.getSearchProperty()).matching(condition.getValue().toString()).createQuery();
	       		 } else if (condition.getOperator() == SearchOperator.gt)
	       			q = querybuilder.range().onField(condition.getSearchProperty()).above(condition.getValue()).excludeLimit().createQuery();
	       		 else if (condition.getOperator() == SearchOperator.gte)
	       			q = querybuilder.range().onField(condition.getSearchProperty()).above(condition.getValue()).createQuery();
	       		 else if (condition.getOperator() == SearchOperator.lt)
	       			q = querybuilder.range().onField(condition.getSearchProperty()).below(condition.getValue()).excludeLimit().createQuery();
	       		 else if (condition.getOperator() == SearchOperator.lte)
	       			q = querybuilder.range().onField(condition.getSearchProperty()).below(condition.getValue()).createQuery();
	       		 else if (condition.getOperator() == SearchOperator.bt) 
	       			q = querybuilder.range().onField(condition.getSearchProperty()).from(condition.getValue()).to(condition.getValue2()).createQuery();
	       		 else if (condition.getOperator() == SearchOperator.prefixLike) //PrefixQuery
	       			q = querybuilder.keyword().wildcard().onField(condition.getSearchProperty()).matching(condition.getValue().toString() + "*").createQuery();
	       		 else if (condition.getOperator() == SearchOperator.prefixNotLike) {
	       			mustNot = true;
	       			q = querybuilder.keyword().wildcard().onField(condition.getSearchProperty()).matching(condition.getValue().toString() + "*").createQuery();
	       		 }else if (condition.getOperator() == SearchOperator.suffixLike)
	       			q = querybuilder.keyword().wildcard().onField(condition.getSearchProperty()).matching("*" + condition.getValue().toString()).createQuery();
	       		 else if (condition.getOperator() == SearchOperator.suffixNotLike) {
	       			mustNot = true;
	       			q = querybuilder.keyword().wildcard().onField(condition.getSearchProperty()).matching("*" + condition.getValue().toString()).createQuery();
	       		 }else if (condition.getOperator() == SearchOperator.like)
	       			q = querybuilder.keyword().wildcard().onField(condition.getSearchProperty()).matching("*" + condition.getValue().toString() + "*").createQuery();
	       		 else if (condition.getOperator() == SearchOperator.notLike) {
	       			mustNot = true;
	       			q = querybuilder.keyword().wildcard().onField(condition.getSearchProperty()).matching("*" + condition.getValue().toString() + "*").createQuery();
	       		 }else if (condition.getOperator() == SearchOperator.contain)
	       			q = querybuilder.keyword().fuzzy().onField(condition.getSearchProperty()).matching(condition.getValue().toString()).createQuery();
	       		 else if (condition.getOperator() == SearchOperator.notContain) {
	       			mustNot = true;
	       			q = querybuilder.keyword().fuzzy().onField(condition.getSearchProperty()).matching(condition.getValue().toString()).createQuery();
	       		 }else if (condition.getOperator() == SearchOperator.isNull)
	       			q = querybuilder.keyword().onField(condition.getSearchProperty()).matching("").createQuery();
	       		 else if (condition.getOperator() == SearchOperator.isNotNull) {
	       			mustNot = true;
	       			q = querybuilder.keyword().onField(condition.getSearchProperty()).matching("").createQuery();
	       		 }else if (condition.getOperator() == SearchOperator.in || condition.getOperator() == SearchOperator.notIn) {
	       			 Object value = condition.getValue();
	       			 BooleanJunction queryIn = querybuilder.bool();
	       			 if(value instanceof Collection<?>){
	       				 for(Object obj : ((Collection<?>)value)) {
       						queryIn = queryIn.should(querybuilder.keyword().onField(condition.getSearchProperty()).matching(obj.toString()).createQuery());
	       				 }
	                  }else if(value instanceof Object[]){
	                	 for(Object obj : ((Object[])value)) {
	                		 queryIn = queryIn.should(querybuilder.keyword().onField(condition.getSearchProperty()).matching(obj.toString()).createQuery());
	       				 }
	                  }
	       			  q = queryIn.createQuery();
	       			  mustNot = (condition.getOperator() == SearchOperator.notIn);
	       		 }
                
                if (q != null) {
               		query = mustNot ? query.must(q).not() : query.must(q);
                }
            }
        }
    	
    	return query;
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
    public static Query generateQuery(String searchTerm, Class searchedEntity, String[] fnames, FullTextEntityManager txtSession, Analyzer defaultAnalyzer) throws ParseException {
        Query qry = null;

        if (searchTerm.equals("*")) {
            qry = new MatchAllDocsQuery();
        } else {
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
                queries[i] = searchTerm; // + "*";
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
    
    public static org.apache.lucene.search.Sort prepareOrder(Searchable search, Class searchedEntity) {
    	List<SortField> sortFields = Lists.newArrayList();
        for (Sort.Order order : search.getSort()) {
        	String[] properties = StringUtils.split(order.getProperty(), ".");
        	Field f = null;
         	if (properties.length > 1) { //&& !properties[0].equalsIgnoreCase("parent")
         		f = FieldUtils.getDeclaredField(searchedEntity, properties[0], true);
         		Class<?> valType = f.getType();
         		for(int i=1; i<properties.length; i++) {
         			f = FieldUtils.getDeclaredField(valType, properties[i], true);
         			valType = f.getType();
         		}
         	}else {
         		f = FieldUtils.getDeclaredField(searchedEntity, order.getProperty(), true);
         	}
         	         	
        	//Field f = FieldUtils.getDeclaredField(this.persistentClass, order.getProperty(), true);
        	if (f!= null) {
	        	Class<?> valType = f.getType();
	        	if (valType == String.class || valType == Date.class) {
	        		sortFields.add(new SortField(order.getProperty(), SortField.Type.STRING, order.getDirection() == Direction.DESC));
	        	} else if (valType == int.class || valType == Integer.class) {
	        		sortFields.add(new SortField(order.getProperty(), SortField.Type.INT, order.getDirection() == Direction.DESC));
	        	}else if (valType == long.class || valType == Long.class) {
	        		sortFields.add(new SortField(order.getProperty(), SortField.Type.LONG, order.getDirection() == Direction.DESC));
	        	}else if (valType == float.class || valType == Float.class) {
	        		sortFields.add(new SortField(order.getProperty(), SortField.Type.FLOAT, order.getDirection() == Direction.DESC));
	        	}else if (valType == double.class || valType == Double.class) {
	        		sortFields.add(new SortField(order.getProperty(), SortField.Type.DOUBLE, order.getDirection() == Direction.DESC));
	        	}else if (valType == int.class || valType == Integer.class) {
	        		sortFields.add(new SortField(order.getProperty(), SortField.Type.INT, order.getDirection() == Direction.DESC));
	        	}else {
	        		sortFields.add(new SortField(order.getProperty(), SortField.Type.DOC));
	        	}
	        	/*if (order.getDirection() == Direction.ASC)
	        		sortFields.add(new SortField(order.getProperty(), SortField.Type.DOC));
	        	else
	        		sortFields.add(new SortField(order.getProperty(), SortField.Type.SCORE));*/
        	}
        }
        
        return new org.apache.lucene.search.Sort(sortFields.toArray(new SortField[0]));
    }


    /**
     * Regenerates the index for a given class
     *
     * @param clazz the class
     * @param sess the hibernate session
     */
    public static void reindex(Class clazz, EntityManager entityManager) {
    	FullTextEntityManager txtSession = Search.getFullTextEntityManager(entityManager);
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
    public static void reindexAll(boolean async, EntityManager entityManager) {
    	FullTextEntityManager txtSession = Search.getFullTextEntityManager(entityManager);
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
    public static javax.persistence.Query databaseQuery(String queryBackup, EntityManager entityManager) {
    	FullTextEntityManager txtSession = Search.getFullTextEntityManager(entityManager);
        
        return txtSession.createNativeQuery(queryBackup);
    }
}
