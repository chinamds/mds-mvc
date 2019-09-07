package com.mds.cm.content;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import com.mds.cm.exception.InvalidGalleryException;
import com.mds.cm.exception.UnsupportedImageTypeException;
import com.mds.core.ContentObjectRotation;
import com.mds.core.DisplayObjectType;
import com.mds.core.RawMetadataItemName;
import com.mds.core.RotateFlipType;
import com.mds.core.Size;
import com.mds.core.exception.ArgumentException;
import com.mds.core.exception.NotSupportedException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.sys.util.AppSettings;
import com.mds.cm.util.CMUtils;
import com.mds.util.FileMisc;
import com.mds.util.HelperFunctions;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.common.BufferedImageFactory;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.io.FilenameUtils;

/// <summary>
///   Provides base functionality for creating and saving the files associated with <see cref="ContentObjectBo" /> objects.
/// </summary>
public abstract class DisplayObjectCreator{
	protected ContentObjectBo contentObject;
	protected DisplayObject parent;
	
	//#region Properties

	/// <summary>
	///   Gets or sets the content object this instance applies to.
	/// </summary>
	protected ContentObjectBo getContentObject(){ 
		return contentObject;
	}
	
	protected void setContentObject(ContentObjectBo contentObject){ 
		this.contentObject = contentObject;
	}

	/// <summary>
	///   Gets the gallery settings associated with the <see cref="ContentObjectBo" />.
	/// </summary>
	protected GallerySettings getGallerySettings() throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		return CMUtils.loadGallerySetting(contentObject.getGalleryId());
	}

	//#endregion

	//#region Methods

	/// <summary>
	/// Gets or sets the display object this instance belongs to.
	/// </summary>
	/// <value>The display object this instance belongs to.</value>
	public DisplayObject getParent() {
		return parent;
	}
	
	public void setParent(DisplayObject parent) {
		this.parent = parent;
	}

	/// <summary>
	///   Generate the file for this display object and save it to the file system. The routine may decide that
	///   a file does not need to be generated, usually because it already exists. However, it will always be
	///   created if the relevant flag is set on the parent <see cref="ContentObjectBo" />. (Example: If
	///   <see cref="ContentObjectBo.RegenerateThumbnailOnSave" /> = true, the thumbnail file will always be created.) No data is
	///   persisted to the data store.
	/// </summary>
	/// <exception cref="System.NotImplementedException"></exception>
	public void generateAndSaveFile() throws IOException, UnsupportedImageTypeException, InvalidGalleryException{
		throw new NotImplementedException();
	}

	/// <summary>
	/// Gets the width and height of the specified <paramref name="displayObject" />. The value is calculated from the 
	/// physical file. Returns an empty <see cref="System.Windows.Size" /> instance if the value cannot be computed or 
	/// is not applicable to the object (for example, for audio files and external content objects).
	/// </summary>
	/// <returns><see cref="System.Windows.Size" />.</returns>
	public Size getSize(DisplayObject displayObject) throws UnsupportedImageTypeException{
		/*if (AppSetting.Instance.AppTrustLevel == ApplicationTrustLevel.Full)
		{
			try
			{
				return GetSizeUsingWpf(displayObject);
			}
			catch (NotSupportedException)
			{
				return GetSizeUsingGdi(displayObject);
			}
		}
		else
		{
			return GetSizeUsingGdi(displayObject);
		}*/
		Size size = Size.Empty;
		try	{
			size = getSizeUsingWpf(displayObject);
		}catch (NotSupportedException | IOException ex){
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		if (size.isEmpty()) {
			File file = new File(displayObject.getFileNamePhysicalPath());
			ByteSourceFile byteSource = new ByteSourceFile(file);
			try {
				final Dimension d = Imaging.getImageSize(byteSource.getAll());
				size = new Size(d.getWidth(), d.getHeight());
			} catch (ImageReadException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new UnsupportedImageTypeException();
			}
		}
		
		return size;
	}

	/// <summary>
	///   Determine name of new JPEG file and ensure it is unique in the directory. (Example: If original = puppy.jpg,
	///   thumbnail = zThumb_puppy.jpg) The new file name's extension will be .jpeg if the original was .jpeg; otherwise it will
	///   be .jpg.
	/// </summary>
	/// <param name="filePath">The path to the directory where the file is to be created.</param>
	/// <param name="fileNamePrefix">The file name prefix. Examples: "zThumb_", "zOpt_"</param>
	/// <returns>Returns the name of the new file name and ensures it is unique in the directory.</returns>
	protected String generateJpegFilename(String filePath, String fileNamePrefix){
		String extension = ((FileMisc.getExt(getContentObject().getOriginal().getFileInfo().getName())).equalsIgnoreCase(".jpeg") ? ".jpeg" : ".jpg");
		String nameWithoutExtension = FilenameUtils.getBaseName(getContentObject().getOriginal().getFileInfo().getName());
		String thumbnailFilename = StringUtils.join(new String[] {fileNamePrefix, nameWithoutExtension, extension});

		return HelperFunctions.validateFileName(filePath, thumbnailFilename);
	}

	/// <summary>
	///   Calculate new width and height values of an existing <paramref name="size" /> instance, making the length
	///   of the longest side equal to <paramref name="maxLength" />. The aspect ratio if preserved. If
	///   <paramref
	///     name="autoEnlarge" />
	///   is <c>true</c>, then increase the size so that the longest side equals <paramref name="maxLength" />
	///   (i.e. enlarge a small image if necessary).
	/// </summary>
	/// <param name="size">The current size of an object.</param>
	/// <param name="maxLength">The target length (in pixels) of the longest side.</param>
	/// <param name="autoEnlarge">
	///   A value indicating whether to enlarge objects that are smaller than the
	///   <paramref name="size" />. If true, the new width and height will be increased if necessary. If false, the original
	///   width and height are returned when their dimensions are smaller than <paramref name="maxLength" />. This
	///   parameter has no effect when <paramref name="maxLength" /> is greater than the width and height of
	///   <paramref
	///     name="size" />
	///   .
	/// </param>
	/// <returns>
	///   Returns a <see cref="Size" /> instance conforming to the requested parameters.
	/// </returns>
	public static Size calculateWidthAndHeight(Size size, int maxLength, boolean autoEnlarge){
		int newWidth, newHeight;

		if (!autoEnlarge && (maxLength > size.Width) && (maxLength > size.Height)){
			// Bitmap is smaller than desired thumbnail dimensions but autoEnlarge = false. Don't enlarge thumbnail; 
			// just use original size.
			newWidth = size.Width.intValue();
			newHeight = size.Height.intValue();
		}
		else if (size.Width > size.Height){
			// Bitmap is in landscape format (width > height). The width will be the longest dimension.
			newWidth = maxLength;
			newHeight = (int)(size.Height * newWidth / size.Width);
		}
		else{
			// Bitmap is in portrait format (height > width). The height will be the longest dimension.
			newHeight = maxLength;
			newWidth = (int)(size.Width * newHeight / size.Height);
		}

		return new Size(newWidth, newHeight);
	}

	/// <summary>
	///   Creates an image file having a max length of <paramref name="maxLength" /> and JPEG quality of
	///   <paramref
	///     name="jpegQuality" />
	///   from the original file of <see cref="ContentObjectBo" />. The file is saved to the location
	///   <paramref
	///     name="newFilePath" />
	///   .
	///   The width and height of the generated image is returned as a <see cref="Size" /> instance.
	/// </summary>
	/// <param name="newFilePath">The full path where the image will be saved.</param>
	/// <param name="maxLength">The maximum length of one side of the image.</param>
	/// <param name="jpegQuality">The JPEG quality.</param>
	/// <returns>
	///   Returns a <see cref="Size" /> instance containing the width and height of the generated image.
	/// </returns>
	/// <exception cref="UnsupportedImageTypeException">
	///   Thrown when MDS System cannot process the image,
	///   most likely because it is corrupt or an unsupported image type.
	/// </exception>
	protected Size generateImageUsingDotNet(String newFilePath, int maxLength, int jpegQuality) throws UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidGalleryException	{
		/*if (AppSettings.getInstance().getAppTrustLevel() == ApplicationTrustLevel.Full)
		{
			try
			{
				return GenerateImageUsingWpf(newFilePath, maxLength, jpegQuality);
			}
			catch (UnsupportedImageTypeException)
			{
				// If we can't process an image using WPF, try the older GDI+ technique. For example, WMF images fail with WPF
				// but succeed with GDI+.
				return GenerateImageUsingGdi(newFilePath, maxLength, jpegQuality);
			}
		}
		else
		{
			return GenerateImageUsingGdi(newFilePath, maxLength, jpegQuality);
		}*/
		//return Size.Empty;
		return generateImageUsingGdi(newFilePath, maxLength, jpegQuality);
	}

	protected Size generateImageUsingImageMagick(String newFilePath, int maxLength, int jpegQuality){
		// Generate a temporary filename to store the thumbnail created by ImageMagick.
		String tmpImagePath = FilenameUtils.concat(AppSettings.getInstance().getTempUploadDirectory(), StringUtils.join(new String[] {UUID.randomUUID().toString(), ".jpg"}));

		if (!StringUtils.isBlank(getContentObject().getOriginal().getTempFilePath())){
			// Use the image that was created earlier in the thumbnail generator.
			tmpImagePath = getContentObject().getOriginal().getTempFilePath();
		}

		// Request that ImageMagick create the image. If successful, the file will be created. If not, it fails silently.
		if (!FileMisc.fileExists(tmpImagePath))	{
			ImageMagick.generateImage(getContentObject().getOriginal().getFileNamePhysicalPath(), tmpImagePath, getContentObject().getGalleryId());
		}

		if (FileMisc.fileExists(tmpImagePath))	{
			// Save the path so it can be used later by the optimized image creator.
			getContentObject().getOriginal().setTempFilePath(tmpImagePath);

			try{
				// ImageMagick successfully created an image. Now resize it to the width and height we need.
				// We can safely use the WPF version since we'll only get this far if we're running in Full Trust.
				return generateImageUsingWpf(tmpImagePath, newFilePath, maxLength, jpegQuality);
			}catch (Exception ex){
				//ex.Data.Add("MDS Info", String.Format("This error occurred while trying to process the ImageMagick-generated file {0}. The original file is {1}. The gallery will try to create an image using .NET instead.", tmpImagePath, getContentObject().getOriginal().FileNamePhysicalPath));
				//EventLogController.RecordError(ex, AppSetting.Instance, getContentObject().GalleryId, Factory.LoadGallerySettings());

				return Size.Empty;
			}
		}

		return Size.Empty;
	}

	/// <summary>
	/// Rotates the <paramref name="filePath" /> by the amount specified in <see cref="ContentObjectBo.Rotation" />.
	/// The rotated file is saved with a JPEG quality of <paramref name="jpegQuality" />. Returns an object indicating
	/// the actual rotation applied to the object and its final size. Some objects may be rotated an amount different
	/// than the requested amount when the displayed orientation is different than the file's actual orientation.
	/// The metadata in the original file is preserved to the extent possible.
	/// </summary>
	/// <param name="filePath">The full path to the file.</param>
	/// <param name="jpegQuality">The JPEG quality.</param>
	/// <returns>Returns a <see cref="Tuple" /> indicating the actual <see cref="ContentObjectRotation" /> and final 
	/// <see cref="Size" /> and of the generated file.</returns>
	protected Pair<ContentObjectRotation, Size> rotate(String filePath, int jpegQuality) throws UnsupportedImageTypeException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		switch (this.getContentObject().getContentObjectType())	{
		case Image:
			return rotateImage(filePath, jpegQuality);

		// Rotate videos only when we're dealing with the thumbnail image. Actual video files will be rotated
		// in ContentConversionQueue/FFmpeg.
		case Video:
			if (this.getParent().getDisplayType() == DisplayObjectType.Thumbnail){
				return rotateImage(filePath, jpegQuality);
			}
			break;
		default:
			break;
		}

		return new ImmutablePair<ContentObjectRotation, Size>(ContentObjectRotation.Rotate0, Size.Empty);
	}

	private Pair<ContentObjectRotation, Size> rotateImage(String filePath, int jpegQuality) throws UnsupportedImageTypeException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		// Grab a reference to the file's metadata properties so we can add them back after the rotation.
		String[] propItems = null;
		/*if (this.parent.getDisplayType() == DisplayObjectType.Original){
			using (var bmp = new Bitmap(filePath))
			{
				propItems = bmp.PropertyItems;
			}
		}
*/
		Pair<ContentObjectRotation, Size> rotateResult;
		try
		{
			rotateResult = rotateUsingWpf(filePath, jpegQuality);
		}catch (NotSupportedException ex){
			rotateResult = rotateUsingGdi(filePath, jpegQuality);
		}
		
		/*if (AppSetting.Instance.AppTrustLevel == ApplicationTrustLevel.Full){
			try
			{
				rotateResult = rotateUsingWpf(filePath, jpegQuality);
			}
			catch (NotSupportedException)
			{
				rotateResult = rotateUsingGdi(filePath, jpegQuality);
			}
		}else{
			rotateResult = rotateUsingGdi(filePath, jpegQuality);
		}*/

		if (rotateResult.getLeft().getContentObjectRotation() > ContentObjectRotation.Rotate0.getContentObjectRotation())	{
			try {
				addMetaValuesBackToRotatedImage(filePath, propItems);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Add meta values back to file
		}

		return rotateResult;
	}

	/// <summary>
	/// Check the orientation meta value of the original content object. If the orientation is anything other than
	/// normal (0 degrees), rotate <paramref name="newFilePath" /> to be in the correct orientation. Returns
	/// <see cref="Size.Empty" /> if no rotation is performed.
	/// </summary>
	/// <param name="newFilePath">The full path of the file to rotate.</param>
	/// <param name="jpegQuality">The JPEG quality.</param>
	/// <returns>Returns a <see cref="Size" /> instance containing the width and height of the generated image.</returns>
	protected Size executeAutoRotation(String newFilePath, int jpegQuality) throws UnsupportedImageTypeException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		// Check for need to rotate and rotate if necessary.
		if (getContentObject().getRotation() != ContentObjectRotation.NotSpecified)	{
			// When a rotation is explicitly being performed, we don't want to do an auto-rotation.
			return Size.Empty;
		}

		switch (getContentObject().getOrientation())
		{
			case Rotated90:
			case Rotated180:
			case Rotated270:
				Pair<ContentObjectRotation, Size> rotateResult = rotate(newFilePath, jpegQuality);
				return rotateResult.getRight();

			default:
				return Size.Empty;
		}
	}

	//#endregion

	//#region Functions

	/// <overloads>
	///   Creates an image file using WPF.
	/// </overloads>
	/// <summary>
	///   Creates an image file having a max length of <paramref name="maxLength" /> and JPEG quality of
	///   <paramref
	///     name="jpegQuality" />
	///   from the original file of <see cref="ContentObjectBo" />. The file is saved to the location
	///   <paramref
	///     name="newFilePath" />
	///   .
	///   The width and height of the generated image is returned as a <see cref="Size" /> instance. The WPF classes
	///   are used to create the image, which are faster than the GDI classes. The caller must verify application is running in Full Trust.
	/// </summary>
	/// <param name="newFilePath">The full path where the image will be saved.</param>
	/// <param name="maxLength">The maximum length of one side of the image.</param>
	/// <param name="jpegQuality">The JPEG quality.</param>
	/// <returns>
	///   Returns a <see cref="Size" /> instance containing the width and height of the generated image.
	/// </returns>
	/// <exception cref="UnsupportedImageTypeException">
	///   Thrown when MDS System cannot process the image,
	///   most likely because it is corrupt or an unsupported image type.
	/// </exception>
	private Size generateImageUsingWpf(String newFilePath, int maxLength, int jpegQuality) throws UnsupportedImageTypeException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		return generateImageUsingWpf(getContentObject().getOriginal().getFileNamePhysicalPath(), newFilePath, maxLength, jpegQuality);
	}

	/// <summary>
	///   Creates an image file having a max length of <paramref name="maxLength" /> and JPEG quality of
	///   <paramref
	///     name="jpegQuality" />
	///   from <paramref name="sourceFilePath" />. The file is saved to the location <paramref name="newFilePath" />.
	///   The width and height of the generated image is returned as a <see cref="Size" /> instance. The WPF classes
	///   are used to create the image, which are faster than the GDI classes. The caller must verify application is running in Full Trust.
	/// </summary>
	/// <param name="sourceFilePath">The full path of the source image.</param>
	/// <param name="newFilePath">The full path where the image will be saved.</param>
	/// <param name="maxLength">The maximum length of one side of the image.</param>
	/// <param name="jpegQuality">The JPEG quality.</param>
	/// <returns>
	///   Returns a <see cref="Size" /> instance containing the width and height of the generated image.
	/// </returns>
	/// <exception cref="UnsupportedImageTypeException">
	///   Thrown when MDS System cannot process the image, most likely because it is corrupt or an unsupported image type.
	/// </exception>
	private Size generateImageUsingWpf(String sourceFilePath, String newFilePath, int maxLength, int jpegQuality) throws UnsupportedImageTypeException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		try{
			BufferedImage origImage = ImageIO.read(new FileInputStream(sourceFilePath));
			if (origImage == null) {
				throw new UnsupportedImageTypeException(getContentObject());
			}
			
            int origHeight = origImage.getHeight(null);
            int origWidth = origImage.getWidth(null);

            Size newSize = calculateWidthAndHeight(new Size(origWidth, origHeight), maxLength, false);

            BufferedImage outImage =ImageHelper.getScaledInstance(origImage, newSize.Width.intValue(), newSize.Height.intValue(), (Object) RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
            ImageHelper.saveImageToDisk(outImage, newFilePath, "jpeg", jpegQuality);
            Size rotatedSize = executeAutoRotation(newFilePath, jpegQuality);
            
			return (rotatedSize.isEmpty() ? newSize : rotatedSize);
		}catch (IOException ex){
			throw new UnsupportedImageTypeException(getContentObject(), ex);
		}catch (NotSupportedException ex){
			throw new UnsupportedImageTypeException(getContentObject(), ex);
		}
	}

	private Pair<ContentObjectRotation, Size> rotateUsingWpf(String filePath, int jpegQuality){
		ContentObjectRotation actualRotation = getContentObject().calculateNeededRotation();

		if (actualRotation.getContentObjectRotation() <= ContentObjectRotation.Rotate0.getContentObjectRotation()){
			return new ImmutablePair<ContentObjectRotation, Size>(actualRotation, Size.Empty);
		}
		
		IMOperation operation = new IMOperation();
		operation.addImage(filePath);
		operation.rotate(getIMRotateAngle());
		operation.addImage(getContentObject().getOriginal().getFileNamePhysicalPath());

		ConvertCmd cmd = new ConvertCmd();
		cmd.setSearchPath(FilenameUtils.concat(AppSettings.getInstance().getHomePath(), "bin")); 
		try {
			cmd.run(operation);
			if (FileMisc.fileExists(getContentObject().getOriginal().getFileNamePhysicalPath())){
				return new ImmutablePair<ContentObjectRotation, Size>(actualRotation, getSize(getContentObject().getOriginal()));
			}
		} catch (IOException | InterruptedException | IM4JavaException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedImageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*try {
			String imgFormat = readFormatName(filePath); // Need to grab the format before we rotate or else we lose it (it changes to MemoryBmp)
			
			final Map<String, Object> params = new HashMap<>();

	        // set optional parameters if you like
	        params.put(ImagingConstants.BUFFERED_IMAGE_FACTORY, new ManagedImageBufferedImageFactory());
	        
			BufferedImage originalBitmap = Imaging.getBufferedImage(new File(filePath), params);
			originalBitmap = ImageHelper.rotate(originalBitmap, getRotateAngle());
			
			ImageHelper.saveImageToDisk(originalBitmap, getContentObject().getOriginal().getFileNamePhysicalPath(), imgFormat, jpegQuality);
			
			return new ImmutablePair<ContentObjectRotation, Size>(actualRotation, new Size(originalBitmap.getWidth(), originalBitmap.getHeight()));
		} catch (ImageReadException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		return new ImmutablePair<ContentObjectRotation, Size>(actualRotation, Size.Empty);
	}

	private Pair<ContentObjectRotation, Size> rotateUsingGdi(String filePath, int jpegQuality) throws UnsupportedImageTypeException	{
		ContentObjectRotation actualRotation = getContentObject().calculateNeededRotation();

		if (actualRotation.getContentObjectRotation() <= ContentObjectRotation.Rotate0.getContentObjectRotation()){
			return new ImmutablePair<ContentObjectRotation, Size>(actualRotation, Size.Empty);
		}
		
		try{
			String imgFormat = readFormatName(filePath); // Need to grab the format before we rotate or else we lose it (it changes to MemoryBmp)
			// Get reference to the bitmap from which the optimized image will be generated.
			BufferedImage originalBitmap = ImageIO.read(new File(filePath));
			
			originalBitmap = ImageHelper.rotate(originalBitmap, getRotateAngle());
			
			ImageHelper.saveImageToDisk(originalBitmap, getContentObject().getOriginal().getFileNamePhysicalPath(), imgFormat, jpegQuality);

			return new ImmutablePair<ContentObjectRotation, Size>(actualRotation, new Size(originalBitmap.getWidth(), originalBitmap.getHeight()));
		}catch (Exception ex){
			throw new UnsupportedImageTypeException();
		}

	}
	
	private String readFormatName(String filePath) throws IOException {
		ImageInputStream input = ImageIO.createImageInputStream(new File(filePath));
		String format = null;
		try {
		    Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
	
		    if (readers.hasNext()) {
		        ImageReader reader = readers.next();
	
		        try {
		            reader.setInput(input);
	
		            //BufferedImage image = reader.read(0);  // Read the same image as ImageIO.read
	
		            // Do stuff with image...
	
		            // When done, either (1):
		            format = reader.getFormatName(); // Get the format name for use later
		        }
		        finally {
		            reader.dispose();
		        }
		    }
		}
		finally {
		    input.close();
		}
		
		return format;
	}

	/// <summary>
	///   Create an image file having a max length of <paramref name="maxLength" /> and JPEG quality of
	///   <paramref
	///     name="jpegQuality" />
	///   from the original file of <see cref="ContentObjectBo" />. The file is saved to the location
	///   <paramref
	///     name="newFilePath" />
	///   .
	///   The width and height of the generated image is returned as a <see cref="Size" /> instance. The GDI+ classes
	///   are used to create the image, which are slower than WPF but run in Medium Trust.
	/// </summary>
	/// <param name="newFilePath">The full path where the image will be saved.</param>
	/// <param name="maxLength">The maximum length of one side of the image.</param>
	/// <param name="jpegQuality">The JPEG quality.</param>
	/// <returns>
	///   Returns a <see cref="Size" /> instance containing the width and height of the generated image.
	/// </returns>
	/// <exception cref="UnsupportedImageTypeException">
	///   Thrown when MDS System cannot process the image,
	///   most likely because it is corrupt or an unsupported image type.
	/// </exception>
	private Size generateImageUsingGdi(String newFilePath, int maxLength, int jpegQuality) throws UnsupportedImageTypeException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		try
		{
			BufferedImage source = ImageIO.read(getContentObject().getOriginal().getFileInfo());
			if (source == null) {
				throw new UnsupportedImageTypeException(getContentObject());
			}
			
			Size newSize = calculateWidthAndHeight(new Size(source.getWidth(), source.getHeight()), maxLength, false);

			// Generate the new image and save to disk.
			newSize = ImageHelper.saveImageFile(source, newFilePath, "JPG", newSize.Width, newSize.Height, jpegQuality);

			Size rotatedSize = executeAutoRotation(newFilePath, jpegQuality);

			return rotatedSize.isEmpty() ? newSize : rotatedSize;
		}catch (ArgumentException | IOException ex){
			throw new UnsupportedImageTypeException(getContentObject(), ex);
		}
	}

	//private static BitmapFrame ReadBitmapFrame(MemoryStream photoStream)
	private static Image readBitmapFrame(MemoryImageSource photoStream){
		Image photoDecoder = Toolkit.getDefaultToolkit().createImage(photoStream);
		//ImageIO.read(input);
		//BufferedImage image;

		return photoDecoder;
	}

	private static void fastResize(BufferedImage photo, BufferedImage outImage){
		
        final double scale = 1.0;
        AffineTransform tx = new AffineTransform();
        tx.scale(scale, scale);

        AffineTransformOp af = new AffineTransformOp(tx, null);
        try {
            af.filter(photo, outImage);
        } catch (ImagingOpException e) {}
	}

	private static void resize(BufferedImage origImage, BufferedImage outImage, int width, int height, int scalingMode)	{
        
        Image scaled = origImage.getScaledInstance(
        		width, height, scalingMode); //Image.SCALE_SMOOTH
        outImage.createGraphics().drawImage(scaled, 0, 0, null);
	}

	/// <summary>
	/// Generates the JPEG in <paramref name="targetFrame" /> to a JPEG byte array having the specified <paramref name="quality" />.
	/// </summary>
	/// <param name="targetFrame">The target frame containing the JPEG.</param>
	/// <param name="quality">The quality the generated JPEG is to have.</param>
	/// <returns>System.Byte[][].</returns>
	/// <exception cref="FileFormatException">Thrown when an input file or a data stream that is supposed to conform to a 
	/// certain file format specification is malformed. Thrown by <see cref="JpegBitmapEncoder.Save" />.</exception>
	/// <exception cref="NotSupportedException">Thrown when <see cref="JpegBitmapEncoder" /> does not have a valid frame.</exception>
	/*private static byte[] generateJpegByteArray(BitmapFrame targetFrame, int quality)
	{
		byte[] targetBytes;
		using (var memoryStream = new MemoryStream())
		{
			var targetEncoder = new JpegBitmapEncoder
														{
															QualityLevel = quality
														};

			targetEncoder.Frames.Add(targetFrame);
			targetEncoder.Save(memoryStream);
			targetBytes = memoryStream.ToArray();
		}

		return targetBytes;
	}*/

	private Size getSizeUsingWpf(DisplayObject displayObject) throws IOException{
		ImageInputStream input = null;
		ImageReader reader = null;
		Size size = Size.Empty;
		try {
			input = ImageIO.createImageInputStream(new File(displayObject.getFileNamePhysicalPath()));
		    Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
		    if (readers.hasNext()) {
		        reader = readers.next();
	            reader.setInput(input, true);

	            size =  new Size(reader.getWidth(0), reader.getHeight(0));
		    }		
		}finally {
			if (reader != null)
				reader.dispose();
			if (input != null)
				input.close();
		}
		
		if (size.isEmpty()) {
			return getSizeUsingGdi(displayObject);
		}
		
		return size;
		/*try
		{
			var photoBytes = File.ReadAllBytes(displayObject.FileNamePhysicalPath);
			using (var photoStream = new MemoryStream(photoBytes))
			{
				var photo = ReadBitmapFrame(photoStream);
				return new Size(photo.PixelWidth, photo.PixelHeight);
			}
		}
		catch (NotSupportedException)
		{
			return GetSizeUsingGdi(displayObject);
		}
		catch (Exception ex)
		{
			if (!ex.Data.Contains("SizeMsg"))
			{
				ex.Data.Add("SizeMsg", String.Format("Unable to get the width and height of content object {0} ({1}). Display Type {2}", getContentObject().Id, displayObject.FileNamePhysicalPath, displayObject.DisplayType));
			}

			EventLogController.RecordError(ex, AppSetting.Instance, getContentObject().GalleryId, Factory.LoadGallerySettings());

			return Size.Empty;
		}*/
	}

	private static Size getSizeUsingGdi(DisplayObject displayObject) throws IOException{
		try
		{
			BufferedImage source = ImageIO.read(new File(displayObject.getFileNamePhysicalPath()));
			if (source != null) {
				return new Size(source.getWidth(), source.getHeight());
			}
		}catch (IllegalArgumentException ex) {
			return Size.Empty;
		}catch (IIOException ex){
			return Size.Empty;
		}catch (OutOfMemoryError ex){
			return Size.Empty;
		}
		
		return Size.Empty;
	}

	private static double getRotationInDegrees(ContentObjectRotation rotation){
		switch (rotation){
			case Rotate0:
				return 0;
			case Rotate90:
				return 90;
			case Rotate180:
				return 180;
			case Rotate270:
				return 270;
			default:
				return 0;
		}
	}

	private RotateFlipType getRotateFlipType()	{
		switch (getContentObject().getRotation()){
			case Rotate0:
				return RotateFlipType.RotateNoneFlipNone;
			case Rotate90:
				return RotateFlipType.Rotate90FlipNone;
			case Rotate180:
				return RotateFlipType.Rotate180FlipNone;
			case Rotate270:
				return RotateFlipType.Rotate270FlipNone;
			default:
				return RotateFlipType.RotateNoneFlipNone;
		}
	}
	
	private double getIMRotateAngle()	{
		switch (getContentObject().getRotation()){
			case Rotate0:
				return 0d;
			case Rotate90:
				return 90d;
			case Rotate180:
				return 180d;
			case Rotate270:
				return 270d;
			default:
				return 0d;
		}
	}
	
	private double getRotateAngle()	{
		switch (getContentObject().getRotation()){
			case Rotate0:
				return Math.toRadians(0);
			case Rotate90:
				return Math.toRadians(90);
			case Rotate180:
				return Math.toRadians(180);
			case Rotate270:
				return Math.toRadians(270);
			default:
				return Math.toRadians(0);
		}
	}

	/// <summary>
	/// Add the <paramref name="metaValues" /> back to the <paramref name="filePath" />.
	/// </summary>
	/// <param name="filePath">The full path to the file.</param>
	/// <param name="metaValues">The property items. If null, no action is taken.</param>
	private void addMetaValuesBackToRotatedImage(String filePath, String[] metaValues) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (metaValues == null)
			return;

		// Create a copy of the file and add the metadata to it.
		String tmpImagePath;
		BufferedImage targetImage = ImageIO.read(new File(filePath));
		/*for (String propertyItem : metaValues){
			// Don't copy width, height or orientation meta items.
			String[] metasToNotCopy = new String[]
									 {
										 RawMetadataItemName.ImageWidth.toString(),
										 RawMetadataItemName.ImageHeight.toString(),
										 RawMetadataItemName.ExifPixXDim.toString(),
										 RawMetadataItemName.ExifPixYDim.toString(),
										 RawMetadataItemName.Orientation.toString()
									 };

			if (ArrayUtils.indexOf(metasToNotCopy, (RawMetadataItemName)propertyItem.Id) >= 0)
				continue;

			targetImage.SetPropertyItem(propertyItem);
		}*/

		// Save image to temporary location. We can't overwrite the original path because the Bitmap has a lock on it.
		tmpImagePath = FilenameUtils.concat(AppSettings.getInstance().getTempUploadDirectory(), UUID.randomUUID().toString().concat(".jpg"));
		ImageHelper.saveImageToDisk(targetImage, tmpImagePath, "JPG", getGallerySettings().getOriginalImageJpegQuality());

		// Now that the original file is freed up, delete it and move the temp file into its place.
		try {
			Files.deleteIfExists(new File(filePath).toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileMisc.moveFile(tmpImagePath, filePath);
	}

	//#endregion
	
	public static class ManagedImageBufferedImageFactory implements BufferedImageFactory {

		@Override
		public BufferedImage getColorBufferedImage(final int width, final int height, final boolean hasAlpha) {
		
		    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		    final GraphicsDevice gd = ge.getDefaultScreenDevice();
		
		    final GraphicsConfiguration gc = gd.getDefaultConfiguration();
		
		    return gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		}
			
		@Override		
		public BufferedImage getGrayscaleBufferedImage(final int width, final int height, final boolean hasAlpha) {
		    return getColorBufferedImage(width, height, hasAlpha);
		
		}
		
	}
}