/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springmodules.validation.commons.DefaultBeanValidator;
import org.springmodules.validation.commons.DefaultValidatorFactory;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.service.MailEngine;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.webapp.common.util.CustomSimpleMappingExceptionResolver;

/**
 * This class provide extra configuration for our Spring Boot Application
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@Configuration
@EnableSpringDataWebSupport
@ComponentScan( {"com.mds.aiotplayer.cm.service", "com.mds.aiotplayer.common.service", "com.mds.aiotplayer.core"
	, "com.mds.aiotplayer.hrm.service", "com.mds.aiotplayer.i18n.service", "com.mds.aiotplayer.log.service", "com.mds.aiotplayer.msg.service"
	, "com.mds.aiotplayer.pl.service", "com.mds.aiotplayer.pm.service", "com.mds.aiotplayer.ps.service", "com.mds.aiotplayer.sys.service", "com.mds.aiotplayer.util", "com.mds.aiotplayer.wf.service"
	, "com.mds.aiotplayer.workflow", "com.mds.aiotplayer.webapp.configuration", "com.mds.aiotplayer.webapp.*.controller"}) //"com.mds.aiotplayer.event.factory", 
public class ApplicationConfig {
    // Allowed CORS origins. Defaults to * (everywhere)
    // Can be overridden in MDS configuration
	@Value("${rest.cors.allowed-origins}")
    private String[] corsAllowedOrigins;

    // Whether to allow credentials (cookies) in CORS requests ("Access-Control-Allow-Credentials" header)
    // Defaults to true. Can be overridden in MDS configuration
    @Value("${rest.cors.allow-credentials:true}")
    private boolean corsAllowCredentials;
    
    // Configured User Interface URL (default: http://localhost:4000)
    @Value("${mds.ui.url:http://localhost:8080/mds-web}")
    private String uiURL;

    public String[] getCorsAllowedOrigins() {
		/*
		 * if (corsAllowedOrigins != null) { return
		 * corsAllowedOrigins.split("\\s*,\\s*"); } return null;
		 */
    	
    	// Use "rest.cors.allowed-origins" if configured. Otherwise, default to the "mds.ui.url" setting.
        if (corsAllowedOrigins != null) {
            return corsAllowedOrigins;
        } else if (uiURL != null) {
            return new String[] {uiURL};
        }
        
        return null;
    }
    
    /**
     * Return whether to allow credentials (cookies) on CORS requests. This is used to set the
     * CORS "Access-Control-Allow-Credentials" header in Application class.
     * @return true or false
     */
    public boolean getCorsAllowCredentials() {
        return corsAllowCredentials;
    }
    
    @Bean(name="springContextHolder")
    @Lazy(false)
    SpringContextHolder springContextHolder() {
    	SpringContextHolder springContextHolder = new SpringContextHolder();
   	
        return springContextHolder;
    }
        
    @Bean TaskScheduler taskScheduler() {
    	return new ThreadPoolTaskScheduler();
    }
        
    @Bean FormattingConversionServiceFactoryBean conversionService() {
    	return new FormattingConversionServiceFactoryBean();
    }
    
    @Bean
    public DefaultValidatorFactory validatorFactory() {
    	DefaultValidatorFactory validatorFactory = new DefaultValidatorFactory();
    	validatorFactory.setValidationConfigLocations(new Resource[] {new FileSystemResource(springContextHolder().getRootRealPath() + "/WEB-INF/validation.xml"), 
    			new FileSystemResource(springContextHolder().getRootRealPath() + "/WEB-INF/validator-rules.xml"), new FileSystemResource(springContextHolder().getRootRealPath() + "/WEB-INF/validator-rules-custom.xml")});
    	
    	return validatorFactory;
    }
    
    @Bean
    public DefaultBeanValidator beanValidator() {
    	DefaultBeanValidator defaultBeanValidator = new DefaultBeanValidator();
    	defaultBeanValidator.setValidatorFactory(validatorFactory());

        return defaultBeanValidator;
    }
    
    @Bean
    HandlerExceptionResolver customExceptionResolver () {
        CustomSimpleMappingExceptionResolver resolver = new CustomSimpleMappingExceptionResolver();
        Properties mappings = new Properties();
        // Mapping Spring internal error NoHandlerFoundException to a view name
        mappings.setProperty(NoHandlerFoundException.class.getName(), "/error/404");
        mappings.setProperty(InternalServerErrorException.class.getName(), "/error/error");
        mappings.setProperty(NullPointerException.class.getName(), "/error/error");
        mappings.setProperty(ClassNotFoundException.class.getName(), "/error/error");
        mappings.setProperty(Exception.class.getName(), "/error/error");
        resolver.setExceptionMappings(mappings);
        // Set specific HTTP codes
        resolver.addStatusCode("404", HttpStatus.NOT_FOUND.value());
        resolver.addStatusCode("500", HttpStatus.INTERNAL_SERVER_ERROR.value());
        resolver.setDefaultErrorView("/error/error");
        resolver.setDefaultStatusCode(200);
        // This resolver will be processed before the default ones
        resolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
        resolver.setExceptionAttribute("exception");
        return resolver;
    }
        
    /*@Bean DomainClassConverter domainClassConverter(FormattingConversionServiceFactoryBean conversionService) {
    	return new DomainClassConverter(conversionService);
    }*/
    
	/*
	 * @Bean(name="Validator") LocalValidatorFactoryBean validator() { return new
	 * LocalValidatorFactoryBean(); }
	 */

    @Bean
    @ConfigurationPropertiesBinding
    Converter<String, Path> pathConverter() {
        return new Converter<String, Path>(){

            @Override
            public Path convert(String source) {
                return Paths.get(source);
            }
        };
    }

    @Bean
    Converter<String, Set<String>> stringToSet() {
        return new Converter<String, Set<String>>() {
            @Override
            public Set<String> convert(String s) {
                return new HashSet(Lists.newArrayList(s.split(",")));
            }
        };
    }
    
	/*
	 * <!-- =================================================================== -->
	 * <!-- Mail: Sender and Velocity configuration --> <!--
	 * =================================================================== --> <bean
	 * id="mailEngine" class="com.mds.aiotplayer.common.service.MailEngine"> <property
	 * name="mailSender" ref="mailSender"/> <property name="velocityEngine"
	 * ref="velocityEngine"/> <property name="from" value="${mail.default.from}"/>
	 * </bean>
	 * 
	 * <bean id="mailSender"
	 * class="org.springframework.mail.javamail.JavaMailSenderImpl"> <property
	 * name="host" value="${mail.host}"/> <property name="defaultEncoding"
	 * value="UTF-8"/> <!-- Uncomment if you need to authenticate with your SMTP
	 * Server --> <property name="username" value="${mail.username}"/> <property
	 * name="password" value="${mail.password}"/> <property
	 * name="javaMailProperties"> <value> mail.smtp.auth=true </value> </property>
	 * </bean>
	 * 
	 * <!-- Configure Velocity for sending e-mail --> <bean id="velocityEngine"
	 * class="org.springframework.ui.velocity.VelocityEngineFactoryBean"> <property
	 * name="velocityProperties"> <props> <prop key="resource.loader">class</prop>
	 * <prop key="class.resource.loader.class">
	 * org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader </prop>
	 * <prop key="velocimacro.library"></prop> </props> </property> </bean>
	 * 
	 * <bean id="mailMessage" class="org.springframework.mail.SimpleMailMessage"
	 * scope="prototype"> <property name="from" value="${mail.default.from}"/>
	 * </bean>
	 */
    
    @Bean SimpleMailMessage mailMessage() {
    	SimpleMailMessage mailMessage =  new SimpleMailMessage();
    	mailMessage.setFrom("${mail.default.from}");
    	
    	return mailMessage;
    }
    
    @Bean VelocityEngineFactoryBean velocityEngine() {
    	VelocityEngineFactoryBean velocityEngine =  new VelocityEngineFactoryBean();
   	
    	return velocityEngine;
    }
    
    @Bean JavaMailSenderImpl mailSender() {
    	JavaMailSenderImpl mailSender =  new JavaMailSenderImpl();
    	mailSender.setHost("${mail.host}");
    	mailSender.setDefaultEncoding("UTF-8");
    	
    	return mailSender;
    }
    
    @Bean MailEngine mailEngine() {
    	MailEngine mailEngine =  new MailEngine();
    	mailEngine.setMailSender(mailSender());
    	mailEngine.setVelocityEngine(velocityEngine().getObject());
    	mailEngine.setFrom("${mail.default.from}");
    	
    	return mailEngine;
    }
}
