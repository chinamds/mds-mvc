package com.mds.aiotplayer.webapp.common.wro;

import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;

/**
 * Locator responsible for locating webjar resources. A webjar resource is a classpath resource respecting a certain
 * standard. <a href="http://www.webjars.org/">Read more</a> about webjars.
 * <p/>
 * This locator uses the following prefix to identify a locator capable of handling webjar resources:
 * <code>webjar:</code>
 *
 * @author Alex Objelean
 * @created 6 Jan 2013
 * @since 1.6.2
 */
public class LocaleWroManagerFactory extends BaseWroManagerFactory {
	 @Override
	 protected WroModelFactory newModelFactory() {
		 return new LocaleXmlModelFactory();
	 }
}