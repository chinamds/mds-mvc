/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.configuration;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.beust.jcommander.internal.Maps;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.service.AlbumsManager;
import com.mds.aiotplayer.cm.service.ContentItemsManager;
import com.mds.aiotplayer.cm.service.ContentMetaService;
import com.mds.aiotplayer.cm.service.DailyListManager;
import com.mds.aiotplayer.cm.service.FeedService;
import com.mds.aiotplayer.cm.service.GalleryManager;
import com.mds.aiotplayer.cm.service.MetaService;
import com.mds.aiotplayer.cm.service.TaskService;
import com.mds.aiotplayer.cm.util.AlbumSyndicationFeedFormatter;
import com.mds.aiotplayer.i18n.service.CultureManager;
import com.mds.aiotplayer.i18n.service.LocalizedResourceManager;
import com.mds.aiotplayer.i18n.service.NeutralResourceManager;
import com.mds.aiotplayer.pm.service.PlayerGroupManager;
import com.mds.aiotplayer.pm.service.PlayerManager;
import com.mds.aiotplayer.sys.service.AreaManager;
import com.mds.aiotplayer.sys.service.DictManager;
import com.mds.aiotplayer.sys.service.MenuFunctionManager;
import com.mds.aiotplayer.sys.service.MyMessageManager;
import com.mds.aiotplayer.sys.service.NotificationManager;
import com.mds.aiotplayer.sys.service.OrganizationManager;
import com.mds.aiotplayer.sys.service.PermissionManager;
import com.mds.aiotplayer.sys.service.RoleManager;
import com.mds.aiotplayer.sys.service.UserManager;
import com.mds.aiotplayer.sys.service.UsersService;
import com.mds.aiotplayer.wf.service.ActivityManager;
import com.mds.aiotplayer.wf.service.OrganizationWorkflowTypeManager;
import com.mds.aiotplayer.wf.service.WorkflowManager;

import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.apache.cxf.jaxrs.swagger.ui.SwaggerUiConfig;

import java.util.Arrays;
import java.util.Collections;

import javax.xml.ws.Endpoint;


@Configuration
public class CxfConfig {
    @Autowired
    private Bus bus;

    @Autowired
    UserManager userManager;
    
    @Autowired
    UsersService usersManager;
    
    @Autowired
    AreaManager areaManager;
    
    @Autowired
    MenuFunctionManager menuFunctionManager;
    
    @Autowired
    OrganizationManager organizationManager;
    
    @Autowired
    RoleManager roleManager;
    
    @Autowired
    PermissionManager permissionManager;
    
    @Autowired
    MyMessageManager myMessageManager;
    
    @Autowired
    NotificationManager notificationManager;
    
    @Autowired
    CultureManager cultureManager;
    
    @Autowired
    NeutralResourceManager neutralResourceManager;
    
    @Autowired
    LocalizedResourceManager localizedResourceManager;
    
    @Autowired
    DictManager dictManager;
    
    @Autowired
    GalleryManager galleryManager;
    
    @Autowired
    AlbumManager albumManager;
    
    @Autowired
    AlbumsManager albumsManager;
    
    @Autowired
    ContentItemsManager contentItemsManager;
    
    @Autowired
    TaskService taskManager;
    
    @Autowired
    FeedService feedManager;
    
    @Autowired
    ContentMetaService contentMetaManager;
    
    @Autowired
    MetaService metaManager;
    
    @Autowired
    DailyListManager dailyListManager;
    
    @Autowired
    PlayerManager playerManager;
    
    @Autowired
    PlayerGroupManager playerGroupManager;
    
    @Autowired
    OrganizationWorkflowTypeManager organizationWorkflowTypeManager;
    
    @Autowired
    ActivityManager activityManager;
    
    @Autowired
    WorkflowManager workflowManager;

    /*@SuppressWarnings("all")
    @Bean
    public ServletRegistrationBean dispatcherServlet() {
        return new ServletRegistrationBean(new CXFServlet(), "/services/*");
    }*/

    /** JAX-WS
     * վ�����
     * **/
    @Bean
    public Endpoint areaService() {
        EndpointImpl endpoint = new EndpointImpl(bus, areaManager);
        endpoint.publish("/AreaService");
        return endpoint;
    }
    
    @Bean
    public Endpoint myMessageService() {
        EndpointImpl endpoint = new EndpointImpl(bus, myMessageManager);
        endpoint.publish("/MyMessageService");
        return endpoint;
    }
        
    @Bean
    public ObjectMapper jacksonMapper() {
    	ObjectMapper jacksonMapper = new ObjectMapper();
    	jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	
        return jacksonMapper;
    }
    
    /*@Bean MethodInvokingFactoryBean jacksonMapperConfigure() {
    	MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
    	methodInvokingFactoryBean.setTargetObject(jacksonMapper());
    	methodInvokingFactoryBean.setTargetMethod("configure"); //configure
    	methodInvokingFactoryBean.setArguments(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	
    	return methodInvokingFactoryBean;
    }*/
    
    @Bean
    public JacksonJsonProvider jsonProvider() {
    	JacksonJsonProvider jsonProvider =  new JacksonJsonProvider();
    	jsonProvider.setMapper(jacksonMapper());
    	
    	return jsonProvider;
    }
    
    @Bean
    public AlbumSyndicationFeedFormatter rssMDSProvider() {
        return new AlbumSyndicationFeedFormatter();
    }
    
    @Bean
    public Server rsServer() {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(bus);
        endpoint.setAddress("/api");
        // Register 2 JAX-RS root resources supporting "/sayHello/{id}" and "/sayHello2/{id}" relative paths
        endpoint.setServiceBeans(Arrays.<Object>asList(
        		userManager, 
        		areaManager,
        		menuFunctionManager,
        		organizationManager,
        		roleManager,
        		permissionManager,
        		myMessageManager,
        		notificationManager,
        		cultureManager,
        		neutralResourceManager,
        		localizedResourceManager,
        		dictManager,
        		galleryManager,
        		albumManager,
        		albumsManager,
        		contentItemsManager,
        		metaManager,
        		taskManager,
        		feedManager,
        		metaManager,
        		dailyListManager,
        		playerManager,
        		playerGroupManager,
        		organizationWorkflowTypeManager,
        		activityManager,
        		workflowManager
        		));
        endpoint.setProviders(Arrays.asList(jsonProvider(), rssMDSProvider()));
        endpoint.setExtensionMappings(Maps.newHashMap("json", "application/json", "xml", "application/xml", "feed", "application/atom+xml"));
        //endpoint.setFeatures(Arrays.asList(new Swagger2Feature()));
        endpoint.setFeatures(Arrays.asList(createOpenApiFeature(), new LoggingFeature()));
        return endpoint.create();
    }
    
    @Bean
    public OpenApiFeature createOpenApiFeature() {
        final OpenApiFeature openApiFeature = new OpenApiFeature();
        openApiFeature.setPrettyPrint(true);
        openApiFeature.setTitle("MDSPlus REST Application");
        openApiFeature.setContactName("China MDS");
        openApiFeature.setDescription("Multimedia distribution service System - digital asset management and multimedia intelligent distribution cloud platform");
        openApiFeature.setVersion("2.0.0.0");
        openApiFeature.setSwaggerUiConfig(
            new SwaggerUiConfig()
                .url("/services/api/openapi.json"));
        openApiFeature.setResourcePackages(Collections.singleton("com"));
        
        return openApiFeature;
    }
}