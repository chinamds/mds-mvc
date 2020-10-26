/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.services.factory;

import com.mds.kernel.ServiceManager;
import com.mds.services.CachingService;
import com.mds.services.ConfigurationService;
import com.mds.services.EmailService;
import com.mds.services.EventService;
import com.mds.services.RequestService;
import com.mds.utils.MDSPlus;

/**
 * Abstract factory to get services for the services package, use MDSServicesFactory.getInstance() to retrieve an
 * implementation
 */
public abstract class MDSServicesFactory {
    public abstract CachingService getCachingService();

    public abstract ConfigurationService getConfigurationService();

    public abstract EmailService getEmailService();

    public abstract EventService getEventService();

    public abstract RequestService getRequestService();

    public abstract ServiceManager getServiceManager();

    public static MDSServicesFactory getInstance() {
        return new MDSPlus().getServiceManager().getServiceByName("mDSServicesFactory", MDSServicesFactory.class);
    }


}
