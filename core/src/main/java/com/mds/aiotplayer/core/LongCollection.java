package com.mds.aiotplayer.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.mds.aiotplayer.core.exception.ArgumentNullException;

/// <summary>
/// Represents a collection of <see cref="int">integers</see>. This is used in various places in MDS System 
/// instead of List&lt;<see cref="int" />&gt; per Microsoft best practices. Read about rule CA1002 for more information.
/// </summary>
public class LongCollection extends ArrayList<Long>{
	private List<LongCollectionListener> listeners = new ArrayList<>();

    public void addLongCollectionListener(LongCollectionListener listener) {
        listeners.add(listener);
    }

    public void removeLongCollectionListener(LongCollectionListener listener) {
        listeners.remove(listener);
    }
	/// <summary>
	/// Initializes a new instance of the <see cref="LongCollection"/> class.
	/// </summary>
	public LongCollection()	{
		super(); //new ArrayList<Long>()
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="LongCollection"/> class.
	/// </summary>
	/// <param name="items">A collection of integers with which to seed the collection.</param>
	public LongCollection(Collection<Long> items)	{
		super(items);
	}
	
	public LongCollection(long[] items)	{
		super();
		for(long item : items) {
			add(item);
		}
	}

	/// <summary>
	/// This event fires after items have been removed from the collection through the Clear() method.
	/// </summary>
	//public event System.EventHandler Cleared;

	/// <summary>
	/// Add the list of integers to the collection.
	/// </summary>
	/// <param name="values">A list of integers to add to the collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="values" /> is null.</exception>
	public void addRange(Iterable<Long> values)	{
		if (values == null)
			throw new ArgumentNullException("values");

		//addAll((Collection<? extends Long>) values);
		for(Long value : values){
			add(value);
		}
	}

	/// <summary>
	/// Removes all elements from the <see cref="T:System.Collections.ObjectModel.Collection`1"/>.
	/// </summary>
	public void clear()	{
		super.clear();

		LongCollectionEvent event = new LongCollectionEvent(this, this);
		listeners.forEach(l -> l.cleared(event));
		/*if (Cleared != null)
		{
			Cleared(this, new EventArgs());
		}*/
	}

	/// <summary>
	/// Converts the integers in the collection to an array.
	/// </summary>
	/// <returns>Returns an array of integers.</returns>
	public Long[] toArray()	{
		return super.toArray(new Long[0]);
	}
	
	public Long[] copyFromIndex(int indexFrom)	{
		List<Long> arrCopy = new ArrayList<Long>();
		arrCopy.addAll(indexFrom, this);
		
		return arrCopy.toArray(new Long[0]);
	}
	
	public List<Long> subList(int fromIndex) {
        return subList(fromIndex, this.size()-1);
    }
}
