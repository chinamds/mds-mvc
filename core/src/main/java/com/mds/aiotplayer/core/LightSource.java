/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Specifies possible light sources (white balance).
/// </summary>
public enum LightSource{
	Unknown(0, ""),
	Daylight(1, ""),
	Fluorescent(2, ""),
	Tungsten(3, ""),
	Flash(4, ""),
	FineWeather(9, "Fine Weather"),
	CloudyWeather(10, "Cloudy Weather"),
	Shade(11, ""),
	DaylightWluorescent(12, "Daylight Fluorescent"),
	DayWhiteFluorescent(13, "Day White Fluorescent"),
	CoolWhiteFluorescent(14, "Cool White Fluorescent"),
	WhiteFluorescent(15, "White Fluorescent"),
	StandardLightA(17, "Standard Light A"),
	StandardLightB(18, "Standard Light B"),
	StandardLightC(19, "Standard Light C"),
	D55(20, ""),
	D65(21, ""),
	D75(22, ""),
	D50(23, ""),
	ISOStudioTungsten(24, "ISO Studio Tungsten"),
	Other(255, "");
	
	private final int lightSource;
	private final String description;
    private LightSource(int lightSource, String description) {
        this.lightSource = lightSource;
        this.description = description;
    }
	public int getLightSource() {
		return lightSource;
	}
	public String getDescription() {
		return description;
	}
}