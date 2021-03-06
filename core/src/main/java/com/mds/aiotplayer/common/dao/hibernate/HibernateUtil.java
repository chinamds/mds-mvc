/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
  private static StandardServiceRegistry standardServiceRegistry;
  private static SessionFactory sessionFactory;

  static{
      if (sessionFactory == null) {    	  
    	  try {
		      standardServiceRegistry = new StandardServiceRegistryBuilder()
		          .configure()
		          .build();
		      MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);
		      Metadata metadata = metadataSources.getMetadataBuilder().build();
		      sessionFactory = metadata.getSessionFactoryBuilder().applyInterceptor(new MultiTenantEntityInterceptor()).build();
		    } catch (Exception e) {
		      e.printStackTrace();
		      if (standardServiceRegistry != null) {
		        StandardServiceRegistryBuilder.destroy(standardServiceRegistry);
		      }
		    }
      }
  }
  
  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }
}