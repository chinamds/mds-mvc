/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao.hibernate;

import com.mds.aiotplayer.common.dao.BaseDaoTestCase;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.mapping.Table;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This class runs a SELECT * of all mapped objects. If an object's
 * corresponding table does not exist in the database, the test will fail.
 */
public class HibernateConfigurationTest extends BaseDaoTestCase {
    @Autowired
    SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @Test
    public void testColumnMapping() throws Exception {
    	Session session = sessionFactory.openSession();
    	try {
	    	for(Namespace namespace : EntityMetaData.getMeta()
	    		    .getDatabase()
	    		    .getNamespaces()) {
	    		 
	    		    for( Table table : namespace.getTables()) {
	    		    	log.debug("Table" + table + " has the following columns: " + 
	    		             StreamSupport.stream(
	    		                Spliterators.spliteratorUnknownSize( 
	    		                    table.getColumnIterator(), 
	    		                    Spliterator.ORDERED
	    		                ), 
	    		                false
	    		            )
	    		            .collect( Collectors.toList()) 
	    		        );
	    		    }
	    		}
    	 } finally {
    		 session.close();
         }
    }
}
