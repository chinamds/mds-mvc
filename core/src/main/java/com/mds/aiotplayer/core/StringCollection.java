/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

import java.util.ArrayList;
import java.util.Collection;

import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// Represents a collection of <see cref="int">integers</see>. This is used in various places in MDS System 
/// instead of List&lt;<see cref="int" />&gt; per Microsoft best practices. Read about rule CA1002 for more information.
/// </summary>
public class StringCollection extends ArrayList<String>
{
	/// <summary>
	/// Initializes a new instance of the <see cref="StringCollection"/> class.
	/// </summary>
	public StringCollection(){
		super(new ArrayList<String>());
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="StringCollection"/> class.
	/// </summary>
	/// <param name="items">A collection of integers with which to seed the collection.</param>
	public StringCollection(Iterable<String> items)
	{
		super(new ArrayList<String>((Collection<? extends String>)items));
	}

	/// <summary>
	/// Add the list of integers to the collection.
	/// </summary>
	/// <param name="values">A list of integers to add to the collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="values" /> is null.</exception>
	public void addRange(Iterable<String> values)
	{
		if (values == null)
			throw new ArgumentNullException("values");
		
		//addAll((Collection<? extends String>) values);
		for(String value : values)
		{
			add(value);
		}
	}

	/// <summary>
	/// Removes all elements from the <see cref="T:System.Collections.ObjectModel.Collection`1"/>.
	/// </summary>
	public void clear()
	{
		super.clear();
		/*if (Cleared != null)
		{
			Cleared(this, new EventArgs());
		}*/
	}

	/// <summary>
	/// Converts the integers in the collection to an array.
	/// </summary>
	/// <returns>Returns an array of integers.</returns>
	public String[] toArray()
	{
		return super.toArray(new String[0]);
	}
}
