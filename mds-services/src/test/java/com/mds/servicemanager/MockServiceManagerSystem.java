/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.servicemanager;

import java.util.List;
import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;


/**
 * This Mock allows us to pretend that a SMS is its own parent,
 * for testing use only
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class MockServiceManagerSystem implements ServiceManagerSystem {

    private final ServiceManagerSystem sms;

    public MockServiceManagerSystem(ServiceManagerSystem sms) {
        this.sms = sms;
    }

    /* (non-Javadoc)
     * @see com.mds.servicemanager.ServiceManagerSystem#getServices()
     */
    public Map<String, Object> getServices() {
        return this.sms.getServices();
    }

    /* (non-Javadoc)
     * @see com.mds.servicemanager.ServiceManagerSystem#shutdown()
     */
    public void shutdown() {
        this.sms.shutdown();
    }

    /* (non-Javadoc)
     * @see com.mds.servicemanager.ServiceManagerSystem#startup()
     */
    public void startup() {
        this.sms.startup();
    }

    /* (non-Javadoc)
     * @see com.mds.servicemanager.ServiceManagerSystem#unregisterService(java.lang.String)
     */
    public void unregisterService(String name) {
        this.sms.unregisterService(name);
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.ServiceManager#getServiceByName(java.lang.String, java.lang.Class)
     */
    public <T> T getServiceByName(String name, Class<T> type) {
        return this.sms.getServiceByName(name, type);
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return sms.getApplicationContext();
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.ServiceManager#getServicesByType(java.lang.Class)
     */
    public <T> List<T> getServicesByType(Class<T> type) {
        return this.sms.getServicesByType(type);
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.ServiceManager#getServicesNames()
     */
    public List<String> getServicesNames() {
        return this.sms.getServicesNames();
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.ServiceManager#isServiceExists(java.lang.String)
     */
    public boolean isServiceExists(String name) {
        return this.sms.isServiceExists(name);
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.ServiceManager#pushConfig(java.util.Map)
     */
    public void pushConfig(Map<String, Object> settings) {
        this.sms.pushConfig(settings);
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.ServiceManager#registerService(java.lang.String, java.lang.Object)
     */
    public void registerService(String name, Object service) {
        this.sms.registerService(name, service);
    }

    @Override
    public void registerServiceNoAutowire(String name, Object service) {
        this.sms.registerServiceNoAutowire(name, service);
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.ServiceManager#registerServiceClass(java.lang.String, java.lang.Class)
     */
    public <T> T registerServiceClass(String name, Class<T> type) {
        return this.sms.registerServiceClass(name, type);
    }

}
