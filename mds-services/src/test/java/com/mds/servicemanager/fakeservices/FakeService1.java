/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.servicemanager.fakeservices;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.mds.kernel.mixins.ConfigChangeListener;
import com.mds.kernel.mixins.InitializedService;
import com.mds.kernel.mixins.ServiceChangeListener;
import com.mds.kernel.mixins.ShutdownService;
import com.mds.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;


/**
 * This is just testing a fake service and running it through some paces to see if the lifecycles work
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class FakeService1 implements ConfigChangeListener, ServiceChangeListener,
                                     InitializedService, ShutdownService, Serializable {
    private static final long serialVersionUID = 1L;

    public int triggers = 0;

    public int getTriggers() {
        return triggers;
    }

    public String something = "aaronz";

    public String getSomething() {
        return something;
    }

    public void setSomething(String something) {
        this.something = something;
    }


    public FakeService1() {
    }

    public FakeService1(ConfigurationService configurationService) {
        // for manual construction
        this.configurationService = configurationService;
    }

    private ConfigurationService configurationService;

    @Autowired
    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.mixins.ConfigChangeListener#configurationChanged(java.util.List, java.util.Map)
     */
    public void configurationChanged(List<String> changedSettingNames,
                                     Map<String, String> changedSettings) {
        something = "config:" + changedSettings.get("azeckoski.FakeService1.something");
        triggers++;
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.mixins.ServiceChangeListener#serviceRegistered(java.lang.String, java.lang.Object, java
     * .util.List)
     */
    public void serviceRegistered(String serviceName, Object service,
                                  List<Class<?>> implementedTypes) {
        something = "registered:" + serviceName;
        triggers++;
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.mixins.ServiceChangeListener#serviceUnregistered(java.lang.String, java.lang.Object)
     */
    public void serviceUnregistered(String serviceName, Object service) {
        something = "unregistered:" + serviceName;
        triggers++;
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.mixins.InitializedService#init()
     */
    public void init() {
        something = "init";
        triggers = 1; // RESET to 1
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.mixins.ShutdownService#shutdown()
     */
    public void shutdown() {
        something = "shutdown";
        triggers++;
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.mixins.ConfigChangeListener#notifyForConfigNames()
     */
    public String[] notifyForConfigNames() {
        return null; // ALL
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.mixins.ServiceChangeListener#notifyForTypes()
     */
    public Class<?>[] notifyForTypes() {
        return null; // ALL
    }

}
