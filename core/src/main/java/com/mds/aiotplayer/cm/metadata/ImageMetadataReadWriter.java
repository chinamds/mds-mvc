package com.mds.aiotplayer.cm.metadata;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.png.PngDirectory;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.core.ExposureProgram;
import com.mds.aiotplayer.core.ExtractedValueType;
import com.mds.aiotplayer.core.MetadataEnumHelper;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.core.Orientation;
import com.mds.aiotplayer.core.RawMetadataItemName;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.core.exception.NotSupportedException;
import com.mds.aiotplayer.core.exception.ArgumentException;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.core.exception.InvalidEnumArgumentException;
import com.mds.aiotplayer.cm.content.nullobjects.NullWpfMetadata;

/// <summary>
/// Provides functionality for reading and writing metadata to or from a gallery object.
/// </summary>
public class ImageMetadataReadWriter extends MediaObjectMetadataReadWriter{
	//#region Fields
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private enum MetaPersistAction{
		Delete,
		Save
	}

	private final int MetadataPaddingInBytes = 2048;

	private String[] propertyItems;
	private WpfMetadata wpfMetadata;
	private int width, height;
	private Map<RawMetadataItemName, MetadataItem> rawMetadata;
	private GpsLocation gpsLocation;
	private static Map<MetadataItemName, String> iptcQueryParameters;
	private static Map<MetadataItemName, String> updatableMetaItems;
	private static final Object _sharedLock = new Object();

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets the property items associated with the image file. Guaranteed to not return null.
	/// </summary>
	/// <value>An array of <see cref="PropertyItem" /> instances.</value>
	//private Iterable<String> getPropertyItems()	{
	private String[] getPropertyItems()	{
		if (this.propertyItems == null) {
			this.propertyItems = getImagePropertyItems();
		}
		
		return this.propertyItems;
	}

	/// <summary>
	/// Gets the raw metadata associated with the current image file. Guaranteed to not return null.
	/// </summary>
	/// <value>The raw metadata associated with the current image file.</value>
	private Map<RawMetadataItemName, MetadataItem> getRawMetadata()	{
		if (rawMetadata == null)
			rawMetadata = getRawMetadataDictionary();
		
		return rawMetadata;
	}

	/// <summary>
	/// Gets an object that can extract metadata from a media file using the .NET WPF classes.
	/// Guaranteed to not return null.
	/// </summary>
	/// <value>An instance of <see cref="WpfMetadata" /> when possible; otherwise 
	/// <see cref="NullObjects.NullWpfMetadata" />.</value>
	private WpfMetadata getWpfMetadata(){
		if (this.wpfMetadata == null)
			this.wpfMetadata = getBitmapMetadata();
		
		return this.wpfMetadata;
	}

	/// <summary>
	/// Gets an object that can retrieve GPS-related data from a media file.
	/// </summary>
	/// <value>An instance of <see cref="GpsLocation" />.</value>
	private GpsLocation getGpsLocation(){
		if (this.gpsLocation == null)
			this.gpsLocation = GpsLocation.parse(getWpfMetadata());
		
		return this.gpsLocation;
	}

	/// <summary>
	/// Gets the query format String to be used for extracting IPTC data from a media file.
	/// Example: "/app13/irb/8bimiptc/iptc/{{str={0}}}"
	/// </summary>
	/// <value>A String.</value>
	private static String getIptcQueryFormatString(){
		return "/app13/irb/8bimiptc/iptc/{{str={0}}}";
	}
	
	/// <summary>
	/// Fill the class-level _rawMetadata dictionary with MetadataItem objects created from the
	/// PropertyItems property of the image. Skip any items that are not defined in the 
	/// RawMetadataItemName enumeration. Guaranteed to not return null.
	/// </summary>
	private Map<RawMetadataItemName, String> initRawMetadataDictionary(){
		/*Map<RawMetadataItemName, Pair<Class<?>, Integer>> rawMetadatas = new HashMap<RawMetadataItemName, Pair<Class<?>, Integer>>();
		
		///<summary>Null-terminated character string that specifies the name of the person who created the image. (0x013B, 315)</summary>
		rawMetadatas.put(RawMetadataItemName.Artist, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_ARTIST));
		///<summary>Number of bits per color component. See also SamplesPerPixel.</summary>
		///<!-- A list of integers, one per channel --> 
        ///<!-- Data type: List of Integer -->
        //<!-- Min length: 1 -->
		rawMetadatas.put(RawMetadataItemName.BitsPerSample, new ImmutablePair<Class<?>, Integer>(PngDirectory.class, PngDirectory.TAG_BITS_PER_SAMPLE));
		///<summary>Height of the dithering or halftoning matrix.</summary>
		rawMetadatas.put(RawMetadataItemName.CellHeight, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_INTEROP_OFFSET));
		///<summary>Width of the dithering or halftoning matrix.</summary>
		rawMetadatas.put(RawMetadataItemName.CellWidth, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Chrominance table. The luminance table and the chrominance table are used to control JPEG quality. 
		///A valid luminance or chrominance table has 64 entries of type PropertyTagTypeShort. If an image has 
		///either a luminance table or a chrominance table, then it must have both tables.</summary>
		rawMetadatas.put(RawMetadataItemName.ChrominanceTable, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Color palette (lookup table) for a palette-indexed image.</summary>
		rawMetadatas.put(RawMetadataItemName.ColorMap, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Table of values that specify color transfer functions.</summary>
		rawMetadatas.put(RawMetadataItemName.ColorTransferFunction, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Compression scheme used for the image data.</summary>
		///<!-- Data type: String -->
		rawMetadatas.put(RawMetadataItemName.Compression, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that contains copyright information.</summary>
		rawMetadatas.put(RawMetadataItemName.Copyright, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Date and time the image was created.</summary>
		rawMetadatas.put(RawMetadataItemName.DateTime, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the name of the document from which the image 
		///was scanned.</summary>
		rawMetadatas.put(RawMetadataItemName.DocumentName, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Color component values that correspond to a 0 percent dot and a 100 percent dot.</summary>
		rawMetadatas.put(RawMetadataItemName.DotRange, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the manufacturer of the equipment used to 
		///record the image.</summary>
		rawMetadatas.put(RawMetadataItemName.EquipMake, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the model name or model number of the 
		///equipment used to record the image.</summary>
		rawMetadatas.put(RawMetadataItemName.EquipModel, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Lens aperture. The unit is the APEX value.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifAperture, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Brightness value. The unit is the APEX value. Ordinarily it is given in the range of 
		///-99.99 to 99.99.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifBrightness, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>The color filter array (CFA) geometric pattern of the image sensor when a one-chip color area sensor 
		///is used. It does not apply to all sensing methods.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifCfaPattern, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Color space specifier. Normally sRGB (=1) is used to define the color space based on the PC monitor 
		///conditions and environment. If a color space other than sRGB is used, Uncalibrated (=65535) is set. Image 
		///data recorded as Uncalibrated can be treated as sRGB when it is converted to FlashPix.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifColorSpace, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Information specific to compressed data. The compression mode used for a compressed image is
		///indicated in unit BPP.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifCompBPP, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Information specific to compressed data. The channels of each component are arranged in order from 
		///the first component to the fourth. For uncompressed data, the data arrangement is given in the 
		///PropertyTagPhotometricInterp tag. However, because PropertyTagPhotometricInterp can only express the 
		///order of Y, Cb, and Cr, this tag is provided for cases when compressed data uses components other than Y, 
		///Cb, and Cr and to support other sequences.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifCompConfig, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Date and time when the image was stored as digital data. If, for example, an image was captured 
		///by DSC and at the same time the file was recorded, then DateTimeOriginal and DateTimeDigitized will have 
		///the same contents. The format is YYYY:MM:DD HH:MM:SS with time shown in 24-hour format and the date and 
		///time separated by one blank character (0x2000). The character string length is 20 bytes including the 
		///NULL terminator. When the field is empty, it is treated as unknown.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifDTDigitized, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies a fraction of a second for the 
		///PropertyTagExifDTDigitized tag.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifDTDigSS, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Date and time when the original image data was generated. For a DSC, the date and time when the 
		///picture was taken. The format is YYYY:MM:DD HH:MM:SS with time shown in 24-hour format and the date and
		///time separated by one blank character (0x2000). The character string length is 20 bytes including the 
		///NULL terminator. When the field is empty, it is treated as unknown.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifDTOrig, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies a fraction of a second for the 
		///PropertyTagExifDTOrig tag.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifDTOrigSS, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies a fraction of a second for the PropertyTagDateTime tag.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifDTSubsec, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Exposure bias. The unit is the APEX value. Ordinarily it is given in the range -99.99 to 99.99.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifExposureBias, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Exposure index selected on the camera or input device at the time the image was captured.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifExposureIndex, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Class of the program used by the camera to set exposure when the picture is taken. The value
		///is an integer with these values: 0 - not defined; 1 - manual; 2 - normal program; 3 - aperture priority;
		///4 - shutter priority; 5 - creative program (biased toward depth of field); 6 - action program (biased
		///toward fast shutter speed); 7 - portrait mode (for close-up photos with the background out of focus);
		///8 - landscape mode (for landscape photos with the background in focus); 9 to 255 - reserved</summary>
		rawMetadatas.put(RawMetadataItemName.ExifExposureProg, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Exposure time, measured in seconds.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifExposureTime, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>The image source. If a DSC recorded the image, the value of this tag is 3.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifFileSource, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Flash status. This tag is recorded when an image is taken using a strobe light (flash). 
		///Bit 0 indicates the flash firing status (0b - flash did not fire 1b - flash fired, ""); and bits 1 and 2 
		///indicate the flash return status (00b - no strobe return detection function 01b - reserved 10b - strobe 
		///return light not detected 11b - strobe return light detected). Resulting flash tag values: 0x0000 - flash 
		///did not fire; 0x0001 - flash fired; 0x0005 - strobe return light not detected</summary>
		rawMetadatas.put(RawMetadataItemName.ExifFlash, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Strobe energy, in Beam Candle Power Seconds (BCPS), at the time the image was captured.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifFlashEnergy, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>F number.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifFNumber, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Actual focal length, in millimeters, of the lens. Conversion is not made to the focal length
		///of a 35 millimeter film camera.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifFocalLength, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Unit of measure for PropertyTagExifFocalXRes and PropertyTagExifFocalYRes.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifFocalResUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixels in the image width (x) direction per unit on the camera focal plane. The unit is 
		///specified in PropertyTagExifFocalResUnit.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifFocalXRes, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixels in the image height (y) direction per unit on the camera focal plane. The unit is
		///specified in PropertyTagExifFocalResUnit.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifFocalYRes, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>FlashPix format version supported by an FPXR file. If the FPXR function supports FlashPix format 
		///version 1.0, this is indicated similarly to PropertyTagExifVer by recording 0100 as a 4-byte ASCII string. 
		///Because the type is PropertyTagTypeUndefined, there is no NULL terminator.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifFPXVer, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Private tag used by GDI+. Not for public use. GDI+ uses this tag to locate Exif-specific 
		///information.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifIFD, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Offset to a block of property items that contain interoperability information.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifInterop, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>ISO speed and ISO latitude of the camera or input device as specified in ISO 12232.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifISOSpeed, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Type of light source. This is an integer with these values: 0 - unknown; 1 - Daylight;
		///2 - Flourescent; 3 - Tungsten; 17 - Standard Light A; 18 - Standard Light B; 19 - Standard Light C;
		///20 - D55; 21 - D65; 22 - D75; 23 to 254 - reserved; 255 - other</summary>
		rawMetadatas.put(RawMetadataItemName.ExifLightSource, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Note tag. A tag used by manufacturers of EXIF writers to record information. The contents are 
		///up to the manufacturer.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifMakerNote, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Smallest F number of the lens. The unit is the APEX value. Ordinarily it is given in the range 
		///of 00.00 to 99.99, but it is not limited to this range.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifMaxAperture, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Metering mode. This is an integer with these values: 0 - unknown; 1 - Average; 2 - 
		///CenterWeightedAverage; 3 - Spot; 4 - MultiSpot; 5 - Pattern; 6 - Partial; 7 to 254 - reserved;
		///255 - other</summary>
		rawMetadatas.put(RawMetadataItemName.ExifMeteringMode, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Optoelectronic conversion function (OECF) specified in ISO 14524. The OECF is the relationship 
		///between the camera optical input and the image values.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifOECF, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Information specific to compressed data. When a compressed file is recorded, the valid width of the 
		///meaningful image must be recorded in this tag, whether or not there is padding data or a restart marker. 
		///This tag should not exist in an uncompressed file.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifPixXDim, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Information specific to compressed data. When a compressed file is recorded, the valid height of the 
		///meaningful image must be recorded in this tag whether or not there is padding data or a restart marker. 
		///This tag should not exist in an uncompressed file. Because data padding is unnecessary in the vertical 
		///direction, the number of lines recorded in this valid image height tag will be the same as that recorded 
		///in the SOF.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifPixYDim, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>The name of an audio file related to the image data. The only relational information recorded is 
		///the EXIF audio file name and extension (an ASCII string that consists of 8 characters plus a period (.), 
		///plus 3 characters). The path is not recorded. When you use this tag, audio files must be recorded in 
		///conformance with the EXIF audio format. Writers can also store audio data within APP2 as FlashPix extension 
		///stream data.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifRelatedWav, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>The type of scene. If a DSC recorded the image, the value of this tag must be set to 1, indicating 
		///that the image was directly photographed.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifSceneType, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Image sensor type on the camera or input device. This is an integer with these values:
		///1 - not defined; 2 - one-chip color area sensor; 3 - two-chip color area sensor; 4 - three-chip color area 
		///sensor; 5 - color sequential area sensor; 7 - trilinear sensor; 8 - color sequential linear sensor;
		///Other - reserved</summary>
		rawMetadatas.put(RawMetadataItemName.ExifSensingMethod, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Shutter speed. The unit is the Additive System of Photographic Exposure (APEX) value.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifShutterSpeed, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Camera or input device spatial frequency table and SFR values in the image width, image height, and 
		///diagonal direction, as specified in ISO 12233.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifSpatialFR, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the spectral sensitivity of each channel of the 
		///camera used. The string is compatible with the standard developed by the ASTM Technical Committee.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifSpectralSense, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Distance to the subject, measured in meters.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifSubjectDist, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Location of the main subject in the scene. The value of this tag represents the pixel at the center 
		///of the main subject relative to the left edge. The first value indicates the column number, and the second 
		///value indicates the row number.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifSubjectLoc, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Comment tag. A tag used by EXIF users to write keywords or comments about the image besides those 
		///in PropertyTagImageDescription and without the character-code limitations of the 
		///PropertyTagImageDescription tag.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifUserComment, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Version of the EXIF standard supported. Nonexistence of this field is taken to mean nonconformance 
		///to the standard. Conformance to the standard is indicated by recording 0210 as a 4-byte ASCII string. 
		///Because the type is PropertyTagTypeUndefined, there is no NULL terminator.</summary>
		rawMetadatas.put(RawMetadataItemName.ExifVer, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of extra color components. For example, one extra component might hold an alpha value.</summary>
		rawMetadatas.put(RawMetadataItemName.ExtraSamples, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Logical order of bits in a byte.</summary>
		rawMetadatas.put(RawMetadataItemName.FillOrder, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Time delay, in hundredths of a second, between two frames in an animated GIF image.</summary>
		rawMetadatas.put(RawMetadataItemName.FrameDelay, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each string of contiguous unused bytes, the number of bytes in that string.</summary>
		rawMetadatas.put(RawMetadataItemName.FreeByteCounts, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each string of contiguous unused bytes, the byte offset of that string.</summary>
		rawMetadatas.put(RawMetadataItemName.FreeOffset, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Gamma value attached to the image. The gamma value is stored as a rational number (pair of long) 
		///with a numerator of 100000. For example, a gamma value of 2.2 is stored as the pair (100000, 45455).</summary>
		rawMetadatas.put(RawMetadataItemName.Gamma, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Color palette for an indexed bitmap in a GIF image.</summary>
		rawMetadatas.put(RawMetadataItemName.GlobalPalette, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Altitude, in meters, based on the reference altitude specified by PropertyTagGpsAltitudeRef.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsAltitude, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Reference altitude, in meters.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsAltitudeRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Bearing to the destination point. The range of values is from 0.00 to 359.99.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsDestBear, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the reference used for giving the bearing to the 
		///destination point. T specifies true direction, and M specifies magnetic direction.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsDestBearRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Distance to the destination point.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsDestDist, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the unit used to express the distance to the 
		///destination point. K, M, and N represent kilometers, miles, and knots respectively.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsDestDistRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Latitude of the destination point. The latitude is expressed as three rational values giving the 
		///degrees, minutes, and seconds respectively. When degrees, minutes, and seconds are expressed, the format 
		///is dd/1, mm/1, ss/1. When degrees and minutes are used and, for example, fractions of minutes are given 
		///up to two decimal places, the format is dd/1, mmmm/100, 0/1.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsDestLat, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies whether the latitude of the destination point 
		///is north or south latitude. N specifies north latitude, and S specifies south latitude.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsDestLatRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Longitude of the destination point. The longitude is expressed as three rational values giving 
		///the degrees, minutes, and seconds respectively. When degrees, minutes, and seconds are expressed, the 
		///format is ddd/1, mm/1, ss/1. When degrees and minutes are used and, for example, fractions of minutes 
		///are given up to two decimal places, the format is ddd/1, mmmm/100, 0/1.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsDestLong, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies whether the longitude of the destination point is 
		///east or west longitude. E specifies east longitude, and W specifies west longitude.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsDestLongRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>GPS DOP (data degree of precision). An HDOP value is written during 2-D measurement, and a 
		///PDOP value is written during 3-D measurement.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsGpsDop, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the GPS measurement mode. 2 specifies 2-D 
		///measurement, and 3 specifies 3-D measurement.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsGpsMeasureMode, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the GPS satellites used for measurements. This tag 
		///can be used to specify the ID number, angle of elevation, azimuth, SNR, and other information about each 
		///satellite. The format is not specified. If the GPS receiver is incapable of taking measurements, the value 
		///of the tag must be set to NULL.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsGpsSatellites, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the status of the GPS receiver when the image is 
		///recorded. A means measurement is in progress, and V means the measurement is Interoperability.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsGpsStatus, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Time as coordinated universal time (UTC). The value is expressed as three rational numbers that 
		///give the hour, minute, and second.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsGpsTime, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Offset to a block of GPS property items. Property items whose tags have the prefix PropertyTagGps 
		///are stored in the GPS block. The GPS property items are defined in the EXIF specification. GDI+ uses this 
		///tag to locate GPS information, but GDI+ does not expose this tag for public use.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsIFD, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Direction of the image when it was captured. The range of values is from 0.00 to 359.99.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsImgDir, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the reference for the direction of the image when 
		///it is captured. T specifies true direction, and M specifies magnetic direction.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsImgDirRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Latitude. Latitude is expressed as three rational values giving the degrees, minutes, and seconds 
		///respectively. When degrees, minutes, and seconds are expressed, the format is dd/1, mm/1, ss/1. When 
		///degrees and minutes are used and, for example, fractions of minutes are given up to two decimal places, 
		///the format is dd/1, mmmm/100, 0/1.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsLatitude, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies whether the latitude is north or south. 
		///N specifies north latitude, and S specifies south latitude.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsLatitudeRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Longitude. Longitude is expressed as three rational values giving the degrees, minutes, and seconds
		///respectively. When degrees, minutes and seconds are expressed, the format is ddd/1, mm/1, ss/1. When 
		///degrees and minutes are used and, for example, fractions of minutes are given up to two decimal places, 
		///the format is ddd/1, mmmm/100, 0/1.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsLongitude, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies whether the longitude is east or west longitude. 
		///E specifies east longitude, and W specifies west longitude.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsLongitudeRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies geodetic survey data used by the GPS receiver. 
		///If the survey data is restricted to Japan, the value of this tag is TOKYO or WGS-84.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsMapDatum, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Speed of the GPS receiver movement.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsSpeed, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the unit used to express the GPS receiver speed 
		///of movement. K, M, and N represent kilometers per hour, miles per hour, and knots respectively.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsSpeedRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Direction of GPS receiver movement. The range of values is from 0.00 to 359.99.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsTrack, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the reference for giving the direction of GPS 
		/// receiver movement. T specifies true direction, and M specifies magnetic direction.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsTrackRef, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Version of the Global Positioning Systems (GPS) IFD, given as 2.0.0.0. This tag is mandatory 
		///when the PropertyTagGpsIFD tag is present. When the version is 2.0.0.0, the tag value is 0x02000000.</summary>
		rawMetadatas.put(RawMetadataItemName.GpsVer, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each possible pixel value in a grayscale image, the optical density of that pixel value.</summary>
		rawMetadatas.put(RawMetadataItemName.GrayResponseCurve, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Precision of the number specified by PropertyTagGrayResponseCurve. 1 specifies tenths, 
		///2 specifies hundredths, 3 specifies thousandths, and so on.</summary>
		rawMetadatas.put(RawMetadataItemName.GrayResponseUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Block of information about grids and guides.</summary>
		rawMetadatas.put(RawMetadataItemName.GridSize, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Angle for screen.</summary>
		rawMetadatas.put(RawMetadataItemName.HalftoneDegree, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Information used by the halftone function</summary>
		rawMetadatas.put(RawMetadataItemName.HalftoneHints, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Ink's screen frequency, in lines per inch.</summary>
		rawMetadatas.put(RawMetadataItemName.HalftoneLPI, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Units for the screen frequency.</summary>
		rawMetadatas.put(RawMetadataItemName.HalftoneLPIUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Miscellaneous halftone information.</summary>
		rawMetadatas.put(RawMetadataItemName.HalftoneMisc, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Boolean value that specifies whether to use the printer's default screens.</summary>
		rawMetadatas.put(RawMetadataItemName.HalftoneScreen, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Shape of the halftone dots.</summary>
		rawMetadatas.put(RawMetadataItemName.HalftoneShape, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the computer and/or operating system 
		///used to create the image.</summary>
		rawMetadatas.put(RawMetadataItemName.HostComputer, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>ICC profile embedded in the image.</summary>
		rawMetadatas.put(RawMetadataItemName.ICCProfile, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that identifies an ICC profile. </summary>
		rawMetadatas.put(RawMetadataItemName.ICCProfileDescriptor, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the title of the image.</summary>
		rawMetadatas.put(RawMetadataItemName.ImageDescription, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixel rows.</summary>
		rawMetadatas.put(RawMetadataItemName.ImageHeight, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the title of the image.</summary>
		rawMetadatas.put(RawMetadataItemName.ImageTitle, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixels per row.</summary>
		rawMetadatas.put(RawMetadataItemName.ImageWidth, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Index of the background color in the palette of a GIF image.</summary>
		rawMetadatas.put(RawMetadataItemName.IndexBackground, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Index of the transparent color in the palette of a GIF image.</summary>
		rawMetadatas.put(RawMetadataItemName.IndexTransparent, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Sequence of concatenated, null-terminated, character strings that specify the names of the 
		///inks used in a separated image.</summary>
		rawMetadatas.put(RawMetadataItemName.InkNames, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Set of inks used in a separated image.</summary>
		rawMetadatas.put(RawMetadataItemName.InkSet, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, the offset to the AC Huffman table for that component. See also 
		///PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGACTables, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, the offset to the DC Huffman table (or lossless Huffman table) for 
		///that component. See also PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGDCTables, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Offset to the start of a JPEG bitstream.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGInterFormat, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Length, in bytes, of the JPEG bitstream.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGInterLength, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, a lossless predictor-selection value for that component. 
		///See also PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGLosslessPredictors, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, a point transformation value for that component. See also 
		///PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGPointTransforms, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>JPEG compression process.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGProc, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, the offset to the quantization table for that component. 
		///See also PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGQTables, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Private tag used by the Adobe Photoshop format. Not for public use.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGQuality, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Length of the restart interval.</summary>
		rawMetadatas.put(RawMetadataItemName.JPEGRestartInterval, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For an animated GIF image, the number of times to display the animation. A value of 0 specifies 
		///that the animation should be displayed infinitely.</summary>
		rawMetadatas.put(RawMetadataItemName.LoopCount, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Luminance table. The luminance table and the chrominance table are used to control JPEG quality. 
		///A valid luminance or chrominance table has 64 entries of type PropertyTagTypeShort. If an image has 
		///either a luminance table or a chrominance table, then it must have both tables.</summary>
		rawMetadatas.put(RawMetadataItemName.LuminanceTable, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, the maximum value assigned to that component. See also 
		///PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.MaxSampleValue, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, the minimum value assigned to that component. See also 
		///PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.MinSampleValue, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Type of data in a subfile.</summary>
		rawMetadatas.put(RawMetadataItemName.NewSubfileType, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of inks.</summary>
		rawMetadatas.put(RawMetadataItemName.NumberOfInks, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Image orientation viewed in terms of rows and columns. The value is a System.UShort, with these
		///values: 1 - The 0th row is at the top of the visual image, and the 0th column is the visual left side. 
		///2 - The 0th row is at the visual top of the image, and the 0th column is the visual right side. 
		///3 - The 0th row is at the visual bottom of the image, and the 0th column is the visual right side. 
		///4 - The 0th row is at the visual bottom of the image, and the 0th column is the visual right side. 
		///5 - The 0th row is the visual left side of the image, and the 0th column is the visual top. 
		///6 - The 0th row is the visual right side of the image, and the 0th column is the visual top. 
		///7 - The 0th row is the visual right side of the image, and the 0th column is the visual bottom. 
		///8 - The 0th row is the visual left side of the image, and the 0th column is the visual bottom. </summary>
		rawMetadatas.put(RawMetadataItemName.Orientation, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the name of the page from which the image was scanned.</summary>
		rawMetadatas.put(RawMetadataItemName.PageName, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Page number of the page from which the image was scanned.</summary>
		rawMetadatas.put(RawMetadataItemName.PageNumber, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Palette histogram.</summary>
		rawMetadatas.put(RawMetadataItemName.PaletteHistogram, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>How pixel data will be interpreted.</summary>
		rawMetadatas.put(RawMetadataItemName.PhotometricInterp, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Pixels per unit in the x direction.</summary>
		rawMetadatas.put(RawMetadataItemName.PixelPerUnitX, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Pixels per unit in the y direction.</summary>
		rawMetadatas.put(RawMetadataItemName.PixelPerUnitY, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Unit for PropertyTagPixelPerUnitX and PropertyTagPixelPerUnitY.</summary>
		rawMetadatas.put(RawMetadataItemName.PixelUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Whether pixel components are recorded in chunky or planar format.</summary>
		rawMetadatas.put(RawMetadataItemName.PlanarConfig, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Type of prediction scheme that was applied to the image data before the encoding scheme was applied.</summary>
		rawMetadatas.put(RawMetadataItemName.Predictor, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each of the three primary colors in the image, the chromaticity of that color.</summary>
		rawMetadatas.put(RawMetadataItemName.PrimaryChromaticities, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Sequence of one-byte Boolean values that specify printing options.</summary>
		rawMetadatas.put(RawMetadataItemName.PrintFlags, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Print flags bleed width.</summary>
		rawMetadatas.put(RawMetadataItemName.PrintFlagsBleedWidth, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Print flags bleed width scale.</summary>
		rawMetadatas.put(RawMetadataItemName.PrintFlagsBleedWidthScale, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Print flags center crop marks.</summary>
		rawMetadatas.put(RawMetadataItemName.PrintFlagsCrop, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Print flags version.</summary>
		rawMetadatas.put(RawMetadataItemName.PrintFlagsVersion, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Reference black point value and reference white point value.</summary>
		rawMetadatas.put(RawMetadataItemName.REFBlackWhite, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Unit of measure for the horizontal resolution and the vertical resolution. 2 = inch, 3 = centimeter</summary>
		rawMetadatas.put(RawMetadataItemName.ResolutionUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Units in which to display the image width.</summary>
		rawMetadatas.put(RawMetadataItemName.ResolutionXLengthUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Units in which to display horizontal resolution.</summary>
		rawMetadatas.put(RawMetadataItemName.ResolutionXUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Units in which to display the image height.</summary>
		rawMetadatas.put(RawMetadataItemName.ResolutionYLengthUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Units in which to display vertical resolution.</summary>
		rawMetadatas.put(RawMetadataItemName.ResolutionYUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of rows per strip. See also PropertyTagStripBytesCount and PropertyTagStripOffsets.</summary>
		rawMetadatas.put(RawMetadataItemName.RowsPerStrip, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, the numerical format (unsigned, signed, floating point) of that
		///component. See also PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.SampleFormat, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of color components per pixel.</summary>
		rawMetadatas.put(RawMetadataItemName.SamplesPerPixel, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, the maximum value of that component. See also PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.SMaxSampleValue, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each color component, the minimum value of that component. See also PropertyTagSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.SMinSampleValue, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the name and version of the software 
		///or firmware of the device used to generate the image.</summary>
		rawMetadatas.put(RawMetadataItemName.SoftwareUsed, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>How the image should be displayed as defined by the International Color Consortium (ICC). If a 
		///GDI+ Image object is constructed with the useEmbeddedColorManagement parameter set to TRUE, then GDI+ 
		///renders the image according to the specified rendering intent. The intent can be set to perceptual, 
		///relative colorimetric, saturation, or absolute colorimetric. Perceptual intent (0), which is suitable for 
		///photographs, gives good adaptation to the display device gamut at the expense of colorimetric accuracy. 
		///Relative colorimetric intent (1) is suitable for images (for example, logos) that require color appearance 
		///matching that is relative to the display device white point. Saturation intent (2), which is suitable for 
		///charts and graphs, preserves saturation at the expense of hue and lightness. Absolute colorimetric intent (3)
		///is suitable for proofs (previews of images destined for a different display device) that require 
		///preservation of absolute colorimetry.</summary>
		rawMetadatas.put(RawMetadataItemName.SRGBRenderingIntent, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each strip, the total number of bytes in that strip.</summary>
		rawMetadatas.put(RawMetadataItemName.StripBytesCount, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each strip, the byte offset of that strip. See also PropertyTagRowsPerStrip
		///and PropertyTagStripBytesCount.</summary>
		rawMetadatas.put(RawMetadataItemName.StripOffsets, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Type of data in a subfile.</summary>
		rawMetadatas.put(RawMetadataItemName.SubfileType, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Set of flags that relate to T4 encoding.</summary>
		rawMetadatas.put(RawMetadataItemName.T4Option, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Set of flags that relate to T6 encoding.</summary>
		rawMetadatas.put(RawMetadataItemName.T6Option, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that describes the intended printing environment.</summary>
		rawMetadatas.put(RawMetadataItemName.TargetPrinter, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Technique used to convert from gray pixels to black and white pixels.</summary>
		rawMetadatas.put(RawMetadataItemName.ThreshHolding, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the name of the person who created the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailArtist, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of bits per color component in the thumbnail image. See also 
		///PropertyTagThumbnailSamplesPerPixel.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailBitsPerSample, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Bits per pixel (BPP) for the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailColorDepth, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Compressed size, in bytes, of the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailCompressedSize, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Compression scheme used for thumbnail image data.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailCompression, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that contains copyright information for the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailCopyRight, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Raw thumbnail bits in JPEG or RGB format. Depends on PropertyTagThumbnailFormat.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailData, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Date and time the thumbnail image was created. See also PropertyTagDateTime.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailDateTime, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the manufacturer of the equipment used to 
		///record the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailEquipMake, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the model name or model number of the
		///equipment used to record the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailEquipModel, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Format of the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailFormat, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Height, in pixels, of the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailHeight, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the title of the image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailImageDescription, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixel rows in the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailImageHeight, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixels per row in the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailImageWidth, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Thumbnail image orientation in terms of rows and columns. See also PropertyTagOrientation.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailOrientation, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>How thumbnail pixel data will be interpreted.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailPhotometricInterp, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Whether pixel components in the thumbnail image are recorded in chunky or planar format. 
		///See also PropertyTagPlanarConfig.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailPlanarConfig, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of color planes for the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailPlanes, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each of the three primary colors in the thumbnail image, the chromaticity of that color. 
		///See also PropertyTagPrimaryChromaticities.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailPrimaryChromaticities, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Byte offset between rows of pixel data.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailRawBytes, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Reference black point value and reference white point value for the thumbnail image. See also 
		///PropertyTagREFBlackWhite.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailRefBlackWhite, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Unit of measure for the horizontal resolution and the vertical resolution of the thumbnail 
		///image. See also PropertyTagResolutionUnit.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailResolutionUnit, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Thumbnail resolution in the width direction. The resolution unit is
		///given in PropertyTagThumbnailResolutionUnit</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailResolutionX, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Thumbnail resolution in the height direction. The resolution unit is 
		///given in PropertyTagThumbnailResolutionUnit</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailResolutionY, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of rows per strip in the thumbnail image. See also 
		///PropertyTagThumbnailStripBytesCount and PropertyTagThumbnailStripOffsets.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailRowsPerStrip, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of color components per pixel in the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailSamplesPerPixel, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Total size, in bytes, of the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailSize, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Null-terminated character string that specifies the name and version of the 
		///software or firmware of the device used to generate the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailSoftwareUsed, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each thumbnail image strip, the total number of bytes in that strip.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailStripBytesCount, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each strip in the thumbnail image, the byte offset of that strip. See also 
		///PropertyTagThumbnailRowsPerStrip and PropertyTagThumbnailStripBytesCount.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailStripOffsets, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Tables that specify transfer functions for the thumbnail image. See also 
		///PropertyTagTransferFunction.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailTransferFunction, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Chromaticity of the white point of the thumbnail image. See also PropertyTagWhitePoint.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailWhitePoint, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Width, in pixels, of the thumbnail image.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailWidth, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Coefficients for transformation from RGB to YCbCr data for the thumbnail image. See also 
		///PropertyTagYCbCrCoefficients.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailYCbCrCoefficients, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Position of chrominance components in relation to the luminance component for the thumbnail image. 
		///See also PropertyTagYCbCrPositioning.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailYCbCrPositioning, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Sampling ratio of chrominance components in relation to the luminance component for the 
		///thumbnail image. See also PropertyTagYCbCrSubsampling.</summary>
		rawMetadatas.put(RawMetadataItemName.ThumbnailYCbCrSubsampling, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each tile, the number of bytes in that tile.</summary>
		rawMetadatas.put(RawMetadataItemName.TileByteCounts, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixel rows in each tile.</summary>
		rawMetadatas.put(RawMetadataItemName.TileLength, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>For each tile, the byte offset of that tile.</summary>
		rawMetadatas.put(RawMetadataItemName.TileOffset, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixel columns in each tile.</summary>
		rawMetadatas.put(RawMetadataItemName.TileWidth, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Tables that specify transfer functions for the image.</summary>
		rawMetadatas.put(RawMetadataItemName.TransferFunction, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Table of values that extends the range of the transfer function.</summary>
		rawMetadatas.put(RawMetadataItemName.TransferRange, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Chromaticity of the white point of the image.</summary>
		rawMetadatas.put(RawMetadataItemName.WhitePoint, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Offset from the left side of the page to the left side of the image. The unit of measure
		///is specified by PropertyTagResolutionUnit.</summary>
		rawMetadatas.put(RawMetadataItemName.XPosition, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixels per unit in the image width (x) direction. The unit is specified by 
		///PropertyTagResolutionUnit.</summary>
		rawMetadatas.put(RawMetadataItemName.XResolution, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Coefficients for transformation from RGB to YCbCr image data. </summary>
		rawMetadatas.put(RawMetadataItemName.YCbCrCoefficients, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Position of chrominance components in relation to the luminance component.</summary>
		rawMetadatas.put(RawMetadataItemName.YCbCrPositioning, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Sampling ratio of chrominance components in relation to the luminance component.</summary>
		rawMetadatas.put(RawMetadataItemName.YCbCrSubsampling, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Offset from the top of the page to the top of the image. The unit of measure is 
		///specified by PropertyTagResolutionUnit.</summary>
		rawMetadatas.put(RawMetadataItemName.YPosition, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));
		///<summary>Number of pixels per unit in the image height (y) direction. The unit is specified by 
		///PropertyTagResolutionUnit.</summary>
		rawMetadatas.put(RawMetadataItemName.YResolution, new ImmutablePair<Class<?>, Integer>(ExifSubIFDDirectory.class, 0));*/
		
		Map<RawMetadataItemName, String> rawMetadata = new HashMap<RawMetadataItemName, String>();
	
		/*for(RawMetadataItemName itemIterator : RawMetadataItemName.values()){
			rawMetadata.add(itemIterator, metadataItem);
		}*/
		
		///<summary>Null-terminated character string that specifies the name of the person who created the image. (0x013B, 315)</summary>
		rawMetadata.put(RawMetadataItemName.Artist, "Artist");
		///<summary>Number of bits per color component. See also SamplesPerPixel.</summary>
		///<!-- A list of integers, one per channel --> 
        ///<!-- Data type: List of Integer -->
        //<!-- Min length: 1 -->
		rawMetadata.put(RawMetadataItemName.BitsPerSample, "Bits Per Sample");
		///<summary>Height of the dithering or halftoning matrix.</summary>
		rawMetadata.put(RawMetadataItemName.CellHeight, "Cell Height");
		///<summary>Width of the dithering or halftoning matrix.</summary>
		rawMetadata.put(RawMetadataItemName.CellWidth, "Cell Width");
		///<summary>Chrominance table. The luminance table and the chrominance table are used to control JPEG quality. 
		///A valid luminance or chrominance table has 64 entries of type PropertyTagTypeShort. If an image has 
		///either a luminance table or a chrominance table, then it must have both tables.</summary>
		rawMetadata.put(RawMetadataItemName.ChrominanceTable, "");
		///<summary>Color palette (lookup table) for a palette-indexed image.</summary>
		rawMetadata.put(RawMetadataItemName.ColorMap, "Palette");
		///<summary>Table of values that specify color transfer functions.</summary>
		rawMetadata.put(RawMetadataItemName.ColorTransferFunction, "");
		///<summary>Compression scheme used for the image data.</summary>
		///<!-- Data type: String -->
		rawMetadata.put(RawMetadataItemName.Compression, "Compression");
		///<summary>Null-terminated character string that contains copyright information.</summary>
		rawMetadata.put(RawMetadataItemName.Copyright, "Copyright");
		///<summary>Date and time the image was created.</summary>
		rawMetadata.put(RawMetadataItemName.DateTime, "Date/Time");
		///<summary>Null-terminated character string that specifies the name of the document from which the image 
		///was scanned.</summary>
		rawMetadata.put(RawMetadataItemName.DocumentName, "Document Name");
		///<summary>Color component values that correspond to a 0 percent dot and a 100 percent dot.</summary>
		rawMetadata.put(RawMetadataItemName.DotRange, "");
		///<summary>Null-terminated character string that specifies the manufacturer of the equipment used to 
		///record the image.</summary>
		rawMetadata.put(RawMetadataItemName.EquipMake, "Make");
		///<summary>Null-terminated character string that specifies the model name or model number of the 
		///equipment used to record the image.</summary>
		rawMetadata.put(RawMetadataItemName.EquipModel, "Model");
		///<summary>Lens aperture. The unit is the APEX value.</summary>
		rawMetadata.put(RawMetadataItemName.ExifAperture, "Aperture Value");
		///<summary>Brightness value. The unit is the APEX value. Ordinarily it is given in the range of 
		///-99.99 to 99.99.</summary>
		rawMetadata.put(RawMetadataItemName.ExifBrightness, "Brightness Value");
		///<summary>The color filter array (CFA) geometric pattern of the image sensor when a one-chip color area sensor 
		///is used. It does not apply to all sensing methods.</summary>
		rawMetadata.put(RawMetadataItemName.ExifCfaPattern, "CFA Pattern");
		///<summary>Color space specifier. Normally sRGB (=1) is used to define the color space based on the PC monitor 
		///conditions and environment. If a color space other than sRGB is used, Uncalibrated (=65535) is set. Image 
		///data recorded as Uncalibrated can be treated as sRGB when it is converted to FlashPix.</summary>
		rawMetadata.put(RawMetadataItemName.ExifColorSpace, "Color Space");
		///<summary>Information specific to compressed data. The compression mode used for a compressed image is
		///indicated in unit BPP.</summary>
		rawMetadata.put(RawMetadataItemName.ExifCompBPP, "Compressed Bits Per Pixel");
		///<summary>Information specific to compressed data. The channels of each component are arranged in order from 
		///the first component to the fourth. For uncompressed data, the data arrangement is given in the 
		///PropertyTagPhotometricInterp tag. However, because PropertyTagPhotometricInterp can only express the 
		///order of Y, Cb, and Cr, this tag is provided for cases when compressed data uses components other than Y, 
		///Cb, and Cr and to support other sequences.</summary>
		rawMetadata.put(RawMetadataItemName.ExifCompConfig, "Components Configuration");
		///<summary>Date and time when the image was stored as digital data. If, for example, an image was captured 
		///by DSC and at the same time the file was recorded, then DateTimeOriginal and DateTimeDigitized will have 
		///the same contents. The format is YYYY:MM:DD HH:MM:SS with time shown in 24-hour format and the date and 
		///time separated by one blank character (0x2000). The character string length is 20 bytes including the 
		///NULL terminator. When the field is empty, it is treated as unknown.</summary>
		rawMetadata.put(RawMetadataItemName.ExifDTDigitized, "Date/Time Digitized");
		///<summary>Null-terminated character string that specifies a fraction of a second for the 
		///PropertyTagExifDTDigitized tag.</summary>
		rawMetadata.put(RawMetadataItemName.ExifDTDigSS, "Sub-Sec Time Digitized");
		///<summary>Date and time when the original image data was generated. For a DSC, the date and time when the 
		///picture was taken. The format is YYYY:MM:DD HH:MM:SS with time shown in 24-hour format and the date and
		///time separated by one blank character (0x2000). The character string length is 20 bytes including the 
		///NULL terminator. When the field is empty, it is treated as unknown.</summary>
		rawMetadata.put(RawMetadataItemName.ExifDTOrig, "Date/Time Original");
		///<summary>Null-terminated character string that specifies a fraction of a second for the 
		///PropertyTagExifDTOrig tag.</summary>
		rawMetadata.put(RawMetadataItemName.ExifDTOrigSS, "Sub-Sec Time Original");
		///<summary>Null-terminated character string that specifies a fraction of a second for the PropertyTagDateTime tag.</summary>
		rawMetadata.put(RawMetadataItemName.ExifDTSubsec, "Sub-Sec Time");
		///<summary>Exposure bias. The unit is the APEX value. Ordinarily it is given in the range -99.99 to 99.99.</summary>
		rawMetadata.put(RawMetadataItemName.ExifExposureBias, "Exposure Bias Value");
		///<summary>Exposure index selected on the camera or input device at the time the image was captured.</summary>
		rawMetadata.put(RawMetadataItemName.ExifExposureIndex, "Exposure Index");
		///<summary>Class of the program used by the camera to set exposure when the picture is taken. The value
		///is an integer with these values: 0 - not defined; 1 - manual; 2 - normal program; 3 - aperture priority;
		///4 - shutter priority; 5 - creative program (biased toward depth of field); 6 - action program (biased
		///toward fast shutter speed); 7 - portrait mode (for close-up photos with the background out of focus);
		///8 - landscape mode (for landscape photos with the background in focus); 9 to 255 - reserved</summary>
		rawMetadata.put(RawMetadataItemName.ExifExposureProg, "Exposure Program");
		///<summary>Exposure time, measured in seconds.</summary>
		rawMetadata.put(RawMetadataItemName.ExifExposureTime, "Exposure Time");
		///<summary>The image source. If a DSC recorded the image, the value of this tag is 3.</summary>
		rawMetadata.put(RawMetadataItemName.ExifFileSource, "File Source");
		///<summary>Flash status. This tag is recorded when an image is taken using a strobe light (flash). 
		///Bit 0 indicates the flash firing status (0b - flash did not fire 1b - flash fired, ""); and bits 1 and 2 
		///indicate the flash return status (00b - no strobe return detection function 01b - reserved 10b - strobe 
		///return light not detected 11b - strobe return light detected). Resulting flash tag values: 0x0000 - flash 
		///did not fire; 0x0001 - flash fired; 0x0005 - strobe return light not detected</summary>
		rawMetadata.put(RawMetadataItemName.ExifFlash, "Flash");
		///<summary>Strobe energy, in Beam Candle Power Seconds (BCPS), at the time the image was captured.</summary>
		rawMetadata.put(RawMetadataItemName.ExifFlashEnergy, "Flash Energy");
		///<summary>F number.</summary>
		rawMetadata.put(RawMetadataItemName.ExifFNumber, "F-Number");
		///<summary>Actual focal length, in millimeters, of the lens. Conversion is not made to the focal length
		///of a 35 millimeter film camera.</summary>
		rawMetadata.put(RawMetadataItemName.ExifFocalLength, "Focal Length");
		///<summary>Unit of measure for PropertyTagExifFocalXRes and PropertyTagExifFocalYRes.</summary>
		rawMetadata.put(RawMetadataItemName.ExifFocalResUnit, "Focal Plane Resolution Unit");
		///<summary>Number of pixels in the image width (x) direction per unit on the camera focal plane. The unit is 
		///specified in PropertyTagExifFocalResUnit.</summary>
		rawMetadata.put(RawMetadataItemName.ExifFocalXRes, "Focal Plane X Resolution");
		///<summary>Number of pixels in the image height (y) direction per unit on the camera focal plane. The unit is
		///specified in PropertyTagExifFocalResUnit.</summary>
		rawMetadata.put(RawMetadataItemName.ExifFocalYRes, "Focal Plane Y Resolution");
		///<summary>FlashPix format version supported by an FPXR file. If the FPXR function supports FlashPix format 
		///version 1.0, this is indicated similarly to PropertyTagExifVer by recording 0100 as a 4-byte ASCII string. 
		///Because the type is PropertyTagTypeUndefined, there is no NULL terminator.</summary>
		rawMetadata.put(RawMetadataItemName.ExifFPXVer, "FlashPix Version");
		///<summary>Private tag used by GDI+. Not for public use. GDI+ uses this tag to locate Exif-specific 
		///information.</summary>
		rawMetadata.put(RawMetadataItemName.ExifIFD, "");
		///<summary>Offset to a block of property items that contain interoperability information.</summary>
		rawMetadata.put(RawMetadataItemName.ExifInterop, "Interoperability Index");
		///<summary>ISO speed and ISO latitude of the camera or input device as specified in ISO 12232.</summary>
		rawMetadata.put(RawMetadataItemName.ExifISOSpeed, "ISO Speed Ratings");
		///<summary>Type of light source. This is an integer with these values: 0 - unknown; 1 - Daylight;
		///2 - Flourescent; 3 - Tungsten; 17 - Standard Light A; 18 - Standard Light B; 19 - Standard Light C;
		///20 - D55; 21 - D65; 22 - D75; 23 to 254 - reserved; 255 - other</summary>
		rawMetadata.put(RawMetadataItemName.ExifLightSource, "Light Source");
		///<summary>Note tag. A tag used by manufacturers of EXIF writers to record information. The contents are 
		///up to the manufacturer.</summary>
		rawMetadata.put(RawMetadataItemName.ExifMakerNote, "Makernote");
		///<summary>Smallest F number of the lens. The unit is the APEX value. Ordinarily it is given in the range 
		///of 00.00 to 99.99, but it is not limited to this range.</summary>
		rawMetadata.put(RawMetadataItemName.ExifMaxAperture, "Max Aperture");
		///<summary>Metering mode. This is an integer with these values: 0 - unknown; 1 - Average; 2 - 
		///CenterWeightedAverage; 3 - Spot; 4 - MultiSpot; 5 - Pattern; 6 - Partial; 7 to 254 - reserved;
		///255 - other</summary>
		rawMetadata.put(RawMetadataItemName.ExifMeteringMode, "Metering Mode");
		///<summary>Optoelectronic conversion function (OECF) specified in ISO 14524. The OECF is the relationship 
		///between the camera optical input and the image values.</summary>
		rawMetadata.put(RawMetadataItemName.ExifOECF, "Opto-electric Conversion Function (OECF)");
		///<summary>Information specific to compressed data. When a compressed file is recorded, the valid width of the 
		///meaningful image must be recorded in this tag, whether or not there is padding data or a restart marker. 
		///This tag should not exist in an uncompressed file.</summary>
		rawMetadata.put(RawMetadataItemName.ExifPixXDim, "Exif Image Width");
		///<summary>Information specific to compressed data. When a compressed file is recorded, the valid height of the 
		///meaningful image must be recorded in this tag whether or not there is padding data or a restart marker. 
		///This tag should not exist in an uncompressed file. Because data padding is unnecessary in the vertical 
		///direction, the number of lines recorded in this valid image height tag will be the same as that recorded 
		///in the SOF.</summary>
		rawMetadata.put(RawMetadataItemName.ExifPixYDim, "Exif Image Height");
		///<summary>The name of an audio file related to the image data. The only relational information recorded is 
		///the EXIF audio file name and extension (an ASCII string that consists of 8 characters plus a period (.), 
		///plus 3 characters). The path is not recorded. When you use this tag, audio files must be recorded in 
		///conformance with the EXIF audio format. Writers can also store audio data within APP2 as FlashPix extension 
		///stream data.</summary>
		rawMetadata.put(RawMetadataItemName.ExifRelatedWav, "Related Sound File");
		///<summary>The type of scene. If a DSC recorded the image, the value of this tag must be set to 1, indicating 
		///that the image was directly photographed.</summary>
		rawMetadata.put(RawMetadataItemName.ExifSceneType, "Scene Type");
		///<summary>Image sensor type on the camera or input device. This is an integer with these values:
		///1 - not defined; 2 - one-chip color area sensor; 3 - two-chip color area sensor; 4 - three-chip color area 
		///sensor; 5 - color sequential area sensor; 7 - trilinear sensor; 8 - color sequential linear sensor;
		///Other - reserved</summary>
		rawMetadata.put(RawMetadataItemName.ExifSensingMethod, "Sensing Method");
		///<summary>Shutter speed. The unit is the Additive System of Photographic Exposure (APEX) value.</summary>
		rawMetadata.put(RawMetadataItemName.ExifShutterSpeed, "Shutter Speed Value");
		///<summary>Camera or input device spatial frequency table and SFR values in the image width, image height, and 
		///diagonal direction, as specified in ISO 12233.</summary>
		rawMetadata.put(RawMetadataItemName.ExifSpatialFR, "Spatial Frequency Response");
		///<summary>Null-terminated character string that specifies the spectral sensitivity of each channel of the 
		///camera used. The string is compatible with the standard developed by the ASTM Technical Committee.</summary>
		rawMetadata.put(RawMetadataItemName.ExifSpectralSense, "Spectral Sensitivity");
		///<summary>Distance to the subject, measured in meters.</summary>
		rawMetadata.put(RawMetadataItemName.ExifSubjectDist, "Subject Distance");
		///<summary>Location of the main subject in the scene. The value of this tag represents the pixel at the center 
		///of the main subject relative to the left edge. The first value indicates the column number, and the second 
		///value indicates the row number.</summary>
		rawMetadata.put(RawMetadataItemName.ExifSubjectLoc, "Subject Location");
		///<summary>Comment tag. A tag used by EXIF users to write keywords or comments about the image besides those 
		///in PropertyTagImageDescription and without the character-code limitations of the 
		///PropertyTagImageDescription tag.</summary>
		rawMetadata.put(RawMetadataItemName.ExifUserComment, "User Comment");
		///<summary>Version of the EXIF standard supported. Nonexistence of this field is taken to mean nonconformance 
		///to the standard. Conformance to the standard is indicated by recording 0210 as a 4-byte ASCII string. 
		///Because the type is PropertyTagTypeUndefined, there is no NULL terminator.</summary>
		rawMetadata.put(RawMetadataItemName.ExifVer, "Exif Version");
		///<summary>Number of extra color components. For example, one extra component might hold an alpha value.</summary>
		rawMetadata.put(RawMetadataItemName.ExtraSamples, "");
		///<summary>Logical order of bits in a byte.</summary>
		rawMetadata.put(RawMetadataItemName.FillOrder, "Fill Order");
		///<summary>Time delay, in hundredths of a second, between two frames in an animated GIF image.</summary>
		rawMetadata.put(RawMetadataItemName.FrameDelay, "");
		///<summary>For each string of contiguous unused bytes, the number of bytes in that string.</summary>
		rawMetadata.put(RawMetadataItemName.FreeByteCounts, "");
		///<summary>For each string of contiguous unused bytes, the byte offset of that string.</summary>
		rawMetadata.put(RawMetadataItemName.FreeOffset, "");
		///<summary>Gamma value attached to the image. The gamma value is stored as a rational number (pair of long) 
		///with a numerator of 100000. For example, a gamma value of 2.2 is stored as the pair (100000, 45455).</summary>
		rawMetadata.put(RawMetadataItemName.Gamma, "Gamma");
		///<summary>Color palette for an indexed bitmap in a GIF image.</summary>
		rawMetadata.put(RawMetadataItemName.GlobalPalette, "");
		///<summary>Altitude, in meters, based on the reference altitude specified by PropertyTagGpsAltitudeRef.</summary>
		rawMetadata.put(RawMetadataItemName.GpsAltitude, "GPS Altitude");
		///<summary>Reference altitude, in meters.</summary>
		rawMetadata.put(RawMetadataItemName.GpsAltitudeRef, "GPS Altitude Ref");
		///<summary>Bearing to the destination point. The range of values is from 0.00 to 359.99.</summary>
		rawMetadata.put(RawMetadataItemName.GpsDestBear, "GPS Dest Bearing");
		///<summary>Null-terminated character string that specifies the reference used for giving the bearing to the 
		///destination point. T specifies true direction, and M specifies magnetic direction.</summary>
		rawMetadata.put(RawMetadataItemName.GpsDestBearRef, "GPS Dest Bearing Ref");
		///<summary>Distance to the destination point.</summary>
		rawMetadata.put(RawMetadataItemName.GpsDestDist, "GPS Dest Distance");
		///<summary>Null-terminated character string that specifies the unit used to express the distance to the 
		///destination point. K, M, and N represent kilometers, miles, and knots respectively.</summary>
		rawMetadata.put(RawMetadataItemName.GpsDestDistRef, "GPS Dest Distance Ref");
		///<summary>Latitude of the destination point. The latitude is expressed as three rational values giving the 
		///degrees, minutes, and seconds respectively. When degrees, minutes, and seconds are expressed, the format 
		///is dd/1, mm/1, ss/1. When degrees and minutes are used and, for example, fractions of minutes are given 
		///up to two decimal places, the format is dd/1, mmmm/100, 0/1.</summary>
		rawMetadata.put(RawMetadataItemName.GpsDestLat, "GPS Dest Latitude");
		///<summary>Null-terminated character string that specifies whether the latitude of the destination point 
		///is north or south latitude. N specifies north latitude, and S specifies south latitude.</summary>
		rawMetadata.put(RawMetadataItemName.GpsDestLatRef, "GPS Dest Latitude Ref");
		///<summary>Longitude of the destination point. The longitude is expressed as three rational values giving 
		///the degrees, minutes, and seconds respectively. When degrees, minutes, and seconds are expressed, the 
		///format is ddd/1, mm/1, ss/1. When degrees and minutes are used and, for example, fractions of minutes 
		///are given up to two decimal places, the format is ddd/1, mmmm/100, 0/1.</summary>
		rawMetadata.put(RawMetadataItemName.GpsDestLong, "GPS Dest Longitude");
		///<summary>Null-terminated character string that specifies whether the longitude of the destination point is 
		///east or west longitude. E specifies east longitude, and W specifies west longitude.</summary>
		rawMetadata.put(RawMetadataItemName.GpsDestLongRef, "GPS Dest Longitude Ref");
		///<summary>GPS DOP (data degree of precision). An HDOP value is written during 2-D measurement, and a 
		///PDOP value is written during 3-D measurement.</summary>
		rawMetadata.put(RawMetadataItemName.GpsGpsDop, "GPS DOP");
		///<summary>Null-terminated character string that specifies the GPS measurement mode. 2 specifies 2-D 
		///measurement, and 3 specifies 3-D measurement.</summary>
		rawMetadata.put(RawMetadataItemName.GpsGpsMeasureMode, "GPS Measure Mode");
		///<summary>Null-terminated character string that specifies the GPS satellites used for measurements. This tag 
		///can be used to specify the ID number, angle of elevation, azimuth, SNR, and other information about each 
		///satellite. The format is not specified. If the GPS receiver is incapable of taking measurements, the value 
		///of the tag must be set to NULL.</summary>
		rawMetadata.put(RawMetadataItemName.GpsGpsSatellites, "GPS Satellites");
		///<summary>Null-terminated character string that specifies the status of the GPS receiver when the image is 
		///recorded. A means measurement is in progress, and V means the measurement is Interoperability.</summary>
		rawMetadata.put(RawMetadataItemName.GpsGpsStatus, "GPS Status");
		///<summary>Time as coordinated universal time (UTC). The value is expressed as three rational numbers that 
		///give the hour, minute, and second.</summary>
		rawMetadata.put(RawMetadataItemName.GpsGpsTime, "GPS Time-Stamp");
		///<summary>Offset to a block of GPS property items. Property items whose tags have the prefix PropertyTagGps 
		///are stored in the GPS block. The GPS property items are defined in the EXIF specification. GDI+ uses this 
		///tag to locate GPS information, but GDI+ does not expose this tag for public use.</summary>
		rawMetadata.put(RawMetadataItemName.GpsIFD, "GPS Area Information");
		///<summary>Direction of the image when it was captured. The range of values is from 0.00 to 359.99.</summary>
		rawMetadata.put(RawMetadataItemName.GpsImgDir, "GPS Img Direction");
		///<summary>Null-terminated character string that specifies the reference for the direction of the image when 
		///it is captured. T specifies true direction, and M specifies magnetic direction.</summary>
		rawMetadata.put(RawMetadataItemName.GpsImgDirRef, "GPS Img Direction Ref");
		///<summary>Latitude. Latitude is expressed as three rational values giving the degrees, minutes, and seconds 
		///respectively. When degrees, minutes, and seconds are expressed, the format is dd/1, mm/1, ss/1. When 
		///degrees and minutes are used and, for example, fractions of minutes are given up to two decimal places, 
		///the format is dd/1, mmmm/100, 0/1.</summary>
		rawMetadata.put(RawMetadataItemName.GpsLatitude, "GPS Latitude");
		///<summary>Null-terminated character string that specifies whether the latitude is north or south. 
		///N specifies north latitude, and S specifies south latitude.</summary>
		rawMetadata.put(RawMetadataItemName.GpsLatitudeRef, "GPS Latitude Ref");
		///<summary>Longitude. Longitude is expressed as three rational values giving the degrees, minutes, and seconds
		///respectively. When degrees, minutes and seconds are expressed, the format is ddd/1, mm/1, ss/1. When 
		///degrees and minutes are used and, for example, fractions of minutes are given up to two decimal places, 
		///the format is ddd/1, mmmm/100, 0/1.</summary>
		rawMetadata.put(RawMetadataItemName.GpsLongitude, "GPS Longitude");
		///<summary>Null-terminated character string that specifies whether the longitude is east or west longitude. 
		///E specifies east longitude, and W specifies west longitude.</summary>
		rawMetadata.put(RawMetadataItemName.GpsLongitudeRef, "GPS Longitude Ref");
		///<summary>Null-terminated character string that specifies geodetic survey data used by the GPS receiver. 
		///If the survey data is restricted to Japan, the value of this tag is TOKYO or WGS-84.</summary>
		rawMetadata.put(RawMetadataItemName.GpsMapDatum, "GPS Map Datum");
		///<summary>Speed of the GPS receiver movement.</summary>
		rawMetadata.put(RawMetadataItemName.GpsSpeed, "GPS Speed");
		///<summary>Null-terminated character string that specifies the unit used to express the GPS receiver speed 
		///of movement. K, M, and N represent kilometers per hour, miles per hour, and knots respectively.</summary>
		rawMetadata.put(RawMetadataItemName.GpsSpeedRef, "GPS Speed Ref");
		///<summary>Direction of GPS receiver movement. The range of values is from 0.00 to 359.99.</summary>
		rawMetadata.put(RawMetadataItemName.GpsTrack, "GPS Track");
		///<summary>Null-terminated character string that specifies the reference for giving the direction of GPS 
		/// receiver movement. T specifies true direction, and M specifies magnetic direction.</summary>
		rawMetadata.put(RawMetadataItemName.GpsTrackRef, "GPS Track Ref");
		///<summary>Version of the Global Positioning Systems (GPS) IFD, given as 2.0.0.0. This tag is mandatory 
		///when the PropertyTagGpsIFD tag is present. When the version is 2.0.0.0, the tag value is 0x02000000.</summary>
		rawMetadata.put(RawMetadataItemName.GpsVer, "GPS Version ID");
		///<summary>For each possible pixel value in a grayscale image, the optical density of that pixel value.</summary>
		rawMetadata.put(RawMetadataItemName.GrayResponseCurve, "");
		///<summary>Precision of the number specified by PropertyTagGrayResponseCurve. 1 specifies tenths, 
		///2 specifies hundredths, 3 specifies thousandths, and so on.</summary>
		rawMetadata.put(RawMetadataItemName.GrayResponseUnit, "");
		///<summary>Block of information about grids and guides.</summary>
		rawMetadata.put(RawMetadataItemName.GridSize, "Grid Size");
		///<summary>Angle for screen.</summary>
		rawMetadata.put(RawMetadataItemName.HalftoneDegree, "");
		///<summary>Information used by the halftone function</summary>
		rawMetadata.put(RawMetadataItemName.HalftoneHints, "");
		///<summary>Ink's screen frequency, in lines per inch.</summary>
		rawMetadata.put(RawMetadataItemName.HalftoneLPI, "");
		///<summary>Units for the screen frequency.</summary>
		rawMetadata.put(RawMetadataItemName.HalftoneLPIUnit, "");
		///<summary>Miscellaneous halftone information.</summary>
		rawMetadata.put(RawMetadataItemName.HalftoneMisc, "");
		///<summary>Boolean value that specifies whether to use the printer's default screens.</summary>
		rawMetadata.put(RawMetadataItemName.HalftoneScreen, "");
		///<summary>Shape of the halftone dots.</summary>
		rawMetadata.put(RawMetadataItemName.HalftoneShape, "");
		///<summary>Null-terminated character string that specifies the computer and/or operating system 
		///used to create the image.</summary>
		rawMetadata.put(RawMetadataItemName.HostComputer, "Host Computer");
		///<summary>ICC profile embedded in the image.</summary>
		rawMetadata.put(RawMetadataItemName.ICCProfile, "Profile Size");
		///<summary>Null-terminated character string that identifies an ICC profile. </summary>
		rawMetadata.put(RawMetadataItemName.ICCProfileDescriptor, "Profile Description");
		///<summary>Null-terminated character string that specifies the title of the image.</summary>
		rawMetadata.put(RawMetadataItemName.ImageDescription, "Image Description");
		///<summary>Number of pixel rows.</summary>
		rawMetadata.put(RawMetadataItemName.ImageHeight, "Image Height");
		///<summary>Null-terminated character string that specifies the title of the image.</summary>
		rawMetadata.put(RawMetadataItemName.ImageTitle, "Image Title");
		///<summary>Number of pixels per row.</summary>
		rawMetadata.put(RawMetadataItemName.ImageWidth, "Image Width");
		///<summary>Index of the background color in the palette of a GIF image.</summary>
		rawMetadata.put(RawMetadataItemName.IndexBackground, "Background Color Index");
		///<summary>Index of the transparent color in the palette of a GIF image.</summary>
		rawMetadata.put(RawMetadataItemName.IndexTransparent, "");
		///<summary>Sequence of concatenated, null-terminated, character strings that specify the names of the 
		///inks used in a separated image.</summary>
		rawMetadata.put(RawMetadataItemName.InkNames, "");
		///<summary>Set of inks used in a separated image.</summary>
		rawMetadata.put(RawMetadataItemName.InkSet, "");
		///<summary>For each color component, the offset to the AC Huffman table for that component. See also 
		///PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGACTables, "JPEGAC Tables");
		///<summary>For each color component, the offset to the DC Huffman table (or lossless Huffman table) for 
		///that component. See also PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGDCTables, "JPEGDC Tables");
		///<summary>Offset to the start of a JPEG bitstream.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGInterFormat, "");
		///<summary>Length, in bytes, of the JPEG bitstream.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGInterLength, "");
		///<summary>For each color component, a lossless predictor-selection value for that component. 
		///See also PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGLosslessPredictors, "JPEG Lossless Predictors");
		///<summary>For each color component, a point transformation value for that component. See also 
		///PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGPointTransforms, "JPEG Point Transforms");
		///<summary>JPEG compression process.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGProc, "JPEG Proc");
		///<summary>For each color component, the offset to the quantization table for that component. 
		///See also PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGQTables, "JPEGQ Tables");
		///<summary>Private tag used by the Adobe Photoshop format. Not for public use.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGQuality, "JPEG Quality");
		///<summary>Length of the restart interval.</summary>
		rawMetadata.put(RawMetadataItemName.JPEGRestartInterval, "");
		///<summary>For an animated GIF image, the number of times to display the animation. A value of 0 specifies 
		///that the animation should be displayed infinitely.</summary>
		rawMetadata.put(RawMetadataItemName.LoopCount, "");
		///<summary>Luminance table. The luminance table and the chrominance table are used to control JPEG quality. 
		///A valid luminance or chrominance table has 64 entries of type PropertyTagTypeShort. If an image has 
		///either a luminance table or a chrominance table, then it must have both tables.</summary>
		rawMetadata.put(RawMetadataItemName.LuminanceTable, "");
		///<summary>For each color component, the maximum value assigned to that component. See also 
		///PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.MaxSampleValue, "Maximum Sample Value");
		///<summary>For each color component, the minimum value assigned to that component. See also 
		///PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.MinSampleValue, "Minimum Sample Value");
		///<summary>Type of data in a subfile.</summary>
		rawMetadata.put(RawMetadataItemName.NewSubfileType, "New Subfile Type");
		///<summary>Number of inks.</summary>
		rawMetadata.put(RawMetadataItemName.NumberOfInks, "");
		///<summary>Image orientation viewed in terms of rows and columns. The value is a System.UShort, with these
		///values: 1 - The 0th row is at the top of the visual image, and the 0th column is the visual left side. 
		///2 - The 0th row is at the visual top of the image, and the 0th column is the visual right side. 
		///3 - The 0th row is at the visual bottom of the image, and the 0th column is the visual right side. 
		///4 - The 0th row is at the visual bottom of the image, and the 0th column is the visual right side. 
		///5 - The 0th row is the visual left side of the image, and the 0th column is the visual top. 
		///6 - The 0th row is the visual right side of the image, and the 0th column is the visual top. 
		///7 - The 0th row is the visual right side of the image, and the 0th column is the visual bottom. 
		///8 - The 0th row is the visual left side of the image, and the 0th column is the visual bottom. </summary>
		rawMetadata.put(RawMetadataItemName.Orientation, "Orientation");
		///<summary>Null-terminated character string that specifies the name of the page from which the image was scanned.</summary>
		rawMetadata.put(RawMetadataItemName.PageName, "Page Name");
		///<summary>Page number of the page from which the image was scanned.</summary>
		rawMetadata.put(RawMetadataItemName.PageNumber, "Page Number");
		///<summary>Palette histogram.</summary>
		rawMetadata.put(RawMetadataItemName.PaletteHistogram, "");
		///<summary>How pixel data will be interpreted.</summary>
		rawMetadata.put(RawMetadataItemName.PhotometricInterp, "Photometric Interpretation");
		///<summary>Pixels per unit in the x direction.</summary>
		rawMetadata.put(RawMetadataItemName.PixelPerUnitX, "");
		///<summary>Pixels per unit in the y direction.</summary>
		rawMetadata.put(RawMetadataItemName.PixelPerUnitY, "");
		///<summary>Unit for PropertyTagPixelPerUnitX and PropertyTagPixelPerUnitY.</summary>
		rawMetadata.put(RawMetadataItemName.PixelUnit, "Pixel Unit");
		///<summary>Whether pixel components are recorded in chunky or planar format.</summary>
		rawMetadata.put(RawMetadataItemName.PlanarConfig, "Planar Configuration");
		///<summary>Type of prediction scheme that was applied to the image data before the encoding scheme was applied.</summary>
		rawMetadata.put(RawMetadataItemName.Predictor, "Predictor");
		///<summary>For each of the three primary colors in the image, the chromaticity of that color.</summary>
		rawMetadata.put(RawMetadataItemName.PrimaryChromaticities, "Primary Chromaticities");
		///<summary>Sequence of one-byte Boolean values that specify printing options.</summary>
		rawMetadata.put(RawMetadataItemName.PrintFlags, "Print Flags");
		///<summary>Print flags bleed width.</summary>
		rawMetadata.put(RawMetadataItemName.PrintFlagsBleedWidth, "");
		///<summary>Print flags bleed width scale.</summary>
		rawMetadata.put(RawMetadataItemName.PrintFlagsBleedWidthScale, "");
		///<summary>Print flags center crop marks.</summary>
		rawMetadata.put(RawMetadataItemName.PrintFlagsCrop, "");
		///<summary>Print flags version.</summary>
		rawMetadata.put(RawMetadataItemName.PrintFlagsVersion, "");
		///<summary>Reference black point value and reference white point value.</summary>
		rawMetadata.put(RawMetadataItemName.REFBlackWhite, "Reference Black/White");
		///<summary>Unit of measure for the horizontal resolution and the vertical resolution. 2 = inch, 3 = centimeter</summary>
		rawMetadata.put(RawMetadataItemName.ResolutionUnit, "Resolution Unit");
		///<summary>Units in which to display the image width.</summary>
		rawMetadata.put(RawMetadataItemName.ResolutionXLengthUnit, "");
		///<summary>Units in which to display horizontal resolution.</summary>
		rawMetadata.put(RawMetadataItemName.ResolutionXUnit, "Resolution X Unit");
		///<summary>Units in which to display the image height.</summary>
		rawMetadata.put(RawMetadataItemName.ResolutionYLengthUnit, "");
		///<summary>Units in which to display vertical resolution.</summary>
		rawMetadata.put(RawMetadataItemName.ResolutionYUnit, "Resolution Y Unit");
		///<summary>Number of rows per strip. See also PropertyTagStripBytesCount and PropertyTagStripOffsets.</summary>
		rawMetadata.put(RawMetadataItemName.RowsPerStrip, "Rows Per Strip");
		///<summary>For each color component, the numerical format (unsigned, signed, floating point) of that
		///component. See also PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.SampleFormat, "SampleFormat");
		///<summary>Number of color components per pixel.</summary>
		rawMetadata.put(RawMetadataItemName.SamplesPerPixel, "Samples Per Pixel");
		///<summary>For each color component, the maximum value of that component. See also PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.SMaxSampleValue, "Maximum Sample Value");
		///<summary>For each color component, the minimum value of that component. See also PropertyTagSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.SMinSampleValue, "Minimum Sample Value");
		///<summary>Null-terminated character string that specifies the name and version of the software 
		///or firmware of the device used to generate the image.</summary>
		rawMetadata.put(RawMetadataItemName.SoftwareUsed, "Software");
		///<summary>How the image should be displayed as defined by the International Color Consortium (ICC). If a 
		///GDI+ Image object is constructed with the useEmbeddedColorManagement parameter set to TRUE, then GDI+ 
		///renders the image according to the specified rendering intent. The intent can be set to perceptual, 
		///relative colorimetric, saturation, or absolute colorimetric. Perceptual intent (0), which is suitable for 
		///photographs, gives good adaptation to the display device gamut at the expense of colorimetric accuracy. 
		///Relative colorimetric intent (1) is suitable for images (for example, logos) that require color appearance 
		///matching that is relative to the display device white point. Saturation intent (2), which is suitable for 
		///charts and graphs, preserves saturation at the expense of hue and lightness. Absolute colorimetric intent (3)
		///is suitable for proofs (previews of images destined for a different display device) that require 
		///preservation of absolute colorimetry.</summary>
		rawMetadata.put(RawMetadataItemName.SRGBRenderingIntent, "Custom Rendered");
		///<summary>For each strip, the total number of bytes in that strip.</summary>
		rawMetadata.put(RawMetadataItemName.StripBytesCount, "Strip Byte Counts");
		///<summary>For each strip, the byte offset of that strip. See also PropertyTagRowsPerStrip
		///and PropertyTagStripBytesCount.</summary>
		rawMetadata.put(RawMetadataItemName.StripOffsets, "Strip Offsets");
		///<summary>Type of data in a subfile.</summary>
		rawMetadata.put(RawMetadataItemName.SubfileType, "Subfile Type");
		///<summary>Set of flags that relate to T4 encoding.</summary>
		rawMetadata.put(RawMetadataItemName.T4Option, "");
		///<summary>Set of flags that relate to T6 encoding.</summary>
		rawMetadata.put(RawMetadataItemName.T6Option, "");
		///<summary>Null-terminated character string that describes the intended printing environment.</summary>
		rawMetadata.put(RawMetadataItemName.TargetPrinter, "");
		///<summary>Technique used to convert from gray pixels to black and white pixels.</summary>
		rawMetadata.put(RawMetadataItemName.ThreshHolding, "Thresholding");
		///<summary>Null-terminated character string that specifies the name of the person who created the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailArtist, "");
		///<summary>Number of bits per color component in the thumbnail image. See also 
		///PropertyTagThumbnailSamplesPerPixel.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailBitsPerSample, "");
		///<summary>Bits per pixel (BPP) for the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailColorDepth, "");
		///<summary>Compressed size, in bytes, of the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailCompressedSize, "");
		///<summary>Compression scheme used for thumbnail image data.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailCompression, "");
		///<summary>Null-terminated character string that contains copyright information for the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailCopyRight, "");
		///<summary>Raw thumbnail bits in JPEG or RGB format. Depends on PropertyTagThumbnailFormat.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailData, "");
		///<summary>Date and time the thumbnail image was created. See also PropertyTagDateTime.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailDateTime, "");
		///<summary>Null-terminated character string that specifies the manufacturer of the equipment used to 
		///record the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailEquipMake, "");
		///<summary>Null-terminated character string that specifies the model name or model number of the
		///equipment used to record the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailEquipModel, "");
		///<summary>Format of the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailFormat, "");
		///<summary>Height, in pixels, of the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailHeight, "ThumbnailH eight");
		///<summary>Null-terminated character string that specifies the title of the image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailImageDescription, "");
		///<summary>Number of pixel rows in the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailImageHeight, "Thumbnail Image Height");
		///<summary>Number of pixels per row in the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailImageWidth, "Thumbnail Image Width");
		///<summary>Thumbnail image orientation in terms of rows and columns. See also PropertyTagOrientation.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailOrientation, "Thumbnail Orientation");
		///<summary>How thumbnail pixel data will be interpreted.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailPhotometricInterp, "");
		///<summary>Whether pixel components in the thumbnail image are recorded in chunky or planar format. 
		///See also PropertyTagPlanarConfig.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailPlanarConfig, "");
		///<summary>Number of color planes for the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailPlanes, "");
		///<summary>For each of the three primary colors in the thumbnail image, the chromaticity of that color. 
		///See also PropertyTagPrimaryChromaticities.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailPrimaryChromaticities, "");
		///<summary>Byte offset between rows of pixel data.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailRawBytes, "");
		///<summary>Reference black point value and reference white point value for the thumbnail image. See also 
		///PropertyTagREFBlackWhite.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailRefBlackWhite, "");
		///<summary>Unit of measure for the horizontal resolution and the vertical resolution of the thumbnail 
		///image. See also PropertyTagResolutionUnit.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailResolutionUnit, "Thumbnail Resolution Unit");
		///<summary>Thumbnail resolution in the width direction. The resolution unit is
		///given in PropertyTagThumbnailResolutionUnit</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailResolutionX, "Thumbnail X Resolution");
		///<summary>Thumbnail resolution in the height direction. The resolution unit is 
		///given in PropertyTagThumbnailResolutionUnit</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailResolutionY, "Thumbnail Y Resolution");
		///<summary>Number of rows per strip in the thumbnail image. See also 
		///PropertyTagThumbnailStripBytesCount and PropertyTagThumbnailStripOffsets.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailRowsPerStrip, "");
		///<summary>Number of color components per pixel in the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailSamplesPerPixel, "");
		///<summary>Total size, in bytes, of the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailSize, "Thumbnail Length");
		///<summary>Null-terminated character string that specifies the name and version of the 
		///software or firmware of the device used to generate the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailSoftwareUsed, "");
		///<summary>For each thumbnail image strip, the total number of bytes in that strip.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailStripBytesCount, "");
		///<summary>For each strip in the thumbnail image, the byte offset of that strip. See also 
		///PropertyTagThumbnailRowsPerStrip and PropertyTagThumbnailStripBytesCount.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailStripOffsets, "Thumbnail Offset");
		///<summary>Tables that specify transfer functions for the thumbnail image. See also 
		///PropertyTagTransferFunction.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailTransferFunction, "");
		///<summary>Chromaticity of the white point of the thumbnail image. See also PropertyTagWhitePoint.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailWhitePoint, "");
		///<summary>Width, in pixels, of the thumbnail image.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailWidth, "Thumbnail Width");
		///<summary>Coefficients for transformation from RGB to YCbCr data for the thumbnail image. See also 
		///PropertyTagYCbCrCoefficients.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailYCbCrCoefficients, "");
		///<summary>Position of chrominance components in relation to the luminance component for the thumbnail image. 
		///See also PropertyTagYCbCrPositioning.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailYCbCrPositioning, "");
		///<summary>Sampling ratio of chrominance components in relation to the luminance component for the 
		///thumbnail image. See also PropertyTagYCbCrSubsampling.</summary>
		rawMetadata.put(RawMetadataItemName.ThumbnailYCbCrSubsampling, "");
		///<summary>For each tile, the number of bytes in that tile.</summary>
		rawMetadata.put(RawMetadataItemName.TileByteCounts, "Tile Byte Counts");
		///<summary>Number of pixel rows in each tile.</summary>
		rawMetadata.put(RawMetadataItemName.TileLength, "Tile Length");
		///<summary>For each tile, the byte offset of that tile.</summary>
		rawMetadata.put(RawMetadataItemName.TileOffset, "Tile Offsets");
		///<summary>Number of pixel columns in each tile.</summary>
		rawMetadata.put(RawMetadataItemName.TileWidth, "Tile Width");
		///<summary>Tables that specify transfer functions for the image.</summary>
		rawMetadata.put(RawMetadataItemName.TransferFunction, "Transfer Function");
		///<summary>Table of values that extends the range of the transfer function.</summary>
		rawMetadata.put(RawMetadataItemName.TransferRange, "Transfer Range");
		///<summary>Chromaticity of the white point of the image.</summary>
		rawMetadata.put(RawMetadataItemName.WhitePoint, "White Point");
		///<summary>Offset from the left side of the page to the left side of the image. The unit of measure
		///is specified by PropertyTagResolutionUnit.</summary>
		rawMetadata.put(RawMetadataItemName.XPosition, "X Position");
		///<summary>Number of pixels per unit in the image width (x) direction. The unit is specified by 
		///PropertyTagResolutionUnit.</summary>
		rawMetadata.put(RawMetadataItemName.XResolution, "X Resolution");
		///<summary>Coefficients for transformation from RGB to YCbCr image data. </summary>
		rawMetadata.put(RawMetadataItemName.YCbCrCoefficients, "YCbCr Coefficients");
		///<summary>Position of chrominance components in relation to the luminance component.</summary>
		rawMetadata.put(RawMetadataItemName.YCbCrPositioning, "YCbCr Positioning");
		///<summary>Sampling ratio of chrominance components in relation to the luminance component.</summary>
		rawMetadata.put(RawMetadataItemName.YCbCrSubsampling, "YCbCr Sub-Sampling");
		///<summary>Offset from the top of the page to the top of the image. The unit of measure is 
		///specified by PropertyTagResolutionUnit.</summary>
		rawMetadata.put(RawMetadataItemName.YPosition, "Y Position");
		///<summary>Number of pixels per unit in the image height (y) direction. The unit is specified by 
		///PropertyTagResolutionUnit.</summary>
		rawMetadata.put(RawMetadataItemName.YResolution, "Y Resolution");
	
		return rawMetadata;
	}

	/// <summary>
	/// Gets a collection of query parameters for extracting IPTC data from a media file.
	/// The key identifies the metadata item. The value is the query identifier that is combined
	/// with the <see cref="IptcQueryFormatString" /> to create a query String that can be
	/// passed to the <see cref="BitmapMetadata.GetQuery" /> method.
	/// </summary>
	/// <value>A Dictionary object.</value>
	private synchronized static Map<MetadataItemName, String> getIptcQueryParameters(){
		if (iptcQueryParameters == null){
			if (iptcQueryParameters == null){
				Map<MetadataItemName, String> tmp = new HashMap<MetadataItemName, String>();

				tmp.put(MetadataItemName.IptcByline, "By-Line");
				tmp.put(MetadataItemName.IptcBylineTitle, "By-line Title");
				tmp.put(MetadataItemName.IptcCaption, "Caption");
				tmp.put(MetadataItemName.IptcCity, "City");
				tmp.put(MetadataItemName.IptcCopyrightNotice, "Copyright Notice");
				tmp.put(MetadataItemName.IptcCountryPrimaryLocationName, "Country/Primary Location Name");
				tmp.put(MetadataItemName.IptcCredit, "Credit");
				tmp.put(MetadataItemName.IptcDateCreated, "Date Created");
				tmp.put(MetadataItemName.IptcHeadline, "Headline");
				tmp.put(MetadataItemName.IptcKeywords, "Keywords");
				tmp.put(MetadataItemName.IptcObjectName, "Object Name");
				tmp.put(MetadataItemName.IptcOriginalTransmissionReference, "Original Transmission Reference");
				tmp.put(MetadataItemName.IptcProvinceState, "Province/State");
				tmp.put(MetadataItemName.IptcRecordVersion, "Record Version");
				tmp.put(MetadataItemName.IptcSource, "Source");
				tmp.put(MetadataItemName.IptcSpecialInstructions, "Special Instructions");
				tmp.put(MetadataItemName.IptcSublocation, "Sub-location");
				tmp.put(MetadataItemName.IptcWriterEditor, "Writer/Editor");

				iptcQueryParameters = tmp;
			}
		}

		return iptcQueryParameters;
	}

	/// <summary>
	/// Gets a collection of meta items that can be updated in the orginal file. The key identifies the 
	/// metadata item. The value is the query that can be passed to any of the <see cref="BitmapMetadata" /> methods.
	/// </summary>
	/// <value>A Dictionary object.</value>
	private synchronized static Map<MetadataItemName, String> getUpdatableMetaItems(){
		if (updatableMetaItems == null){
			if (updatableMetaItems == null)	{
				Map<MetadataItemName, String> tmp = new HashMap<MetadataItemName, String>();
				tmp.put(MetadataItemName.Orientation, "ImageOrientation" );

				updatableMetaItems = tmp;
			}
		}

		return updatableMetaItems;
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentObjectMetadataReadWriter" /> class.
	/// </summary>
	/// <param name="contentObject">The content object.</param>
	public ImageMetadataReadWriter(ContentObjectBo contentObject)	{
		super(contentObject);
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Gets the metadata value for the specified <paramref name="metaName" />. May return null.
	/// </summary>
	/// <param name="metaName">Name of the metadata item to retrieve.</param>
	/// <returns>An instance that implements <see cref="MetaValue" />.</returns>
	public MetaValue getMetaValue(MetadataItemName metaName){
		switch (metaName){
			case Title: return getTitle();
			case DatePictureTaken: return getDatePictureTaken();
			case Author: return getAuthor();
			case CameraModel: return getCameraModel();
			case EquipmentManufacturer: return getCameraManufacturer();
			case Tags: return getKeywords();
			case Rating: return getRating();
			case Comment: return getComment();
			case Copyright: return getCopyright();
			case Subject: return getSubject();
			case ColorRepresentation: return getColorRepresentation();
			case Description: return getDescription();
			case Dimensions: return getDimensions();
			case ExposureCompensation: return getExposureCompensation();
			case ExposureProgram: return getExposureProgram();
			case Orientation: return getOrientation();
			case ExposureTime: return getExposureTime();
			case FlashMode: return getFlashMode();
			case FNumber: return getFNumber();
			case FocalLength: return getFocalLength();
			case Height: return getHeight();
			case HorizontalResolution: return getHorizontalResolution();
			case IsoSpeed: return getIsoSpeed();
			case LensAperture: return getLensAperture();
			case LightSource: return getLightSource();
			case MeteringMode: return getMeteringMode();
			case SubjectDistance: return getSubjectDistance();
			case VerticalResolution: return getVerticalResolution();
			case Width: return getWidth();

			case GpsVersion:
			case GpsLocation:
			case GpsLatitude:
			case GpsLongitude:
			case GpsAltitude:
			case GpsDestLocation:
			case GpsDestLatitude:
			//case GpsLocationWithMapLink: // Built from template, nothing to extract
			//case GpsDestLocationWithMapLink: // Built from template, nothing to extract
			case GpsDestLongitude: return getGpsValue(metaName);

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
			case IptcWriterEditor: return getIptcValue(metaName);

			default:
				return super.getMetaValue(metaName);
		}
	}

	/// <summary>
	/// Persists the meta value identified by <paramref name="metaName" /> to the media file. It is expected the meta item
	/// exists in <see cref="ContentObject.MetadataItems" />.
	/// </summary>
	/// <param name="metaName">Name of the meta item to persist.</param>
	/// <exception cref="System.NotSupportedException"></exception>
	public void SaveMetaValue(MetadataItemName metaName){
		persistMetaValue(metaName, MetaPersistAction.Save);
	}

	/// <summary>
	/// Permanently removes the meta value from the media file. The item is also removed from
	/// <see cref="ContentObject.MetadataItems" />.
	/// </summary>
	/// <param name="metaName">Name of the meta item to delete.</param>
	/// <exception cref="System.NotSupportedException"></exception>
	public void DeleteMetaValue(MetadataItemName metaName)	{
		persistMetaValue(metaName, MetaPersistAction.Delete);
	}

	//#endregion

	//#region Metadata functions

	private MetaValue getTitle(){
		// Look in three places for title:
		// 1. The Title property in the WPF BitmapMetadata class.
		// 2. The ImageTitle property of the GDI+ property tags.
		// 3. The filename.
		MetaValue wpfTitle = getWpfTitle();

		if (wpfTitle != null)
			return wpfTitle;

		String title = getStringMetadataItem(RawMetadataItemName.ImageTitle);

		return !StringUtils.isBlank(title) ? new MetaValue(title, title) : new MetaValue(getContentObject().getOriginal().getFileName());
	}

	private MetaValue getWpfTitle()	{
		String wpfValue = null;

		/*try
		{
			wpfValue = wpfMetadata.getTitle();
		}
		catch (NotSupportedException) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException) { }
		catch (InvalidOperationException) { }*/

		return (!StringUtils.isBlank(wpfValue) ? new MetaValue(wpfValue.trim(), wpfValue) : null);
	}

	private MetaValue getDatePictureTaken()	{
		MetaValue metaValue = getDatePictureTakenWpf();
		if (metaValue == null)
			metaValue =  getDatePictureTakenGdi();
		
		return  metaValue;
	}

	private MetaValue getAuthor(){
		/*try
		{
			String author = convertStringCollectionToDelimitedString(wpfMetadata.getAuthor());

			return new MetaValue(author, author);
		}
		catch (NotSupportedException) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException) { }
		catch (InvalidOperationException) { }*/

		return null;
	}

	private MetaValue getCameraModel(){
		MetaValue wpfCameraModel = getWpfCameraModel();

		if (wpfCameraModel != null)
			return wpfCameraModel;

		String cameraModel = getStringMetadataItem(RawMetadataItemName.EquipModel);

		return !StringUtils.isBlank(cameraModel) ? new MetaValue(cameraModel, cameraModel) : null;
	}

	private MetaValue getWpfCameraModel(){
		String wpfValue = null;

		/*try
		{
			wpfValue = WpfMetadata.CameraModel;
		}
		catch (NotSupportedException) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException) { }
		catch (InvalidOperationException) { }*/

		return (wpfValue != null ? new MetaValue(wpfValue.trim(), wpfValue) : null);
	}

	private MetaValue getCameraManufacturer(){
		MetaValue wpfCameraManufacturer = getWpfCameraManufacturer();

		if (wpfCameraManufacturer != null)
			return wpfCameraManufacturer;

		String cameraMfg = getStringMetadataItem(RawMetadataItemName.EquipMake);

		return !StringUtils.isBlank(cameraMfg) ? new MetaValue(cameraMfg, cameraMfg) : null;
	}

	private MetaValue getWpfCameraManufacturer(){
		String wpfValue = null;

		/*try
		{
			wpfValue = WpfMetadata.CameraManufacturer;
		}
		catch (NotSupportedException) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException) { }
		catch (InvalidOperationException) { }*/

		return (wpfValue != null ? new MetaValue(wpfValue.trim(), wpfValue) : null);
	}

	private MetaValue getKeywords()	{
		/*try
		{
			var keywords = convertStringCollectionToDelimitedString(WpfMetadata.Keywords);

			return new MetaValue(keywords, keywords);
		}
		catch (NotSupportedException) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException) { }
		catch (InvalidOperationException) { }*/

		return null;
	}

	private MetaValue getRating(){
		/*try
		{
			var rating = WpfMetadata.Rating;
			return (rating > 0 ? new MetaValue(rating.toString(), rating.toString()) : null);
		}
		catch (NotSupportedException) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException) { }
		catch (InvalidOperationException) { }*/

		return null;
	}

	private MetaValue getComment()	{
		MetaValue wpfComment = getWpfComment();

		if (wpfComment != null)
			return wpfComment;

		String comment = getStringMetadataItem(RawMetadataItemName.ExifUserComment);

		return !StringUtils.isBlank(comment) ? new MetaValue(comment, comment) : null;
	}

	private MetaValue getWpfComment()	{
		String wpfValue = null;

		/*try
		{
			wpfValue = WpfMetadata.Comment;
		}
		catch (NotSupportedException) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException) { }
		catch (InvalidOperationException) { }*/

		return (wpfValue != null ? new MetaValue(wpfValue.trim(), wpfValue) : null);
	}

	private MetaValue getCopyright()	{
		MetaValue wpfCopyright = getWpfCopyright();

		if (wpfCopyright != null)
			return wpfCopyright;

		String copyright = getStringMetadataItem(RawMetadataItemName.Copyright);

		return !StringUtils.isBlank(copyright) ? new MetaValue(copyright, copyright) : null;
	}

	private MetaValue getWpfCopyright()	{
		String wpfValue = null;

		/*try
		{
			wpfValue = WpfMetadata.Copyright;
		}
		catch (NotSupportedException) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException) { }
		catch (InvalidOperationException) { }*/

		return (wpfValue != null ? new MetaValue(wpfValue.trim(), wpfValue) : null);
	}

	private MetaValue getSubject()	{
		MetaValue wpfSubject = getWpfSubject();
		if (wpfSubject != null)
			return wpfSubject;
		else
			return null;
	}

	private MetaValue getWpfSubject()	{
		String wpfValue = null;
		/*try
		{
			wpfValue = WpfMetadata.Subject;
		}
		catch (NotSupportedException) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException) { }
		catch (InvalidOperationException) { }*/

		return (wpfValue != null ? new MetaValue(wpfValue.trim(), wpfValue) : null);
	}

	private MetaValue getColorRepresentation()	{
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifColorSpace)) != null){
			String value = rawMdi.getValue().toString().trim();

			if (value == "1")
				return new MetaValue("{metadata.colorRepresentation_sRGB}", value);
			else
				return new MetaValue("{metadata.colorRepresentation_Uncalibrated}", value);
		}

		return null;
	}

	private MetaValue getDescription()	{
		String desc = getStringMetadataItem(RawMetadataItemName.ImageDescription);

		return (desc != null ? new MetaValue(desc, desc) : null);
	}

	private MetaValue getDimensions()	{
		int width = getWidthAsInt();
		int height = getHeightAsInt();

		if ((width > 0) && (height > 0)){
			return new MetaValue(StringUtils.join(new Object[] {width, " x ", height}));
		}

		return null;
	}

	private MetaValue getExposureCompensation()	{
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifExposureBias)) != null){
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Fraction){
				float value = ((Fraction)rawMdi.getValue()).floatValue();

				return new MetaValue(
					StringUtils.join(new String[] {String.format("##0.# ", value), "{metadata.exposureCompensation_Suffix}"}),
					String.valueOf(value));
			}*/
			/*return new MetaValue(
					StringUtils.join(new String[] {rawMdi.getValue().toString(), "{metadata.exposureCompensation_Suffix}"}), rawMdi.getValue().toString());*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}
		return null;
	}

	private MetaValue getExposureProgram()	{
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifExposureProg)) != null)
		{
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Int64)
			{
				ExposureProgram expProgram = (ExposureProgram)(Int64)rawMdi.Value;
				if (MetadataEnumHelper.IsValidExposureProgram(expProgram))
				{
					return new MetaValue(expProgram.ToString(), ((ushort)expProgram).ToString(CultureInfo.InvariantCulture));
				}
			}*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}
		return null;
	}

	private MetaValue getOrientation()	{
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.Orientation)) != null)
		{
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Int64)
			{
				Orientation orientation = (Orientation)(Int64)rawMdi.Value;
				if (MetadataEnumHelper.IsValidOrientation(orientation))
				{
					return new MetaValue(orientation.getDescription(), ((ushort)orientation).ToString(CultureInfo.InvariantCulture));
				}
			}*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}
		return null;
	}

	private MetaValue getExposureTime()	{
		MetadataItem rawMdi;
		final int numSeconds = 1; // If the exposure time is less than this # of seconds, format as fraction (1/350 sec.); otherwise convert to Single (2.35 sec.)
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifExposureTime)) != null){
			/*String exposureTime;
			if ((rawMdi.getExtractedValueType() == ExtractedValueType.Fraction) && ((Fraction)rawMdi.Value).ToSingle() > numSeconds)
			{
				exposureTime = Math.Round(((Fraction)rawMdi.Value).ToSingle(), 2).ToString(CultureInfo.InvariantCulture);
			}
			else
			{
				exposureTime = rawMdi.getValue().toString();
			}

			return new MetaValue(String.Concat(exposureTime, " ", Resources.Metadata_ExposureTime_Units), exposureTime);*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}
		return null;
	}

	private MetaValue getFlashMode(){
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifFlash)) != null){
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Int64)
			{
				var flashMode = (FlashMode)(Int64)rawMdi.Value;
				if (MetadataEnumHelper.IsValidFlashMode(flashMode))
				{
					return new MetaValue(flashMode.getDescription(), ((ushort)flashMode).ToString(CultureInfo.InvariantCulture));
				}
			}*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}
		return null;
	}

	private MetaValue getFNumber(){
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifFNumber)) != null)
		{
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Fraction)
			{
				float value = ((Fraction)rawMdi.Value).ToSingle();
				return new MetaValue(value.ToString("f/##0.#", CultureInfo.InvariantCulture), value.ToString(CultureInfo.InvariantCulture));
			}*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}
		return null;
	}

	private MetaValue getFocalLength(){
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifFocalLength)) != null)
		{
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Fraction)
			{
				float value = ((Fraction)rawMdi.Value).ToSingle();
				return new MetaValue(String.Concat(Math.Round(value), " ", Resources.Metadata_FocalLength_Units), value.ToString(CultureInfo.InvariantCulture));
			}*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}
		return null;
	}

	private MetaValue getHeight(){
		int height = getHeightAsInt();

		return (height > 0 ? new MetaValue(StringUtils.join(new Object[] {height, " ", "{metadata.height_Units}"}), Integer.toString(height)) : null);
	}

	private MetaValue getHorizontalResolution(){
		MetadataItem rawMdi;
		String resolutionUnit = StringUtils.EMPTY;

		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ResolutionXUnit)) != null)	{
			resolutionUnit = rawMdi.getValue().toString();
		}

		if ((StringUtils.isBlank(resolutionUnit)) && ((rawMdi = getRawMetadata().get(RawMetadataItemName.ResolutionUnit)) != null))	{
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Int64)
			{
				ResolutionUnit resUnit = (ResolutionUnit)(Int64)rawMdi.Value;
				if (MetadataEnumHelper.IsValidResolutionUnit(resUnit))
				{
					resolutionUnit = resUnit.ToString();
				}
			}*/
		}

		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.XResolution)) != null){
			/*String xResolution;
			if (rawMdi.getExtractedValueType() == ExtractedValueType.Fraction)
			{
				xResolution = Math.Round(((Fraction)rawMdi.Value).ToSingle(), 2).ToString(CultureInfo.InvariantCulture);
			}
			else
			{
				xResolution = rawMdi.getValue().toString();
			}

			return new MetaValue(String.Concat(xResolution, " ", resolutionUnit), xResolution);*/
		}

		return null;
	}

	private MetaValue getIsoSpeed()	{
		String iso = getStringMetadataItem(RawMetadataItemName.ExifISOSpeed);

		return (!StringUtils.isBlank(iso) ? new MetaValue(iso, iso) : null);
	}

	private MetaValue getLensAperture()	{
		// The aperture is the same as the F-Number if present; otherwise it is calculated from ExifAperture.
		MetadataItem rawMdi;
		String aperture = StringUtils.EMPTY;
		float apertureRaw = 0;

		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifFNumber)) != null){
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Fraction)
			{
				apertureRaw = ((Fraction)rawMdi.Value).ToSingle();
				aperture = apertureRaw.ToString("f/##0.#", CultureInfo.InvariantCulture);
			}*/
			aperture = rawMdi.getValue().toString();
			return new MetaValue(rawMdi.getValue().toString(), aperture);
		}

		if ((StringUtils.isBlank(aperture)) && ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifAperture)) != null))	{
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Fraction)
			{
				apertureRaw = ((Fraction)rawMdi.Value).ToSingle();
				var exifFNumber = (float)Math.Round(Math.Pow(Math.Sqrt(2), apertureRaw), 1);
				aperture = exifFNumber.ToString("f/##0.#", CultureInfo.InvariantCulture);
			}*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}

		return (!StringUtils.isBlank(aperture) ? new MetaValue(aperture, Float.toString(apertureRaw)) : null);
	}

	private MetaValue getLightSource(){
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifLightSource)) != null){
			if (rawMdi.getExtractedValueType() == ExtractedValueType.Int64)
			{
				/*var lightSource = (LightSource)(Int64)rawMdi.Value;
				if (MetadataEnumHelper.IsValidLightSource(lightSource))
				{
					// Don't bother with it if it is "Unknown"
					if (lightSource != LightSource.Unknown)
					{
						return new MetaValue(lightSource.getDescription(), ((ushort)lightSource).ToString(CultureInfo.InvariantCulture));
					}
				}*/
			}
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}
		return null;
	}

	private MetaValue getMeteringMode()	{
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifMeteringMode)) != null)	{
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Int64)
			{
				var meterMode = (MeteringMode)(Int64)rawMdi.Value;
				if (MetadataEnumHelper.IsValidMeteringMode(meterMode))
				{
					return new MetaValue(meterMode.ToString(), ((ushort)meterMode).ToString(CultureInfo.InvariantCulture));
				}
			}*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}
		
		return null;
	}

	private MetaValue getSubjectDistance(){
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifSubjectDist)) != null){
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Fraction)
			{
				double distance = ((Fraction)rawMdi.Value).ToSingle();

				if (distance > 1)
				{
					distance = Math.Round(distance, 1);
				}

				return new MetaValue(String.Concat(distance.ToString("0.### ", CultureInfo.InvariantCulture), Resources.Metadata_SubjectDistance_Units), distance.ToString("0.### ", CultureInfo.InvariantCulture));
			}
			else
			{
				String value = rawMdi.getValue().toString().Trim().TrimEnd(new[] { '\0' });

				if (!StringUtils.isBlank(value))
				{
					return new MetaValue(StringUtils.format(String.Concat("{0} ", Resources.Metadata_SubjectDistance_Units), value), value);
				}
			}*/
			return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
		}

		return null;
	}

	private MetaValue getVerticalResolution(){
		MetadataItem rawMdi;
		String resolutionUnit = StringUtils.EMPTY;

		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ResolutionYUnit)) != null){
			resolutionUnit = rawMdi.getValue().toString();
		}

		if ((StringUtils.isBlank(resolutionUnit)) && ((rawMdi = getRawMetadata().get(RawMetadataItemName.ResolutionUnit)) != null))	{
			/*if (rawMdi.getExtractedValueType() == ExtractedValueType.Int64)
			{
				ResolutionUnit resUnit = (ResolutionUnit)(Int64)rawMdi.Value;
				if (MetadataEnumHelper.IsValidResolutionUnit(resUnit))
				{
					resolutionUnit = resUnit.ToString();
				}
			}*/
		}

		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.YResolution)) != null){
			/*String yResolution;
			if (rawMdi.getExtractedValueType() == ExtractedValueType.Fraction)
			{
				yResolution = Math.Round(((Fraction)rawMdi.Value).ToSingle(), 2).ToString(CultureInfo.InvariantCulture);
			}
			else
			{
				yResolution = rawMdi.getValue().toString();
			}

			return new MetaValue(String.Concat(yResolution, " ", resolutionUnit), yResolution);*/
		}

		return null;
	}

	private MetaValue getWidth(){
		int width = getWidthAsInt();

		return (width > 0 ? new MetaValue(StringUtils.join(new Object[] {width, " ", "{metadata.width_Units}"}), Integer.toString(width)) : null);
	}

	private MetaValue getGpsValue(MetadataItemName metaName){
		switch (metaName){
			case GpsVersion:
				return (!StringUtils.isBlank(getGpsLocation().getVersion()) ? new MetaValue(getGpsLocation().getVersion(), getGpsLocation().getVersion()) : null);

			case GpsLocation:
				if ((getGpsLocation().getLatitude() != null) && (getGpsLocation().getLongitude() != null)){
					String loc = getGpsLocation().toLatitudeLongitudeDecimalString();
					return new MetaValue(loc, loc);
				}
				else
					return null;

			case GpsLatitude:
				if ((getGpsLocation().getLatitude() != null) && (getGpsLocation().getLongitude() != null)){
					String lat = MessageFormat.format("{0, number, #.000000}", getGpsLocation().getLatitude().toDouble()); //"F6", CultureInfo.InvariantCulture);
					return new MetaValue(lat, lat);
				}
				else
					return null;

			case GpsLongitude:
				if ((getGpsLocation().getLatitude() != null) && (getGpsLocation().getLongitude() != null)){
					String longitude = MessageFormat.format("{0, number, #.000000}", getGpsLocation().getLongitude().toDouble()); // gpsLocation.getLongitude().toDouble().toString("F6", CultureInfo.InvariantCulture);
					return new MetaValue(longitude, longitude);
				}
				else
					return null;

			case GpsAltitude:
				if (getGpsLocation().getAltitude() != null){
					String altitude = MessageFormat.format("{0, number, integer}", getGpsLocation().getAltitude()); //gpsLocation.getAltitude().getValue().toString("N0", CultureInfo.CurrentCulture);
					return new MetaValue(StringUtils.join(new String[] {altitude, " ", "{metadata.meters}"}), altitude);
				}
				else
					return null;

			case GpsDestLocation:
				if ((getGpsLocation().getDestLatitude() != null) && (getGpsLocation().getDestLongitude() != null)){
					String loc = getGpsLocation().toDestLatitudeLongitudeDecimalString();
					return new MetaValue(loc, loc);
				}
				else
					return null;

			case GpsDestLatitude:
				if ((getGpsLocation().getDestLatitude() != null) && (getGpsLocation().getDestLongitude() != null)){
					String lat = MessageFormat.format("{0, number, #.000000}", getGpsLocation().getDestLatitude().toDouble());// gpsLocation.getDestLatitude().toDouble().toString("F6", CultureInfo.InvariantCulture);
					return new MetaValue(lat, lat);
				}else
					return null;

			case GpsDestLongitude:
				if ((getGpsLocation().getDestLatitude() != null) && (getGpsLocation().getDestLongitude() != null)){
					String longitude = MessageFormat.format("{0, number, #.000000}", getGpsLocation().getDestLongitude().toDouble());// gpsLocation.getDestLongitude().toDouble().toString("F6", CultureInfo.InvariantCulture);
					return new MetaValue(longitude, longitude);
				}else
					return null;

			default:
				throw new ArgumentException(MessageFormat.format("The function getGpsValue() expects a GPS-related parameter; instead the value {0} was passed.", metaName), "metaName");
		}
	}

	private MetaValue getIptcValue(MetadataItemName metaName){
		String iptcValue = null;
		try	{
			Object objIptcValue = getWpfMetadata().getQuery(StringUtils.format(getIptcQueryFormatString(), getIptcQueryParameters().get(metaName)));
			if (objIptcValue != null)
				iptcValue = objIptcValue.toString();
		}catch (ArgumentNullException ex){
			// Some images throw this exception. When this happens, just exit.
			return null;
		}catch (UnsupportedOperationException ex){
			// Some images throw this exception. When this happens, just exit.
			return null;
		}

		if (StringUtils.isBlank(iptcValue))
			return null;

		String formattedIptcValue = iptcValue;

		// For dates, format to a specific pattern.
		if (metaName == MetadataItemName.IptcDateCreated){
			Date dateTaken = tryParseDate(iptcValue);

			if (dateTaken.getYear() > DateUtils.MinValue.getYear())
				formattedIptcValue = DateUtils.getDateTime(this.getDateTimeFormatString(), dateTaken);
		}

		return new MetaValue(formattedIptcValue, iptcValue);
	}

	//#endregion

	//#region Functions

	/// <summary>
	/// Fill the class-level _rawMetadata dictionary with MetadataItem objects created from the
	/// PropertyItems property of the image. Skip any items that are not defined in the 
	/// RawMetadataItemName enumeration. Guaranteed to not return null.
	/// </summary>
	private Map<RawMetadataItemName, MetadataItem> getRawMetadataDictionary(){
		Map<RawMetadataItemName, MetadataItem> rawMetadata = new HashMap<RawMetadataItemName, MetadataItem>();
		
		String filePath = getContentObject().getOriginal().getFileNamePhysicalPath();

		if (!StringUtils.isBlank(filePath))	{		
			com.drew.metadata.Metadata metadatas;
			try {
				metadatas = ImageMetadataReader.readMetadata(new File(filePath));
				Map<RawMetadataItemName, String> rawMetadataItems = initRawMetadataDictionary();
				for(RawMetadataItemName item :  rawMetadataItems.keySet()) {
					if (StringUtils.isNotBlank(rawMetadataItems.get(item))) {
						 boolean addMeta = false;
						 for (Directory directory : metadatas.getDirectories()) {  
							 for (Tag tag : directory.getTags()) {
								 if (tag.getTagName().equalsIgnoreCase(rawMetadataItems.get(item))) {
									//log.info(tag.toString());
									MetadataItem metadataItem = new MetadataItem(tag.getTagName(), tag.getDescription());
									if (metadataItem.getValue() != null)
										rawMetadata.put(item, metadataItem);
									addMeta = true;
								 	break;
								 }
							 }
							 if (addMeta)
								 break;
						 }
					}
				}
			} catch (ImageProcessingException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		//for(String itemIterator : getPropertyItems()){
			/*RawMetadataItemName metadataName = (RawMetadataItemName)itemIterator.Id;
			if (Enum.IsDefined(typeof(RawMetadataItemName), metadataName))
			{
				if (!rawMetadata.ContainsKey(metadataName))
				{
					var metadataItem = new MetadataItem(itemIterator);
					if (metadataItem.Value != null)
						rawMetadata.Add(metadataName, metadataItem);
				}
			}*/
		//}

		return rawMetadata;
	}

	/// <summary>
	/// Extract the property items of the specified image. Guaranteed to not return null.
	/// </summary>
	private String[] getImagePropertyItems()	{
		String filePath = getContentObject().getOriginal().getFileNamePhysicalPath();

		if (StringUtils.isBlank(filePath))
			return new String[0];

		/*if (AppSetting.Instance.AppTrustLevel == ApplicationTrustLevel.Full)
		{
			return getPropertyItemsUsingFullTrustTechnique(filePath);
		}
		else
		{
			return getPropertyItemsUsingLimitedTrustTechnique(filePath);
		}*/
		String[] propertyItems = getPropertyItemsUsingFullTrustTechnique(filePath);
		if (propertyItems == null) {
			propertyItems = getPropertyItemsUsingLimitedTrustTechnique(filePath);
		}
		
		//BitmapMetadata bmData = new BitmapMetadata();
		//bmData.getQuery(filePath);
		
		return propertyItems;
	}
	
	void displayMetadata(List<String> propertyItems, Node root) {
        displayMetadata(propertyItems, root, 0);
    }

    void displayMetadata(List<String> propertyItems, Node node, int level) {
        // print open tag of element
    	log.info("Current Item Name: " + node.getNodeName());
    	NamedNodeMap map = node.getAttributes();
        if (map != null) {
            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                if (StringUtils.isBlank(attr.getNodeName()))
                	continue;
                
                /*System.out.print(" " + attr.getNodeName() +
                                 "=\"" + attr.getNodeValue() + "\"");*/
                log.info(" " + attr.getNodeName() +
                                 "=\"" + attr.getNodeValue() + "\"");
            }
        }

        Node child = node.getFirstChild();
        if (child == null) {
            // no children, so close element and return
            //System.out.println("/>");
            return;
        }

        // children, so close current tag
        //System.out.println(">");
        while (child != null) {
            // print children recursively
            displayMetadata(propertyItems, child, level + 1);
            child = child.getNextSibling();
        }
    }


	private String[] getPropertyItemsUsingFullTrustTechnique(String imageFilePath){
		// This technique is fast but requires full trust. Can only be called when app is running under full trust.
		//if (AppSetting.Instance.AppTrustLevel != ApplicationTrustLevel.Full)
		//	throw new InvalidOperationException("The method ContentObjectMetadataExtractor.getPropertyItemsUsingFullTrustTechnique can only be called when the application is running under full trust. The application should have already checked for this before calling this method. The developer needs to modify the source code to fix this.");
		log.info("File name: " + imageFilePath);
		
		ImageInputStream iis = null;
		try {
			
            File file = new File( imageFilePath );
            iis = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            List<String> propertyItems = Lists.newArrayList();
            if (readers.hasNext()) {

                // pick the first available ImageReader
                ImageReader reader = readers.next();

                // attach source to the reader
                reader.setInput(iis, true);

                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);

                String[] names = metadata.getMetadataFormatNames();
                int length = names.length;
                for (int i = 0; i < length; i++) {
                    //System.out.println( "Format name: " + names[ i ] );
                    log.info("Format name: " + names[ i ]);
                    displayMetadata(propertyItems, metadata.getAsTree(names[i]));
                }
                
                return names;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }finally {
        	try {
        		if (iis != null)
        			iis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		return new String[0];


		/*using (Stream stream = new FileStream(imageFilePath, FileMode.Open, FileAccess.Read, FileShare.Read))
		{
			try
			{
				BufferImage 
				using (System.Drawing.Image image = System.Drawing.Image.FromStream(stream, true, false))
				{
					try
					{
						return image.PropertyItems;
					}
					catch (NotImplementedException)
					{
						// Some images, such as wmf, throw this exception. We'll make a note of it and set our field to an empty array.
						//if (!ex.Data.Contains("Metadata Extraction Error"))
						//{
						//	ex.Data.Add("Metadata Extraction Error", StringUtils.format("Cannot extract metadata from file \"{0}\".", imageFilePath));
						//}

						//logError(ex, ContentObject.GalleryId);
						return new PropertyItem[0];
					}
				}
			}
			catch (ArgumentException)
			{
				//if (!ex.Data.Contains("Metadata Extraction Error"))
				//{
				//	ex.Data.Add("Metadata Extraction Error", StringUtils.format("Cannot extract metadata from file \"{0}\".", imageFilePath));
				//}

				//logError(ex, ContentObject.GalleryId);
				return new PropertyItem[0];
			}
		}*/
	}

	private String[] getPropertyItemsUsingLimitedTrustTechnique(String imageFilePath)	{
		// This technique is not as fast as the one in the method getPropertyItemsUsingFullTrustTechnique() but in works in limited
		// trust environments.
		try
		{
			BufferedImage image = ImageIO.read(new File(imageFilePath));
			try
			{
				return image.getPropertyNames();
			}
			catch (NotImplementedException ex)
			{
				// Some images, such as wmf, throw this exception. We'll make a note of it and set our field to an empty array.
				logError(ex, contentObject.getGalleryId());
				return new String[0];
			}
		}
		catch (ArgumentException | IOException ex)
		{
			logError(ex, contentObject.getGalleryId());
			return new String[0];
		}
	}

	/// <summary>
	/// Get a reference to the <see cref="BitmapMetadata" /> object for this image file that contains 
	/// the metadata such as title, keywords, etc. Guaranteed to not return null. Returns an instance 
	/// of <see cref="NullObjects.NullWpfMetadata" /> if an actual <see cref="BitmapMetadata" /> object 
	/// is not available.
	/// </summary>
	/// <returns> Returns a reference to the BitmapMetadata object for this image file that contains 
	/// the metadata such as title, keywords, etc.</returns>
	/// <remarks>A BitmapDecoder object is created from the absolute filepath passed into the constructor. Through trial and
	/// error, the relevant metadata appears to be stored in the first frame in the BitmapDecoder property of the first frame
	/// of the root-level BitmapDecoder object. One might expect the Metadata property of the root-level BitmapDecoder object to
	/// contain the metadata, but it seems to always be null.</remarks>
	private WpfMetadata getBitmapMetadata(){
		// We can access the BitmapMetadata object in the WPF namespace only when the app is running 
		// in Full Trust. There is also a config setting that enables this functionality, so query 
		// that as well. (The config setting allows it to be disabled due to the reliability issues 
		// found with the WPF classes.)
		/*if ((AppSetting.Instance.AppTrustLevel < ApplicationTrustLevel.Full)
				|| (!Factory.LoadGallerySetting(ContentObject.GalleryId).ExtractMetadataUsingWpf))
		{
			return new NullWpfMetadata();
		}*/
		/*if (!CMUtils.loadGallerySetting(contentObject.getGalleryId()).getExtractMetadataUsingWpf()){
			return new NullWpfMetadata();
		}


		// Do not use the BitmapCacheOption.Default or None option, as it will hold a lock on the file until garbage collection. I discovered
		// this problem and it has been submitted to MS as a bug. See thread in the managed newsgroup:
		// http://www.microsoft.com/communities/newsgroups/en-us/default.aspx?dg=microsoft.public.dotnet.framework&tid=b694ada2-10c4-4999-81f8-97295eb024a9&cat=en_US_a4ab6128-1a11-4169-8005-1d640f3bd725&lang=en&cr=US&sloc=en-us&m=1&p=1
		// Also do not use BitmapCacheOption.OnLoad as suggested in the thread, as it causes the memory to not be released until 
		// eventually IIS crashes when you do things like synchronize 100 images.
		// BitmapCacheOption.OnDemand seems to be the only option that doesn't lock the file or crash IIS.
		// Update 2007-07-29: OnDemand seems to also lock the file. There is no good solution! Acckkk
		// Update 2007-08-04: After installing VS 2008 beta 2, which also installs .NET 2.0 SP1, I discovered that OnLoad no longer crashes IIS.
		// Update 2008-05-19: The Create method doesn't release the file lock when an exception occurs, such as when the file is a WMF. See:
		// http://www.microsoft.com/communities/newsgroups/en-us/default.aspx?dg=microsoft.public.dotnet.framework&tid=fe3fb82f-0191-40a3-b789-0602cc4445d3&cat=&lang=&cr=&sloc=&p=1
		// Bug submission: https://connect.microsoft.com/VisualStudio/feedback/ViewFeedback.aspx?FeedbackID=344914
		// The workaround is to use a different overload of Create that takes a FileStream.

		String filePath = contentObject.getOriginal().getFileNamePhysicalPath();

		if (StringUtils.isBlank(filePath))
			return new NullWpfMetadata();
		
		 Metadata metadata = ImageMetadataReader.readMetadata(file);  
		 for (Directory directory : metadata.getDirectories()) {  
			 for (Tag tag : directory.getTags()) {
			 }
		 }

		if (bitmapMetadata != null)
			return new WpfMetadata(bitmapMetadata);
		else
			return new NullWpfMetadata();*/
		return new NullWpfMetadata();
	}

	private static String convertStringCollectionToDelimitedString(List<String> StringCollection){
		if (StringCollection == null)
			return null;

		// If any of the entries is itself a comma-separated list, parse them. Remove any duplicates.
		List<String> Strings = Lists.newArrayList();

		for (String s : StringCollection){
			Strings.addAll(Lists.newArrayList(StringUtils.split(s, ",")));
		}

		return StringUtils.join(Strings.stream().filter(s->!s.isEmpty()).map(s->s.trim()).distinct().collect(Collectors.toList()), ", ");
	}

	private static void logError(Exception ex, long galleryId){
		//todo
		/*EventLogController.RecordError(ex, AppSetting.Instance, galleryId, Factory.LoadGallerySettings());
		HelperFunctions.PurgeCache();*/
	}
	
	private String getStringMetadataItem(RawMetadataItemName sourceRawMetadataName) {
		return getStringMetadataItem(sourceRawMetadataName, "{0}");
	}

	private String getStringMetadataItem(RawMetadataItemName sourceRawMetadataName, String formatString){
		if (formatString == null)
			formatString = "{0}";
			
		MetadataItem rawMdi = getRawMetadata().get(sourceRawMetadataName);
		String rawValue = null;

		if (rawMdi != null)	{
			String unformattedValue = StringUtils.stripEnd(rawMdi.getValue().toString().trim(), "\0");

			rawValue = StringUtils.format(formatString, unformattedValue);
		}

		return rawValue;
	}

	/// <summary>
	/// Try to convert <paramref name="dteRaw" /> to a valid <see cref="Date" /> object. If it cannot be converted, return
	/// <see cref="Date.MinValue" />.
	/// </summary>
	/// <param name="dteRaw">The String containing the date/time to convert.</param>
	/// <returns>Returns a <see cref="Date" /> instance.</returns>
	/// <remarks>The IPTC specs do not define an exact format for the ITPC Date Created field, so it is unclear how to reliably parse
	/// it. However, an analysis of sample photos, including those provided by IPTC (http://www.iptc.org), show that the format
	/// yyyyMMdd is consistently used, so we'll try that if the more generic parsing doesnt work.</remarks>
	private static Date tryParseDate(String dteRaw)	{
		Date result;
		if ((result = DateUtils.parseDate(dteRaw)) != null){
			return result;
		}else if ((result = DateUtils.parseDate(dteRaw, "yyyyMMdd")) != null)	{
			return result;
		}

		return DateUtils.MinValue;
	}

	/// <summary>
	/// Convert an EXIF-formatted timestamp to the .NET DateTime type. Returns DateTime.MinValue when the date cannot be parsed.
	/// </summary>
	/// <param name="exifDateTime">An EXIF-formatted timestamp. The format is YYYY:MM:DD HH:MM:SS with time shown 
	/// in 24-hour format and the date and time separated by one blank character (0x2000). The character 
	/// String length is 20 bytes including the NULL terminator.</param>
	/// <returns>Returns the EXIF-formatted timestamp as a .NET DateTime type.</returns>
	private static Date convertExifDateTimeToDateTime(String exifDateTime){
		Date convertedDateTimeValue = DateUtils.MinValue;
		final int minCharsReqdToSpecifyDate = 10; // Need at least 10 characters to specify a date (e.g. 2010:10:15)

		if (StringUtils.isBlank(exifDateTime) || (exifDateTime.trim().length() < minCharsReqdToSpecifyDate))
			return convertedDateTimeValue; // No date/time is present; just return

		exifDateTime = exifDateTime.trim();

		//String[] ymdhms = StringUtils.split(exifDateTime, " :");
		String[] ymdhms = exifDateTime.split(" |:");
		

		// Default to lowest possible year, first month and first day
		int year = DateUtils.MinValue.getYear(), month = 1, day = 1, hour = 0, minute = 0, second = 0;
		if (ymdhms.length >= 2)	{
			year = NumberUtils.toInt(ymdhms[0]);
			month = NumberUtils.toInt(ymdhms[1]);
			day = NumberUtils.toInt(ymdhms[2]);
		}

		if (ymdhms.length >= 6)	{
			// The hour, minute and second will default to 0 if it can't be parsed, which is good.
			hour = NumberUtils.toInt(ymdhms[3]);
			minute = NumberUtils.toInt(ymdhms[4]);
			second = NumberUtils.toInt(ymdhms[5]);
		}
		if (year > DateUtils.MinValue.getYear()){
			convertedDateTimeValue = new GregorianCalendar(year, month, day, hour, minute, second).getTime();
		}

		return convertedDateTimeValue;
	}

	private MetaValue getDatePictureTakenWpf(){
		try
		{
			String dateTakenRaw = getWpfMetadata().getDateTaken();
			if (!StringUtils.isBlank(dateTakenRaw))	{
				Date dateTaken = tryParseDate(dateTakenRaw);
				if (dateTaken.getYear() > DateUtils.MinValue.getYear()){
					return new MetaValue(DateUtils.getDateTime(getDateTimeFormatString(), dateTaken), dateTaken.toString()); //.toString("O")
				}
				else
					return new MetaValue(dateTakenRaw, dateTakenRaw); // We can't parse it so just return it as is
			}
		}
		catch (NotSupportedException ex) { } // Some image types, such as png, throw a NotSupportedException. Let's swallow them and move on.
		catch (ArgumentException ex ) { }
		catch (UnsupportedOperationException ex) { }

		return null;
	}

	private MetaValue getDatePictureTakenGdi(){
		MetadataItem rawMdi;
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifDTOrig)) != null)	{
			Date convertedDateTimeValue = convertExifDateTimeToDateTime(rawMdi.getValue().toString());
			if (convertedDateTimeValue.after(DateUtils.MinValue)){
				return new MetaValue(DateUtils.getDateTime( getDateTimeFormatString(), convertedDateTimeValue), convertedDateTimeValue.toString()); //"O", CultureInfo.InvariantCulture
			}else if (!StringUtils.isBlank(rawMdi.getValue().toString())){
				return new MetaValue(rawMdi.getValue().toString(), rawMdi.getValue().toString());
			}
		}

		return null;
	}

	/// <summary>
	/// Get the height of the content object. Extracted from RawMetadataItemName.ExifPixXDim for compressed images and
	/// from RawMetadataItemName.ImageHeight for uncompressed images. The value is stored in a private class level variable
	/// for quicker subsequent access.
	/// </summary>
	/// <returns>Returns the height of the content object.</returns>
	private int getWidthAsInt()	{
		if (this.width > 0)
			return this.width;

		MetadataItem rawMdi;
		int width = Integer.MIN_VALUE;
		boolean foundWidth = false;

		// Compressed images store their width in ExifPixXDim. Uncompressed images store their width in ImageWidth.
		// First look in ExifPixXDim since most images are likely to be compressed ones. If we don't find that one,
		// look for ImageWidth. If we don't find that one either (which should be unlikely to ever happen), then just give 
		// up and return null.
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifPixXDim)) != null){
			foundWidth = ((width = StringUtils.toInt(rawMdi.getValue().toString())) != Integer.MIN_VALUE);
		}

		if ((!foundWidth) && ((rawMdi = getRawMetadata().get(RawMetadataItemName.ImageWidth)) != null)){
			foundWidth = ((width = StringUtils.toInt(rawMdi.getValue().toString())) != Integer.MIN_VALUE);
		}

		if (!foundWidth){
			width = this.contentObject.getOriginal().getWidth();
			foundWidth = (width > 0);
		}

		if (foundWidth)
			this.width = width;

		return width;
	}

	/// <summary>
	/// Get the width of the content object. Extracted from RawMetadataItemName.ExifPixYDim for compressed images and
	/// from RawMetadataItemName.ImageWidth for uncompressed images. The value is stored in a private class level variable
	/// for quicker subsequent access.
	/// </summary>
	/// <returns>Returns the width of the content object.</returns>
	private int getHeightAsInt()	{
		if (this.height > 0)
			return this.height;

		MetadataItem rawMdi;
		int height = Integer.MIN_VALUE;
		boolean foundHeight = false;

		// Compressed images store their width in ExifPixXDim. Uncompressed images store their width in ImageWidth.
		// First look in ExifPixXDim since most images are likely to be compressed ones. If we don't find that one,
		// look for ImageWidth. If we don't find that one either (which should be unlikely to ever happen), then just give 
		// up and return null.
		if ((rawMdi = getRawMetadata().get(RawMetadataItemName.ExifPixYDim)) != null){
			foundHeight = ((height = StringUtils.toInt(rawMdi.getValue().toString())) != Integer.MIN_VALUE);
		}

		if ((!foundHeight) && ((rawMdi = getRawMetadata().get(RawMetadataItemName.ImageHeight)) != null)){
			foundHeight = ((height = StringUtils.toInt(rawMdi.getValue().toString())) != Integer.MIN_VALUE);
		}

		if (!foundHeight){
			height = this.contentObject.getOriginal().getHeight();
			foundHeight = (height > 0);
		}

		if (foundHeight)
			this.height = height;

		return height;
	}

	/// <summary>
	/// Persists the meta value.
	/// </summary>
	/// <param name="metaName">Name of the meta.</param>
	/// <param name="persistAction">The persist action.</param>
	private void persistMetaValue(MetadataItemName metaName, MetaPersistAction persistAction)	{
		if (!getUpdatableMetaItems().containsKey(metaName)){
			//EventLogController.RecordEvent(String.Format("This version of MDS System does not support modifying the meta value {0} in the original file. The request to save or delete the meta value was ignored.", metaName));
			return;
		}

		synchronized (_sharedLock)
		{
			boolean isSuccessful = false;
			String filePath = contentObject.getOriginal().getFileNamePhysicalPath();

			/*Stream savedFile = File.Open(filePath, FileMode.Open, FileAccess.ReadWrite);
			{
				var output = BitmapDecoder.Create(savedFile, BitmapCreateOptions.None, BitmapCacheOption.Default);
				var bitmapMetadata = output.Frames[0].CreateInPlaceBitmapMetadataWriter();

				if (bitmapMetadata != null)
				{
					setMetadata(bitmapMetadata, metaName, persistAction);

					if (bitmapMetadata.TrySave())
					{
						isSuccessful = true;
					}
				}
			}*/

			// If the save wasn't successful, try to save another way.
			if (!isSuccessful){
				String tmpFilePath = FilenameUtils.concat(AppSettings.getInstance().getTempUploadDirectory(), UUID.randomUUID().toString().concat(".tmp"));

				try {
					tryAlternateMethodsOfPersistingMetadata(filePath, tmpFilePath, metaName, persistAction);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				replaceFileSafely(tmpFilePath, filePath);
			}
		}
	}

	private void setMetadata(BitmapMetadata bitmapMetadata, MetadataItemName metaName, MetaPersistAction persistAction)	{
		if (!getUpdatableMetaItems().containsKey(metaName)){
			throw new ArgumentException(StringUtils.format("This function does not support persisting the meta item {0}.", metaName));
		}

		switch (metaName)
		{
			case Orientation:
				setOrientationMetadata(bitmapMetadata, metaName, persistAction);
				break;

			default:
				throw new InvalidEnumArgumentException(StringUtils.format("This function is not designed to handle the enumeration value {0}. The function must be updated.", metaName));
		}


		//if (caption != null)
		//{
		//	bitmapMetadata.Comment = caption;
		//}

		//if (dateTaken.HasValue)
		//{
		//	bitmapMetadata.DateTaken = dateTaken.getValue().toString("M/d/yyyy HH:mm:ss");
		//	bitmapMetadata.SetQuery(DATE_TAKEN_QUERY, dateTaken.getValue().toString("yyyy:MM:dd HH:mm:ss"));
		//	bitmapMetadata.SetQuery(DIGITIZED_DATE_QUERY, dateTaken.getValue().toString("yyyy:MM:dd HH:mm:ss"));
		//	bitmapMetadata.SetQuery(ORIGINAL_DATE_QUERY, dateTaken.getValue().toString("yyyy:MM:dd HH:mm:ss"));
		//}

		////-----------tags----------------------
		//List<String> tagsList = new List<String>();

		//foreach (String tag in tags)
		//{
		//	if (tag.length() > 0)
		//		tagsList.Add(tag);
		//}

		//if (tagsList.Count == 0)
		//	tagsList.Add("");

		////XMP
		//bitmapMetadata.Keywords = new System.Collections.ObjectModel.ReadOnlyCollection<String>(tagsList);

		////IPTC
		//String[] iptcTagsList = tagsList.ToArray();
		//bitmapMetadata.SetQuery(IPTC_KEYWORDS_QUERY, iptcTagsList);
		////-----------tags----------------------

	}

	private void setOrientationMetadata(BitmapMetadata bitmapMetadata, MetadataItemName metaName, MetaPersistAction persistAction){
		switch (persistAction){
			case Delete:
				bitmapMetadata.removeQuery(getUpdatableMetaItems().get(metaName));
				break;

			case Save:
				ContentObjectMetadataItem orientationMeta;
				if ((orientationMeta = contentObject.getMetadataItems().tryGetMetadataItem(metaName)) != null)				{
					int orientationRaw;
					if ((orientationRaw = NumberUtils.toInt(orientationMeta.getRawValue(), Integer.MIN_VALUE)) != Integer.MIN_VALUE  
							&& MetadataEnumHelper.isValidOrientation(Orientation.getOrientation(orientationRaw))){
						bitmapMetadata.setQuery(getUpdatableMetaItems().get(metaName), orientationRaw);
					}
				}
				break;

			default:
				throw new InvalidEnumArgumentException(StringUtils.format("This function is not designed to handle the enumeration value {0}. The function must be updated.", persistAction));
		}
	}
	
	 private void setOrientation(IIOMetadata metadata, int orientationRaw) throws IIOInvalidTreeException {  
	  
		// for PMG, it's dots per millimeter  
		/*double dotsPerMilli = 1.0 * DPI / 10 / INCH_2_CM;  
		    
	    IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");  
	    horiz.setAttribute("value", Double.toString(dotsPerMilli));  
	  
	    IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");  
	    vert.setAttribute("value", Double.toString(dotsPerMilli));*/
	    
	    IIOMetadataNode orientation = new IIOMetadataNode("ImageOrientation");  
	    orientation.setAttribute("value", Integer.toString(orientationRaw));
	  
	    IIOMetadataNode dim = new IIOMetadataNode("Dimension");  
	    dim.appendChild(orientation);  
	  
	    IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");  
	    root.appendChild(dim);  
	  
	    metadata.mergeTree("javax_imageio_1.0", root);  
	 } 
	
	private void tryAlternateMethodsOfPersistingMetadata(String sourceFileName, String outputFileName, MetadataItemName metaName, MetaPersistAction persistAction) throws IOException{
		// Three alternate attempts to persist the metadata:
		// 1. Use outputFileName parameter and a cloned copy of the file's metadata
		// 2. Use outputFileName parameter and the original file's metadata
		// 3. Rename the file and try again using a cloned copy of the file's metadata
		// Adapted from: https://code.google.com/p/flickrmetasync/source/browse/trunk/FlickrMetadataSync/Picture.cs?spec=svn29&r=29
		boolean tryOneLastMethod = false;

		File file = new File(sourceFileName);
	    ImageInputStream stream = ImageIO.createImageInputStream(file);
	    Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
	    if(readers.hasNext()) {
	        ImageReader reader = readers.next();
	        reader.setInput(stream);
	        IIOMetadata metadata = reader.getImageMetadata(0);

	        IIOMetadataNode standardTree = (IIOMetadataNode)metadata.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
	        IIOMetadataNode colorSpaceType = (IIOMetadataNode)standardTree.getElementsByTagName("ColorSpaceType").item(0);
	        String colorSpaceName = colorSpaceType.getAttribute("name");
	        if(colorSpaceName.equals("RGB"))
	            throw new RuntimeException("Identified incorrect ColorSpace");
	    }


	    
	    /*ImageDecoder dec = ImageCodec.createImageDecoder(stream, null);
	    		
		Stream originalFile = new FileStream(sourceFileName, FileMode.Open, FileAccess.Read, FileShare.Read);
		{
			final BitmapCreateOptions createOptions = BitmapCreateOptions.PreservePixelFormat | BitmapCreateOptions.IgnoreColorProfile;
			BitmapDecoder original = BitmapDecoder.Create(originalFile, createOptions, BitmapCacheOption.None);

			var output = new JpegBitmapEncoder();

			if (original.Frames[0] != null && original.Frames[0].Metadata != null)
			{
				BitmapMetadata bitmapMetadata = original.Frames[0].Metadata.Clone() as BitmapMetadata;
				bitmapMetadata.SetQuery("/app1/ifd/PaddingSchema:Padding", MetadataPaddingInBytes);
				bitmapMetadata.SetQuery("/app1/ifd/exif/PaddingSchema:Padding", MetadataPaddingInBytes);
				bitmapMetadata.SetQuery("/xmp/PaddingSchema:Padding", MetadataPaddingInBytes);

				setMetadata(bitmapMetadata, metaName, persistAction);

				output.Frames.Add(BitmapFrame.Create(original.Frames[0], original.Frames[0].Thumbnail, bitmapMetadata, original.Frames[0].ColorContexts));
			}

			try
			{
				Stream outputFile = File.Open(outputFileName, FileMode.Create, FileAccess.ReadWrite);
				{
					output.Save(outputFile);
				}
			}
			catch (Exception e) //System.Exception, NotSupportedException, InvalidOperationException, ArgumentException
			{
				if (e is NotSupportedException || e is ArgumentException)
				{
					output = new JpegBitmapEncoder();

					output.Frames.Add(BitmapFrame.Create(original.Frames[0], original.Frames[0].Thumbnail, original.Metadata, original.Frames[0].ColorContexts));

					using (Stream outputFile = File.Open(outputFileName, FileMode.Create, FileAccess.ReadWrite))
					{
						output.Save(outputFile);
					}

					tryOneLastMethod = true;
				}
				else
				{
					throw new Exception("Error saving picture.", e);
				}
			}
		}

		if (tryOneLastMethod)
		{
			File.Move(outputFileName, outputFileName + "tmp");

			using (Stream recentlyOutputFile = new FileStream(outputFileName + "tmp", FileMode.Open, FileAccess.Read, FileShare.Read))
			{
				const BitmapCreateOptions createOptions = BitmapCreateOptions.PreservePixelFormat | BitmapCreateOptions.IgnoreColorProfile;
				BitmapDecoder original = BitmapDecoder.Create(recentlyOutputFile, createOptions, BitmapCacheOption.None);
				JpegBitmapEncoder output = new JpegBitmapEncoder();
				if (original.Frames[0] != null && original.Frames[0].Metadata != null)
				{
					BitmapMetadata bitmapMetadata = original.Frames[0].Metadata.Clone() as BitmapMetadata;
					bitmapMetadata.SetQuery("/app1/ifd/PaddingSchema:Padding", MetadataPaddingInBytes);
					bitmapMetadata.SetQuery("/app1/ifd/exif/PaddingSchema:Padding", MetadataPaddingInBytes);
					bitmapMetadata.SetQuery("/xmp/PaddingSchema:Padding", MetadataPaddingInBytes);

					SetMetadata(bitmapMetadata, metaName, persistAction);

					output.Frames.Add(BitmapFrame.Create(original.Frames[0], original.Frames[0].Thumbnail, bitmapMetadata, original.Frames[0].ColorContexts));
				}

				using (Stream outputFile = File.Open(outputFileName, FileMode.Create, FileAccess.ReadWrite))
				{
					output.Save(outputFile);
				}
			}
			File.Delete(outputFileName + "tmp");
		}*/
	}

	/// <summary>
	/// Replaces the <paramref name="destFilePath" /> with <paramref name="sourceFilePath" />. No action - or errors - are thrown
	/// if either file does not exist.
	/// </summary>
	/// <param name="sourceFilePath">The source file path.</param>
	/// <param name="destFilePath">The destination file path.</param>
	/// <returns><c>true</c> if <paramref name="sourceFilePath" /> is successfully moved to <paramref name="destFilePath" />,
	/// <c>false</c> otherwise.</returns>
	private static boolean replaceFileSafely(String sourceFilePath, String destFilePath){
		File sourceFile = new File(sourceFilePath);
		File destFile = new File(destFilePath);
		if (destFile.exists() && sourceFile.exists()){
			destFile.delete();
		}

		if (sourceFile.exists()){
			try {
				FileUtils.moveFile(sourceFile, destFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		return false;
	}

	//#endregion
}