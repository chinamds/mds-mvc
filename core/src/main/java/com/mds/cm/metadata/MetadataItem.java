package com.mds.cm.metadata;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.Fraction;

import com.mds.core.exception.ArgumentNullException;
import com.mds.core.exception.BusinessException;
import com.mds.core.ExtractedValueType;
import com.mds.core.PropertyTagType;
import com.mds.core.RawMetadataItemName;

/// <summary>
/// Contains functionality for interacting with a <see cref="Property" /> object.
/// </summary>
public class MetadataItem
{
	//#region Private Fields

	private String propItem;
	private Object propertyItemValue;
	private ExtractedValueType extractedValueType;

	private final int NUM_BITS_PER_BYTE = 8;
	private final int NUM_BYTES_PER_32_BIT_INT = 4;
	private final int NUM_BYTES_PER_16_BIT_INT = 2;

	//private static System.Text.UTF8Encoding _utf8Encoding = new System.Text.UTF8Encoding();
	//private static System.Text.UnicodeEncoding _unicodeEncoding = new System.Text.UnicodeEncoding();

	//#endregion

	//#region Constructors

	/// <summary>
	/// Private constructor so class can't be instantiated with default constructor from the outside.
	/// </summary>
	private MetadataItem() { }

	/// <summary>
	/// Instantiate a new instance of the <see cref="MetadataItem" /> class with the specified <paramref name="propItem"/>.
	/// </summary>
	/// <param name="propItem">A <see cref="System.Drawing.Imaging.Property" /> object for which to retrieve information.</param>
	public MetadataItem(String propItem){
		this.propItem = propItem;

		ExtractPropertyValue();
	}
	
	public MetadataItem(String propItem, String value){
		this.propItem = propItem;
		this.propertyItemValue = value;
	}


	//#endregion

	//#region Public Properties

	/// <summary>
	/// Gets the <see cref="RawMetadataItemName"/> of the current instance. This value corresponds to the Id property
	/// of the <see cref="System.Drawing.Imaging.Property"/> instance specified in the constructor.
	/// </summary>
	/// <value>The name of the raw metadata item.</value>
	public RawMetadataItemName getRawMetadataItemName()	{
		return null; //(RawMetadataItemName)this.propItem.Id;
	}

	/// <summary>
	/// Gets an enumeration specifying the data type of the values stored in the current <see cref="Property"/> property.
	/// Note that these are not ordinary .NET types.
	/// </summary>
	/// <value>The enum value specifying the data type of the values stored in the current <see cref="Property"/> property.</value>
	public PropertyTagType getPropertyTagType()	{
		return null; //(PropertyTagType)this.propItem.Type;
	}

	/// <summary>
	/// Gets an enumeration specifying the data type of the value stored in the <see cref="Value" /> property.
	/// </summary>
	/// <value>The enum value specifying the data type of the value stored in the <see cref="Value" /> property.</value>
	public ExtractedValueType getExtractedValueType(){
		return this.extractedValueType; 
	}
	
	public void setExtractedValueType(ExtractedValueType extractedValueType){
		this.extractedValueType = extractedValueType;
	}

	/// <summary>
	/// Gets the <see cref="System.Drawing.Imaging.Property" /> for this metadata item.
	/// </summary>
	public String getProperty(){
		return this.propItem;
	}

	/// <summary>
	/// Gets the value of the current <see cref="Property"/>. It is converted from the original byte array to
	/// to the appropriate user-friendly .NET type. The type of the value is stored in the <see cref="ExtractedValueType" /> property.
	/// </summary>
	public Object getValue(){
		return this.propertyItemValue;
	}

	//#endregion

	//#region Private Methods

	/// <summary>
	/// Extracts the value of the current Property and stores it in the private field _propertyItemValue. 
	/// It is converted to the appropriate user-friendly .NET type based on the PropertyTagType enumeration and whether
	/// the value contains a single item or an array. The type of the converted value is stored in the ExtractedValueType property.
	/// </summary>
	/// <remarks>
	/// PropertyTagType.Byte and ASCII are converted to a String (ExtractedValueType.String). PropertyTagType.UnsignedShort, UnsignedInt, 
	/// and Int are converted to System.Int64 or System.Int64[] (ExtractedValueType.Int64 or ExtractedValueType.Int64Array).
	/// PropertyTagType.Fraction and UnsignedFraction are converted to the Fraction class or an array of Fraction objects 
	/// (ExtractedValueType.Fraction or ExtractedValueType.FractionArray). PropertyTagType.Undefined and any other 
	/// PropertyTagType enumerations are returned as System.Byte[] (ExtractedValueType.ByteArray). After this function runs, 
	/// the ExtractedValueType property is guaranteed to be set a value other than ExtractedValueType.NotDefined. The field 
	/// _propertyItemValue is guaranteed to be a type that matches the ExtractedValueType enumeration, which means it will be
	/// one of these types: String, Int64, Int64[], Byte[], Fraction, or Fraction[].
	/// </remarks>
	private void ExtractPropertyValue()	{
		Object propertyItemValue = StringUtils.EMPTY;
		ExtractedValueType formattedValueType = ExtractedValueType.NotDefined;

		/*switch ((PropertyTagType)this.getPropertyTagType()){
			case Byte: { propertyItemValue = ExtractPropertyValueByte(ref formattedValueType); break; }
			case ASCII: { propertyItemValue = ExtractPropertyValueString(ref formattedValueType); break; }
			case UnsignedShort: { propertyItemValue = ExtractPropertyValueUnsignedShort(ref formattedValueType); break; }
			case UnsignedInt: { propertyItemValue = ExtractPropertyValueUnsignedInt(ref formattedValueType); break; }
			case Int: { propertyItemValue = ExtractPropertyValueInt(ref formattedValueType); break; }
			case UnsignedFraction: { propertyItemValue = ExtractPropertyValueUnsignedFraction(ref formattedValueType); break; }
			case Fraction: { propertyItemValue = ExtractPropertyValueSignedFraction(ref formattedValueType); break; }
			case Undefined:
			default: { propertyItemValue = ExtractPropertyValueUndefined(ref formattedValueType); break; }
		}

		this._extractedValueType = formattedValueType;
		this._propertyItemValue = propertyItemValue;*/

		if (this.extractedValueType == ExtractedValueType.NotDefined)
		{
			throw new BusinessException("The function MDS.Business.Metadata.MetadataItem.ExtractPropertyValue() must assign a value other than NotDefined to the field _extractedValueType.");
		}
	}

	private byte[] ExtractPropertyValueUndefined(ExtractedValueType formattedValueType)	{
		/*formattedValueType = ExtractedValueType.ByteArray;

		if (this.propItem.Value == null)
			return new byte[] { 0 };

		return this.propItem.Value;*/
		return null;
	}

	private Object ExtractPropertyValueSignedFraction(ExtractedValueType formattedValueType){
		/*Fraction[] resultSFraction = new Fraction[this.propItem.Len / NUM_BITS_PER_BYTE];
		int sNominator;
		int sDenominator;
		for (int i = 0; i < resultSFraction.length(); i++)
		{
			try
			{
				sNominator = BitConverter.ToInt32(this.propItem.Value, i * NUM_BITS_PER_BYTE);
				sDenominator = BitConverter.ToInt32(this.propItem.Value, (i * NUM_BITS_PER_BYTE) + NUM_BYTES_PER_32_BIT_INT);
			}
			catch (ArgumentNullException)
			{
				formattedValueType = ExtractedValueType.Fraction;
				return new Fraction(0, 1);
			}
			catch (ArgumentOutOfRangeException)
			{
				formattedValueType = ExtractedValueType.Fraction;
				return new Fraction(0, 1);
			}

			resultSFraction[i] = new Fraction(sNominator, sDenominator);
		}

		if (resultSFraction.length() == 1)
		{
			formattedValueType = ExtractedValueType.Fraction;
			return resultSFraction[0];
		}
		else
		{
			formattedValueType = ExtractedValueType.FractionArray;

			// Comment out, since it causes a huge performance hit when synchronizing files that contain this type of metadata item.
			// Perhaps find some way to raise the visibility of this by noting it once per application run.
			//String msg = String.format("Discovered an Exif metadata item named {0} that is of type MDS.Business.Fraction[] rather than the usual MDS.Business.Fraction. MDS System cannot process this metadata and will discard it. One may want to modify MDS System to handle this data type.", this.RawMetadataItemName);
			//ErrorHandler.CustomExceptions.BusinessException ex = new ErrorHandler.CustomExceptions.BusinessException(msg);
			//ErrorHandler.Error.Record(ex, int.MinValue, Factory.LoadGallerySettings(), AppSetting.Instance);
			return null;
		}*/
		return null;
	}

	private Object ExtractPropertyValueUnsignedFraction(ExtractedValueType formattedValueType){
		/*Fraction[] resultFraction = new Fraction[this.propItem.Len / NUM_BITS_PER_BYTE];
		uint uNominator;
		uint uDenominator;
		for (int i = 0; i < resultFraction.length(); i++)
		{
			uNominator = 1;
			try
			{
				uNominator = BitConverter.ToUInt32(this.propItem.Value, i * NUM_BITS_PER_BYTE);
				uDenominator = BitConverter.ToUInt32(this.propItem.Value, (i * NUM_BITS_PER_BYTE) + NUM_BYTES_PER_32_BIT_INT);
			}
			catch (ArgumentNullException)
			{
				formattedValueType = ExtractedValueType.Fraction;
				return new Fraction(0, 1);
			}
			catch (ArgumentOutOfRangeException)
			{
				formattedValueType = ExtractedValueType.Fraction;
				return new Fraction(0, 1);
			}

			resultFraction[i] = new Fraction(uNominator, uDenominator);
		}

		if (resultFraction.length() == 1)
		{
			formattedValueType = ExtractedValueType.Fraction;
			return resultFraction[0];
		}
		else
		{
			formattedValueType = ExtractedValueType.FractionArray;

			// Comment out, since it causes a huge performance hit when synchronizing files that contain this type of metadata item.
			// Perhaps find some way to raise the visibility of this by noting it once per application run.
			//String msg = String.format("Discovered an Exif metadata item named {0} that is of type MDS.Business.Fraction[] rather than the usual MDS.Business.Fraction. MDS System cannot process this metadata and will discard it. One may want to modify MDS System to handle this data type.", this.RawMetadataItemName);
			//ErrorHandler.CustomExceptions.BusinessException ex = new ErrorHandler.CustomExceptions.BusinessException(msg);
			//ErrorHandler.Error.Record(ex, int.MinValue, Factory.LoadGallerySettings(), AppSetting.Instance);
			return null;
		}*/
		return null;
	}

	private Object ExtractPropertyValueInt(ExtractedValueType formattedValueType){
		/*System.Int64[] resultInt64 = new System.Int64[this.propItem.Len / NUM_BYTES_PER_32_BIT_INT];
		for (int i = 0; i < resultInt64.length(); i++)
		{
			try
			{
				resultInt64[i] = Convert.ToInt64(BitConverter.ToInt32(this.propItem.Value, i * NUM_BYTES_PER_32_BIT_INT));
			}
			catch (ArgumentNullException)
			{
				formattedValueType = ExtractedValueType.Int64;
				return Convert.ToInt64(0);
			}
			catch (ArgumentOutOfRangeException)
			{
				formattedValueType = ExtractedValueType.Int64;
				return Convert.ToInt64(0);
			}
		}

		if (resultInt64.length() == 1)
		{
			formattedValueType = ExtractedValueType.Int64;
			return resultInt64[0];
		}
		else
		{
			formattedValueType = ExtractedValueType.Int64Array;

			// Comment out, since it causes a huge performance hit when synchronizing files that contain this type of metadata item.
			// Perhaps find some way to raise the visibility of this by noting it once per application run.
			//String msg = String.format("Discovered an Exif metadata item named {0} that is of type System.Int64[] rather than the usual System.Int64. MDS System cannot process this metadata and will discard it. One may want to modify MDS System to handle this data type.", this.RawMetadataItemName);
			//ErrorHandler.CustomExceptions.BusinessException ex = new ErrorHandler.CustomExceptions.BusinessException(msg);
			//ErrorHandler.Error.Record(ex, int.MinValue, Factory.LoadGallerySettings(), AppSetting.Instance);
			return null;
		}*/
		return null;
	}

	private Object ExtractPropertyValueUnsignedInt(ExtractedValueType formattedValueType){
		/*System.Int64[] resultInt64 = new System.Int64[this.propItem.Len / NUM_BYTES_PER_32_BIT_INT];
		for (int i = 0; i < resultInt64.length(); i++)
		{
			try
			{
				resultInt64[i] = Convert.ToInt64(BitConverter.ToUInt32(this.propItem.Value, i * NUM_BYTES_PER_32_BIT_INT));
			}
			catch (ArgumentNullException)
			{
				formattedValueType = ExtractedValueType.Int64;
				return Convert.ToInt64(0);
			}
			catch (ArgumentOutOfRangeException)
			{
				formattedValueType = ExtractedValueType.Int64;
				return Convert.ToInt64(0);
			}
		}

		if (resultInt64.length() == 1)
		{
			formattedValueType = ExtractedValueType.Int64;
			return resultInt64[0];
		}
		else
		{
			formattedValueType = ExtractedValueType.Int64Array;

			// Comment out, since it causes a huge performance hit when synchronizing files that contain this type of metadata item.
			// Perhaps find some way to raise the visibility of this by noting it once per application run.
			//String msg = String.format("Discovered an Exif metadata item named {0} that is of type System.UInt32[] rather than the usual System.UInt32. MDS System cannot process this metadata and will discard it. One may want to modify MDS System to handle this data type.", this.RawMetadataItemName);
			//ErrorHandler.CustomExceptions.BusinessException ex = new ErrorHandler.CustomExceptions.BusinessException(msg);
			//ErrorHandler.Error.Record(ex, int.MinValue, Factory.LoadGallerySettings(), AppSetting.Instance);
			return null;
		}*/
		return null;
	}

	private Object ExtractPropertyValueUnsignedShort(ExtractedValueType formattedValueType){
		/*Long[] resultInt64 = new Long[this.propItem.Len / NUM_BYTES_PER_16_BIT_INT];
		for (int i = 0; i < resultInt64.length(); i++)
		{
			try
			{
				resultInt64[i] = Convert.ToInt64(BitConverter.ToUInt16(this.propItem.Value, i * NUM_BYTES_PER_16_BIT_INT));
			}
			catch (ArgumentNullException)
			{
				formattedValueType = ExtractedValueType.Int64;
				return Convert.ToInt64(0);
			}
			catch (ArgumentOutOfRangeException)
			{
				formattedValueType = ExtractedValueType.Int64;
				return Convert.ToInt64(0);
			}
		}

		if (resultInt64.length() == 1)
		{
			formattedValueType = ExtractedValueType.Int64;
			return resultInt64[0];
		}
		else
		{
			formattedValueType = ExtractedValueType.Int64Array;

			// Comment out, since it causes a huge performance hit when synchronizing files that contain this type of metadata item.
			// Perhaps find some way to raise the visibility of this by noting it once per application run.
			//String msg = String.format("Discovered an Exif metadata item named {0} that is of type System.UInt16[] rather than the usual System.UInt16. MDS System cannot process this metadata and will discard it. One may want to modify MDS System to handle this data type.", this.RawMetadataItemName);
			//ErrorHandler.CustomExceptions.BusinessException ex = new ErrorHandler.CustomExceptions.BusinessException(msg);
			//ErrorHandler.Error.Record(ex, int.MinValue, Factory.LoadGallerySettings(), AppSetting.Instance);
			return null;
		}*/
		return null;
	}

	private String ExtractPropertyValueString(ExtractedValueType formattedValueType){
		formattedValueType = ExtractedValueType.String;

		/*if (this.propItem.Value() == null)
			return StringUtils.EMPTY;

		// Do not use ASCII decoding because it can't handle UTF8-encoded data, which is fairly common.
		// See http://stackoverflow.com/questions/19284205/safe-to-use-utf8-decoding-for-exif-property-marked-as-ascii
		return _utf8Encoding.GetString(this.propItem.Value);*/
		return StringUtils.EMPTY;
	}

	private String ExtractPropertyValueByte(ExtractedValueType formattedValueType){
		formattedValueType = ExtractedValueType.String;

		/*if (this.propItem.Value == null)
			return StringUtils.EMPTY;

		if (this.propItem.Value.length() == 1)
		{
			return this.propItem.Value[0].ToString(CultureInfo.InvariantCulture);
		}
		else
		{
			return _unicodeEncoding.GetString(this.propItem.Value);
		}*/
		return StringUtils.EMPTY;
	}

	//#endregion
}
