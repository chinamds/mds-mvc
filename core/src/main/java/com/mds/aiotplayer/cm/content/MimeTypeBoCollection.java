/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.ArrayList;

import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// Represents a set of MIME types.
/// </summary>
public class MimeTypeBoCollection extends ArrayList<MimeTypeBo>
{
	/// <summary>
	/// Initializes a new instance of the <see cref="MimeTypeCollection"/> class.
	/// </summary>
	public MimeTypeBoCollection(){
		super(new ArrayList<MimeTypeBo>());
	}

	/// <summary>
	/// Adds the specified MIME type.
	/// </summary>
	/// <param name="item">The MIME type to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addMimeType(MimeTypeBo item)	{
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing MimeTypeCollection. Items.Count = " + size());

		add(item);
	}

	/// <summary>
	/// Find the MIME type in the collection that matches the specified <paramref name="fileExtension" />. If no matching object is found,
	/// null is returned. It is not case sensitive.
	/// </summary>
	/// <param name="fileExtension">A String representing the file's extension, including the period (e.g. ".jpg", ".avi").
	/// It is not case sensitive.</param>
	/// <returns>Returns an <see cref="MimeTypeBo" />object from the collection that matches the specified <paramref name="fileExtension" />,
	/// or null if no matching object is found.</returns>
	public MimeTypeBo find(String fileExtension){
		for(MimeTypeBo item : this) {
			if (item.getExtension().equalsIgnoreCase(fileExtension)) {
				return item;
			}
		}
		
		return null;
	}
}
