package com.mds.aiotplayer;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import com.mds.aiotplayer.common.web.interceptor.SetCommonDataInterceptor;

import com.mds.utils.servlet.MDSWebappServletFilter;
import com.opensymphony.module.sitemesh.filter.PageFilter;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;

import ro.isdc.wro.http.WroFilter;

import com.mds.aiotplayer.common.web.bind.method.annotation.PageableMethodArgumentResolver;
import com.mds.aiotplayer.common.web.bind.method.annotation.SearchableMethodArgumentResolver;
import com.mds.aiotplayer.common.web.jcaptcha.JCaptchaFilter;
import com.mds.aiotplayer.webapp.common.filter.MDSRequestContextFilter;
import com.mds.aiotplayer.webapp.common.listener.MDSContextListener;
import com.mds.aiotplayer.webapp.common.listener.StartupListener;
import com.mds.aiotplayer.webapp.common.util.CustomSimpleMappingExceptionResolver;
import com.mds.aiotplayer.webapp.common.util.DatabaseMessageSource;
import com.mds.aiotplayer.webapp.common.util.MDSConfigurationInitializer;
import com.mds.aiotplayer.webapp.common.util.MDSKernelInitializer;
import com.mds.aiotplayer.webapp.configuration.ApplicationConfig;
import com.mds.aiotplayer.webapp.sys.bind.method.CurrentUserMethodArgumentResolver;
import com.mds.aiotplayer.webapp.common.filter.LocaleFilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.ws.rs.InternalServerErrorException;

/**
 * Created by John Lee on 11/04/15
 */
/*
 * @SpringBootApplication public class Application {
 * 
 * public static void main(String[] args) {
 * SpringApplication.run(Application.class, args); }
 * 
 * }
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	@Autowired
    private ApplicationConfig configuration;

    /*public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }*/

    @Override
    protected final SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(Application.class).initializers(new MDSKernelInitializer(), new MDSConfigurationInitializer());
    }
    
    /**
     * Register the "MDSContextListener" so that it is loaded
     * for this Application.
     *
     * @return MDSContextListener
     */
    @Bean
    @Order(2)
    protected MDSContextListener mdsContextListener() {
        // This listener initializes the MDS Context object
        return new MDSContextListener();
    }

    /**
     * Register the MDSWebappServletFilter, which initializes the
     * MDS RequestService / SessionService
     *
     * @return MDSWebappServletFilter
     */
    @Bean
    @Order(1)
    protected Filter mdsWebappServletFilter() {
        return new MDSWebappServletFilter();
    }

    /**
     * Register the MDSRequestContextFilter, a Filter which checks for open
     * Context objects *after* a request has been fully processed, and closes them
     *
     * @return MDSRequestContextFilter
     */
    @Bean
    @Order(2)
    protected Filter mdsRequestContextFilter() {
        return new MDSRequestContextFilter();
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
    
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
        	/**
             * Create a custom CORS mapping for the MDS REST API (/api/ paths), based on configured allowed origins.
             * @param registry CorsRegistry
             */
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                String[] corsAllowedOrigins = configuration.getCorsAllowedOrigins();
                boolean corsAllowCredentials = configuration.getCorsAllowCredentials();
                if (corsAllowedOrigins != null) {
                    registry.addMapping("/api/**").allowedMethods(CorsConfiguration.ALL)
	                    // Set Access-Control-Allow-Credentials to "true" and specify which origins are valid
	                    // for our Access-Control-Allow-Origin header
	                    .allowCredentials(corsAllowCredentials).allowedOrigins(corsAllowedOrigins)
	                    // Whitelist of request preflight headers allowed to be sent to us from the client
                        .allowedHeaders("Authorization", "Content-Type",
                            "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method",
                            "Access-Control-Request-Headers", "X-On-Behalf-Of")
                        // Whitelist of response headers allowed to be sent by us (the server)
                        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Authorization");
                }
            }

            /**
             * Add a new ResourceHandler to allow us to use WebJars.org to pull in web dependencies
             * dynamically for HAL Browser, and access them off the /webjars path.
             * @param registry ResourceHandlerRegistry
             */
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry
                    .addResourceHandler("/webjars/**")
                    .addResourceLocations("/webjars/");
                
				/*
				 * registry .addResourceHandler("/static/jcaptcha.jpg")
				 * .addResourceLocations("/static/jcaptcha.jpg");
				 */
            }
            
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                /* For example, Mapping to the login view. */
                // registry.addViewController("/login").setViewName("login");
            	registry.addViewController("/error/403").setViewName("error/403");
            	registry.addViewController("/error/404").setViewName("error/404");
            	registry.addViewController("/error/error").setViewName("error/error");
            	registry.addViewController("/error/dataAccessFailure").setViewName("error/dataAccessFailure");
            }
            
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
            	registry.addInterceptor(localeChangeInterceptor()).order(1);
                registry.addInterceptor(new SetCommonDataInterceptor()).addPathPatterns("/**").excludePathPatterns("/sys/polling*", "/services/api/**").order(2);
            }
            
            @Override
            @Bean
            public Validator getValidator() {
                final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
                validator.setValidationMessageSource(messageSource());

                return validator;
            }
                        
            @Override
            public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> argumentResolvers) {
                //argumentResolvers.add(new SearchFilterResolver());
            	argumentResolvers.add(new SearchableMethodArgumentResolver());
            	//argumentResolvers.add(new CurrentUserMethodArgumentResolver());
            	argumentResolvers.add(new PageableMethodArgumentResolver());
            }
            
            @Override
            public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
                configurer.enable();
            }
        };
    }
    
    @Bean
    public LocaleResolver localeResolver() {
    	SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    	localeResolver.setDefaultLocale(Locale.US);
    	//localeResolver.setLocaleAttributeName(localeAttributeName);

        return localeResolver;
    }
    
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
       LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
       localeChangeInterceptor.setParamName("locale");
       return localeChangeInterceptor;
    }
    
    @Bean
    public MessageSource propertiesMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:ApplicationResources");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        // # -1 : never reload, 0 always reload
        // messageSource.setCacheSeconds(0);
        return messageSource;
    }
    
    @Bean
    public MessageSource messageSource() {
    	DatabaseMessageSource messageSource = new DatabaseMessageSource();
        messageSource.setParentMessageSource(propertiesMessageSource());
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }
    
    @Bean
    public FilterRegistrationBean localeFilter(){
    	LocaleFilter localeFilter = new LocaleFilter();
        FilterRegistrationBean registration = new FilterRegistrationBean(localeFilter);
        registration.setOrder(1);
        
        registration.setUrlPatterns(Arrays.asList("/*"));
        
        return registration;
    }
    
    @Bean
    public FilterRegistrationBean urlRewrite(){
        UrlRewriteFilter rewriteFilter=new UrlRewriteFilter();
        FilterRegistrationBean registration = new FilterRegistrationBean(rewriteFilter);
        registration.setUrlPatterns(Arrays.asList("/*"));
        Map initParam=new HashMap();
        //initParam.put("confPath","urlrewirte.xml");
        initParam.put("logLevel", "commons");
        initParam.put("confReloadCheckInterval", "-1");
        registration.setInitParameters(initParam);
        registration.setOrder(1);
        
        return  registration;
    }
    
    @Bean
    public FilterRegistrationBean siteMeshFilter(){
    	SiteMeshFilter siteMeshFilter = new SiteMeshFilter();
        FilterRegistrationBean registration = new FilterRegistrationBean(siteMeshFilter);
        
        registration.setUrlPatterns(Arrays.asList("/*"));
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        registration.setOrder(2);
        Map initParam=new HashMap();
        //initParam.put("configFile","sitemesh.xml");
        //initParam.put("autoReload", "commons");
        registration.setInitParameters(initParam);
        
        return registration;
    }
    
    @Bean
    public FilterRegistrationBean wroFilter(){
    	WroFilter wroFilter = new WroFilter();
        FilterRegistrationBean registration = new FilterRegistrationBean(wroFilter);
        
        registration.setUrlPatterns(Arrays.asList("/assets/*"));
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        Map initParam=new HashMap();
        //initParam.put("configFile","wro.xml");
        //initParam.put("autoReload", "commons");
        registration.setInitParameters(initParam);
        
        return registration;
    }
    
    /*@Bean
    public FilterRegistrationBean jCaptchaFilter(){
    	JCaptchaFilter jCaptchaFilter = new JCaptchaFilter();
        FilterRegistrationBean registration = new FilterRegistrationBean(jCaptchaFilter);
        
        registration.setUrlPatterns(Arrays.asList("/jcaptcha.jpg"));
        //registration.setName("jCaptchaFilter");
        registration.setEnabled(false);
        //registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        //Map initParam=new HashMap();
        //initParam.put("configFile","wro.xml");
        //initParam.put("autoReload", "commons");
        //registration.setInitParameters(initParam);
        
        return registration;
    }*/
}