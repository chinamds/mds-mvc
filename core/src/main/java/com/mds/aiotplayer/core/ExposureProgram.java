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
/// Specifies the class of the program used by the camera to set exposure when the picture is taken.
/// </summary>
public enum ExposureProgram{
	///<summary>not defined</summary>
	Undefined(0),
	///<summary>manual</summary>
	Manual(1),
	///<summary>normal program</summary>
	Normal(2),
	///<summary>aperture priority</summary>
	Aperture(3),
	///<summary>shutter priority</summary>
	Shutter(4),
	///<summary>creative program (biased toward depth of field)</summary>
	Creative(5),
	///<summary>action program (biased toward fast shutter speed)</summary>
	Action(6),
	///<summary>portrait mode (for close-up photos with the background out of focus)</summary>
	Portrait(7),
	///<summary>landscape mode (for landscape photos with the background in focus)</summary>
	Landscape(8),
	///<summary>9 to 255 - reserved</summary>
	Reserved(9);
	
	private final int exposureProgram;
    private ExposureProgram(int exposureProgram) {
        this.exposureProgram = exposureProgram;
    }
	public int getExposureProgram() {
		return exposureProgram;
	}
}