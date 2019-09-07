package com.mds.core;

import com.mds.core.exception.ArgumentException;

/// <summary>
/// References a version of the database schema used by MDS System. A new schema version is added for any
/// release that requires a database change. Data schemas earlier than 2.1.3162 are not supported.
/// </summary>
public enum MDSDataSchemaVersion
{
	// IMPORTANT: When modifying these values, be sure to update the functions ConvertMDSDataSchemaVersionToString and
	// ConvertMDSDataSchemaVersionToEnum as well!
	/// <summary>
	/// Gets the Unknown data schema version.
	/// </summary>
	Unknown(0),
	/// <summary>
	/// Gets the schema version for 2.1.3162.
	/// </summary>
	V1_0_0(1);
	
	private final int dataSchemaVersion;
    

    private MDSDataSchemaVersion(int dataSchemaVersion) {
        this.dataSchemaVersion = dataSchemaVersion;
    }

	/// <summary>
	/// Convert <paramref name="version"/> to its string equivalent. Example: Return "2.4.1" when <paramref name="version"/> 
	/// is <see cref="MDSDataSchemaVersion.V2_4_1"/>. This is a lookup function and does not return the current version 
	/// of the database or application schema requirements.
	/// </summary>
	/// <param name="version">The version of the gallery's data schema for which a string representation is to be returned.</param>
	/// <returns>Returns the string equivalent of the specified <see cref="MDSDataSchemaVersion"/> value.</returns>
	public static String convertMDSDataSchemaVersionToString(MDSDataSchemaVersion version)
	{
		switch (version)
		{
			case V1_0_0:
				return "1.0.0";
			default:
				throw new ArgumentException("The function MDS.Business.ConvertMDSDataSchemaVersionToString was not designed to handle the MDSDataSchemaVersion enumeration value" + version + ". A developer must update this method to handle this value.");
		}
	}

	/// <summary>
	/// Convert <paramref name="version"/> to its <see cref="MDSDataSchemaVersion"/> equivalent. Example: Return 
	/// <see cref="MDSDataSchemaVersion.V2_4_1"/> when <paramref name="version"/> is "02.04.01" or "2.4.1". This is a 
	/// lookup function and does not return the current version of the database or application schema requirements.
	/// </summary>
	/// <param name="version">The version of the gallery's data schema.</param>
	/// <returns>Returns the <see cref="MDSDataSchemaVersion"/> equivalent of the specified string.</returns>
	public static MDSDataSchemaVersion convertMDSDataSchemaVersionToEnum(String version)
	{
		if (version == null)
		{
			return MDSDataSchemaVersion.Unknown;
		}

		switch (version)
		{
			case "1.0.0":
				return MDSDataSchemaVersion.V1_0_0;
			
			default:
				return MDSDataSchemaVersion.Unknown;
		}
	}
}
