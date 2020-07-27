package com.mds.aiotplayer.cm.content;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.service.GallerySettingManager;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.ArgumentOutOfRangeException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.ContentAlignment;
import com.mds.aiotplayer.core.Size;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.util.FileMisc;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.mds.aiotplayer.sys.util.UserUtils;

/// <summary>
/// The Watermark class contains functionality for applying a text and/or image watermark to an image.
/// </summary>
public class Watermark implements AutoCloseable{
	private static GallerySettingManager gallerySettingManager = SpringContextHolder.getBean(GallerySettingManager.class);
	//#region Private Fields
	private static Map<Long, Watermark> _watermarks = new HashMap<Long, Watermark>(1);

	private BufferedImage _watermarkImage;
	private int _watermarkImageWidth = Integer.MIN_VALUE;
	private int _watermarkImageHeight = Integer.MIN_VALUE;
	private String _imagePath;
	private ContentAlignment _imageLocation;
	private int _imageWidthPercent;
	private int _imageOpacityPercent;

	private String _watermarkText;
	private Color _textColor;
	private int _textHeightPixels;
	private int _textWidthPercent;
	private String _textFontName;
	private ContentAlignment _textLocation;
	private int _textOpacityPercent;

	private final int MIN_FONT_HEIGHT_PIXELS = 4;
	private final int DEFAULT_TEXT_HEIGHT_PIXELS = 12; // Text height used if not specified.
	private final float BORDER_PERCENT = .01f; // The distance from the border to place the watermark text and image. Ex: .01
	// means border thickness should be 1% of the width of the recipient image.
	private static final Object _sharedLock = new Object();
	private boolean _hasBeenDisposed; // Used by Dispose() methods

	//#endregion
	
	//#region Public Properties

    /// <summary>
    /// Gets or sets the location for the watermark image on the recipient image.
    /// </summary>
    /// <value>The image location.</value>
	public ContentAlignment getImageLocation(){
	    return _imageLocation; 
    }
	
    public void setImageLocation(ContentAlignment _imageLocation){
    	this._imageLocation = _imageLocation;
    }

    /// <summary>
    /// Gets or sets the percent of the overall width of the recipient image that should be covered with the
    /// watermark image. The size of the image is automatically scaled to achieve the desired width. For example,
    /// a value of 50 means the watermark image is 50% as wide as the recipient image. Valid values are 0 - 100.
    /// A value of 0 turns off this feature and causes the image to be rendered its actual size.
    /// </summary>
    /// <value>The image width, in percent.</value>
    public int getImageWidthPercent(){
    	return _imageWidthPercent;
    }
    
    public void setImageWidthPercent(int _imageWidthPercent){
    	this._imageWidthPercent = _imageWidthPercent;
    }

    /// <summary>
    /// Gets or sets the watermark text to be applied to the recipient image.
    /// </summary>
    /// <value>The watermark text.</value>
    public String getWatermarkText() {
      return _watermarkText;
    }
    
    public void setWatermarkText(String _watermarkText) {
      this._watermarkText = _watermarkText;
    }

    /// <summary>
    /// Gets or sets the height, in pixels, of the watermark text. This value is ignored if the property
    /// TextWidthPercent is non-zero. Valid values are 0 - 10000.
    /// </summary>
    /// <value>The text height, in pixels.</value>
    public int getTextHeightPixels() {
      return _textHeightPixels;
    }
    
    public void setTextHeightPixels(int _textHeightPixels){
    	if ((_textHeightPixels < 0) || (_textHeightPixels > 10000))
        {
          throw new ArgumentOutOfRangeException(StringUtils.format("TextHeightPixels must be an integer between 0 and 10000. The value {0} is invalid.", _textHeightPixels));
        }
    	
        this._textHeightPixels = _textHeightPixels;
    }

    /// <summary>
    /// Gets or sets the percent of the overall width of the recipient image that should be covered with the
    /// watermark text. The size of the text is automatically scaled up or down to achieve the desired width. For example,
    /// a value of 50 means the text is 50% as wide as the recipient image. Valid values are 0 - 100. The text is never
    /// rendered in a font smaller than 6 pixels, so in cases of long text it may stretch wider than the percentage
    /// specified in this setting. A value of 0 turns off this feature and causes the text size to be determined by the
    /// TextSizePixels property.
    /// </summary>
    /// <value>The text width, in percent.</value>
    public int getTextWidthPercent() {
      return _textWidthPercent;
    }
    
    public void setTextWidthPercent(int _textWidthPercent) {
        if ((_textWidthPercent < 0) || (_textWidthPercent > 100))
        {
          throw new ArgumentOutOfRangeException(StringUtils.format("TextWidthPercent must be an integer between 0 and 100. The value {0} is invalid.", _textWidthPercent));
        }

        this._textWidthPercent = _textWidthPercent;
    }

    /// <summary>
    /// Gets or sets the font family name to use for the watermark text applied to the recipient image.
    /// If the name does not represent a font installed on the server, a generic sans serif font is used.
    /// </summary>
    /// <value>The name of the text font.</value>   
    public String getTextFontName()  {
      return _textFontName;
    }
    
    public void setTextFontName(String _textFontName) {
      this._textFontName = _textFontName;
    }

    /// <summary>
    /// Gets or sets the location for the watermark text on the recipient image.
    /// </summary>
    /// <value>The text location.</value>
    public ContentAlignment getTextLocation() {
      return _textLocation;
    }
    
    public void setTextLocation(ContentAlignment _textLocation)  {
      this._textLocation = _textLocation;
    }

    /// <summary>
    /// Gets or sets the color of the watermark text.
    /// </summary>
    /// <value>The color of the text.</value>
    public Color getTextColor() {
      return _textColor;
    }
    
    public void setTextColor(Color _textColor) {
      this._textColor = _textColor;
    }

    /// <summary>
    /// Gets or sets the opacity of the watermark text. Valid values are 0 - 100, with 0 being completely
    /// transparent and 100 completely opaque.
    /// </summary>
    /// <value>The text opacity, in percent.</value>
    public int getTextOpacityPercent() {
      return _textOpacityPercent;
    }
    
    public void setTextOpacityPercent(int _textOpacityPercent) {
    	this._textOpacityPercent = _textOpacityPercent;
    }

    /// <summary>
    /// Gets or sets the opacity of the watermark image. Valid values are 0 - 100, with 0 being completely
    /// transparent and 100 completely opaque.
    /// </summary>
    /// <value>The image opacity, in percent.</value>
    public int getImageOpacityPercent(){
      return _imageOpacityPercent;
    }
    
    public void setImageOpacityPercent(int _imageOpacityPercent) {
    	this._imageOpacityPercent = _imageOpacityPercent;
    }

    /// <summary>
    /// Gets or sets the full path to a watermark image to be applied to the recipient image. The image
    /// must be in a format that allows it to be instantiated in a System.Drawing.Bitmap object. If a relative
    /// path is assigned to this property, it is combined with the current application's path and checked to ensure
    /// it exists. A System.IO.FileNotFoundException is thrown if this property is assigned a non-empty value and
    /// the value does not represent a file on the hard drive. Setting this property
    /// also assigns the WatermarkImage property. An exception is thrown if .NET is unable to create a
    /// System.Drawing.Image object from the file path. Returns String.Empty if user did not specify a value in
    /// the configuration file.
    /// </summary>
    /// <value>The full path to a watermark image to be applied to the recipient image.</value>
    public String getImagePath() {
      return _imagePath;
    }
    
    public void setImagePath(String _imagePath, HttpServletRequest request) throws IOException  {
        if (StringUtils.isBlank(_imagePath)) {
          this._imagePath = StringUtils.EMPTY;
          this._watermarkImage = null;
          return;
        }else if (Files.exists(new File(_imagePath).toPath())){
          this._imagePath = _imagePath; // File exists. OK to set property.
        }else{
          // File doesn't exist, but maybe user specified a relative path. Combine with the application path and try again.
          String relativePath = StringUtils.stripStart(_imagePath, "/\\").replace("/", File.separator);
/*          String fullPath = FilenameUtils.concat(request.getServletContext().getRealPath("/"), Utils.getSkinPath(request));
          fullPath = FilenameUtils.concat(fullPath, relativePath);*/
          String fullPath = FilenameUtils.concat(AppSettings.getInstance().getHomePath(), relativePath);

          if (!Files.exists(new File(fullPath).toPath())){
            throw new FileNotFoundException(StringUtils.format("No image file exists at {0} or {1}. Check the watermark settings and verify a valid file exists in the specified location.", _imagePath, fullPath));
          }

          // File exists! Assign.
          this._imagePath = fullPath;
        }
        
       	_watermarkImage = ImageIO.read(new File(this._imagePath));
    }

    /// <summary>
    /// Gets the watermark image to be applied to the recipient image. The image is created when the
    /// ImagePath property is assigned. Returns null if ImagePath is not specified (that is, the user did
    /// not enter a value in the watermarkImagePath property in the configuration file).
    /// </summary>
    /// <value>The watermark image to be applied to the recipient image.</value>
    public BufferedImage getWatermarkImage(){
      return _watermarkImage;
    }
    
    public void setWatermarkImage(BufferedImage _watermarkImage){
        this._watermarkImage = _watermarkImage;
      }

    /// <summary>
    /// Gets the width, in pixels, of the watermark image. Returns int.MinValue if no watermark image is specified.
    /// </summary>
    /// <value>The width, in pixels, of the watermark image.</value>
    public int getWatermarkImageWidth(){
        if ((_watermarkImageWidth == Integer.MIN_VALUE) && (this._watermarkImage != null)){
          _watermarkImageWidth = this._watermarkImage.getWidth();
        }

        return _watermarkImageWidth;
    }

    /// <summary>
    /// Gets the height, in pixels, of the watermark image. Returns int.MinValue if no watermark image is specified.
    /// </summary>
    /// <value>The height, in pixels, of the watermark image.</value>
    public int getWatermarkImageHeight()  {
        if ((_watermarkImageHeight == Integer.MIN_VALUE) && (this._watermarkImage != null))  {
          _watermarkImageHeight = this._watermarkImage.getHeight();
        }

        return _watermarkImageHeight;
    }

    //#endregion

	//#region Constructors

	private Watermark(){
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Overlay the text and/or image watermark over the image specified in the <paramref name="filePath"/> parameter and return.
	/// </summary>
	/// <param name="filePath">A String representing the full path to the image file  
	/// (e.g. "C:\mypics\myprettypony.jpg", "myprettypony.jpg").</param>
	/// <returns>Returns a <see cref="BufferedImage" /> instance containing the image with the watermark applied.</returns>
	public BufferedImage applyWatermark(String filePath) throws IOException	{
		BufferedImage img = ImageIO.read(new File(filePath));

		applyTextWatermark(img);

		if (this._watermarkImage != null) {
			BufferedImage watermarkedImage = applyImageWatermark(img);
		
			return watermarkedImage;
		}else{
			return img;
		}
	}

	/// <summary>
	/// Gets the watermark that is configured for the specified <paramref name="galleryId" />.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns a <see cref="Watermark" /> instance.</returns>
	public static Watermark getUserSpecifiedWatermark(long galleryId, HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		Watermark tempWatermark = null;
		
		tempWatermark = new Watermark();
		tempWatermark.setWatermarkText(gallerySetting.getWatermarkText());
		tempWatermark.setTextFontName(gallerySetting.getWatermarkTextFontName());
		tempWatermark.setTextColor(HelperFunctions.getColor(gallerySetting.getWatermarkTextColor()));
		tempWatermark.setTextHeightPixels(gallerySetting.getWatermarkTextFontSize());
		tempWatermark.setTextWidthPercent(gallerySetting.getWatermarkTextWidthPercent());
		tempWatermark.setTextOpacityPercent(gallerySetting.getWatermarkTextOpacityPercent());
		tempWatermark.setTextLocation(gallerySetting.getWatermarkTextLocation());
		tempWatermark.setImagePath(gallerySetting.getWatermarkImagePath(), request);
		tempWatermark.setImageWidthPercent(gallerySetting.getWatermarkImageWidthPercent());
		tempWatermark.setImageOpacityPercent(gallerySetting.getWatermarkImageOpacityPercent());
		tempWatermark.setImageLocation(gallerySetting.getWatermarkImageLocation());

		return tempWatermark;
	}

	/// <summary>
	/// Gets the watermark to use when the application is in reduced functionality mode.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns a <see cref="Watermark" /> instance.</returns>
	public static Watermark getReducedFunctionalityModeWatermark(long galleryId) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

	  	Watermark tempWatermark = null;
		tempWatermark = new Watermark();
		tempWatermark.setWatermarkText(I18nUtils.getMessage("watermark.Reduced_Functionality_Mode_Watermark_Text"));
		tempWatermark.setTextFontName(gallerySetting.getWatermarkTextFontName());
		tempWatermark.setTextColor(HelperFunctions.getColor(gallerySetting.getWatermarkTextColor()));
		tempWatermark.setTextHeightPixels(0);
		tempWatermark.setTextWidthPercent(100);
		tempWatermark.setTextOpacityPercent(100);
		tempWatermark.setTextLocation(ContentAlignment.MiddleCenter);
		tempWatermark.setWatermarkImage(ImageHelper.getImageResource("/images/mds_logo.png", tempWatermark.getClass()));
		tempWatermark.setImageWidthPercent(85);
		tempWatermark.setImageOpacityPercent(50);
		tempWatermark.setImageLocation(ContentAlignment.BottomCenter);

		return tempWatermark;
	}
	
	/// <summary>
	/// Gets the watermark instance for the specified <paramref name="galleryId" />.
	/// </summary>
	/// <param name="galleryId">The gallery ID.</param>
	/// <returns>Returns a <see cref="Watermark" /> instance for the specified <paramref name="galleryId" />.</returns>
	public static Watermark getWatermarkInstance(long galleryId, HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		if (galleryId == Long.MIN_VALUE){
			throw new ArgumentOutOfRangeException("galleryId", StringUtils.format("The gallery ID must be a valid ID. Instead, the value passed was {0}.", galleryId));
		}

		Watermark watermark = _watermarks.get(galleryId);

		if (watermark == null){
			watermark = createWatermarkInstance(galleryId, request);
		}

		return watermark;
	}
	
	public synchronized static Watermark createWatermarkInstance(long galleryId, HttpServletRequest request) throws IOException, UnsupportedContentObjectTypeException, InvalidGalleryException{
		Watermark watermark = null;
		if (!_watermarks.containsKey(galleryId))	{
			// A watermark object for the gallery was not found. Create it and add it to the dictionary.
			//Watermark tempWatermark = AppSetting.Instance.License.IsInReducedFunctionalityMode ? Watermark.GetReducedFunctionalityModeWatermark(galleryId) : Watermark.GetUserSpecifiedWatermark(galleryId);
			Watermark tempWatermark = Watermark.getUserSpecifiedWatermark(galleryId, request);

			_watermarks.put(galleryId, tempWatermark);

			watermark = tempWatermark;
		}else {
			watermark = _watermarks.get(galleryId);
		}

		return watermark;
	}

	//#endregion

	//#region Private Methods
		
	private BufferedImage applyImageWatermark(BufferedImage recipientImage)	{
	    if (recipientImage == null)
	    	throw new ArgumentNullException("recipientImage");

	    if (this._watermarkImage == null)
	    	return recipientImage;

	    // Create a Bitmap from the image we are going to draw the watermark on.
	    synchronized (_sharedLock){
			  int recipientImageWidth = recipientImage.getWidth();
			  int recipientImageHeight = recipientImage.getHeight();
		
			  // Get the watermark image, scaling it up or down if needed.
			  BufferedImage watermarkImage = getWatermarkImage(recipientImageWidth, recipientImageHeight);
		
			  int watermarkWidth = watermarkImage.getWidth();
			  int watermarkHeight = watermarkImage.getHeight();
		
			  // Turn off the border if the watermark image is too big to allow for it.
			  float borderPercent = BORDER_PERCENT;
			  if ((watermarkHeight > (recipientImageHeight - (recipientImageHeight * borderPercent))) ||
				  (watermarkWidth > (recipientImageWidth - (recipientImageWidth * borderPercent)))) {
				  borderPercent = 0;
			  }
		
			  // Get the X and Y position for where to start drawing the watermark image on the recipient image.
			  Point watermarkStartingPoint = getWatermarkStartingPoint((float)watermarkWidth, (float)watermarkHeight, (float)recipientImageWidth
					  , (float)recipientImageHeight, this.getImageLocation(), borderPercent);
			  
			  // Draw the watermark image on the recipient image.
			  Graphics2D g2d = recipientImage.createGraphics();
			  g2d.setComposite(makeComposite((float)this.getImageOpacityPercent()/100.0f));
			  g2d.drawImage(watermarkImage,
								  watermarkStartingPoint.x, watermarkStartingPoint.y, watermarkStartingPoint.x + watermarkWidth, watermarkStartingPoint.y + watermarkHeight, //Set the destination position
								  0, // x-coordinate of recipient image to start drawing watermark 
								  0, // y-coordinate of of recipient image to start drawing watermark
								  watermarkWidth,
								  watermarkHeight,
								  null);
			  g2d.dispose();
	    }

	    return recipientImage;
	}

	private BufferedImage getWatermarkImage(int recipientImageWidth, int recipientImageHeight){
	  int watermarkWidth = this.getWatermarkImageWidth();
	  int watermarkHeight = this.getWatermarkImageHeight();

	  if (this.getImageWidthPercent() > 0) {
		// We need to resize the watermark image so that its width takes up the specified percentage of
		// the overall width of the recipient image.
		int resizedWatermarkWidth = (int)(recipientImageWidth * (((float)this.getImageWidthPercent()) / 100));
		int resizedWatermarkHeight = (resizedWatermarkWidth * watermarkHeight) / watermarkWidth;

		// If the resized height is taller than the recipient image, then readjust the width and height
		// to make the watermark as tall as the recipient image.
		if (resizedWatermarkHeight > recipientImageHeight){
		  resizedWatermarkHeight = recipientImageHeight;
		  resizedWatermarkWidth = (watermarkWidth * resizedWatermarkHeight) / watermarkHeight;
		}

		// Get the resized image and assign the width and height vars.
		return ImageHelper.createResizedBitmap(this._watermarkImage, watermarkWidth, watermarkHeight, resizedWatermarkWidth, resizedWatermarkHeight);
	  } else {
		return this._watermarkImage;
	  }
	}
	
	private AlphaComposite makeComposite(float alpha) {
	  int type = AlphaComposite.SRC_ATOP;
	  
	  return(AlphaComposite.getInstance(type, alpha));
    }

	private void applyTextWatermark(BufferedImage img){
	  if (StringUtils.isBlank(this._watermarkText))
		return;

	  float opacity = ((this._textOpacityPercent) / 100.0f);
	  int recipientImageWidth = img.getWidth();
	  int recipientImageHeight = img.getHeight();

	  Font font = null;
	  Graphics2D gr = img.createGraphics();
      FontMetrics fm = null;      
      
	  try
	  {
		//#region Generate font

		if (this._textWidthPercent == 0){
		  // We want to use the TextHeightPixels property to set the size.
		  int fontSize = (this._textHeightPixels == 0 ? DEFAULT_TEXT_HEIGHT_PIXELS : this._textHeightPixels);
		  font = new Font(this._textFontName, Font.PLAIN, fontSize);
		}else{
		  // We have a value for TextWidthPercent, which means we want to create a font/size combination
		  // whose width takes up the specified percentage across the recipient image.
		  int fontSize = MIN_FONT_HEIGHT_PIXELS;
		  float maxTextWidth = recipientImageWidth * (this._textWidthPercent / 100.0f);
		  font = new Font(this._textFontName, Font.PLAIN, fontSize);
		  gr.setFont(font);
	      fm = gr.getFontMetrics();

		  // Starting with the default minimum font size, keep increasing it until we reach the desired width. Note that
		  // we may end up with a font height taller than the image. An early version of this routine limited the font height 
		  // to no larger than the recipient image height, but that resulted in undesirable empty space above and below the 
		  // text, since the measured height includes space for all characters in the character set. This created the 
		  // impression that the character was not really as tall as the recipient image. 'tis better to let the watermark text be
		  // taller than the image in certain circumstances - the user can always reduce the TextWidthPercent until the 
		  // desired height is achieved.
		  while (fm.stringWidth(this._watermarkText) < maxTextWidth) {
			fontSize += 1;
			font = new Font(this._textFontName, Font.PLAIN, fontSize);
			gr.setFont(font);
			
		    fm = gr.getFontMetrics();
		  }

		  // At this point the font size is one larger than it should be. Reduce it and create the final font object.
		  fontSize -= 1;
		  font = new Font(this._textFontName, Font.PLAIN, fontSize);
		  gr.setFont(font);
		}

		//#endregion

		Rectangle2D watermarkSize = fm.getStringBounds(this._watermarkText, gr);

		// Turn off the border if the watermark text is too big to allow for it.
		float borderPercent = BORDER_PERCENT;
		if ((watermarkSize.getHeight() > (recipientImageHeight - (recipientImageHeight * borderPercent))) ||
			(watermarkSize.getWidth() > (recipientImageWidth - (recipientImageWidth * borderPercent)))){
		  borderPercent = 0;
		}

		Point textStartingPoint = getWatermarkStartingPoint((float)watermarkSize.getWidth(), (float)watermarkSize.getHeight()
				, (float)recipientImageWidth, (float)recipientImageHeight, this._textLocation, borderPercent);
		//Color fontColor = new Color(this._textColor.getRed(), this._textColor.getGreen(), this._textColor.getBlue(), opacity);
		gr.setColor(this._textColor);
		gr.setComposite(makeComposite(opacity));
		gr.drawString(this._watermarkText, textStartingPoint.x, textStartingPoint.y + fm.getAscent());
	  } finally{
		if (gr != null){
		  gr.dispose();
		}
	  }
	}

	private static Point getWatermarkStartingPoint(float watermarkWidth, float watermarkHeight, float imageWidth, float imageHeight, ContentAlignment watermarkLocation, float borderPercent){
	  Point startingPoint = new Point();
	  switch (watermarkLocation) {
		case TopLeft:
		  startingPoint.x = (int)(imageWidth * borderPercent);
		  startingPoint.y = (int)(imageHeight * borderPercent);
		  break;
		case TopCenter:
		  startingPoint.x = (int)(imageWidth - watermarkWidth) / 2;
		  startingPoint.y = (int)(imageHeight * borderPercent);
		  break;
		case TopRight:
		  startingPoint.x = (int)(imageWidth - watermarkWidth - (imageWidth * borderPercent));
		  startingPoint.y = (int)(imageHeight * borderPercent);
		  break;
		case MiddleLeft:
		  startingPoint.x = (int)(imageWidth * borderPercent);
		  startingPoint.y = (int)(imageHeight - watermarkHeight) / 2;
		  break;
		case MiddleCenter:
		  startingPoint.x = (int)(imageWidth - watermarkWidth) / 2;
		  startingPoint.y = (int)(imageHeight - watermarkHeight) / 2;
		  break;
		case MiddleRight:
		  startingPoint.x = (int)(imageWidth - watermarkWidth - (imageWidth * borderPercent));
		  startingPoint.y = (int)(imageHeight - watermarkHeight) / 2;
		  break;
		case BottomLeft:
		  startingPoint.x = (int)(imageWidth * borderPercent);
		  startingPoint.y = (int)(imageHeight - watermarkHeight - (imageHeight * borderPercent));
		  break;
		case BottomCenter:
		  startingPoint.x = (int)(imageWidth - watermarkWidth) / 2;
		  startingPoint.y = (int)(imageHeight - watermarkHeight - (imageHeight * borderPercent));
		  break;
		case BottomRight:
		  startingPoint.x = (int)(imageWidth - watermarkWidth - (imageWidth * borderPercent));
		  startingPoint.y = (int)(imageHeight - watermarkHeight - (imageHeight * borderPercent));
		  break;
		default:
		  startingPoint.x = (int)(imageWidth * borderPercent);
		  startingPoint.y = (int)(imageHeight * borderPercent);
		  break;
	  }

	  return startingPoint;
	}
	
	///#region AutoCloseable

    /// <summary>
    /// Releases unmanaged and - optionally - managed resources
    /// </summary>
    /// <param name="disposing"><c>true</c> to release both managed and unmanaged resources; <c>false</c> to release only unmanaged resources.</param>
    protected void dispose(boolean disposing){
      if (!this._hasBeenDisposed){
        // Dispose of resources held by this instance.

        // Set the sentinel.
        this._hasBeenDisposed = true;
      }
    }

    /// <summary>
    /// Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
    /// </summary>
    @Override
    public void close() {
    	dispose(true);
    }

    ///#endregion
    
}
