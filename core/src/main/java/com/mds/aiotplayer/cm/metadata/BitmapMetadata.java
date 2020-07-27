package com.mds.aiotplayer.cm.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/*import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
//import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/// <summary>
/// Contains functionality for interacting with a file's metadata through the WPF classes.
/// Essentially it is a wrapper for the <see cref="BitmapMetadata" /> class.
/// </summary>
public class BitmapMetadata{
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/// <summary>
	/// Gets or sets a value that indicates the date that the image was taken.
	/// </summary>
	/// <value>A String.</value>
	public String DateTaken;

	/// <summary>
	/// Gets or sets a value that indicates the title of an image file.
	/// </summary>
	/// <value>A String.</value>
	public String Title;

	/// <summary>
	/// Gets or sets a value that indicates the author of an image.
	/// </summary>
	/// <value>A collection.</value>
	public List<String> Author;

	/// <summary>
	/// Gets or sets a value that identifies the camera model that was used to capture the image.
	/// </summary>
	/// <value>A String.</value>
	public String CameraModel;

	/// <summary>
	/// Gets or sets a value that identifies the camera manufacturer that is associated with an image.
	/// </summary>
	/// <value>A String.</value>
	public String CameraManufacturer;

	/// <summary>
	/// Gets or sets a collection of keywords that describe the image.
	/// </summary>
	/// <value>A collection.</value>
	public List<String> Keywords;

	/// <summary>
	/// Gets or sets a value that identifies the image rating.
	/// </summary>
	/// <value>An integer.</value>
	public int Rating;

	/// <summary>
	/// Gets or sets a value that identifies a comment that is associated with an image.
	/// </summary>
	/// <value>A String.</value>
	public String Comment;

	/// <summary>
	/// Gets or sets a value that identifies copyright information that is associated with an image.
	/// </summary>
	/// <value>A String.</value>
	public String Copyright;

	/// <summary>
	/// Gets or sets a value that indicates the subject matter of an image.
	/// </summary>
	/// <value>A String.</value>
	public String Subject;

	/// <summary>
	/// Provides access to a metadata query reader that can extract metadata from a bitmap image file.
	/// </summary>
	/// <param name="query">Identifies the String that is being queried in the current object.</param>
	/// <returns>The metadata at the specified query location.</returns>
	/// <exception cref="ArgumentNullException">Thrown when query is null.</exception>
	public Object getQuery(String query){
		log.info("com.drew.metadata.Metadata File name: " + query);
		com.drew.metadata.Metadata metadatas;
		try {
			metadatas = ImageMetadataReader.readMetadata(new File(query));
		
			 for (Directory directory : metadatas.getDirectories()) {  
				 for (Tag tag : directory.getTags()) {
					 log.info(tag.toString());
				 }
			 }
		} catch (ImageProcessingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//Assume that boy.jpg is in your current directory
	    //File file = new File(query);

	      //Parser method parameters
		/*
		 * AutoDetectParser parser = new AutoDetectParser(); BodyContentHandler handler
		 * = new BodyContentHandler();
		 * 
		 * 
		 * try { Metadata metadata = new Metadata(); FileInputStream inputstream = new
		 * FileInputStream(file); //ParseContext context = new ParseContext();
		 * 
		 * //parser.parse(inputstream, handler, metadata, context);
		 * parser.parse(inputstream, handler, metadata); inputstream.close();
		 * 
		 * System.out.println(handler.toString()); log.info(handler.toString());
		 * 
		 * //getting the list of all meta data elements String[] metadataNames =
		 * metadata.names();
		 * 
		 * for(String name : metadataNames) { //System.out.println(name + ": " +
		 * metadata.get(name)); log.info(name + ": " + metadata.get(name)); }
		 * 
		 * } catch (IOException | SAXException | TikaException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	      

		 
		return null;
	}
	
	//
    // Summary:
    //     Removes a metadata query from an instance of System.Windows.Media.Imaging.BitmapMetadata.
    //
    // Parameters:
    //   query:
    //     The metadata query to remove.
    //
    // Exceptions:
    //   T:System.ArgumentNullException:
    //     query is null.
    //
    //   T:System.InvalidOperationException:
    //     Occurs when image metadata is read-only.
	 public void removeQuery(String query) {}
     //
     // Summary:
     //     Provides access to a metadata query writer that can write metadata to a bitmap
     //     image file.
     //
     // Parameters:
     //   query:
     //     Identifies the location of the metadata to be written.
     //
     //   value:
     //     The value of the metadata to be written.
     public void setQuery(String query, Object value) {}
}