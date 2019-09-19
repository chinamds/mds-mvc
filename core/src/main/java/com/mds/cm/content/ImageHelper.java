package com.mds.cm.content;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.sun.imageio.plugins.jpeg.*;
/*import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;*/

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.mds.core.exception.ArgumentNullException;
import com.mds.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.cm.exception.InvalidGalleryException;
import com.mds.core.Size;

/// <summary>
/// Contains image manipulation functions useful for MDS System.
/// </summary>
@SuppressWarnings("restriction")
public class ImageHelper {
	//#region Public Static Methods

	/// <summary>
	/// Generate a new image from the bitmap with the specified format, width, and height, and at the specified location.
	/// Returns the actual size of the generated image, which may differ from the requested values by a pixel or so.
	/// </summary>
	/// <param name="sourceBmp">The bitmap containing an image from which to generate a new image with the
	/// specified settings. This bitmap is not modified.</param>
	/// <param name="newFilePath">The location on disk to store the image that is generated.</param>
	/// <param name="newImageFormat">The new image format.</param>
	/// <param name="newWidth">The width to make the new image.</param>
	/// <param name="newHeight">The height to make the new image.</param>
	/// <param name="newJpegQuality">The JPEG quality setting (0 - 100) for the new image. Only used if the
	/// image format parameter is JPEG; ignored for all other formats.</param>
	/// <returns>An instance of <see cref="Size" /> that describes the actual width and height of the generated image.</returns>
	/// <exception cref="System.ArgumentNullException">sourceBmp</exception>
	/// <exception cref="MDS.EventLogs.CustomExceptions.UnsupportedImageTypeException">Thrown when <paramref name="sourceBmp" />
	/// cannot be resized to the requested dimensions.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="sourceBmp" /> is null.</exception>
	public static Size saveImageFile(BufferedImage sourceBmp, String newFilePath, String newImageFormat, double newWidth, double newHeight, int newJpegQuality)	{
		if (sourceBmp == null)
			throw new ArgumentNullException("sourceBmp");

		//Create new bitmap with the new dimensions and in the specified format.
		BufferedImage destinationBmp = createResizedBitmap(sourceBmp, sourceBmp.getWidth(), sourceBmp.getHeight(), newWidth, newHeight);

		saveImageToDisk(destinationBmp, newFilePath, newImageFormat, newJpegQuality);
		
		return new Size(destinationBmp.getWidth(), destinationBmp.getHeight());
	}

	/// <summary>
	/// Overlay the text and/or image watermark over the image specified in the <paramref name="filePath" /> parameter and return.
	/// </summary>
	/// <param name="filePath">A String representing the full path to the image file
	/// (e.g. "C:\mypics\myprettypony.jpg", "myprettypony.jpg").</param>
	/// <param name="galleryId">The gallery ID. The watermark associated with this gallery is applied to the file.</param>
	/// <returns>
	/// Returns a BufferedImage instance containing the image with the watermark applied.
	/// </returns>
	public static BufferedImage addWatermark(String filePath, long galleryId, HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException	{
		Watermark wm = Watermark.getWatermarkInstance(galleryId, request);
		return wm.applyWatermark(filePath);
	}

	/// <summary>
	/// Create a new Bitmap with the specified dimensions.
	/// </summary>
	/// <param name="inputBmp">The source bitmap to use.</param>
	/// <param name="sourceBmpWidth">The width of the input bitmap. This should be equal to inputBmp.Size.Width, but it is added as
	/// a parameter so that calling code can send a cached value rather than requiring this method to query the bitmap for the data.
	/// If a value less than zero is specified, then inputBmp.Size.Width is used.
	/// </param>
	/// <param name="sourceBmpHeight">The height of the input bitmap. This should be equal to inputBmp.Size.Height, but it is added as
	/// a parameter so that calling code can send a cached value rather than requiring this method to query the bitmap for the data.</param>
	/// If a value less than zero is specified, then inputBmp.Size.Height is used.
	/// <param name="newWidth">The width of the new bitmap.</param>
	/// <param name="newHeight">The height of the new bitmap.</param>
	/// <returns>Returns a new Bitmap with the specified dimensions.</returns>
	/// <exception cref="MDS.EventLogs.CustomExceptions.UnsupportedImageTypeException">Thrown when <paramref name="inputBmp"/> 
	/// cannot be resized to the requested dimensions. Typically this will occur during 
	/// <see cref="Graphics.DrawImage(Image, Rectangle, Rectangle, GraphicsUnit)"/> because there is not enough system memory.</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="inputBmp" /> is null.</exception>
	public static BufferedImage createResizedBitmap(BufferedImage inputBmp, int sourceBmpWidth, int sourceBmpHeight, double newWidth, double newHeight)	{
		//Adapted (but mostly copied) from http://www.codeproject.com/cs/media/bitmapmanip.asp
		//Create a new bitmap object based on the input
		if (inputBmp == null)
			throw new ArgumentNullException("inputBmp");

		if (sourceBmpWidth <= 0)
			sourceBmpWidth = inputBmp.getWidth();

		if (sourceBmpHeight <= 0)
			sourceBmpHeight = inputBmp.getHeight();

		double xScaleFactor = newWidth/sourceBmpWidth;
		double yScaleFactor = newHeight/sourceBmpHeight;

		int calculatedNewWidth = (int) (sourceBmpWidth*xScaleFactor);
		int calculatedNewHeight = (int) (sourceBmpHeight*yScaleFactor);

		if (calculatedNewWidth <= 0)
		{
			calculatedNewWidth = 1; // Make sure the value is at least 1.
			xScaleFactor = (float) calculatedNewWidth/(float) sourceBmpWidth; // Update the scale factor to reflect the new width
		}

		if (calculatedNewHeight <= 0)
		{
			calculatedNewHeight = 1; // Make sure the value is at least 1.
			yScaleFactor = (float) calculatedNewHeight/(float) sourceBmpHeight; // Update the scale factor to reflect the new height
		}
		
		// create an image buffer for the thumbnail with the new xsize, ysize
        BufferedImage newBmp = new BufferedImage((int) calculatedNewWidth, (int) calculatedNewHeight,
                                                    BufferedImage.TYPE_INT_RGB);
        
        // now render the image into the thumbnail buffer
        Graphics2D g2d = newBmp.createGraphics();
        g2d.drawImage(inputBmp, 0, 0, (int) calculatedNewWidth, (int) calculatedNewHeight, null);
        g2d.dispose();

		return newBmp;
	}
	
	public static BufferedImage getImageResource(String resourceName, Class<?> clazz) throws IOException {
		InputStream is = null;
		try {
			is = clazz.getResourceAsStream(resourceName);
			if (is != null) {
				return ImageIO.read(is);
			}
		}finally {
			if (is != null) {
				is.close();
			}
		}
		
		return null;
	}
	
	public static InputStream getThumbDim(BufferedImage buf, boolean verbose, float xmax, float ymax,
            boolean blurring, boolean hqscaling, int brandHeight, int brandFontPoint,
            String brandFont)
		throws Exception {
		// now get the image dimensions
		float xsize = (float) buf.getWidth(null);
		float ysize = (float) buf.getHeight(null);
		
		// if verbose flag is set, print out dimensions
		// to STDOUT
		if (verbose) {
			System.out.println("original size: " + xsize + "," + ysize);
		}
		
		// scale by x first if needed
		if (xsize > xmax) {
			// calculate scaling factor so that xsize * scale = new size (max)
			float scale_factor = xmax / xsize;
			
			// if verbose flag is set, print out extracted text
			// to STDOUT
			if (verbose) {
				System.out.println("x scale factor: " + scale_factor);
			}
		
			// now reduce x size
			// and y size
			xsize = xsize * scale_factor;
			ysize = ysize * scale_factor;
			
			// if verbose flag is set, print out extracted text
			// to STDOUT
			if (verbose) {
				System.out.println("size after fitting to maximum width: " + xsize + "," + ysize);
			}
		}
		
		// scale by y if needed
		if (ysize > ymax) {
			float scale_factor = ymax / ysize;
			
			// now reduce x size
			// and y size
			xsize = xsize * scale_factor;
			ysize = ysize * scale_factor;
		}
		
		// if verbose flag is set, print details to STDOUT
		if (verbose) {
			System.out.println("size after fitting to maximum height: " + xsize + ", "
			            + ysize);
		}
		
		// create an image buffer for the thumbnail with the new xsize, ysize
		BufferedImage thumbnail = new BufferedImage((int) xsize, (int) ysize,
		                             BufferedImage.TYPE_INT_RGB);
		
		// Use blurring if selected in config.
		// a little blur before scaling does wonders for keeping moire in check.
		if (blurring) {
			// send the buffered image off to get blurred.
			buf = getBlurredInstance((BufferedImage) buf);
		}
		
		// Use high quality scaling method if selected in config.
		// this has a definite performance penalty.
		if (hqscaling) {
			// send the buffered image off to get an HQ downscale.
			buf = getScaledInstance((BufferedImage) buf, (int) xsize, (int) ysize,
			             (Object) RenderingHints.VALUE_INTERPOLATION_BICUBIC, (boolean) true);
		}
		
		// now render the image into the thumbnail buffer
		Graphics2D g2d = thumbnail.createGraphics();
		g2d.drawImage(buf, 0, 0, (int) xsize, (int) ysize, null);
			
		// now create an input stream for the thumbnail buffer and return it
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		ImageIO.write(thumbnail, "jpeg", baos);
		
		// now get the array
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		
		return bais; // hope this gets written out before its garbage collected!
	}
	
	public static BufferedImage getNormalizedInstance(BufferedImage buf) {
        int type = (buf.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB_PRE;
        int w = buf.getWidth();
        int h = buf.getHeight();
        BufferedImage normal = new BufferedImage(w, h, type);
        Graphics2D g2d = normal.createGraphics();
        g2d.drawImage(buf, 0, 0, w, h, Color.WHITE, null);
        g2d.dispose();
        return normal;
    }

    /**
     * Convenience method that returns a blurred instance of the
     * provided {@code BufferedImage}.
     *
     * @param buf buffered image
     * @return updated BufferedImage
     */
    public static BufferedImage getBlurredInstance(BufferedImage buf) {
        buf = getNormalizedInstance(buf);

        // kernel for blur op
        float[] matrix = {
            0.111f, 0.111f, 0.111f,
            0.111f, 0.111f, 0.111f,
            0.111f, 0.111f, 0.111f,
        };

        // perform the blur and return the blurred version.
        BufferedImageOp blur = new ConvolveOp(new Kernel(3, 3, matrix));
        BufferedImage blurbuf = blur.filter(buf, null);
        return blurbuf;
    }
	
	/**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param buf           the original image to be scaled
     * @param targetWidth   the desired width of the scaled instance,
     *                      in pixels
     * @param targetHeight  the desired height of the scaled instance,
     *                      in pixels
     * @param hint          one of the rendering hints that corresponds to
     *                      {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *                      {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *                      {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *                      {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *                      scaling technique that provides higher quality than the usual
     *                      one-step technique (only useful in downscaling cases, where
     *                      {@code targetWidth} or {@code targetHeight} is
     *                      smaller than the original dimensions, and generally only when
     *                      the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    public static BufferedImage getScaledInstance(BufferedImage buf,
                                           int targetWidth,
                                           int targetHeight,
                                           Object hint,
                                           boolean higherQuality) {
        int type = (buf.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scalebuf = (BufferedImage) buf;
        int w;
        int h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = buf.getWidth();
            h = buf.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2d = tmp.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2d.drawImage(scalebuf, 0, 0, w, h, Color.WHITE, null);
            g2d.dispose();

            scalebuf = tmp;
        } while (w != targetWidth || h != targetHeight);

        return scalebuf;
    }

	/// <overloads>Persist the specified image to disk at the specified path.</overloads>
	/// <summary>
	/// Persist the specified image to disk at the specified path. If the directory to contain the file does not exist, it
	/// is automatically created.
	/// </summary>
	/// <param name="image">The image to persist to disk.</param>
	/// <param name="newFilePath">The full physical path, including the file name to where the image is to be stored. Ex: C:\mypics\cache\2008\May\flower.jpg</param>
	/// <param name="imageFormat">The file format for the image.</param>
	/// <param name="jpegQuality">The quality value to save JPEG images at. This is a value between 1 and 100. This parameter
	/// is ignored if the image format is not JPEG.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="imageFormat" /> is null.</exception>
	public static void saveImageToDisk(BufferedImage image, String newFilePath, String imageFormat, int jpegQuality){
		if (imageFormat == null)
			throw new ArgumentNullException("imageFormat");

		if (StringUtils.isBlank(newFilePath))
			throw new ArgumentNullException("newFilePath");

		verifyDirectoryExistsForNewFile(newFilePath);

		if (imageFormat.equalsIgnoreCase("Jpeg") || imageFormat.equalsIgnoreCase("JPG"))
			saveJpgImageToDisk(image, newFilePath, jpegQuality);
		else
			saveNonJpgImageToDisk(image, newFilePath, imageFormat);
	}

	/// <summary>
	/// Persist the specified image to disk at the specified path. If the directory to contain the file does not exist, it
	/// is automatically created.
	/// </summary>
	/// <param name="imageData">The image to persist to disk.</param>
	/// <param name="newFilePath">The full physical path, including the file name to where the image is to be stored. Ex: C:\mypics\cache\2008\May\flower.jpg</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="imageData" /> or <paramref name="newFilePath" /> is null.</exception>
	public static void saveImageToDisk(byte[] imageData, String newFilePath){
		if (imageData == null)
			throw new ArgumentNullException("imageData");

		if (StringUtils.isBlank(newFilePath))
			throw new ArgumentNullException("newFilePath");

		verifyDirectoryExistsForNewFile(newFilePath);
		
		try {
			FileUtils.writeByteArrayToFile(new File(newFilePath), imageData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static BufferedImage rotate(BufferedImage src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(
                src_width, src_height)), angel);

        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = res.createGraphics();
        g2.translate((rect_des.width - src_width) / 2,
                (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);

        g2.drawImage(src, null, null);
        g2.dispose();

        return res;
    }

    public static Rectangle calcRotatedSize(Rectangle src, int angel) {
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));

        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        
        return new Rectangle(new Dimension(des_width, des_height));
    }
    
    public static BufferedImage rotate(BufferedImage image, double angle) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int) Math.floor(h * cos + w * sin);
        
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(neww, newh, Transparency.TRANSLUCENT);
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(image, null);
        g.dispose();
        
        return result;
    }

	//#endregion

	//#region Private Static Methods
    
    private static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

	private static void saveJpgImageToDisk(BufferedImage image, String newFilepath, float jpegQuality)	{
		FileOutputStream out;
		ImageWriter imageWriter = null;
		ImageOutputStream ios =  null;
		try {
			out = new FileOutputStream(newFilepath);
			
			//JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpg").next();
			imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
			ios = ImageIO.createImageOutputStream(out);
			imageWriter.setOutput(ios);
			// and metadata
			IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), null);
			// if(dpi != null && !dpi.equals("")){
			//
			// //old metadata
			// //jpegEncodeParam.setDensityUnit(com.sun.image.codec.jpeg.JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
			// //jpegEncodeParam.setXDensity(dpi);
			// //jpegEncodeParam.setYDensity(dpi);
			//
			// //new metadata
			// Element tree = (Element)
			// imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
			// Element jfif =
			// (Element)tree.getElementsByTagName("app0JFIF").item(0);
			// jfif.setAttribute("Xdensity", Integer.toString(dpi) );
			// jfif.setAttribute("Ydensity", Integer.toString(dpi));
			//
			// }
			if (jpegQuality > 1f) {
				jpegQuality = jpegQuality / 100.0f;
			}
			
			if (jpegQuality >= 0 && jpegQuality <= 1f) {
				// old compression
				// jpegEncodeParam.setQuality(JPEGcompression,false);
				// new Compression
				JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
				jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
				jpegParams.setCompressionQuality(jpegQuality);
			}
			// old write and clean
			// jpegEncoder.encode(image_to_save, jpegEncodeParam);
			// new Write and clean up
			imageWriter.write(imageMetaData, new IIOImage(image, null, null), null);			
			/*JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
			param.setQuality(jpegQuality, true);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(image);*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (imageWriter != null)
				imageWriter.dispose();
			if (ios != null) {
				try {
					ios.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/// <summary>
	/// Make sure the directory exists for the file at the specified path. It is created if it does not exist. 
	/// (For example, it might not exist when the user changes the thumbnail or optimized location and subsequently 
	/// synchronizes. This process creates a new directory structure to match the directory structure where the 
	/// originals are stored, and there may be cases where we need to save a file to a directory that doesn't yet exist.
	/// </summary>
	/// <param name="newFilepath">The full physical path for which to verify the directory exists. Ex: C:\mypics\cache\2008\May\flower.jpg</param>
	private static void verifyDirectoryExistsForNewFile(String newFilepath)	{
		File file = new File(newFilepath);
		String absolutePath = file.getAbsolutePath();
	    
	    String filePath = absolutePath.
	    	     substring(0,absolutePath.lastIndexOf(File.separator));

	    File path= new File(filePath);
		if (!Files.exists(path.toPath())){
			path.mkdirs();
		}
	}

	private static void saveNonJpgImageToDisk(BufferedImage image, String newFilepath, String imgFormat){
		try {
			ImageIO.write(image, imgFormat, new File(newFilepath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//#endregion
}
