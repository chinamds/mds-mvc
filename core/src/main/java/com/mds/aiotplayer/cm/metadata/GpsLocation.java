/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.metadata;

import java.util.Arrays;

import org.apache.commons.lang3.Conversion;
import org.apache.commons.lang3.StringUtils;

import com.mds.aiotplayer.core.exception.NotSupportedException;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.MathUtil;

/// <summary>
/// Represents a geographical location on earth.
/// </summary>
public class GpsLocation {
	private String version;
	private Double altitude;
	private GpsDistance latitude;
	private GpsDistance longitude;
	private GpsDistance destLatitude;
	private GpsDistance destLongitude;

	/// <summary>
	/// The version of the GPS information. Example: "2.2.0.0"
	/// </summary>
	/// <value>The version of the GPS information.</value>
	public String getVersion()	{
		return this.version;
	}
	
	public void setVersion(String version)	{
		this.version = version;
	}

	/// <summary>
	/// The altitude, in meters, of the content object. Will be negative for values below sea level.
	/// </summary>
	/// <value>The altitude.</value>
	public Double getAltitude()	{
		return this.altitude;
	}
	
	public void setAltitude(Double altitude)	{
		this.altitude = altitude;
	}

	/// <summary>
	/// Gets or sets the latitude portion of the current instance.
	/// </summary>
	/// <value>The latitude.</value>
	public GpsDistance getLatitude()	{
		return this.latitude;
	}
	
	public void setLatitude(GpsDistance latitude){
		this.latitude = latitude;
	}

	/// <summary>
	/// Gets or sets the longitude portion of the current instance.
	/// </summary>
	/// <value>The longitude.</value>
	public GpsDistance getLongitude()	{
		return this.longitude;
	}
	
	public void setLongitude(GpsDistance longitude)	{
		this.longitude = longitude;
	}

	/// <summary>
	/// Gets or sets the destination latitude portion of the current instance.
	/// </summary>
	/// <value>The latitude.</value>
	public GpsDistance getDestLatitude()	{
		return this.destLatitude;
	}
	
	public void setDestLatitude(GpsDistance destLatitude)	{
		this.destLatitude = destLatitude;
	}

	/// <summary>
	/// Gets or sets the destination longitude portion of the current instance.
	/// </summary>
	/// <value>The longitude.</value>
	public GpsDistance getDestLongitude()	{
		return this.destLongitude;
	}
	
	public void setDestLongitude(GpsDistance destLongitude)	{
		this.destLongitude = destLongitude;
	}

	/// <summary>
	/// Generates a decimal-based version of the GPS coordinates. Ex: "46.5925° N 88.9882° W"
	/// </summary>
	/// <returns>A <see cref="System.String"/> that represents this instance.</returns>
	public String toLatitudeLongitudeDecimalString(){
		return StringUtils.join(new String[]{latitude.toDoubleString(), " ", longitude.toDoubleString()});
	}

	/// <summary>
	/// Expresses the value of the GPS coordinates in terms of degrees, minutes, and seconds. Ex: "46°32'15.24" N 88°53'25.82" W"
	/// </summary>
	/// <returns>A <see cref="System.String"/> that represents this instance.</returns>
	public String toLatitudeLongitudeDegreeMinuteSecondString()	{
		return StringUtils.join(new String[]{latitude.toDegreeMinuteSecondString(), " ", longitude.toDegreeMinuteSecondString()});
	}

	/// <summary>
	/// Generates a decimal-based version of the destination GPS coordinates. Ex: "46.5925° N 88.9882° W"
	/// </summary>
	/// <returns>A <see cref="System.String"/> that represents this instance.</returns>
	public String toDestLatitudeLongitudeDecimalString(){
		return StringUtils.join(new String[]{destLatitude.toDoubleString(), " ", destLongitude.toDoubleString()});
	}

	/// <summary>
	/// Expresses the value of the destination GPS coordinates in terms of degrees, minutes, and seconds. Ex: "46°32'15.24" N 88°53'25.82" W"
	/// </summary>
	/// <returns>A <see cref="System.String"/> that represents this instance.</returns>
	public String toDestLatitudeLongitudeDegreeMinuteSecondString()	{
		return StringUtils.join(new String[]{destLatitude.toDegreeMinuteSecondString(), " ", destLongitude.toDegreeMinuteSecondString()});
	}

	/// <summary>
	/// Parses the GPS data from the specified <paramref name="bmpMetadata" /> and returns the data in an instance of <see cref="GpsLocation" />.
	/// </summary>
	/// <param name="bmpMetadata">An object containing the metadata.</param>
	/// <returns>An instance of <see cref="GpsLocation" />.</returns>
	public static GpsLocation parse(WpfMetadata bmpMetadata){
		GpsLocation gps = new GpsLocation();

		gps.setVersion(getVersion(bmpMetadata));
		gps.setLatitude(getLatitude(bmpMetadata));
		gps.setLongitude(getLongitude(bmpMetadata));
		gps.setAltitude(getAltitude(bmpMetadata));
		gps.setDestLatitude(getDestLatitude(bmpMetadata));
		gps.setDestLongitude(getDestLongitude(bmpMetadata));

		//// Combine date portion of gpsDate or gpsDate2 with time portion of gpsTime2
		//object gpsDate = bmpMetadata.getQuery("System.GPS.Date"); // System.Runtime.InteropServices.ComTypes.FILETIME
		//object gpsDate2 = bmpMetadata.getQuery("/app1/ifd/gps/{ushort=29}"); // 2010:08:08

		//DateTime gpsDate3 = Convert((System.Runtime.InteropServices.ComTypes.FILETIME)gpsDate);

		//ulong[] gpsTime2 = bmpMetadata.getQuery("/app1/ifd/gps/{ushort=7}") ; // ulong[3]
		//double hh = SplitLongAndDivide(gpsTime2[0]);
		//double mm = SplitLongAndDivide(gpsTime2[1]);
		//double ss = SplitLongAndDivide(gpsTime2[2]);

		//object satellites = bmpMetadata.getQuery("System.GPS.Satellites"); //"05"
		//object satellites2 = bmpMetadata.getQuery("/app1/ifd/gps/{ushort=8}"); //"05"

		//double longitude = ConvertCoordinate(longitudeArray);

		return gps;
	}

	private static Double getAltitude(WpfMetadata bmpMetadata)	{
		Object altObj = getQuery(bmpMetadata, "System.GPS.Altitude");

		if (altObj == null)
		{
			altObj = getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=6}");

			if (altObj != null)
			{
				altObj = convertCoordinate(new long[] { (long)altObj })[0];
			}
		}

		if (altObj == null)
		{
			return null;
		}

		return (isBelowSeaLevel(bmpMetadata) ? (double)altObj * (-1) : (double)altObj);
	}

	/// <summary>
	/// Determines whether the GPS altitude is above or below sea level. Returns <c>false</c> if the metadata is not present.
	/// </summary>
	/// <param name="bmpMetadata">An Object containing the metadata.</param>
	/// <returns>
	/// 	<c>true</c> if the GPS position is below sea level; otherwise, <c>false</c>.
	/// </returns>
	private static boolean isBelowSeaLevel(WpfMetadata bmpMetadata)	{
		Object directionObj = getQuery(bmpMetadata, "System.GPS.AltitudeRef");
		if (directionObj == null)
			directionObj = getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=5}");

		boolean isBelowSeaLevel = false;
		if (directionObj != null)
		{
			/*try
			{
				isBelowSeaLevel = (Convert.toByte(directionObj, CultureInfo.InvariantCulture) == 1); // 0 = above sea level; 1 = below sea level
			}
			catch (InvalidCastException) { }*/
		}

		return isBelowSeaLevel;
	}

	/// <summary>
	/// Gets the latitude GPS data from <paramref name="bmpMetadata" />.
	/// </summary>
	/// <param name="bmpMetadata">An Object containing the metadata.</param>
	/// <returns>An instance of <see cref="GpsDistance" />.</returns>
	private static GpsDistance getLatitude(WpfMetadata bmpMetadata)	{
		String direction = (String)getQuery(bmpMetadata, "System.GPS.LatitudeRef");
		if (direction == null)
			direction = (String)getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=1}");

		double[] latitude = (double[])getQuery(bmpMetadata, "System.GPS.Latitude") ;
		if (latitude == null)
			latitude = convertCoordinate((long[])getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=2}") );

		if (!StringUtils.isBlank(direction) && (latitude != null))
		{
			return new GpsDistance(direction, latitude[0], latitude[1], latitude[2]);
		}
		else
		{
			return null;
		}
	}

	/// <summary>
	/// Gets the longitude GPS data from <paramref name="bmpMetadata" />.
	/// </summary>
	/// <param name="bmpMetadata">An Object containing the metadata.</param>
	/// <returns>An instance of <see cref="GpsDistance" />.</returns>
	private static GpsDistance getLongitude(WpfMetadata bmpMetadata){
		String direction = (String)getQuery(bmpMetadata, "System.GPS.LongitudeRef");
		if (direction == null)
			direction = (String)getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=3}");

		double[] longitude = (double[])getQuery(bmpMetadata, "System.GPS.Longitude") ;
		if (longitude == null)
			longitude = convertCoordinate((long[])getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=4}") );

		if (!StringUtils.isBlank(direction) && (longitude != null))
		{
			return new GpsDistance(direction, longitude[0], longitude[1], longitude[2]);
		}
		else
		{
			return null;
		}
	}

	/// <summary>
	/// Gets the destination latitude GPS data from <paramref name="bmpMetadata" />.
	/// </summary>
	/// <param name="bmpMetadata">An Object containing the metadata.</param>
	/// <returns>An instance of <see cref="GpsDistance" />.</returns>
	private static GpsDistance getDestLatitude(WpfMetadata bmpMetadata)	{
		String direction = (String)getQuery(bmpMetadata, "System.GPS.DestLatitudeRef");
		if (direction == null)
			direction = (String)getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=19}");

		double[] latitude = (double[])getQuery(bmpMetadata, "System.GPS.DestLatitude") ;
		if (latitude == null)
			latitude = convertCoordinate((long[])getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=20}") );

		if (!StringUtils.isBlank(direction) && (latitude != null))
		{
			return new GpsDistance(direction, latitude[0], latitude[1], latitude[2]);
		}
		else
		{
			return null;
		}
	}

	/// <summary>
	/// Gets the destination longitude GPS data from <paramref name="bmpMetadata" />.
	/// </summary>
	/// <param name="bmpMetadata">An Object containing the metadata.</param>
	/// <returns>An instance of <see cref="GpsDistance" />.</returns>
	private static GpsDistance getDestLongitude(WpfMetadata bmpMetadata)	{
		String direction = (String)getQuery(bmpMetadata, "System.GPS.DestLongitudeRef");
		if (direction == null)
			direction = (String)getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=21}");

		double[] longitude = (double[])getQuery(bmpMetadata, "System.GPS.DestLongitude");
		if (longitude == null)
			longitude = convertCoordinate((long[])getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=22}"));

		if (!StringUtils.isBlank(direction) && (longitude != null))
		{
			return new GpsDistance(direction, longitude[0], longitude[1], longitude[2]);
		}
		else
		{
			return null;
		}
	}

	/// <summary>
	/// Gets the version of the GPS information. Example: "2.2.0.0"
	/// </summary>
	/// <param name="bmpMetadata">An Object containing the metadata.</param>
	/// <returns>An instance of <see cref="System.String" />.</returns>
	private static String getVersion(WpfMetadata bmpMetadata)	{
		String version = StringUtils.EMPTY;
		byte[] versionTokens = (byte[])getQuery(bmpMetadata, "System.GPS.VersionID");
		if (versionTokens == null)
			versionTokens = (byte[])getQuery(bmpMetadata, "/app1/ifd/gps/{ushort=0}");

		if (versionTokens == null) return version;

		for(byte versionToken : versionTokens){
			version += versionToken + ".";
		}

		return StringUtils.stripEnd(version, ".");
	}

	/// <summary>
	/// Invokes the <see cref="BitmapMetadata.getQuery" /> method on <paramref name="bmpMetadata" />, passing in the specified
	/// <paramref name="query" />. Any <see cref="NotSupportedException" /> exceptions are silently swallowed.
	/// </summary>
	/// <param name="bmpMetadata">An object containing the metadata.</param>
	/// <param name="query">The query to execute against <paramref name="bmpMetadata" />.</param>
	/// <returns></returns>
	private static Object getQuery(WpfMetadata bmpMetadata, String query)	{
		return bmpMetadata.getQuery(query);
		/*try
		{
			return bmpMetadata.getQuery(query);
		}
		catch (NotSupportedException) { return null; }
		catch (COMException) { return null; }*/
	}

	/// <summary>
	/// Convert the unsigned long values into an equivalent array of <see cref="System.Double" /> values.
	/// </summary>
	/// <param name="values">The values to convert.</param>
	/// <returns>Returns an array of <see cref="System.Double" /> values.</returns>
	private static double[] convertCoordinate(long[] values){
		if (values == null) return null;

		double[] convertedValues = new double[values.length];

		for (int index = 0; index < values.length; index++){
			convertedValues[index] = splitLongAndDivide(values[index]);
		}

		return convertedValues;
	}

	/// <summary>
	/// Convert the <paramref name="number" /> into a <see cref="System.Double" />.
	/// </summary>
	/// <param name="number">The number to convert.</param>
	/// <returns>Returns a <see cref="System.Double" />.</returns>
	private static double splitLongAndDivide(long number)	{
		byte[] bytes = MathUtil.longToBytes(number);
		double dbl1 = MathUtil.bytesToLong(Arrays.copyOfRange(bytes, 0, 3));
		double dbl2 = MathUtil.bytesToLong(Arrays.copyOfRange(bytes, 4, 7));

		if (Math.abs(dbl2 - 0) < .0001) return 0;

		return (dbl1 / dbl2);
	}
}