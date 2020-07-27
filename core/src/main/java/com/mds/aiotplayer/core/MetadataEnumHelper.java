package com.mds.aiotplayer.core;

import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Contains functionality to support the various enumerations related to metadata.
/// </summary>
public class MetadataEnumHelper{
	/// <summary>
	/// Determines if the specified <see cref="FlashMode" /> is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="flashMode">An instance of <see cref="FlashMode" /> to test.</param>
	/// <returns>Returns true if <paramref name="flashMode"/> is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidFlashMode(FlashMode flashMode)
	{
		switch (flashMode)
		{
			case FlashDidNotFire:
			case FlashFired:
			case StrobeReturnLightNotDetected:
			case StrobeReturnLightDetected:
			case FlashFiredCompulsoryFlashMode:
			case FlashFiredCompulsoryFlashModeReturnLightNotDetected:
			case FlashFiredCompulsoryFlashModeReturnLightDetected:
			case FlashDidNotFireCompulsoryFlashMode:
			case FlashDidNotFireAutoMode:
			case FlashFiredAutoMode:
			case FlashFiredAutoModeReturnLightNotDetected:
			case FlashFiredAutoModeReturnLightDetected:
			case NoFlashFunction:
			case FlashFiredRedEyeReductionMode:
			case FlashFiredRedEyeReductionModeReturnLightNotDetected:
			case FlashFiredRedEyeReductionModeReturnLightDetected:
			case FlashFiredCompulsoryFlashModeRedEyeReductionMode:
			case FlashFiredCompulsoryFlashModeRedEyeReductionModeReturnLightNotDetected:
			case FlashFiredCompulsoryFlashModeRedEyeReductionModeReturnLightDetected:
			case FlashFiredAutoModeRedEyeReductionMode:
			case FlashFiredAutoModeReturnLightNotDetectedRedEyeReductionMode:
			case FlashFiredAutoModeReturnLightDetectedRedEyeReductionMode:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Determines if the specified <see cref="MeteringMode" /> is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="meteringMode">An instance of <see cref="MeteringMode" /> to test.</param>
	/// <returns>Returns true if <paramref name="meteringMode"/> is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidMeteringMode(MeteringMode meteringMode)
	{
		switch (meteringMode)
		{
			case Average:
			case CenterWeightedAverage:
			case MultiSpot:
			case Other:
			case Partial:
			case Pattern:
			case Spot:
			case Unknown:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Determines if the specified <see cref="LightSource" /> is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="lightSource">An instance of <see cref="LightSource" /> to test.</param>
	/// <returns>Returns true if <paramref name="lightSource"/> is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidLightSource(LightSource lightSource)
	{
		switch (lightSource)
		{
			case D55:
			case D65:
			case D75:
			case Daylight:
			case Flash:
			case Fluorescent:
			case Other:
			case StandardLightA:
			case StandardLightB:
			case StandardLightC:
			case Tungsten:
			case Unknown:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Determines if the specified <see cref="ResolutionUnit" /> is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="resUnit">An instance of <see cref="ResolutionUnit" /> to test.</param>
	/// <returns>Returns true if <paramref name="resUnit"/> is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidResolutionUnit(ResolutionUnit resUnit)
	{
		switch (resUnit)
		{
			case dpcm:
			case dpi:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Determines if the specified <see cref="ExposureProgram" /> is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="expProgram">An instance of <see cref="ExposureProgram" /> to test.</param>
	/// <returns>Returns true if <paramref name="expProgram"/> is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidExposureProgram(ExposureProgram expProgram)
	{
		switch (expProgram)
		{
			case Action:
			case Aperture:
			case Creative:
			case Landscape:
			case Manual:
			case Normal:
			case Portrait:
			case Reserved:
			case Shutter:
			case Undefined:
				break;

			default:
				return false;
		}
		return true;
	}

	/// <summary>
	/// Determines if the specified <see cref="Orientation" /> is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="orientation">An instance of <see cref="Orientation" /> to test.</param>
	/// <returns>Returns true if <paramref name="orientation"/> is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidOrientation(Orientation orientation)
	{
		switch (orientation)
		{
			case NotInitialized:
			case None:
			case Normal:
			case Mirrored:
			case Rotated180:
			case Flipped:
			case FlippedAndRotated90:
			case Rotated270:
			case FlippedAndRotated270:
			case Rotated90:
				break;

			default:
				return false;
		}
		return true;
	}
}
