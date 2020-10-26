/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content.nullobjects;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.DisplayObject;
import com.mds.aiotplayer.cm.content.DisplayObjectCreator;
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.core.DisplayObjectType;
import com.mds.aiotplayer.core.MimeTypeCategory;
import com.mds.aiotplayer.core.Size;

/// <summary>
/// Represents a <see cref="IDisplayObject" /> that is equivalent to null. This class is used instead of null to prevent 
/// <see cref="NullReferenceException" /> errors if the calling code accesses a property or executes a method.
/// </summary>
public class NullDisplayObject extends DisplayObject{
	
	public NullDisplayObject() {
		//super(new NullContentObject(), DisplayObjectType.Unknown, MimeTypeCategory.NotSet);
	}

	@Override
	public ContentObjectBo getParent(){
		return new NullContentObject();
	}
	
	@Override
	public void setParent(ContentObjectBo parent){
	}

	@Override
	public int getWidth(){
		return Integer.MIN_VALUE;
	}
	
	@Override
	public void setWidth(int width)	{
	}

	@Override
	public int getHeight()	{
		return Integer.MIN_VALUE;
	}
	
	@Override
	public void setHeight(int height) {
	}

	@Override
	public String getFileName(){
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setFileName(String fileName){
	}

	@Override
	public String getFileNamePhysicalPath()	{
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setFileNamePhysicalPath(String fileNamePhysicalPath){
	}

	@Override
	public String getTempFilePath()	{
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setTempFilePath(String tempFilePath) {
	}

	@Override
	public MimeTypeBo getMimeType()	{
		return new NullMimeType();
	}

	@Override
	public DisplayObjectType getDisplayType(){
		return DisplayObjectType.Unknown;
	}
	
	@Override
	public void setDisplayType(DisplayObjectType displayType)	{
	}

	@Override
	public long getContentObjectId() {
		return Long.MIN_VALUE;
	}
	
	@Override
	public void setContentObjectId(long contentObjectId)	{
	}

	@Override
	public DisplayObjectCreator getDisplayObjectCreator(){
		return new NullDisplayObjectCreator();
	}
	
	@Override
	public void setDisplayObjectCreator(DisplayObjectCreator displayObjectCreator)	{
	}

	@Override
	public String getExternalHtmlSource(){
		return StringUtils.EMPTY;
	}
	
	@Override
	public void setExternalHtmlSource(String externalHtmlSource){
	}

	@Override
	public MimeTypeCategory getExternalType(){
		return MimeTypeCategory.NotSet;
	}
	
	@Override
	public void setExternalType(MimeTypeCategory externalType){
	}

	@Override
	public void generateAndSaveFile(){
	}

	@Override
	public Size getSize(){
		return Size.Empty;
	}

	@Override
	public File getFileInfo(){
		return null;
	}

	@Override
	public void setFileInfo(File fileInfo) {
	}
	
	@Override
	public long getFileSizeKB()	{
		return Long.MIN_VALUE;
	}
	
	@Override
	public void setFileSizeKB(long fileSizeKB){
	}
}
