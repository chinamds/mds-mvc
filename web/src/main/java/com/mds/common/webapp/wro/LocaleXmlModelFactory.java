package com.mds.common.webapp.wro;


import org.apache.commons.lang.StringUtils;

import com.mds.i18n.util.I18nUtils;

import ro.isdc.wro.model.factory.XmlModelFactory;

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
public class LocaleXmlModelFactory extends XmlModelFactory {
	private static final String DEFAULT_FILE_NAME_LOCALE = "wro_{languageTag}.xml";
	@Override
	protected String getDefaultModelFilename() {
		String langTag = I18nUtils.getCurrentLanguageTag();
		if (StringUtils.isBlank(langTag) || langTag.equalsIgnoreCase("en") || langTag.equalsIgnoreCase("en_US")) {
			return super.getDefaultModelFilename();
		}
		
		return DEFAULT_FILE_NAME_LOCALE.replace("{languageTag}", langTag);
	}
}