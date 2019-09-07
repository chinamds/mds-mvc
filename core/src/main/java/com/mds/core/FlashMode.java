package com.mds.core;

import com.mds.util.StringUtils;

/// <summary>
/// Specifies the flash mode.
/// </summary>
public enum FlashMode{
	FlashDidNotFire(0x0000, "Flash did not fire"),
	FlashFired(0x0001, "Flash fired"),
	StrobeReturnLightNotDetected(0x0005, "Strobe return light not detected"),
	StrobeReturnLightDetected(0x0007, "Strobe return light detected"),
	FlashFiredCompulsoryFlashMode(0x0009, "Flash fired, compulsory flash mode"),
	FlashFiredCompulsoryFlashModeReturnLightNotDetected(0x000D, "Flash fired, compulsory flash mode, return light not detected"),
	FlashFiredCompulsoryFlashModeReturnLightDetected(0x000F, "Flash fired, compulsory flash mode, return light detected"),
	FlashDidNotFireCompulsoryFlashMode(0x0010, "Flash did not fire, compulsory flash mode"),
	FlashDidNotFireAutoMode(0x0018, "Flash did not fire, auto mode"),
	FlashFiredAutoMode(0x0019, "Flash fired, auto mode"),
	FlashFiredAutoModeReturnLightNotDetected(0x001D, "Flash fired, auto mode, return light not detected"),
	FlashFiredAutoModeReturnLightDetected(0x001F, "Flash fired, auto mode, returnLightDetected"),
	NoFlashFunction(0x0020, "No flash function"),
	FlashFiredRedEyeReductionMode(0x0041, "Flash fired, red-eye reduction mode"),
	FlashFiredRedEyeReductionModeReturnLightNotDetected(0x0045, "Flash fired, red-eye reduction mode, return light not detected"),
	FlashFiredRedEyeReductionModeReturnLightDetected(0x0047, "Flash fired, red-eye reduction mode, returnLightDetected"),
	FlashFiredCompulsoryFlashModeRedEyeReductionMode(0x0049, "Flash fired, compulsory flash mode, red-eye reduction mode"),
	FlashFiredCompulsoryFlashModeRedEyeReductionModeReturnLightNotDetected(0x004D, "Flash fired, compulsory flash mode, red-eye reduction mode, return light not detected"),
	FlashFiredCompulsoryFlashModeRedEyeReductionModeReturnLightDetected(0x004F, "Flash fired, compulsory flash mode, red-eye reduction mode, returnLightDetected"),
	FlashFiredAutoModeRedEyeReductionMode(0x0059, "Flash fired, auto mode, red-eye reduction mode"),
	FlashFiredAutoModeReturnLightNotDetectedRedEyeReductionMode(0x005D, "Flash fired, auto mode, return light not detected, red-eye reduction mode"),
	FlashFiredAutoModeReturnLightDetectedRedEyeReductionMode(0x005F, "Flash fired, auto mode, return light detected, red-eye reduction mode");
	
	private final int flashMode;
	private final String description;
    private FlashMode(int flashMode, String description) {
        this.flashMode = flashMode;
        this.description = description;
    }
	public String getDescription() {
		return description;
	}
	public int getFlashMode() {
		return flashMode;
	}
}