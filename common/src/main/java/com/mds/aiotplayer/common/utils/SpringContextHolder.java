/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

/**
 * keep Spring ApplicationContext with static variable, So feel free to get ApplicaitonContext.
 * 
 * @author Zaric
 * @date 2013-5-29 1:25:40 pm
 */
@Service
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext applicationContext = null;

	private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

	/**
	 * Get ApplicationContext stored in static variable.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}
	
	public static String getRootRealPath(){
		String rootRealPath ="";
		try {
			rootRealPath=getApplicationContext().getResource("").getFile().getAbsolutePath();
		} catch (IOException e) {
			logger.warn("Get system path failure");
		}
		return rootRealPath;
	}
	
	public static String getResourceRootRealPath(){
		String rootRealPath ="";
		try {
			rootRealPath=new DefaultResourceLoader().getResource("").getFile().getAbsolutePath();
		} catch (IOException e) {
			logger.warn("Get system resource path failure");
		}
		return rootRealPath;
	}
	

	/**
	 * get Bean from static variable - applicationContext, Automatic transformation to the type of assignment object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) throws BeansException{
		assertContextInjected();
		try{
			return (T) applicationContext.getBean(name);
		}catch(Exception e)	{
			throw new RuntimeException("Bean not found");
		}
	}

	/**
	 * get Bean from static variable - applicationContext, Automatic transformation to the type of assignment object.
	 */
	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		assertContextInjected();
		try{
			return applicationContext.getBean(requiredType);
		}catch(Exception e)	{
			throw new RuntimeException("Bean not found");
		}
	}
	
	public static <T> T getBean(String name, Class<T> requiredType) throws BeansException{
		assertContextInjected();
		return applicationContext.getBean(name, requiredType);
	}

	/**
	 * clear ApplicationContext.
	 */
	public static void clearHolder() {
		if (logger.isDebugEnabled()){
			logger.debug("Clear static variable - applicationContext in SpringContextHolder:" + applicationContext);
		}
		applicationContext = null;
	}

	/**
	 * Implement ApplicationContextAware interface, Inject Context to applicationContext.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		logger.debug("Inject ApplicationContext into SpringContextHolder:" + applicationContext);
		if (SpringContextHolder.applicationContext != null) {
			logger.info("Overwrite variable applicationContext, Origin ApplicationContext:" + SpringContextHolder.applicationContext);
		}

		SpringContextHolder.applicationContext = applicationContext;
	}

	/**
	 * Implement DisposableBean interface, clear ApplicationContext when Context has been destroy.
	 */
	@Override
	public void destroy() throws Exception {
		SpringContextHolder.clearHolder();
	}

	/**
	 * Check ApplicationContext not null.
	 */
	private static void assertContextInjected() {
		Validate.validState(applicationContext != null, "applicaitonContext property is not injected, please define SpringContextHolder service in applicationContext.xml.");
	}
}