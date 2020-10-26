/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.configuration;

import com.mds.aiotplayer.sys.dao.UserDao;
import com.mds.aiotplayer.webapp.common.security.CaptchaCaptureFilter;
import com.mds.aiotplayer.webapp.common.security.MdsAuthenticationProvider;
import com.mds.aiotplayer.webapp.common.security.MdsLoginFilter;
import com.mds.aiotplayer.webapp.common.security.MdsLoginHandler;
import com.mds.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security configuration for MDS Spring Rest
 *
 * @author Frederic Van Reet (frederic dot vanreet at atmire dot com)
 * @author Tom Desair (tom dot desair at atmire dot com)
 */
@EnableWebSecurity
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String ADMIN_GRANT = "ADMIN";
    public static final String AUTHENTICATED_GRANT = "AUTHENTICATED";
    public static final String ANONYMOUS_GRANT = "ANONYMOUS";

    @Value("${spring.security.jcaptchaEnabled:true}")
    private boolean jcaptchaEnabled;
    
    @Autowired
    private RequestService requestService;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    MdsAuthenticationProvider mdsAuthenticationProvider;

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity
            .ignoring()
            	.antMatchers("/static/**", "/assets/**", "/app/login*", "/login*", "/login.jsp*", "/index.jsp*")
                .antMatchers(HttpMethod.GET, "/api/authn/login")
                .antMatchers(HttpMethod.PUT, "/api/authn/login")
                .antMatchers(HttpMethod.PATCH, "/api/authn/login")
                .antMatchers(HttpMethod.DELETE, "/api/authn/login");
    }

    /*.authorizeRequests() , "/services/**"
	.antMatchers("/static/**", "/assets/**", "/app/login*", "/login*", "/login.jsp*", "/index.jsp*")
	.permitAll()
	.anyRequest()
	.fullyAuthenticated()
	.and()*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin().cacheControl();
        http.headers().httpStrictTransportSecurity().disable();
        	//.maxAgeInSeconds(0)
        	//.includeSubDomains(true);
        http
            //Tell Spring to not create Sessions
            //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            //Anonymous requests should have the "ANONYMOUS" security grant
            //.anonymous().authorities(ANONYMOUS_GRANT).and()
            //Wire up the HttpServletRequest with the current SecurityContext values
            //.servletApi().and().cors().and()
            //Disable CSRF as our API can be used by clients on an other domain, we are also protected against this,
            // since we pass the token in a header
            .csrf().disable()
            /*.formLogin()
            	.loginPage("/login")
            	.successForwardUrl("/home")
            	.failureUrl("/login?error=true").permitAll()
            .and()
            	.authorizeRequests().antMatchers("/app/sys/passwordHint/**", "/app/sys/requestRecoveryToken/**"
            			, "/app/sys/updatePassword*", "/app/sys/signup*", "/app/cm/galleryview*", "/app/jcaptcha-validate*").permitAll()*/ //.anyRequest().fullyAuthenticated()
            	.authorizeRequests().antMatchers("/sys/passwordHint/**", "/sys/requestRecoveryToken/**"
        			, "/sys/updatePassword*", "/sys/signup*", "/cm/galleryview*", "/jcaptcha-validate*"
        			, "/services/api/users/jcaptcha*", "/services/api/playerGroups/filelist*"
        			, "/services/api/contentitems/getmedia*", "/services/api/ftpStatuses*").permitAll()
            		.antMatchers("/**").authenticated()
            /*.and()
            	.authorizeRequests()
            	.antMatchers("/**").authenticated()*/
            .and()
            	.addFilterAt(mdsLoginFilter(), UsernamePasswordAuthenticationFilter.class)
            	.addFilterBefore(captchaCaptureFilter(), UsernamePasswordAuthenticationFilter.class) //)new CaptchaCaptureFilter(jcaptchaEnabled)
            	.rememberMe().userDetailsService(userDao).key("e37f4b31-0c45-11dd-bd0b-0800200c9a66")
	        .and()
		        .httpBasic()
		            .authenticationEntryPoint(loginEntryPoint())
		     .and()
		     	.exceptionHandling()
		     		.accessDeniedPage("/error/403");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(mdsAuthenticationProvider);
    }
    
    @Bean PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    
    @Bean StandardPasswordEncoder passwordTokenEncoder() {
    	return new StandardPasswordEncoder();
    }
    
    @Bean
    public MdsAuthenticationProvider mdsAuthenticationProvider(){  
    	MdsAuthenticationProvider mdsAuthenticationProvider = new MdsAuthenticationProvider();
    	mdsAuthenticationProvider.setUserDetailsService(userDao);
    	mdsAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    	mdsAuthenticationProvider.setCaptchaCaptureFilter(captchaCaptureFilter());
    	
	    return mdsAuthenticationProvider;
    }
    
    @Bean
    public CaptchaCaptureFilter captchaCaptureFilter(){  
    	CaptchaCaptureFilter captchaCaptureFilter = new CaptchaCaptureFilter();
    	captchaCaptureFilter.setJcaptchaEnabled(jcaptchaEnabled);
    	
	    return captchaCaptureFilter;
    }
    
    @Bean
    public MdsLoginHandler mdsLoginHandler(){  
    	MdsLoginHandler mdsLoginHandler = new MdsLoginHandler();
    	mdsLoginHandler.setDefaultTargetUrl("/home");
    	
	    return mdsLoginHandler;
    }
    
    @Bean
    public LoginUrlAuthenticationEntryPoint loginEntryPoint(){     	
	    return new LoginUrlAuthenticationEntryPoint("/login");
    }
    
    @Bean
    public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler(){  
    	SimpleUrlAuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
    	authenticationFailureHandler.setDefaultFailureUrl("/login?error=true");
    	
	    return authenticationFailureHandler;
    }
    
    @Bean
    public MdsLoginFilter mdsLoginFilter() throws Exception{  
    	MdsLoginFilter mdsLoginFilter = new MdsLoginFilter();
    	mdsLoginFilter.setFilterProcessesUrl("/j_security_check");
    	mdsLoginFilter.setUsernameParameter("username");
    	mdsLoginFilter.setPasswordParameter("password");
    	mdsLoginFilter.setJcaptchaCodeParameter("jcaptchaCode");
    	mdsLoginFilter.setJcaptchaEnabled(jcaptchaEnabled);
    	mdsLoginFilter.setMobileDeviceParameter("mobileDevice");
    	mdsLoginFilter.setAuthenticationManager(authenticationManager());
    	mdsLoginFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
    	mdsLoginFilter.setAuthenticationSuccessHandler(mdsLoginHandler());
    	
	    return mdsLoginFilter;
    }
}
