package com.mds.aiotplayer.core;

import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Specifies the name of the formatted metadata item associated with a content object. The data for each item may
/// originate from one of these sources: (1) An EXIF, IPTC, or XMP value as determined by
/// System.Windows.Content.Imaging.BitmapMetadata, (2) A GDI+ image property tag (The GDI+ property tags are defined
/// in the <see cref="RawMetadataItemName" /> enumeration.), (3) A file property (these are grouped at the end of
/// this enumeration). This enumeration differs from <see cref="RawMetadataItemName" /> in that these items are
/// formatted to a user-friendly value (e.g. "1/500 sec") and may include more than just GDI+ property tags (such
/// as the file properties).
/// </summary>
/// <remarks>
/// Note to developer: If you edit these items, be sure to update
/// <see cref="MetadataItemNameEnumHelper.IsValidFormattedMetadataItemName" /> to match your changes.
/// </remarks>
public enum MetadataItemName
{
	/// <summary>Specifies that no particular enumeration has been selected.</summary>
	NotSpecified(-2147483648),
	/// <summary>The bit rate of the audio portion of the content object.</summary>
	AudioBitRate(0),
	/// <summary>Data about the audio portion of the content object.</summary>
	AudioFormat(1),
	/// <summary>Specifies the name of the person who created the image. Derived from BitmapMetadata.Author.
	/// If that property is empty, it is derived from Artist.</summary>
	Author(2),
	/// <summary>The overall bitrate of the content object.</summary>
	BitRate(3),
	/// <summary>Specifies the model name or model number of the equipment used to record the image.
	/// Derived from BitmapMetadata.CameraModel.</summary>
	CameraModel(4),
	/// <summary>Comment tag. Derived from BitmapMetadata.Comment. If that property is empty, it is derived from
	/// ExifEPersonComment.</summary>
	Comment(5),
	/// <summary>Color space specifier. Derived from ExifColorSpace.</summary>
	ColorRepresentation(6),
	/// <summary>Specifies the copyright information. Derived from BitmapMetadata.Copyright.</summary>
	Copyright(7),
	/// <summary>Date and time when the original image data was generated. Derived from BitmapMetadata.DateTaken.</summary>
	DatePictureTaken(8),
	/// <summary>Specifies the title of the image. Derived from ImageDescription.</summary>
	Description(9),
	/// <summary>Specifies the width and height of the image in pixels. Derived from ImageWidth and
	/// ImageHeight for uncompressed images such as TIFF, and from ExifPixXDim and ExifPixYDim for compressed
	/// images such as JPEG.</summary>
	Dimensions(10),
	/// <summary>Specifies the duration of the content object. Applies only to content objects that span time, such as audio
	/// or video.</summary>
	Duration(11),
	/// <summary>Specifies the manufacturer of the equipment used to record the image.
	/// Derived from EquipMake.</summary>
	EquipmentManufacturer(12),
	/// <summary>Exposure bias. The unit is the APEX value. Ordinarily it is given in the range -99.99 
	/// to 99.99. Derived from ExifExposureBias.</summary>
	ExposureCompensation(13),
	/// <summary>Class of the program used by the camera to set exposure when the picture is taken.
	/// Derived from ExifExposureProg.</summary>
	ExposureProgram(14),
	/// <summary>Exposure time, measured in seconds. Derived from ExifExposureTime.</summary>
	ExposureTime(15),
	/// <summary>Flash status. This tag is recorded when an image is taken using a strobe light (flash).
	/// Derived from ExifFlash.</summary>
	FlashMode(16),
	/// <summary>F number. Derived from ExifFNumber.</summary>
	FNumber(17),
	/// <summary>Actual focal length, in millimeters, of the lens. Conversion is not made to the focal length
	///of a 35 millimeter film camera. Derived from ExifFocalLength.</summary>
	FocalLength(18),
	/// <summary>Number of pixels rows. For uncompressed images such as TIFF, this value is derived from 
	/// ImageHeight. For compressed images such as JPEG, this value is derived from ExifPixYDim.  For videos, this
	/// is parsed from the output of the FFmpeg utility.</summary>
	Height(19),
	/// <summary>Number of pixels per unit in the image width (x) direction. The value is derived from 
	/// XResolution and the unit of measure (inch, centimeter, etc.) is derived from ResolutionUnit.</summary>
	HorizontalResolution(20),
	/// <summary>ISO speed and ISO latitude of the camera or input device as specified in ISO 12232.
	/// Derived from ExifISOSpeed.</summary>
	IsoSpeed(21),
	/// <summary>A comma-separated list of tag names associated with the object. Derived from BitmapMetadata.Keywords.</summary>
	Tags(22),
	/// <summary>Lens aperture. The unit is the APEX value. The value is the same as the F-Number if present;
	/// otherwise it is calculated from ExifAperture using this formula: Math.Pow(Sqrt(2), ExifAperture).</summary>
	LensAperture(23),
	/// <summary>Type of light source. Derived from ExifLightSource.</summary>
	LightSource(24),
	/// <summary>Metering mode. Derived from ExifMeteringMode.</summary>
	MeteringMode(25),
	/// <summary>Rating. The value is an integer from 0-5. Derived from BitmapMetadata.Rating.</summary>
	Rating(26),
	/// <summary>Distance to the subject, measured in meters. Derived from ExifSubjectDist.</summary>
	SubjectDistance(27),
	/// <summary>The subject pertaining to the image. Derived from BitmapMetadata.Subject.</summary>
	Subject(28),
	/// <summary>Specifies the title of a content object. For images, it derives from BitmapMetadata.Title.</summary>
	Title(29),
	/// <summary>Number of pixels per unit in the image height (y) direction. The value is derived from 
	/// YResolution and the unit of measure (inch, centimeter, etc.) is derived from ResolutionUnit.</summary>
	VerticalResolution(30),
	/// <summary>The bit rate of the video portion of the content object.</summary>
	VideoBitRate(31),
	/// <summary>Data about the video portion of the content object.</summary>
	VideoFormat(32),
	/// <summary>Number of pixels per row. For uncompressed images such as TIFF, this value is derived from 
	/// ImageWidth. For compressed images such as JPEG, this value is derived from ExifPixXDim. For videos, this
	/// is parsed from the output of the FFmpeg utility.</summary>
	Width(33),
	/// <summary>
	/// The name of the file associated with the content object.
	/// </summary>
	FileName(34),
	/// <summary>
	/// The name of the file associated with the content object, excluding the file extension.
	/// </summary>
	FileNameWithoutExtension(35),
	/// <summary>
	/// The size, in KB, of the file associated with the content object.
	/// </summary>
	FileSizeKb(36),
	/// <summary>
	/// The file creation timestamp of the file associated with the content object, in local time.
	/// </summary>
	DateFileCreated(37),
	/// <summary>
	/// The file creation timestamp of the file associated with the content object, in UTC time.
	/// </summary>
	DateFileCreatedUtc(38),
	/// <summary>
	/// The date last modified timestamp of the file associated with the content object, in local time.
	/// </summary>
	DateFileLastModified(39),
	/// <summary>
	/// The date last modified timestamp of the file associated with the content object, in UTC time.
	/// </summary>
	DateFileLastModifiedUtc(40),
	/// <summary>
	/// The caption for the object.
	/// </summary>
	Caption(41),
	/// <summary>
	/// A comma-separated list of person names associated with the object.
	/// </summary>
	People(42),
	/// <summary>
	/// The orientation of an image or video.
	/// </summary>
	Orientation(43),
    /// <summary>
    /// A comma-separated list of players associated with the object.
    /// </summary>
    Player(44),
    /// <summary>
    /// The Approved Level for the object.
    /// </summary>
    ApprovedLevel(45),
    /// <summary>
    /// The Approve status for the object.
    /// </summary>
    ApproveStatus(46),
    /// <summary>
    /// The Approval for the object.
    /// </summary>
    Approval(47),
    /// <summary>
    /// The Date of approval for the object.
    /// </summary>
    ApprovalDate(48),
    /// <summary>
    /// The MD5 value for the object.
    /// </summary>
    MD5(49),
	/// <summary>
	/// The latitude and longitude of the location of the content object. Example: "27.1234 N 15.5678 W"
	/// </summary>
	GpsLocation(101),
	/// <summary>
	/// The latitude and longitude of the location of the content object, enclosed in a hyperlink to a mapping service.
	/// Example: "27.1234 N 15.5678 W"
	/// </summary>
	GpsLocationWithMapLink(102),
	/// <summary>
	/// The latitude of the location of the content object. Value will be negative for latitudes south of the equator.
	/// Example: "27.1234"
	/// </summary>
	GpsLatitude(103),
	/// <summary>
	/// The longitude of the location of the content object. Value will be negative for longitudes west of the Prime Meridian. 
	/// Example: "27.1234"
	/// </summary>
	GpsLongitude(104),
	/// <summary>
	/// The latitude and longitude of the destination location of the content object. Example: "27.1234 N 15.5678 W"
	/// </summary>
	GpsDestLocation(105),
	/// <summary>
	/// The latitude and longitude of the destination location of the content object, enclosed in a hyperlink to a mapping service.
	/// Example: "27.1234 N 15.5678 W"
	/// </summary>
	GpsDestLocationWithMapLink(106),
	/// <summary>
	/// The latitude of the destination location of the content object. Value will be negative for latitudes south of the equator. 
	/// Example: "27.1234"
	/// </summary>
	GpsDestLatitude(107),
	/// <summary>
	/// The longitude of the destination location of the content object. Value will be negative for longitudes west of the Prime Meridian. 
	/// Example: "27.1234"
	/// </summary>
	GpsDestLongitude(108),
	/// <summary>
	/// The altitude, in meters, of the content object.
	/// </summary>
	GpsAltitude(109),
	/// <summary>
	/// The version of the GPS information. Example: "2.2.0.0"
	/// </summary>
	GpsVersion(110),
	/// <summary>
	/// The timestamp for when the item was added to the gallery.
	/// </summary>
	DateAdded(111),
	/// <summary>
	/// The HTML fragment / embed code that defines the content of an external content object.
	/// Note that the application is hard-coded to add an item of this type ONLY when the media
	/// object has the type ExternalContentobject. 
	/// </summary>
	HtmlSource(112),
	/// <summary>
	/// The number of ratings that have been applied to an album or content object.
	/// </summary>
	RatingCount(113),
	/// <summary>
	/// The caption an album. Applies only to albums.
	/// </summary>
	//AlbumCaption(114),
	/// <summary>
	/// The IPTC by-line.
	/// </summary>
	IptcByline(1001),
	/// <summary>
	/// The IPTC by-line title.
	/// </summary>
	IptcBylineTitle(1002),
	/// <summary>
	/// The IPTC caption.
	/// </summary>
	IptcCaption(1003),
	/// <summary>
	/// The IPTC city.
	/// </summary>
	IptcCity(1004),
	/// <summary>
	/// The IPTC copyright notice.
	/// </summary>
	IptcCopyrightNotice(1005),
	/// <summary>
	/// The IPTC country name.
	/// </summary>
	IptcCountryPrimaryLocationName(1006),
	/// <summary>
	/// The IPTC credit.
	/// </summary>
	IptcCredit(1007),
	/// <summary>
	/// The IPTC date created value.
	/// </summary>
	IptcDateCreated(1008),
	/// <summary>
	/// The IPTC headline.
	/// </summary>
	IptcHeadline(1009),
	/// <summary>
	/// The IPTC keywords.
	/// </summary>
	IptcKeywords(1010),
	/// <summary>
	/// The IPTC object name.
	/// </summary>
	IptcObjectName(1011),
	/// <summary>
	/// The IPTC original transmission reference.
	/// </summary>
	IptcOriginalTransmissionReference(1012),
	/// <summary>
	/// The IPTC province/state.
	/// </summary>
	IptcProvinceState(1013),
	/// <summary>
	/// The IPTC record version.
	/// </summary>
	IptcRecordVersion(1014),
	/// <summary>
	/// The IPTC source.
	/// </summary>
	IptcSource(1015),
	/// <summary>
	/// The IPTC special instructions.
	/// </summary>
	IptcSpecialInstructions(1016),
	/// <summary>
	/// The IPTC sub-location.
	/// </summary>
	IptcSublocation(1017),
	/// <summary>
	/// The IPTC writer/editor.
	/// </summary>
	IptcWriterEditor(1018),
	Custom1(2000),
	Custom2(2001),
	Custom3(2002),
	Custom4(2003),
	Custom5(2004),
	Custom6(2005),
	Custom7(2006),
	Custom8(2007),
	Custom9(2008),
	Custom10(2009),
	Custom11(2010),
	Custom12(2011),
	Custom13(2012),
	Custom14(2013),
	Custom15(2014),
	Custom16(2015),
	Custom17(2016),
	Custom18(2017),
	Custom19(2018),
	Custom20(2019);
	
	private final int metadataItemName;
    

    private MetadataItemName(int metadataItemName) {
        this.metadataItemName = metadataItemName;
    }
    
    public int value() {
    	return metadataItemName;
    }
    
    public static MetadataItemName getMetadataItemName(int metadataItemName) {
		for(MetadataItemName value : MetadataItemName.values()) {
			if (value.value() == metadataItemName)
				return value;
		}
		
		return MetadataItemName.NotSpecified;
	}
    
    public static MetadataItemName getMetadataItemName(String metadataItemName) {
		for(MetadataItemName value : MetadataItemName.values()) {
			if (value.toString().equalsIgnoreCase(metadataItemName))
				return value;
		}
		
		return MetadataItemName.NotSpecified;
	}
    
    public static MetadataItemName parse(String metadataItemName) {
		int val = StringUtils.toInteger(metadataItemName);
		for(MetadataItemName value : MetadataItemName.values()) {
			if (value.value() == val)
				return value;
		}
		
		return MetadataItemName.valueOf(metadataItemName);
	}

	/// <summary>
	/// Determines if the <paramref name="item" /> parameter is one of the defined enumerations. This method is more efficient than using
	/// <see cref="Enum.IsDefined" />, since <see cref="Enum.IsDefined" /> uses reflection.
	/// </summary>
	/// <param name="item">An instance of <see cref="MetadataItemName" /> to test.</param>
	/// <returns>Returns true if <paramref name="item" /> is one of the defined items in the enumeration; otherwise returns false.</returns>
	public static boolean isValidFormattedMetadataItemName(MetadataItemName item)
	{
		switch (item)
		{
			// Most frequently specified items are at the top
			case Title:

			case AudioBitRate:
			case AudioFormat:
			case Author:
			case BitRate:
			case CameraModel:
			case Comment:
			case ColorRepresentation:
			case Copyright:
			case DatePictureTaken:
			case Description:
			case Dimensions:
			case Duration:
			case EquipmentManufacturer:
			case ExposureCompensation:
			case ExposureProgram:
			case ExposureTime:
			case FlashMode:
			case FNumber:
			case FocalLength:
			case Height:
			case HorizontalResolution:
			case IsoSpeed:
			case Tags:
			case LensAperture:
			case LightSource:
			case MeteringMode:
			case Rating:
			case SubjectDistance:
			case Subject:
			case VerticalResolution:
			case VideoBitRate:
			case VideoFormat:
			case Width:
			case FileName:
			case FileNameWithoutExtension:
			case FileSizeKb:
			case DateFileCreated:
			case DateFileCreatedUtc:
			case DateFileLastModified:
			case DateFileLastModifiedUtc:
			case Caption:
			case People:
			case Orientation:

			case GpsLocation:
			case GpsLocationWithMapLink:
			case GpsLatitude:
			case GpsLongitude:
			case GpsDestLocation:
			case GpsDestLocationWithMapLink:
			case GpsDestLatitude:
			case GpsDestLongitude:
			case GpsAltitude:
			case GpsVersion:

			case DateAdded:
			case HtmlSource:
			case RatingCount:
			//case AlbumTitle:
			//case AlbumCaption:

			case IptcByline:
			case IptcBylineTitle:
			case IptcCaption:
			case IptcCity:
			case IptcCopyrightNotice:
			case IptcCountryPrimaryLocationName:
			case IptcCredit:
			case IptcDateCreated:
			case IptcHeadline:
			case IptcKeywords:
			case IptcObjectName:
			case IptcOriginalTransmissionReference:
			case IptcProvinceState:
			case IptcRecordVersion:
			case IptcSource:
			case IptcSpecialInstructions:
			case IptcSublocation:
			case IptcWriterEditor:
			case Custom1:
			case Custom2:
			case Custom3:
			case Custom4:
			case Custom5:
			case Custom6:
			case Custom7:
			case Custom8:
			case Custom9:
			case Custom10:
			case Custom11:
			case Custom12:
			case Custom13:
			case Custom14:
			case Custom15:
			case Custom16:
			case Custom17:
			case Custom18:
			case Custom19:
			case Custom20:
				break;

			default:
				return false;
		}
		return true;
	}
}


