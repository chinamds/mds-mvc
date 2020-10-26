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
/// Specifies the unit of measure used for the horizontal resolution and the vertical resolution.
/// </summary>
public enum ResolutionUnit{
	///<summary>Dots Per Inch</summary>
	dpi(2),
	///<summary>Centimeters Per Inch</summary>
	dpcm(3);
	
	private final int resolutionUnit;
    private ResolutionUnit(int resolutionUnit) {
        this.resolutionUnit = resolutionUnit;
    }
}