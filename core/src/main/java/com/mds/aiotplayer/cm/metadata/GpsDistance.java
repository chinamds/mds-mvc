/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import java.text.MessageFormat;
import java.util.Arrays;

/// <summary>
/// Represents a measure of angular distance. Can be used to store the latitude or longitude component of GPS coordinates.
/// </summary>
public class GpsDistance{
	String[] directionValues = new String[] { "N", "S", "W", "E" };
	String[] negativeDirectionValues = new String[] { "S", "W" };

	private String direction; // "N", "S", "W", "E"
	private double degrees;
	private double minutes;
	private double seconds;

	/// <summary>
	/// Gets the bearing of this instance. Returns "N", "S", "W", "E".
	/// </summary>
	/// <value>A <see cref="System.String"/>.</value>
	public String getDirection(){
		return this.direction; 
	}

	/// <summary>
	/// Gets the degrees component of the current instance.
	/// </summary>
	/// <value>The degrees.</value>
	public double getDegrees()	{
		return this.degrees; 
	}

	/// <summary>
	/// Gets the minutes component of the current instance.
	/// </summary>
	/// <value>The minutes.</value>
	public double getMinutes()	{
		return this.minutes; 
	}

	/// <summary>
	/// Gets the seconds component of the current instance.
	/// </summary>
	/// <value>The seconds.</value>
	public double getSeconds()	{
		return this.seconds; 
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="GpsDistance"/> class.
	/// </summary>
	/// <param name="direction">The bearing of the direction. Specify "N", "S", "W", or "E".</param>
	/// <param name="degrees">The degrees.</param>
	/// <param name="minutes">The minutes.</param>
	/// <param name="seconds">The seconds.</param>
	public GpsDistance(String direction, double degrees, double minutes, double seconds){
		Arrays.sort(this.directionValues);
		if (Arrays.binarySearch(this.directionValues, direction) >= 0){
			this.direction = direction;
		}

		this.degrees = (float)degrees;
		this.minutes = (float)minutes;
		this.seconds = (float)seconds;
	}

	/// <summary>
	/// Performs an explicit conversion from <see cref="MDS.Business.Metadata.GpsDistance"/> to <see cref="System.Double"/>.
	/// </summary>
	/// <param name="obj">The obj.</param>
	/// <returns>The result of the conversion.</returns>
	/*public static explicit operator double(GpsDistance obj)
	{
		if (obj == null)
			return 0;

		return obj.toDouble();
	}*/

	/// <summary>
	/// Generates an integer representation of the current instance. Will be negative for values west of the Prime Meridian
	/// and south of the equator. Ex: "46.5925", "-88.9882"
	/// </summary>
	/// <returns>A <see cref="System.Double"/> that represents this instance.</returns>
	public double toDouble(){
		double distance = degrees + minutes / 60.0d + seconds / 3600.0d;

		Arrays.sort(this.negativeDirectionValues);
		if (Arrays.binarySearch(this.negativeDirectionValues, this.direction) >= 0)	{
			distance = distance * -1;
		}

		return distance;
	}

	/// <summary>
	/// Generates a decimal representation of the current instance, including the north/south/east/west indicator.
	/// Ex: "46.5925° N", "88.9882° W"
	/// </summary>
	/// <returns>A <see cref="System.String"/> that represents this instance.</returns>
	public String toDoubleString(){
		return MessageFormat.format("{0, number, #.000000}° {1}", Math.abs(toDouble()), direction);
	}

	/// <summary>
	/// Generates a String containing the degrees, minutes, and seconds of the current instance. Includes the north/south/east/west indicator.
	/// Ex: "46°32'15.24" N"
	/// </summary>
	/// <returns>A <see cref="System.String"/> that represents this instance.</returns>
	public String toDegreeMinuteSecondString(){
		return MessageFormat.format("{0, number, integer}°{1, number, integer}'{2, number, #.00}\" {3}", (float)degrees, (float)minutes, (float)seconds, direction);
	}

	/// <summary>
	/// Returns a <see cref="System.String"/> that represents this instance. Internally, this function calls <see cref="toDegreeMinuteSecondString" />.
	/// </summary>
	/// <returns>A <see cref="System.String"/> that represents this instance.</returns>
	public String toString(){
		return toDegreeMinuteSecondString();
	}
}
