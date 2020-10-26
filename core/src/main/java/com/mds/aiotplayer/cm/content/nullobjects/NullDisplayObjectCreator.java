/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content.nullobjects;

import com.mds.aiotplayer.cm.content.DisplayObject;
import com.mds.aiotplayer.cm.content.DisplayObjectCreator;

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
