/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;

@Converter(autoApply = true)
public class MenuTargetConverter implements AttributeConverter<MenuTarget, String> {

    @Override
    public String convertToDatabaseColumn(MenuTarget attribute) {
    	if (attribute == null || attribute == MenuTarget.notspecified)
    		return null;
    	
        return attribute.toString();
    }

    @Override
    public MenuTarget convertToEntityAttribute(String dbData) {
    	if (StringUtils.isBlank(dbData))
    		return MenuTarget.notspecified;
    	
        return MenuTarget.valueOf(dbData);
    }
}
