package com.mds.cm.content.nullobjects;

import com.mds.cm.content.DisplayObject;
import com.mds.cm.content.DisplayObjectCreator;

/// <summary>
/// Represents a <see cref="IDisplayObjectCreator" /> that is equivalent to null. This class is used instead of null to prevent 
/// <see cref="NullReferenceException" /> errors if the calling code accesses a property or executes a method.
/// </summary>
public class NullDisplayObjectCreator extends DisplayObjectCreator{
	public NullDisplayObjectCreator() {
	}
	
	public NullDisplayObjectCreator(DisplayObject parent) {
		this.setParent(parent);
	}
	
	@Override
	public void generateAndSaveFile(){
	}
}
