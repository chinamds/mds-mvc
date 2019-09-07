package com.mds.core;

import com.mds.util.StringUtils;


/// <summary>
/// Specifies the Microsoft Windows GDI+ image property tags.
/// </summary>	
/// <remarks>The enumeration members and their comments taken from MSDN documentation at
/// <see cref="http://msdn.microsoft.com/en-us/library/ms534417(VS.85).aspx" /></remarks>
public enum RawMetadataItemName{
	///<summary>Null-terminated character string that specifies the name of the person who created the image. (0x013B, 315)</summary>
	Artist(315),
	///<summary>Number of bits per color component. See also SamplesPerPixel.</summary>
	BitsPerSample(258),
	///<summary>Height of the dithering or halftoning matrix.</summary>
	CellHeight(265),
	///<summary>Width of the dithering or halftoning matrix.</summary>
	CellWidth(264),
	///<summary>Chrominance table. The luminance table and the chrominance table are used to control JPEG quality. 
	///A valid luminance or chrominance table has 64 entries of type PropertyTagTypeShort. If an image has 
	///either a luminance table or a chrominance table, then it must have both tables.</summary>
	ChrominanceTable(20625),
	///<summary>Color palette (lookup table) for a palette-indexed image.</summary>
	ColorMap(320),
	///<summary>Table of values that specify color transfer functions.</summary>
	ColorTransferFunction(20506),
	///<summary>Compression scheme used for the image data.</summary>
	Compression(259),
	///<summary>Null-terminated character string that contains copyright information.</summary>
	Copyright(33432),
	///<summary>Date and time the image was created.</summary>
	DateTime(306),
	///<summary>Null-terminated character string that specifies the name of the document from which the image 
	///was scanned.</summary>
	DocumentName(269),
	///<summary>Color component values that correspond to a 0 percent dot and a 100 percent dot.</summary>
	DotRange(336),
	///<summary>Null-terminated character string that specifies the manufacturer of the equipment used to 
	///record the image.</summary>
	EquipMake(271),
	///<summary>Null-terminated character string that specifies the model name or model number of the 
	///equipment used to record the image.</summary>
	EquipModel(272),
	///<summary>Lens aperture. The unit is the APEX value.</summary>
	ExifAperture(37378),
	///<summary>Brightness value. The unit is the APEX value. Ordinarily it is given in the range of 
	///-99.99 to 99.99.</summary>
	ExifBrightness(37379),
	///<summary>The color filter array (CFA) geometric pattern of the image sensor when a one-chip color area sensor 
	///is used. It does not apply to all sensing methods.</summary>
	ExifCfaPattern(41730),
	///<summary>Color space specifier. Normally sRGB (=1) is used to define the color space based on the PC monitor 
	///conditions and environment. If a color space other than sRGB is used, Uncalibrated (=65535) is set. Image 
	///data recorded as Uncalibrated can be treated as sRGB when it is converted to FlashPix.</summary>
	ExifColorSpace(40961),
	///<summary>Information specific to compressed data. The compression mode used for a compressed image is
	///indicated in unit BPP.</summary>
	ExifCompBPP(37122),
	///<summary>Information specific to compressed data. The channels of each component are arranged in order from 
	///the first component to the fourth. For uncompressed data, the data arrangement is given in the 
	///PropertyTagPhotometricInterp tag. However, because PropertyTagPhotometricInterp can only express the 
	///order of Y, Cb, and Cr, this tag is provided for cases when compressed data uses components other than Y, 
	///Cb, and Cr and to support other sequences.</summary>
	ExifCompConfig(37121),
	///<summary>Date and time when the image was stored as digital data. If, for example, an image was captured 
	///by DSC and at the same time the file was recorded, then DateTimeOriginal and DateTimeDigitized will have 
	///the same contents. The format is YYYY:MM:DD HH:MM:SS with time shown in 24-hour format and the date and 
	///time separated by one blank character (0x2000). The character string length is 20 bytes including the 
	///NULL terminator. When the field is empty, it is treated as unknown.</summary>
	ExifDTDigitized(36868),
	///<summary>Null-terminated character string that specifies a fraction of a second for the 
	///PropertyTagExifDTDigitized tag.</summary>
	ExifDTDigSS(37522),
	///<summary>Date and time when the original image data was generated. For a DSC, the date and time when the 
	///picture was taken. The format is YYYY:MM:DD HH:MM:SS with time shown in 24-hour format and the date and
	///time separated by one blank character (0x2000). The character string length is 20 bytes including the 
	///NULL terminator. When the field is empty, it is treated as unknown.</summary>
	ExifDTOrig(36867),
	///<summary>Null-terminated character string that specifies a fraction of a second for the 
	///PropertyTagExifDTOrig tag.</summary>
	ExifDTOrigSS(37521),
	///<summary>Null-terminated character string that specifies a fraction of a second for the PropertyTagDateTime tag.</summary>
	ExifDTSubsec(37520),
	///<summary>Exposure bias. The unit is the APEX value. Ordinarily it is given in the range -99.99 to 99.99.</summary>
	ExifExposureBias(37380),
	///<summary>Exposure index selected on the camera or input device at the time the image was captured.</summary>
	ExifExposureIndex(41493),
	///<summary>Class of the program used by the camera to set exposure when the picture is taken. The value
	///is an integer with these values: 0 - not defined; 1 - manual; 2 - normal program; 3 - aperture priority;
	///4 - shutter priority; 5 - creative program (biased toward depth of field); 6 - action program (biased
	///toward fast shutter speed); 7 - portrait mode (for close-up photos with the background out of focus);
	///8 - landscape mode (for landscape photos with the background in focus); 9 to 255 - reserved</summary>
	ExifExposureProg(34850),
	///<summary>Exposure time, measured in seconds.</summary>
	ExifExposureTime(33434),
	///<summary>The image source. If a DSC recorded the image, the value of this tag is 3.</summary>
	ExifFileSource(41728),
	///<summary>Flash status. This tag is recorded when an image is taken using a strobe light (flash). 
	///Bit 0 indicates the flash firing status (0b - flash did not fire 1b - flash fired), and bits 1 and 2 
	///indicate the flash return status (00b - no strobe return detection function 01b - reserved 10b - strobe 
	///return light not detected 11b - strobe return light detected). Resulting flash tag values: 0x0000 - flash 
	///did not fire; 0x0001 - flash fired; 0x0005 - strobe return light not detected</summary>
	ExifFlash(37385),
	///<summary>Strobe energy, in Beam Candle Power Seconds (BCPS), at the time the image was captured.</summary>
	ExifFlashEnergy(41483),
	///<summary>F number.</summary>
	ExifFNumber(33437),
	///<summary>Actual focal length, in millimeters, of the lens. Conversion is not made to the focal length
	///of a 35 millimeter film camera.</summary>
	ExifFocalLength(37386),
	///<summary>Unit of measure for PropertyTagExifFocalXRes and PropertyTagExifFocalYRes.</summary>
	ExifFocalResUnit(41488),
	///<summary>Number of pixels in the image width (x) direction per unit on the camera focal plane. The unit is 
	///specified in PropertyTagExifFocalResUnit.</summary>
	ExifFocalXRes(41486),
	///<summary>Number of pixels in the image height (y) direction per unit on the camera focal plane. The unit is
	///specified in PropertyTagExifFocalResUnit.</summary>
	ExifFocalYRes(41487),
	///<summary>FlashPix format version supported by an FPXR file. If the FPXR function supports FlashPix format 
	///version 1.0, this is indicated similarly to PropertyTagExifVer by recording 0100 as a 4-byte ASCII string. 
	///Because the type is PropertyTagTypeUndefined, there is no NULL terminator.</summary>
	ExifFPXVer(40960),
	///<summary>Private tag used by GDI+. Not for public use. GDI+ uses this tag to locate Exif-specific 
	///information.</summary>
	ExifIFD(34665),
	///<summary>Offset to a block of property items that contain interoperability information.</summary>
	ExifInterop(40965),
	///<summary>ISO speed and ISO latitude of the camera or input device as specified in ISO 12232.</summary>
	ExifISOSpeed(34855),
	///<summary>Type of light source. This is an integer with these values: 0 - unknown; 1 - Daylight;
	///2 - Flourescent; 3 - Tungsten; 17 - Standard Light A; 18 - Standard Light B; 19 - Standard Light C;
	///20 - D55; 21 - D65; 22 - D75; 23 to 254 - reserved; 255 - other</summary>
	ExifLightSource(37384),
	///<summary>Note tag. A tag used by manufacturers of EXIF writers to record information. The contents are 
	///up to the manufacturer.</summary>
	ExifMakerNote(37500),
	///<summary>Smallest F number of the lens. The unit is the APEX value. Ordinarily it is given in the range 
	///of 00.00 to 99.99, but it is not limited to this range.</summary>
	ExifMaxAperture(37381),
	///<summary>Metering mode. This is an integer with these values: 0 - unknown; 1 - Average; 2 - 
	///CenterWeightedAverage; 3 - Spot; 4 - MultiSpot; 5 - Pattern; 6 - Partial; 7 to 254 - reserved;
	///255 - other</summary>
	ExifMeteringMode(37383),
	///<summary>Optoelectronic conversion function (OECF) specified in ISO 14524. The OECF is the relationship 
	///between the camera optical input and the image values.</summary>
	ExifOECF(34856),
	///<summary>Information specific to compressed data. When a compressed file is recorded, the valid width of the 
	///meaningful image must be recorded in this tag, whether or not there is padding data or a restart marker. 
	///This tag should not exist in an uncompressed file.</summary>
	ExifPixXDim(40962),
	///<summary>Information specific to compressed data. When a compressed file is recorded, the valid height of the 
	///meaningful image must be recorded in this tag whether or not there is padding data or a restart marker. 
	///This tag should not exist in an uncompressed file. Because data padding is unnecessary in the vertical 
	///direction, the number of lines recorded in this valid image height tag will be the same as that recorded 
	///in the SOF.</summary>
	ExifPixYDim(40963),
	///<summary>The name of an audio file related to the image data. The only relational information recorded is 
	///the EXIF audio file name and extension (an ASCII string that consists of 8 characters plus a period (.), 
	///plus 3 characters). The path is not recorded. When you use this tag, audio files must be recorded in 
	///conformance with the EXIF audio format. Writers can also store audio data within APP2 as FlashPix extension 
	///stream data.</summary>
	ExifRelatedWav(40964),
	///<summary>The type of scene. If a DSC recorded the image, the value of this tag must be set to 1, indicating 
	///that the image was directly photographed.</summary>
	ExifSceneType(41729),
	///<summary>Image sensor type on the camera or input device. This is an integer with these values:
	///1 - not defined; 2 - one-chip color area sensor; 3 - two-chip color area sensor; 4 - three-chip color area 
	///sensor; 5 - color sequential area sensor; 7 - trilinear sensor; 8 - color sequential linear sensor;
	///Other - reserved</summary>
	ExifSensingMethod(41495),
	///<summary>Shutter speed. The unit is the Additive System of Photographic Exposure (APEX) value.</summary>
	ExifShutterSpeed(37377),
	///<summary>Camera or input device spatial frequency table and SFR values in the image width, image height, and 
	///diagonal direction, as specified in ISO 12233.</summary>
	ExifSpatialFR(41484),
	///<summary>Null-terminated character string that specifies the spectral sensitivity of each channel of the 
	///camera used. The string is compatible with the standard developed by the ASTM Technical Committee.</summary>
	ExifSpectralSense(34852),
	///<summary>Distance to the subject, measured in meters.</summary>
	ExifSubjectDist(37382),
	///<summary>Location of the main subject in the scene. The value of this tag represents the pixel at the center 
	///of the main subject relative to the left edge. The first value indicates the column number, and the second 
	///value indicates the row number.</summary>
	ExifSubjectLoc(41492),
	///<summary>Comment tag. A tag used by EXIF users to write keywords or comments about the image besides those 
	///in PropertyTagImageDescription and without the character-code limitations of the 
	///PropertyTagImageDescription tag.</summary>
	ExifUserComment(37510),
	///<summary>Version of the EXIF standard supported. Nonexistence of this field is taken to mean nonconformance 
	///to the standard. Conformance to the standard is indicated by recording 0210 as a 4-byte ASCII string. 
	///Because the type is PropertyTagTypeUndefined, there is no NULL terminator.</summary>
	ExifVer(36864),
	///<summary>Number of extra color components. For example, one extra component might hold an alpha value.</summary>
	ExtraSamples(338),
	///<summary>Logical order of bits in a byte.</summary>
	FillOrder(266),
	///<summary>Time delay, in hundredths of a second, between two frames in an animated GIF image.</summary>
	FrameDelay(20736),
	///<summary>For each string of contiguous unused bytes, the number of bytes in that string.</summary>
	FreeByteCounts(289),
	///<summary>For each string of contiguous unused bytes, the byte offset of that string.</summary>
	FreeOffset(288),
	///<summary>Gamma value attached to the image. The gamma value is stored as a rational number (pair of long) 
	///with a numerator of 100000. For example, a gamma value of 2.2 is stored as the pair (100000, 45455).</summary>
	Gamma(769),
	///<summary>Color palette for an indexed bitmap in a GIF image.</summary>
	GlobalPalette(20738),
	///<summary>Altitude, in meters, based on the reference altitude specified by PropertyTagGpsAltitudeRef.</summary>
	GpsAltitude(6),
	///<summary>Reference altitude, in meters.</summary>
	GpsAltitudeRef(5),
	///<summary>Bearing to the destination point. The range of values is from 0.00 to 359.99.</summary>
	GpsDestBear(24),
	///<summary>Null-terminated character string that specifies the reference used for giving the bearing to the 
	///destination point. T specifies true direction, and M specifies magnetic direction.</summary>
	GpsDestBearRef(23),
	///<summary>Distance to the destination point.</summary>
	GpsDestDist(26),
	///<summary>Null-terminated character string that specifies the unit used to express the distance to the 
	///destination point. K, M, and N represent kilometers, miles, and knots respectively.</summary>
	GpsDestDistRef(25),
	///<summary>Latitude of the destination point. The latitude is expressed as three rational values giving the 
	///degrees, minutes, and seconds respectively. When degrees, minutes, and seconds are expressed, the format 
	///is dd/1, mm/1, ss/1. When degrees and minutes are used and, for example, fractions of minutes are given 
	///up to two decimal places, the format is dd/1, mmmm/100, 0/1.</summary>
	GpsDestLat(20),
	///<summary>Null-terminated character string that specifies whether the latitude of the destination point 
	///is north or south latitude. N specifies north latitude, and S specifies south latitude.</summary>
	GpsDestLatRef(19),
	///<summary>Longitude of the destination point. The longitude is expressed as three rational values giving 
	///the degrees, minutes, and seconds respectively. When degrees, minutes, and seconds are expressed, the 
	///format is ddd/1, mm/1, ss/1. When degrees and minutes are used and, for example, fractions of minutes 
	///are given up to two decimal places, the format is ddd/1, mmmm/100, 0/1.</summary>
	GpsDestLong(22),
	///<summary>Null-terminated character string that specifies whether the longitude of the destination point is 
	///east or west longitude. E specifies east longitude, and W specifies west longitude.</summary>
	GpsDestLongRef(21),
	///<summary>GPS DOP (data degree of precision). An HDOP value is written during 2-D measurement, and a 
	///PDOP value is written during 3-D measurement.</summary>
	GpsGpsDop(11),
	///<summary>Null-terminated character string that specifies the GPS measurement mode. 2 specifies 2-D 
	///measurement, and 3 specifies 3-D measurement.</summary>
	GpsGpsMeasureMode(10),
	///<summary>Null-terminated character string that specifies the GPS satellites used for measurements. This tag 
	///can be used to specify the ID number, angle of elevation, azimuth, SNR, and other information about each 
	///satellite. The format is not specified. If the GPS receiver is incapable of taking measurements, the value 
	///of the tag must be set to NULL.</summary>
	GpsGpsSatellites(8),
	///<summary>Null-terminated character string that specifies the status of the GPS receiver when the image is 
	///recorded. A means measurement is in progress, and V means the measurement is Interoperability.</summary>
	GpsGpsStatus(9),
	///<summary>Time as coordinated universal time (UTC). The value is expressed as three rational numbers that 
	///give the hour, minute, and second.</summary>
	GpsGpsTime(7),
	///<summary>Offset to a block of GPS property items. Property items whose tags have the prefix PropertyTagGps 
	///are stored in the GPS block. The GPS property items are defined in the EXIF specification. GDI+ uses this 
	///tag to locate GPS information, but GDI+ does not expose this tag for public use.</summary>
	GpsIFD(34853),
	///<summary>Direction of the image when it was captured. The range of values is from 0.00 to 359.99.</summary>
	GpsImgDir(17),
	///<summary>Null-terminated character string that specifies the reference for the direction of the image when 
	///it is captured. T specifies true direction, and M specifies magnetic direction.</summary>
	GpsImgDirRef(16),
	///<summary>Latitude. Latitude is expressed as three rational values giving the degrees, minutes, and seconds 
	///respectively. When degrees, minutes, and seconds are expressed, the format is dd/1, mm/1, ss/1. When 
	///degrees and minutes are used and, for example, fractions of minutes are given up to two decimal places, 
	///the format is dd/1, mmmm/100, 0/1.</summary>
	GpsLatitude(2),
	///<summary>Null-terminated character string that specifies whether the latitude is north or south. 
	///N specifies north latitude, and S specifies south latitude.</summary>
	GpsLatitudeRef(1),
	///<summary>Longitude. Longitude is expressed as three rational values giving the degrees, minutes, and seconds
	///respectively. When degrees, minutes and seconds are expressed, the format is ddd/1, mm/1, ss/1. When 
	///degrees and minutes are used and, for example, fractions of minutes are given up to two decimal places, 
	///the format is ddd/1, mmmm/100, 0/1.</summary>
	GpsLongitude(4),
	///<summary>Null-terminated character string that specifies whether the longitude is east or west longitude. 
	///E specifies east longitude, and W specifies west longitude.</summary>
	GpsLongitudeRef(3),
	///<summary>Null-terminated character string that specifies geodetic survey data used by the GPS receiver. 
	///If the survey data is restricted to Japan, the value of this tag is TOKYO or WGS-84.</summary>
	GpsMapDatum(18),
	///<summary>Speed of the GPS receiver movement.</summary>
	GpsSpeed(13),
	///<summary>Null-terminated character string that specifies the unit used to express the GPS receiver speed 
	///of movement. K, M, and N represent kilometers per hour, miles per hour, and knots respectively.</summary>
	GpsSpeedRef(12),
	///<summary>Direction of GPS receiver movement. The range of values is from 0.00 to 359.99.</summary>
	GpsTrack(15),
	///<summary>Null-terminated character string that specifies the reference for giving the direction of GPS 
	/// receiver movement. T specifies true direction, and M specifies magnetic direction.</summary>
	GpsTrackRef(14),
	///<summary>Version of the Global Positioning Systems (GPS) IFD, given as 2.0.0.0. This tag is mandatory 
	///when the PropertyTagGpsIFD tag is present. When the version is 2.0.0.0, the tag value is 0x02000000.</summary>
	GpsVer(0),
	///<summary>For each possible pixel value in a grayscale image, the optical density of that pixel value.</summary>
	GrayResponseCurve(291),
	///<summary>Precision of the number specified by PropertyTagGrayResponseCurve. 1 specifies tenths, 
	///2 specifies hundredths, 3 specifies thousandths, and so on.</summary>
	GrayResponseUnit(290),
	///<summary>Block of information about grids and guides.</summary>
	GridSize(20497),
	///<summary>Angle for screen.</summary>
	HalftoneDegree(20492),
	///<summary>Information used by the halftone function</summary>
	HalftoneHints(321),
	///<summary>Ink's screen frequency, in lines per inch.</summary>
	HalftoneLPI(20490),
	///<summary>Units for the screen frequency.</summary>
	HalftoneLPIUnit(20491),
	///<summary>Miscellaneous halftone information.</summary>
	HalftoneMisc(20494),
	///<summary>Boolean value that specifies whether to use the printer's default screens.</summary>
	HalftoneScreen(20495),
	///<summary>Shape of the halftone dots.</summary>
	HalftoneShape(20493),
	///<summary>Null-terminated character string that specifies the computer and/or operating system 
	///used to create the image.</summary>
	HostComputer(316),
	///<summary>ICC profile embedded in the image.</summary>
	ICCProfile(34675),
	///<summary>Null-terminated character string that identifies an ICC profile. </summary>
	ICCProfileDescriptor(770),
	///<summary>Null-terminated character string that specifies the title of the image.</summary>
	ImageDescription(270),
	///<summary>Number of pixel rows.</summary>
	ImageHeight(257),
	///<summary>Null-terminated character string that specifies the title of the image.</summary>
	ImageTitle(800),
	///<summary>Number of pixels per row.</summary>
	ImageWidth(256),
	///<summary>Index of the background color in the palette of a GIF image.</summary>
	IndexBackground(20739),
	///<summary>Index of the transparent color in the palette of a GIF image.</summary>
	IndexTransparent(20740),
	///<summary>Sequence of concatenated, null-terminated, character strings that specify the names of the 
	///inks used in a separated image.</summary>
	InkNames(333),
	///<summary>Set of inks used in a separated image.</summary>
	InkSet(332),
	///<summary>For each color component, the offset to the AC Huffman table for that component. See also 
	///PropertyTagSamplesPerPixel.</summary>
	JPEGACTables(521),
	///<summary>For each color component, the offset to the DC Huffman table (or lossless Huffman table) for 
	///that component. See also PropertyTagSamplesPerPixel.</summary>
	JPEGDCTables(520),
	///<summary>Offset to the start of a JPEG bitstream.</summary>
	JPEGInterFormat(513),
	///<summary>Length, in bytes, of the JPEG bitstream.</summary>
	JPEGInterLength(514),
	///<summary>For each color component, a lossless predictor-selection value for that component. 
	///See also PropertyTagSamplesPerPixel.</summary>
	JPEGLosslessPredictors(517),
	///<summary>For each color component, a point transformation value for that component. See also 
	///PropertyTagSamplesPerPixel.</summary>
	JPEGPointTransforms(518),
	///<summary>JPEG compression process.</summary>
	JPEGProc(512),
	///<summary>For each color component, the offset to the quantization table for that component. 
	///See also PropertyTagSamplesPerPixel.</summary>
	JPEGQTables(519),
	///<summary>Private tag used by the Adobe Photoshop format. Not for public use.</summary>
	JPEGQuality(20496),
	///<summary>Length of the restart interval.</summary>
	JPEGRestartInterval(515),
	///<summary>For an animated GIF image, the number of times to display the animation. A value of 0 specifies 
	///that the animation should be displayed infinitely.</summary>
	LoopCount(20737),
	///<summary>Luminance table. The luminance table and the chrominance table are used to control JPEG quality. 
	///A valid luminance or chrominance table has 64 entries of type PropertyTagTypeShort. If an image has 
	///either a luminance table or a chrominance table, then it must have both tables.</summary>
	LuminanceTable(20624),
	///<summary>For each color component, the maximum value assigned to that component. See also 
	///PropertyTagSamplesPerPixel.</summary>
	MaxSampleValue(281),
	///<summary>For each color component, the minimum value assigned to that component. See also 
	///PropertyTagSamplesPerPixel.</summary>
	MinSampleValue(280),
	///<summary>Type of data in a subfile.</summary>
	NewSubfileType(254),
	///<summary>Number of inks.</summary>
	NumberOfInks(334),
	///<summary>Image orientation viewed in terms of rows and columns. The value is a System.UShort, with these
	///values: 1 - The 0th row is at the top of the visual image, and the 0th column is the visual left side. 
	///2 - The 0th row is at the visual top of the image, and the 0th column is the visual right side. 
	///3 - The 0th row is at the visual bottom of the image, and the 0th column is the visual right side. 
	///4 - The 0th row is at the visual bottom of the image, and the 0th column is the visual right side. 
	///5 - The 0th row is the visual left side of the image, and the 0th column is the visual top. 
	///6 - The 0th row is the visual right side of the image, and the 0th column is the visual top. 
	///7 - The 0th row is the visual right side of the image, and the 0th column is the visual bottom. 
	///8 - The 0th row is the visual left side of the image, and the 0th column is the visual bottom. </summary>
	Orientation(274),
	///<summary>Null-terminated character string that specifies the name of the page from which the image was scanned.</summary>
	PageName(285),
	///<summary>Page number of the page from which the image was scanned.</summary>
	PageNumber(297),
	///<summary>Palette histogram.</summary>
	PaletteHistogram(20755),
	///<summary>How pixel data will be interpreted.</summary>
	PhotometricInterp(262),
	///<summary>Pixels per unit in the x direction.</summary>
	PixelPerUnitX(20753),
	///<summary>Pixels per unit in the y direction.</summary>
	PixelPerUnitY(20754),
	///<summary>Unit for PropertyTagPixelPerUnitX and PropertyTagPixelPerUnitY.</summary>
	PixelUnit(20752),
	///<summary>Whether pixel components are recorded in chunky or planar format.</summary>
	PlanarConfig(284),
	///<summary>Type of prediction scheme that was applied to the image data before the encoding scheme was applied.</summary>
	Predictor(317),
	///<summary>For each of the three primary colors in the image, the chromaticity of that color.</summary>
	PrimaryChromaticities(319),
	///<summary>Sequence of one-byte Boolean values that specify printing options.</summary>
	PrintFlags(20485),
	///<summary>Print flags bleed width.</summary>
	PrintFlagsBleedWidth(20488),
	///<summary>Print flags bleed width scale.</summary>
	PrintFlagsBleedWidthScale(20489),
	///<summary>Print flags center crop marks.</summary>
	PrintFlagsCrop(20487),
	///<summary>Print flags version.</summary>
	PrintFlagsVersion(20486),
	///<summary>Reference black point value and reference white point value.</summary>
	REFBlackWhite(532),
	///<summary>Unit of measure for the horizontal resolution and the vertical resolution. 2 = inch, 3 = centimeter</summary>
	ResolutionUnit(296),
	///<summary>Units in which to display the image width.</summary>
	ResolutionXLengthUnit(20483),
	///<summary>Units in which to display horizontal resolution.</summary>
	ResolutionXUnit(20481),
	///<summary>Units in which to display the image height.</summary>
	ResolutionYLengthUnit(20484),
	///<summary>Units in which to display vertical resolution.</summary>
	ResolutionYUnit(20482),
	///<summary>Number of rows per strip. See also PropertyTagStripBytesCount and PropertyTagStripOffsets.</summary>
	RowsPerStrip(278),
	///<summary>For each color component, the numerical format (unsigned, signed, floating point) of that
	///component. See also PropertyTagSamplesPerPixel.</summary>
	SampleFormat(339),
	///<summary>Number of color components per pixel.</summary>
	SamplesPerPixel(277),
	///<summary>For each color component, the maximum value of that component. See also PropertyTagSamplesPerPixel.</summary>
	SMaxSampleValue(341),
	///<summary>For each color component, the minimum value of that component. See also PropertyTagSamplesPerPixel.</summary>
	SMinSampleValue(340),
	///<summary>Null-terminated character string that specifies the name and version of the software 
	///or firmware of the device used to generate the image.</summary>
	SoftwareUsed(305),
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
	SRGBRenderingIntent(771),
	///<summary>For each strip, the total number of bytes in that strip.</summary>
	StripBytesCount(279),
	///<summary>For each strip, the byte offset of that strip. See also PropertyTagRowsPerStrip
	///and PropertyTagStripBytesCount.</summary>
	StripOffsets(273),
	///<summary>Type of data in a subfile.</summary>
	SubfileType(255),
	///<summary>Set of flags that relate to T4 encoding.</summary>
	T4Option(292),
	///<summary>Set of flags that relate to T6 encoding.</summary>
	T6Option(293),
	///<summary>Null-terminated character string that describes the intended printing environment.</summary>
	TargetPrinter(337),
	///<summary>Technique used to convert from gray pixels to black and white pixels.</summary>
	ThreshHolding(263),
	///<summary>Null-terminated character string that specifies the name of the person who created the thumbnail image.</summary>
	ThumbnailArtist(20532),
	///<summary>Number of bits per color component in the thumbnail image. See also 
	///PropertyTagThumbnailSamplesPerPixel.</summary>
	ThumbnailBitsPerSample(20514),
	///<summary>Bits per pixel (BPP) for the thumbnail image.</summary>
	ThumbnailColorDepth(20501),
	///<summary>Compressed size, in bytes, of the thumbnail image.</summary>
	ThumbnailCompressedSize(20505),
	///<summary>Compression scheme used for thumbnail image data.</summary>
	ThumbnailCompression(20515),
	///<summary>Null-terminated character string that contains copyright information for the thumbnail image.</summary>
	ThumbnailCopyRight(20539),
	///<summary>Raw thumbnail bits in JPEG or RGB format. Depends on PropertyTagThumbnailFormat.</summary>
	ThumbnailData(20507),
	///<summary>Date and time the thumbnail image was created. See also PropertyTagDateTime.</summary>
	ThumbnailDateTime(20531),
	///<summary>Null-terminated character string that specifies the manufacturer of the equipment used to 
	///record the thumbnail image.</summary>
	ThumbnailEquipMake(20518),
	///<summary>Null-terminated character string that specifies the model name or model number of the
	///equipment used to record the thumbnail image.</summary>
	ThumbnailEquipModel(20519),
	///<summary>Format of the thumbnail image.</summary>
	ThumbnailFormat(20498),
	///<summary>Height, in pixels, of the thumbnail image.</summary>
	ThumbnailHeight(20500),
	///<summary>Null-terminated character string that specifies the title of the image.</summary>
	ThumbnailImageDescription(20517),
	///<summary>Number of pixel rows in the thumbnail image.</summary>
	ThumbnailImageHeight(20513),
	///<summary>Number of pixels per row in the thumbnail image.</summary>
	ThumbnailImageWidth(20512),
	///<summary>Thumbnail image orientation in terms of rows and columns. See also PropertyTagOrientation.</summary>
	ThumbnailOrientation(20521),
	///<summary>How thumbnail pixel data will be interpreted.</summary>
	ThumbnailPhotometricInterp(20516),
	///<summary>Whether pixel components in the thumbnail image are recorded in chunky or planar format. 
	///See also PropertyTagPlanarConfig.</summary>
	ThumbnailPlanarConfig(20527),
	///<summary>Number of color planes for the thumbnail image.</summary>
	ThumbnailPlanes(20502),
	///<summary>For each of the three primary colors in the thumbnail image, the chromaticity of that color. 
	///See also PropertyTagPrimaryChromaticities.</summary>
	ThumbnailPrimaryChromaticities(20534),
	///<summary>Byte offset between rows of pixel data.</summary>
	ThumbnailRawBytes(20503),
	///<summary>Reference black point value and reference white point value for the thumbnail image. See also 
	///PropertyTagREFBlackWhite.</summary>
	ThumbnailRefBlackWhite(20538),
	///<summary>Unit of measure for the horizontal resolution and the vertical resolution of the thumbnail 
	///image. See also PropertyTagResolutionUnit.</summary>
	ThumbnailResolutionUnit(20528),
	///<summary>Thumbnail resolution in the width direction. The resolution unit is
	///given in PropertyTagThumbnailResolutionUnit</summary>
	ThumbnailResolutionX(20525),
	///<summary>Thumbnail resolution in the height direction. The resolution unit is 
	///given in PropertyTagThumbnailResolutionUnit</summary>
	ThumbnailResolutionY(20526),
	///<summary>Number of rows per strip in the thumbnail image. See also 
	///PropertyTagThumbnailStripBytesCount and PropertyTagThumbnailStripOffsets.</summary>
	ThumbnailRowsPerStrip(20523),
	///<summary>Number of color components per pixel in the thumbnail image.</summary>
	ThumbnailSamplesPerPixel(20522),
	///<summary>Total size, in bytes, of the thumbnail image.</summary>
	ThumbnailSize(20504),
	///<summary>Null-terminated character string that specifies the name and version of the 
	///software or firmware of the device used to generate the thumbnail image.</summary>
	ThumbnailSoftwareUsed(20530),
	///<summary>For each thumbnail image strip, the total number of bytes in that strip.</summary>
	ThumbnailStripBytesCount(20524),
	///<summary>For each strip in the thumbnail image, the byte offset of that strip. See also 
	///PropertyTagThumbnailRowsPerStrip and PropertyTagThumbnailStripBytesCount.</summary>
	ThumbnailStripOffsets(20520),
	///<summary>Tables that specify transfer functions for the thumbnail image. See also 
	///PropertyTagTransferFunction.</summary>
	ThumbnailTransferFunction(20529),
	///<summary>Chromaticity of the white point of the thumbnail image. See also PropertyTagWhitePoint.</summary>
	ThumbnailWhitePoint(20533),
	///<summary>Width, in pixels, of the thumbnail image.</summary>
	ThumbnailWidth(20499),
	///<summary>Coefficients for transformation from RGB to YCbCr data for the thumbnail image. See also 
	///PropertyTagYCbCrCoefficients.</summary>
	ThumbnailYCbCrCoefficients(20535),
	///<summary>Position of chrominance components in relation to the luminance component for the thumbnail image. 
	///See also PropertyTagYCbCrPositioning.</summary>
	ThumbnailYCbCrPositioning(20537),
	///<summary>Sampling ratio of chrominance components in relation to the luminance component for the 
	///thumbnail image. See also PropertyTagYCbCrSubsampling.</summary>
	ThumbnailYCbCrSubsampling(20536),
	///<summary>For each tile, the number of bytes in that tile.</summary>
	TileByteCounts(325),
	///<summary>Number of pixel rows in each tile.</summary>
	TileLength(323),
	///<summary>For each tile, the byte offset of that tile.</summary>
	TileOffset(324),
	///<summary>Number of pixel columns in each tile.</summary>
	TileWidth(322),
	///<summary>Tables that specify transfer functions for the image.</summary>
	TransferFunction(301),
	///<summary>Table of values that extends the range of the transfer function.</summary>
	TransferRange(342),
	///<summary>Chromaticity of the white point of the image.</summary>
	WhitePoint(318),
	///<summary>Offset from the left side of the page to the left side of the image. The unit of measure
	///is specified by PropertyTagResolutionUnit.</summary>
	XPosition(286),
	///<summary>Number of pixels per unit in the image width (x) direction. The unit is specified by 
	///PropertyTagResolutionUnit.</summary>
	XResolution(282),
	///<summary>Coefficients for transformation from RGB to YCbCr image data. </summary>
	YCbCrCoefficients(529),
	///<summary>Position of chrominance components in relation to the luminance component.</summary>
	YCbCrPositioning(531),
	///<summary>Sampling ratio of chrominance components in relation to the luminance component.</summary>
	YCbCrSubsampling(530),
	///<summary>Offset from the top of the page to the top of the image. The unit of measure is 
	///specified by PropertyTagResolutionUnit.</summary>
	YPosition(287),
	///<summary>Number of pixels per unit in the image height (y) direction. The unit is specified by 
	///PropertyTagResolutionUnit.</summary>
	YResolution(283);
	
	private final int rawMetadataItemName;
    private RawMetadataItemName(int rawMetadataItemName) {
        this.rawMetadataItemName = rawMetadataItemName;
    }
}
