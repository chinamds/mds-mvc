package com.mds.aiotplayer.webapp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.mds.servicemanager.spring.ResourceFinder;
import com.mds.services.ConfigurationService;
import com.mds.services.factory.MDSServicesFactory;
//import com.zaxxer.hikari.HikariDataSource;

import liquibase.integration.spring.SpringLiquibase;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static java.time.ZonedDateTime.now;

import java.io.File;

/**
 * Created by kevin on 11/04/15
 */
@Configuration
@EntityScan(basePackages={"com.mds.aiotplayer.*.model", "com.mds.aiotplayer.common.*.model", "com.mds.aiotplayer"})
@ComponentScan("com.mds.aiotplayer.*.dao")
@EnableTransactionManagement
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class DataSourceConfig {
	
	ConfigurationService config = MDSServicesFactory.getInstance().getConfigurationService();
	
	/*
	 * <property name="driverClassName" value="${mds.jdbc.driverClassName}" />
	 * 
	 * <!-- base property: url、user、password mds.--> <property name="url"
	 * value="${mds.jdbc.url}" /> <property name="username"
	 * value="${mds.jdbc.username}" /> <property name="password"
	 * value="${mds.jdbc.password}" />
	 * 
	 * <property name="initialSize" value="${jdbc.pool.minIdle}" /> <property
	 * name="minIdle" value="${jdbc.pool.minIdle}" /> <property name="maxActive"
	 * value="${jdbc.pool.maxActive}" />
	 * @Value("${db.username:root}") String user,
            @Value("${db.password:}") String password,
            @Value("${db.url}") String url,
            @Value("${db.driver}") String driveClassName
	 */

    @Bean(destroyMethod="close")
    public DataSource dataSource() throws SQLException {
   	
    	DruidDataSource dataSource = new DruidDataSource();

        dataSource.setDriverClassName(config.getProperty("db.driver"));
        dataSource.setUrl(config.getProperty("db.url"));
        dataSource.setUsername(config.getProperty("db.username"));
        dataSource.setPassword(config.getProperty("db.password"));
        //dataSource.setMaxActive(config.getIntProperty("db.password"));
        dataSource.setMaxIdle(config.getIntProperty("db.maxidle"));
        //dataSource.setMinIdle(config.getIntProperty("db.minidle"));
        dataSource.setMaxWait(config.getIntProperty("db.maxwait"));
        dataSource.setMaxActive(config.getIntProperty("db.maxconnections"));

        return dataSource;
    }
    
    /*@Bean(destroyMethod="close")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDriverClassName(config.getProperty("db.driver"));
        //dataSource.setDataSourceClassName(className);
        dataSource.setJdbcUrl(config.getProperty("db.url"));
        dataSource.setUsername(config.getProperty("db.username"));
        dataSource.setPassword(config.getProperty("db.password"));
        
        dataSource.setIdleTimeout(config.getIntProperty("db.maxidle"));
        //dataSource.setMinIdle(config.getIntProperty("db.minidle"));
        dataSource.setMaxLifetime(config.getIntProperty("db.maxwait"));

        return dataSource;
    }*/
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factory, DataSource dataSource) {
	    return factory.dataSource(dataSource).packages("com.mds.aiotplayer.*.model", "com.mds.aiotplayer.common.*.model").persistenceUnit("default").build();
    }
    
    
    /*@Bean
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactoryBuilder factory, DataSource dataSource) throws SQLException {   	
	    return new JpaTransactionManager(entityManagerFactory(factory, dataSource).getNativeEntityManagerFactory());
    }*/
    
    /*@Bean(destroyMethod="destroy")
    @Lazy(true)
    public LocalSessionFactoryBean sessionFactory() throws SQLException {
    	LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    	sessionFactory.setConfigLocation(new FileSystemResource(config.getProperty("mdsplus.home") + "/config/hibernate.cfg.xml")); //"file:" + config.getProperty("mdsplus.home") + ResourceFinder.getResource("/config/hibernate.cfg.xml")
    	sessionFactory.setDataSource(dataSource());
    	
    	Properties hibernateProperties = sessionFactory.getHibernateProperties();
        hibernateProperties.put("hibernate.dialect", config.getProperty("db.dialect"));
     	hibernateProperties.put("hibernate.default_schema", config.getProperty("db.schema"));
        hibernateProperties.put("net.sf.ehcache.configurationResourceName", "file:" + config.getProperty("mdsplus.home") + "/config/hibernate-ehcache-config.xml");
        //sessionFactory.setHibernateProperties(hibernateProperties());
    	
    	return sessionFactory;
    }*/
    
    
	/*
	 * <!-- Transaction manager for a single Hibernate SessionFactory (alternative
	 * to JTA) --> <bean id="transactionManager"
	 * class="org.springframework.orm.hibernate5.HibernateTransactionManager">
	 * <property name="sessionFactory" ref="sessionFactory"/> </bean>
	 */
    /*@Bean
    public HibernateTransactionManager transactionManager() throws SQLException {   	
	    return new HibernateTransactionManager(sessionFactory().getObject());
    }*/
    
    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", config.getProperty("db.dialect"));
    	hibernateProperties.put("hibernate.default_schema", config.getProperty("db.schema"));
        hibernateProperties.put("net.sf.ehcache.configurationResourceName", "file:" + config.getProperty("mdsplus.home") + "/config/hibernate-ehcache-config.xml");
        /*hibernateProperties.setProperty("hibernate.current_session_context_class", environment.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
        properties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.setProperty("hibernate.show-sql", environment.getProperty("spring.jpa.properties.hibernate.show-sql"));
        properties.setProperty("hibernate.cache.use_second_level_cache", environment.getProperty("spring.jpa.properties.hibernate.cache.use_second_level_cache"));
        properties.setProperty("hibernate.cache.use_query_cache", environment.getProperty("spring.jpa.properties.hibernate.cache.use_query_cache"));*/
        
        return hibernateProperties;
    }
    
	/*
	 * @Bean public SpringLiquibase liquibase(DataSource dataSource) {
	 * SpringLiquibase liquibase = new SpringLiquibase();
	 * liquibase.setDataSource(dataSource);
	 * liquibase.setChangeLog("classpath:/liquibase/mds_db.changelog.xml");
	 * liquibase.setContexts("default,development,test,prod");
	 * //liquibase.setShouldRun(true);
	 * 
	 * return liquibase; }
	 */
    
    @Bean
    DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(now());
    }
}
